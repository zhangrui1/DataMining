package jp.co.freedom.master.dto.yakiniku;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【YAKINIKU】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class YakinikuUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public YakinikuUserDataDto() {
		super();
		super.cardInfo = new YakinikuCardDto();
		super.questionInfo = new YakinikuQuestionDto();
	}

}
