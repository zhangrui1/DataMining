package jp.co.freedom.master.fixedlengthdata.tokyo;

/**
 * 【固定データ長】棚卸表／経理処理控共通Config
 * 
 * @author フリーダム・グループ
 * 
 */
public class CommonConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL_LOCAL = "jdbc:mysql://localhost:3306/tokyo";
	public static final String PSQL_URL_REMOTE = "jdbc:mysql://118.22.30.75:63306/tokyo";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD_LOCAL = "";
	public static final String PSQL_PASSWORD_REMOTE = "aq6ftg2f";

	// ◆◆◆出力データファイル◆◆◆
	/** 棚卸表マスターデータファイル */
	public static final String TANAOROSHI_TABLE_FILE_NAME = "棚卸表.txt";
	public static final String TANAOROSHI_TABLE_CSV_FILE_NAME = "棚卸表_debug.txt";
	/** 経理処理控マスターデータファイル */
	public static final String KEIRISYORI_HIKAE_FILE_NAME = "経理処理控.txt";
	public static final String KEIRISYORI_HIKAE_CSV_FILE_NAME = "経理処理控_debug.txt";
}
