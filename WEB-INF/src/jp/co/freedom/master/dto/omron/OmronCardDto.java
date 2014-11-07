package jp.co.freedom.master.dto.omron;

import jp.co.freedom.master.dto.CardDto;

/**
 * 【OMRON】名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronCardDto extends CardDto {

	/** スタッフコード */
	public String staffCode;

	/** 業務開始日 */
	public String startYear;

	/** コンストラクタ */
	public OmronCardDto() {
		super();
	}

}
