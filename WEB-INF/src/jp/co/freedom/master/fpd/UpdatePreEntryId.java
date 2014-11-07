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

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.fpd.FpdConfig;
import jp.co.freedom.master.utilities.fpd.FpdUtil;

/**
 * 【FPD】変換表により事前登録の6桁バーコード番号を特定した上で、DBに記憶するサーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class UpdatePreEntryId extends HttpServlet {

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

		// 出力ファイル名
		String outputFileName = FpdConfig.UNMATCH_PREENTY_ID_FILENAME;

		// 変換表のロード　(備忘：変換表データBOMなしCSVである必要あり)
		List<String[]> convertTableData = FileUtil.loadCsv(
				FpdConfig.CONVERT_TABLE_DIRECTORY,
				FpdConfig.ENQUOTE_BY_DOUBLE_QUOTATION,
				FpdConfig.REMOVE_HEADER_RECORD, ",",
				FpdConfig.ALLOWS_EXTENSIONS);
		assert convertTableData != null;

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
			// DB保存
			for (FpdUserDataDto userdata : convertTableList) {
				if (!FpdUtil.updatePreEntryId(conn, userdata)) {
					unmatchList.add(userdata);
				}
			}
			conn.close();
			if (!FpdUtil.downLoadForUnmatch(request, response, outputFileName,
					unmatchList, "\t")) {
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