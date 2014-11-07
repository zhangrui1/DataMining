package jp.co.freedom.master.jgas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.jgas.JgasUserDataDto;
import jp.co.freedom.master.utilities.jgas.JgasConfig;
import jp.co.freedom.master.utilities.jgas.JgasUtil;

/**
 * 【JGAS】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class BarcodeMatchingForJgas extends HttpServlet {

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

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(JgasConfig.csvDataDirectory,
				JgasConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				JgasConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				JgasConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(JgasConfig.csvDataDirectory);
		// ユーザーデータを保持するリスト
		List<JgasUserDataDto> userDataList = JgasUtil.createInstance(csvData);
		assert userDataList != null;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(JgasConfig.PSQL_URL,
					JgasConfig.PSQL_USER, JgasConfig.PSQL_PASSWORD);

			// 事前登録の全データ
			List<JgasUserDataDto> allPreRegistDataList = JgasUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, JgasUserDataDto> preRegistInfoMap = JgasUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<JgasUserDataDto> allAppointedDataList = JgasUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, JgasUserDataDto> appointedInfoMap = JgasUtil
					.getMap(allAppointedDataList);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (JgasUserDataDto userData : userDataList) {
				if (JgasUtil.isPreEntry(userData.id)) {
					// 事前登録ユーザーである場合
					JgasUserDataDto tmpData = preRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else if (JgasUtil.isAppEntry(userData.id)) {
					// 当日登録ユーザーである場合
					JgasUserDataDto tmpData = appointedInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					assert false;
				}
			}
			// マッチングデータのダウンロード
			if (!JgasUtil.downLoad(request, response, outputFileName,
					userDataList, "\t", false)) {
				System.out.println("Error: Failed download CSV file");
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