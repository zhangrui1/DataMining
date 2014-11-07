package jp.co.freedom.master.utilities.jgas;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.jgas.JgasCardDto;
import jp.co.freedom.master.dto.jgas.JgasEnqueteDto;
import jp.co.freedom.master.dto.jgas.JgasQuestionDto;
import jp.co.freedom.master.dto.jgas.JgasUserDataDto;

/**
 * JGAS向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JgasUtil {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<JgasUserDataDto> createInstance(List<String[]> csvData) {
		List<JgasUserDataDto> userData = new ArrayList<JgasUserDataDto>();// ユーザーデータを保持するリスト
		JgasUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new JgasUserDataDto();
				}
				// リクエストコードの格納
				String request = row[1].substring(1);
				dataDto.requestCode.add(request);
			} else if (row.length == 3) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new JgasUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = row[1];
				dataDto.id = rfid.substring(1, rfid.length());
				dataDto.timeByRfid = row[2];
			} else {
				// 読み飛ばし
				continue;
			}
		}
		// 最後尾のユーザーデータのリスト格納
		if (dataDto != null) {
			// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * 【アンケートマッチング用】ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<JgasUserDataDto> createInstanceForEnquete(
			List<String[]> csvData) {
		List<JgasUserDataDto> userData = new ArrayList<JgasUserDataDto>();// ユーザーデータを保持するリスト
		JgasUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new JgasUserDataDto();
				}
				// リクエストコードの格納
				String request = row[1].substring(1);
				dataDto.requestCode.add(request);
			} else if (row.length == 3) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new JgasUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = row[1];
				dataDto.id = rfid.substring(1, rfid.length());
				dataDto.timeByRfid = row[2];
			} else {
				// 読み飛ばし
				continue;
			}
		}
		// 最後尾のユーザーデータのリスト格納
		if (dataDto != null) {
			// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * 指定配列の最後の要素を削除
	 * 
	 * @param array
	 *            対象配列
	 * @return 最後の要素を削除した配列
	 */
	private static String[] removeLastSpaceElement(String[] array) {
		assert array != null;
		String[] newArray = new String[array.length - 1];
		for (int nIndex = 0; nIndex < array.length - 1; nIndex++) {
			newArray[nIndex] = array[nIndex];
		}
		return newArray;
	}

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            RFID番号
	 * @return 検証結果のブール値
	 */
	public static boolean isPreEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith("2");
	}

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            RFID番号
	 * @return 検証結果のブール値
	 */
	public static boolean isAppEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith("3");
	}

	/**
	 * アンケート情報の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userdata
	 *            <b>JgasUserDataDto</b>
	 * @param enqueteId
	 *            アンケート質問番号
	 * @return <b>JgasUserDataDto</b>
	 * @throws SQLException
	 */
	public static JgasEnqueteDto[] getEnqueteData(Connection conn,
			JgasUserDataDto userdata, String enqueteId) throws SQLException {
		JgasEnqueteDto[] enquetes = new JgasEnqueteDto[6];
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM enquete WHERE V_VID = 'a" + enqueteId
				+ "a';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			JgasEnqueteDto enqueteInfo = new JgasEnqueteDto();
			enqueteInfo.enqueteId = rs.getString("V_enquete");
			enqueteInfo.questionNo = rs.getString("V_Q_ID");
			enqueteInfo.answerId = rs.getString("V_A_ID");
			enqueteInfo.iPadAnswerId = rs.getString("V_iPad_ID");
			enqueteInfo.barcodeId = rs.getString("V_VID");
			enqueteInfo.choiceId = rs.getString("V_Choice_ID");
			enqueteInfo.choiceTitle = rs.getString("V_Choice");
			// アンケート情報の格納
			int questionId = getQuestionId(enqueteInfo.questionNo);
			if (questionId != -1) {
				enquetes[questionId] = enqueteInfo;
			}
		}
		rs.close();
		stmt.close();
		return enquetes;
	}

	private static int getQuestionId(String idStr) {
		if (StringUtil.isNotEmpty(idStr)) {
			String id = idStr.substring(3);
			if (StringUtil.isNotEmpty(id) && StringUtil.isNumeric(id)) {
				return Integer.parseInt(id) - 1;
			}
		}
		return -1;
	}

	/**
	 * 事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param rfid
	 *            バーコード番号
	 * @param time
	 *            タイムスタンプ
	 * @return　事前登録データ
	 * @throws SQLException
	 */
	public static JgasUserDataDto getPreRegistData(Connection conn,
			String rfid, String time) throws SQLException {
		JgasUserDataDto userdata = new JgasUserDataDto();
		JgasCardDto cardInfo = new JgasCardDto(); // 名刺情報DTO
		JgasQuestionDto questionInfo = new JgasQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry WHERE V_VID = '" + rfid + "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			/* 名刺情報　 */
			cardInfo.V_VID = rfid; // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_POST"); // 役職
			String zip = rs.getString("V_ZIP"); // 郵便番号
			cardInfo.V_ZIP = zipNormalize(zip);
			cardInfo.V_ADDR1 = getAddr1(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_DAY = getDay(time); // 来場日
			/* アンケート情報 */
			String countryCode = rs.getString("V_Country"); // 国コード
			questionInfo.V_Q1 = "0".equals(countryCode) ? "1" : "2";
			// questionInfo.V_Q1_other = rs.getString("V_Q1_other"); //
			// アンケート1その他FA
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			// questionInfo.V_Q2_other = rs.getString("V_Q2_other"); //
			// アンケート2その他FA
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			// questionInfo.V_Q3_other = rs.getString("V_Q3_other"); //
			// アンケート3その他FA
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			// questionInfo.V_Q5_other = rs.getString("V_Q5_other"); //
			// アンケート5その他FA
			questionInfo.V_Q6 = rs.getString("V_Q6"); // アンケート6
			questionInfo.V_Q7 = rs.getString("V_Q7"); // アンケート7
			// questionInfo.V_Q7_other = rs.getString("V_Q7_other"); //
			// アンケート7その他FA
			String visitFlg = "1".equals(rs.getString("V_Q8_V")) ? "1" : "";
			String exibitFlg = "1".equals(rs.getString("V_Q8_E")) ? "1" : "";
			String q8Value = StringUtil.concat(visitFlg, exibitFlg);
			questionInfo.V_Q8 = q8Value; // アンケート8
			questionInfo.V_Q9 = rs.getString("V_Q9"); // アンケート9

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
		}
		rs.close();
		stmt.close();
		return userdata;
	}

	/**
	 * 都道府県コードから都道府県名を特定する
	 * 
	 * @param value
	 *            都道府県コード
	 * @return 都道府県名
	 */
	private static String getAddr1(String value) {
		if (StringUtil.isNotEmpty(value)) {
			return JgasConstants.ADDR1_MAP.get(value);
		}
		return "";
	}

	/**
	 * 当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param rfid
	 *            バーコード番号
	 * @param day
	 *            タイムスタンプ
	 * @return　当日登録データ
	 * @throws SQLException
	 */
	public static JgasUserDataDto getAppointedDayData(Connection conn,
			String rfid, String time) throws SQLException {
		JgasUserDataDto userdata = new JgasUserDataDto();
		JgasCardDto cardInfo = new JgasCardDto(); // 名刺情報DTO
		JgasQuestionDto questionInfo = new JgasQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday WHERE V_VID = '" + rfid + "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			/* 名刺情報　 */
			cardInfo.V_VID = rfid; // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			String zip = rs.getString("V_ZIP"); // 郵便番号
			cardInfo.V_ZIP = zipNormalize(zip);
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_DAY = getDay(time); // 来場日
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q1_other = rs.getString("V_Q1_other"); // アンケート1その他FA
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q2_other = rs.getString("V_Q2_other"); // アンケート2その他FA
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q3_other = rs.getString("V_Q3_other"); // アンケート3その他FA
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			questionInfo.V_Q5_other = rs.getString("V_Q5_other"); // アンケート5その他FA
			questionInfo.V_Q6 = rs.getString("V_Q6"); // アンケート6
			questionInfo.V_Q7 = rs.getString("V_Q7"); // アンケート7
			questionInfo.V_Q7_other = rs.getString("V_Q7_other"); // アンケート7その他FA
			questionInfo.V_Q8 = rs.getString("V_Q8"); // アンケート8

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
		}
		rs.close();
		stmt.close();
		return userdata;
	}

	/**
	 * 全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<JgasUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<JgasUserDataDto> userDataList = new ArrayList<JgasUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			JgasUserDataDto userdata = new JgasUserDataDto();
			JgasCardDto cardInfo = new JgasCardDto(); // 名刺情報DTO
			JgasQuestionDto questionInfo = new JgasQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			String zip = rs.getString("V_ZIP"); // 郵便番号
			cardInfo.V_ZIP = zipNormalize(zip);
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			// cardInfo.V_DAY = getDay(time); // 来場日
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q1_other = rs.getString("V_Q1_other"); // アンケート1その他FA
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q2_other = rs.getString("V_Q2_other"); // アンケート2その他FA
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q3_other = rs.getString("V_Q3_other"); // アンケート3その他FA
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			questionInfo.V_Q5_other = rs.getString("V_Q5_other"); // アンケート5その他FA
			questionInfo.V_Q6 = rs.getString("V_Q6"); // アンケート6
			questionInfo.V_Q7 = rs.getString("V_Q7"); // アンケート7
			questionInfo.V_Q7_other = rs.getString("V_Q7_other"); // アンケート7その他FA
			questionInfo.V_Q8 = rs.getString("V_Q8"); // アンケート8

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * 全ての事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての事前登録データ
	 * @throws SQLException
	 */
	public static List<JgasUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<JgasUserDataDto> userDataList = new ArrayList<JgasUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			JgasUserDataDto userdata = new JgasUserDataDto();
			JgasCardDto cardInfo = new JgasCardDto(); // 名刺情報DTO
			JgasQuestionDto questionInfo = new JgasQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_POST"); // 役職
			String zip = rs.getString("V_ZIP"); // 郵便番号
			cardInfo.V_ZIP = zipNormalize(zip);
			cardInfo.V_ADDR1 = getAddr1(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			// cardInfo.V_DAY = getDay(time); // 来場日
			/* アンケート情報 */
			String countryCode = rs.getString("V_Country"); // 国コード
			questionInfo.V_Q1 = "0".equals(countryCode) ? "1" : "2";
			// questionInfo.V_Q1_other = rs.getString("V_Q1_other"); //
			// アンケート1その他FA
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			// questionInfo.V_Q2_other = rs.getString("V_Q2_other"); //
			// アンケート2その他FA
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			// questionInfo.V_Q3_other = rs.getString("V_Q3_other"); //
			// アンケート3その他FA
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			// questionInfo.V_Q5_other = rs.getString("V_Q5_other"); //
			// アンケート5その他FA
			questionInfo.V_Q6 = rs.getString("V_Q6"); // アンケート6
			questionInfo.V_Q7 = rs.getString("V_Q7"); // アンケート7
			// questionInfo.V_Q7_other = rs.getString("V_Q7_other"); //
			// アンケート7その他FA
			String visitFlg = "1".equals(rs.getString("V_Q8_V")) ? "1" : "";
			String exibitFlg = "1".equals(rs.getString("V_Q8_E")) ? "2" : "";
			String q8Value = StringUtil.concat(visitFlg, exibitFlg);
			questionInfo.V_Q8 = q8Value; // アンケート8
			questionInfo.V_Q9 = rs.getString("V_Q9"); // アンケート9

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * タイムスタンプより来場日を取得
	 * 
	 * @param time
	 *            タイムスタンプ
	 * @return 来場日
	 */
	private static String getDay(String time) {
		if (StringUtil.isNotEmpty(time)) {
			String month = time.substring(4, 6);
			month = month.startsWith("0") ? month.substring(1) : month;
			String day = time.substring(6, 8);
			day = day.startsWith("0") ? day.substring(1) : day;
			return month + "月" + day + "日";
		}
		return "";
	}

	/**
	 * 照合結果をTXT形式でダウンロード
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param isMaster
	 *            マスターデータ作成フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<JgasUserDataDto> userDataList, String dim, boolean isMaster)
			throws ServletException, IOException, SQLException {

		int columnNum = JgasConfig.OUTPUT_ENQUETE_RESULTS_FLG ? 72 : 22;
		if (isMaster) {
			columnNum--;
		}
		// マッチングアンケートを出力する場合
		if (JgasConfig.OUTPUT_MATCHING_ENQUETE_RESULTS_FLG) {
			columnNum += 6;
		}

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		String[] header = new String[columnNum];

		int headerNum = -1;
		header[++headerNum] = StringUtil.enquote("バーコード番号");
		header[++headerNum] = StringUtil.enquote("リーダー番号");
		if (!isMaster) {
			header[++headerNum] = StringUtil.enquote("来場日");
		}
		header[++headerNum] = StringUtil.enquote("海外住所フラグ");
		header[++headerNum] = StringUtil.enquote("原票状況不備");
		header[++headerNum] = StringUtil.enquote("原票種別");
		header[++headerNum] = StringUtil.enquote("国名");
		header[++headerNum] = StringUtil.enquote("原票手書き");
		header[++headerNum] = StringUtil.enquote("会社名");
		header[++headerNum] = StringUtil.enquote("会社名（カナ）");
		header[++headerNum] = StringUtil.enquote("部署");
		header[++headerNum] = StringUtil.enquote("役職");
		header[++headerNum] = StringUtil.enquote("氏名");
		header[++headerNum] = StringUtil.enquote("氏名（カナ）");
		header[++headerNum] = StringUtil.enquote("郵便番号");
		header[++headerNum] = StringUtil.enquote("都道府県");
		header[++headerNum] = StringUtil.enquote("住所");
		header[++headerNum] = StringUtil.enquote("ビル名");
		header[++headerNum] = StringUtil.enquote("電子メールアドレス");
		header[++headerNum] = StringUtil.enquote("電話番号");
		header[++headerNum] = StringUtil.enquote("FAX番号");
		if (JgasConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header[++headerNum] = StringUtil.enquote("業種");
			header[++headerNum] = StringUtil.enquote("職種");
		}
		header[++headerNum] = StringUtil.enquote("リクエストコード表");

		// アンケート項目
		if (JgasConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			// Q1. どこの国から来られましたか？
			header[++headerNum] = StringUtil.enquote("日本");
			header[++headerNum] = StringUtil.enquote("外国");
			header[++headerNum] = StringUtil.enquote("国名");
			// Q2. あなたの会社の業種は？
			header[++headerNum] = StringUtil.enquote("企画・デザイン");
			header[++headerNum] = StringUtil.enquote("プリプレス");
			header[++headerNum] = StringUtil.enquote("印刷");
			header[++headerNum] = StringUtil.enquote("製本・紙工");
			header[++headerNum] = StringUtil.enquote("ベンダー");
			header[++headerNum] = StringUtil.enquote("その他");
			header[++headerNum] = StringUtil.enquote("その他FA");
			// Q3. あなたの職種は？
			header[++headerNum] = StringUtil.enquote("経営者");
			header[++headerNum] = StringUtil.enquote("経営企画");
			header[++headerNum] = StringUtil.enquote("購買");
			header[++headerNum] = StringUtil.enquote("生産");
			header[++headerNum] = StringUtil.enquote("広報");
			header[++headerNum] = StringUtil.enquote("営業");
			header[++headerNum] = StringUtil.enquote("デザイン");
			header[++headerNum] = StringUtil.enquote("研究・開発");
			header[++headerNum] = StringUtil.enquote("その他");
			header[++headerNum] = StringUtil.enquote("その他FA");
			// Q4. あなたの役職は？
			header[++headerNum] = StringUtil.enquote("経営者・役員");
			header[++headerNum] = StringUtil.enquote("部長・次長");
			header[++headerNum] = StringUtil.enquote("課長");
			header[++headerNum] = StringUtil.enquote("係長・主任");
			header[++headerNum] = StringUtil.enquote("一般社員・職員");
			// Q5. あなたの来場の目的は？
			header[++headerNum] = StringUtil.enquote("製品の購入を決定するため");
			header[++headerNum] = StringUtil.enquote("製品を比較検討して絞り込むため");
			header[++headerNum] = StringUtil.enquote("課題が解決できそうな製品を探すため");
			header[++headerNum] = StringUtil.enquote("情報収集のため");
			header[++headerNum] = StringUtil.enquote("その他");
			header[++headerNum] = StringUtil.enquote("その他FA");
			// Q6. あなたは製品選定・購入にどのように関与されていますか？
			header[++headerNum] = StringUtil.enquote("最終決定する権限を持っている");
			header[++headerNum] = StringUtil.enquote("製品選定をほぼ決定づけている");
			header[++headerNum] = StringUtil.enquote("製品選定を提案する立場である");
			header[++headerNum] = StringUtil.enquote("関与しない");
			// Q7.今回の展示会を何で知りましたか？
			header[++headerNum] = StringUtil.enquote("招待状");
			header[++headerNum] = StringUtil.enquote("ホームページ（主催者）");
			header[++headerNum] = StringUtil.enquote("ホームページ（出展者）");
			header[++headerNum] = StringUtil.enquote("ホームページ（その他）");
			header[++headerNum] = StringUtil.enquote("業界紙・業界雑誌");
			header[++headerNum] = StringUtil.enquote("一般紙・雑誌");
			header[++headerNum] = StringUtil.enquote("出展者から聞いた");
			header[++headerNum] = StringUtil.enquote("自分の会社から聞いた");
			header[++headerNum] = StringUtil.enquote("その他");
			header[++headerNum] = StringUtil.enquote("その他FA");
			// Q8. 次回IGAS2015について
			header[++headerNum] = StringUtil.enquote("来場を希望する");
			header[++headerNum] = StringUtil.enquote("出展者を希望する");

			header[++headerNum] = StringUtil.enquote("JGASからの情報をお送りしてもよいですか？");
		}

		// マッチングアンケートの出力
		if (JgasConfig.OUTPUT_MATCHING_ENQUETE_RESULTS_FLG) {
			header[++headerNum] = StringUtil.enquote("Q1");
			header[++headerNum] = StringUtil.enquote("Q2");
			header[++headerNum] = StringUtil.enquote("Q3");
			header[++headerNum] = StringUtil.enquote("Q4");
			header[++headerNum] = StringUtil.enquote("Q5");
			header[++headerNum] = StringUtil.enquote("Q6");
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (JgasUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			if (!isMaster) {
				// 来場日
				cols[++nColumn] = userdata.cardInfo != null
						&& StringUtil.isNotEmpty(userdata.cardInfo.V_VID) ? StringUtil
						.enquote(getDay(userdata.timeByRfid)) : StringUtil
						.enquote(userdata.timeByRfid);
			}
			// 海外住所フラグ
			cols[++nColumn] = isOversea(userdata) ? StringUtil.enquote("1")
					: StringUtil.enquote("");
			// 原票状況不備
			cols[++nColumn] = getLackFlg(userdata);
			// 原票種別
			String type = userdata.cardInfo != null ? getTicketType(userdata)
					: "";
			cols[++nColumn] = StringUtil.isNotEmpty(type) ? StringUtil
					.enquote(type) : StringUtil.enquote("");
			// 国名
			String country = null;
			if (userdata.questionInfo != null) {
				if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q1_other)) {
					country = userdata.questionInfo.V_Q1_other;
				} else if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q1)) {
					country = JgasConstants.OUNTRY_MAP
							.get(userdata.questionInfo.V_Q1);
				} else if (userdata.cardInfo != null) {
					if (StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1)
							&& !userdata.cardInfo.V_ADDR1.equals("海外")) {
						country = "日本";
					}
				}
			}
			cols[++nColumn] = StringUtil.isNotEmpty(country) ? StringUtil
					.enquote(country) : StringUtil.enquote("");
			// 原票手書き
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_TICKET_HAND) ? StringUtil
					.enquote(userdata.cardInfo.V_TICKET_HAND) : StringUtil
					.enquote("");
			// 会社名
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_CORP) ? StringUtil
					.enquote(userdata.cardInfo.V_CORP) : StringUtil.enquote("");
			// 会社名カナ
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_CORP_KANA) ? StringUtil
					.enquote(userdata.cardInfo.V_CORP_KANA) : StringUtil
					.enquote("");
			// 部署
			String dept = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_DEPT1, userdata.cardInfo.V_DEPT2,
					userdata.cardInfo.V_DEPT3, userdata.cardInfo.V_DEPT4) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(dept) ? StringUtil
					.enquote(dept) : StringUtil.enquote("");
			// 役職
			String biz = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_BIZ1, userdata.cardInfo.V_BIZ2,
					userdata.cardInfo.V_BIZ3, userdata.cardInfo.V_BIZ4) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(biz) ? StringUtil
					.enquote(biz) : StringUtil.enquote("");
			// 氏名
			String name = userdata.cardInfo != null ? StringUtil
					.concatWithDelimit("　", userdata.cardInfo.V_NAME1,
							userdata.cardInfo.V_NAME2) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(name) ? StringUtil
					.enquote(name) : StringUtil.enquote("");
			// 氏名カナ
			String kana = userdata.cardInfo != null ? StringUtil
					.concatWithDelimit("　", userdata.cardInfo.V_NAMEKANA1,
							userdata.cardInfo.V_NAMEKANA2) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(kana) ? StringUtil
					.enquote(kana) : StringUtil.enquote("");
			// 郵便番号
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ZIP) ? StringUtil
					.enquote(userdata.cardInfo.V_ZIP) : StringUtil.enquote("");
			// 都道府県
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1) ? StringUtil
					.enquote(userdata.cardInfo.V_ADDR1) : StringUtil
					.enquote("");
			// 住所(市区郡+町域+番号他)
			String addr2 = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4) : "";

			cols[++nColumn] = StringUtil.isNotEmpty(addr2) ? StringUtil
					.enquote(addr2) : StringUtil.enquote("");
			// ビル名
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR5) ? StringUtil
					.enquote(userdata.cardInfo.V_ADDR5) : StringUtil
					.enquote("");
			// メールアドレス (■が含まれている場合は空値に置換)
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.V_EMAIL) : StringUtil
					.enquote("");
			// 電話番号 (■が含まれている場合は空値に置換)
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_TEL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_TEL) ? StringUtil
					.enquote(userdata.cardInfo.V_TEL) : StringUtil.enquote("");
			// FAX番号 (■が含まれている場合は空値に置換)
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_FAX)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_FAX) ? StringUtil
					.enquote(userdata.cardInfo.V_FAX) : StringUtil.enquote("");
			if (JgasConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				// 業種
				String bizEnq = null;
				if (userdata.questionInfo != null
						&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q2)) {
					String value = null;
					String[] bizBuff = userdata.questionInfo.V_Q2.split(" ");
					if (bizBuff.length != 0) {
						value = bizBuff[0];
					} else if (StringUtil
							.isNotEmpty(userdata.questionInfo.V_Q2_other)) {
						value = JgasConstants.BIZ_OTHER_NUM;
					}
					if (StringUtil.isNotEmpty(value)) {
						bizEnq = JgasConstants.BIZ_MAP.get(value);
						if (JgasConstants.BIZ_OTHER_NUM.equals(value)
								&& StringUtil
										.isNotEmpty(userdata.questionInfo.V_Q2_other)) {
							bizEnq = bizEnq + "("
									+ userdata.questionInfo.V_Q2_other + ")";
						}
					}
				}
				cols[++nColumn] = StringUtil.isNotEmpty(bizEnq) ? StringUtil
						.enquote(bizEnq) : StringUtil.enquote("");
				// 職種
				String occp = null;
				if (userdata.questionInfo != null
						&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q3)) {
					String value = null;
					String[] occpBuff = userdata.questionInfo.V_Q3.split(" ");
					if (occpBuff.length != 0) {
						value = occpBuff[0];
					} else if (StringUtil
							.isNotEmpty(userdata.questionInfo.V_Q3_other)) {
						value = JgasConstants.OCCP_OTHER_NUM;
					}
					if (StringUtil.isNotEmpty(value)) {
						occp = JgasConstants.OCCP_MAP.get(value);
						if (JgasConstants.OCCP_OTHER_NUM.equals(value)
								&& StringUtil
										.isNotEmpty(userdata.questionInfo.V_Q3_other)) {
							occp = occp + "("
									+ userdata.questionInfo.V_Q3_other + ")";
						}
					}
				}
				cols[++nColumn] = StringUtil.isNotEmpty(occp) ? StringUtil
						.enquote(occp) : StringUtil.enquote("");
			}
			// リクエストコード
			List<String> requestCodes = userdata.requestCode;
			StringBuffer sb = new StringBuffer();
			if (requestCodes != null) {
				for (int nIndex = 0; nIndex < requestCodes.size(); nIndex++) {
					sb.append(getRequestValue(userdata.reader,
							requestCodes.get(nIndex)));
					if (nIndex != requestCodes.size() - 1) {
						sb.append(",");
					}
				}
			}
			String requestValues = sb.toString();
			cols[++nColumn] = StringUtil.isNotEmpty(requestValues) ? StringUtil
					.enquote(requestValues) : StringUtil.enquote("");
			// アンケート項目
			if (JgasConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				cols = outputEnqueteData(cols, userdata, nColumn);
			}
			// マッチングアンケート
			if (JgasConfig.OUTPUT_MATCHING_ENQUETE_RESULTS_FLG) {
				for (int nIndex = 0; nIndex < 6; nIndex++) {
					if (userdata.enqueteInfo != null) {
						JgasEnqueteDto enqueteInfo = userdata.enqueteInfo[nIndex];
						if (enqueteInfo != null) {
							cols[++nColumn] = StringUtil
									.isNotEmpty(enqueteInfo.choiceTitle) ? StringUtil
									.enquote(enqueteInfo.choiceTitle)
									: StringUtil.enquote("");
						} else {
							cols[++nColumn] = StringUtil.enquote("");
						}
					} else {
						cols[++nColumn] = StringUtil.enquote("");
					}
				}
			}

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 【アンケート出力用】照合結果をTXT形式でダウンロード
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param isMaster
	 *            マスターデータ作成フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForEnquete(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<JgasUserDataDto> userDataList, String dim, boolean isMaster)
			throws ServletException, IOException, SQLException {

		int columnNum = 7;
		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		String[] header = new String[columnNum];

		int headerNum = -1;
		header[++headerNum] = StringUtil.enquote("バーコード番号");
		header[++headerNum] = StringUtil.enquote("Q1");
		header[++headerNum] = StringUtil.enquote("Q2");
		header[++headerNum] = StringUtil.enquote("Q3");
		header[++headerNum] = StringUtil.enquote("Q4");
		header[++headerNum] = StringUtil.enquote("Q5");
		header[++headerNum] = StringUtil.enquote("Q6");

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (JgasUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// アンケート項目
			for (int nIndex = 0; nIndex < 6; nIndex++) {
				JgasEnqueteDto enqueteInfo = userdata.enqueteInfo[nIndex];
				if (enqueteInfo != null) {
					cols[++nColumn] = StringUtil
							.isNotEmpty(enqueteInfo.choiceTitle) ? StringUtil
							.enquote(enqueteInfo.choiceTitle) : StringUtil
							.enquote("");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JgasUserDataDto</b>
	 * @param nColumn
	 *            出力バッファにおける出力位置
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static String[] outputEnqueteData(String cols[],
			JgasUserDataDto userdata, int nColumn) {
		// Q1. どこの国から来られましたか？
		String[] q1Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q1)) {
			q1Buff = userdata.questionInfo.V_Q1.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q1Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "2")
				|| (userdata.questionInfo != null && StringUtil
						.isNotEmpty(userdata.questionInfo.V_Q1_other)) ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q1_other) ? StringUtil
				.enquote(userdata.questionInfo.V_Q1_other) : StringUtil
				.enquote("");

		// Q2. あなたの会社の業種は？
		String[] q2Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q2)) {
			q2Buff = userdata.questionInfo.V_Q2.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q2Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "5") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "6")
				|| (userdata.questionInfo != null && StringUtil
						.isNotEmpty(userdata.questionInfo.V_Q2_other)) ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q2_other) ? StringUtil
				.enquote(userdata.questionInfo.V_Q2_other) : StringUtil
				.enquote("");

		// Q3. あなたの職種は？
		String[] q3Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q3)) {
			q3Buff = userdata.questionInfo.V_Q3.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q3Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "5") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "6") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "7") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "8") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q3Buff, "9")
				|| (userdata.questionInfo != null && StringUtil
						.isNotEmpty(userdata.questionInfo.V_Q3_other)) ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q3_other) ? StringUtil
				.enquote(userdata.questionInfo.V_Q3_other) : StringUtil
				.enquote("");

		// Q4. あなたの役職は？
		String[] q4Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q4)) {
			q4Buff = userdata.questionInfo.V_Q4.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q4Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q4Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q4Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q4Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q4Buff, "5") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// Q5. あなたの来場の目的は？★マルチ回答★
		String[] q5Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q5)) {
			q5Buff = userdata.questionInfo.V_Q5.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q5Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q5Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q5Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q5Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q5Buff, "5")
				|| (userdata.questionInfo != null && StringUtil
						.isNotEmpty(userdata.questionInfo.V_Q5_other)) ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q5_other) ? StringUtil
				.enquote(userdata.questionInfo.V_Q5_other) : StringUtil
				.enquote("");

		// Q6. あなたの来場の目的は？
		String[] q6Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q6)) {
			q6Buff = userdata.questionInfo.V_Q6.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q6Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q6Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q6Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q6Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// Q7. あなたの来場の目的は？★マルチ回答★
		String[] q7Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q7)) {
			q7Buff = userdata.questionInfo.V_Q7.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q7Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "5") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "6") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "7") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "8") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q7Buff, "9")
				|| (userdata.questionInfo != null && StringUtil
						.isNotEmpty(userdata.questionInfo.V_Q7_other)) ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q7_other) ? StringUtil
				.enquote(userdata.questionInfo.V_Q7_other) : StringUtil
				.enquote("");

		// Q8. 次回IGAS2015について★マルチ回答★
		String[] q8Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q8)) {
			q8Buff = userdata.questionInfo.V_Q8.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q8Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q8Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// TODO: とりあえず事前登録のデータをそのまま入れる // Q9. JGASからの情報をお送りしてもよいですか？
		cols[++nColumn] = userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q9) ? StringUtil
				.enquote(userdata.questionInfo.V_Q9) : StringUtil.enquote("");

		return cols;
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            <b>JgasCardDto</b>
	 * @return 原票種別
	 */
	private static String getTicketType(UserDataDto userdata) {
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_IMAGE_PATH)) {
			String value = userdata.cardInfo.V_IMAGE_PATH.substring(13, 15);
			if ("SH".equals(value)) {
				return "招待";
			} else if ("TO".equals(value)) {
				return "当日";
			} else if ("PA".equals(value)) {
				return "団体";
			}
		} else if (isPreEntry(userdata.id)) {
			return "事前";
		}
		return "";
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>JgasUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(JgasUserDataDto userdata) {
		boolean lack1Flg = !isValid(userdata, "1"); // 不備フラグ1
		boolean lack2Flg = !isValid(userdata, "2"); // 不備フラグ2
		if (lack1Flg && !lack2Flg) {
			return "1";
		} else if (lack2Flg) {
			return "2";
		} else {
			return "";
		}

	}

	/**
	 * ハイフンなし7桁郵便番号であるか否かの検証
	 * 
	 * @param zip
	 *            郵便番号
	 * @return　検証結果のブール値
	 */
	private static boolean is7Zip(String zip) {
		if (StringUtil.isNotEmpty(zip)) {
			boolean digitCheck = zip.length() == 7;
			boolean isValid = StringUtil.isNumeric(zip);
			return digitCheck && isValid;
		}
		return false;
	}

	/**
	 * 郵便番号の正規化
	 * 
	 * @param zip
	 *            郵便番号
	 * @return　正規化後の郵便番号
	 */
	private static String zipNormalize(String zip) {
		if (is7Zip(zip)) {
			return zip.substring(0, 3) + "-" + zip.substring(3);
		}
		return zip;
	}

	/**
	 * 海外住所フラグの検証
	 * 
	 * @param cardInfo
	 *            <b>JgasCardDto</b>
	 * @return 検証結果のブール値
	 */
	private static boolean isOversea(JgasUserDataDto userdata) {
		if (userdata.cardInfo != null) {
			boolean result1;
			// 海外住所である否かの検証
			if (isPreEntry(userdata.id)) { // 事前登録データである場合
				result1 = "海外".equals(userdata.cardInfo.V_ADDR1);
			} else { // 当日登録データである場合
				boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
				boolean check2 = StringUtil
						.isNotEmpty(userdata.cardInfo.V_ADDR2);
				boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
				boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
				boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
				result1 = check1 && check2 && check3 && check4 && check5;
			}
			boolean result2 = "2".equals(userdata.questionInfo.V_Q1); // アンケートQ1の回答内容
			boolean result2Other = StringUtil
					.isNotEmpty(userdata.questionInfo.V_Q1_other);
			return result1 || result2 || result2Other;
		}
		return false;
	}

	/**
	 * バリデーションチェック
	 * 
	 * @param userdata
	 *            個人情報
	 * @param validationType
	 *            バリデーション種別(1=■存在チェック, 2=必須制約違反)
	 * @return 異常がある場合は1,　それ以外は0
	 */
	private static boolean isValid(JgasUserDataDto userdata,
			String validationType) {
		assert userdata != null;
		if (userdata.cardInfo == null) {
			return false;
		}
		if ("1".equals(validationType)) {
			/*
			 * 不明マーク(■)の存在チェック
			 */
			// 氏名情報
			boolean V_NAME1 = !StringUtil
					.containWildcard(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = !StringUtil
					.containWildcard(userdata.cardInfo.V_NAME2); // 事前登録ユーザーの場合には姓名名は空文字列
			// 連絡先情報
			boolean V_ZIP = !StringUtil
					.containWildcard(userdata.cardInfo.V_ZIP);
			boolean V_ADDR1 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR1);
			boolean V_ADDR2 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR2);
			boolean V_ADDR3 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR3);
			boolean V_ADDR4 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR4);
			// 電話番号
			boolean V_TEL = !StringUtil
					.containWildcard(userdata.cardInfo.V_TEL);
			// FAX
			boolean V_FAX = !StringUtil
					.containWildcard(userdata.cardInfo.V_FAX);
			// メールアドレス
			boolean V_EMAIL = !StringUtil
					.containWildcard(userdata.cardInfo.V_EMAIL);

			return V_NAME1 && V_NAME2 && V_ZIP && V_ADDR1 && V_ADDR2 && V_ADDR3
					&& V_ADDR4 && V_TEL && V_FAX && V_EMAIL;
		} else {
			/*
			 * 必須制約違反の検証
			 */
			// 氏名情報
			boolean V_NAME1 = StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = isPreEntry(userdata.id.substring(1)) ? true
					: StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2);
			// 連絡先情報
			boolean V_ZIP = StringUtil.isNotEmpty(userdata.cardInfo.V_ZIP);
			boolean V_ADDR1 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1);
			boolean V_ADDR2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean V_ADDR3 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR3);
			boolean V_ADDR4 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4);
			// 電話番号
			boolean V_TEL = StringUtil.isNotEmpty(userdata.cardInfo.V_TEL);
			// FAX
			boolean V_FAX = StringUtil.isNotEmpty(userdata.cardInfo.V_FAX);
			// メールアドレス
			boolean V_EMAIL = StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL);

			// 氏名情報の欠落フラグ
			boolean nameIsValid = V_NAME1 && V_NAME2;
			// 連絡先情報の欠落フラグ
			boolean addressIsValid = V_ZIP && V_ADDR1 && V_ADDR2 && V_ADDR3
					&& V_ADDR4;
			return nameIsValid & (addressIsValid || V_TEL || V_FAX || V_EMAIL);
		}
	}

	/**
	 * リクエストコードに対応する値を取得
	 * 
	 * @param reader
	 *            バーコードリーダーID
	 * @param code
	 *            リクエストコード
	 * @return　リクエストコードに対応する値
	 */
	public static String getRequestValue(String reader, String code) {
		assert StringUtil.isNotEmpty(reader);
		String value = null;
		if (StringUtil.isNotEmpty(code)) {
			if (JgasConstants.READER_AIREC_LIST.contains(reader)) {
				value = JgasConstants.REQUESTCODES_AIREC.get(code);
			} else if (JgasConstants.READER_BALDWIN_LIST.contains(reader)) {
				value = JgasConstants.REQUESTCODES_BALDWIN.get(code);
			} else if (JgasConstants.READER_COLOR_LIST.contains(reader)) {
				value = JgasConstants.REQUESTCODES_JAPANCOLOR.get(code);
			} else if (JgasConstants.READER_PRINTPACK_LIST.contains(reader)) {
				value = JgasConstants.REQUESTCODES_PRINTPACK.get(code);
			} else if (JgasConstants.READER_SCREEN_LIST.contains(reader)) {
				value = JgasConstants.REQUESTCODES_SCREEN.get(code);
			} else {
				value = code; // 対応表が存在しない場合はリクエストコードをそのまま返却
			}
			if (StringUtil.isEmpty(value)) {
				value = code; // 対応表にリクエストコードが掲載されていない場合はリクエストコードをそのまま返却
			}
		}
		return value;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>ScfUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, JgasUserDataDto> getMap(
			List<JgasUserDataDto> userDataList) {
		Map<String, JgasUserDataDto> map = new HashMap<String, JgasUserDataDto>();
		for (JgasUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 来場フラグの更新
	 * 
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param id
	 *            バーコード番号
	 * @param type
	 *            原票種別
	 * @throws SQLException
	 */
	public static void updateVisitorFlg(Connection conn, String id, String type)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		if ("preentry".equals(type)) {
			sql = "update preentry set V_VISITOR_FLG = \"1\" where V_VID = \""
					+ id + "\";";
		} else {
			sql = "update appointedday set V_VISITOR_FLG = \"1\" where V_VID = \""
					+ id + "\";";
		}
		stmt.executeUpdate(sql);
		stmt.close();
	}

}