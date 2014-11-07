package jp.co.freedom.master.dto.jeca;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【MESAGO】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class JecaUserDataDto extends UserDataDto {

	/** 事前登録フラグ */
	public boolean preentry;

	/** 当日登録フラグ */
	public boolean appointedday;

	/** サブバーコード */
	public String subBarcode;

	/** 団体登録フラグ */
	public boolean group;

	/** アンマッチフラグ */
	public boolean unmatch;

	/** コンストラクタ */
	public JecaUserDataDto() {
		super();
		super.cardInfo = new JecaCardDto();
		super.questionInfo = new JecaQuestionDto();
	}

}
