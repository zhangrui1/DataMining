package jp.co.freedom.master.mesago.osaka;

import java.io.IOException;
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
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaConfig;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaUtil;

/**
 * 【BWJTokyo2014】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeMatchingForMesagoOsaka", urlPatterns = { "/BarcodeMatchingForMesagoOsaka" })
public class BarcodeMatchingForMesagoOsaka extends HttpServlet {

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
				OsakaConfig.barcodeDataDirectory, false, true,
				Config.DELIMITER_COMMNA, OsakaConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;


		// 出力ファイル名
		String outputFileName =OsakaConfig.MATCHING_RESULT_FILE_NAME;
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = OsakaUtil
				.createInstance(csvData);
		assert userDataList != null;

		// アンマッチバーコード番号リスト
		List<MesagoUserDataDto> unmatchList = new ArrayList<MesagoUserDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			java.sql.Connection conn = DriverManager.getConnection(
					OsakaConfig.PSQL_URL_LOCAL_OSAKA2014, OsakaConfig.PSQL_USER,
					OsakaConfig.PSQL_PASSWORD_LOCAL);

			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = MesagoUtil
					.getAllPreRegistData(conn, true);

			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = MesagoUtil
					.getMap(allPreRegistDataList);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (MesagoUserDataDto userData : userDataList) {

				if (StringUtil.isNotEmpty(userData.id)) {
					// 事前登録ユーザーである場合
					MesagoUserDataDto tmpData = preRegistInfoMap
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
				if (StringUtil.isEmpty(userData.id)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!OsakaUtil.downLoad(request, response, outputFileName,
						conn, userDataList, "\t", false, false, true)) {
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