package jp.co.freedom.valdac;

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

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.valdac.ValdacUserDataDto;
import jp.co.freedom.valdac.utilities.ValdacConfig;
import jp.co.freedom.valdac.utilities.ValdacUtilites;

/**
 *
 */
@WebServlet(name = "KokyakuTable", urlPatterns = { "/KokyakuTable" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class KokyakuTable extends HttpServlet {

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
			// 1.顧客の全データ
			List<ValdacUserDataDto> allKokyakuData = ValdacUtilites
					.getKokyakuData(conn);
			// 機器システムの全データMap
			Map<String, ValdacUserDataDto> allKokyakuDataMap = ValdacUtilites
					.getallKokyakuDataMap(allKokyakuData);


				for (ValdacUserDataDto userData : allKokyakuData) {
					//**0000じゃない場合、日本語の顧客名をつける
					if(!("00".equals(userData.kCodeM))){

						// 親IDである場合　**0000の日本語を取得する
						String grandfather=StringUtil.concatWithDelimit("", userData.kCodeL,"0000");
						ValdacUserDataDto tmKokyakuData = allKokyakuDataMap
								.get(grandfather);

						if (tmKokyakuData != null) {
							userData.kCodeLKanji = tmKokyakuData.kName;
						} else {
							System.out
									.println("該顧客の親データがみつかりません：" + userData.id);
						}

						//****00の日本語を取得する
						if(!("00".equals(userData.kCodeS))){

							// 親IDである場合　****00の日本語を取得する
							String father=StringUtil.concatWithDelimit("", userData.kCodeL,userData.kCodeM,"00");
							ValdacUserDataDto tmKokyakuFatherData = allKokyakuDataMap
									.get(father);

							if (tmKokyakuFatherData != null) {
								userData.kCodeMKanji = tmKokyakuFatherData.kName;

							} else {
								System.out
										.println("該顧客の親データがみつかりません：" + userData.id);
							}

							userData.kCodeSKanji=userData.kName;

						}else{
							userData.kCodeMKanji=userData.kName;
						}

					}else{
						//**0000場合は、日本語名をkCodeLにする
						userData.kCodeLKanji = userData.kName;
					}
				}

					if (!ValdacUtilites.downLoadKokyaku(request, response,
							ValdacConfig.OUTPUT_KOKYAKU, allKokyakuData,
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