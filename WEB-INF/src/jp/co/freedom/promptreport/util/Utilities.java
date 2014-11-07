package jp.co.freedom.promptreport.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.promptreport.dto.BarcodeDto;
import jp.co.freedom.promptreport.gp.GeneralPurposePromptReportConfig;

import org.w3c.dom.Element;

public class Utilities {
	// 入力データ関連のプロパティ名
	/** プロパティ：CSVデータのセパレイト文字 */
	private static final String PROPERTY_INPUT_SEPARATE_MARK = "input-separate-mark";
	/** プロパティ：CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値 */
	private static final String PROPERTY_ENQUOTE_BY_DOUBLE_QUOTATION = "enquote-by-double-quotation";
	/** 最初の行がヘッダー行であるか否かのブール値 */
	private static final String PROPERTY_REMOVE_HEADER_RECORD = "remove-header-record";
	/** バーコードデータのタイムスタンプにおける日時情報の開始インデックス（例. 20131106の場合は7を指定） */
	private static final String PROPERTY_STARTINDEX_DAY_IN_TIMESTAMP = "start-index-day-in-timestamp";
	/** バーコードデータのタイムスタンプにおける日時情報の終了インデックス（例. 20131106の場合は8を指定） */
	private static final String PROPERTY_ENDINDEX_DAY_IN_TIMESTAMP = "end-index-day-in-timestamp";

	/**
	 * バーコードデータのインスタンス化
	 *
	 * @param allData
	 *            バーコードデータ
	 * @param removeFirstChar
	 *            最初の文字の除去フラグ
	 * @return <b>BarcodeDto</b>の<b>List</b>
	 */
	public static List<BarcodeDto> createInstance(List<String[]> allData,
			boolean removeFirstChar) {
		List<BarcodeDto> barcodes = new ArrayList<BarcodeDto>();
		for (String[] data : allData) {
			if (data.length > 1 && data[1].length() > 1) {
				BarcodeDto barcodeInfo = new BarcodeDto();
				barcodeInfo.id = removeFirstChar ? data[1].substring(1)
						: data[1]; // バーコード番号
				assert barcodeInfo.id.length() == 10;
				barcodeInfo.time = data[2]; // 来場日時
				barcodes.add(barcodeInfo);
			}
		}
		return barcodes;
	}

	/**
	 * 【BWJ専用】バーコードデータのインスタンス化
	 *
	 * @param allData
	 *            バーコードデータ
	 * @return <b>BarcodeDto</b>の<b>List</b>
	 */
	public static List<BarcodeDto> createBwjInstance(List<String[]> allData) {
		List<BarcodeDto> barcodes = new ArrayList<BarcodeDto>();
		for (String[] data : allData) {
			if (data.length > 1 && data[1].length() > 1) {
				BarcodeDto barcodeInfo = new BarcodeDto();
				barcodeInfo.time = data[1]; // 来場日時
				barcodeInfo.id = data[2]; // バーコード番号
				barcodes.add(barcodeInfo);
			}
		}
		return barcodes;
	}

	/**
	 * 【ILT専用】バーコードデータのインスタンス化
	 *
	 * @param allData
	 *            バーコードデータ
	 * @return <b>BarcodeDto</b>の<b>List</b>
	 */
	public static List<BarcodeDto> createIltInstance(List<String[]> allData) {
		List<BarcodeDto> barcodes = new ArrayList<BarcodeDto>();
		for (String[] data : allData) {
			if (data.length > 1 && data[1].length() > 1) {
				BarcodeDto barcodeInfo = new BarcodeDto();
				barcodeInfo.time = data[1]; // 来場日時
				// バーコード番号
				String barcode = data[0];
				if (barcode.length() == 7) {
					barcodeInfo.id = barcode.substring(1);
					barcodes.add(barcodeInfo);
				} else if (barcode.length() == 10 && barcode.startsWith("3377")) {
					barcodeInfo.id = barcode;
					barcodes.add(barcodeInfo);
				} else if (barcode.length() == 10 && barcode.startsWith("0000")) {
					barcodeInfo.id = barcode.substring(4);
					barcodes.add(barcodeInfo);
				} else {
					System.out.println("不正バーコード：" + barcode);
				}
			}
		}
		return barcodes;
	}

