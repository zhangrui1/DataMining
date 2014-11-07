package jp.co.freedom.master.dto.omron;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【OMRON】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public OmronUserDataDto() {
		super();
		super.cardInfo = new OmronCardDto();
		super.questionInfo = new OmronQuestionDto();
	}

}
