package jp.co.freedom.master.utilities.mesago.ilt;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoConfig.TICKET_TYPE;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【ILT】ILT向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class IltUtil extends Util {

	/**
	 * NGバーコード一覧
	 */
	private static List<String> NG_BARCODES = initializedNgBarcode();

	private static List<String> initializedNgBarcode() {
		List<String> list = new ArrayList<String>();
		list.add("002009");
		list.add("000929");
		list.add("661068");
		list.add("000584");
		list.add("104094");
		list.add("000859");
		list.add("000054");
		list.add("322672");
		list.add("116554");
		list.add("2107001908");
		list.add("1011007194");
		list.add("1011008893");
		return list;
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
		if (((MesagoUserDataDto) userdata).vipSponsor
				&& ((MesagoUserDataDto) userdata).preentry2) {
			return true;
		} else {
			String barcodeStr = userdata.id;
			if (StringUtil.isNotEmpty(barcodeStr)
					&& StringUtil.integerStringCheck(barcodeStr)) {
				int barcode = Integer.parseInt(barcodeStr);
				boolean check1 = 800001 <= barcode && barcode <= 800044;
				return check1;
			}
		}
		return false;
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            <b>MesagoCardDto</b>
	 * @return 原票種別
	 */
	private static TICKET_TYPE getTicketType(UserDataDto userdata) {
		IltUtil util = new IltUtil();
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
	 * 海外住所フラグの検証
	 * 
	 * @param cardInfo
	 *            <b>MesagoCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(MesagoUserDataDto userdata) {
		IltUtil util = new IltUtil();
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

	/**
	 * 【ILT】ユーザーデータのインスタンスを生成しリストに格納
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
			// String[] row = removeLastSpaceElement(csvData.get(nIndex));
			String[] row = csvData.get(nIndex);
			if (row.length == 2) {// リクエストコードの行
				if (dataDto == null) {
					dataDto = new MesagoUserDataDto();
				}
				// リクエストコードの格納
				String request = row[1].substring(1);
				dataDto.requestCode.add(request);
			} else if (row.length == 5) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new MesagoUserDataDto();
				dataDto.reader = row[3];// バーコードリーダーID
				String barcode = row[0];
				if (barcode.length() == 7) {
					barcode = barcode.substring(1);
				} else if (barcode.length() == 10 && barcode.startsWith("3377")) {
				} else if (barcode.length() == 10 && barcode.startsWith("0000")) {
					barcode = barcode.substring(4);
				} else {
					System.out.println("不正バーコード：" + barcode);
				}
				dataDto.id = barcode;
				dataDto.timeByRfid = row[1] + row[2];
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

			if (StringUtil.isNotEmpty(rs.getString("V_VIP_sponsor"))) {
				userdata.vipSponsor = true;
			}
			if (StringUtil.isNotEmpty(rs.getString("V_VIP_exhibi"))) {
				userdata.vipExhibi = true;
			}
			if (userdata.vipSponsor || userdata.vipExhibi) {
				userdata.vipInvitation = true; // VIP
			} else if (StringUtil.isNotEmpty(rs.getString("V_SH"))) {
				userdata.invitation = true; // 招待状
			} else {
				userdata.appointedday = true; // 当日
			}

			/*
			 * 名刺情報
			 */
			cardInfo.V_CORP = MesagoUtil.normalize(rs.getString("V_CORP")); // 会社名
			String dept1 = MesagoUtil.normalize(rs.getString("V_DEPT1")); // 部署1
			String dept2 = MesagoUtil.normalize(rs.getString("V_DEPT2")); // 部署2
			String dept3 = MesagoUtil.normalize(rs.getString("V_DEPT3")); // 部署3
			String dept4 = MesagoUtil.normalize(rs.getString("V_DEPT4")); // 部署4
			cardInfo.V_DEPT1 = StringUtil.concat(dept1, dept2, dept3, dept4);
			String biz1 = MesagoUtil.normalize(rs.getString("V_BIZ1")); // 役職1
			String biz2 = MesagoUtil.normalize(rs.getString("V_BIZ2")); // 役職2
			String biz3 = MesagoUtil.normalize(rs.getString("V_BIZ3")); // 役職3
			String biz4 = MesagoUtil.normalize(rs.getString("V_BIZ4")); // 役職4
			cardInfo.V_BIZ1 = StringUtil.concat(biz1, biz2, biz3, biz4);
			cardInfo.V_NAME1 = MesagoUtil.normalize(rs.getString("V_NAME1")); // 氏名姓漢字
			cardInfo.V_NAME2 = MesagoUtil.normalize(rs.getString("V_NAME2")); // 氏名名漢字
			cardInfo.V_TEL = MesagoUtil.normalize(rs.getString("V_TEL")); // 電話番号
			cardInfo.V_FAX = MesagoUtil.normalize(rs.getString("V_FAX")); // FAX番号
			cardInfo.V_EMAIL = MesagoUtil.normalize(rs.getString("V_EMAIL")); // Email
			cardInfo.V_URL = MesagoUtil.normalizedUrl(MesagoUtil.normalize(rs
					.getString("V_WEB"))); // Web
			cardInfo.V_COUNTRY = MesagoUtil
					.normalize(rs.getString("V_Country")); // 国名
			cardInfo.V_ZIP = MesagoUtil.zipNormalize(MesagoUtil.normalize(rs
					.getString("V_ZIP"))); // 郵便番号
			cardInfo.V_ADDR1 = MesagoUtil.normalize(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = MesagoUtil.normalize(rs.getString("V_ADDR2")); // 市区部
			cardInfo.V_ADDR3 = StringUtil.concat(
					MesagoUtil.normalize(rs.getString("V_ADDR3")),
					MesagoUtil.normalize(rs.getString("V_ADDR4"))); // 住所
			cardInfo.V_ADDR4 = MesagoUtil.normalize(rs.getString("V_ADDR5")); // ビル名

			/*
			 * アンケート情報
			 */
			String q1Kubun = MesagoUtil.normalize(rs.getString("V_Q1_KUBUN"));
			int q1Index = 18;
			int q1Buff[] = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q1Kubun,
					q1Index);
			String q1Fa = MesagoUtil.normalize(rs.getString("V_Q1_FA5"));
			// 業種区分 その他 FA
			if (StringUtil.isNotEmpty(q1Fa)) {
				q1Buff[q1Index] = 1;
				questionInfo.V_Q1_other = q1Fa; // 業種区分 その他 FA
			}
			// 業種区分
			for (int nIndex = 1; nIndex <= q1Index; nIndex++) {
				if (1 == q1Buff[nIndex]) {
					questionInfo.V_Q1_kubun = MesagoUtil.normalize(String
							.valueOf(nIndex));
					break; // 前方一致
				}
			}

			String q3Kubun = MesagoUtil.normalize(rs.getString("V_Q3_KUBUN"));
			int q3Index = 6;
			int q3Buff[] = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q3Kubun,
					q3Index);
			String q3Fa = MesagoUtil.normalize(rs.getString("V_Q3_FA"));
			// 職種区分 その他 FA
			if (StringUtil.isNotEmpty(q3Fa)) {
				q3Buff[q3Index] = 1;
				questionInfo.V_Q3_other = q3Fa;
			}
			// 職種区分
			for (int nIndex = 1; nIndex <= q3Index; nIndex++) {
				if (1 == q3Buff[nIndex]) {
					questionInfo.V_Q3_kubun = MesagoUtil.normalize(String
							.valueOf(nIndex));
					break; // 前方一致
				}
			}

			String q4Kubun = MesagoUtil.normalize(rs.getString("V_Q4_KUBUN"));
			int q4Index = 4;
			int q4Buff[] = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q4Kubun,
					q4Index);
			String q4Fa = MesagoUtil.normalize(rs.getString("V_Q4_FA"));
			// 役職区分 その他 FA
			if (StringUtil.isNotEmpty(q4Fa)) {
				q4Buff[q4Index] = 1;
				questionInfo.V_Q4_other = q4Fa;
			}
			// 職種区分
			for (int nIndex = 1; nIndex <= q4Index; nIndex++) {
				if (1 == q4Buff[nIndex]) {
					questionInfo.V_Q4_kubun = MesagoUtil.normalize(String
							.valueOf(nIndex));
					break; // 前方一致
				}
			}

			questionInfo.V_Q5 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q5"))); // 従業員数
			questionInfo.V_Q6 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q6"))); // 買付け決定権
			questionInfo.V_Q7 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q7"))); // 来場動機

			String q8 = MesagoUtil.normalize(rs.getString("V_Q8")); // 出展検討
			if (StringUtil.isNotEmpty(q8) && q8.contains("1")) {
				questionInfo.V_Q8_1 = "1";
			}
			if (StringUtil.isNotEmpty(q8) && q8.contains("2")) {
				questionInfo.V_Q8_2 = "1";
			}

			/* プレス情報 */
			cardInfo.V_P_NAME = MesagoUtil.normalize(rs.getString("V_P_NAME"));
			cardInfo.V_P_NAME_KUBUN = MesagoUtil.normalize(rs
					.getString("V_P_NAMEKUBUN"));
			cardInfo.V_P_BIZ = MesagoUtil.normalize(rs.getString("V_P_BIZ"));
			cardInfo.V_P_OCC = MesagoUtil.normalize(rs.getString("V_P_OCC"));

			cardInfo.V_IMAGE_PATH = MesagoUtil.normalize(rs
					.getString("IMAGE_PATH")); // イメージパス

			// 環境依存文字の存在性を確認
			if (IltConfig.EXECUTE_VALIDATE_MODEL_DEPENDENCE
					&& cardInfo.contailsModelDependence()) {
				userdata.result.containsModelDependenceCharacter = true;
			}

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
			for (String day : IltConfig.DAYS) {
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

			if (StringUtil.isNotEmpty(rs.getString("V_VIP_sponsor"))) {
				userdata.vipSponsor = true;
			}
			if (StringUtil.isNotEmpty(rs.getString("V_VIP_exhibi"))) {
				userdata.vipExhibi = true;
			}
			if (userdata.vipSponsor || userdata.vipExhibi) {
				userdata.vipInvitation = true; // VIP
				userdata.preentry2 = true; // 事前登録の主催者VIPであるかどうかの判定に使用
			} else {
				userdata.preentry = true; // 事前登録フラグ
			}

			// バーコード番号
			userdata.id = rs.getString("V_NO"); // バーコード番号
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.PREENTRY_ID = MesagoUtil.normalize(rs.getString("V_NO")); // 登録番号
			cardInfo.V_CORP = MesagoUtil.normalize(rs.getString("V_CORP")); // 会社名
			cardInfo.V_DEPT1 = MesagoUtil.normalize(rs.getString("V_DEPT1")); // 部署
			cardInfo.V_BIZ1 = MesagoUtil.normalize(rs.getString("V_BIZ1")); // 役職
			cardInfo.V_TITLE = MesagoUtil.normalize(rs.getString("V_PREFIX")); // 敬称
			cardInfo.V_NAME1 = MesagoUtil.normalize(rs.getString("V_NAME1")); // 氏名姓漢字
			cardInfo.V_TEL = MesagoUtil.normalize(rs.getString("V_TEL")); // 電話番号
			cardInfo.V_FAX = MesagoUtil.normalize(rs.getString("V_FAX")); // FAX番号
			cardInfo.V_EMAIL = MesagoUtil.normalize(rs.getString("V_EMAIL")); // Email
			cardInfo.V_URL = MesagoUtil.normalize(rs.getString("V_WEB")); // Web
			cardInfo.SEND_FLG = MesagoUtil.normalize(rs.getString("V_SEND")); // 送付先
			cardInfo.V_COUNTRY = MesagoUtil
					.normalize(rs.getString("V_COUNTRY")); // 国名
			cardInfo.V_ZIP = MesagoUtil.zipNormalize(MesagoUtil.normalize(rs
					.getString("V_ZIP"))); // 郵便番号
			cardInfo.V_ADDR1 = MesagoUtil.normalize(rs.getString("V_ADDR1")); // 都道府県
			cardInfo.V_ADDR2 = MesagoUtil.normalize(rs.getString("V_ADDR2")); // 市区部
			cardInfo.V_ADDR3 = MesagoUtil.normalize(rs.getString("V_ADDR3")); // 以下住所
			cardInfo.V_ADDR4 = MesagoUtil.normalize(rs.getString("V_ADDR4")); // ビル名

			/*
			 * アンケート情報
			 */
			questionInfo.V_Q1_kubun = MesagoUtil.normalize(rs
					.getString("V_Q1_KUBUN")); // 業種区分
			questionInfo.V_Q1_other = MesagoUtil.normalize(rs
					.getString("V_Q1_FA"));// 業種区分 その他 FA
			questionInfo.V_Q1_code = MesagoUtil.normalize(rs
					.getString("V_Q1_CODE"));// 業種区分コード
			questionInfo.V_Q2 = MesagoUtil.normalize(rs.getString("V_Q2")); // 専門分野
			questionInfo.V_Q2_code = MesagoUtil.normalize(rs
					.getString("V_Q2_CODE")); // 専門分野(コード)
			questionInfo.V_Q3_kubun = MesagoUtil.normalize(rs
					.getString("V_Q3_KUBUN")); // 職種区分
			questionInfo.V_Q3_other = MesagoUtil.normalize(rs
					.getString("V_Q3_FA"));// 職種区分 その他 FA
			questionInfo.V_Q3_code = MesagoUtil.normalize(rs
					.getString("V_Q3_CODE"));// 職種区分コード

			questionInfo.V_Q4_kubun = MesagoUtil.normalize(rs
					.getString("V_Q4_KUBUN"));// 役職区分
			questionInfo.V_Q4_other = MesagoUtil.normalize(rs
					.getString("V_Q4_FA"));// 役職区分 その他 FA
			questionInfo.V_Q4_code = MesagoUtil.normalize(rs
					.getString("V_Q4_CODE"));// 役職区分コード

			questionInfo.V_Q5 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q5"))); // 従業員数
			questionInfo.V_Q6 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q6"))); // 買付け決定権
			questionInfo.V_Q7 = EnqueteUtil.convertSingleAnswer(MesagoUtil
					.normalize(rs.getString("V_Q7"))); // 来場動機
			questionInfo.V_Q8_1 = MesagoUtil.normalize(rs.getString("V_Q8")); // 出展検討
			cardInfo.REGIST_DATE = rs.getString("V_DATE"); // 登録日時

			// 環境依存文字の存在性を確認
			if (IltConfig.EXECUTE_VALIDATE_MODEL_DEPENDENCE
					&& cardInfo.contailsModelDependence()) {
				userdata.result.containsModelDependenceCharacter = true;
			}

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
		header.add(StringUtil.enquote("VIP招待券(出展社)"));
		header.add(StringUtil.enquote("VIP招待券(主催者)"));
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
		if (IltConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
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
			header.add(StringUtil.enquote("出展検討1"));
			header.add(StringUtil.enquote("出展検討2"));
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
		for (int nDay = 1; nDay <= IltConfig.DAYS.length; nDay++) {
			header.add(StringUtil.enquote("来場登録日" + String.valueOf(nDay) + "日目"));
		}
		for (int nDay = 1; nDay <= IltConfig.DAYS.length; nDay++) {
			header.add(StringUtil.enquote("来場日" + String.valueOf(nDay) + "日目"));
		}
		header.add(StringUtil.enquote("画像パス"));

		FileUtil.writer(header, writer, dim);

		// SLXコード
		Map<String, MesagoSLXDto> slxMap = IltSLXCodeUtil.getSLXMap(conn,
				IltConfig.EXHIBITION_NAME);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		int counter = 0;
		int press = 0;
		int rfid2013 = 0;
		int ngbarcode = 0;
		for (MesagoUserDataDto userdata : userDataList) {
			// NGバーコードを出力対象外とする
			if (NG_BARCODES.contains(userdata.id)) {
				ngbarcode++;
				continue;
			}
			if (!isAppMaster && !isPreMaster) {
				// プレスを出力対象外とする
				if (userdata.id.startsWith("3377")) {
					press++;
					continue;
				}
			}

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
							IltConfig.YEAR, IltConfig.MONTH,
							userdata.cardInfo.V_DAY, IltConfig.HOUR)));
				} else {
					continue;
				}
			} else {
				cols.add(StringUtil.enquote(""));
			}

			// 登録番号(来場事前登録)
			String preentryId = ((MesagoCardDto) userdata.cardInfo).PREENTRY_ID;
			if (StringUtil.isNotEmpty(preentryId)
					&& !preentryId.startsWith("ilt")) {
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
			MesagoUtil.outputNameAndCompanyData(cols, userdata);
			// 住所項目
			MesagoUtil.outputAddressData(cols, userdata, isOversea(userdata));
			// アンケート項目
			if (IltConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				IltUtil util = new IltUtil();
				if (util.isPreEntry(userdata) || util.isPreVIP(userdata)) {
					outputEnqueteDataForPreentry(cols, userdata);
				} else {
					outputEnqueteDataForAppointedday(cols, userdata, slxMap);
				}
			}
			// 登録日時
			cols.add(StringUtil.enquote(MesagoUtil
					.normalizedRegistDate(((MesagoCardDto) userdata.cardInfo).REGIST_DATE)));
			// プレス関連項目
			MesagoUtil.outputPressData(cols, userdata);

			// 統計情報
			MesagoUtil.outputStatisticalData(conn, cols, userdata,
					outputStatisticalData, IltConfig.MONTH, IltConfig.DAYS2);

			// 画像パス
			cols.add(StringUtil.enquote(userdata.cardInfo.V_IMAGE_PATH));

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		} finally {
			System.out.println("press=" + String.valueOf(press));
			System.out.println("rfid2013=" + String.valueOf(rfid2013));
			System.out.println("ngbarcode=" + String.valueOf(ngbarcode));
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
		cols.add(StringUtil
				.enquote(TICKET_TYPE.preentry == type ? MesagoConfig.MARK : "")); // 来場事前登録
		cols.add(StringUtil
				.enquote(TICKET_TYPE.invitation == type ? MesagoConfig.MARK
						: "")); // 招待状
		cols.add(StringUtil
				.enquote(((MesagoUserDataDto) userdata).vipExhibi ? MesagoConfig.MARK
						: "")); // VIP招待券(出展社)
		cols.add(StringUtil
				.enquote(((MesagoUserDataDto) userdata).vipSponsor ? MesagoConfig.MARK
						: "")); // VIP招待券(主催者)
		// cols.add(StringUtil
		// .enquote(TICKET_TYPE.vip_invitation == type ? MesagoConfig.MARK
		// : "")); // VIP招待券
		cols.add(StringUtil
				.enquote(TICKET_TYPE.appointedday == type ? MesagoConfig.MARK
						: "")); // 当日売り券
		return cols;
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
		return MesagoUtil.outputAddressData(cols, userdata, isOversea);
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
		Map<String, MesagoSLXDto> contactPostType = IltSLXCodeUtil.contactPostType;
		Map<String, MesagoSLXDto> contactPostGroup = IltSLXCodeUtil.contactPostGroup;
		Map<String, MesagoSLXDto> accOfStaff = IltSLXCodeUtil.accOfStaff;

		String q1Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun;
		MesagoSLXDto slxInfo = StringUtil.isNotEmpty(q1Num) ? slxMaster
				.get(q1Num) : null;
		// 業種番号(アンケート項目番号)
		cols.add(StringUtil.enquote(q1Num));
		// 業種区分
		cols.add(StringUtil.enquote(slxInfo != null ? slxInfo.bizSlx : ""));
		// 業種区分その他テキスト
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(userdata.questionInfo.V_Q1_other)));
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
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q3_other)));
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
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(userdata.questionInfo.V_Q4_other)));
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
		// 出展検討1
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText8(userdata.questionInfo.V_Q8_1)));
		// 出展検討2
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText8(userdata.questionInfo.V_Q8_2)));
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
		Map<String, String> accOfStaffForPreRegist = IltSLXCodeUtil.accOfStaffForPreRegist;
		// 業種番号
		cols.add(StringUtil.enquote(""));
		// 業種区分
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun)));
		// 業種区分その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1_other));
		// SLX業種区分コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q1_code));
		// 専門分野
		cols.add(MesagoUtil.normalizedEnqueteText1_4(StringUtil
				.enquote(userdata.questionInfo.V_Q2)));
		// SLX専門分野コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q2_code));
		// 職種番号
		cols.add(StringUtil.enquote(""));
		// 職種区分
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q3_kubun)));
		// 職種区分その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3_other));
		// SLX職種区分コード
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q3_code));
		// 役職番号
		cols.add(StringUtil.enquote(""));
		// 役職区分
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText1_4(((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun)));
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
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText6_7(userdata.questionInfo.V_Q6)));
		// 来場動機
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText6_7(userdata.questionInfo.V_Q7)));
		// 来場動機その他テキスト
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7_other));
		// 出展検討1
		cols.add(StringUtil.enquote(MesagoUtil
				.normalizedEnqueteText8(userdata.questionInfo.V_Q8_1)));
		// 出展検討2
		cols.add(StringUtil.enquote(""));
		return cols;
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
			for (String day : IltConfig.DAYS) {
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
}
