package jp.co.freedom.crosstabulation;

/**
 * 【たき工房】コンフィグ情報
 * 
 * @author フリーダム・グループ
 * 
 */
public class CrossTabulationForTakiConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/taki?characterEncoding=utf8";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/taki";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆Excelファイル情報◆◆◆
	/** 出力EXCELファイルのファイルパス */
	public static final String EXCEL_FILENAME_PATH = "f:\\test - コピー.xlsx";

}
