package jp.co.freedom.master.basf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.master.dto.basf.BasfUserDataDto;
import jp.co.freedom.master.utilities.basf.BasfConfig;
import jp.co.freedom.master.utilities.basf.BasfUtil;

/**
 * 【BASF】マスターデータのダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class MasterDataDLForBasf extends HttpServlet {

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
		String outputFileName = BasfConfig.MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(BasfConfig.PSQL_URL,
					BasfConfig.PSQL_USER, BasfConfig.PSQL_PASSWORD);
			// マスターデータ取得
			List<BasfUserDataDto> userDataList = BasfUtil.getAllData(conn);
			if (!BasfUtil.downLoad(request, response, outputFileName,
					userDataList, ",")) {
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