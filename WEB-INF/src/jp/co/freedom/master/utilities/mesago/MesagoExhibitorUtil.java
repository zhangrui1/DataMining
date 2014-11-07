package jp.co.freedom.master.utilities.mesago;

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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.gpenquete.GeneralPurposeEnqueteMiningUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoSLXDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * Mesago 出展者納品データ作成
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoExhibitorUtil extends Util {

	/* リクエストコード対応表 */
	/** ダン 株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_DAN = createRequestCodesMapForDan();

	/** エステ・ラ・フェ 株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_ESTHE = createRequestCodesMapForESTHE();

	/** サンナチュラルズ 株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_SUNATURALS = createRequestCodesMapForSunaturals();

	/** MOGA株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_MOGA = createRequestCodesMapForMoga();

	/* リーダー端末対応リスト */
	/** ダン 株式会社向け　バーコードリーダーリスト */
	public static List<String> READER__DAN = createReaderForDan();

	/** エステ・ラ・フェ 株式会社 バーコードリーダーリスト */
	public static List<String> READER_ESTHE = createReaderForESTHE();

	/** サンナチュラルズ株式会社 バーコードリーダーリスト */
	public static List<String> READER_SUNATURALS = createReaderForSunaturals();

	/** MOGA株式会社 バーコードリーダーリスト */
	public static List<String> READER_MOGA = createReaderForMoga();

	/**
	 * ダン株式会社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return ダン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForDan() {
		List<String> reader = new ArrayList<String>();
		reader.add("041");
		return reader;
	}

	/**
	 * エステ・ラ・フェ株式会社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return ダン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForESTHE() {
		List<String> reader = new ArrayList<String>();
		reader.add("043");
		return reader;
	}

	/**
	 * サンナチュラルズ株式会社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return ダン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForSunaturals() {
		List<String> reader = new ArrayList<String>();
		reader.add("039");
		return reader;
	}

	/**
	 * MOGA株式会社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return ダン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForMoga() {
		List<String> reader = new ArrayList<String>();
		reader.add("015");
		reader.add("016");
		return reader;
	}

	/**
	 * ダン 株式会社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return ダン 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForDan() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("0000001", "K-A");
		requests.put("0000002", "K-B");
		requests.put("0000003", "K-C");
		requests.put("0000004", "M-A");
		requests.put("0000005", "M-B");
		requests.put("0000006", "M-C");
		requests.put("0000007", "N-A");
		requests.put("0000008", "N-B");
		requests.put("0000009", "N-C");
		return requests;
	}

	/**
	 * エステ・ラ・フェ 株式会社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return エステ・ラ・フェ 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForESTHE() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("0000001", "資料希望");
		requests.put("0000002", "デモ体験希望");
		requests.put("0000003", "商品購入");
		requests.put("0000004", "酸素関連");
		requests.put("0000005", "その他");
		return requests;
	}

	/**
	 * サンナチュラルズ 株式会社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return サンナチュラルズ 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForSunaturals() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("0000001", "MESA");
		requests.put("0000002", "WAKAN");
		requests.put("0000003", "Resple");
		return requests;
	}

	/**
	 * MOGA 株式会社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return MOGA 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForMoga() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("0011111", "Calgel 1本体験");
		requests.put("0022222", "サービスカウンター");
		requests.put("0033333", "スペシャルレッスン");
		requests.put("0044444", "クラスルーム");
		requests.put("0055555", "その他");
		return requests;
	}

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createInstance(List<String[]> csvData) {
		/** リクエストコードリスト */
		final String REQUEST_CODE = "0000001|0000002|0000003|0000004|0000005|0000006|0000007|0000008|0000009|0000010|0000011|0000012|0000013|0000014|0000015|0000016|0000017|0000018|0000019|0000999|0011111|0022222|0033333|0044444|0055555|0066666";
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		MesagoUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			if (row.length != 0 && StringUtil.find(row[0], REQUEST_CODE)) {// リクエストコード
				if (dataDto == null) {
					dataDto = new MesagoUserDataDto();
				}
				// リクエストコードの格納
				dataDto.requestCode.add(row[0]);
			} else if (row.length == 5) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new MesagoUserDataDto();
				dataDto.reader = row[3];// バーコードリーダーID
				dataDto.id = row[0];
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
	 * 変換表のインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<MesagoUserDataDto> createInstanceForConvertTable(
			List<String[]> csvData) {
		List<MesagoUserDataDto> userData = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			MesagoUserDataDto dataDto = new MesagoUserDataDto();
			if (row.length == 2) {
				String barcode = row[0];
				dataDto.id = barcode;
				dataDto.cardInfo.V_CID = row[1];
				userData.add(dataDto);
			} else {
				System.out.println("変換表データ中に2列ではない行を発見");
			}
		}
		return userData;
	}

	/**
	 * 全ての当日登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO
			MesagoQuestionDto questionInfo = new MesagoQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字

			/* 名刺情報 */
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			String dept1 = rs.getString("V_DEPT1"); // 部署1
			String dept2 = rs.getString("V_DEPT2"); // 部署2
			String dept3 = rs.getString("V_DEPT3"); // 部署3
			String dept4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_DEPT1 = StringUtil.concat(dept1, dept2, dept3, dept4);
			String biz1 = rs.getString("V_BIZ1"); // 役職1
			String biz2 = rs.getString("V_BIZ2"); // 役職2
			String biz3 = rs.getString("V_BIZ3"); // 役職3
			String biz4 = rs.getString("V_BIZ4"); // 役職4
			cardInfo.V_BIZ1 = StringUtil.concat(biz1, biz2, biz3, biz4);
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // Email
			cardInfo.V_COUNTRY = rs.getString("V_Country"); // 国名
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 住所2
			cardInfo.V_ADDR3 = StringUtil.concatWithDelimit("",
					rs.getString("V_ADDR3"), rs.getString("V_ADDR4")); // 住所
			cardInfo.V_ADDR4 = rs.getString("V_ADDR5"); // ビル名

			cardInfo.V_OVERSEA = rs.getString("V_OVERSEA"); // 海外フラグ

			/* アンケート情報 */
			String q1Kubun = (rs.getString("V_Q1_KUBUN"));
			int q1Buff[];
			if (!GeneralPurposeEnqueteMiningUtil.validateForMaxConstraint(
					q1Kubun, 34, " ")) {
				System.out.println("業種 バーコード:" + cardInfo.V_VID + "\t 画像パス:"
						+ rs.getString("IMAGE_PATH") + "\n");
				q1Buff = EnqueteUtil.arrayInit(35);
			} else {
				q1Buff = EnqueteUtil
						.convertMultiAnswerTo1FlgBuffer(q1Kubun, 34);
			}
			int faIndex[] = { 13, 19, 25, 33, 34 };
			// 業種区分(その他FA)
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				String q1Fa = (rs.getString("V_Q1_FA" + String.valueOf(nIndex)));
				if (StringUtil.isNotEmpty(q1Fa)) {
					q1Buff[faIndex[nIndex - 1]] = 1;
					questionInfo.V_Q1_other = q1Fa;
					break;
				}
			}
			// 業種区分
			for (int nIndex = 1; nIndex <= 34; nIndex++) {
				if (1 == q1Buff[nIndex]) {
					questionInfo.V_Q1_kubun = String.valueOf(nIndex);
					break; // 前方一致
				}
			}

			questionInfo.V_Q1_other = StringUtil.concatWithDelimit(" ",
					rs.getString("V_Q1_FA1"), rs.getString("V_Q1_FA2"),
					rs.getString("V_Q1_FA3"), rs.getString("V_Q1_FA4"),
					rs.getString("V_Q1_FA5")); // 業種のその他

			// 職種区分
			String q3Kubun = rs.getString("V_Q3_KUBUN");
			questionInfo.V_Q3_other = rs.getString("V_Q3_FA"); // 職種(その他FA)
			int q3Buff[];
			if (!GeneralPurposeEnqueteMiningUtil.validateForMaxConstraint(
					q3Kubun, 6, " ")) {
				System.out.println("職種 バーコード:" + cardInfo.V_VID + "\t 画像パス:"
						+ rs.getString("IMAGE_PATH") + "\n");
				q3Buff = EnqueteUtil.arrayInit(7);
			} else {
				q3Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q3Kubun, 6);

			}
			if (StringUtil.isNotEmpty(questionInfo.V_Q3_other)) {
				q3Buff[6] = 1;
			}
			for (int nIndex = 1; nIndex <= 6; nIndex++) {
				if (1 == q3Buff[nIndex]) {
					questionInfo.V_Q3_kubun = String.valueOf(nIndex);
					break; // 前方一致
				}
			}

			// 役職区分
			String q4Kubun = rs.getString("V_Q4_KUBUN");
			questionInfo.V_Q4_other = rs.getString("V_Q4_FA"); // 役職(その他FA)
			int q4Buff[];
			if (!GeneralPurposeEnqueteMiningUtil.validateForMaxConstraint(
					q4Kubun, 4, " ")) {
				System.out.println("役職 バーコード:" + cardInfo.V_VID + "\t 画像パス:"
						+ rs.getString("IMAGE_PATH") + "\n");
				q4Buff = EnqueteUtil.arrayInit(5);
			} else {
				q4Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q4Kubun, 4);
			}
			if (StringUtil.isNotEmpty(questionInfo.V_Q4_other)) {
				q3Buff[4] = 1;
			}
			for (int nIndex = 1; nIndex <= 4; nIndex++) {
				if (1 == q4Buff[nIndex]) {
					questionInfo.V_Q4_kubun = String.valueOf(nIndex);
					break; // 前方一致
				}
			}

			questionInfo.V_Q5 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q5")); // 従業員数
			questionInfo.V_Q6 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q6")); // 買付け決定権
			// 来場動機
			String q7 = rs.getString("V_Q7");
			questionInfo.V_Q7_other = rs.getString("V_Q7_FA"); // 来場動機(その他FA)
			int q7Buff[];
			if (!GeneralPurposeEnqueteMiningUtil.validateForMaxConstraint(q7,
					5, " ")) {
				System.out.println("来場動機 バーコード:" + cardInfo.V_VID + "\t 画像パス:"
						+ rs.getString("IMAGE_PATH") + "\n");
				q7Buff = EnqueteUtil.arrayInit(6);
			} else {
				q7Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q7, 5);
			}
			if (StringUtil.isNotEmpty(questionInfo.V_Q7_other)) {
				q7Buff[5] = 1;
			}
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				if (1 == q7Buff[nIndex]) {
					questionInfo.V_Q7 = String.valueOf(nIndex);
					break; // 前方一致
				}
			}

			questionInfo.V_Q8 = rs.getString("V_Q8"); // 出展検討
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス

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
	public static List<MesagoUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM preentry;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO
			MesagoQuestionDto questionInfo = new MesagoQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */

			// バーコード番号
			userdata.id = rs.getString("V_NO"); // バーコード番号

			/* 名刺情報 */
			cardInfo.V_CID = rs.getString("V_NO"); // 発券番号
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職
			cardInfo.V_TITLE = rs.getString("V_PREFIX"); // 敬称
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // Email
			cardInfo.V_URL = rs.getString("V_WEB"); // Web
			cardInfo.SEND_FLG = rs.getString("V_SEND"); // 送付先
			cardInfo.V_COUNTRY = rs.getString("V_COUNTRY"); // 国名
			if (!"Japan".equals(cardInfo.V_COUNTRY)) {
				cardInfo.V_OVERSEA = "1";
			}
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 住所2
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 住所3
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // ビル名

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			/* アンケート情報 */
			questionInfo.V_Q1_kubun = rs.getString("V_Q1_KUBUN"); // 業種区分
			questionInfo.V_Q1_other = rs.getString("V_Q1_FA"); // 業種区分(その他FA)
			questionInfo.V_Q1_code = rs.getString("V_Q1_CODE"); // 業種区分(コード)
			questionInfo.V_Q2 = rs.getString("V_Q2"); // 専門分野
			questionInfo.V_Q2_code = rs.getString("V_Q2_CODE"); // 専門分野(コード)
			questionInfo.V_Q3_kubun = rs.getString("V_Q3_KUBUN"); // 職種区分
			questionInfo.V_Q3_other = rs.getString("V_Q3_FA"); // 職種(その他FA)
			questionInfo.V_Q3_code = rs.getString("V_Q3_CODE"); // 職種(コード)
			questionInfo.V_Q4_kubun = rs.getString("V_Q4_KUBUN"); // 役職区分
			questionInfo.V_Q4_other = rs.getString("V_Q4_FA"); // 役職(その他FA)
			questionInfo.V_Q4_code = rs.getString("V_Q4_CODE"); // 役職(コード)
			questionInfo.V_Q5 = rs.getString("V_Q5"); // 従業員数
			questionInfo.V_Q6 = rs.getString("V_Q6"); // 買付け決定権
			questionInfo.V_Q7 = rs.getString("V_Q7"); // 来場動機
			questionInfo.V_Q7_other = rs.getString("V_Q7_FA"); // 来場動機(その他FA)
			questionInfo.V_Q8 = rs.getString("V_Q8"); // 出展検討
			cardInfo.REGIST_DATE = rs.getString("V_DATE"); // 登録日時

			/* アンケート情報 */

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
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
			HttpServletResponse response, Connection conn,
			String outputFileName, List<MesagoUserDataDto> userDataList,
			String dim, boolean isPreMaster, boolean isAppMaster)
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

		// 会社名、氏名
		header.add(StringUtil.enquote("リーダー番号"));
		header.add(StringUtil.enquote("バーコード番号"));
		header.add(StringUtil.enquote("来場日"));
		header.add(StringUtil.enquote("原票状況不備"));

		if (MesagoConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}

		header.add(StringUtil.enquote("海外フラグ"));

		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("住所1(都道府県)"));
		header.add(StringUtil.enquote("住所2(市区町村番地)"));
		header.add(StringUtil.enquote("住所3(ビル名)"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));
		header.add(StringUtil.enquote("Emailアドレス"));
		header.add(StringUtil.enquote("業種"));
		header.add(StringUtil.enquote("業種その他"));
		header.add(StringUtil.enquote("職種"));
		header.add(StringUtil.enquote("職種その他"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("役職その他"));
		header.add(StringUtil.enquote("従業員数"));
		header.add(StringUtil.enquote("商品買付決定権"));
		header.add(StringUtil.enquote("来場動機"));
		header.add(StringUtil.enquote("来場動機その他"));
		header.add(StringUtil.enquote("リクエストコード"));

		FileUtil.writer(header, writer, dim);
		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する

		// SLXコード
		Map<String, MesagoSLXDto> slxMap = MesagoSLXCodeUtil.getSLXMap(conn,
				MesagoConfig.EXHIBITION_NAME);
		for (MesagoUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// リーダー番号
			cols.add(StringUtil.enquote(userdata.reader));
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			// 来場日時
			if (StringUtil.isNotEmpty(userdata.timeByRfid)) {
				cols.add(StringUtil
						.enquote(normalizedDateStrForExhibitorTimestamp(userdata.timeByRfid)));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// 原票状況不備フラグ
			String lackFlg = getLackFlg(userdata);
			// 原票状況不備
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (MesagoConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}
			// 海外フラグ
			// cols.add(StringUtil.enquote(userdata.cardInfo.V_OVERSEA));
			cols.add(StringUtil.enquote(MesagoUtil.isOversea(userdata) ? "1"
					: ""));
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
			String name = StringUtil.concat(userdata.cardInfo.V_NAME1,
					userdata.cardInfo.V_NAME2);
			cols.add(StringUtil.enquote(name));

			// 郵便番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			// 住所1(都道府県)
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
			// 住所2(市区町村番地)
			String add2 = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3);
			cols.add(StringUtil.enquote(add2));
			// 住所3(ビル名)
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR4));

			// 電話番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TEL));
			// Fax
			cols.add(StringUtil.enquote(userdata.cardInfo.V_FAX));
			// Emailアドレス
			cols.add(StringUtil.enquote(userdata.cardInfo.V_EMAIL));

			// アンケート項目
			if (userdata.preentry) {
				outputExhibitorEnqueteDataForPreentry(cols, userdata);

			} else {
				outExhibitorputEnqueteDataForAppointedday(cols, userdata,
						slxMap);
			}
			// リクエストコード
			List<String> requestCodes = userdata.requestCode;
			StringBuffer sb = new StringBuffer();
			for (int nIndex = 0; nIndex < requestCodes.size(); nIndex++) {
				sb.append(getRequestValue(userdata.reader,
						requestCodes.get(nIndex)));
				if (nIndex != requestCodes.size() - 1) {
					sb.append(",");
				}
			}
			cols.add(StringUtil.isNotEmpty(sb.toString()) ? StringUtil
					.enquote(sb.toString()) : StringUtil.enquote(""));

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
			List<MesagoUserDataDto> userDataList, String dim)
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
		for (MesagoUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			// 来場日
			String time = normalizedDateStrForExhibitorTimestamp(userdata.timeByRfid);
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
			if (READER__DAN.contains(reader)) {
				value = REQUESTCODES_DAN.get(code);
			} else if (READER_ESTHE.contains(reader)) {
				value = REQUESTCODES_ESTHE.get(code);
			} else if (READER_SUNATURALS.contains(reader)) {
				value = REQUESTCODES_SUNATURALS.get(code);
			} else if (READER_MOGA.contains(reader)) {
				value = REQUESTCODES_MOGA.get(code);
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
	 *            <b>MesagoUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, MesagoUserDataDto> getAppointedDayMap(
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
	 * 【バーコードマッチング高速化対応】事前登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>MesagoUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, MesagoUserDataDto> getPreEntryMap(
			List<MesagoUserDataDto> userDataList) {
		Map<String, MesagoUserDataDto> map = new HashMap<String, MesagoUserDataDto>();
		for (MesagoUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.cardInfo.V_CID)) {
				map.put(userData.cardInfo.V_CID, userData);
			}
		}
		return map;
	}

	/**
	 * バーコードデータより来場日を特定する
	 *
	 * @param timestamp
	 *            バーコードデータ中のタイムスタンプ
	 * @return 来場日
	 */
	public static String normalizedDateStrForExhibitorTimestamp(String timestamp) {
		if (StringUtil.isNotEmpty(timestamp) && timestamp.length() == 8) {
			StringBuffer sb = new StringBuffer();
			sb.append(timestamp.substring(4, 8));
			return sb.toString();
		}
		return null;
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
	private static List<String> outputExhibitorEnqueteDataForPreentry(
			List<String> cols, MesagoUserDataDto userdata) {

		// 業種
		String biz = getPreentryBiz(
				((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun,
				userdata.questionInfo.V_Q2);
		if (StringUtil.isNotEmpty(biz)) {
			biz = StringUtil.convertFullWidthKatakana(biz);
		}
		cols.add(StringUtil.enquote(biz));
		// 業種その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1_other));
		// 職種
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q3_kubun));
		// 職種その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3_other));
		// 役職
		cols.add(StringUtil
				.enquote(((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun));
		// 役職その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q4_other));
		// 従業員数区分
		String q5Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q5;
		MesagoSLXDto accOfStaffInfo = MesagoSLXCodeUtil.accOfStaff.get(q5Num);

		cols.add(StringUtil
				.enquote(accOfStaffInfo != null ? accOfStaffInfo.accOfStaff
						: ""));
		// 商品買付け決定権
		String q6 = ((MesagoQuestionDto) userdata.questionInfo).V_Q6;
		if (StringUtil.isNotEmpty(q6)) {
			q6 = q6.substring(1, q6.length() - 1);
		}
		cols.add(StringUtil.enquote(MesagoConstants.DECIDE_CATEGORIES.get(q6)));

		// 出展動機
		String q7 = ((MesagoQuestionDto) userdata.questionInfo).V_Q7;
		if (StringUtil.isNotEmpty(q7)) {
			q7 = q7.substring(1, q7.length() - 1);
		}
		cols.add(StringUtil.enquote(MesagoConstants.PURPOSE_CATEGORIES.get(q7)));
		// 出展動機その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7_other));
		return cols;
	}

	private static String getPreentryBiz(String q1, String q2) {
		if (StringUtil.isNotEmpty(q1)) {
			List<Map.Entry<String, Integer>> bizTypeMapEntryList = new ArrayList<Map.Entry<String, Integer>>(
					MesagoConstants.PREENTRY_BIZ_TYPE_MAP.entrySet());
			int matchId = 0;
			String category1 = null;
			for (Entry<String, Integer> bizTypeEntry : bizTypeMapEntryList) {
				if (StringUtil.find(q1, bizTypeEntry.getKey())) {
					category1 = bizTypeEntry.getKey();
					matchId = bizTypeEntry.getValue();
					break;
				}
			}
			String[] categories = MesagoConstants.PREENTRY_BIZ_CATEGORIES_LIST
					.get(matchId);
			if (0 == matchId || StringUtil.isNotEmpty(q2)) {
				for (String category2 : categories) {
					String value = matchId == 0 ? q1 : q2;
					if (StringUtil.find(value, category2)) {
						return StringUtil.concatWithDelimit("-", category1,
								category2);
					}
				}
			} else {
				return category1;
			}

		}
		return null;
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
	private static List<String> outExhibitorputEnqueteDataForAppointedday(
			List<String> cols, MesagoUserDataDto userdata,
			Map<String, MesagoSLXDto> slxMaster) {

		// 業種
		cols.add(StringUtil.enquote(MesagoConstants.BIZ_CATEGORIES
				.get(((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun)));

		// 業種その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1_other));

		// 職種
		String q3Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q3_kubun;
		MesagoSLXDto bizInfo = MesagoSLXCodeUtil.contactPostType.get(q3Num);
		cols.add(StringUtil.enquote(bizInfo != null ? bizInfo.contactPostType
				: ""));
		// 職種その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3_other));
		// 役職区分
		String q4Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun;
		MesagoSLXDto positionInfo = MesagoSLXCodeUtil.contactPostGroup
				.get(q4Num);

		cols.add(StringUtil
				.enquote(positionInfo != null ? positionInfo.contactPostGroup
						: ""));
		// 役職その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q4_other));
		// 従業員数区分
		String q5Num = ((MesagoQuestionDto) userdata.questionInfo).V_Q5;
		MesagoSLXDto accOfStaffInfo = MesagoSLXCodeUtil.accOfStaff.get(q5Num);

		cols.add(StringUtil
				.enquote(accOfStaffInfo != null ? accOfStaffInfo.accOfStaff
						: ""));
		// 商品買付け決定権
		cols.add(StringUtil.enquote(MesagoConstants.DECIDE_CATEGORIES
				.get(userdata.questionInfo.V_Q6)));
		// 出展動機
		cols.add(StringUtil.enquote(MesagoConstants.PURPOSE_CATEGORIES
				.get(((MesagoQuestionDto) userdata.questionInfo).V_Q7)));
		// 出展動機その他
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7_other));

		return cols;
	}

	/**
	 * 不備フラグの特定
	 *
	 * @param userdata
	 *            　<b>MesagoUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(MesagoUserDataDto userdata) {
		MesagoValidator validator = new MesagoValidator("■");
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

	@Override
	public boolean isPreEntry(UserDataDto userdata) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isAppEntry(UserDataDto userdata) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}