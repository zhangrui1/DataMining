package jp.co.freedom.master.dto.mesago;

import jp.co.freedom.master.utilities.mesago.MesagoConfig;

/**
 * 【MESAGO】RFIDデータDTO
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoRfidDto {

	/** 訪問履歴 */
	public boolean visitFlgs[];

	/** コンストラクタ */
	public MesagoRfidDto() {
		this.visitFlgs = new boolean[MesagoConfig.DAYS.length];
	}

}
