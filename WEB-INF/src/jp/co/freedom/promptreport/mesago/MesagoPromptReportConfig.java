package jp.co.freedom.promptreport.mesago;

/**
 * コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class MesagoPromptReportConfig {

	// ◆◆◆バーコードデータファイル◆◆◆
	/** rfidログデータフォルダ */
	public static final String csvDataDirectory = "f:\\matching\\scf";
	/** デリミタ カンマ */
	public static String DELIMITER_COMMNA = ",";

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL_REMOTE = "jdbc:mysql://118.22.30.77:13306/bwj2014";
	public static final String PSQL_URL_LOCAL = "jdbc:mysql://localhost:3306/scf";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD_REMOTE = "aq6ftg2f";
	public static final String PSQL_PASSWORD_LOCAL = "";

	/** 開催日 */
	public static final String[] DAYS = { "26", "27", "28" };

	/** バーコードデータのタイムスタンプにおける日時情報の開始インデックス（例. 20131106の場合は7を指定） */
	public static final int START_INDEX_DAY_IN_TIMESTAMP = 6;

	/** バーコードデータのタイムスタンプにおける日時情報の終了インデックス（例. 20131106の場合は8を指定） */
	public static final int END_INDEX_DAY_IN_TIMESTAMP = 8;

}
