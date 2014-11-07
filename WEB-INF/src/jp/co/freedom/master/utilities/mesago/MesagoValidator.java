package jp.co.freedom.master.utilities.mesago;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.Validator;

/**
 * 【MESAGO】妥当性検証
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoValidator extends Validator {

	private String undecipherableMark;

	/**
	 * コンストラクタ
	 *
	 * @param mark
	 *            不明マーク
	 */
	public MesagoValidator(String mark) {
		super();
		this.undecipherableMark = mark;
	}

	@Override
	public void validate(UserDataDto userdata, String validationType) {
		assert userdata != null;
		if (userdata.cardInfo == null) {
			this.allValidationError();
			return;
		}
		if ("1".equals(validationType)) {
			/*
			 * 不明マーク(■)の存在チェック
			 */
			// 氏名情報
			boolean V_NAME1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_NAME1, this.undecipherableMark);
			boolean V_NAME2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_NAME2, this.undecipherableMark); // 事前登録ユーザーの場合には姓名名は空文字列
			// 会社情報
			boolean V_COMPANY = !StringUtil.containWildcard(
					userdata.cardInfo.V_CORP, this.undecipherableMark);
			boolean V_DEPT1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT1, this.undecipherableMark);
			boolean V_DEPT2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT2, this.undecipherableMark);
			boolean V_DEPT3 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT3, this.undecipherableMark);
			boolean V_DEPT4 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT4, this.undecipherableMark);
			boolean V_DEPT = V_DEPT1 && V_DEPT2 && V_DEPT3 && V_DEPT4;

			boolean V_BIZ1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ1, this.undecipherableMark);
			boolean V_BIZ2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ2, this.undecipherableMark);
			boolean V_BIZ3 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ3, this.undecipherableMark);
			boolean V_BIZ4 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ4, this.undecipherableMark);
			boolean V_BIZ = V_BIZ1 && V_BIZ2 && V_BIZ3 && V_BIZ4;
			// 連絡先情報
			boolean V_ZIP = !StringUtil.containWildcard(
					userdata.cardInfo.V_ZIP, this.undecipherableMark);
			boolean V_ADDR1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_ADDR1, this.undecipherableMark);
			boolean V_ADDR2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_ADDR2, this.undecipherableMark);
			boolean V_ADDR3 = !StringUtil.containWildcard(
					userdata.cardInfo.V_ADDR3, this.undecipherableMark);
			boolean V_ADDR4 = !StringUtil.containWildcard(
					userdata.cardInfo.V_ADDR4, this.undecipherableMark);
			boolean V_ADDR = V_ADDR1 && V_ADDR2 && V_ADDR3 && V_ADDR4;
			boolean V_TEL = !StringUtil.containWildcard(
					userdata.cardInfo.V_TEL, this.undecipherableMark);
			boolean V_FAX = !StringUtil.containWildcard(
					userdata.cardInfo.V_FAX, this.undecipherableMark);
			// メールアドレス
			boolean V_EMAIL = !StringUtil.containWildcard(
					userdata.cardInfo.V_EMAIL, this.undecipherableMark);

			this.setResult1(V_NAME1 && V_NAME2 && V_COMPANY && V_DEPT && V_BIZ
					&& V_ZIP && V_ADDR && V_TEL && V_FAX && V_EMAIL);
		} else {
			/*
			 * 必須制約違反の検証
			 */
			MesagoUtil util = new MesagoUtil();
			// 氏名情報
			boolean V_NAME1 = StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = MesagoUtil
					.isOversea((MesagoUserDataDto) userdata)
					|| util.isPreEntry(userdata) || util.isPreVIP(userdata) ? true
					: StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2);
			// 連絡先情報
			boolean V_ZIP = StringUtil.isNotEmpty(userdata.cardInfo.V_ZIP);
			boolean V_ADDR1 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1);
			boolean V_ADDR2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean V_ADDR3 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR3);
			// boolean V_ADDR4 =
			// StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4);
			// V_ADDR4はビル名で
			boolean V_ADDR4 = true;
			boolean V_TEL = StringUtil.isNotEmpty(userdata.cardInfo.V_TEL);
			boolean V_FAX = StringUtil.isNotEmpty(userdata.cardInfo.V_FAX);
			// メールアドレス
			boolean V_EMAIL = StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL);

			// 氏名情報の欠落フラグ
			boolean nameIsValid = V_NAME1 && V_NAME2;
			// 連絡先情報の欠落フラグ
			boolean addressIsValid = MesagoUtil
					.isOversea((MesagoUserDataDto) userdata) ? V_ADDR2 : V_ZIP
					&& V_ADDR1 && V_ADDR2 && V_ADDR3 && V_ADDR4;

			this.setNameIsValid(nameIsValid);
			this.setContactIsValid(addressIsValid || V_TEL || V_FAX || V_EMAIL);
			this.setResult2(nameIsValid
					& (addressIsValid || V_TEL || V_FAX || V_EMAIL));
		}
	}
}
