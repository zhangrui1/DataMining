package jp.co.freedom.master.fixedlengthdata.tokyo;

import java.util.ArrayList;
import java.util.List;

/**
 * 【固定データ長】棚卸表DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class TanaoroshiTableDto {

	/** 伝票番号 */
	public String paymentNo;

	/** 店コード */
	public String shopCode;

	/** 明細情報 */
	public List<TanaoroshiDetailDto> detailInfo;

	/** コンストラクタ */
	public TanaoroshiTableDto() {
		this.detailInfo = new ArrayList<TanaoroshiDetailDto>();
	}

}
