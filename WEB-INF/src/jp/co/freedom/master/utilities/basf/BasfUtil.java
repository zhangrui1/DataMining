package jp.co.freedom.master.utilities.basf;

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
import jp.co.freedom.master.dto.basf.BasfCardDto;
import jp.co.freedom.master.dto.basf.BasfDetailEnqueteDto;
import jp.co.freedom.master.dto.basf.BasfQuestionDto;
import jp.co.freedom.master.dto.basf.BasfUserDataDto;

/**
 * Basf向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class BasfUtil {

	private static Map<String, String> q2_1ValueMap = createQ2_1ValueMap();
	private static Map<String, String> q2_4ValueMap = createQ2_4ValueMap();
	private static Map<String, String> q2OtherValueMap = createQ2OtherValueMap();

	private static Map<String, String> createQ2_1ValueMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "適切");
		map.put("2", "もっと長くてもよい");
		map.put("3", "もっと短くてもよい");
		return map;
	}

	private static Map<String, String> createQ2OtherValueMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "大変良い");
		map.put("2", "良い");
		map.put("3", "あまりよくない");
		map.put("4", "よくない");
		return map;
	}

	private static Map<String, String> createQ2_4ValueMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "満足");
		map.put("2", "やや満足");
		map.put("3", "あまりよくない");
		map.put("4", "よくない");
		return map;
	}

	/**
	 * マスターデータの取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　マスターデータ
	 * @throws SQLException
	 */
	public static List<BasfUserDataDto> getAllData(Connection conn)
			throws SQLException {
		List<BasfUserDataDto> userDataList = new ArrayList<BasfUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM master;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			BasfUserDataDto userdata = new BasfUserDataDto();
			BasfCardDto cardInfo = new BasfCardDto(); // 名刺情報DTO
			BasfQuestionDto questionInfo = new BasfQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
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
			questionInfo.Q1_2_FA = rs.getString("Q1_2_FA");
			questionInfo.Q2_1 = rs.getString("Q2_1");
			questionInfo.Q2_2 = rs.getString("Q2_2");
			questionInfo.Q2_3 = rs.getString("Q2_3");
			questionInfo.Q2_4 = rs.getString("Q2_4");
			questionInfo.Q2_FA = rs.getString("Q2_FA");
			questionInfo.Q3_1 = rs.getString("Q3_1");
			questionInfo.Q3_2 = rs.getString("Q3_2");
			questionInfo.Q3_3 = rs.getString("Q3_3");
			questionInfo.Q3_4 = rs.getString("Q3_4");
			questionInfo.Q3_5 = rs.getString("Q3_5");
			questionInfo.Q3_6 = rs.getString("Q3_6");
			questionInfo.Q3_FA = rs.getString("Q3_FA");
			questionInfo.Q4 = rs.getString("Q4");
			questionInfo.Q5 = rs.getString("Q5");
			// 詳細アンケート情報
			List<BasfDetailEnqueteDto> detail = new ArrayList<BasfDetailEnqueteDto>();
			for (int nIndex = 1; nIndex <= BasfConfig.DETAIL_ENQUETE_MAXNUM; nIndex++) {
				BasfDetailEnqueteDto enqueteInfo = new BasfDetailEnqueteDto();
				enqueteInfo.id = rs
						.getString("V_E_ID" + String.valueOf(nIndex));
				enqueteInfo.check = rs.getString("V_E_O"
						+ String.valueOf(nIndex));
				enqueteInfo.comment = rs.getString("V_E_C"
						+ String.valueOf(nIndex));
				detail.add(enqueteInfo);
			}
			questionInfo.detail = detail;
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
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<BasfUserDataDto> userDataList, String dim)
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

		header.add(StringUtil.enquote("海外住所フラグ"));
		header.add(StringUtil.enquote("原票状況不備"));
		if (BasfConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}
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
		/* Q1-1. BASFについてご存知でしたか？ */
		header.add(StringUtil.enquote("BASFについてご存知でしたか？-はい"));
		header.add(StringUtil.enquote("BASFについてご存知でしたか？-いいえ"));
		/* Q1-2 */
		header.add(StringUtil.enquote("1 総合化学会社"));
		header.add(StringUtil.enquote("2 混和剤メーカー"));
		header.add(StringUtil.enquote("3 建材メーカー"));
		header.add(StringUtil.enquote("4 原料・素材メーカー"));
		header.add(StringUtil.enquote("5 あまり良く知らない"));
		header.add(StringUtil.enquote("6 革新的な製品"));
		header.add(StringUtil.enquote("7 持続可能性のある商品"));
		header.add(StringUtil.enquote("8 高品質の製品"));
		header.add(StringUtil.enquote("9 幅広い製品ポートフォリオ"));
		header.add(StringUtil.enquote("10 ソリューション・プロバイダー"));
		header.add(StringUtil.enquote("11ベストパートナー"));
		header.add(StringUtil.enquote("12 その他"));
		header.add(StringUtil.enquote("12 その他FA"));
		/* Q2 */
		header.add(StringUtil.enquote("開催時間"));
		header.add(StringUtil.enquote("パネル内容"));
		header.add(StringUtil.enquote("スタッフの説明"));
		header.add(StringUtil.enquote("全体として"));
		/* Q2 FA */
		header.add(StringUtil.enquote("お気づきの点"));
		/* Q3 */
		header.add(StringUtil.enquote("混和剤 『スマートダイナミックコンクリート、マスターグレニウム6500』"));
		header.add(StringUtil.enquote("早強材 『マスターエックスシード』"));
		header.add(StringUtil.enquote("含漫材 『プロテクトシルCIT』"));
		header.add(StringUtil.enquote("コーティング材 『Polyceram P No.6800SK』"));
		header.add(StringUtil.enquote("断熱材 『パソテクト』"));
		header.add(StringUtil.enquote("生分解性樹脂 『ecovio』"));
		/* Q3 FA */
		header.add(StringUtil.enquote("お気づきの点"));
		/* Q4 */
		header.add(StringUtil.enquote("情報提供-はい"));
		header.add(StringUtil.enquote("情報提供-いいえ"));
		/* Q5 */
		header.add(StringUtil.enquote("要望"));

		/* 詳細アンケート */
		for (int nIndex = 1; nIndex <= 36; nIndex++) {
			header.add(StringUtil.enquote("関心" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("コメント" + String.valueOf(nIndex)));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (BasfUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			String lackFlg = getLackFlg(userdata);// 原票状況不備フラグ
			// 海外住所フラグ
			cols.add(isOversea(userdata) && StringUtil.isEmpty(lackFlg) ? StringUtil
					.enquote("1") : StringUtil.enquote(""));
			// 原票状況不備
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (BasfConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}

			// 氏名／会社情報項目
			outputNameAndCompanyData(cols, userdata);

			// 住所項目
			outputAddressData(cols, userdata);

			// アンケート項目
			outputEnqueteData(cols, userdata);

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
	 * 氏名／会社情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>BasfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			BasfUserDataDto userdata) {
		// 会社名
		cols.add(userdata.cardInfo != null
				&& StringUtil.isNotEmpty(userdata.cardInfo.V_CORP) ? StringUtil
				.enquote(userdata.cardInfo.V_CORP) : StringUtil.enquote(""));
		// 部署
		String dept = userdata.cardInfo != null ? StringUtil.concat(
				userdata.cardInfo.V_DEPT1, userdata.cardInfo.V_DEPT2,
				userdata.cardInfo.V_DEPT3, userdata.cardInfo.V_DEPT4) : "";
		cols.add(StringUtil.isNotEmpty(dept) ? StringUtil.enquote(dept)
				: StringUtil.enquote(""));
		// 役職
		String biz = userdata.cardInfo != null ? StringUtil.concat(
				userdata.cardInfo.V_BIZ1, userdata.cardInfo.V_BIZ2,
				userdata.cardInfo.V_BIZ3, userdata.cardInfo.V_BIZ4) : "";
		cols.add(StringUtil.isNotEmpty(biz) ? StringUtil.enquote(biz)
				: StringUtil.enquote(""));
		// 氏名
		String name = userdata.cardInfo != null ? StringUtil.concatWithDelimit(
				"　", userdata.cardInfo.V_NAME1, userdata.cardInfo.V_NAME2) : "";
		cols.add(StringUtil.isNotEmpty(name) ? StringUtil.enquote(name)
				: StringUtil.enquote(""));
		return cols;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>BasfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			BasfUserDataDto userdata) {
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
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ZIP) ? StringUtil
					.enquote(userdata.cardInfo.V_ZIP) : StringUtil.enquote(""));
			// 都道府県
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1) ? StringUtil
					.enquote(userdata.cardInfo.V_ADDR1) : StringUtil
					.enquote(""));
			// 住所(市区郡+町域+番号他)
			String addr2 = userdata.cardInfo != null ? StringUtil.concat(
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4) : "";
			cols.add(StringUtil.isNotEmpty(addr2) ? StringUtil.enquote(addr2)
					: StringUtil.enquote(""));
			// ビル名
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR5) ? StringUtil
					.enquote(userdata.cardInfo.V_ADDR5) : StringUtil
					.enquote(""));
			// メールアドレス (■が含まれている場合は空値に置換)
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.V_EMAIL) : StringUtil
					.enquote(""));
			// 電話番号 (■が含まれている場合は空値に置換)
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_TEL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_TEL) ? StringUtil
					.enquote(userdata.cardInfo.V_TEL) : StringUtil.enquote(""));
			// FAX番号 (■が含まれている場合は空値に置換)
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_FAX)
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
	 *            <b>BasfUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			BasfUserDataDto userdata) {
		BasfQuestionDto questionInfo = (BasfQuestionDto) userdata.questionInfo;

		// Q1-1
		if (StringUtil.isNotEmpty(questionInfo.Q1_1)) {
			String[] q1_1 = questionInfo.Q1_1.split(" ");
			for (int nIndex = 1; nIndex <= 2; nIndex++) {
				cols.add(StringUtil.contains(q1_1, String.valueOf(nIndex)) ? StringUtil
						.enquote("1") : StringUtil.enquote(""));
			}
		} else {
			for (int nIndex = 1; nIndex <= 2; nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}

		// Q1-2
		if (StringUtil.isNotEmpty(questionInfo.Q1_2)) {
			String[] q1_2 = questionInfo.Q1_2.split(" ");
			for (int nIndex = 1; nIndex <= 12; nIndex++) {
				cols.add(StringUtil.contains(q1_2, String.valueOf(nIndex)) ? StringUtil
						.enquote("1") : StringUtil.enquote(""));
			}
		} else {
			for (int nIndex = 1; nIndex <= 12; nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}

		// Q1-2 FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q1_2_FA) ? StringUtil
				.enquote(questionInfo.Q1_2_FA) : StringUtil.enquote(""));

		// Q2-1
		if (StringUtil.isNotEmpty(questionInfo.Q2_1)) {
			String q2_1 = q2_1ValueMap.get(questionInfo.Q2_1);
			cols.add(StringUtil.isNotEmpty(q2_1) ? StringUtil.enquote(q2_1)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q2-2
		if (StringUtil.isNotEmpty(questionInfo.Q2_2)) {
			String q2_2 = q2OtherValueMap.get(questionInfo.Q2_2);
			cols.add(StringUtil.isNotEmpty(q2_2) ? StringUtil.enquote(q2_2)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q2-3
		if (StringUtil.isNotEmpty(questionInfo.Q2_3)) {
			String q2_3 = q2OtherValueMap.get(questionInfo.Q2_3);
			cols.add(StringUtil.isNotEmpty(q2_3) ? StringUtil.enquote(q2_3)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q2-4
		if (StringUtil.isNotEmpty(questionInfo.Q2_4)) {
			String q2_4 = q2_4ValueMap.get(questionInfo.Q2_4);
			cols.add(StringUtil.isNotEmpty(q2_4) ? StringUtil.enquote(q2_4)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q2 FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q2_FA) ? StringUtil
				.enquote(questionInfo.Q2_FA) : StringUtil.enquote(""));

		// Q3-1
		if (StringUtil.isNotEmpty(questionInfo.Q3_1)) {
			String q3_1 = q2OtherValueMap.get(questionInfo.Q3_1);
			cols.add(StringUtil.isNotEmpty(q3_1) ? StringUtil.enquote(q3_1)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3-2
		if (StringUtil.isNotEmpty(questionInfo.Q3_2)) {
			String q3_2 = q2OtherValueMap.get(questionInfo.Q3_2);
			cols.add(StringUtil.isNotEmpty(q3_2) ? StringUtil.enquote(q3_2)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3-3
		if (StringUtil.isNotEmpty(questionInfo.Q3_3)) {
			String q3_3 = q2OtherValueMap.get(questionInfo.Q3_3);
			cols.add(StringUtil.isNotEmpty(q3_3) ? StringUtil.enquote(q3_3)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3-4
		if (StringUtil.isNotEmpty(questionInfo.Q3_4)) {
			String q3_4 = q2OtherValueMap.get(questionInfo.Q3_4);
			cols.add(StringUtil.isNotEmpty(q3_4) ? StringUtil.enquote(q3_4)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3-5
		if (StringUtil.isNotEmpty(questionInfo.Q3_5)) {
			String q3_5 = q2OtherValueMap.get(questionInfo.Q3_5);
			cols.add(StringUtil.isNotEmpty(q3_5) ? StringUtil.enquote(q3_5)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3-6
		if (StringUtil.isNotEmpty(questionInfo.Q3_6)) {
			String q3_6 = q2OtherValueMap.get(questionInfo.Q3_6);
			cols.add(StringUtil.isNotEmpty(q3_6) ? StringUtil.enquote(q3_6)
					: StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// Q3 FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q3_FA) ? StringUtil
				.enquote(questionInfo.Q3_FA) : StringUtil.enquote(""));

		// Q4
		if (StringUtil.isNotEmpty(questionInfo.Q4)) {
			String[] q4 = questionInfo.Q4.split(" ");
			for (int nIndex = 1; nIndex <= 2; nIndex++) {
				cols.add(StringUtil.contains(q4, String.valueOf(nIndex)) ? StringUtil
						.enquote("1") : StringUtil.enquote(""));
			}
		} else {
			for (int nIndex = 1; nIndex <= 2; nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}

		// Q5
		cols.add(StringUtil.isNotEmpty(questionInfo.Q5) ? StringUtil
				.enquote(questionInfo.Q5) : StringUtil.enquote(""));

		// 詳細アンケート
		Map<Integer, BasfDetailEnqueteDto> enqueteMap = new HashMap<Integer, BasfDetailEnqueteDto>();
		for (BasfDetailEnqueteDto enqueteInfo : questionInfo.detail) {
			if (StringUtil.isNotEmpty(enqueteInfo.id)) {
				enqueteMap.put(Integer.parseInt(enqueteInfo.id), enqueteInfo);
			}
		}
		for (int nIndex = 1; nIndex <= 36; nIndex++) {
			if (enqueteMap.containsKey(nIndex)) {
				BasfDetailEnqueteDto detail = enqueteMap.get(nIndex);
				// 関心
				String interestValue = null;
				String interest = detail.check;
				if (StringUtil.isNotEmpty(interest)) {
					String[] buff = interest.split(" ");
					List<String> list = new ArrayList<String>();
					if (StringUtil.contains(buff, "1")) {
						list.add("打ち合わせ希望");
					}
					if (StringUtil.contains(buff, "2")) {
						list.add("サンプル希望");
					}
					if (StringUtil.contains(buff, "3")) {
						list.add("興味あり");
					}
					interestValue = StringUtil.concatWithDelimit(",", list);
				}
				cols.add(StringUtil.isNotEmpty(interestValue) ? StringUtil
						.enquote(interestValue) : StringUtil.enquote(""));
				// コメント
				cols.add(StringUtil.isNotEmpty(detail.comment) ? StringUtil
						.enquote(detail.comment) : StringUtil.enquote(""));
			} else {
				cols.add(StringUtil.enquote(""));
				cols.add(StringUtil.enquote(""));
			}
		}
		return cols;
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>BasfUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(BasfUserDataDto userdata) {
		BasfValidator validator = new BasfValidator();
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
	 *            <b>BasfCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(BasfUserDataDto userdata) {
		if (userdata.cardInfo != null) {
			boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
			boolean check2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
			boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
			boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
			return check1 && check2 && check3 && check4 && check5;
		}
		return false;
	}

}