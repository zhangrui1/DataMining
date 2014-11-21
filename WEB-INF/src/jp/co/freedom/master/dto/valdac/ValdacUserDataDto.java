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

	/** 機器ID */
	public Map<String,ValdacBuhiDto> kikiList;

	/** コンストラクタ */
	public ValdacUserDataDto() {
	}

}
