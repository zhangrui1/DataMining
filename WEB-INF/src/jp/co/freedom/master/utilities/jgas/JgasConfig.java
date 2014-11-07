package jp.co.freedom.master.utilities.jgas;

/**
 * JGAS用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JgasConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/jgasmatching";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/jgasmatching";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆CSVデータ関連◆◆◆
	public static final String ALLOWS_EXTENSIONS[] = { "txt", "TXT" };
	/** データフォルダ */
	public static final String csvDataDirectory = "f:\\matching\\jgas";
	/** アンケートデータフォルダ */
	public static final String enqueteCsvDataDirectory = "f:\\matching\\jgasEnquete";
	/** ダブルコーテーションによるエンクォートが施されているか否かのブール値 */
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = false;

	// ◆◆◆マッチングデータフォーマット◆◆◆
	/** リクエストコードの項目数 */
	public static final int REQUEST_CODE_NUM = 20;

	// ◆◆◆出力データファイル◆◆◆
	/** アンケート項目を出力フラグ */
	public static final boolean OUTPUT_ENQUETE_RESULTS_FLG = true;
	/** マッチングアンケート項目を出力フラグ */
	public static final boolean OUTPUT_MATCHING_ENQUETE_RESULTS_FLG = false;
	// /** 名刺情報の項目数 */
	// public static final int CARD_INFO_NUM = 19;
	//
	/** マッチング結果ファイル */
	public static final String MATCHING_RESULT_FILE_NAME = "マッチングデータ.txt";

	/** 当日登録マスターデータファイル */
	public static final String APPOINTEDDAY_MASTERFILE_NAME = "当日登録.txt";

	/** 事前登録マスターデータファイル */
	public static final String PREENTRY_MASTERFILE_NAME = "事前登録.txt";

	// /** 当日登録不備データ一覧ファイル */
	// public static final String APPOINTEDDAY_VALIDERROR_MASTERFILE_NAME =
	// "当日不備データ.txt";

}
