package jp.co.freedom.master.fixedlengthdata.tokyo;

/**
 * 【固定データ長】棚卸表詳細DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class TanaoroshiDetailDto {

	/** 取消 */
	public String cancel;

	/** 部門 */
	public String dept;

	/** 仕入先 */
	public String supplier;

	/** 金額 */
	public int money;

	/** 金額(文字列) */
	public String moneyStr;

	/** 数値 */
	public int amount;

	/** 数値(文字列) */
	public String amountStr;

	/** コンストラクタ */
	public TanaoroshiDetailDto() {
	}

}
