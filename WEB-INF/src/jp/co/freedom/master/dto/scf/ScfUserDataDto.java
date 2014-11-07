package jp.co.freedom.master.dto.scf;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【SCF】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfUserDataDto extends UserDataDto {

	/** 名刺マスターデータの存在フラグ */
	public String masterCardDataExist;

	/** アンケートマスターデータの存在フラグ */
	public String masterEnqueteDataExist;

	/** コンストラクタ */
	public ScfUserDataDto() {
		super();
		super.cardInfo = new ScfCardDto();
		super.questionInfo = new ScfQuestionDto();
	}

}
