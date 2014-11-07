package jp.co.freedom.master.utilities.noma;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.noma.NomaCardDto;
import jp.co.freedom.master.dto.noma.NomaQuestionDto;
import jp.co.freedom.master.dto.noma.NomaUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * NOMA 出展者納品データ作成
 *
 * @author フリーダム・グループ
 *
 */
public class NomaExhibitorUtil extends Util {

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<NomaUserDataDto> createInstance(List<String[]> csvData) {
		List<NomaUserDataDto> userData = new ArrayList<NomaUserDataDto>();// ユーザーデータを保持するリスト
		NomaUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			if (row.length == 6 && row[1].startsWith("000")) {// リクエストコード
				if (dataDto == null) {
					dataDto = new NomaUserDataDto();
				}
				// リクエストコードの格納
				dataDto.requestCode.add(row[1]);
			} else if (row.length == 6 && !row[1].startsWith("000")) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new NomaUserDataDto();
				dataDto.filename = row[0]; // CSVファイル名
				dataDto.reader = row[4];// バーコードリーダーID
				dataDto.id = row[1];
				dataDto.timeByRfid = row[2] + row[3];
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
	 * 変換表のインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @return ユーザデータのインスタンスを格納するリスト
	 */
	public static List<NomaUserDataDto> createInstanceForConvertTable(
			List<String[]> csvData) {
		List<NomaUserDataDto> userData = new ArrayList<NomaUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			NomaUserDataDto dataDto = new NomaUserDataDto();
			if (row.length == 2) {
				String barcode = row[0];
				dataDto.id = barcode;
				dataDto.cardInfo.V_CID = row[1];
				userData.add(dataDto);
			} else {
				System.out.println("変換表データ中に2列ではない行を発見");
			}
		}
		return userData;
	}

	/**
	 * 全ての当日登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<NomaUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<NomaUserDataDto> userDataList = new ArrayList<NomaUserDataDto>();
		String sql = "SELECT * FROM appointedday;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			NomaUserDataDto userdata = new NomaUserDataDto();
			NomaCardDto cardInfo = new NomaCardDto(); // 名刺情報DTO
			NomaQuestionDto questionInfo = new NomaQuestionDto(); // アンケート情報DTO

			userdata.appointedday = true;

			/* バーコード番号 */
			userdata.id = rs.getString("V_VID");
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			// cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名フリガナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 部署1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 部署2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 部署3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 部署4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4
			// cardInfo.V_TITLE = rs.getString("V_PREFIX"); // 敬称
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓フリガナ
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名フリガナ
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // Email
			// cardInfo.V_URL = rs.getString("V_WEB"); // Web
			// cardInfo.SEND_FLG = rs.getString("V_SEND"); // 送付先
			// cardInfo.V_COUNTRY = rs.getString("V_COUNTRY"); // 国名
			// if (!"Japan".equals(cardInfo.V_COUNTRY)) {
			// cardInfo.V_OVERSEA = "1";
			// }
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 住所1
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 住所2
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 住所3
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号

