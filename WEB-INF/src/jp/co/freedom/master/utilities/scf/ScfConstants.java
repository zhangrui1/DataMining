package jp.co.freedom.master.utilities.scf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SCF向け定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfConstants {

	/** 都道府県コード */
	public static Map<String, String> ADDR1_MAP = createAddr1Map();

	/** 国コード */
	public static Map<String, String> COUNTRY_MAP = createCountryMap();

	/* 業種 */
	/** 業種(SCF) */
	public static Map<String, String> scfBusinessCategoryMap = createScfBusinessCategoryMap();

	/** 業種(ロボット展) */
	public static Map<String, String> robotBusinessCategoryMap = createRobotBusinessCategoryMap();

	/* 職種 */
	/** 職種(SCF) */
	public static Map<String, String> scfOccupationCategoryMap = createScfOccupationCategoryMap();

	/** 職種(ロボット展) */
	public static Map<String, String> robotOccupationCategoryMap = createRobotOccupationCategoryMap();

	/* リクエストコード対応表 */
	/** 株式会社アルバック向けリクエストコード */
	public static Map<String, String> REQUESTCODES_ENDO = createRequestCodesMapForEndo();

	/* リーダー端末対応リスト */
	/** 遠藤工業株式会社　バーコードリーダーリスト */
	public static List<String> READER_ENDO_LIST = createReaderForEndo();

	/* ブース番号 */
	/** バーコードReaderIDとブース番号の対応 */
	public static Map<String, String> BOOTH_MAP = createBoothMap();

	/**
	 * ブース番号<b>Map</b>の初期化
	 * 
	 * @return ブース番号<b>Map</b>
	 */
	private static Map<String, String> createBoothMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("4001", "s-46");
		map.put("4004", "s-16");
		map.put("4005", "c-02");
		map.put("4006", "m-04");
		map.put("4007", "m-50");
		map.put("4008", "c-01");
		map.put("4009", "c-02");
		map.put("4010", "m-06");
		map.put("4011", "s-54");
		map.put("4012", "s-61");
		map.put("4013", "c-02");
		map.put("4014", "c-02");
		map.put("4015", "s-38");
		map.put("4016", "s-65");
		map.put("4017", "c-01");
		map.put("4018", "n-03");
		map.put("4019", "s-65");
		map.put("4021", "c-02");
		map.put("4022", "c-01");
		map.put("4023", "m-25");
		map.put("4024", "m-53");
		map.put("4025", "s-05");
		map.put("4026", "s-09");
		map.put("4027", "s-67");
		map.put("4028", "n-03");
		map.put("4029", "c-02");
		map.put("4031", "c-02");
		map.put("4032", "s-29");
		map.put("4033", "s-09");
		map.put("4034", "s-65");
		map.put("4036", "s-61");
		map.put("4037", "s-13");
		map.put("4038", "n-04");
		map.put("4039", "c-01");
		map.put("4040", "s-14");
		map.put("4042", "c-01");
		map.put("4043", "m-04");
		map.put("4044", "s-14");
		map.put("4045", "s-82");
		map.put("4046", "s-13");
		map.put("4047", "s-67");
		map.put("4048", "c-01");
		map.put("4050", "m-27");
		map.put("4051", "s-02");
		map.put("4052", "m-04");
		map.put("4054", "m-06");
		map.put("4055", "m-53");
		map.put("4057", "c-02");
		map.put("4058", "c-02");
		map.put("4059", "s-13");
		map.put("4062", "s-10");
		map.put("4063", "m-53");
		map.put("4064", "s-82");
		map.put("4065", "s-01");
		map.put("4066", "m-50");
		map.put("4067", "c-04");
		map.put("4068", "s-38");
		map.put("4069", "s-14");
		map.put("4072", "s-70");
		map.put("4073", "s-55");
		map.put("4074", "s-05");
		map.put("4077", "s-55");
		map.put("4078", "s-24");
		map.put("4079", "s-54");
		map.put("4080", "c-01");
		map.put("4081", "c-01");
		map.put("4082", "m-29");
		map.put("4083", "c-01");
		map.put("4084", "n-04");
		map.put("4086", "s-61");
		map.put("4087", "c-01");
		map.put("4088", "s-01");
		map.put("4089", "s-61");
		map.put("4090", "c-02");
		map.put("4091", "m-33");
		map.put("4092", "s-70");
		map.put("4093", "c-01");
		map.put("4095", "c-06");
		map.put("4096", "m-16");
		map.put("4097", "s-24");
		map.put("4098", "s-38");
		map.put("4099", "n-05");
		map.put("4101", "s-27");
		map.put("4102", "s-05");
		map.put("4104", "c-02");
		map.put("4105", "s-29");
		map.put("4106", "n-05");
		map.put("4107", "s-13");
		map.put("4108", "c-04");
		map.put("4109", "s-13");
		map.put("4110", "s-44");
		map.put("4111", "c-01");
		map.put("4112", "c-01");
		map.put("4113", "m-53");
		map.put("4114", "s-27");
		map.put("4115", "s-78");
		map.put("4116", "c-02");
		map.put("4117", "s-01");
		map.put("4118", "c-04");
		map.put("4119", "c-02");
		map.put("4120", "s-46");
		map.put("4121", "s-62");
		map.put("4122", "c-02");
		map.put("4124", "c-01");
		map.put("4126", "c-02");
		map.put("4128", "n-06");
		map.put("4129", "c-02");
		map.put("4130", "s-29");
		map.put("4131", "s-16");
		map.put("4132", "n-06");
		map.put("4134", "c-01");
		map.put("4136", "s-65");
		map.put("4137", "m-33");
		map.put("4138", "n-07");
		map.put("4140", "c-02");
		map.put("4141", "m-25");
		map.put("4142", "s-42");
		map.put("4143", "s-78");
		map.put("4144", "s-05");
		map.put("4145", "m-16");
		map.put("4146", "c-01");
		map.put("4147", "s-02");
		map.put("4148", "s-65");
		map.put("4149", "s-13");
		map.put("4150", "s-42");
		map.put("4151", "s-01");
		map.put("4152", "s-52");
		map.put("4153", "s-01");
		map.put("4154", "s-70");
		map.put("4156", "s-27");
		map.put("4157", "s-55");
		map.put("4158", "c-01");
		map.put("4160", "m-34");
		map.put("4161", "s-65");
		map.put("4163", "s-10");
		map.put("4165", "m-27");
		map.put("4166", "c-02");
		map.put("4167", "c-02");
		map.put("4168", "c-02");
		map.put("4169", "c-06");
		map.put("4170", "m-23");
		map.put("4172", "c-01");
		map.put("4173", "m-04");
		map.put("4174", "s-62");
		map.put("4175", "c-01");
		map.put("4178", "m-34");
		map.put("4179", "s-05");
		map.put("4180", "s-13");
		map.put("4181", "c-02");
		map.put("4182", "s-65");
		map.put("4183", "s-52");
		map.put("4184", "s-44");
		map.put("4185", "c-02");
		map.put("4189", "s-20");
		map.put("4191", "s-11");
		map.put("4192", "c-01");
		map.put("4193", "s-01");
		map.put("4195", "m-23");
		map.put("4196", "m-29");
		map.put("4197", "s-05");
		map.put("4198", "m-50");
		map.put("4199", "c-02");
		map.put("4200", "s-11");
		map.put("4201", "m-06");
		map.put("4202", "c-02");
		map.put("4203", "s-13");
		map.put("4205", "s-62");
		map.put("4206", "s-20");
		map.put("4207", "c-01");
		map.put("4208", "s-62");
		map.put("4209", "s-13");
		map.put("4211", "c-02");
		map.put("4300", "s-13");
		map.put("5002", "s-73");
		map.put("5004", "p-01");
		map.put("5032", "s-78");
		map.put("5033", "p-01");
		map.put("5048", "s-73");
		map.put("5063", "n-08");
		map.put("5066", "s-73");
		map.put("5081", "s-73");
		map.put("5086", "n-08");
		map.put("5093", "m-04");
		return map;
	}

	/**
	 * 遠藤工業株式会社向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 遠藤工業株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForEndo() {
		List<String> reader = new ArrayList<String>();
		reader.add("4191");
		reader.add("4200");
		return reader;
	}

	/**
	 * 国コード<b>Map</b>の初期化
	 * 
	 * @return 国コード<b>Map</b>
	 */
	private static Map<String, String> createCountryMap() {
		Map<String, String> country = new HashMap<String, String>();
		country.put("0", "日本");
		country.put("392", "海外");
		return country;
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
	 * SCF向け業種<b>Map</b>の初期化
	 * 
	 * @return SCF向け業種<b>Map</b>
	 */
	private static Map<String, String> createScfBusinessCategoryMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "電機・電子製造業(電機・電子)");
		map.put("2", "電機・電子製造業(その他)");
		map.put("3", "機械製造業(機械・工具)");
		map.put("4", "機械製造業(精密機器)");
		map.put("5", "機械製造業(自動車・部品)");
		map.put("6", "機械製造業(その他輸送用機器)");
		map.put("7", "機械製造業(その他)");
		map.put("8", "製造業(鉄鋼・金属)");
		map.put("9", "製造業(プラスチック)");
		map.put("10", "製造業(繊維・衣料)");
		map.put("11", "製造業(化学)");
		map.put("12", "製造業(食品・飲料)");
		map.put("13", "製造業(その他)");
		map.put("14", "非製造業(エネルギー)");
		map.put("15", "非製造業(印刷・出版)");
		map.put("16", "非製造業(建設・土木)");
		map.put("17", "非製造業(流通・サービス)");
		map.put("18", "非製造業(情報・通信)");
		map.put("19", "非製造業(商社)");
		map.put("20", "非製造業(金融・保険)");
		map.put("21", "非製造業(その他)");
		map.put("22", "官公庁・団体・学校(官公庁・教育機関)");
		map.put("23", "官公庁・団体・学校(団体・その他)");
		map.put("24", "報道関係(報道)");
		map.put("25", "報道関係(その他)");
		return map;
	}

	/**
	 * ロボット展向け業種<b>Map</b>の初期化
	 * 
	 * @return ロボット展向け業種<b>Map</b>
	 */
	private static Map<String, String> createRobotBusinessCategoryMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "電機・電子製造業(電機・電子)");
		map.put("2", "電機・電子製造業(その他)");
		map.put("3", "機械製造業(機械・工具)");
		map.put("4", "機械製造業(精密機器)");
		map.put("5", "機械製造業(自動車・部品)");
		map.put("6", "機械製造業(その他輸送用機器)");
		map.put("7", "機械製造業(その他)");
		map.put("8", "製造業(鉄鋼・金属)");
		map.put("9", "製造業(プラスチック)");
		map.put("10", "製造業(繊維・衣料)");
		map.put("11", "製造業(化学)");
		map.put("12", "製造業(食品・飲料)");
		map.put("13", "製造業(その他)");
		map.put("14", "非製造業(エネルギー)");
		map.put("15", "非製造業(印刷・出版)");
		map.put("16", "非製造業(建設・土木)");
		map.put("17", "非製造業(流通・サービス)");
		map.put("18", "非製造業(情報・通信)");
		map.put("19", "非製造業(商社)");
		map.put("20", "非製造業(金融・保険)");
		map.put("21", "非製造業(その他)");
		map.put("22", "官公庁・団体・学校(官公庁・教育機関)");
		map.put("23", "官公庁・団体・学校(団体・その他)");
		map.put("24", "報道関係(報道)");
		return map;
	}

	/**
	 * SCF向け職種<b>Map</b>の初期化
	 * 
	 * @return SCF向け職種<b>Map</b>
	 */
	private static Map<String, String> createScfOccupationCategoryMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "研究・開発");
		map.put("2", "設計");
		map.put("3", "生産技術");
		map.put("4", "製造");
		map.put("5", "資材・購買");
		map.put("6", "品質管理・検査");
		map.put("7", "営業・販売");
		map.put("8", "宣伝・企画");
		map.put("9", "経営・管理");
		map.put("10", "官公庁・団体");
		map.put("11", "その他");
		map.put("12", "学生");
		map.put("13", "海外");
		return map;
	}

	/**
	 * ロボット展向け職種<b>Map</b>の初期化
	 * 
	 * @return ロボット展向け職種<b>Map</b>
	 */
	private static Map<String, String> createRobotOccupationCategoryMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "研究・開発");
		map.put("2", "設計");
		map.put("3", "生産技術");
		map.put("4", "製造");
		map.put("5", "資材・購買");
		map.put("6", "品質管理・検査");
		map.put("7", "営業・販売");
		map.put("8", "宣伝・企画");
		map.put("9", "経営・管理");
		map.put("10", "官公庁・団体");
		map.put("11", "その他");
		return map;
	}

	/**
	 * 遠藤工業株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 遠藤工業株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForEndo() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "情報収集・カタログ収集のみ");
		requests.put("002", "今後検討の可能性あり");
		requests.put("003", "具体的検討・見積希望");
		requests.put("004", "営業訪問希望");
		requests.put("010", "アテンドシート有");
		requests.put("011", "対応者：小甲");
		requests.put("012", "対応者：渋木");
		requests.put("013", "対応者：笹川");
		requests.put("014", "対応者：吉岡");
		requests.put("015", "対応者：松本");
		requests.put("016", "対応者：平山");
		requests.put("017", "対応者：蟻生");
		requests.put("018", "対応者：岡村");
		requests.put("019", "対応者：渡辺");
		requests.put("020", "対応者：日伝");
		return requests;
	}

}