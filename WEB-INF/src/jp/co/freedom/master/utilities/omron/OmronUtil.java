package jp.co.freedom.master.utilities.omron;

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

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.omron.OmronCardDto;
import jp.co.freedom.master.dto.omron.OmronQuestionDto;
import jp.co.freedom.master.dto.omron.OmronUserDataDto;

/**
 * Omron向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronUtil {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<OmronUserDataDto> createInstance(List<String[]> csvData) {
		List<OmronUserDataDto> userData = new ArrayList<OmronUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			OmronUserDataDto dataDto = new OmronUserDataDto();
			OmronCardDto cardDto = new OmronCardDto();
			dataDto.id = row[0];// スタッフコード
			cardDto.V_CORP = row[1]; // 会社名
			dataDto.cardInfo = cardDto;
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * DB中の全ての入力データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　DB中の全ての入力データ
	 * @throws SQLException
	 */
	public static List<OmronUserDataDto> getAllData(Connection conn)
			throws SQLException {
		List<OmronUserDataDto> userDataList = new ArrayList<OmronUserDataDto>();
		String sql = "SELECT * FROM staffenquete;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			OmronUserDataDto userdata = new OmronUserDataDto();
			OmronCardDto cardInfo = new OmronCardDto(); // 名刺情報DTO
			OmronQuestionDto questionInfo = new OmronQuestionDto(); // アンケート情報DTO
			/* 個人情報　 */
			cardInfo.staffCode = rs.getString("V_STAFFCODE"); // スタッフコード
			userdata.id = cardInfo.staffCode;
			cardInfo.startYear = rs.getString("V_STARTYEAR");
			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q1_FA = rs.getString("V_Q1_FA");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q2_1 = rs.getString("V_Q2_1");
			questionInfo.V_Q2_1_FA = rs.getString("V_Q2_1_FA");
			questionInfo.V_Q3 = rs.getString("V_Q3");
			questionInfo.V_Q3_FA = rs.getString("V_Q3_FA");
			questionInfo.V_Q4_1 = rs.getString("V_Q4_1");
			questionInfo.V_Q4_2 = rs.getString("V_Q4_2");

			questionInfo.V_Q5_CAP_NAME1 = rs.getString("V_Q5_CAP_NAME1");
			questionInfo.V_Q5_CAP_YEAR1 = rs.getString("V_Q5_CAP_YEAR1");
			questionInfo.V_Q5_CAP_NAME2 = rs.getString("V_Q5_CAP_NAME2");
			questionInfo.V_Q5_CAP_YEAR2 = rs.getString("V_Q5_CAP_YEAR2");
			questionInfo.V_Q5_CAP_NAME3 = rs.getString("V_Q5_CAP_NAME3");
			questionInfo.V_Q5_CAP_YEAR3 = rs.getString("V_Q5_CAP_YEAR3");

			questionInfo.V_Q5_TRA_NAME1 = rs.getString("V_Q5_TRA_NAME1");
			questionInfo.V_Q5_TRA_YEAR1 = rs.getString("V_Q5_TRA_YEAR1");
			questionInfo.V_Q5_TRA_OWNER1 = rs.getString("V_Q5_TRA_OWNER1");
			questionInfo.V_Q5_TRA_NAME2 = rs.getString("V_Q5_TRA_NAME2");
			questionInfo.V_Q5_TRA_YEAR2 = rs.getString("V_Q5_TRA_YEAR2");
			questionInfo.V_Q5_TRA_OWNER2 = rs.getString("V_Q5_TRA_OWNER2");
			questionInfo.V_Q5_TRA_NAME3 = rs.getString("V_Q5_TRA_NAME3");
			questionInfo.V_Q5_TRA_YEAR3 = rs.getString("V_Q5_TRA_YEAR3");
			questionInfo.V_Q5_TRA_OWNER3 = rs.getString("V_Q5_TRA_OWNER3");

			questionInfo.V_Q6 = rs.getString("V_Q6");
			questionInfo.V_Q7 = rs.getString("V_Q7");
			questionInfo.V_Q8_1 = rs.getString("V_Q8_1");
			questionInfo.V_Q8_2 = rs.getString("V_Q8_2");
			questionInfo.V_Q8_3 = rs.getString("V_Q8_3");
			questionInfo.V_Q9 = rs.getString("V_Q9");
			questionInfo.V_Q9_FA = rs.getString("V_Q9_FA");

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
			List<OmronUserDataDto> userDataList, String dim,
			boolean isPreMaster, boolean isAppMaster) throws ServletException,
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

		header.add(StringUtil.enquote("スタッフコード"));
		header.add(StringUtil.enquote("業務開始日"));
		header.add(StringUtil.enquote("個人情報-理解度"));

		if (OmronConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("問1"));
			header.add(StringUtil.enquote("問1-理由"));
			header.add(StringUtil.enquote("問2"));
			header.add(StringUtil.enquote("問2-理由"));
			header.add(StringUtil.enquote("問2-その他"));
			header.add(StringUtil.enquote("問3"));
			header.add(StringUtil.enquote("問3-理由"));
			header.add(StringUtil.enquote("問4-1"));
			header.add(StringUtil.enquote("問4-2"));
			header.add(StringUtil.enquote("問5-資格取得"));
			header.add(StringUtil.enquote("問5-受講研修"));
			header.add(StringUtil.enquote("問5-受講研修(エラー箇所)"));
			header.add(StringUtil.enquote("問6"));
			header.add(StringUtil.enquote("問7"));
			header.add(StringUtil.enquote("問8-1位"));
			header.add(StringUtil.enquote("問8-2位"));
			header.add(StringUtil.enquote("問8-3位"));
			header.add(StringUtil.enquote("問9"));
			header.add(StringUtil.enquote("問9-その他"));
		}
		header.add(StringUtil.enquote("区分"));
		header.add(StringUtil.enquote("略称"));
		header.add(StringUtil.enquote("会社名"));

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (OmronUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			// 業務開始日
			cols.add(StringUtil
					.enquote(((OmronCardDto) userdata.cardInfo).startYear));
			// 個人情報-理解度
			cols.add(StringUtil.enquote(""));
			// アンケート項目
			if (OmronConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				outputEnqueteData(cols, userdata);
			}
			// 区分、略称
			outputCategoryAndShortName(cols, userdata);

			// 会社名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));

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
			List<OmronUserDataDto> userDataList, String dim)
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
		header.add(StringUtil.enquote("区分"));
		header.add(StringUtil.enquote("略称"));
		header.add(StringUtil.enquote("会社名"));
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (OmronUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));

			// 区分、略称
			outputCategoryAndShortName(cols, userdata);

			// 会社名
			cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));

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
	 * 区分と略称の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>OmronUserDataDto</b>
	 * @return　区分と略称を出力後の出力バッファ
	 */
	private static List<String> outputCategoryAndShortName(List<String> cols,
			OmronUserDataDto userdata) {
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_CORP)) {
			String companyName = userdata.cardInfo.V_CORP;
			companyName = StringUtil.convertHalfWidthString(companyName, false); // 半角文字変換
			Set<Entry<String, String>> entrySetOc = OmronConstants.OC_COMPANY_SHORTNAME
					.entrySet();
			for (Entry<String, String> entry : entrySetOc) {
				if (StringUtil.find(companyName, entry.getKey())) {
					cols.add(StringUtil.enquote("OC"));
					cols.add(StringUtil.enquote(entry.getValue()));
					return cols;
				}
			}
			Set<Entry<String, String>> entrySetOcg = OmronConstants.OCG_COMPANY_SHORTNAME
					.entrySet();
			for (Entry<String, String> entry : entrySetOcg) {
				if (StringUtil.find(companyName, entry.getKey())) {
					cols.add(StringUtil.enquote("OCG"));
					cols.add(StringUtil.enquote(entry.getValue()));
					return cols;
				}
			}
			Set<Entry<String, String>> entrySetOther = OmronConstants.OTHER_COMPANY_SHORTNAME
					.entrySet();
			for (Entry<String, String> entry : entrySetOther) {
				if (StringUtil.find(companyName, entry.getKey())) {
					cols.add(StringUtil.enquote("外部"));
					cols.add(StringUtil.enquote(entry.getValue()));
					return cols;
				}
			}
			if (StringUtil.find(companyName, "オムロン")) {
				cols.add(StringUtil.enquote("OC"));
				cols.add(StringUtil.enquote("OC"));
				return cols;
			}
			// cols.add(StringUtil.enquote("外部"));
			// cols.add(StringUtil.enquote("-"));
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			return cols;
		} else {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			return cols;
		}
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>OmronUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			OmronUserDataDto userdata) {
		// 問1
		String[] q1Buff = null;
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q1)) {
			q1Buff = userdata.questionInfo.V_Q1.split(" ");
			cols.add(StringUtil.enquote(q1Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問1-理由
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q1_FA));

		// 問2
		String[] q2Buff = null;
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q2)) {
			q2Buff = userdata.questionInfo.V_Q2.split(" ");
			cols.add(StringUtil.enquote(q2Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問2-理由
		String[] q2_1Buff = null;
		if (StringUtil
				.isNotEmpty(((OmronQuestionDto) userdata.questionInfo).V_Q2_1)) {
			q2_1Buff = ((OmronQuestionDto) userdata.questionInfo).V_Q2_1
					.split(" ");
			cols.add(StringUtil.enquote(StringUtil.concatWithDelimit("、",
					q2_1Buff)));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問2-その他
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q2_1_FA));

		// 問3
		String[] q3Buff = null;
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q3)) {
			q3Buff = userdata.questionInfo.V_Q3.split(" ");
			cols.add(StringUtil.enquote(q3Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問3-理由
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q3_FA));

		// 問4-1
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q4_1));

		// 問4-2
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q4_2));

		// 問5-資格取得
		List<String> outputCap = new ArrayList<String>();
		String capName1 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_NAME1;
		String capYear1 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_YEAR1;
		if (StringUtil.isNotEmpty(capName1)) {
			outputCap.add(StringUtil.concatWithDelimit("", capName1, "[",
					capYear1, "]"));
		}
		String capName2 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_NAME2;
		String capYear2 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_YEAR2;
		if (StringUtil.isNotEmpty(capName2)) {
			outputCap.add(StringUtil.concatWithDelimit("", capName2, "[",
					capYear2, "]"));
		}
		String capName3 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_NAME3;
		String capYear3 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_CAP_YEAR3;
		if (StringUtil.isNotEmpty(capName3)) {
			outputCap.add(StringUtil.concatWithDelimit("", capName3, "[",
					capYear3, "]"));
		}
		String q5Cap = StringUtil.concatWithDelimit(",", outputCap);
		cols.add(StringUtil.enquote(q5Cap));

		// 問5-受講研修
		StringBuffer errorPositionSb = new StringBuffer(); // 不正ポジション
		List<String> outputTra = new ArrayList<String>();

		// 受講年月と受講内容を分解
		String traNameAndYear1 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_NAME1;
		String traName1 = null, traYear1 = null;
		if (StringUtil.isNotEmpty(traNameAndYear1)) {
			if (1 == StringUtil.matchCount(traNameAndYear1, ",")) {
				String buff1[] = traNameAndYear1.split(",");
				traYear1 = buff1[0];
				traName1 = buff1[1];
			} else {
				errorPositionSb.append("1");
			}
		}
		String traOwner1 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_OWNER1;
		if (StringUtil.isNotEmpty(traName1)) {
			outputTra.add(StringUtil.concatWithDelimit("", traName1, "（",
					traOwner1, "）", "[", traYear1, "]"));
		}

		// 受講年月と受講内容を分解
		String traNameAndYear2 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_NAME2;
		String traName2 = null, traYear2 = null;
		if (StringUtil.isNotEmpty(traNameAndYear2)) {
			if (1 == StringUtil.matchCount(traNameAndYear2, ",")) {
				String buff2[] = traNameAndYear2.split(",");
				traYear2 = buff2[0];
				traName2 = buff2[1];
			} else {
				errorPositionSb.append("2");
			}
		}
		String traOwner2 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_OWNER2;
		if (StringUtil.isNotEmpty(traName2)) {
			outputTra.add(StringUtil.concatWithDelimit("", traName2, "（",
					traOwner2, "）", "[", traYear2, "]"));
		}

		// 受講年月と受講内容を分解
		String traNameAndYear3 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_NAME3;
		String traName3 = null, traYear3 = null;
		if (StringUtil.isNotEmpty(traNameAndYear3)) {
			if (1 == StringUtil.matchCount(traNameAndYear3, ",")) {
				String buff3[] = traNameAndYear3.split(",");
				traYear3 = buff3[0];
				traName3 = buff3[1];
			} else {
				errorPositionSb.append("3");
			}
		}
		String traOwner3 = ((OmronQuestionDto) userdata.questionInfo).V_Q5_TRA_OWNER3;
		if (StringUtil.isNotEmpty(traName3)) {
			outputTra.add(StringUtil.concatWithDelimit("", traName3, "（",
					traOwner3, "）", "[", traYear3, "]"));
		}
		String q5Tra = StringUtil.concatWithDelimit(",", outputTra);
		cols.add(StringUtil.enquote(q5Tra));
		cols.add(StringUtil.enquote(errorPositionSb.toString()));

		// 問6
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));

		// 問7
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7));

		// 問8-1位
		String[] q8_1Buff = null;
		if (StringUtil
				.isNotEmpty(((OmronQuestionDto) userdata.questionInfo).V_Q8_1)) {
			q8_1Buff = ((OmronQuestionDto) userdata.questionInfo).V_Q8_1
					.split(" ");
			cols.add(StringUtil.enquote(q8_1Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		// 問8-2位
		String[] q8_2Buff = null;
		if (StringUtil
				.isNotEmpty(((OmronQuestionDto) userdata.questionInfo).V_Q8_2)) {
			q8_2Buff = ((OmronQuestionDto) userdata.questionInfo).V_Q8_2
					.split(" ");
			cols.add(StringUtil.enquote(q8_2Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		// 問8-3位
		String[] q8_3Buff = null;
		if (StringUtil
				.isNotEmpty(((OmronQuestionDto) userdata.questionInfo).V_Q8_3)) {
			q8_3Buff = ((OmronQuestionDto) userdata.questionInfo).V_Q8_3
					.split(" ");
			cols.add(StringUtil.enquote(q8_3Buff[0]));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問9
		String[] q9Buff = null;
		if (StringUtil
				.isNotEmpty(((OmronQuestionDto) userdata.questionInfo).V_Q9)) {
			q9Buff = ((OmronQuestionDto) userdata.questionInfo).V_Q9.split(" ");
			cols.add(StringUtil.enquote(StringUtil.concatWithDelimit("、",
					q9Buff)));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// 問9-その他
		cols.add(StringUtil
				.enquote(((OmronQuestionDto) userdata.questionInfo).V_Q9_FA));

		return cols;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>OmronUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, OmronUserDataDto> getMap(
			List<OmronUserDataDto> userDataList) {
		Map<String, OmronUserDataDto> map = new HashMap<String, OmronUserDataDto>();
		for (OmronUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

}