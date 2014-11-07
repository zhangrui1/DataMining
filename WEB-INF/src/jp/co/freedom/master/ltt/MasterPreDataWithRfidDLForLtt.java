package jp.co.freedom.master.ltt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import jp.co.freedom.master.dto.ltt.LttUserDataDto;
import jp.co.freedom.master.utilities.ltt.LTTConfig;
import jp.co.freedom.master.utilities.ltt.LTTExhibitorUtil;

/**
 * 来場履歴付き当日登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterPreDataWithRfidDLForLtt", urlPatterns = { "/MasterPreDataWithRfidDLForLtt" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class MasterPreDataWithRfidDLForLtt extends HttpServlet {

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

		// 事前バーコード変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				LTTConfig.CONVERT_TABLE_DIRECTORY,
				LTTConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE, true, "\t",
				LTTConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		
		// 出力ファイル名
		String outputFileName = LTTConfig.PREENTRY_MASTERFILE_NAME;

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
			/*
			 * RFIDデータのコピー
			 */
			for (LttUserDataDto userData : allPreRegistDataList) {
				LttUserDataDto rfidDetailInfo = preRegistInfoMap.get(userData.id);
				if (rfidDetailInfo != null) {
					userData.visitFlgs = rfidDetailInfo.visitFlgs;
				}
			}
			if (!LTTExhibitorUtil.downLoadForApp(request, response, conn,
					outputFileName, allPreRegistDataList, Config.DELIMITER_TAB, false,
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

}