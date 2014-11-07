package jp.co.freedom.common.utilities;

/**
 * 共通コンフィグレーションクラス
 *
 * @author フリーダム・グループ
 *
 */
public class Config {

	// ◆◆◆MySQL接続情報◆◆◆
	/** 《リモート》共通DBサーバーURL */
	public static final String PSQL_URL_COMMONDB_REMOTE = "jdbc:mysql://118.22.30.75:63306/commonDB";
	/** 《ローカル》共通DBサーバーURL */
	public static final String PSQL_URL_COMMONDB_LOCAL = "jdbc:mysql://localhost:3306/commonDB";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** 《リモート》DBユーザーパスワード */
	public static final String PSQL_PASSWORD_REMOTE = "aq6ftg2f";
	/** 《ローカル》DBユーザーパスワード 空値 */
	public static final String PSQL_PASSWORD_LOCAL = "";

	// ◆◆◆ローカルの編集対象データ◆◆◆
	/** 汎用アンケート用データフォルダ */
	public static final String GP_ENQUETE_MINING_DIRECTORY = "f:\\enquete\\xmlConfig";
	/** 各データがダブルコートされているか否かのブール値 */
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION = false;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD = true;
	/** デリミタ タブ */
	public static String DELIMITER_TAB = "\t";
	/** デリミタ カンマ */
	public static String DELIMITER_COMMNA = ",";
	/** ファイル拡張子(TXT) */
	public static final String ALLOWS_EXTENSIONS_TXT[] = { "txt", "TXT" };
	/** ファイル拡張子(CSV) */
	public static final String ALLOWS_EXTENSIONS_CSV[] = { "csv", "CSV" };

	// ◆◆◆アップロードディレクトリ◆◆◆
	/** ファイルアップロードディレクトリ */
	public static final String UPLOAD_DIR = "data_mining_upload_dir";
	/** エラーログファイル */
	public static final String ERRORLOG_FILE = "エラーログ.txt";

}
