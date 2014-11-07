package jp.co.freedom.master.utilities;

import java.util.ArrayList;
import java.util.List;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;

/**
 * 妥当性検証結果
 *
 * @author フリーダム・グループ
 *
 */
public abstract class Validator {

	/** タイプ1の妥当性(ワイルドカードの存在チェック)検証結果のブール値 */
	private boolean type1Result;

	/** タイプ1の妥当性違反理由 */
	private List<String> type1InvalidInfo;

	/** タイプ2の妥当性検証(氏名、連絡先情報に対する妥当性検証)結果のブール値 */
	private boolean type2Result;

	/** 氏名情報に対する妥当性検証結果 */
	private boolean nameIsValid;

	/** 連絡先情報に対する妥当性検証結果 */
	private boolean contactIsValid;

	/**
	 * コンストラクタ
	 */
	public Validator() {
		this.type1InvalidInfo = new ArrayList<String>();
	}

	/**
	 * 全ての項目を妥当性違反に設定
	 */
	public void allValidationError() {
		this.type1Result = false;
		this.type2Result = false;
		this.nameIsValid = false;
		this.contactIsValid = false;
	}

	/**
	 * タイプ1の妥当性検証結果のブール値を返却
	 *
	 * @return タイプ1の妥当性検証結果のブール値
	 */
	public boolean getResult1() {
		return type1Result;
	}

	/**
	 * タイプ1の妥当性検証結果の格納
	 *
	 * @param type1Result
	 *            タイプ1の妥当性検証結果
	 */
	public void setResult1(boolean type1Result) {
		this.type1Result = type1Result;
	}

	/**
	 * タイプ1の妥当性違反理由を取得
	 *
	 * @return タイプ1の妥当性違反理由
	 */
	public String getType1InvalidInfo() {
		return StringUtil.concatWithDelimit(",", this.type1InvalidInfo);
	}

	/**
	 * タイプ1の妥当性違反理由を格納
	 *
	 * @param value
	 *            妥当性違反理由
	 */
	public void addType1InvalidInfo(String value) {
		this.type1InvalidInfo.add(value);
	}

	/**
	 * タイプ2の妥当性検証結果のブール値を返却
	 *
	 * @return タイプ1の妥当性検証結果のブール値
	 */
	public boolean getResult2() {
		return type2Result;
	}

	/**
	 * タイプ2の妥当性検証結果の格納
	 *
	 * @param type2Result
	 *            タイプ2の妥当性検証結果
	 */
	public void setResult2(boolean type2Result) {
		this.type2Result = type2Result;
	}

	/**
	 * タイプ2の氏名情報に対する妥当性検証結果の格納
	 *
	 * @param nameIsValid
	 *            　氏名情報に対する妥当性検証結果
	 */
	public void setNameIsValid(boolean nameIsValid) {
		this.nameIsValid = nameIsValid;
	}

	/**
	 * タイプ2の連絡先情報に対する妥当性検証結果の格納
	 *
	 * @param nameIsValid
	 *            　連絡先情報に対する妥当性検証結果
	 */
	public void setContactIsValid(boolean contactIsValid) {
		this.contactIsValid = contactIsValid;
	}

	/**
	 * タイプ2に対する妥当性違反の詳細情報を返却
	 *
	 * @return　タイプ2に対する妥当性違反の詳細情報。タイプ2に対する妥当性検証結果がtrueである場合にはnullを返却
	 */
	public String getResultDetail() {
		if (!this.type2Result) {
			List<String> info = new ArrayList<String>();
			if (!this.nameIsValid) {
				info.add("氏名");
			}
			if (!this.contactIsValid) {
				info.add("連絡先");
			}
			return StringUtil.concatWithDelimit(",", info);
		} else if (!this.type1Result) {
			return this.getType1InvalidInfo();
		}
		return null;
	}

	/**
	 * 妥当性検証の実行
	 *
	 * @param userdata
	 *            　<b>UserDataDto</b>
	 * @param validationType
	 *            妥当性検証の種別
	 */
	public abstract void validate(UserDataDto userdata, String validationType);
}
