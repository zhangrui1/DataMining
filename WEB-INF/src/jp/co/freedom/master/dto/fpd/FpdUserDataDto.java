package jp.co.freedom.master.dto.fpd;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【FPD】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdUserDataDto extends UserDataDto {

	/** 事前登録発券番号 */
	public String preEntryId;

	/** 展示会種別 */
	public String exhibitionType;

	/** コンストラクタ */
	public FpdUserDataDto() {
		super();
		super.cardInfo = new FpdCardDto();
		super.questionInfo = new FpdQuestionDto();
	}

}
