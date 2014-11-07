package jp.co.freedom.promptreport.gp.mesago;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.promptreport.gp.GeneralPurposePromptReportConfig;
import jp.co.freedom.promptreport.util.SqlUtil;
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 【汎用】BWJ 速報集計サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BwjGeneralPurposePromptReportExecute", urlPatterns = { "/BwjGeneralPurposePromptReportExecute" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BwjGeneralPurposePromptReportExecute extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Getメソッドの処理
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;

		// config.xmlのパーズ
		File configXml = FileUtil.getXmlFile(uploadDirPath);
		assert configXml != null;
		GeneralPurposePromptReportConfig config = null;
		try {
			// xmlファイルのパーズによりDocumentノードを生成
			Document document = DOMUtil.domParser(configXml.getAbsolutePath());
			assert document != null;
			// config.xmlのルート要素ノード
			Element configElement = document.getDocumentElement();
			config = Utilities.createConfig(configElement);
		} catch (SAXException e) {
			e.printStackTrace();
		}

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					Config.PSQL_URL_COMMONDB_LOCAL, Config.PSQL_USER,
					Config.PSQL_PASSWORD_LOCAL);

			/* 【BWJ専用】事前登録用カテゴリ分類定義の読み込み */
			config.bwjPreRegistCategoryRules = Utilities
					.loadBwjPreRegistCategoryMap(conn);

			/* 集計結果DTOの初期化 */
			List<BwjGeneralPurposePromptReportResult> resultList = new ArrayList<BwjGeneralPurposePromptReportResult>();
			for (int nIndex = 0; nIndex < config.days.length; nIndex++) {
				BwjGeneralPurposePromptReportResult result = new BwjGeneralPurposePromptReportResult();
				List<Map.Entry<String, String>> bwjCategory = new ArrayList<Map.Entry<String, String>>(
						config.bwjAppointeddayCategoryRules.entrySet());
				for (Entry<String, String> entry : bwjCategory) {
					String eps = entry.getValue();
					result.newRegistApp.put(eps, 0);
					result.newRegistPre.put(eps, 0);
					result.repeat.put(eps, 0);
				}
				/* 去年配布バッチ使用者用 */
				result.newRegistApp.put("lastyear", 0);
				result.newRegistPre.put("lastyear", 0);
				result.repeat.put("lastyear", 0);

				resultList.add(result);
			}
			Map<String, Integer> repeatTotal = new LinkedHashMap<String, Integer>();
			List<Map.Entry<String, String>> bwjCategory = new ArrayList<Map.Entry<String, String>>(
					config.bwjAppointeddayCategoryRules.entrySet());
			for (Entry<String, String> entry : bwjCategory) {
				String eps = entry.getValue();
				repeatTotal.put(eps, 0);
			}
			/* 去年配布バッチ使用者用 */
			repeatTotal.put("lastyear", 0);

			/* 新規登録 */
			for (int nIndex = 0; nIndex < config.days.length; nIndex++) {
				StringBuffer query = new StringBuffer();
				for (int nPrevious = 0; nPrevious < nIndex; nPrevious++) {
					String previous = config.days[nPrevious] + "_cnt";
					SqlUtil.appendIntegerQuery(query, previous, 0, true);
				}
				String current = config.days[nIndex] + "_cnt";
				SqlUtil.appendIntegerQuery(query, current, 0, false);
				List<String> barcodes = Utilities.getBarcodeListBySQLForBwj(
						conn, query);
				BwjGeneralPurposePromptReportResult result = resultList
						.get(nIndex);
				result.newRegistApp = Utilities.saveData(config,
						result.newRegistApp, barcodes, false);
				result.newRegistPre = Utilities.saveData(config,
						result.newRegistPre, barcodes, true);
			}

			/* リピーター */
			if (config.days.length == 1) { // 初日
			} else if (config.days.length == 2) { // 2日目
				calcRepeatSecondDay(conn, config, resultList);
			} else if (config.days.length == 3) { // 3日目
				calcRepeatSecondDay(conn, config, resultList);
				calcRepeatThirdDay(conn, config, resultList);
			} else if (config.days.length == 4) { // 4日目
				calcRepeatSecondDay(conn, config, resultList);
				calcRepeatThirdDay(conn, config, resultList);
				calcRepeatFourthDay(conn, config, resultList);
			}

			/* リピーター合計 */
			calcRepeatTotal(conn, config, repeatTotal);

			/* 総合計 */
			int newRegistAppTotal = 0;
			int newRegistPreTotal = 0;
			for (int nIndex = 0; nIndex < config.days.length; nIndex++) {
				newRegistAppTotal += Utilities
						.sum(resultList.get(nIndex).newRegistApp);
				newRegistPreTotal += Utilities
						.sum(resultList.get(nIndex).newRegistPre);
			}
			int repeatTotalTotal = Utilities.sum(repeatTotal);

			// JSPへのデータ引継ぎ
			request.setAttribute("days", config.days);
			request.setAttribute("categoryList", config.bwjCategoryList);
			request.setAttribute("resultList", resultList);
			request.setAttribute("repeatTotal", repeatTotal);
			request.setAttribute("newRegistAppTotal", newRegistAppTotal);
			request.setAttribute("newRegistPreTotal", newRegistPreTotal);
			request.setAttribute("repeatTotalTotal", repeatTotalTotal);

			request.setAttribute("backURL",
					"./html/prompt/bwjGeneralPurposePromptReport.html");

			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/gp/bwjResult.jsp");
			dispatcher.forward(request, response);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 2日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param config
	 *            設定情報
	 * @param resultList
	 *            集計結果格納用リスト
	 * @return 集計結果格納用リスト
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static List<BwjGeneralPurposePromptReportResult> calcRepeatSecondDay(
			Connection conn, GeneralPurposePromptReportConfig config,
			List<BwjGeneralPurposePromptReportResult> resultList)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String[] days = config.days;
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		List<String> barcodes = Utilities
				.getBarcodeListBySQLForBwj(conn, query);
		BwjGeneralPurposePromptReportResult result = resultList.get(1);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes);
		return resultList;
	}

	/**
	 * 3日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param config
	 *            設定情報
	 * @param resultList
	 *            集計結果格納用リスト
	 * @return 集計結果格納用リスト
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static List<BwjGeneralPurposePromptReportResult> calcRepeatThirdDay(
			Connection conn, GeneralPurposePromptReportConfig config,
			List<BwjGeneralPurposePromptReportResult> resultList)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String[] days = config.days;
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes1 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes2 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes3 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		BwjGeneralPurposePromptReportResult result = resultList.get(2);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes1);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes2);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes3);
		return resultList;
	}

	/**
	 * 4日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param config
	 *            設定情報
	 * @param resultList
	 *            集計結果格納用リスト
	 * @return 集計結果格納用リスト
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static List<BwjGeneralPurposePromptReportResult> calcRepeatFourthDay(
			Connection conn, GeneralPurposePromptReportConfig config,
			List<BwjGeneralPurposePromptReportResult> resultList)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String[] days = config.days;
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes1 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes2 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes3 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes4 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes5 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes6 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		List<String> barcodes7 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		BwjGeneralPurposePromptReportResult result = resultList.get(3);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes1);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes2);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes3);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes4);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes5);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes6);
		result.repeat = Utilities.saveData(config, result.repeat, barcodes7);
		return resultList;
	}

	/**
	 * リピーター合計数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param config
	 *            設定情報
	 * @param resultMap
	 *            集計結果格納用Map
	 * @return 集計結果格納用Map
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static Map<String, Integer> calcRepeatTotal(Connection conn,
			GeneralPurposePromptReportConfig config,
			Map<String, Integer> resultMap) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String[] days = config.days;
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes1 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes2 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		List<String> barcodes3 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		List<String> barcodes4 = Utilities.getBarcodeListBySQLForBwj(conn,
				query);
		resultMap = Utilities.saveData(config, resultMap, barcodes1);
		resultMap = Utilities.saveData(config, resultMap, barcodes2);
		resultMap = Utilities.saveData(config, resultMap, barcodes3);
		resultMap = Utilities.saveData(config, resultMap, barcodes4);
		return resultMap;

	}

	/**
	 * POSTメソッドの処理
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response); // Getメソッドに受け流す
	}
}