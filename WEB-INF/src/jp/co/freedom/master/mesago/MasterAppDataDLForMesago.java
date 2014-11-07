package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 当日登録マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class MasterAppDataDLForMesago extends HttpServlet {

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

		// ユニークキー生成の有無
		Object createUniqueKeyObj = request.getParameter("createUniqueKey");
		String createUniqueKey = (String) createUniqueKeyObj;
		assert StringUtil.isNotEmpty(createUniqueKey);

		// 出力ファイル名
		String outputFileName = MesagoConfig.APPOINTEDDAY_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE, MesagoConfig.PSQL_USER,
					MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録ユーザーである場合
			List<MesagoUserDataDto> userDataList = MesagoUtil
					.getAllAppointedDayData(conn);
			// 登録券番号の生成
			if ("true".equals(createUniqueKey)) {
				int uniqueKey = 0; // ユニークキー
				List<String> uniqueNameList = new ArrayList<String>();
				Map<String, String> map = new LinkedHashMap<String, String>();
				for (MesagoUserDataDto userdata : userDataList) {
					String name = StringUtil.concatWithDelimit("",
							userdata.cardInfo.V_NAME1,
							userdata.cardInfo.V_NAME2,
							userdata.cardInfo.V_EMAIL);
					// String name = StringUtil.concatWithDelimit("",
					// userdata.cardInfo.V_NAME1,
					// userdata.cardInfo.V_NAME2);
					String preentryId;
					if (StringUtil.isEmpty(name)) {
						preentryId = StringUtil.convertFixLengthFromInteger(
								++uniqueKey, 8);
					} else if (!uniqueNameList.contains(name)) {
						preentryId = StringUtil.convertFixLengthFromInteger(
								++uniqueKey, 8);
						uniqueNameList.add(name);
						map.put(name, preentryId);
					} else {
						preentryId = map.get(name);
					}
					((MesagoCardDto) userdata.cardInfo).PREENTRY_ID = preentryId;
				}
			}
			if (!MesagoUtil.downLoad(request, response, outputFileName, conn,
					userDataList, Config.DELIMITER_TAB, false, true, true)) {
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