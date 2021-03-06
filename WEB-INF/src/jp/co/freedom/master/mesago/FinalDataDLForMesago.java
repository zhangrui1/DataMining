package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.FinalDataDLForMesagoUtil;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaConfig;

/**
 * 【MESAGO】クレンジング後の最終データDL用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class FinalDataDLForMesago extends HttpServlet {

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

		// 出力ファイル名
		String outputFileName = MesagoConfig.FINAL_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
//			conn = DriverManager.getConnection(MesagoConfig.PSQL_URL_REMOTE_IP2014,
//					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			conn = DriverManager.getConnection(
					OsakaConfig.PSQL_URL_LOCAL_IFFT2014, OsakaConfig.PSQL_USER,
					OsakaConfig.PSQL_PASSWORD_LOCAL);
			List<MesagoUserDataDto> userDataList = FinalDataDLForMesagoUtil
					.getData(conn, mode);
			if (!FinalDataDLForMesagoUtil.downLoadForFinalData(request,
					response, outputFileName, conn, userDataList,
					Config.DELIMITER_TAB)) {
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