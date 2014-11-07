package jp.co.freedom.master.dto.basf;

import java.util.List;

import jp.co.freedom.master.dto.QuestionDto;

/**
 * 【BASF】アンケート情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class BasfQuestionDto extends QuestionDto {

	public String Q1_1;

	public String Q1_2;

	public String Q1_2_FA;

	public String Q2_1;

	public String Q2_2;

	public String Q2_3;

	public String Q2_4;

	public String Q2_FA;

	public String Q3_1;

	public String Q3_2;

	public String Q3_3;

	public String Q3_4;

	public String Q3_5;

	public String Q3_6;

	public String Q3_FA;

	public String Q4;

	public String Q5;

	public List<BasfDetailEnqueteDto> detail;

	/** コンストラクタ */
	public BasfQuestionDto() {
		super();
	}

}
