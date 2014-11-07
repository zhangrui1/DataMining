package jp.co.freedom.master.dto.scf;

import jp.co.freedom.master.dto.CardDto;

/**
 * 【SCF】名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ScfCardDto extends CardDto {

	/** 【ロボット展専用】原票状況不備 */
	public String robotInvalidFlg;

	/** 【ロボット展専用】海外住所フラグ */
	public String robotOverseaFlg;

	/** コンストラクタ */
	public ScfCardDto() {
		super();
	}

}
