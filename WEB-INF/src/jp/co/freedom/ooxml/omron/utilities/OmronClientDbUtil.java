package jp.co.freedom.ooxml.omron.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.co.freedom.ooxml.omron.dto.OmronChoiceDto;
import jp.co.freedom.ooxml.omron.dto.OmronEnqueteDto;

/**
 * 【OMRON】客先評価DBアクセスユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronClientDbUtil {
	/**
	 * 設問1.派遣社員評価の回答結果の取得(拠点別)
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象のカラム名
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1PositionMap(Connection conn,
			String targetColumnName) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_POS,'total') AS 'position',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(case when " + targetColumnName + " like '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' then 1 else null end) as 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(case when " + targetColumnName + " like '%"
						+ String.valueOf(nIndex)
						+ "%' then 1 else null end) as 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (nIndex != 10) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL GROUP BY V_POS WITH ROLLUP;");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("position");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1.派遣社員評価の回答結果の取得(グループ別)
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param wholeFlg
	 *            全体フラグ
	 * @param deptGroup
	 *            所属
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1Map(Connection conn,
			String targetColumnName, boolean wholeFlg, String deptGroup)
			throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(" + (wholeFlg ? "V_DEPT_G1" : "V_DEPT_G2")
				+ ",'total') AS 'position',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(case when " + targetColumnName + " like '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' then 1 else null end) as 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(case when " + targetColumnName + " like '%"
						+ String.valueOf(nIndex)
						+ "%' then 1 else null end) as 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (nIndex != 10) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL");
		if (!wholeFlg) {
			sb.append(" AND V_DEPT_G1='" + deptGroup + "'");
		}
		sb.append(" GROUP BY " + (wholeFlg ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("position");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1.派遣社員評価の回答結果の取得(支店別)
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @param branchId
	 *            支店ID
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1BranchMap(Connection conn,
			String targetColumnName, String branchId) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_SALES,'total') AS 'v_sales',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex)
						+ "%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (10 != nIndex) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL AND V_POS = '" + branchId
				+ "' GROUP BY V_SALES WITH ROLLUP");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("v_sales");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1) Q1-1×Q1_8のクロス集計結果の取得　M1シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1_8MapForM1Sheet(
			Connection conn, String targetColumnName) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_Q1_8,'total') AS 'V_Q1_8',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex)
						+ "%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (10 != nIndex) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL GROUP BY V_Q1_8;");
		String sql = sb.toString();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("V_Q1_8");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1) Q1-1×Q1_9のクロス集計結果の取得　M1シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1_9MapForM1Sheet(
			Connection conn, String targetColumnName) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_Q1_9,'total') AS 'V_Q1_9',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex)
						+ "%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (10 != nIndex) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL GROUP BY V_Q1_9;");
		String sql = sb.toString();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("V_Q1_9");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1) Q1-1×Q2_4のクロス集計結果の取得　M1シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param targetColumnName
	 *            集計対象の質問内容（DBカラム名）
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ2_4MapForM1Sheet(
			Connection conn, String targetColumnName) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_Q2_4,'total') AS 'V_Q2_4',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 10; nIndex++) {
			if (1 == nIndex) {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex) + "%' and " + targetColumnName
						+ " not like '%10%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			} else {
				sb.append("count(CASE WHEN " + targetColumnName + " LIKE '%"
						+ String.valueOf(nIndex)
						+ "%' THEN 1 ELSE NULL END) AS 'Q1_"
						+ String.valueOf(nIndex) + "'");
			}
			if (10 != nIndex) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete WHERE " + targetColumnName
				+ " IS NOT NULL GROUP BY V_Q2_4;");
		String sql = sb.toString();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("V_Q2_4");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			info.count7 = rs.getInt("Q1_6");
			info.count8 = rs.getInt("Q1_7");
			info.count9 = rs.getInt("Q1_8");
			info.count10 = rs.getInt("Q1_9");
			info.count11 = rs.getInt("Q1_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問1fa. 現在の派遣先満足度FAの回答結果の取得（派遣先別）
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            所属グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ1FaListForDispatch(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM customernquete WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q1fa = rs.getString("V_Q1_FA");
			list.add(info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return list;
	}

	/**
	 * 設問1fa. 現在の派遣先満足度FAの回答結果の取得（担当者別）
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param salesman
	 *            担当者名
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ1FaListBySalesman(Connection conn,
			String position, String salesman) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM customernquete WHERE V_POS = '" + position
				+ "' AND V_SALES = '" + salesman + "' ORDER BY V_SALES DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.salesman = rs.getString("V_SALES");
			info.q1fa = rs.getString("V_Q1_FA");
			list.add(info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return list;
	}

	/**
	 * 設問2) Q2_4の集計結果の取得　M2シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ2_4MapForM2Sheet(
			Connection conn) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COALESCE(V_Q2_4,'total') AS 'V_Q2_4',");
		sb.append("count(*) AS 'total',");
		for (int nIndex = 1; nIndex <= 5; nIndex++) {
			sb.append("count(CASE WHEN V_Q2_4 LIKE '%" + String.valueOf(nIndex)
					+ "%' THEN 1 ELSE NULL END) AS 'Q2_"
					+ String.valueOf(nIndex) + "'");
			if (5 != nIndex) {
				sb.append(",");
			}
		}
		sb.append("FROM customernquete GROUP BY V_Q2_4;");
		String sql = sb.toString();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String key = rs.getString("V_Q2_4");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			info.count5 = rs.getInt("Q2_4");
			info.count6 = rs.getInt("Q2_5");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 設問8. 弊社へのご意見ご要望の回答結果の取得（派遣先別）
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            所属グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ8ListForDispatch(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM customernquete WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q8 = rs.getString("V_Q8");
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
	 * 設問8. 弊社へのご意見ご要望の回答結果の取得（担当者別）
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param salesman
	 *            担当者名
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ8ListBySalesman(Connection conn,
			String position, String salesman) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM customernquete WHERE V_POS = '" + position
				+ "' AND V_SALES = '" + salesman + "' ORDER BY V_SALES DESC;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.salesman = rs.getString("V_SALES");
			info.q8 = rs.getString("V_Q8");
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
	 * 設問7. 今後の外部人材活用計画についてFAの回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ7FaList(Connection conn)
			throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM customernquete WHERE V_Q7_FA IS NOT NULL ORDER BY V_POS ASC, V_DEPT_G1 ASC, V_DEPT_G2 ASC;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.position = rs.getString("V_POS");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.dept = rs.getString("V_CORP");
			info.q7_fa = rs.getString("V_Q7_FA");
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

}