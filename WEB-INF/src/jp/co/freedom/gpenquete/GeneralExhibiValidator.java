package jp.co.freedom.gpenquete;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.utilities.Validator;

/**
 * 【展示会納品データ集計】妥当性検証
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralExhibiValidator extends Validator {

	private String undecipherableMark;

	/**
	 * コンストラクタ
	 *
	 * @param mark
	 *            不明マーク
	 */
	public GeneralExhibiValidator(String mark) {
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
			if (!V_NAME1 || !V_NAME2) {
				this.addType1InvalidInfo("氏名");
			}

			// 会社情報
			boolean V_COMPANY = !StringUtil.containWildcard(
					userdata.cardInfo.V_CORP, this.undecipherableMark);
			if (!V_COMPANY) {
				this.addType1InvalidInfo("会社名");
			}

			boolean V_DEPT1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT1, this.undecipherableMark);
			boolean V_DEPT2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT2, this.undecipherableMark);
			boolean V_DEPT3 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT3, this.undecipherableMark);
			boolean V_DEPT4 = !StringUtil.containWildcard(
					userdata.cardInfo.V_DEPT4, this.undecipherableMark);
			boolean V_DEPT = V_DEPT1 && V_DEPT2 && V_DEPT3 && V_DEPT4;
			if (!V_DEPT) {
				this.addType1InvalidInfo("部署");
			}

			boolean V_BIZ1 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ1, this.undecipherableMark);
			boolean V_BIZ2 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ2, this.undecipherableMark);
			boolean V_BIZ3 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ3, this.undecipherableMark);
			boolean V_BIZ4 = !StringUtil.containWildcard(
					userdata.cardInfo.V_BIZ4, this.undecipherableMark);
			boolean V_BIZ = V_BIZ1 && V_BIZ2 && V_BIZ3 && V_BIZ4;
			if (!V_BIZ) {
				this.addType1InvalidInfo("所属");
			}

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
			if (!V_ZIP || !V_ADDR) {
				this.addType1InvalidInfo("住所");
			}

			boolean V_TEL = !StringUtil.containWildcard(
					userdata.cardInfo.V_TEL, this.undecipherableMark);
			if (!V_TEL) {
				this.addType1InvalidInfo("TEL");
			}

			boolean V_FAX = !StringUtil.containWildcard(
					userdata.cardInfo.V_FAX, this.undecipherableMark);
			if (!V_FAX) {
				this.addType1InvalidInfo("FAX");
			}

			// メールアドレス
			boolean V_EMAIL = !StringUtil.containWildcard(
					userdata.cardInfo.V_EMAIL, this.undecipherableMark);
			if (!V_EMAIL) {
				this.addType1InvalidInfo("EMAIL");
			}

			this.setResult1(V_NAME1 && V_NAME2 && V_COMPANY && V_DEPT && V_BIZ
					&& V_ZIP && V_ADDR && V_TEL && V_FAX && V_EMAIL);
		} else {
			/*
			 * 必須制約違反の検証
			 */
			// 氏名情報
			boolean V_NAME1 = StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = GeneralPurposeEnqueteMiningUtil
					.isOversea(userdata)
			/* || GeneralPurposeEnqueteMiningUtil.isPreEntry(userdata) */? true
					: StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2);
			// 連絡先情報
			boolean V_ZIP = StringUtil.isNotEmpty(userdata.cardInfo.V_ZIP);
			boolean V_ADDR1 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1);
			boolean V_ADDR2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
			boolean V_ADDR3 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR3);
			boolean V_ADDR4 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4);
			boolean V_TEL = StringUtil.isNotEmpty(userdata.cardInfo.V_TEL);
			boolean V_FAX = StringUtil.isNotEmpty(userdata.cardInfo.V_FAX);
			// メールアドレス
			boolean V_EMAIL = StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL);

			// 氏名情報の欠落フラグ
			boolean nameIsValid = V_NAME1 && V_NAME2;
			// 連絡先情報の欠落フラグ
			boolean addressIsValid = GeneralPurposeEnqueteMiningUtil
					.isOversea(userdata) ? V_ADDR2 : V_ZIP && V_ADDR1
					&& V_ADDR2 && V_ADDR3 && V_ADDR4;

			this.setNameIsValid(nameIsValid);
			this.setContactIsValid(addressIsValid || V_TEL || V_FAX || V_EMAIL);
			this.setResult2(nameIsValid
					& (addressIsValid || V_TEL || V_FAX || V_EMAIL));
		}
	}
}
