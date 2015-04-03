package jp.co.freedom.valdac;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.valdac.ValdacUserDataDto;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 *
 */
@WebServlet(name = "ChangeKenan", urlPatterns = { "/ChangeKenan" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class ChangeKenan extends HttpServlet {

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

			// ｔｘｔファイルを選択し、データを読み取る
			// アップロードディレクトリの絶対パス
			String uploadDirPath = request.getServletContext().getRealPath("")
					+ File.separator + Config.UPLOAD_DIR;
			// アップロードファイルの削除
			FileUtil.delete(uploadDirPath);
			// アップロードファイルの保存
			FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
			// CSVデータのロード
			List<String[]> csvData = FileUtil.loadCsvSavingFileName(
					uploadDirPath, ValdacConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
					ValdacConfig.REMOVE_HEADER_RECORD, ValdacConfig.DELIMITER,
					ValdacConfig.ALLOWS_EXTENSIONS);
			assert csvData != null;
			// アップロードファイルの削除
			FileUtil.delete(uploadDirPath);

			// ユーザーデータを保持するリスト
			List<String[]> userDataList = ValdacUtilites
					.createInstanceCheckDataLength(csvData,ValdacConfig.Length_Kenan);
			//kenanをユーザデータに変更
			List<ValdacUserDataDto> allKenanDataList = ValdacUtilites
					.changeKenanToUserDataList(userDataList);
			assert userDataList != null;

  		//kikisystem,kiki,buhin
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


				for (ValdacUserDataDto userData : allKenanDataList) {

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
					if((userData.KikiSysId==null) || (userData.kikiID==null) ){
						System.out.println(userData.KikiSysIdOld+userData.kikiIDOld);
					}
				}
				//DBにインポート
				//kenanテーブルに
				ValdacUtilites.resetDB(conn, ValdacConfig.TABLENAME_Kenan); // DBの初期化
				ValdacUtilites.importDataToKenanTable(conn, allKenanDataList,
				ValdacConfig.TABLENAME_Kenan); // 点検機器テーブル

				// koujiRelationテーブルに保存 kouji=0
//				ValdacUtilites.resetDB(conn, ValdacConfig.TABLENAME_KoujiRelation); // DBの初期化
				ValdacUtilites.importDataKoujiRelationForKenan(conn, allKenanDataList,
				ValdacConfig.TABLENAME_KoujiRelation); // koujiRelationテーブル


				if (!ValdacUtilites.downLoadKenan(request, response,
						ValdacConfig.OUTPUT_KENAN, allKenanDataList,
						ValdacConfig.DELIMITER)) {
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