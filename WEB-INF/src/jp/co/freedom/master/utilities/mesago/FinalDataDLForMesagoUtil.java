package jp.co.freedom.master.utilities.mesago;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.AnalysisTelFax;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;

/**
 * 【MESAGO】最終データDL用ユーティリティ
 *
 * @author フリーダム・グループ
 *
 */
public class FinalDataDLForMesagoUtil {

	/**
	 * secondテーブルから最終データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param mode
	 *            処理モード
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getData(Connection conn, String mode)
			throws SQLException {
		List<MesagoUserDataDto> allDataList = new ArrayList<MesagoUserDataDto>();
		String sql;
		if ("normal".equals(mode)) {
			sql = "SELECT * FROM second;";
		} else {
			sql = "SELECT * FROM second WHERE `duplicate`=? or remove1=? or remove2=? or remove3=? or remove4=? or remove5=? or remove6=? or remove7=? or remove8=?;";
		}
		PreparedStatement ps = conn.prepareStatement(sql);
		if (!"normal".equals(mode)) {
			for (int position = 1; position <= 9; position++) {
				ps.setString(position, "1");
			}
		}
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if ("normal".equals(mode)) {
				// 削除フラグが有効である場合はデータ読み込みを中止
				String duplicate = rs.getString("duplicate");
				String remove1 = rs.getString("remove1");
				String remove2 = rs.getString("remove2");
				String remove3 = rs.getString("remove3");
				String remove4 = rs.getString("remove4");
				String remove5 = rs.getString("remove5");
				String remove6 = rs.getString("remove6");
				String remove7 = rs.getString("remove7");
				String remove8 = rs.getString("remove8");
				if (mergeRemoveFlg(duplicate, remove1, remove2, remove3,
						remove4, remove5, remove6, remove7, remove8)) {
					continue;
				}
			}
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto();
			MesagoQuestionDto questionInfo = new MesagoQuestionDto();
			String visitFlg = rs.getString("visit");
			userdata.visitor = "T".equals(visitFlg); // 来場フラグ
			cardInfo.V_CORP_UPDATE = rs.getString("V_CORP_UPDATE"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 所属
			cardInfo.V_BIZ1 = rs.getString("V_BIZ"); // 役職
			cardInfo.V_TITLE = rs.getString("V_TITLE"); // 敬称
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名性
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX_CNT = rs.getString("V_FAX_CNT"); // FAX番号(国名)
			cardInfo.V_FAX_AREA = rs.getString("V_FAX_AREA"); // FAX番号(市外局番)
			cardInfo.V_FAX_LOCAL = rs.getString("V_FAX_LOCAL"); // FAX番号(市内局番-番号)
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_URL = rs.getString("V_WEB"); // URL
			cardInfo.SEND_FLG = rs.getString("V_SEND"); // 送付先
			cardInfo.V_COUNTRY = rs.getString("V_COUNTRY"); // 国名
			cardInfo.V_ZIP = rs.getString("V_ZIP"); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // ビル名
			// プレス情報
			cardInfo.V_P_NAME = rs.getString("V_P_NAME");
			cardInfo.V_P_NAME_KUBUN = rs.getString("V_P_NAMEKUBUN");
			cardInfo.V_P_BIZ = rs.getString("V_P_BIZ");
			cardInfo.V_P_BIZ_CODE = rs.getString("V_P_BIZCODE");
			cardInfo.V_P_OCC = rs.getString("V_P_OCC");
			cardInfo.V_P_OCC_CODE = rs.getString("V_P_OCCCODE");
			// アンケート情報
			questionInfo.V_Q1_code = rs.getString("V_Q1_CODE"); // 業種区分
			questionInfo.V_Q2_code = rs.getString("V_Q2_CODE"); // 専門分野
			questionInfo.V_Q3_code = rs.getString("V_Q3_CODE"); // 職種
			questionInfo.V_Q4_code = rs.getString("V_Q4_CODE"); // 役職
			questionInfo.V_Q5_code = rs.getString("V_Q5_CODE"); // 従業員数
			questionInfo.V_Q6 = rs.getString("V_Q6"); // 買付決定権
			questionInfo.V_Q7 = rs.getString("V_Q7"); // 来場動機
			questionInfo.V_Q8 = rs.getString("V_Q8"); // 出展検討
			// 原票種別
			cardInfo.V_PREENTRY = rs.getString("V_PRE");
			cardInfo.V_INVITATION = rs.getString("V_SYOTAI");
			cardInfo.V_VIP = rs.getString("V_VIP");
			cardInfo.V_APPOINTEDDAY = rs.getString("V_APP");
			// 仕分用
			cardInfo.V_TICKET_TYPE = rs.getString("V_TICKET_TYPE");
			cardInfo.V_IMAGE_PATH = rs.getString("V_PATH");

			userdata.cardInfo = cardInfo;
			userdata.questionInfo = questionInfo;
			allDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return allDataList;
	}

	/**
	 * 削除フラグのマージ
	 *
	 * @param removeFlgs
	 *            複数の削除フラグ
	 * @return ブール型の削除フラグ
	 */
	private static boolean mergeRemoveFlg(String... removeFlgs) {
		for (String flg : removeFlgs) {
			if ("1".equals(flg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 【最終データ】照合結果をTXT形式でダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForFinalData(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Connection conn, List<MesagoUserDataDto> userDataList, String dim)
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
		List<String> header = MesagoConstants
				.createOutputHeaderForFinalDLData();

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (MesagoUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// 海外住所フラグ
			boolean oversea = isOversea(userdata);

			// 会社情報
			outputCompanyData(cols, userdata, oversea);

			// 住所情報
			outputAddressData(cols, userdata, oversea);

			// URL等
			outputWebData(cols, userdata);

			// 名前情報
			outputNameData(cols, userdata, oversea);

			// 会社詳細情報
			outputCompanyDetailData(cols, userdata, oversea);

			// タイプ情報
			outputTypeData(cols, userdata, oversea);

			// アンケート情報
			outputEnqueteData(cols, userdata, oversea);

			// プレス情報
			outputPressData(cols, userdata);

			// 仕分用情報
			cols.add(StringUtil
					.enquote(((MesagoCardDto) userdata.cardInfo).V_TICKET_TYPE));
			cols.add(StringUtil
					.enquote(((MesagoCardDto) userdata.cardInfo).V_IMAGE_PATH));

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
	 * 会社情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　会社情報出力後の出力バッファ
	 */
	private static List<String> outputCompanyData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
		if (oversea) { // 海外住所である場合
			cols.add(StringUtil.enquote(cardInfo.V_CORP_UPDATE)); // Account
			cols.add(StringUtil.enquote("")); // AccountLocalName
		} else { // 国内住所である場合
			cols.add(StringUtil.enquote("")); // Account
			cols.add(StringUtil.enquote(cardInfo.V_CORP_UPDATE)); // AccountLocalName
		}
		cols.add(StringUtil.enquote("")); // AccountPronounciation
		return cols;
	}

	/**
	 * 住所情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　住所情報出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		if (oversea) { // 海外住所である場合
			// TODO: 海外住所の場合の収納方法
			cols.add(StringUtil.enquote("")); // Address1
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR3)); // Address2
			cols.add(StringUtil.enquote("")); // Address3
			cols.add(StringUtil.enquote("")); // City
			cols.add(StringUtil.enquote("")); // State
			cols.add(StringUtil.enquote("")); // Zip
			cols.add(StringUtil.enquote(userdata.cardInfo.V_COUNTRY)); // Country
			outputBlankData(cols, 7);
		} else { // 国内住所である場合
			outputBlankData(cols, 7);
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR4)); // LAddress1
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR3)); // LAddress2
			cols.add(StringUtil.enquote("")); // LAddress3
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR2)); // LCity
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1)); // LState
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP)); // LZip
			cols.add(StringUtil.enquote(userdata.cardInfo.V_COUNTRY)); // LCountry
		}
		return cols;
	}

	/**
	 * URL情報等の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return　URL情報等の出力後の出力バッファ
	 */
	private static List<String> outputWebData(List<String> cols,
			MesagoUserDataDto userdata) {
		outputBlankData(cols, 15);
		cols.add(StringUtil.enquote(userdata.cardInfo.V_URL)); // WebSite
		outputBlankData(cols, 17);
		return cols;
	}

	/**
	 * 名前情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　名前情報出力後の出力バッファ
	 */
	private static List<String> outputNameData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		if (oversea) { // 海外住所である場合
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TITLE)); // Prefix
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME2)); // FirstName
			outputBlankData(cols, 1); // MiddleName
			cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1)); // LastName
			outputBlankData(cols, 2); // LContactName,LContactPronounciation
		} else { // 国内住所である場合
			outputBlankData(cols, 4); // Prefix
			cols.add(StringUtil.enquote(StringUtil.concat(
					userdata.cardInfo.V_NAME1, userdata.cardInfo.V_NAME2))); // LContactName
			outputBlankData(cols, 1); // LContactPronounciation
		}
		return cols;
	}

	/**
	 * 会社詳細情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　会社詳細情報出力後の出力バッファ
	 */
	private static List<String> outputCompanyDetailData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
		if (oversea) { // 海外住所である場合
			cols.add(StringUtil.enquote(cardInfo.V_DEPT1)); // Department
			outputBlankData(cols, 1); // LDepartment
			cols.add(StringUtil.enquote(cardInfo.V_BIZ1)); // Title
			outputBlankData(cols, 1); // LTitle
		} else { // 国内住所である場合
			outputBlankData(cols, 1); // Department
			cols.add(StringUtil.enquote(cardInfo.V_DEPT1)); // LDepartment
			outputBlankData(cols, 1); // Title
			cols.add(StringUtil.enquote(cardInfo.V_BIZ1)); // LTitle
		}
		AnalysisTelFax analyzer = new AnalysisTelFax(cardInfo.V_TEL, oversea);
		analyzer.execute(true);
		cols.add(StringUtil.enquote(analyzer.country)); // ContactPersonTelCntyCode
		cols.add(StringUtil.enquote(analyzer.areaCode)); // ContactPersonTelAreaCode
		cols.add(StringUtil.enquote(analyzer.localCode)); // ContactPersonTel
		cols.add(StringUtil.enquote(analyzer.extension)); // ContactPersonTelExt
		outputBlankData(cols, 4);
		cols.add(StringUtil.enquote(cardInfo.V_FAX_CNT)); // ContactPersonFaxCntyCode
		cols.add(StringUtil.enquote(cardInfo.V_FAX_AREA)); // ContactPersonFaxAreaCode
		cols.add(StringUtil.enquote(cardInfo.V_FAX_LOCAL)); // ContactPersonFax
		outputBlankData(cols, 8);
		cols.add(StringUtil.enquote(cardInfo.V_EMAIL)); // ContactPersonEmail
		outputBlankData(cols, 37);
		return cols;
	}

	/**
	 * タイプ情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　タイプ情報出力後の出力バッファ
	 */
	private static List<String> outputTypeData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		cols.add(StringUtil.enquote("V")); // Type (V/E)
		cols.add(StringUtil.enquote(oversea ? "E" : "J")); // LanguageCode(SC/TC/J)
		cols.add(StringUtil.enquote("Japan")); // Owner (Hong Kong/China/Japan)
		outputBlankData(cols, 3);
		return cols;
	}

	/**
	 * アンケート情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @param oversea
	 *            海外住所フラグ
	 * @return　アンケート情報出力後の出力バッファ
	 */
	private static List<String> outputEnqueteData(List<String> cols,
			MesagoUserDataDto userdata, boolean oversea) {
		MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
		MesagoQuestionDto questionInfo = (MesagoQuestionDto) userdata.questionInfo;
		cols.add(StringUtil.enquote(questionInfo.V_Q1_code)); // Misc00
		cols.add(StringUtil.enquote(questionInfo.V_Q2_code)); // Misc01
		outputBlankData(cols, 1); // Misc02 DoNotSolicit(T)
		cols.add(StringUtil.enquote(questionInfo.V_Q5_code)); // Misc03
		cols.add(StringUtil.enquote(cardInfo.V_VIP)); // Misc04
		cols.add(StringUtil.enquote(questionInfo.V_Q3_code)); // Misc05
		cols.add(StringUtil.enquote(questionInfo.V_Q4_code)); // Misc06
		cols.add(StringUtil.enquote(userdata.visitor ? "T" : "")); // Misc07
		cols.add(StringUtil.enquote(cardInfo.V_PREENTRY)); // Misc08
		cols.add(StringUtil.enquote(cardInfo.V_INVITATION)); // Misc09
		outputBlankData(cols, 2);
		cols.add(StringUtil.enquote(questionInfo.V_Q7)); // Misc12
		cols.add(StringUtil.enquote(questionInfo.V_Q6)); // Misc13
		if (!isOversea(userdata) && "自宅".equals(cardInfo.SEND_FLG)) {
			cols.add(StringUtil.enquote("HOME")); // Misc14
		} else if ("国内".equals(cardInfo.SEND_FLG)) {
			// TODO: 事前登録以外の場合はこれで正しい？
			cols.add(StringUtil.enquote("Shipping")); // Misc14
		} else if ("海外".equals(cardInfo.SEND_FLG)) {
			// TODO: 事前登録以外の場合はこれで正しい？
			cols.add(StringUtil.enquote("Primary")); // Misc14
		} else {
			cols.add(StringUtil.enquote("")); // Misc14
		}
		outputBlankData(cols, 1); // Misc15
		cols.add(StringUtil.enquote(questionInfo.V_Q8)); // Misc16
		outputBlankData(cols, 1); // Misc17

		return cols;
	}

	/**
	 * プレス情報の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>MesagoUserDataDto</b>
	 * @return プレス情報出力後の出力バッファ
	 */
	private static List<String> outputPressData(List<String> cols,
			MesagoUserDataDto userdata) {
		MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
		String pressName = cardInfo.V_P_NAME;
		// Misc18 Lpublication Name
		cols.add(StringUtil.enquote(pressName));
		// Misc19 Press Publication Type
		cols.add(StringUtil.enquote(""));
		// Misc20 Press Business Nature
		cols.add(StringUtil.enquote(cardInfo.V_P_BIZ_CODE));
		// Misc21 Press Specialized In
		cols.add(StringUtil.enquote(cardInfo.V_P_OCC_CODE));
		// Misc22 Press Rank
		cols.add(StringUtil.enquote(""));
		// Misc23 Invited
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(pressName) ? "T" : ""));
		// Misc24 Came;
		cols.add(StringUtil.enquote(""));
		outputBlankData(cols, 16);
		return cols;
	}

	/**
	 * 指定した列数分連続して空値を出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param count
	 *            出力回数
	 * @return　空列出力後の出力バッファ
	 */
	private static List<String> outputBlankData(List<String> cols, int count) {
		for (int nIndex = 1; nIndex <= count; nIndex++) {
			cols.add(StringUtil.enquote(""));
		}
		return cols;
	}

	/**
	 * 海外住所フラグの検証
	 *
	 * @param cardInfo
	 *            <b>MesagoCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(MesagoUserDataDto userdata) {
		boolean eng = "Japan".equals(userdata.cardInfo.V_COUNTRY);
		boolean jpn = "日本".equals(userdata.cardInfo.V_COUNTRY);
		return !eng && !jpn;
	}
}