package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoRfidDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoConstants;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 当日登録未来場者データのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterAppointedDataForMesagoNonVisitor", urlPatterns = { "/MasterAppointedDataForMesagoNonVisitor" })
public class MasterAppointedDataForMesagoNonVisitor extends HttpServlet {

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

		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				MesagoConfig.CONVERT_TABLE_DIRECTORY,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION, true,
				Config.DELIMITER_TAB, MesagoConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;

		// 出力ファイル名
		String outputFileName = MesagoConfig.APPOINTEDDAY_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = MesagoUtil
					.getAllAppointedDayData(conn);
			// RFIDデータMap
			Map<String, MesagoRfidDto> allRfidInfoMap = MesagoUtil
					.getAllRfidMap(conn);

			// 当日未来場者データリスト
			List<MesagoUserDataDto> nonAppVisitors = new ArrayList<MesagoUserDataDto>();

			/*
			 * RFIDデータのコピー
			 */
			for (MesagoUserDataDto userData : allAppointedDataList) {
				String barcode = userData.id; // バーコード番号
				/*
				 * サブバーコードが存在する場合はRFIDデータ参照のためにバーコードを差し替える
				 */
				String subBarcode = MesagoConstants.SUB_BARCODES_CONVERT_MAP
						.get(userData.id);
				if (StringUtil.isNotEmpty(subBarcode)) {
					barcode = subBarcode;
				}
				MesagoRfidDto rfidDetailInfo = allRfidInfoMap.get(barcode);
				if (rfidDetailInfo == null) {
					nonAppVisitors.add(userData);
				}
			}
			if (!MesagoUtil.downLoad(request, response, outputFileName, conn,
					nonAppVisitors, Config.DELIMITER_TAB, true, false, false,
					false)) {
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