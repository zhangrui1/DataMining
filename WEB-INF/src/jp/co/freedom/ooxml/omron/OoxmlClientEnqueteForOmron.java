package jp.co.freedom.ooxml.omron;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.OoxmlUtil;
import jp.co.freedom.ooxml.omron.utilities.OmronClientOoxmlUtil;
import jp.co.freedom.ooxml.omron.utilities.OmronConfig;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【Omron】顧客評価Excelファイルの編集用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class OoxmlClientEnqueteForOmron extends HttpServlet {

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

		// OOXML形式でExcelファイルをオープン
		XSSFWorkbook workbook = OoxmlUtil
				.load(OmronConfig.CLIENT_ENQUETE_FILE_PATH);
		assert workbook != null;
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBサーバーへの接続
			conn = DriverManager.getConnection(OmronConfig.PSQL_URL,
					OmronConfig.PSQL_USER, OmronConfig.PSQL_PASSWORD);
			/*
			 * Q1. 派遣社員評価
			 */
			// Q1-1～Q1-9
			String questions1[] = { "V_Q1_1", "V_Q1_2", "V_Q1_3", "V_Q1_4",
					"V_Q1_5", "V_Q1_6", "V_Q1_7", "V_Q1_8", "V_Q1_9" };
			int question1Id = -1;
			for (int nPage = 3; nPage <= 11; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM1Sheet(nPage, sheet,
						questions1[++question1Id], conn);
			}
			// Q1-9 FA （派遣先別）
			for (int nPage = 13; nPage <= 17; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM1faSheetForDispatch(nPage, sheet,
						String.valueOf(nPage - 12), conn);
			}
			// Q1-9 FA （担当者別）
			for (int nPage = 19; nPage <= 23; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM1faSheetForSales(nPage, sheet,
						String.valueOf(nPage - 18), conn);
			}
			/*
			 * Q2.弊社評価
			 */
			String questions2[] = { "V_Q2_4", "V_Q2_1", "V_Q2_2", "V_Q2_3" };
			int question2Id = -1;
			for (int nPage = 25; nPage <= 28; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM2Sheet(nPage, sheet,
						questions2[++question2Id], conn);
			}
			/*
			 * Q3.他の派遣会社の利用状況
			 */
			String questions345[] = { "V_Q3", "V_Q4", "V_Q5" };
			int question345Id = -1;
			for (int nPage = 29; nPage <= 31; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM345Sheet(nPage, sheet,
						questions345[++question345Id], conn);
			}
			// Q8.弊社へのご意見ご要望 （派遣先別）
			for (int nPage = 33; nPage <= 37; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM8SheetForDispatch(nPage, sheet,
						String.valueOf(nPage - 32), conn);
			}
			// Q1-9 FA （担当者別）
			for (int nPage = 39; nPage <= 43; nPage++) {
				XSSFSheet sheet = OoxmlUtil.getSheet(workbook,
						String.valueOf(nPage));
				OmronClientOoxmlUtil.editM8SheetForSales(nPage, sheet,
						String.valueOf(nPage - 38), conn);
			}
			// Q6.「派遣」に関しての相談事や疑問点
			XSSFSheet sheet45 = OoxmlUtil.getSheet(workbook, "45");
			OmronClientOoxmlUtil.editM6Sheet(45, sheet45, "V_Q6", conn);
			// Q7.今後の外部人材活用の計画
			XSSFSheet sheet50 = OoxmlUtil.getSheet(workbook, "50");
			OmronClientOoxmlUtil.editM7Sheet(50, sheet50, "V_Q7", conn);
			// Q7.今後の外部人材活用の計画 FA
			XSSFSheet sheet52 = OoxmlUtil.getSheet(workbook, "52");
			OmronClientOoxmlUtil.editM7FaSheet(52, sheet52, conn);

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

		// マスターシートの削除
		// OmronOoxmlUtil.removeAllMasterSheet(workbook);

		// フォーカス制御
		OoxmlUtil.setFocusFistCell(workbook);

		// 計算式の強制的再評価
		OoxmlUtil.setForceFormulaRecalculation(workbook);

		// 拡大率
		OoxmlUtil.setZoom(workbook, 9, 10);

		// Excelファイル出力
		if (!OoxmlUtil.saveAs(workbook, OmronConfig.CLIENT_ENQUETE_FILE_PATH)) {
			System.out.println("ファイル出力失敗");
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
}