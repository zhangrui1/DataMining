package jp.co.freedom.promptreport.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.promptreport.dto.BarcodeDto;
import jp.co.freedom.promptreport.util.Utilities;

/**
 * MESAGO向けCSVデータのアップロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class InitializeDBForMesagoPromptReport extends HttpServlet {

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
		List<String[]> allData = FileUtil.loadCsv(
				MesagoConfig.barcodeDataDirectory,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				MesagoConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				MesagoConfig.ALLOWS_EXTENSIONS);
		assert allData != null;
		// バーコードデータ
		// TODO:　暫定的に事前登録番号をバーコード番号とする
		List<BarcodeDto> barcodes = Utilities.createInstance(allData, false);
		// ユニークIDの抽出
		List<String> unique = Utilities.getUniqueList(barcodes);
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE, MesagoConfig.PSQL_USER,
					MesagoConfig.PSQL_PASSWORD_REMOTE);
			Utilities.initializeRfidTable(conn, unique,
					MesagoPromptReportConfig.DAYS.length); // RFIDテーブルにユニークバーコード番号を登録する
			Utilities.storeRfidData(conn, barcodes,
					MesagoPromptReportConfig.START_INDEX_DAY_IN_TIMESTAMP,
					MesagoPromptReportConfig.END_INDEX_DAY_IN_TIMESTAMP,
					MesagoPromptReportConfig.DAYS); // バーコードデータのストア
			conn.close();
			request.setAttribute("backURL", "./html/prompt/scf2013.html");
			request.setAttribute("result", "バーコードデータのDB登録が完了しました");
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