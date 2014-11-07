package jp.co.freedom.common.dto.enquete;

/**
 * アンケート情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class EnqueteDto {

	/** イベント名 */
	public String event;

	/** 来場日 */
	public String day;

	/** 担当支店 */
	public String charge;

	/** 名刺情報 */
	public EnqueteCardDto cardInfo;

	/** アンケート情報 */
	public EnqueteQuestionDto questionInfo;

	/** コンストラクタ */
	public EnqueteDto() {
	}
}
