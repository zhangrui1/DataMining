package jp.co.freedom.master.utilities.scf;

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

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.scf.ScfCardDto;
import jp.co.freedom.master.dto.scf.ScfExhibitorDataDto;
import jp.co.freedom.master.dto.scf.ScfExhibitorMasterDto;
import jp.co.freedom.master.dto.scf.ScfQuestionDto;
import jp.co.freedom.master.dto.scf.ScfUserDataDto;
import jp.co.freedom.master.dto.scf.ScfYokogawaQuestionDto;
import jp.co.freedom.master.utilities.Util;

/**
 * SCF向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfUtil extends Util {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<ScfUserDataDto> createInstance(List<String[]> csvData) {
		List<ScfUserDataDto> userData = new ArrayList<ScfUserDataDto>();// ユーザーデータを保持するリスト
		ScfUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new ScfUserDataDto();
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
				dataDto = new ScfUserDataDto();
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
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isPreEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof ScfUserDataDto;
		if (StringUtil.isNotEmpty(userdata.id)) {
			return userdata.id.startsWith(ScfConfig.PREENTRY_BARCODE_START_BIT);
		} else {
			return false;
		}
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
		return id.startsWith(ScfConfig.PREENTRY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isAppEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof ScfUserDataDto;
		if (StringUtil.isNotEmpty(userdata.id)) {
			return !userdata.id
					.startsWith(ScfConfig.PREENTRY_BARCODE_START_BIT);
		} else {
			return false;
		}
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
		return !id.startsWith(ScfConfig.PREENTRY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDがロボット展からの来場者であるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public static boolean isRobotEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof ScfUserDataDto;
		if (StringUtil.isNotEmpty(userdata.id)) {
			return userdata.id
					.startsWith(ScfConfig.ROBOTENTRY_BARCODE_START_BIT);
		} else {
			return false;
		}
	}

	/**
	 * 指定IDがロボット展からの来場者であるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public static boolean isRobotEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(ScfConfig.ROBOTENTRY_BARCODE_START_BIT);
	}

	/**
	 * 全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ScfUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<ScfUserDataDto> userDataList = new ArrayList<ScfUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ScfUserDataDto userdata = new ScfUserDataDto();
			ScfCardDto cardInfo = new ScfCardDto(); // 名刺情報DTO
			ScfQuestionDto questionInfo = new ScfQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1"); // アンケート1
			questionInfo.V_Q2 = rs.getString("V_Q2"); // アンケート2
			questionInfo.V_Q3 = rs.getString("V_Q3"); // アンケート3

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * [Yokogawa]全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ScfUserDataDto> getAllAppointedDayDataForYokogawa(
			Connection conn) throws SQLException {
		List<ScfUserDataDto> userDataList = new ArrayList<ScfUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM yokogawa;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ScfUserDataDto userdata = new ScfUserDataDto();
			ScfCardDto cardInfo = new ScfCardDto(); // 名刺情報DTO
			ScfYokogawaQuestionDto questionInfo = new ScfYokogawaQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			// cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			/* アンケート情報 */
			questionInfo.Q1_1 = rs.getString("Q1_1");
			questionInfo.Q1_2 = rs.getString("Q1_2");
			questionInfo.Q1_FA = rs.getString("Q1_FA");
			questionInfo.Q2_1 = rs.getString("Q2_1");
			questionInfo.Q2_2 = rs.getString("Q2_2");
			questionInfo.Q2_FA = rs.getString("Q2_FA");
			questionInfo.Q3_1 = rs.getString("Q3_1");
			questionInfo.Q3_2 = rs.getString("Q3_2");
			questionInfo.Q3_FA = rs.getString("Q3_FA");
			questionInfo.Q4_1 = rs.getString("Q4_1");
			questionInfo.Q4_2 = rs.getString("Q4_2");
			questionInfo.Q4_FA = rs.getString("Q4_FA");
			questionInfo.Q5_1 = rs.getString("Q5_1");
			questionInfo.Q5_2 = rs.getString("Q5_2");
			questionInfo.Q5_FA = rs.getString("Q5_FA");
			questionInfo.Q6_1 = rs.getString("Q6_1");
			questionInfo.Q6_2 = rs.getString("Q6_2");
			questionInfo.Q6_FA = rs.getString("Q6_FA");
			questionInfo.Q7_1 = rs.getString("Q7_1");
			questionInfo.Q7_2 = rs.getString("Q7_2");
			questionInfo.Q7_FA = rs.getString("Q7_FA");
			questionInfo.Q8_1 = rs.getString("Q8_1");
			questionInfo.Q8_2 = rs.getString("Q8_2");
			questionInfo.Q8_FA = rs.getString("Q8_FA");
			questionInfo.Q9_1 = rs.getString("Q9_1");
			questionInfo.Q9_2 = rs.getString("Q9_2");
			questionInfo.Q9_FA = rs.getString("Q9_FA");
			questionInfo.Q10_1 = rs.getString("Q10_1");
			questionInfo.Q10_2 = rs.getString("Q10_2");
			questionInfo.Q10_FA = rs.getString("Q10_FA");
			questionInfo.Q11_1 = rs.getString("Q11_1");
			questionInfo.Q11_2 = rs.getString("Q11_2");
			questionInfo.Q11_FA = rs.getString("Q11_FA");
			questionInfo.Q12_1 = rs.getString("Q12_1");
			questionInfo.Q12_2 = rs.getString("Q12_2");
			questionInfo.Q12_FA = rs.getString("Q12_FA");
			questionInfo.Q13_1 = rs.getString("Q13_1");
			questionInfo.Q13_2 = rs.getString("Q13_2");
			questionInfo.Q13_FA = rs.getString("Q13_FA");
			questionInfo.Q14_1 = rs.getString("Q14_1");
			questionInfo.Q14_2 = rs.getString("Q14_2");
			questionInfo.Q14_FA = rs.getString("Q14_FA");
			questionInfo.Q15_1 = rs.getString("Q15_1");
			questionInfo.Q15_2 = rs.getString("Q15_2");
			questionInfo.Q15_FA = rs.getString("Q15_FA");
			questionInfo.Q16_1 = rs.getString("Q16_1");
			questionInfo.Q16_2 = rs.getString("Q16_2");
			questionInfo.Q16_FA = rs.getString("Q16_FA");
			questionInfo.Q17_1 = rs.getString("Q17_1");
			questionInfo.Q17_2 = rs.getString("Q17_2");
			questionInfo.Q17_FA = rs.getString("Q17_FA");
			questionInfo.Q18_1 = rs.getString("Q18_1");
			questionInfo.Q18_2 = rs.getString("Q18_2");
			questionInfo.Q18_FA = rs.getString("Q18_FA");
			questionInfo.Q19_1 = rs.getString("Q19_1");
			questionInfo.Q19_2 = rs.getString("Q19_2");
			questionInfo.Q19_FA = rs.getString("Q19_FA");
			questionInfo.Q20_1 = rs.getString("Q20_1");
			questionInfo.Q20_2 = rs.getString("Q20_2");
			questionInfo.Q20_FA = rs.getString("Q20_FA");
			questionInfo.Q21_1 = rs.getString("Q21_1");
			questionInfo.Q21_2 = rs.getString("Q21_2");
			questionInfo.Q21_FA = rs.getString("Q21_FA");
			questionInfo.Q22_1 = rs.getString("Q22_1");
			questionInfo.Q22_2 = rs.getString("Q22_2");
			questionInfo.Q22_FA = rs.getString("Q22_FA");
			questionInfo.youbou = rs.getString("youbou");
			questionInfo.kubun = rs.getString("kubun");
			questionInfo.eigyou = rs.getString("eigyou");
			questionInfo.eigyou_fa = rs.getString("eigyou_fa");
			questionInfo.dairiten = rs.getString("dairiten");
			questionInfo.sentan = rs.getString("sentan");
			questionInfo.sentan_fa = rs.getString("sentan_fa");
			questionInfo.counter = rs.getString("counter");
			questionInfo.comment = rs.getString("comment");

			userdata.masterCardDataExist = rs.getString("EXIST");
			userdata.masterEnqueteDataExist = rs.getString("EXIST_ENQUETE");
			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * [Yokogawa]名刺情報の取得
	 * 
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param id
	 *            バーコード番号
	 * @throws SQLException
	 */
	public static ScfCardDto getCardInfo(Connection conn, String id)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		if (isRobotEntry(id)) {
			sql = "SELECT * FROM robot where V_VID='" + id + "';";
		} else if (isAppEntry(id)) {
			sql = "SELECT * FROM appointedday where V_VID='" + id + "';";
		} else {
			sql = "SELECT * FROM preentry where V_VID='" + id + "';";
		}
		ResultSet rs = stmt.executeQuery(sql);
		ScfCardDto cardInfo = new ScfCardDto();
		if (isRobotEntry(id)) {
			while (rs.next()) {
				cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
				cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
				cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
				cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
				cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
				cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
				cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
				cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
				cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
				cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
				cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
				cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
				cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
				cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
				cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
				cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
				((ScfCardDto) cardInfo).robotInvalidFlg = rs
						.getString("V_fubi"); // 原票不備フラグ
				((ScfCardDto) cardInfo).robotOverseaFlg = rs
						.getString("V_oversea"); // 海外住所フラグ
			}
		} else if (isAppEntry(id)) {
			while (rs.next()) {
				cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
				cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
				cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
				cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
				cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
				cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
				cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
				cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
				cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
				cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
				cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
				cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
				cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
				cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
				cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
				cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
				cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
				cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
				cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
				cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
				cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
				// cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
				cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			}
		} else {
			while (rs.next()) {
				cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
				cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
				cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
				cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
				cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署1
				cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職1
				cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
				String addr1Id = rs.getString("V_ADDR1");
				if (StringUtil.isNotEmpty(addr1Id)) {
					cardInfo.V_ADDR1 = ScfConstants.ADDR1_MAP.get(addr1Id); // 都道府県
				}
				cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
				cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
				cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
				cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
				cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
				cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
				cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
				// cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
				// cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			}
		}
		rs.close();
		stmt.close();
		return cardInfo;
	}

	/**
	 * [Yokogawa]アンケート情報の取得
	 * 
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param counter
	 *            アンケートの[Counter]
	 * 
	 * @throws SQLException
	 */
	public static ScfYokogawaQuestionDto getQuestionInfo(Connection conn,
			String counter) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM yokogawa_enquete where counter='" + counter
				+ "';";
		ResultSet rs = stmt.executeQuery(sql);
		ScfYokogawaQuestionDto questionInfo = new ScfYokogawaQuestionDto();
		while (rs.next()) {
			questionInfo.counter = counter;
			questionInfo.Q1_1 = rs.getString("Q1_1");
			questionInfo.Q1_2 = rs.getString("Q1_2");
			questionInfo.Q1_FA = rs.getString("Q1_FA");
			questionInfo.Q2_1 = rs.getString("Q2_1");
			questionInfo.Q2_2 = rs.getString("Q2_2");
			questionInfo.Q2_FA = rs.getString("Q2_FA");
			questionInfo.Q3_1 = rs.getString("Q3_1");
			questionInfo.Q3_2 = rs.getString("Q3_2");
			questionInfo.Q3_FA = rs.getString("Q3_FA");
			questionInfo.Q4_1 = rs.getString("Q4_1");
			questionInfo.Q4_2 = rs.getString("Q4_2");
			questionInfo.Q4_FA = rs.getString("Q4_FA");
			questionInfo.Q5_1 = rs.getString("Q5_1");
			questionInfo.Q5_2 = rs.getString("Q5_2");
			questionInfo.Q5_FA = rs.getString("Q5_FA");
			questionInfo.Q6_1 = rs.getString("Q6_1");
			questionInfo.Q6_2 = rs.getString("Q6_2");
			questionInfo.Q6_FA = rs.getString("Q6_FA");
			questionInfo.Q7_1 = rs.getString("Q7_1");
			questionInfo.Q7_2 = rs.getString("Q7_2");
			questionInfo.Q7_FA = rs.getString("Q7_FA");
			questionInfo.Q8_1 = rs.getString("Q8_1");
			questionInfo.Q8_2 = rs.getString("Q8_2");
			questionInfo.Q8_FA = rs.getString("Q8_FA");
			questionInfo.Q9_1 = rs.getString("Q9_1");
			questionInfo.Q9_2 = rs.getString("Q9_2");
			questionInfo.Q9_FA = rs.getString("Q9_FA");
			questionInfo.Q10_1 = rs.getString("Q10_1");
			questionInfo.Q10_2 = rs.getString("Q10_2");
			questionInfo.Q10_FA = rs.getString("Q10_FA");
			questionInfo.Q11_1 = rs.getString("Q11_1");
			questionInfo.Q11_2 = rs.getString("Q11_2");
			questionInfo.Q11_FA = rs.getString("Q11_FA");
			questionInfo.Q12_1 = rs.getString("Q12_1");
			questionInfo.Q12_2 = rs.getString("Q12_2");
			questionInfo.Q12_FA = rs.getString("Q12_FA");
			questionInfo.Q13_1 = rs.getString("Q13_1");
			questionInfo.Q13_2 = rs.getString("Q13_2");
			questionInfo.Q13_FA = rs.getString("Q13_FA");
			questionInfo.Q14_1 = rs.getString("Q14_1");
			questionInfo.Q14_2 = rs.getString("Q14_2");
			questionInfo.Q14_FA = rs.getString("Q14_FA");
			questionInfo.Q15_1 = rs.getString("Q15_1");
			questionInfo.Q15_2 = rs.getString("Q15_2");
			questionInfo.Q15_FA = rs.getString("Q15_FA");
			questionInfo.Q16_1 = rs.getString("Q16_1");
			questionInfo.Q16_2 = rs.getString("Q16_2");
			questionInfo.Q16_FA = rs.getString("Q16_FA");
			questionInfo.Q17_1 = rs.getString("Q17_1");
			questionInfo.Q17_2 = rs.getString("Q17_2");
			questionInfo.Q17_FA = rs.getString("Q17_FA");
			questionInfo.Q18_1 = rs.getString("Q18_1");
			questionInfo.Q18_2 = rs.getString("Q18_2");
			questionInfo.Q18_FA = rs.getString("Q18_FA");
			questionInfo.Q19_1 = rs.getString("Q19_1");
			questionInfo.Q19_2 = rs.getString("Q19_2");
			questionInfo.Q19_FA = rs.getString("Q19_FA");
			questionInfo.Q20_1 = rs.getString("Q20_1");
			questionInfo.Q20_2 = rs.getString("Q20_2");
			questionInfo.Q20_FA = rs.getString("Q20_FA");
			questionInfo.Q21_1 = rs.getString("Q21_1");
			questionInfo.Q21_2 = rs.getString("Q21_2");
			questionInfo.Q21_FA = rs.getString("Q21_FA");
			questionInfo.Q22_1 = rs.getString("Q22_1");
			questionInfo.Q22_2 = rs.getString("Q22_2");
			questionInfo.Q22_FA = rs.getString("Q22_FA");
			questionInfo.youbou = rs.getString("youbou");
			questionInfo.kubun = rs.getString("kubun");
			questionInfo.eigyou = rs.getString("eigyou");
			questionInfo.eigyou_fa = rs.getString("eigyou_fa");
			questionInfo.dairiten = rs.getString("dairiten");
			questionInfo.sentan = rs.getString("sentan");
			questionInfo.sentan_fa = rs.getString("sentan_fa");
			questionInfo.comment = rs.getString("comment");
		}
		rs.close();
		stmt.close();
		return questionInfo;
	}

	/**
	 * 全てのロボット展登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全てのロボット展登録データ
	 * @throws SQLException
	 */
	public static List<ScfUserDataDto> getAllRobotData(Connection conn)
			throws SQLException {
		List<ScfUserDataDto> userDataList = new ArrayList<ScfUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM robot;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ScfUserDataDto userdata = new ScfUserDataDto();
			ScfCardDto cardInfo = new ScfCardDto(); // 名刺情報DTO
			ScfQuestionDto questionInfo = new ScfQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			((ScfCardDto) cardInfo).robotInvalidFlg = rs.getString("V_fubi"); // 原票不備フラグ
			((ScfCardDto) cardInfo).robotOverseaFlg = rs.getString("V_oversea"); // 海外住所フラグ
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("Q2"); // アンケート1 業種
			questionInfo.V_Q2 = rs.getString("Q3"); // アンケート2 職種
			// questionInfo.V_Q3 = rs.getString("Q3"); // アンケート3
			// questionInfo.V_Q4 = rs.getString("Q4"); // アンケート4
			// questionInfo.V_Q5 = rs.getString("Q5"); // アンケート5
			// questionInfo.V_Q6 = rs.getString("Q6"); // アンケート6
			// questionInfo.V_Q7 = rs.getString("Q7"); // アンケート7
			// questionInfo.V_Q8 = rs.getString("Q8"); // アンケート8
			// questionInfo.V_Q9 = rs.getString("Q9"); // アンケート9
			// questionInfo.V_Q10 = rs.getString("Q10"); // アンケート10
			// questionInfo.V_Q11 = rs.getString("Q11"); // アンケート11
			// questionInfo.V_Q12 = rs.getString("Q12"); // アンケート12
			// questionInfo.V_Q13 = rs.getString("Q13"); // アンケート13
			// questionInfo.V_Q14 = rs.getString("Q14"); // アンケート14
			// questionInfo.V_Q15 = rs.getString("Q15"); // アンケート15
			// questionInfo.V_Q16 = rs.getString("Q16"); // アンケート16
			// questionInfo.V_Q17 = rs.getString("Q17"); // アンケート17
			// questionInfo.V_Q18 = rs.getString("Q18"); // アンケート18
			// questionInfo.V_Q19 = rs.getString("Q19"); // アンケート19
			// questionInfo.V_Q20 = rs.getString("Q20"); // アンケート20

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
	public static List<ScfUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<ScfUserDataDto> userDataList = new ArrayList<ScfUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ScfUserDataDto userdata = new ScfUserDataDto();
			ScfCardDto cardInfo = new ScfCardDto(); // 名刺情報DTO
			ScfQuestionDto questionInfo = new ScfQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			String countryId = rs.getString("V_COUNTRY");
			if (StringUtil.isNotEmpty(countryId)) {
				cardInfo.V_COUNTRY = ScfConstants.COUNTRY_MAP.get(countryId); // 国名
			}
			String addr1Id = rs.getString("V_ADDR1");
			if (StringUtil.isNotEmpty(addr1Id)) {
				cardInfo.V_ADDR1 = ScfConstants.ADDR1_MAP.get(addr1Id); // 都道府県
			}
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区部
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
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
	 * タイムスタンプより来場日を取得
	 * 
	 * @param time
	 *            タイムスタンプ
	 * @return 来場日
	 */
	public static String getDay(String time) {
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
	 * @param Yokogawa
	 *            加古川電機フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<ScfUserDataDto> userDataList, String dim, boolean isPreMaster,
			boolean isAppMaster, boolean Yokogawa) throws ServletException,
			IOException, SQLException {

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
		List<String> header = new ArrayList<String>();

		if (ScfConfig.OUTPUT_BOOTH_NUMBER_FLG) {
			header.add(StringUtil.enquote("ブース番号"));
		}
		header.add(StringUtil.enquote("バーコード番号"));
		header.add(StringUtil.enquote("リーダー番号"));
		if (!isPreMaster && !isAppMaster) {
			header.add(StringUtil.enquote("来場日"));
		}
		header.add(StringUtil.enquote("海外住所フラグ"));
		header.add(StringUtil.enquote("原票状況不備"));
		if (ScfConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}
		header.add(StringUtil.enquote("原票種別"));
		header.add(StringUtil.enquote("原票手書き"));
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("住所"));
		header.add(StringUtil.enquote("ビル名"));
		header.add(StringUtil.enquote("電子メールアドレス"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));

		// アンケート項目
		if (Yokogawa) { // 加古川電機である場合
			for (int nIndex = 1; nIndex <= 22; nIndex++) {
				header.add(StringUtil.enquote("興味あり" + String.valueOf(nIndex)));
				header.add(StringUtil.enquote("説明希望" + String.valueOf(nIndex)));
				header.add(StringUtil.enquote("導入検討" + String.valueOf(nIndex)));
			}
			for (int nIndex = 1; nIndex <= 40; nIndex++) {
				header.add(StringUtil.enquote("興味のある製品・サービス"
						+ String.valueOf(nIndex)));
			}
			header.add(StringUtil.enquote("意見・要望"));
			header.add(StringUtil.enquote("区分"));
			header.add(StringUtil.enquote("営業"));
			header.add(StringUtil.enquote("代理店"));
			header.add(StringUtil.enquote("先端ディスクリートゾーン専用"));
			header.add(StringUtil.enquote("e"));
			header.add(StringUtil.enquote("Counter"));
			header.add(StringUtil.enquote("コメント"));
		} else if (ScfConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("目的"));
			header.add(StringUtil.enquote("業種"));
			header.add(StringUtil.enquote("職種"));
		}

		// リクエストコード
		if (!Yokogawa) {
			header.add(StringUtil.enquote("リクエストコード"));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (ScfUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// ブース番号
			if (ScfConfig.OUTPUT_BOOTH_NUMBER_FLG) {
				String boothNo = ScfConstants.BOOTH_MAP.get(userdata.reader);
				cols.add(StringUtil.enquote(boothNo));
			}
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			// リーダー番号
			cols.add(StringUtil.enquote(userdata.reader));
			// 来場日
			if (!isPreMaster && !isAppMaster) {
				cols.add(StringUtil.enquote(getDay(userdata.timeByRfid)));
			}
			String lackFlg = getLackFlg(userdata);// 原票状況不備フラグ
			// 海外住所フラグ
			cols.add(isOversea(userdata) && StringUtil.isEmpty(lackFlg) ? StringUtil
					.enquote("1") : StringUtil.enquote(""));
			// 原票状況不備
			cols.add(StringUtil.enquote(lackFlg));
			// 不備詳細情報
			if (ScfConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.enquote(userdata.validationErrResult));
			}
			// 原票種別
			cols.add(StringUtil.enquote(getTicketType(userdata)));
			// 原票手書き
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TICKET_HAND));
			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			if (Yokogawa) {
				outputYokogawaEnqueteData(cols, userdata);
			} else if (ScfConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				if (isRobotEntry(userdata)) {
					cols.add(StringUtil.enquote("ロボット展"));
					cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1));
					cols.add(StringUtil.enquote(userdata.questionInfo.V_Q2));
				} else {
					outputEnqueteData(cols, userdata);
				}
			}

			if (!Yokogawa) {
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
				cols.add(StringUtil.enquote(sb.toString()));
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
			List<ScfUserDataDto> userDataList, String dim)
			throws ServletException, IOException, SQLException {

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
		// String[] header = new String[columnNum];
		List<String> header = new ArrayList<String>();

		header.add(StringUtil.enquote("バーコード番号"));
		header.add(StringUtil.enquote("リーダー番号"));
		header.add(StringUtil.enquote("来場日"));
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (ScfUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			// リーダー番号
			cols.add(StringUtil.enquote(userdata.reader));
			// 来場日
			String time = getTime(userdata.timeByRfid);
			cols.add(StringUtil.enquote(time));

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
	 * 氏名および住所情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>ScfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			ScfUserDataDto userdata) {
		// 会社名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));
		// 部署
		String dept = StringUtil.concat(userdata.cardInfo.V_DEPT1,
				userdata.cardInfo.V_DEPT2, userdata.cardInfo.V_DEPT3,
				userdata.cardInfo.V_DEPT4);
		cols.add(StringUtil.enquote(dept));
		// 役職
		String biz = StringUtil.concat(userdata.cardInfo.V_BIZ1,
				userdata.cardInfo.V_BIZ2, userdata.cardInfo.V_BIZ3,
				userdata.cardInfo.V_BIZ4);
		cols.add(StringUtil.enquote(biz));
		// 氏名
		String name = StringUtil.concatWithDelimit("　",
				userdata.cardInfo.V_NAME1, userdata.cardInfo.V_NAME2);
		cols.add(StringUtil.enquote(name));
		return cols;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>ScfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			ScfUserDataDto userdata) {
		if (userdata.cardInfo == null) { // 住所情報が存在しない場合
			// 郵便番号
			cols.add(StringUtil.enquote(""));
			// 都道府県
			cols.add(StringUtil.enquote(""));
			// 住所(市区郡+町域+番号他)
			cols.add(StringUtil.enquote(""));
			// ビル名
			cols.add(StringUtil.enquote(""));
			// メールアドレス
			cols.add(StringUtil.enquote(""));
			// 電話番号
			cols.add(StringUtil.enquote(""));
			// FAX番号
			cols.add(StringUtil.enquote(""));
		} else {
			// 郵便番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			// 都道府県
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
			// 住所(市区郡+町域+番号他)
			String addr2 = StringUtil.concat(userdata.cardInfo.V_ADDR2,
					userdata.cardInfo.V_ADDR3, userdata.cardInfo.V_ADDR4);
			cols.add(StringUtil.enquote(addr2));
			// ビル名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR5));
			// メールアドレス (■が含まれている場合は空値に置換)
			cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.V_EMAIL) : StringUtil
					.enquote(""));
			// 電話番号 (■が含まれている場合は空値に置換)
			cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_TEL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_TEL) ? StringUtil
					.enquote(userdata.cardInfo.V_TEL) : StringUtil.enquote(""));
			// FAX番号 (■が含まれている場合は空値に置換)
			cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_FAX)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_FAX) ? StringUtil
					.enquote(userdata.cardInfo.V_FAX) : StringUtil.enquote(""));
		}
		return cols;
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>ScfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			ScfUserDataDto userdata) {
		// // Q1. 来場目的
		// List<String> purpose = new ArrayList<String>();
		// String[] q1Buff = null;
		// if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q1)) {
		// q1Buff = userdata.questionInfo.V_Q1.split(" ");
		// }
		// if (StringUtil.contains(q1Buff, "1")) { // SCF2013と計測展2013TOKYOの両方
		// purpose.add("1");
		// } else if (StringUtil.contains(q1Buff, "2")
		// && StringUtil.contains(q1Buff, "3")) { // SCF2013と計測展2013TOKYOの両方
		// purpose.add("1");
		// } else {
		// for (int nIndex = 1; nIndex <= 3; nIndex++) {
		// if (StringUtil.contains(q1Buff, String.valueOf(nIndex))) {
		// purpose.add(String.valueOf(nIndex));
		// }
		// }
		// }
		// String purposeValue = StringUtil.concat(purpose);
		// cols.add(StringUtil.enquote(purposeValue));

		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1));

		// Q2. 業種
		String[] q2Buff = null;
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q2)) {
			q2Buff = userdata.questionInfo.V_Q2.split(" ");
		}
		String businessCategoryName = null;
		if (q2Buff != null) {
			for (String id : q2Buff) {
				businessCategoryName = getBusinessCategoryName(userdata, id);
				break;
			}
		}
		cols.add(StringUtil.enquote(businessCategoryName));

		// Q3. 職種
		String[] q3Buff = null;
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q3)) {
			q3Buff = userdata.questionInfo.V_Q3.split(" ");
		}
		String occupationCategoryName = null;
		if (q3Buff != null) {
			for (String id : q3Buff) {
				occupationCategoryName = getOccupationCategoryName(userdata, id);
				break;
			}
		}
		cols.add(StringUtil.enquote(occupationCategoryName));

		return cols;
	}

	/**
	 * [Yokogawa]アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>ScfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputYokogawaEnqueteData(List<String> cols,
			ScfUserDataDto userdata) {
		ScfYokogawaQuestionDto questionInfo = (ScfYokogawaQuestionDto) userdata.questionInfo;

		outputCheck(questionInfo.Q1_1, cols);
		outputCheck(questionInfo.Q2_1, cols);
		outputCheck(questionInfo.Q3_1, cols);
		outputCheck(questionInfo.Q4_1, cols);
		outputCheck(questionInfo.Q5_1, cols);
		outputCheck(questionInfo.Q6_1, cols);
		outputCheck(questionInfo.Q7_1, cols);
		outputCheck(questionInfo.Q8_1, cols);
		outputCheck(questionInfo.Q9_1, cols);
		outputCheck(questionInfo.Q10_1, cols);
		outputCheck(questionInfo.Q11_1, cols);
		outputCheck(questionInfo.Q12_1, cols);
		outputCheck(questionInfo.Q13_1, cols);
		outputCheck(questionInfo.Q14_1, cols);
		outputCheck(questionInfo.Q15_1, cols);
		outputCheck(questionInfo.Q16_1, cols);
		outputCheck(questionInfo.Q17_1, cols);
		outputCheck(questionInfo.Q18_1, cols);
		outputCheck(questionInfo.Q19_1, cols);
		outputCheck(questionInfo.Q20_1, cols);
		outputCheck(questionInfo.Q21_1, cols);
		outputCheck(questionInfo.Q22_1, cols);

		if (StringUtil.isNotEmpty(questionInfo.Q1_2)) {
			String[] check1 = questionInfo.Q1_2.split(" ");
			cols.add(StringUtil.contains(check1, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check1, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q4_2)) {
			String[] check4 = questionInfo.Q4_2.split(" ");
			cols.add(StringUtil.contains(check4, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check4, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check4, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check4, "4") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check4, "5") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q6_2)) {
			String[] check6 = questionInfo.Q6_2.split(" ");
			cols.add(StringUtil.contains(check6, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check6, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q7_2)) {
			String[] check7 = questionInfo.Q7_2.split(" ");
			cols.add(StringUtil.contains(check7, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check7, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check7, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check7, "4") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q8_2)) {
			String[] check8 = questionInfo.Q8_2.split(" ");
			cols.add(StringUtil.contains(check8, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check8, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q9_2)) {
			String[] check9 = questionInfo.Q9_2.split(" ");
			cols.add(StringUtil.contains(check9, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check9, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check9, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q10_2)) {
			String[] check10 = questionInfo.Q10_2.split(" ");
			cols.add(StringUtil.contains(check10, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check10, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q11_2)) {
			String[] check11 = questionInfo.Q11_2.split(" ");
			cols.add(StringUtil.contains(check11, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check11, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check11, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q12_2)) {
			String[] check12 = questionInfo.Q12_2.split(" ");
			cols.add(StringUtil.contains(check12, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check12, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q13_2)) {
			String[] check13 = questionInfo.Q13_2.split(" ");
			cols.add(StringUtil.contains(check13, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check13, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check13, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check13, "4") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q17_2)) {
			String[] check17 = questionInfo.Q17_2.split(" ");
			cols.add(StringUtil.contains(check17, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check17, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check17, "3") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q19_2)) {
			String[] check19 = questionInfo.Q19_2.split(" ");
			cols.add(StringUtil.contains(check19, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check19, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q20_2)) {
			String[] check20 = questionInfo.Q20_2.split(" ");
			cols.add(StringUtil.contains(check20, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check20, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q21_2)) {
			String[] check21 = questionInfo.Q21_2.split(" ");
			cols.add(StringUtil.contains(check21, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check21, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		if (StringUtil.isNotEmpty(questionInfo.Q22_2)) {
			String[] check22 = questionInfo.Q22_2.split(" ");
			cols.add(StringUtil.contains(check22, "1") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
			cols.add(StringUtil.contains(check22, "2") ? StringUtil
					.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}

		// ご意見・ご要望
		cols.add(StringUtil.enquote(questionInfo.youbou));
		// 区分
		String kubun = null;
		if (StringUtil.isNotEmpty(questionInfo.kubun)) {
			String[] kubunList = questionInfo.kubun.split(" ");
			List<String> kubunValues = new ArrayList<String>();
			if (StringUtil.contains(kubunList, "1")) {
				kubunValues.add("YY");
			}
			if (StringUtil.contains(kubunList, "2")) {
				kubunValues.add("DD");
			}
			if (StringUtil.contains(kubunList, "3")) {
				kubunValues.add("YD");
			}
			if (StringUtil.contains(kubunList, "4")) {
				kubunValues.add("FR");
			}
			kubun = StringUtil.concatWithDelimit(",", kubunValues);
		}
		cols.add(StringUtil.enquote(kubun));
		// 営業
		String eigyou = null;
		int[] checkEigyouBuff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.eigyou, 9);
		if (StringUtil.isNotEmpty(questionInfo.eigyou_fa)) {
			checkEigyouBuff[9] = 1;
		}
		List<String> eigyouList = new ArrayList<String>();
		if (checkEigyouBuff[1] == 1) {
			eigyouList.add("環境");
		}
		if (checkEigyouBuff[2] == 1) {
			eigyouList.add("プラント");
		}
		if (checkEigyouBuff[3] == 1) {
			eigyouList.add("1営");
		}
		if (checkEigyouBuff[4] == 1) {
			eigyouList.add("2営");
		}
		if (checkEigyouBuff[5] == 1) {
			eigyouList.add("3営");
		}
		if (checkEigyouBuff[6] == 1) {
			eigyouList.add("4営");
		}
		if (checkEigyouBuff[7] == 1) {
			eigyouList.add("情SOL");
		}
		if (checkEigyouBuff[8] == 1) {
			eigyouList.add("サービス");
		}
		if (checkEigyouBuff[9] == 1) {
			eigyouList.add("その他");
		}
		eigyou = StringUtil.concatWithDelimit(",", eigyouList);
		if (StringUtil.isNotEmpty(questionInfo.eigyou_fa)) {
			eigyou = eigyou + "(" + questionInfo.eigyou_fa + ")";
		}
		cols.add(StringUtil.enquote(eigyou));
		// 代理店
		String dairiten = null;
		if (StringUtil.isNotEmpty(questionInfo.dairiten)) {
			List<String> dairitenList = new ArrayList<String>();
			String[] dairitenBuff = questionInfo.dairiten.split(" ");
			if (StringUtil.contains(dairitenBuff, "1")) {
				dairitenList.add("太陽");
			}
			if (StringUtil.contains(dairitenBuff, "2")) {
				dairitenList.add("西川");
			}
			if (StringUtil.contains(dairitenBuff, "3")) {
				dairitenList.add("横商");
			}
			if (StringUtil.contains(dairitenBuff, "4")) {
				dairitenList.add("電産");
			}
			if (StringUtil.contains(dairitenBuff, "5")) {
				dairitenList.add("美和");
			}
			if (StringUtil.contains(dairitenBuff, "6")) {
				dairitenList.add("シカデン");
			}
			if (StringUtil.contains(dairitenBuff, "7")) {
				dairitenList.add("協立");
			}
			if (StringUtil.contains(dairitenBuff, "8")) {
				dairitenList.add("八洲");
			}
			if (StringUtil.contains(dairitenBuff, "9")) {
				dairitenList.add("ヨネイ");
			}
			if (StringUtil.contains(dairitenBuff, "10")) {
				dairitenList.add("ニノテック");
			}
			if (StringUtil.contains(dairitenBuff, "11")) {
				dairitenList.add("菱電");
			}
			if (StringUtil.contains(dairitenBuff, "12")) {
				dairitenList.add("カナデン");
			}
			if (StringUtil.contains(dairitenBuff, "13")) {
				dairitenList.add("国華");
			}
			if (StringUtil.contains(dairitenBuff, "14")) {
				dairitenList.add("向洋");
			}
			if (StringUtil.contains(dairitenBuff, "15")) {
				dairitenList.add("大豊");
			}
			if (StringUtil.contains(dairitenBuff, "16")) {
				dairitenList.add("金陵");
			}
			if (StringUtil.contains(dairitenBuff, "17")) {
				dairitenList.add("港産業");
			}
			if (StringUtil.contains(dairitenBuff, "18")) {
				dairitenList.add("ワイディ");
			}
			if (StringUtil.contains(dairitenBuff, "19")) {
				dairitenList.add("牛島");
			}
			if (StringUtil.contains(dairitenBuff, "20")) {
				dairitenList.add("明治");
			}
			if (StringUtil.contains(dairitenBuff, "21")) {
				dairitenList.add("名三");
			}
			if (StringUtil.contains(dairitenBuff, "22")) {
				dairitenList.add("新川");
			}
			if (StringUtil.contains(dairitenBuff, "23")) {
				dairitenList.add("井上");
			}
			if (StringUtil.contains(dairitenBuff, "24")) {
				dairitenList.add("南向洋");
			}
			if (StringUtil.contains(dairitenBuff, "25")) {
				dairitenList.add("吉澤");
			}
			dairiten = StringUtil.concatWithDelimit(",", dairitenList);
		}
		cols.add(StringUtil.enquote(dairiten));
		// 先端ディスクリートゾーン専用
		String sentan = null;
		int[] checkSentanBuff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.sentan, 12);
		if (StringUtil.isNotEmpty(questionInfo.sentan_fa)) {
			checkSentanBuff[11] = 1;
		}
		List<String> sentanList = new ArrayList<String>();
		if (checkSentanBuff[1] == 1) {
			sentanList.add("太陽");
		}
		if (checkSentanBuff[2] == 1) {
			sentanList.add("西川");
		}
		if (checkSentanBuff[3] == 1) {
			sentanList.add("横商");
		}
		if (checkSentanBuff[4] == 1) {
			sentanList.add("電産");
		}
		if (checkSentanBuff[5] == 1) {
			sentanList.add("美和");
		}
		if (checkSentanBuff[6] == 1) {
			sentanList.add("向洋");
		}
		if (checkSentanBuff[7] == 1) {
			sentanList.add("金陵");
		}
		if (checkSentanBuff[8] == 1) {
			sentanList.add("ワイディ");
		}
		if (checkSentanBuff[9] == 1) {
			sentanList.add("新川");
		}
		if (checkSentanBuff[10] == 1) {
			sentanList.add("吉澤");
		}
		if (checkSentanBuff[11] == 1) {
			sentanList.add("その他");
		}
		sentan = StringUtil.concatWithDelimit(",", sentanList);
		if (StringUtil.isNotEmpty(questionInfo.sentan_fa)) {
			sentan = sentan + "(" + questionInfo.sentan_fa + ")";
		}
		cols.add(StringUtil.enquote(sentan));
		// e
		cols.add(checkSentanBuff[12] == 1 ? StringUtil.enquote("e")
				: StringUtil.enquote(""));
		// Counter
		cols.add(StringUtil.enquote(questionInfo.counter));
		// コメント
		String comment = StringUtil.concatWithDelimit("、",
				questionInfo.comment, questionInfo.Q1_FA, questionInfo.Q2_FA,
				questionInfo.Q3_FA, questionInfo.Q4_FA, questionInfo.Q5_FA,
				questionInfo.Q6_FA, questionInfo.Q7_FA, questionInfo.Q8_FA,
				questionInfo.Q9_FA, questionInfo.Q10_FA, questionInfo.Q11_FA,
				questionInfo.Q12_FA, questionInfo.Q13_FA, questionInfo.Q14_FA,
				questionInfo.Q15_FA, questionInfo.Q16_FA, questionInfo.Q17_FA,
				questionInfo.Q18_FA, questionInfo.Q19_FA, questionInfo.Q20_FA,
				questionInfo.Q21_FA, questionInfo.Q22_FA);
		cols.add(StringUtil.enquote(comment));
		return cols;
	}

	private static List<String> outputCheck(String enquete, List<String> cols) {
		String[] buff = null;
		if (StringUtil.isNotEmpty(enquete)) {
			buff = enquete.split(" ");
		}
		cols.add(StringUtil.contains(buff, "1") ? StringUtil
				.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		cols.add(StringUtil.contains(buff, "2") ? StringUtil
				.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		cols.add(StringUtil.contains(buff, "3") ? StringUtil
				.enquote(ScfConfig.MARK) : StringUtil.enquote(""));
		return cols;
	}

	/**
	 * 業種名（アンケート）の取得
	 * 
	 * @param userdata
	 *            　<b>ScfUserDataDto</b>
	 * @param categoryId
	 *            業種ID
	 * @return 業種名
	 */
	private static String getBusinessCategoryName(ScfUserDataDto userdata,
			String categoryId) {
		assert userdata != null && StringUtil.isNotEmpty(userdata.id);
		if (StringUtil.isNotEmpty(categoryId)) {
			if (ScfConfig.ROBOTENTRY_BARCODE_START_BIT.equals(userdata.id)) { // ロボット展登録データである場合
				// return ScfConstants.robotBusinessCategoryMap.get(categoryId);
				return userdata.questionInfo.V_Q1;
			} else { // SCF登録データである場合
				return ScfConstants.scfBusinessCategoryMap.get(categoryId);
			}
		}
		return "";
	}

	/**
	 * 職種名（アンケート）の取得
	 * 
	 * @param userdata
	 *            　<b>ScfUserDataDto</b>
	 * @param categoryId
	 *            業種ID
	 * @return 職種名
	 */
	private static String getOccupationCategoryName(ScfUserDataDto userdata,
			String categoryId) {
		assert userdata != null && StringUtil.isNotEmpty(userdata.id);
		if (StringUtil.isNotEmpty(categoryId)) {
			if (ScfConfig.ROBOTENTRY_BARCODE_START_BIT.equals(userdata.id)) { // ロボット展登録データである場合
				// return
				// ScfConstants.robotOccupationCategoryMap.get(categoryId);
				return userdata.questionInfo.V_Q2;
			} else { // SCF登録データである場合
				return ScfConstants.scfOccupationCategoryMap.get(categoryId);
			}
		}
		return "";
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            <b>ScfCardDto</b>
	 * @return 原票種別
	 */
	private static String getTicketType(UserDataDto userdata) {
		ScfUtil util = new ScfUtil();
		if (isRobotEntry(userdata)) { // ロボット展からの来場者データである場合
			return ScfConfig.TICKET_TYPE_ROBOT;
		} else if (util.isAppEntry(userdata)) { // 当日登録データである場合
			return ScfConfig.TICKET_TYPE_APPOINTEDDAY;
		} else if (util.isPreEntry(userdata)) { // 事前登録データである場合
			return ScfConfig.TICKET_TYPE_PREENTRY;
		}
		return "";
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>ScfUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(ScfUserDataDto userdata) {
		ScfValidator validator = new ScfValidator();
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
	 *            <b>ScfCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(ScfUserDataDto userdata) {
		ScfUtil util = new ScfUtil();
		boolean result;
		if (isRobotEntry(userdata)) { // ロボット展登録データである場合
			result = "1"
					.equals(((ScfCardDto) userdata.cardInfo).robotOverseaFlg);
		} else if (util.isPreEntry(userdata)) { // 事前登録データである場合
			result = "海外".equals(userdata.cardInfo.V_ADDR1);
		} else { // 当日登録データである場合
			boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
			boolean check2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
			boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
			boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
			result = check1 && check2 && check3 && check4 && check5;
		}
		return result;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>ScfUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, ScfUserDataDto> getMap(
			List<ScfUserDataDto> userDataList) {
		Map<String, ScfUserDataDto> map = new HashMap<String, ScfUserDataDto>();
		for (ScfUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
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
			if (ScfConstants.READER_ENDO_LIST.contains(reader)) {
				value = ScfConstants.REQUESTCODES_ENDO.get(code);
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

	/*
	 * 出展者情報の重複検証用
	 */
	/**
	 * 全ての出展者情報を取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての出展者情報
	 * @throws SQLException
	 */
	public static List<ScfExhibitorDataDto> getAllExhibitorData(Connection conn)
			throws SQLException {
		List<ScfExhibitorDataDto> userDataList = new ArrayList<ScfExhibitorDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM masterscf ORDER BY V_year desc;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ScfExhibitorDataDto userdata = new ScfExhibitorDataDto();
			userdata.V_no = rs.getString("V_no");
			userdata.V_year = rs.getString("V_year");
			userdata.V_com = rs.getString("V_com");
			userdata.charge_com = rs.getString("charge_com");
			userdata.charge_name = rs.getString("charge_name_memo");
			userdata.Resp_com = rs.getString("Resp_com");
			userdata.Resp_name = rs.getString("Resp_name_memo");
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * 出展者マスタと照合して重複検証
	 * 
	 * @param masterDataList
	 *            　出展者マスタ
	 * @param data
	 *            検証データ
	 * @return 検証結果のブール値
	 */
	public static boolean dupulicateCheck(
			List<ScfExhibitorMasterDto> masterDataList, ScfExhibitorDataDto data) {
		assert masterDataList != null && data != null;
		boolean dupulicate = false;
		for (ScfExhibitorMasterDto master : masterDataList) {
			boolean company = master.company.equals(data.V_com);
			boolean name1 = master.name.equals(data.charge_name);
			boolean name2 = master.name.equals(data.Resp_name);
			if (company && (name1 || name2)) {
				dupulicate = true;
				master.dupulicate.add(data.V_no);
				break;
			}
		}
		return dupulicate;
	}

	/**
	 * 重複FLGの登録
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param id
	 *            　重複ID
	 * @return 登録処理の成否のブール値
	 * @throws SQLException
	 */
	public static boolean setDupulicateFlg(Connection conn, String id)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "update masterscf set dupulicate = \"1\" where V_no = \""
				+ id + "\";";
		stmt.executeUpdate(sql);
		stmt.close();
		return true;
	}

	/**
	 * 出展者IDから出展年度を特定する
	 * 
	 * @param allData
	 *            全ての出展者データ
	 * @param id
	 *            出展者ID
	 * @return 出展年度
	 */
	public static String getYear(List<ScfExhibitorDataDto> allData, String id) {
		for (ScfExhibitorDataDto data : allData) {
			if (data.V_no.equals(id)) {
				return data.V_year;
			}
		}
		return null;
	}

	/**
	 * 出展フラグを設定する
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param id
	 *            出展者ID
	 * @param year
	 *            出展年度
	 * @return DB操作の成否のブール値
	 * @throws SQLException
	 */
	public static boolean setYearFlg(Connection conn, String id, String year)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = null;
		if ("2007".equals(year)) {
			sql = "update masterscf set V_2007 = '1' where V_no = '" + id
					+ "';";
		} else if ("2008".equals(year)) {
			sql = "update masterscf set V_2008 = '1' where V_no = '" + id
					+ "';";
		} else if ("2009".equals(year)) {
			sql = "update masterscf set V_2009 = '1' where V_no = '" + id
					+ "';";
		} else if ("2010".equals(year)) {
			sql = "update masterscf set V_2010 = '1' where V_no = '" + id
					+ "';";
		} else if ("2011".equals(year)) {
			sql = "update masterscf set V_2011 = '1' where V_no = '" + id
					+ "';";
		} else if ("2012".equals(year)) {
			sql = "update masterscf set V_2012 = '1' where V_no = '" + id
					+ "';";
		} else if ("2013".equals(year)) {
			sql = "update masterscf set V_2013 = '1' where V_no = '" + id
					+ "';";
		}
		stmt.executeUpdate(sql);
		stmt.close();
		return true;
	}
}