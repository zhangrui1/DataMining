package jp.co.freedom.master.dto.jgas;

/**
 * 【JGAS】マッチングアンケート情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class JgasEnqueteDto {

	/** アンケートID */
	public String enqueteId;

	/** 質問No */
	public String questionNo;

	/** 回答ID(アンケート単位) */
	public String answerId;

	/** iPad回答ID */
	public String iPadAnswerId;

	/** バーコード番号 */
	public String barcodeId;

	/** 選択肢ID */
	public String choiceId;

	/** 選択肢見出し(自由記入でない場合) */
	public String choiceTitle;
}
