package jp.co.freedom.promptreport.gp.mesago;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import jp.co.freedom.promptreport.dto.BarcodeDto;
import jp.co.freedom.promptreport.gp.GeneralPurposePromptReportConfig;
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 【ILT汎用】速報集計 DBデータアップロードサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "IltUploadRfidDataForGeneralPurposePromptReport", urlPatterns = { "/IltUploadRfidDataForGeneralPurposePromptReport" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class IltUploadRfidDataForGeneralPurposePromptReport extends HttpServlet {

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
		// バーコードデータのロード
		List<String[]> allData = FileUtil.loadCsv(uploadDirPath,
				config.enquoteByDoubleQuotation, config.removeHeaderRecord,
				config.inputSeparateMark, Config.ALLOWS_EXTENSIONS_CSV);
		assert allData != null;

		// バーコードデータ
		List<BarcodeDto> barcodes = Utilities.createIltInstance(allData);
		// ユニークIDの抽出
		List<String> unique = Utilities.getUniqueList(barcodes);
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(Config.PSQL_URL_COMMONDB_LOCAL,
					Config.PSQL_USER, Config.PSQL_PASSWORD_LOCAL);

			/* 【BWJ専用】事前登録用カテゴリ分類定義の読み込み */
			config.bwjPreRegistCategoryRules = Utilities
					.loadIltPreRegistCategoryMap();

			Utilities.iltInitializeRfidTable(conn, unique, config); // RFIDテーブルにユニークバーコード番号を登録する
			Utilities.storeRfidData(conn, barcodes,
					config.startIndexDayInTimestamp,
					config.endIndexDayInTimestamp, config.days); // バーコードデータのストア
			request.setAttribute("backURL",
					"./html/prompt/bwjgGeneralPurposePromptReport.html");
			request.setAttribute("result", "バーコードデータのDB登録が完了しました");
			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/dbOperation.jsp");
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