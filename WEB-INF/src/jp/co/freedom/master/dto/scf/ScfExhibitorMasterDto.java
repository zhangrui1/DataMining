package jp.co.freedom.master.dto.scf;

import java.util.ArrayList;
import java.util.List;

/**
 * 【SCF】出展者マスター情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfExhibitorMasterDto {

	public String id;

	public String year;

	/** 社名 */
	public String company;

	/** 担当者氏名 */
	public String name;

	/** 重複ID */
	public List<String> dupulicate;

	/** コンストラクタ */
	public ScfExhibitorMasterDto() {
		this.dupulicate = new ArrayList<String>();
	}

}
