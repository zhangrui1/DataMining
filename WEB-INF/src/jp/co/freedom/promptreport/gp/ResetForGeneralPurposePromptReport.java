package jp.co.freedom.promptreport.gp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 【汎用】速報集計 DB初期化サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class ResetForGeneralPurposePromptReport extends HttpServlet {

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
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);

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
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					Config.PSQL_URL_COMMONDB_LOCAL, Config.PSQL_USER,
					Config.PSQL_PASSWORD_LOCAL);
			Utilities.removeRfidTable(conn); // rfidテーブルを削除
			Utilities.createRfidTable(conn, config.days); // rfidテーブルを新規作成
			conn.close();
			request.setAttribute("backURL",
					"./html/prompt/generalPurposePromptReport.html");
			request.setAttribute("result", "DBのリセット操作が完了しました");
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