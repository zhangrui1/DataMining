package jp.co.freedom.master.utilities.ime;

/**
 * SCF用コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImeConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL = "jdbc:mysql://localhost:3306/ime";
	// public static final String PSQL_URL =
	// "jdbc:mysql://118.22.30.75:63306/scf";
	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD = "";
	// public static final String PSQL_PASSWORD = "aq6ftg2f";

	// ◆◆◆出力データファイル◆◆◆
	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** 当日登録マスターデータファイル */
	public static final String APPOINTEDDAY_MASTERFILE_NAME = "当日登録.txt";

	// ◆◆◆原票種別◆◆◆
	/** 画像ファイル名の原票種別の出現開始位置 */
	public static final int TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME = 13;

	/** 画像ファイル名の原票種別の出現終端位置 */
	public static final int TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME = 15;

	/** 画像ファイル名の来場月の出現開始位置 */
	public static final int VISIT_MONTH_START_POSITION_FOR_IMAGE_FILENAME = 4;

	/** 画像ファイル名の来場日時の出現終端位置 */
	public static final int VISIT_MONTH_END_POSITION_FOR_IMAGE_FILENAME = 7;

}
