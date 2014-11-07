package jp.co.freedom.master.dto.valdac;

import java.util.Map;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【LTT】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class ValdacUserDataDto extends UserDataDto {

	/** 旧機器システムID */
	public String KikiSysIdOld;

	/** 機器ID */
	public String kikiID;

	/** 機器ID */
	public String kikiIDOld;

	/** 部品ID */
	public String buhinID;

	/** 機器ID */
	public String buhinIDOld;

	/** 機器ID */
	public Map<String,ValdacBuhiDto> kikiList;

	/** コンストラクタ */
	public ValdacUserDataDto() {
		super();
		super.cardInfo = new ValdacCardDto();
		super.questionInfo = new ValdacQuestionDto();
	}

}
