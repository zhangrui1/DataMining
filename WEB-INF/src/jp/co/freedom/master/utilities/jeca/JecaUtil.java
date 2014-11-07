package jp.co.freedom.master.utilities.jeca;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.jeca.JecaCardDto;
import jp.co.freedom.master.dto.jeca.JecaQuestionDto;
import jp.co.freedom.master.dto.jeca.JecaUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * Jeca向けユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class JecaUtil extends Util {

	@Override
	public boolean isPreEntry(UserDataDto userdata) {
		if (userdata instanceof JecaUserDataDto) {
			JecaUserDataDto info = (JecaUserDataDto) userdata;
			return info.preentry;
		}
		return false;
	}

	@Override
	public boolean isAppEntry(UserDataDto userdata) {
		if (userdata instanceof JecaUserDataDto) {
			JecaUserDataDto info = (JecaUserDataDto) userdata;
			return info.appointedday;
		}
		return false;
	}

	/**
	 * 団体登録ユーザーであるか否かの検証
	 *
	 * @param userdata
	 *            ユーザーデータ
	 */
	public boolean isGroup(UserDataDto userdata) {
		if (userdata instanceof JecaUserDataDto) {
			JecaUserDataDto info = (JecaUserDataDto) userdata;
			return info.group;
		}
		return false;
	}

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 *
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isPreEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(JecaConfig.PREENTRY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDが当日入力ユーザーであるか否かの検証
	 *
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isAppEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(JecaConfig.APPOINTEDDAY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDが団体登録ユーザーであるか否かの検証
	 *
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isGroupEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(JecaConfig.GROUP_BARCODE_START_BIT);
	}

	/**
	 * 海外住所フラグの検証
	 *
	 * @param cardInfo
	 *            <b>JecaCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(JecaUserDataDto userdata) {
		JecaUtil util = new JecaUtil();
		boolean result;
		if (util.isPreEntry(userdata)) { // 事前登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else if (util.isAppEntry(userdata)) { // 当日登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else if (util.isGroup(userdata)) { // 団体登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else {
			System.out.println("海外住所フラグの特定に失敗しました:" + userdata.id);
			result = false;
		}
		return result;
	}

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<JecaUserDataDto> createInstance(List<String[]> csvData) {
		List<JecaUserDataDto> userData = new ArrayList<JecaUserDataDto>();// ユーザーデータを保持するリスト
		JecaUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 2) {// アンケートシール番号／要望コードの行
				if (dataDto == null) {
					dataDto = new JecaUserDataDto();
				}
				String value = row[1];
				if (value.startsWith("A")) {// アンケートシール番号の格納
					String enquete = row[1].substring(1);
					dataDto.enqueteCode.add(enquete);
				} else if (value.startsWith("Y")) {// 要望コードの格納
					String request = row[1].substring(1);
					dataDto.requestCode.add(request);
				}
			} else if (row.length == 3) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new JecaUserDataDto();
				dataDto.reader = row[0];// バーコードリーダーID
				String rfid = row[1];
				dataDto.id = rfid.substring(1);
				dataDto.timeByRfid = row[2];
			} else {
				// 読み飛ばし
				continue;
			}
		}
		// 最後尾のユーザーデータのリスト格納
		if (dataDto != null) {
			// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * 指定配列の最後の要素を削除
	 *
	 * @param array
	 *            対象配列
	 * @return 最後の要素を削除した配列
	 */
	private static String[] removeLastSpaceElement(String[] array) {
		assert array != null;
		String[] newArray = new String[array.length - 1];
		for (int nIndex = 0; nIndex < array.length - 1; nIndex++) {
			newArray[nIndex] = array[nIndex];
		}
		return newArray;
	}

	/**
	 * 変換表のインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<JecaUserDataDto> createInstanceForConvertTable(
			List<String[]> csvData) {
		List<JecaUserDataDto> userData = new ArrayList<JecaUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			JecaUserDataDto dataDto = new JecaUserDataDto();
			if (row.length == 7) {
				dataDto.id = row[0];
				((JecaCardDto) dataDto.cardInfo).PREENTRY_ID = row[1];
				dataDto.cardInfo.V_NAME1 = row[2];
				dataDto.cardInfo.V_NAME2 = row[3];
				userData.add(dataDto);
			} else {
				System.out.println("変換表データ中に2列ではない行を発見");
			}
		}
		return userData;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>JecaUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, JecaUserDataDto> getMap(
			List<JecaUserDataDto> userDataList) {
		Map<String, JecaUserDataDto> map = new HashMap<String, JecaUserDataDto>();
		for (JecaUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 全ての事前登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての事前登録データ
	 * @throws SQLException
	 */
	public static List<JecaUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<JecaUserDataDto> userDataList = new ArrayList<JecaUserDataDto>();
		String sql = "SELECT * FROM preentry;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JecaUserDataDto userdata = new JecaUserDataDto();
			JecaCardDto cardInfo = new JecaCardDto(); // 名刺情報DTO
			JecaQuestionDto questionInfo = new JecaQuestionDto(); // アンケート情報DTO

			userdata.preentry = true; // 事前登録フラグ

			/* 名刺情報 */
			cardInfo.PREENTRY_ID = rs.getString("V_NO"); // 登録券番号
			userdata.id = cardInfo.PREENTRY_ID; // [備忘] MAPのkeyとして利用するために設定
			String id = rs.getString("V_ID");
			cardInfo.V_OVERSEA = id.startsWith("E") ? "1" : ""; // 海外住所フラグ
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓仮名
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名仮名

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名仮名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 以下住所
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q1_other = rs.getString("V_Q1_FA");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q2_other = rs.getString("V_Q2_FA");
			questionInfo.V_Q3 = rs.getString("V_Q3");
			questionInfo.V_Q3_other = rs.getString("V_Q3_FA");
			questionInfo.V_Q4 = rs.getString("V_Q4");
			questionInfo.V_Q4_other = rs.getString("V_Q4_FA");
			questionInfo.V_Q5 = rs.getString("V_Q5");
			questionInfo.V_Q5_other = rs.getString("V_Q5_FA");
			questionInfo.V_Q6 = rs.getString("V_Q6");
			questionInfo.V_Q6_other = rs.getString("V_Q6_FA");
			questionInfo.V_Q7 = rs.getString("V_Q7");
			questionInfo.V_Q7_other = rs.getString("V_Q7_FA");
			questionInfo.V_Q8 = rs.getString("V_Q8");
			questionInfo.V_Q8_other = rs.getString("V_Q8_FA");
			questionInfo.V_Q9 = rs.getString("V_Q9");
			questionInfo.V_Q10 = rs.getString("V_Q10");
			questionInfo.V_Q11 = rs.getString("V_Q11");

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
	 * 全ての当日登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<JecaUserDataDto> getAllAppointeddayData(Connection conn)
			throws SQLException {
		List<JecaUserDataDto> userDataList = new ArrayList<JecaUserDataDto>();
		String sql = "SELECT * FROM appointedday;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JecaUserDataDto userdata = new JecaUserDataDto();
			JecaCardDto cardInfo = new JecaCardDto(); // 名刺情報DTO
			JecaQuestionDto questionInfo = new JecaQuestionDto(); // アンケート情報DTO

			userdata.appointedday = true; // 当日入力フラグ

			// バーコード番号
			userdata.id = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.V_OVERSEA = rs.getString("V_OVERSEA"); // 海外住所フラグ
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓仮名
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名仮名

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名仮名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 所属部署名2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 所属部署名3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 所属部署名4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q3 = rs.getString("V_Q3");
			questionInfo.V_Q4 = rs.getString("V_Q4");
			questionInfo.V_Q5 = rs.getString("V_Q5");
			questionInfo.V_Q6 = rs.getString("V_Q6");
			questionInfo.V_Q7 = rs.getString("V_Q7");

			userdata.cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票状況手書き

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
	 * 全ての団体登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての団体登録データ
	 * @throws SQLException
	 */
	public static List<JecaUserDataDto> getAllGroupData(Connection conn)
			throws SQLException {
		List<JecaUserDataDto> userDataList = new ArrayList<JecaUserDataDto>();
		String sql = "SELECT * FROM groupentry;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JecaUserDataDto userdata = new JecaUserDataDto();
			JecaCardDto cardInfo = new JecaCardDto(); // 名刺情報DTO
			JecaQuestionDto questionInfo = new JecaQuestionDto(); // アンケート情報DTO

			userdata.group = true; // 団体登録フラグ

			// バーコード番号
			userdata.id = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.V_OVERSEA = rs.getString("V_OVERSEA"); // 海外住所フラグ
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名漢字

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");

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
	 * @param conn
	 *            DBサーバーへの接続情報
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
			Connection conn, List<JecaUserDataDto> userDataList, String dim,
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

		header.add(StringUtil.enquote("ID番号"));
		header.add(StringUtil.enquote("アンマッチフラグ"));
		header.add(StringUtil.enquote("原票状況不備フラグ"));
		header.add(StringUtil.enquote("原票不備詳細"));
		header.add(StringUtil.enquote("海外住所フラグ"));
		// 名刺情報
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("氏名フリガナ(カタカナ)"));
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("会社名フリガナ(カタカナ)"));
		header.add(StringUtil.enquote("所属部署名"));
		header.add(StringUtil.enquote("役職"));

		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("住所1"));
		header.add(StringUtil.enquote("住所2"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("ＦＡＸ番号"));
		header.add(StringUtil.enquote("Ｅ－ＭＡＩＬ"));

		// アンケート情報
		if (JecaConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("①業種"));
			header.add(StringUtil.enquote("①業種 ※詳細"));
			header.add(StringUtil.enquote("①業種 ※詳細 その他"));
			header.add(StringUtil.enquote("②職種"));
			header.add(StringUtil.enquote("②職種 その他"));
			header.add(StringUtil.enquote("③役種"));
			header.add(StringUtil.enquote("③役種 その他"));
			header.add(StringUtil.enquote("④年齢"));
			header.add(StringUtil.enquote("⑤来場目的"));
			header.add(StringUtil.enquote("⑤来場目的 その他"));
		}

		// アンケートシール／要望コード
		header.add(StringUtil.enquote("端末番号"));
		header.add(StringUtil.enquote("読取日"));
		header.add(StringUtil.enquote("読取時間"));
		header.add(StringUtil.enquote("アンケートシール番号1"));
		header.add(StringUtil.enquote("アンケートシール番号2"));
		for (int nIndex = 1; nIndex <= JecaConfig.REQUEST_CODE_NUM; nIndex++) {
			header.add(StringUtil.enquote("要望コード(読取" + String.valueOf(nIndex)
					+ ")"));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (JecaUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// バーコード [備忘]サブバーコードがあればそちらを出力
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.subBarcode) ? userdata.subBarcode
					: userdata.id));

			// アンマッチフラグ
			cols.add(StringUtil.enquote(userdata.unmatch ? "1" : ""));

			// 原票状況不備
			String lackFlg = getLackFlg(userdata);
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (JecaConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}

			// 海外住所フラグ
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? "1" : ""));

			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			if (JecaConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				JecaUtil util = new JecaUtil();
				if (util.isPreEntry(userdata)) { // 事前登録データ
					outputEnqueteDataForPreentry(cols, userdata);
				} else if (util.isAppEntry(userdata)) { // 当日入力データ
					outputEnqueteDataForAppointedday(cols, userdata);
				} else if (util.isGroup(userdata)) { // 団体登録データ
					outputEnqueteDataForGroup(cols, userdata);
				} else {
					outputEnqueteDataForUnmatch(cols, userdata); // アンマッチデータ
				}
			}
			// バーコードデータ
			outputBarcodeData(cols, userdata);

			// 1レコード分のデータ書き出し
			FileUtil.writer(cols, writer, dim);
		}
		try {
			if (writer != null) {
				writer.close();
			}
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
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			JecaUserDataDto userdata) {
		/*
		 * 氏名情報の間のデリミタの特定
		 */
		String delimit = "";
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1)
				&& StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2)) {
			if (StringUtil.isHalfWidthString(userdata.cardInfo.V_NAME1)
					&& StringUtil.isHalfWidthString(userdata.cardInfo.V_NAME2)) {
				delimit = " ";
			} else {
				delimit = "　";
			}
		}
		// 氏名
		cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(delimit,
				userdata.cardInfo.V_NAME1, userdata.cardInfo.V_NAME2)));
		// 氏名フリガナ(カタカナ)
		cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(delimit,
				userdata.cardInfo.V_NAMEKANA1, userdata.cardInfo.V_NAMEKANA2)));

		// 会社名
		cols.add(StringUtil
				.enquote(normalizeCompanyName(userdata.cardInfo.V_CORP)));
		// 会社名フリガナ(カタカナ)
		JecaUtil util = new JecaUtil();
		cols.add(StringUtil.enquote(util.isPreEntry(userdata) ? userdata.cardInfo.V_CORP_KANA
				: ""));

		// 所属部署名
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
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			JecaUserDataDto userdata) {
		// 郵便番号
		cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
		if (isOversea(userdata)) {
			String addr = StringUtil.concat(userdata.cardInfo.V_ADDR5,
					userdata.cardInfo.V_ADDR4, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR1);
			// 都道府県
			cols.add(StringUtil.enquote(""));
			// 住所1
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(""));
		} else {
			// 都道府県
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
			// 住所1
			String addr = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4);
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR5));
		}
		// 電話番号
		String tel = userdata.cardInfo.V_TEL;
		tel = StringUtil.replace(tel, "tel:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(tel)
				&& !StringUtil.containWildcard(tel) ? tel : ""));
		// FAX番号
		String fax = userdata.cardInfo.V_FAX;
		fax = StringUtil.replace(fax, "fax:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(fax)
				&& !StringUtil.containWildcard(fax) ? fax : ""));
		// E-mail
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.isEmailAddress(userdata.cardInfo.V_EMAIL)) { // E-mailに対する妥当性検証
			System.out.println("E-mailアドレスの構文エラー:" + userdata.id
					+ userdata.cardInfo.V_EMAIL);
		}
		cols.add(StringUtil.enquote(StringUtil
				.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? userdata.cardInfo.V_EMAIL
				: ""));
		return cols;

	}

	/**
	 * 【事前登録】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForPreentry(List<String> cols,
			JecaUserDataDto userdata) {

		JecaQuestionDto questionDto = (JecaQuestionDto) userdata.questionInfo;

		// ①業種
		cols.add(StringUtil.enquote(getBizCategoryForPreRegist(userdata,
				questionDto.V_Q1)));
		// ①業種 ※詳細
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA)) {
			cols.add(StringUtil
					.enquote(JecaConstants.BIZ_ENG_CATEGORIES_DETAIL_MAP
							.get(questionDto.V_Q1)));
		} else {
			cols.add(StringUtil
					.enquote(JecaConstants.BIZ_JPN_CATEGORIES_DETAIL_MAP
							.get(questionDto.V_Q1)));
		}
		// ①業種 ※詳細 その他
		cols.add(StringUtil.enquote(questionDto.V_Q1_other));
		// ②職種
		cols.add(StringUtil.enquote(questionDto.V_Q2));
		// ②職種 その他
		cols.add(StringUtil.enquote(questionDto.V_Q2_other));
		// ③役職
		cols.add(StringUtil.enquote(questionDto.V_Q3));
		// ③役職 その他
		cols.add(StringUtil.enquote(questionDto.V_Q3_other));
		// ④年齢
		cols.add(StringUtil.enquote(questionDto.V_Q4));
		// ⑤来場目的
		cols.add(StringUtil.enquote(questionDto.V_Q5));
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(questionDto.V_Q5_other));

		return cols;
	}

	/**
	 * 【当日入力】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForAppointedday(
			List<String> cols, JecaUserDataDto userdata) {

		JecaQuestionDto questionDto = (JecaQuestionDto) userdata.questionInfo;

		// ①業種
		String q1 = questionDto.V_Q1;
		if (StringUtil.isNotEmpty(q1)) {
			q1 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q1);
		}
		cols.add(StringUtil.enquote(JecaConstants.BIZ_JPN_CATEGORIES_MAP
				.get(q1)));
		// ①業種 ※詳細
		cols.add(StringUtil.enquote(""));
		// ①業種 ※詳細 その他
		cols.add(StringUtil.enquote(""));
		// ②職種
		String q2 = questionDto.V_Q2;
		if (StringUtil.isNotEmpty(q2)) {
			q2 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q2);
			if (StringUtil.isNotEmpty(q2) && q2.length() == 1) {
				q2 = "0" + q2;
			}
		}
		cols.add(StringUtil.enquote(q2));
		// ②職種 その他
		cols.add(StringUtil.enquote(""));
		// ③役職
		String q3 = questionDto.V_Q3;
		if (StringUtil.isNotEmpty(q3)) {
			q3 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q3);
			if (StringUtil.isNotEmpty(q3) && q3.length() == 1) {
				q3 = "0" + q3;
			}
		}
		cols.add(StringUtil.enquote(q3));
		// ③役職 その他
		cols.add(StringUtil.enquote(""));
		// ④年齢
		String q4 = questionDto.V_Q4;
		if (StringUtil.isNotEmpty(q4)) {
			q4 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q4);
			if (StringUtil.isNotEmpty(q4) && q4.length() == 1) {
				q4 = "0" + q4;
			}
		}
		cols.add(StringUtil.enquote(q4));
		// ⑤来場目的
		if (StringUtil.isNotEmpty(questionDto.V_Q5)) {
			String q5Buff[] = questionDto.V_Q5.split(" ");
			List<String> q5ValueList = new LinkedList<String>();
			for (String value : q5Buff) {
				if (StringUtil.isNotEmpty(value) && value.length() == 1) {
					value = "0" + value;
				}
				q5ValueList.add(value);
			}
			cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(",",
					q5ValueList)));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(""));

		return cols;
	}

	/**
	 * 【団体登録】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForGroup(List<String> cols,
			JecaUserDataDto userdata) {

		JecaQuestionDto questionDto = (JecaQuestionDto) userdata.questionInfo;

		// ①業種
		cols.add(StringUtil.enquote(JecaConstants.BIZ_CATEGORIES_MAP_FOR_GROUP
				.get(questionDto.V_Q1)));
		// ①業種 ※詳細
		cols.add(StringUtil.enquote(""));
		// ①業種 ※詳細 その他
		cols.add(StringUtil.enquote(""));
		// ②職種
		cols.add(StringUtil.enquote(""));
		// ②職種 その他
		cols.add(StringUtil.enquote(""));
		// ③役職
		cols.add(StringUtil.enquote(""));
		// ③役職 その他
		cols.add(StringUtil.enquote(""));
		// ④年齢
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(""));

		return cols;
	}

	/**
	 * 【アンマッチ】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForUnmatch(List<String> cols,
			JecaUserDataDto userdata) {

		// ①業種
		cols.add(StringUtil.enquote(""));
		// ①業種 ※詳細
		cols.add(StringUtil.enquote(""));
		// ①業種 ※詳細 その他
		cols.add(StringUtil.enquote(""));
		// ②職種
		cols.add(StringUtil.enquote(""));
		// ②職種 その他
		cols.add(StringUtil.enquote(""));
		// ③役職
		cols.add(StringUtil.enquote(""));
		// ③役職 その他
		cols.add(StringUtil.enquote(""));
		// ④年齢
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(""));

		return cols;
	}

	/**
	 * バーコードデータの出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return バーコードデータを出力後の出力バッファ
	 */
	private static List<String> outputBarcodeData(List<String> cols,
			JecaUserDataDto userdata) {

		// バーコードリーダー番号
		cols.add(StringUtil.enquote(userdata.reader));
		String timestamp = userdata.timeByRfid;
		// バーコード読取日
		cols.add(StringUtil.enquote(timestamp.substring(0, 8)));
		// バーコード時間
		cols.add(StringUtil.enquote(timestamp.substring(8)));
		// アンケートシール番号
		if (userdata.enqueteCode.size() == 0) {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		} else if (userdata.enqueteCode.size() == 1) {
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(0)));
			cols.add(StringUtil.enquote(""));
		} else if (userdata.enqueteCode.size() == 2) {
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(0)));
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(1)));
		} else {
			System.out.println("アンケートシールが2個以上存在");
		}
		// 要望コード
		List<String> requestCodes = userdata.requestCode;
		for (String code : requestCodes) {
			cols.add(StringUtil.enquote(code));
		}
		if (JecaConfig.REQUEST_CODE_NUM < requestCodes.size()) {
			System.out.println("リクエストコード数が上限に達しました");
		} else {
			for (int nIndex = 0; nIndex < JecaConfig.REQUEST_CODE_NUM
					- requestCodes.size(); nIndex++) {
				cols.add(StringUtil.enquote(""));
			}
		}
		return cols;
	}

	/**
	 * 事前登録ユーザーの業種の特定
	 *
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @param q1
	 *            Q1の回答
	 * @return 事前登録ユーザーの業種名
	 */
	private static String getBizCategoryForPreRegist(JecaUserDataDto userdata,
			String q1) {
		if (StringUtil.isNotEmpty(q1)) {
			if (q1.startsWith("0")) {
				q1 = q1.substring(1);
			}
			int nQ1 = Integer.parseInt(q1);
			if (1 <= nQ1 && nQ1 <= 14) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("1") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("1");
			} else if (15 <= nQ1 && nQ1 <= 21) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("2") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("2");
			} else if (22 <= nQ1 && nQ1 <= 25) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("3") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("3");
			} else if (26 <= nQ1 && nQ1 <= 29) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("4") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("4");
			} else if (30 <= nQ1 && nQ1 <= 36) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("5") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("5");
			} else if (37 <= nQ1 && nQ1 <= 39) {
				return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? JecaConstants.BIZ_ENG_CATEGORIES_MAP
						.get("6") : JecaConstants.BIZ_JPN_CATEGORIES_MAP
						.get("6");
			} else {
				System.out.println("業種カテゴリの特定に失敗しました");
			}
		}
		return null;
	}

	/**
	 * 不備フラグの特定
	 *
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(JecaUserDataDto userdata) {
		JecaValidator validator = new JecaValidator("■");
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
	 * 会社名の正規化
	 *
	 * @param value
	 *            会社名
	 * @return 正規化後の会社名
	 */
	private static String normalizeCompanyName(String value) {
		if (StringUtil.isNotEmpty(value)) {
			// 略称表記の括弧を全角に変換
			Set<Entry<String, String>> entrySet1 = JecaConstants.CONVERT_COMPANY_SHORTNAME1
					.entrySet();
			for (Entry<String, String> entry : entrySet1) {
				value = StringUtil.replace(value, entry.getKey(),
						entry.getValue());
			}
		}
		if (StringUtil.isNotEmpty(value)) {
			// 略称表記の展開
			Set<Entry<String, String>> entrySet2 = JecaConstants.CONVERT_COMPANY_SHORTNAME2
					.entrySet();
			for (Entry<String, String> entry : entrySet2) {
				value = StringUtil.replace(value, entry.getKey(),
						entry.getValue());
			}
		}

		return value;
	}

	/**
	 * 郵便番号の正規化
	 *
	 * @param zip
	 *            郵便番号
	 * @return 正規化後の郵便番号
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

}