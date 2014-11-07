package jp.co.freedom.master.noma;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.noma.NomaUserDataDto;
import jp.co.freedom.master.utilities.noma.NomaConfig;
import jp.co.freedom.master.utilities.noma.NomaConstants;
import jp.co.freedom.master.utilities.noma.NomaExhibitorUtil;
import jp.co.freedom.master.utilities.noma.NomaUtil;

/**
 * 【NOMA】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeExhibitorMatchingForNoma", urlPatterns = { "/BarcodeExhibitorMatchingForNoma" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BarcodeExhibitorMatchingForNoma extends HttpServlet {

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

		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsvSavingFileName(uploadDirPath,
				NomaConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				NomaConfig.REMOVE_HEADER_RECORD_FALSE, Config.DELIMITER_COMMNA,
				NomaConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;
		// // 変換表のロード
		// List<String[]> convertTableData = FileUtil.loadCsv(
		// NomaConfig.CONVERT_TABLE_DIRECTORY,
		// NomaConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE, true,
		// Config.DELIMITER_TAB, NomaConfig.ALLOWS_EXTENSIONS);
		// assert convertTableData != null;
		// 出力ファイル名
		String outputFileName = NomaConfig.EXHIBITOR_RESULT_FILE_NAME;
		// String outputFileName = FileUtil
		// .getOutputFileName(NomaConfig.barcodeDataDirectoryNomaExhibitor);
		// ユーザーデータを保持するリスト
		List<NomaUserDataDto> userDataList = NomaExhibitorUtil
				.createInstance(csvData);
		assert userDataList != null;

		// // 変換表の作成
		// List<NomaUserDataDto> convertTableList = NomaExhibitorUtil
		// .createInstanceForConvertTable(convertTableData);
		// for (NomaUserDataDto data : convertTableList) {
		// if (StringUtil.isNotEmpty(data.id)
		// && StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
		// String id = data.id;
		// if (!this.convertTable.containsKey(id)) {
		// this.convertTable.put(id, data.cardInfo.V_CID);
		// } else {
		// System.out.println("変換表データに重複発見");
		// System.out.println("◆バーコード=" + id);
		// System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
		// }
		// } else {
		// System.out.println("変換表データが不正");
		// System.out.println("◆バーコード=" + data.id);
		// System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
		// }
		// }
		// assert convertTableList != null;

		// アンマッチバーコード番号リスト
		List<NomaUserDataDto> unmatchList = new ArrayList<NomaUserDataDto>();
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(NomaConfig.PSQL_URL_LOCAL,
					NomaConfig.PSQL_USER, NomaConfig.PSQL_PASSWORD_LOCAL);
			// 事前登録の全データ
			List<NomaUserDataDto> allPreRegistDataList = NomaExhibitorUtil
					.getAllPreRegistData(conn, "all");
			// 事前登録ユーザー情報Map
			Map<String, NomaUserDataDto> preRegistInfoMap = NomaExhibitorUtil
					.getPreEntryMap(allPreRegistDataList);
			// 当日登録の全データ
			List<NomaUserDataDto> allAppointedDataList = NomaExhibitorUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, NomaUserDataDto> appointedInfoMap = NomaExhibitorUtil
					.getAppointedDayMap(allAppointedDataList);
			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (NomaUserDataDto userData : userDataList) {
				String barcode = userData.id;
				/* 読取不可バーコードの差し替え */
				String tmpBarcode = NomaConstants.UNREADABLE_PREENTRY_BARCODES
						.get(barcode);
				if (StringUtil.isNotEmpty(tmpBarcode)) {
					userData.id = tmpBarcode;
					userData.cardInfo.V_VID = tmpBarcode;
				}
				NomaUtil util = new NomaUtil();
				if (util.isPreEntry(userData)) {
					// 事前登録ユーザーである場合
					NomaUserDataDto tmpData = preRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.preentry = true;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					// 当日登録ユーザーである場合
					NomaUserDataDto tmpData = appointedInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.appointedday = true;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!NomaExhibitorUtil
						.downLoad(request, response, conn, outputFileName,
								userDataList, "\t", false, false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else if ("unmatch".equals(mode)) { // アンマッチバーコード番号リストのダウンロード
				if (!NomaExhibitorUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
					System.out.println("Error: Failed download CSV file");
				}
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