			cardInfo.V_TICKET_HAND = rs.getString("V_HAND"); // 原票手書きフラグ
			cardInfo.V_OVERSEA = rs.getString("V_OVERSEA"); // 海外住所フラグ
			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス
			cardInfo.V_DAY = rs.getString("V_form");

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q2_A = rs.getString("V_Q3");
			questionInfo.V_Q3 = rs.getString("V_Q4");

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
	 * 全ての事前登録データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param mode
	 *            処理モード
	 * @return 全ての事前登録データ
	 * @throws SQLException
	 */
	public static List<NomaUserDataDto> getAllPreRegistData(Connection conn,
			String mode) throws SQLException {
		List<NomaUserDataDto> userDataList = new ArrayList<NomaUserDataDto>();
		String sql;
		if ("all".equals(mode)) {
			sql = "SELECT * FROM preentry;";
		} else if ("visitor".equals(mode)) {
			sql = "SELECT * FROM preentry where V_VISITOR_FLG = '1';";
		} else {
			sql = "SELECT * FROM preentry where V_VISITOR_FLG IS NULL;";
		}
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			NomaUserDataDto userdata = new NomaUserDataDto();
			NomaCardDto cardInfo = new NomaCardDto(); // 名刺情報DTO
			NomaQuestionDto questionInfo = new NomaQuestionDto(); // アンケート情報DTO

			userdata.preentry = true;

			// バーコード番号
			userdata.id = rs.getString("V_VID");
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名フリガナ
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 部署
			cardInfo.V_BIZ1 = rs.getString("V_POST"); // 役職
			// cardInfo.V_TITLE = rs.getString("V_PREFIX"); // 敬称
			cardInfo.V_NAME1 = rs.getString("V_NAME"); // 氏名姓漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA"); // 氏名姓フリガナ
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // Email
			// cardInfo.V_URL = rs.getString("V_WEB"); // Web
			// cardInfo.SEND_FLG = rs.getString("V_SEND"); // 送付先
			// cardInfo.V_COUNTRY = rs.getString("V_COUNTRY"); // 国名
			// if (!"Japan".equals(cardInfo.V_COUNTRY)) {
			// cardInfo.V_OVERSEA = "1";
			// }
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 住所1
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 住所2
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 住所3
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q2_A = rs.getString("V_Q2A");
			questionInfo.V_Q2_B = rs.getString("V_Q2B");
			questionInfo.V_Q3 = rs.getString("V_Q4");

			cardInfo.V_OVERSEA = "48".equals(rs.getString("V_OVERSEA")) ? "1"
					: ""; // 海外住所フラグ

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
	 * @param conn
	 *            DBサーバー接続情報
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
	 * @param obverseVisitedDay
	 *            来場日の補正フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, Connection conn,
			String outputFileName, List<NomaUserDataDto> userDataList,
			String dim, boolean isPreMaster, boolean isAppMaster,
			boolean obverseVisitedDay) throws ServletException, IOException,
			SQLException {

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

		header.add(StringUtil.enquote("ファイル名"));
		/* バーコード情報 */
		header.add(StringUtil.enquote("リーダー番号"));
		header.add(StringUtil.enquote("バーコード番号"));
		header.add(StringUtil.enquote("来場日"));

		/* 各種フラグ */
		header.add(StringUtil.enquote("原票状況不備"));
		if (NomaConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
			header.add(StringUtil.enquote("不備詳細"));
		}
		header.add(StringUtil.enquote("海外住所フラグ"));

		/* 名刺情報 */
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("部署"));
		header.add(StringUtil.enquote("役職"));
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("住所"));
		header.add(StringUtil.enquote("ビル名"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));
		header.add(StringUtil.enquote("Emailアドレス"));

		/* アンケート情報 */
		header.add(StringUtil.enquote("Q1.勤務地"));
		header.add(StringUtil.enquote("Q2.勤務先"));
		header.add(StringUtil.enquote("Q2-A.所属"));
		header.add(StringUtil.enquote("Q2-B.部門"));
		header.add(StringUtil.enquote("Q3.DM・メルマガ希望"));
		header.add(StringUtil.enquote("リクエストコード"));

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (NomaUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// CSVファイル名
			cols.add(StringUtil.enquote(userdata.filename));
			// リーダー番号
			cols.add(StringUtil.enquote(userdata.reader));
			// バーコード番号
			cols.add(StringUtil.enquote(userdata.id));
			// 来場日時
			if (StringUtil.isNotEmpty(userdata.timeByRfid)) {
				cols.add(StringUtil
						.enquote(normalizedDateStrForExhibitorTimestamp(userdata.timeByRfid)));
			} else if (obverseVisitedDay
					&& StringUtil
							.isNotEmpty(((NomaCardDto) userdata.cardInfo).V_DAY)) {
				cols.add(StringUtil
						.enquote(((NomaCardDto) userdata.cardInfo).V_DAY));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// 原票状況不備フラグ
			String lackFlg = getLackFlg(userdata);
			// 原票状況不備
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (NomaConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}
			// 海外フラグ
			// cols.add(StringUtil.enquote(userdata.cardInfo.V_OVERSEA));
			cols.add(StringUtil.enquote(NomaUtil.isOversea(userdata) ? "1" : ""));
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
			// 氏名
			String name = StringUtil.concat(userdata.cardInfo.V_NAME1,
					userdata.cardInfo.V_NAME2);
			cols.add(StringUtil.enquote(name));

			// 郵便番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			// 住所1(都道府県)
			cols.add(StringUtil.enquote(StringUtil
					.convertHalfWidthNumberAndAlphabetAndHyphenhyphenate(userdata.cardInfo.V_ADDR1)));
			// 住所2(市区町村番地)
			String add2 = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4);
			cols.add(StringUtil.enquote(StringUtil
					.convertHalfWidthNumberAndAlphabetAndHyphenhyphenate(add2)));
			// 住所3(ビル名)
			cols.add(StringUtil.enquote(StringUtil
					.convertHalfWidthNumberAndAlphabetAndHyphenhyphenate(userdata.cardInfo.V_ADDR5)));

			// 電話番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_TEL));
			// Fax
			cols.add(StringUtil.enquote(userdata.cardInfo.V_FAX));
			// Emailアドレス
			cols.add(StringUtil.enquote(userdata.cardInfo.V_EMAIL));

			// アンケート項目
			NomaUtil util = new NomaUtil();
			if (util.isPreEntry(userdata)) {
				outputExhibitorEnqueteDataForPreentry(cols, userdata);
			} else {
				outputExhibitorputEnqueteDataForAppointedday(cols, userdata);
			}
			// リクエストコード
			outputRequestCodes(cols, userdata);

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
			List<NomaUserDataDto> userDataList, String dim)
			throws ServletException, IOException, SQLException {

		// CSVの列数の決定
		int columnNum = 3;

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
		header[++headerNum] = StringUtil.enquote("バーコード番号");
		header[++headerNum] = StringUtil.enquote("リーダー番号");
		header[++headerNum] = StringUtil.enquote("来場日");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (NomaUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			// 来場日
			String time = normalizedDateStrForExhibitorTimestamp(userdata.timeByRfid);
			cols[++nColumn] = StringUtil.isNotEmpty(time) ? StringUtil
					.enquote(time) : StringUtil.enquote("");

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
	 * リクエストコードに対応する値を取得
	 *
	 * @param reader
	 *            バーコードリーダーID
	 * @param code
	 *            リクエストコード
	 * @return リクエストコードに対応する値
	 */
	public static String getRequestValue(String reader, String code) {
		assert StringUtil.isNotEmpty(reader);
		String value = null;
		if (StringUtil.isNotEmpty(code)) {
			if (NomaRequestCodes.READER_GREEN.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_GREEN.get(code);
			} else if (NomaRequestCodes.READER_REKISEN.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_REKISEN.get(code);
			} else if (NomaRequestCodes.READER_OHSAKI.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_OHSAKI.get(code);
			} else if (NomaRequestCodes.READER_EIKOU.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_EIKOU.get(code);
			} else if (NomaRequestCodes.READER_IYOU.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_IYOU.get(code);
			} else if (NomaRequestCodes.READER_WACOM.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_WACOM.get(code);
			} else if (NomaRequestCodes.READER_MAR.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_MAR.get(code);
			} else if (NomaRequestCodes.READER_HITACHI.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_HITACHI.get(code);
			} else if (NomaRequestCodes.READER_KANAMIC.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_KANAMIC.get(code);
			} else if (NomaRequestCodes.READER_INFOCOM.contains(reader)) {
				value = NomaRequestCodes.REQUESTCODES_INFOCOM.get(code);
			} else {
				if (code.startsWith("000")) { // 6桁⇒3桁
					code = code.substring(3, code.length());
				}
				value = code; // 対応表が存在しない場合はリクエストコードをそのまま返却
			}
			if (StringUtil.isEmpty(value)) {
				if (code.startsWith("000")) { // 6桁⇒3桁
					code = code.substring(3, code.length());
				}
				value = code; // 対応表にリクエストコードが掲載されていない場合はリクエストコードをそのまま返却
			}
		}
		return value;
	}

	/**
	 * 【バーコードマッチング高速化対応】当日登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>NomaUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, NomaUserDataDto> getAppointedDayMap(
			List<NomaUserDataDto> userDataList) {
		Map<String, NomaUserDataDto> map = new HashMap<String, NomaUserDataDto>();
		for (NomaUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 【バーコードマッチング高速化対応】事前登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>NomaUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, NomaUserDataDto> getPreEntryMap(
			List<NomaUserDataDto> userDataList) {
		Map<String, NomaUserDataDto> map = new HashMap<String, NomaUserDataDto>();
		for (NomaUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * バーコードデータより来場日を特定する
	 *
	 * @param timestamp
	 *            バーコードデータ中のタイムスタンプ
	 * @return 来場日
	 */
	public static String normalizedDateStrForExhibitorTimestamp(String timestamp) {
		if (StringUtil.isNotEmpty(timestamp) && timestamp.length() == 14) {
			StringBuffer sb = new StringBuffer();
			sb.append(timestamp.substring(4, 8));
			return sb.toString();
		}
		return null;
	}

	/**
	 * 【事前登録】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>NomaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputExhibitorEnqueteDataForPreentry(
			List<String> cols, NomaUserDataDto userdata) {

		// Q1.勤務地
		String q1 = userdata.questionInfo.V_Q1;
		String place = null;
		if (StringUtil.isNotEmpty(q1)) {
			place = NomaConstants.BIZ_PLACES.get(q1);
		}
		cols.add(StringUtil.enquote(place));
		// Q2.勤務先
		String q2 = userdata.questionInfo.V_Q2;
		String biz = null;
		if (StringUtil.isNotEmpty(q2)) {
			biz = NomaConstants.BIZ_CATEGORIES_JPN.get(q2);
		}
		cols.add(StringUtil.enquote(biz));
		// Q2-A.所属A
		String q2a = ((NomaQuestionDto) userdata.questionInfo).V_Q2_A;
		String deptA = null;
		if (StringUtil.isNotEmpty(q2a)) {
			deptA = NomaConstants.DEPT_CATEGORIES_A_JPN.get(q2a);
		}
		cols.add(StringUtil.enquote(deptA));
		// Q2-B.所属B
		String q2b = ((NomaQuestionDto) userdata.questionInfo).V_Q2_B;
		String deptB = null;
		if (StringUtil.isNotEmpty(q2b)) {
			deptB = NomaConstants.DEPT_CATEGORIES_B.get(q2b);
		}
		cols.add(StringUtil.enquote(deptB));
		// Q3.DMメルマガ希望
		if ("1".equals(userdata.questionInfo.V_Q3)) {
			cols.add(StringUtil.enquote("はい"));
		} else if ("2".equals(userdata.questionInfo.V_Q3)) {
			cols.add(StringUtil.enquote("いいえ"));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		return cols;
	}

	/**
	 * 【当日登録】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>NomaUserDataDto</b>
	 * @param slxMaster
	 *            SLXコードマスター
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputExhibitorputEnqueteDataForAppointedday(
			List<String> cols, NomaUserDataDto userdata) {

		// Q1.勤務地
		String q1 = EnqueteUtil.convertSingleAnswer(userdata.questionInfo.V_Q1);
		String place = null;
		if (StringUtil.isNotEmpty(q1)) {
			place = NomaConstants.BIZ_PLACES.get(q1);
		}
		cols.add(StringUtil.enquote(place));
		String q2a = EnqueteUtil
				.convertSingleAnswer(((NomaQuestionDto) userdata.questionInfo).V_Q2_A);
		// Q2.勤務先
		String q2;
		if (StringUtil.isNotEmpty(q2a) && !isEnglishData(userdata)) {
			q2 = "1";
		} else {
			q2 = EnqueteUtil.convertSingleAnswer(userdata.questionInfo.V_Q2);
		}
		String biz = null;
		if (StringUtil.isNotEmpty(q2)) {
			if (isEnglishData(userdata)) {
				biz = NomaConstants.BIZ_CATEGORIES_EN.get(q2);
			} else {
				biz = NomaConstants.BIZ_CATEGORIES_JPN.get(q2);
			}
		}
		cols.add(StringUtil.enquote(biz));
		// Q2-A.所属A
		String deptA = null;
		if (StringUtil.isNotEmpty(q2a)) {
			if (isEnglishData(userdata)) {
				deptA = NomaConstants.DEPT_CATEGORIES_A_EN.get(q2a);
			} else {
				deptA = NomaConstants.DEPT_CATEGORIES_A_JPN.get(q2a);
			}
		}
		cols.add(StringUtil.enquote(deptA));
		// Q2-B.所属B
		cols.add(StringUtil.enquote(""));
		// Q3.DMメルマガ希望
		if (StringUtil.isNotEmpty(userdata.questionInfo.V_Q3)) {
			cols.add(StringUtil.enquote("はい"));
		} else {
			// 原票状況不備フラグ
			String lackFlg = getLackFlg(userdata);
			if ("2".equals(lackFlg)
					&& "氏名,連絡先".equals(userdata.validationErrResult)) {
				cols.add(StringUtil.enquote(""));
			} else {
				cols.add(StringUtil.enquote("いいえ"));
			}
		}
		return cols;
	}

	/**
	 * リクエストコードの出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>NomaUserDataDto</b>
	 * @return リクエストコード出力後の出力バッファ
	 */
	private static List<String> outputRequestCodes(List<String> cols,
			NomaUserDataDto userdata) {
		List<String> requestCodes = userdata.requestCode;
		/*
		 * キャンセルコードを加味した正規化
		 */
		List<String> normalizedRequestCodes = new LinkedList<String>();
		for (String code : requestCodes) {
			if ("000999".equals(code)) {
				normalizedRequestCodes = new LinkedList<String>();
			} else if ("000040".equals(code)
					&& StringUtil.isNotEmpty(userdata.filename)
					&& userdata.filename.startsWith("hitachi")
					&& normalizedRequestCodes.size() != 0) {
				normalizedRequestCodes
						.remove(normalizedRequestCodes.size() - 1);
			} else {
				normalizedRequestCodes.add(code);
			}
		}
		/*
		 * リクエストコード表による変換
		 */
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 0; nIndex < normalizedRequestCodes.size(); nIndex++) {
			sb.append(getRequestValue(userdata.reader,
					normalizedRequestCodes.get(nIndex)));
			if (nIndex != normalizedRequestCodes.size() - 1) {
				sb.append(",");
			}
		}
		cols.add(StringUtil.isNotEmpty(sb.toString()) ? StringUtil.enquote(sb
				.toString()) : StringUtil.enquote(""));
		return cols;
	}

	/**
	 * 英語データであるか否かの判定
	 *
	 * @param userdata
	 *            <b>NomaUserDataDto</b>
	 * @return 判定結果のブール値
	 */
	private static boolean isEnglishData(NomaUserDataDto userdata) {
		return userdata.cardInfo.V_IMAGE_PATH.startsWith("X");
	}

	/**
	 * 不備フラグの特定
	 *
	 * @param userdata
	 *            <b>NomaUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(NomaUserDataDto userdata) {
		NomaValidator validator = new NomaValidator("■");
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

	@Override
	public boolean isPreEntry(UserDataDto userdata) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean isAppEntry(UserDataDto userdata) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}