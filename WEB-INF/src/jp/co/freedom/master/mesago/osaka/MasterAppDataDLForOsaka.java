package jp.co.freedom.master.mesago.osaka;

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
import jp.co.freedom.master.utilities.mesago.osaka.OsakaConfig;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaUtil;

/**
 * 来場履歴付き当日登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterAppDataDLForOsaka", urlPatterns = { "/MasterAppDataDLForOsaka" })
public class MasterAppDataDLForOsaka extends HttpServlet {

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
		String outputFileName = OsakaConfig.APPOINTEDDAY_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(
					OsakaConfig.PSQL_URL_LOCAL_OSAKA2014, OsakaConfig.PSQL_USER,
					OsakaConfig.PSQL_PASSWORD_LOCAL);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = OsakaUtil
					.getAllAppointedDayData(conn);

			if (!OsakaUtil.downLoad(request, response, outputFileName, conn,
					allAppointedDataList, Config.DELIMITER_TAB, false, true,
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