package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import jp.co.freedom.master.utilities.mesago.DataCollectingForMesagoUtil;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;

/**
 * 【MESAGO】データ修正用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
@WebServlet(name = "DataCollectingForSecondTableMesago", urlPatterns = { "/DataCollectingForSecondTableMesago" })
public class DataCollectingForSecondTableMesago extends HttpServlet {

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
				MesagoConfig.barcodeDataDirectory,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				MesagoConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_TAB,
				MesagoConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// ユーザーデータのインスタンス化
		List<MesagoUserDataDto> userDataList = DataCollectingForMesagoUtil
				.createInstanceForSecond (csvData);

		// JDBCドライバーのロード
		DataCleansingForMesagoUtil.loadJdbcPluginJar();
		Connection conn = null;
		try {
			// DBサーバーへの接続情報
			conn = DriverManager.getConnection(MesagoConfig.PSQL_URL_LOCAL_ILF2014,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_LOCAL);
			// データ修復
			DataCollectingForMesagoUtil.collectForSecond(conn, userDataList);
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