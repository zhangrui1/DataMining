package jp.co.freedom.master.utilities.catv;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import jp.co.freedom.master.dto.catv.CatvCardDto;
import jp.co.freedom.master.dto.catv.CatvQuestionDto;
import jp.co.freedom.master.dto.catv.CatvSeminarDto;
import jp.co.freedom.master.dto.catv.CatvUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * Catv向けユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class CatvUtil extends Util {

	@Override
	public boolean isPreEntry(UserDataDto userdata) {
		if (userdata instanceof CatvUserDataDto) {
			CatvUserDataDto info = (CatvUserDataDto) userdata;
			return info.preentry;
		}
		return false;
	}

	@Override
	public boolean isAppEntry(UserDataDto userdata) {
		if (userdata instanceof CatvUserDataDto) {
			CatvUserDataDto info = (CatvUserDataDto) userdata;
			return info.appointedday;
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
		return id.startsWith(CatvConfig.PREENTRY_BARCODE_START_BIT);
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
		return id.startsWith(CatvConfig.APPOINTEDDAY_BARCODE_START_BIT);
	}

	/**
	 * 海外住所フラグの検証
	 *
	 * @param cardInfo
	 *            <b>CatvCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(CatvUserDataDto userdata) {
		CatvUtil util = new CatvUtil();
		boolean result;
		if (util.isPreEntry(userdata)) { // 事前登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else if (util.isAppEntry(userdata)) { // 当日登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else {
			// System.out.println("海外住所フラグの特定に失敗しました:" + userdata.id);
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
	public static List<CatvUserDataDto> createInstance(List<String[]> csvData) {
		List<CatvUserDataDto> userData = new ArrayList<CatvUserDataDto>();// ユーザーデータを保持するリスト
		CatvUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			if (row.length == 4) {//
				if (dataDto != null) {
					userData.add(dataDto);
				}
				dataDto = new CatvUserDataDto();
				dataDto.id = row[0]; // バーコード番号
				dataDto.timeByRfid = row[1]; // バーコード読取日時
				dataDto.imagePath = row[2]; // 画像ファイルパス
				dataDto.count = row[3]; // カウンタ値
			} else {
				// 読み飛ばし
				continue;
			}
		}
		return userData;
	}

	/**
	 * 【バーコードマッチング高速化対応】DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>CatvUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, CatvUserDataDto> getMap(
			List<CatvUserDataDto> userDataList) {
		Map<String, CatvUserDataDto> map = new HashMap<String, CatvUserDataDto>();
		for (CatvUserDataDto userData : userDataList) {
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
	public static List<CatvUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<CatvUserDataDto> userDataList = new ArrayList<CatvUserDataDto>();
		String sql = "SELECT * FROM preentry;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			CatvUserDataDto userdata = new CatvUserDataDto();
			CatvCardDto cardInfo = new CatvCardDto(); // 名刺情報DTO
			CatvQuestionDto questionInfo = new CatvQuestionDto(); // アンケート情報DTO

			userdata.preentry = true; // 事前登録フラグ

			/* 名刺情報 */
			String id = rs.getString("V_VID"); // バーコード
			userdata.id = id;
			cardInfo.V_VID = id;

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名仮名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT"); // 所属部署名
			cardInfo.V_BIZ1 = rs.getString("V_POST"); // 役職

			cardInfo.V_NAME1 = rs.getString("V_NAME"); // 氏名姓漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA"); // 氏名姓仮名

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("_V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 以下住所
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("_V_Q1"); // 年齢
			questionInfo.V_Q2 = rs.getString("_V_Q2"); // 業種
			questionInfo.V_Q3 = rs.getString("_V_Q3"); // 職種
			questionInfo.V_Q4 = rs.getString("_V_Q4"); // 役職

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
	public static List<CatvUserDataDto> getAllAppointeddayData(Connection conn)
			throws SQLException {
		List<CatvUserDataDto> userDataList = new ArrayList<CatvUserDataDto>();
		String sql = "SELECT * FROM appointedday;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			CatvUserDataDto userdata = new CatvUserDataDto();
			CatvCardDto cardInfo = new CatvCardDto(); // 名刺情報DTO
			CatvQuestionDto questionInfo = new CatvQuestionDto(); // アンケート情報DTO

			userdata.appointedday = true; // 当日登録フラグ

			/* 名刺情報 */
			String id = rs.getString("V_VID"); // バーコード
			userdata.id = id;
			cardInfo.V_VID = id;

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名仮名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 所属部署名2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 所属部署名3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 所属部署名4
			cardInfo.V_BIZ1 = rs.getString("V_POST1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_POST2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_POST3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_POST4"); // 役職4

			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓仮名
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名仮名

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("_V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 以下住所
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("_V_Q1"); // 年齢
			questionInfo.V_Q2 = rs.getString("_V_Q2"); // 業種
			questionInfo.V_Q3 = rs.getString("_V_Q3"); // 職種
			questionInfo.V_Q4 = rs.getString("_V_Q4"); // 役職

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
			Connection conn, List<CatvUserDataDto> userDataList, String dim,
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

		header.add(StringUtil.enquote("バーコード"));
		header.add(StringUtil.enquote("原票状況不備フラグ"));
		header.add(StringUtil.enquote("原票不備詳細"));
		header.add(StringUtil.enquote("セミナー番号"));
		header.add(StringUtil.enquote("読取時間"));
		header.add(StringUtil.enquote("画像パス"));
		// 名刺情報
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("氏名フリガナ"));
		header.add(StringUtil.enquote("会社名"));
		header.add(StringUtil.enquote("会社名フリガナ"));
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
		if (CatvConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("年齢"));
			header.add(StringUtil.enquote("業種"));
			header.add(StringUtil.enquote("職種"));
			header.add(StringUtil.enquote("役種"));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (CatvUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// バーコード
			cols.add(StringUtil.enquote(userdata.id));

			// 原票状況不備
			String lackFlg = getLackFlg(userdata);
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (CatvConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}
			// セミナー番号
			outputSeminarNum(cols, userdata);
			// [デバッグ用] 読取時間
			cols.add(StringUtil.enquote(userdata.timeByRfid));
			// [デバッグ用] 画像パス
			cols.add(StringUtil.enquote(userdata.imagePath));
			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			if (CatvConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				CatvUtil util = new CatvUtil();
				if (util.isPreEntry(userdata)) { // 事前登録データ
					outputEnqueteDataForPreentry(cols, userdata);
				} else if (util.isAppEntry(userdata)) { // 当日登録データ
					outputEnqueteDataForAppointedday(cols, userdata);
				} else {
					outputEnqueteDataForUnmatch(cols, userdata); // アンマッチデータ
				}
			}

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
	 * セミナー番号の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>CatvUserDataDto</b>
	 * @return セミナー番号を出力後の出力バッファ
	 */
	private static List<String> outputSeminarNum(List<String> cols,
			CatvUserDataDto userdata) {
		if (StringUtil.isNotEmpty(userdata.imagePath)) {
			String[] buff = userdata.imagePath.split("_");
			String day = buff[0];
			String hole = buff[1];
			Date date = convert(userdata.timeByRfid); // バーコード読取日時
			String seminarNum = null;
			if ("29".equals(day)) {
				for (CatvSeminarDto seminarInfo : CatvConstants.SEMINAR_29_INFO) {
					if (hole.equals(seminarInfo.hole)) {
						boolean startCheck = date.compareTo(seminarInfo.start) >= 0;
						boolean endCheck = seminarInfo.end.compareTo(date) >= 0;
						if (startCheck && endCheck) {
							seminarNum = seminarInfo.seminar;
							break;
						}
					}
				}
			} else {
				for (CatvSeminarDto seminarInfo : CatvConstants.SEMINAR_30_INFO) {
					if (hole.equals(seminarInfo.hole)) {
						boolean startCheck = date.compareTo(seminarInfo.start) >= 0;
						boolean endCheck = seminarInfo.end.compareTo(date) >= 0;
						if (startCheck && endCheck) {
							seminarNum = seminarInfo.seminar;
							break;
						}
					}
				}
			}
			cols.add(StringUtil.enquote(seminarNum));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		return cols;
	}

	/**
	 * 日時文字列を<b>Date</b>型に変換
	 *
	 * @param time
	 *            日時文字列
	 * @return <b>Date</b>オブジェクト
	 */
	public static Date convert(String time) {
		if (StringUtil.isNotEmpty(time)) {
			Calendar cal = Calendar.getInstance();
			String date = time.substring(7, 9);
			String tmpBuff[] = time.split(" ");
			String tmpBuff2[] = tmpBuff[1].split(":");
			String hourOfDay = tmpBuff2[0];
			String minute = tmpBuff2[1];
			cal.set(2014, 6, Integer.valueOf(date), Integer.valueOf(hourOfDay),
					Integer.valueOf(minute), 0);
			return cal.getTime();
		}
		return null;
	}

	/**
	 * 氏名および住所情報項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>CatvUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			CatvUserDataDto userdata) {
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
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));
		// 会社名フリガナ(カタカナ)
		CatvUtil util = new CatvUtil();
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
	 *            <b>CatvUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			CatvUserDataDto userdata) {
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
	 *            <b>CatvUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForPreentry(List<String> cols,
			CatvUserDataDto userdata) {

		CatvQuestionDto questionDto = (CatvQuestionDto) userdata.questionInfo;

		// 年齢
		cols.add(StringUtil.enquote(questionDto.V_Q1));
		// 業種
		cols.add(StringUtil.enquote(questionDto.V_Q2));
		// 職種
		cols.add(StringUtil.enquote(questionDto.V_Q3));
		// 役職
		cols.add(StringUtil.enquote(questionDto.V_Q4));

		return cols;
	}

	/**
	 * 【当日登録】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>CatvUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForAppointedday(
			List<String> cols, CatvUserDataDto userdata) {

		CatvQuestionDto questionDto = (CatvQuestionDto) userdata.questionInfo;

		// 年齢
		String q1 = questionDto.V_Q1;
		if (StringUtil.isNotEmpty(q1)) {
			q1 = EnqueteUtil.convertSingleAnswer(q1);
		}
		cols.add(StringUtil.enquote(CatvConstants.AGE_CATEGORIES_MAP.get(q1)));

		// 業種
		String q2 = questionDto.V_Q2;
		if (StringUtil.isNotEmpty(q2)) {
			q2 = EnqueteUtil.convertSingleAnswer(q2);
		}
		cols.add(StringUtil.enquote(CatvConstants.DEPT_CATEGORIES_MAP.get(q2)));

		// 職種
		String q3 = questionDto.V_Q3;
		if (StringUtil.isNotEmpty(q3)) {
			q3 = EnqueteUtil.convertSingleAnswer(q3);
		}
		cols.add(StringUtil.enquote(CatvConstants.BIZ_CATEGORIES_MAP.get(q3)));

		// 役職
		String q4 = questionDto.V_Q4;
		if (StringUtil.isNotEmpty(q4)) {
			q4 = EnqueteUtil.convertSingleAnswer(q4);
		}
		cols.add(StringUtil.enquote(CatvConstants.POS_CATEGORIES_MAP.get(q4)));

		return cols;
	}

	/**
	 * 【アンマッチ】アンケート項目の出力
	 *
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>CatvUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForUnmatch(List<String> cols,
			CatvUserDataDto userdata) {

		// 年齢
		cols.add(StringUtil.enquote(""));
		// 業種
		cols.add(StringUtil.enquote(""));
		// 職種
		cols.add(StringUtil.enquote(""));
		// 役職
		cols.add(StringUtil.enquote(""));

		return cols;
	}

	/**
	 * 不備フラグの特定
	 *
	 * @param userdata
	 *            <b>CatvUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(CatvUserDataDto userdata) {
		CatvValidator validator = new CatvValidator("■");
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