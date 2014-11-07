package jp.co.freedom.master.noma;

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
import jp.co.freedom.master.dto.noma.NomaUserDataDto;
import jp.co.freedom.master.utilities.noma.NomaConfig;
import jp.co.freedom.master.utilities.noma.NomaExhibitorUtil;

/**
 * 【NOMA】当日登録マスターデータDLを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterAppDataDLForNoma", urlPatterns = { "/MasterAppDataDLForNoma" })
public class MasterAppDataDLForNoma extends HttpServlet {

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
		String outputFileName = NomaConfig.APPOINTEDDAY_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(NomaConfig.PSQL_URL_LOCAL,
					NomaConfig.PSQL_USER, NomaConfig.PSQL_PASSWORD_LOCAL);
			// 当日入力の全データ
			List<NomaUserDataDto> allAppRegistDataList = NomaExhibitorUtil
					.getAllAppointedDayData(conn);
			if (!NomaExhibitorUtil.downLoad(request, response, conn,
					outputFileName, allAppRegistDataList, Config.DELIMITER_TAB,
					false, false, true)) {
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