package jp.co.freedom.master.dto.ltt;

import jp.co.freedom.master.utilities.ltt.LTTConfig;

/**
 * 【LTT】RFIDデータDTO
 *
 * @author フリーダム・グループ
 *
 */
public class LttRfidDto {

	/** 訪問履歴 */
	public boolean visitFlgs[];

	/** コンストラクタ */
	public LttRfidDto() {
		this.visitFlgs = new boolean[LTTConfig.DAYS.length];
	}

}
