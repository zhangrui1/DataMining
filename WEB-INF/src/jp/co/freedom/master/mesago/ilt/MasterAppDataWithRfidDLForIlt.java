package jp.co.freedom.master.mesago.ilt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.mesago.MesagoRfidDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.ilt.IltConfig;
import jp.co.freedom.master.utilities.mesago.ilt.IltUtil;

/**
 * 来場履歴付き当日登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterAppDataWithRfidDLForIlt", urlPatterns = { "/MasterAppDataWithRfidDLForIlt" })
public class MasterAppDataWithRfidDLForIlt extends HttpServlet {

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

		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				IltConfig.CONVERT_TABLE_DIRECTORY,
				IltConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				IltConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_TAB,
				IltConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;

		// 出力ファイル名
		String outputFileName = IltConfig.APPOINTEDDAY_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(IltConfig.PSQL_URL_REMOTE,
					IltConfig.PSQL_USER, IltConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = IltUtil
					.getAllAppointedDayData(conn);
			// RFIDデータMap
			Map<String, MesagoRfidDto> allRfidInfoMap = IltUtil
					.getAllRfidMap(conn);

			/*
			 * RFIDデータのコピー
			 */
			for (MesagoUserDataDto userData : allAppointedDataList) {
				MesagoRfidDto rfidDetailInfo = allRfidInfoMap.get(userData.id);
				if (rfidDetailInfo != null) {
					userData.visitFlgs = rfidDetailInfo.visitFlgs;
				}
			}

			if (!IltUtil.downLoad(request, response, outputFileName, conn,
					allAppointedDataList, Config.DELIMITER_TAB, true, false,
					false)) {
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