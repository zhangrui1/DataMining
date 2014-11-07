package jp.co.freedom.master.utilities.noma;

import java.util.HashMap;
import java.util.Map;

/**
 * NOMA向け定数定義クラス
 *
 * @author フリーダム・グループ
 *
 */
public class NomaConstants {

	/** Q1.勤務地区分 */
	final public static Map<String, String> BIZ_PLACES = createBizPlaces();

	/** Q2.勤務先区分(JPN) */
	final public static Map<String, String> BIZ_CATEGORIES_JPN = createBizCategoriesJpn();

	/** Q2.勤務先区分(EN) */
	final public static Map<String, String> BIZ_CATEGORIES_EN = createBizCategoriesEn();

	/** Q2-A.所属区分A(JPN) */
	final public static Map<String, String> DEPT_CATEGORIES_A_JPN = createDeptCategoriesA_Jpn();

	/** Q2-A.所属区分A(EN) */
	final public static Map<String, String> DEPT_CATEGORIES_A_EN = createDeptCategoriesA_En();

	/** Q2-B.所属区分B */
	final public static Map<String, String> DEPT_CATEGORIES_B = createDeptCategoriesB();

	/** 読取不可事前バーコード */
	final public static Map<String, String> UNREADABLE_PREENTRY_BARCODES = createUnreadablePreentryBarcodes();

	/**
	 * 勤務地区分の変換ルール<b>Map</b>の初期化
	 *
	 * @return 勤務地区分の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createBizPlaces() {
		Map<String, String> places = new HashMap<String, String>();
		places.put("1", "北海道地区");
		places.put("2", "東北地区");
		places.put("3", "関東地区");
		places.put("4", "中部地区");
		places.put("5", "近畿地区");
		places.put("6", "中国・四国地区");
		places.put("7", "九州・沖縄地区");
		places.put("8", "海外(Overseas)");
		return places;
	}

	/**
	 * 勤務先区分(JPN)の変換ルール<b>Map</b>の初期化
	 *
	 * @return 勤務先区分の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createBizCategoriesJpn() {
		Map<String, String> categories = new HashMap<String, String>();
		categories.put("1", "病院・クリニック関係者(医療従事者)");
		categories.put("2", "保健・福祉施設(老健施設・老人ホームなど)");
		categories.put("3", "在宅サービス");
		categories.put("4", "建築設計・施工関係");
		categories.put("5", "医療関連メーカー");
		categories.put("6", "医療関連ディーラー");
		categories.put("7", "官公庁・団体・教育機関");
		categories.put("8", "一般");
		return categories;
	}

	/**
	 * 勤務先区分(EN)の変換ルール<b>Map</b>の初期化
	 *
	 * @return 勤務先区分の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createBizCategoriesEn() {
		Map<String, String> categories = new HashMap<String, String>();
		categories.put("1", "Medical Profession");
		categories.put("2", "Dealers");
		categories.put("3", "Government,Association,Education");
		categories.put("4", "Manufacturer");
		categories.put("5", "Public Health/Wellare Concern");
		categories.put("6", "Trading Firm");
		categories.put("7", "Genral");
		categories.put("8", "Others");
		return categories;
	}

	/**
	 * 所属区分A(JPN)の変換ルール<b>Map</b>の初期化
	 *
	 * @return 所属区分A(JPN)の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createDeptCategoriesA_Jpn() {
		Map<String, String> categories = new HashMap<String, String>();
		categories.put("1", "経営・管理部門(理事長・院長・副院長・事務長・部長他)");
		categories.put("2", "診療部門(医長・医師他)");
		categories.put("3", "医療技術部門(薬剤・検査・放射線・リハビリ・栄養管理他)");
		categories.put("4", "看護部門(病棟・手術・外来・中材・教育他)");
		categories.put("5", "事務部門(医事・庶務人事・用度施設・会計経理・企画他)");
		categories.put("6", "医療情報システム部門");
		categories.put("7", "調剤薬局");
		categories.put("8", "病院・クリニックその他(ソーシャルワーカー・コンサルタント他)");
		return categories;
	}

	/**
	 * 所属区分A(EN)の変換ルール<b>Map</b>の初期化
	 *
	 * @return 所属区分A(EN)の変換ルール<b>Map</b>
	 */
	private static Map<String, String> createDeptCategoriesA_En() {
		Map<String, String> categories = new HashMap<String, String>();
		categories.put("1", "Doctor");
		categories.put("2", "Dealer");
		categories.put("3", "Hospital Management");
		categories.put("4", "Manufacturer");
		categories.put("5", "Treatment Technical");
		categories.put("6", "Public Servant");
		categories.put("7", "Medical Information System");
		categories.put("8", "Organization/Association");
		categories.put("9", "Nursing service");
		categories.put("10", "Researcher");
		categories.put("11", "Medical Office Worker");
		categories.put("12", "Others");
		return categories;
	}

