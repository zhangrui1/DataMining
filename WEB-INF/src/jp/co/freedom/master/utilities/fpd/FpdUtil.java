package jp.co.freedom.master.utilities.fpd;

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
import jp.co.freedom.master.dto.fpd.FpdCardDto;
import jp.co.freedom.master.dto.fpd.FpdQuestionDto;
import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * FPD向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdUtil extends Util {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<FpdUserDataDto> createInstance(List<String[]> csvData) {
		List<FpdUserDataDto> userData = new ArrayList<FpdUserDataDto>();// ユーザーデータを保持するリスト
		FpdUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2 && row[1].length() == 4) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new FpdUserDataDto();
				}
				// リクエストコードの格納
				String request = row[1].substring(1);
				dataDto.requestCode.add(request);
			} else if (row.length == 2 && row[1].length() == 6) {// アンケートコードの行
				if (dataDto == null) {
					dataDto = new FpdUserDataDto();
				}
				// アンケートコードの格納
				String enquete = row[1].substring(1);
				dataDto.enqueteCode.add(enquete);
			} else if (row.length == 3) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new FpdUserDataDto();
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
	 * 変換表のインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<FpdUserDataDto> createInstanceForConvertTable(
			List<String[]> csvData) {
		List<FpdUserDataDto> userData = new ArrayList<FpdUserDataDto>();// ユーザーデータを保持するリスト
		FpdUserDataDto dataDto = null;
		boolean abort = false; // 読み飛ばしフラグ
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			if (row.length == 1 && row[0].length() == 11) {// 発券番号の行
				if (abort) {
					continue;
				}
				if (dataDto == null) {
					dataDto = new FpdUserDataDto();
				}
				// 発券番号の格納
				dataDto.preEntryId = row[0];
			} else if (row.length == 1 && row[0].length() == 6) {// RFID番号の行
				if (abort) {
					abort = false;
				}
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new FpdUserDataDto();
				dataDto.id = row[0];
			} else {
				// 読み飛ばし
				abort = true;
				continue;
			}
		}
		// 最後尾のユーザーデータのリスト格納
		if (dataDto != null) {
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * 出展者貸出バーコード向けメールアドレス変換表のインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static Map<String, String> createInstanceForEmailConverter(
			List<String[]> csvData) {
		Map<String, String> map = new HashMap<String, String>();
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			String barcode = row[0];
			String email = row[1];
			if (StringUtil.isNotEmpty(barcode) && StringUtil.isNotEmpty(email)) {
				map.put(barcode, email);
			}
		}
		return map;
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
	public boolean isPreEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof FpdUserDataDto;
		return StringUtil.isNotEmpty(((FpdUserDataDto) userdata).preEntryId);
	}

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            RFID番号
	 * @return 検証結果のブール値
	 */
	public boolean isAppEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof FpdUserDataDto
				&& StringUtil.isNotEmpty(userdata.id);
		return StringUtil.isEmpty(((FpdUserDataDto) userdata).preEntryId);
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
	public static FpdUserDataDto getPreRegistData(Connection conn, String rfid,
			String time) throws SQLException {
		FpdUserDataDto userdata = new FpdUserDataDto();
		FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
		FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry WHERE V_VID11 = '" + rfid + "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			userdata.exhibitionType = rs.getString("V_EXHIBITION_TYPE"); // 展示会種別
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ
			// 勤務先住所情報
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			// cardInfo.V_ADDR1 = getAddr1(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			normalizedAddress(cardInfo); // 都道府県の切り出し
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_DAY = getDay(time); // 来場日
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			// お知らせ区分
			questionInfo.info1 = rs.getString("V_info1");
			questionInfo.info2 = rs.getString("V_info2");
			questionInfo.info3 = rs.getString("V_info3");
			questionInfo.info4 = rs.getString("V_info4");

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
		}
		rs.close();
		stmt.close();
		return userdata;
	}

	/**
	 * 事前登録データの住所情報の正規化
	 * 
	 * @param cardInfo
	 *            <b>FpdCardDto</b>
	 * @return 住所情報を修正した<b>FpdCardDto</b>
	 */
	private static FpdCardDto normalizedAddress(FpdCardDto cardInfo) {
		if (cardInfo != null && StringUtil.isNotEmpty(cardInfo.V_ADDR2)) {
			int separator = cardInfo.V_ADDR2.indexOf("　");
			if (-1 != separator) {
				String addr1 = cardInfo.V_ADDR2.substring(0, separator);
				if (StringUtil.isNotEmpty(addr1)
						&& FpdConstants.ADDR1_MAP.containsValue(addr1)) {
					cardInfo.V_ADDR1 = addr1;
					cardInfo.V_ADDR2 = cardInfo.V_ADDR2.substring(
							separator + 1, cardInfo.V_ADDR2.length());
				}
			}
		}
		return cardInfo;
	}

	/**
	 * 事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param email
	 *            メールアドレス
	 * @param time
	 *            タイムスタンプ
	 * @return　事前登録データ
	 * @throws SQLException
	 */
	public static FpdUserDataDto getPreRegistDataByEmail(Connection conn,
			String email, String time) throws SQLException {
		FpdUserDataDto userdata = new FpdUserDataDto();
		FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
		FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry WHERE V_EMAIL = '" + email + "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			userdata.exhibitionType = rs.getString("V_EXHIBITION_TYPE"); // 展示会種別
			cardInfo.V_EMAIL = email; // メールアドレス
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ
			// 勤務先住所情報
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			normalizedAddress(cardInfo); // 都道府県の切り出し
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_DAY = getDay(time); // 来場日
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5
			// お知らせ区分
			questionInfo.info1 = rs.getString("V_info1");
			questionInfo.info2 = rs.getString("V_info2");
			questionInfo.info3 = rs.getString("V_info3");
			questionInfo.info4 = rs.getString("V_info4");

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			break;
		}
		rs.close();
		stmt.close();
		return userdata;
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
	public static FpdUserDataDto getAppointedDayData(Connection conn,
			String rfid, String time) throws SQLException {
		FpdUserDataDto userdata = new FpdUserDataDto();
		FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
		FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday WHERE V_VID = '" + rfid + "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			/* 名刺情報　 */
			cardInfo.V_VID = rfid; // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			// cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			// cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			// cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			// cardInfo.V_DEPT_KANA = rs.getString("V_DEPTKANA"); // 部署名カナ
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			// cardInfo.V_BIZ_KANA = rs.getString("V_BIZKANA"); // 役職名カナ
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			// cardInfo.V_ADDR_KANA = rs.getString("V_ADDRKANA"); // 住所カナ
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ
			cardInfo.V_DAY = getDay(time); // 来場日
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
		}
		rs.close();
		stmt.close();
		return userdata;
	}

	/**
	 * 当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param email
	 *            メールアドレス
	 * @param day
	 *            タイムスタンプ
	 * @return　当日登録データ
	 * @throws SQLException
	 */
	public static FpdUserDataDto getAppointedDayDataByEmail(Connection conn,
			String email, String time) throws SQLException {
		FpdUserDataDto userdata = new FpdUserDataDto();
		FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
		FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday WHERE V_EMAIL = '" + email
				+ "';";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			cardInfo.V_EMAIL = email; // メールアドレス
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			// cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			// cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			// cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			// cardInfo.V_DEPT_KANA = rs.getString("V_DEPTKANA"); // 部署名カナ
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			// cardInfo.V_BIZ_KANA = rs.getString("V_BIZKANA"); // 役職名カナ
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			// cardInfo.V_ADDR_KANA = rs.getString("V_ADDRKANA"); // 住所カナ
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ
			cardInfo.V_DAY = getDay(time); // 来場日
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			break;
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
	public static List<FpdUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<FpdUserDataDto> userDataList = new ArrayList<FpdUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			FpdUserDataDto userdata = new FpdUserDataDto();
			FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
			FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			// cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			// cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			// cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			// cardInfo.V_DEPT_KANA = rs.getString("V_DEPTKANA"); // 部署名カナ
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			// cardInfo.V_BIZ_KANA = rs.getString("V_BIZKANA"); // 役職名カナ
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
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
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4

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
	public static List<FpdUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<FpdUserDataDto> userDataList = new ArrayList<FpdUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			FpdUserDataDto userdata = new FpdUserDataDto();
			FpdCardDto cardInfo = new FpdCardDto(); // 名刺情報DTO
			FpdQuestionDto questionInfo = new FpdQuestionDto(); // アンケート情報DTO
			userdata.exhibitionType = rs.getString("V_EXHIBITION_TYPE"); // 展示会種別
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			userdata.preEntryId = rs.getString("V_VID11"); // 発券番号
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職
			cardInfo.priorityFlg = rs.getString("V_Priority_FLG"); // 住所優先フラグ

			// 自宅住所
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			normalizedAddress(cardInfo); // 都道府県の切り出し
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			// cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号

			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3
			questionInfo.V_Q4 = rs.getString("V_Q4"); // アンケート4
			questionInfo.V_Q5 = rs.getString("V_Q5"); // アンケート5

			// お知らせ区分
			questionInfo.info1 = rs.getString("V_info1");
			questionInfo.info2 = rs.getString("V_info2");
			questionInfo.info3 = rs.getString("V_info3");
			questionInfo.info4 = rs.getString("V_info4");

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
	 * タイムスタンプより来場日時を取得
	 * 
	 * @param time
	 *            タイムスタンプ
	 * @return 来場日
	 */
	private static String getTime(String time) {
		if (StringUtil.isNotEmpty(time)) {
			String year = time.substring(0, 4);
			String month = time.substring(4, 6);
			String day = time.substring(6, 8);
			month = month.startsWith("0") ? month.substring(1) : month;
			day = day.startsWith("0") ? day.substring(1) : day;
			String hour = time.substring(8, 10);
			String minite = time.substring(10, 12);
			String second = time.substring(12, 14);
			return year + "/" + month + "/" + day + " " + hour + ":" + minite
					+ ":" + second;
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
	 * @param isPreMaster
	 *            事前登録マスターデータ作成フラグ
	 * @param isAppMaster
	 *            当日登録マスターデータ作成フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<FpdUserDataDto> userDataList, String dim, boolean isPreMaster,
			boolean isAppMaster) throws ServletException, IOException,
			SQLException {

		// CSVの列数の決定
		int columnNum = FpdConfig.OUTPUT_ENQUETE_RESULTS_FLG ? 64 : 22;
		if (isPreMaster) {
			// 不要項目：来場日
			// 追加項目：発券番号
			columnNum = columnNum - 1 + 1;
		} else if (isAppMaster) {
			// 不要項目：来場日
			columnNum = columnNum - 1;
		}
		// 妥当性検証違反の詳細情報を出力する場合
		if (FpdConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			columnNum = columnNum + 1;
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
		header[++headerNum] = StringUtil.enquote("展示会種別");
		header[++headerNum] = StringUtil.enquote("バーコード番号");
		if (isPreMaster) {
			header[++headerNum] = StringUtil.enquote("発券番号");
		}
		header[++headerNum] = StringUtil.enquote("リーダー番号");
		if (!isPreMaster && !isAppMaster) {
			header[++headerNum] = StringUtil.enquote("来場日");
		}
		header[++headerNum] = StringUtil.enquote("海外住所フラグ");
		header[++headerNum] = StringUtil.enquote("住所優先フラグ");
		header[++headerNum] = StringUtil.enquote("原票状況不備");
		if (FpdConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header[++headerNum] = StringUtil.enquote("不備詳細");
		}
		header[++headerNum] = StringUtil.enquote("原票種別");
		// header[++headerNum] = StringUtil.enquote("国名");
		header[++headerNum] = StringUtil.enquote("原票手書き");
		header[++headerNum] = StringUtil.enquote("会社名");
		// header[++headerNum] = StringUtil.enquote("会社名（カナ）");
		header[++headerNum] = StringUtil.enquote("部署");
		// header[++headerNum] = StringUtil.enquote("部署（カナ）");
		header[++headerNum] = StringUtil.enquote("役職");
		// header[++headerNum] = StringUtil.enquote("役職（カナ）");
		header[++headerNum] = StringUtil.enquote("氏名");
		// header[++headerNum] = StringUtil.enquote("氏名（カナ）");
		header[++headerNum] = StringUtil.enquote("勤務先郵便番号");
		header[++headerNum] = StringUtil.enquote("勤務先都道府県");
		header[++headerNum] = StringUtil.enquote("勤務先住所");
		header[++headerNum] = StringUtil.enquote("勤務先ビル名");
		// header[++headerNum] = StringUtil.enquote("住所（カナ）");
		header[++headerNum] = StringUtil.enquote("電子メールアドレス");
		header[++headerNum] = StringUtil.enquote("勤務先電話番号");
		header[++headerNum] = StringUtil.enquote("勤務先FAX番号");
		// リクエストコード
		header[++headerNum] = StringUtil.enquote("リクエストコード");
		// アンケートコード
		header[++headerNum] = StringUtil.enquote("アンケートコード");

		// アンケート項目
		if (FpdConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			// Q1. あなたの勤務先の業種は？
			header[++headerNum] = StringUtil.enquote("電子機器");
			header[++headerNum] = StringUtil.enquote("電子部品・電子デバイス");
			header[++headerNum] = StringUtil.enquote("製造／検査装置");
			header[++headerNum] = StringUtil.enquote("材料");
			header[++headerNum] = StringUtil.enquote("ソフトウェア・情報通信");
			header[++headerNum] = StringUtil.enquote("自動車");
			header[++headerNum] = StringUtil.enquote("建築・住設・不動産");
			header[++headerNum] = StringUtil.enquote("商業施設・交通機関");
			header[++headerNum] = StringUtil.enquote("商社");
			header[++headerNum] = StringUtil.enquote("広告・放送・出版");
			header[++headerNum] = StringUtil.enquote("調査・コンサルティング");
			header[++headerNum] = StringUtil.enquote("官公庁・団体、学校・研究機関・学生");
			header[++headerNum] = StringUtil.enquote("その他");
			// Q2. あなたの職種は？
			header[++headerNum] = StringUtil.enquote("研究・開発");
			header[++headerNum] = StringUtil.enquote("設計");
			header[++headerNum] = StringUtil.enquote("品質管理");
			header[++headerNum] = StringUtil.enquote("生産・製造");
			header[++headerNum] = StringUtil.enquote("経営");
			header[++headerNum] = StringUtil.enquote("営業・企画");
			header[++headerNum] = StringUtil.enquote("購買");
			header[++headerNum] = StringUtil.enquote("その他");
			// Q3. あなたの役職は？
			header[++headerNum] = StringUtil.enquote("会長・社長");
			header[++headerNum] = StringUtil.enquote("役員・執行役員クラス");
			header[++headerNum] = StringUtil.enquote("局長・部長クラス");
			header[++headerNum] = StringUtil.enquote("次長・課長クラス");
			header[++headerNum] = StringUtil.enquote("係長・主任クラス");
			header[++headerNum] = StringUtil.enquote("一般社員");
			header[++headerNum] = StringUtil.enquote("その他");
			// Q4. 製品・サービス購入に際してのあなたの立場は？
			header[++headerNum] = StringUtil.enquote("検討・決定する立場");
			header[++headerNum] = StringUtil.enquote("候補を提案する立場");
			header[++headerNum] = StringUtil.enquote("情報収集する立場");
			header[++headerNum] = StringUtil.enquote("関与しない");
			// Q5. FPDからのメール送信を希望するか？
			header[++headerNum] = StringUtil.enquote("希望する");
			header[++headerNum] = StringUtil.enquote("希望しない");
			// お知らせ区分
			header[++headerNum] = StringUtil.enquote("お知らせ区分・許可");
			header[++headerNum] = StringUtil.enquote("お知らせ区分・禁止");
			header[++headerNum] = StringUtil.enquote("日経BPグループ各社からの各種ご案内・許可");
			header[++headerNum] = StringUtil.enquote("日経BPグループ各社からの各種ご案内・禁止");
			header[++headerNum] = StringUtil.enquote("日経BPグループ各社からのアンケート・許可");
			header[++headerNum] = StringUtil.enquote("日経BPグループ各社からのアンケート・禁止");
			header[++headerNum] = StringUtil
					.enquote("日経BPグループ各社以外の製品やサービス情報・許可");
			header[++headerNum] = StringUtil
					.enquote("日経BPグループ各社以外の製品やサービス情報・禁止");
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (FpdUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// 展示会種別
			String exhibitionId = getExhibitionType(userdata);
			cols[++nColumn] = StringUtil.isNotEmpty(exhibitionId) ? StringUtil
					.enquote(exhibitionId) : StringUtil.enquote("");

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// 発券番号
			if (isPreMaster) {
				cols[++nColumn] = StringUtil.isNotEmpty(userdata.preEntryId) ? StringUtil
						.enquote(userdata.preEntryId) : StringUtil.enquote("");
			}
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			if (!isPreMaster && !isAppMaster) {
				// 来場日
				cols[++nColumn] = userdata.cardInfo != null
						&& StringUtil.isNotEmpty(userdata.cardInfo.V_VID) ? StringUtil
						.enquote(getDay(userdata.timeByRfid)) : StringUtil
						.enquote(getTime(userdata.timeByRfid));
			}
			// 原票状況不備フラグ
			String lackFlg = getLackFlg(userdata);
			// 海外住所フラグ
			cols[++nColumn] = isOversea(userdata)
					&& StringUtil.isEmpty(lackFlg) ? StringUtil.enquote("1")
					: StringUtil.enquote("");
			// 住所優先フラグ
			String[] priorityBuff = null;
			if (userdata.cardInfo != null
					&& StringUtil
							.isNotEmpty(((FpdCardDto) userdata.cardInfo).priorityFlg)) {
				priorityBuff = ((FpdCardDto) userdata.cardInfo).priorityFlg
						.split(" ");
				// 前方一致(最初の方を優先)
				if (StringUtil.contains(priorityBuff, "1")) {
					cols[++nColumn] = StringUtil.enquote("勤務先");
				} else if (StringUtil.contains(priorityBuff, "2")) {
					cols[++nColumn] = StringUtil.enquote("自宅");
				} else if (StringUtil.contains(priorityBuff, "3")) {
					cols[++nColumn] = StringUtil.enquote("海外会社");
				} else if (StringUtil.contains(priorityBuff, "4")) {
					cols[++nColumn] = StringUtil.enquote("海外自宅");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			} else {
				cols[++nColumn] = StringUtil.enquote("");
			}
			// 原票状況不備
			cols[++nColumn] = StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote("");
			// 不備詳細情報
			if (FpdConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols[++nColumn] = StringUtil
						.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote("");
			}
			// 原票種別
			String type = userdata.cardInfo != null ? getTicketType(userdata)
					: "";
			cols[++nColumn] = StringUtil.isNotEmpty(type) ? StringUtil
					.enquote(type) : StringUtil.enquote("");
			// // 国名
			// if (isOversea(userdata) && isPreEntry(userdata.id)) {
			// cols[++nColumn] = StringUtil
			// .isNotEmpty(((FpdCardDto) userdata.cardInfo).V_COUNTRY) ?
			// StringUtil
			// .enquote(((FpdCardDto) userdata.cardInfo).V_COUNTRY)
			// : StringUtil.enquote("");
			// } else {
			// cols[++nColumn] = StringUtil.enquote("");
			// }
			// 原票手書き
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_TICKET_HAND) ? StringUtil
					.enquote(userdata.cardInfo.V_TICKET_HAND) : StringUtil
					.enquote("");
			// 会社名
			cols[++nColumn] = userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_CORP) ? StringUtil
					.enquote(userdata.cardInfo.V_CORP) : StringUtil.enquote("");
			// // 会社名カナ
			// cols[++nColumn] = userdata.cardInfo != null
			// && StringUtil.isNotEmpty(userdata.cardInfo.V_CORP_KANA) ?
			// StringUtil
			// .enquote(userdata.cardInfo.V_CORP_KANA) : StringUtil
			// .enquote("");
			// 部署
			String dept = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_DEPT1, userdata.cardInfo.V_DEPT2,
					userdata.cardInfo.V_DEPT3, userdata.cardInfo.V_DEPT4) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(dept) ? StringUtil
					.enquote(dept) : StringUtil.enquote("");
			// // 部署カナ
			// cols[++nColumn] = userdata.cardInfo != null
			// && StringUtil
			// .isNotEmpty(((FpdCardDto) userdata.cardInfo).V_DEPT_KANA) ?
			// StringUtil
			// .enquote(((FpdCardDto) userdata.cardInfo).V_DEPT_KANA)
			// : StringUtil.enquote("");
			// 役職
			String biz = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_BIZ1, userdata.cardInfo.V_BIZ2,
					userdata.cardInfo.V_BIZ3, userdata.cardInfo.V_BIZ4) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(biz) ? StringUtil
					.enquote(biz) : StringUtil.enquote("");
			// // 役職カナ
			// cols[++nColumn] = userdata.cardInfo != null
			// && StringUtil
			// .isNotEmpty(((FpdCardDto) userdata.cardInfo).V_BIZ_KANA) ?
			// StringUtil
			// .enquote(((FpdCardDto) userdata.cardInfo).V_BIZ_KANA)
			// : StringUtil.enquote("");
			// 氏名
			String name = userdata.cardInfo != null ? StringUtil
					.concatWithDelimit("　", userdata.cardInfo.V_NAME1,
							userdata.cardInfo.V_NAME2) : "";
			cols[++nColumn] = StringUtil.isNotEmpty(name) ? StringUtil
					.enquote(name) : StringUtil.enquote("");
			// // 氏名カナ
			// String kana = userdata.cardInfo != null ? StringUtil
			// .concatWithDelimit("　", userdata.cardInfo.V_NAMEKANA1,
			// userdata.cardInfo.V_NAMEKANA2) : "";
			// cols[++nColumn] = StringUtil.isNotEmpty(kana) ? StringUtil
			// .enquote(kana) : StringUtil.enquote("");

			// 住所項目
			outputAddressData(cols, userdata, nColumn);
			nColumn += 7;

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
			cols[++nColumn] = StringUtil.isNotEmpty(sb.toString()) ? StringUtil
					.enquote(sb.toString()) : StringUtil.enquote("");

			// アンケートコード
			List<String> enqueteCodes = userdata.enqueteCode;
			String enquete = null;
			if (enqueteCodes != null) {
				enquete = StringUtil.concatWithDelimit(",", enqueteCodes);
			}
			cols[++nColumn] = StringUtil.isNotEmpty(enquete) ? StringUtil
					.enquote(enquete) : StringUtil.enquote("");

			// アンケート項目
			if (FpdConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				outputEnqueteData(cols, userdata, nColumn);
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
	 * アンマッチバーコード番号リストをTXT形式でダウンロード
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
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForUnmatch(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<FpdUserDataDto> userDataList, String dim)
			throws ServletException, IOException, SQLException {

		// CSVの列数の決定
		int columnNum = 3;

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
		header[++headerNum] = StringUtil.enquote("来場日");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (FpdUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			// 来場日
			String time = getTime(userdata.timeByRfid);
			cols[++nColumn] = StringUtil.isNotEmpty(time) ? StringUtil
					.enquote(time) : StringUtil.enquote("");

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
	 * 6桁→11桁のバーコード対応表をTXT形式でダウンロード
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
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForConverter(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<FpdUserDataDto> userDataList, String dim)
			throws ServletException, IOException, SQLException {

		// CSVの列数の決定
		int columnNum = 2;

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
		header[++headerNum] = StringUtil.enquote("6桁");
		header[++headerNum] = StringUtil.enquote("11桁");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (FpdUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// 発券番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.preEntryId) ? StringUtil
					.enquote(userdata.preEntryId) : StringUtil.enquote("");

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
	 * 住所項目の出力（事前登録マスターデータ作成モード以外の場合）
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>FpdUserDataDto</b>
	 * @param nColumn
	 *            出力バッファにおける出力位置
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static String[] outputAddressData(String cols[],
			FpdUserDataDto userdata, int nColumn) {
		if (userdata.cardInfo == null) { // 住所情報が存在しない場合
			// 郵便番号
			cols[++nColumn] = StringUtil.enquote("");
			// 都道府県
			cols[++nColumn] = StringUtil.enquote("");
			// 住所(市区郡+町域+番号他)
			cols[++nColumn] = StringUtil.enquote("");
			// ビル名
			cols[++nColumn] = StringUtil.enquote("");
			// // 住所カナ
			// cols[++nColumn] = StringUtil.enquote("");
			// メールアドレス
			cols[++nColumn] = StringUtil.enquote("");
			// 電話番号
			cols[++nColumn] = StringUtil.enquote("");
			// FAX番号
			cols[++nColumn] = StringUtil.enquote("");
		} else {
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
			// // 住所カナ
			// cols[++nColumn] = userdata.cardInfo != null
			// && StringUtil
			// .isNotEmpty(((FpdCardDto) userdata.cardInfo).V_ADDR_KANA) ?
			// StringUtil
			// .enquote(((FpdCardDto) userdata.cardInfo).V_ADDR_KANA)
			// : StringUtil.enquote("");
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
		}
		return cols;
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>FpdUserDataDto</b>
	 * @param nColumn
	 *            出力バッファにおける出力位置
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static String[] outputEnqueteData(String cols[],
			FpdUserDataDto userdata, int nColumn) {
		// Q1. あなたの勤務先の業種は？
		String[] q1Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q1)) {
			q1Buff = userdata.questionInfo.V_Q1.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q1Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "3") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "4") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "5") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "6") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "7") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "8") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "9") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "10") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "11") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "12") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q1Buff, "13") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// Q2. あなたの職種は？
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
		cols[++nColumn] = StringUtil.contains(q2Buff, "6") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "7") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q2Buff, "8") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// Q3. あなたの役職は？
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

		// Q4. 製品・サービス購入に関してのあなたの立場は？
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

		// Q5. FPDからの情報をお送りしてもよいですか？
		String[] q5Buff = null;
		if (userdata.questionInfo != null
				&& StringUtil.isNotEmpty(userdata.questionInfo.V_Q5)) {
			q5Buff = userdata.questionInfo.V_Q5.split(" ");
		}
		cols[++nColumn] = StringUtil.contains(q5Buff, "1") ? StringUtil
				.enquote("1") : StringUtil.enquote("");
		cols[++nColumn] = StringUtil.contains(q5Buff, "2") ? StringUtil
				.enquote("1") : StringUtil.enquote("");

		// お知らせ区分
		String info1 = ((FpdQuestionDto) userdata.questionInfo).info1;
		if ("1".equals(info1)) {
			cols[++nColumn] = StringUtil.enquote("1");
			cols[++nColumn] = StringUtil.enquote("");
		} else if ("2".equals(info1)) {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("1");
		} else {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("");
		}
		// 日経BPグループ各社からの各種ご案内
		String info2 = ((FpdQuestionDto) userdata.questionInfo).info2;
		if ("1".equals(info2)) {
			cols[++nColumn] = StringUtil.enquote("1");
			cols[++nColumn] = StringUtil.enquote("");
		} else if ("2".equals(info2)) {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("1");
		} else {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("");
		}
		// 日経BPグループ各社からのアンケート
		String info3 = ((FpdQuestionDto) userdata.questionInfo).info3;
		if ("1".equals(info3)) {
			cols[++nColumn] = StringUtil.enquote("1");
			cols[++nColumn] = StringUtil.enquote("");
		} else if ("2".equals(info3)) {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("1");
		} else {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("");
		}
		// 日経BPグループ各社以外の製品やサービス情報
		String info4 = ((FpdQuestionDto) userdata.questionInfo).info4;
		if ("1".equals(info4)) {
			cols[++nColumn] = StringUtil.enquote("1");
			cols[++nColumn] = StringUtil.enquote("");
		} else if ("2".equals(info4)) {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("1");
		} else {
			cols[++nColumn] = StringUtil.enquote("");
			cols[++nColumn] = StringUtil.enquote("");
		}
		return cols;
	}

	/**
	 * 展示会種別の特定
	 * 
	 * @param userdata
	 *            <b>FpdCardDto</b>
	 * @return 展示会種別
	 */
	private static String getExhibitionType(UserDataDto userdata) {
		FpdUtil util = new FpdUtil();
		String key;
		if (util.isAppEntry(userdata)) {
			key = userdata.cardInfo.V_IMAGE_PATH;
		} else {
			key = ((FpdUserDataDto) userdata).exhibitionType;
		}
		if (StringUtil.isEmpty(key)) {
			return "";
		}
		if (key.startsWith("toj")) {
			key = key.substring(3);
		}
		if (key.startsWith("fpd")) {
			return FpdConfig.EXHIBITION_TYPE_FPD;
		} else if (key.startsWith("scw")) {
			return FpdConfig.EXHIBITION_TYPE_SCW;
		} else if (key.startsWith("tma")) {
			return FpdConfig.EXHIBITION_TYPE_TMA;
		} else {
			return "";
		}
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            <b>FpdCardDto</b>
	 * @return 原票種別
	 */
	private static String getTicketType(UserDataDto userdata) {
		FpdUtil util = new FpdUtil();
		String key = userdata.cardInfo.V_IMAGE_PATH;
		if (StringUtil.isNotEmpty(key)) { // 当日登録データである場合
			return key.startsWith("toj") ? FpdConfig.TICKET_TYPE_APPOINTEDDAY
					: FpdConfig.TICKET_TYPE_INVITATION;
		} else if (util.isPreEntry(userdata)) { // 事前登録データである場合
			return FpdConfig.TICKET_TYPE_PREENTRY;
		}
		return "";
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>FpdUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(FpdUserDataDto userdata) {
		FpdValidator validator = new FpdValidator();
		validator.validate(userdata, "1"); // ワイルドカード(■)の存在チェック
		validator.validate(userdata, "2"); // 氏名、連絡先情報に対する妥当性検証
		boolean lack1Flg = !validator.getResult1(); // 不備フラグ1
		boolean lack2Flg = !validator.getResult2(); // 不備フラグ2
		userdata.validationErrResult = validator.getResultDetail(); // タイプ2に対する妥当性検証違反の詳細情報を格納
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
	 *            <b>FpdCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(FpdUserDataDto userdata) {
		FpdUtil util = new FpdUtil();
		if (userdata.cardInfo != null) {
			boolean result;
			// 海外住所である否かの検証
			if (util.isPreEntry(userdata)) { // 事前登録データである場合
				result = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
			} else { // 当日登録データである場合
				boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
				boolean check2 = StringUtil
						.isNotEmpty(userdata.cardInfo.V_ADDR2);
				boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
				boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
				boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
				result = check1 && check2 && check3 && check4 && check5;
			}
			return result;
		}
		return false;
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
			if (FpdConstants.READER_ULVAC_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_ULVAC.get(code);
			} else if (FpdConstants.READER_AKT_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_AKT.get(code);
			} else if (FpdConstants.READER_NANOSCIENCE_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_NANOSCIENCE.get(code);
			} else if (FpdConstants.READER_JSR_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_JSR.get(code);
			} else if (FpdConstants.READER_CYOUSYUU_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_CYOUSYUU.get(code);
			} else if (FpdConstants.READER_DEXERIALS_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_DEXERIALS.get(code);
			} else if (FpdConstants.READER_TOYOSHIMA_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_TOYOSHIMA.get(code);
			} else if (FpdConstants.READER_TAKATORI_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_TAKATORI.get(code);
			} else if (FpdConstants.READER_SEL_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_SEL.get(code);
			} else if (FpdConstants.READER_PANASONIC_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_PANASONIC.get(code);
			} else if (FpdConstants.READER_CKB_LIST.contains(reader)) {
				value = FpdConstants.REQUESTCODES_CKB.get(code);
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
	 * 【バーコードマッチング高速化対応】当日登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>FpdUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, FpdUserDataDto> getAppointedDayMap(
			List<FpdUserDataDto> userDataList) {
		Map<String, FpdUserDataDto> map = new HashMap<String, FpdUserDataDto>();
		for (FpdUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 【バーコードマッチング高速化対応】事前登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>FpdUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, FpdUserDataDto> getPreEntryMap(
			List<FpdUserDataDto> userDataList) {
		Map<String, FpdUserDataDto> map = new HashMap<String, FpdUserDataDto>();
		for (FpdUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.preEntryId)) {
				map.put(userData.preEntryId, userData);
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
			sql = "update preentry set V_VISITOR_FLG = \"1\" where V_VID11 = \""
					+ id + "\";";
		} else {
			sql = "update appointedday set V_VISITOR_FLG = \"1\" where V_VID = \""
					+ id + "\";";
		}
		stmt.executeUpdate(sql);
		stmt.close();
	}

	/**
	 * 事前登録の6桁バーコード番号の登録
	 * 
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param userdata
	 *            <b>FpdUserDataDto</b>
	 * @throws SQLException
	 */
	public static boolean updatePreEntryId(Connection conn,
			FpdUserDataDto userdata) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "update preentry set V_VID = '" + userdata.id
				+ "' where V_VID11 = '" + userdata.preEntryId + "';";
		int result = stmt.executeUpdate(sql);
		stmt.close();
		return result == 1;
	}

}