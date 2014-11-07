package jp.co.freedom.common.utilities.enquete.ceatec;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.enquete.EnqueteDto;
import jp.co.freedom.common.dto.enquete.EnqueteQuestionDto;
import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * 【CEATEC】アンケート集計用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class NedoEnqueteUtil {

	/**
	 * 【NEDO CEATEC】ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<EnqueteDto> createInstanceForNedo(
			List<String[]> csvData, String event) {
		List<EnqueteDto> userDataList = new ArrayList<EnqueteDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			EnqueteDto dataInfo = new EnqueteDto();
			EnqueteQuestionDto questionInfo = new EnqueteQuestionDto();

			/* 来場日 */
			dataInfo.day = getDay(row[0]);

			/* アンケート情報　 */
			questionInfo.Q1 = row[1];
			questionInfo.Q1_FA = row[2];
			questionInfo.Q2 = row[3];
			questionInfo.Q2_FA = row[4];
			questionInfo.Q2_FA2 = row[5];
			questionInfo.Q3 = row[6];
			questionInfo.Q3_FA = row[7];
			questionInfo.Q4 = row[8];
			questionInfo.Q5_1 = row[9]; // 性別
			questionInfo.Q5_2 = row[10]; // 年齢
			questionInfo.Q5_3 = row[11]; // 業種
			questionInfo.Q5_3_FA = row[12]; // 業種その他FA
			questionInfo.Q5_4 = row[13]; // 職種
			questionInfo.Q5_4_FA = row[14]; // 職種その他FA
			questionInfo.Q5_5 = row[15]; // 所属団体規模

			dataInfo.questionInfo = questionInfo;
			userDataList.add(dataInfo);
		}
		return userDataList;
	}

	/**
	 * ファイル名より来場日を取得
	 * 
	 * @param fileName
	 *            ファイル名 (例：NEDO_1001)
	 * @return 来場日
	 */
	private static String getDay(String fileName) {
		return fileName.substring(5, 7) + "月" + fileName.substring(7, 9) + "日";
	}

	/**
	 * 【NEDO CEATEC】照合結果をTXT形式でダウンロード
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
	 */
	public static boolean downLoadForNedo(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<EnqueteDto> userDataList, String dim) throws IOException {

		int columnNum = 66;
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
		header[++headerNum] = StringUtil.enquote("日付");
		header[++headerNum] = StringUtil.enquote("");
		header[++headerNum] = StringUtil.enquote("Q1:1");
		header[++headerNum] = StringUtil.enquote("Q1:2");
		header[++headerNum] = StringUtil.enquote("Q1:3");
		header[++headerNum] = StringUtil.enquote("Q1:4");
		header[++headerNum] = StringUtil.enquote("Q1:5");
		header[++headerNum] = StringUtil.enquote("Q1:6");
		header[++headerNum] = StringUtil.enquote("Q1:7");
		header[++headerNum] = StringUtil.enquote("Q1:7FA");
		header[++headerNum] = StringUtil.enquote("");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った1");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った2");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った3");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った4");
		header[++headerNum] = StringUtil.enquote("Q2:役に立った4FA");
		header[++headerNum] = StringUtil.enquote("Q2:役に立たなかった");
		header[++headerNum] = StringUtil.enquote("Q2:役に立たなかった1");
		header[++headerNum] = StringUtil.enquote("Q2:役に立たなかった2");
		header[++headerNum] = StringUtil.enquote("Q2:役に立たなかった3");
		header[++headerNum] = StringUtil.enquote("Q2:役に立たなかった3FA");
		header[++headerNum] = StringUtil.enquote("Q2:どちらともいえない");
		header[++headerNum] = StringUtil.enquote("");
		header[++headerNum] = StringUtil.enquote("Q3:あった");
		header[++headerNum] = StringUtil.enquote("Q3:ブース番号1");
		header[++headerNum] = StringUtil.enquote("Q3:ブース番号2");
		header[++headerNum] = StringUtil.enquote("Q3:ブース番号3");
		header[++headerNum] = StringUtil.enquote("Q3:ブース番号4");
		header[++headerNum] = StringUtil.enquote("Q3:ブース番号5");
		header[++headerNum] = StringUtil.enquote("Q3:なかった");
		header[++headerNum] = StringUtil.enquote("Q4");
		header[++headerNum] = StringUtil.enquote("");
		header[++headerNum] = StringUtil.enquote("Q5:性別 男");
		header[++headerNum] = StringUtil.enquote("Q5:性別 女");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 ～19");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 20代");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 30代");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 40代");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 50代");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 60代");
		header[++headerNum] = StringUtil.enquote("Q5:年齢 70代～");
		header[++headerNum] = StringUtil.enquote("Q5:業種1");
		header[++headerNum] = StringUtil.enquote("Q5:業種2");
		header[++headerNum] = StringUtil.enquote("Q5:業種3");
		header[++headerNum] = StringUtil.enquote("Q5:業種4");
		header[++headerNum] = StringUtil.enquote("Q5:業種5");
		header[++headerNum] = StringUtil.enquote("Q5:業種6");
		header[++headerNum] = StringUtil.enquote("Q5:業種7");
		header[++headerNum] = StringUtil.enquote("Q5:業種8");
		header[++headerNum] = StringUtil.enquote("Q5:業種9");
		header[++headerNum] = StringUtil.enquote("Q5:業種10");
		header[++headerNum] = StringUtil.enquote("Q5:業種11");
		header[++headerNum] = StringUtil.enquote("Q5:業種12");
		header[++headerNum] = StringUtil.enquote("Q5:業種13");
		header[++headerNum] = StringUtil.enquote("Q5:業種14");
		header[++headerNum] = StringUtil.enquote("Q5:業種15");
		header[++headerNum] = StringUtil.enquote("Q5:業種15FA");
		header[++headerNum] = StringUtil.enquote("Q5:職種1");
		header[++headerNum] = StringUtil.enquote("Q5:職種2");
		header[++headerNum] = StringUtil.enquote("Q5:職種3");
		header[++headerNum] = StringUtil.enquote("Q5:職種4");
		header[++headerNum] = StringUtil.enquote("Q5:職種5");
		header[++headerNum] = StringUtil.enquote("Q5:職種5FA");
		header[++headerNum] = StringUtil.enquote("Q5:所属団体規模 300人未満");
		header[++headerNum] = StringUtil.enquote("Q5:所属団体規模 300人以上");

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (EnqueteDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			/* ◆◆◆◆◆セミナー概要◆◆◆◆◆ */
			// 日付
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.day) ? StringUtil
					.enquote(userdata.day) : StringUtil.enquote("");
			// 空列
			cols[++nColumn] = StringUtil.enquote("");
			// Q1
			int[] checkQ1Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q1, 7);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q1_FA)) {
				checkQ1Buff[7] = 1;
			}
			for (int nIndex = 1; nIndex < checkQ1Buff.length; nIndex++) {
				if (checkQ1Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q1 その他FA
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.questionInfo.Q1_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q1_FA) : StringUtil
					.enquote("");
			// 空列
			cols[++nColumn] = StringUtil.enquote("");
			// Q2(役に立った)
			int[] checkQ2Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q2, 10);
			if (checkQ2Buff[2] == 1 || checkQ2Buff[3] == 1
					|| checkQ2Buff[4] == 1 || checkQ2Buff[5] == 1) {
				checkQ2Buff[1] = 1;
			}
			if (checkQ2Buff[7] == 1 || checkQ2Buff[8] == 1
					|| checkQ2Buff[9] == 1) {
				checkQ2Buff[6] = 1;
			}
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q2_FA)) {
				checkQ2Buff[1] = 1;
				checkQ2Buff[5] = 1;
			}
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q2_FA2)) {
				checkQ2Buff[6] = 1;
				checkQ2Buff[9] = 1;
			}
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				if (checkQ2Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q2 その他FA
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.questionInfo.Q2_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q2_FA) : StringUtil
					.enquote("");
			// Q2(役に立たなかった)
			for (int nIndex = 6; nIndex <= 9; nIndex++) {
				if (checkQ2Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q2 その他FA2
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.questionInfo.Q2_FA2) ? StringUtil
					.enquote(userdata.questionInfo.Q2_FA2) : StringUtil
					.enquote("");
			// Q2(どちらともいえない)
			cols[++nColumn] = checkQ2Buff[10] == 1 ? StringUtil.enquote("1")
					: StringUtil.enquote("");
			// 空列
			cols[++nColumn] = StringUtil.enquote("");

			// Q3
			int[] checkQ3Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q3, 2);
			cols[++nColumn] = checkQ3Buff[1] == 1
					|| StringUtil.isNotEmpty(userdata.questionInfo.Q3_FA) ? StringUtil
					.enquote("1") : StringUtil.enquote("");

			String[] q3FaBuff = userdata.questionInfo.Q3_FA.split(",");
			if (q3FaBuff.length <= 5) {
				for (String q3Fa : q3FaBuff) {
					cols[++nColumn] = StringUtil.isNotEmpty(q3Fa) ? StringUtil
							.enquote(q3Fa) : StringUtil.enquote("");
				}
				for (int nIndex = 0; nIndex < 5 - q3FaBuff.length; nIndex++) {
					cols[++nColumn] = StringUtil.enquote("");
				}
			} else {
				for (int nIndex = 0; nIndex < 5; nIndex++) {
					cols[++nColumn] = StringUtil.isNotEmpty(q3FaBuff[nIndex]) ? StringUtil
							.enquote(q3FaBuff[nIndex]) : StringUtil.enquote("");
				}
			}
			cols[++nColumn] = checkQ3Buff[2] == 1 ? StringUtil.enquote("1")
					: StringUtil.enquote("");
			// Q4
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q4) ? StringUtil
					.enquote(userdata.questionInfo.Q4) : StringUtil.enquote("");
			// 空列
			cols[++nColumn] = StringUtil.enquote("");
			// Q5 性別
			int[] checkQ5_1Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5_1, 2);
			for (int nIndex = 1; nIndex <= 2; nIndex++) {
				if (checkQ5_1Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q5 年齢
			String ageStr = userdata.questionInfo.Q5_2;
			if (StringUtil.isNotEmpty(ageStr) && StringUtil.isNumeric(ageStr)) {
				int age = Integer.parseInt(ageStr);
				if (age <= 19) {
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
				} else if (20 <= age && age < 30) {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
				} else if (30 <= age && age < 40) {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
				} else if (40 <= age && age < 50) {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
				} else if (50 <= age && age < 60) {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
				} else if (60 <= age && age < 70) {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("");
					cols[++nColumn] = StringUtil.enquote("1");
					cols[++nColumn] = StringUtil.enquote("");
				}
			} else {
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
				cols[++nColumn] = StringUtil.enquote("");
			}
			// Q5 業種
			int[] checkQ5_3Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5_3, 15);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q5_3_FA)) {
				checkQ5_3Buff[15] = 1;
			}
			for (int nIndex = 1; nIndex < checkQ5_3Buff.length; nIndex++) {
				if (checkQ5_3Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q5 業種 その他FA
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.questionInfo.Q5_3_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q5_3_FA) : StringUtil
					.enquote("");
			// Q5 職種
			int[] checkQ5_4Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5_4, 5);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q5_4_FA)) {
				checkQ5_4Buff[5] = 1;
			}
			for (int nIndex = 1; nIndex < checkQ5_4Buff.length; nIndex++) {
				if (checkQ5_4Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
				} else {
					cols[++nColumn] = StringUtil.enquote("");
				}
			}
			// Q5 職種 その他FA
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.questionInfo.Q5_4_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q5_4_FA) : StringUtil
					.enquote("");
			// Q5 所属団体規模
			int[] checkQ5_5Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5_5, 2);
			for (int nIndex = 1; nIndex < checkQ5_5Buff.length; nIndex++) {
				if (checkQ5_5Buff[nIndex] == 1) {
					cols[++nColumn] = StringUtil.enquote("1");
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
}