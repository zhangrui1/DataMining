package jp.co.freedom.master.utilities.jeca;

import java.util.HashMap;
import java.util.Map;

/**
 * JECA向け定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JecaConstants {

	/** 法人名の略称の変換ルール1 */
	final public static Map<String, String> CONVERT_COMPANY_SHORTNAME1 = createConvertCompanyShortName1();

	/** 法人名の略称の変換ルール2 */
	final public static Map<String, String> CONVERT_COMPANY_SHORTNAME2 = createConvertCompanyShortName2();

	/** 業種カテゴリMAP(日本語版) */
	final public static Map<String, String> BIZ_JPN_CATEGORIES_MAP = createJpnBizCategoriesMap();

	/** 業種カテゴリMAP(英語版) */
	final public static Map<String, String> BIZ_ENG_CATEGORIES_MAP = createEngBizCategoriesMap();

	/** 業種カテゴリMAP(団体登録ユーザー) */
	final public static Map<String, String> BIZ_CATEGORIES_MAP_FOR_GROUP = createBizCategoriesMapForGroup();

	/** 業種詳細カテゴリ(日本語版)MAP */
	final public static Map<String, String> BIZ_JPN_CATEGORIES_DETAIL_MAP = createJpnBizCategoryDetailMap();

	/** 業種詳細カテゴリ(英語版)MAP */
	final public static Map<String, String> BIZ_ENG_CATEGORIES_DETAIL_MAP = createEngBizCategoryDetailMap();

	/** 読取不可のサブバーコードMAP */
	final public static Map<String, String> SUB_BARCODES_MAP = createSubBarcodeConverter();

	/**
	 * 法人名の略称の変換ルール<b>Map</b>1の初期化
	 * 
	 * @return 法人名の略称の変換ルール<b>Map</b>1
	 */
	private static Map<String, String> createConvertCompanyShortName1() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("\\（株\\）", "(株)");
		map.put("株\\）", "(株)");
		map.put("\\（有\\）", "(有)");
		map.put("\\（名\\）", "(名)");
		map.put("\\（資\\）", "(資)");
		map.put("\\（同\\）", "(同)");
		map.put("\\（財\\）", "(財)");
		map.put("\\（一財\\）", "(一財)");
		map.put("\\（公財\\）", "(公財)");
		map.put("\\（社\\）", "(社)");
		map.put("\\（一社\\）", "(一社)");
		map.put("\\（公社\\）", "(公社)");
		map.put("\\（医\\）", "(医)");
		map.put("\\（学\\）", "(学)");
		map.put("\\（独\\）", "(独)");
		return map;
	}

	/**
	 * 法人名の略称の変換ルール<b>Map</b>1の初期化
	 * 
	 * @return 法人名の略称の変換ルール<b>Map</b>2
	 */
	private static Map<String, String> createConvertCompanyShortName2() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("\\(株\\)", "株式会社");
		map.put("株\\)", "株式会社");
		map.put("\\(有\\)", "有限会社");
		map.put("\\(名\\)", "合名会社");
		map.put("\\(資\\)", "合資会社");
		map.put("\\(同\\)", "合同会社");
		map.put("\\(財\\)", "財団法人");
		map.put("\\(一財\\)", "一般財団法人");
		map.put("\\(公財\\)", "公益財団法人");
		map.put("\\(社\\)", "社団法人");
		map.put("\\(一社\\)", "一般社団法人");
		map.put("\\(公社\\)", "公益社団法人");
		map.put("\\(医\\)", "医療法人");
		map.put("\\(学\\)", "学校法人");
		map.put("\\(独\\)", "独立行政法人");
		// 特殊文字
		map.put("㈱", "株式会社");
		map.put("㈲", "有限会社");
		map.put("㈾", "合資会社");
		map.put("㈳", "社団法人");
		map.put("㈴", "合名会社");
		map.put("㈶", "財団法人");
		map.put("㈻", "学校法人");
		return map;
	}

	/**
	 * 業種カテゴリ(日本語)<b>Map</b>の初期化
	 * 
	 * @return 業種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createJpnBizCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1. 電気設備ユーザー");
		map.put("2", "2. 電気設備メーカー");
		map.put("3", "3. 電気設備機器販売業（ディーラー）");
		map.put("4", "4. 官公庁・設計事務所・協会・団体");
		map.put("5", "5. 学校・研究機関");
		map.put("6", "6. その他");
		return map;
	}

	/**
	 * 業種カテゴリ(英語)<b>Map</b>の初期化
	 * 
	 * @return 業種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createEngBizCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1) Electrical equipment USER");
		map.put("2", "2) Electrical equipment MANUFACTURER");
		map.put("3", "3) Electrical equipment SALES, DEALER");
		map.put("4", "4) Government and other public offices / Design office");
		map.put("5", "5) School / Academic institute");
		map.put("6", "6) Others");
		return map;
	}

	private static Map<String, String> createBizCategoriesMapForGroup() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("電気設備ユーザー", "1. 電気設備ユーザー");
		map.put("電気設備メーカー", "2. 電気設備メーカー");
		map.put("電気設備機器販売業（ディーラー）", "3. 電気設備機器販売業（ディーラー）");
		map.put("官公庁・設計事務所・協会・団体", "4. 官公庁・設計事務所・協会・団体");
		map.put("学校・研究機関", "5. 学校・研究機関");
		map.put("その他", "6. その他");
		return map;
	}

	/**
	 * 業種詳細カテゴリ日本語版<b>Map</b>の初期化
	 * 
	 * @return 業種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createJpnBizCategoryDetailMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("01", "a.電気工事業");
		map.put("02", "b.通信工事業");
		map.put("03", "c.建設・土木工事業");
		map.put("04", "d.電力・ガス・エネルギー供給業");
		map.put("05", "e.電鉄・バス等運輸交通業");
		map.put("06", "f.機械・金属製造組立業");
		map.put("07", "g.化学・石油・繊維・印刷・その他石油製品関連業");
		map.put("08", "h.食品・衛生・薬品製造業");
		map.put("09", "i.病院・医療・介護・医療関連業");
		map.put("10", "j.運輸・倉庫・流通関連業");
		map.put("11", "k.農業・水産業");
		map.put("12", "l.商業・金融・保険・不動産関連業");
		map.put("13", "m.電気設備保守・保全・メンテサービス業");
		map.put("14", "n.その他");
		map.put("15", "a.電気設備機器総合製造業");
		map.put("16", "b.素材（原料）製造業");
		map.put("17", "c.部品（材）製造業");
		map.put("18", "d.電気設備機器加工・組立業");
		map.put("19", "e.ＯＥＭ・ＯＤＭ（他社ブランド製品）製造業");
		map.put("20", "f.メーカー製品サービス業");
		map.put("21", "g.その他  ");
		map.put("22", "a.代理店・特約店");
		map.put("23", "b.商社・通販・流通業");
		map.put("24", "c.機器メーカー販売会社");
		map.put("25", "d.その他  ");
		map.put("26", "a.官公庁");
		map.put("27", "b.設計事務所");
		map.put("28", "c.コンサルタント会社");
		map.put("29", "d.協会・組合・関連団体");
		map.put("30", "a.大学生・大学院生");
		map.put("31", "b.高等専門学校生");
		map.put("32", "c.高校生");
		map.put("33", "d.専修学校生・専門学校生");
		map.put("34", "e.職業訓練校生");
		map.put("35", "f.その他学生");
		map.put("36", "g.教育・研究機関");
		map.put("37", "a.報道・情報伝達（新聞・出版・放送・通信)");
		map.put("38", "b.広報・広告・宣伝・企画");
		map.put("39", "c.その他");
		return map;
	}

	/**
	 * 業種詳細カテゴリ英語版<b>Map</b>の初期化
	 * 
	 * @return 業種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createEngBizCategoryDetailMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("01", "a) Electrical contractor");
		map.put("02", "b) Telecom contractor");
		map.put("03", "c) Construction, Civil engineering");
		map.put("04", "d) Power supply");
		map.put("05", "e) Traffic industry (railway, bus)");
		map.put("06", "f) Mechanic, Metal Manufacturing Assembly");
		map.put("07", "g) Chemical, Petroleum oil, Textile");
		map.put("08", "h) Food, Health, Medicine");
		map.put("09", "i) Hospital, Medical, Nursing service");
		map.put("10", "ｊ) Transportation, Warehousing, Distribution");
		map.put("11", "k) Agriculture, Fishery");
		map.put("12", "l) Trading, Finance, Insurance, Real estate");
		map.put("13", "m) Maintenance of electrical equipment");
		map.put("14", "n) Others (*please specify)");
		map.put("15", "a) Comprehensive manufacturer");
		map.put("16", "b) Material manufacturer");
		map.put("17", "c) Component manufacturer");
		map.put("18", "d) Processor, Assembly service");
		map.put("19", "e) OEM / OCM");
		map.put("20", "f) Service person at manufacturer");
		map.put("21", "g) Others (*please specify)");
		map.put("22", "a) Agent, Dealership");
		map.put("23", "b) Trade, Distributer, Catalog shipper");
		map.put("24", "c) Subsidiary supplier of manufacturer");
		map.put("25", "d) Others (*please specify)");
		map.put("26", "a) Government, Public office");
		map.put("27", "b) Architect office");
		map.put("28", "c) Consulting company");
		map.put("29", "d) Union, Association, Organization");
		map.put("30", "a) University / Graduate school students");
		map.put("31", "b) Technical college student");
		map.put("32", "c) High school student");
		map.put("33", "d) Vocational college student");
		map.put("34", "e) Job training school student");
		map.put("35", "f) Other student");
		map.put("36", "g) Education/ Research Institute");
		map.put("37", "a) Press (newspaper, publisher, broadcast)");
		map.put("38", "b) Promotion, Advertisement");
		map.put("39", "c) Others (*please specify) ");
		return map;
	}

	private static Map<String, String> createSubBarcodeConverter() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("2011400101", "1011011166");
		map.put("2012400047", "1011010396");
		map.put("2011400883", "1011000523");
		map.put("2011400781", "1011010985");
		map.put("2011400135", "1011011595");
		map.put("2011400780", "1011010991");
		map.put("2013402777", "1011009121");
		map.put("2013400189", "1011012086");
		map.put("2014401620", "1011000205");
		map.put("2011401142", "1011011180");
		map.put("2011401144", "1011011199");
		map.put("2011401143", "1011011194");
		map.put("2011400028", "1011001976");
		map.put("2013402916", "1011005179");
		map.put("2011400839", "1011400839");
		map.put("2011401739", "1011006150");
		map.put("2011400839", "1011009572");
		map.put("2011401740", "1011006144");
		map.put("2013402975", "1011010885");
		map.put("2011400846", "1011010982");
		map.put("2011401403", "1011009406");
		map.put("2016400082", "1011010704");
		map.put("2015405928", "1011006828");
		map.put("2012406540", "1011013079");
		map.put("2013401349", "1011013473");
		map.put("2013401348", "1011013493");
		map.put("2012406535", "1011010196");
		map.put("2012401139", "1011004889");
		map.put("2012401114", "1011007244");
		map.put("2011401010", "1011002309");
		map.put("2015403219", "1011007574");
		map.put("2011401093", "1011010614");
		map.put("2011401070", "1011000439");
		map.put("2015405204", "1011002876");
		map.put("2011401120", "1011007739");
		map.put("2011401014", "1011013284");
		map.put("2011400583", "1011009333");
		map.put("2011401330", "1011013496");
		map.put("2015405533", "1011012607");
		map.put("2013400789", "1011004958");
		map.put("2012400121", "1011000913");
		map.put("2011401314", "1011000152");
		map.put("2012400918", "1011006613");
		map.put("2012401144", "1011010907");
		map.put("2011401091", "1011004727");
		map.put("2012400524", "1011004873");
		map.put("2011400314", "1011010485");
		map.put("2012401130", "1011007251");
		map.put("2011401395", "1011012148");
		map.put("2011400188", "1011002503");
		map.put("2011401267", "1011014181");
		map.put("2012402210", "1011010734");
		map.put("2015403932", "1011014340");
		map.put("2016400047", "1011007128");
		map.put("2014401726", "1011012720");
		map.put("2012400418", "1011008692");
		map.put("2011401271", "1011014009");
		map.put("2011401627", "1011005779");
		map.put("2012401866", "1011001039");
		map.put("2015403728", "1011001268");
		map.put("2011401557", "1011001510");
		map.put("2012401932", "1011013300");
		map.put("2013401695", "1011012511");
		map.put("2013401686", "1011013672");
		map.put("2011400539", "1011012233");
		map.put("2011406019", "1011013658");
		map.put("2012405295", "1011004335");
		map.put("2014400253", "1012002356");
		map.put("2012406077", "1012004205");
		map.put("2012406077", "1012004205");
		map.put("2012406076", "1012002612");
		map.put("2011401316", "2015405517");
		return map;
	}

}