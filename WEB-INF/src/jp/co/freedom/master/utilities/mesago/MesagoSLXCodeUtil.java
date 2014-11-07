package jp.co.freedom.master.utilities.mesago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoSLXDto;

/**
 * 【MESAGO】SLXコードユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoSLXCodeUtil {

	/** 展示会名称：BWJ */
	public static final String BWJ = "BWJ/BWJW";

	/** 職種 */
	public static final Map<String, MesagoSLXDto> contactPostType = createContactPostTypeMap();

	/** 役職 */
	public static final Map<String, MesagoSLXDto> contactPostGroup = createContactPostGroupMap();

	/** 従業員数 */
	public static final Map<String, MesagoSLXDto> accOfStaff = createAccOfStaffMap();
	
	/** 【事前登録用】従業員数 */
	public static final Map<String, String> accOfStaffForPreRegist = createAccOfStaffMapForPreRegist();

	/** プレス業種区分SLXコード */
	private static final Map<String, String> pressBizMap = createPressBizMap();

	/** プレス専門分野SLXコード */
	private static final Map<String, String> pressSpecializedIn = createPressSpecializedInMap();

	/**
	 * 従業員数<b>Map</b>の初期化
	 *
	 * @return 従業員数<b>Map</b>
	 */
	private static Map<String, MesagoSLXDto> createAccOfStaffMap() {
		Map<String, MesagoSLXDto> map = new LinkedHashMap<String, MesagoSLXDto>();
		MesagoSLXDto info;

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("1名", "1");
		map.put("1", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("2名", "2");
		map.put("2", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("3～10名", "3");
		map.put("3", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("11～50名", "11");
		map.put("4", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("51～100名", "100");
		map.put("5", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("101～500名", "500");
		map.put("6", info);

		info = new MesagoSLXDto();
		info.setAccOfStaffInfo("501名以上", "501");
		map.put("7", info);

		return map;
	}
	

	/**
	 * 【事前登録用】従業員数<b>Map</b>の初期化
	 * 
	 * @return 従業員数<b>Map</b>
	 */
	private static Map<String, String> createAccOfStaffMapForPreRegist() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1名");
		map.put("2", "2名");
		map.put("3", "3～10名");
		map.put("11", "11～50名");
		map.put("100", "51～100名");
		map.put("500", "101～500名");
		map.put("501", "501名以上");
		return map;
	}

	/**
	 * 職種<b>Map</b>の初期化
	 *
	 * @return 職種<b>Map</b>
	 */
	private static Map<String, MesagoSLXDto> createContactPostTypeMap() {
		Map<String, MesagoSLXDto> map = new LinkedHashMap<String, MesagoSLXDto>();
		MesagoSLXDto info;

		/* BWJ */
		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("購買・仕入", "PT0");
		map.put("1", info);

		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("技術者", "PT1");
		map.put("2", info);

		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("営業・企画・マーケティング", "PT2");
		map.put("3", info);

		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("広報宣伝", "PT3");
		map.put("4", info);

		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("管理部門", "PT4");
		map.put("5", info);

		info = new MesagoSLXDto();
		info.setContactPostTypeInfo("その他", "PT9");
		map.put("6", info);

		/* ナノ・マイクロビジネス展 */
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("研究/開発", "PT6");
		// map.put("1", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("管理部門", "PT4");
		// map.put("2", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("購買・仕入れ", "PT0");
		// map.put("3", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("設計", "PT7");
		// map.put("4", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("技術", "PT1");
		// map.put("5", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("生産･製造", "PT8");
		// map.put("6", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("営業･企画･マーケティング", "PT2");
		// map.put("7", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("広報宣伝", "PT3");
		// map.put("8", info);
		//
		// info = new MesagoSLXDto();
		// info.setContactPostTypeInfo("その他", "PT9");
		// map.put("9", info);

		return map;
	}

	/**
	 * 役職<b>Map</b>の初期化
	 *
	 * @return 役職<b>Map</b>
	 */
	private static Map<String, MesagoSLXDto> createContactPostGroupMap() {
		Map<String, MesagoSLXDto> map = new LinkedHashMap<String, MesagoSLXDto>();
		MesagoSLXDto info;

		info = new MesagoSLXDto();
		info.setContactPostGroupInfo("経営者・役員", "PG0");
		map.put("1", info);

		info = new MesagoSLXDto();
		info.setContactPostGroupInfo("管理職", "PG1");
		map.put("2", info);

		info = new MesagoSLXDto();
		info.setContactPostGroupInfo("社員・職員", "PG2");
		map.put("3", info);

		info = new MesagoSLXDto();
		info.setContactPostGroupInfo("その他", "PG3");
		map.put("4", info);

		return map;

	}

	/**
	 * SLXコードもしくは登録券番号に対応する業種区分／専門分野を取得する
	 *
	 * @param exhibitionName
	 *            展示会名称
	 * @param code
	 *            SLXコード
	 * @param registNo
	 *            登録券番号
	 * @return 業種区分
	 * @throws SQLException
	 */
	public static Map<String, MesagoSLXDto> getSLXMap(Connection conn,
			String exhibitionName) throws SQLException {
		Map<String, MesagoSLXDto> map = new LinkedHashMap<String, MesagoSLXDto>();
		if (BWJ.equals(exhibitionName)) {
			String sql = "SELECT * FROM slx WHERE V_type=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, exhibitionName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				MesagoSLXDto info = new MesagoSLXDto();
				String registNo = rs.getString("V_Regist_No");
				info.bizSlx = rs.getString("V_BIZ_SLX"); // 業種区分
				info.bizSlxCode = rs.getString("V_BIZ_SLX_CODE"); // 業種区分SLXコード
				info.accSlx = rs.getString("V_Acc_SLX"); // 専門分野
				info.accSlxCode = rs.getString("V_Acc_SLX_CODE"); // 専門分野SLXコード
				map.put(registNo, info);
			}
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
		return map;
	}

	/**
	 * アンケート回答内容を基にプレス業種区分SLXコードを取得
	 *
	 * @param value
	 *            アンケート回答内容
	 * @return プレス業種区分SLXコード
	 */
	public static String getPressBizSLX(String value) {
		if (StringUtil.isNotEmpty(value)) {
			return "PrBN" + StringUtil.convertFixLengthFromInteger(value, 2);
		}
		return null;
	}

	/**
	 * アンケート回答内容を基にプレス職種区分SLXコードを取得
	 *
	 * @param value
	 *            アンケート回答内容
	 * @return プレス職種区分SLXコード
	 */
	public static String getPressSpecializedInSLX(String value) {
		if (StringUtil.isNotEmpty(value)) {
			return "PrSP" + StringUtil.convertFixLengthFromInteger(value, 2);
		}
		return null;
	}

	/**
	 * プレス業種区分SLXコードを基にプレス業種区分を取得
	 *
	 * @param slx
	 *            プレス業種区分SLXコード
	 * @return プレス業種区分
	 */
	public static String getPressBiz(String slx) {
		return pressBizMap.get(slx);
	}

	/**
	 * プレス職種区分SLXコードを基にプレス職種区分を取得
	 *
	 * @param slx
	 *            プレス職種区分SLXコード
	 * @return プレス職種区分
	 */
	public static String getPressSpecializedIn(String slx) {
		return pressSpecializedIn.get(slx);
	}

	/**
	 * プレス業種区分SLXコード<b>Map</b>の初期化
	 *
	 * @return プレス業種区分SLXコード<b>Map</b>
	 */
	private static Map<String, String> createPressBizMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("PrBN01", "テレビ局");
		map.put("PrBN02", "ウェブサイト運営会社");
		map.put("PrBN03", "出版社");
		map.put("PrBN04", "新聞社");
		map.put("PrBN05", "その他");
		map.put("PrBN06", "団体･協会");
		map.put("PrBN07", "フリーランス");
		map.put("PrBN08", "編集プロダクション");
		map.put("PrBN09", "広告代理店");
		map.put("PrBN10", "ラジオ局");
		map.put("PrBN11", "制作プロダクション");
		return map;
	}

	/**
	 * プレス専門分野SLXコード<b>Map</b>の初期化
	 *
	 * @return プレス専門分野SLXコード<b>Map</b>
	 */
	private static Map<String, String> createPressSpecializedInMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("PrSP01", "営業･企画");
		map.put("PrSP02", "カメラマン");
		map.put("PrSP03", "記者･ライター");
		map.put("PrSP04", "広告");
		map.put("PrSP05", "その他");
		map.put("PrSP06", "広報");
		map.put("PrSP07", "編集");
		map.put("PrSP08", "レポーター");
		map.put("PrSP09", "番組制作");
		map.put("PrSP10", "");
		map.put("PrSP11", "");
		map.put("PrSP12", "");
		map.put("PrSP13", "");
		return map;
	}

}
