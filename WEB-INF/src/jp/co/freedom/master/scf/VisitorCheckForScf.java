package jp.co.freedom.master.scf;

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

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.scf.ScfUserDataDto;
import jp.co.freedom.master.utilities.scf.ScfConfig;
import jp.co.freedom.master.utilities.scf.ScfUtil;

/**
 * 【SCF】来場チェック結果をDB保存するサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class VisitorCheckForScf extends HttpServlet {

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

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(
				ScfConfig.barcodeDataDirectory,
				ScfConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				ScfConfig.REMOVE_HEADER_RECORD, ",",
				ScfConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// ユーザーデータを保持するリスト
		List<ScfUserDataDto> userDataList = ScfUtil.createInstance(csvData);
		assert userDataList != null;

		// ユニークIDの抽出
		List<String> uniqueList = new ArrayList<String>();
		for (ScfUserDataDto userInfo : userDataList) {
			String id = userInfo.id;
			if (!uniqueList.contains(id)) {
				uniqueList.add(id);
			}
		}
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(ScfConfig.PSQL_URL,
					ScfConfig.PSQL_USER, ScfConfig.PSQL_PASSWORD);
			// DB保存
			for (String id : uniqueList) {
				if (ScfUtil.isPreEntry(id)) {
					ScfUtil.updateVisitorFlg(conn, id, "preentry");
				} else if (ScfUtil.isAppEntry(id)) {
					ScfUtil.updateVisitorFlg(conn, id, "appointedday");
				} else {
					assert false;
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
		// JSPにフォワード
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/result.jsp");
		dispatcher.forward(request, response);
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