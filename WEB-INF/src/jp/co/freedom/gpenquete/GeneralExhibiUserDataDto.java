package jp.co.freedom.gpenquete;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【展示会納品データ集計】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class GeneralExhibiUserDataDto extends UserDataDto {

	/** コンストラクタ */
	public GeneralExhibiUserDataDto() {
		super();
		super.cardInfo = new GeneralExhibiCardDto();
	}

}
