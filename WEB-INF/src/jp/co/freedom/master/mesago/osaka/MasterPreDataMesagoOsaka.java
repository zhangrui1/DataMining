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
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaConfig;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaUtil;

/**
 * 【MESAGO】事前登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterPreDataMesagoOsaka", urlPatterns = { "/MasterPreDataMesagoOsaka" })
public class MasterPreDataMesagoOsaka extends HttpServlet {

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
					OsakaConfig.PSQL_URL_LOCAL_OSAKA2014, OsakaConfig.PSQL_USER,
					OsakaConfig.PSQL_PASSWORD_LOCAL);
			// 事前登録ユーザーである場合
			List<MesagoUserDataDto> userDataList = OsakaUtil
					.getAllPreRegistData(conn, false);
			if (!OsakaUtil.downLoad(request, response, outputFileName, conn,
					userDataList, Config.DELIMITER_TAB, true, false, false)) {
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