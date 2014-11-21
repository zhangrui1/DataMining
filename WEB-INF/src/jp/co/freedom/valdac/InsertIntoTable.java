package jp.co.freedom.valdac;

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
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 * 【固定データ長】棚卸表データのインポート用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "InsertIntoTable", urlPatterns = { "/InsertIntoTable" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class InsertIntoTable extends HttpServlet {

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

		// テーブル名
		Object tableObj = request.getParameter("tablename");
		String tablename = (String) tableObj;
		assert StringUtil.isNotEmpty(tablename);


		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath, false,true,
				ValdacConfig.DELIMITER, ValdacConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(ValdacConfig.PSQL_URL,
					ValdacConfig.PSQL_USER, ValdacConfig.PSQL_PASSWORD);

			ValdacUtilites.resetDB(conn,tablename); // DBの初期化
			if("kikisys".equals(tablename)){
				ValdacUtilites.importDataKikiSys(conn, csvData,tablename); // 機器システムテーブル
			}else if("kiki".equals(tablename)){
				ValdacUtilites.importDataKiki(conn, csvData,tablename); // 機器テーブル
			}else if("buhin".equals(tablename)){
				ValdacUtilites.importDataBuhi(conn, csvData,tablename); // 部品テーブル
			}else if("kouji".equals(tablename)){
				ValdacUtilites.importDataKouji(conn, csvData,tablename); // 部品テーブル
			}


			request.setAttribute("backURL", "./html/valdac/valdac.html");
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