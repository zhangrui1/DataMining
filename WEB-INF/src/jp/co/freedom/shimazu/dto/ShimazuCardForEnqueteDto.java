package jp.co.freedom.shimazu.dto;

/**
 * Shimazu向け名刺情報（アンケート用）DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ShimazuCardForEnqueteDto {

	/** 団体代表名 */
	public String COM;

	/** 事業所名 */
	public String COM_OFFICE;

	/** 氏名フルネーム漢字 */
	public String NAME;

	/** 所属名1 */
	public String DEPT1;

	/** 所属名2 */
	public String DEPT2;

	/** 所属名3 */
	public String DEPT3;

	/** 所属名4 */
	public String DEPT4;

	/** 個人姓 */
	public String NAME_LAST;

	/** 個人名 */
	public String NAME_FIRST;

	/** 個人姓カナ */
	public String NAME_LAST_KANA;

	/** 個人名カナ */
	public String NAME_FIRST_KANA;

	/** 役職1 */
	public String POS1;

	/** 役職2 */
	public String POS2;

	/** 役職3 */
	public String POS3;

	/** 役職4 */
	public String POS4;

	/** 郵便番号 */
	public String ZIP;

	/** 都道府県 */
	public String ADDR1;

	/** 市区部 */
	public String ADDR2;

	/** 町域 */
	public String ADDR3;

	/** 丁目番地 */
	public String ADDR4;

	/** ビル名 */
	public String ADDR5;

	/** 電話番号 */
	public String TEL;

	/** FAX番号 */
	public String FAX;

	/** メールアドレス */
	public String EMAIL;

	/**
	 * コンストラクタ
	 */
	public ShimazuCardForEnqueteDto() {

	}
}
