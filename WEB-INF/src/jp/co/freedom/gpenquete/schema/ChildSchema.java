package jp.co.freedom.gpenquete.schema;

/**
 * 内訳回答スキーマ情報
 * 
 * @author フリーダム・グループ
 * 
 */
public class ChildSchema {

	/** グループID */
	public String groupId;

	/** 内訳用項目ID */
	public String childId;

	/** 内訳記述用ラベル（前） */
	public String childbeforeValue;

	/** 内訳記述用ラベル（後） */
	public String childAfterValue;

	/** 部分文字列取得用正規表現 */
	public String matchByRegex;

	/** 正規表現で指定するグループ番号 */
	public String matchGroupId;

	/**
	 * コンストラクタ
	 */
	public ChildSchema() {
	}
}
