package jp.co.freedom.master.mesago.ip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.ip.IpConfig;
import jp.co.freedom.master.utilities.mesago.ip.IpUtil;

/**
 * 来場履歴付き当日登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterAppDataDLForIp", urlPatterns = { "/MasterAppDataDLForIp" })
public class MasterAppDataDLForIp extends HttpServlet {

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

		// 出力ファイル名
		String outputFileName = IpConfig.APPOINTEDDAY_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(IpConfig.PSQL_URL_REMOTE,
					IpConfig.PSQL_USER, IpConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = IpUtil
					.getAllAppointedDayData(conn);
			// // RFIDデータMap
			// Map<String, MesagoRfidDto> allRfidInfoMap = IpUtil
			// .getAllRfidMap(conn);

			// /*
			// * RFIDデータのコピー
			// */
			// for (MesagoUserDataDto userData : allAppointedDataList) {
			// MesagoRfidDto rfidDetailInfo = allRfidInfoMap.get(userData.id);
			// if (rfidDetailInfo != null) {
			// userData.visitFlgs = rfidDetailInfo.visitFlgs;
			// }
			// }

			if (!IpUtil.downLoad(request, response, outputFileName, conn,
					allAppointedDataList, Config.DELIMITER_TAB, true, false,
					true)) {
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