package jp.co.freedom.master.dto.fpd;

import jp.co.freedom.master.dto.QuestionDto;

/**
 * 【FPD】アンケート情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdQuestionDto extends QuestionDto {

	/** お知らせ区分 */
	public String info1;

	/** 日経BPグループ各社からの各種ご案内 */
	public String info2;

	/** 日経BPグループ各社からのアンケート */
	public String info3;

	/** 日経BPグループ各社以外の製品やサービス情報 */
	public String info4;

	/** コンストラクタ */
	public FpdQuestionDto() {
		super();
	}

}
