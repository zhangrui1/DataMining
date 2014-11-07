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
import jp.co.freedom.ooxml.omron.utilities.OmronConfig;
import jp.co.freedom.ooxml.omron.utilities.OmronDbUtil;
import jp.co.freedom.ooxml.omron.utilities.OmronOoxmlUtil;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【Omron】スタッフアンケートExcelファイルの編集用サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class OoxmlStaffEnqueteForOmron extends HttpServlet {

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
				.load(OmronConfig.STAFF_ENQUETE_FILE_PATH);
		assert workbook != null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBサーバーへの接続
			Connection conn = DriverManager.getConnection(OmronConfig.PSQL_URL,
					OmronConfig.PSQL_USER, OmronConfig.PSQL_PASSWORD);
			int nSheet = 1;
			// 全体
			String headerAll = "全店";
			OmronOoxmlUtil.createIndexSheet(workbook, ++nSheet, headerAll); // キャプション
			// 1.現在の派遣先満足度
			XSSFSheet m1SheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"m1", String.valueOf(nSheet));
			OmronOoxmlUtil
					.editM1Sheet(nSheet, m1SheetAll, headerAll, "0", conn);
			// 2.仕事上で困っていること
			XSSFSheet m2SheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"m2", String.valueOf(nSheet));
			OmronOoxmlUtil
					.editM2Sheet(nSheet, m2SheetAll, headerAll, "0", conn);
			// 2-1.仕事上で困っていること(困っている内容)
			XSSFSheet m2_1SheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"m2_1", String.valueOf(nSheet));
			OmronOoxmlUtil.editM2_1Sheet(nSheet, m2_1SheetAll, headerAll, "0",
					conn);
			// 3.派遣先の継続意向
			XSSFSheet m3SheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"m3", String.valueOf(nSheet));
			OmronOoxmlUtil
					.editM3Sheet(nSheet, m3SheetAll, headerAll, "0", conn);
			// 8.派遣会社を選ぶポイント(派遣先別)
			for (int nRank = 1; nRank <= 3; nRank++) {
				XSSFSheet m8Sheet1 = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m8r" + String.valueOf(nRank), String.valueOf(nSheet));
				OmronOoxmlUtil.editM8Sheet(nSheet, m8Sheet1, headerAll, "0",
						nRank, conn);
			}
			// 9.オムロンパーソネルでの就業を決めたポイント(派遣先別)
			XSSFSheet m9SheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"m9", String.valueOf(nSheet));
			OmronOoxmlUtil
					.editM9Sheet(nSheet, m9SheetAll, headerAll, "0", conn);
			// 9FA.オムロンパーソネルでの就業を決めたポイント(派遣先別)
			XSSFSheet m9FaSheetAll = OoxmlUtil.cloneSheet(workbook, ++nSheet,
					"all1", String.valueOf(nSheet));
			OmronOoxmlUtil.editM9faSheetForDispatch(nSheet, m9FaSheetAll,
					headerAll, conn);

			// 拠点別まとめ
			OoxmlUtil.cloneSheet(workbook, ++nSheet, "all2",
					String.valueOf(nSheet));
			OoxmlUtil.cloneSheet(workbook, ++nSheet, "all3",
					String.valueOf(nSheet));
			OoxmlUtil.cloneSheet(workbook, ++nSheet, "all4",
					String.valueOf(nSheet));

			// 拠点別
			for (int positionId = 1; positionId <= 5; positionId++) {
				String header = OmronDbUtil.POSITION.get(positionId);
				OmronOoxmlUtil.createIndexSheet(workbook, ++nSheet, header); // キャプション
				// 1.現在の派遣先満足度
				XSSFSheet m1Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m1", String.valueOf(nSheet));
				OmronOoxmlUtil.editM1Sheet(nSheet, m1Sheet, header,
						String.valueOf(positionId), conn);
				// 1FA.派遣先満足度の理由(派遣先別)
				XSSFSheet m1faSheet1 = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m1fa派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM1faSheetForDispatch(nSheet, m1faSheet1,
						header, String.valueOf(positionId), conn);
				// 1FA.派遣先満足度の理由(担当者別)
				XSSFSheet m1faSheet2 = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m1fa担", String.valueOf(nSheet));
				OmronOoxmlUtil.editM1faSheetForSales(nSheet, m1faSheet2,
						header, String.valueOf(positionId), conn);
				// 2.仕事上で困っていること
				XSSFSheet m2Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m2", String.valueOf(nSheet));
				OmronOoxmlUtil.editM2Sheet(nSheet, m2Sheet, header,
						String.valueOf(positionId), conn);
				// 2-1.仕事上で困っていること(困っている内容)
				XSSFSheet m2_1Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m2_1", String.valueOf(nSheet));
				OmronOoxmlUtil.editM2_1Sheet(nSheet, m2_1Sheet, header,
						String.valueOf(positionId), conn);
				// 2-1FA.困っている内容のその他(派遣先別)
				XSSFSheet m2_1faSheet1 = OoxmlUtil.cloneSheet(workbook,
						++nSheet, "m2_1fa派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM2_1faSheetForDispatch(nSheet, m2_1faSheet1,
						header, String.valueOf(positionId), conn);
				// 2-1FA.困っている内容のその他(担当者別)
				XSSFSheet m2_1faSheet2 = OoxmlUtil.cloneSheet(workbook,
						++nSheet, "m2_1fa担", String.valueOf(nSheet));
				OmronOoxmlUtil.editM2_1faSheetForSales(nSheet, m2_1faSheet2,
						header, String.valueOf(positionId), conn);
				// 3.派遣先の継続意向
				XSSFSheet m3Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m3", String.valueOf(nSheet));
				OmronOoxmlUtil.editM3Sheet(nSheet, m3Sheet, header,
						String.valueOf(positionId), conn);
				// 3fa.継続をしないや検討中の理由(派遣先別)
				XSSFSheet m3faSheet1 = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m3fa派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM3faSheetForDispatch(nSheet, m3faSheet1,
						header, String.valueOf(positionId), conn);
				// 3fa.継続をしないや検討中の理由(担当者別)
				XSSFSheet m3faSheet2 = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m3fa担", String.valueOf(nSheet));
				OmronOoxmlUtil.editM3faSheetForSales(nSheet, m3faSheet2,
						header, String.valueOf(positionId), conn);
				// 5-1.資格取得(派遣先別)
				XSSFSheet m5_1Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m5_1派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM5_1Sheet(nSheet, m5_1Sheet, header,
						String.valueOf(positionId), conn);
				// 5-2.受講研修セミナー(派遣先別)
				XSSFSheet m5_2Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m5_2派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM5_2Sheet(nSheet, m5_2Sheet, header,
						String.valueOf(positionId), conn);
				// 6.新たに習得したスキル(派遣先別)
				XSSFSheet m6Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m6派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM6Sheet(nSheet, m6Sheet, header,
						String.valueOf(positionId), conn);
				// 7.習得したいスキルや受けたい研修・講演(派遣先別)
				XSSFSheet m7Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m7派", String.valueOf(nSheet));
				OmronOoxmlUtil.editM7Sheet(nSheet, m7Sheet, header,
						String.valueOf(positionId), conn);
				// 8.派遣会社を選ぶポイント(派遣先別)
				for (int nRank = 1; nRank <= 3; nRank++) {
					XSSFSheet m8Sheet1 = OoxmlUtil.cloneSheet(workbook,
							++nSheet, "m8r" + String.valueOf(nRank),
							String.valueOf(nSheet));
					OmronOoxmlUtil.editM8Sheet(nSheet, m8Sheet1, header,
							String.valueOf(positionId), nRank, conn);
				}
				// 9.オムロンパーソネルでの就業を決めたポイント(派遣先別)
				XSSFSheet m9Sheet = OoxmlUtil.cloneSheet(workbook, ++nSheet,
						"m9", String.valueOf(nSheet));
				OmronOoxmlUtil.editM9Sheet(nSheet, m9Sheet, header,
						String.valueOf(positionId), conn);
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

		// マスターシートの削除
		OmronOoxmlUtil.removeAllMasterSheet(workbook);

		// フォーカス制御
		OoxmlUtil.setFocusFistCell(workbook);

		// 計算式の強制的再評価
		OoxmlUtil.setForceFormulaRecalculation(workbook);

		// 拡大率
		OoxmlUtil.setZoom(workbook, 8, 10);

		// Excelファイル出力
		if (!OoxmlUtil.saveAs(workbook, OmronConfig.STAFF_ENQUETE_FILE_PATH)) {
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