package jp.co.freedom.master.fpd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.fpd.FpdConfig;
import jp.co.freedom.master.utilities.fpd.FpdUtil;

/**
 * 【FPD】来場チェック結果をDB保存するサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class VisitorCheckForFpd extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 変換表 */
	private Map<String, String> convertTable = new HashMap<String, String>(); // 変換表

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

		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(FpdConfig.barcodeDataDirectory,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, ",",
				FpdConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;
		// 変換表のロード　(備忘：変換表データBOMなしCSVである必要あり)
		List<String[]> convertTableData = FileUtil.loadCsv(
				FpdConfig.CONVERT_TABLE_DIRECTORY,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, ",",
				FpdConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;

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

		// ユニークIDの抽出
		List<String> uniqueList = new ArrayList<String>();
		for (FpdUserDataDto userInfo : userDataList) {
			String id = userInfo.id;
			if (!uniqueList.contains(id)) {
				uniqueList.add(id);
			}
		}
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(FpdConfig.PSQL_URL,
					FpdConfig.PSQL_USER, FpdConfig.PSQL_PASSWORD);
			// DB保存
			for (String id : uniqueList) {
				String preEntryId = getPreEntryId(id);
				if (StringUtil.isNotEmpty(preEntryId)) { // 事前登録データ
					FpdUtil.updateVisitorFlg(conn, preEntryId, "preentry");
				} else { // 当日登録データ
					FpdUtil.updateVisitorFlg(conn, id, "appointedday");
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
		// JSPにフォワード
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/result.jsp");
		dispatcher.forward(request, response);
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