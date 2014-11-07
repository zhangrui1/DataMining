package jp.co.freedom.master.utilities.ilf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ILF向け定数定義クラス
 *
 * @author フリーダム・グループ
 *
 */
public class ILFMesagoConstants {

	/** 出展者用業種区分 */
	final public static Map<String, String> BIZ_CATEGORIES = createBizCategories();

	/** 従業員数 */
	final public static Map<String, String> ENTERPRISE_SCALE = createEnterpriseScale();

	/** 商品買い付け決定件 */
	final public static Map<String, String> DECIDE_CATEGORIES = createDecideCategories();

	/** 出展動機 */
	final public static Map<String, String> PURPOSE_CATEGORIES = createPurposeCategories();

	/** 次回以降の出展に興味がある */
	final public static Map<String, String> NEXT_EXHIBI_CATEGORIES = createNextExhibiCategories();

	/** 事前業種リスト */
	final public static Map<String, Integer> PREENTRY_BIZ_TYPE_MAP = createPreentryBizTypeMap();

	/** 事前専門分野リスト */
	final public static List<String[]> PREENTRY_BIZ_CATEGORIES_LIST = createPreentryBizCategoriesList();

	/**
	 * 出展者納品用 業種の変換ルール<b>Map</b>の初期化
	 *
	 * @return 業種の変換ルール<b>Map</b>
	 */
	private static Map<String, Integer> createPreentryBizTypeMap() {
		Map<String, Integer> types = new HashMap<String, Integer>();
		return types;
	}

	/**
	 * 出展者納品用 専門分野リスト<b>Map</b>の初期化
	 *
	 * @return 専門分野のリスト<b>Map</b>
	 */
	private static List<String[]> createPreentryBizCategoriesList() {
		List<String[]> allCategoies = new LinkedList<String[]>();
		String[] cateory0 = { "小売・専門店", "百貨店", "量販店 ", "通信販売 ", "卸・商社（国内商材専門）",
				"輸入業・商社（海外商材専門）", "デザイン事務所", "インテリアコーディネーター・デコレーター", "設計事務所",
				"建設・住宅関連", "ホテル", "レストラン", "カフェ", "バー", "メーカー", "官公庁・団体・大使館",
				"学生", "その他" };
		allCategoies.add(cateory0);
		return allCategoies;
	}

	/**
	 * 商品買い付け決定件<b>Map</b>の初期化
	 *
	 * @return 商品買い付け決定件<b>Map</b>
	 */
	private static Map<String, String> createDecideCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "決定権を持っている");
		map.put("2", "一部決定権を持っている");
		map.put("3", "決定に影響力を持っている");
		map.put("4", "決定権を持っていない");
		return map;
	}

	/**
	 * 商品買い付け決定件<b>Map</b>の初期化
	 *
	 * @return 商品買い付け決定件<b>Map</b>
	 */
	private static Map<String, String> createPurposeCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "製品の購入・発注");
		map.put("2", "製品購入のための情報収集");
		map.put("3", "市場・業界の動向調査");
		map.put("4", "特別展示への来場");
		map.put("5", "その他");
		return map;
	}

	/**
	 * ILF2014用 次回以降の出展に興味がある<b>Map</b>の初期化
	 *
	 * @return 次回以降の出展に興味がある<b>Map</b>
	 */
	private static Map<String, String> createNextExhibiCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "IFFT/インテリア ライフスタイル リビング(2014年11月)");
		map.put("2", "インテリア ライフスタイル(2015年6月)");
		return map;
	}

	/**
	 * 出展者用業種区分<b>Map</b>の初期化
	 *
	 * @return 出展者用業種区分<b>Map</b>
	 */
	private static Map<String, String> createBizCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "小売・専門店");
		map.put("2", "百貨店");
		map.put("3", "量販店");
		map.put("4", "通信販売");
		map.put("5", "卸・商社（国内商材専門）");
		map.put("6", "輸入業・商社（海外商材専門）");
		map.put("7", "デザイン事務所");
		map.put("8", "インテリアコーディネーター・デコレーター");
		map.put("9", "設計事務所");
		map.put("10", "建設・住宅関連");
		map.put("11", "カフェ");
		map.put("12", "バー");
		map.put("13", "ホテル");
		map.put("14", "レストラン");
		map.put("15", "メーカー");
		map.put("16", "官公庁・団体・大使館");
		map.put("17", "学生");
		map.put("18", "その他");
		return map;
	}

	/**
	 * 従業員数<b>Map</b>の初期化
	 *
	 * @return 従業員数<b>Map</b>
	 */
	private static Map<String, String> createEnterpriseScale() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("5", "1-5名");
		map.put("20", "6-22名");
		map.put("50", "21～50名");
		map.put("100", "51～100名");
		map.put("500", "101～500名");
		map.put("1000", "501～1000名");
		map.put("1001", "1001名以上");
		return map;
	}

}