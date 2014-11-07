package jp.co.freedom.master.dto.ltt;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【LTT】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class LttUserDataDto extends UserDataDto {

	/** 事前登録フラグ */
	public boolean preentry;

	/** 当日登録フラグ */
	public boolean appointedday;

	/** 画像ファイルパス */
	public String imagePath;

	/** [デバッグ用]カウンタ値 */
	public String count;

	/** 来場フラグ */
	public Object visitFlgs;
	
	/** サブバーコード */
	public String subBarcode;

	/** 団体登録フラグ */
	public boolean group;
	
	/** ファイル名 */
	public String filename;	

	/** アンマッチフラグ */
	public boolean unmatch;

	/** コンストラクタ */
	public LttUserDataDto() {
		super();
		super.cardInfo = new LttCardDto();
		super.questionInfo = new LttQuestionDto();
	}

}
