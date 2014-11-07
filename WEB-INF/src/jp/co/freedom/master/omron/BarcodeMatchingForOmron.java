package jp.co.freedom.master.omron;

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
import jp.co.freedom.master.dto.omron.OmronUserDataDto;
import jp.co.freedom.master.utilities.omron.OmronConfig;
import jp.co.freedom.master.utilities.omron.OmronUtil;

/**
 * 【OMRON】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class BarcodeMatchingForOmron extends HttpServlet {

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
				OmronConfig.barcodeDataDirectory,
				OmronConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				OmronConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				OmronConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(OmronConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<OmronUserDataDto> userDataList = OmronUtil.createInstance(csvData);
		assert userDataList != null;

		// アンマッチバーコード番号リスト
		List<OmronUserDataDto> unmatchList = new ArrayList<OmronUserDataDto>();
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(OmronConfig.PSQL_URL_LOCAL,
					OmronConfig.PSQL_USER, OmronConfig.PSQL_PASSWORD_LOCAL);

			// DB中の全ての入力データ
			List<OmronUserDataDto> allDataList = OmronUtil.getAllData(conn);
			// 入力データMAP
			Map<String, OmronUserDataDto> preRegistInfoMap = OmronUtil
					.getMap(allDataList);

			// 入力データMAPから必要な情報を取得しユーザーデータに取り込む
			for (OmronUserDataDto userData : userDataList) {
				String companyName = userData.cardInfo.V_CORP;
				OmronUserDataDto tmpData = preRegistInfoMap.get(userData.id);
				if (tmpData != null) {
					userData.cardInfo = tmpData.cardInfo;
					userData.cardInfo.V_CORP = companyName;
					userData.questionInfo = tmpData.questionInfo;
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!OmronUtil.downLoad(request, response, outputFileName,
						userDataList, "\t", false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else { // アンマッチバーコード番号リストのダウンロード
				if (!OmronUtil.downLoadForUnmatch(request, response,
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