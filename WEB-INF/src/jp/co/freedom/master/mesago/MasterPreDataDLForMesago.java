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
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【MESAGO】事前登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class MasterPreDataDLForMesago extends HttpServlet {

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
		Object visitObj = request.getParameter("visit");
		String visit = (String) visitObj;
		assert StringUtil.isNotEmpty(visit);
		boolean visitFlg = StringUtil.toBoolean(visit);

		// 出力ファイル名
		String outputFileName = MesagoConfig.PREENTRY_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO, MesagoConfig.PSQL_USER,
					MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録ユーザーである場合
			List<MesagoUserDataDto> userDataList = MesagoUtil
					.getAllPreRegistData(conn, visitFlg);
			if (!MesagoUtil.downLoad(request, response, outputFileName, conn,
					userDataList, Config.DELIMITER_TAB, true, false, visitFlg)) {
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