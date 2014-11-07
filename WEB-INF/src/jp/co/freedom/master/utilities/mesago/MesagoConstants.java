package jp.co.freedom.master.utilities.mesago;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * MESAGO向け定数定義クラス
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoConstants {

	/** プレス業種区分 */
	final public static Map<String, String> PRESS_BIZ_CATEGORIES = createPressBizCategories();

	/** プレス職種区分 */
	final public static Map<String, String> PRESS_OCC_CATEGORIES = createPressOccCategories();

	/** 出展者用業種区分 */
	final public static Map<String, String> BIZ_CATEGORIES = createBizCategories();

	/** 従業員数 */
	final public static Map<String, String> ENTERPRISE_SCALE = createEnterpriseScale();

	/** 商品買い付け決定件 */
	final public static Map<String, String> DECIDE_CATEGORIES = createDecideCategories();

	/** 出展動機 */
	final public static Map<String, String> PURPOSE_CATEGORIES = createPurposeCategories();

	/** NG会社名リスト */
	final public static String NG_COMPANY_NAMES = "税理|税務|銀行|保険|信用金庫|信金|トレードショーオーガナイザーズ|TRADESHOW ORGANIZERS INC.|ビジネスガイド社|Business Guide-Sha|リード エグジビション|リード エグジビジョン ジャパン|リードエグジビションジャパン|リードエグジビションジャパン（株）|Reed Exhibitions|日本能率協会|Japan Management Association|日本インテリアファブリックス協会|Nippon Interior Fabrics Association|デザインアソシエーション|DESIGN ASSOCIATION NPO|大阪見本市協会|Osaka International Trade Fair Commission|ICSコンベンションデザイン|ICS Convention Design, Inc.|シー・エヌ・ティ|CNT Inc.|フジサンケイビジネスアイ|Fuji Sankei Business i|UBMジャパン|UBM Japan |UBMメディア|UBM Media|Reed ISG Japan KK|リードジャパン|一般（社）日本能率協会|一般社団法人日本能率協会";

	/** NG会社名：アテックス(株) */
	final public static String NG_COMPANY_NAME1 = "アテックス（株）";

	/** NG会社名：日本経済新聞社 */
	final public static String NG_COMPANY_NAME2 = "日本経済新聞社";

	/** 入力規則 */
	final public static Map<String, String> INPUT_REGULATION = createInputRegulation();

	/** 国名変換ルール */
	final public static Map<String, String> CONVERT_COUNTRY_NAME = createCountryNameConvertRule();

	/** 法人名の略称の変換ルール1 */
	final public static Map<String, String> CONVERT_COMPANY_SHORTNAME1 = createConvertCompanyShortName1();

	/** 法人名の略称の変換ルール2 */
	final public static Map<String, String> CONVERT_COMPANY_SHORTNAME2 = createConvertCompanyShortName2();

	/** 括弧を含まない法人名の略称リスト */
	final public static List<String> COMPANY_SHORTNAME_WITHOUT_BRACKET = createCompanyShortNameWithoutBracket();

	/** 事前業種リスト */
	final public static Map<String, Integer> PREENTRY_BIZ_TYPE_MAP = createPreentryBizTypeMap();

	/** 事前専門分野リスト */
	final public static List<String[]> PREENTRY_BIZ_CATEGORIES_LIST = createPreentryBizCategoriesList();

	/** プレスバーコードリスト */
	final public static List<String> PRESS_RFID_LIST = createPressRfidList();

	/** 2013バーコードリスト */
	final public static List<String> RFID_2013_LIST = create2013RfidList();

	/** 読取不可のサブバーコードMAP */
	final public static Map<String, String> SUB_BARCODES_MAP = createSubBarcodeConverter();

	/** 読取不可のサブバーコードMAP(逆変換) */
	final public static Map<String, String> SUB_BARCODES_CONVERT_MAP = createSubBarcodeConverter2();

	private static Map<String, String> createSubBarcodeConverter() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("6103411", "6101242");
		map.put("5300963", "5304112");
		map.put("5705791", "5603261");
		map.put("5801484", "5800737");
		map.put("5106629", "1055033");
		map.put("5106702", "1061652");
		map.put("5402497", "1059485");
		map.put("5301354", "5302842");
		map.put("5404549", "1060082");
		map.put("5410088", "5410243");
		map.put("1054698", "5901447");
		map.put("5013307", "5013424");
		map.put("5006522", "5006464");
		map.put("5006720", "5006778");
		map.put("5310164", "5310478");
		map.put("5111961", "1069428");
//		map.put("1065991", "7001108");
//		map.put("1065881", "7001105");
		map.put("7001106", "1063569");
		map.put("7001107", "1064397");
		map.put("5004280", "5004386");
		map.put("7010450", "7010445");
		map.put("5101976", "5406765");
		map.put("5101977", "5406764");
		map.put("5310344", "5310342");
		map.put("5007193", "5710712");
		map.put("5007414", "6111685");
		map.put("5106588", "1060964");
		map.put("5310326", "5303309");
		map.put("5105692", "5102922");
		map.put("5303007", "5405436");
		map.put("5800820", "5800512");
		map.put("5107010", "5901086");
		map.put("5009605", "1052830");
		map.put("5105757", "5710372");
		map.put("5004315", "1062236");
		map.put("5302771", "1065101");
		map.put("5012862", "1064823");
		map.put("5001850", "1055166");
		map.put("5015102", "5015380");
		map.put("5015101", "5603073");
		map.put("5301945", "5410363");
		map.put("5009894", "5009387");
		map.put("5302982", "1055139");
		map.put("5015251", "1071169");
		map.put("5002404", "5010615");
		map.put("5009148", "5010724");
		map.put("5009016", "5005833");
		map.put("5005264", "5900520");
		map.put("5001811", "5010501");
		map.put("5102853", "1058275");
		map.put("5700490", "5700490");
		map.put("7004880", "5003269");
		map.put("5005283", "5011262");
		map.put("5310717", "5700799");
		map.put("5013218", "5013221");
		map.put("5701129", "1065629");
		map.put("5410159", "1071086");
		map.put("5006299", "1053884");
		map.put("5410158", "1070964");
		map.put("5410162", "1071079");
		map.put("5002036", "1070958");
		map.put("1061622", "5110266");
		map.put("5006801", "1057221");
		map.put("1059278", "5302030");
		map.put("5004848", "1065460");
		map.put("5111456", "1065890");
		map.put("5302697", "1062463");
		map.put("5102165", "1058785");
		map.put("5006719", "1056941");
		map.put("5106231", "1054350");
		map.put("5006229", "5011064");
		map.put("5311297", "5311265");
		map.put("5404746", "5404990");
		map.put("5007558", "6114095");
		map.put("5010877", "1068723");
		map.put("5310689", "5311065");
		map.put("6112311", "6111148");
		map.put("5211689", "5301549");
		map.put("7002277", "5017701");
		map.put("7002238", "5406709");
		map.put("5017092", "1063268");
		map.put("5111200", "5310287");
		map.put("5310205", "5310282");
		map.put("5410596", "5410532");
		map.put("5015909", "5016376");
		map.put("5016377", "1066788");
		map.put("5410558", "1058020");
		map.put("5017456", "5016348");
		map.put("5302719", "5311085");
		map.put("5110539", "1059675");
		return map;
	}

	private static Map<String, String> createSubBarcodeConverter2() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("6101242", "6103411");
		map.put("5304112", "5300963");
		map.put("5603261", "5705791");
		map.put("5800737", "5801484");
		map.put("1055033", "5106629");
		map.put("1061652", "5106702");
		map.put("1059485", "5402497");
		map.put("5302842", "5301354");
		map.put("1060082", "5404549");
		map.put("5410243", "5410088");
		map.put("5901447", "1054698");
		map.put("5013424", "5013307");
		map.put("5006464", "5006522");
		map.put("5006778", "5006720");
		map.put("5310478", "5310164");
		map.put("1069428", "5111961");
