package jp.co.freedom.ooxml.omron.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.freedom.common.utilities.OoxmlUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.ooxml.omron.dto.OmronChoiceDto;
import jp.co.freedom.ooxml.omron.dto.OmronEnqueteDto;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【OMRON】OOXML編集ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronOoxmlUtil {

	/**
	 * 見出しシートの作成
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param sheetId
	 *            シート番号
	 * @param name
	 *            見出し名
	 * @return 見出しシート
	 */
	public static XSSFSheet createIndexSheet(XSSFWorkbook workbook,
			int sheetId, String name) {
		assert workbook != null && sheetId > 0 && StringUtil.isNotEmpty(name);
		XSSFSheet sheet = OoxmlUtil.cloneSheet(workbook, sheetId, "m表紙",
				String.valueOf(sheetId) + name);
		OoxmlUtil.setData(sheet, 21, 0, name, null); // キャプション
		sheet.getWorkbook().setPrintArea(sheetId, 0, 10, 0, 55); // 印刷範囲の設定
		return sheet;
	}

	/**
	 * [1.現在の派遣先満足度]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM1Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 13, header); // ヘッダータイトルの設定
		saveWholeForM1Sheet(sheet, conn, positionId, 4, 5, 7); // グループ全体の出力
		saveIndividualForM1Sheet(sheet, conn, positionId, "OC", 8, 11); // OC
		saveIndividualForM1Sheet(sheet, conn, positionId, "OCG", 12, 30); // OCG
		saveIndividualForM1Sheet(sheet, conn, positionId, "外部", 31, 32); // 外部
		int nLastRow = saveQ3ForM1Sheet(sheet, conn, positionId, 33); // 継続希望
		sheet.getWorkbook().setPrintArea(nSheet, 0, 13, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [2.仕事上で困っていること]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM2Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 13, header); // ヘッダータイトルの設定
		saveWholeForM2Sheet(sheet, conn, positionId, 4, 5, 7); // グループ全体の出力
		saveIndividualForM2Sheet(sheet, conn, positionId, "OC", 8, 11); // OC
		saveIndividualForM2Sheet(sheet, conn, positionId, "OCG", 12, 30); // OCG
		saveIndividualForM2Sheet(sheet, conn, positionId, "外部", 31, 32); // 外部
		saveQ1ForM2Sheet(sheet, conn, positionId, 33); // 満足度
		int nLastRow = saveQ3ForM2Sheet(sheet, conn, positionId, 36); // 継続希望
		sheet.getWorkbook().setPrintArea(nSheet, 0, 13, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [2-1.困っている内容]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM2_1Sheet(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 14, header); // ヘッダータイトルの設定
		saveWholeForM2_1Sheet(sheet, conn, positionId, 4, 5, 7); // グループ全体の出力
		saveIndividualForM2_1Sheet(sheet, conn, positionId, "OC", 8, 11); // OC
		saveIndividualForM2_1Sheet(sheet, conn, positionId, "OCG", 12, 30); // OCG
		saveIndividualForM2_1Sheet(sheet, conn, positionId, "外部", 31, 32); // 外部
		saveQ1ForM2_1Sheet(sheet, conn, positionId, 33); // 満足度
		int nLastRow = saveQ3ForM2_1Sheet(sheet, conn, positionId, 36); // 継続希望
		sheet.getWorkbook().setPrintArea(nSheet, 0, 14, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [1.現在の派遣先満足度]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveWholeForM1Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map1 = OmronDbUtil.getQ1Map(conn, true,
				positionId, null);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map1.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map1.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [1.現在の派遣先満足度]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualForM1Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1Map(conn, false,
				positionId, target);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [1.現在の派遣先満足度]シート　Q3の回答別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ3ForM1Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3MapForM1Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
		return nRow;
	}

	/**
	 * [2-1.困っている内容]シート　Q3の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ3ForM2_1Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3MapForM2_1Sheet(
				conn, positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
		return nRow;
	}

	/**
	 * [8.派遣会社を選ぶポイント]シート　Q3の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param rank
	 *            ランク
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ3ForM8Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int rank) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3MapForM8Sheet(conn,
				positionId, rank);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
		return nRow;
	}

	/**
	 * [9.就業を決めたポイント]シート　Q3の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ3ForM9Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3MapForM9Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
		return nRow;
	}

	/**
	 * [1fa.派遣先満足度の理由(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM1faSheetForDispatch(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 5, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ1FaListByDept(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q1fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2,
							OmronDbUtil.Q1.get(enqueteInfo.q1), style2);
					OoxmlUtil.setData(sheet, nRow, 3, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 4, enqueteInfo.q1fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 6, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [1fa.派遣先満足度の理由(担当者別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM1faSheetForSales(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		// 担当者別の集計結果を出力
		List<String> salesmanList = OmronDbUtil.getSalesManList(positionId); // 担当者リスト
		for (String salesman : salesmanList) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ1FaListBySalesman(
					conn, positionId, salesman); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, salesman, style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q1fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1,
							OmronDbUtil.Q1.get(enqueteInfo.q1), style2);
					OoxmlUtil.setData(sheet, nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q1fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [2.仕事上で困っていること]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveWholeForM2Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map1 = OmronDbUtil.getQ2Map(conn, true,
				positionId, null);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map1.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map1.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [2.仕事上で困っていること]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualForM2Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ2Map(conn, false,
				positionId, target);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [2.仕事上で困っていること]シート　Q1の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1ForM2Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1MapForM2Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("大変満足".equals(entry.getKey()) || "満足".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
			} else if ("普通".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
			} else if ("大変不満".equals(entry.getKey())
					|| "不満".equals(entry.getKey())) {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
			}
		}
		for (OmronChoiceDto info : list) {
			OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
			OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
					/ (double) info.count1 * (double) 100.0, null);
			++nRow;
		}
	}

	/**
	 * [2.仕事上で困っていること]シート　Q3の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力番号
	 * @throws SQLException
	 */
	private static int saveQ3ForM2Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3MapForM2Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
		return nRow;
	}

	/**
	 * [2-1.困っている内容]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveWholeForM2_1Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ2_1Map(conn, true,
				positionId, null);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 9, (double) totalInfo.count7
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 10, (double) totalInfo.count8
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 11, (double) totalInfo.count9
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 12, (double) totalInfo.count10
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 13, (double) totalInfo.count11
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [2-1.困っている内容]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualForM2_1Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ2_1Map(conn, false,
				positionId, target);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [2-1.困っている内容]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1ForM2_1Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1MapForM2_1Sheet(
				conn, positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
				++nRow;
			}
		}
	}

	/**
	 * [2-1fa.困っている内容のその他(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM2_1faSheetForDispatch(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ2_1FaListByDept(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q2_1_fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q2_1_fa,
							style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [2-1fa.困っている内容のその他(担当者別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM2_1faSheetForSales(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 3, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<String> salesmanList = OmronDbUtil.getSalesManList(positionId); // 担当者リスト
		// グループ別の集計結果を出力
		for (String salesman : salesmanList) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ2_1FaListBySales(conn,
					positionId, salesman); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, salesman, style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q2_1_fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 2, enqueteInfo.q2_1_fa,
							style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 3, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [3.派遣先の継続意向]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM3Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 14, header); // ヘッダータイトルの設定
		saveWholeForM3Sheet(sheet, conn, positionId, 4, 5, 7); // グループ全体の出力
		saveIndividualForM3Sheet(sheet, conn, positionId, "OC", 8, 11); // OC
		saveIndividualForM3Sheet(sheet, conn, positionId, "OCG", 12, 30); // OCG
		saveIndividualForM3Sheet(sheet, conn, positionId, "外部", 31, 32); // 外部
		int nLastRow = saveQ1ForM3Sheet(sheet, conn, positionId, 33); // 満足度
		sheet.getWorkbook().setPrintArea(nSheet, 0, 14, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [3.派遣先の継続意向]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveWholeForM3Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map1 = OmronDbUtil.getQ3Map(conn, true,
				positionId, null);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map1.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map1.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [3.派遣先の継続意向]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualForM3Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ3Map(conn, false,
				positionId, target);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [3.派遣先の継続意向]シート　Q1の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ1ForM3Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1MapForM3Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("大変満足".equals(entry.getKey()) || "満足".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
			} else if ("普通".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
			} else if ("大変不満".equals(entry.getKey())
					|| "不満".equals(entry.getKey())) {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
			}
		}

		for (OmronChoiceDto info : list) {
			OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
			OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
					/ (double) info.count1 * (double) 100.0, null);
			++nRow;
		}
		return nRow;
	}

	/**
	 * [3-1fa.継続をしないや検討中の理由(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM3faSheetForDispatch(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 6, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ3FaListByDept(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q3_fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil
							.setData(sheet, nRow, 3, enqueteInfo.q3_fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 5, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [3-1fa.継続をしないや検討中の理由(担当者別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM3faSheetForSales(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 6, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<String> salesmanList = OmronDbUtil.getSalesManList(positionId); // 担当者リスト
		// グループ別の集計結果を出力
		for (String salesman : salesmanList) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ3FaListBySales(conn,
					positionId, salesman); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, salesman, style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q3_fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, "・", style3);
					OoxmlUtil
							.setData(sheet, nRow, 2, enqueteInfo.q3_fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [5-1.資格取得(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM5_1Sheet(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 1, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ5_1List(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.capacities)
						&& isBeneficial(enqueteInfo.capacities)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 3, enqueteInfo.capacities,
							style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [5-2.受講研修セミナー(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM5_2Sheet(int nSheet, XSSFSheet sheet,
			String header, String positionId, Connection conn)
			throws SQLException {
		setHeaderTitle(sheet, 0, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ5_2List(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.trainings)
						&& isBeneficial(enqueteInfo.trainings)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 3, enqueteInfo.trainings,
							style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [6.新たに習得したスキル(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM6Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ6List(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q6)
						&& isBeneficial(enqueteInfo.q6)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q6, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [7.習得したいスキルや受けたい研修・講演(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM7Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 4, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		List<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());
		// グループ別の集計結果を出力
		for (Map.Entry<String, String> entry : entries) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ7List(conn,
					positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q7)
						&& isBeneficial(enqueteInfo.q7)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q7, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [8.派遣会社を選ぶポイント(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param rank
	 *            ランク
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM8Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, int rank, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 14, header); // ヘッダータイトルの設定
		saveWholeForM8Sheet(sheet, conn, positionId, 4, 5, 7, rank); // グループ全体の出力
		saveIndividualForM8Sheet(sheet, conn, positionId, "OC", 8, 11, rank); // OC
		saveIndividualForM8Sheet(sheet, conn, positionId, "OCG", 12, 30, rank); // OCG
		saveIndividualForM8Sheet(sheet, conn, positionId, "外部", 31, 32, rank); // 外部
		saveQ1ForM8Sheet(sheet, conn, positionId, 33, rank); // 満足度
		int nLastRow = saveQ3ForM8Sheet(sheet, conn, positionId, 36, rank); // 継続希望
		sheet.getWorkbook().setPrintArea(nSheet, 0, 14, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [8.派遣会社を選ぶポイント]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @param rank
	 *            ランク
	 * @throws SQLException
	 */
	private static void saveWholeForM8Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow, int rank)
			throws SQLException {
		Map<String, OmronChoiceDto> map1 = OmronDbUtil.getQ8Map(conn, true,
				positionId, null, rank);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map1.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map1.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 9, (double) totalInfo.count7
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 10, (double) totalInfo.count8
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 11, (double) totalInfo.count9
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [8.派遣会社を選ぶポイント]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @param rank
	 *            ランク
	 * @throws SQLException
	 */
	private static void saveIndividualForM8Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow, int rank) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ8Map(conn, false,
				positionId, target, rank);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [8.派遣会社を選ぶポイント]シート　Q1の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param rank
	 *            ランク
	 * @throws SQLException
	 */
	private static void saveQ1ForM8Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int rank) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1MapForM8Sheet(conn,
				positionId, rank);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());

		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("大変満足".equals(entry.getKey()) || "満足".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
				list.get(0).count7 += info.count7;
				list.get(0).count8 += info.count8;
				list.get(0).count9 += info.count9;
			} else if ("普通".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
				list.get(1).count7 += info.count7;
				list.get(1).count8 += info.count8;
				list.get(1).count9 += info.count9;
			} else if ("大変不満".equals(entry.getKey())
					|| "不満".equals(entry.getKey())) {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
				list.get(2).count7 += info.count7;
				list.get(2).count8 += info.count8;
				list.get(2).count9 += info.count9;
			}
		}
		for (OmronChoiceDto info : list) {
			OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
			OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
					/ (double) info.count1 * (double) 100.0, null);
			++nRow;
		}
	}

	/**
	 * [9.就業を決めたポイント(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM9Sheet(int nSheet, XSSFSheet sheet, String header,
			String positionId, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 14, header); // ヘッダータイトルの設定
		saveWholeForM9Sheet(sheet, conn, positionId, 4, 5, 7); // グループ全体の出力
		saveIndividualForM9Sheet(sheet, conn, positionId, "OC", 8, 11); // OC
		saveIndividualForM9Sheet(sheet, conn, positionId, "OCG", 12, 30); // OCG
		saveIndividualForM9Sheet(sheet, conn, positionId, "外部", 31, 32); // 外部
		saveQ1ForM9Sheet(sheet, conn, positionId, 33); // 満足度
		int nLastRow = saveQ3ForM9Sheet(sheet, conn, positionId, 36); // 継続希望
		sheet.getWorkbook().setPrintArea(nSheet, 0, 14, 0, nLastRow); // 印刷範囲の設定
	}

	/**
	 * [9.就業を決めたポイント]シート　グループ全体の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveWholeForM9Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map1 = OmronDbUtil.getQ9Map(conn, true,
				positionId, null);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map1.entrySet());
		// 合計
		OmronChoiceDto totalInfo = map1.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 9, (double) totalInfo.count7
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 10, (double) totalInfo.count8
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 11, (double) totalInfo.count9
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 12, (double) totalInfo.count10
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 13, (double) totalInfo.count11
				/ (double) totalInfo.count1 * (double) 100.0, null);
		// グループ
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [9.就業を決めたポイント]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualForM9Sheet(XSSFSheet sheet,
			Connection conn, String positionId, String target, int startRow,
			int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ9Map(conn, false,
				positionId, target);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String key = entry.getKey();
			if (!"total".equals(key)) {
				int nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow,
						endRow, key); // 出力先行番号の探索
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
						/ (double) info.count1 * (double) 100.0, null);
			}
		}
	}

	/**
	 * [9.就業を決めたポイント]シート　Q1の回答別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param positionId
	 *            所属ID
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1ForM9Sheet(XSSFSheet sheet, Connection conn,
			String positionId, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronDbUtil.getQ1MapForM9Sheet(conn,
				positionId);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());

		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("大変満足".equals(entry.getKey()) || "満足".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
				list.get(0).count7 += info.count7;
				list.get(0).count8 += info.count8;
				list.get(0).count9 += info.count9;
				list.get(0).count10 += info.count10;
				list.get(0).count11 += info.count11;
			} else if ("普通".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
				list.get(1).count7 += info.count7;
				list.get(1).count8 += info.count8;
				list.get(1).count9 += info.count9;
				list.get(1).count10 += info.count10;
				list.get(1).count11 += info.count11;
			} else if ("大変不満".equals(entry.getKey())
					|| "不満".equals(entry.getKey())) {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
				list.get(2).count7 += info.count7;
				list.get(2).count8 += info.count8;
				list.get(2).count9 += info.count9;
				list.get(2).count10 += info.count10;
				list.get(2).count11 += info.count11;
			}
		}
		for (OmronChoiceDto info : list) {
			OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
			OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 6, (double) info.count4
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 10, (double) info.count8
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 11, (double) info.count9
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 12, (double) info.count10
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 13, (double) info.count11
					/ (double) info.count1 * (double) 100.0, null);
			++nRow;
		}
	}

	/**
	 * [9fa.派遣先満足度の理由(派遣先別)]シートの編集
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param header
	 *            ヘッダータイトル
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM9faSheetForDispatch(int nSheet, XSSFSheet sheet,
			String header, Connection conn) throws SQLException {
		setHeaderTitle(sheet, 0, 5, header); // ヘッダー名の設定
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		// 所属別の集計結果を出力
		for (int positionId = 1; positionId <= 5; positionId++) {
			List<OmronEnqueteDto> list = OmronDbUtil.getQ9FaListByDept(conn,
					String.valueOf(positionId)); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0,
					OmronDbUtil.POSITION.get(positionId), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				OoxmlUtil.setData(sheet, ++nRow, 1,
						OmronDbUtil.Q1.get(enqueteInfo.q1), style2);
				OoxmlUtil.setData(sheet, nRow, 2, enqueteInfo.q9_fa, style3);
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 5, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * 回答内容が「なし」等の不要なものでないかどうか検証
	 * 
	 * @param value
	 *            この1年で取得した資格
	 * @return
	 */
	private static boolean isBeneficial(String value) {
		assert StringUtil.isNotEmpty(value);
		String[] keywords = { "^なし$", "^なし\\[\\]$", "^無し$", "特になし", "特にありません" };
		for (String keyword : keywords) {
			if (StringUtil.find(value, keyword)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 罫線スタイルの<b>CellStyle</b>を生成
	 * 
	 * @param workbook
	 *            ワークブック
	 * @return <b>CellStyle</b>
	 */
	private static XSSFCellStyle createIndexCaption(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		return style;
	}

	/**
	 * ヘッダータイトルの設定
	 * 
	 * @param sheet
	 *            シート
	 * @param nRow
	 *            行番号
	 * @param nColumn
	 *            列番号
	 * @param title
	 *            ヘッダータイトル
	 */
	public static void setHeaderTitle(XSSFSheet sheet, int nRow, int nColumn,
			String title) {
		XSSFCellStyle style = createIndexCaption(sheet.getWorkbook());
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		OoxmlUtil.setData(sheet, nRow, nColumn, title, style);
	}

	/**
	 * マスターシートの削除
	 * 
	 * @param workbook
	 *            ワークブック
	 */
	public static void removeAllMasterSheet(XSSFWorkbook workbook) {
		OoxmlUtil.removeSheet(workbook, "m表紙");
		OoxmlUtil.removeSheet(workbook, "all1");
		OoxmlUtil.removeSheet(workbook, "all2");
		OoxmlUtil.removeSheet(workbook, "all3");
		OoxmlUtil.removeSheet(workbook, "all4");
		OoxmlUtil.removeSheet(workbook, "m1");
		OoxmlUtil.removeSheet(workbook, "m1fa派");
		OoxmlUtil.removeSheet(workbook, "m1fa担");
		OoxmlUtil.removeSheet(workbook, "m2");
		OoxmlUtil.removeSheet(workbook, "m2_1");
		OoxmlUtil.removeSheet(workbook, "m2_1fa派");
		OoxmlUtil.removeSheet(workbook, "m2_1fa担");
		OoxmlUtil.removeSheet(workbook, "m3");
		OoxmlUtil.removeSheet(workbook, "m3fa派");
		OoxmlUtil.removeSheet(workbook, "m3fa担");
		OoxmlUtil.removeSheet(workbook, "m5_1派");
		OoxmlUtil.removeSheet(workbook, "m5_2派");
		OoxmlUtil.removeSheet(workbook, "m6派");
		OoxmlUtil.removeSheet(workbook, "m7派");
		OoxmlUtil.removeSheet(workbook, "m8r1");
		OoxmlUtil.removeSheet(workbook, "m8r2");
		OoxmlUtil.removeSheet(workbook, "m8r3");
		OoxmlUtil.removeSheet(workbook, "m9");
	}
}
