package jp.co.freedom.common.utilities.enquete.shimazu;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.enquete.EnqueteCardDto;
import jp.co.freedom.common.dto.enquete.EnqueteDto;
import jp.co.freedom.common.dto.enquete.EnqueteQuestionDto;
import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * 【島津製作所】アンケート集計用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ShimazuEnqueteUtil {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納(微粒子特性評価の新たな挑戦)
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<EnqueteDto> createInstanceForBiryushi(
			List<String[]> csvData, String event) {
		List<EnqueteDto> userDataList = new ArrayList<EnqueteDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			EnqueteDto dataInfo = new EnqueteDto();
			EnqueteCardDto cardInfo = new EnqueteCardDto();
			EnqueteQuestionDto questionInfo = new EnqueteQuestionDto();

			/* 来場日 */
			dataInfo.day = EnqueteUtil.getVisitDay(row[0]);

			/* 名刺情報 */
			cardInfo.EMAIL = row[1]; // メールアドレス
			cardInfo.ZIP = row[2]; // 郵便番号
			cardInfo.ADDR1 = row[3]; // 都道府県
			cardInfo.ADDR2 = row[4]; // 市区郡
			cardInfo.ADDR3 = row[5]; // 町域
			cardInfo.ADDR4 = row[6]; // 丁目番地
			cardInfo.ADDR5 = row[7]; // ビル名
			cardInfo.COM = row[8]; // 団体代表名
			// cardInfo.COM_OFFICE = row[8]; // 事業所名
			// 部署1~4
			cardInfo.DEPT1 = row[9];
			cardInfo.DEPT2 = row[10];
			cardInfo.DEPT3 = row[11];
			cardInfo.DEPT4 = row[12];
			// 役職1~4
			cardInfo.POS1 = row[13];
			cardInfo.POS2 = row[14];
			cardInfo.POS3 = row[15];
			cardInfo.POS4 = row[16];
			cardInfo.NAME_LAST = row[17]; // 個人姓
			cardInfo.NAME_FIRST = row[18]; // 個人名
			cardInfo.TEL = row[21]; // 電話番号
			cardInfo.FAX = row[22]; // FAX
			dataInfo.event = event; // イベント名
			dataInfo.cardInfo = cardInfo; // 名刺情報

			/* アンケート情報　 */
			questionInfo.Q1 = row[23];
			questionInfo.Q1_FA = row[24];
			questionInfo.Q2 = row[25];
			questionInfo.Q2_FA = row[26];
			questionInfo.Q3_1 = row[27];
			questionInfo.Q3_2 = row[28];
			questionInfo.Q3_3 = row[29];
			questionInfo.Q3_4 = row[30];
			questionInfo.Q4_1 = row[31];
			questionInfo.Q4_1_FA = row[32];
			questionInfo.Q4_2 = row[33];
			questionInfo.Q4_2_FA = row[34];
			questionInfo.Q5 = row[35];
			questionInfo.Q5_4 = row[36];
			questionInfo.Q6 = row[37];
			questionInfo.Q7 = row[38];

			dataInfo.questionInfo = questionInfo;
			userDataList.add(dataInfo);
		}
		return userDataList;
	}

	/**
	 * 照合結果をTXT形式でダウンロード(微粒子特性評価の新たな挑戦)
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
	public static boolean downLoadForBiryushi(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<EnqueteDto> userDataList, String dim) throws IOException {

		int columnNum = 26;
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
		header[++headerNum] = StringUtil.enquote("ｲﾍﾞﾝﾄ名");
		header[++headerNum] = StringUtil.enquote("来場日");
		header[++headerNum] = StringUtil.enquote("担当支店");
		header[++headerNum] = StringUtil.enquote("団体代表名");
		header[++headerNum] = StringUtil.enquote("事業所名");
		header[++headerNum] = StringUtil.enquote("所属名");
		header[++headerNum] = StringUtil.enquote("個人姓");
		header[++headerNum] = StringUtil.enquote("個人名");
		header[++headerNum] = StringUtil.enquote("役職");
		header[++headerNum] = StringUtil.enquote("郵便番号");
		header[++headerNum] = StringUtil.enquote("都道府県");
		header[++headerNum] = StringUtil.enquote("市");
		header[++headerNum] = StringUtil.enquote("電話");
		header[++headerNum] = StringUtil.enquote("FAX");
		header[++headerNum] = StringUtil.enquote("E-mail");
		header[++headerNum] = StringUtil.enquote("1.業務分野");
		header[++headerNum] = StringUtil.enquote("2.目的");
		header[++headerNum] = StringUtil.enquote("3-1.内容");
		header[++headerNum] = StringUtil.enquote("3-2.表示");
		header[++headerNum] = StringUtil.enquote("3-3.テキスト");
		header[++headerNum] = StringUtil.enquote("3-4.講演者の表現力");
		header[++headerNum] = StringUtil.enquote("4-1.機種、オプションへの関心");
		header[++headerNum] = StringUtil.enquote("4-2.購入計画");
		header[++headerNum] = StringUtil.enquote("5.テーマ・製品");
		header[++headerNum] = StringUtil.enquote("6.機種選定への関与");
		header[++headerNum] = StringUtil.enquote("7.ご意見、ご希望");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (EnqueteDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			/* ◆◆◆◆◆セミナー概要◆◆◆◆◆ */
			// イベント名
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.event) ? StringUtil
					.enquote(userdata.event) : StringUtil.enquote("");
			// 来場日
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.day) ? StringUtil
					.enquote(userdata.day) : StringUtil.enquote("");
			// 担当支店
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.charge) ? StringUtil
					.enquote(userdata.charge) : StringUtil.enquote("");

			/* ◆◆◆◆◆名刺情報◆◆◆◆◆ */
			// 団体代表名
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.COM) ? StringUtil
					.enquote(userdata.cardInfo.COM) : StringUtil.enquote("");
			// 事業所名
			String dept = StringUtil.concat(userdata.cardInfo.DEPT1,
					userdata.cardInfo.DEPT2, userdata.cardInfo.DEPT3,
					userdata.cardInfo.DEPT4);
			cols[++nColumn] = StringUtil.isNotEmpty(dept) ? StringUtil
					.enquote(dept) : StringUtil.enquote("");
			// 所属名
			cols[++nColumn] = StringUtil.enquote("");
			// 個人姓
			String name = StringUtil.concatWithDelimit("　",
					userdata.cardInfo.NAME_LAST, userdata.cardInfo.NAME_FIRST);
			cols[++nColumn] = StringUtil.isNotEmpty(name) ? StringUtil
					.enquote(name) : StringUtil.enquote("");
			// 個人名
			cols[++nColumn] = StringUtil.enquote("");
			// 役職
			String pos = StringUtil.concat(userdata.cardInfo.POS1,
					userdata.cardInfo.POS2, userdata.cardInfo.POS3,
					userdata.cardInfo.POS4);
			cols[++nColumn] = StringUtil.isNotEmpty(pos) ? StringUtil
					.enquote(pos) : StringUtil.enquote("");
			// 郵便番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.ZIP) ? StringUtil
					.enquote(userdata.cardInfo.ZIP) : StringUtil.enquote("");
			// 都道府県
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.ADDR1) ? StringUtil
					.enquote(userdata.cardInfo.ADDR1) : StringUtil.enquote("");
			// 市
			String addr = StringUtil.concatWithDelimit("",
					userdata.cardInfo.ADDR2, userdata.cardInfo.ADDR3,
					userdata.cardInfo.ADDR4, userdata.cardInfo.ADDR5);
			cols[++nColumn] = StringUtil.isNotEmpty(addr) ? StringUtil
					.enquote(addr) : StringUtil.enquote("");
			// 電話
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.TEL) ? StringUtil
					.enquote(userdata.cardInfo.TEL) : StringUtil.enquote("");
			// FAX
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.FAX) ? StringUtil
					.enquote(userdata.cardInfo.FAX) : StringUtil.enquote("");
			// E-mail
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.EMAIL) : StringUtil.enquote("");

			/* ◆◆◆◆◆アンケート情報◆◆◆◆◆　 */
			// Q1
			int[] checkQ1Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q1, 30);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q1_FA)) {
				checkQ1Buff[30] = 1;
			}
			List<String> Q1List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ1Buff.length; nIndex++) {
				if (checkQ1Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q1List.add("化学");
						break;
					case 2:
						Q1List.add("石油化学");
						break;
					case 3:
						Q1List.add("ゴム・プラスチック");
						break;
					case 4:
						Q1List.add("繊維");
						break;
					case 5:
						Q1List.add("製薬");
						break;
					case 6:
						Q1List.add("バイオテクノロジ");
						break;
					case 7:
						Q1List.add("抗体医薬");
						break;
					case 8:
						Q1List.add("食品");
						break;
					case 9:
						Q1List.add("医療");
						break;
					case 10:
						Q1List.add("臨床検査");
						break;
					case 11:
						Q1List.add("農林水産");
						break;
					case 12:
						Q1List.add("畜産");
						break;
					case 13:
						Q1List.add("紙・パルプ");
						break;
					case 14:
						Q1List.add("機械");
						break;
					case 15:
						Q1List.add("電機");
						break;
					case 16:
						Q1List.add("電子機器");
						break;
					case 17:
						Q1List.add("半導体");
						break;
					case 18:
						Q1List.add("電池(PV,Li,燃料)");
						break;
					case 19:
						Q1List.add("自動車");
						break;
					case 20:
						Q1List.add("鉄鋼");
						break;
					case 21:
						Q1List.add("非鉄金属");
						break;
					case 22:
						Q1List.add("環境分析");
						break;
					case 23:
						Q1List.add("委託分析");
						break;
					case 24:
						Q1List.add("メンテナンスサービス");
						break;
					case 25:
						Q1List.add("ガス・電力");
						break;
					case 26:
						Q1List.add("学校・教育関係");
						break;
					case 27:
						Q1List.add("学生");
						break;
					case 28:
						Q1List.add("金融");
						break;
					case 29:
						Q1List.add("商社");
						break;
					case 30:
						StringBuffer sb = new StringBuffer();
						sb.append("その他");
						if (StringUtil.isNotEmpty(userdata.questionInfo.Q1_FA)) {
							sb.append("(" + userdata.questionInfo.Q1_FA + ")");
						}
						Q1List.add(sb.toString());
						break;
					default:
						break;
					}
				}
			}
			String Q1 = StringUtil.concatWithDelimit(",", Q1List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q1) ? StringUtil
					.enquote(Q1) : StringUtil.enquote("");

			// Q2
			int[] checkQ2Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q2, 4);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q2_FA)) {
				checkQ2Buff[4] = 1;
			}
			List<String> Q2List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ2Buff.length; nIndex++) {
				if (checkQ2Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q2List.add("仕事に直接関係しているから");
						break;
					case 2:
						Q2List.add("技術的な関心があるから");
						break;
					case 3:
						Q2List.add("導入を検討しているから");
						break;
					case 4:
						StringBuffer sb = new StringBuffer();
						sb.append("その他");
						if (StringUtil.isNotEmpty(userdata.questionInfo.Q2_FA)) {
							sb.append("(" + userdata.questionInfo.Q2_FA + ")");
						}
						Q2List.add(sb.toString());
						break;
					default:
						break;
					}
				}
			}
			String Q2 = StringUtil.concatWithDelimit(",", Q2List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q2) ? StringUtil
					.enquote(Q2) : StringUtil.enquote("");

			// Q3-1
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q3_1) ? StringUtil
					.enquote(userdata.questionInfo.Q3_1) : StringUtil
					.enquote("");
			// Q3-2
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q3_2) ? StringUtil
					.enquote(userdata.questionInfo.Q3_2) : StringUtil
					.enquote("");
			// Q3-3
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q3_3) ? StringUtil
					.enquote(userdata.questionInfo.Q3_3) : StringUtil
					.enquote("");
			// Q3-4
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q3_4) ? StringUtil
					.enquote(userdata.questionInfo.Q3_4) : StringUtil
					.enquote("");

			// Q4-1
			int[] checkQ4_1Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q4_1, 7);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q4_1_FA)) {
				checkQ4_1Buff[7] = 1;
			}
			List<String> Q4_1List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ4_1Buff.length; nIndex++) {
				if (checkQ4_1Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q4_1List.add("バイオ医薬品凝集性評価システム Aggregates Sizer");
						break;
					case 2:
						Q4_1List.add("シングルナノ粒子径測定装置 IG-1000 Plus");
						break;
					case 3:
						Q4_1List.add("粒子径分布測定装置 SALDシリーズ(3100,7500nano、2300、300V,200V ER)");
						break;
					case 4:
						Q4_1List.add("乾式自動密度計アキュピックⅡ 1340シリーズ");
						break;
					case 5:
						Q4_1List.add("自動ポロシメータ オートポアⅣ9500シリーズ");
						break;
					case 6:
						Q4_1List.add("比表面積／細孔分布測定装置(アサップ2020、アサップ2420、トライスターⅡ3020、ジェミニⅦ2390)");
						break;
					case 7:
						StringBuffer sb = new StringBuffer();
						sb.append("その他");
						if (StringUtil
								.isNotEmpty(userdata.questionInfo.Q4_1_FA)) {
							sb.append("(" + userdata.questionInfo.Q4_1_FA + ")");
						}
						Q4_1List.add(sb.toString());
						break;
					default:
						break;
					}
				}
			}
			String Q4_1 = StringUtil.concatWithDelimit(",", Q4_1List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q4_1) ? StringUtil
					.enquote(Q4_1) : StringUtil.enquote("");

			// Q4-2
			int[] checkQ4_2Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q4_2, 7);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q4_2_FA)) {
				checkQ4_2Buff[7] = 1;
			}
			List<String> Q4_2List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ4_2Buff.length; nIndex++) {
				if (checkQ4_2Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q4_2List.add("バイオ医薬品凝集性評価システム Aggregates Sizer");
						break;
					case 2:
						Q4_2List.add("シングルナノ粒子径測定装置 IG-1000 Plus");
						break;
					case 3:
						Q4_2List.add("粒子径分布測定装置 SALDシリーズ(3100,7500nano、2300、300V,200V ER)");
						break;
					case 4:
						Q4_2List.add("乾式自動密度計Ⅱ 1340シリーズ");
						break;
					case 5:
						Q4_2List.add("自動ポロシメータ オートポアⅣ9500シリーズ");
						break;
					case 6:
						Q4_2List.add("比表面積／細孔分布測定装置(アサップ2020、アサップ2420、トライスターⅡ3020、ジェミニⅦ2390)");
						break;
					case 7:
						StringBuffer sb = new StringBuffer();
						sb.append("その他");
						if (StringUtil
								.isNotEmpty(userdata.questionInfo.Q4_2_FA)) {
							sb.append("(" + userdata.questionInfo.Q4_2_FA + ")");
						}
						Q4_2List.add(sb.toString());
						break;
					default:
						break;
					}
				}
			}
			String Q4_2 = StringUtil.concatWithDelimit(",", Q4_2List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q4_2) ? StringUtil
					.enquote(Q4_2) : StringUtil.enquote("");

			// Q5_4
			int[] checkQ5_4Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5_4, 3);
			List<String> Q5_4List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ5_4Buff.length; nIndex++) {
				if (checkQ5_4Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q5_4List.add("半年以内");
						break;
					case 2:
						Q5_4List.add("1年以内");
						break;
					case 3:
						Q5_4List.add("1年以上");
						break;
					default:
						break;
					}
				}
			}
			String Q5_4 = StringUtil.concatWithDelimit(",", Q5_4List);

			// Q5
			int[] checkQ5Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q5, 5);
			if (StringUtil.isNotEmpty(userdata.questionInfo.Q5_4)) {
				checkQ5Buff[4] = 1;
			}
			List<String> Q5List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ5Buff.length; nIndex++) {
				if (checkQ5Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q5List.add("カタログ、資料希望");
						break;
					case 2:
						Q5List.add("詳しい説明を希望");
						break;
					case 3:
						Q5List.add("デモ希望");
						break;
					case 4:
						StringBuffer sb = new StringBuffer();
						sb.append("購入計画あり");
						if (StringUtil.isNotEmpty(Q5_4)) {
							sb.append("(購入予定:" + Q5_4 + ")");
						}
						Q5List.add(sb.toString());
						break;
					case 5:
						Q5List.add("見積希望");
						break;
					default:
						break;
					}
				}
			}
			String Q5 = StringUtil.concatWithDelimit(",", Q5List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q5) ? StringUtil
					.enquote(Q5) : StringUtil.enquote("");

			// Q6
			int[] checkQ6Buff = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(
					userdata.questionInfo.Q6, 3);
			List<String> Q6List = new ArrayList<String>();
			for (int nIndex = 0; nIndex < checkQ6Buff.length; nIndex++) {
				if (checkQ6Buff[nIndex] == 1) {
					switch (nIndex) {
					case 1:
						Q6List.add("選定を決定する立場");
						break;
					case 2:
						Q6List.add("選定に助言する立場");
						break;
					case 3:
						Q6List.add("いずれでもない");
						break;
					default:
						break;
					}
				}
			}
			String Q6 = StringUtil.concatWithDelimit(",", Q6List);
			cols[++nColumn] = StringUtil.isNotEmpty(Q6) ? StringUtil
					.enquote(Q6) : StringUtil.enquote("");

			// Q7
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.questionInfo.Q7) ? StringUtil
					.enquote(userdata.questionInfo.Q7) : StringUtil.enquote("");

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