package jp.co.freedom.ooxml.omron.utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.ooxml.omron.dto.OmronChoiceDto;
import jp.co.freedom.ooxml.omron.dto.OmronEnqueteDto;

/**
 * 【OMRON】DBアクセスユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronDbUtil {

	/** 所属名 */
	public static Map<Integer, String> POSITION = createPositionMap();

	/** 所属名（表示名） */
	public static Map<Integer, String> DISPLAY_POSITION = createDisplayPositionMap();

	/** グループ名 */
	public static Map<String, String> GROUP = createGroupMap();

	/** 担当者名1 */
	public static List<String> SALESMAN1 = createSalesManList1();
	public static List<String> SALESMAN2 = createSalesManList2();
	public static List<String> SALESMAN3 = createSalesManList3();
	public static List<String> SALESMAN4 = createSalesManList4();
	public static List<String> SALESMAN5 = createSalesManList5();

	/** 設問1の回答選択肢 */
	public static Map<String, String> Q1 = createQ1Map();

	/**
	 * 担当者リストの取得
	 * 
	 * @param pos
	 *            所属ID
	 * @return 担当者リスト
	 */
	public static List<String> getSalesManList(String pos) {
		if ("1".equals(pos)) {
			return SALESMAN1;
		} else if ("2".equals(pos)) {
			return SALESMAN2;
		} else if ("3".equals(pos)) {
			return SALESMAN3;
		} else if ("4".equals(pos)) {
			return SALESMAN4;
		} else if ("5".equals(pos)) {
			return SALESMAN5;
		}
		return null;
	}

	private static Map<Integer, String> createPositionMap() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "全店");
		map.put(1, "京都支店");
		map.put(2, "大阪支店");
		map.put(3, "東京支店");
		map.put(4, "名古屋営業所");
		map.put(5, "エンジニア");
		return map;
	}

	private static Map<Integer, String> createDisplayPositionMap() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "《京都支店》");
		map.put(2, "《大阪支店》");
		map.put(3, "《東京支店》");
		map.put(4, "《名古屋営業所》");
		map.put(5, "《エンジニア》");
		return map;
	}

	private static Map<String, String> createGroupMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("OC", "＜ＯＣ＞");
		map.put("OCG", "＜ＯＣＧ＞");
		map.put("外部", "＜外部＞");
		return map;
	}

	private static List<String> createSalesManList1() {
		List<String> list = new ArrayList<String>();
		list.add("藤井");
		list.add("河邨");
		list.add("薬司");
		list.add("高崎");
		list.add("杉本");
		list.add("千代");
		list.add("岡島");
		list.add("糀谷");
		list.add("磯部");
		list.add("乾");
		list.add("神馬");
		list.add("島田");
		list.add("宮田（真）");
		list.add("福井");
		list.add("横山");
		return list;
	}

	private static List<String> createSalesManList2() {
		List<String> list = new ArrayList<String>();
		list.add("大平");
		list.add("髙田");
		list.add("渡部");
		list.add("森本");
		return list;
	}

	private static List<String> createSalesManList3() {
		List<String> list = new ArrayList<String>();
		list.add("横田");
		list.add("山本　");
		list.add("金田");
		list.add("荻島");
		list.add("西川");
		list.add("山本");
		return list;
	}

	private static List<String> createSalesManList4() {
		List<String> list = new ArrayList<String>();
		list.add("宮田（梨）");
		list.add("岡");
		list.add("宮田");
		return list;
	}

	private static List<String> createSalesManList5() {
		List<String> list = new ArrayList<String>();
		list.add("玉那覇");
		list.add("伴");
		list.add("斉藤");
		list.add("河野");
		list.add("田村");
		list.add("吉田");
		list.add("東");
		list.add("薮上");
		return list;
	}

	private static Map<String, String> createQ1Map() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("1", "大変満足／満足");
		map.put("2", "大変満足／満足");
		map.put("3", "普通");
		map.put("4", "不満／大変不満");
		map.put("5", "不満／大変不満");
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1Map(Connection conn,
			boolean all, String position, String deptGroup) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q1 like '%1%' then 1 else null end) as 'Q1_1',");
		sb.append("count(case when V_Q1 like '%2%' then 1 else null end) as 'Q1_2',");
		sb.append("count(case when V_Q1 like '%3%' then 1 else null end) as 'Q1_3',");
		sb.append("count(case when V_Q1 like '%4%' then 1 else null end) as 'Q1_4',");
		sb.append("count(case when V_Q1 like '%5%' then 1 else null end) as 'Q1_5'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " is not null and V_Q1 is not null ");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1fa. 現在の派遣先満足度FAの回答結果の取得(派遣先別)
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
	public static List<OmronEnqueteDto> getQ1FaListByDept(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup
				+ "' ORDER BY V_CORP DESC, V_Q1 DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q1 = rs.getString("V_Q1");
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
	 * 設問1fa. 現在の派遣先満足度FAの回答結果の取得(担当者別)
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
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_SALES = '" + salesman
				+ "' ORDER BY V_SALES DESC, V_Q1 DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.salesman = rs.getString("V_SALES");
			info.q1 = rs.getString("V_Q1");
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
	 * 設問2.仕事上で困っていることの回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ2Map(Connection conn,
			boolean all, String position, String deptGroup) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2 like '%1%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2 is null  then 1 else null end) as 'Q2_3'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2") + " is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3.派遣先の継続意向の回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3Map(Connection conn,
			boolean all, String position, String deptGroup) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q3 like '%1%' then 1 else null end) as 'Q3_1',");
		sb.append("count(case when V_Q3 like '%2%' then 1 else null end) as 'Q3_2',");
		sb.append("count(case when V_Q3 like '%3%' then 1 else null end) as 'Q3_3'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " is not null and V_Q3 is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q3_1");
			info.count3 = rs.getInt("Q3_2");
			info.count4 = rs.getInt("Q3_3");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問2-1.困っている内容の回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ2_1Map(Connection conn,
			boolean all, String position, String deptGroup) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2_1 like '%1%' and V_Q2_1 not like '%10%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2_1 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2_1 like '%3%' then 1 else null end) as 'Q2_3',");
		sb.append("count(case when V_Q2_1 like '%4%' then 1 else null end) as 'Q2_4',");
		sb.append("count(case when V_Q2_1 like '%5%' then 1 else null end) as 'Q2_5',");
		sb.append("count(case when V_Q2_1 like '%6%' then 1 else null end) as 'Q2_6',");
		sb.append("count(case when V_Q2_1 like '%7%' then 1 else null end) as 'Q2_7',");
		sb.append("count(case when V_Q2_1 like '%8%' then 1 else null end) as 'Q2_8',");
		sb.append("count(case when V_Q2_1 like '%9%' then 1 else null end) as 'Q2_9',");
		sb.append("count(case when V_Q2_1 like '%10%' then 1 else null end) as 'Q2_10'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " is not null and V_Q2_1 is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			info.count5 = rs.getInt("Q2_4");
			info.count6 = rs.getInt("Q2_5");
			info.count7 = rs.getInt("Q2_6");
			info.count8 = rs.getInt("Q2_7");
			info.count9 = rs.getInt("Q2_8");
			info.count10 = rs.getInt("Q2_9");
			info.count11 = rs.getInt("Q2_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問2-1fa. 困っている内容FAの回答結果の取得(派遣先別)
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
	public static List<OmronEnqueteDto> getQ2_1FaListByDept(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q2_1_fa = rs.getString("V_Q2_1_FA");
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
	 * 設問2-1fa. 困っている内容FAの回答結果の取得(担当者別)
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
	public static List<OmronEnqueteDto> getQ2_1FaListBySales(Connection conn,
			String position, String salesman) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_SALES = '" + salesman + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.salesman = rs.getString("V_SALES");
			info.q2_1_fa = rs.getString("V_Q2_1_FA");
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
	 * 設問3. 現在の派遣先での契約継続希望の回答結果の取得　M1シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3MapForM1Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q3 as 'V_Q3',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q1 like '%1%' then 1 else null end) as 'Q1_1',");
		sb.append("count(case when V_Q1 like '%2%' then 1 else null end) as 'Q1_2',");
		sb.append("count(case when V_Q1 like '%3%' then 1 else null end) as 'Q1_3',");
		sb.append("count(case when V_Q1 like '%4%' then 1 else null end) as 'Q1_4',");
		sb.append("count(case when V_Q1 like '%5%' then 1 else null end) as 'Q1_5'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q3 is not null and V_Q1 is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q3;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "継続を希望", "検討中", "継続を希望しない" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q1_1");
			info.count3 = rs.getInt("Q1_2");
			info.count4 = rs.getInt("Q1_3");
			info.count5 = rs.getInt("Q1_4");
			info.count6 = rs.getInt("Q1_5");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得　M2シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1MapForM2Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q1 as 'V_Q1',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2 like '%1%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2 is null  then 1 else null end) as 'Q2_3'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q1 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q1;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "大変満足", "満足", "普通", "不満", "大変不満" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1MapForM2_1Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q1 as 'V_Q1',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2_1 like '%1%' and V_Q2_1 not like '%10%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2_1 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2_1 like '%3%' then 1 else null end) as 'Q2_3',");
		sb.append("count(case when V_Q2_1 like '%4%' then 1 else null end) as 'Q2_4',");
		sb.append("count(case when V_Q2_1 like '%5%' then 1 else null end) as 'Q2_5',");
		sb.append("count(case when V_Q2_1 like '%6%' then 1 else null end) as 'Q2_6',");
		sb.append("count(case when V_Q2_1 like '%7%' then 1 else null end) as 'Q2_7',");
		sb.append("count(case when V_Q2_1 like '%8%' then 1 else null end) as 'Q2_8',");
		sb.append("count(case when V_Q2_1 like '%9%' then 1 else null end) as 'Q2_9',");
		sb.append("count(case when V_Q2_1 like '%10%' then 1 else null end) as 'Q2_10'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q1 IS NOT NULL AND V_Q2_1 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q1;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "大変満足", "満足", "普通", "不満", "大変不満" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			info.count5 = rs.getInt("Q2_4");
			info.count6 = rs.getInt("Q2_5");
			info.count7 = rs.getInt("Q2_6");
			info.count8 = rs.getInt("Q2_7");
			info.count9 = rs.getInt("Q2_8");
			info.count10 = rs.getInt("Q2_9");
			info.count11 = rs.getInt("Q2_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得　M3シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1MapForM3Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q1 as 'V_Q1',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q3 like '%1%' then 1 else null end) as 'Q3_1',");
		sb.append("count(case when V_Q3 like '%2%' then 1 else null end) as 'Q3_2',");
		sb.append("count(case when V_Q3 like '%3%' then 1 else null end) as 'Q3_3'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q1 IS NOT NULL AND V_Q3 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q1;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "大変満足", "満足", "普通", "不満", "大変不満" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q3_1");
			info.count3 = rs.getInt("Q3_2");
			info.count4 = rs.getInt("Q3_3");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得　M8シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param rank
	 *            ランク
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1MapForM8Sheet(
			Connection conn, String position, int rank) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q1 as 'V_Q1',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%1%' then 1 else null end) as 'Q8_1',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%2%' then 1 else null end) as 'Q8_2',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%3%' then 1 else null end) as 'Q8_3',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%4%' then 1 else null end) as 'Q8_4',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%5%' then 1 else null end) as 'Q8_5',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%6%' then 1 else null end) as 'Q8_6',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%7%' then 1 else null end) as 'Q8_7',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%8%' then 1 else null end) as 'Q8_8'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q1 IS NOT NULL AND V_Q8_" + rank + " is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q1;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "大変満足", "満足", "普通", "不満", "大変不満" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q8_1");
			info.count3 = rs.getInt("Q8_2");
			info.count4 = rs.getInt("Q8_3");
			info.count5 = rs.getInt("Q8_4");
			info.count6 = rs.getInt("Q8_5");
			info.count7 = rs.getInt("Q8_6");
			info.count8 = rs.getInt("Q8_7");
			info.count9 = rs.getInt("Q8_8");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問1. 現在の派遣先満足度の回答結果の取得　M9シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ1MapForM9Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q1 as 'V_Q1',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q9 like '%1%' and V_Q9 not like '%10%' then 1 else null end) as 'Q9_1',");
		sb.append("count(case when V_Q9 like '%2%' then 1 else null end) as 'Q9_2',");
		sb.append("count(case when V_Q9 like '%3%' then 1 else null end) as 'Q9_3',");
		sb.append("count(case when V_Q9 like '%4%' then 1 else null end) as 'Q9_4',");
		sb.append("count(case when V_Q9 like '%5%' then 1 else null end) as 'Q9_5',");
		sb.append("count(case when V_Q9 like '%6%' then 1 else null end) as 'Q9_6',");
		sb.append("count(case when V_Q9 like '%7%' then 1 else null end) as 'Q9_7',");
		sb.append("count(case when V_Q9 like '%8%' then 1 else null end) as 'Q9_8',");
		sb.append("count(case when V_Q9 like '%9%' then 1 else null end) as 'Q9_9',");
		sb.append("count(case when V_Q9 like '%10%' then 1 else null end) as 'Q9_10'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q1 IS NOT NULL  AND V_Q9 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q1;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "大変満足", "満足", "普通", "不満", "大変不満" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q9_1");
			info.count3 = rs.getInt("Q9_2");
			info.count4 = rs.getInt("Q9_3");
			info.count5 = rs.getInt("Q9_4");
			info.count6 = rs.getInt("Q9_5");
			info.count7 = rs.getInt("Q9_6");
			info.count8 = rs.getInt("Q9_7");
			info.count9 = rs.getInt("Q9_8");
			info.count10 = rs.getInt("Q9_9");
			info.count11 = rs.getInt("Q9_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3. 現在の派遣先での契約継続希望の回答結果の取得　M2シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3MapForM2Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q3 as 'V_Q3',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2 like '%1%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2 is null then 1 else null end) as 'Q2_3'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q3 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q3;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "継続を希望", "検討中", "継続を希望しない" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3. 現在の派遣先での契約継続希望の回答結果の取得　M2_1シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3MapForM2_1Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q3 as 'V_Q3',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q2_1 like '%1%' and V_Q2_1 not like '%10%' then 1 else null end) as 'Q2_1',");
		sb.append("count(case when V_Q2_1 like '%2%' then 1 else null end) as 'Q2_2',");
		sb.append("count(case when V_Q2_1 like '%3%' then 1 else null end) as 'Q2_3',");
		sb.append("count(case when V_Q2_1 like '%4%' then 1 else null end) as 'Q2_4',");
		sb.append("count(case when V_Q2_1 like '%5%' then 1 else null end) as 'Q2_5',");
		sb.append("count(case when V_Q2_1 like '%6%' then 1 else null end) as 'Q2_6',");
		sb.append("count(case when V_Q2_1 like '%7%' then 1 else null end) as 'Q2_7',");
		sb.append("count(case when V_Q2_1 like '%8%' then 1 else null end) as 'Q2_8',");
		sb.append("count(case when V_Q2_1 like '%9%' then 1 else null end) as 'Q2_9',");
		sb.append("count(case when V_Q2_1 like '%10%' then 1 else null end) as 'Q2_10'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q3 is not null and V_Q2_1 is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q3;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "サンプル数", "業務内容", "業務量", "時間外勤務", "業務の変更・拡大", "職場環境",
				"やりがいを感じない", "成長しない", "人間関係", "OPCの対応", "その他" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q2_1");
			info.count3 = rs.getInt("Q2_2");
			info.count4 = rs.getInt("Q2_3");
			info.count5 = rs.getInt("Q2_4");
			info.count6 = rs.getInt("Q2_5");
			info.count7 = rs.getInt("Q2_6");
			info.count8 = rs.getInt("Q2_7");
			info.count9 = rs.getInt("Q2_8");
			info.count10 = rs.getInt("Q2_9");
			info.count11 = rs.getInt("Q2_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3.派遣先の継続意向の回答結果の取得　M8シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @param rank
	 *            ランク
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3MapForM8Sheet(
			Connection conn, String position, int rank) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q3 as 'V_Q3',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%1%' then 1 else null end) as 'Q8_1',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%2%' then 1 else null end) as 'Q8_2',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%3%' then 1 else null end) as 'Q8_3',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%4%' then 1 else null end) as 'Q8_4',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%5%' then 1 else null end) as 'Q8_5',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%6%' then 1 else null end) as 'Q8_6',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%7%' then 1 else null end) as 'Q8_7',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%8%' then 1 else null end) as 'Q8_8'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q3 IS NOT NULL AND V_Q8_" + rank + " is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q3;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "継続を希望", "検討中", "継続を希望しない" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q8_1");
			info.count3 = rs.getInt("Q8_2");
			info.count4 = rs.getInt("Q8_3");
			info.count5 = rs.getInt("Q8_4");
			info.count6 = rs.getInt("Q8_5");
			info.count7 = rs.getInt("Q8_6");
			info.count8 = rs.getInt("Q8_7");
			info.count9 = rs.getInt("Q8_8");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3.派遣先の継続意向の回答結果の取得　M8シート向け
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ3MapForM9Sheet(
			Connection conn, String position) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select V_Q3 as 'V_Q3',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q9 like '%1%' and V_Q9 not like '%10%' then 1 else null end) as 'Q9_1',");
		sb.append("count(case when V_Q9 like '%2%' then 1 else null end) as 'Q9_2',");
		sb.append("count(case when V_Q9 like '%3%' then 1 else null end) as 'Q9_3',");
		sb.append("count(case when V_Q9 like '%4%' then 1 else null end) as 'Q9_4',");
		sb.append("count(case when V_Q9 like '%5%' then 1 else null end) as 'Q9_5',");
		sb.append("count(case when V_Q9 like '%6%' then 1 else null end) as 'Q9_6',");
		sb.append("count(case when V_Q9 like '%7%' then 1 else null end) as 'Q9_7',");
		sb.append("count(case when V_Q9 like '%8%' then 1 else null end) as 'Q9_8',");
		sb.append("count(case when V_Q9 like '%9%' then 1 else null end) as 'Q9_9',");
		sb.append("count(case when V_Q9 like '%10%' then 1 else null end) as 'Q9_10'");
		sb.append(" from  staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		conditions.add("V_Q3 IS NOT NULL AND V_Q9 IS NOT NULL");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY V_Q3;";
		ResultSet rs = stmt.executeQuery(sql);
		String keys[] = { "継続を希望", "検討中", "継続を希望しない" };
		int nKey = -1;
		while (rs.next()) {
			String key = keys[++nKey];
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q9_1");
			info.count3 = rs.getInt("Q9_2");
			info.count4 = rs.getInt("Q9_3");
			info.count5 = rs.getInt("Q9_4");
			info.count6 = rs.getInt("Q9_5");
			info.count7 = rs.getInt("Q9_6");
			info.count8 = rs.getInt("Q9_7");
			info.count9 = rs.getInt("Q9_8");
			info.count10 = rs.getInt("Q9_9");
			info.count11 = rs.getInt("Q9_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問3. 継続をしないや検討中の理由の回答結果の取得(派遣先別)
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
	public static List<OmronEnqueteDto> getQ3FaListByDept(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q3_fa = rs.getString("V_Q3_FA");
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
	 * 設問3. 継続をしないや検討中の理由の回答結果の取得(担当者別)
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
	public static List<OmronEnqueteDto> getQ3FaListBySales(Connection conn,
			String position, String salesman) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_SALES = '" + salesman + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.salesman = rs.getString("V_SALES");
			info.q3_fa = rs.getString("V_Q3_FA");
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
	 * 設問5-1. この1年で取得した資格(派遣先別)
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
	public static List<OmronEnqueteDto> getQ5_1List(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.capacities = rs.getString("V_Q5_CAP_NAME1");
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
	 * 設問5-2. この1年で受講した研修・セミナー(派遣先別)
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
	public static List<OmronEnqueteDto> getQ5_2List(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.trainings = rs.getString("V_Q5_TRA_NAME1");
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
	 * 設問6. 新たに習得したスキル(派遣先別)
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
	public static List<OmronEnqueteDto> getQ6List(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q6 = rs.getString("V_Q6");
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
	 * 設問7. 今後習得したいスキルや受けたい研修・講演(派遣先別)
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
	public static List<OmronEnqueteDto> getQ7List(Connection conn,
			String position, String deptGroup) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT * FROM `staffenquete` WHERE V_POS = '" + position
				+ "' AND V_DEPT_G1 = '" + deptGroup + "' ORDER BY V_CORP DESC;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.dept = rs.getString("V_CORP");
			info.deptGroup1 = rs.getString("V_DEPT_G1");
			info.deptGroup2 = rs.getString("V_DEPT_G2");
			info.q7 = rs.getString("V_Q7");
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
	 * 設問3.派遣先の継続意向の回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @param rank
	 *            ランク
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ8Map(Connection conn,
			boolean all, String position, String deptGroup, int rank)
			throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%1%' then 1 else null end) as 'Q8_1',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%2%' then 1 else null end) as 'Q8_2',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%3%' then 1 else null end) as 'Q8_3',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%4%' then 1 else null end) as 'Q8_4',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%5%' then 1 else null end) as 'Q8_5',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%6%' then 1 else null end) as 'Q8_6',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%7%' then 1 else null end) as 'Q8_7',");
		sb.append("count(case when V_Q8_");
		sb.append(rank + " like '%8%' then 1 else null end) as 'Q8_8'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " is not null and V_Q8_" + rank + " is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q8_1");
			info.count3 = rs.getInt("Q8_2");
			info.count4 = rs.getInt("Q8_3");
			info.count5 = rs.getInt("Q8_4");
			info.count6 = rs.getInt("Q8_5");
			info.count7 = rs.getInt("Q8_6");
			info.count8 = rs.getInt("Q8_7");
			info.count9 = rs.getInt("Q8_8");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問9.就業を決めたポイントの回答結果の取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param all
	 *            全体フラグ
	 * @param position
	 *            所属ID
	 * @param deptGroup
	 *            部署グループ
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static Map<String, OmronChoiceDto> getQ9Map(Connection conn,
			boolean all, String position, String deptGroup) throws SQLException {
		Map<String, OmronChoiceDto> map = new LinkedHashMap<String, OmronChoiceDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("select COALESCE(");
		sb.append(all ? "V_DEPT_G1" : "V_DEPT_G2");
		sb.append(",'total') as 'dept',");
		sb.append("count(*) as 'total',");
		sb.append("count(case when V_Q9 like '%1%' and V_Q9 not like '%10%' then 1 else null end) as 'Q9_1',");
		sb.append("count(case when V_Q9 like '%2%' then 1 else null end) as 'Q9_2',");
		sb.append("count(case when V_Q9 like '%3%' then 1 else null end) as 'Q9_3',");
		sb.append("count(case when V_Q9 like '%4%' then 1 else null end) as 'Q9_4',");
		sb.append("count(case when V_Q9 like '%5%' then 1 else null end) as 'Q9_5',");
		sb.append("count(case when V_Q9 like '%6%' then 1 else null end) as 'Q9_6',");
		sb.append("count(case when V_Q9 like '%7%' then 1 else null end) as 'Q9_7',");
		sb.append("count(case when V_Q9 like '%8%' then 1 else null end) as 'Q9_8',");
		sb.append("count(case when V_Q9 like '%9%' then 1 else null end) as 'Q9_9',");
		sb.append("count(case when V_Q9 like '%10%' then 1 else null end) as 'Q9_10'");
		sb.append(" from staffenquete WHERE ");
		List<String> conditions = new ArrayList<String>();
		if (!"0".equals(position)) {
			conditions.add("V_POS = '" + position + "'");
		}
		if (StringUtil.isNotEmpty(deptGroup)) {
			conditions.add("V_DEPT_G1='" + deptGroup + "'");
		}
		conditions.add((all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " is not null and V_Q9 is not null");
		Statement stmt = conn.createStatement();
		String sql = sb.toString()
				+ StringUtil.concatWithDelimit(" AND ", conditions)
				+ " GROUP BY " + (all ? "V_DEPT_G1" : "V_DEPT_G2")
				+ " WITH ROLLUP;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String key = rs.getString("dept");
			OmronChoiceDto info = new OmronChoiceDto();
			info.count1 = rs.getInt("total");
			info.count2 = rs.getInt("Q9_1");
			info.count3 = rs.getInt("Q9_2");
			info.count4 = rs.getInt("Q9_3");
			info.count5 = rs.getInt("Q9_4");
			info.count6 = rs.getInt("Q9_5");
			info.count7 = rs.getInt("Q9_6");
			info.count8 = rs.getInt("Q9_7");
			info.count9 = rs.getInt("Q9_8");
			info.count10 = rs.getInt("Q9_9");
			info.count11 = rs.getInt("Q9_10");
			map.put(key, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		return map;
	}

	/**
	 * 設問9fa. オムロンパーソネルでの就業を決めたポイントFAの回答結果の取得(派遣先別)
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param position
	 *            所属ID
	 * @return　回答結果
	 * @throws SQLException
	 */
	public static List<OmronEnqueteDto> getQ9FaListByDept(Connection conn,
			String position) throws SQLException {
		List<OmronEnqueteDto> list = new ArrayList<OmronEnqueteDto>();
		String sql = "SELECT V_Q9_FA FROM `staffenquete` WHERE V_POS = '"
				+ position + "' and V_Q9_FA is not null;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			OmronEnqueteDto info = new OmronEnqueteDto();
			info.q9_fa = rs.getString("V_Q9_FA");
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

}
