package jp.co.freedom.valdac;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.valdac.ValdacUserDataDto;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 *
 */
@WebServlet(name = "NewKoujiRelationTable", urlPatterns = { "/NewKoujiRelationTable" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class NewKoujiRelationTable extends HttpServlet {

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
		// 顧客名
		Object KCodeObj = request.getParameter("KCode");
		String TableName = (String) KCodeObj;
		assert StringUtil.isNotEmpty(TableName);
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(ValdacConfig.PSQL_URL,
					ValdacConfig.PSQL_USER, ValdacConfig.PSQL_PASSWORD);
			// 1.機器システムの全データ
			List<ValdacUserDataDto> allKikiSysIdData = ValdacUtilites
					.getKikiSysIdData(conn);
			// 機器システムの全データMap
			Map<String, ValdacUserDataDto> allKikiSysIdDataMap = ValdacUtilites
					.getallKikiSysIdDataMap(allKikiSysIdData);
			// 2.機器の全データ
			List<ValdacUserDataDto> allKikiDataList = ValdacUtilites
					.getKikiIdData(conn);
			// 機器システの全データMap
			Map<String, ValdacUserDataDto> allKikiDataMap = ValdacUtilites
					.getallKikiDataMap(allKikiDataList);
			// 3.工事の全データ
			List<ValdacUserDataDto> allKoujiDataList = ValdacUtilites
					.getKoujiIdData(conn);
			// 工事の全データMap
			Map<String, ValdacUserDataDto> allKoujiDataListMap = ValdacUtilites
					.getallKoujiDataListMap(allKoujiDataList);

			// 4.工事点検機器の全データ

			Map<String, List<ValdacUserDataDto>> allKoujiKikiData = new HashMap<String, List<ValdacUserDataDto>>();
			int mapSize = ValdacUtilites.KCORD_MAP.size();
			for (int nIndex = 1; nIndex <= ValdacUtilites.KCORD_MAP.size(); nIndex++) {
				String KCodename1 = ValdacUtilites.KCORD_MAP.get(String
						.valueOf(nIndex));
				List<ValdacUserDataDto> allKoujiKikiDataList = new ArrayList<ValdacUserDataDto>();
				if("k04".equals(TableName)){
					 allKoujiKikiDataList = ValdacUtilites
							.getKoujiKikiIdData(conn, KCodename1, request,
									 response);
				}else if ("k05".equals(TableName)){
					 allKoujiKikiDataList = ValdacUtilites
							.getTenkenRirekiIdData(conn, TableName);
				}

				for (ValdacUserDataDto userData : allKoujiKikiDataList) {

					// 工事である場合
					ValdacUserDataDto tmKoujiData = allKoujiDataListMap
							.get(userData.koujiIDOld);
					if (tmKoujiData != null) {
						userData.koujiID = tmKoujiData.koujiID;
					} else {
						System.out.println("該工事ID：" + userData.koujiIDOld);
					}

					// 機器システムIDである場合
					ValdacUserDataDto tmKikisysData = allKikiSysIdDataMap
							.get(userData.KikiSysIdOld);
					if (tmKikisysData != null) {
						userData.KikiSysId = tmKikisysData.KikiSysId;
					} else {
						System.out
								.println("該機器システムID：" + userData.KikiSysIdOld);
					}

					// 機器である場合
					ValdacUserDataDto tmKikiData = allKikiDataMap
							.get(userData.kikiIDOld);
					if (tmKikiData != null) {
						userData.kikiID = tmKikiData.kikiID;
					} else {
						System.out.println("該機器ID：" + userData.kikiIDOld);
					}
				}

				allKoujiKikiData.put(KCodename1,
						allKoujiKikiDataList);
			}
			if (!ValdacUtilites.downLoadKoujiRealation2(request, response,
					ValdacConfig.OUTPUT_FILENAME_RELATION, allKoujiKikiData,
					ValdacConfig.DELIMITER)) {
				System.out.println("Error: Failed download CSV file");
			}

			// List<ValdacUserDataDto> allKoujiKikiDataList = ValdacUtilites
			// .getKoujiKikiIdData(conn,KCodename);

			// 機器システムテーブルから

			// for (ValdacUserDataDto userData : allKoujiKikiData) {
			//
			// // 工事である場合
			// ValdacUserDataDto tmKoujiData = allKoujiDataListMap
			// .get(userData.koujiIDOld);
			// if (tmKoujiData != null) {
			// userData.koujiID = tmKoujiData.koujiID;
			// } else {
			// System.out
			// .println("該工事ID：" + userData.koujiIDOld);
			// }
			//
			// // 機器システムIDである場合
			// ValdacUserDataDto tmKikisysData = allKikiSysIdDataMap
			// .get(userData.KikiSysIdOld);
			// if (tmKikisysData != null) {
			// userData.KikiSysId = tmKikisysData.KikiSysId;
			// } else {
			// System.out
			// .println("該機器システムID：" + userData.KikiSysIdOld);
			// }
			//
			// // 機器である場合
			// ValdacUserDataDto tmKikiData = allKikiDataMap
			// .get(userData.kikiIDOld);
			// if (tmKikiData != null) {
			// userData.kikiID = tmKikiData.kikiID;
			// } else {
			// System.out
			// .println("該機器ID：" + userData.kikiIDOld);
			// }
			// }

			// マッチングデータのダウンロード.
			// if (!ValdacUtilites.downLoadKoujiRealation(request, response,
			// ValdacConfig.OUTPUT_FILENAME_RELATION, allKoujiKikiDataList,
			// Config.DELIMITER_TAB)) {
			// System.out.println("Error: Failed download CSV file");
			// }

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
}