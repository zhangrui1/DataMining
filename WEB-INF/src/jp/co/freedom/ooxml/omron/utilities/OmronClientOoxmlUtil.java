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

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * 【OMRON】客先評価OOXML編集ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronClientOoxmlUtil {
	/**
	 * [1.派遣社員評価] シートの編集
	 * 
	 * @param nPage
	 *            ページ番号
	 * @param sheet
	 *            シート
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM1Sheet(int nPage, XSSFSheet sheet,
			String questionColumnName, Connection conn) throws SQLException {
		savePositionWholeForM1Sheet(sheet, nPage, conn, questionColumnName, 5,
				6, 10); // 拠点全体の出力
		saveGroupWholeForM1Sheet(sheet, nPage, conn, questionColumnName, 11, 14); // グループ別の出力
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"OC", 15, 17); // OC
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"OCG", 18, 34); // OCG
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"外部", 35, 35); // 外部
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "1", 36,
				49);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "2", 50,
				53);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "3", 54,
				58);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "4", 59,
				60);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "5", 61,
				70);
		int nLastRow = -1;
		if (10 == nPage) {
			saveQ1_9ForM1Sheet(sheet, nPage, conn, questionColumnName, 71); // 継続意向
			nLastRow = saveQ2_4ForM1Sheet(sheet, nPage, conn,
					questionColumnName, 73); // 総合評価
		} else if (11 == nPage) {
			saveQ1_8ForM1Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
			nLastRow = saveQ2_4ForM1Sheet(sheet, nPage, conn,
					questionColumnName, 74); // 総合評価
		} else {
			saveQ1_8ForM1Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
			saveQ1_9ForM1Sheet(sheet, nPage, conn, questionColumnName, 74); // 継続意向
			nLastRow = saveQ2_4ForM1Sheet(sheet, nPage, conn,
					questionColumnName, 76); // 総合評価
		}
		sheet.getWorkbook().setPrintArea(nPage, 0, 11 == nPage ? 7 : 9, 0,
				nLastRow); // 印刷範囲の設定
	}

	/**
	 * [1.派遣社員評価]シート　拠点全体の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            合計出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void savePositionWholeForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow, int startRow,
			int endRow) throws SQLException {
		Map<String, OmronChoiceDto> mapPosition = OmronClientDbUtil
				.getQ1PositionMap(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				mapPosition.entrySet());
		// 合計
		OmronChoiceDto totalInfo = mapPosition.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		if (11 != nPage) {
			OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
					/ (double) totalInfo.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
					/ (double) totalInfo.count1 * (double) 100.0, null);
		}
		// 拠点
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String keyId = entry.getKey();
			if ("total".equals(keyId)) {
				continue;
			}
			String key = OmronDbUtil.POSITION.get(Integer.parseInt(keyId));
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
				if (11 != nPage) {
					OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [1.派遣社員評価]シート　グループ別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveGroupWholeForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, true, null);
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
				if (11 != nPage) {
					OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [1.派遣社員評価]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualGroupForM1Sheet(XSSFSheet sheet,
			int nPage, Connection conn, String questionColumnName,
			String target, int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, false, target);
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
				if (11 != nPage) {
					OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [1.派遣社員評価]シート　支店別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param branchId
	 *            支店ID
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveBranchForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, String branchId,
			int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(branchId);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1BranchMap(
				conn, questionColumnName, branchId);
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
				if (11 != nPage) {
					OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [1.派遣社員評価]シート　Q1-8とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_8ForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_8MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
			} else if ("4".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
			} else {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
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
			if (11 != nPage) {
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
	}

	/**
	 * [1.派遣社員評価]シート　Q1-9とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_9ForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_9MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("1".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
			} else {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
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
			if (11 != nPage) {
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
	}

	/**
	 * [1.派遣社員評価]シート　Q2-4とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ2_4ForM1Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ2_4MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
			} else if ("4".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
			} else {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
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
			if (11 != nPage) {
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
		return nRow;
	}

	/**
	 * [1.派遣社員評価]シート　Q1FAの出力　（派遣先別）
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
			String positionId, Connection conn) throws SQLException {
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
			List<OmronEnqueteDto> list = OmronClientDbUtil
					.getQ1FaListForDispatch(conn, positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q1fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q1fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [1.派遣社員評価]シート　Q1FAの出力　（担当者別）
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM1faSheetForSales(int nSheet, XSSFSheet sheet,
			String positionId, Connection conn) throws SQLException {
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		// 担当者別の集計結果を出力
		List<String> salesmanList = OmronDbUtil.getSalesManList(positionId); // 担当者リスト
		for (String salesman : salesmanList) {
			List<OmronEnqueteDto> list = OmronClientDbUtil
					.getQ1FaListBySalesman(conn, positionId, salesman); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, salesman, style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q1fa)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 2, enqueteInfo.q1fa, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 3, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [2.弊社評価] シートの編集
	 * 
	 * @param nPage
	 *            ページ番号
	 * @param sheet
	 *            シート
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM2Sheet(int nPage, XSSFSheet sheet,
			String questionColumnName, Connection conn) throws SQLException {
		savePositionWholeForM1Sheet(sheet, nPage, conn, questionColumnName, 5,
				6, 10); // 拠点全体の出力
		saveGroupWholeForM1Sheet(sheet, nPage, conn, questionColumnName, 11, 14); // グループ別の出力
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"OC", 15, 17); // OC
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"OCG", 18, 34); // OCG
		saveIndividualGroupForM1Sheet(sheet, nPage, conn, questionColumnName,
				"外部", 35, 35); // 外部
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "1", 36,
				49);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "2", 50,
				53);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "3", 54,
				58);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "4", 59,
				60);
		saveBranchForM1Sheet(sheet, nPage, conn, questionColumnName, "5", 61,
				70);
		saveQ1_8ForM1Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
		saveQ1_9ForM1Sheet(sheet, nPage, conn, questionColumnName, 74); // 継続意向
		int nLastRow = -1;
		if (25 == nPage) {
			nLastRow = saveQ2_4ForM2Sheet(sheet, nPage, conn, 76); // 総合評価
		} else {
			nLastRow = saveQ2_4ForM1Sheet(sheet, nPage, conn,
					questionColumnName, 76); // 総合評価
		}
		sheet.getWorkbook().setPrintArea(nPage, 0, 11 == nPage ? 7 : 9, 0,
				nLastRow); // 印刷範囲の設定
	}

	/**
	 * [2.弊社評価]シート　Q2-4とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ2_4ForM2Sheet(XSSFSheet sheet, int nPage,
			Connection conn, int nRow) throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ2_4MapForM2Sheet(conn);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
			} else if ("4".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
			} else if (!"total".equals(entry.getKey())) {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
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
			if (11 != nPage) {
				OoxmlUtil.setData(sheet, nRow, 7, (double) info.count5
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
		return nRow;
	}

	/**
	 * [3.他の派遣会社の利用状況] シートの編集
	 * 
	 * @param nPage
	 *            ページ番号
	 * @param sheet
	 *            シート
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM345Sheet(int nPage, XSSFSheet sheet,
			String questionColumnName, Connection conn) throws SQLException {
		savePositionWholeForM3Sheet(sheet, nPage, conn, questionColumnName, 5,
				6, 10); // 拠点全体の出力
		saveGroupWholeForM3Sheet(sheet, nPage, conn, questionColumnName, 11, 14); // グループ別の出力
		saveIndividualGroupForM3Sheet(sheet, nPage, conn, questionColumnName,
				"OC", 15, 17); // OC
		saveIndividualGroupForM3Sheet(sheet, nPage, conn, questionColumnName,
				"OCG", 18, 34); // OCG
		saveIndividualGroupForM3Sheet(sheet, nPage, conn, questionColumnName,
				"外部", 35, 35); // 外部
		saveBranchForM3Sheet(sheet, nPage, conn, questionColumnName, "1", 36,
				49);
		saveBranchForM3Sheet(sheet, nPage, conn, questionColumnName, "2", 50,
				53);
		saveBranchForM3Sheet(sheet, nPage, conn, questionColumnName, "3", 54,
				58);
		saveBranchForM3Sheet(sheet, nPage, conn, questionColumnName, "4", 59,
				60);
		saveBranchForM3Sheet(sheet, nPage, conn, questionColumnName, "5", 61,
				70);
		saveQ1_8ForM3Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
		saveQ1_9ForM3Sheet(sheet, nPage, conn, questionColumnName, 74); // 継続意向
		int nLastRow = saveQ2_4ForM3Sheet(sheet, nPage, conn,
				questionColumnName, 76); // 総合評価
		sheet.getWorkbook().setPrintArea(nPage, 0, 11 == nPage ? 7 : 9, 0,
				nLastRow); // 印刷範囲の設定
	}

	/**
	 * Q3～Q5シート　拠点全体の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            合計出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void savePositionWholeForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow, int startRow,
			int endRow) throws SQLException {
		Map<String, OmronChoiceDto> mapPosition = OmronClientDbUtil
				.getQ1PositionMap(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				mapPosition.entrySet());
		// 合計
		OmronChoiceDto totalInfo = mapPosition.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		if (29 != nPage) {
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
		}
		// 拠点
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String keyId = entry.getKey();
			if ("total".equals(keyId)) {
				continue;
			}
			String key = OmronDbUtil.POSITION.get(Integer.parseInt(keyId));
			nRow = CommonOoxmlUtil.searchRow(sheet, 2, startRow, endRow, key); // 出力先行番号の探索
			if (!"total".equals(key)) {
				OmronChoiceDto info = entry.getValue();
				OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
				OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
						/ (double) info.count1 * (double) 100.0, null);
				if (29 != nPage) {
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
	}

	/**
	 * Q3～Q5シート　グループ別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveGroupWholeForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, true, null);
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
				if (29 != nPage) {
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
	}

	/**
	 * Q3～Q5シート　所属先別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualGroupForM3Sheet(XSSFSheet sheet,
			int nPage, Connection conn, String questionColumnName,
			String target, int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, false, target);
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
				if (29 != nPage) {
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
	}

	/**
	 * Q3～Q5シート　支店別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param branchId
	 *            支店ID
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveBranchForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, String branchId,
			int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(branchId);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1BranchMap(
				conn, questionColumnName, branchId);
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
				if (29 != nPage) {
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
	}

	/**
	 * Q3～Q5シート　Q1-8とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_8ForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_8MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
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
			} else if ("4".equals(entry.getKey())) {
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
			} else {
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
			if (29 != nPage) {
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
			++nRow;
		}
	}

	/**
	 * Q3～Q5シート　Q1-9とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_9ForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_9MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("1".equals(entry.getKey())) {
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
			} else {
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
			}
		}
		for (OmronChoiceDto info : list) {
			OoxmlUtil.setData(sheet, nRow, 3, info.count1, null);
			OoxmlUtil.setData(sheet, nRow, 4, (double) info.count2
					/ (double) info.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 5, (double) info.count3
					/ (double) info.count1 * (double) 100.0, null);
			if (29 != nPage) {
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
			++nRow;
		}
	}

	/**
	 * Q3～Q5シート　Q2-4とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ2_4ForM3Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ2_4MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
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
			} else if ("4".equals(entry.getKey())) {
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
			} else {
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
			if (29 != nPage) {
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
			++nRow;
		}
		return nRow;
	}

	/**
	 * [8.弊社へのご意見ご要望]シート　（派遣先別）
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM8SheetForDispatch(int nSheet, XSSFSheet sheet,
			String positionId, Connection conn) throws SQLException {
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
			List<OmronEnqueteDto> list = OmronClientDbUtil
					.getQ8ListForDispatch(conn, positionId, entry.getKey()); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q8)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, enqueteInfo.dept,
							style2);
					OoxmlUtil.setData(sheet, ++nRow, 2, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 3, enqueteInfo.q8, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [8.弊社へのご意見ご要望]シート　（担当者別）
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param positionId
	 *            所属ID
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM8SheetForSales(int nSheet, XSSFSheet sheet,
			String positionId, Connection conn) throws SQLException {
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;
		// 担当者別の集計結果を出力
		List<String> salesmanList = OmronDbUtil.getSalesManList(positionId); // 担当者リスト
		for (String salesman : salesmanList) {
			List<OmronEnqueteDto> list = OmronClientDbUtil.getQ8ListBySalesman(
					conn, positionId, salesman); // 回答結果
			OoxmlUtil.setData(sheet, nRow, 0, salesman, style1);
			for (OmronEnqueteDto enqueteInfo : list) {
				if (StringUtil.isNotEmpty(enqueteInfo.q8)) {
					OoxmlUtil.setData(sheet, ++nRow, 1, "・", style3);
					OoxmlUtil.setData(sheet, nRow, 2, enqueteInfo.q8, style3);
				}
			}
			++nRow;
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 3, 0, nRow); // 印刷範囲の設定
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点] シートの編集
	 * 
	 * @param nPage
	 *            ページ番号
	 * @param sheet
	 *            シート
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM6Sheet(int nPage, XSSFSheet sheet,
			String questionColumnName, Connection conn) throws SQLException {
		savePositionWholeForM6Sheet(sheet, nPage, conn, questionColumnName, 5,
				6, 10); // 拠点全体の出力
		saveGroupWholeForM6Sheet(sheet, nPage, conn, questionColumnName, 11, 14); // グループ別の出力
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"OC", 15, 17); // OC
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"OCG", 18, 34); // OCG
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"外部", 35, 35); // 外部
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "1", 36,
				49);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "2", 50,
				53);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "3", 54,
				58);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "4", 59,
				60);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "5", 61,
				70);
		saveQ1_8ForM6Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
		saveQ1_9ForM6Sheet(sheet, nPage, conn, questionColumnName, 74); // 継続意向
		int nLastRow = saveQ2_4ForM6Sheet(sheet, nPage, conn,
				questionColumnName, 76); // 総合評価
		sheet.getWorkbook().setPrintArea(nPage, 0, 11 == nPage ? 7 : 9, 0,
				nLastRow); // 印刷範囲の設定
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　拠点全体の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            合計出力先行番号
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void savePositionWholeForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow, int startRow,
			int endRow) throws SQLException {
		Map<String, OmronChoiceDto> mapPosition = OmronClientDbUtil
				.getQ1PositionMap(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				mapPosition.entrySet());
		// 合計
		OmronChoiceDto totalInfo = mapPosition.get("total");
		OoxmlUtil.setData(sheet, nRow, 3, totalInfo.count1, null);
		OoxmlUtil.setData(sheet, nRow, 4, (double) totalInfo.count2
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 5, (double) totalInfo.count3
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 6, (double) totalInfo.count4
				/ (double) totalInfo.count1 * (double) 100.0, null);
		OoxmlUtil.setData(sheet, nRow, 7, (double) totalInfo.count5
				/ (double) totalInfo.count1 * (double) 100.0, null);
		if (50 == nPage) {
			OoxmlUtil.setData(sheet, nRow, 8, (double) totalInfo.count6
					/ (double) totalInfo.count1 * (double) 100.0, null);
			OoxmlUtil.setData(sheet, nRow, 9, (double) totalInfo.count7
					/ (double) totalInfo.count1 * (double) 100.0, null);
		}
		// 拠点
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			String keyId = entry.getKey();
			if ("total".equals(keyId)) {
				continue;
			}
			String key = OmronDbUtil.POSITION.get(Integer.parseInt(keyId));
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
				if (50 == nPage) {
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　グループ別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveGroupWholeForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int startRow, int endRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, true, null);
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
				if (50 == nPage) {
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　派遣先別の出力
	 * 
	 * @param sheet
	 *            　シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param target
	 *            派遣先
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveIndividualGroupForM6Sheet(XSSFSheet sheet,
			int nPage, Connection conn, String questionColumnName,
			String target, int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(target);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1Map(conn,
				questionColumnName, false, target);
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
				if (50 == nPage) {
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　支店別の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param branchId
	 *            支店ID
	 * @param startRow
	 *            出力先開始行番号
	 * @param endRow
	 *            出力先終端行番号
	 * @throws SQLException
	 */
	private static void saveBranchForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, String branchId,
			int startRow, int endRow) throws SQLException {
		assert StringUtil.isNotEmpty(branchId);
		Map<String, OmronChoiceDto> map = OmronClientDbUtil.getQ1BranchMap(
				conn, questionColumnName, branchId);
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
				if (50 == nPage) {
					OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
							/ (double) info.count1 * (double) 100.0, null);
					OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
							/ (double) info.count1 * (double) 100.0, null);
				}
			}
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　Q1-8とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_8ForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_8MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
				list.get(0).count7 += info.count7;
			} else if ("4".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
				list.get(1).count7 += info.count7;
			} else {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
				list.get(2).count7 += info.count7;
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
			if (50 == nPage) {
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　Q1-9とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @throws SQLException
	 */
	private static void saveQ1_9ForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ1_9MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("1".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
				list.get(0).count7 += info.count7;
			} else {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
				list.get(1).count7 += info.count7;
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
			if (50 == nPage) {
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
	}

	/**
	 * [Q6.「派遣」に関しての相談事や疑問点]シート　Q2-4とのクロス集計結果の出力
	 * 
	 * @param sheet
	 *            シート
	 * @param nPage
	 *            ページ番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param nRow
	 *            出力先行番号
	 * @return 最終出力行番号
	 * @throws SQLException
	 */
	private static int saveQ2_4ForM6Sheet(XSSFSheet sheet, int nPage,
			Connection conn, String questionColumnName, int nRow)
			throws SQLException {
		Map<String, OmronChoiceDto> map = OmronClientDbUtil
				.getQ2_4MapForM1Sheet(conn, questionColumnName);
		List<Map.Entry<String, OmronChoiceDto>> entries = new ArrayList<Map.Entry<String, OmronChoiceDto>>(
				map.entrySet());
		// データ集計
		List<OmronChoiceDto> list = new ArrayList<OmronChoiceDto>();
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		list.add(new OmronChoiceDto());
		for (Map.Entry<String, OmronChoiceDto> entry : entries) {
			OmronChoiceDto info = entry.getValue();
			if ("5".equals(entry.getKey())) {
				list.get(0).count1 += info.count1;
				list.get(0).count2 += info.count2;
				list.get(0).count3 += info.count3;
				list.get(0).count4 += info.count4;
				list.get(0).count5 += info.count5;
				list.get(0).count6 += info.count6;
				list.get(0).count7 += info.count7;
			} else if ("4".equals(entry.getKey())) {
				list.get(1).count1 += info.count1;
				list.get(1).count2 += info.count2;
				list.get(1).count3 += info.count3;
				list.get(1).count4 += info.count4;
				list.get(1).count5 += info.count5;
				list.get(1).count6 += info.count6;
				list.get(1).count7 += info.count7;
			} else {
				list.get(2).count1 += info.count1;
				list.get(2).count2 += info.count2;
				list.get(2).count3 += info.count3;
				list.get(2).count4 += info.count4;
				list.get(2).count5 += info.count5;
				list.get(2).count6 += info.count6;
				list.get(2).count7 += info.count7;
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
			if (50 == nPage) {
				OoxmlUtil.setData(sheet, nRow, 8, (double) info.count6
						/ (double) info.count1 * (double) 100.0, null);
				OoxmlUtil.setData(sheet, nRow, 9, (double) info.count7
						/ (double) info.count1 * (double) 100.0, null);
			}
			++nRow;
		}
		return nRow;
	}

	/**
	 * [Q7.今後の外部人材活用計画について] シートの編集
	 * 
	 * @param nPage
	 *            ページ番号
	 * @param sheet
	 *            シート
	 * @param questionColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM7Sheet(int nPage, XSSFSheet sheet,
			String questionColumnName, Connection conn) throws SQLException {
		savePositionWholeForM6Sheet(sheet, nPage, conn, questionColumnName, 5,
				6, 10); // 拠点全体の出力
		saveGroupWholeForM6Sheet(sheet, nPage, conn, questionColumnName, 11, 14); // グループ別の出力
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"OC", 15, 17); // OC
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"OCG", 18, 34); // OCG
		saveIndividualGroupForM6Sheet(sheet, nPage, conn, questionColumnName,
				"外部", 35, 35); // 外部
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "1", 36,
				49);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "2", 50,
				53);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "3", 54,
				58);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "4", 59,
				60);
		saveBranchForM6Sheet(sheet, nPage, conn, questionColumnName, "5", 61,
				70);
		saveQ1_8ForM6Sheet(sheet, nPage, conn, questionColumnName, 71); // 満足総合
		saveQ1_9ForM6Sheet(sheet, nPage, conn, questionColumnName, 74); // 継続意向
		int nLastRow = saveQ2_4ForM6Sheet(sheet, nPage, conn,
				questionColumnName, 76); // 総合評価
		sheet.getWorkbook().setPrintArea(nPage, 0, 11 == nPage ? 7 : 9, 0,
				nLastRow); // 印刷範囲の設定
	}

	/**
	 * [Q7.今後の外部人材活用計画について]シート FA
	 * 
	 * @param nSheet
	 *            シート番号
	 * @param sheet
	 *            シート
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static void editM7FaSheet(int nSheet, XSSFSheet sheet,
			Connection conn) throws SQLException {
		XSSFCellStyle style1 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), true, false, 12);
		XSSFCellStyle style2 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 10);
		XSSFCellStyle style3 = OoxmlUtil.createDefaultCellStyle(
				sheet.getWorkbook(), false, false, 9);
		style3.setWrapText(true); // 折り返して全体表示
		int nRow = 2;

		List<Map.Entry<Integer, String>> entries = new ArrayList<Map.Entry<Integer, String>>(
				OmronDbUtil.DISPLAY_POSITION.entrySet());
		List<Map.Entry<String, String>> entriesGroup = new ArrayList<Map.Entry<String, String>>(
				OmronDbUtil.GROUP.entrySet());

		List<OmronEnqueteDto> list = OmronClientDbUtil.getQ7FaList(conn); // 回答結果
		// 所属別の集計結果を出力
		for (Map.Entry<Integer, String> entry : entries) {
			String position = String.valueOf(entry.getKey());
			OoxmlUtil.setData(sheet, nRow, 0, entry.getValue(), style1);
			for (Map.Entry<String, String> entryGroup : entriesGroup) {
				String group = entryGroup.getKey(); // 出力対象グループ
				// データ先読み
				boolean exist = false;
				for (OmronEnqueteDto enqueteInfo : list) {
					if (group.equals(enqueteInfo.deptGroup1)
							&& position.equals(enqueteInfo.position)
							&& StringUtil.isNotEmpty(enqueteInfo.q7_fa)) {
						exist = true;
					}
				}
				if (exist) {
					OoxmlUtil.setData(sheet, ++nRow, 1, entryGroup.getValue(),
							style1);
					for (OmronEnqueteDto enqueteInfo : list) {
						if (group.equals(enqueteInfo.deptGroup1)
								&& position.equals(enqueteInfo.position)
								&& StringUtil.isNotEmpty(enqueteInfo.q7_fa)) {
							OoxmlUtil.setData(sheet, ++nRow, 2,
									enqueteInfo.dept, style2);
							OoxmlUtil.setData(sheet, ++nRow, 3, "・", style3);
							OoxmlUtil.setData(sheet, nRow, 4,
									enqueteInfo.q7_fa, style3);
						}
					}
					++nRow;
				}
			}
		}
		sheet.getWorkbook().setPrintArea(nSheet, 0, 4, 0, nRow); // 印刷範囲の設定
	}
}