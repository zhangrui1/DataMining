package jp.co.freedom.master.dto;

/**
 * 名刺情報DTO
 *
 * @author フリーダム・グループ
 *
 */
public abstract class CardDto {

	/** バーコード番号 */
	public String V_VID;

	/** CID番号 */
	public String V_CID;

	/** バスワード */
	public String V_PASSWORD;

	/** タイトル番号 */
	public String V_TITLE;

	/** タイトル */
	public String _V_TITLE;

	/** 氏名姓漢字 */
	public String V_NAME1;

	/** 氏名名漢字 */
	public String V_NAME2;

	/** 姓名(英語) */
	public String V_NAME3;

	/** 氏名姓カナ */
	public String V_NAMEKANA1;

	/** 氏名名カナ */
	public String V_NAMEKANA2;

	/** 会社名 */
	public String V_CORP;

	/** 会社名カナ */
	public String V_CORP_KANA;

	/** 部署1 */
	public String V_DEPT1;

	/** 部署2 */
	public String V_DEPT2;

	/** 部署3 */
	public String V_DEPT3;

	/** 部署4 */
	public String V_DEPT4;

	/** 役職1 */
	public String V_BIZ1;

	/** 役職2 */
	public String V_BIZ2;

	/** 役職3 */
	public String V_BIZ3;

	/** 役職4 */
	public String V_BIZ4;

	/** 郵便番号 */
	public String V_ZIP;

	/** 国名 */
	public String V_COUNTRY;

	/** 都道府県 */
	public String V_ADDR1;

	/** 市区部 */
	public String V_ADDR2;

	/** 町域 */
	public String V_ADDR3;

	/** 丁目番地 */
	public String V_ADDR4;

	/** ビル名 */
	public String V_ADDR5;

	/** 電話番号 */
	public String V_TEL;

	/** FAX番号 */
	public String V_FAX;

	/** メールアドレス */
	public String V_EMAIL;

	/** URL */
	public String V_URL;

	/** 来場日（当日登録のみ） */
	public String V_DAY;

	/** 海外住所フラグ */
	public String V_OVERSEA;

	/** 原票状況手書き（当日登録のみ） */
	public String V_TICKET_HAND;

	/** JPG画像パス（当日登録のみ） */
	public String V_IMAGE_PATH;

	/** 登録日 */
	public String V_DT;

	/** 更新日 */
	public String V_TS;

	/** コンストラクタ */
	public CardDto() {

	}

}
