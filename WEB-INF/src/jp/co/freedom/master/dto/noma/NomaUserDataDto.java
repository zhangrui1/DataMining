package jp.co.freedom.master.dto.noma;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【NOMA】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class NomaUserDataDto extends UserDataDto {

	/** 事前登録フラグ */
	public boolean preentry;

	/**
	 * 事前登録フラグ2 [備忘] ILT2014において事前登録の主催者VIPであるかどうかの判定に使用
	 */
	public boolean preentry2;

	/** 当日登録フラグ */
	public boolean appointedday;

	/** 招待状フラグ */
	public boolean invitation;

	/** VIP招待券フラグ */
	public boolean vipInvitation;

	/** 出展社VIPフラグ */
	public boolean vipExhibi;

	/** 主催者VIPフラグ */
	public boolean vipSponsor;

	/** 事前VIPフラグ */
	public boolean preVip;

	/** プレスフラグ */
	public boolean press;

	/** CSVファイル名 */
	public String filename;

	/** コンストラクタ */
	public NomaUserDataDto() {
		super();
		super.cardInfo = new NomaCardDto();
		super.questionInfo = new NomaQuestionDto();
	}

}
