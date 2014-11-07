package jp.co.freedom.master.fixedlengthdata.tokyo;

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
import jp.co.freedom.common.utilities.FileUtil;

/**
 * 【固定データ長】経理処理控データのインポート用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "InsertPaymasterProcessing", urlPatterns = { "/InsertPaymasterProcessing" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class InsertPaymasterProcessing extends HttpServlet {

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
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath, false, true,
				Config.DELIMITER_TAB, Config.ALLOWS_EXTENSIONS_TXT);
		assert csvData != null;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(CommonConfig.PSQL_URL_LOCAL,
					CommonConfig.PSQL_USER, CommonConfig.PSQL_PASSWORD_LOCAL);

			PaymasterProcessingUtil.resetDB(conn); // DBの初期化
			PaymasterProcessingUtil.importData(conn, csvData); // データインポート

			request.setAttribute("backURL", "./html/fixedlengthdata/tokyo.html");
			request.setAttribute("result", "インポート処理が終了しました。");
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