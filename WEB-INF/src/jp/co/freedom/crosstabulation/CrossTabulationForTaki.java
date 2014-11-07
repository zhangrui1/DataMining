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

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【たき工房】クロス集計のダウンロード用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class CrossTabulationForTaki extends HttpServlet {

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
		assert workbook != null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBサーバーへの接続
			Connection conn = DriverManager.getConnection(
					CrossTabulationForTakiConfig.PSQL_URL,
					CrossTabulationForTakiConfig.PSQL_USER,
					CrossTabulationForTakiConfig.PSQL_PASSWORD);
			// ヘッダースタイルの設定
			XSSFCellStyle headerStyle = OoxmlUtil
					.createDefaultTableHeaderCellStyle(workbook, false, true,
							10, new Color(184, 204, 228));
			XSSFCellStyle detailHeaderStyle = OoxmlUtil
					.createDefaultTableHeaderCellStyle(workbook, false, true,
							10, new Color(208, 173, 215));
			// 47都道府県各々のシートに処理を行う
			for (int sheetId = 0; sheetId <= 47; sheetId++) {
				System.out.println(sheetId);
				XSSFSheet sheet = workbook.getSheetAt(sheetId); // シート
				/*
				 * 年齢×業種のクロス集計の作成
				 */
				// 男女全体
				Map<CrossTabulationForTakiDto, Integer> mapType1 = CrossTabulationForTakiUtil
						.executeCrossTabulate(conn, filterConditionType,
								sheetId, 1);
				CrossTabulationForTakiUtil
						.saveCrossTabulationDataForCrossTabulation(sheet,
								sheetId, mapType1, 4, 15, 8, 19,
								filterConditionType, 1);
				// 女性のみ
				Map<CrossTabulationForTakiDto, Integer> mapType2 = CrossTabulationForTakiUtil
						.executeCrossTabulate(conn, filterConditionType,
								sheetId, 2);
				CrossTabulationForTakiUtil
						.saveCrossTabulationDataForCrossTabulation(sheet,
								sheetId, mapType2, 21, 32, 8, 19,
								filterConditionType, 2);
				// 男性のみ
				Map<CrossTabulationForTakiDto, Integer> mapType3 = CrossTabulationForTakiUtil
						.executeCrossTabulate(conn, filterConditionType,
								sheetId, 3);
				CrossTabulationForTakiUtil
						.saveCrossTabulationDataForCrossTabulation(sheet,
								sheetId, mapType3, 38, 49, 8, 19,
								filterConditionType, 3);

				/*
				 * ランキングデータの作成
				 */
				// 男女全体
				List<LankingForTakiDto> rankingType1 = CrossTabulationForTakiUtil
						.createLanking(mapType1);
				OoxmlUtil.setData(sheet, 53, 8, "1)男女全体　ベスト10", OoxmlUtil
						.createDefaultCellStyle(workbook, false, false, 11));
				CrossTabulationForTakiUtil.outputLankingHeader(sheet, 8, 54,
						headerStyle, detailHeaderStyle);
				int lastRowType1 = CrossTabulationForTakiUtil.saveLankingData(
						conn, filterConditionType, sheetId, 1, sheet,
						rankingType1, 8, 55, 10, true);
				// 女性のみ
				List<LankingForTakiDto> rankingType2 = CrossTabulationForTakiUtil
						.createLanking(mapType2);
				OoxmlUtil.setData(sheet, lastRowType1 + 2, 8, "2)女性のみ　ベスト10",
						OoxmlUtil.createDefaultCellStyle(workbook, false,
								false, 11));
				CrossTabulationForTakiUtil.outputLankingHeader(sheet, 8,
						lastRowType1 + 3, headerStyle, detailHeaderStyle);
				int lastRowType2 = CrossTabulationForTakiUtil.saveLankingData(
						conn, filterConditionType, sheetId, 2, sheet,
						rankingType2, 8, lastRowType1 + 4, 10, true);
				// 男性のみ
				List<LankingForTakiDto> rankingType3 = CrossTabulationForTakiUtil
						.createLanking(mapType3);
				OoxmlUtil.setData(sheet, lastRowType2 + 2, 8, "3)男性のみ　ベスト10",
						OoxmlUtil.createDefaultCellStyle(workbook, false,
								false, 11));
				CrossTabulationForTakiUtil.outputLankingHeader(sheet, 8,
						lastRowType2 + 3, headerStyle, detailHeaderStyle);
				int lastRowType3 = CrossTabulationForTakiUtil.saveLankingData(
						conn, filterConditionType, sheetId, 3, sheet,
						rankingType3, 8, lastRowType2 + 4, 10, true);

				/*
				 * 使用洗剤の集計
				 */
				// 男女全体
				Map<String, Integer> cleanerTabulationType1 = CrossTabulationForTakiUtil
						.executeCleanerTabulate(conn, filterConditionType,
								sheetId, 1);
				// 女性のみ
				Map<String, Integer> cleanerTabulationType2 = CrossTabulationForTakiUtil
						.executeCleanerTabulate(conn, filterConditionType,
								sheetId, 2);
				// 男性のみ
				Map<String, Integer> cleanerTabulationType3 = CrossTabulationForTakiUtil
						.executeCleanerTabulate(conn, filterConditionType,
								sheetId, 3);
				int maxRow = lastRowType3;

				// キャプション出力
				XSSFCellStyle captionStyle = OoxmlUtil.createDefaultCellStyle(
						workbook, true, false, 14);
				XSSFCellStyle subCaptionStyle = OoxmlUtil
						.createDefaultCellStyle(workbook, false, false, 11);
				OoxmlUtil.setData(sheet, maxRow + 2, 8, "2.使用洗剤だけの集計",
						captionStyle);
				OoxmlUtil.setData(sheet, maxRow + 3, 8, "1)男女全体　降順",
						subCaptionStyle);
				OoxmlUtil.setData(sheet, maxRow + 3, 13, "2)女性のみ　降順",
						subCaptionStyle);
				OoxmlUtil.setData(sheet, maxRow + 3, 18, "3)男性のみ　降順",
						subCaptionStyle);

				int tableStartRow = maxRow + 4;
				// ヘッダーラベルの出力
				OoxmlUtil.setData(sheet, tableStartRow, 8, "使用洗剤", null);
				OoxmlUtil.setData(sheet, tableStartRow, 11, "件数", null);
				OoxmlUtil.setData(sheet, tableStartRow, 13, "使用洗剤", null);
				OoxmlUtil.setData(sheet, tableStartRow, 16, "件数", null);
				OoxmlUtil.setData(sheet, tableStartRow, 18, "使用洗剤", null);
				OoxmlUtil.setData(sheet, tableStartRow, 21, "件数", null);
				OoxmlUtil.setStyle(sheet, tableStartRow, tableStartRow, 8, 11,
						headerStyle);
				OoxmlUtil.setStyle(sheet, tableStartRow, tableStartRow, 13, 16,
						headerStyle);
				OoxmlUtil.setStyle(sheet, tableStartRow, tableStartRow, 18, 21,
						headerStyle);
				// ヘッダーセルの結合
				// [備忘]セル結合はデータ出力／スタイル設定後に
				sheet.addMergedRegion(new CellRangeAddress(tableStartRow,
						tableStartRow, 8, 10));
				sheet.addMergedRegion(new CellRangeAddress(tableStartRow,
						tableStartRow, 13, 15));
				sheet.addMergedRegion(new CellRangeAddress(tableStartRow,
						tableStartRow, 18, 20));
				// データ出力
				CrossTabulationForTakiUtil.saveCleanerData(sheet,
						cleanerTabulationType1, 8, tableStartRow + 1);
				CrossTabulationForTakiUtil.saveCleanerData(sheet,
						cleanerTabulationType2, 13, tableStartRow + 1);
				CrossTabulationForTakiUtil.saveCleanerData(sheet,
						cleanerTabulationType3, 18, tableStartRow + 1);
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