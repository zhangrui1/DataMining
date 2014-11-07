package jp.co.freedom.gpenquete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.freedom.gpenquete.schema.GeneralValidationRulesSchema;
import jp.co.freedom.gpenquete.schema.QuestionSchema;

/**
 * アンケートコンフィグ情報
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralPurposeEnqueteConfig {

	/** CSVデータのセパレイト文字(入力ファイル) */
	public String inputSeparateMark;

	/** CSVデータのセパレイト文字(出力ファイル) */
	public String outputSeparateMark;

	/** CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値(入力ファイル) */
	public boolean enquoteByDoubleQuotation;

	/** CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値(出力ファイル) */
	public boolean outputEnquoteByDoubleQuotation;

	/** CSVの最初の行がヘッダー行であるか否かのブール値 */
	public boolean removeHeaderRecord;

	/** ヘッダー行出力フラグ */
	public boolean outputHeaderRow;

	/** 出力ファイル名 */
	public String outputFileName;

	/** 単純回答連結時に使用するデリミタ */
	public String concatDelimiterForSimpleAnswer;

	/** 複数回答連結時に使用するデリミタ */
	public String concatDelimiterForMultiAnswer;

	/** 内訳回答連結時に使用するデリミタ */
	public String concatDelimiterForChild;

	/** 1フラグ展開に使用する文字列 */
	public String flg1Mark;

	/** その他FAや内訳回答が存在する場合にフラグ補正を行うか否かのブール値 */
	public boolean correctChoiceSelectFa;

	/** 選択肢型回答の入力タイプ */
	public String choiceAnswerInputType;

	/** 不明マーク */
	public String undecipherableMark;

	/** 選択肢型回答の出力タイプ */
	public String choiceAnswerOutputType;

	/** アンケート出力形式 */
	public List<QuestionSchema> enquetes;

	/** 【展示会データ集計用】マッピングルール */
	public Map<String, Integer> mapping;

	/** 妥当性検証ルール */
	public Map<Integer, GeneralValidationRulesSchema[]> validationRules;

	/**
	 * コンストラクタ
	 */
	public GeneralPurposeEnqueteConfig() {
		this.enquetes = new ArrayList<QuestionSchema>();
		this.mapping = new HashMap<String, Integer>();
		this.validationRules = new HashMap<Integer, GeneralValidationRulesSchema[]>();
	}
}
