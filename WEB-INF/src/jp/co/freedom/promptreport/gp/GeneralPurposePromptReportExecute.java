package jp.co.freedom.promptreport.gp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.promptreport.util.SqlUtil;
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 【汎用】速報集計サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class GeneralPurposePromptReportExecute extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** リピーター数 */
	private static List<Integer> repeat;

	/** 3日目のリピーター詳細 */
	private static Map<String, Integer> repeatDetail3 = new LinkedHashMap<String, Integer>();
	/** 4日目のリピーター詳細 */
	private static Map<String, Integer> repeatDetail4 = new LinkedHashMap<String, Integer>();

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
		repeat = new ArrayList<Integer>();

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					Config.PSQL_URL_COMMONDB_LOCAL, Config.PSQL_USER,
					Config.PSQL_PASSWORD_LOCAL);

			List<Integer> newRegist = new ArrayList<Integer>();
			List<Integer> dupRegist = new ArrayList<Integer>();

			/* 新規登録 */
			for (int nIndex = 0; nIndex < config.days.length; nIndex++) {
				StringBuffer query = new StringBuffer();
				for (int nPrevious = 0; nPrevious < nIndex; nPrevious++) {
					String previous = config.days[nPrevious] + "_cnt";
					SqlUtil.appendIntegerQuery(query, previous, 0, true);
				}
				String current = config.days[nIndex] + "_cnt";
				SqlUtil.appendIntegerQuery(query, current, 0, false);
				newRegist.add(Utilities.getVisitorNumBySQL(conn, query));
			}

			/* Total(重複含む) */
			for (String day : config.days) {
				dupRegist.add(Utilities.getVisitorNum(conn, day + "_cnt"));
			}

			/* リピーター */
			if (config.days.length == 1) { // 初日
				calcRepeatFirstDay(config.days);
			} else if (config.days.length == 2) { // 2日目
				calcRepeatFirstDay(config.days);
				calcRepeatSecondDay(conn, config.days);
			} else if (config.days.length == 3) { // 3日目
				calcRepeatFirstDay(config.days);
				calcRepeatSecondDay(conn, config.days);
				calcRepeatThirdDay(conn, config.days);
			} else if (config.days.length == 4) { // 4日目
				calcRepeatFirstDay(config.days);
				calcRepeatSecondDay(conn, config.days);
				calcRepeatThirdDay(conn, config.days);
				calcRepeatFourthDay(conn, config.days);
			}

			int totalNewRegist = Utilities.sum(newRegist);
			int totalRepeat = Utilities.sum(repeat);
			int totaldupRegist = Utilities.sum(dupRegist);

			// JSPへのデータ引継ぎ
			request.setAttribute("days", config.days);
			request.setAttribute("newRegist", newRegist);
			request.setAttribute("repeat", repeat);
			request.setAttribute("dupRegist", dupRegist);

			request.setAttribute("totalNewRegist", totalNewRegist);
			request.setAttribute("totalRepeat", totalRepeat);
			request.setAttribute("totaldupRegist", totaldupRegist);

			request.setAttribute("repeatDetail3", repeatDetail3);
			request.setAttribute("repeatDetail4", repeatDetail4);

			request.setAttribute("backURL",
					"./html/prompt/generalPurposePromptReport.html");

			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/gp/result.jsp");
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
	 * 初日のリピーター数の算出
	 *
	 * @param days
	 *            会期日時
	 */
	private static void calcRepeatFirstDay(String days[]) {
		repeat.add(0);
	}

	/**
	 * 2日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日時
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void calcRepeatSecondDay(Connection conn, String days[])
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		int day1_2 = Utilities.getVisitorNumBySQL(conn, query);
		repeat.add(day1_2);
	}

	/**
	 * 3日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日時
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void calcRepeatThirdDay(Connection conn, String days[])
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		int day1_3 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		int day2_3 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		int day1_2_3 = Utilities.getVisitorNumBySQL(conn, query);
		repeatDetail3.put(createLabel(days, 1, 3), day1_3);
		repeatDetail3.put(createLabel(days, 2, 3), day2_3);
		repeatDetail3.put(createLabel(days, 1, 2, 3), day1_2_3);
		repeat.add(day1_3 + day2_3 + day1_2_3);
	}

	/**
	 * 4日目のリピーター数の算出
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日時
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void calcRepeatFourthDay(Connection conn, String days[])
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		StringBuffer query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day1_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day2_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day3_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day1_2_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day1_3_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, true);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day2_3_4 = Utilities.getVisitorNumBySQL(conn, query);
		query = new StringBuffer();
		SqlUtil.appendIntegerQuery(query, days[0] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[1] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[2] + "_cnt", 0, false);
		SqlUtil.appendIntegerQuery(query, days[3] + "_cnt", 0, false);
		int day1_2_3_4 = Utilities.getVisitorNumBySQL(conn, query);
		repeatDetail4.put(createLabel(days, 1, 4), day1_4);
		repeatDetail4.put(createLabel(days, 2, 4), day2_4);
		repeatDetail4.put(createLabel(days, 3, 4), day3_4);
		repeatDetail4.put(createLabel(days, 1, 2, 4), day1_2_4);
		repeatDetail4.put(createLabel(days, 1, 3, 4), day1_3_4);
		repeatDetail4.put(createLabel(days, 2, 3, 4), day2_3_4);
		repeatDetail4.put(createLabel(days, 1, 2, 3, 4), day1_2_3_4);
		repeat.add(day1_4 + day2_4 + day3_4 + day1_2_4 + day1_3_4 + day2_3_4
				+ day1_2_3_4);
	}

	/**
	 * リピート詳細のラベルを生成
	 *
	 * @param days
	 *            展示会の会期日程
	 * @param indices
	 *            配列daysのインデックス
	 * @return リポート詳細のラベル文字列
	 */
	private static String createLabel(String[] days, int... indices) {
		assert days.length != 0;
		List<String> output = new ArrayList<String>();
		for (int index : indices) {
			output.add(days[index - 1] + "日");
		}
		return StringUtil.concatWithDelimit("＆", output);
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