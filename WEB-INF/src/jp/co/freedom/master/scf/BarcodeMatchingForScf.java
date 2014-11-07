package jp.co.freedom.master.scf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.scf.ScfUserDataDto;
import jp.co.freedom.master.utilities.scf.ScfConfig;
import jp.co.freedom.master.utilities.scf.ScfUtil;

/**
 * 【SCF】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class BarcodeMatchingForScf extends HttpServlet {

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

		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(
				ScfConfig.barcodeDataDirectory,
				ScfConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				ScfConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				ScfConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(ScfConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<ScfUserDataDto> userDataList = ScfUtil.createInstance(csvData);
		assert userDataList != null;

		// アンマッチバーコード番号リスト
		List<ScfUserDataDto> unmatchList = new ArrayList<ScfUserDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(ScfConfig.PSQL_URL,
					ScfConfig.PSQL_USER, ScfConfig.PSQL_PASSWORD);

			// 事前登録の全データ
			List<ScfUserDataDto> allPreRegistDataList = ScfUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, ScfUserDataDto> preRegistInfoMap = ScfUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<ScfUserDataDto> allAppointedDataList = ScfUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, ScfUserDataDto> appointedInfoMap = ScfUtil
					.getMap(allAppointedDataList);
			// ロボット展登録の全データ
			List<ScfUserDataDto> allRobotDataList = ScfUtil
					.getAllRobotData(conn);
			// ロボット展登録ユーザー情報Map
			Map<String, ScfUserDataDto> robotInfoMap = ScfUtil
					.getMap(allRobotDataList);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			ScfUtil util = new ScfUtil();
			for (ScfUserDataDto userData : userDataList) {
				if (util.isPreEntry(userData)) {
					// 事前登録ユーザーである場合
					ScfUserDataDto tmpData = preRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else if (ScfUtil.isRobotEntry(userData)) {
					// ロボット登録ユーザーである場合
					ScfUserDataDto tmpData = robotInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else if (util.isAppEntry(userData)) {
					// 当日登録ユーザーである場合
					ScfUserDataDto tmpData = appointedInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					assert false;
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!ScfUtil.downLoad(request, response, outputFileName,
						userDataList, "\t", false, false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else { // アンマッチバーコード番号リストのダウンロード
				if (!ScfUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
					System.out.println("Error: Failed download CSV file");
				}
			}
			conn.close();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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