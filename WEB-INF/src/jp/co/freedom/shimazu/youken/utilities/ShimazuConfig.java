package jp.co.freedom.shimazu.youken.utilities;

/**
 * Shimazuコンフィグレーションクラス
 *
 * @author フリーダム・グループ
 *
 */
public class ShimazuConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/shimazucata?characterEncoding=utf8";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/shimazucata?characterEncoding=utf8";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆CSVデータ関連◆◆◆
	/** rfidログデータフォルダ */
	// public static final String logDataDirectory = "/data/contents/input";
	/** ファイル拡張子 */
	public static final String ALLOWS_EXTENSIONS[] = { "txt", "TXT" };
	// public static final String logDataDirectory =
	// "f:\\matching\\shimazuYouken";
	public static final String OUTPUT_FILENAME = "島津要件票.txt";
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = true;
	/** デリミタ */
	public static String DELIMITER = "\t";

}
