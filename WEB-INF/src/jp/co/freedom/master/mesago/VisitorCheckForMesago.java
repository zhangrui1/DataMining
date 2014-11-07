package jp.co.freedom.master.mesago;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoConstants;
import jp.co.freedom.master.utilities.mesago.MesagoExhibitorUtil;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【MESAGO】事前登録者の来場日時データをDB保存するサーブレット
 *
 * @author フリーダム・グループ
 *
 */
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class VisitorCheckForMesago extends HttpServlet {

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
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(uploadDirPath,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION_TRUE,
				MesagoConfig.REMOVE_HEADER_RECORD_TRUE,
				Config.DELIMITER_COMMNA, MesagoConfig.ALLOWS_EXTENSIONS_CSV);
		assert csvData != null;

		// ユニークIDの抽出
		List<String[]> uniqueFullList = new ArrayList<String[]>();
		List<String> uniqueList = new ArrayList<String>();
		for (String[] data : csvData) {
			String id = data[2];
			if (!uniqueList.contains(id)) {
				uniqueList.add(id);
				uniqueFullList.add(data);
			}
		}

		// 変換表のロード
		List<String[]> convertTableData = FileUtil.loadCsv(
				MesagoConfig.CONVERT_TABLE_DIRECTORY,
				MesagoConfig.ENQUOTE_BY_DOUBLE_QUOTATION, true,
				Config.DELIMITER_TAB, MesagoConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;
		// 変換表の作成
		List<MesagoUserDataDto> convertTableList = MesagoExhibitorUtil
				.createInstanceForConvertTable(convertTableData);
		for (MesagoUserDataDto data : convertTableList) {
			if (StringUtil.isNotEmpty(data.id)
					&& StringUtil.isNotEmpty(data.cardInfo.V_CID)) {
				String id = data.id;
				id = id.substring(1, id.length() - 1);
				this.convertTable.put(id, data.cardInfo.V_CID);
			} else {
				assert false;
			}
		}
		assert convertTableList != null;

		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);

			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = MesagoUtil
					.getAllPreRegistData(conn, true);
			// 事前登録ユーザー情報Map
			Map<String, MesagoUserDataDto> preRegistInfoMap = MesagoUtil
					.getMap(allPreRegistDataList);
			// 当日登録の全データ
			List<MesagoUserDataDto> allAppointedDataList = MesagoUtil
					.getAllAppointedDayData(conn);
			// 当日登録ユーザー情報Map
			Map<String, MesagoUserDataDto> appointedInfoMap = MesagoUtil
					.getMap(allAppointedDataList);

			for (String data[] : uniqueFullList) {
				String barcode = data[2];
				String day = data[1].substring(8, 10);

				// サブバーコードの存在確認
				String subBarcode = MesagoConstants.SUB_BARCODES_MAP
						.get(barcode);
				if (StringUtil.isNotEmpty(subBarcode)) { // サブバーコードの保存
					barcode = subBarcode;
				}

				String registId = getPreEntryId(barcode);
				if (StringUtil.isNotEmpty(registId)) {
					MesagoUserDataDto preRegistInfo = preRegistInfoMap
							.get(registId); // 事前登録データ
					if (preRegistInfo != null) {
						if ("19".equals(day)) {
							preRegistInfo.visit19Flg = true;
						} else if ("20".equals(day)) {
							preRegistInfo.visit20Flg = true;
						} else {
							preRegistInfo.visit21Flg = true;
						}
					} else {
						MesagoUserDataDto appointeddayInfo = appointedInfoMap
								.get(barcode);
						if (appointeddayInfo != null) {
							if ("19".equals(day)) {
								appointeddayInfo.visit19Flg = true;
							} else if ("20".equals(day)) {
								appointeddayInfo.visit20Flg = true;
							} else {
								appointeddayInfo.visit21Flg = true;
							}
						}
					}
				} else {
					MesagoUserDataDto appointeddayInfo = appointedInfoMap
							.get(barcode);
					if (appointeddayInfo != null) {
						if ("19".equals(day)) {
							appointeddayInfo.visit19Flg = true;
						} else if ("20".equals(day)) {
							appointeddayInfo.visit20Flg = true;
						} else {
							appointeddayInfo.visit21Flg = true;
						}
					}
				}
			}
			List<MesagoUserDataDto> nonVisitors = new ArrayList<MesagoUserDataDto>();
			List<Map.Entry<String, MesagoUserDataDto>> mapValuesList = null;
			if ("preentry".equals(mode)) {
				mapValuesList = new ArrayList<Map.Entry<String, MesagoUserDataDto>>(
						preRegistInfoMap.entrySet());
			} else {
				mapValuesList = new ArrayList<Map.Entry<String, MesagoUserDataDto>>(
						appointedInfoMap.entrySet());
			}
			for (Entry<String, MesagoUserDataDto> entry : mapValuesList) {
				MesagoUserDataDto info = entry.getValue();
				if (!info.visit19Flg && !info.visit20Flg && !info.visit21Flg) {
					nonVisitors.add(info);
				}
			}
			if (!MesagoExhibitorUtil.downLoadForUnmatch(request, response,
					"未来場者リスト.txt", nonVisitors, "\t")) {
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