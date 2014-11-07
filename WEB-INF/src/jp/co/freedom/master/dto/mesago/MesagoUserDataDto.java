package jp.co.freedom.master.dto.mesago;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【MESAGO】ユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class MesagoUserDataDto extends UserDataDto {

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

	/** 来場フラグ */
	public boolean visitor;

	/** 来場フラグ一覧 */
	public boolean visitFlgs[];

	public boolean visit19Flg;

	public boolean visit20Flg;

	public boolean visit21Flg;

	/** クレンジング結果 */
	public MesagoValidationResult result;

	/** コンストラクタ */
	public MesagoUserDataDto() {
		super();
		this.result = new MesagoValidationResult();
		super.cardInfo = new MesagoCardDto();
		super.questionInfo = new MesagoQuestionDto();
	}

}
