package jp.co.freedom.master.utilities.ltt;

import java.util.HashMap;
import java.util.Map;

/**
 * LTT向け定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class LTTConstants {

	/** 業種 */
	final public static Map<String, String> BIZ_CATEGORIES = createBizCategories();

	/** 役職 */
	final public static Map<String, String> DEPT_CATEGORIES = createDeptCategories();

	/** 職種 */
	final public static Map<String, String> SYKUSYU_CATEGORIES = createSykusyuCategories();

	/** 関与 */
	final public static Map<String, String> KANYO_CATEGORIES = createKanyoCategories();

	/** 来場目的 */
	final public static Map<String, String> PURPOSE_CATEGORIES = createPurposeCategories();

	/** 読取不可のサブバーコードMAP */
	final public static Map<String, String> SUB_BARCODES_MAP = createSubBarcodeConverter();

	/**
	 * 出展者用業種<b>Map</b>の初期化
	 * 
	 * @return 出展者用業種<b>Map</b>
	 */
	private static Map<String, String> createBizCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "食品");
		map.put("2", "パルプ・紙");
		map.put("3", "化学");
		map.put("4", "医薬品");
		map.put("5", "アパレル製品");
		map.put("6", "鉄鋼");
		map.put("7", "非鉄金属");
		map.put("8", "輸送用機器");
		map.put("9", "精密機器");
		map.put("10", "電気機器");
		map.put("11", "物流用機器");
		map.put("12", "工作機械");
		map.put("13", "電池・発電機");
		map.put("14", "その他製造業");
		map.put("15", "小売業");
		map.put("16", "卸売業・商社");
		map.put("17", "通販業");
		map.put("18", "物流業(トラック輸送)");
		map.put("19", "物流業(鉄道)");
		map.put("20", "物流業(海運)");
		map.put("21", "物流業(空運)");
		map.put("22", "物流業(倉庫)");
		map.put("23", "物流業(物流子会社)");
		map.put("24", "その他の物流業");
		map.put("25", "サービス");
		map.put("26", "電力・ガス");
		map.put("27", "金融・保険");
		map.put("28", "不動産");
		map.put("29", "情報・通信");
		map.put("30", "水産・農林・鉱業");
		map.put("31", "建設");
		map.put("32", "大学");
		map.put("33", "団体・研究機関");
		map.put("34", "官庁・地方自治体・公団・公社");
		map.put("35", "その他非製造業");
		return map;
	}

	/**
	 * 役職<b>Map</b>の初期化
	 * 
	 * @return 役職<b>Map</b>
	 */
	private static Map<String, String> createDeptCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "経営者・役員");
		map.put("2", "部長・次長");
		map.put("3", "課長");
		map.put("4", "係長・主任");
		map.put("5", "社員・職員");
		map.put("6", "その他");
		return map;
	}
	
	/**
	 * 職種<b>Map</b>の初期化
	 * 
	 * @return 職種<b>Map</b>
	 */
	private static Map<String, String> createSykusyuCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "経営企画・社長室");
		map.put("2", "ロジスティクス");
		map.put("3", "物流・流通管理");
		map.put("4", "営業(物流事業者)");
		map.put("5", "営業(製造業・販売業)");
		map.put("6", "環境・CSR");
		map.put("7", "包装研究・開発");
		map.put("8", "包装設計");
		map.put("9", "購買・資材");
		map.put("10", "生産・設計・開発");
		map.put("11", "技術・研究");
		map.put("12", "企画");
		map.put("13", "情報システム");
		map.put("14", "エンジニアリング");
		map.put("15", "広報・宣伝");
		map.put("16", "マーケティング");
		map.put("17", "総務・人事・経理");
		map.put("18", "その他");
		return map;
	}

	
	/**
	 * 関与<b>Map</b>の初期化
	 * 
	 * @return 関与<b>Map</b>
	 */
	private static Map<String, String> createKanyoCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "購入決定権をもっている");
		map.put("2", "決定権はないが購入に関与している");
		map.put("3", "関与していない");
		return map;
	}

	/**
	 * 来場目的<b>Map</b>の初期化
	 * 
	 * @return 来場目的<b>Map</b>
	 */
	private static Map<String, String> createPurposeCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "製品購入のため");
		map.put("2", "導入予定の製品・サービスの絞込みを行うため");
		map.put("3", "新製品・サービスに興味があるから");
		map.put("4", "業務で役立つ情報を探すため");
		map.put("5", "新しい取引先を探すため");
		map.put("6", "人の交流を図るため");
		map.put("7", "教育・研修の一環で");
		map.put("8", "次回の出展を検討するため");
		return map;
	}
	private static Map<String, String> createSubBarcodeConverter() {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}
	
}