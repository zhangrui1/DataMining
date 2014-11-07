package jp.co.freedom.master.utilities.scf;

/**
 * SCF用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/scf";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/scf";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆CSVデータ関連◆◆◆
	/** ファイル拡張子 */
	public static final String ALLOWS_EXTENSIONS[] = { "txt", "TXT" };
	/** データフォルダ */
	public static final String barcodeDataDirectory = "f:\\matching\\scf";
	/** ダブルコーテーションによるエンクォートが施されているか否かのブール値 */
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = false;

	// ◆◆◆出力データファイル◆◆◆
	/** アンケート項目の出力フラグ */
	public static final boolean OUTPUT_ENQUETE_RESULTS_FLG = true;

	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** ブース番号の出力フラグ */
	public static final boolean OUTPUT_BOOTH_NUMBER_FLG = false;

	/** マッチング結果ファイル */
	public static final String MATCHING_RESULT_FILE_NAME = "マッチングデータ.txt";

	/** 当日登録マスターデータファイル */
	public static final String APPOINTEDDAY_MASTERFILE_NAME = "当日登録.txt";

	/** 事前登録マスターデータファイル */
	public static final String PREENTRY_MASTERFILE_NAME = "事前登録.txt";

	// ◆◆◆バーコード番号◆◆◆
	/** 事前登録用バーコード番号の先頭数字 */
	public static final String PREENTRY_BARCODE_START_BIT = "2";

	/** ロボット展からの来場者バーコード番号の先頭数字 */
	public static final String ROBOTENTRY_BARCODE_START_BIT = "5";

	// ◆◆◆原票種別◆◆◆
	/** 画像ファイル名の原票種別の出現開始位置 */
	public static final int TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME = 0;

	/** 画像ファイル名の原票種別の出現終端位置 */
	public static final int TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME = 2;

	/** 原票種別：事前 */
	public static final String TICKET_TYPE_PREENTRY = "事前";

	/** 原票種別：招待 */
	public static final String TICKET_TYPE_INVITATION = "招待";

	/** 原票種別：当日 */
	public static final String TICKET_TYPE_APPOINTEDDAY = "当日";

	/** 原票種別:ロボット展からの来場者 */
	public static final String TICKET_TYPE_ROBOT = "ロボット展";

	public static final String MARK = "●";

	/** デリミタ カンマ */
	public static String DELIMITER_COMMNA = ",";
}
