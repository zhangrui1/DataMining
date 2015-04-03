package jp.co.freedom.valdac;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
@WebServlet(name = "ChangeTenkenRireki", urlPatterns = { "/ChangeTenkenRireki" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class ChangeTenkenRireki extends HttpServlet {

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
					.createInstanceCheckDataLength(csvData,ValdacConfig.Length_TenkenRireki);
			assert userDataList != null;
			// 工事履歴の全データMap
			List<ValdacUserDataDto> allKoujiRirekiDataList = ValdacUtilites
							.changeToUserDataList(userDataList);
			// 点検履歴の全データMap
			Map<String, ValdacUserDataDto> allKoujiRirekiDataListMap = ValdacUtilites
					.getallTenkenkikiRankMap(allKoujiRirekiDataList);

			// 工事履歴の 工事IDがないデータMap
			List<ValdacUserDataDto> allNoMatchKoujiRirekiDataList = new ArrayList<ValdacUserDataDto>();



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
			// 3.工事の全データ
			List<ValdacUserDataDto> allKoujiDataList = ValdacUtilites
					.getKoujiIdData(conn);
			// 工事の全データMap
			Map<String, ValdacUserDataDto> allKoujiDataListMap = ValdacUtilites
					.getallKoujiDataListMap(allKoujiDataList);
			// 4.点検機器のデータを取得
			List<ValdacUserDataDto> allTenkenkikiRank = ValdacUtilites
					.getTenkenkikiRank(conn);
//			 点検機器の全データMap
//			Map<String, ValdacUserDataDto> allTenkenkikiRankMap = ValdacUtilites
//					.getallTenkenkikiRankMap(allTenkenkikiRank);

			//点検機器に点検結果、備考
			for (ValdacUserDataDto userData : allTenkenkikiRank) {

				// 工事である場合
				ValdacUserDataDto tmKoujiData = allKoujiDataListMap
						.get(userData.koujiIDOld);
				if (tmKoujiData != null) {
					userData.koujiID = tmKoujiData.koujiID;
				} else {
//					System.out.println("該工事ID：" + userData.koujiIDOld);
				}

				// 機器システムIDである場合
				ValdacUserDataDto tmKikisysData = allKikiSysIdDataMap
						.get(userData.KikiSysIdOld);
				if (tmKikisysData != null) {
					userData.KikiSysId = tmKikisysData.KikiSysId;
				} else {
//					System.out
//							.println("該機器システムID：" + userData.KikiSysIdOld);
				}

				// 機器である場合
				ValdacUserDataDto tmKikiData = allKikiDataMap
						.get(userData.kikiIDOld);
				if (tmKikiData != null) {
					userData.kikiID = tmKikiData.kikiID;
				} else {
//					System.out.println("該機器ID：" + userData.kikiIDOld);
				}
				// 工事ID、弁ID、機器IDのいずれが空である場合
				if((userData.koujiID==null) ||(userData.KikiSysId==null) || (userData.kikiID==null) ){
					allNoMatchKoujiRirekiDataList.add(userData);
				}
				//tenkenkekka,bikouを取得
				String tenkenkiki=StringUtil.concatWithDelimit("",userData.koujiIDOld,userData.kikiIDOld);
				ValdacUserDataDto tmTenkenRank=allKoujiRirekiDataListMap.get(tenkenkiki);
				if (tmTenkenRank != null) {
					userData.tenkenBikou = tmTenkenRank.tenkenBikou;
					userData.tenkenKekka0 = tmTenkenRank.tenkenKekka0;
					userData.tenkenNendo = tmTenkenRank.tenkenNendo;
					userData.KanryoFlg = tmTenkenRank.KanryoFlg;
					userData.tenkenDate = tmTenkenRank.tenkenDate;
					userData.trkDate = tmTenkenRank.trkDate;
					userData.updDate = tmTenkenRank.updDate;
				} else {
//					System.out.println("該点検機器のID：" + tenkenkiki);
				}

			}

			//点検ランク、点検内容を追加
			//tenkenRirekiテーブルに保存
			ValdacUtilites.resetDB(conn, ValdacConfig.TABLENAME_TenkenRireki); // DBの初期化
			ValdacUtilites.importDataTenkenKiki(conn, allTenkenkikiRank,
			ValdacConfig.TABLENAME_TenkenRireki); // 点検機器テーブル

			// koujiRelationテーブルに保存
			ValdacUtilites.resetDB(conn, ValdacConfig.TABLENAME_KoujiRelation); // DBの初期化
			ValdacUtilites.importDataKoujiRelation(conn, allTenkenkikiRank,
			ValdacConfig.TABLENAME_KoujiRelation); // koujiRelationテーブル

			if (!ValdacUtilites.downLoadTenkenRireki(request, response,
					ValdacConfig.OUTPUT_TENKENRIREKI, allTenkenkikiRank,
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