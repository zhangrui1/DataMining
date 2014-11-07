package jp.co.freedom.master.scf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.scf.ScfCardDto;
import jp.co.freedom.master.dto.scf.ScfUserDataDto;
import jp.co.freedom.master.dto.scf.ScfYokogawaQuestionDto;
import jp.co.freedom.master.utilities.scf.ScfConfig;
import jp.co.freedom.master.utilities.scf.ScfUtil;

/**
 * [横河電機]当日登録マスターデータのダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class MasterAppDataDLForScfYokogawa extends HttpServlet {

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
		String outputFileName = ScfConfig.APPOINTEDDAY_MASTERFILE_NAME;

		// アンマッチバーコード番号リスト
		List<ScfUserDataDto> unmatchList = new ArrayList<ScfUserDataDto>();

		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(ScfConfig.PSQL_URL,
					ScfConfig.PSQL_USER, ScfConfig.PSQL_PASSWORD);
			// 横河電機の全データを取得
			List<ScfUserDataDto> userDataList = ScfUtil
					.getAllAppointedDayDataForYokogawa(conn);
			// データ修復
			for (ScfUserDataDto userdata : userDataList) {
				// 名刺データの修復
				if ("1".equals(userdata.masterCardDataExist)
						&& StringUtil.isNotEmpty(userdata.id)) {
					ScfCardDto tmpCardInfo = ScfUtil.getCardInfo(conn,
							userdata.id);
					if (StringUtil.isNotEmpty(tmpCardInfo.V_VID)) {
						userdata.cardInfo = tmpCardInfo;
					} else {
						unmatchList.add(userdata);
					}
				}
				// アンケートデータの修復
				if ("1".equals(userdata.masterEnqueteDataExist)
						&& userdata.questionInfo != null) {
					ScfYokogawaQuestionDto questionInfo = (ScfYokogawaQuestionDto) userdata.questionInfo;
					if (StringUtil.isNotEmpty(questionInfo.counter)) {
						ScfYokogawaQuestionDto tmpQuestionInfo = ScfUtil
								.getQuestionInfo(conn, questionInfo.counter);
						if (StringUtil.isNotEmpty(tmpQuestionInfo.counter)) {
							userdata.questionInfo = tmpQuestionInfo;
						} else {
							unmatchList.add(userdata);
						}
					} else {
						unmatchList.add(userdata);
					}
				}
			}
			if (!ScfUtil.downLoad(request, response, outputFileName,
					userDataList, ",", false, true, true)) {
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