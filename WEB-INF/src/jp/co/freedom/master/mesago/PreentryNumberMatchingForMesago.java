package jp.co.freedom.master.mesago;

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
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【MESAGO】事前登録の登録券番号にマッチングを行い事前登録者分の納品データを作成するサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "PreentryNumberMatchingForMesago", urlPatterns = { "/PreentryNumberMatchingForMesago" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class PreentryNumberMatchingForMesago extends HttpServlet {

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

		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				MesagoConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				MesagoConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = MesagoUtil
				.createInstancePreentryNumberMatching(csvData);
		assert userDataList != null;

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);

		Connection conn = null;
		// 事前登録ユーザー情報Map
		Map<String, MesagoUserDataDto> preRegistInfoMap = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(MesagoConfig.PSQL_URL_REMOTE,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = MesagoUtil
					.getAllPreRegistData(conn, true);
			preRegistInfoMap = MesagoUtil
					.getMapWithPreentryNumber(allPreRegistDataList);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 登録券番号によるマッチング
		for (MesagoUserDataDto userdata : userDataList) {
			MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
			MesagoUserDataDto tmpData = preRegistInfoMap
					.get(cardInfo.PREENTRY_ID);
			if (tmpData != null) {
				userdata.preentry = true;
				userdata.cardInfo = tmpData.cardInfo;
				userdata.questionInfo = tmpData.questionInfo;
				userdata.visitFlgs = tmpData.visitFlgs;
			} else {
				((MesagoCardDto) userdata.cardInfo).unmatching = true;
				System.out.println("■アンマッチング:" + cardInfo.PREENTRY_ID);
			}
		}

		// 納品データDL
		try {
			if (!MesagoUtil.downLoad(request, response,
					MesagoConfig.MATCHING_RESULT_FILE_NAME, conn, userDataList,
					Config.DELIMITER_TAB, false, false, true)) {
				System.out.println("Error: Failed download CSV file");
			}
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