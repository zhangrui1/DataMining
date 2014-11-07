package jp.co.freedom.master.fixedlengthdata.awards;

/**
 * 【広告対象】審査表データ マスターデータDL用Config
 * 
 * @author フリーダム・グループ
 * 
 */
public class AdvertisingAwardsConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/awards";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/scf";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆出力データファイル◆◆◆
	/** マスターデータファイル */
	public static final String MASTERDATA_FILE_NAME = "マスターデータ.txt";

	/** マスターデータファイル(デバッグ) */
	public static final String MASTERDATA_DEBUG_FILE_NAME = "マスターデータ_debug.txt";

	/** 1行に格納する審査件数の最大値 */
	public static final int JUDGE_DATA_MAX_NUMBER = 80;

	/** 空値のシンボル */
	public static final String EMPTY_MARK = " ";
}
