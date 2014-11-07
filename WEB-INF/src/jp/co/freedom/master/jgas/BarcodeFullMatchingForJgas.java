package jp.co.freedom.master.jgas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.jgas.JgasEnqueteDto;
import jp.co.freedom.master.dto.jgas.JgasUserDataDto;
import jp.co.freedom.master.utilities.jgas.JgasConfig;
import jp.co.freedom.master.utilities.jgas.JgasUtil;

/**
 * 【JGAS】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class BarcodeFullMatchingForJgas extends HttpServlet {

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

		/*
		 * 名刺情報＆リクエストコードの取得
		 */
		List<String[]> csvData = FileUtil.loadCsv(JgasConfig.csvDataDirectory,
				JgasConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				JgasConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				JgasConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		List<JgasUserDataDto> userDataList = JgasUtil.createInstance(csvData);
		assert userDataList != null;

		/*
		 * アンケートIDの取得
		 */
		List<String[]> enqueteCsvData = FileUtil.loadCsv(
				JgasConfig.enqueteCsvDataDirectory,
				JgasConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				JgasConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				JgasConfig.ALLOWS_EXTENSIONS);
		assert enqueteCsvData != null;
		List<JgasUserDataDto> enqueteDataList = JgasUtil
				.createInstanceForEnquete(enqueteCsvData);
		assert userDataList != null;

		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(JgasConfig.csvDataDirectory);

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(JgasConfig.PSQL_URL,
					JgasConfig.PSQL_USER, JgasConfig.PSQL_PASSWORD);

			/*
			 * アンケートデータの取得
			 */
			Map<String, JgasEnqueteDto[]> enqueteMap = new HashMap<String, JgasEnqueteDto[]>();
			for (JgasUserDataDto enquetedata : enqueteDataList) {
				for (String enqueteId : enquetedata.requestCode) {
					enqueteMap.put(enquetedata.id, JgasUtil.getEnqueteData(
							conn, enquetedata, enqueteId));
				}
			}

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (JgasUserDataDto userData : userDataList) {
				if (JgasUtil.isPreEntry(userData.id)) {
					// 事前登録ユーザーである場合
					JgasUserDataDto tmpData = JgasUtil.getPreRegistData(conn,
							userData.id, userData.timeByRfid);
					userData.cardInfo = tmpData.cardInfo;
					userData.questionInfo = tmpData.questionInfo;
					userData.enqueteInfo = enqueteMap.get(userData.id);
				} else if (JgasUtil.isAppEntry(userData.id)) {
					// 当日登録ユーザーである場合
					JgasUserDataDto tmpData = JgasUtil.getAppointedDayData(
							conn, userData.id, userData.timeByRfid);
					userData.cardInfo = tmpData.cardInfo;
					userData.questionInfo = tmpData.questionInfo;
					userData.enqueteInfo = enqueteMap.get(userData.id);
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