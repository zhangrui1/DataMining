package jp.co.freedom.master.yakiniku;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.yakiniku.YakinikuUserDataDto;
import jp.co.freedom.master.utilities.yakiniku.YakinikuConfig;
import jp.co.freedom.master.utilities.yakiniku.YakinikuUtil;

/**
 * 【YAKINIKU】当日登録マスターデータのダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class MasterAppDataDLForYakiniku extends HttpServlet {

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
		String outputFileName = YakinikuConfig.APPOINTEDDAY_MASTERFILE_NAME;
		
		// 都道府県
		Object cityObj = request.getParameter("city");
		String city = (String) cityObj;
		assert StringUtil.isNotEmpty(city);

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(YakinikuConfig.PSQL_URL,
					YakinikuConfig.PSQL_USER, YakinikuConfig.PSQL_PASSWORD);
			// 当日登録ユーザーである場合
			List<YakinikuUserDataDto> userDataList = YakinikuUtil
					.getAllAppointedDayData(conn,city);
			if (!YakinikuUtil.downLoad(request, response, outputFileName,
					userDataList, "\t",city)) {
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