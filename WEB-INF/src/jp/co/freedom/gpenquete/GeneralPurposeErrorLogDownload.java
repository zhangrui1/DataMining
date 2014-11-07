package jp.co.freedom.gpenquete;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;

/**
 * 【汎用アンケート】エラーログ確認サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "GeneralPurposeErrorLogDownload", urlPatterns = { "/GeneralPurposeErrorLogDownload" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class GeneralPurposeErrorLogDownload extends HttpServlet {

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
		if (!GeneralPurposeEnqueteMiningUtil.downLoadForErrolog(request,
				response, Config.ERRORLOG_FILE, Config.DELIMITER_TAB)) {
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