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

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoExhibitorUtil;

/**
 * 【Mesago】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeExhibitorMatchingForMesago", urlPatterns = { "/BarcodeExhibitorMatchingForMesago" })
public class BarcodeExhibitorMatchingForMesago extends HttpServlet {

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
				MesagoConfig.barcodeDataDirectory,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				MesagoConfig.REMOVE_HEADER_RECORD, ",",
				MesagoConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;
		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				MesagoConfig.CONVERT_TABLE_DIRECTORY,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION, true, "\t",
				MesagoConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(MesagoConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = MesagoExhibitorUtil
				.createInstance(csvData);
		assert userDataList != null;

		// 変換表の作成
		List<MesagoUserDataDto> convertTableList = MesagoExhibitorUtil
				.createInstanceForConvertTable(convertTableData);
		for (MesagoUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
				String id = data.id;
				id = id.substring(1, id.length() - 1);
				if (!this.convertTable.containsKey(id)) {
					this.convertTable.put(id, data.cardInfo.V_CID);
				} else {
					System.out.println("変換表データに重複発見");
					System.out.println("◆バーコード=" + id);
					System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
				}
			} else {
				System.out.println("変換表データが不正");
				System.out.println("◆バーコード=" + data.id);
				System.out.println("◆登録券番号=" + data.cardInfo.V_CID);
			}
		}
		assert convertTableList != null;

		// アンマッチバーコード番号リスト
		List<MesagoUserDataDto> unmatchList = new ArrayList<MesagoUserDataDto>();
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = MesagoExhibitorUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = MesagoExhibitorUtil
					.getPreEntryMap(allPreRegistDataList);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = MesagoExhibitorUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, MesagoUserDataDto> appointedInfoMap = MesagoExhibitorUtil
					.getAppointedDayMap(allAppointedDataList);
			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (MesagoUserDataDto userData : userDataList) {
				userData.cardInfo.V_CID = getPreEntryId(userData.id);
				if (StringUtil.isNotEmpty(userData.cardInfo.V_CID)) {
					// 事前登録ユーザーである場合
					MesagoUserDataDto tmpData = preRegistInfoMap
							.get(userData.cardInfo.V_CID);
					if (tmpData != null) {
						userData.preentry = true;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					// 当日登録ユーザーである場合
					MesagoUserDataDto tmpData = appointedInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.appointedday = true;
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
				if (!MesagoExhibitorUtil.downLoad(request, response, conn,
						outputFileName, userDataList, "\t", false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else if ("unmatch".equals(mode)) { // アンマッチバーコード番号リストのダウンロード
				if (!MesagoExhibitorUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
					System.out.println("Error: Failed download CSV file");
				}
			}
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