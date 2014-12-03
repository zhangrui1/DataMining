package jp.co.freedom.master.dto.valdac;

import java.util.Map;

/**
 * 【LTT】ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class ValdacUserDataDto  {

	/** 機器システムID */
	public String id;

	/** 機器システムID */
	public String KikiSysId;

	/** 旧機器システムID */
	public String KikiSysIdOld;

	/** 機器ID */
	public String kikiID;

	/** 機器ID */
	public String kikiIDOld;

	/** 部品ID */
	public String buhinID;

	/** 機器ID */
	public String buhinIDOld;

	/** 工事ID */
	public String koujiID;

	/** 工事ID */
	public String koujiIDOld;

//	/** 点検機器用 */
	public String tenkenSisin;
	public String tenkenRank;
	public String tenkenNaiyo;
	public String gyosya;
	public String tenkenKekka0;
	public String tenkenKekka1;
	public String tenkenKekka2;
	public String tenkenKekka3;
	public String tenkenKekka4;
	public String tenkenKekka5;

	public String KanryoFlg;
	public String tenkenNendo;


//	/** 懸案事項用 */
	public String kenanNo;
	public String hakkenDate;
	public String taisakuDate;
	public String taiouFlg;
	public String jisyo;
	public String buhin;
	public String gensyo;
	public String youin;
	public String taisaku;
	public String hakkenJyokyo;
	public String syotiNaiyou;
	public String trkDate;
	public String updDate;

//画像部分

	public String imagesyu;
	public String page;
	public String imagename;
	public String papersize;
	public String imagebiko;
	public String tosyoMei;

//顧客部分
	public String kCode;
	public String kName;
	public String kNameNew;
	public String kCodeL;
	public String kCodeLKanji;
	public String kCodeM;
	public String kCodeMKanji;
	public String kCodeS;
	public String kCodeSKanji;
	/** 機器ID */
	public Map<String,ValdacBuhiDto> kikiList;

	/** コンストラクタ */
	public ValdacUserDataDto() {
	}

}
