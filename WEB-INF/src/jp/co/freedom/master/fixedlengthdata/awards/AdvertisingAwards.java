package jp.co.freedom.master.fixedlengthdata.awards;

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

/**
 * 【広告対象】審査表データ マスターデータDL用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class AdvertisingAwards extends HttpServlet {

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

		// 地区ID
		Object areaObj = request.getParameter("area");
		String area = (String) areaObj;
		assert StringUtil.isNotEmpty(area);
		// 出力モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);

		// 出力ファイル名
		String outputFileName;
		if ("1".equals(mode)) {
			outputFileName = AdvertisingAwardsConfig.MASTERDATA_FILE_NAME;
		} else {
			outputFileName = AdvertisingAwardsConfig.MASTERDATA_DEBUG_FILE_NAME;
		}

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					AdvertisingAwardsConfig.PSQL_URL,
					AdvertisingAwardsConfig.PSQL_USER,
					AdvertisingAwardsConfig.PSQL_PASSWORD);
			// マスターデータの取得
			List<AdvertisingAwardsDto> userDataList = AdvertisingAwardsUtil
					.getMasterData(conn);
			if ("1".equals(mode)) {
				if (!AdvertisingAwardsUtil.downLoad(request, response,
						outputFileName, userDataList, area)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else {
				if (!AdvertisingAwardsUtil.downLoadCsv(request, response,
						outputFileName, userDataList, area)) {
					System.out.println("Error: Failed download CSV file");
				}
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