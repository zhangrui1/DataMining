package jp.co.freedom.master.utilities.mesago;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.AnalysisTelFax;
import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoRfidDto;
import jp.co.freedom.master.dto.mesago.MesagoSLXDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.Util;
import jp.co.freedom.master.utilities.mesago.MesagoConfig.TICKET_TYPE;

/**
 * MESAGO向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class MesagoUtil extends Util {

	/** NG Unicodeリスト */
	private static final List<Integer> NG_UNICODE_CHARACTER = createNgUnicodeCharacterList();

	/** NGマークリスト */
	private static final List<String> NG_MARK = createNgMarkList();

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createInstance(List<String[]> csvData) {
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		MesagoUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new MesagoUserDataDto();
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
				dataDto = new MesagoUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = row[1];
				// dataDto.id = rfid.substring(1, rfid.length());
				dataDto.id = rfid;
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
	 * 【BWJTokyo】ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createBwjInstance(
			List<String[]> csvData) {
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		MesagoUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			// String[] row = removeLastSpaceElement(csvData.get(nIndex));
			String[] row = csvData.get(nIndex);
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new MesagoUserDataDto();
				}
				// リクエストコードの格納
				String request = row[1].substring(1);
				dataDto.requestCode.add(request);
			} else if (row.length == 4) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new MesagoUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = row[2];
				// dataDto.id = rfid.substring(1, rfid.length());
				dataDto.id = rfid;
				dataDto.timeByRfid = row[1];
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
	 * 【BWJTokyo】（未来場者向け）ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createBwjNonVisitorsInstance(
			List<String[]> csvData) {
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			// String[] row = removeLastSpaceElement(csvData.get(nIndex));
			String[] row = csvData.get(nIndex);
			if (row.length == 1) {// RFID番号の行
				MesagoUserDataDto dataDto = new MesagoUserDataDto();
				dataDto.id = row[0];
				userData.add(dataDto);
			} else {
				System.out.println("不正なデータがCSVに含まれています");
			}
		}
		return userData;
	}

	/**
	 * 【事前登録 登録券番号マッチング用】ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createInstancePreentryNumberMatching(
			List<String[]> csvData) {
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		MesagoUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new MesagoUserDataDto();
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
				dataDto = new MesagoUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = "bwj2014f-" + row[1];
				// dataDto.id = rfid.substring(1, rfid.length());
				((MesagoCardDto) dataDto.cardInfo).PREENTRY_ID = rfid;
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
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		return ((MesagoUserDataDto) userdata).preentry;
	}

	/**
	 * 指定IDが事前登録VIPユーザーであるか否かの検証
	 * 
	 * @param userdata
	 * @return
	 */
	public boolean isPreVIP(UserDataDto userdata) {
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		if (((MesagoUserDataDto) userdata).preVip) {
			return true;
		}
		if (StringUtil.isNotEmpty(userdata.id)
				&& StringUtil.integerStringCheck(userdata.id)) {
			int barcode = Integer.parseInt(userdata.id);
			boolean check1 = 7010951 <= barcode && barcode <= 7011318;
			boolean check2 = 1050001 <= barcode && barcode <= 1050640;
			return check1 || check2;
		}
		return false;
	}

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isAppEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		return ((MesagoUserDataDto) userdata).appointedday;
	}

	/**
	 * 指定IDが招待状ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isInvitation(UserDataDto userdata) {
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		return ((MesagoUserDataDto) userdata).invitation;
	}

	/**
	 * 指定IDがVIP招待券ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isVIPInvitation(UserDataDto userdata) {
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		return ((MesagoUserDataDto) userdata).vipInvitation;
	}

	/**
	 * 指定IDがプレスユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isPress(UserDataDto userdata) {
		assert userdata != null && userdata instanceof MesagoUserDataDto;
		return ((MesagoUserDataDto) userdata).press;
	}

	// /**
	// * 指定IDが事前登録ユーザーであるか否かの検証
	// *
	// * @param id
	// * RFID番号
	// * @return 検証結果のブール値
	// */
	// public static boolean isPreEntry(String id) {
	// assert StringUtil.isNotEmpty(id);
	// // TODO: 事前登録ユーザーの検証方法
	// return id.startsWith(MesagoConfig.PREENTRY_BARCODE_START_BIT);
	// }

	// /**
	// * 指定IDが当日登録ユーザーであるか否かの検証
	// *
	// * @param id
	// * RFID番号
	// * @return 検証結果のブール値
	// */
	// public static boolean isAppEntry(String id) {
	// assert StringUtil.isNotEmpty(id);
	// // TODO: 当日登録ユーザーの検証方法
	// return id.startsWith(MesagoConfig.APPOINTEDDAY_BARCODE_START_BIT);
	// }

	/** 学生向けバーコード番号カウンタ */
	public static int masterStudentCount = 6011500;

	/** その他向けバーコード番号カウンタ */
	public static int masterOtherCount = 6112900;

	/**
	 * 【バッチ作成用】全ての当日登録(学生／その他)データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param fileName
	 *            ファイル名
	 * @return 全ての当日登録(学生／その他)データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllBadgeStudentAppointedDayData(
			Connection conn, String fileName) throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		String sql = "SELECT * FROM bwj_appointedday WHERE V_ND = ? ORDER BY CAST(V_ID AS SIGNED)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, fileName);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO

			/* 名刺情報 */
			cardInfo.V_CORP = rs.getString("V_corp"); // 会社名
			cardInfo.V_NAME1 = rs.getString("V_name"); // 氏名姓漢字
			cardInfo.V_TICKET_TYPE = rs.getString("V_Q1_CODE"); // 原票種別区分

			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 【バッチ作成用】全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllBadgeAppointedDayData(
			Connection conn) throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		String sql = "SELECT * FROM bwj_appointedday ORDER BY CAST(V_ID AS SIGNED)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO

			/* 名刺情報 */
			cardInfo.V_CORP = rs.getString("V_corp"); // 会社名
			cardInfo.V_NAME1 = rs.getString("V_name"); // 氏名姓漢字

			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 【バッチ作成用】全ての事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param type
	 *            原票種別
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllBadgePreentryData(
			Connection conn, String type) throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		String sql = "SELECT * FROM bwj_preentry ORDER BY CAST(V_ID AS SIGNED);";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO

			/* 名刺情報 */
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_zip")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_addr1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_addr2"); // 市区町村
			cardInfo.V_ADDR3 = rs.getString("V_addr3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_addr4"); // ビル名
			cardInfo.V_CORP = rs.getString("V_corp"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_dept"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_biz"); // 役職
			cardInfo.V_NAME1 = rs.getString("V_name"); // 氏名姓漢字
			cardInfo.SEND_FLG = rs.getString("V_send"); // 送付先
			// 原票種別
			if ("99".equals(type)) {
				String slxCode = rs.getString("V_Q1_CODE"); // SLXコード
				cardInfo.V_TICKET_TYPE = getType(slxCode);
			} else {
				cardInfo.V_TICKET_TYPE = type;
			}

			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 原票種別の取得
	 * 
	 * @param slxCode
	 * @return
	 */
	private static String getType(String slxCode) {
		assert StringUtil.isNotEmpty(slxCode);
		final Map<String, String> map = new HashMap<String, String>();
		map.put("BN0", "01");
		map.put("BN1", "02");
		map.put("BN2", "03");
		map.put("BN3", "04");
		map.put("BN4", "05");
		map.put("BN5", "05");
		map.put("BN13", "08");
		map.put("BN25", "07");
		map.put("BN26", "07");
		map.put("BN6", "09");
		map.put("BN27", "06");
		map.put("BN17", "10");
		return map.get(slxCode);
	}

	/**
	 * ホール入り口のバーコードデータ
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, MesagoRfidDto> getAllRfidMap(Connection conn)
			throws SQLException {

		Map<String, MesagoRfidDto> allRfidData = new HashMap<String, MesagoRfidDto>();

		String sql = "SELECT * FROM rfid;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String rfid = rs.getString("rfid_no"); // RFID番号
			MesagoRfidDto rfidDto = new MesagoRfidDto();
			int nIndex = -1;
			for (String day : MesagoConfig.DAYS) {
				rfidDto.visitFlgs[++nIndex] = 0 != rs.getInt(day + "_cnt");
			}
			allRfidData.put(rfid, rfidDto);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return allRfidData;
	}

	/**
	 * 全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		String sql = "SELECT * FROM appointedday;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO
			MesagoQuestionDto questionInfo = new MesagoQuestionDto(); // アンケート情報DTO

			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;

			if (StringUtil.isNotEmpty(rs.getString("V_VIP"))) {
				userdata.vipInvitation = true; // VIP
			} else if (StringUtil.isNotEmpty(rs.getString("V_SH"))) {
				userdata.invitation = true; // 招待状
			} else {
				userdata.appointedday = true; // 当日
			}

			/* 名刺情報 */
			cardInfo.V_CORP = normalize(rs.getString("V_CORP")); // 会社名
			String dept1 = normalize(rs.getString("V_DEPT1")); // 部署1
			String dept2 = normalize(rs.getString("V_DEPT2")); // 部署2
			String dept3 = normalize(rs.getString("V_DEPT3")); // 部署3
			String dept4 = normalize(rs.getString("V_DEPT4")); // 部署4
			cardInfo.V_DEPT1 = StringUtil.concat(dept1, dept2, dept3, dept4);
			String biz1 = normalize(rs.getString("V_BIZ1")); // 役職1
			String biz2 = normalize(rs.getString("V_BIZ2")); // 役職2
			String biz3 = normalize(rs.getString("V_BIZ3")); // 役職3
			String biz4 = normalize(rs.getString("V_BIZ4")); // 役職4
			cardInfo.V_BIZ1 = StringUtil.concat(biz1, biz2, biz3, biz4);
			cardInfo.V_NAME1 = normalize(rs.getString("V_NAME1")); // 氏名姓漢字
			cardInfo.V_NAME2 = normalize(rs.getString("V_NAME2")); // 氏名名漢字
			cardInfo.V_TEL = normalize(rs.getString("V_TEL")); // 電話番号
			cardInfo.V_FAX = normalize(rs.getString("V_FAX")); // FAX番号
			cardInfo.V_EMAIL = normalize(rs.getString("V_EMAIL")); // Email
			cardInfo.V_URL = normalizedUrl(normalize(rs.getString("V_WEB"))); // Web
			cardInfo.V_COUNTRY = normalize(rs.getString("V_Country")); // 国名
			cardInfo.V_ZIP = zipNormalize(normalize(rs.getString("V_ZIP"))); // 郵便番号
			cardInfo.V_ADDR1 = normalize(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = normalize(rs.getString("V_ADDR2")); // 市区部
			cardInfo.V_ADDR3 = StringUtil.concat(
					normalize(rs.getString("V_ADDR3")),
					normalize(rs.getString("V_ADDR4"))); // 住所
			cardInfo.V_ADDR4 = normalize(rs.getString("V_ADDR5")); // ビル名

			/* アンケート情報 */
			String q1Kubun = normalize(rs.getString("V_Q1_KUBUN"));
			int q1Buff[] = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q1Kubun,
					34);
			int faIndex[] = { 13, 19, 25, 33, 34 };
			// 業種区分(その他FA)
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				String q1Fa = normalize(rs.getString("V_Q1_FA"
						+ String.valueOf(nIndex)));
				if (StringUtil.isNotEmpty(q1Fa)) {
					q1Buff[faIndex[nIndex - 1]] = 1;
					questionInfo.V_Q1_other = q1Fa;
					break;
				}
			}
			// 業種区分
			for (int nIndex = 1; nIndex <= 34; nIndex++) {
				if (1 == q1Buff[nIndex]) {
					questionInfo.V_Q1_kubun = normalize(String.valueOf(nIndex));
					break; // 前方一致
				}
			}
			questionInfo.V_Q3_kubun = EnqueteUtil
					.convertSingleAnswer(normalize(rs.getString("V_Q3_KUBUN"))); // 職種区分
			questionInfo.V_Q3_other = normalize(rs.getString("V_Q3_FA")); // 職種(その他FA)
			questionInfo.V_Q4_kubun = EnqueteUtil
					.convertSingleAnswer(normalize(rs.getString("V_Q4_KUBUN"))); // 役職区分
			questionInfo.V_Q4_other = normalize(rs.getString("V_Q4_FA")); // 役職(その他FA)
			questionInfo.V_Q5 = EnqueteUtil.convertSingleAnswer(normalize(rs
					.getString("V_Q5"))); // 従業員数
			questionInfo.V_Q6 = EnqueteUtil.convertSingleAnswer(normalize(rs
					.getString("V_Q6"))); // 買付け決定権
			questionInfo.V_Q7 = EnqueteUtil.convertSingleAnswer(normalize(rs
					.getString("V_Q7"))); // 来場動機
			questionInfo.V_Q7_other = normalize(rs.getString("V_Q7_FA")); // 来場動機(その他FA)
			questionInfo.V_Q8 = normalize(rs.getString("V_Q8")); // 出展検討

			/* プレス情報 */
			cardInfo.V_P_NAME = normalize(rs.getString("V_P_NAME"));
			cardInfo.V_P_NAME_KUBUN = normalize(rs.getString("V_P_NAMEKUBUN"));
			cardInfo.V_P_BIZ = normalize(rs.getString("V_P_BIZ"));
			cardInfo.V_P_OCC = normalize(rs.getString("V_P_OCC"));

			cardInfo.V_IMAGE_PATH = normalize(rs.getString("IMAGE_PATH")); // イメージパス

			// // 来場フラグ
			// int dayCount = MesagoConfig.DAYS.length; // 開催日数
			// boolean visitFlgs[] = new boolean[dayCount];
			// if (StringUtil.isNotEmpty(userdata.id)) { // バーコード貸出を行う場合
			// int nIndex = -1;
			// for (String day : MesagoConfig.DAYS) {
			// visitFlgs[++nIndex] = 0 != rs.getInt(day + "_cnt");
			// }
			// } else { // バーコード貸出を行わない場合
			// String day = getDayFromImagePath(cardInfo.V_IMAGE_PATH); //
			// 画像ファイル名より来場日を特定
			// cardInfo.V_DAY = day;
			// assert StringUtil.isNotEmpty(day);
			// for (int nIndex = 0; nIndex < MesagoConfig.DAYS.length; nIndex++)
			// {
			// if (MesagoConfig.DAYS[nIndex].equals(day)) {
			// visitFlgs[nIndex] = true;
			// break;
			// }
			// }
			// }
			// userdata.visitFlgs = visitFlgs;

			// 環境依存文字の存在性を確認
			// if (cardInfo.contailsModelDependence()) {
			// userdata.result.containsModelDependenceCharacter = true;
			// }

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 全ての事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param visitFlg
	 *            true=来場者データ,false=未来場者データ
	 * @return 全ての事前登録データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllPreRegistData(Connection conn,
			boolean visitFlg) throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		String sql;
		if (visitFlg) {
			sql = "SELECT * FROM preentry;";
		} else {
			sql = "SELECT * FROM preentry WHERE ";
			List<String> conditions = new ArrayList<String>();
			for (String day : MesagoConfig.DAYS) {
				if (StringUtil.isNotEmpty(day)) {
					conditions.add("DAY" + day + " IS NULL");
				}
			}
			sql = sql + StringUtil.concatWithDelimit(" and ", conditions);
		}
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO
			MesagoQuestionDto questionInfo = new MesagoQuestionDto(); // アンケート情報DTO

			if (StringUtil.isNotEmpty(rs.getString("V_VIP"))) {
				userdata.vipInvitation = true; // VIP
				userdata.preVip = true; // 事前VIP
			} else {
				userdata.preentry = true; // 事前登録フラグ
			}

			// バーコード番号
			userdata.id = rs.getString("V_NO"); // バーコード番号
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.PREENTRY_ID = normalize(rs.getString("V_NO")); // 登録番号
			cardInfo.V_CORP = normalize(rs.getString("V_CORP")); // 会社名
			cardInfo.V_DEPT1 = normalize(rs.getString("V_DEPT1")); // 部署
			cardInfo.V_BIZ1 = normalize(rs.getString("V_BIZ1")); // 役職
			cardInfo.V_TITLE = normalize(rs.getString("V_PREFIX")); // 敬称
			cardInfo.V_NAME1 = normalize(rs.getString("V_NAME1")); // 氏名姓漢字
			cardInfo.V_TEL = normalize(rs.getString("V_TEL")); // 電話番号
			cardInfo.V_FAX = normalize(rs.getString("V_FAX")); // FAX番号
			cardInfo.V_EMAIL = normalize(rs.getString("V_EMAIL")); // Email
			cardInfo.V_URL = normalize(rs.getString("V_WEB")); // Web
			cardInfo.SEND_FLG = normalize(rs.getString("V_SEND")); // 送付先
			cardInfo.V_COUNTRY = normalize(rs.getString("V_COUNTRY")); // 国名
			cardInfo.V_ZIP = zipNormalize(normalize(rs.getString("V_ZIP"))); // 郵便番号
			cardInfo.V_ADDR1 = normalize(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = normalize(rs.getString("V_ADDR2")); // 市区部
			cardInfo.V_ADDR3 = normalize(rs.getString("V_ADDR3")); // 以下住所
			cardInfo.V_ADDR4 = normalize(rs.getString("V_ADDR4")); // ビル名

			/* アンケート情報 */
			questionInfo.V_Q1_kubun = normalize(rs.getString("V_Q1_KUBUN")); // 業種区分
			questionInfo.V_Q1_other = normalize(rs.getString("V_Q1_FA")); // 業種区分(その他FA)
			questionInfo.V_Q1_code = normalize(rs.getString("V_Q1_CODE")); // 業種区分(コード)
			questionInfo.V_Q2 = normalize(rs.getString("V_Q2")); // 専門分野
			questionInfo.V_Q2_code = normalize(rs.getString("V_Q2_CODE")); // 専門分野(コード)
			questionInfo.V_Q3_kubun = normalize(rs.getString("V_Q3_KUBUN")); // 職種区分
			questionInfo.V_Q3_other = normalize(rs.getString("V_Q3_FA")); // 職種(その他FA)
			questionInfo.V_Q3_code = normalize(rs.getString("V_Q3_CODE")); // 職種(コード)
			questionInfo.V_Q4_kubun = normalize(rs.getString("V_Q4_KUBUN")); // 役職区分
			questionInfo.V_Q4_other = normalize(rs.getString("V_Q4_FA")); // 役職(その他FA)
			questionInfo.V_Q4_code = normalize(rs.getString("V_Q4_CODE")); // 役職(コード)
			questionInfo.V_Q5 = normalize(rs.getString("V_Q5")); // 従業員数
			questionInfo.V_Q6 = normalize(rs.getString("V_Q6")); // 買付け決定権
			questionInfo.V_Q7 = normalize(rs.getString("V_Q7")); // 来場動機
			questionInfo.V_Q7_other = normalize(rs.getString("V_Q7_FA")); // 来場動機(その他FA)
			questionInfo.V_Q8 = normalize(rs.getString("V_Q8")); // 出展検討
			cardInfo.REGIST_DATE = rs.getString("V_DATE"); // 登録日時

			// // 来場フラグ
			// if (visitFlg) {
			// int dayCount = MesagoConfig.DAYS.length; // 開催日数
			// boolean visitFlgs[] = new boolean[dayCount];
			// if (StringUtil.isNotEmpty(userdata.id)) { // バーコード貸出を行う場合
			// int nIndex = -1;
			// for (String day : MesagoConfig.DAYS) {
			// visitFlgs[++nIndex] = 0 != rs.getInt(day + "_cnt");
			// }
			// } else if (StringUtil.isNotEmpty(cardInfo.V_IMAGE_PATH)) { //
			// バーコード貸出を行わない場合(画像ファイルパスが存在)
			// String day = getDayFromImagePath(cardInfo.V_IMAGE_PATH); //
			// 画像ファイル名より来場日を特定
			// cardInfo.V_DAY = day;
			// assert StringUtil.isNotEmpty(day);
			// for (int nIndex = 0; nIndex < MesagoConfig.DAYS.length; nIndex++)
			// {
			// if (MesagoConfig.DAYS[nIndex].equals(day)) {
			// visitFlgs[nIndex] = true;
			// break;
			// }
			// }
			// } else { // DB上のフラグにより判定
			// for (String day : MesagoConfig.DAYS) {
			// if (StringUtil.isNotEmpty(rs.getString("DAY" + day))) {
			// cardInfo.V_DAY = day;
			// assert StringUtil.isNotEmpty(day);
			// for (int nIndex = 0; nIndex < MesagoConfig.DAYS.length; nIndex++)
			// {
			// if (MesagoConfig.DAYS[nIndex].equals(day)) {
			// visitFlgs[nIndex] = true;
			// break;
			// }
			// }
			// }
			// }
			// }
			// userdata.visitFlgs = visitFlgs;
			// }

			// 環境依存文字の存在性を確認
			// if (cardInfo.contailsModelDependence()) {
			// userdata.result.containsModelDependenceCharacter = true;
			// }

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 入力規則によるデータ補正
	 * 
	 * @param value
	 *            対象文字列
	 * @return 正規化後の文字列
	 */
	public static String normalize(String value) {
		if (StringUtil.isNotEmpty(value)) {
			// 1.半角カタカナ→全角カタカナ変換
			value = StringUtil.convertFullWidthKatakana(value);
			// 2.英語、数字、記号を半角変換
			value = StringUtil.convertHalfWidthString(value, false);
			// 4.「✩」などの特定Unicode文字を半角スペースに置換する
			value = replaceUnicodeMarkByHalfSpace(value);
			// value = value.replaceAll("■", "●");
		}
		return value;
	}

	/**
	 * URLの正規化
	 * 
	 * @param url
	 *            URL文字列
	 * @return 正規化後のURL文字列
	 */
	public static String normalizedUrl(String url) {
		if (StringUtil.isNotEmpty(url)) {
			if (StringUtil.find(url, "@")) {
				return "";
			}
			if (url.startsWith("http")) {
				return url;
			}
			url = "http://" + url;
			if (!StringUtil.isUrl(url)) {
				return "";
			}
		}
		return url;
	}

	/**
	 * 「✩」等の特定Unicode文字を半角スペースに置換する
	 * 
	 * @param value
	 *            対象文字列
	 * @return 置換処理後の文字列
	 */
	public static String replaceUnicodeMarkByHalfSpace(String value) {
		if (StringUtil.isNotEmpty(value)) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				int unicode = (int) c;
				if (!NG_UNICODE_CHARACTER.contains(unicode)
						&& !NG_MARK.contains(String.valueOf(c))) {
					sb.append(c);
				} else {
					sb.append(' ');
				}
			}
			return sb.toString();
		}
		return value;
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
	 * 画像ファイル名より来場日を取得
	 * 
	 * @param userdata
	 *            ユーザーデータ
	 * @return 来場日
	 */
	public static String getDayFromImagePath(String imagePath) {
		if (StringUtil.isNotEmpty(imagePath)) {
			return imagePath.substring(
					MesagoConfig.DATE_START_POSITION_FOR_IMAGE_FILENAME,
					MesagoConfig.DATE_END_POSITION_FOR_IMAGE_FILENAME);
		}
		return null;
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
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param isPreMaster
	 *            事前登録マスターデータ作成フラグ
	 * @param isAppMaster
	 *            当日登録マスターデータ作成フラグ
	 * @param visitFlg
	 *            true=来場者データのDL／false=未来場者データのDL
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Connection conn, List<MesagoUserDataDto> userDataList, String dim,
			boolean isPreMaster, boolean isAppMaster, boolean visitFlg)
			throws ServletException, IOException, SQLException {

		return downLoad(request, response, outputFileName, conn, userDataList,
				dim, isPreMaster, isAppMaster, visitFlg, true);
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
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param isPreMaster
	 *            事前登録マスターデータ作成フラグ
	 * @param isAppMaster
	 *            当日登録マスターデータ作成フラグ
	 * @param visitFlg
	 *            true=来場者データのDL／false=未来場者データのDL
	 * @param outputStatisticalData
	 *            来場履歴情報の出力フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Connection conn, List<MesagoUserDataDto> userDataList, String dim,
			boolean isPreMaster, boolean isAppMaster, boolean visitFlg,
			boolean outputStatisticalData) throws ServletException,
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

		header.add(StringUtil.enquote("通し番号"));
		header.add(StringUtil.enquote("環境依存文字"));
		header.add(StringUtil.enquote("来場日時"));
		header.add(StringUtil.enquote("登録番号(来場事前登録)"));
		header.add(StringUtil.enquote("バーコード番号"));
		// 原票種別
		header.add(StringUtil.enquote("来場事前登録"));
		header.add(StringUtil.enquote("招待状"));
		header.add(StringUtil.enquote("VIP招待券"));
		header.add(StringUtil.enquote("当日売り券"));
		// 会社名、氏名
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("Title"));
		header.add(StringUtil.enquote("氏名"));
		// 連絡先
		header.add(StringUtil.enquote("TEL"));
		header.add(StringUtil.enquote("TEL（国番号）"));
		header.add(StringUtil.enquote("TEL（市外局番）"));
		header.add(StringUtil.enquote("TEL（市内局番-番号）"));
		header.add(StringUtil.enquote("内線番号"));
		header.add(StringUtil.enquote("FAX"));
		header.add(StringUtil.enquote("FAX（国番号）"));
		header.add(StringUtil.enquote("FAX（市外局番）"));
		header.add(StringUtil.enquote("FAX（市内局番-番号）"));
		header.add(StringUtil.enquote("Email"));
		header.add(StringUtil.enquote("Web"));
		header.add(StringUtil.enquote("送付先"));
		header.add(StringUtil.enquote("国"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("市区町村"));
		header.add(StringUtil.enquote("住所"));
		header.add(StringUtil.enquote("ビル名"));
		// アンケート項目
		if (MesagoConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("業種番号"));
			header.add(StringUtil.enquote("業種区分"));
			header.add(StringUtil.enquote("業種その他テキスト"));
			header.add(StringUtil.enquote("SLX業種区分コード"));
			header.add(StringUtil.enquote("専門分野"));
			header.add(StringUtil.enquote("SLX専門分野コード"));
			header.add(StringUtil.enquote("職種番号"));
			header.add(StringUtil.enquote("職種区分"));
			header.add(StringUtil.enquote("職種その他テキスト"));
			header.add(StringUtil.enquote("SLX職種区分コード"));
			header.add(StringUtil.enquote("役職番号"));
			header.add(StringUtil.enquote("役職区分"));
			header.add(StringUtil.enquote("役職その他テキスト"));
			header.add(StringUtil.enquote("SLX役職区分コード"));
			header.add(StringUtil.enquote("従業員数番号"));
			header.add(StringUtil.enquote("従業員数"));
			header.add(StringUtil.enquote("SLX従業員数コード"));
			header.add(StringUtil.enquote("商品買付け決定権"));
			header.add(StringUtil.enquote("来場動機"));
			header.add(StringUtil.enquote("来場動機その他テキスト"));
			header.add(StringUtil.enquote("出展検討"));
		}
		// 登録日時
		header.add(StringUtil.enquote("登録日時"));
		// プレス情報
		header.add(StringUtil.enquote("プレス媒体名"));
		header.add(StringUtil.enquote("プレス媒体区分"));
		header.add(StringUtil.enquote("プレス業種区分"));
		header.add(StringUtil.enquote("プレス業種区分コード"));
		header.add(StringUtil.enquote("プレス職種区分"));
		header.add(StringUtil.enquote("プレス職種区分コード"));
		// 統計情報
		for (int nDay = 1; nDay <= MesagoConfig.DAYS.length; nDay++) {
			header.add(StringUtil.enquote("来場登録日" + String.valueOf(nDay) + "日目"));
		}
		for (int nDay = 1; nDay <= MesagoConfig.DAYS.length; nDay++) {
			header.add(StringUtil.enquote("来場日" + String.valueOf(nDay) + "日目"));
		}

		header.add(StringUtil.enquote("画像パス"));

		FileUtil.writer(header, writer, dim);

		// SLXコード
		Map<String, MesagoSLXDto> slxMap = MesagoSLXCodeUtil.getSLXMap(conn,
				MesagoConfig.EXHIBITION_NAME);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		int counter = 0;
		int press = 0;
		int rfid2013 = 0;
		for (MesagoUserDataDto userdata : userDataList) {

			if (!isAppMaster && !isPreMaster) {
				// プレスを出力対象外とする
				if (MesagoConstants.PRESS_RFID_LIST.contains(userdata.id)
						|| userdata.id.startsWith("8")) {
					press++;
					continue;
				}
			}

			// else if (MesagoConstants.RFID_2013_LIST.contains(userdata.id)) {
			// // RFID2013を出力対象外とする
			// rfid2013++;
			// continue;
			// }

			List<String> cols = new ArrayList<String>();

			// 通し番号
			cols.add(StringUtil.enquote(String.valueOf(++counter)));
			// 環境依存文字の存在性
			cols.add(StringUtil
					.enquote(userdata.result.containsModelDependenceCharacter ? "T"
							: ""));
			// 来場日時
			if (visitFlg) {
				if (StringUtil.isNotEmpty(userdata.timeByRfid)) {
					cols.add(StringUtil.enquote(userdata.timeByRfid));
				} else if (StringUtil.isNotEmpty(userdata.cardInfo.V_DAY)) {
					cols.add(StringUtil.enquote(StringUtil.normalizedDateStr(
							MesagoConfig.YEAR, MesagoConfig.MONTH,
							userdata.cardInfo.V_DAY, MesagoConfig.HOUR)));
				} else {
					continue;
				}
			} else {
				cols.add(StringUtil.enquote(""));
			}

			// 登録番号(来場事前登録)
			String preentryId = ((MesagoCardDto) userdata.cardInfo).PREENTRY_ID;
			if (StringUtil.isNotEmpty(preentryId)
					&& !preentryId.startsWith("bwj2014")) {
				preentryId = null;
			}
			cols.add(StringUtil.enquote(preentryId));
			// バーコード番号
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.subBarcode) ? userdata.subBarcode
					: userdata.id));
			// 原票種別項目
			outputTicketType(cols, userdata);
			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			if (MesagoConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				MesagoUtil util = new MesagoUtil();
				if (util.isPreEntry(userdata) || util.isPreVIP(userdata)) {
					outputEnqueteDataForPreentry(cols, userdata);
				} else {
					outputEnqueteDataForAppointedday(cols, userdata, slxMap);
				}
			}
			// 登録日時
			cols.add(StringUtil
					.enquote(normalizedRegistDate(((MesagoCardDto) userdata.cardInfo).REGIST_DATE)));
			// プレス関連項目
			outputPressData(cols, userdata);

			// 統計情報
			outputStatisticalData(conn, cols, userdata, outputStatisticalData);

			cols.add(StringUtil
					.enquote(((MesagoCardDto) userdata.cardInfo).V_IMAGE_PATH));

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		} finally {
			System.out.println("press=" + String.valueOf(press));
			System.out.println("rfid2013=" + String.valueOf(rfid2013));
		}
		return true;
	}

	/**
	 * 【バッチ作成用】当日マスターデータをTXT形式でダウンロード
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param type
	 *            分類帯
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadBadgeAppointeddayData(
			HttpServletRequest request, HttpServletResponse response,
			String outputFileName, Connection conn,
			List<MesagoUserDataDto> userDataList, String dim, String type)
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
		List<String> header = new ArrayList<String>();

		for (int nHeader = 1; nHeader <= 5; nHeader++) {
			header.add(StringUtil.enquote(String.valueOf(nHeader)));
		}

		FileUtil.writer(header, writer, dim);

		int barcode = Integer.MIN_VALUE;
		if ("11.eps".equals(type)) { // VIP
			barcode = MesagoConfig.barcodeStartForAppointeddayVIP;
		} else if ("14.eps".equals(type)) { // JNA認定講師
			barcode = MesagoConfig.barcodeStartForAppointeddayJNA;
		}

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (MesagoUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// 注文番号
			cols.add(StringUtil.enquote("B" + String.valueOf(++barcode) + "B"));
			// 分類帯
			cols.add(StringUtil.enquote(type));
			// 会社名
			cols.add(StringUtil
					.enquote(normalizedCompanyName(userdata.cardInfo.V_CORP)));
			// 空列
			cols.add(StringUtil.enquote(""));
			// 氏名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1 + "　様"));

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
	 * 【バッチ作成用】当日(学生／その他)マスターデータをTXT形式でダウンロード
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param studentCount
	 *            学生バーコード開始番号
	 * @param otherCount
	 *            その他バーコード開始番号
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadBadgeStudentAppointeddayData(
			HttpServletRequest request, HttpServletResponse response,
			String outputFileName, Connection conn,
			List<MesagoUserDataDto> userDataList, String dim, int studentCount,
			int otherCount) throws ServletException, IOException, SQLException {

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

		for (int nHeader = 1; nHeader <= 5; nHeader++) {
			header.add(StringUtil.enquote(String.valueOf(nHeader)));
		}

		FileUtil.writer(header, writer, dim);

		// int barcode = Integer.MIN_VALUE;
		// if ("11.eps".equals(type)) { // VIP
		// barcode = MesagoConfig.barcodeStartForAppointeddayVIP;
		// } else if ("14.eps".equals(type)) { // JNA認定講師
		// barcode = MesagoConfig.barcodeStartForAppointeddayJNA;
		// }

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (MesagoUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			int barcode = Integer.MIN_VALUE;
			String epsType;
			if ("BN14"
					.equals(((MesagoCardDto) userdata.cardInfo).V_TICKET_TYPE)) {
				barcode = ++masterStudentCount;
				epsType = "12.eps";
			} else {
				barcode = ++masterOtherCount;
				epsType = "10.eps";
			}

			// 注文番号
			cols.add(StringUtil.enquote("B" + String.valueOf(barcode) + "B"));
			// 分類帯
			cols.add(StringUtil.enquote(epsType));
			// 会社名
			cols.add(StringUtil
					.enquote(normalizedCompanyName(userdata.cardInfo.V_CORP)));
			// 空列
			cols.add(StringUtil.enquote(""));
			// 氏名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1 + "　様"));

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		} finally {
			System.out.println("◆ファイル名：" + outputFileName);
			System.out.println("◆最後の学生バーコード番号："
					+ String.valueOf(masterStudentCount));
			System.out.println("◆最後の学生バーコード番号："
					+ String.valueOf(masterOtherCount));
		}
		return true;
	}

	/**
	 * 【バッチ作成用】事前登録マスターデータをTXT形式でダウンロード
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadBadgePreentryData(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Connection conn, List<MesagoUserDataDto> userDataList, String dim)
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
		List<String> header = new ArrayList<String>();

		for (int nHeader = 1; nHeader <= 15; nHeader++) {
			header.add(StringUtil.enquote(String.valueOf(nHeader)));
		}

		FileUtil.writer(header, writer, dim);

		int barcode = MesagoConfig.barcodeStartForPreentry; // バーコード番号

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (MesagoUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// 注文番号
			cols.add(StringUtil.enquote("B" + String.valueOf(++barcode) + "B"));
			// 分類帯
			cols.add(StringUtil
					.enquote(((MesagoCardDto) userdata.cardInfo).V_TICKET_TYPE
							+ ".eps"));
			// 郵便番号
			cols.add(StringUtil.enquote("〒" + userdata.cardInfo.V_ZIP));
			// 住所
			String addr1 = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR1, userdata.cardInfo.V_ADDR2,
					userdata.cardInfo.V_ADDR3);
			cols.add(StringUtil.enquote(addr1));
			// ビル名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR4));
			// 空列
			cols.add(StringUtil.enquote(""));
			if ("勤務先".equals(((MesagoCardDto) userdata.cardInfo).SEND_FLG)) {
				// 会社名
				cols.add(StringUtil
						.enquote(normalizedCompanyName(userdata.cardInfo.V_CORP)));
				// 所属役職1
				cols.add(StringUtil.enquote(userdata.cardInfo.V_DEPT1));
				// 所属役職2
				cols.add(StringUtil.enquote(userdata.cardInfo.V_BIZ1));
			} else {
				// 会社名
				cols.add(StringUtil.enquote(""));
				// 所属役職1
				cols.add(StringUtil.enquote(""));
				// 所属役職2
				cols.add(StringUtil.enquote(""));
			}
			// 所属役職3
			cols.add(StringUtil.enquote(""));
			// 氏名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1 + "　様"));
			// 会社名
			cols.add(StringUtil
					.enquote(normalizedCompanyName(userdata.cardInfo.V_CORP)));
			// 空列
			cols.add(StringUtil.enquote(""));
			// 氏名(敬称略)
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1));
			// 裏画像
			cols.add(StringUtil.enquote("U"
					+ ((MesagoCardDto) userdata.cardInfo).V_TICKET_TYPE
					+ ".eps"));

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
	 * 会社名の正規化
	 * 
	 * @param companyName
	 *            会社名
	 * @return 正規化後の会社名
	 */
	private static String normalizedCompanyName(String companyName) {
		if (StringUtil.isNotEmpty(companyName)) {
			final String NGLIST[] = { "なし", "ナシ", "無し", "開店準備中" };
			for (String ngWord : NGLIST) {
				if (ngWord.equals(companyName)) { // 完全一致で判断
					companyName = null;
					break;
				}
			}
		}
		return companyName;
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
			List<MesagoUserDataDto> userDataList, String dim)
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
		for (MesagoUserDataDto userdata : userDataList) {

			// プレスを出力対象外とする
			if (MesagoConstants.PRESS_RFID_LIST.contains(userdata.id)
					|| userdata.id.startsWith("8")) {
				continue;
			}

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
	 * 原票種別項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return 原票種別項目を出力後の出力バッファ
	 */
	public static List<String> outputTicketType(List<String> cols,
			MesagoUserDataDto userdata) {
		TICKET_TYPE type = getTicketType(userdata);
		return outputTicketType(cols, userdata, type);
	}

	/**
	 * 原票種別項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param type
	 *            原票種別
	 * @return 原票種別項目を出力後の出力バッファ
	 */
	public static List<String> outputTicketType(List<String> cols,
			MesagoUserDataDto userdata, TICKET_TYPE type) {
		cols.add(StringUtil
				.enquote(TICKET_TYPE.preentry == type ? MesagoConfig.MARK : "")); // 来場事前登録
		cols.add(StringUtil
				.enquote(TICKET_TYPE.invitation == type ? MesagoConfig.MARK
						: "")); // 招待状
		cols.add(StringUtil
				.enquote(TICKET_TYPE.vip_invitation == type ? MesagoConfig.MARK
						: "")); // VIP招待券
		cols.add(StringUtil
				.enquote(TICKET_TYPE.appointedday == type ? MesagoConfig.MARK
						: "")); // 当日売り券
		return cols;
	}

	/**
	 * 氏名および住所情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputNameAndCompanyData(List<String> cols,
			MesagoUserDataDto userdata) {
		// 会社名
		cols.add(StringUtil
				.enquote(normalizeCompanyName(userdata.cardInfo.V_CORP)));
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
		// Title
		cols.add(StringUtil.enquote(userdata.cardInfo.V_TITLE));
		// 氏名
		String name = StringUtil.concat(userdata.cardInfo.V_NAME1,
				userdata.cardInfo.V_NAME2);
		cols.add(StringUtil.enquote(name));
		return cols;
	}

	/**
	 * 会社名の正規化
	 * 
	 * @param value
	 *            会社名
	 * @return 正規化後の会社名
	 */
	public static String normalizeCompanyName(String value) {
		if (StringUtil.isNotEmpty(value)) {
			// 略称表記の括弧を全角に変換
			Set<Entry<String, String>> entrySet1 = MesagoConstants.CONVERT_COMPANY_SHORTNAME1
					.entrySet();
			for (Entry<String, String> entry : entrySet1) {
				value = StringUtil.replace(value, entry.getKey(),
						entry.getValue());
			}
			// 略称表記に変換
			Set<Entry<String, String>> entrySet2 = MesagoConstants.CONVERT_COMPANY_SHORTNAME2
					.entrySet();
			for (Entry<String, String> entry : entrySet2) {
				value = StringUtil.replace(value, entry.getKey(),
						entry.getValue());
			}
		}
		return value;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputAddressData(List<String> cols,
			MesagoUserDataDto userdata) {
		boolean isOversea = isOversea(userdata);
		return outputAddressData(cols, userdata, isOversea);
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param isOversea
	 *            海外住所フラグ
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputAddressData(List<String> cols,
			MesagoUserDataDto userdata, boolean isOversea) {
		// String lackFlg = getLackFlg(userdata);
		// boolean foreign = isOversea && StringUtil.isEmpty(lackFlg);
		boolean foreign = isOversea;
		AnalysisTelFax telAnalysis = new AnalysisTelFax(
				userdata.cardInfo.V_TEL, foreign);
		telAnalysis.execute(false);
		// TEL
		cols.add(StringUtil.enquote(userdata.cardInfo.V_TEL));
		// TEL（国番号）
		cols.add(StringUtil.enquote(telAnalysis.country));
		// TEL（市外局番）
		cols.add(StringUtil.enquote(telAnalysis.areaCode));
		// TEL（市内局番-番号）
		cols.add(StringUtil.enquote(telAnalysis.localCode));
		// 内線番号
		cols.add(StringUtil.enquote(telAnalysis.extension));

		AnalysisTelFax faxAnalysis = new AnalysisTelFax(
				userdata.cardInfo.V_FAX, foreign);
		faxAnalysis.execute(false);
		// FAX
		cols.add(StringUtil.enquote(userdata.cardInfo.V_FAX));
		// FAX（国番号）
		cols.add(StringUtil.enquote(faxAnalysis.country));
		// FAX（市外局番）
		cols.add(StringUtil.enquote(faxAnalysis.areaCode));
		// FAX（市内局番-番号）
		cols.add(StringUtil.enquote(faxAnalysis.localCode));
		// Email
		cols.add(StringUtil.enquote(userdata.cardInfo.V_EMAIL));
		// Web
		cols.add(StringUtil.enquote(userdata.cardInfo.V_URL));
		// 送付先
		cols.add(StringUtil
				.enquote(((MesagoCardDto) userdata.cardInfo).SEND_FLG));
		// 国
		String country = userdata.cardInfo.V_COUNTRY;
		if (StringUtil.isEmpty(country)
				&& !((MesagoCardDto) userdata.cardInfo).unmatching) {
			country = "Japan";
		}
		cols.add(StringUtil.enquote(normalizedCountry(country)));
		// 郵便番号
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
		// 都道府県
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
		// 市区町村
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR2));
		// 住所
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR3));
		// ビル名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR4));
		return cols;
	}

	/**
	 * 国名の正規化
	 * 
	 * @param country
	 *            国名
	 * @return 正規化後の国名
	 */
	private static String normalizedCountry(String country) {
		if (StringUtil.isNotEmpty(country)) {
			Set<Entry<String, String>> entrySet = MesagoConstants.CONVERT_COUNTRY_NAME
					.entrySet();
			for (Entry<String, String> entry : entrySet) {
				country = StringUtil.replace(country, entry.getKey(),
						entry.getValue());
			}
		}
		return country;
	}

	/**
	 * プレス関連項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return プレス関連項目を出力後の出力バッファ
	 */
	public static List<String> outputPressData(List<String> cols,
			MesagoUserDataDto userdata) {
		// プレス媒体名
		cols.add(StringUtil
				.enquote(((MesagoCardDto) userdata.cardInfo).V_P_NAME));
		// プレス媒体区分
		cols.add(StringUtil
				.enquote(((MesagoCardDto) userdata.cardInfo).V_P_NAME_KUBUN));

		String bizSlx = MesagoSLXCodeUtil
				.getPressBizSLX(((MesagoCardDto) userdata.cardInfo).V_P_BIZ); // プレス業種区分SLXコード
		String specializedInSlx = MesagoSLXCodeUtil
				.getPressBizSLX(((MesagoCardDto) userdata.cardInfo).V_P_OCC); // プレス職種区分SLXコード

		// プレス業種区分
		cols.add(StringUtil.enquote(MesagoSLXCodeUtil.getPressBiz(bizSlx)));
		// プレス業種区分コード
		cols.add(StringUtil.enquote(bizSlx));
		// プレス職種区分
		cols.add(StringUtil.enquote(MesagoSLXCodeUtil
				.getPressSpecializedIn(specializedInSlx)));
		// プレス職種区分コード
		cols.add(StringUtil.enquote(specializedInSlx));
		return cols;
	}

	/**
	 * 統計情報の出力
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param outputStatisticalData
	 *            統計情報出力フラグ
	 * @return 統計情報を出力後の出力バッファ
	 * @throws SQLException
	 */
	public static List<String> outputStatisticalData(Connection conn,
			List<String> cols, MesagoUserDataDto userdata,
			boolean outputStatisticalData) throws SQLException {
		return outputStatisticalData(conn, cols, userdata,
				outputStatisticalData, MesagoConfig.MONTH, MesagoConfig.DAYS);
	}

	/**
	 * 統計情報の出力
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param outputStatisticalData
	 *            統計情報出力フラグ
	 * @param month
	 *            会期月
	 * @param days
	 *            会期日リスト
	 * @return 統計情報を出力後の出力バッファ
	 * @throws SQLException
	 */
	public static List<String> outputStatisticalData(Connection conn,
			List<String> cols, MesagoUserDataDto userdata,
			boolean outputStatisticalData, String month, String days[])
			throws SQLException {

		// 来場登録日
		boolean first = false;
		if (!outputStatisticalData) {
			for (int nIndex = 1; nIndex <= days.length; nIndex++) {
				cols.add(StringUtil.enquote(""));
				cols.add(StringUtil.enquote(""));
			}
		} else {
			if (userdata.visitFlgs == null) {
				String imagePath = ((MesagoCardDto) userdata.cardInfo).V_IMAGE_PATH;
				if (StringUtil.isNotEmpty(imagePath)) {
					boolean flg1 = StringUtil.find(imagePath, month + days[0]);
					boolean flg2 = StringUtil.find(imagePath, month + days[1]);
					boolean flg3 = StringUtil.find(imagePath, month + days[2]);
					userdata.visitFlgs = new boolean[3];
					userdata.visitFlgs[0] = flg1;
					userdata.visitFlgs[1] = flg2;
					userdata.visitFlgs[2] = flg3;
				}
			}
			if (userdata.visitFlgs != null) {
				for (boolean visit : userdata.visitFlgs) {
					if (!first && visit) {
						first = true;
						cols.add(StringUtil.enquote("T"));
					} else {
						cols.add(StringUtil.enquote(""));
					}
				}
				// 来場日
				// String visitedDay = userdata.timeByRfid.substring(8, 10);
				// for (String day : MesagoConfig.DAYS) {
				// if (day.equals(visitedDay)) {
				// cols.add(StringUtil.enquote("T"));
				// } else {
				// cols.add(StringUtil.enquote(""));
				// }
				// }
				for (boolean visit : userdata.visitFlgs) {
					cols.add(StringUtil.enquote(visit ? "T" : ""));
				}
			} else {
				for (int nIndex = 1; nIndex <= days.length; nIndex++) {
					cols.add(StringUtil.enquote(""));
					cols.add(StringUtil.enquote(""));
				}
			}
		}
		return cols;
	}

	/**
	 * 【事前登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputEnqueteDataForPreentry(List<String> cols,
			MesagoUserDataDto userdata) {
		return outputEnqueteDataForPreentry(cols, userdata,
				MesagoSLXCodeUtil.accOfStaffForPreRegist);
	}

	/**
	 * 【事前登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param accOfStaffForPreRegist
	 *            従業員規模<b>Map</b>(事前登録用)
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputEnqueteDataForPreentry(List<String> cols,
			MesagoUserDataDto userdata,
			Map<String, String> accOfStaffForPreRegist) {

		// 業種番号
		cols.add(StringUtil.enquote(""));
		// 業種区分
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun)));
		// 業種区分その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1_other));
		// SLX業種区分コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q1_code));
		// 専門分野
		cols.add(normalizedEnqueteText1_4(StringUtil
				.enquote(userdata.questionInfo.V_Q2)));
		// SLX専門分野コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q2_code));
		// 職種番号
		cols.add(StringUtil.enquote(""));
		// 職種区分
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q3_kubun)));
		// 職種区分その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3_other));
		// SLX職種区分コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q3_code));
		// 役職番号
		cols.add(StringUtil.enquote(""));
		// 役職区分
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun)));
		// 役職区分その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q4_other));
		// SLX役職区分コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q4_code));
		// 従業員数番号
		cols.add(StringUtil.enquote(""));
		// 従業員数
		String q5Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q5;
		cols.add(StringUtil.enquote(accOfStaffForPreRegist.get(q5Num)));
		// SLX従業員数コード
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5));
		// 商品買付け決定権
		cols.add(StringUtil
				.enquote(normalizedEnqueteText6_7(userdata.questionInfo.V_Q6)));
		// 来場動機
		cols.add(StringUtil
				.enquote(normalizedEnqueteText6_7(userdata.questionInfo.V_Q7)));
		// 来場動機その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7_other));
		// 出展検討
		cols.add(StringUtil
				.enquote(normalizedEnqueteText8(userdata.questionInfo.V_Q8)));
		return cols;
	}

	/**
	 * 【当日登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param slxMaster
	 *            SLXコードマスター
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputEnqueteDataForAppointedday(
			List<String> cols, MesagoUserDataDto userdata,
			Map<String, MesagoSLXDto> slxMaster) {
		Map<String, MesagoSLXDto> contactPostType = MesagoSLXCodeUtil.contactPostType;
		Map<String, MesagoSLXDto> contactPostGroup = MesagoSLXCodeUtil.contactPostGroup;
		Map<String, MesagoSLXDto> accOfStaff = MesagoSLXCodeUtil.accOfStaff;
		return outputEnqueteDataForAppointedday(cols, userdata, slxMaster,
				contactPostType, contactPostGroup, accOfStaff);
	}

	/**
	 * 【当日登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param slxMaster
	 *            SLXコードマスター
	 * @param contactPostType
	 *            職種区分<b>Map</b>
	 * @param contactPostGroup
	 *            役職区分<b>Map</b>
	 * @param accOfStaff
	 *            従業員数コード<b>Map</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	public static List<String> outputEnqueteDataForAppointedday(
			List<String> cols, MesagoUserDataDto userdata,
			Map<String, MesagoSLXDto> slxMaster,
			Map<String, MesagoSLXDto> contactPostType,
			Map<String, MesagoSLXDto> contactPostGroup,
			Map<String, MesagoSLXDto> accOfStaff) {
		// TODO: 事前登録の場合はSLXコードから登録券番号を逆変換

		String q1Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun;
		MesagoSLXDto slxInfo = StringUtil.isNotEmpty(q1Num) ? slxMaster
				.get(q1Num) : null;
		// 業種番号(アンケート項目番号)
		cols.add(StringUtil.enquote(q1Num));
		// 業種区分
		cols.add(StringUtil.enquote(slxInfo != null ? slxInfo.bizSlx : ""));
		// 業種区分その他テキスト
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(userdata.questionInfo.V_Q1_other)));
		// SLX業種区分コード
		cols.add(StringUtil.enquote(slxInfo != null ? slxInfo.bizSlxCode : ""));
		// 専門分野
		cols.add(StringUtil.enquote(slxInfo != null ? slxInfo.accSlx : ""));
		// SLX専門分野コード
		cols.add(StringUtil.enquote(slxInfo != null ? slxInfo.accSlxCode : ""));

		String q3Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q3_kubun;
		MesagoSLXDto bizInfo = contactPostType.get(q3Num);
		// 職種番号(アンケート項目番号)
		cols.add(StringUtil.enquote(q3Num));
		// 職種区分
		cols.add(StringUtil.enquote(bizInfo != null ? bizInfo.contactPostType
				: ""));
		// 職種区分その他テキスト
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q3_other)));
		// SLX職種区分コード
		cols.add(StringUtil
				.enquote(bizInfo != null ? bizInfo.contactPostTypeSLXCode : ""));

		String q4Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun;
		MesagoSLXDto positionInfo = contactPostGroup.get(q4Num);
		// 役職番号(アンケート項目番号)
		cols.add(StringUtil.enquote(q4Num));
		// 役職区分
		cols.add(StringUtil
				.enquote(positionInfo != null ? positionInfo.contactPostGroup
						: ""));
		// 役職区分その他テキスト
		cols.add(StringUtil
				.enquote(normalizedEnqueteText1_4(userdata.questionInfo.V_Q4_other)));
		// SLX役職区分コード
		cols.add(StringUtil
				.enquote(positionInfo != null ? positionInfo.contactPostGroupSLXCode
						: ""));

		String q5Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q5;
		MesagoSLXDto accOfStaffInfo = accOfStaff.get(q5Num);
		// 従業員数番号(アンケート項目番号)
		cols.add(StringUtil.enquote(q5Num));
		// 従業員数
		cols.add(StringUtil
				.enquote(accOfStaffInfo != null ? accOfStaffInfo.accOfStaff
						: ""));
		// SLX従業員数コード
		cols.add(StringUtil
				.enquote(accOfStaffInfo != null ? accOfStaffInfo.accOfStaffSLXCode
						: ""));

		// 商品買付け決定権(アンケート項目番号)
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));
		// 来場動機(アンケート項目番号)
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7));
		// 来場動機その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7_other));
		// 出展検討
		cols.add(StringUtil
				.enquote(normalizedEnqueteText8(userdata.questionInfo.V_Q8)));
		return cols;
	}

	/**
	 * アンケート回答内容の正規化(1～4)
	 * 
	 * @param enqueteText
	 *            アンケート回答テキスト
	 * @return 正規化後のテキスト
	 */
	public static String normalizedEnqueteText1_4(String enqueteText) {
		if (StringUtil.isNotEmpty(enqueteText)) {
			for (String remove : MesagoConfig.REMOVE_ENQUETE_TEXTS) {
				enqueteText = StringUtil.replace(enqueteText, remove, "");
			}
		}
		return enqueteText;
	}

	/**
	 * アンケート回答内容の正規化(6～7)
	 * 
	 * @param enqueteText
	 *            アンケート回答テキスト
	 * @return 正規化後のテキスト
	 */
	public static String normalizedEnqueteText6_7(String enqueteText) {
		if (StringUtil.isNotEmpty(enqueteText) && enqueteText.length() > 1) {
			enqueteText = enqueteText.substring(1, 2);
		}
		return enqueteText;
	}

	/**
	 * アンケート回答内容の正規化(8)
	 * 
	 * @param enqueteText
	 *            アンケート回答テキスト
	 * @return 正規化後のテキスト
	 */
	public static String normalizedEnqueteText8(String enqueteText) {
		if (StringUtil.isNotEmpty(enqueteText)) {
			enqueteText = StringUtil.convertHalfWidthString(enqueteText, false); // 半角英字に変換
			if ("Yes".equals(enqueteText) || "1".equals(enqueteText)) {
				enqueteText = "T";
			} else if ("No".equals(enqueteText)) {
				enqueteText = "";
			}
		}
		return enqueteText;
	}

	/**
	 * 登録日時の正規化
	 * 
	 * @param registDate
	 *            登録日時
	 * @return 正規化後の登録日時
	 */
	public static String normalizedRegistDate(String registDate) {
		if (StringUtil.isNotEmpty(registDate)) {
			registDate = StringUtil.replace(registDate, "-", "/");
		}
		return registDate;
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            <b>MesagoCardDto</b>
	 * @return 原票種別
	 */
	private static TICKET_TYPE getTicketType(UserDataDto userdata) {
		MesagoUtil util = new MesagoUtil();
		if (util.isAppEntry(userdata)) { // 当日登録データである場合
			return TICKET_TYPE.appointedday;
		} else if (util.isInvitation(userdata)) { // 招待状ユーザーである場合
			return TICKET_TYPE.invitation;
		} else if (util.isVIPInvitation(userdata)) { // VIP招待券ユーザーである場合
			return TICKET_TYPE.vip_invitation;
		} else if (util.isPreEntry(userdata)) { // 事前登録データである場合
			return TICKET_TYPE.preentry;
		} else { // 不正な種別である場合
			return TICKET_TYPE.unknown;
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
	public static String zipNormalize(String zip) {
		if (is7Zip(zip)) {
			return zip.substring(0, 3) + "-" + zip.substring(3);
		}
		return zip;
	}

	/**
	 * 海外住所フラグの検証
	 * 
	 * @param cardInfo
	 *            <b>MesagoCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(MesagoUserDataDto userdata) {
		MesagoUtil util = new MesagoUtil();
		boolean result;
		String country = userdata.cardInfo.V_COUNTRY;
		if (util.isPreEntry(userdata) || util.isPreVIP(userdata)) { // 事前登録データである場合
			boolean eng = "Japan".equals(country);
			boolean jpn = "日本".equals(country);
			result = !eng && !jpn;
		} else { // 当日登録データである場合
			boolean wildcard1 = "■".equals(country);
			boolean wildcard2 = "●".equals(country);
			boolean blank = StringUtil.isEmpty(country);
			result = !wildcard1 && !wildcard2 && !blank;
		}
		return result;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>MesagoUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, MesagoUserDataDto> getMap(
			List<MesagoUserDataDto> userDataList) {
		Map<String, MesagoUserDataDto> map = new HashMap<String, MesagoUserDataDto>();
		for (MesagoUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 【登録券番号マッチング高速化対応】DBアクセスに依らずユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 * @return
	 */
	public static Map<String, MesagoUserDataDto> getMapWithPreentryNumber(
			List<MesagoUserDataDto> userDataList) {
		Map<String, MesagoUserDataDto> map = new HashMap<String, MesagoUserDataDto>();
		for (MesagoUserDataDto userData : userDataList) {
			if (StringUtil
					.isNotEmpty(((MesagoCardDto) userData.cardInfo).PREENTRY_ID)) {
				map.put(((MesagoCardDto) userData.cardInfo).PREENTRY_ID,
						userData);
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
		String sql;
		if ("preentry".equals(type)) {
			sql = "update preentry set V_VISITOR_FLG = '1' where V_VID =?;";
		} else {
			sql = "update appointedday set V_VISITOR_FLG = '1' where V_VID = ?;";
		}
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, id);
		ps.executeUpdate();
		if (ps != null) {
			ps.close();
		}
	}

	/**
	 * 事前登録者の来場フラグを登録
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param registId
	 *            事前登録者の登録番号
	 * @param day
	 *            来場日
	 * @throws SQLException
	 */
	public static void updateVisitorFlgForPreentry(Connection conn,
			String registId, String day) throws SQLException {
		String sql = "UPDATE preentry SET DAY" + day + "='1' WHERE V_NO=?;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, registId);
		if (1 != ps.executeUpdate()) {
			System.out.println(registId + "は存在していません。");
		}
		if (ps != null) {
			ps.close();
		}
	}

	/**
	 * NGなUnicodeのリストの初期化
	 * 
	 * @return NGなUnicodeのリスト
	 */
	private static List<Integer> createNgUnicodeCharacterList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(9824);
		list.add(9827);
		list.add(9829);
		list.add(9830);
		list.add(9674);
		list.add(9828);
		list.add(9831);
		list.add(9826);
		list.add(9825);
		list.add(10084);
		list.add(10085);
		list.add(10083);
		list.add(128147);
		list.add(128148);
		list.add(128149);
		list.add(128150);
		list.add(128151);
		list.add(128152);
		list.add(128157);
		list.add(128158);
		list.add(128159);
		list.add(34);
		list.add(38);
		list.add(60);
		list.add(62);
		list.add(160);
		list.add(160);
		list.add(8764);
		list.add(8629);
		list.add(8224);
		list.add(8225);
		list.add(9768);
		list.add(169);
		list.add(174);
		list.add(153);
		list.add(9744);
		list.add(9745);
		list.add(9746);
		list.add(9763);
		list.add(9762);
		list.add(9728);
		list.add(9729);
		list.add(9730);
		list.add(9731);
		list.add(9833);
		list.add(9834);
		list.add(9835);
		list.add(9836);
		list.add(9837);
		list.add(9838);
		list.add(9839);
		list.add(9823);
		list.add(9822);
		list.add(9821);
		list.add(9820);
		list.add(9819);
		list.add(9818);
		list.add(9817);
		list.add(9816);
		list.add(9815);
		list.add(9814);
		list.add(9813);
		list.add(9812);
		list.add(9800);
		list.add(9801);
		list.add(9802);
		list.add(9803);
		list.add(9804);
		list.add(9805);
		list.add(9806);
		list.add(9807);
		list.add(9808);
		list.add(9809);
		list.add(9810);
		list.add(9811);
		list.add(9737);
		list.add(9790);
		list.add(9791);
		list.add(9792);
		list.add(9794);
		list.add(9795);
		list.add(9796);
		list.add(9797);
		list.add(9798);
		list.add(9799);
		list.add(9765);
		list.add(9764);
		list.add(9877);
		list.add(10017);
		return list;
	}

	/**
	 * NG記号のリストの初期化
	 * 
	 * @return NGなUnicodeのリスト
	 */
	private static List<String> createNgMarkList() {
		List<String> list = new ArrayList<String>();
		list.add("○");
		list.add("◎");
		list.add("△");
		list.add("▲");
		list.add("▽");
		list.add("▼");
		list.add("□");
		// list.add("■");
		list.add("◇");
		list.add("◆");
		list.add("☆");
		list.add("★");
		list.add("♂");
		list.add("♀");
		list.add("〒");
		list.add("※");
		list.add("→");
		list.add("←");
		return list;
	}
}