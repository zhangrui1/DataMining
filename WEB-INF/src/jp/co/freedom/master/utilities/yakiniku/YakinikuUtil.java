package jp.co.freedom.master.utilities.yakiniku;

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

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.yakiniku.YakinikuCardDto;
import jp.co.freedom.master.dto.yakiniku.YakinikuQuestionDto;
import jp.co.freedom.master.dto.yakiniku.YakinikuUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * YAKINIKU用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class YakinikuUtil extends Util {

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isPreEntry(UserDataDto userdata) {
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
		return true;
	}

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            RFID番号
	 * @return 検証結果のブール値
	 */
	public boolean isPreEntry(String id) {
		return false;
	}

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            RFID番号
	 * @return 検証結果のブール値
	 */
	public static boolean isAppEntry(String id) {
		return true;
	}

	/**
	 * 全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param city
	 *            東京と大阪を取得する
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<YakinikuUserDataDto> getAllAppointedDayData(
			Connection conn, String city) throws SQLException {
		List<YakinikuUserDataDto> userDataList = new ArrayList<YakinikuUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			YakinikuUserDataDto userdata = new YakinikuUserDataDto();
			YakinikuCardDto cardInfo = new YakinikuCardDto(); // 名刺情報DTO
			YakinikuQuestionDto questionInfo = new YakinikuQuestionDto(); // アンケート情報DTO
			/* 名刺情報 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
			String corpValue = rs.getString("V_CORP");
			// 会社名 株式会社→(株) 有限会社→(有)
			if (YakinikuConfig.SIMPLIFY_CORP_NAME_FLG
					&& StringUtil.isNotEmpty(corpValue)) {
				corpValue = StringUtil.replace(corpValue, "株式会社", "(株)");
				corpValue = StringUtil.replace(corpValue, "有限会社", "(有)");
			}
			cardInfo.V_CORP = corpValue;// 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名カナ
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
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			if ("Tokyo".equals(city)) {
				cardInfo.V_DAY = getDayTokyo(cardInfo.V_IMAGE_PATH); // 来場日
			} else if ("Osaka".equals(city)) {
				cardInfo.V_DAY = getDayOsaka(cardInfo.V_IMAGE_PATH); // 来場日
			}

			// Q1の配列を初期化
			int checkQ1[] = EnqueteUtil.arrayInit(53); // Q1の回答結果
			// アルファから数字に変換
			String q1_a = alphaChangeToNumber2(rs.getString("V_Q1_A"));
			String q1_b = alphaChangeToNumber2(rs.getString("V_Q1_B"));
			String q1_c = alphaChangeToNumber2(rs.getString("V_Q1_C"));
			String q1_d = alphaChangeToNumber2(rs.getString("V_Q1_D"));
			String q1_e = alphaChangeToNumber2(rs.getString("V_Q1_E"));
			String q1_f = alphaChangeToNumber2(rs.getString("V_Q1_F"));
			String q1_g = alphaChangeToNumber2(rs.getString("V_Q1_G"));
			String q1_h = alphaChangeToNumber2(rs.getString("V_Q1_H"));
			String q1_i = alphaChangeToNumber2(rs.getString("V_Q1_I"));
			String q1_j = alphaChangeToNumber2(rs.getString("V_Q1_J"));
			// アンケートQ1を1フラグ立つに保存
			save(checkQ1, 0, q1_a);
			save(checkQ1, 5, q1_b);
			save(checkQ1, 10, q1_c);
			save(checkQ1, 22, q1_d);
			save(checkQ1, 25, q1_e);
			save(checkQ1, 27, q1_f);
			save(checkQ1, 32, q1_g);
			save(checkQ1, 38, q1_h);
			save(checkQ1, 43, q1_i);
			save(checkQ1, 47, q1_j);
			// アンケートQ1その他に1フラグ保存
			saveFa(checkQ1, 22, rs.getString("V_Q1_C_FA"));
			saveFa(checkQ1, 25, rs.getString("V_Q1_D_FA"));
			saveFa(checkQ1, 32, rs.getString("V_Q1_F_FA"));
			saveFa(checkQ1, 38, rs.getString("V_Q1_G_FA"));
			saveFa(checkQ1, 43, rs.getString("V_Q1_H_FA"));
			saveFa(checkQ1, 47, rs.getString("V_Q1_I_FA"));
			saveFa(checkQ1, 52, rs.getString("V_Q1_J_FA"));

			for (int nIndex = 1; nIndex <= 52; nIndex++) {
				if (checkQ1[nIndex] == 1) {
					questionInfo.V_Q1 = normalize(String.valueOf(nIndex));
					break;
				}
			}
			questionInfo.V_Q1_C_other = rs.getString("V_Q1_C_FA"); // アンケート1_C
																	// その他
			questionInfo.V_Q1_D_other = rs.getString("V_Q1_D_FA"); // アンケート1_D
																	// その他
			questionInfo.V_Q1_F_other = rs.getString("V_Q1_F_FA"); // アンケート1_F
																	// その他
			questionInfo.V_Q1_G_other = rs.getString("V_Q1_G_FA"); // アンケート1_G
																	// その他
			questionInfo.V_Q1_H_other = rs.getString("V_Q1_H_FA"); // アンケート1_H
																	// その他
			questionInfo.V_Q1_I_other = rs.getString("V_Q1_I_FA"); // アンケート1_I
																	// その他
			questionInfo.V_Q1_J_other = rs.getString("V_Q1_J_FA"); // アンケート1_J
																	// その他

			// アンケートQ2処理
			int check02[] = EnqueteUtil.arrayInit(13); // Q2の回答結果
			String q2answer = alphaChangeToNumber2(rs.getString("V_Q2")); // 英字→数字に変更
			save(check02, 0, q2answer);
			saveFa(check02, 12, rs.getString("V_Q2_FA"));// アンケートQ2その他に1フラグ保存
			for (int nIndex = 1; nIndex <= 12; nIndex++) {
				if (check02[nIndex] == 1) {
					questionInfo.V_Q2 = normalize(String.valueOf(nIndex));
					break;
				}
			}
			questionInfo.V_Q2_other = rs.getString("V_Q2_FA"); // アンケート2 その他

			questionInfo.V_Q3 = alphaChangeToNumber(rs.getString("V_Q3")); // アンケート3
			// アンケートQ4処理
			int check04[] = EnqueteUtil.arrayInit(6); // Q2の回答結果
			String q4answer = alphaChangeToNumber2(rs.getString("V_Q4")); // 英字→数字に変更
			save(check04, 0, q4answer);
			for (int nIndex = 1; nIndex <= 5; nIndex++) {
				if (check04[nIndex] == 1) {
					questionInfo.V_Q4 = normalize(String.valueOf(nIndex));
					break;
				}
			}
			String q5value = alphaChangeToNumber(rs.getString("V_Q5")); // アンケート5
			if (StringUtil.isNotEmpty(rs.getString("V_Q5_FA"))) {
				if (StringUtil.isEmpty(q5value)) {
					q5value = "09";
				} else if (!q5value.contains("9")) {
					q5value = q5value + "09";
				}
			}
			questionInfo.V_Q5 = q5value; // アンケート5
			questionInfo.V_Q5_other = rs.getString("V_Q5_FA"); // アンケート5　その他
			questionInfo.V_Q6 = alphaChangeToNumber(rs.getString("V_Q6")); // アンケート6
			// アンケートQ7処理
			int check07[] = EnqueteUtil.arrayInit(4); // Q2の回答結果
			String q7answer = alphaChangeToNumber2(rs.getString("V_Q7")); // 英字→数字に変更
			save(check07, 0, q7answer);
			for (int nIndex = 1; nIndex <= 3; nIndex++) {
				if (check07[nIndex] == 1) {
					questionInfo.V_Q7 = normalize(String.valueOf(nIndex));
					break;
				}
			}
			questionInfo.V_Q8 = rs.getString("V_Q8"); // アンケート8
			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * アンケート回答内容を1フラグ展開する
	 * 
	 * @param masterCheck
	 *            1フラグ保存用のマスター配列
	 * @param offset
	 *            マスター配列の保存先開始オフセット番号
	 * @param value
	 *            アンケート回答(例：2 4 5)
	 * @return マスター配列
	 */
	private static int[] save(int[] masterCheck, int offset, String value) {
		int[] check = EnqueteUtil.convertMultiAnswer(value);
		if (check != null) {
			for (int nIndex = 0; nIndex < check.length; nIndex++) {
				masterCheck[check[nIndex] + offset] = 1;
			}
		}
		return masterCheck;
	}

	/**
	 * アンケートのその他の回答をマスター配列に保存
	 * 
	 * @param masterCheck
	 *            1フラグ保存用のマスター配列
	 * @param target
	 *            目的のインデックス
	 * @param value
	 *            アンケート回答FA
	 * @return マスター配列
	 */
	private static int[] saveFa(int[] masterCheck, int target, String value) {
		if (StringUtil.isNotEmpty(value)) {
			masterCheck[target] = 1;
		}
		return masterCheck;
	}

	/**
	 * アンケート回答内容の正規化（1→01、2→02、・・・・、9→09）
	 * 
	 * @param num
	 *            アンケート回答
	 * @return 正規化後のアンケート回答
	 */
	private static String normalize(String num) {
		if (StringUtil.isNotEmpty(num) && num.length() == 1) {
			return "0" + num;
		}
		return num;
	}

	/**
	 * アンケート回答内容の数字化（a→1、b→2、・・・・、i→9）
	 * 
	 * @param alpha
	 *            アンケート回答
	 * @return 数字化後のアンケート回答
	 */
	private static String alphaChangeToNumber2(String alpha) {
		if (StringUtil.isNotEmpty(alpha)) {
			alpha = alpha.replace("a", "1");
			alpha = alpha.replace("b", "2");
			alpha = alpha.replace("c", "3");
			alpha = alpha.replace("d", "4");
			alpha = alpha.replace("e", "5");
			alpha = alpha.replace("f", "6");
			alpha = alpha.replace("g", "7");
			alpha = alpha.replace("h", "8");
			alpha = alpha.replace("i", "9");
			alpha = alpha.replace("j", "10");
			alpha = alpha.replace("k", "11");
			alpha = alpha.replace("l", "12");
		}
		return alpha;
	}

	/**
	 * アンケート回答内容の数字化（a→01、b→02、・・・・、i→09）
	 * 
	 * @param alpha
	 *            アンケート回答
	 * @return 数字化後のアンケート回答
	 */
	private static String alphaChangeToNumber(String alpha) {
		if (StringUtil.isNotEmpty(alpha)) {
			alpha = alpha.replace(" ", "");
			alpha = alpha.replace("a", "01");
			alpha = alpha.replace("b", "02");
			alpha = alpha.replace("c", "03");
			alpha = alpha.replace("d", "04");
			alpha = alpha.replace("e", "05");
			alpha = alpha.replace("f", "06");
			alpha = alpha.replace("g", "07");
			alpha = alpha.replace("h", "08");
			alpha = alpha.replace("i", "09");
			alpha = alpha.replace("j", "10");
			alpha = alpha.replace("k", "11");
			alpha = alpha.replace("l", "12");
			alpha = alpha.replace("m", "13");
			alpha = alpha.replace("n", "14");
			alpha = alpha.replace("o", "15");
			alpha = alpha.replace("p", "16");
		}
		return alpha;
	}

	/**
	 * ファイル名より来場日時を特定 東京データ用
	 * 
	 * @param path
	 *            ファイル名
	 * @return 来場日
	 */
	private static String getDayTokyo(String path) {
		assert StringUtil.isNotEmpty(path);
		String type = path
				.substring(
						YakinikuConfig.TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME_TOKYO,
						YakinikuConfig.TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME_TOKYO);
		// [備忘]画像パスの14,15桁により来場日判断
		// 30 以下は"0122"にし、その以外は"0123"にする
		// 例：yakiniku2014_01_001 →0122
		// yakiniku2014_31_001 →0123
		if (StringUtil.toInteger(type) < 30) {
			return "0122";
		} else {
			return "0123";
		}
	}

	/**
	 * ファイル名より来場日時を特定 大阪データ用
	 * 
	 * @param path
	 *            ファイル名
	 * @return 来場日
	 */
	private static String getDayOsaka(String path) {
		assert StringUtil.isNotEmpty(path);
		String type = path
				.substring(
						YakinikuConfig.TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME_OSAKA,
						YakinikuConfig.TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME_OSAKA);
		// [備忘]画像パスの5,6桁により来場日判断
		// 04 以下は"0204"にし、その以外は"0205"にする
		// 例：YAKI04 →0204
		// YAKI05 →0205
		if (StringUtil.toInteger(type) < 05) {
			return "0204";
		} else {
			return "0205";
		}
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
			List<YakinikuUserDataDto> userDataList, String dim, String city)
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

		header.add(StringUtil.enquote("ＩＤ－Ｎｏ．"));
		header.add(StringUtil.enquote("海外住所ＦＬＧ"));
		header.add(StringUtil.enquote("姓"));
		header.add(StringUtil.enquote("名"));
		header.add(StringUtil.enquote("姓（カナ）"));
		header.add(StringUtil.enquote("名（カナ）"));
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("会社名（カナ）"));
		header.add(StringUtil.enquote("所属部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("市区郡"));
		header.add(StringUtil.enquote("町域"));
		header.add(StringUtil.enquote("番地"));
		header.add(StringUtil.enquote("ビル名"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("ＦＡＸ番号"));
		header.add(StringUtil.enquote("Ｅ－ＭＡＩＬ"));
		// アンケート項目
		header.add(StringUtil.enquote("Ｑ１．業種"));
		header.add(StringUtil.enquote("Ｑ１．業種 C.外食・飲食店 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 D.商社/問屋/卸 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 F.中食/小売業界 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 G.店舗設備/事業出資 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 H.開業予定者 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 I.公共団体/組合/協会/その他 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ１．業種 J.プレス その他（  ）"));
		header.add(StringUtil.enquote("Ｑ２．職種"));
		header.add(StringUtil.enquote("Ｑ２．職種 その他（  ）"));
		header.add(StringUtil.enquote("Ｑ３．来場の目的は何ですか？"));
		header.add(StringUtil.enquote("Ｑ４．仕入・導入についての権限"));
		header.add(StringUtil.enquote("Ｑ５．開催をどこで知りましたか？"));
		header.add(StringUtil.enquote("Ｑ５．開催をどこで知りましたか？ その他（  ）"));
		header.add(StringUtil.enquote("Ｑ６．関心のある展示内容"));
		header.add(StringUtil.enquote("Ｑ７．次回の出展について"));
		header.add(StringUtil.enquote("Ｑ８．情報配信"));
		header.add(StringUtil.enquote("アンケート１８"));
		header.add(StringUtil.enquote("アンケート１９"));
		header.add(StringUtil.enquote("アンケート２０"));
		header.add(StringUtil.enquote("来場日"));
		header.add(StringUtil.enquote("原票種別"));
		header.add(StringUtil.enquote("原票手書き"));
		header.add(StringUtil.enquote("原票状況不備"));
		header.add(StringUtil.enquote("項目１"));
		header.add(StringUtil.enquote("項目２"));
		if (YakinikuConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (YakinikuUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			String lackFlg = getLackFlg(userdata);// 原票状況不備フラグ
			// 海外住所フラグ
			cols.add(isOversea(userdata) && StringUtil.isEmpty(lackFlg) ? StringUtil
					.enquote("1") : StringUtil.enquote(""));
			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			outputEnqueteData(cols, userdata);
			// 来場日
			cols.add(StringUtil.enquote(userdata.cardInfo.V_DAY));
			// 原票種別
			if ("Tokyo".equals(city)) {
				cols.add(StringUtil
						.enquote(getTicketTypeTokyo(userdata.cardInfo.V_IMAGE_PATH)));// 原票種別
			} else if ("Osaka".equals(city)) {
				cols.add(StringUtil
						.enquote(getTicketTypeOsaka(userdata.cardInfo.V_IMAGE_PATH)));// 原票種別
			}

			// 原票手書き
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TICKET_HAND));
			// 原票状況不備
			cols.add(StringUtil.enquote(lackFlg));
			// 項目1-2 空の列
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
			// 不備詳細情報
			if (YakinikuConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.enquote(userdata.validationErrResult));
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

	/**
	 * 氏名および住所情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>YakinikuUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			YakinikuUserDataDto userdata) {
		// 氏名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1)); // 姓
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME2)); // 名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA1)); // 姓カナ
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA2)); // 名カナ
		// 会社名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));// 会社名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP_KANA));// 会社名カナ
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
		return cols;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>YakinikuUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			YakinikuUserDataDto userdata) {
		// 郵便番号
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
		// 都道府県
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
		// 市区郡
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR2));
		// 町域
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR3));
		// 番地
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR4));
		// ビル名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR5));
		// 電話番号 (■が含まれている場合は空値に置換)
		cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_TEL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_TEL) ? StringUtil
				.enquote(userdata.cardInfo.V_TEL) : StringUtil.enquote(""));
		// FAX番号 (■が含まれている場合は空値に置換)
		cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_FAX)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_FAX) ? StringUtil
				.enquote(userdata.cardInfo.V_FAX) : StringUtil.enquote(""));
		// メールアドレス (■が含まれている場合は空値に置換)
		cols.add(StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? StringUtil
				.enquote(userdata.cardInfo.V_EMAIL) : StringUtil.enquote(""));
		return cols;
	}

	/**
	 * アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>YakinikuUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			YakinikuUserDataDto userdata) {
		// Q1. 業種
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_C_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_D_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_F_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_G_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_H_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_I_other));
		cols.add(StringUtil
				.enquote(((YakinikuQuestionDto) userdata.questionInfo).V_Q1_J_other));
		// Q2. 職種
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q2));
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q2_other));
		// Q3. 来場目的
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3));
		// Q4. 仕入・導入についての権限
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q4));
		// Q5. どこでお知りになられました
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5));
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5_other));
		// Q6. 本展示会の開催
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));
		// Q7. 各種案内
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7));
		// Q8. 上記を承諾しない
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q8));
		// アンケート18-20 空の列
		cols.add(StringUtil.enquote(""));
		cols.add(StringUtil.enquote(""));
		cols.add(StringUtil.enquote(""));
		return cols;
	}

	/**
	 * 原票種別の特定 東京データ用
	 * 
	 * @param path
	 *            イメージファイルパス
	 * @return 原票種別
	 */
	private static String getTicketTypeTokyo(String path) {
		assert StringUtil.isNotEmpty(path);
		String type = path
				.substring(
						YakinikuConfig.TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME_TOKYO,
						YakinikuConfig.TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME_TOKYO);
		// [備忘]画像パスの14,15桁により原票種別判断
		// 「29,37」を2にし、その以外は1にする
		// 例：yakiniku2014_01_001 →1
		// yakiniku2014_29_001 →2
		if ("29".equals(type) || "37".equals(type)) {
			return "2";
		} else {
			return "1";
		}
	}

	/**
	 * 原票種別の特定 大阪データ用
	 * 
	 * @param path
	 *            イメージファイルパス
	 * @return 原票種別
	 */
	private static String getTicketTypeOsaka(String path) {
		assert StringUtil.isNotEmpty(path);
		// [備忘]画像パスにより原票種別判断
		// 「VIP」が含まれた場合は2にし、その以外は1にする
		// 例：YAKI04 →1
		// YAKI04VIP →2
		if (path.contains("VIP")) {
			return "2";
		} else {
			return "1";
		}
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            <b>YakinikuUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(YakinikuUserDataDto userdata) {
		YakinikuValidator validator = new YakinikuValidator();
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
	 * @return 検証結果のブール値
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
	 * @param UserDataDto
	 *            <b>YakinikuUserDataDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(YakinikuUserDataDto userdata) {
		boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
		boolean check2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
		boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
		boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
		boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
		return check1 && check2 && check3 && check4 && check5;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>YakinikuUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, YakinikuUserDataDto> getMap(
			List<YakinikuUserDataDto> userDataList) {
		Map<String, YakinikuUserDataDto> map = new HashMap<String, YakinikuUserDataDto>();
		for (YakinikuUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

}