package jp.co.freedom.master.dto.mesago;

import jp.co.freedom.master.dto.QuestionDto;

/**
 * 【MESAGO】アンケート情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class MesagoQuestionDto extends QuestionDto {

	public String V_Q1_code;

	public String V_Q2_code;

	public String V_Q3_code;

	public String V_Q4_code;

	public String V_Q5_code;

	/* 2次データ用追加分 ここから↓↓　 */

	public String V_Q1_kubun;

	public String V_Q3_kubun;

	public String V_Q4_kubun;

	public String V_Q5_kubun;

	/* 2次データ用追加分 ここまで↑↑ */

	/** コンストラクタ */
	public MesagoQuestionDto() {
		super();
	}
}
