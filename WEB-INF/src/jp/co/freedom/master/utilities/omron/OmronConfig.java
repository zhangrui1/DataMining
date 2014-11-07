package jp.co.freedom.master.utilities.omron;

/**
 * OMRON用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL_LOCAL = "jdbc:mysql://localhost:3306/omron";
	public static final String PSQL_URL_REMOTE = "jdbc:mysql://118.22.30.75:63306/omron";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD_LOCAL = "";
	public static final String PSQL_PASSWORD_REMOTE = "aq6ftg2f";

	// ◆◆◆CSVデータ関連◆◆◆
	/** ファイル拡張子 */
	public static final String ALLOWS_EXTENSIONS[] = { "txt", "TXT" };
	/** データフォルダ */
	public static final String barcodeDataDirectory = "f:\\matching\\omron";
	/** ダブルコーテーションによるエンクォートが施されているか否かのブール値 */
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = false;

	// ◆◆◆出力データファイル◆◆◆
	/** アンケート項目の出力フラグ */
	public static final boolean OUTPUT_ENQUETE_RESULTS_FLG = true;

	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** マッチング結果ファイル */
	public static final String MATCHING_RESULT_FILE_NAME = "マッチングデータ.txt";

	/** デリミタ カンマ */
	public static String DELIMITER_COMMNA = ",";
}
