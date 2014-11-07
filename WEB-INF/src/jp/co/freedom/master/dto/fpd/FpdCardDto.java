package jp.co.freedom.master.dto.fpd;

import jp.co.freedom.master.dto.CardDto;

/**
 * 【FPD】名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdCardDto extends CardDto {

	/** 住所優先フラグ 1=勤務先／2=自宅 */
	public String priorityFlg;

	/** 住所カナ */
	public String V_ADDR_KANA;

	/** 部署名カナ */
	public String V_DEPT_KANA;

	/** 役職名カナ */
	public String V_BIZ_KANA;

	/** 国名 */
	public String V_COUNTRY;

	/** コンストラクタ */
	public FpdCardDto() {
		super();
	}

}
