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
import jp.co.freedom.common.utilities.StringUtil;
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
@WebServlet(name = "BwjGateCalculateMultiType", urlPatterns = { "/BwjGateCalculateMultiType" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BwjGateCalculateMultiType extends HttpServlet {

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
			String dayStr = (String) dayObj;
			int day = Integer.parseInt(dayStr);

			// 業種別集計結果
			List<Integer> e3single = new ArrayList<Integer>();// シングル
			List<Integer> e3multi = new ArrayList<Integer>(); // マルチ
			List<Integer> e4single = new ArrayList<Integer>();// シングル
			List<Integer> e4multi = new ArrayList<Integer>(); // マルチ
			List<Integer> e5single = new ArrayList<Integer>();// シングル
			List<Integer> e5multi = new ArrayList<Integer>(); // マルチ
			List<Integer> e6single = new ArrayList<Integer>();// シングル
			List<Integer> e6multi = new ArrayList<Integer>(); // マルチ
			int total = config.bwjCategoryList.size();
			// 集計
			String gateId = "E3";
			for (int nIndex = 1; nIndex <= total; nIndex++) {
				String gyoNo = Integer.toString(nIndex);
				gyoNo = gyoNo.length() == 1 ? "0" + gyoNo : gyoNo;
				e3single.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, false));
				e3multi.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, true));
			}
			gateId = "E4";
			for (int nIndex = 1; nIndex <= total; nIndex++) {
				String gyoNo = Integer.toString(nIndex);
				gyoNo = gyoNo.length() == 1 ? "0" + gyoNo : gyoNo;
				e4single.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, false));
				e4multi.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, true));
			}
			gateId = "E5";
			for (int nIndex = 1; nIndex <= total; nIndex++) {
				String gyoNo = Integer.toString(nIndex);
				gyoNo = gyoNo.length() == 1 ? "0" + gyoNo : gyoNo;
				e5single.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, false));
				e5multi.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, true));
			}
			gateId = "E6";
			for (int nIndex = 1; nIndex <= total; nIndex++) {
				String gyoNo = Integer.toString(nIndex);
				gyoNo = gyoNo.length() == 1 ? "0" + gyoNo : gyoNo;
				e6single.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, false));
				e6multi.add(Utilities.getBwjSingleMultiTotalRfidNum(conn, day,
						gyoNo, gateId, true));
			}

			// 合計
			int e3singleTotal = Utilities.sum(e3single);
			int e3multiTotal = Utilities.sum(e3multi);
			int e4singleTotal = Utilities.sum(e4single);
			int e4multiTotal = Utilities.sum(e4multi);
			int e5singleTotal = Utilities.sum(e5single);
			int e5multiTotal = Utilities.sum(e5multi);
			int e6singleTotal = Utilities.sum(e6single);
			int e6multiTotal = Utilities.sum(e6multi);

			// JSPへのデータ引継ぎ
			request.setAttribute(
					"caption",
					StringUtil.concatWithDelimit("/", config.year,
							config.month, String.valueOf(day)) + "を対象としたデータ分析");
			request.setAttribute("categoryList", config.bwjCategoryList);
			request.setAttribute("e3single", e3single);
			request.setAttribute("e3multi", e3multi);
			request.setAttribute("e4single", e4single);
			request.setAttribute("e4multi", e4multi);
			request.setAttribute("e5single", e5single);
			request.setAttribute("e5multi", e5multi);
			request.setAttribute("e6single", e6single);
			request.setAttribute("e6multi", e6multi);

			request.setAttribute("e3singleTotal", e3singleTotal);
			request.setAttribute("e3multiTotal", e3multiTotal);
			request.setAttribute("e4singleTotal", e4singleTotal);
			request.setAttribute("e4multiTotal", e4multiTotal);
			request.setAttribute("e5singleTotal", e5singleTotal);
			request.setAttribute("e5multiTotal", e5multiTotal);
			request.setAttribute("e6singleTotal", e6singleTotal);
			request.setAttribute("e6multiTotal", e6multiTotal);

			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/gp/gateAnalyzeMultiType.jsp");
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