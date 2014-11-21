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
//	public String tenkenSisin;
//	public String tenkenRank;
//	public String tenkenNaiyo;
//	public String gyosya;
//	public String tenkenKekka0;
//	public String tenkenKekka1;
//	public String tenkenKekka2;
//	public String tenkenKekka3;
//	public String tenkenKekka4;
//	public String tenkenKekka5;


	/** 機器ID */
	public Map<String,ValdacBuhiDto> kikiList;

	/** コンストラクタ */
	public ValdacUserDataDto() {
	}

}