//		map.put("7001108", "1065991");
//		map.put("7001105", "1065881");
		map.put("1063569", "7001106");
		map.put("1064397", "7001107");
		map.put("5004386", "5004280");
		map.put("7010445", "7010450");
		map.put("5406765", "5101976");
		map.put("5406764", "5101977");
		map.put("5310342", "5310344");
		map.put("5710712", "5007193");
		map.put("6111685", "5007414");
		map.put("1060964", "5106588");
		map.put("5303309", "5310326");
		map.put("5102922", "5105692");
		map.put("5405436", "5303007");
		map.put("5800512", "5800820");
		map.put("5901086", "5107010");
		map.put("1052830", "5009605");
		map.put("5710372", "5105757");
		map.put("1062236", "5004315");
		map.put("1065101", "5302771");
		map.put("1064823", "5012862");
		map.put("1055166", "5001850");
		map.put("5015380", "5015102");
		map.put("5603073", "5015101");
		map.put("5410363", "5301945");
		map.put("5009387", "5009894");
		map.put("1055139", "5302982");
		map.put("1071169", "5015251");
		map.put("5010615", "5002404");
		map.put("5010724", "5009148");
		map.put("5005833", "5009016");
		map.put("5900520", "5005264");
		map.put("5010501", "5001811");
		map.put("1058275", "5102853");
		map.put("5700490", "5700490");
		map.put("5003269", "7004880");
		map.put("5011262", "5005283");
		map.put("5700799", "5310717");
		map.put("5013221", "5013218");
		map.put("1065629", "5701129");
		map.put("1071086", "5410159");
		map.put("1053884", "5006299");
		map.put("1070964", "5410158");
		map.put("1071079", "5410162");
		map.put("1070958", "5002036");
		map.put("5110266", "1061622");
		map.put("1057221", "5006801");
		map.put("5302030", "1059278");
		map.put("1065460", "5004848");
		map.put("1065890", "5111456");
		map.put("1062463", "5302697");
		map.put("1058785", "5102165");
		map.put("1056941", "5006719");
		map.put("1054350", "5106231");
		map.put("5011064", "5006229");
		map.put("5311265", "5311297");
		map.put("5404990", "5404746");
		map.put("6114095", "5007558");
		map.put("1068723", "5010877");
		map.put("5311065", "5310689");
		map.put("6111148", "6112311");
		map.put("5301549", "5211689");
		map.put("5017701", "7002277");
		map.put("5406709", "7002238");
		map.put("1063268", "5017092");
		map.put("5310287", "5111200");
		map.put("5310282", "5310205");
		map.put("5410532", "5410596");
		map.put("5016376", "5015909");
		map.put("1066788", "5016377");
		map.put("1058020", "5410558");
		map.put("5016348", "5017456");
		map.put("5311085", "5302719");
		map.put("1059675", "5110539");
		return map;
	}

	/**
	 * 2013バーコードリストの初期化
	 *
	 * @return 2013バーコードリスト
	 */
	private static List<String> create2013RfidList() {
		List<String> rfid = new ArrayList<String>();
		rfid.add("5303512");
		rfid.add("0002103");
		rfid.add("1022570");
		rfid.add("1021168");
		rfid.add("5301457");
		rfid.add("1020618");
		rfid.add("1014469");
		rfid.add("7001207");
		rfid.add("5304807");
		rfid.add("0003853");
		rfid.add("5007230");
		rfid.add("5007418");
		rfid.add("5405164");
		rfid.add("5007429");
		return rfid;
	}

	/**
	 * 出展者納品用 業種の変換ルール<b>Map</b>の初期化
	 *
	 * @return 業種の変換ルール<b>Map</b>
	 */
	private static Map<String, Integer> createPreentryBizTypeMap() {
		Map<String, Integer> types = new HashMap<String, Integer>();
		types.put("問屋･商社", 1);
		types.put("輸入業･商社", 2);
		types.put("小売関係", 3);
		types.put("メーカー", 4);
		return types;
	}

	/**
	 * 出展者納品用 専門分野リスト<b>Map</b>の初期化
	 *
	 * @return 専門分野のリスト<b>Map</b>
	 */
	private static List<String[]> createPreentryBizCategoriesList() {
		List<String[]> allCategoies = new LinkedList<String[]>();
		// 共通
		String[] cateory0 = { "専業エステティックサロン", "専業ネイルサロン", "専業理美容サロン",
				"兼業・併設のサロン", "スパ、温泉", "ホテル、旅館、レジャー施設関係者", "医療関係", "学生",
				"協会、団体関係者", "その他" };
		// 問屋・商社（国内商材専門）
		String[] cateory1 = { "化粧品", "美容機器", "理美容関連", "健康・ダイエット関連", "美容関連雑貨",
				"その他" };
		// 輸入業・商社（海外商材専門）
		String[] cateory2 = { "化粧品", "美容機器", "理美容関連", "健康・ダイエット関連", "美容関連雑貨",
				"その他" };
		// 小売関係
		String[] cateory3 = { "百貨店", "量販店、コンビニエンスストア",
				"通販、訪販、TVショッピング、インターネットショッピング", "セレクトショップ、化粧品専門店", "ドラッグストア",
				"その他" };
		// メーカー
		String[] cateory4 = { "化粧品", "美容機器", "健康･ダイエット関連", "医薬品", "スパ関連",
				"パッケージ", "美容関連雑貨", "その他" };

		allCategoies.add(cateory0);
		allCategoies.add(cateory1);
		allCategoies.add(cateory2);
		allCategoies.add(cateory3);
		allCategoies.add(cateory4);

		return allCategoies;
	}

	/**
	 * 国名の変換ルール<b>Map</b>の初期化
	 *
	 * @return 国名の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createCountryNameConvertRule() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("South Korea", "S. Korea");
		map.put("United Kingdom", "U.K.");
		map.put("United States", "U.S.A.");
		map.put("日本", "Japan");
		return map;
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
		map.put("4", "セミナー・フォーラム・プレゼンテーションなどへの参加");
		map.put("5", "その他");
		return map;
	}

	/**
	 * 出展者用業種区分<b>Map</b>の初期化
	 *
	 * @return 出展者用業種区分<b>Map</b>
	 */
	private static Map<String, String> createBizCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "専業エステティックサロン");
		map.put("2", "専業ネイルサロン");
		map.put("3", "専業理美容サロン");
		map.put("4", "兼業・併設のサロン");
		map.put("5", "スパ、温泉");
		map.put("6", "ホテル、旅館、レジャー施設関係者");
		map.put("7", "医療関係");
		map.put("8", "問屋･商社-化粧品");
		map.put("9", "問屋･商社-美容機器");
		map.put("10", "問屋･商社-理美容関連");
		map.put("11", "問屋･商社-健康・ダイエット関連");
		map.put("12", "問屋･商社-美容関連雑貨");
		map.put("13", "問屋･商社-その他");
		map.put("14", "輸入業･商社-化粧品");
		map.put("15", "輸入業･商社-美容機器");
		map.put("16", "輸入業･商社-理美容関連");
		map.put("17", "輸入業･商社-健康・ダイエット関連");
		map.put("18", "輸入業･商社-美容関連雑貨");
		map.put("19", "輸入業･商社-その他");
		map.put("20", "小売関係-百貨店");
		map.put("21", "小売関係-量販店、コンビニエンスストア");
		map.put("22", "小売関係-通販、訪販、TVショッピング、インターネットショッピング");
		map.put("23", "小売関係-セレクトショップ、化粧品専門店");
		map.put("24", "小売関係-ドラッグストア");
		map.put("25", "小売関係-その他");
		map.put("26", "メーカー-化粧品");
		map.put("27", "メーカー-美容機器");
		map.put("28", "メーカー-健康・ダイエット関連製品");
		map.put("29", "メーカー-医薬品");
		map.put("30", "メーカー-スパ関連");
		map.put("31", "メーカー-パッケージ");
		map.put("32", "メーカー-美容関連雑貨");
		map.put("33", "メーカー-その他");
		map.put("34", "その他");

		return map;
	}

	/**
	 * 法人名の略称の変換ルール<b>Map</b>1の初期化
	 *
	 * @return 法人名の略称の変換ルール<b>Map</b>1
	 */
	private static Map<String, String> createConvertCompanyShortName1() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("\\(株\\)", "（株）");
		map.put("株\\)", "（株）");
		map.put("\\(有\\)", "（有）");
		map.put("\\(名\\)", "（名）");
		map.put("\\(資\\)", "（資）");
		map.put("\\(同\\)", "（同）");
		map.put("\\(財\\)", "（財）");
		map.put("\\(一財\\)", "（一財）");
		map.put("\\(公財\\)", "（公財）");
		map.put("\\(社\\)", "（社）");
		map.put("\\(一社\\)", "（一社）");
		map.put("\\(公社\\)", "（公社）");
		map.put("\\(医\\)", "（医）");
		map.put("\\(学\\)", "（学）");
		map.put("\\(独\\)", "（独）");
		return map;
	}

	/**
	 * 法人名の略称の変換ルール<b>Map</b>2の初期化
	 *
	 * @return 法人名の略称の変換ルール<b>Map</b>2
	 */
	private static Map<String, String> createConvertCompanyShortName2() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("株式会社", "（株）");
		map.put("株式會社", "（株）");
		map.put("有限会社", "（有）");
		map.put("合名会社", "（名）");
		map.put("合資会社", "（資）");
		map.put("合同会社", "（同）");
		map.put("一般財団法人", "（一財）");
		map.put("公益財団法人", "（公財）");
		map.put("一般社団法人", "（一社）");
		map.put("公益社団法人", "（公社）");
		map.put("公益（社）", "（公社）");
		map.put("一（社）", "（一社）");
		map.put("一般（社）", "（一社）");
		map.put("一（財）", "（一財）");
		map.put("一般（財）", "（一財）");
		map.put("公（社）", "（公社）");
		map.put("公（財）", "（公財）");
		map.put("財団法人", "（財）");
		map.put("社団法人", "（社）");
		map.put("医療法人", "（医）");
		map.put("学校法人", "（学）");
		map.put("独立行政法人", "（独）");
		return map;
	}

	/**
	 * 括弧を含まない法人名の略称<b>List</b>の初期化
	 *
	 * @return 括弧を含まない法人名の略称<b>List</b>
	 */
	private static List<String> createCompanyShortNameWithoutBracket() {
		List<String> list = new ArrayList<String>();
		list.add("株");
		list.add("有");
		list.add("名");
		list.add("資");
		list.add("同");
		list.add("財");
		list.add("一財");
		list.add("公財");
		list.add("社");
		list.add("一社");
		list.add("公社");
		list.add("医");
		list.add("学");
		list.add("独");
		return list;
	}

	/**
	 * プレスバーコードリスト<b>List</b>の初期化
	 *
	 * @return BWJ1次納品データに削除すべきプレスバーコードリスト<b>List</b>
	 */
	private static List<String> createPressRfidList() {
		List<String> list = new ArrayList<String>();
		list.add("6101968");
		list.add("6101969");
		list.add("6101970");
		list.add("6102012");
		list.add("6102013");
		list.add("6102014");
		list.add("6102015");
		list.add("6102016");
		list.add("6102017");
		list.add("6102018");
		list.add("6102019");
		list.add("6102020");
		list.add("6102042");
		list.add("6102043");
		list.add("6102044");
		list.add("6102045");
		list.add("6102046");
		list.add("6102047");
		list.add("6102048");
		list.add("6102049");
		list.add("6102050");
		list.add("6102093");
		list.add("6102097");
		list.add("6102099");
		list.add("6102100");
		return list;
	}

	/**
	 * 従業員数<b>Map</b>の初期化
	 *
	 * @return 従業員数<b>Map</b>
	 */
	private static Map<String, String> createEnterpriseScale() {
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
	 * 入力規則<b>Map</b>の初期化
	 *
	 * @return 入力規則<b>Map</b>
	 */
	private static Map<String, String> createInputRegulation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("", "");
		return map;
	}

	/**
	 * プレス業種区分<b>Map</b>の初期化
	 *
	 * @return プレス業種区分<b>Map</b>
	 */
	private static Map<String, String> createPressBizCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("テレビ局", "PrBN01");
		map.put("ウェブサイト運営会社", "PrBN02");
		map.put("出版社", "PrBN03");
		map.put("新聞社", "PrBN04");
		map.put("その他", "PrBN05");
		map.put("団体･協会", "PrBN06");
		map.put("フリーランス", "PrBN07");
		map.put("編集プロダクション", "PrBN08");
		map.put("広告代理店", "PrBN09");
		map.put("ラジオ局", "PrBN10");
		map.put("制作プロダクション", "PrBN11");
		return map;
	}

	/**
	 * プレス職種区分<b>Map</b>の初期化
	 *
	 * @return プレス職種区分<b>Map</b>
	 */
	private static Map<String, String> createPressOccCategories() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("営業･企画", "PrSP01");
		map.put("カメラマン", "PrSP02");
		map.put("記者･ライター", "PrSP03");
		map.put("広告", "PrSP04");
		map.put("その他", "PrSP05");
		map.put("広報", "PrSP06");
		map.put("編集", "PrSP07");
		map.put("レポーター", "PrSP08");
		map.put("番組制作", "PrSP09");
		return map;
	}

	/**
	 * 最終データ出力用CSVのヘッダーの生成
	 *
	 * @return 最終データ出力用CSVのヘッダーを収録した<b>List</b>
	 */
	public static List<String> createOutputHeaderForFinalDLData() {
		List<String> header = new ArrayList<String>();
		header.add("Account");
		header.add("AccountLocalName");
		header.add("AccountPronounciation");
		header.add("Address1");
		header.add("Address2");
		header.add("Address3");
		header.add("City");
		header.add("State");
		header.add("Zip");
		header.add("Country");
		header.add("LAddress1");
		header.add("LAddress2");
		header.add("LAddress3");
		header.add("LCity");
		header.add("LState");
		header.add("LZip");
		header.add("LCountry");
		header.add("TelCtryCode");
		header.add("TelAreaCode");
		header.add("Tel");
		header.add("Tel2CtryCode");
		header.add("Tel2AreaCode");
		header.add("Tel2");
		header.add("FaxCtryCode");
		header.add("FaxAreaCode");
		header.add("Fax");
		header.add("FaxExt");
		header.add("Fax2CtryCode");
		header.add("Fax2AreaCode");
		header.add("Fax2");
		header.add("Fax2Ext");
		header.add("Email");
		header.add("WebSite");
		header.add("BusinessNature");
		header.add("Markets");
		header.add("Industry");
		header.add("BrandName");
		header.add("ProductCategories");
		header.add("ProductSubcategories");
		header.add("ProductDetails");
		header.add("ProductOthers");
		header.add("EndUse");
		header.add("Brands");
		header.add("VisitorInterests");
		header.add("VisitorPromotionCode");
		header.add("JobNatures");
		header.add("InformationSources");
		header.add("EventHistory");
		header.add("SourceCode");
		header.add("LeadSource");
		header.add("Prefix");
		header.add("FirstName");
		header.add("MiddleName");
		header.add("LastName");
		header.add("LContactlName");
		header.add("LContactPronounciation");
		header.add("Department");
		header.add("LDepartment");
		header.add("Title");
		header.add("LTitle");
		header.add("ContactPersonTelCntyCode");
		header.add("ContactPersonTelAreaCode");
		header.add("ContactPersonTel");
		header.add("ContactPersonTelExt");
		header.add("ContactPersonTel2CntyCode");
		header.add("ContactPersonTel2AreaCode");
		header.add("ContactPersonTel2");
		header.add("ContactPersonTel2Ext");
		header.add("ContactPersonFaxCntyCode");
		header.add("ContactPersonFaxAreaCode");
		header.add("ContactPersonFax");
		header.add("ContactPersonFaxExt");
		header.add("ContactPersonFax2CntyCode");
		header.add("ContactPersonFax2AreaCode");
		header.add("ContactPersonFax2");
		header.add("ContactPersonFax2Ext");
		header.add("MobCtryCode");
		header.add("MobAreaCode");
		header.add("Mobile");
		header.add("ContactPersonEmail");
		header.add("ContactPersonEmail2");
		header.add("Sex");
		header.add("DoNotMail (T/F)");
		header.add("DoNotFax (T/F)");
		header.add("DoNotPhone (T/F)");
		header.add("DoNotEmail (T/F)");
		header.add("JoinMethod");
		header.add("PartnerName");
		header.add("BoothType");
		header.add("Product Name");
		header.add("Hall");
		header.add("Booth");
		header.add("Opensides");
		header.add("Width");
		header.add("Depth");
		header.add("Pillar");
		header.add("BoothSize");
		header.add("RawSize");
		header.add("FreeSize");
		header.add("Qty");
		header.add("Remark");
		header.add("PilotRecBy");
		header.add("PilotRecDate");
		header.add("PilotSndDate");
		header.add("PilotWebLink");
		header.add("PilotRemark");
		header.add("LeadSourceID (or Abbrev)");
		header.add("LeadDate");
		header.add("ExhibitionID (or Exh Code)");
		header.add("Provider");
		header.add("AccountID");
		header.add("ContactID");
		header.add("OpportunityID");
		header.add("ExhibitBoothID");
		header.add("AccAddressID");
		header.add("CntAddressID");
		header.add("ReferenceNo");
		header.add("Type (V/E)");
		header.add("LanguageCode (SC/TC/J)");
		header.add("Owner (Hong Kong/China/Japan)");
		header.add("ForceUpdate (T/F)");
		header.add("LastUpdate");
		header.add("GroupName (Initial + YYYYMMDD)");
		header.add("Misc00 ContactBusinessNature (code)");
		header.add("Misc01 ContactSpecializedIn (code)");
		header.add("Misc02 DoNotSolicit (T)");
		header.add("Misc03 Account No of Staff (Actual Picklist Description Provided)");
		header.add("Misc04 Contact Type (VIsitorVIP)");
		header.add("Misc05 contact post type (code)");
		header.add("Misc06 contact post group (code)");
		header.add("Misc07 VisitorHistory Joined (T)");
		header.add("Misc08 VisitorHistory Registered (T)");
		header.add("Misc09 VisitorHistory Onsite(T)");
		header.add("Misc10 VisitorHistory FAX (T)");
		header.add("Misc11 VisitorHistory Admission paid (T)");
		header.add("Misc12 contact product categories visiting objective (Item number)");
		header.add("Misc13 contact product categories purchasing decisions (Item number)");
		header.add("Misc14 contact address description");
		header.add("Misc15 contact product categories job nature");
		header.add("Misc16 contact interested in exhibiting (T)");
		header.add("Misc17 ContactID");
		header.add("Misc18 Lpublication Name");
		header.add("Misc19 Press Publication Type");
		header.add("Misc20 Press Business Nature");
		header.add("Misc21 Press Specialized In");
		header.add("Misc22 Press Rank");
		header.add("Misc23 Invited");
		header.add("Misc24 Came");
		header.add("Misc25 ");
		header.add("Misc26 ");
		header.add("Misc27 ");
		header.add("Misc28 ");
		header.add("Misc29 ");
		header.add("Misc30 ");
		header.add("Misc31 ");
		header.add("Misc32 ");
		header.add("Misc33 ");
		header.add("Misc34 ");
		header.add("Misc35 ");
		header.add("Misc36 ");
		header.add("Misc37 ");
		header.add("Misc38 ");
		header.add("Misc39 ");
		header.add("Result ");
		header.add("原票状況種別");
		header.add("画像パス");
		return header;
	}

}