package jp.co.freedom.master.mesago.ilt;

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
import jp.co.freedom.master.utilities.ilf.ILFMesagoExhibitorUtil;
import jp.co.freedom.master.utilities.mesago.ilt.IltConfig;

/**
 * 【ILT】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeExhibitorMatchingForILF", urlPatterns = { "/BarcodeExhibitorMatchingForILF" })
public class BarcodeExhibitorMatchingForILF extends HttpServlet {

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
				IltConfig.barcodeDataDirectoryIltExhibitor,
				IltConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				IltConfig.REMOVE_HEADER_RECORD_FALSE, ",",
				IltConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;
		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				IltConfig.CONVERT_TABLE_DIRECTORY,
				IltConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE, true, "\t",
				IltConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(IltConfig.barcodeDataDirectoryIltExhibitor);
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = ILFMesagoExhibitorUtil
				.createInstance(csvData);
		assert userDataList != null;

		// 変換表の作成
		List<MesagoUserDataDto> convertTableList = ILFMesagoExhibitorUtil
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
			conn = DriverManager.getConnection(IltConfig.PSQL_URL_REMOTE,
					IltConfig.PSQL_USER, IltConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = ILFMesagoExhibitorUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = ILFMesagoExhibitorUtil
					.getPreEntryMap(allPreRegistDataList);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = ILFMesagoExhibitorUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, MesagoUserDataDto> appointedInfoMap = ILFMesagoExhibitorUtil
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
				if (!ILFMesagoExhibitorUtil.downLoad(request, response, conn,
						outputFileName, userDataList, "\t", false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else if ("unmatch".equals(mode)) { // アンマッチバーコード番号リストのダウンロード
				if (!ILFMesagoExhibitorUtil.downLoadForUnmatch(request,
						response, outputFileName, unmatchList, "\t")) {
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