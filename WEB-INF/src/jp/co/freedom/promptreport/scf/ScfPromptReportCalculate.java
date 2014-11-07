package jp.co.freedom.promptreport.scf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.promptreport.util.SqlUtil;
import jp.co.freedom.promptreport.util.Utilities;

/**
 * データ集計用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfPromptReportCalculate extends HttpServlet {

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
					ScfPromptReportConfig.PSQL_URL,
					ScfPromptReportConfig.PSQL_USER,
					ScfPromptReportConfig.PSQL_PASSWORD);

			List<Integer> newRegist = new ArrayList<Integer>();
			List<Integer> dupRegist = new ArrayList<Integer>();
			List<Integer> repeat = new ArrayList<Integer>();

			/* 新規登録 */
			// 11月6日
			StringBuffer query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, false);
			newRegist.add(Utilities.getVisitorNumBySQL(conn, query));
			// 11月7日
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, true);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, false);
			newRegist.add(Utilities.getVisitorNumBySQL(conn, query));
			// 11月8日
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, true);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, true);
			SqlUtil.appendIntegerQuery(query, "8_cnt", 0, false);
			newRegist.add(Utilities.getVisitorNumBySQL(conn, query));

			/* Total(重複含む) */
			dupRegist.add(Utilities.getVisitorNum(conn, "6_cnt"));
			dupRegist.add(Utilities.getVisitorNum(conn, "7_cnt"));
			dupRegist.add(Utilities.getVisitorNum(conn, "8_cnt"));

			/* リピーター */
			// 11月6日
			repeat.add(0);
			// 11月7日
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, false);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, false);
			repeat.add(Utilities.getVisitorNumBySQL(conn, query));
			// 11月8日
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, false);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, true);
			SqlUtil.appendIntegerQuery(query, "8_cnt", 0, false);
			int day6_8 = Utilities.getVisitorNumBySQL(conn, query);
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, true);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, false);
			SqlUtil.appendIntegerQuery(query, "8_cnt", 0, false);
			int day7_8 = Utilities.getVisitorNumBySQL(conn, query);
			query = new StringBuffer();
			SqlUtil.appendIntegerQuery(query, "6_cnt", 0, false);
			SqlUtil.appendIntegerQuery(query, "7_cnt", 0, false);
			SqlUtil.appendIntegerQuery(query, "8_cnt", 0, false);
			int day6_7_8 = Utilities.getVisitorNumBySQL(conn, query);
			repeat.add(day6_8 + day7_8 + day6_7_8);

			int totalNewRegist = Utilities.sum(newRegist);
			int totalRepeat = Utilities.sum(repeat);
			int totaldupRegist = Utilities.sum(dupRegist);

			// JSPへのデータ引継ぎ
			request.setAttribute("newRegist", newRegist);
			request.setAttribute("repeat", repeat);
			request.setAttribute("dupRegist", dupRegist);

			request.setAttribute("totalNewRegist", totalNewRegist);
			request.setAttribute("totalRepeat", totalRepeat);
			request.setAttribute("totaldupRegist", totaldupRegist);

			// 11/8のリピーター数詳細
			request.setAttribute("day6_8", day6_8);
			request.setAttribute("day7_8", day7_8);
			request.setAttribute("day6_7_8", day6_7_8);
			request.setAttribute("backURL", "./html/prompt/scf2013.html");

			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/scf/result.jsp");
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