package jp.co.freedom.master.dto.mesago.ip;

import jp.co.freedom.master.utilities.mesago.ip.IpConfig;

/**
 * 【IP】RFIDデータDTO
 *
 * @author フリーダム・グループ
 *
 */
public class IpRfidDto {

	/** 訪問履歴 */
	public boolean visitFlgs[];

	/** コンストラクタ */
	public IpRfidDto() {
		this.visitFlgs = new boolean[IpConfig.DAYS.length];
	}

}
