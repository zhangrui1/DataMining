package jp.co.freedom.master.dto.emb;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【Embedded Technology 2013】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class EmbUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public EmbUserDataDto() {
		super();
		super.cardInfo = new EmbCardDto();
		super.questionInfo = new EmbQuestionDto();
	}

}
