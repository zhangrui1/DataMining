package jp.co.freedom.shimazu.youken;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.shimazu.dto.ShimazuCatalogDto;
import jp.co.freedom.shimazu.dto.ShimazuUserDataDto;
import jp.co.freedom.shimazu.youken.utilities.ShimazuConfig;
import jp.co.freedom.shimazu.youken.utilities.ShimazuUtilites;

/**
 * 【ShimazuOsaka】納品データの作成用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class MasterDataForShimazuYouken extends HttpServlet {

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
		// イベント名
		String event = new String(request.getParameter("event").getBytes(
				"ISO_8859_1"), "UTF-8");
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsvSavingFileName(uploadDirPath,
				ShimazuConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				ShimazuConfig.REMOVE_HEADER_RECORD, ShimazuConfig.DELIMITER,
				ShimazuConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);

		// カタログ情報の取得
		List<ShimazuCatalogDto> catalogData = null;
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(ShimazuConfig.PSQL_URL,
					ShimazuConfig.PSQL_USER, ShimazuConfig.PSQL_PASSWORD);
			// カタログ情報の取得
			catalogData = ShimazuUtilites.getCatalogList(conn);
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

		// ユーザーデータを保持するリスト
		List<ShimazuUserDataDto> userDataList = ShimazuUtilites.createInstance(
				csvData, event);
		assert userDataList != null;
		// マッチングデータのダウンロード
		if (!ShimazuUtilites.downLoad(request, response,
				ShimazuConfig.OUTPUT_FILENAME, userDataList,
				Config.DELIMITER_TAB, catalogData)) {
			System.out.println("Error: Failed download CSV file");
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