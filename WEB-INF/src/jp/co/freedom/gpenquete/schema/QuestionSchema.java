package jp.co.freedom.gpenquete.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 質問スキーマ情報
 *
 * @author フリーダム・グループ
 *
 */
public class QuestionSchema {

	/** 固定値フラグ */
	public boolean fix;

	/** 固定値 */
	public String fixValue;

	/** 固定値の形式 */
	public String fixFormat;

	/** 固定値データ長 */
	public String fixLength;

	/** 文字列長補完のために使用するマーク */
	public String fixCompleteMark;

	/** 文字列長補完ポジション(前or後) */
	public String fixCompleteMarkPosition;

	/** 全角文字変換フラグ */
	public boolean convertFullString;

	/** アンケート項目ID */
	public String id;

	/** グループID */
	public String groupId;

	/** アンケート項目名 */
	public String header;

	/** アンケート項目タイプ ('expand'=1立て展開／'choice'=選択肢／空値) */
	public String type;

	/** 選択肢型種別 ('single'=シングル回答／'multi'=マルチ回答) */
	public String mode;

	/** 出力文字列開始位置 */
	public int start;

	/** 出力文字列終端位置 */
	public int end;

	/** 部分文字列抽出用正規表現 */
	public String matchByRegex;

	/** 部分文字列抽出用正規表現のグループ番号 */
	public String matchGroupId;

	/** match-by-regex属性で指定された正規表現にマッチする文字列を置換するテキスト */
	public String replaceText;

	/** 文字列検索用正規表現 */
	public String containsByRegex;

	/** 【出力データの加工用】 指定文字列の挿入先位置 */
	public int insertPosition;

	/** 【出力データの加工用】 挿入する文字列 */
	public String insertValue;

	/** 【出力データの加工用】 出力データフォーマット */
	public String dataFormat;

	/** 【出力データの加工用】デリミタ */
	public String delimit;

	/** 【出力データの加工用】置換対象の正規表現 */
	public String replaceBeforeRegex;

	/** 【出力データの加工用】置換後の文字列 */
	public String replaceAfterValue;

	/** 【出力データの加工用】デリミタ文字の個別使用フラグ */
	public boolean useDelimiter;

	/** 必須制約 */
	public boolean required;

	/** 文字列長 */
	public String dataLength;

	/** 最小文字列長 */
	public String dataMinLength;

	/** 最大文字列長 */
	public String dataMaxLength;

	/** アンケート項目の選択肢 */
	public List<ChoiceSchema> choices;

	/** 条件式 */
	public List<IfElseSchema> conditions;

	/**
	 * コンストラクタ
	 */
	public QuestionSchema() {
		this.choices = new ArrayList<ChoiceSchema>();
	}
}
