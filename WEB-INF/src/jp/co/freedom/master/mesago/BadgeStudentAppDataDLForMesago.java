package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【バッチ作成用】当日登録(学生／その他)マスターデータのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BadgeStudentAppDataDLForMesago", urlPatterns = { "/BadgeStudentAppDataDLForMesago" })
public class BadgeStudentAppDataDLForMesago extends HttpServlet {

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

		request.setCharacterEncoding("UTF-8");
		// ファイル名
		String outputFileName = new String(request.getParameter("file")
				.getBytes("ISO_8859_1"), "UTF-8");
		assert StringUtil.isNotEmpty(outputFileName);

		// 学生向け連番開始番号
		Object studentCountObj = request.getParameter("studentCount");
		int studentCount = StringUtil.toInteger((String) studentCountObj);
		if (-1 != studentCount) {
			MesagoUtil.masterStudentCount = studentCount;
		}
		// その他向け連番開始番号
		Object otherCountObj = request.getParameter("otherCount");
		int otherCount = StringUtil.toInteger((String) otherCountObj);
		if (-1 != otherCount) {
			MesagoUtil.masterOtherCount = otherCount;
		}

		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(
					MesagoConfig.PSQL_URL_REMOTE_BWJ2014TOKYO,
					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			// 当日登録ユーザーである場合
			List<MesagoUserDataDto> userDataList = MesagoUtil
					.getAllBadgeStudentAppointedDayData(conn, outputFileName);
			if (!MesagoUtil.downLoadBadgeStudentAppointeddayData(request,
					response, outputFileName + ".txt", conn, userDataList,
					Config.DELIMITER_COMMNA, studentCount, otherCount)) {
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