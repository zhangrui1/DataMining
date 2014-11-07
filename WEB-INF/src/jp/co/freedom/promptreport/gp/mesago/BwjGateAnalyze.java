package jp.co.freedom.promptreport.gp.mesago;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * ゲート別データ集計用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BwjGateAnalyze", urlPatterns = { "/BwjGateAnalyze" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BwjGateAnalyze extends HttpServlet {

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
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
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
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(Config.PSQL_URL_COMMONDB_LOCAL,
					Config.PSQL_USER, Config.PSQL_PASSWORD_LOCAL);

			Object dayObj = request.getParameter("day");
			assert dayObj != null && dayObj instanceof String;
			String day = (String) dayObj;
			Object timeObj = request.getParameter("time");
			assert timeObj != null && timeObj instanceof String;
			String time = (String) timeObj;

			List<String[]> newEntry = Utilities.getNewEntryRfidDetailInfo(conn,
					config.days, day, "9", time); // 新規エントリ総数
			List<String[]> repeat = Utilities.getRepeatRfidDetailInfo(conn,
					config.days, day, "9", time); // リピーター総数

			// 業種別集計結果
			List<Integer> e3AppointedDay = new ArrayList<Integer>();// 当日登録
			List<Integer> e3breakdown = new ArrayList<Integer>(); // 事前登録
			List<Integer> e3repeat = new ArrayList<Integer>(); // リピーター
			List<Integer> e4AppointedDay = new ArrayList<Integer>();// 当日登録
			List<Integer> e4breakdown = new ArrayList<Integer>(); // 事前登録
			List<Integer> e4repeat = new ArrayList<Integer>(); // リピーター
			List<Integer> e5AppointedDay = new ArrayList<Integer>();// 当日登録
			List<Integer> e5breakdown = new ArrayList<Integer>(); // 事前登録
			List<Integer> e5repeat = new ArrayList<Integer>(); // リピーター
			List<Integer> e6AppointedDay = new ArrayList<Integer>();// 当日登録
			List<Integer> e6breakdown = new ArrayList<Integer>(); // 事前登録
			List<Integer> e6repeat = new ArrayList<Integer>(); // リピーター
			// 初期化
			for (int nIndex = 0; nIndex <= config.bwjCategoryList.size(); nIndex++) {
				e3AppointedDay.add(0);
				e3breakdown.add(0);
				e3repeat.add(0);
				e4AppointedDay.add(0);
				e4breakdown.add(0);
				e4repeat.add(0);
				e5AppointedDay.add(0);
				e5breakdown.add(0);
				e5repeat.add(0);
				e6AppointedDay.add(0);
				e6breakdown.add(0);
				e6repeat.add(0);
			}
			// 新規登録集計
			for (int nIndex = 0; nIndex < newEntry.size(); nIndex++) {
				String[] info = newEntry.get(nIndex);
				int categoryId = Integer.parseInt(info[1]);
				boolean preFlg = "1".equals(info[2]);
				if ("E3".equals(info[3])) {
					if (preFlg) {
						e3breakdown.set(categoryId,
								e3breakdown.get(categoryId) + 1);
					} else {
						e3AppointedDay.set(categoryId,
								e3AppointedDay.get(categoryId) + 1);
					}
				} else if ("E4".equals(info[3])) {
					if (preFlg) {
						e4breakdown.set(categoryId,
								e4breakdown.get(categoryId) + 1);
					} else {
						e4AppointedDay.set(categoryId,
								e4AppointedDay.get(categoryId) + 1);
					}
				} else if ("E5".equals(info[3])) {
					if (preFlg) {
						e5breakdown.set(categoryId,
								e5breakdown.get(categoryId) + 1);
					} else {
						e5AppointedDay.set(categoryId,
								e5AppointedDay.get(categoryId) + 1);
					}
				} else if ("E6".equals(info[3])) {
					if (preFlg) {
						e6breakdown.set(categoryId,
								e6breakdown.get(categoryId) + 1);
					} else {
						e6AppointedDay.set(categoryId,
								e6AppointedDay.get(categoryId) + 1);
					}
				}
			}
			// リピーター集計
			for (int nIndex = 0; nIndex < repeat.size(); nIndex++) {
				String[] info = repeat.get(nIndex);
				int categoryId = Integer.parseInt(info[1]);
				if ("E3".equals(info[3])) {
					e3repeat.set(categoryId, e3repeat.get(categoryId) + 1);
				} else if ("E4".equals(info[3])) {
					e4repeat.set(categoryId, e4repeat.get(categoryId) + 1);
				} else if ("E5".equals(info[3])) {
					e5repeat.set(categoryId, e5repeat.get(categoryId) + 1);
				} else if ("E6".equals(info[3])) {
					e6repeat.set(categoryId, e6repeat.get(categoryId) + 1);
				}
			}

			// 合計
			int e3AppointedDayTotal = Utilities.sum(e3AppointedDay);
			int e3breakdownTotal = Utilities.sum(e3breakdown);
			int e4AppointedDayTotal = Utilities.sum(e4AppointedDay);
			int e4breakdownTotal = Utilities.sum(e4breakdown);
			int e5AppointedDayTotal = Utilities.sum(e5AppointedDay);
			int e5breakdownTotal = Utilities.sum(e5breakdown);
			int e6AppointedDayTotal = Utilities.sum(e6AppointedDay);
			int e6breakdownTotal = Utilities.sum(e6breakdown);
			int e3RepeatTotal = Utilities.sum(e3repeat);
			int e4RepeatTotal = Utilities.sum(e4repeat);
			int e5RepeatTotal = Utilities.sum(e5repeat);
			int e6RepeatTotal = Utilities.sum(e6repeat);

			// JSPへのデータ引継ぎ
			request.setAttribute("caption", "2013/05/" + day + "を対象としたデータ分析");
			request.setAttribute("categoryList", config.bwjCategoryList);
			request.setAttribute("e3AppointedDay", e3AppointedDay);
			request.setAttribute("e3breakdown", e3breakdown);
			request.setAttribute("e3repeat", e3repeat);
			request.setAttribute("e4AppointedDay", e4AppointedDay);
			request.setAttribute("e4breakdown", e4breakdown);
			request.setAttribute("e4repeat", e4repeat);
			request.setAttribute("e5AppointedDay", e5AppointedDay);
			request.setAttribute("e5breakdown", e5breakdown);
			request.setAttribute("e5repeat", e5repeat);
			request.setAttribute("e6AppointedDay", e6AppointedDay);
			request.setAttribute("e6breakdown", e6breakdown);
			request.setAttribute("e6repeat", e6repeat);

			request.setAttribute("e3AppointedDayTotal", e3AppointedDayTotal);
			request.setAttribute("e3breakdownTotal", e3breakdownTotal);
			request.setAttribute("e4AppointedDayTotal", e4AppointedDayTotal);
			request.setAttribute("e4breakdownTotal", e4breakdownTotal);
			request.setAttribute("e5AppointedDayTotal", e5AppointedDayTotal);
			request.setAttribute("e5breakdownTotal", e5breakdownTotal);
			request.setAttribute("e6AppointedDayTotal", e6AppointedDayTotal);
			request.setAttribute("e6breakdownTotal", e6breakdownTotal);

			request.setAttribute("e3RepeatTotal", e3RepeatTotal);
			request.setAttribute("e4RepeatTotal", e4RepeatTotal);
			request.setAttribute("e5RepeatTotal", e5RepeatTotal);
			request.setAttribute("e6RepeatTotal", e6RepeatTotal);

			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/gp/gateAnalyze.jsp");
			dispatcher.forward(request, response);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
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