package jp.co.freedom.master.dto.basf;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【BASF】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class BasfUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public BasfUserDataDto() {
		super();
		super.cardInfo = new BasfCardDto();
		super.questionInfo = new BasfQuestionDto();
	}

}
