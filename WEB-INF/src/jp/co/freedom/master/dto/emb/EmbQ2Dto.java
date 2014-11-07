package jp.co.freedom.master.dto.emb;

import java.util.ArrayList;
import java.util.List;

import jp.co.freedom.common.utilities.StringUtil;

/**
 * 【Embedded Technology 2013】アンケート設問2データ管理クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class EmbQ2Dto {

	/** メーカーID */
	public int makerId;
	/** 担当者ID */
	public String personId;
	/** 担当者名FA */
	public String personFa;

	/**
	 * メーカー名の取得
	 * 
	 * @return メーカー名
	 */
	public String getMakerName() {
		if (1 == this.makerId) {
			return "コアスタッフ";
		} else if (2 == this.makerId) {
			return "梅沢無線電機";
		} else if (3 == this.makerId) {
			return "東京エレクトロンデバイス";
		} else if (4 == this.makerId) {
			return "ノバラックス／新光商事";
		} else if (5 == this.makerId) {
			return "スズデン";
		} else if (6 == this.makerId) {
			return "福西電機";
		} else if (7 == this.makerId) {
			return "アットマークテクノ";
		}
		return "";
	}

	/**
	 * 担当者名の取得
	 * 
	 * @return 担当者名
	 */
	public String getPersonName() {
		List<String> persons = new ArrayList<String>();
		if (StringUtil.isNotEmpty(this.personId)) {
			String[] personBuff = this.personId.split(" ");
			if (1 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("山中");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("山田");
				}
			} else if (2 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("橋野");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("岸田");
				}
			} else if (3 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("加藤");
				}
			} else if (4 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("小松");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("伊藤");
				}
			} else if (5 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("高山");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("品川");
				}
				if (StringUtil.contains(personBuff, "3")) {
					persons.add("渡辺");
				}
				if (StringUtil.contains(personBuff, "4")) {
					persons.add("原");
				}
			} else if (6 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("清水");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("稲田");
				}
			} else if (7 == this.makerId) {
				if (StringUtil.contains(personBuff, "1")) {
					persons.add("森島");
				}
				if (StringUtil.contains(personBuff, "2")) {
					persons.add("吉田");
				}
				if (StringUtil.contains(personBuff, "3")) {
					persons.add("砂川");
				}
				if (StringUtil.contains(personBuff, "4")) {
					persons.add("大野");
				}
				if (StringUtil.contains(personBuff, "5")) {
					persons.add("沼澤");
				}
				if (StringUtil.contains(personBuff, "6")) {
					persons.add("橋爪");
				}
				if (StringUtil.contains(personBuff, "7")) {
					persons.add("佐々木拓");
				}
				if (StringUtil.contains(personBuff, "8")) {
					persons.add("尾藤");
				}
				if (StringUtil.contains(personBuff, "9")) {
					persons.add("中井");
				}
				if (StringUtil.contains(personBuff, "10")) {
					persons.add("実吉");
				}
				if (StringUtil.contains(personBuff, "11")) {
					persons.add("花田");
				}
				if (StringUtil.contains(personBuff, "12")) {
					persons.add("竹之下");
				}
				if (StringUtil.contains(personBuff, "13")) {
					persons.add("古関");
				}
				if (StringUtil.contains(personBuff, "14")) {
					persons.add("伊藤透");
				}
				if (StringUtil.contains(personBuff, "15")) {
					persons.add("佐々木大");
				}
				if (StringUtil.contains(personBuff, "16")) {
					persons.add("原田");
				}
				if (StringUtil.contains(personBuff, "17")) {
					persons.add("大山");
				}
				if (StringUtil.contains(personBuff, "18")) {
					persons.add("大澤");
				}
				if (StringUtil.contains(personBuff, "19")) {
					persons.add("石田");
				}
			}
		}
		if (StringUtil.isNotEmpty(this.personFa)) {
			persons.add(this.personFa);
		}
		return StringUtil.concatWithDelimit(",", persons);
	}
}
