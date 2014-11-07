package jp.co.freedom.gpenquete.schema;

/**
 * 条件式スキーマ情報
 * 
 * @author フリーダム・グループ
 * 
 */
public class IfElseSchema {

	/** 条件式(正規表現) */
	public String regex;

	/** 条件式を満たした場合に付与する値 */
	public String value;

	/** else文であるか否かのブール値 */
	public boolean elseFlg;

	/** child要素 */
	public ChildSchema child;

	/**
	 * コンストラクタ
	 */
	public IfElseSchema() {
	}
}
