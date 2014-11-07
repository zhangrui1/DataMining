package jp.co.freedom.master.noma;

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
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.noma.NomaUserDataDto;
import jp.co.freedom.master.utilities.noma.NomaConfig;
import jp.co.freedom.master.utilities.noma.NomaExhibitorUtil;
import jp.co.freedom.master.utilities.noma.NomaUtil;

/**
 * 【NOMA】来場チェック結果をDB保存するサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "VisitorCheckForNoma", urlPatterns = { "/VisitorCheckForNoma" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class VisitorCheckForNoma extends HttpServlet {

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
		List<String[]> csvData = FileUtil.loadCsvSavingFileName(uploadDirPath,
				NomaConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				NomaConfig.REMOVE_HEADER_RECORD_FALSE, Config.DELIMITER_COMMNA,
				NomaConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;

		// ユーザーデータを保持するリスト
		List<NomaUserDataDto> userDataList = NomaExhibitorUtil
				.createInstance(csvData);
		assert userDataList != null;

		// ユニークIDの抽出
		List<String> uniqueList = new ArrayList<String>();
		for (NomaUserDataDto userInfo : userDataList) {
			String id = userInfo.id;
			if (!uniqueList.contains(id)) {
				uniqueList.add(id);
			}
		}
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(NomaConfig.PSQL_URL_LOCAL,
					NomaConfig.PSQL_USER, NomaConfig.PSQL_PASSWORD_LOCAL);
			// DB保存
			for (String id : uniqueList) {
				NomaUtil.updateVisitorFlg(conn, id, "preentry");
			}
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
		// JSPにフォワード
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/result.jsp");
		dispatcher.forward(request, response);
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