	/**
	 * vistors_historyテーブルに来場履歴データをストアする
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param allData
	 *            RFIDログデータ
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void storeVistorHistory(Connection conn,
			List<String[]> allData) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		assert allData != null;
		long id = -1;
		for (String[] data : allData) {
			String sql = "INSERT INTO visitors_history VALUES(?,?,?,?);";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, String.valueOf(++id));
			ps.setString(2, data[2]);
			ps.setString(3, data[1]);
			ps.setString(4, data[3]);
			int num = ps.executeUpdate();
			assert num != 0;
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * ユニークなバーコード番号の<b>List</b>を取得
	 *
	 * @param barcodes
	 *            　バーコードデータ
	 * @return ユニークなバーコード番号の<b>List</b>
	 */
	public static List<String> getUniqueList(List<BarcodeDto> barcodes) {
		List<String> uniqueId = new ArrayList<String>();
		for (BarcodeDto barcodeInfo : barcodes) {
			if (!uniqueId.contains(barcodeInfo.id)) {
				uniqueId.add(barcodeInfo.id);
			}
		}
		return uniqueId;
	}

	/**
	 * 指定した条件を満たす来場者数を算出する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param query
	 *            SQLクエリのWhere句
	 * @return 条件を満たす来場者数
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static int getVisitorNumBySQL(Connection conn, StringBuffer query)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		StringBuffer sql = new StringBuffer(
				"select count(*) as TOTAL from rfid");
		if (query.length() != 0) {
			sql.append(" where ");
			sql.append(query);
			sql.append(";");
		} else {
			sql.append(";");
		}
		PreparedStatement ps = conn.prepareStatement(sql.toString());
		ResultSet rs = ps.executeQuery();
		rs.next();
		int cnt = rs.getInt("TOTAL");
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return cnt;
	}

	/**
	 * 指定した条件を満たす来場者数を算出する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param query
	 *            SQLクエリのWhere句
	 * @return 条件を満たす来場者数
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static List<String> getBarcodeListBySQLForBwj(Connection conn,
			StringBuffer query) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		StringBuffer sql = new StringBuffer("select rfid_no from rfid");
		if (query.length() != 0) {
			sql.append(" where ");
			sql.append(query);
			sql.append(";");
		} else {
			sql.append(";");
		}
		PreparedStatement ps = conn.prepareStatement(sql.toString());
		ResultSet rs = ps.executeQuery();
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			String rfid = rs.getString("rfid_no");
			list.add(rfid);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return list;
	}

	/**
	 * 【BWJ専用】集計結果を<b>Map</b>に登録
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param master
	 *            集計結果<b>Map</b>
	 * @param barcodes
	 *            バーコード一覧
	 * @param isPreRegist
	 *            集計対象が当日登録データであるか否かのブール値
	 * @return 集計結果を格納した<b>Map</b>
	 */
	public static Map<String, Integer> saveData(
			GeneralPurposePromptReportConfig config,
			Map<String, Integer> master, List<String> barcodes,
			boolean isPreRegist) {
		for (String barcode : barcodes) {
			if (isPreRegist && isPreRegist(barcode)) {
				String eps = getCategoryEps(config, barcode);
				if (StringUtil.isEmpty(eps)) {
					eps = "lastyear";
				}
				int count = master.get(eps);
				master.put(eps, ++count);
			} else if (!isPreRegist && isAppRegist(barcode)) {
				String eps = getCategoryEps(config, barcode);
				// if (StringUtil.isEmpty(eps)) {
				// eps = "lastyear";
				// }
				if (StringUtil.isNotEmpty(eps)) {
					int count = master.get(eps);
					master.put(eps, ++count);
				} else {
					System.out.println("■業種不明：" + barcode);
				}
			}
		}
		return master;
	}

	/**
	 * 【ILT専用】集計結果を<b>Map</b>に登録
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param master
	 *            集計結果<b>Map</b>
	 * @param barcodes
	 *            バーコード一覧
	 * @param isPreRegist
	 *            集計対象が当日登録データであるか否かのブール値
	 * @return 集計結果を格納した<b>Map</b>
	 */
	public static Map<String, Integer> saveDataForIlt(
			GeneralPurposePromptReportConfig config,
			Map<String, Integer> master, List<String> barcodes,
			boolean isPreRegist) {
		for (String barcode : barcodes) {
			if (isPreRegist && isPreRegistForILt(barcode)) {
				String eps = getCategoryEpsForILT(config, barcode);
				if (StringUtil.isEmpty(eps)) {
					eps = "other";
					System.out.println("■事前その他:" + barcode);
				}
				int count = master.get(eps);
				master.put(eps, ++count);
			} else if (!isPreRegist && isAppRegistForIlt(barcode)) {
				String eps = getCategoryEpsForILT(config, barcode);
				if (StringUtil.isEmpty(eps)) {
					eps = "other";
					System.out.println("■当日その他:" + barcode);
				}
				int count = master.get(eps);
				master.put(eps, ++count);
			}
		}
		return master;
	}

