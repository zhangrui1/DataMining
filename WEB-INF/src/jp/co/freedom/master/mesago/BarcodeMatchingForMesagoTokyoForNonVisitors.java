package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoConfig;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

/**
 * 【BWJTokyo2014】セキュア・バーコードによるマッチングを行うサーブレット ※事前登録の未来場者データを対象
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BarcodeMatchingForMesagoTokyoForNonVisitors", urlPatterns = { "/BarcodeMatchingForMesagoTokyoForNonVisitors" })
public class BarcodeMatchingForMesagoTokyoForNonVisitors extends HttpServlet {

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

		// 処理モード
		Object modeObj = request.getParameter("mode");
		String mode = (String) modeObj;
		assert StringUtil.isNotEmpty(mode);
		// CSVデータのロード
		List<String[]> csvData = FileUtil.loadCsv(
				MesagoConfig.barcodeDataDirectory, true, true,
				Config.DELIMITER_TAB, MesagoConfig.ALLOWS_EXTENSIONS);
		assert csvData != null;

		// 出力ファイル名
		String outputFileName = FileUtil
				.getOutputFileName(MesagoConfig.barcodeDataDirectory);
		// ユーザーデータを保持するリスト
		List<MesagoUserDataDto> userDataList = MesagoUtil
				.createBwjNonVisitorsInstance(csvData);
		assert userDataList != null;
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

			// DBデータから必要な情報を取得しユーザーデータに取り込む
			for (MesagoUserDataDto userData : userDataList) {
				userData.cardInfo.V_CID = userData.id;
				if ("preentry".equals(mode)
						&& StringUtil.isNotEmpty(userData.cardInfo.V_CID)) {
					// 事前登録ユーザーである場合
					MesagoUserDataDto tmpData = preRegistInfoMap
							.get(userData.cardInfo.V_CID);
					if (tmpData != null) {
						userData.preentry = tmpData.preentry;
						userData.vipInvitation = tmpData.vipInvitation;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
					}
				} else if ("appointedday".equals(mode)) {
					// 当日入力ユーザーである場合
					MesagoUserDataDto tmpData = appointedInfoMap
							.get(userData.id);
					if (tmpData != null) {
						userData.preentry = tmpData.preentry;
						userData.vipInvitation = tmpData.vipInvitation;
						userData.cardInfo = tmpData.cardInfo;
						userData.questionInfo = tmpData.questionInfo;
						String imagePath = ((MesagoCardDto) userData.cardInfo).V_IMAGE_PATH;
						boolean flg19 = StringUtil.find(imagePath, "0519");
						boolean flg20 = StringUtil.find(imagePath, "0520");
						boolean flg21 = StringUtil.find(imagePath, "0521");
						String day = null;
						if (flg19) {
							day = "19";
						} else if (flg20) {
							day = "20";
						} else if (flg21) {
							day = "21";
						}
						userData.visitFlgs = new boolean[3];
						userData.visitFlgs[0] = flg19;
						userData.visitFlgs[1] = flg20;
						userData.visitFlgs[2] = flg21;
						userData.cardInfo.V_DAY = day;
					}
				}
			}
			if (!MesagoUtil.downLoad(request, response, outputFileName, conn,
					userDataList, "\t", false, false, true)) {
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
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
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