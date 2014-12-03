package jp.co.freedom.valdac.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;


/**
 * Shimazu向けユーティリティクラス2
 *
 * @author フリーダム・グループ
 *
 */
public class FileValdacUtilites {

	private static Object mTable;

	public static List<String[]> loadCsv(String dirctoryPath, boolean enquote,
			boolean removeHeader, String delimiter, String... allowsExtension) {
		List<String[]> allCsvData = new ArrayList<String[]>();
		File dir = new File(dirctoryPath); // txtデータを格納するフォルダ
		List<File> csvFiles = getFiles(dir, allowsExtension); // 指定フォルダ以下にある全てのCSVファイル
		// 1ファイルずつCSVデータを収集してリストに追
		for (File csvFile : csvFiles) {

		}
		return allCsvData;
	}

	/**
	 * 指定フォルダ以下の全てのファイルを取得
	 *
	 * @param directory
	 *            　対象フォルダ
	 * @param allowsExtension
	 *            許容するファイル拡張子
	 * @return　ファイルリスト
	 */
	public static List<File> getFiles(File directory, String... allowsExtension) {
		List<File> allFiles = new ArrayList<File>();
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				allFiles.addAll(getFiles(file, allowsExtension));
			} else {
				String fileName = file.getName();
				int nIndex = fileName.indexOf(".");
				String extension = fileName.substring(nIndex + 1, file
						.getName().length()); // ファイル拡張子
				// ファイル拡張子が許容される場合はリストに登録する
				if (StringUtil.contains(allowsExtension, extension)) {
					allFiles.add(file);
				}
			}
		}
		return allFiles;
	}

	/**
	 * 照合結果をTXT形式でダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param catalogList
	 *            カタログリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			String uploadDirPath,String dim, String... allowsExtension) throws IOException {
		File dir = new File(uploadDirPath); // txtデータを格納するフォルダ

		List<File> csvFiles = getFiles(dir, allowsExtension); // 指定フォルダ以下にある全てのCSVファイル
		// 1ファイルずつCSVデータを収集してリストに追


		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (File csvFile : csvFiles) {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			String line = br.readLine();

			while ((line = br.readLine()) != null) {
				writer(line, writer, dim); // 1レコード分のデータ書き出し
			}
			br.close();
		}



		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * データ出力
	 *
	 * @param data
	 *            出力用データ
	 * @param writer
	 *            Writer
	 * @param dim
	 *            デリミタ
	 */
	public static void writer(String data, PrintWriter writer, String dim) {
		StringBuffer row = new StringBuffer();
			row.append(data);
		writer.println(row.toString()); // 1レコード分のデータを出力
	}

}