	/**
	 * 所属区分Bの変換ルール<b>Map</b>の初期化
	 *
	 * @return 所属区分Bの変換ルール<b>Map</b>
	 */
	private static Map<String, String> createDeptCategoriesB() {
		Map<String, String> categories = new HashMap<String, String>();
		categories.put("1", "医療法人病院");
		categories.put("2", "国立病院機構・公立（自治体）病院");
		categories.put("3", "その他公的病院");
		categories.put("4", "学校法人・会社病院");
		categories.put("5", "その他の法人病院");
		categories.put("6", "個人病院");
		categories.put("7", "一般診療所・クリニック");
		categories.put("8", "歯科診療所");
		categories.put("9", "調剤薬局");
		categories.put("10", "その他");
		return categories;
	}

	/**
	 * 事前バーコードの変換ルール<b>Map</b>の初期化
	 *
	 * @return 事前バーコードの変換ルール<b>Map</b>
	 */
	private static Map<String, String> createUnreadablePreentryBarcodes() {
		Map<String, String> barcodes = new HashMap<String, String>();
		barcodes.put("321539", "206508");
		barcodes.put("321538", "205039");
		barcodes.put("322907", "200155");
		barcodes.put("322909", "203443");
		barcodes.put("322911", "206011");
		barcodes.put("322912", "206064");
		barcodes.put("322910", "205997");
		barcodes.put("322917", "208218");
		barcodes.put("322916", "201886");
		barcodes.put("322915", "203130");
		barcodes.put("320308", "206887");
		barcodes.put("320311", "203746");
		barcodes.put("320314", "208346");
		barcodes.put("320306", "206205");
		barcodes.put("320315", "208463");
		barcodes.put("320312", "205282");
		barcodes.put("320313", "204198");
		barcodes.put("320309", "202935");
		barcodes.put("320310", "200614");
		barcodes.put("510502", "208259");
		barcodes.put("510503", "208327");
		barcodes.put("510504", "208347");
		barcodes.put("510073", "205158");
		barcodes.put("510072", "202063");
		barcodes.put("510074", "201417");
		barcodes.put("520502", "202446");
		barcodes.put("610450", "200431");
		barcodes.put("700348", "205222");
		barcodes.put("700675", "205563");
		barcodes.put("701242", "208165");
		barcodes.put("701219", "208578");
		barcodes.put("701220", "201506");
		barcodes.put("111510", "208464");
		barcodes.put("112866", "207841");
		barcodes.put("112883", "200465");
		barcodes.put("112910", "201106");
		barcodes.put("112863", "205170");
		barcodes.put("112868", "204225");
		barcodes.put("112865", "200785");
		barcodes.put("112869", "207078");
		barcodes.put("120129", "203948");
		barcodes.put("120262", "208124");
		barcodes.put("120261", "203279");
		barcodes.put("181047", "200440");
		barcodes.put("181086", "201193");
		barcodes.put("181085", "207214");
		barcodes.put("181084", "204227");
		barcodes.put("181045", "205700");
		barcodes.put("181044", "201854");
		barcodes.put("181043", "206294");
		barcodes.put("181042", "201789");
		barcodes.put("181046", "205568");
		barcodes.put("181041", "205839");
		barcodes.put("140732", "200559");
		barcodes.put("140733", "200558");
		barcodes.put("140229", "207447");
		barcodes.put("311037", "202915");
		barcodes.put("311038", "200994");
		barcodes.put("311488", "204658");
		barcodes.put("311041", "206646");
		barcodes.put("311040", "208613");
		barcodes.put("311039", "208761");
		barcodes.put("310066", "204858");
		barcodes.put("310069", "204109");
		barcodes.put("310032", "201910");
		barcodes.put("310065", "207664");
		barcodes.put("310063", "204794");
		barcodes.put("310060", "208271");
		barcodes.put("310058", "206793");
		barcodes.put("310057", "206532");
		barcodes.put("310056", "206730");
		barcodes.put("310198", "203510");
		barcodes.put("310031", "206084");
		barcodes.put("310033", "207656");
		barcodes.put("310034", "206304");
		barcodes.put("310068", "206293");
		barcodes.put("310035", "206255");
		barcodes.put("310036", "207613");
		barcodes.put("310059", "208215");
		barcodes.put("310061", "208389");
		barcodes.put("151978", "201320");
		barcodes.put("152279", "206217");
		barcodes.put("152280", "200248");
		barcodes.put("150088", "200839");
		barcodes.put("150115", "202764");
		barcodes.put("150126", "207561");
		barcodes.put("150148", "202074");
		barcodes.put("150146", "203644");
		barcodes.put("150152", "201767");
		barcodes.put("150094", "205626");
		barcodes.put("150090", "207780");
		barcodes.put("150093", "202049");
		barcodes.put("150092", "203847");
		barcodes.put("150091", "207823");
		barcodes.put("150089", "208493");
		barcodes.put("800568", "208689");
		barcodes.put("803040", "208577");
		barcodes.put("803043", "202623");
		barcodes.put("803052", "208828");
		barcodes.put("803050", "208858");
		barcodes.put("803046", "205401");
		barcodes.put("620818", "208113");
		barcodes.put("620819", "207518");
		barcodes.put("620820", "206534");
		barcodes.put("804151", "206446");
		barcodes.put("803039", "208057");
		barcodes.put("803041", "208484");
		barcodes.put("803042", "207686");
		barcodes.put("803044", "202513");
		barcodes.put("803045", "208238");
		barcodes.put("803060", "208691");
		barcodes.put("803049", "208533");
		barcodes.put("803048", "208548");
		barcodes.put("803047", "208504");
		barcodes.put("800540", "207099");
		barcodes.put("800542", "205918");
		barcodes.put("800547", "202875");
		barcodes.put("800550", "208616");
		barcodes.put("800557", "207943");
		barcodes.put("800559", "208332");
		barcodes.put("800563", "204264");
		barcodes.put("800566", "208486");
		barcodes.put("800698", "201353");
		barcodes.put("800684", "204181");
		barcodes.put("800543", "201249");
		barcodes.put("800541", "208208");
		barcodes.put("800539", "200690");
		barcodes.put("800544", "205840");
		barcodes.put("800548", "208209");
		barcodes.put("800549", "207253");
		barcodes.put("800552", "203914");
		barcodes.put("800553", "204053");
		barcodes.put("800554", "207407");
		barcodes.put("800556", "206777");
		barcodes.put("800558", "207727");
		barcodes.put("800561", "201262");
		barcodes.put("800562", "207978");
		barcodes.put("800564", "207700");
		barcodes.put("800565", "208510");
		barcodes.put("800560", "200396");
		barcodes.put("800546", "207486");
		barcodes.put("170139", "208646");
		barcodes.put("170138", "208671");
		barcodes.put("170140", "205309");
		barcodes.put("130578", "202033");
		barcodes.put("130577", "202047");
		barcodes.put("130547", "205385");
		barcodes.put("800555", "208440");
		barcodes.put("801263", "210455");
		barcodes.put("801999", "209265");
		barcodes.put("801817", "210451");
		barcodes.put("801996", "208910");
		barcodes.put("803670", "205930");
		barcodes.put("803667", "206254");
		barcodes.put("800410", "210214");
		barcodes.put("800411", "210310");
		barcodes.put("800413", "208654");
		barcodes.put("801000", "201898");
		barcodes.put("800393", "210121");
		barcodes.put("800394", "210157");
		barcodes.put("800395", "206964");
		barcodes.put("800469", "202488");
		barcodes.put("801993", "210382");
		barcodes.put("801821", "201894");
		barcodes.put("801992", "210109");
		barcodes.put("800279", "209655");
		barcodes.put("801988", "202539");
		barcodes.put("801990", "209819");
		barcodes.put("801987", "202344");
		barcodes.put("801989", "205174");
		barcodes.put("801991", "205176");
		barcodes.put("800616", "209401");
		barcodes.put("802000", "210181");
		barcodes.put("804346", "206313");
		barcodes.put("804347", "206306");
		barcodes.put("804348", "205578");
		barcodes.put("801997", "203673");
		barcodes.put("801998", "200876");
		barcodes.put("801826", "209309");
		barcodes.put("801827", "204959");
		barcodes.put("801828", "205514");
		barcodes.put("801825", "205508");
		barcodes.put("801267", "209682");
		barcodes.put("801268", "204303");
		barcodes.put("801266", "207607");
		barcodes.put("801264", "207079");
		barcodes.put("801265", "200303");
		barcodes.put("801261", "209937");
		barcodes.put("801260", "204846");
		barcodes.put("801262", "203719");
		barcodes.put("802219", "208791");
		barcodes.put("311528", "209550");
		barcodes.put("311529", "209557");
		barcodes.put("311530", "209558");
		barcodes.put("11531", "209561");
		barcodes.put("311532", "209562");
		barcodes.put("315139", "210164");
		barcodes.put("310598", "209447");
		barcodes.put("511209", "203231");
		barcodes.put("310740", "206212");
		barcodes.put("310973", "203663");
		barcodes.put("310976", "205654");
		barcodes.put("310965", "207986");
		barcodes.put("310259", "210178");
		barcodes.put("310597", "209445");
		barcodes.put("315190", "206083");
		barcodes.put("315137", "208750");
		barcodes.put("315189", "210082");
		barcodes.put("315186", "206605");
		barcodes.put("315174", "209623");
		barcodes.put("315182", "206549");
		barcodes.put("315183", "207085");
		barcodes.put("311801", "200765");
		barcodes.put("311798", "201567");
		barcodes.put("315138", "202821");
		barcodes.put("182314", "207832");
		barcodes.put("182435", "201239");
		barcodes.put("182077", "202925");
		barcodes.put("170288", "210450");
		barcodes.put("170287", "209437");
		barcodes.put("170337", "201125");
		barcodes.put("160262", "203836");
		barcodes.put("700617", "200490");
		barcodes.put("700616", "205041");
		barcodes.put("700980", "206614");
		barcodes.put("131191", "207484");
		barcodes.put("110072", "202078");
		barcodes.put("110073", "209727");
		barcodes.put("110074", "202940");
		barcodes.put("111773", "208460");
		barcodes.put("112523", "207233");
		barcodes.put("152337", "209987");
		barcodes.put("520459", "210371");
		barcodes.put("520375", "210388");
		barcodes.put("520166", "210343");
		barcodes.put("322846", "209674");
		barcodes.put("321454", "208589");
		barcodes.put("321855", "203934");
		barcodes.put("610152", "209464");
		barcodes.put("610151", "201501");
		barcodes.put("510530", "210411");
		return barcodes;
	}

}