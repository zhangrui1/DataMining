package jp.co.freedom.promptreport.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.promptreport.util.Utilities;

/**
 * MESAGO向けCSVデータのアップロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class ResetDBForMesagoPromptReport extends HttpServlet {

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

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE, MesagoConfig.PSQL_USER,
					MesagoConfig.PSQL_PASSWORD_REMOTE);
			Utilities.deleteAllData(conn); // rfidテーブルの全てのデータを削除
			conn.close();
			request.setAttribute("backURL", "./html/prompt/mesago.html");
			request.setAttribute("result", "DBのリセット操作が完了しました");
			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/dbOperation.jsp");
			dispatcher.forward(request, response);
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