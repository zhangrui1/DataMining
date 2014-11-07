package jp.co.freedom.master.mesago;

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
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【バッチ作成用】事前登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BadgePreentryDataDLForMesago", urlPatterns = { "/BadgePreentryDataDLForMesago" })
public class BadgePreentryDataDLForMesago extends HttpServlet {

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

		// 原票種別
		Object typeObj = request.getParameter("type");
		String type = (String) typeObj;
		assert StringUtil.isNotEmpty(type);

		// 出力ファイル名
		String outputFileName = MesagoConfig.BATCH_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録ユーザーである場合
			List<MesagoUserDataDto> userDataList = MesagoUtil
					.getAllBadgePreentryData(conn, type);
			if (!MesagoUtil
					.downLoadBadgePreentryData(request, response,
							outputFileName, conn, userDataList,
							Config.DELIMITER_COMMNA)) {
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