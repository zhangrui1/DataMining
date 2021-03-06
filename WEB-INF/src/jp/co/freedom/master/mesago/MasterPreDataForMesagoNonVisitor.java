package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import jp.co.freedom.master.utilities.mesago.MesagoExhibitorUtil;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 事前登録未来場者データのダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
@WebServlet(name = "MasterPreDataForMesagoNonVisitor", urlPatterns = { "/MasterPreDataForMesagoNonVisitor" })
public class MasterPreDataForMesagoNonVisitor extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 変換表 */
	private Map<String, String> convertTable = new HashMap<String, String>();

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
		// 変換表の作成(登録券番号⇒バーコード番号)
		List<MesagoUserDataDto> convertTableList = MesagoExhibitorUtil
				.createInstanceForConvertTable(convertTableData);
		for (MesagoUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
				String id = data.id;
				id = id.substring(1, id.length() - 1);
				this.convertTable.put(data.cardInfo.V_CID, id);
			} else {
				assert false;
			}
		}
		assert convertTableList != null;

		// 出力ファイル名
		String outputFileName = MesagoConfig.PREENTRY_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = MesagoUtil
					.getAllPreRegistData(conn, true);
			// RFIDデータMap
			Map<String, MesagoRfidDto> allRfidInfoMap = MesagoUtil
					.getAllRfidMap(conn);

			// 事前未来場者データリスト
			List<MesagoUserDataDto> nonPreEntryVisitors = new ArrayList<MesagoUserDataDto>();

			/*
			 * RFIDデータのコピー
			 */
			for (MesagoUserDataDto userData : allPreRegistDataList) {
				String barcode = getPreEntryId(userData.id); // 登録券番号からバーコード番号を特定
				/*
				 * サブバーコードが存在する場合はRFIDデータ参照のためにバーコードを差し替える
				 */
				String subBarcode = null;
				if (StringUtil.isNotEmpty(barcode)) {
					subBarcode = MesagoConstants.SUB_BARCODES_CONVERT_MAP
							.get(barcode);
				}
				if (StringUtil.isNotEmpty(subBarcode)) {
					barcode = subBarcode;
				}
				MesagoRfidDto rfidDetailInfo = allRfidInfoMap.get(barcode);
				if (rfidDetailInfo == null) {
					nonPreEntryVisitors.add(userData);
				}
			}

			if (!MesagoUtil.downLoad(request, response, outputFileName, conn,
					nonPreEntryVisitors, Config.DELIMITER_TAB, true, false,
					false, false)) {
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

	/**
	 * 事前登録発券番号を取得
	 * 
	 * @param id
	 *            バーコード番号
	 * @return 事前登録発券番号
	 */
	private String getPreEntryId(String id) {
		assert StringUtil.isNotEmpty(id);
		String value = this.convertTable.get(id);
		return value;
	}
}