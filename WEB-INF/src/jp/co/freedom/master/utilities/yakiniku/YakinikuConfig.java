package jp.co.freedom.master.utilities.yakiniku;

/**
 * YAKINIKU用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class YakinikuConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/yakiniku";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆出力データファイル◆◆◆
	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** 会社名 略記フラグ */
	public static final boolean SIMPLIFY_CORP_NAME_FLG = true;
	
	/** 当日登録マスターデータファイル */
	public static final String APPOINTEDDAY_MASTERFILE_NAME = "焼肉フェア2014の当日登録.txt";

	// ◆◆◆原票種別◆◆◆
	/** 画像ファイル名の原票種別の出現開始位置 */
	public static final int TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME_TOKYO = 13;

	/** 画像ファイル名の原票種別の出現終端位置 */
	public static final int TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME_TOKYO =15;
	
	// ◆◆◆原票種別◆◆◆
	/** 画像ファイル名の原票種別の出現開始位置 */
	public static final int TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME_OSAKA = 4;

	/** 画像ファイル名の原票種別の出現終端位置 */
	public static final int TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME_OSAKA = 6;
}
