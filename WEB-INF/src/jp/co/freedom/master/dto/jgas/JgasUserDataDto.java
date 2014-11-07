package jp.co.freedom.master.dto.jgas;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【JGAS】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class JgasUserDataDto extends UserDataDto {

	/** アンケート6種 */
	public JgasEnqueteDto[] enqueteInfo;

	/** コンストラクタ */
	public JgasUserDataDto() {
		super();
		super.cardInfo = new JgasCardDto();
		super.questionInfo = new JgasQuestionDto();
		enqueteInfo = new JgasEnqueteDto[6];
	}

}
