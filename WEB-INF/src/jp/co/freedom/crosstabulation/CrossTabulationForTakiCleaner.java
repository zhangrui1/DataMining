package jp.co.freedom.crosstabulation;

import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.OoxmlUtil;
import jp.co.freedom.common.utilities.StringUtil;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【たき工房】使用洗剤クロス集計のダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class CrossTabulationForTakiCleaner extends HttpServlet {

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

		// 条件ID
		Object filterConditionObj = request.getParameter("conditionType");
		String filterConditionStr = (String) filterConditionObj;
		assert StringUtil.isNotEmpty(filterConditionStr);
		int filterConditionType = Integer.parseInt(filterConditionStr);
		// OOXML形式でExcelファイルをオープン
		XSSFWorkbook workbook = OoxmlUtil
				.load(CrossTabulationForTakiConfig.EXCEL_FILENAME_PATH);
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBサーバーへの接続
			Connection conn = DriverManager.getConnection(
					CrossTabulationForTakiConfig.PSQL_URL,
					CrossTabulationForTakiConfig.PSQL_USER,
					CrossTabulationForTakiConfig.PSQL_PASSWORD);

			// ヘッダースタイル
			XSSFCellStyle headerStyle = OoxmlUtil
					.createDefaultTableHeaderCellStyle(workbook, false, true,
							10, new Color(184, 204, 228));

			// 29種の使用洗剤各々のシートに処理を行う
			for (int sheetId = 1; sheetId <= 30; sheetId++) {
				System.out.println(sheetId);
				String sheetName = CrossTabulationForTakiUtil.CLEANER_MAP
						.get(sheetId);
				XSSFSheet sheet = workbook.cloneSheet(0);
				workbook.setSheetName(sheetId,
						StringUtil.isNotEmpty(sheetName) ? sheetName : "ALL");
				/*
				 * 年齢×業種のクロス集計の作成
				 */
				// 男女全体
				Map<CrossTabulationForTakiDto, Integer> mapType1 = CrossTabulationForTakiUtil
						.executeCleanerCrossTabulate(conn, filterConditionType,
								sheetName, 1);
				CrossTabulationForTakiUtil
						.saveCleanerCrossTabulationDataForCrossTabulation(
								sheet, sheetId, mapType1, 3, 13, 0, 12,
								filterConditionType, 1);
				// 女性のみ
				Map<CrossTabulationForTakiDto, Integer> mapType2 = CrossTabulationForTakiUtil
						.executeCleanerCrossTabulate(conn, filterConditionType,
								sheetName, 2);
				CrossTabulationForTakiUtil
						.saveCleanerCrossTabulationDataForCrossTabulation(
								sheet, sheetId, mapType2, 18, 28, 0, 12,
								filterConditionType, 2);
				// 男性のみ
				Map<CrossTabulationForTakiDto, Integer> mapType3 = CrossTabulationForTakiUtil
						.executeCleanerCrossTabulate(conn, filterConditionType,
								sheetName, 3);
				CrossTabulationForTakiUtil
						.saveCleanerCrossTabulationDataForCrossTabulation(
								sheet, sheetId, mapType3, 33, 43, 0, 12,
								filterConditionType, 3);

				// /*
				// * 年齢×都道府県のクロス集計の作成
				// */
				// // 男女全体
				// Map<CrossTabulationForTakiDto, Integer> cleanerType1 =
				// CrossTabulationForTakiUtil
				// .executeCleanerAddrCrossTabulate(conn,
				// filterConditionType, sheetName, 1);
				// CrossTabulationForTakiUtil
				// .saveCleanerAddrCrossTabulationDataForCrossTabulation(
				// sheet, sheetId, cleanerType1, 49, 95, 0, 12,
				// filterConditionType, 1);
				// // 女性のみ
				// Map<CrossTabulationForTakiDto, Integer> cleanerType2 =
				// CrossTabulationForTakiUtil
				// .executeCleanerAddrCrossTabulate(conn,
				// filterConditionType, sheetName, 2);
				// CrossTabulationForTakiUtil
				// .saveCleanerAddrCrossTabulationDataForCrossTabulation(
				// sheet, sheetId, cleanerType2, 100, 146, 0, 12,
				// filterConditionType, 2);
				// // 男性のみ
				// Map<CrossTabulationForTakiDto, Integer> cleanerType3 =
				// CrossTabulationForTakiUtil
				// .executeCleanerAddrCrossTabulate(conn,
				// filterConditionType, sheetName, 3);
				// CrossTabulationForTakiUtil
				// .saveCleanerAddrCrossTabulationDataForCrossTabulation(
				// sheet, sheetId, cleanerType3, 151, 197, 0, 12,
				// filterConditionType, 3);

				int lastRowType1 = 46;
				/*
				 * ランキングデータの作成
				 */
				List<LankingForTakiDto> rankingType2 = CrossTabulationForTakiUtil
						.createLanking(mapType2);
				OoxmlUtil.setData(sheet, lastRowType1, 0, "2)女性のみ　ベスト5",
						OoxmlUtil.createDefaultCellStyle(workbook, false,
								false, 11));
				CrossTabulationForTakiUtil.outputLankingHeader(sheet, 0,
						lastRowType1 + 1, headerStyle);
				int lastRowType2 = CrossTabulationForTakiUtil.saveLankingData(
						conn, filterConditionType, sheetId, 2, sheet,
						rankingType2, 0, lastRowType1 + 2, 5, false);

				List<LankingForTakiDto> rankingType3 = CrossTabulationForTakiUtil
						.createLanking(mapType3);
				OoxmlUtil.setData(sheet, lastRowType2 + 1, 0, "2)男性のみ　ベスト5",
						OoxmlUtil.createDefaultCellStyle(workbook, false,
								false, 11));
				CrossTabulationForTakiUtil.outputLankingHeader(sheet, 0,
						lastRowType2 + 2, headerStyle);
				CrossTabulationForTakiUtil.saveLankingData(conn,
						filterConditionType, sheetId, 3, sheet, rankingType3,
						0, lastRowType2 + 3, 5, false);
			}
			System.out.println("◆正常終了◆");
			conn.close();
			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/result.jsp");
			dispatcher.forward(request, response);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// フォーカス制御
		OoxmlUtil.setFocusFistCell(workbook);

		// 計算式の強制的再評価
		OoxmlUtil.setForceFormulaRecalculation(workbook);

		// Excelファイル出力
		if (!OoxmlUtil.saveAs(workbook,
				CrossTabulationForTakiConfig.EXCEL_FILENAME_PATH)) {
			System.out.println("ファイル出力失敗");
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