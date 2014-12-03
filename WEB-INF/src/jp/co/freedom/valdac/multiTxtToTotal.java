package jp.co.freedom.valdac;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.valdac.utilities.FileValdacUtilites;
import jp.co.freedom.valdac.utilities.ValdacConfig;

/**
 * 【固定データ長】棚卸表データのインポート用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "multiTxtToTotal", urlPatterns = { "/multiTxtToTotal" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class multiTxtToTotal extends HttpServlet {

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


		// マッチングデータのダウンロード
		if (!FileValdacUtilites.downLoad(request, response,
				ValdacConfig.OUTPUT_FILENAME,uploadDirPath,
				Config.DELIMITER_TAB, ValdacConfig.ALLOWS_EXTENSIONS)) {
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