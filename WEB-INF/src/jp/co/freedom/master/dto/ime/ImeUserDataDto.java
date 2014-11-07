package jp.co.freedom.master.dto.ime;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【IME】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImeUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public ImeUserDataDto() {
		super();
		super.cardInfo = new ImeCardDto();
		super.questionInfo = new ImeQuestionDto();
	}

}
