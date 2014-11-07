package jp.co.freedom.common.dto.zip;

/**
 * 郵便番号サービス向け名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class ZipDto {

	/** 顧客ID */
	public String id;

	/** 郵便番号 */
	public String zip;

	/** 都道府県 */
	public String addr1;

	/** 市区郡 */
	public String addr2;

	/** 町域 */
	public String addr3;

	/**
	 * コンストラクタ
	 */
	public ZipDto() {

	}
}
