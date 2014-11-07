package jp.co.freedom.master.utilities.ltt;

/**
 * LTT用コンフィグレーションクラス
 *
 * @author フリーダム・グループ
 *
 */
public class LTTConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL_LOCAL = "jdbc:mysql://localhost:3306/ltt2014?useUnicode=true&characterEncoding=utf8";

	/** DBユーザー名 */
	public static final String PSQL_USER = "root";
	/** DBユーザーパスワード */
	public static final String PSQL_PASSWORD_LOCAL = "";
	public static final String PSQL_PASSWORD_REMOTE = "aq6ftg2f";

	// ◆◆◆CSVデータ関連◆◆◆
	/** ファイル拡張子 */
	public static final String ALLOWS_EXTENSIONS[] = { "txt", "TXT" };

	/** ファイル拡張子 */
	public static final String ALLOWS_EXTENSIONS_CSV[] = { "csv", "CSV" };

	/** ダブルコーテーションによるエンクォートが施されているか否かのブール値 */
	/** 事前バーコードと登録番号対応表 */
	public static final String CONVERT_TABLE_DIRECTORY = "f:\\matching\\LTTConvertTable";
	/** ダブルコーテーションによるエンクォートが施されているか否かのブール値 */

	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION_FALSE = false;
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION_TRUE = true;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD_FALSE = false;
	public static boolean REMOVE_HEADER_RECORD_TRUE = true;

	// ◆◆◆展示会情報◆◆◆
	/** 展示会名称 */
	public static final String EXHIBITION_NAME = "LTT2014";

	/** 開催年 */
	public static final String YEAR = "2014";

	/** 開催月 */
	public static final String MONTH = "09";

	/** 開催日 */
	public static final String[] DAYS = { "09", "10", "11" , "12"};
	
	// ◆◆◆ロジック制御◆◆◆
	/** 環境依存文字の存在チェック */
	public static boolean EXECUTE_VALIDATE_MODEL_DEPENDENCE = false;

	// ◆◆◆マッチングデータフォーマット◆◆◆
	/** リクエストコードの項目数 */
	public static final int REQUEST_CODE_NUM = 20;

	// ◆◆◆出力データファイル◆◆◆
	/** アンケート項目の出力フラグ */
	public static final boolean OUTPUT_ENQUETE_RESULTS_FLG = true;

	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** マッチング結果ファイル */
	public static final String MATCHING_RESULT_FILE_NAME = "LTTマッチングデータ.txt";

	/** 当日登録マスターデータファイル */
	public static final String APPOINTEDDAY_MASTERFILE_NAME = "当日登録マスター.txt";

	/** 事前登録マスターデータファイル */
	public static final String PREENTRY_MASTERFILE_NAME = "事前登録マスター.txt";

	/** 出展者データファイル */
	public static final String EXHIBITOR_RESULT_FILE_NAME = "出展者来場者納品データ.txt";

	/** 1立てマーク */
	public static final String MARK = "T";

	// ◆◆◆バーコード番号◆◆◆
	
	/** 事前登録用バーコード番号の先頭数字 */
	public static final String PREENTRY_BARCODE_START_BIT = "10";

	/** 当日入力用バーコード番号の先頭数字 */
	public static final String APPOINTEDDAY_BARCODE_START_BIT = "2";

	/** 団体登録用バーコード番号の先頭数字 */
	public static final String GROUP_BARCODE_START_BIT = "3";


	/** 原票種別 */
	public enum TICKET_TYPE {
		preentry/* 事前登録 */, invitation/* 招待状 */, vip_invitation/* VIP招待券 */, appointedday/* 当日売り券 */, unknown /* 不正な種別 */;
	}

	// ◆◆◆画像ファイル名◆◆◆
	/** 画像ファイル名の来場日の出現位置 */
	public static final int DATE_START_POSITION_FOR_IMAGE_FILENAME = 8;

	/** 画像ファイル名の来場日の出現位置 */
	public static final int DATE_END_POSITION_FOR_IMAGE_FILENAME = 10;
}
