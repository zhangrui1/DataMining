package jp.co.freedom.master.dto.emb;

import java.util.ArrayList;
import java.util.List;

import jp.co.freedom.master.dto.QuestionDto;

/**
 * 【Embedded Technology 2013】アンケート情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class EmbQuestionDto extends QuestionDto {

	public String Q1;

	public List<EmbQ2Dto> q2List;

	public String Q3;

	public String Q4;

	public String Q5;

	/** コンストラクタ */
	public EmbQuestionDto() {
		super();
		this.q2List = new ArrayList<EmbQ2Dto>();
	}

}
