package jp.co.freedom.gpenquete.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 選択肢スキーマ情報
 * 
 * @author フリーダム・グループ
 * 
 */
public class ChoiceSchema {

	/** 選択肢ID */
	public String id;

	/** 選択肢ラベル */
	public String value;

	/** 内訳回答用項目 */
	public List<ChildSchema> children;

	/** 参照要素ID */
	public String referId;

	/**
	 * コンストラクタ
	 */
	public ChoiceSchema() {
		children = new ArrayList<ChildSchema>();
	}
}
