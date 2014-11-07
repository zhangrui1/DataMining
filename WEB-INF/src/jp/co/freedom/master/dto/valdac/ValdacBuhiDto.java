package jp.co.freedom.master.dto.valdac;

import java.util.ArrayList;

/**
 * 【LTT】RFIDデータDTO
 *
 * @author フリーダム・グループ
 *
 */
public class ValdacBuhiDto {



	/** 部品ID */
	public ArrayList<String> buhinIDList;

	/** コンストラクタ */
	public ValdacBuhiDto() {
		buhinIDList = new ArrayList<String>();
	}

}
