package jp.co.freedom.master.utilities.enquete.jismee;

/**
 * SCF用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JismeeConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/jismee";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/jismee";

	/** DBユーザー名 */
	public static final String PSQL_USER = "root";

	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆出力データファイル◆◆◆
	/** 出力ファイル名 */
	public static final String MASTERFILE_NAME = "jismee.txt";

}
