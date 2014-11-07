package jp.co.freedom.master.fpd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.fpd.FpdConfig;
import jp.co.freedom.master.utilities.fpd.FpdUtil;

/**
 * 事前登録マスターデータのダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class MasterPreDataDLForFpd extends HttpServlet {

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
		String outputFileName = FpdConfig.PREENTRY_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(FpdConfig.PSQL_URL,
					FpdConfig.PSQL_USER, FpdConfig.PSQL_PASSWORD);
			// 事前登録ユーザーである場合
			List<FpdUserDataDto> userDataList = FpdUtil
					.getAllPreRegistData(conn);
			if (!FpdUtil.downLoad(request, response, outputFileName,
					userDataList, ",", true, false)) {
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