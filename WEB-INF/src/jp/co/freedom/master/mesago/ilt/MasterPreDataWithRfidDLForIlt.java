package jp.co.freedom.master.mesago.ilt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import jp.co.freedom.master.utilities.ilf.ILFMesagoExhibitorUtil;
import jp.co.freedom.master.utilities.mesago.ilt.IltConfig;
import jp.co.freedom.master.utilities.mesago.ilt.IltUtil;

/**
 * 来場履歴付き事前登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterPreDataWithRfidDLForIlt", urlPatterns = { "/MasterPreDataWithRfidDLForIlt" })
public class MasterPreDataWithRfidDLForIlt extends HttpServlet {

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
				IltConfig.CONVERT_TABLE_DIRECTORY,
				IltConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				IltConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_TAB,
				IltConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 変換表の作成(登録券番号⇒バーコード番号)
		List<MesagoUserDataDto> convertTableList = ILFMesagoExhibitorUtil
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
		String outputFileName = IltConfig.PREENTRY_MASTERFILE_NAME;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(IltConfig.PSQL_URL_REMOTE,
					IltConfig.PSQL_USER, IltConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = IltUtil
					.getAllPreRegistData(conn, true);
			// RFIDデータMap
			Map<String, MesagoRfidDto> allRfidInfoMap = IltUtil
					.getAllRfidMap(conn);

			/*
			 * RFIDデータのコピー
			 */
			for (MesagoUserDataDto userData : allPreRegistDataList) {
				String barcode = getPreEntryId(userData.id); // 登録券番号からバーコード番号を特定
				MesagoRfidDto rfidDetailInfo = allRfidInfoMap.get(barcode);
				if (rfidDetailInfo != null) {
					userData.visitFlgs = rfidDetailInfo.visitFlgs;
				}
			}

			if (!IltUtil.downLoad(request, response, outputFileName, conn,
					allPreRegistDataList, Config.DELIMITER_TAB, true, false,
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
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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