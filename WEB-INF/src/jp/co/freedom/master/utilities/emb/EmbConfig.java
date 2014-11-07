package jp.co.freedom.master.utilities.emb;

/**
 * 
 * 【Embedded Technology 2013】コンフィグレーションクラス
 * 
 * */
public class EmbConfig {
	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/ete2013";

	/** DBユーザー名 */
	public static final String PSQL_USER = "root";

	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";

	// ◆◆◆出力データファイル◆◆◆
	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** 出力ファイル名 */
	public static final String MASTERFILE_NAME = "Emb_納品データ一覧.txt";

}
