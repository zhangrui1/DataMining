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
@WebServlet(name = "Test2NewKoujiRelationTable", urlPatterns = { "/Test2NewKoujiRelationTable" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class Test2NewKoujiRelationTable extends HttpServlet {

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
		Object StartObj = request.getParameter("Start");
		String StartName = (String) StartObj;
		assert StringUtil.isNotEmpty(StartName);


		Object mode = request.getParameter("mode");
		String ModeName = (String) mode;
		assert StringUtil.isNotEmpty(ModeName);


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

			// 工事履歴の 工事IDがないデータMap
			List<ValdacUserDataDto> allNoMatchKoujiRirekiDataList = new ArrayList<ValdacUserDataDto>();


			Map<String, List<ValdacUserDataDto>> allKoujiKikiData = new HashMap<String, List<ValdacUserDataDto>>();
			int startnIndex=Integer.valueOf(StartName);
			for (int nIndex = startnIndex; nIndex < startnIndex+20; nIndex++) {
				List<ValdacUserDataDto>	 allKoujiKikiDataList = ValdacUtilites
							.getKoujiKikiIdDataForStep(conn, nIndex);

				for (ValdacUserDataDto userData : allKoujiKikiDataList) {

					// 工事である場合
					ValdacUserDataDto tmKoujiData = allKoujiDataListMap
							.get(userData.koujiIDOld);
					if (tmKoujiData != null) {
						userData.koujiID = tmKoujiData.koujiID;
					} else {
//						System.out.println("該工事ID：" + userData.koujiIDOld);
					}

					// 機器システムIDである場合
					ValdacUserDataDto tmKikisysData = allKikiSysIdDataMap
							.get(userData.KikiSysIdOld);
					if (tmKikisysData != null) {
						userData.KikiSysId = tmKikisysData.KikiSysId;
					} else {
//						System.out
//								.println("該機器システムID：" + userData.KikiSysIdOld);
					}

					// 機器である場合
					ValdacUserDataDto tmKikiData = allKikiDataMap
							.get(userData.kikiIDOld);
					if (tmKikiData != null) {
						userData.kikiID = tmKikiData.kikiID;
					} else {
//						System.out.println("該機器ID：" + userData.kikiIDOld);
					}

					// 工事ID、弁ID、機器IDのいずれが空である場合
					if((userData.koujiID==null) ||(userData.KikiSysId==null) || (userData.kikiID==null) ){
						allNoMatchKoujiRirekiDataList.add(userData);
					}
				}

				allKoujiKikiData.put(Integer.toString(nIndex),
						allKoujiKikiDataList);


			}
//			if (!ValdacUtilites.downLoadKoujiRealation2(request, response,
//					ValdacConfig.OUTPUT_FILENAME_RELATION, allKoujiKikiData,
//					ValdacConfig.DELIMITER)) {
//				System.out.println("Error: Failed download CSV file");
//			}
//			ModeName="match";
			if ("match".equals(ModeName)){
				if (!ValdacUtilites.downLoadTestKoujiRealation2(request, response,
						ValdacConfig.OUTPUT_TENKENKIKI, allKoujiKikiData,
						ValdacConfig.DELIMITER,startnIndex)) {
					System.out.println("Error: Failed download CSV file");
				}
			}else {
				if (!ValdacUtilites.downLoadTenkenRireki(request, response,
						ValdacConfig.OUTPUT_TENKENKIKI_NO, allNoMatchKoujiRirekiDataList,
						ValdacConfig.DELIMITER)) {
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
}