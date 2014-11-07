package jp.co.freedom.master.mesago.ip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.dto.mesago.ip.IpRfidDto;
import jp.co.freedom.master.utilities.mesago.ip.IpConfig;
import jp.co.freedom.master.utilities.mesago.ip.IpUtil;

/**
 * 【IP】事前登録未来場者データのダウンロード用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "MasterPreDataForIpNonVisitor", urlPatterns = { "/MasterPreDataForIpNonVisitor" })
public class MasterPreDataForIpNonVisitor extends HttpServlet {

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

		// 出力ファイル名
		String outputFileName = IpConfig.PREENTRY_NONVISITOR_MASTERFILE_NAME;

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(
					IpConfig.PSQL_URL_REMOTE, IpConfig.PSQL_USER,
					IpConfig.PSQL_PASSWORD_REMOTE);
			// 事前登録の全データ
			List<MesagoUserDataDto> allPreRegistDataList = IpUtil
					.getAllPreRegistData(conn, true);
			// RFIDデータMap
			Map<String, IpRfidDto> allRfidInfoMap = IpUtil.getAllRfidMap(conn);

			// 事前未来場者データリスト
			List<MesagoUserDataDto> nonPreEntryVisitors = new ArrayList<MesagoUserDataDto>();

			/*
			 * RFIDデータのコピー
			 */
			for (MesagoUserDataDto userData : allPreRegistDataList) {
				IpRfidDto rfidDetailInfo = allRfidInfoMap.get(userData.id);
				if (rfidDetailInfo == null) {
					nonPreEntryVisitors.add(userData);
				}
			}

			if (!IpUtil.downLoad(request, response, outputFileName, conn,
					nonPreEntryVisitors, Config.DELIMITER_TAB, true, false,
					false, false)) {
				System.out.println("Error: Failed download CSV file");
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