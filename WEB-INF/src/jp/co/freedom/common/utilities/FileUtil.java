package jp.co.freedom.common.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 * Fileユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class FileUtil {

	/**
	 * ローカルの全てのCSVデータをRFIDログデータを取得する
	 *
	 * @param dirctoryPath
	 *            CSVデータのフォルダパス
	 * @param enquote
	 *            ダブルクォートでエンクォートされているか否かのブール値
	 * @param removeHeader
	 *            ヘッダ行を削除するか否かのブール値
	 * @param delimiter
	 *            デリミタ
	 * @param allowsExtension
	 *            許容するファイル拡張子
	 * @return 全てのCSVデータを格納する<b>List</b>
	 */
	public static List<String[]> loadCsv(String dirctoryPath, boolean enquote,
			boolean removeHeader, String delimiter, String... allowsExtension) {
		List<String[]> allCsvData = new ArrayList<String[]>();
		File dir = new File(dirctoryPath); // txtデータを格納するフォルダ
		List<File> csvFiles = getFiles(dir, allowsExtension); // 指定フォルダ以下にある全てのCSVファイル
		// 1ファイルずつCSVデータを収集してリストに追加
		for (File csvFile : csvFiles) {
			List<String[]> csvData = getRfidData(csvFile, enquote,
					removeHeader, delimiter);
			allCsvData.addAll(csvData);
		}
		return allCsvData;
	}

	/**
	 * ローカルの全てのCSVデータをRFIDログデータを取得する
	 *
	 * @param dirctoryPath
	 *            CSVデータのフォルダパス
	 * @param enquote
	 *            ダブルクォートでエンクォートされているか否かのブール値
	 * @param removeHeader
	 *            ヘッダ行を削除するか否かのブール値
	 * @param delimiter
	 *            デリミタ
	 * @param allowsExtension
	 *            許容するファイル拡張子
	 * @return 全てのCSVデータを格納する<b>List</b>
	 */
	public static List<String[]> loadCsvSavingFileName(String dirctoryPath,
			boolean enquote, boolean removeHeader, String delimiter,
			String... allowsExtension) {
		List<String[]> allCsvData = new ArrayList<String[]>();
		File dir = new File(dirctoryPath); // txtデータを格納するフォルダ
		List<File> csvFiles = getFiles(dir, allowsExtension); // 指定フォルダ以下にある全てのCSVファイル
		// 1ファイルずつCSVデータを収集してリストに追加
		for (File csvFile : csvFiles) {
			List<String[]> csvData = getRfidDataSavingFileName(csvFile,
					enquote, removeHeader, delimiter);
			allCsvData.addAll(csvData);
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
	 * CSVファイルの読み込み
	 *
	 * @param csvFile
	 *            CSVファイル
	 * @param enquote
	 *            ダブルクォートでエンクォートされているか否かのブール値
	 * @param removeHeader
	 *            ヘッダー行を除去するか否かのブール値
	 * @param delimiter
	 *            デリミタ
	 * @return 一つのtxtファイルのデータを格納する<b>List</b>
	 */
	private static List<String[]> getRfidData(File csvFile, boolean enquote,
			boolean removeHeader, String delimiter) {
		assert csvFile != null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));

			List<String[]> lists = new ArrayList<String[]>();
			String line = null;
			while ((line = br.readLine()) != null) {
				// 読み込んだ文字列をタブで分割してリストに格納
				if ("\t".equals(delimiter)) {
					if (enquote) {
						lists.add(line.substring(1, line.length() - 1).split(
								"\"\t\""));// ダブルクォートを除去して分割
					} else {
						lists.add(line.split("\t"));
					}
				} else if (",".equals(delimiter)) {
					if (enquote) {
						lists.add(line.substring(1, line.length() - 1).split(
								"\",\""));// ダブルクォートを除去して分割
					} else {
						lists.add(line.split(","));
					}
				}
			}
			br.close();
			if (removeHeader) {// ヘッダー情報を除去
				lists.remove(0);
			}
			return lists;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * CSVファイルの読み込み
	 *
	 * @param csvFile
	 *            CSVファイル
	 * @param enquote
	 *            ダブルクォートでエンクォートされているか否かのブール値
	 * @param removeHeader
	 *            ヘッダー行を除去するか否かのブール値
	 * @param delimiter
	 *            デリミタ
	 * @return 一つのtxtファイルのデータを格納する<b>List</b>
	 */
	private static List<String[]> getRfidDataSavingFileName(File csvFile,
			boolean enquote, boolean removeHeader, String delimiter) {
		assert csvFile != null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));

			List<String[]> lists = new ArrayList<String[]>();
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] buff = null;
				// 読み込んだ文字列をタブで分割してリストに格納
				if ("\t".equals(delimiter)) {
					if (enquote) {
						buff = line.substring(1, line.length() - 1).split(
								"\"\t\""); // ダブルクォートを除去して分割
					} else {
						buff = line.split("\t");
					}
				} else if (",".equals(delimiter)) {
					if (enquote) {
						buff = line.substring(1, line.length() - 1).split(
								"\",\"");// ダブルクォートを除去して分割
					} else {
						buff = line.split(",");
					}
				}
				if (buff != null) {
					String[] extendedBuff = copy(buff,
							new String[buff.length + 1], 1);
					extendedBuff[0] = csvFile.getName(); // ファイル名を先頭に記憶
					lists.add(extendedBuff);
				}
			}
			br.close();
			if (removeHeader) {// ヘッダー情報を除去
				lists.remove(0);
			}
			return lists;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 配列fromの内容を配列toにコピーする
	 *
	 * @param from
	 *            コピー元配列
	 * @param to
	 *            　コピー先配列
	 * @param start
	 *            配列toにおけるコピー開始要素番号
	 * @return 配列to
	 */
	private static String[] copy(String[] from, String[] to, int start) {
		for (int nIndex = 0; nIndex < from.length; nIndex++) {
			to[start + nIndex] = from[nIndex];
		}
		return to;
	}

	/**
	 * データフォルダのパスより出力ファイル名を決定する
	 *
	 * @param directoryPath
	 *            CSVファイルを格納するディレクトリパス
	 *
	 * @return 出力ファイル名
	 */
	public static String getOutputFileName(String directoryPath) {
		File dir = new File(directoryPath);
		File[] allDir = dir.listFiles();
		if (allDir.length == 1) {
			if (allDir[0].isDirectory()) {
				String requestFileName = allDir[0].getName();
				return requestFileName + ".txt";
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 指定フォルダ中に含まれるXMLファイルの<b>File</b>オブジェクトを返却
	 *
	 * @param directoryPath
	 *            　XMLファイルを格納するディレクトリパス
	 * @return XMLファイルの<b>File</b>
	 */
	public static File getXmlFile(String directoryPath) {
		File dir = new File(directoryPath);
		List<File> allXmlFiles = getFiles(dir, "xml", "XML"); // 指定フォルダ以下にある全てのXMLファイル
		for (File xmlFile : allXmlFiles) {
			return xmlFile;
		}
		return null;
	}

	/**
	 * 指定フォルダ中に含まれるXSDファイルの<b>File</b>オブジェクトを返却
	 *
	 * @param directoryPath
	 *            　XSDファイルを格納するディレクトリパス
	 * @return XSDファイルの<b>File</b>
	 */
	public static File getXsdFile(String directoryPath) {
		File dir = new File(directoryPath);
		List<File> allXmlFiles = getFiles(dir, "xsd", "XSD"); // 指定フォルダ以下にある全てのXSDファイル
		for (File xmlFile : allXmlFiles) {
			return xmlFile;
		}
		return null;
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
	public static void writer(String[] data, PrintWriter writer, String dim) {
		StringBuffer row = new StringBuffer();
		for (int j = 0; j < data.length; j++) {
			row.append(data[j]);
			if (j + 1 < data.length) {
				row.append(dim); // 最終カラム以外はデリミタを付加する
			}
		}
		writer.println(row.toString()); // 1レコード分のデータを出力
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
	public static void writer(List<String> list, PrintWriter writer, String dim) {
		writer.println(StringUtil.concatWithDelimit(dim, list)); // 1レコード分のデータを出力
	}

	/**
	 * データ出力(改行なし)
	 *
	 * @param data
	 *            出力用データ
	 * @param writer
	 *            Writer
	 * @param dim
	 *            デリミタ
	 */
	public static void writerWithNoReturnCode(List<String> list,
			PrintWriter writer, String dim) {
		writer.print(StringUtil.concatWithDelimit(dim, list)); // 1レコード分のデータを出力
	}

	/**
	 * HTTPリクエストよりアップロードされたファイルを指定したディレクトリに保存する
	 *
	 * @param request
	 *            <b>HttpServletRequest</b>
	 * @param uploadDirectoryPath
	 *            アップロード先のディレクトリパス
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws ServletException
	 */
	public static void saveUploadFiles(HttpServletRequest request,
			String uploadDirectoryPath) throws IOException,
			IllegalStateException, ServletException {
		/*
		 * バーコードデータファイルのアップロード
		 */
		String applicationPath = request.getServletContext().getRealPath(""); // アプリケーションの絶対パス
		String uploadDirPath = applicationPath + File.separator
				+ uploadDirectoryPath; // アップロードディレクトリの絶対パス
		File uploadDir = new File(uploadDirPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		for (Part part : request.getParts()) {
			if (part.getName().equals("files")) {
				part.write(uploadDirPath + File.separator + getFileName(part));
			}
		}
	}

	/**
	 * HTTPヘッダー情報(content-disposition)よりアップロードファイルのファイル名を取得する
	 *
	 * @param part
	 *            <b>Part</b>
	 * @return アップロードファイルのファイル名
	 */
	private static String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2,
						token.length() - 1);
			}
		}
		return "";
	}

	/**
	 * ファイル／ディレクトリの削除
	 *
	 * @param path
	 *            削除対象のファイル／ディレクトリのパス
	 */
	public static void delete(String path) {
		delete(new File(path));
	}

	/**
	 * ファイル／ディレクトリの削除
	 *
	 * @param file
	 *            削除対象のファイル／ディレクトリ
	 */
	public static void delete(File file) {
		if (file.exists() == false) {
			return;
		}

		if (file.isFile()) {
			file.delete();
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
			file.delete();
		}
	}
}
