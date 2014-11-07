package jp.co.freedom.valdac.utilities;

/**
 * Shimazuコンフィグレーションクラス
 *
 * @author フリーダム・グループ
 *
 */
public class ValdacConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/valdacold?characterEncoding=utf8";
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
	/** 東洋バルブ */
	public static final String OUTPUT_FILENAME = "東洋バルブ.txt";
	/** 東洋バルブID対応関係 */
	public static final String OUTPUT_FILENAME_RELATION = "東洋バルブID対応関係.txt";
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = false;
	/** デリミタ */
	public static String DELIMITER = "\t";

}
