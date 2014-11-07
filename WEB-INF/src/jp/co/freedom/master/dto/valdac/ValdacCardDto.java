package jp.co.freedom.master.dto.valdac;

import jp.co.freedom.master.dto.CardDto;

/**
 * 【LTT】名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ValdacCardDto extends CardDto {

	/** 送付先 */
	public String SEND_FLG;

	/** 事前登録日 */
	public String REGIST_DATE;

	/** 【事前登録専用】 登録番号（来場事前登録） */
	public String PREENTRY_ID;

	/** コンストラクタ */
	public ValdacCardDto() {
		super();
	}

}
