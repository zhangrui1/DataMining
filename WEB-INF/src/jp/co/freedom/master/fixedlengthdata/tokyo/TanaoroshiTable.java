package jp.co.freedom.master.fixedlengthdata.tokyo;

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
 * 【固定データ長】棚卸表DL用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class TanaoroshiTable extends HttpServlet {

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

		// 出力モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);

		// 出力ファイル名
		String outputFileName;
		if ("1".equals(mode)) {
			outputFileName = CommonConfig.TANAOROSHI_TABLE_FILE_NAME;
		} else {
			outputFileName = CommonConfig.TANAOROSHI_TABLE_CSV_FILE_NAME;
		}
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(CommonConfig.PSQL_URL_LOCAL,
					CommonConfig.PSQL_USER, CommonConfig.PSQL_PASSWORD_LOCAL);
			// マスターデータの取得
			List<TanaoroshiTableDto> userDataList = TanaoroshiTableUtil
					.getAllData(conn);
			if ("1".equals(mode)) {
				if (!TanaoroshiTableUtil.downLoad(request, response,
						outputFileName, userDataList)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else {
				if (!TanaoroshiTableUtil.downLoadCsv(request, response,
						outputFileName, userDataList)) {
					System.out.println("Error: Failed download CSV file");
				}
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