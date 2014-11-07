package jp.co.freedom.master.utilities.fpd;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.fpd.FpdUserDataDto;
import jp.co.freedom.master.utilities.Validator;

/**
 * 【FPD】名刺情報DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class FpdValidator extends Validator {

	/** コンストラクタ */
	public FpdValidator() {
		super();
	}

	@Override
	public void validate(UserDataDto userdata, String validationType) {
		assert userdata != null;
		if (userdata.cardInfo == null) {
			this.allValidationError();
		}
		if ("1".equals(validationType)) {
			/*
			 * 不明マーク(■)の存在チェック
			 */
			// 氏名情報
			boolean V_NAME1 = !StringUtil
					.containWildcard(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = !StringUtil
					.containWildcard(userdata.cardInfo.V_NAME2); // 事前登録ユーザーの場合には姓名名は空文字列
			// 連絡先情報
			boolean V_ZIP = !StringUtil
					.containWildcard(userdata.cardInfo.V_ZIP);
			boolean V_ADDR1 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR1);
			boolean V_ADDR2 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR2);
			boolean V_ADDR3 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR3);
			boolean V_ADDR4 = !StringUtil
					.containWildcard(userdata.cardInfo.V_ADDR4);
			boolean V_TEL = !StringUtil
					.containWildcard(userdata.cardInfo.V_TEL);
			boolean V_FAX = !StringUtil
					.containWildcard(userdata.cardInfo.V_FAX);
			// メールアドレス
			boolean V_EMAIL = !StringUtil
					.containWildcard(userdata.cardInfo.V_EMAIL);

			this.setResult1(V_NAME1 && V_NAME2 && V_ZIP && V_ADDR1 && V_ADDR2
					&& V_ADDR3 && V_ADDR4 && V_TEL && V_FAX && V_EMAIL);
		} else {
			/*
			 * 必須制約違反の検証
			 */
			FpdUtil util = new FpdUtil();
			// 氏名情報
			boolean V_NAME1 = StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1);
			boolean V_NAME2 = FpdUtil.isOversea((FpdUserDataDto) userdata)
					|| util.isPreEntry(userdata) ? true : StringUtil
					.isNotEmpty(userdata.cardInfo.V_NAME2);
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
			boolean addressIsValid = FpdUtil
					.isOversea((FpdUserDataDto) userdata) ? V_ADDR2 : V_ZIP
					&& V_ADDR1 && V_ADDR2 && V_ADDR3 && V_ADDR4;

			this.setNameIsValid(nameIsValid);
			this.setContactIsValid(addressIsValid || V_TEL || V_FAX || V_EMAIL);
			this.setResult2(nameIsValid
					& (addressIsValid || V_TEL || V_FAX || V_EMAIL));
		}
	}
}