	/**
	 * 【BWJ専用】集計結果を<b>Map</b>に登録
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param master
	 *            集計結果<b>Map</b>
	 * @param barcodes
	 *            バーコード一覧
	 * @return 集計結果を格納した<b>Map</b>
	 */
	public static Map<String, Integer> saveData(
			GeneralPurposePromptReportConfig config,
			Map<String, Integer> master, List<String> barcodes) {
		for (String barcode : barcodes) {
			String eps = getCategoryEps(config, barcode);
			if (StringUtil.isEmpty(eps)) {
				eps = "lastyear";
			}
			int count = master.get(eps);
			master.put(eps, ++count);
		}
		return master;
	}

	/**
	 * 【ILT専用】集計結果を<b>Map</b>に登録
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param master
	 *            集計結果<b>Map</b>
	 * @param barcodes
	 *            バーコード一覧
	 * @return 集計結果を格納した<b>Map</b>
	 */
	public static Map<String, Integer> saveDataForIlt(
			GeneralPurposePromptReportConfig config,
			Map<String, Integer> master, List<String> barcodes) {
		for (String barcode : barcodes) {
			String eps = getCategoryEpsForILT(config, barcode);
			if (StringUtil.isEmpty(eps)) {
				eps = "other";
			}
			int count = master.get(eps);
			master.put(eps, ++count);
		}
		return master;
	}

