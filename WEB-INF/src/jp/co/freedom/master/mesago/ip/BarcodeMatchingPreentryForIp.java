package jp.co.freedom.master.mesago.ip;

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
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.dto.mesago.ip.IpRfidDto;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;
import jp.co.freedom.master.utilities.mesago.ip.IpConfig;
import jp.co.freedom.master.utilities.mesago.ip.IpUtil;

/**
 * 【IP2014】セキュア・バーコードによるマッチングを行うサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeMatchingPreentryForIp", urlPatterns = { "/BarcodeMatchingPreentryForIp" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BarcodeMatchingPreentryForIp extends HttpServlet {

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
				IpConfig.ENQUOTE_BY_DOUBLE_QUOTATION_TRUE,
				IpConfig.REMOVE_HEADER_RECORD_TRUE, Config.DELIMITER_COMMNA,
				IpConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// 出力ファイル名
		String outputFileName = IpConfig.PREENTRY_MASTERFILE_NAME;
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = IpUtil.createInstance(csvData);
		assert userDataList != null;

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(IpConfig.PSQL_URL_REMOTE,
					IpConfig.PSQL_USER, IpConfig.PSQL_PASSWORD_REMOTE);

			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = IpUtil
					.getAllPreRegistData(conn, true);
			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = MesagoUtil
					.getMap(allPreRegistDataList);
			// RFIDデータMap
			Map<String, IpRfidDto> allRfidInfoMap = IpUtil.getAllRfidMap(conn);

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (MesagoUserDataDto userData : userDataList) {
				// 来場履歴データの取得
				IpRfidDto rfidInfo = allRfidInfoMap.get(userData.id);
				if (rfidInfo != null) {
					userData.visitFlgs = rfidInfo.visitFlgs;
				}
				// 名刺情報およびアンケート情報の取得
				MesagoUserDataDto tmpData = preRegistInfoMap.get(userData.id);
				if (tmpData != null) {
					userData.preentry = tmpData.preentry;
					userData.cardInfo = tmpData.cardInfo;
					userData.questionInfo = tmpData.questionInfo;
				}
			}
			if (!IpUtil.downLoad(request, response, outputFileName, conn,
					userDataList, Config.DELIMITER_TAB, false, false, true)) {
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