package jp.co.freedom.master.utilities.emb;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.emb.EmbCardDto;
import jp.co.freedom.master.dto.emb.EmbQ2Dto;
import jp.co.freedom.master.dto.emb.EmbQuestionDto;
import jp.co.freedom.master.dto.emb.EmbUserDataDto;

public class EmbUtil {

	/**
	 * マスターデータの取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　マスターデータ
	 * @throws SQLException
	 */
	public static List<EmbUserDataDto> getAllMasterData(Connection conn)
			throws SQLException {
		// マスターデータ取得
		List<EmbUserDataDto> userDataList = new ArrayList<EmbUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * from master;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			EmbUserDataDto userdata = new EmbUserDataDto();
			EmbCardDto cardInfo = new EmbCardDto(); // 名刺情報いDTO
			EmbQuestionDto questionInfo = new EmbQuestionDto();// アンケート情報DTO

			/* 名刺情報 */
			cardInfo.V_NAME1 = rs.getString("V_NAME1");
			cardInfo.V_NAME2 = rs.getString("V_NAME2");
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
			cardInfo.V_IMAGE_PATH = rs.getString("ImagePath"); // イメージパス

			/* アンケート情報 */
			questionInfo.Q1 = rs.getString("Q1");
			for (int nIndex = 1; nIndex <= 7; nIndex++) {
				EmbQ2Dto q2Info = new EmbQ2Dto();
				String key = "Q2_" + String.valueOf(nIndex);
				String makerId = rs.getString(key);
				String personId = rs.getString(key + "_NAME");
				String personFa = null;
				if (nIndex != 6 && nIndex != 7) {
					personFa = rs.getString(key + "_NAME_FA");
				}
				if (StringUtil.isNotEmpty(makerId)
						|| StringUtil.isNotEmpty(personId)
						|| StringUtil.isNotEmpty(personFa)) {
					q2Info.makerId = nIndex;
					q2Info.personId = personId;
					q2Info.personFa = personFa;
					questionInfo.q2List.add(q2Info);
				}
			}
			questionInfo.Q3 = rs.getString("Q3");
			questionInfo.Q4 = rs.getString("Q4");
			questionInfo.Q5 = rs.getString("Q5");

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;

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
	 * @throws IOException
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<EmbUserDataDto> userDataList, String dim, boolean b,
			boolean c, boolean d) throws IOException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		writer.print('\uFEFF'); // エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力

		// ヘッダ情報の書き出し
		List<String> header = new ArrayList<String>();
		header.add(StringUtil.enquote("海外住所フラグ"));
		header.add(StringUtil.enquote("原票状況不備"));
		if (EmbConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}

		// アンケート項目
		/** Q1対応者名 */
		header.add(StringUtil.enquote("Q1ヒアリングシートNo."));
		header.add(StringUtil.enquote("Q2対応者：会社名(選択)"));
		header.add(StringUtil.enquote("Q2対応者：名(記入)"));
		/** Q2製品名 */
		header.add(StringUtil.enquote("Q3_1 Armadillo-810"));
		header.add(StringUtil.enquote("Q3_2 Armadillo-840"));
		header.add(StringUtil.enquote("Q3_3 Armadillo-800 EVA"));
		header.add(StringUtil.enquote("Q3_4 Armadillo-WLAN"));
		header.add(StringUtil.enquote("Q3_5 Armadillo-410"));
		header.add(StringUtil.enquote("Q3_6 Armadillo-420"));
		header.add(StringUtil.enquote("Q3_7 Armadillo-440"));
		header.add(StringUtil.enquote("Q3_8 Armadillo-460"));
		header.add(StringUtil.enquote("Q3_9 その他"));
		/** Q3案件 */
		header.add(StringUtil.enquote("Q4 進行中の商談"));
		/** Q4案件詳細 */
		header.add(StringUtil.enquote("Q5_1 カメラ "));
		header.add(StringUtil.enquote("Q5_2 画像処理 "));
		header.add(StringUtil.enquote("Q5_3 クラウド "));
		header.add(StringUtil.enquote("Q5_4 動画再生 "));
		header.add(StringUtil.enquote("Q5_5 Qt "));
		header.add(StringUtil.enquote("Q5_6 Java "));
		header.add(StringUtil.enquote("Q5_7 OS-9 "));

