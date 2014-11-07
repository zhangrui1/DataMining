package jp.co.freedom.gpenquete;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.FileUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XMLSchemaによるカスタマイズ可能な汎用展示会データ集計サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class GeneralExhibitionDataMining extends HttpServlet {

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
		// エラーログ削除
		GeneralPurposeEnqueteMiningUtil.removeErrorLog();
		// config.xmlの解釈
		File configXml = FileUtil.getXmlFile(uploadDirPath);
		assert configXml != null;
		GeneralPurposeEnqueteConfig config = null;
		try {
			// xmlファイルのパーズによりDocumentノードを生成
			Document document = DOMUtil.domParser(configXml.getAbsolutePath());
			assert document != null;
			// config.xmlのルート要素ノード
			Element configElement = document.getDocumentElement();
			// GeneralPurposeEnqueteConfigのインスタンス化
			config = GeneralPurposeEnqueteMiningUtil
					.createConfig(configElement);
			assert config != null;
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath,
				config.enquoteByDoubleQuotation, config.removeHeaderRecord,
				config.inputSeparateMark, Config.ALLOWS_EXTENSIONS_TXT);
		assert csvData != null;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// 集計データのダウンロード
		if (!GeneralPurposeEnqueteMiningUtil.download(request, response,
				config, csvData, config.outputSeparateMark, true)) {
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