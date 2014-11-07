package jp.co.freedom.dboperate.zip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.zip.ZipDto;
import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.ZipUtil;

/**
 * 日本郵便株式会社の7桁郵便番号簿を使用して、郵便番号から都道府県を特定するサーブレット
 * [注意]7桁郵便番号簿は毎月更新されるので、定期的に最新情報に更新する必要がある。
 * 
 * @author フリーダム・グループ
 * 
 */
public class ConvertZipToPrefectures extends HttpServlet {

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
			conn = DriverManager.getConnection(Config.PSQL_URL_COMMONDB_LOCAL,
					Config.PSQL_USER, Config.PSQL_PASSWORD_LOCAL);
			Map<String, ZipDto> master = ZipUtil.getAddrAll(conn); // 郵便番号MAP
			List<ZipDto> input = ZipUtil.getData(conn); // 入力データ
			List<ZipDto> output = new ArrayList<ZipDto>();
			int count = 0;
			for (ZipDto cardInfo : input) {
				++count;
				ZipDto tmpInfo = master.get(cardInfo.zip);
				if (tmpInfo != null) {
					tmpInfo.id = cardInfo.id;
					output.add(tmpInfo);
				} else {
					output.add(cardInfo);
				}
				System.out.println(count);
			}
			if (!ZipUtil.downLoad(request, response, "郵便番号.txt", output,
					Config.DELIMITER_TAB)) {
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