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

	/** 東洋バルブ点検履歴*/
	public static final String OUTPUT_TENKENRIREKI = "東洋バルブ点検履歴変換後.txt";
	public static final String OUTPUT_TENKENRIREKI_NO = "東洋バルブ点検履歴変換後(NO).txt";

	/** 東洋バルブ点検履歴機器*/
	public static final String OUTPUT_TENKENKIKI = "東洋バルブ点検機器変換後.txt";
	public static final String OUTPUT_TENKENKIKI_NO = "東洋バルブ点検機器変換後.txt";

	/** 東洋バルブ懸案事項*/
	public static final String OUTPUT_KENAN = "懸案事項変換後.txt";
	public static final String OUTPUT_KENAN_NO = "懸案事項変換後(NO).txt";

	/** 東洋バルブ画像事項*/
	public static final String OUTPUT_IMAGEREPORT = "画像情報変換後.txt";
	public static final String OUTPUT_IMAGEREPORT_NO = "画像情報変換後(NO).txt";


	/** 東洋バルブ顧客データ*/
	public static final String OUTPUT_KOKYAKU = "顧客データ変換後.txt";

	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = false;
	/** デリミタ */
	public static String DELIMITER = "\t";

}
