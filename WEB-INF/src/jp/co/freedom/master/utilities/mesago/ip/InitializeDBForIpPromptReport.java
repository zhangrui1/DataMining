package jp.co.freedom.master.utilities.mesago.ip;

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
import jp.co.freedom.promptreport.dto.BarcodeDto;
import jp.co.freedom.promptreport.util.Utilities;

/**
 * IP向けCSVデータのアップロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "InitializeDBForIpPromptReport", urlPatterns = { "/InitializeDBForIpPromptReport" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class InitializeDBForIpPromptReport extends HttpServlet {

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

		/* ファイルアップロード */
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR; // アップロードディレクトリの絶対パス
		FileUtil.delete(uploadDirPath); // アップロードファイルの削除
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR); // アップロードファイルの保存

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath,
				IpConfig.ENQUOTE_BY_DOUBLE_QUOTATION_TRUE,
				IpConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_COMMNA,
				IpConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// バーコードデータ
		List<BarcodeDto> barcodes = Utilities.createBwjInstance(csvData);
		// ユニークIDの抽出
		List<String> unique = Utilities.getUniqueList(barcodes);
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(IpConfig.PSQL_URL_REMOTE,
					IpConfig.PSQL_USER, IpConfig.PSQL_PASSWORD_REMOTE);
			Utilities.initializeRfidTable(conn, unique, IpConfig.DAYS.length); // RFIDテーブルにユニークバーコード番号を登録する
			Utilities.storeRfidData(conn, barcodes,
					IpConfig.START_INDEX_DAY_IN_TIMESTAMP,
					IpConfig.END_INDEX_DAY_IN_TIMESTAMP, IpConfig.DAYS); // バーコードデータのストア
			request.setAttribute("backURL", "./html/master/ip.html");
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