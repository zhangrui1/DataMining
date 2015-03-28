package jp.co.freedom.valdac;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.master.dto.valdac.ValdacBuhiDto;
import jp.co.freedom.master.dto.valdac.ValdacUserDataDto;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 *
 */
@WebServlet(name = "DoNewKikisystemRelationTable", urlPatterns = { "/DoNewKikisystemRelationTable" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class DoNewKikisystemRelationTable extends HttpServlet {

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
			// 2.機器の全データ
			List<ValdacUserDataDto> allKikiDataList = ValdacUtilites
					.getKikiIdData(conn);
			// // 機器の全データ情報Map
			// Map<String, ValdacUserDataDto> allKikiDataMap = ValdacUtilites
			// .getKikiDataIdMap(allKikiDataList);
			// 3.部品の全データ
			List<ValdacUserDataDto> allBuhiDataList = ValdacUtilites
					.getBuhiIdData(conn);

			// 機器システムテーブルから
			for (ValdacUserDataDto userData : allKikiSysIdData) {
				Map<String, ValdacBuhiDto> kikiListTemp = new HashMap<String, ValdacBuhiDto>();
				//機器テーブルに
				for (ValdacUserDataDto userKikiData : allKikiDataList) {
					ValdacBuhiDto buhinId=new ValdacBuhiDto();
					String KikiIdOld= userKikiData.id;
					String tempKikiId = KikiIdOld.substring(0,
							KikiIdOld.length() - 3);
					if (userData.KikiSysIdOld.equals(tempKikiId)) {
						//部品テーブルに
						for (ValdacUserDataDto buhiDataList : allBuhiDataList) {

							String BuhinIdOld= buhiDataList.id;
							String tempBuhinIdOld = BuhinIdOld.substring(0,
									BuhinIdOld.length() - 3);
							if (tempBuhinIdOld.equals(KikiIdOld)){
//								buhinId.buhinIDList.add(BuhinIdOld);
								buhinId.buhinIDList.add(buhiDataList.buhinID);
							}
						}
						kikiListTemp.put(userKikiData.kikiID, buhinId);
						userData.kikiList=kikiListTemp;
					}
				}
			}

			// マッチングデータのダウンロード
			if (!ValdacUtilites.downLoadIdRealation(request, response,
					ValdacConfig.OUTPUT_FILENAME_RELATION, allKikiSysIdData,
					Config.DELIMITER_TAB)) {
				System.out.println("Error: Failed download CSV file");
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