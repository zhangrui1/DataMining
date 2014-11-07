package jp.co.freedom.master.utilities.ime;

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
import jp.co.freedom.master.dto.ime.ImeCardDto;
import jp.co.freedom.master.dto.ime.ImeQuestionDto;
import jp.co.freedom.master.dto.ime.ImeUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * SCF向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImeUtil extends Util {

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
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ImeUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<ImeUserDataDto> userDataList = new ArrayList<ImeUserDataDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM appointedday;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ImeUserDataDto userdata = new ImeUserDataDto();
			ImeCardDto cardInfo = new ImeCardDto(); // 名刺情報DTO
			ImeQuestionDto questionInfo = new ImeQuestionDto(); // アンケート情報DTO
			/* 名刺情報　 */
			cardInfo.V_VID = rs.getString("V_VID"); // バーコード番号
			userdata.id = cardInfo.V_VID;
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓カナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名カナ
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
			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			cardInfo.V_DAY = getDay(cardInfo.V_IMAGE_PATH); // 来場日
			/* アンケート情報 */
			questionInfo.V_Q1 = normalize(rs.getString("V_Q1")); // アンケート1
			questionInfo.V_Q2 = normalize(rs.getString("V_Q2")); // アンケート2
			questionInfo.V_Q3 = normalize(rs.getString("V_Q3")); // アンケート3
			questionInfo.V_Q4 = normalize(rs.getString("V_Q4")); // アンケート4
			questionInfo.V_Q5 = normalize(rs.getString("V_Q5")); // アンケート5
			questionInfo.V_Q5_other = rs.getString("V_Q5_FA"); // アンケート5主催予定の案件
			questionInfo.V_Q5_other2 = rs.getString("V_Q5_FA2"); // アンケート5サポートする案件
			String q6 = rs.getString("V_Q6");
			String q6Value = null;
			if (StringUtil.isNotEmpty(q6)) {
				int[] q6Check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(q6,
						11);
				StringBuffer sb = new StringBuffer();
				for (int nIndex = 1; nIndex <= 11; nIndex++) {
					sb.append(q6Check[nIndex] == 1 ? normalize(String
							.valueOf(nIndex)) : "");
				}
				q6Value = sb.toString();
			}
			questionInfo.V_Q6 = q6Value; // アンケート6
			questionInfo.V_Q7 = normalize(rs.getString("V_Q7")); // アンケート7

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
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
	 * ファイル名より来場日時を特定
	 * 
	 * @param path
	 *            ファイル名
	 * @return 来場日
	 */
	public static String getDay(String path) {
		if (StringUtil.isNotEmpty(path)) {
			return path.substring(4, 8);
		}
		return null;
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
			List<ImeUserDataDto> userDataList, String dim)
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
		header.add(StringUtil.enquote("Ｑ２．職種"));
		header.add(StringUtil.enquote("Ｑ３．役職"));
		header.add(StringUtil.enquote("Ｑ４．関わる立場"));
		header.add(StringUtil.enquote("Ｑ５．展示会の計画"));
		header.add(StringUtil.enquote("Ｑ５．主催予定の案件"));
		header.add(StringUtil.enquote("Ｑ５．サポートする案件"));
		header.add(StringUtil.enquote("Ｑ６．本展示会の開催"));
		header.add(StringUtil.enquote("Ｑ７．各種案内"));
		header.add(StringUtil.enquote("来場日"));
		header.add(StringUtil.enquote("原票種別"));
		header.add(StringUtil.enquote("原票手書き"));
		header.add(StringUtil.enquote("原票状況不備"));
		if (ImeConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (ImeUserDataDto userdata : userDataList) {

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
			cols.add(StringUtil
					.enquote(getTicketType(userdata.cardInfo.V_IMAGE_PATH)));
			// 原票手書き
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TICKET_HAND));
			// 原票状況不備
			cols.add(StringUtil.enquote(lackFlg));
			// 不備詳細情報
			if (ImeConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
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
	 *            <b>ImeUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			ImeUserDataDto userdata) {
		// 氏名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1)); // 姓
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME2)); // 名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA1)); // 姓カナ
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA2)); // 名カナ
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
		return cols;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>ImeUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			ImeUserDataDto userdata) {
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
	 *            <b>ImeUserDataDto</b>
	 * @return　アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			ImeUserDataDto userdata) {
		// Q1. 業種
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q1));
		// Q2. 職種
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q2));
		// Q3. 役職
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q3));
		// Q4. 関わる立場
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q4));
		// Q5. 展示会の計画
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5));
		// Q5. 展示会の計画(主催予定の案件)
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5_other));
		// Q5. 展示会の計画(サポートする案件)
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q5_other2));
		// Q6. 本展示会の開催
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));
		// Q7. 各種案内
		cols.add(StringUtil.enquote(userdata.questionInfo.V_Q7));
		return cols;
	}

	/**
	 * 原票種別の特定
	 * 
	 * @param path
	 *            イメージファイルパス
	 * @return 原票種別
	 */
	private static String getTicketType(String path) {
		assert StringUtil.isNotEmpty(path);
		String type = path.substring(
				ImeConfig.TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME,
				ImeConfig.TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME);
		if ("TJ".equals(type)) {
			return "1";
		} else if ("ST".equals(type)) {
			return "2";
		} else if ("SM".equals(type)) {
			return "3";
		}
		return "";
	}

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>ImeUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(ImeUserDataDto userdata) {
		ImeValidator validator = new ImeValidator();
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
	 * @param UserDataDto
	 *            <b>ImeUserDataDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(ImeUserDataDto userdata) {
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
	 *            <b>ImeUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, ImeUserDataDto> getMap(
			List<ImeUserDataDto> userDataList) {
		Map<String, ImeUserDataDto> map = new HashMap<String, ImeUserDataDto>();
		for (ImeUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

}