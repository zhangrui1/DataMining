package jp.co.freedom.master.ltt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import jp.co.freedom.master.dto.ltt.LttUserDataDto;
import jp.co.freedom.master.utilities.ltt.LTTConfig;
import jp.co.freedom.master.utilities.ltt.LTTExhibitorUtil;

/**
 * 【LTT】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
@WebServlet(name = "BarcodeExhibitorMatchingForLTT", urlPatterns = { "/BarcodeExhibitorMatchingForLTT" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BarcodeExhibitorMatchingForLTT extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 変換表 */
	private Map<String, String> convertTable = new HashMap<String, String>();

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
		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsvSavingFileName(uploadDirPath,
				LTTConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				LTTConfig.REMOVE_HEADER_RECORD_FALSE, Config.DELIMITER_COMMNA,
				LTTConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 事前バーコード変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				LTTConfig.CONVERT_TABLE_DIRECTORY,
				LTTConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE, true, "\t",
				LTTConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 出力ファイル名
		String outputFileName = LTTConfig.MATCHING_RESULT_FILE_NAME;
		// ユーザーデータを保持するリスト
		List<LttUserDataDto> userDataList = LTTExhibitorUtil
				.createInstance(csvData);
		assert userDataList != null;
		// 事前バーコード変換表の作成
		List<LttUserDataDto> convertTableList = LTTExhibitorUtil
				.createInstanceForConvertTable(convertTableData);
		for (LttUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
				String id = data.id;
//				id = id.substring(1, id.length() - 1);
				if (!this.convertTable.containsKey(id)) {
					this.convertTable.put(id, data.cardInfo.V_CID);
				} else {
					System.out.println("変換表データに重複発見");
					System.out.println("◆バーコード=" + id);
					System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
				}
			} else {
				System.out.println("変換表データが不正");
				System.out.println("◆バーコード=" + data.id);
				System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
			}
		}
		assert convertTableList != null;

		// アンマッチバーコード番号リスト
		List<LttUserDataDto> unmatchList = new ArrayList<LttUserDataDto>();
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(LTTConfig.PSQL_URL_LOCAL,
					LTTConfig.PSQL_USER, LTTConfig.PSQL_PASSWORD_LOCAL);
			// 事前登録の全データ
			List<LttUserDataDto> allPreRegistDataList = LTTExhibitorUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, LttUserDataDto> preRegistInfoMap = LTTExhibitorUtil
					.getPreEntryMap(allPreRegistDataList);
			// 当日登録の全データ
			List<LttUserDataDto> allAppointedDataList = LTTExhibitorUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, LttUserDataDto> appointedInfoMap = LTTExhibitorUtil
					.getAppointedDayMap(allAppointedDataList);
			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (LttUserDataDto userData : userDataList) {
				userData.unmatch = true; // アンマッチフラグ
				userData.cardInfo.V_CID = getPreEntryId(userData.id);
//				String subBarcode = LTTConstants.SUB_BARCODES_MAP
//						.get(userData.id);
//				if (StringUtil.isNotEmpty(subBarcode)) { // サブバーコードの保存
//					userData.subBarcode = userData.id;
//					userData.id = subBarcode;
//				}
				if (StringUtil.isNotEmpty(userData.cardInfo.V_CID)) {
					// 事前登録ユーザーである場合
					LttUserDataDto tmpData = preRegistInfoMap
							.get(userData.cardInfo.V_CID);
					if (tmpData != null) {
						userData.preentry = true;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.unmatch = false; // アンマッチフラグ
					}
				} else {
					// 当日登録ユーザーである場合
					LttUserDataDto tmpData = appointedInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.appointedday = true;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.unmatch = false; // アンマッチフラグ
					}
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
				

				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if (!LTTExhibitorUtil.downLoad(request, response, conn,
					outputFileName, userDataList, Config.DELIMITER_TAB, false,
					false,true)) {
				System.out.println("Error: Failed download CSV file");
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

	/**
	 * 事前登録発券番号を取得
	 * 
	 * @param id
	 *            バーコード番号
	 * @return 事前登録発券番号
	 */
	private String getPreEntryId(String id) {
		assert StringUtil.isNotEmpty(id);
		String value = this.convertTable.get(id);
		return value;
	}
}