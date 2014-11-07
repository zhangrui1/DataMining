package jp.co.freedom.enquete;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.enquete.EnqueteDto;
import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.enquete.ceatec.NedoEnqueteUtil;
import jp.co.freedom.common.utilities.enquete.shimazu.ShimazuEnqueteUtil;
import jp.co.freedom.common.utilities.enquete.yokohama.YokohamaEnqueteUtil;

/**
 * アンケート集計用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class EnqueteDataMining extends HttpServlet {

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

		// CSVデータのロード(ファイル名をCSVの最初に保存する)
		List<String[]> csvData = FileUtil
				.loadCsvSavingFileName(Config.GP_ENQUETE_MINING_DIRECTORY,
						Config.ENQUOTE_BY_DOUBLE_QUOTATION,
						Config.REMOVE_HEADER_RECORD, Config.DELIMITER_TAB,
						"txt", "TXT");
		assert csvData != null;

		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(Config.GP_ENQUETE_MINING_DIRECTORY);

		// ユーザーデータを保持するリスト
		List<EnqueteDto> userDataList = null;
		if ("biryushi".equals(event)) {
			userDataList = ShimazuEnqueteUtil.createInstanceForBiryushi(
					csvData, event);
		} else if ("nedo".equals(event)) {
			userDataList = NedoEnqueteUtil
					.createInstanceForNedo(csvData, event);
		} else if ("tire".equals(event)) {
			userDataList = YokohamaEnqueteUtil.createInstanceForYokohama(
					csvData, event);
		}

		assert userDataList != null;
		// マッチングデータのダウンロード
		if ("biryushi".equals(event)) {
			if (!ShimazuEnqueteUtil.downLoadForBiryushi(request, response,
					outputFileName, userDataList, "\t")) {
				System.out.println("Error: Failed download CSV file");
			}
		} else if ("nedo".equals(event)) {
			if (!NedoEnqueteUtil.downLoadForNedo(request, response,
					outputFileName, userDataList, "\t")) {
				System.out.println("Error: Failed download CSV file");
			}
		} else if ("tire".equals(event)) {
			if (!YokohamaEnqueteUtil.downLoadForYokohama(request, response,
					outputFileName, userDataList, "\t")) {
				System.out.println("Error: Failed download CSV file");
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