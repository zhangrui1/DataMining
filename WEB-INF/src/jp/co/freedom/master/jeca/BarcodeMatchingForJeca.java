package jp.co.freedom.master.jeca;

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
import jp.co.freedom.master.dto.jeca.JecaUserDataDto;
import jp.co.freedom.master.utilities.jeca.JecaConfig;
import jp.co.freedom.master.utilities.jeca.JecaConstants;
import jp.co.freedom.master.utilities.jeca.JecaUtil;

/**
 * 【JECA】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeMatchingForJeca", urlPatterns = { "/BarcodeMatchingForJeca" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BarcodeMatchingForJeca extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// /** 事前登録用変換表 */
	// private Map<String, JecaUserDataDto> convertTable = new HashMap<String,
	// JecaUserDataDto>();

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
		// Object modeObj = request.getParameter("mode");
		// String mode = (String) modeObj;
		// assert StringUtil.isNotEmpty(mode);
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);

		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;

		/*
		 * バーコードデータのインスタンス化
		 */
		List<String[]> barcodeList = FileUtil.loadCsv(uploadDirPath,
				JecaConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				JecaConfig.REMOVE_HEADER_RECORD_FALSE, Config.DELIMITER_COMMNA,
				JecaConfig.ALLOWS_EXTENSIONS);
		assert barcodeList != null;
		List<JecaUserDataDto> userDataList = JecaUtil
				.createInstance(barcodeList);
		assert userDataList != null;

		/*
		 * 変換表のインスタンス化
		 */
		// List<String[]> convertTableData = FileUtil.loadCsv(
		// JecaConfig.CONVERT_TABLE_DIRECTORY,
		// JecaConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
		// JecaConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_TAB,
		// JecaConfig.ALLOWS_EXTENSIONS);
		// assert convertTableData != null;
		// List<JecaUserDataDto> convertTableList = JecaUtil
		// .createInstanceForConvertTable(convertTableData);
		// assert convertTableList != null;
		// for (JecaUserDataDto data : convertTableList) {
		// if (StringUtil.isNotEmpty(data.id)
		// && StringUtil
		// .isNotEmpty(((JecaCardDto) data.cardInfo).PREENTRY_ID)) {
		// String id = data.id;
		// // id = id.substring(1, id.length() - 1);
		// this.convertTable.put(id, data);
		// } else {
		// assert false;
		// }
		// }

		// 出力ファイル名
		String outputFileName = JecaConfig.EXHIBITOR_RESULT_FILE_NAME;

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);

		// アンマッチバーコード番号リスト
		List<JecaUserDataDto> unmatchList = new ArrayList<JecaUserDataDto>();
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(JecaConfig.PSQL_URL_REMOTE,
					JecaConfig.PSQL_USER, JecaConfig.PSQL_PASSWORD_REMOTE);

			// 事前登録の全データ
			List<JecaUserDataDto> allPreRegistDataList = JecaUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, JecaUserDataDto> preRegistInfoMap = JecaUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<JecaUserDataDto> allAppointedDataList = JecaUtil
					.getAllAppointeddayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, JecaUserDataDto> appointedInfoMap = JecaUtil
					.getMap(allAppointedDataList);
			// 団体登録の全データ
			List<JecaUserDataDto> allGroupDataList = JecaUtil
					.getAllGroupData(conn);
			// 団体登録ユーザー情報Map
			Map<String, JecaUserDataDto> groupInfoMap = JecaUtil
					.getMap(allGroupDataList);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (JecaUserDataDto userData : userDataList) {
				userData.unmatch = true; // アンマッチフラグ
				String subBarcode = JecaConstants.SUB_BARCODES_MAP
						.get(userData.id);
				if (StringUtil.isNotEmpty(subBarcode)) { // サブバーコードの保存
					userData.subBarcode = userData.id;
					userData.id = subBarcode;
				}
				if (JecaUtil.isPreEntry(userData.id)) {
					// 事前登録ユーザーである場合
					JecaUserDataDto tmpData = preRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.appointedday = tmpData.appointedday;
						userData.preentry = tmpData.preentry;
						userData.group = tmpData.group;
						userData.unmatch = false;
					} else {
						System.out
								.println("事前登録変換表による変換に失敗しました：" + userData.id);
					}
				} else if (JecaUtil.isAppEntry(userData.id)) {
					// 当日入力ユーザーである場合
					JecaUserDataDto tmpData = appointedInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.appointedday = tmpData.appointedday;
						userData.preentry = tmpData.preentry;
						userData.group = tmpData.group;
						userData.unmatch = false;
					} else {
						System.out
								.println("当日入力MAPによる変換に失敗しました：" + userData.id);
					}
				} else if (JecaUtil.isGroupEntry(userData.id)) {
					// 団体登録ユーザーである場合
					JecaUserDataDto tmpData = groupInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.appointedday = tmpData.appointedday;
						userData.preentry = tmpData.preentry;
						userData.group = tmpData.group;
						userData.unmatch = false;
					} else {
						System.out
								.println("団体登録MAPによる変換に失敗しました：" + userData.id);
					}
				} else {
					assert false;
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if (!JecaUtil.downLoad(request, response, outputFileName, conn,
					userDataList, "\t", false, false)) {
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
}