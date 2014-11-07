package jp.co.freedom.search;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * イメージファイル検索サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImageFileSearch extends HttpServlet {

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
		// 検索キーワード
		Object keywordObj = request.getParameter("keyword");
		String keyword = (String) keywordObj;
		assert StringUtil.isNotEmpty(keyword);

		File directory = new File(ImageFileSearchConfig.ENQUETE_DATA_DIRECTORY);
		assert directory != null && directory.isDirectory();
		List<File> tiffList = FileUtil.getFiles(directory,
				ImageFileSearchConfig.ALLOWS_EXTENSIONS_TIFF);
		String resultFilePath = ImageFileSearchConfig.ENQUETE_DATA_DIRECTORY
				+ "\\" + ImageFileSearchConfig.RESULT_FILE_NAME; // OCR結果データファイル
		List<String> hitList = new ArrayList<String>();
		for (File tiff : tiffList) {
			assert tiff != null;
			// プロセス起動(OCRコマンド発行)
			ProcessBuilder processBuilder = new ProcessBuilder(
					StringUtil.enquote(ImageFileSearchConfig.OCR_EXE_FILE_PATH),
					tiff.getAbsolutePath(),
					ImageFileSearchConfig.ENQUETE_DATA_DIRECTORY + "\\"
							+ ImageFileSearchConfig.RESULT_FILE_BASENAME);
			File resultFile = null;
			Process process = processBuilder.start();
			try {
				int responseCode = process.waitFor(); // プロセス終了待ち
				if (0 == responseCode) { // 正常終了である場合
					resultFile = new File(resultFilePath);
					String allResult = ImageFileSearchUtil
							.getOCRData(resultFile); // OCR実行結果
					if (StringUtil.isNotEmpty(allResult)
							&& StringUtil.find(allResult, keyword)) { // キーワード検索にヒットした場合
						URI uri = tiff.toURI();
						String path = uri.getScheme() + "://" + uri.getPath();
						System.out.println(path);
						hitList.add(path);
					}
				}
				process.destroy(); // プロセスの明示的な終了
				if (resultFile != null) {
					resultFile.delete(); // OCR結果データファイル削除
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// JSPにフォワード
		request.setAttribute("backURL", "./html/common/imageFileSearch.html");
		request.setAttribute("hitList", hitList);
		request.setAttribute("keyword", keyword);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/search/imageSearchResult.jsp");
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