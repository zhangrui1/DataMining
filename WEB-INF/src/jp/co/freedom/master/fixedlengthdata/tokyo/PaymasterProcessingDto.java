package jp.co.freedom.master.fixedlengthdata.tokyo;

import java.util.ArrayList;
import java.util.List;

/**
 * 【固定データ長】棚卸表詳細DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class PaymasterProcessingDto {

	/** 伝票番号 */
	public String paymentNo;

	/** 店コード */
	public String shopCode;

	/** 日付 */
	public String date;

	/** 出荷店コード */
	public String shippingShopCode;

	/** 明細情報 */
	public List<PaymasterProcessingDetailDto> detailInfo;

	/** コンストラクタ */
	public PaymasterProcessingDto() {
		this.detailInfo = new ArrayList<PaymasterProcessingDetailDto>();
	}

}
