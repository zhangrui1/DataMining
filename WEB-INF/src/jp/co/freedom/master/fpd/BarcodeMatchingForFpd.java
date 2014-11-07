package jp.co.freedom.master.fpd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.fpd.FpdConfig;
import jp.co.freedom.master.utilities.fpd.FpdUtil;

/**
 * 【FPD】セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class BarcodeMatchingForFpd extends HttpServlet {

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
				FpdConfig.barcodeDataDirectory,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				FpdConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				FpdConfig.CONVERT_TABLE_DIRECTORY,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, ",",
				FpdConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(FpdConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<FpdUserDataDto> userDataList = FpdUtil.createInstance(csvData);
		assert userDataList != null;

		// 変換表の作成
		List<FpdUserDataDto> convertTableList = FpdUtil
				.createInstanceForConvertTable(convertTableData);
		for (FpdUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.preEntryId)) {
				this.convertTable.put(data.id, data.preEntryId);
			} else {
				assert false;
			}
		}
		assert convertTableList != null;

		// アンマッチバーコード番号リスト
		List<FpdUserDataDto> unmatchList = new ArrayList<FpdUserDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(FpdConfig.PSQL_URL,
					FpdConfig.PSQL_USER, FpdConfig.PSQL_PASSWORD);
			// 事前登録の全データ
			List<FpdUserDataDto> allPreRegistDataList = FpdUtil
					.getAllPreRegistData(conn);
			// 事前登録ユーザー情報Map
			Map<String, FpdUserDataDto> preRegistInfoMap = FpdUtil
					.getPreEntryMap(allPreRegistDataList);
			// 当日登録の全データ
			List<FpdUserDataDto> allAppointedDataList = FpdUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, FpdUserDataDto> appointedInfoMap = FpdUtil
					.getAppointedDayMap(allAppointedDataList);
			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (FpdUserDataDto userData : userDataList) {
				userData.preEntryId = getPreEntryId(userData.id);
				if (StringUtil.isNotEmpty(userData.preEntryId)) {
					// 事前登録ユーザーである場合
					FpdUserDataDto tmpData = preRegistInfoMap
							.get(userData.preEntryId);
					if (tmpData != null) {
						userData.exhibitionType = tmpData.exhibitionType;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else {
					// 当日登録ユーザーである場合
					FpdUserDataDto tmpData = appointedInfoMap.get(userData.id);
					if (tmpData != null) {
						userData.exhibitionType = tmpData.exhibitionType;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				}
				// アンマッチバーコード番号リストへの追加
				if (StringUtil.isEmpty(userData.cardInfo.V_VID)) {
					unmatchList.add(userData);
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!FpdUtil.downLoad(request, response, outputFileName,
						userDataList, "\t", false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else if ("unmatch".equals(mode)) { // アンマッチバーコード番号リストのダウンロード
				if (!FpdUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
					System.out.println("Error: Failed download CSV file");
				}
			} else {
				if (!FpdUtil.downLoadForConverter(request, response,
						outputFileName, convertTableList, "\t")) {
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