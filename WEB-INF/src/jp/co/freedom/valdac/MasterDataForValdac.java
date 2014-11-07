package jp.co.freedom.valdac;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 * 【ShimazuOsaka】納品データの作成用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterDataForValdac", urlPatterns = { "/MasterDataForValdac" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class MasterDataForValdac extends HttpServlet {

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
		Integer event = new Integer(request.getParameter("event"));
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsvSavingFileName(uploadDirPath,
				ValdacConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				ValdacConfig.REMOVE_HEADER_RECORD, ValdacConfig.DELIMITER,
				ValdacConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);

		// ユーザーデータを保持するリスト
		List<String[]> userDataList = ValdacUtilites.createInstance(
				csvData,event);
		assert userDataList != null;
		// マッチングデータのダウンロード
		if (!ValdacUtilites.downLoad(request, response,
				ValdacConfig.OUTPUT_FILENAME, userDataList,
				Config.DELIMITER_TAB)) {
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