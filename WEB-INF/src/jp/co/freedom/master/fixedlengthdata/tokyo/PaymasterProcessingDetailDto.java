package jp.co.freedom.master.fixedlengthdata.tokyo;

/**
 * 【固定データ長】棚卸表詳細DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class PaymasterProcessingDetailDto {

	/** 部門 */
	public String dept;

	/** 単価 */
	public long price;

	/** 単価(文字列) */
	public String priceStr;

	/** 金額 */
	public long money;

	/** 金額(文字列) */
	public String moneyStr;

	/** 数値 */
	public long amount;

	/** 数値(文字列) */
	public String amountStr;

	/** 売価 */
	public long sale;

	/** 売価(文字列) */
	public String saleStr;

	/** コンストラクタ */
	public PaymasterProcessingDetailDto() {
	}

}
