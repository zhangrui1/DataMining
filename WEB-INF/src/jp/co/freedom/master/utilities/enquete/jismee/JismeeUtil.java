package jp.co.freedom.master.utilities.enquete.jismee;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.enquete.EnqueteQuestionDto;
import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * JISMEE向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JismeeUtil {

	/**
	 * マスターデータの取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　マスターデータ
	 * @throws SQLException
	 */
	public static List<EnqueteQuestionDto> getAllData(Connection conn)
			throws SQLException {
		List<EnqueteQuestionDto> userDataList = new ArrayList<EnqueteQuestionDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM jismee;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			EnqueteQuestionDto questionInfo = new EnqueteQuestionDto(); // アンケート情報DTO
			questionInfo.V_VID = rs.getString("V_VID");
			questionInfo.Q1_1 = rs.getString("Q1_1");
			questionInfo.Q1_2 = rs.getString("Q1_2");
			questionInfo.Q2 = rs.getString("Q2");
			questionInfo.Q2_FA = rs.getString("Q2_FA");
			questionInfo.Q3 = rs.getString("Q3");
			questionInfo.Q3_FA = rs.getString("Q3_FA");
			questionInfo.Q3_1 = rs.getString("Q3_1");
			questionInfo.Q3_1_FA = rs.getString("Q3_1_FA");
			questionInfo.Q3_6 = rs.getString("Q3_6");
			questionInfo.Q3_6_FA = rs.getString("Q3_6_FA");
			questionInfo.Q4 = rs.getString("Q4");
			questionInfo.Q4_FA = rs.getString("Q4_FA");
			questionInfo.Q5_1 = rs.getString("Q5_1");
			questionInfo.Q5_1_FA = rs.getString("Q5_1_FA");
			questionInfo.Q5_2 = rs.getString("Q5_2");
			questionInfo.Q5_2_FA = rs.getString("Q5_2_FA");
			questionInfo.Q6 = rs.getString("Q6");
			questionInfo.Q6_FA = rs.getString("Q6_FA");
			questionInfo.Q7 = rs.getString("Q7");
			questionInfo.Q8 = rs.getString("Q8");
			questionInfo.Q8_FA = rs.getString("Q8_FA");
			questionInfo.Q8_5 = rs.getString("Q8_5");
			questionInfo.Q8_6 = rs.getString("Q8_6");
			questionInfo.Q8_6_FA = rs.getString("Q8_6_FA");
			questionInfo.Q8_7 = rs.getString("Q8_7");
			questionInfo.Q8_7_FA = rs.getString("Q8_7_FA");
			questionInfo.Q8_11 = rs.getString("Q8_11");
			questionInfo.Q8_11_FA = rs.getString("Q8_11_FA");
			questionInfo.Q8_12 = rs.getString("Q8_12");
			questionInfo.Q8_12_FA = rs.getString("Q8_12_FA");
			questionInfo.Q9 = rs.getString("Q9");
			questionInfo.Q9_FA = rs.getString("Q9_FA");
			questionInfo.Q10 = rs.getString("Q10");
			questionInfo.Q10_FA = rs.getString("Q10_FA");
			questionInfo.Q10_1 = rs.getString("Q10_1");
			questionInfo.Q10_2 = rs.getString("Q10_2");
			questionInfo.Q10_3 = rs.getString("Q10_3");
			questionInfo.Q10_4 = rs.getString("Q10_4");
			questionInfo.Q10_5 = rs.getString("Q10_5");
			questionInfo.Q10_6 = rs.getString("Q10_6");
			questionInfo.Q10_7 = rs.getString("Q10_7");
			questionInfo.Q10_8 = rs.getString("Q10_8");
			questionInfo.Q10_9 = rs.getString("Q10_9");
			questionInfo.Q10_10 = rs.getString("Q10_10");
			questionInfo.Q10_11 = rs.getString("Q10_11");
			questionInfo.Q11 = rs.getString("Q11");
			questionInfo.Q11_FA = rs.getString("Q11_FA");
			questionInfo.Q11_1_1 = rs.getString("Q11_1_1");
			questionInfo.Q11_1_2 = rs.getString("Q11_1_2");
			questionInfo.Q11_2_1 = rs.getString("Q11_2_1");
			questionInfo.Q11_2_2 = rs.getString("Q11_2_2");
			questionInfo.Q11_3_1 = rs.getString("Q11_3_1");
			questionInfo.Q11_3_2 = rs.getString("Q11_3_2");
			questionInfo.Q11_4_1 = rs.getString("Q11_4_1");
			questionInfo.Q11_4_2 = rs.getString("Q11_4_2");
			questionInfo.Q11_5_1 = rs.getString("Q11_5_1");
			questionInfo.Q11_5_2 = rs.getString("Q11_5_2");
			questionInfo.Q11_6_1 = rs.getString("Q11_6_1");
			questionInfo.Q11_6_2 = rs.getString("Q11_6_2");
			questionInfo.Q11_7_1 = rs.getString("Q11_7_1");
			questionInfo.Q11_7_2 = rs.getString("Q11_7_2");
			questionInfo.Q11_8_1 = rs.getString("Q11_8_1");
			questionInfo.Q11_8_2 = rs.getString("Q11_8_2");
			questionInfo.Q11_9_1 = rs.getString("Q11_9_1");
			questionInfo.Q11_9_2 = rs.getString("Q11_9_2");
			questionInfo.Q11_10_1 = rs.getString("Q11_10_1");
			questionInfo.Q11_10_2 = rs.getString("Q11_10_2");
			questionInfo.Q11_11_1 = rs.getString("Q11_11_1");
			questionInfo.Q11_11_2 = rs.getString("Q11_11_2");
			questionInfo.Q12 = rs.getString("Q12");
			questionInfo.Q13_1 = rs.getString("Q13_1");
			questionInfo.Q13_1_1 = rs.getString("Q13_1_1");
			questionInfo.Q13_1_2 = rs.getString("Q13_1_2");
			questionInfo.Q13_1_3 = rs.getString("Q13_1_3");
			questionInfo.Q13_1_4 = rs.getString("Q13_1_4");
			questionInfo.Q13_1_5 = rs.getString("Q13_1_5");
			questionInfo.Q13_2 = rs.getString("Q13_2");
			questionInfo.Q13_2_1 = rs.getString("Q13_2_1");
			questionInfo.Q13_2_2 = rs.getString("Q13_2_2");
			questionInfo.Q13_2_3 = rs.getString("Q13_2_3");
			questionInfo.Q13_2_4 = rs.getString("Q13_2_4");
			questionInfo.Q13_2_5 = rs.getString("Q13_2_5");
			questionInfo.Q14 = rs.getString("Q14");
			questionInfo.Q15_1 = rs.getString("Q15_1");
			questionInfo.Q15_2 = rs.getString("Q15_2");
			questionInfo.Q15_2_10 = rs.getString("Q15_2_10");
			questionInfo.Q15_2_FA = rs.getString("Q15_2_FA");
			questionInfo.Q15_3 = rs.getString("Q15_3");
			questionInfo.Q15_3_10 = rs.getString("Q15_3_10");
			questionInfo.Q15_3_FA = rs.getString("Q15_3_FA");
			questionInfo.Q16 = rs.getString("Q16");
			questionInfo.Q17 = rs.getString("Q17");

			userDataList.add(questionInfo);
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
	 * @param enqueteDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<EnqueteQuestionDto> enqueteDataList, String dim)
			throws IOException {

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

		header.add(StringUtil.enquote("バーコード"));
		/* Q1.性別・年齢 */
		header.add(StringUtil.enquote("性別"));
		header.add(StringUtil.enquote("年齢"));
		/* Q2.所在地 */
		header.add(StringUtil.enquote("所在地"));
		header.add(StringUtil.enquote("所在地 地名"));
		/* Q3.業種 */
		header.add(StringUtil.enquote("業種"));
		header.add(StringUtil.enquote("業種 その他FA"));
		// 製造業の内訳
		header.add(StringUtil.enquote("食品・飲料"));
		header.add(StringUtil.enquote("繊維・衣服"));
		header.add(StringUtil.enquote("化学"));
		header.add(StringUtil.enquote("出版・印刷"));
		header.add(StringUtil.enquote("パルプ・紙・紙加工品"));
		header.add(StringUtil.enquote("プラスチック"));
		header.add(StringUtil.enquote("鉄鋼業"));
		header.add(StringUtil.enquote("非鉄金属"));
		header.add(StringUtil.enquote("金属製品"));
		header.add(StringUtil.enquote("一般機械"));
		header.add(StringUtil.enquote("電気・電子"));
		header.add(StringUtil.enquote("精密機械"));
		header.add(StringUtil.enquote("輸送用機械・機器"));
		header.add(StringUtil.enquote("その他"));
		header.add(StringUtil.enquote("その他FA"));
		// 金融・証券・保険の内訳
		header.add(StringUtil.enquote("政府系金融機関"));
		header.add(StringUtil.enquote("民間金融機関"));
		header.add(StringUtil.enquote("証券会社"));
		header.add(StringUtil.enquote("保険業"));
		header.add(StringUtil.enquote("ベンチャーキャピタル"));
		header.add(StringUtil.enquote("その他"));
		header.add(StringUtil.enquote("その他FA"));
		/* Q4.会社規模 */
		header.add(StringUtil.enquote("会社規模"));
		header.add(StringUtil.enquote("会社規模 その他FA"));
		/* Q5.職種・役職 */
		header.add(StringUtil.enquote("職種"));
		header.add(StringUtil.enquote("職種 その他FA"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("役職 その他FA"));
		/* Q6.来場対象 */
		header.add(StringUtil.enquote("中小企業総合展①"));
		header.add(StringUtil.enquote("産業交流展2013"));
		header.add(StringUtil.enquote("ものづくりＮＥＸＴ１ 2013"));
		header.add(StringUtil.enquote("その他"));
		header.add(StringUtil.enquote("その他FA"));
		header.add(StringUtil.enquote("特になし"));
		/* Q7.来場実績 */
		header.add(StringUtil.enquote("来場実績"));
		/* Q8.来場のきっかけ */
		header.add(StringUtil.enquote("来場きっかけ1"));
		header.add(StringUtil.enquote("来場きっかけ2"));
		header.add(StringUtil.enquote("来場きっかけ3"));
		header.add(StringUtil.enquote("来場きっかけ4"));
		header.add(StringUtil.enquote("来場きっかけ5"));
		header.add(StringUtil.enquote("来場きっかけ5_1"));
		header.add(StringUtil.enquote("来場きっかけ5_2"));
		header.add(StringUtil.enquote("来場きっかけ5_3"));
		header.add(StringUtil.enquote("来場きっかけ5_4"));
		header.add(StringUtil.enquote("来場きっかけ5_5"));
		header.add(StringUtil.enquote("来場きっかけ6"));
		header.add(StringUtil.enquote("来場きっかけ6_1"));
		header.add(StringUtil.enquote("来場きっかけ6_2"));
		header.add(StringUtil.enquote("来場きっかけ6_3"));
		header.add(StringUtil.enquote("来場きっかけ6_3FA"));
		header.add(StringUtil.enquote("来場きっかけ7"));
		header.add(StringUtil.enquote("来場きっかけ7_1"));
		header.add(StringUtil.enquote("来場きっかけ7_2"));
		header.add(StringUtil.enquote("来場きっかけ7_2FA"));
		header.add(StringUtil.enquote("来場きっかけ8"));
		header.add(StringUtil.enquote("来場きっかけ9"));
		header.add(StringUtil.enquote("来場きっかけ10"));
		header.add(StringUtil.enquote("来場きっかけ11"));
		header.add(StringUtil.enquote("来場きっかけ11_1"));
		header.add(StringUtil.enquote("来場きっかけ11_2"));
		header.add(StringUtil.enquote("来場きっかけ11_3"));
		header.add(StringUtil.enquote("来場きっかけ11_4"));
		header.add(StringUtil.enquote("来場きっかけ11_4FA"));
		header.add(StringUtil.enquote("来場きっかけ12"));
		header.add(StringUtil.enquote("来場きっかけ12_1"));
		header.add(StringUtil.enquote("来場きっかけ12_2"));
		header.add(StringUtil.enquote("来場きっかけ12_3"));
		header.add(StringUtil.enquote("来場きっかけ12_3FA"));
		header.add(StringUtil.enquote("来場きっかけ13"));
		header.add(StringUtil.enquote("来場きっかけ14"));
		header.add(StringUtil.enquote("来場きっかけ その他FA"));
		// Q9.来場目的
		for (int nIndex = 1; nIndex <= 16; nIndex++) {
			header.add(StringUtil.enquote("来場目的" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("来場目的 その他FA"));
		// Q10.商談
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			header.add(StringUtil.enquote("商談" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("件数" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("商談 その他"));
		header.add(StringUtil.enquote("商談 その他FA"));
		header.add(StringUtil.enquote("商談 その他 件数"));
		header.add(StringUtil.enquote("商談なし"));
		// Q11.投資・融資
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			header.add(StringUtil.enquote("投資・融資" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("件数1" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("件数2" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("投資・融資 その他"));
		header.add(StringUtil.enquote("投資・融資 その他FA"));
		header.add(StringUtil.enquote("投資・融資 その他 件数1"));
		header.add(StringUtil.enquote("投資・融資 その他 件数2"));
		header.add(StringUtil.enquote("投資・融資なし"));
		// Q12.どのような事業者と出会いたいか
		header.add(StringUtil.enquote("どのような事業者と出会いたいか"));
		// Q13.お目当てのブース
		header.add(StringUtil.enquote("お目当てのブース有無"));
		header.add(StringUtil.enquote("お目当てのブース1"));
		header.add(StringUtil.enquote("お目当てのブース2"));
		header.add(StringUtil.enquote("お目当てのブース3"));
		header.add(StringUtil.enquote("お目当てのブース4"));
		header.add(StringUtil.enquote("お目当てのブース5"));
		// Q13.回ったブース
		header.add(StringUtil.enquote("回ったブース有無"));
		header.add(StringUtil.enquote("回ったブース1"));
		header.add(StringUtil.enquote("回ったブース2"));
		header.add(StringUtil.enquote("回ったブース3"));
		header.add(StringUtil.enquote("回ったブース4"));
		header.add(StringUtil.enquote("回ったブース5"));
		// Q14.商談実績
		header.add(StringUtil.enquote("商談実績"));
		// Q15.全体感想
		header.add(StringUtil.enquote("全体感想"));
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			header.add(StringUtil.enquote("詳細" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("FA"));
		header.add(StringUtil.enquote("その他"));
		header.add(StringUtil.enquote("その他FA"));
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			header.add(StringUtil.enquote("詳細2" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("FA2"));
		header.add(StringUtil.enquote("その他2"));
		header.add(StringUtil.enquote("その他FA2"));
		// Q16.企画
		header.add(StringUtil.enquote("企画"));
		// Q17.感想・意見
		header.add(StringUtil.enquote("感想・意見"));

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (EnqueteQuestionDto questionInfo : enqueteDataList) {

			List<String> cols = new ArrayList<String>();

			// バーコード番号
			cols.add(StringUtil.isNotEmpty(questionInfo.V_VID) ? StringUtil
					.enquote(questionInfo.V_VID) : StringUtil.enquote(""));

			// アンケート項目
			outputEnqueteData(cols, questionInfo);

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
	 *            <b>EnqueteQuestionDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 * 
	 * @return
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			EnqueteQuestionDto questionInfo) {

		// Q1-1.性別
		cols.add(StringUtil.isNotEmpty(questionInfo.Q1_1) ? StringUtil
				.enquote(questionInfo.Q1_1) : StringUtil.enquote(""));
		// Q1-2.年齢
		cols.add(StringUtil.isNotEmpty(questionInfo.Q1_2) ? StringUtil
				.enquote(questionInfo.Q1_2) : StringUtil.enquote(""));
		// Q2.所在地
		cols.add(StringUtil.isNotEmpty(questionInfo.Q2) ? StringUtil
				.enquote(questionInfo.Q2) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q2_FA) ? StringUtil
				.enquote(questionInfo.Q2_FA) : StringUtil.enquote(""));
		// Q3.業種
		cols.add(StringUtil.isNotEmpty(questionInfo.Q3) ? StringUtil
				.enquote(questionInfo.Q3) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q3_FA) ? StringUtil
				.enquote(questionInfo.Q3_FA) : StringUtil.enquote(""));
		// Q3-1.業種(製造業内訳)
		int[] check3_1Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q3_1, 14);
		for (int nIndex = 1; nIndex <= 14; nIndex++) {
			cols.add(check3_1Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q3_1_FA) ? StringUtil
				.enquote(questionInfo.Q3_1_FA) : StringUtil.enquote(""));
		// Q3-6.業種(金融・証券・保険業内訳)
		int[] check3_6Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q3_6, 6);
		for (int nIndex = 1; nIndex <= 6; nIndex++) {
			cols.add(check3_6Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q3_6_FA) ? StringUtil
				.enquote(questionInfo.Q3_6_FA) : StringUtil.enquote(""));
		// Q4.会社規模
		cols.add(StringUtil.isNotEmpty(questionInfo.Q4) ? StringUtil
				.enquote(questionInfo.Q4) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q4_FA) ? StringUtil
				.enquote(questionInfo.Q4_FA) : StringUtil.enquote(""));
		// Q5-1.職種
		cols.add(StringUtil.isNotEmpty(questionInfo.Q5_1) ? StringUtil
				.enquote(questionInfo.Q5_1) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q5_1_FA) ? StringUtil
				.enquote(questionInfo.Q5_1_FA) : StringUtil.enquote(""));
		// Q5-2.役職
		cols.add(StringUtil.isNotEmpty(questionInfo.Q5_2) ? StringUtil
				.enquote(questionInfo.Q5_2) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q5_2_FA) ? StringUtil
				.enquote(questionInfo.Q5_2_FA) : StringUtil.enquote(""));
		// Q6.来場対象
		int[] check6Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q6, 6);
		for (int nIndex = 1; nIndex <= 4; nIndex++) {
			cols.add(check6Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q6_FA) ? StringUtil
				.enquote(questionInfo.Q6_FA) : StringUtil.enquote(""));
		cols.add(check6Buff[5] == 1 ? "1" : "");
		// Q7.来場実績
		cols.add(StringUtil.isNotEmpty(questionInfo.Q7) ? StringUtil
				.enquote(questionInfo.Q7) : StringUtil.enquote(""));
		// Q8.来場のきっかけ
		int[] check8Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8, 14);
		// 選択肢1～5
		for (int nIndex = 1; nIndex <= 5; nIndex++) {
			cols.add(check8Buff[nIndex] == 1 ? "1" : "");
		}
		// 選択肢5の内訳
		int[] check8_5Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8_5, 5);
		for (int nIndex = 1; nIndex <= 5; nIndex++) {
			cols.add(check8_5Buff[nIndex] == 1 ? "1" : "");
		}
		// 選択肢6
		cols.add(check8Buff[6] == 1 ? "1" : "");
		// 選択肢6の内訳
		int[] check8_6Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8_6, 3);
		for (int nIndex = 1; nIndex <= 3; nIndex++) {
			cols.add(check8_6Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q8_6_FA) ? StringUtil
				.enquote(questionInfo.Q8_6_FA) : StringUtil.enquote(""));
		// 選択肢7
		cols.add(check8Buff[7] == 1 ? "1" : "");
		// 選択肢7の内訳
		int[] check8_7Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8_7, 2);
		for (int nIndex = 1; nIndex <= 2; nIndex++) {
			cols.add(check8_7Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q8_7_FA) ? StringUtil
				.enquote(questionInfo.Q8_7_FA) : StringUtil.enquote(""));
		// 選択肢8～11
		for (int nIndex = 8; nIndex <= 11; nIndex++) {
			cols.add(check8Buff[nIndex] == 1 ? "1" : "");
		}
		// 選択肢11の内訳
		int[] check8_11Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8_11, 4);
		for (int nIndex = 1; nIndex <= 4; nIndex++) {
			cols.add(check8_11Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q8_11_FA) ? StringUtil
				.enquote(questionInfo.Q8_11_FA) : StringUtil.enquote(""));
		// 選択肢12
		cols.add(check8Buff[12] == 1 ? "1" : "");
		// 選択肢12の内訳
		int[] check8_12Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q8_12, 3);
		for (int nIndex = 1; nIndex <= 3; nIndex++) {
			cols.add(check8_12Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q8_12_FA) ? StringUtil
				.enquote(questionInfo.Q8_12_FA) : StringUtil.enquote(""));
		// 選択肢13～14
		for (int nIndex = 13; nIndex <= 14; nIndex++) {
			cols.add(check8Buff[nIndex] == 1 ? "1" : "");
		}
		// その他FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q8_FA) ? StringUtil
				.enquote(questionInfo.Q8_FA) : StringUtil.enquote(""));

		// Q9.来場目的
		int[] check9Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q9, 16);
		for (int nIndex = 1; nIndex <= 16; nIndex++) {
			cols.add(check9Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q9_FA) ? StringUtil
				.enquote(questionInfo.Q9_FA) : StringUtil.enquote(""));
		// Q10.商談
		int[] check10Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q10, 12);
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			cols.add(check10Buff[nIndex] == 1 ? "1" : "");
			String count = getQuestion10DetailCount(questionInfo, nIndex);
			cols.add(StringUtil.isNotEmpty(count) ? StringUtil.enquote(count)
					: StringUtil.enquote(""));
		}
		cols.add(check10Buff[11] == 1 ? "1" : "");
		// その他FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q10_FA) ? StringUtil
				.enquote(questionInfo.Q10_FA) : StringUtil.enquote(""));
		String count = getQuestion10DetailCount(questionInfo, 11);
		cols.add(StringUtil.isNotEmpty(count) ? StringUtil.enquote(count)
				: StringUtil.enquote(""));
		cols.add(check10Buff[12] == 1 ? "1" : "");
		// Q11.投資・出資
		int[] check11Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q11, 12);
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			cols.add(check11Buff[nIndex] == 1 ? "1" : "");
			String count1 = getQuestion11DetailCount(questionInfo, nIndex, "1");
			cols.add(StringUtil.isNotEmpty(count1) ? StringUtil.enquote(count1)
					: StringUtil.enquote(""));
			String count2 = getQuestion11DetailCount(questionInfo, nIndex, "2");
			cols.add(StringUtil.isNotEmpty(count2) ? StringUtil.enquote(count2)
					: StringUtil.enquote(""));
		}
		cols.add(check11Buff[11] == 1 ? "1" : "");
		// その他FA
		cols.add(StringUtil.isNotEmpty(questionInfo.Q11_FA) ? StringUtil
				.enquote(questionInfo.Q11_FA) : StringUtil.enquote(""));
		String count1 = getQuestion11DetailCount(questionInfo, 11, "1");
		cols.add(StringUtil.isNotEmpty(count1) ? StringUtil.enquote(count1)
				: StringUtil.enquote(""));
		String count2 = getQuestion11DetailCount(questionInfo, 11, "2");
		cols.add(StringUtil.isNotEmpty(count2) ? StringUtil.enquote(count2)
				: StringUtil.enquote(""));
		cols.add(check11Buff[12] == 1 ? "1" : "");
		// Q12.どのような事業者と出会いたいか
		cols.add(StringUtil.isNotEmpty(questionInfo.Q12) ? StringUtil
				.enquote(questionInfo.Q12) : StringUtil.enquote(""));
		// Q13-1.お目当てのブース
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1) ? StringUtil
				.enquote(questionInfo.Q13_1) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1_1) ? StringUtil
				.enquote(questionInfo.Q13_1_1) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1_2) ? StringUtil
				.enquote(questionInfo.Q13_1_2) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1_3) ? StringUtil
				.enquote(questionInfo.Q13_1_3) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1_4) ? StringUtil
				.enquote(questionInfo.Q13_1_4) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_1_5) ? StringUtil
				.enquote(questionInfo.Q13_1_5) : StringUtil.enquote(""));
		// Q13-2.回ったのブース
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2) ? StringUtil
				.enquote(questionInfo.Q13_2) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2_1) ? StringUtil
				.enquote(questionInfo.Q13_2_1) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2_2) ? StringUtil
				.enquote(questionInfo.Q13_2_2) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2_3) ? StringUtil
				.enquote(questionInfo.Q13_2_3) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2_4) ? StringUtil
				.enquote(questionInfo.Q13_2_4) : StringUtil.enquote(""));
		cols.add(StringUtil.isNotEmpty(questionInfo.Q13_2_5) ? StringUtil
				.enquote(questionInfo.Q13_2_5) : StringUtil.enquote(""));
		// Q14.商談の成果
		cols.add(StringUtil.isNotEmpty(questionInfo.Q14) ? StringUtil
				.enquote(questionInfo.Q14) : StringUtil.enquote(""));
		// Q15-1.全体の感想
		cols.add(StringUtil.isNotEmpty(questionInfo.Q15_1) ? StringUtil
				.enquote(questionInfo.Q15_1) : StringUtil.enquote(""));
		// Q15-2.詳細
		int[] check15_2Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q15_2, 11);
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			cols.add(check15_2Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q15_2_10) ? StringUtil
				.enquote(questionInfo.Q15_2_10) : StringUtil.enquote(""));
		cols.add(check15_2Buff[11] == 1 ? "1" : "");
		cols.add(StringUtil.isNotEmpty(questionInfo.Q15_2_FA) ? StringUtil
				.enquote(questionInfo.Q15_2_FA) : StringUtil.enquote(""));
		// Q15-3.詳細2
		int[] check15_3Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
				questionInfo.Q15_3, 11);
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			cols.add(check15_3Buff[nIndex] == 1 ? "1" : "");
		}
		cols.add(StringUtil.isNotEmpty(questionInfo.Q15_3_10) ? StringUtil
				.enquote(questionInfo.Q15_3_10) : StringUtil.enquote(""));
		cols.add(check15_3Buff[11] == 1 ? "1" : "");
		cols.add(StringUtil.isNotEmpty(questionInfo.Q15_3_FA) ? StringUtil
				.enquote(questionInfo.Q15_3_FA) : StringUtil.enquote(""));
		// Q16.企画
		cols.add(StringUtil.isNotEmpty(questionInfo.Q16) ? StringUtil
				.enquote(questionInfo.Q16) : StringUtil.enquote(""));
		// Q17.感想・意見
		cols.add(StringUtil.isNotEmpty(questionInfo.Q17) ? StringUtil
				.enquote(questionInfo.Q17) : StringUtil.enquote(""));
		return cols;
	}

	private static String getQuestion10DetailCount(
			EnqueteQuestionDto questionInfo, int nIndex) {
		if (nIndex == 1) {
			return questionInfo.Q10_1;
		} else if (nIndex == 2) {
			return questionInfo.Q10_2;
		} else if (nIndex == 3) {
			return questionInfo.Q10_3;
		} else if (nIndex == 4) {
			return questionInfo.Q10_4;
		} else if (nIndex == 5) {
			return questionInfo.Q10_5;
		} else if (nIndex == 6) {
			return questionInfo.Q10_6;
		} else if (nIndex == 7) {
			return questionInfo.Q10_7;
		} else if (nIndex == 8) {
			return questionInfo.Q10_8;
		} else if (nIndex == 9) {
			return questionInfo.Q10_9;
		} else if (nIndex == 10) {
			return questionInfo.Q10_10;
		} else if (nIndex == 11) {
			return questionInfo.Q10_11;
		} else {
			return null;
		}
	}

	private static String getQuestion11DetailCount(
			EnqueteQuestionDto questionInfo, int nIndex, String type) {
		if ("1".equals(type)) {
			if (nIndex == 1) {
				return questionInfo.Q11_1_1;
			} else if (nIndex == 2) {
				return questionInfo.Q11_2_1;
			} else if (nIndex == 3) {
				return questionInfo.Q11_3_1;
			} else if (nIndex == 4) {
				return questionInfo.Q11_4_1;
			} else if (nIndex == 5) {
				return questionInfo.Q11_5_1;
			} else if (nIndex == 6) {
				return questionInfo.Q11_6_1;
			} else if (nIndex == 7) {
				return questionInfo.Q11_7_1;
			} else if (nIndex == 8) {
				return questionInfo.Q11_8_1;
			} else if (nIndex == 9) {
				return questionInfo.Q11_9_1;
			} else if (nIndex == 10) {
				return questionInfo.Q11_10_1;
			} else if (nIndex == 11) {
				return questionInfo.Q11_11_1;
			} else {
				return null;
			}
		} else {
			if (nIndex == 1) {
				return questionInfo.Q11_1_2;
			} else if (nIndex == 2) {
				return questionInfo.Q11_2_2;
			} else if (nIndex == 3) {
				return questionInfo.Q11_3_2;
			} else if (nIndex == 4) {
				return questionInfo.Q11_4_2;
			} else if (nIndex == 5) {
				return questionInfo.Q11_5_2;
			} else if (nIndex == 6) {
				return questionInfo.Q11_6_2;
			} else if (nIndex == 7) {
				return questionInfo.Q11_7_2;
			} else if (nIndex == 8) {
				return questionInfo.Q11_8_2;
			} else if (nIndex == 9) {
				return questionInfo.Q11_9_2;
			} else if (nIndex == 10) {
				return questionInfo.Q11_10_2;
			} else if (nIndex == 11) {
				return questionInfo.Q11_11_2;
			} else {
				return null;
			}
		}
	}
}