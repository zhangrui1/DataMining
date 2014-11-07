package jp.co.freedom.master.catv;

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
import jp.co.freedom.master.dto.catv.CatvUserDataDto;
import jp.co.freedom.master.utilities.catv.CatvConfig;
import jp.co.freedom.master.utilities.catv.CatvUtil;

/**
 * 【CATV2014】セミナー参加者情報DL用のサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "SeminarDataDLForCatv2014", urlPatterns = { "/SeminarDataDLForCatv2014" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class SeminarDataDLForCatv2014 extends HttpServlet {

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

		/* ファイルアップロード */
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR; // アップロードディレクトリの絶対パス
		FileUtil.delete(uploadDirPath); // アップロードファイルの削除
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR); // アップロードファイルの保存

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath,
				CatvConfig.ENQUOTE_BY_DOUBLE_QUOTATION_FALSE,
				CatvConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_TAB,
				CatvConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// 出力ファイル名
		String outputFileName = CatvConfig.MATCHING_RESULT_FILE_NAME;
		// ユーザーデータを保持するリスト
		List<CatvUserDataDto> userDataList = CatvUtil.createInstance(csvData);
		assert userDataList != null;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(CatvConfig.PSQL_URL_REMOTE,
					CatvConfig.PSQL_USER, CatvConfig.PSQL_PASSWORD_REMOTE);

			// 事前登録の全データ
			List<CatvUserDataDto> allPreRegistDataList = CatvUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, CatvUserDataDto> preRegistInfoMap = CatvUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<CatvUserDataDto> allAppRegistDataList = CatvUtil
					.getAllPreRegistData(conn);
			// 当日登録ユーザー情報Map
			Map<String, CatvUserDataDto> appRegistInfoMap = CatvUtil
					.getMap(allAppRegistDataList);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (CatvUserDataDto userData : userDataList) {
				if (CatvUtil.isAppEntry(userData.id)) {
					// 名刺情報およびアンケート情報の取得
					CatvUserDataDto tmpData = appRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.appointedday = tmpData.appointedday;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else if (CatvUtil.isPreEntry(userData.id)) {
					// 名刺情報およびアンケート情報の取得
					CatvUserDataDto tmpData = preRegistInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.preentry = tmpData.preentry;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				}
			}
			if (!CatvUtil.downLoad(request, response, outputFileName, conn,
					userDataList, Config.DELIMITER_TAB, false, false)) {
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