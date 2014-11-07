package jp.co.freedom.master.enquete;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.enquete.EnqueteQuestionDto;
import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.utilities.enquete.jismee.JismeeConfig;
import jp.co.freedom.master.utilities.enquete.jismee.JismeeUtil;

/**
 * DB格納アンケート集計用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class DbEnqueteDataMining extends HttpServlet {

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
		// イベント名
		String event = new String(request.getParameter("event").getBytes(
				"ISO_8859_1"), "UTF-8");
		String psqlUrl = null;
		if ("jismee".equals(event)) {
			psqlUrl = JismeeConfig.PSQL_URL;
		}
		assert StringUtil.isNotEmpty(psqlUrl);

		// 出力ファイル名
		String outputFileName = JismeeConfig.MASTERFILE_NAME;

		// ユーザーデータを保持するリスト
		List<EnqueteQuestionDto> enqueteDataList = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(psqlUrl,
					Config.PSQL_USER, Config.PSQL_PASSWORD_LOCAL);
			if ("jismee".equals(event)) {
				enqueteDataList = JismeeUtil.getAllData(conn);
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

		if ("jismee".equals(event)) {
			if (!JismeeUtil.downLoad(request, response, outputFileName,
					enqueteDataList, "\t")) {
				System.out.println("Error: Failed download CSV file");
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