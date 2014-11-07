package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【MESAGO】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class BarcodeMatchingForMesago extends HttpServlet {

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

		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(
				MesagoConfig.barcodeDataDirectory,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				MesagoConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				MesagoConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(MesagoConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = MesagoUtil
				.createInstance(csvData);
		assert userDataList != null;

		// アンマッチバーコード番号リスト
		List<MesagoUserDataDto> unmatchList = new ArrayList<MesagoUserDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE, MesagoConfig.PSQL_USER,
					MesagoConfig.PSQL_PASSWORD_REMOTE);

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

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			MesagoUtil util = new MesagoUtil();
			for (MesagoUserDataDto userData : userDataList) {
				if (util.isPreEntry(userData)) {
					// 事前登録ユーザーである場合
					MesagoUserDataDto tmpData = preRegistInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.visitFlgs = tmpData.visitFlgs;
					}
				} else if (util.isAppEntry(userData)) {
					// 当日登録ユーザーである場合
					MesagoUserDataDto tmpData = appointedInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						userData.visitFlgs = tmpData.visitFlgs;
					}
				} else {
					assert false;
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
}