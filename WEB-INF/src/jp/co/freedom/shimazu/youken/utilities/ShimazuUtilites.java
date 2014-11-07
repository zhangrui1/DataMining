package jp.co.freedom.shimazu.youken.utilities;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.shimazu.dto.ShimazuCardForEnqueteDto;
import jp.co.freedom.shimazu.dto.ShimazuCatalogDto;
import jp.co.freedom.shimazu.dto.ShimazuUserDataDto;

/**
 * Shimazu向けユーティリティクラス2
 * 
 * @author フリーダム・グループ
 * 
 */
public class ShimazuUtilites {

	/** 設備導入検討調査内訳 */
	private static Map<String, String> IMPLEMENTATION_RESEARCH_DETAILS = createImplementationResearchDetails();

	/** 情報収集内訳 */
	private static Map<String, String> INFORMATION_RESEARCH_DETAILS = createInformationResearchDetails();

	/** 島津製品ユーザー内訳 */
	private static Map<String, String> SHIMAZU_USER_DETAILS = createShimazuUserDetails();

	/**
	 * 設備導入検討調査内訳用Mapの初期化
	 * 
	 * @return 設備導入検討調査内訳Map
	 */
	private static Map<String, String> createImplementationResearchDetails() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "お見積希望");
		map.put("2", "デモ分析希望");
		map.put("3", "ご決済者");
		map.put("4", "ご選定者");
		map.put("5", "ご担当者");
		return map;
	}

	/**
	 * 情報収集内訳用Mapの初期化
	 * 
	 * @return 情報収集内訳Map
	 */
	private static Map<String, String> createInformationResearchDetails() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "業務関連調査");
		map.put("2", "勉強");
		return map;
	}

	/**
	 * 島津ユーザー内訳用Mapの初期化
	 * 
	 * @return 島津ユーザー内訳Map
	 */
	private static Map<String, String> createShimazuUserDetails() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "分析相談");
		map.put("2", "カスタム相談");
		map.put("3", "修理・点検相談");
		map.put("4", "保守相談");
		map.put("5", "クレーム");
		return map;
	}

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<ShimazuUserDataDto> createInstance(
			List<String[]> csvData, String event) {
		List<ShimazuUserDataDto> userDataList = new ArrayList<ShimazuUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			ShimazuUserDataDto dataInfo = new ShimazuUserDataDto();
			ShimazuCardForEnqueteDto cardInfo = new ShimazuCardForEnqueteDto();
			String fileName = row[0]; // ファイル名
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
			cardInfo.FAX = row[22]; // FAX番号

			dataInfo.event = event; // イベント名
			dataInfo.cardInfo = cardInfo; // 名刺情報
			String month = fileName.substring(4, 6);
			month = month.startsWith("0") ? month.substring(1) : month;
			String day = fileName.substring(6, 8);
			day = day.startsWith("0") ? day.substring(1) : day;
			dataInfo.day = month + "月" + day + "日";
			dataInfo.orderId = row[23]; // 受付番号
			dataInfo.orderPerson = StringUtil.concat(row[24], row[25]); // 第1対応者
			dataInfo.referenceModel = row[26]; // ご説明機種
			dataInfo.referenceModelName = row[27]; // ご説明装置
			dataInfo.order = row[28]; // ご用件
			// 設備導入検討調査
			dataInfo.implementationPlanFlg = row[29];
			dataInfo.implementationYear = row[30];
			dataInfo.implementationMonth = row[31];
			dataInfo.implementationResearchDetail = row[32];
			// 情報収集
			dataInfo.researchFlg = row[33];
			dataInfo.researchDetail = row[34];
			// 島津製品ユーザー
			dataInfo.shimazuUserFlg = row[35];
			dataInfo.shimazuUserDetail = row[36];
			dataInfo.follow = row[37]; // フォロー
			dataInfo.followMonth = row[38]; // 訪問希望期日 月
			dataInfo.followDay = row[39]; // 訪問希望期日 日
			dataInfo.sendCatalog = row[40]; // カタログ渡し済
			dataInfo.wantCatalog = row[41]; // カタログ後送
			if (row.length >= 43) {
				dataInfo.sendCatalogOther = row[42]; // カタログ渡し済（手書き）
			}
			if (row.length >= 44) {
				dataInfo.wantCatalogOther = row[43]; // カタログ後送（手書き）
			}
			userDataList.add(dataInfo);
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
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param catalogList
	 *            カタログリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<ShimazuUserDataDto> userDataList, String dim,
			List<ShimazuCatalogDto> catalogList) throws IOException {

		int columnNum = 23;
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
		header[++headerNum] = StringUtil.enquote("団体代表名");
		header[++headerNum] = StringUtil.enquote("事業所名");
		header[++headerNum] = StringUtil.enquote("所属名");
		header[++headerNum] = StringUtil.enquote("個人姓");
		header[++headerNum] = StringUtil.enquote("個人名");
		header[++headerNum] = StringUtil.enquote("役職");
		header[++headerNum] = StringUtil.enquote("郵便番号");
		header[++headerNum] = StringUtil.enquote("都道府県");
		header[++headerNum] = StringUtil.enquote("市");
		header[++headerNum] = StringUtil.enquote("町");
		header[++headerNum] = StringUtil.enquote("番地");
		header[++headerNum] = StringUtil.enquote("電話");
		header[++headerNum] = StringUtil.enquote("FAX");
		header[++headerNum] = StringUtil.enquote("E-mail");
		header[++headerNum] = StringUtil.enquote("用件票番号");
		header[++headerNum] = StringUtil.enquote("用件記入担当名");
		header[++headerNum] = StringUtil.enquote("ご用件");
		header[++headerNum] = StringUtil.enquote("来場目的");
		header[++headerNum] = StringUtil.enquote("営業訪問必要・不要");
		header[++headerNum] = StringUtil.enquote("渡し済みｶﾀﾛｸﾞ");
		header[++headerNum] = StringUtil.enquote("ｶﾀﾛｸﾞ後送希望");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (ShimazuUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;
			// イベント名
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.event) ? StringUtil
					.enquote(userdata.event) : StringUtil.enquote("");
			// 来場日
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.day) ? StringUtil
					.enquote(userdata.day) : StringUtil.enquote("");
			// 団体代表名
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.COM) ? StringUtil
					.enquote(userdata.cardInfo.COM) : StringUtil.enquote("");
			// 事業所名
			cols[++nColumn] = StringUtil.enquote("");
			// 所属名
			String dept = StringUtil.concat(userdata.cardInfo.DEPT1,
					userdata.cardInfo.DEPT2, userdata.cardInfo.DEPT3,
					userdata.cardInfo.DEPT4);
			cols[++nColumn] = StringUtil.isNotEmpty(dept) ? StringUtil
					.enquote(dept) : StringUtil.enquote("");
			// 個人姓
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.cardInfo.NAME_LAST) ? StringUtil
					.enquote(userdata.cardInfo.NAME_LAST) : StringUtil
					.enquote("");
			// 個人名
			cols[++nColumn] = StringUtil
					.isNotEmpty(userdata.cardInfo.NAME_FIRST) ? StringUtil
					.enquote(userdata.cardInfo.NAME_FIRST) : StringUtil
					.enquote("");
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
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.ADDR2) ? StringUtil
					.enquote(userdata.cardInfo.ADDR2) : StringUtil.enquote("");
			// 町
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.ADDR3) ? StringUtil
					.enquote(userdata.cardInfo.ADDR3) : StringUtil.enquote("");
			// 番地
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.ADDR4) ? StringUtil
					.enquote(userdata.cardInfo.ADDR4) : StringUtil.enquote("");
			// 電話
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.TEL) ? StringUtil
					.enquote(userdata.cardInfo.TEL) : StringUtil.enquote("");
			// FAX
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.FAX) ? StringUtil
					.enquote(userdata.cardInfo.FAX) : StringUtil.enquote("");
			// E-mail
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.cardInfo.EMAIL) ? StringUtil
					.enquote(userdata.cardInfo.EMAIL) : StringUtil.enquote("");
			// 用件票番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.orderId) ? StringUtil
					.enquote(userdata.orderId) : StringUtil.enquote("");
			// 用件記入担当名
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.orderPerson) ? StringUtil
					.enquote(userdata.orderPerson) : StringUtil.enquote("");
			// ご用件
			String order = StringUtil.concatWithDelimit(",",
					getReferenceModelAndModelName(userdata), userdata.order);
			cols[++nColumn] = StringUtil.isNotEmpty(order) ? StringUtil
					.enquote(order) : StringUtil.enquote("");
			// 来場目的
			String purpose = createPurposeText(userdata);
			cols[++nColumn] = StringUtil.isNotEmpty(purpose) ? StringUtil
					.enquote(purpose) : StringUtil.enquote("");
			// 営業訪問
			StringBuffer sb = new StringBuffer();
			if (StringUtil.isNotEmpty(userdata.followMonth)
					|| StringUtil.isNotEmpty(userdata.followDay)) {
				userdata.follow = "1";
				sb.append("(");
				if (StringUtil.isNotEmpty(userdata.followMonth)) {
					sb.append(userdata.followMonth + "月");
				}
				if (StringUtil.isNotEmpty(userdata.followDay)) {
					sb.append(userdata.followDay + "日");
				}
				sb.append("まで)");
			}
			String follow = getFollowText(userdata.follow);
			String followDate = sb.toString();
			if (StringUtil.isNotEmpty(followDate)) {
				follow = follow + followDate;
			}
			cols[++nColumn] = StringUtil.isNotEmpty(follow) ? StringUtil
					.enquote(follow) : StringUtil.enquote("");

			// 渡し済カタログ
			String sendCatalog = getCatalogText(userdata.sendCatalog,
					catalogList);
			// String otherSendCatalog = StringUtil
			// .isNotEmpty(userdata.sendCatalogOther) ? StringUtil
			// .replace(userdata.sendCatalogOther, " ", ",") : "";
			String allSendCatalog = StringUtil
					.isNotEmpty(userdata.sendCatalogOther) ? StringUtil
					.concatWithDelimit(",", sendCatalog,
							userdata.sendCatalogOther) : sendCatalog;
			cols[++nColumn] = StringUtil.isNotEmpty(allSendCatalog) ? StringUtil
					.enquote(allSendCatalog) : StringUtil.enquote("");
			// カタログ後送希望
			String wantCatalog = getCatalogText(userdata.wantCatalog,
					catalogList);
			// String otherWantCatalog = StringUtil
			// .isNotEmpty(userdata.wantCatalogOther) ? StringUtil
			// .replace(userdata.wantCatalogOther, " ", ",") : "";
			String allWantCatalog = StringUtil
					.isNotEmpty(userdata.wantCatalogOther) ? StringUtil
					.concatWithDelimit(",", wantCatalog,
							userdata.wantCatalogOther) : wantCatalog;
			cols[++nColumn] = StringUtil.isNotEmpty(allWantCatalog) ? StringUtil
					.enquote(allWantCatalog) : StringUtil.enquote("");

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
	 * ご説明機種およびご説明装置の出力文字列の取得
	 * 
	 * @param userdata
	 *            　<b>ShimazuUserData2Dto</b>
	 * @return ご説明機種およびご説明装置の出力文字列
	 */
	private static String getReferenceModelAndModelName(
			ShimazuUserDataDto userdata) {
		StringBuffer sb = new StringBuffer();
		if (userdata != null) {
			// ご説明機種
			if (StringUtil.isNotEmpty(userdata.referenceModel)) {
				String[] modelBuff = userdata.referenceModel.split(" ");
				sb.append(StringUtil.concat(modelBuff));
			}
			// 括弧の出力フラグ
			boolean parenthesis = StringUtil
					.isNotEmpty(userdata.referenceModel)
					&& StringUtil.isNotEmpty(userdata.referenceModelName);
			if (parenthesis) {
				sb.append("(");
			}
			// ご説明装置
			if (StringUtil.isNotEmpty(userdata.referenceModelName)) {
				sb.append(userdata.referenceModelName);
			}
			if (parenthesis) {
				sb.append(")");
			}
		}
		return sb.toString();
	}

	/**
	 * 来場目的のテキストの生成
	 * 
	 * @param dataInfo
	 *            <b>ShimazuUserData2Dto</b>
	 * @return 来場目的のテキスト
	 */
	private static String createPurposeText(ShimazuUserDataDto dataInfo) {
		StringBuffer sb = new StringBuffer();
		// 設備導入検討調査
		boolean flg1 = StringUtil.isNotEmpty(dataInfo.implementationPlanFlg);
		boolean flg2 = StringUtil.isNotEmpty(dataInfo.implementationYear)
				|| StringUtil.isNotEmpty(dataInfo.implementationMonth);
		boolean flg3 = StringUtil
				.isNotEmpty(dataInfo.implementationResearchDetail);
		if (flg1 || flg2 || flg3) {
			sb.append("設備導入検討調査");
			if (flg2 || flg3) {
				sb.append("(");
			}
			if (flg2) {
				sb.append("導入予定時期:");
				if (StringUtil.isNotEmpty(dataInfo.implementationYear)) {
					sb.append(dataInfo.implementationYear + "年");
				}
				if (StringUtil.isNotEmpty(dataInfo.implementationMonth)) {
					sb.append(dataInfo.implementationMonth + "月");
				}
				sb.append("頃");
			}
			if (flg3) {
				if (flg2) {
					sb.append("／");
				}
				sb.append(getImplementationText(dataInfo.implementationResearchDetail));
			}
			if (flg2 || flg3) {
				sb.append(")");
			}
		}
		// 情報収集
		boolean flg4 = StringUtil.isNotEmpty(dataInfo.researchFlg);
		boolean flg5 = StringUtil.isNotEmpty(dataInfo.researchDetail);
		if (flg4 || flg5) {
			if (flg1 || flg2 || flg3) {
				sb.append(",");
			}
			sb.append("情報収集");
			if (flg5) {
				sb.append("(");
				sb.append(getInformationText(dataInfo.researchDetail));
				sb.append(")");
			}
		}
		// 島津製品ユーザー
		boolean flg6 = StringUtil.isNotEmpty(dataInfo.shimazuUserFlg);
		boolean flg7 = StringUtil.isNotEmpty(dataInfo.shimazuUserDetail);
		if (flg6 || flg7) {
			if (flg1 || flg2 || flg3 || flg4 || flg5) {
				sb.append(",");
			}
			sb.append("島津製品ユーザー");
			if (flg7) {
				sb.append("(");
				sb.append(getShimazuUserText(dataInfo.shimazuUserDetail));
				sb.append(")");
			}
		}
		return sb.toString();
	}

	/**
	 * カタログ情報の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　カタログ情報
	 * @throws SQLException
	 */
	public static List<ShimazuCatalogDto> getCatalogList(Connection conn)
			throws SQLException {
		List<ShimazuCatalogDto> catalogList = new ArrayList<ShimazuCatalogDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM catalog;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ShimazuCatalogDto catalogInfo = new ShimazuCatalogDto();
			catalogInfo.id = rs.getString("CID");
			catalogInfo.name = rs.getString("NAME");
			catalogInfo.catalogNum = rs.getString("CATALOG");
			catalogList.add(catalogInfo);
		}
		return catalogList;
	}

	private static String getCatalogText(String text,
			List<ShimazuCatalogDto> catalogList) {
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isNotEmpty(text)) {
			String[] buff = text.split(" ");
			for (int nIndex = 0; nIndex < buff.length; nIndex++) {
				ShimazuCatalogDto catalogInfo = getCatalogInfo(catalogList,
						buff[nIndex]);
				if (catalogInfo != null) {
					String value = StringUtil.concat(catalogInfo.catalogNum,
							catalogInfo.name);
					sb.append(value);
					if (nIndex != buff.length - 1) {
						sb.append(",");
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 指定IDを有するカタログ情報を取得
	 * 
	 * @param catalogList
	 *            カタログ情報リスト
	 * @param id
	 *            ID
	 * @return　カタログ情報
	 */
	private static ShimazuCatalogDto getCatalogInfo(
			List<ShimazuCatalogDto> catalogList, String id) {
		assert catalogList != null && StringUtil.isNotEmpty(id);
		for (ShimazuCatalogDto catalogInfo : catalogList) {
			if (id.equals(catalogInfo.id)) {
				return catalogInfo;
			}
		}
		return null;
	}

	private static String getImplementationText(String text) {
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isNotEmpty(text)) {
			String[] buff = text.split(" ");
			for (int nIndex = 0; nIndex < buff.length; nIndex++) {
				String value = IMPLEMENTATION_RESEARCH_DETAILS
						.get(buff[nIndex]);
				if (StringUtil.isNotEmpty(value)) {
					sb.append(value);
					if (nIndex != buff.length - 1) {
						sb.append("・");
					}
				}
			}
		}
		return sb.toString();
	}

	private static String getInformationText(String text) {
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isNotEmpty(text)) {
			String[] buff = text.split(" ");
			for (int nIndex = 0; nIndex < buff.length; nIndex++) {
				String value = INFORMATION_RESEARCH_DETAILS.get(buff[nIndex]);
				if (StringUtil.isNotEmpty(value)) {
					sb.append(value);
					if (nIndex != buff.length - 1) {
						sb.append("・");
					}
				}
			}
		}
		return sb.toString();
	}

	private static String getShimazuUserText(String text) {
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isNotEmpty(text)) {
			String[] buff = text.split(" ");
			for (int nIndex = 0; nIndex < buff.length; nIndex++) {
				String value = SHIMAZU_USER_DETAILS.get(buff[nIndex]);
				if (StringUtil.isNotEmpty(value)) {
					sb.append(value);
					if (nIndex != buff.length - 1) {
						sb.append("・");
					}
				}
			}
		}
		return sb.toString();
	}

	private static String getFollowText(String value) {
		if (StringUtil.isNotEmpty(value)) {
			if ("1".equals(value)) {
				return "必要";
			} else if ("2".equals(value)) {
				return "緊急必要";
			} else if ("3".equals(value)) {
				return "不要";
			} else {
				return "";
			}
		}
		return "";
	}
}