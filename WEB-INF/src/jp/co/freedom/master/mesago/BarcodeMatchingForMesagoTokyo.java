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
 * 【BWJTokyo2014】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeMatchingForMesagoTokyo", urlPatterns = { "/BarcodeMatchingForMesagoTokyo" })
public class BarcodeMatchingForMesagoTokyo extends HttpServlet {

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

		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(
				MesagoConfig.barcodeDataDirectory, true, true,
				Config.DELIMITER_COMMNA, MesagoConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;
		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				MesagoConfig.CONVERT_TABLE_DIRECTORY,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION, true,
				Config.DELIMITER_TAB, MesagoConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 変換表の作成
		List<MesagoUserDataDto> convertTableList = MesagoExhibitorUtil
				.createInstanceForConvertTable(convertTableData);
		for (MesagoUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
				String id = data.id;
				id = id.substring(1, id.length() - 1);
				this.convertTable.put(id, data.cardInfo.V_CID);
			} else {
				assert false;
			}
		}
		assert convertTableList != null;

		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(MesagoConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = MesagoUtil
				.createBwjInstance(csvData);
		assert userDataList != null;

		// アンマッチバーコード番号リスト
		List<MesagoUserDataDto> unmatchList = new ArrayList<MesagoUserDataDto>();
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
			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = MesagoUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = MesagoUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, MesagoUserDataDto> appointedInfoMap = MesagoUtil
					.getMap(allAppointedDataList);
			// RFIDデータMap
			Map<String, MesagoRfidDto> allRfidInfoMap = MesagoUtil
					.getAllRfidMap(conn);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (MesagoUserDataDto userData : userDataList) {
				MesagoRfidDto rfidInfo = allRfidInfoMap.get(userData.id);
				// サブバーコードの存在確認
				String subBarcode = MesagoConstants.SUB_BARCODES_MAP
						.get(userData.id);
				if (StringUtil.isNotEmpty(subBarcode)) { // サブバーコードの保存
					userData.subBarcode = userData.id;
					userData.id = subBarcode;
				}
				userData.cardInfo.V_CID = getPreEntryId(userData.id);
				if (rfidInfo != null) {
					userData.visitFlgs = rfidInfo.visitFlgs;
				} else {
					// サブバーコードでアンマッチングになる場合は変換前バーコードで再度検証
					if (StringUtil.isNotEmpty(subBarcode)) {
						userData.id = userData.subBarcode;
						userData.subBarcode = null;
						userData.cardInfo.V_CID = getPreEntryId(userData.id);
						MesagoRfidDto rfidInfo2 = allRfidInfoMap
								.get(userData.id);
						if (rfidInfo2 != null) {
							userData.visitFlgs = rfidInfo2.visitFlgs;
						}
					}
				}
				if (StringUtil.isNotEmpty(userData.cardInfo.V_CID)) {
					// 事前登録ユーザーである場合
					MesagoUserDataDto tmpData = preRegistInfoMap
							.get(userData.cardInfo.V_CID);
					if (tmpData != null) {
						userData.preentry = tmpData.preentry;
						userData.appointedday = tmpData.appointedday;
						userData.vipInvitation = tmpData.vipInvitation;
						userData.invitation = tmpData.invitation;
						userData.press = tmpData.press;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					// 当日登録ユーザーである場合
					MesagoUserDataDto tmpData = appointedInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.preentry = tmpData.preentry;
						userData.appointedday = tmpData.appointedday;
						userData.vipInvitation = tmpData.vipInvitation;
						userData.invitation = tmpData.invitation;
						userData.press = tmpData.press;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!MesagoUtil.downLoad(request, response, outputFileName,
						conn, userDataList, "\t", false, false, true)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else { // アンマッチバーコード番号リストのダウンロード
				if (!MesagoUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
					System.out.println("Error: Failed download CSV file");
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