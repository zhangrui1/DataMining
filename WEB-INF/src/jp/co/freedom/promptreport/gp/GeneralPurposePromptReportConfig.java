package jp.co.freedom.promptreport.gp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 【汎用】速報集計用Config
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralPurposePromptReportConfig {

	/** CSVデータのセパレイト文字(入力ファイル) */
	public String inputSeparateMark;

	/** CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値 */
	public boolean enquoteByDoubleQuotation;

	/** CSVの最初の行がヘッダー行であるか否かのブール値 */
	public boolean removeHeaderRecord;

	/** 展示会会期日程-年 */
	public String year;

	/** 展示会会期日程-月 */
	public String month;

	/** 展示会会期日程-日 */
	public String[] days;

	/** バーコードデータのタイムスタンプにおける日時情報の開始インデックス（例. 20131106の場合は7を指定） */
	public int startIndexDayInTimestamp;

	/** バーコードデータのタイムスタンプにおける日時情報の終了インデックス（例. 20131106の場合は8を指定） */
	public int endIndexDayInTimestamp;

	/** 【BWJ専用】当日入力カテゴリ分類定義 */
	public Map<String, String> bwjAppointeddayCategoryRules;

	/** 【BWJ専用】事前登録カテゴリ分類定義 */
	public Map<String, String> bwjPreRegistCategoryRules;

	/** 【BWJ専用】カテゴリ一覧 */
	public List<String> bwjCategoryList;

	/** 【BWJ専用】カテゴリマッピングルール */
	public Map<String, String> bwjCategoriesMap;

	/** コンストラクタ */
	public GeneralPurposePromptReportConfig() {
		this.bwjAppointeddayCategoryRules = new LinkedHashMap<String, String>();
		this.bwjPreRegistCategoryRules = new LinkedHashMap<String, String>();
		this.bwjCategoryList = new ArrayList<String>();
		this.bwjCategoriesMap = new LinkedHashMap<String, String>();
	}

}
