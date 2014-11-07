package jp.co.freedom.gpenquete.schema;

/**
 * 【汎用アンケート】妥当性検証ルール情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralValidationRulesSchema {

	/** 開始値 */
	public String start;

	/** 終了値 */
	public String end;

	/** 正規表現 */
	public String regex;

	/** データ範囲の反転フラグ */
	public boolean reverse;

	/**
	 * コンストラクタ
	 */
	public GeneralValidationRulesSchema() {

	}

	/**
	 * コンストラクタ
	 *
	 * @param start
	 *            開始値
	 * @param end
	 *            終了値
	 * @param regex
	 *            正規表現
	 * @param reverse
	 *            データ範囲の反転フラグ
	 */
	public GeneralValidationRulesSchema(String start, String end, String regex,
			boolean reverse) {
		this.start = start;
		this.end = end;
		this.regex = regex;
		this.reverse = reverse;
	}

}
