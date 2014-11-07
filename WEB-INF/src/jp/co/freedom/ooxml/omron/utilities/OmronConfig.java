package jp.co.freedom.ooxml.omron.utilities;

/**
 * OMRON用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/omron?characterEncoding=utf8";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/omron";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆編集対象Excelファイル◆◆◆
	/** スタッフアンケートExcelファイルのファイルパス */
	public static final String STAFF_ENQUETE_FILE_PATH = "f:\\enquete\\omron\\master_staff_enquete - コピー.xlsx";
	/** 顧客評価Excelファイルのファイルパス */
	public static final String CLIENT_ENQUETE_FILE_PATH = "f:\\enquete\\omron\\master_client_enquete - コピー.xlsx";

}
