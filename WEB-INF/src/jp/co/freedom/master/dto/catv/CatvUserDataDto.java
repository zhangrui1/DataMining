package jp.co.freedom.master.dto.catv;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 【CATV】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class CatvUserDataDto extends UserDataDto {

	/** 事前登録フラグ */
	public boolean preentry;

	/** 当日登録フラグ */
	public boolean appointedday;

	/** 画像ファイルパス */
	public String imagePath;

	/** [デバッグ用]カウンタ値 */
	public String count;

	/** コンストラクタ */
	public CatvUserDataDto() {
		super();
		super.cardInfo = new CatvCardDto();
		super.questionInfo = new CatvQuestionDto();
	}

}
