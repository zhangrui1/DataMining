package jp.co.freedom.common.utilities.enquete.yokohama;

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
 * 【ヨコハマタイヤ】アンケート集計用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class YokohamaEnqueteUtil {

	/**
	 * 【ヨコハマタイヤ】ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<EnqueteDto> createInstanceForYokohama(
			List<String[]> csvData, String event) {
		List<EnqueteDto> userDataList = new ArrayList<EnqueteDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			EnqueteDto dataInfo = new EnqueteDto();
			EnqueteQuestionDto questionInfo = new EnqueteQuestionDto();

			/* 来場日 */
			dataInfo.day = row[1];

			/* アンケート情報　 */
			questionInfo.Q1 = row[2];
			questionInfo.Q2 = row[3];
			questionInfo.Q3 = row[4];
			questionInfo.Q4 = row[5];
			questionInfo.Q5_1 = row[6];
			questionInfo.Q5_2 = row[7];
			questionInfo.Q6 = row[8];
			questionInfo.Q7 = row[9];
			questionInfo.Q7_FA = row[10];
			questionInfo.Q8 = row[11];
			questionInfo.Q9 = row[12];
			questionInfo.Q10_1 = row[13];
			questionInfo.Q10_2 = row[14];
			questionInfo.Q10_3 = row[15];
			questionInfo.Q11_1 = row[16];
			questionInfo.Q11_2 = row[17];
			questionInfo.Q11_3 = row[18];
			questionInfo.Q11_4 = row[19];
			questionInfo.Q11_FA = row[20];
			questionInfo.Q12 = row[21];
			questionInfo.Q12_FA = row[22];
			questionInfo.Q13 = row[23];
			questionInfo.Q13_FA = row[24];
			questionInfo.Q14 = row[25];
			questionInfo.Q15 = row[26];
			questionInfo.Q15_FA = row[27];
			questionInfo.Q16 = row[28];
			questionInfo.Q17 = row[29];

			dataInfo.questionInfo = questionInfo;
			userDataList.add(dataInfo);
		}
		return userDataList;
	}

	/**
	 * 【ヨコハマタイヤ】照合結果をTXT形式でダウンロード
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
	public static boolean downLoadForYokohama(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<EnqueteDto> userDataList, String dim) throws IOException {

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

		header.add(StringUtil.enquote("日付"));
		header.add(StringUtil.enquote("性別"));
		header.add(StringUtil.enquote("年齢"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("車の所有"));
		header.add(StringUtil.enquote("車メーカー"));
		header.add(StringUtil.enquote("車種"));
		for (int nIndex = 1; nIndex <= 5; nIndex++) {
			header.add(StringUtil.enquote("滑走日数" + String.valueOf(nIndex)));
		}
		for (int nIndex = 1; nIndex <= 4; nIndex++) {
			header.add(StringUtil.enquote("交通手段" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("交通手段FA"));
		header.add(StringUtil.enquote("スタッドレスタイヤ所有"));
		header.add(StringUtil.enquote("購入経過年"));
		header.add(StringUtil.enquote("タイヤメーカー"));
		header.add(StringUtil.enquote("タイヤブランド"));
		header.add(StringUtil.enquote("わからない"));
		for (int nIndex = 1; nIndex <= 4; nIndex++) {
			header.add(StringUtil.enquote("購入注意点" + String.valueOf(nIndex)));
		}
		for (int nIndex = 1; nIndex <= 11; nIndex++) {
			header.add(StringUtil.enquote("決め手" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("決め手FA"));
		for (int nIndex = 1; nIndex <= 6; nIndex++) {
			header.add(StringUtil.enquote("使用期間" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("使用期間FA"));
		header.add(StringUtil.enquote("事前チェック"));
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			header.add(StringUtil.enquote("参考情報源" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("参考情報源FA"));
		header.add(StringUtil.enquote("具体的情報源"));
		header.add(StringUtil.enquote("備考"));

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (EnqueteDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			/* ◆◆◆◆◆セミナー概要◆◆◆◆◆ */
			// 日付
			cols.add(StringUtil.isNotEmpty(userdata.day) ? StringUtil
					.enquote(userdata.day) : StringUtil.enquote(""));

			// Q1.性別
			if ("1".equals(userdata.questionInfo.Q1)) {
				cols.add(StringUtil.enquote("男性"));
			} else if ("2".equals(userdata.questionInfo.Q1)) {
				cols.add(StringUtil.enquote("女性"));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// Q2.年齢
			if ("1".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("10代"));
			} else if ("2".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("20代"));
			} else if ("3".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("30代"));
			} else if ("4".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("40代"));
			} else if ("5".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("50代"));
			} else if ("6".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("60代"));
			} else if ("7".equals(userdata.questionInfo.Q2)) {
				cols.add(StringUtil.enquote("70代以上"));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// Q3.都道府県
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q3) ? StringUtil
					.enquote(userdata.questionInfo.Q3) : StringUtil.enquote(""));
			// Q4.車所有
			String q4 = userdata.questionInfo.Q4;
			if (StringUtil.isNotEmpty(q4)) {
				String[] q4List = q4.split(" ");
				if (StringUtil.contains(q4List, "1")) {
					cols.add(StringUtil.enquote("有"));
				} else if (StringUtil.contains(q4List, "2")) {
					cols.add(StringUtil.enquote("無"));
				} else {
					cols.add(StringUtil.enquote(""));
				}
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// Q5.所有車メーカー／車種
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q5_1) ? StringUtil
					.enquote(userdata.questionInfo.Q5_1) : StringUtil
					.enquote(""));
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q5_2) ? StringUtil
					.enquote(userdata.questionInfo.Q5_2) : StringUtil
					.enquote(""));
			// Q6.滑走日数
			int[] q6Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q6, 5);
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				cols.add(q6Check[nIndex] == 1 ? StringUtil.enquote("1")
						: StringUtil.enquote(""));
			}
			// Q7.交通手段
			int[] q7Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q7, 4);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q7_FA)) {
				q7Check[4] = 1;
			}
			for (int nIndex = 1; nIndex <= 4; nIndex++) {
				cols.add(q7Check[nIndex] == 1 ? StringUtil.enquote("1")
						: StringUtil.enquote(""));
			}
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q7_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q7_FA) : StringUtil
					.enquote(""));
			// Q8.スタッドレスタイヤ所有
			if ("1".equals(userdata.questionInfo.Q8)) {
				cols.add(StringUtil.enquote("有"));
			} else if ("2".equals(userdata.questionInfo.Q8)) {
				cols.add(StringUtil.enquote("無"));
			}
			// Q9.スタッドレスの所有年
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q9) ? StringUtil
					.enquote(userdata.questionInfo.Q9) : StringUtil.enquote(""));
			// Q10.スタッドレスタイヤのメーカー／ブランド
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q10_1) ? StringUtil
					.enquote(userdata.questionInfo.Q10_1) : StringUtil
					.enquote(""));
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q10_2) ? StringUtil
					.enquote(userdata.questionInfo.Q10_2) : StringUtil
					.enquote(""));
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q10_3) ? StringUtil
					.enquote("1") : StringUtil.enquote(""));
			// Q11.購入時注意点
			String q11 = StringUtil.concat(userdata.questionInfo.Q11_1,
					userdata.questionInfo.Q11_2, userdata.questionInfo.Q11_3,
					userdata.questionInfo.Q11_4);
			if (StringUtil.isNotEmpty(q11)) {
				String[] q11Buff = q11.split(" ");
				for (int nIndex = 0; nIndex < q11Buff.length; nIndex++) {
					String value = q11Buff[nIndex];
					if ("1".equals(value)) {
						cols.add(StringUtil.enquote("メーカー・ブランド"));
					} else if ("2".equals(value)) {
						cols.add(StringUtil.enquote("価格"));
					} else if ("3".equals(value)) {
						cols.add(StringUtil.enquote("機能・性能"));
					} else if ("4".equals(value)) {
						cols.add(StringUtil.enquote("人気・売れ行きのよさ"));
					} else if ("6".equals(value)) {
						cols.add(StringUtil.enquote("CM・広告"));
					} else if ("7".equals(value)) {
						cols.add(StringUtil.enquote("見た目・デザイン"));
					} else if ("8".equals(value)) {
						cols.add(StringUtil.enquote("店員のおススメ"));
					} else if ("9".equals(value)) {
						cols.add(StringUtil.enquote("評判の良さ（口コミなど）"));
					} else if ("10".equals(value)) {
						cols.add(StringUtil.enquote("キャンペーン情報"));
					} else if ("11".equals(value)) {
						cols.add(StringUtil.enquote(StringUtil
								.isNotEmpty(userdata.questionInfo.Q11_FA) ? "その他（"
								+ userdata.questionInfo.Q11_FA + "）"
								: "その他"));
					} else {
						cols.add(StringUtil.enquote(""));
					}
				}
				for (int nIndex = 1; nIndex <= 4 - q11Buff.length; nIndex++) {
					cols.add(StringUtil.enquote(""));
				}
			} else {
				for (int nIndex = 1; nIndex <= 4; nIndex++) {
					cols.add(StringUtil.enquote(""));
				}
			}

			// Q12.購入の決め手
			int[] q12Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q12, 11);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q12_FA)) {
				q12Check[11] = 1;
			}
			for (int nIndex = 1; nIndex <= 11; nIndex++) {
				cols.add(q12Check[nIndex] == 1 ? StringUtil.enquote("1")
						: StringUtil.enquote(""));
			}
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q12_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q12_FA) : StringUtil
					.enquote(""));
			// Q13.スタッドレスタイヤの使用予定期間
			int[] q13Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q13, 6);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q13_FA)) {
				q13Check[6] = 1;
			}
			for (int nIndex = 1; nIndex <= 6; nIndex++) {
				cols.add(q13Check[nIndex] == 1 ? StringUtil.enquote("1")
						: StringUtil.enquote(""));
			}
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q13_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q13_FA) : StringUtil
					.enquote(""));
			// Q14.事前チェックの有無
			if ("1".equals(userdata.questionInfo.Q14)) {
				cols.add(StringUtil.enquote("はい"));
			} else if ("2".equals(userdata.questionInfo.Q14)) {
				cols.add(StringUtil.enquote("いいえ"));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// Q15.参考メディア／情報源
			int[] q15Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q15, 10);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q15_FA)) {
				q15Check[10] = 1;
			}
			for (int nIndex = 1; nIndex <= 10; nIndex++) {
				cols.add(q15Check[nIndex] == 1 ? StringUtil.enquote("1")
						: StringUtil.enquote(""));
			}
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q15_FA) ? StringUtil
					.enquote(userdata.questionInfo.Q15_FA) : StringUtil
					.enquote(""));
			// Q16.具体的情報源
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q16) ? StringUtil
					.enquote(userdata.questionInfo.Q16) : StringUtil
					.enquote(""));
			// Q17.備考
			cols.add(StringUtil.isNotEmpty(userdata.questionInfo.Q17) ? StringUtil
					.enquote(userdata.questionInfo.Q17) : StringUtil
					.enquote(""));

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