	/**
	 * 【BWJ専用】条件を満たすRFIDの総数を求める
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param day
	 *            日付
	 * @param gyoNo
	 *            業種ID
	 * @param gateId
	 *            ゲートID
	 * @param isMulti
	 *            マルチアカウント方式で計数するか否か
	 * @return 条件を満たすRFIDの総数
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static int getBwjSingleMultiTotalRfidNum(Connection conn, int day,
			String gyoNo, String gateId, boolean isMulti)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String distinct = !isMulti ? "DISTINCT " : "";
		String sql = "SELECT count("
				+ distinct
				+ "t1.rfid_no) AS TOTAL FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = ? and t2.gyo_no = ? and SUBSTRING(gate_id,1,2) = ?;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, day);
		ps.setString(2, gyoNo);
		ps.setString(3, gateId);
		int cnt = 0;
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cnt += rs.getInt("TOTAL");
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return cnt;
	}

	/**
	 * 【BWJ専用】条件を満たす新規エントリRFIDのリストを求める
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日程
	 * @param day
	 *            対象日付
	 * @param start
	 *            集計対象の時間帯の開始時間
	 * @param end
	 *            集計対象の時間帯の終了時間
	 * @param
	 * @return 条件を満たす重複RFIDのリスト
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static List<String[]> getNewEntryRfidDetailInfo(Connection conn,
			String[] days, String day, String start, String end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		List<String[]> detailInfo = new ArrayList<String[]>();
		String sql = null;
		if ("all".equals(day)) {
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[0]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and " + days[0] + "_cnt > 0 ORDER BY t1.visitors_day;";
			detailInfo = Utilities.executeQuery(conn, sql, detailInfo);
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[1]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt = 0 and "
					+ days[1]
					+ "_cnt > 0 ORDER BY t1.visitors_day;";
			detailInfo = executeQuery(conn, sql, detailInfo);
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[2]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt = 0 and "
					+ days[1]
					+ "_cnt = 0 and "
					+ days[2]
					+ "_cnt > 0 ORDER BY t1.visitors_day;";
			detailInfo = executeQuery(conn, sql, detailInfo);
		} else {
			if (days[0].equals(day)) {
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[0]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[0]
						+ "_cnt > 0 ORDER BY t1.visitors_day;";
			} else if (days[1].equals(day)) {
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[1]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[0]
						+ "_cnt = 0 and "
						+ days[1]
						+ "_cnt > 0 ORDER BY t1.visitors_day;";
			} else if (days[2].equals(day)) {
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[2]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[0]
						+ "_cnt = 0 and "
						+ days[1]
						+ "_cnt = 0 and "
						+ days[2]
						+ "_cnt > 0 ORDER BY t1.visitors_day;";
			}
			detailInfo = executeQuery(conn, sql, detailInfo);
			for (int nIndex = 0; nIndex < detailInfo.size(); nIndex++) {
				String[] data = detailInfo.get(nIndex);
				data[3] = getGateIdBySQLQuery(conn, data[0], day);
				detailInfo.set(nIndex, data);
			}

		}
		return detailInfo;
	}

	/**
	 * 【BWJ専用】条件を満たすリピートRFIDのリストを求める
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日程
	 * @param day
	 *            対象日付
	 * @param start
	 *            集計対象の時間帯の開始時間
	 * @param end
	 *            集計対象の時間帯の終了時間
	 * @return　条件を満たすリピートRFIDのリスト
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static List<String[]> getRepeatRfidDetailInfo(Connection conn,
			String[] days, String day, String start, String end)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String sql = null;
		List<String[]> detailInfo = new ArrayList<String[]>();
		if ("all".equals(day)) {
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[1]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt > 0 and "
					+ days[2]
					+ "_cnt = 0;";
			detailInfo = executeQuery(conn, sql, detailInfo);
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[2]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt > 0 and "
					+ days[1]
					+ "_cnt = 0";
			detailInfo = executeQuery(conn, sql, detailInfo);
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[2]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt = 0 and "
					+ days[1]
					+ "_cnt > 0";
			detailInfo = executeQuery(conn, sql, detailInfo);
			sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
					+ days[2]
					+ " and "
					+ start
					+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
					+ end
					+ " and "
					+ days[0]
					+ "_cnt > 0 and "
					+ days[1]
					+ "_cnt > 0";
			detailInfo = executeQuery(conn, sql, detailInfo);
		} else {
			if (days[1].equals(day)) {
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[1]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end + " and " + days[0] + "_cnt > 0;";
				detailInfo = executeQuery(conn, sql, detailInfo);
			} else if (days[2].equals(day)) {
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[2]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[1]
						+ "_cnt = 0 and "
						+ days[0]
						+ "_cnt > 0";
				detailInfo = executeQuery(conn, sql, detailInfo);
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[2]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[1]
						+ "_cnt > 0 and "
						+ days[0]
						+ "_cnt =0";
				detailInfo = executeQuery(conn, sql, detailInfo);
				sql = "SELECT DISTINCT t1.rfid_no, t2.gyo_no, t2.pre_flg FROM visitors_history t1 left join rfid t2 on t2.rfid_no = t1.rfid_no where DAY(visitors_day) = "
						+ days[2]
						+ " and "
						+ start
						+ " <= HOUR(visitors_day) and HOUR(visitors_day) < "
						+ end
						+ " and "
						+ days[0]
						+ "_cnt > 0 and "
						+ days[1]
						+ "_cnt > 0";
				detailInfo = executeQuery(conn, sql, detailInfo);
			} else {
				assert false;
			}
			for (int nIndex = 0; nIndex < detailInfo.size(); nIndex++) {
				String[] data = detailInfo.get(nIndex);
				data[3] = getGateIdBySQLQuery(conn, data[0], day);
				detailInfo.set(nIndex, data);
			}
		}
		return detailInfo;
	}

	private static String getGateIdBySQLQuery(Connection conn, String rfidNo,
			String day) throws SQLException {
		String sql = "SELECT SUBSTRING(gate_id,1,2) AS gate FROM visitors_history WHERE rfid_no ='"
				+ rfidNo
				+ "' and DAY(visitors_day) = "
				+ day
				+ " ORDER BY visitors_day LIMIT 1;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		String gateId = null;
		while (rs.next()) {
			gateId = rs.getString("gate");
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return gateId;
	}

	/**
	 * 条件を満たすRFIDデータのリストを取得する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param sql
	 *            SQL文
	 * @param list
	 *            条件を満たすRFIDデータのリスト
	 * @return 条件を満たすRFIDデータのリスト
	 * @throws SQLException
	 */
	private static List<String[]> executeQuery(Connection conn, String sql,
			List<String[]> list) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String[] info = new String[4];
			info[0] = rs.getString("rfid_no");
			info[1] = rs.getString("gyo_no");
			info[2] = rs.getString("pre_flg");
			info[3] = null;
			list.add(info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return list;
	}

