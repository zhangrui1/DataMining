package jp.co.freedom.master.utilities.jgas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JGAS向け定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JgasConstants {

	/** 都道府県コード */
	public static Map<String, String> ADDR1_MAP = createAddr1Map();

	/** 業種コード */
	public static Map<String, String> BIZ_MAP = createBizMap();

	/** 職種コード */
	public static Map<String, String> OCCP_MAP = createOccpMap();

	/** 国名 */
	public static Map<String, String> OUNTRY_MAP = createCountryMap();

	/* リクエストコード対応表 */
	/** Japan Color(一般社団法人　日本印刷産業機械工業会)向けリクエストコード */
	public static Map<String, String> REQUESTCODES_JAPANCOLOR = createRequestCodesMapForJapanColor();

	/** アイレック技建 株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_AIREC = createRequestCodesMapForAIREC();

	/** 株式会社プリントパック向けリクエストコード */
	public static Map<String, String> REQUESTCODES_PRINTPACK = createRequestCodesMapForPrintpack();

	/** 大日本スクリーン製造株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_SCREEN = createRequestCodesMapForScreen();

	/** 日本ボールドウィン株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_BALDWIN = createRequestCodesMapForBaldWin();

	/* リーダー端末対応リスト */
	/** アイレック技建 株式会社 バーコードリーダーリスト */
	public static List<String> READER_AIREC_LIST = createReaderForAIREC();

	/** Japan Color(一般社団法人　日本印刷産業機械工業会) バーコードリーダーリスト */
	public static List<String> READER_COLOR_LIST = createReaderForColor();

	/** 日本株式会社プリントパック　バーコードリーダーリスト */
	public static List<String> READER_PRINTPACK_LIST = createReaderForPrintPack();

	/** 大日本スクリーン製造株式会社　バーコードリーダーリスト */
	public static List<String> READER_SCREEN_LIST = createReaderForScreen();

	/** 日本ボールドウィン株式会社　バーコードリーダーリスト */
	public static List<String> READER_BALDWIN_LIST = createReaderForBaldWin();

	/**
	 * アイレック技建 株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return アイレック技建 株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForAIREC() {
		List<String> reader = new ArrayList<String>();
		reader.add("4122");
		reader.add("4059");
		return reader;
	}

	/**
	 * Japan Color(一般社団法人　日本印刷産業機械工業会)向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return Japan Color(一般社団法人　日本印刷産業機械工業会)向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForColor() {
		List<String> reader = new ArrayList<String>();
		reader.add("4064");
		return reader;
	}

	/**
	 * 日本株式会社プリントパック向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 日本株式会社プリントパック向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForPrintPack() {
		List<String> reader = new ArrayList<String>();
		reader.add("4121");
		reader.add("4048");
		reader.add("4078");
		reader.add("4090");
		reader.add("4190");
		reader.add("4152");
		return reader;
	}

	/**
	 * 大日本スクリーン製造株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 大日本スクリーン製造株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForScreen() {
		List<String> reader = new ArrayList<String>();
		reader.add("4123");
		reader.add("4019");
		return reader;
	}

	/**
	 * 日本ボールドウィン株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 日本ボールドウィン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForBaldWin() {
		List<String> reader = new ArrayList<String>();
		reader.add("4128");
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
	 * 国名<b>Map</b>の初期化
	 * 
	 * @return 国名<b>Map</b>
	 */
	private static Map<String, String> createCountryMap() {
		Map<String, String> country = new HashMap<String, String>();
		country.put("1", "日本");
		country.put("2", "海外");
		return country;
	}

	/** 職種<b>Map</b>における「その他」のID番号 */
	public static String OCCP_OTHER_NUM = "9";

	/**
	 * 職種<b>Map</b>の初期化
	 * 
	 * @return 職種<b>Map</b>
	 */
	private static Map<String, String> createOccpMap() {
		Map<String, String> occp = new HashMap<String, String>();
		occp.put("1", "経営者");
		occp.put("2", "経営企画");
		occp.put("3", "購買");
		occp.put("4", "生産");
		occp.put("5", "広報");
		occp.put("6", "営業");
		occp.put("7", "デザイン");
		occp.put("8", "研究・開発");
		occp.put("9", "その他");
		return occp;
	}

	/** 業種<b>Map</b>における「その他」のID番号 */
	public static String BIZ_OTHER_NUM = "6";

	/**
	 * 業種<b>Map</b>の初期化
	 * 
	 * @return 業種<b>Map</b>
	 */
	private static Map<String, String> createBizMap() {
		Map<String, String> biz = new HashMap<String, String>();
		biz.put("1", "企画・デザイン");
		biz.put("2", "プリプレス");
		biz.put("3", "印刷");
		biz.put("4", "製本・紙工");
		biz.put("5", "ベンダー");
		biz.put("6", "その他");
		return biz;
	}

	/**
	 * Japan Color(一般社団法人　日本印刷産業機械工業会)向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return Japan Color(一般社団法人　日本印刷産業機械工業会)向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForJapanColor() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "取得を検討");
		requests.put("002", "調査中");
		return requests;
	}

	/**
	 * アイレック技建 株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return アイレック技建 株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForAIREC() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "ブランクス （高）");
		requests.put("002", "ブランクス （低）");
		requests.put("003", "カード （高）");
		requests.put("004", "カード （低）");
		requests.put("005", "ラベルシール （高）");
		requests.put("006", "ラベルシール （低）");
		requests.put("007", "軟包装 （高）");
		requests.put("008", "軟包装 （低）");
		requests.put("009", "商業印刷 （高）");
		requests.put("010", "商業印刷 （低）");
		requests.put("011", "その他");
		return requests;
	}

	/**
	 * 株式会社プリントパック向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 株式会社プリントパック向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForPrintpack() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "New （会場）");
		requests.put("002", "New （帰）");
		requests.put("003", "Cus （会場）");
		requests.put("004", "Cus （帰）");
		return requests;
	}

	/**
	 * 大日本スクリーン製造株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 大日本スクリーン製造株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForScreen() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001",
				"UVi インクジェットラベルプリンティングシステム（ラベル印刷） Truepress JetL350UV");
		requests.put("002", "フルカラーバリアブルプリンティングシステム（連帳印刷） Truepress Jet 520ZZ");
		requests.put("003", "インライン検査システム JetInspection");
		requests.put("004", "ユニバーサルワークフロー EQUIOS（イクオス）");
		requests.put("005", "オンライン入稿/校正/承認システム EQUIOS Online Ver.2.1");
		requests.put("006", "クラウド バリアブル データ 制作システム バリアブルフロントエンドサービス");
		requests.put("007", "出力サンプル");
		requests.put("008", "エムティサービス東日本コーナー（サーバーやサポート商品）");
		requests.put("009", "プリンテッドエレクトロニクスコーナー");
		requests.put("010", "３Ｄプリンター");
		requests.put("011", "カタログがほしい");
		requests.put("012", "サンプルが欲しい");
		requests.put("013", "見積りが欲しい");
		requests.put("014", "詳しい説明に来てほしい");
		requests.put("020", "大日本スクリーン");
		return requests;
	}

	/**
	 * 日本ボールドウィン株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 日本ボールドウィン製造株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForBaldWin() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "LED－UV装置");
		requests.put("002", "ＵＶ照射 Green UV、Cool Arc");
		requests.put("003", "NM");
		requests.put("004", "NF");
		requests.put("005", "NR");
		requests.put("006", "プリパック");
		requests.put("007", "ブラン洗、圧洗");
		requests.put("008", "UVランプ");
		requests.put("009", "Q．I．");
		requests.put("010", "超音波洗浄");
		requests.put("011", "購入したい");
		requests.put("012", "カタログ欲しい");
		requests.put("013", "連絡欲しい");
		requests.put("014", "詳しい説明が聞きたい");
		requests.put("015", "全部のカタログが欲しい");
		requests.put("016", "その他");
		return requests;
	}

}