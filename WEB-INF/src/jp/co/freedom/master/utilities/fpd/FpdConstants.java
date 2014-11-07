package jp.co.freedom.master.utilities.fpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FPD向け定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdConstants {

	/** 都道府県コード */
	public static Map<String, String> ADDR1_MAP = createAddr1Map();

	/* リクエストコード対応表 */
	/** 株式会社アルバック向けリクエストコード */
	public static Map<String, String> REQUESTCODES_ULVAC = createRequestCodesMapForULVAC();

	/** エーケーティー 株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_AKT = createRequestCodesMapForAKT();

	/** ナノサイエンス株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_NANOSCIENCE = createRequestCodesMapForNanoScience();

	/** JSR株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_JSR = createRequestCodesMapForJSR();

	/** 長州産業株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_CYOUSYUU = createRequestCodesMapForCYOUSYUU();

	/** デリセリアルズ株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_DEXERIALS = createRequestCodesMapForDexerials();

	/** 株式会社豊島製作所向けリクエストコード */
	public static Map<String, String> REQUESTCODES_TOYOSHIMA = createRequestCodesMapForToyoshima();

	/** 株式会社タカトリ向けリクエストコード */
	public static Map<String, String> REQUESTCODES_TAKATORI = createRequestCodesMapForTakatori();

	/** 株式会社半導体エネルギー研究所向けリクエストコード */
	public static Map<String, String> REQUESTCODES_SEL = createRequestCodesMapForSEL();

	/** パナソニック株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_PANASONIC = createRequestCodesMapForPanasonic();

	/** 株式会社シーケービー向けリクエストコード */
	public static Map<String, String> REQUESTCODES_CKB = createRequestCodesMapForCKB();

	/* リーダー端末対応リスト */
	/** 株式会社アルバック　バーコードリーダーリスト */
	public static List<String> READER_ULVAC_LIST = createReaderForUlvac();

	/** エーケーティー株式会社 バーコードリーダーリスト */
	public static List<String> READER_AKT_LIST = createReaderForAKT();

	/** ナノサイエンス株式会社 バーコードリーダーリスト */
	public static List<String> READER_NANOSCIENCE_LIST = createReaderForNanoscience();

	/** 尾池工業　株式会社 バーコードリーダーリスト */
	public static List<String> READER_OIKEKOGYO_LIST = createReaderForOikekogyo();

	/** クリーンサアフェイス技術　株式会社 バーコードリーダーリスト */
	public static List<String> READER_CSTH_LIST = createReaderForCsth();

	/** JSR株式会社 バーコードリーダーリスト */
	public static List<String> READER_JSR_LIST = createReaderForJsr();

	/** 株式会社　ジェービーエム バーコードリーダーリスト */
	public static List<String> READER_JBM_LIST = createReaderForJbm();

	/** タッチパネル・システムズ株式会社 バーコードリーダーリスト */
	public static List<String> READER_TPS_LIST = createReaderForTps();

	/** 長州産業　株式会社 バーコードリーダーリスト */
	public static List<String> READER_CYOUSYUU_LIST = createReaderForCyouSyuu();

	/** デクセリアルズ　株式会社 バーコードリーダーリスト */
	public static List<String> READER_DEXERIALS_LIST = createReaderForDexerials();

	/** 東京応化工業　株式会社 バーコードリーダーリスト */
	public static List<String> READER_TOK_LIST = createReaderForTok();

	/** 東レエンジニアリング株式会社 バーコードリーダーリスト */
	public static List<String> READER_TORAYEN_LIST = createReaderForTorayen();

	/** 株式会社　豊島製作所 バーコードリーダーリスト */
	public static List<String> READER_TOYOSHIMA_LIST = createReaderForToyoshima();

	/** 長瀬産業　株式会社 バーコードリーダーリスト */
	public static List<String> READER_NAGASE_LIST = createReaderForNagase();

	/** 日東電工　株式会社 バーコードリーダーリスト */
	public static List<String> READER_NITTO_LIST = createReaderForNitto();

	/** 平井工業　株式会社 バーコードリーダーリスト */
	public static List<String> READER_HIRAKEN_LIST = createReaderForHiraken();

	/** ヘンケルジャパン株式会社 バーコードリーダーリスト */
	public static List<String> READER_HENKEL_LIST = createReaderForHenkel();

	/** メルク株式会社 バーコードリーダーリスト */
	public static List<String> READER_MERCK_LIST = createReaderForMerck();

	/** エヌピー・ディー・ジャパン株式会社 バーコードリーダーリスト */
	public static List<String> READER_NPDJAPAN_LIST = createReaderForNpdjapan();

	/** 日本ゼオン株式会社 バーコードリーダーリスト */
	public static List<String> READER_ZEON_LIST = createReaderForZeon();

	/** 株式会社　タッチパネル研究所 バーコードリーダーリスト */
	public static List<String> READER_TOUCHPANEL_LIST = createReaderForTouchpanel();

	/** 株式会社 ブライト バーコードリーダーリスト */
	public static List<String> READER_BRIGHT_LIST = createReaderForBright();

	/** キヤノントッキ　株式会社 バーコードリーダーリスト */
	public static List<String> READER_CANONTOKKI_LIST = createReaderForCanontokki();

	/** 株式会社　タカトリ バーコードリーダーリスト */
	public static List<String> READER_TAKATORI_LIST = createReaderForTakatori();

	/** 株式会社　半導体エネルギー研究所 バーコードリーダーリスト */
	public static List<String> READER_SEL_LIST = createReaderForSel();

	/** パナソニック　株式会社 バーコードリーダーリスト */
	public static List<String> READER_PANASONIC_LIST = createReaderForPanasonic();

	/** 株式会社　シーケービー バーコードリーダーリスト */
	public static List<String> READER_CKB_LIST = createReaderForCkb();

	/** エイソンテクノロジー株式会社 バーコードリーダーリスト */
	public static List<String> READER_ASON_LIST = createReaderForAson();

	/**
	 * 株式会社アルバック向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社アルバック向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForUlvac() {
		List<String> reader = new ArrayList<String>();
		reader.add("4138");
		return reader;
	}

	/**
	 * エーケーティー株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return エーケーティー株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForAKT() {
		List<String> reader = new ArrayList<String>();
		reader.add("4047");
		reader.add("4099");
		return reader;
	}

	/**
	 * ナノサイエンス株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return ナノサイエンス株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForNanoscience() {
		List<String> reader = new ArrayList<String>();
		reader.add("4073");
		return reader;
	}

	/**
	 * 尾池工業　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 尾池工業　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForOikekogyo() {
		List<String> reader = new ArrayList<String>();
		reader.add("4034");
		return reader;
	}

	/**
	 * クリーンサアフェイス技術　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return クリーンサアフェイス技術　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForCsth() {
		List<String> reader = new ArrayList<String>();
		reader.add("4069");
		return reader;
	}

	/**
	 * JSR株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return JSR株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForJsr() {
		List<String> reader = new ArrayList<String>();
		reader.add("4198");
		return reader;
	}

	/**
	 * 株式会社　ジェービーエム向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　ジェービーエム向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForJbm() {
		List<String> reader = new ArrayList<String>();
		reader.add("4190");
		return reader;
	}

	/**
	 * タッチパネル・システムズ株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return タッチパネル・システムズ株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForTps() {
		List<String> reader = new ArrayList<String>();
		reader.add("4040");
		reader.add("4089");
		reader.add("4115");
		reader.add("4136");
		return reader;
	}

	/**
	 * 長州産業　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 長州産業　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForCyouSyuu() {
		List<String> reader = new ArrayList<String>();
		reader.add("4066");
		reader.add("4154");
		return reader;
	}

	/**
	 * デクセリアルズ　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return デクセリアルズ　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForDexerials() {
		List<String> reader = new ArrayList<String>();
		reader.add("4012");
		reader.add("4018");
		reader.add("4132");
		return reader;
	}

	/**
	 * 東京応化工業　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 東京応化工業　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForTok() {
		List<String> reader = new ArrayList<String>();
		reader.add("4065");
		reader.add("4128");
		return reader;
	}

	/**
	 * 東レエンジニアリング株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 東レエンジニアリング株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForTorayen() {
		List<String> reader = new ArrayList<String>();
		reader.add("4038");
		return reader;
	}

	/**
	 * 株式会社　豊島製作所向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　豊島製作所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForToyoshima() {
		List<String> reader = new ArrayList<String>();
		reader.add("4045");
		return reader;
	}

	/**
	 * 長瀬産業　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 長瀬産業　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForNagase() {
		List<String> reader = new ArrayList<String>();
		reader.add("4072");
		return reader;
	}

	/**
	 * 日東電工　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 日東電工　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForNitto() {
		List<String> reader = new ArrayList<String>();
		reader.add("4079");
		reader.add("4092");
		return reader;
	}

	/**
	 * 平井工業　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 平井工業　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForHiraken() {
		List<String> reader = new ArrayList<String>();
		reader.add("4121");
		return reader;
	}

	/**
	 * ヘンケルジャパン株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return ヘンケルジャパン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForHenkel() {
		List<String> reader = new ArrayList<String>();
		reader.add("4064");
		return reader;
	}

	/**
	 * メルク株式会社 向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return メルク株式会社 向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForMerck() {
		List<String> reader = new ArrayList<String>();
		reader.add("4174");
		return reader;
	}

	/**
	 * エヌピー・ディー・ジャパン株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return エヌピー・ディー・ジャパン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForNpdjapan() {
		List<String> reader = new ArrayList<String>();
		reader.add("4007");
		reader.add("4044");
		return reader;
	}

	/**
	 * 日本ゼオン株式会社 向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 日本ゼオン株式会社 向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForZeon() {
		List<String> reader = new ArrayList<String>();
		reader.add("4056");
		return reader;
	}

	/**
	 * 株式会社　タッチパネル研究所向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　タッチパネル研究所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForTouchpanel() {
		List<String> reader = new ArrayList<String>();
		reader.add("4088");
		return reader;
	}

	/**
	 * 株式会社 ブライト向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社 ブライト向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForBright() {
		List<String> reader = new ArrayList<String>();
		reader.add("4086");
		return reader;
	}

	/**
	 * キヤノントッキ　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return キヤノントッキ　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForCanontokki() {
		List<String> reader = new ArrayList<String>();
		reader.add("4106");
		reader.add("4177");
		return reader;
	}

	/**
	 * 株式会社　タカトリ向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　タカトリ向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForTakatori() {
		List<String> reader = new ArrayList<String>();
		reader.add("4035");
		return reader;
	}

	/**
	 * 株式会社　半導体エネルギー研究所向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　半導体エネルギー研究所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForSel() {
		List<String> reader = new ArrayList<String>();
		reader.add("4027");
		reader.add("4143");
		reader.add("4205");
		reader.add("4208");
		return reader;
	}

	/**
	 * パナソニック　株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return パナソニック　株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForPanasonic() {
		List<String> reader = new ArrayList<String>();
		reader.add("4028");
		return reader;
	}

	/**
	 * 株式会社　シーケービー向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 株式会社　シーケービー向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForCkb() {
		List<String> reader = new ArrayList<String>();
		reader.add("4036");
		return reader;
	}

	/**
	 * エイソンテクノロジー株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return エイソンテクノロジー株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForAson() {
		List<String> reader = new ArrayList<String>();
		reader.add("4070");
		return reader;
	}

	/**
	 * 都道府県コード<b>Map</b>の初期化
	 * 
	 * @return 都道府県コード<b>Map</b>
	 */
	private static Map<String, String> createAddr1Map() {
		Map<String, String> addr1 = new HashMap<String, String>();
		addr1.put("0", "海外");
		addr1.put("1", "北海道");
		addr1.put("2", "青森県");
		addr1.put("3", "岩手県");
		addr1.put("4", "宮城県");
		addr1.put("5", "秋田県");
		addr1.put("6", "山形県");
		addr1.put("7", "福島県");
		addr1.put("8", "茨城県");
		addr1.put("9", "栃木県");
		addr1.put("10", "群馬県");
		addr1.put("11", "埼玉県");
		addr1.put("12", "千葉県");
		addr1.put("13", "東京都");
		addr1.put("14", "神奈川県");
		addr1.put("15", "新潟県");
		addr1.put("16", "富山県");
		addr1.put("17", "石川県");
		addr1.put("18", "福井県");
		addr1.put("19", "山梨県");
		addr1.put("20", "長野県");
		addr1.put("21", "岐阜県");
		addr1.put("22", "静岡県");
		addr1.put("23", "愛知県");
		addr1.put("24", "三重県");
		addr1.put("25", "滋賀県");
		addr1.put("26", "京都府");
		addr1.put("27", "大阪府");
		addr1.put("28", "兵庫県");
		addr1.put("29", "奈良県");
		addr1.put("30", "和歌山県");
		addr1.put("31", "鳥取県");
		addr1.put("32", "島根県");
		addr1.put("33", "岡山県");
		addr1.put("34", "広島県");
		addr1.put("35", "山口県");
		addr1.put("36", "徳島県");
		addr1.put("37", "香川県");
		addr1.put("38", "愛媛県");
		addr1.put("39", "高知県");
		addr1.put("40", "福岡県");
		addr1.put("41", "佐賀県");
		addr1.put("42", "長崎県");
		addr1.put("43", "熊本県");
		addr1.put("44", "大分県");
		addr1.put("45", "宮崎県");
		addr1.put("46", "鹿児島県");
		addr1.put("47", "沖縄県");
		return addr1;
	}

	/**
	 * 株式会社アルバック向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社アルバック向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForULVAC() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "来場メモあり");
		requests.put("002", "対応：アルバック販売");
		requests.put("003", "対応：UJ FPD・PV事業部");
		requests.put("004", "対応：UJ マテリアル事業部");
		requests.put("005", "対応：受付");
		requests.put("006", "対応：その他");
		requests.put("007", "(FPD・PV)FPD Solution");
		requests.put("008", "(FPD・PV)Organic EL Solution");
		requests.put("009", "(FPD・PV)Inkjet System S-200");
		requests.put("010", "(FPD・PV)LTPS Solution");
		requests.put("011", "(マテ)低抵抗Cu配線プロセス");
		requests.put("012", "(マテ)IGZOターゲット");
		requests.put("013", "(マテ)ナノメタルインク");
		return requests;
	}

	/**
	 * エーケーティー 株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return エーケーティー 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForAKT() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "Display");
		requests.put("002", "Web");
		requests.put("003", "Both");
		return requests;
	}

	/**
	 * ナノサイエンス株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return ナノサイエンス株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForNanoScience() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "会社案内配布済");
		requests.put("002", "営業要対応");
		requests.put("006", "GDMSに興味あり");
		requests.put("007", "TOFに興味あり");
		requests.put("008", "SIMSに興味あり");
		requests.put("009", "TEMに興味あり");
		requests.put("010", "RBSに興味あり");
		requests.put("011", "パワー半導体・太陽電池");
		requests.put("012", "LED");
		requests.put("013", "フラットディスプレイ");
		requests.put("014", "有機膜");
		requests.put("015", "薄膜・コーティング");
		requests.put("019", "会社案内送付する");
		requests.put("020", "料金表を希望");
		return requests;
	}

	/**
	 * JSR株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return JSR株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForJSR() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "LCD関連全て");
		requests.put("002", "OPSTAR関連全て");
		requests.put("003", "OLED");
		requests.put("004", "Metal Ink/Photo-induced surface control material");
		requests.put("006", "LCD OPTMER CR Series");
		requests.put("007", "LCD OPTMER NN,PC,JTP Series");
		requests.put("008", "LCD OPTMER AL Series");
		requests.put("009", "OPSTAR Coating Materials");
		requests.put("010", "OPSTAR Coating Materials for Display");
		requests.put("011", "OPSTAR Coating Materials for TSP");
		requests.put("012", "OPSTAR Coating Materials for Chassis");
		return requests;
	}

	/**
	 * 長州産業株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 長州産業株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForCYOUSYUU() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "有機EL　量産／開発");
		requests.put("002", "MTSP　量産／開発");
		requests.put("003", "真空装置　一般");
		requests.put("004", "情報収集目的");
		requests.put("005", "その他");
		return requests;
	}

	/**
	 * デリセリアルズ株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return デリセリアルズ株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForDexerials() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "ヘンケル製品を使用している");
		requests.put("002", "ヘンケル製品を以前使用していた");
		requests.put("003", "ヘンケルを知らない");
		requests.put("005", "LOCA その他製品");
		requests.put("006", "LOCA 3800");
		requests.put("007", "LOCA 3198");
		requests.put("008", "LOCA クリーナー");
		requests.put("009", "LOCA 塗布・貼り合わせ");
		requests.put("010", "構造用PURホットメルト");
		requests.put("011", "構造用アクリル");
		requests.put("012", "構造用塗布機");
		requests.put("014", "email欲しい");
		return requests;
	}

	/**
	 * 株式会社豊島製作所向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社豊島製作所向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForToyoshima() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "新規");
		requests.put("002", "既存");
		return requests;
	}

	/**
	 * 株式会社タカトリ向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社タカトリ向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForTakatori() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "真空貼り合せシリーズ TPLｼﾘｰｽﾞ");
		requests.put("002", "OCA貼付装置 LPLｼﾘｰｽﾞ");
		requests.put("003", "パネル洗浄装置 LCSｼﾘｰｽﾞ");
		requests.put("004", "偏光板貼付装置 LPAｼﾘｰｽﾞ");
		requests.put("005", "その他装置 タッチパネル関連");
		requests.put("006", "その他装置 フィルム貼付関連");
		requests.put("007", "その他装置 OCR関連");
		requests.put("008", "その他装置 医療機器関連");
		requests.put("009", "その他装置 圧着、組立、検査 他");
		requests.put("010", "その他装置");
		requests.put("011", "訪問・お打合せ要望");
		requests.put("012", "テスト・デモ要望");
		requests.put("013", "情報収集");
		requests.put("015", "半導体分野");
		requests.put("016", "MWS分野");
		requests.put("017", "TAC分野");
		requests.put("018", "医療関連");
		requests.put("019", "LED関連");
		requests.put("020", "会社案内・総合カタログのみ");
		return requests;
	}

	/**
	 * 株式会社半導体エネルギー研究所向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社半導体エネルギー研究所向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForSEL() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "資料A(日本語)");
		requests.put("002", "資料B(英語)");
		requests.put("003", "送付希望(後日郵送)");
		requests.put("004", "資料D");
		requests.put("005", "資料E");
		requests.put("011", "Wanted配布");
		return requests;
	}

	/**
	 * パナソニック株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return パナソニック株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForPanasonic() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "訪問希望");
		requests.put("002", "電話希望");
		requests.put("003", "サンプル希望");
		requests.put("004", "正式見積／仕様書希望");
		requests.put("005", "概算見積／価格問合");
		requests.put("010", "至急対応");
		requests.put("011", "商品カタログ希望");
		requests.put("012", "簡易商品説明のみ");
		requests.put("018", "ProxStream 1エンコーダソリューション");
		requests.put("019", "ProXStream 14Kデコーダソリューション");
		requests.put("020", "CMOS 24GHz 電波センサ");
		return requests;
	}

	/**
	 * 株式会社CKB向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社CKB向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForCKB() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "金型用途");
		requests.put("002", "試作用途");
		requests.put("003", "その他用途／不明");
		requests.put("004", "商社、代理店、新聞社など");
		requests.put("005", "カタログ後日郵送");
		requests.put("007", "M1");
		requests.put("008", "M2");
		requests.put("009", "Mlab");
		requests.put("010", "X-line");
		requests.put("012", "商談希望");
		requests.put("013", "検討中/これから検討");
		requests.put("014", "試作屋があれば・・・");
		requests.put("015", "活用法模作中");
		requests.put("016", "平田");
		requests.put("017", "黒田");
		requests.put("018", "佐藤");
		requests.put("019", "鈴木");
		requests.put("020", "他社員");
		return requests;
	}
}