	/**
	 * 事前登録データであるか否かの検証
	 *
	 * @param barcode
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	private static boolean isPreRegist(String barcode) {
		if (StringUtil.isNotEmpty(barcode)) {
			return barcode.startsWith("1") || barcode.startsWith("0");
		}
		return false;
	}

	/**
	 * [ILT]事前登録データであるか否かの検証
	 *
	 * @param barcode
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	private static boolean isPreRegistForILt(String barcode) {
		if (StringUtil.isNotEmpty(barcode)
				&& StringUtil.integerStringCheck(barcode)) {
			int nBarcode = Integer.parseInt(barcode);
			boolean check1 = 200001 <= nBarcode && nBarcode <= 201241;
			boolean check2 = 400001 <= nBarcode && nBarcode <= 400705;
			boolean check3 = 600001 <= nBarcode && nBarcode <= 600909;
			boolean check4 = 500001 <= nBarcode && nBarcode <= 500047;
			boolean check5 = 300001 <= nBarcode && nBarcode <= 300490;
			boolean check6 = 700301 <= nBarcode && nBarcode <= 700783;
			boolean check7 = 700001 <= nBarcode && nBarcode <= 700041;
			boolean check8 = 700061 <= nBarcode && nBarcode <= 700081;
			boolean check9 = 700086 <= nBarcode && nBarcode <= 700110;
			boolean check10 = 700118 <= nBarcode && nBarcode <= 700152;
			boolean check11 = 700246 <= nBarcode && nBarcode <= 700300;
			boolean check12 = 700784 <= nBarcode && nBarcode <= 700804;
			boolean check13 = 800001 <= nBarcode && nBarcode <= 800044;
			return check1 || check2 || check3 || check4 || check5 || check6
					|| check7 || check8 || check9 || check10 || check11
					|| check12 || check13;
		}
		return false;
	}

	/**
	 * 当日入力データであるか否かの検証
	 *
	 * @param barcode
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	private static boolean isAppRegist(String barcode) {
		return !isPreRegist(barcode);
	}

	/**
	 * [ILT]当日入力データであるか否かの検証
	 *
	 * @param barcode
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	private static boolean isAppRegistForIlt(String barcode) {
		return !isPreRegistForILt(barcode);
	}

	/**
	 * 指定日の来場者数を算出する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param day_cnt
	 *            来場日
	 * @return 来場者数
	 * @throws SQLException
	 */
	public static Integer getVisitorNum(Connection conn, String day_cnt)
			throws SQLException {
		StringBuffer sql = new StringBuffer("select sum(" + day_cnt
				+ ")  as TOTAL from rfid");
		PreparedStatement ps = conn.prepareStatement(sql.toString());
		ResultSet rs = ps.executeQuery();
		rs.next();
		int cnt = rs.getInt("TOTAL");
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return cnt;
	}

	/**
	 * 数値型のリストの中身の加算結果を返却
	 *
	 * @param list
	 *            数値型のリスト
	 * @return 数値型のリストの加算結果
	 */
	public static int sum(List<Integer> list) {
		int sum = 0;
		for (int value : list) {
			sum += value;
		}
		return sum;
	}

