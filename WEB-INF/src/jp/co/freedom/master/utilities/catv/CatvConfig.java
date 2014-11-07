package jp.co.freedom.master.utilities.catv;

/**
 * IP用コンフィグレーションクラス
 *
 * @author フリーダム・グループ
 *
 */
public class CatvConfig {

	// ◆◆◆MySQL接続情報◆◆◆
	/** DBサーバーURL */
	public static final String PSQL_URL_REMOTE = "jdbc:mysql://118.22.30.75:63306/catv2014?useUnicode=true&characterEncoding=utf8";

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
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION_FALSE = false;
	public static boolean ENQUOTE_BY_DOUBLE_QUOTATION_TRUE = true;
	/** 最初の行がヘッダー行であるか否かのブール値 */
	public static boolean REMOVE_HEADER_RECORD_FALSE = false;
	public static boolean REMOVE_HEADER_RECORD_TRUE = true;

	// ◆◆◆ロジック制御◆◆◆
	/** 環境依存文字の存在チェック */
	public static boolean EXECUTE_VALIDATE_MODEL_DEPENDENCE = false;

	// ◆◆◆出力データファイル◆◆◆
	/** アンケート項目の出力フラグ */
	public static final boolean OUTPUT_ENQUETE_RESULTS_FLG = true;

	/** 妥当性検証違反の詳細情報の出力フラグ */
	public static final boolean OUTPUT_VALIDATION_ERROR_RESULT = true;

	/** セミナー参加者データファイル */
	public static final String MATCHING_RESULT_FILE_NAME = "セミナー参加者データ.txt";

	/** 1立てマーク */
	public static final String MARK = "T";

	// ◆◆◆バーコード番号◆◆◆
	/** タイムスタンプ中の来場日の出現位置(開始位置) */
	public static final int START_INDEX_DAY_IN_TIMESTAMP = 8;
	/** タイムスタンプ中の来場日の出現位置(終端位置) */
	public static final int END_INDEX_DAY_IN_TIMESTAMP = 10;

	/** 事前登録用バーコード番号の先頭数字 */
	public static final String PREENTRY_BARCODE_START_BIT = "2";

	/** 当日登録用バーコード番号の先頭数字 */
	public static final String APPOINTEDDAY_BARCODE_START_BIT = "3";

	//
	// /** 招待状用バーコード番号の先頭数字 */
	// public static final String INVITATION_BARCODE_START_BIT = "3";
	//
	// /** VIP招待券用バーコード番号の先頭数字 */
	// public static final String VIP_INVITATION_BARCODE_START_BIT = "4";
	//
	// /** プレス用バーコード番号の先頭数字 */
	// public static final String PRESS_BARCODE_START_BIT = "5";

	// ◆◆◆原票種別◆◆◆
	// /** 画像ファイル名の原票種別の出現開始位置 */
	// public static final int TICKET_TYPE_START_POSITION_FOR_IMAGE_FILENAME =
	// 0;
	//
	// /** 画像ファイル名の原票種別の出現終端位置 */
	// public static final int TICKET_TYPE_END_POSITION_FOR_IMAGE_FILENAME = 2;

	/** 原票種別 */
	public enum TICKET_TYPE {
		preentry/* 事前登録 */, invitation/* 招待状 */, vip_invitation/* VIP招待券 */, appointedday/* 当日売り券 */, unknown /* 不正な種別 */;
	}

	// ◆◆◆画像ファイル名◆◆◆
	/** 画像ファイル名の来場日の出現位置 */
	public static final int DATE_START_POSITION_FOR_IMAGE_FILENAME = 8;

	/** 画像ファイル名の来場日の出現位置 */
	public static final int DATE_END_POSITION_FOR_IMAGE_FILENAME = 10;

	// // ◆◆◆展示会情報◆◆◆
	// /** 展示会名称 */
	// public static final String EXHIBITION_NAME = "IP";
	//
	// /** 開催年 */
	// public static final String YEAR = "2014";
	//
	// /** 開催月 */
	// public static final String MONTH = "07";
	//
	// /** 開催日 */
	// public static final String[] DAYS = { "24", "25", "26", "27" };
	//
	// /** 開場時間 */
	// public static final String HOUR = "10";

	// /** 【バッチ作成用】事前登録 */
	// public static final int barcodeStartForPreentry = 1067269;
	//
	// /** 【バッチ作成用】事前VIP */
	// public static final int barcodeStartForPreentryVIP = 1050000;
	//
	// /** 【バッチ作成用】当日VIP */
	// public static final int barcodeStartForAppointeddayVIP = 7010950;
	//
	// /** 【バッチ作成用】当日JNA認定講師 */
	// public static final int barcodeStartForAppointeddayJNA = 5210800;
}
