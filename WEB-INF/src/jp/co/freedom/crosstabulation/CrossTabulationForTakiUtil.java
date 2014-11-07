package jp.co.freedom.crosstabulation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.freedom.common.utilities.OoxmlUtil;
import jp.co.freedom.common.utilities.StringUtil;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 【たき工房】クロス集計ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class CrossTabulationForTakiUtil {

	/** 職種コード */
	private static Map<Integer, String> BIZ_MAP = createBizMap();

	/** 使用洗剤コード */
	public static Map<Integer, String> CLEANER_MAP = createCleanerMap();

	/** 年齢 */
	public static Map<Integer, String> AGE_MAP = createAgeMap();

	/**
	 * クロス集計結果をデータセルに保存
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param sheetId
	 *            シート番号
	 * @param map
	 *            クロス集計データ
	 * @param rowStartId
	 *            編集対象セルの開始行番号
	 * @param rowEndId
	 *            編集対象セルの終了行番号
	 * @param columnStartId
	 *            編集対象セルの開始列番号
	 * @param columnEndId
	 *            編集対象セルの終了列番号
	 * @param filterConditionType
	 *            絞込条件ID
	 * @param sexType
	 *            性別種別ID
	 */
	public static void saveCrossTabulationDataForCrossTabulation(
			XSSFSheet sheet, int sheetId,
			Map<CrossTabulationForTakiDto, Integer> map, int rowStartId,
			int rowEndId, int columnStartId, int columnEndId,
			int filterConditionType, int sexType) {
		// 編集対象セルを0で初期化(データ形式：数値型)
		for (int nRow = rowStartId + 1; nRow <= rowEndId + 1; nRow++) {
			OoxmlUtil.setZero(sheet, nRow, columnStartId + 1, columnEndId + 1);
		}
		Set<Entry<CrossTabulationForTakiDto, Integer>> entrySet = map
				.entrySet();
		Iterator<Entry<CrossTabulationForTakiDto, Integer>> iteratorInit = entrySet
				.iterator();
		if (!iteratorInit.hasNext()) {
			return; // 該当データが存在しない場合は処理を中止
		}
		Entry<CrossTabulationForTakiDto, Integer> entryInit = iteratorInit
				.next();
		// 出力列番号
		int nColumn = columnStartId;
		// 出力行番号
		int nRow = rowStartId
				+ CrossTabulationForTakiUtil.searchHeaderIndex(sheet,
						rowStartId, columnStartId, entryInit.getKey().AGE, 11);
		// 年齢の値が符合する行を探索して、その行のセルにクロス集計結果を出力していく
		for (Iterator<Entry<CrossTabulationForTakiDto, Integer>> iterator = entrySet
				.iterator(); iterator.hasNext();) {
			Entry<CrossTabulationForTakiDto, Integer> entry = iterator.next();
			CrossTabulationForTakiDto info = entry.getKey();
			if (nColumn < columnEndId) {
				nColumn++;
			} else {
				nColumn = columnStartId + 1;
				int correctPosition = CrossTabulationForTakiUtil
						.searchHeaderIndex(sheet, rowStartId, columnStartId,
								info.AGE, 11);
				nRow = rowStartId + correctPosition;
			}
			XSSFRow row = sheet.getRow(nRow);
			XSSFCell cell = row.getCell(nColumn);
			cell.setCellValue((double) entry.getValue()); // セルデータ出力
		}
		// 行方向の合計値をセルに格納
		for (nRow = rowStartId; nRow <= rowEndId; nRow++) {
			int sum = OoxmlUtil.sumRow(sheet, nRow, columnStartId + 1,
					columnEndId);
			XSSFRow row = sheet.getRow(nRow);
			XSSFCell cell = row.getCell(columnEndId + 1);
			cell.setCellValue(sum);
		}
		// 列方向の合計値をセルに格納
		for (nColumn = columnStartId + 1; nColumn <= columnEndId + 1; nColumn++) {
			int sum = OoxmlUtil.sumColumn(sheet, nColumn, rowStartId, rowEndId);
			XSSFRow row = sheet.getRow(rowEndId + 1);
			XSSFCell cell = row.getCell(nColumn);
			cell.setCellValue(sum);
		}
	}

	/**
	 * クロス集計の実行
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param addrId
	 *            都道府県ID
	 * @param sexType
	 *            性別ID
	 * @return クロス集計結果
	 * @throws SQLException
	 */
	public static Map<CrossTabulationForTakiDto, Integer> executeCrossTabulate(
			Connection conn, int filterConditionId, int addrId, int sexType)
			throws SQLException {
		Map<CrossTabulationForTakiDto, Integer> map = new LinkedHashMap<CrossTabulationForTakiDto, Integer>();
		String addrIdStr = String.valueOf(addrId); // 都道府県ID
		Statement stmt = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("select YEAR__OLD as age,");
		sb.append("count(case when PROF like '%小学生%' then 1 else null end) as biz1,");
		sb.append("count(case when PROF like '%中学生%' then 1 else null end) as biz2,");
		sb.append("count(case when PROF like '%高校生%' then 1 else null end) as biz3,");
		sb.append("count(case when PROF like '%短大・大学・専門学校生%' then 1 else null end) as biz4,");
		sb.append("count(case when PROF like '%会社員%' then 1 else null end) as biz5,");
		sb.append("count(case when PROF like '%自営業%' then 1 else null end) as biz6,");
		sb.append("count(case when PROF like '%自由業%' then 1 else null end) as biz7,");
		sb.append("count(case when PROF like '%専業主婦%' then 1 else null end) as biz8,");
		sb.append("count(case when PROF like '%パートタイマー%' then 1 else null end) as biz9,");
		sb.append("count(case when PROF like '%無職%' then 1 else null end) as biz10,");
		sb.append("count(case when PROF like '%その他%' then 1 else null end) as biz11");
		sb.append(" from taki1213 where ");
		List<String> conditions = new ArrayList<String>();
		// 都道府県による絞込条件
		if (addrId != 0) {
			conditions.add("Addr_memo='" + addrIdStr + "'");
		}
		// 性別による絞込条件
		if (2 == sexType) {
			conditions.add("SEX='女性'");
		} else if (3 == sexType) {
			conditions.add("SEX='男性'");
		}
		// 年齢による絞込条件
		if (1 == filterConditionId) {
			conditions
					.add("thoughts_2 is not null and Introducer_id is not null GROUP BY YEAR__OLD");
		} else if (2 == filterConditionId) {
			conditions.add("thoughts_2 is not null GROUP BY YEAR__OLD");
		} else if (3 == filterConditionId) {
			conditions.add("thoughts_2 is null GROUP BY YEAR__OLD");
		}
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" and ", conditions);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			for (int nBizId = 1; nBizId <= 11; nBizId++) {
				CrossTabulationForTakiDto info = new CrossTabulationForTakiDto();
				info.AGE = rs.getString("age");
				info.BIZ = BIZ_MAP.get(nBizId);
				int count = rs.getInt("biz" + String.valueOf(nBizId));
				map.put(info, count);
			}
		}
		rs.close();
		stmt.close();
		return map;
	}

	/**
	 * ランキングデータの使用洗剤内訳件数の集計実行
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param addrId
	 *            都道府県ID
	 * @param sexType
	 *            性別ID
	 * @param age
	 *            年齢
	 * @param biz
	 *            業種
	 * @return 使用洗剤内訳件数データ
	 * @throws SQLException
	 */
	public static Map<Integer, Integer> executeLankingDetailDataTabulate(
			Connection conn, int filterConditionId, int addrId, int sexType,
			String age, String biz) throws SQLException {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		/*
		 * [サンプルSQL] SELECT count(case when Q2 LIKE '%1.アタックNeo（ネオ）バイオEXパワー%'
		 * then 1 else null end) FROM taki1213 WHERE Addr_memo ='47' and
		 * YEAR__OLD = '30～34歳' and PROF like '%専業主婦%' and thoughts_2 is not
		 * null and Introducer_id is not null;
		 */
		String addrIdStr = String.valueOf(addrId);// 都道府県ID
		Statement stmt = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		for (int nIndex = 1; nIndex <= CLEANER_MAP.size(); nIndex++) {
			sb.append("count(case when Q2 like '%" + CLEANER_MAP.get(nIndex)
					+ "%' then 1 else null end) as count"
					+ String.valueOf(nIndex));
			if (nIndex != CLEANER_MAP.size()) {
				sb.append(",");
			}
		}
		sb.append(" from ");
		if (1 == filterConditionId) {
			sb.append("taki0110a");
		} else if (2 == filterConditionId) {
			sb.append("taki0110b");
		} else {
			sb.append("taki0110c");
		}
		sb.append(" where ");
		List<String> conditions = new ArrayList<String>();
		// 都道府県による絞込条件
		if (addrId != 0) {
			conditions.add("Addr_memo='" + addrIdStr + "'");
		}
		// 性別による絞込条件
		if (2 == sexType) {
			conditions.add("SEX='女性'");
		} else if (3 == sexType) {
			conditions.add("SEX='男性'");
		}
		// 年齢による絞込条件
		if (StringUtil.isNotEmpty(age)) {
			conditions.add("YEAR__OLD ='" + age + "'");
		}
		// 職種による絞込条件
		if (StringUtil.isNotEmpty(biz)) {
			conditions.add("PROF like '%" + biz + "%'");
		}
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" and ", conditions)
				+ " GROUP BY YEAR__OLD";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			for (int nCleaner = 1; nCleaner <= CLEANER_MAP.size(); nCleaner++) {
				map.put(nCleaner, rs.getInt("count" + String.valueOf(nCleaner)));
			}
		}
		return map;
	}

	/**
	 * 使用洗剤集計の実行
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param addrId
	 *            都道府県ID
	 * @param sexType
	 *            性別ID
	 * @return 集計結果
	 * @throws SQLException
	 */
	public static Map<String, Integer> executeCleanerTabulate(Connection conn,
			int filterConditionId, int addrId, int sexType) throws SQLException {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		String addrIdStr = String.valueOf(addrId); // 都道府県ID
		Statement stmt = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT Q2,count(*) as count FROM `taki1213` where ");
		List<String> conditions = new ArrayList<String>();
		// 都道府県による絞込条件
		if (addrId != 0) {
			conditions.add("Addr_memo='" + addrIdStr + "'");
		}
		// 性別による絞込条件
		if (2 == sexType) {
			conditions.add("SEX='女性'");
		} else if (3 == sexType) {
			conditions.add("SEX='男性'");
		}
		// 感想による絞込条件
		if (1 == filterConditionId) {
			conditions
					.add("thoughts_2 is not null and Introducer_id is not null GROUP BY Q2 ORDER BY CAST(Q2 AS SIGNED)");
		} else if (2 == filterConditionId) {
			conditions
					.add("thoughts_2 is not null GROUP BY Q2 ORDER BY CAST(Q2 AS SIGNED)");
		} else if (3 == filterConditionId) {
			conditions
					.add("thoughts_2 is null GROUP BY Q2 ORDER BY CAST(Q2 AS SIGNED)");
		}
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" and ", conditions);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String cleaner = rs.getString("Q2");
			int count = rs.getInt("count");
			map.put(cleaner, count);
		}
		rs.close();
		stmt.close();
		return map;
	}

	/**
	 * 使用洗剤別の年齢*業種クロス集計の実行
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param cleanerStr
	 *            使用洗剤名
	 * @param sexType
	 *            性別ID
	 * @return 集計結果
	 * @throws SQLException
	 */
	public static Map<CrossTabulationForTakiDto, Integer> executeCleanerCrossTabulate(
			Connection conn, int filterConditionId, String cleanerStr,
			int sexType) throws SQLException {
		Map<CrossTabulationForTakiDto, Integer> map = new LinkedHashMap<CrossTabulationForTakiDto, Integer>();
		Statement stmt = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("select PROF as PROF,");
		sb.append("count(case when YEAR__OLD like '%9歳以下%' then 1 else null end) as old1,");
		sb.append("count(case when YEAR__OLD like '%10～14歳%' then 1 else null end) as old2,");
		sb.append("count(case when YEAR__OLD like '%15～19歳%' then 1 else null end) as old3,");
		sb.append("count(case when YEAR__OLD like '%20～24歳%' then 1 else null end) as old4,");
		sb.append("count(case when YEAR__OLD like '%25～29歳%' then 1 else null end) as old5,");
		sb.append("count(case when YEAR__OLD like '%30～34歳%' then 1 else null end) as old6,");
		sb.append("count(case when YEAR__OLD like '%35～39歳%' then 1 else null end) as old7,");
		sb.append("count(case when YEAR__OLD like '%40～44歳%' then 1 else null end) as old8,");
		sb.append("count(case when YEAR__OLD like '%45～49歳%' then 1 else null end) as old9,");
		sb.append("count(case when YEAR__OLD like '%50～54歳%' then 1 else null end) as old10,");
		sb.append("count(case when YEAR__OLD like '%55～59歳%' then 1 else null end) as old11,");
		sb.append("count(case when YEAR__OLD like '%60歳以上%' then 1 else null end) as old12");
		sb.append(" from ");
		if (1 == filterConditionId) {
			sb.append("taki0110a");
		} else if (2 == filterConditionId) {
			sb.append("taki0110b");
		} else {
			sb.append("taki0110c");
		}
		List<String> conditions = new ArrayList<String>();
		if (cleanerStr != null || sexType == 2 || sexType == 3) {
			sb.append(" where ");
		}
		// 使用洗剤による絞込条件
		if (cleanerStr != null) {
			conditions.add("Q2='" + cleanerStr + "'");
		}
		// 性別による絞込条件
		if (2 == sexType) {
			conditions.add("SEX='女性'");
		} else if (3 == sexType) {
			conditions.add("SEX='男性'");
		}
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" and ", conditions)
				+ " GROUP BY PROF";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			for (int nOldId = 1; nOldId <= 12; nOldId++) {
				CrossTabulationForTakiDto info = new CrossTabulationForTakiDto();
				info.AGE = AGE_MAP.get(nOldId);
				info.BIZ = rs.getString("PROF");
				int count = rs.getInt("old" + String.valueOf(nOldId));
				map.put(info, count);
			}
		}
		rs.close();
		stmt.close();
		return map;
	}

	/**
	 * 使用洗剤別の年齢*都道府県クロス集計の実行
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param cleanerId
	 *            使用洗剤ID
	 * @param sexType
	 *            性別ID
	 * @return 集計結果
	 * @throws SQLException
	 */
	public static Map<CrossTabulationForTakiDto, Integer> executeCleanerAddrCrossTabulate(
			Connection conn, int filterConditionId, String sheetName,
			int sexType) throws SQLException {
		Map<CrossTabulationForTakiDto, Integer> map = new LinkedHashMap<CrossTabulationForTakiDto, Integer>();
		String cleanerStr = String.valueOf(sheetName); // 使用洗剤
		Statement stmt = conn.createStatement();
		StringBuffer sb = new StringBuffer();
		sb.append("select ADDR1 as ADDR1,");
		sb.append("count(case when YEAR__OLD like '%9歳以下%' then 1 else null end) as old1,");
		sb.append("count(case when YEAR__OLD like '%10～14歳%' then 1 else null end) as old2,");
		sb.append("count(case when YEAR__OLD like '%15～19歳%' then 1 else null end) as old3,");
		sb.append("count(case when YEAR__OLD like '%20～24歳%' then 1 else null end) as old4,");
		sb.append("count(case when YEAR__OLD like '%25～29歳%' then 1 else null end) as old5,");
		sb.append("count(case when YEAR__OLD like '%30～34歳%' then 1 else null end) as old6,");
		sb.append("count(case when YEAR__OLD like '%35～39歳%' then 1 else null end) as old7,");
		sb.append("count(case when YEAR__OLD like '%40～44歳%' then 1 else null end) as old8,");
		sb.append("count(case when YEAR__OLD like '%45～49歳%' then 1 else null end) as old9,");
		sb.append("count(case when YEAR__OLD like '%50～54歳%' then 1 else null end) as old10,");
		sb.append("count(case when YEAR__OLD like '%55～59歳%' then 1 else null end) as old11,");
		sb.append("count(case when YEAR__OLD like '%60歳以上%' then 1 else null end) as old12");
		sb.append(" from taki1213 where ");
		List<String> conditions = new ArrayList<String>();
		// 使用洗剤による絞込条件
		if (cleanerStr != null) {
			conditions.add("Q2='" + cleanerStr + "'");
		}
		// 性別による絞込条件
		if (2 == sexType) {
			conditions.add("SEX='女性'");
		} else if (3 == sexType) {
			conditions.add("SEX='男性'");
		}
		// 年齢による絞込条件
		if (1 == filterConditionId) {
			conditions
					.add("thoughts_2 is not null and Introducer_id is not null GROUP BY ADDR1");
		} else if (2 == filterConditionId) {
			conditions.add("thoughts_2 is not null GROUP BY ADDR1");
		} else if (3 == filterConditionId) {
			conditions.add("thoughts_2 is null GROUP BY ADDR1");
		}
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" and ", conditions);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			for (int nOldId = 1; nOldId <= 12; nOldId++) {
				CrossTabulationForTakiDto info = new CrossTabulationForTakiDto();
				info.AGE = AGE_MAP.get(nOldId);
				info.ADDR1 = rs.getString("ADDR1");
				int count = rs.getInt("old" + String.valueOf(nOldId));
				map.put(info, count);
			}
		}
		rs.close();
		stmt.close();
		return map;
	}

	/**
	 * 使用洗剤別の年齢*業種クロス集計をデータセルに保存
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param sheetId
	 *            シート番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param rowStartId
	 *            編集対象セルの開始行番号
	 * @param rowEndId
	 *            編集対象セルの終了行番号
	 * @param columnStartId
	 *            編集対象セルの開始列番号
	 * @param columnEndId
	 *            編集対象セルの終了列番号
	 * @param filterConditionType
	 *            絞込条件ID
	 * @param sexType
	 *            性別種別ID
	 */
	public static void saveCleanerCrossTabulationDataForCrossTabulation(
			XSSFSheet sheet, int sheetId,
			Map<CrossTabulationForTakiDto, Integer> map, int rowStartId,
			int rowEndId, int columnStartId, int columnEndId,
			int filterConditionType, int sexType) {
		// 編集対象セルを0で初期化(データ形式：数値型)
		for (int nRow = rowStartId; nRow <= rowEndId + 1; nRow++) {
			OoxmlUtil.setZero(sheet, nRow, columnStartId + 1, columnEndId + 1);
		}
		Set<Entry<CrossTabulationForTakiDto, Integer>> entrySet = map
				.entrySet();
		Iterator<Entry<CrossTabulationForTakiDto, Integer>> iteratorInit = entrySet
				.iterator();
		if (!iteratorInit.hasNext()) {
			return; // 該当データが存在しない場合は処理を中止
		}
		Entry<CrossTabulationForTakiDto, Integer> entryInit = iteratorInit
				.next();
		// 出力列番号
		int nColumn = columnStartId;
		// 出力行番号
		int nRow = rowStartId
				+ CrossTabulationForTakiUtil.searchHeaderIndex(sheet,
						rowStartId, columnStartId, entryInit.getKey().BIZ, 11);
		// 業種の値が符合する行を探索して、その行のセルにクロス集計結果を出力していく
		for (Iterator<Entry<CrossTabulationForTakiDto, Integer>> iterator = entrySet
				.iterator(); iterator.hasNext();) {
			Entry<CrossTabulationForTakiDto, Integer> entry = iterator.next();
			CrossTabulationForTakiDto info = entry.getKey();
			if (nColumn < columnEndId) {
				nColumn++;
			} else {
				nColumn = columnStartId + 1;
				int correctPosition = CrossTabulationForTakiUtil
						.searchHeaderIndex(sheet, rowStartId, columnStartId,
								info.BIZ, 11);
				nRow = rowStartId + correctPosition;
			}
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, nRow);
			XSSFCell cell = OoxmlUtil.getCellAnyway(row, nColumn);
			cell.setCellValue((double) entry.getValue()); // セルデータ出力
		}
		// 行方向の合計値をセルに格納
		for (nRow = rowStartId; nRow <= rowEndId; nRow++) {
			int sum = OoxmlUtil.sumRow(sheet, nRow, columnStartId + 1,
					columnEndId);
			XSSFRow row = sheet.getRow(nRow);
			XSSFCell cell = row.getCell(columnEndId + 1);
			cell.setCellValue(sum);
		}
		// 列方向の合計値をセルに格納
		for (nColumn = columnStartId + 1; nColumn <= columnEndId + 1; nColumn++) {
			int sum = OoxmlUtil.sumColumn(sheet, nColumn, rowStartId, rowEndId);
			XSSFRow row = sheet.getRow(rowEndId + 1);
			XSSFCell cell = row.getCell(nColumn);
			cell.setCellValue(sum);
		}
	}

	/**
	 * 使用洗剤別の年齢*都道府県クロス集計をデータセルに保存
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param sheetId
	 *            シート番号
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param rowStartId
	 *            編集対象セルの開始行番号
	 * @param rowEndId
	 *            編集対象セルの終了行番号
	 * @param columnStartId
	 *            編集対象セルの開始列番号
	 * @param columnEndId
	 *            編集対象セルの終了列番号
	 * @param filterConditionType
	 *            絞込条件ID
	 * @param sexType
	 *            性別種別ID
	 */
	public static void saveCleanerAddrCrossTabulationDataForCrossTabulation(
			XSSFSheet sheet, int sheetId,
			Map<CrossTabulationForTakiDto, Integer> map, int rowStartId,
			int rowEndId, int columnStartId, int columnEndId,
			int filterConditionType, int sexType) {
		// 編集対象セルを0で初期化(データ形式：数値型)
		for (int nRow = rowStartId; nRow <= rowEndId + 1; nRow++) {
			OoxmlUtil.setZero(sheet, nRow, columnStartId + 1, columnEndId + 1);
		}
		Set<Entry<CrossTabulationForTakiDto, Integer>> entrySet = map
				.entrySet();
		Iterator<Entry<CrossTabulationForTakiDto, Integer>> iteratorInit = entrySet
				.iterator();
		if (!iteratorInit.hasNext()) {
			return; // 該当データが存在しない場合は処理を中止
		}
		Entry<CrossTabulationForTakiDto, Integer> entryInit = iteratorInit
				.next();
		// 出力列番号
		int nColumn = columnStartId;
		// 出力行番号
		int nRow = rowStartId
				+ CrossTabulationForTakiUtil
						.searchHeaderIndex(sheet, rowStartId, columnStartId,
								entryInit.getKey().ADDR1, 47);
		// 業種の値が符合する行を探索して、その行のセルにクロス集計結果を出力していく
		for (Iterator<Entry<CrossTabulationForTakiDto, Integer>> iterator = entrySet
				.iterator(); iterator.hasNext();) {
			Entry<CrossTabulationForTakiDto, Integer> entry = iterator.next();
			CrossTabulationForTakiDto info = entry.getKey();
			if (nColumn < columnEndId) {
				nColumn++;
			} else {
				nColumn = columnStartId + 1;
				int correctPosition = CrossTabulationForTakiUtil
						.searchHeaderIndex(sheet, rowStartId, columnStartId,
								info.ADDR1, 47);
				nRow = rowStartId + correctPosition;
			}
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, nRow);
			XSSFCell cell = OoxmlUtil.getCellAnyway(row, nColumn);
			int value = entry.getValue();
			System.out.println("value=" + value);
			cell.setCellValue((double) entry.getValue()); // セルデータ出力
		}
		// 行方向の合計値をセルに格納
		for (nRow = rowStartId; nRow <= rowEndId; nRow++) {
			int sum = OoxmlUtil.sumRow(sheet, nRow, columnStartId + 1,
					columnEndId);
			XSSFRow row = sheet.getRow(nRow);
			XSSFCell cell = row.getCell(columnEndId + 1);
			cell.setCellValue(sum);
		}
		// 列方向の合計値をセルに格納
		for (nColumn = columnStartId + 1; nColumn <= columnEndId + 1; nColumn++) {
			int sum = OoxmlUtil.sumColumn(sheet, nColumn, rowStartId, rowEndId);
			XSSFRow row = sheet.getRow(rowEndId + 1);
			XSSFCell cell = row.getCell(nColumn);
			cell.setCellValue(sum);
		}
	}

	/**
	 * ランキング<b>List</b>の作成
	 * 
	 * @param map
	 *            クロス集計結果
	 * @return　ランキング<b>List</b>
	 */
	public static List<LankingForTakiDto> createLanking(
			Map<CrossTabulationForTakiDto, Integer> map) {
		List<LankingForTakiDto> ranking = new ArrayList<LankingForTakiDto>();
		List<Map.Entry<CrossTabulationForTakiDto, Integer>> mapValuesList = new ArrayList<Map.Entry<CrossTabulationForTakiDto, Integer>>(
				map.entrySet());
		// 降順ソート
		Collections
				.sort(mapValuesList,
						new Comparator<Map.Entry<CrossTabulationForTakiDto, Integer>>() {
							@Override
							public int compare(
									Entry<CrossTabulationForTakiDto, Integer> entry1,
									Entry<CrossTabulationForTakiDto, Integer> entry2) {
								return ((Integer) entry2.getValue())
										.compareTo((Integer) entry1.getValue());
							}
						});
		int currentMaxCount = Integer.MAX_VALUE; // 現在の最大カウント数
		for (Entry<CrossTabulationForTakiDto, Integer> entry : mapValuesList) {
			CrossTabulationForTakiDto info = entry.getKey();
			int count = entry.getValue(); // 現在のカウンタ件数
			// 一つ低い順位もしくは同位の順位のデータを発見
			if (count <= currentMaxCount) {
				currentMaxCount = count;
				int currentRank = calculateRank(ranking, count);// 順位
				ranking.add(new LankingForTakiDto(currentRank, info.AGE,
						info.BIZ, entry.getValue()));
				// ここでList<Integer>をコンストラクタに渡してNewする
			}
		}
		return ranking;
	}

	/**
	 * 指定したカウンタ件数に対する順位を算出する
	 * 
	 * @param rankingList
	 *            　ランキングデータ
	 * @param count
	 *            調査対象のカウンタ値
	 * @return　調査対象のカウンタ値に対する順位
	 */
	private static int calculateRank(List<LankingForTakiDto> rankingList,
			int count) {
		assert rankingList != null;
		int rank = 1;
		for (LankingForTakiDto info : rankingList) {
			if (info.count > count) {
				rank++;
			} else if (info.count == count) {
				rank = info.rank;
			} else if (info.count < count) {
				break;
			}
		}
		return rank;
	}

	/**
	 * ランキング表のヘッダーを出力
	 * 
	 * @param sheet
	 *            シート
	 * @param startColumn
	 *            出力開始列番号
	 * @param startRow
	 *            出力開始行番号
	 * @param style
	 *            ランキングデータのセルスタイル
	 * @param detailStyle
	 *            使用洗剤内訳データのセルスタイル
	 */
	public static void outputLankingHeader(XSSFSheet sheet, int startColumn,
			int startRow, XSSFCellStyle style, XSSFCellStyle detailStyle) {
		List<Map.Entry<Integer, String>> entries = new ArrayList<Map.Entry<Integer, String>>(
				CLEANER_MAP.entrySet());
		OoxmlUtil.setData(sheet, startRow, startColumn, "順位", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 1, "年齢", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 2, "業種", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 3, "件数", style);
		int nColumn = startColumn + 3;
		for (Map.Entry<Integer, String> entry : entries) {
			OoxmlUtil.setData(sheet, startRow, ++nColumn, entry.getValue(),
					detailStyle);
		}
	}

	/**
	 * ランキング表のヘッダーを出力
	 * 
	 * @param sheet
	 *            シート
	 * @param startColumn
	 *            出力開始列番号
	 * @param startRow
	 *            出力開始行番号
	 * @param style
	 *            ランキングデータのセルスタイル
	 */
	public static void outputLankingHeader(XSSFSheet sheet, int startColumn,
			int startRow, XSSFCellStyle style) {
		OoxmlUtil.setData(sheet, startRow, startColumn, "順位", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 1, "年齢", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 2, "業種", style);
		OoxmlUtil.setData(sheet, startRow, startColumn + 3, "件数", style);
	}

	/**
	 * ランキングデータをデータセルに保存
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param filterConditionId
	 *            絞込条件ID
	 * @param addrId
	 *            都道府県ID
	 * @param sexType
	 *            性別種別
	 * @param sheet
	 *            シート
	 * @param ranking
	 *            ランキングデータ
	 * @param startColumn
	 *            出力開始列番号
	 * @param startRow
	 *            出力開始行番号
	 * @param maxRank
	 *            出力最大ランク
	 * @param showDetail
	 *            使用洗剤内訳件数を出力するか否かのブール値
	 * @return 出力最終行番号
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	public static int saveLankingData(Connection conn, int filterConditionId,
			int addrId, int sexType, XSSFSheet sheet,
			List<LankingForTakiDto> ranking, int startColumn, int startRow,
			int maxRank, boolean showDetail) throws NumberFormatException,
			SQLException {
		int nRow = startRow;
		for (LankingForTakiDto rankInfo : ranking) {
			if (rankInfo.rank > maxRank || rankInfo.count == 0) {
				return nRow;
			}
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, nRow);
			// ランキング表の出力
			saveLankingRowData(sheet.getWorkbook(), row, rankInfo, startColumn);
			// 使用洗剤の内訳件数の出力
			Map<Integer, Integer> details = executeLankingDetailDataTabulate(
					conn, filterConditionId, addrId, sexType, rankInfo.age,
					rankInfo.biz);
			if (showDetail) {
				saveDetailRowData(sheet.getWorkbook(), row, details,
						startColumn + 4);
			}
			nRow++;
		}
		return nRow;
	}

	/**
	 * 使用洗剤集計データをデータセルに保存
	 * 
	 * @param sheet
	 *            シート
	 * @param map
	 *            使用洗剤集計データ
	 * @param startColumn
	 *            出力開始列番号
	 * @param startRow
	 *            出力開始行番号
	 * @param maxRank
	 *            出力最大ランク
	 * @return 出力最終行番号
	 */
	public static void saveCleanerData(XSSFSheet sheet,
			Map<String, Integer> map, int startColumn, int startRow) {
		int nRow = startRow;
		List<Map.Entry<String, Integer>> mapValuesList = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		// 降順ソート
		Collections.sort(mapValuesList,
				new Comparator<Map.Entry<String, Integer>>() {
					@Override
					public int compare(Entry<String, Integer> entry1,
							Entry<String, Integer> entry2) {
						return ((Integer) entry2.getValue())
								.compareTo((Integer) entry1.getValue());
					}
				});
		// データ出力
		for (Entry<String, Integer> entry : mapValuesList) {
			String cleaner = entry.getKey();
			int count = entry.getValue();
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, nRow);
			saveCleanerRowData(sheet.getWorkbook(), row, cleaner, count,
					startColumn);
			nRow++;
		}

		int sum = OoxmlUtil.sumColumn(sheet, startColumn + 3, startRow,
				nRow - 1);
		OoxmlUtil.setData(sheet, nRow, startColumn, "合計", null);
		OoxmlUtil.setData(sheet, nRow, startColumn + 3, sum, null);

		// 罫線スタイルの設定
		XSSFCellStyle dataStyle = OoxmlUtil.createTableDataCellStyle(sheet
				.getWorkbook());
		for (int nIndex = startRow; nIndex < nRow + 1; nIndex++) {
			OoxmlUtil.setStyle(sheet, nIndex, nIndex, startColumn,
					startColumn + 2, dataStyle);
			sheet.addMergedRegion(new CellRangeAddress(nIndex, nIndex,
					startColumn, startColumn + 2));
		}
		OoxmlUtil.setStyle(sheet, nRow, nRow, startColumn + 3, startColumn + 3,
				dataStyle); // 合計値セルに対する設定
	}

	/**
	 * ランキングデータの行単位の出力
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param row
	 *            　<b>Row</b>
	 * @param rankInfo
	 *            出力対象データ
	 * @param startColumn
	 *            出力開始列番号
	 */
	private static void saveLankingRowData(XSSFWorkbook workbook, XSSFRow row,
			LankingForTakiDto rankInfo, int startColumn) {
		System.out.println(StringUtil.concatWithDelimit(",",
				String.valueOf(rankInfo.rank), rankInfo.age, rankInfo.biz,
				String.valueOf(rankInfo.count)));
		// 罫線スタイル
		XSSFCellStyle style = OoxmlUtil.createTableDataCellStyle(workbook);
		// 順位の出力
		XSSFCell rankCell = row.createCell(startColumn);
		rankCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		rankCell.setCellValue(rankInfo.rank);
		rankCell.setCellStyle(style);
		// 年齢の出力
		XSSFCell ageCell = row.createCell(startColumn + 1);
		ageCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		ageCell.setCellValue(rankInfo.age);
		ageCell.setCellStyle(style);
		// 業種の出力
		XSSFCell bizCell = row.createCell(startColumn + 2);
		bizCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		bizCell.setCellValue(rankInfo.biz);
		bizCell.setCellStyle(style);
		// 件数の出力
		XSSFCell countCell = row.createCell(startColumn + 3);
		countCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		countCell.setCellValue(rankInfo.count);
		countCell.setCellStyle(style);
	}

	/**
	 * 使用洗剤内訳データの行単位の出力
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param row
	 *            <b>Row</b>
	 * @param details
	 *            出力対象内訳データ
	 * @param startColumn
	 *            出力開始列番号
	 */
	private static void saveDetailRowData(XSSFWorkbook workbook, XSSFRow row,
			Map<Integer, Integer> details, int startColumn) {
		// 罫線スタイル
		XSSFCellStyle style = OoxmlUtil.createTableDataCellStyle(workbook);
		for (int nIndex = 1; nIndex <= details.size(); nIndex++) {
			XSSFCell countCell = row.createCell(startColumn + nIndex - 1);
			countCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			countCell.setCellValue(details.get(nIndex));
			countCell.setCellStyle(style);
		}
	}

	/**
	 * 使用洗剤集計データの行単位の出力
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param row
	 *            　<b>Row</b>
	 * @param rankInfo
	 *            出力対象データ
	 * @param startColumn
	 *            出力開始列番号
	 */
	private static void saveCleanerRowData(XSSFWorkbook workbook, XSSFRow row,
			String cleaner, int count, int startColumn) {
		System.out.println(StringUtil.concatWithDelimit(",", cleaner,
				String.valueOf(count)));
		// 罫線スタイル
		XSSFCellStyle style = OoxmlUtil.createTableDataCellStyle(workbook);
		// 使用洗剤名の出力
		XSSFCell nameCell = row.createCell(startColumn);
		nameCell.setCellType(XSSFCell.CELL_TYPE_STRING);
		nameCell.setCellValue(cleaner);
		nameCell.setCellStyle(style);
		// 件数の出力
		XSSFCell ageCell = row.createCell(startColumn + 3);
		ageCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		ageCell.setCellValue(count);
		ageCell.setCellStyle(style);
	}

	/**
	 * 職種コード<b>Map</b>の初期化
	 * 
	 * @return 職種コード<b>Map</b>
	 */
	private static Map<Integer, String> createBizMap() {
		Map<Integer, String> biz = new LinkedHashMap<Integer, String>();
		biz.put(1, "小学生");
		biz.put(2, "中学生");
		biz.put(3, "高校生");
		biz.put(4, "短大・大学・専門学校生");
		biz.put(5, "会社員");
		biz.put(6, "自営業");
		biz.put(7, "自由業");
		biz.put(8, "専業主婦");
		biz.put(9, "パートタイマー");
		biz.put(10, "無職");
		biz.put(11, "その他");
		return biz;
	}

	/**
	 * 使用洗剤コード<b>Map</b>の初期化
	 * 
	 * @return 使用洗剤コード
	 * */
	private static Map<Integer, String> createCleanerMap() {
		Map<Integer, String> cleaner = new LinkedHashMap<Integer, String>();
		cleaner.put(1, "1.アタックNeo（ネオ）バイオEXパワー");
		cleaner.put(2, "2.アタックNeo（ネオ）抗菌EXパワー");
		cleaner.put(3, "3.アタック高活性バイオEXジェル");
		cleaner.put(4, "4.アリエールイオンパワージェル抗菌フレッシュ");
		cleaner.put(5, "5.アリエールREVO（レボ）");
		cleaner.put(6, "6.トップクリアリキッド");
		cleaner.put(7, "7.液体部屋干しトップ");
		cleaner.put(8, "8.トップNANOX(ナノックス）");
		cleaner.put(9, "9.香りつづくトップ(レギュラータイプ)");
		cleaner.put(10, "10.香りつづくトップplus(超コンパクトタイプ)");
		cleaner.put(11, "11.トップ HYGIA(ハイジア）");
		cleaner.put(12, "12.フレグランスニュービーズジェル");
		cleaner.put(13, "13.フレグランスニュービーズNeo（ネオ）");
		cleaner.put(14, "14.ボールドはじけて香るジェル");
		cleaner.put(15, "15.ボールドコンパクトダウニーエイプリルフレッシュの香り");
		cleaner.put(16, "16.さらさ");
		cleaner.put(17, "17.エマール");
		cleaner.put(18, "18.アクロン");
		cleaner.put(19, "19.ボールド香りのおしゃれ着洗剤");
		cleaner.put(20, "20.アタック高活性バイオEX");
		cleaner.put(21, "21.アタック高浸透リセットパワー");
		cleaner.put(22, "22.フレグランスニュービーズ");
		cleaner.put(23, "23.アリエールスピーディー");
		cleaner.put(24, "24.アリエール頑固汚れ用");
		cleaner.put(25, "25.トッププラチナクリア");
		cleaner.put(26, "26.部屋干しトップ");
		cleaner.put(27, "27.ボールドはじけて香る粉末");
		cleaner.put(28, "28.消臭ブルーダイヤ");
		cleaner.put(29, "29.その他");
		return cleaner;
	}

	/**
	 * 年齢<b>Map</b>の初期化
	 * 
	 * @return 年齢
	 * */
	private static Map<Integer, String> createAgeMap() {
		Map<Integer, String> age = new LinkedHashMap<Integer, String>();
		age.put(1, "9歳以下");
		age.put(2, "10～14歳");
		age.put(3, "15～19歳");
		age.put(4, "20～24歳");
		age.put(5, "25～29歳");
		age.put(6, "30～34歳");
		age.put(7, "35～39歳");
		age.put(8, "40～44歳");
		age.put(9, "45～49歳");
		age.put(10, "50～54歳");
		age.put(11, "55～59歳");
		age.put(12, "60歳以上");
		return age;
	}

	/**
	 * ヘッダー行の中から指定ラベルの行番号を探索する
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param nStartRow
	 *            探索開始行番号
	 * @param nColumn
	 *            探索対象列番号
	 * @param target
	 *            探索対象ラベル
	 * @param max
	 *            インデックスの最大値
	 * @return 該当
	 */
	private static int searchHeaderIndex(XSSFSheet sheet, int nStartRow,
			int nColumn, String target, int max) {
		assert StringUtil.isNotEmpty(target);
		for (int nIndex = 0; nIndex <= max; nIndex++) {
			int current = nIndex + nStartRow;
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, current);
			XSSFCell cell = OoxmlUtil.getCellAnyway(row, nColumn);
			String index = cell.getStringCellValue();
			assert StringUtil.isNotEmpty(index);
			if (index.equals(target)) {
				return nIndex;
			}
		}
		return -1;
	}

	/**
	 * 最大値を取得
	 * 
	 * @param num1
	 *            数値1
	 * @param num2
	 *            数値2
	 * @param num3
	 *            数値3
	 * @return
	 */
	public static int max(int num1, int num2, int num3) {
		int max = num1;
		if (num2 > max) {
			max = num2;
		}
		if (num3 > max) {
			max = num3;
		}
		return max;
	}
}