		header.add(StringUtil.enquote("氏"));
		header.add(StringUtil.enquote("名"));
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("市町区村"));
		header.add(StringUtil.enquote("番地・ビル名"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));
		header.add(StringUtil.enquote("電子メールアドレス"));

		FileUtil.writer(header, writer, dim);

		for (EmbUserDataDto userdata : userDataList) {
			List<String> cols = new ArrayList<String>();
			String lackFlg = getLackFlg(userdata);// 原票状況不備フラグ

			// 海外住所フラグ
			cols.add(isOversea(userdata) && StringUtil.isEmpty(lackFlg) ? StringUtil
					.enquote("1") : StringUtil.enquote(""));
			// 原票情報不備
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (EmbConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}

			// アンケート項目
			outputEnqueteData(cols, userdata);

			// 氏名／会社情報項目
			outputNameAndCompanyData(cols, userdata);

			// 住所項目
			outputAddressData(cols, userdata);
			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}

		return true;
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>EmbUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */

	private static List<String> outputEnqueteData(List<String> cols,
			EmbUserDataDto userdata) {

		EmbQuestionDto questionInfo = (EmbQuestionDto) userdata.questionInfo;
		// Q_NO
		String q1 = questionInfo.Q1 != null ? "ET2013-" + questionInfo.Q1 : "";
		cols.add(StringUtil.isNotEmpty(q1) ? StringUtil.enquote(q1)
				: StringUtil.enquote(""));
		// Q2
		if (questionInfo.q2List.size() != 0) {
			EmbQ2Dto q2Info = questionInfo.q2List.get(0);
			cols.add(StringUtil.enquote(q2Info.getMakerName()));
			cols.add(StringUtil.enquote(q2Info.getPersonName()));
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		}
		// Q3
		if (StringUtil.isNotEmpty(questionInfo.Q3)) {
			int[] q3Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					questionInfo.Q3, 9);
			for (int nIndex = 1; nIndex <= 9; nIndex++) {
				cols.add(q3Buff[nIndex] == 1 ? StringUtil.enquote("○")
						: StringUtil.enquote(""));
			}
		} else {
			for (int nIndex = 1; nIndex <= 9; nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}
		// Q4
		cols.add(StringUtil.isNotEmpty(questionInfo.Q4) ? StringUtil
				.enquote("○") : StringUtil.enquote(""));
		// Q5
		if (StringUtil.isNotEmpty(questionInfo.Q5)) {
			int[] q5Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					questionInfo.Q5, 7);
			for (int nIndex = 1; nIndex <= 7; nIndex++) {
				cols.add(q5Buff[nIndex] == 1 ? StringUtil.enquote("○")
						: StringUtil.enquote(""));
			}
		} else {
			for (int nIndex = 1; nIndex <= 7; nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}

		return cols;

	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>EmbUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			EmbUserDataDto userdata) {
		if (userdata.cardInfo == null) { // 住所情報が存在しない場合
			// 郵便番号
			cols.add(StringUtil.enquote(""));
			// 都道府県
			cols.add(StringUtil.enquote(""));
			// 住所(市区郡+町域+番号他)
			cols.add(StringUtil.enquote(""));
			// ビル名
			cols.add(StringUtil.enquote(""));
			// 電話番号
			cols.add(StringUtil.enquote(""));
			// FAX番号
			cols.add(StringUtil.enquote(""));
			// メールアドレス
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
			// メールアドレス (■が含まれている場合は空値に置換)
			cols.add(userdata.cardInfo != null
					&& StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
					&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.V_EMAIL) : StringUtil
					.enquote(""));
		}
		return cols;
	}

	/**
	 * 氏名／会社情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>EmbUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			EmbUserDataDto userdata) {

		// 氏
		cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1) ? StringUtil
				.enquote(userdata.cardInfo.V_NAME1) : StringUtil.enquote(""));
		// 名
		cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2) ? StringUtil
				.enquote(userdata.cardInfo.V_NAME2) : StringUtil.enquote(""));

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

		return cols;
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>EmbUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(EmbUserDataDto userdata) {

		EmbValidator validator = new EmbValidator();
		validator.validate(userdata, "1");
		validator.validate(userdata, "2");
		boolean lack1Flg = !validator.getResult1();// 不備フラグ1
		boolean lack2Flg = !validator.getResult2();// 不備フラグ2
		userdata.validationErrResult = validator.getResultDetail();

		if (lack1Flg && !lack2Flg) {
			return "1";
		} else if (lack2Flg) {
			return "2";
		} else if (lack2Flg) {
			return "";
		}

		return null;
	}

	/**
	 * 海外住所フラグの検証
	 * 
	 * @param cardInfo
	 *            <b>EmbCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(EmbUserDataDto userdata) {
		if (userdata.cardInfo != null) {
			boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
			boolean check2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
			boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
			boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
			return check1 && check2 && check3 & check4 & check5;
		}
		return false;
	}

}
