package jp.co.freedom.promptreport.scf;

/**
 * コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfPromptReportConfig {

	// ◆◆◆バーコードデータファイル◆◆◆
	/** rfidログデータフォルダ */
	public static final String csvDataDirectory = "f:\\matching\\scf";
	/** デリミタ カンマ */
	public static String DELIMITER_COMMNA = ",";

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.77:13306/bwj2013";
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/scf";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	// public static final String PSQL_PASSWORD = "aq6ftg2f";
	public static final String PSQL_PASSWORD = "";

	/** 開催日 */
	public static final String[] DAYS = { "5", "6", "7", "8" };

	/** バーコードデータのタイムスタンプにおける日時情報の開始インデックス（例. 20131106の場合は7を指定） */
	public static final int START_INDEX_DAY_IN_TIMESTAMP = 7;

	/** バーコードデータのタイムスタンプにおける日時情報の終了インデックス（例. 20131106の場合は8を指定） */
	public static final int END_INDEX_DAY_IN_TIMESTAMP = 8;

}