	/**
	 * 【BWJ専用】数値型のリストの中身の加算結果を返却
	 *
	 * @param map
	 *            集計結果
	 * @return 集計結果の加算結果
	 */
	public static int sum(Map<String, Integer> map) {
		int sum = 0;
		List<Map.Entry<String, Integer>> mapValuesList = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());
		for (Entry<String, Integer> entry : mapValuesList) {
			sum += entry.getValue();
		}
		return sum;
	}

	/**
	 * 数値型のリストの中身の加算結果を返却
	 *
	 * @param list
	 *            数値型のリスト
	 * @return　数値型のリストの加算結果
	 */
	public static int sum(Integer[] buff) {
		int sum = 0;
		for (int nIndex = 0; nIndex < buff.length; nIndex++) {
			sum += buff[nIndex];
		}
		return sum;
	}

	/**
	 * Rfidテーブルの初期化
	 *
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param uniqueIdList
	 *            ユニークバーコードIDリスト
	 * @param totalDays
	 *            開催日数
	 * @throws SQLException
	 */
	public static void initializeRfidTable(Connection conn,
			List<String> uniqueIdList, int totalDays) throws SQLException {
		assert uniqueIdList != null;
		String zeroSet = repeatZeroSet(totalDays);
		String sql = "INSERT INTO rfid VALUES(?," + zeroSet + ");";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (String id : uniqueIdList) {
			ps.setString(1, id);
			ps.addBatch();
		}
		ps.executeBatch();
		if (ps != null) {
			ps.close();
		}
	}

	/**
	 * (BWJ)Rfidテーブルの初期化
	 *
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param uniqueIdList
	 *            ユニークバーコードIDリスト
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @throws SQLException
	 */
	public static void bwjInitializeRfidTable(Connection conn,
			List<String> uniqueIdList, GeneralPurposePromptReportConfig config)
			throws SQLException {
		assert uniqueIdList != null;
		int totalDays = config.days.length; // 開催日数
		String zeroSet = repeatZeroSet(totalDays);
		String sql = "INSERT INTO rfid VALUES(?," + zeroSet + ",?,?,?);";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (String id : uniqueIdList) {
			String categoryEps = getCategoryEps(config, id);
			String categoryId = config.bwjCategoriesMap.get(categoryEps);
			ps.setString(1, id);
			ps.setString(2, isPreRegist(id) ? "1" : "");
			ps.setString(3, categoryEps);
			ps.setString(4, categoryId);
			ps.addBatch();
		}
		ps.executeBatch();
		if (ps != null) {
			ps.close();
		}
	}

	/**
	 * (ILT)Rfidテーブルの初期化
	 *
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param uniqueIdList
	 *            ユニークバーコードIDリスト
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @throws SQLException
	 */
	public static void iltInitializeRfidTable(Connection conn,
			List<String> uniqueIdList, GeneralPurposePromptReportConfig config)
			throws SQLException {
		assert uniqueIdList != null;
		int totalDays = config.days.length; // 開催日数
		String zeroSet = repeatZeroSet(totalDays);
		String sql = "INSERT INTO rfid VALUES(?," + zeroSet + ",?,?,?);";
		PreparedStatement ps = conn.prepareStatement(sql);
		for (String id : uniqueIdList) {
			String categoryEps = getCategoryEpsForILT(config, id);
			String categoryId = config.bwjCategoriesMap.get(categoryEps);
			ps.setString(1, id);
			ps.setString(2, isPreRegistForILt(id) ? "1" : "");
			ps.setString(3, categoryEps);
			ps.setString(4, categoryId);
			ps.addBatch();
		}
		ps.executeBatch();
		if (ps != null) {
			ps.close();
		}
	}

	/**
	 * 《SQLクエリーの組立て》 '0','0',・・・を指定回数繰り返す
	 *
	 * @param repeat
	 *            繰り返し回数
	 * @return
	 */
	private static String repeatZeroSet(int repeat) {
		List<String> list = new ArrayList<String>();
		for (int nIndex = 0; nIndex < repeat; nIndex++) {
			list.add("'0'");
		}
		return StringUtil.concatWithDelimit(",", list);
	}

	/**
	 * rfidテーブルに来場日を記憶する
	 *
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param barcodes
	 *            バーコードデータ
	 * @param startIndex
	 *            バーコードデータのタイムスタンプにおける日時情報の開始インデックス(例. 20131106の場合は7を指定)
	 * @param endIndex
	 *            バーコードデータのタイムスタンプにおける日時情報の終了インデックス(例. 20131106の場合は8を指定)
	 * @param days
	 *            開催日の集合(2/26～2/28の場合は{"26","27","28"}を指定)
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void storeRfidData(Connection conn,
			List<BarcodeDto> barcodes, int startIndex, int endIndex,
			String days[]) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		for (BarcodeDto barcodeInfo : barcodes) {
			// 来場日
			String day = barcodeInfo.time.substring(startIndex, endIndex);
			if (day.startsWith("0")) {
				day = day.substring(1);
			}
			boolean[] hit = new boolean[days.length];
			int nIndex = -1;
			for (int nDay = 0; nDay < days.length; nDay++) {
				hit[++nIndex] = days[nDay].equals(day);
			}
			// DB更新
			if (isHit(hit)) {
				boolean result = updateStatusBySQL(conn, barcodeInfo.id, hit,
						days); // UPDATE文実行
				assert !result;
			}
		}
	}

	/**
	 * 来場チェック。指定配列に少なくともひとつ<b>true</b>が含まれている場合は<b>true</b>を返却
	 *
	 * @param buff
	 *            　<b>boolean</b>型配列
	 * @return 検証結果のブール値
	 */
	private static boolean isHit(boolean[] buff) {
		for (int nIndex = 0; nIndex < buff.length; nIndex++) {
			if (buff[nIndex]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定バーコードをDBに更新
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param rfid
	 *            RFIDno
	 * @param hit
	 *            来場判定バッファ
	 * @param days
	 *            開催日の集合(2/26～2/28の場合は{"26","27","28"}を指定)
	 * @return 実行結果の成否のブール値
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static boolean updateStatusBySQL(Connection conn, String rfid,
			boolean[] hit, String[] days) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		// SQL文実行
		for (int nIndex = 0; nIndex < hit.length; nIndex++) {
			String day = days[nIndex];
			if (hit[nIndex]) {
				String sql = "update rfid set " + day + "_cnt = (" + day
						+ "_cnt + 1)" + " where rfid_no = ?;";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, rfid);
				int num = ps.executeUpdate();
				if (ps != null) {
					ps.close();
				}
				return num != 1;
			}
		}
		return false;
	}

	/**
	 * rfidテーブルの全データを削除する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　処理の成否のブール値
	 * @throws SQLException
	 */
	public static boolean deleteAllData(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "delete from rfid;";
		stmt.executeUpdate(sql);
		stmt.close();
		return true;

	}

	/**
	 * rfidテーブルを削除する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　処理の成否のブール値
	 * @throws SQLException
	 */
	public static boolean removeRfidTable(Connection conn) throws SQLException {
		String sql = "DROP TABLE IF EXISTS `rfid`;";
		PreparedStatement ps = conn.prepareStatement(sql);
		boolean result = ps.execute();
		if (ps != null) {
			ps.close();
		}
		return result;
	}

	/**
	 * rfidテーブルを新規作成する
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param days
	 *            会期日程
	 * @return　処理の成否のブール値
	 * @throws SQLException
	 */
	public static boolean createRfidTable(Connection conn, String[] days)
			throws SQLException {
		List<String> output = new ArrayList<String>();
		output.add("CREATE TABLE `rfid` (`rfid_no` char(100) NOT NULL");
		for (String day : days) {
			output.add(StringUtil.enquoteWith("`", day + "_cnt")
					+ " int(3) default NULL");
		}
		output.add("`pre_flg` char(1)");
		output.add("`gyo_id` char(10)");
		output.add("`gyo_no` char(2)");
		output.add("PRIMARY KEY (`rfid_no`)) engine = MyISAM DEFAULT CHARACTER  SET latin1 COLLATE latin1_swedish_ci ");
		String sql = StringUtil.concatWithDelimit(",", output);
		PreparedStatement ps = conn.prepareStatement(sql);
		return ps.execute();
	}

	/**
	 * GeneralPurposeEnqueteConfigオブジェクトのインスタンス化
	 *
	 * @param configElement
	 *            config.xmlの頂点要素ノード
	 * @return <code>GeneralPurposePromptReportConfig</code>インスタンス
	 */
	public static GeneralPurposePromptReportConfig createConfig(
			Element configElement) {
		GeneralPurposePromptReportConfig config = new GeneralPurposePromptReportConfig();
		// config.xmlのルート要素
		Element propertiesElement = DOMUtil.getFirstChildElement(configElement);
		/* プロパティの読み込み */
		List<Element> properties = DOMUtil
				.getChildrenElement(propertiesElement);
		config.inputSeparateMark = getPropertyValue(properties,
				PROPERTY_INPUT_SEPARATE_MARK);
		config.enquoteByDoubleQuotation = StringUtil
				.toBoolean(getPropertyValue(properties,
						PROPERTY_ENQUOTE_BY_DOUBLE_QUOTATION));
		config.removeHeaderRecord = StringUtil.toBoolean(getPropertyValue(
				properties, PROPERTY_REMOVE_HEADER_RECORD));
		config.startIndexDayInTimestamp = StringUtil
				.toInteger(getPropertyValue(properties,
						PROPERTY_STARTINDEX_DAY_IN_TIMESTAMP));
		config.endIndexDayInTimestamp = StringUtil.toInteger(getPropertyValue(
				properties, PROPERTY_ENDINDEX_DAY_IN_TIMESTAMP));
		/* 会期日程の読み込み */
		Element year = DOMUtil.getNextSiblingElement(propertiesElement);
		config.year = DOMUtil.getData(year);
		Element month = DOMUtil.getNextSiblingElement(year);
		config.month = DOMUtil.getData(month);
		Element days = DOMUtil.getNextSiblingElement(month);
		assert days != null;
		List<Element> children = DOMUtil.getChildrenElement(days);
		String[] dayBuff = new String[children.size()];
		int position = -1;
		for (Element child : children) {
			dayBuff[++position] = DOMUtil.getData(child);
		}
		config.days = dayBuff;
		/* 【BWJ専用】当日入力用カテゴリ分類定義の読み込み */
		Element rules = DOMUtil.getNextSiblingElement(days);
		if (rules != null) {
			children = DOMUtil.getChildrenElement(rules);
			for (Element rule : children) {
				String barcode = DOMUtil.getAttributeValue(rule, "barcode");
				String label = DOMUtil.getAttributeValue(rule, "label");
				String value = DOMUtil.getAttributeValue(rule, "value");
				String id = DOMUtil.getAttributeValue(rule, "id");
				assert StringUtil.isNotEmpty(barcode)
						&& StringUtil.isNotEmpty(value)
						&& StringUtil.isNotEmpty(label);
				config.bwjAppointeddayCategoryRules.put(barcode, value);
				config.bwjCategoryList.add(label);
				config.bwjCategoriesMap.put(value, id);
			}
		}
		return config;
	}

	/**
	 * 【BWJ専用】事前登録用カテゴリ分類定義の取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 事前登録用カテゴリ分類定義
	 * @throws SQLException
	 */
	public static Map<String, String> loadBwjPreRegistCategoryMap(
			Connection conn) throws SQLException {
		String sql = "SELECT * FROM bwj_preregist_eps;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		Map<String, String> rules = new HashMap<String, String>();
		while (rs.next()) {
			String barcode = rs.getString("barcode");
			String eps = rs.getString("eps");
			assert StringUtil.isNotEmpty(barcode) && StringUtil.isNotEmpty(eps);
			rules.put(barcode, eps);
		}
		return rules;
	}

	/**
	 * 【ILT専用】事前登録用カテゴリ分類定義の取得
	 *
	 * @return 事前登録用カテゴリ分類定義
	 */
	public static Map<String, String> loadIltPreRegistCategoryMap() {
		Map<String, String> rules = new HashMap<String, String>();
		rules.put("20", "01.eps");
		rules.put("40", "02.eps");
		rules.put("60", "03.eps");
		rules.put("50", "04.eps");
		rules.put("30", "05.eps");
		rules.put("70", "06.eps");
		rules.put("80", "07.eps");
		return rules;
	}

	/**
	 * 【BWJ専用】カテゴリ分類の特定
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param barcode
	 *            バーコード番号
	 * @return カテゴリEPS
	 */
	public static String getCategoryEps(
			GeneralPurposePromptReportConfig config, String barcode) {
		assert config != null && StringUtil.isNotEmpty(barcode);
		if (isPreRegist(barcode)) { // 事前登録
			return config.bwjPreRegistCategoryRules.get(StringUtil.enquoteWith(
					"B", barcode));
		} else { // 当日入力
			return config.bwjAppointeddayCategoryRules.get(barcode.substring(0,
					2));
		}
	}

	/**
	 * 【ILT専用】カテゴリ分類の特定
	 *
	 * @param config
	 *            <b>GeneralPurposePromptReportConfig</b>
	 * @param barcode
	 *            バーコード番号
	 * @return カテゴリEPS
	 */
	public static String getCategoryEpsForILT(
			GeneralPurposePromptReportConfig config, String barcode) {
		assert config != null && StringUtil.isNotEmpty(barcode);
		if (isPreRegistForILt(barcode)) { // 事前登録
			return config.bwjPreRegistCategoryRules
					.get(barcode.substring(0, 2));
		} else { // 当日入力
			return config.bwjAppointeddayCategoryRules.get(barcode.substring(0,
					2));
		}
	}

	/**
	 * 指定プロパティの値を取得
	 *
	 * @param children
	 *            　property要素ノードの<b>List</b>
	 * @param target
	 *            プロパティ名
	 * @return プロパティの値
	 */
	private static String getPropertyValue(List<Element> children, String target) {
		assert StringUtil.isNotEmpty(target);
		for (Element property : children) {
			String name = property.getAttribute("name");
			if (target.equals(name)) {
				String value = property.getAttribute("value");
				return normalize(value);
			}
		}
		return null;
	}

	/**
	 * config.xmlの属性値の正規化
	 *
	 * @param value
	 *            属性値
	 * @return 正規化後の属性値
	 */
	private static String normalize(String value) {
		if (StringUtil.isNotEmpty(value)) {
			value = value.replaceAll("\\\\t", "\t");
		}
		return value;
	}

}
