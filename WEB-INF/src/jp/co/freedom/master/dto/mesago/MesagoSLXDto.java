package jp.co.freedom.master.dto.mesago;

/**
 * 【MESAGO】SLXコード情報DTO
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoSLXDto {

	/** 業種区分 */
	public String bizSlx;

	/** 業種区分SLXコード */
	public String bizSlxCode;

	/** 専門分野 */
	public String accSlx;

	/** 専門分野SLXコード */
	public String accSlxCode;

	/** 職種 */
	public String contactPostType;

	/** 職種SLXコード */
	public String contactPostTypeSLXCode;

	/** 役職 */
	public String contactPostGroup;

	/** 役職SLXコード */
	public String contactPostGroupSLXCode;

	/** 従業員数 */
	public String accOfStaff;

	/** 従業員数SLXコード */
	public String accOfStaffSLXCode;

	/** コンストラクタ */
	public MesagoSLXDto() {

	}

	/**
	 * 職種情報の設定
	 *
	 * @param contactPostType
	 *            職種
	 * @param contactPostTypeSLXCode
	 *            職種SLXコード
	 */
	public void setContactPostTypeInfo(String contactPostType,
			String contactPostTypeSLXCode) {
		this.contactPostType = contactPostType;
		this.contactPostTypeSLXCode = contactPostTypeSLXCode;
	}

	/**
	 * 役職情報の設定
	 *
	 * @param contactPostGroup
	 *            役職
	 * @param contactPostGroupSLXCode
	 *            役職SLXコード
	 */
	public void setContactPostGroupInfo(String contactPostGroup,
			String contactPostGroupSLXCode) {
		this.contactPostGroup = contactPostGroup;
		this.contactPostGroupSLXCode = contactPostGroupSLXCode;
	}

	/**
	 * 従業員数情報の設定
	 *
	 * @param accOfStaff
	 *            従業員数
	 * @param accOfStaffSLXCode
	 *            従業員数SLXコード
	 */
	public void setAccOfStaffInfo(String accOfStaff, String accOfStaffSLXCode) {
		this.accOfStaff = accOfStaff;
		this.accOfStaffSLXCode = accOfStaffSLXCode;
	}

}
