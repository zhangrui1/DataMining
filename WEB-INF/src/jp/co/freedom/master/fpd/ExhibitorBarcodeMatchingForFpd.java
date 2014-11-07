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
 * 【FPD】《出展者貸出用》セキュア・バーコードによるマッチングを行うサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class ExhibitorBarcodeMatchingForFpd extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** バーコードToメールアドレス変換表 */
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

		// 出展者貸出バーコードCSVデータ
		List<String[]> csvData = FileUtil.loadCsv(
				FpdConfig.exhibitorBarcodeDataDirectory,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, Config.DELIMITER_COMMNA,
				FpdConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// メールアドレス変換表
		List<String[]> emailConvertTable = FileUtil.loadCsv(
				FpdConfig.exhibitorAddressDirectory,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, ",",
				FpdConfig.ALLOWS_EXTENSIONS);
		assert emailConvertTable != null;

		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(FpdConfig.barcodeDataDirectory);

		// ユーザーデータを保持するリスト
		List<FpdUserDataDto> userDataList = FpdUtil.createInstance(csvData);
		assert userDataList != null;

		// メールアドレス変換表の作成
		this.convertTable = FpdUtil
				.createInstanceForEmailConverter(emailConvertTable);

		// アンマッチバーコード番号リスト
		List<FpdUserDataDto> unmatchList = new ArrayList<FpdUserDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(FpdConfig.PSQL_URL,
					FpdConfig.PSQL_USER, FpdConfig.PSQL_PASSWORD);
			// DBデータから必要な情報を取得しユーザーデータに取り込む

			for (FpdUserDataDto userdata : userDataList) {
				if (StringUtil.isNotEmpty(userdata.id)) {
					String email = this.convertTable.get(userdata.id);
					FpdUserDataDto tmpData;
					if (StringUtil.isNotEmpty(email)) {
						// 事前登録DBの検索
						tmpData = FpdUtil.getPreRegistDataByEmail(conn, email,
								userdata.timeByRfid);
						if (StringUtil.isNotEmpty(tmpData.cardInfo.V_VID)) {
							userdata.cardInfo = tmpData.cardInfo;
							userdata.questionInfo = tmpData.questionInfo;
						} else {
							tmpData = FpdUtil.getAppointedDayDataByEmail(conn,
									email, userdata.timeByRfid);
							if (StringUtil.isNotEmpty(tmpData.cardInfo.V_VID)) {
								userdata.cardInfo = tmpData.cardInfo;
								userdata.questionInfo = tmpData.questionInfo;
							} else {
								// アンマッチバーコード番号リストへの追加
								unmatchList.add(userdata);
							}
						}
					} else {
						tmpData = FpdUtil.getAppointedDayData(conn,
								userdata.id, userdata.timeByRfid);
						if (StringUtil.isNotEmpty(tmpData.cardInfo.V_VID)) {
							userdata.cardInfo = tmpData.cardInfo;
							userdata.questionInfo = tmpData.questionInfo;
						} else {
							// アンマッチバーコード番号リストへの追加
							unmatchList.add(userdata);
						}
					}
				}
			}
			if ("match".equals(mode)) { // マッチングデータのダウンロード
				if (!FpdUtil.downLoad(request, response, outputFileName,
						userDataList, "\t", false, false)) {
					System.out.println("Error: Failed download CSV file");
				}
			} else { // アンマッチバーコード番号リストのダウンロード
				if (!FpdUtil.downLoadForUnmatch(request, response,
						outputFileName, unmatchList, "\t")) {
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
}