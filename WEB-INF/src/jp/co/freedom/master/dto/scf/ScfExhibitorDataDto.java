package jp.co.freedom.master.dto.scf;

/**
 * 【SCF】出展者情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfExhibitorDataDto {

	/** ID　 */
	public String V_no;

	/** 出展年度 */
	public String V_year;

	/** 社名 */
	public String V_com;

	/** 担当者社名 */
	public String charge_com;

	/** 担当者氏名 */
	public String charge_name;

	/** 責任者社名 */
	public String Resp_com;

	/** 責任者氏名 */
	public String Resp_name;

	/** コンストラクタ */
	public ScfExhibitorDataDto() {
		super();
	}

}
