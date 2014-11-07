package jp.co.freedom.master.utilities.basf;

/**
 * SCF用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class BasfConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/basf";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/basf";

	/** DBユーザー名 */
	public static final String PSQL_USER = "root";

	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆入力データ関連◆◆◆
	public static final int DETAIL_ENQUETE_MAXNUM = 18;

	// ◆◆◆出力データファイル◆◆◆
	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** 出力ファイル名 */
	public static final String MASTERFILE_NAME = "マスターデータ.txt";

}
