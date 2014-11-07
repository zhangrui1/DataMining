package jp.co.freedom.crosstabulation;

import java.util.List;

/**
 * 【たき工房】ランキングデータDTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class LankingForTakiDto {

	/** 順位 */
	public int rank;

	/** 年齢 */
	public String age;

	/** 職業 */
	public String biz;

	/** 件数 */
	public int count;

	/** 使用洗剤内訳件数データ */
	public List<Integer> details;

	/**
	 * コンストラクタ
	 */
	public LankingForTakiDto() {

	}

	/**
	 * コンストラクタ
	 * 
	 * @param rank
	 *            順位
	 * @param age
	 *            年齢
	 * @param biz
	 *            職業
	 * @param count
	 *            件数
	 */
	public LankingForTakiDto(int rank, String age, String biz, int count) {
		this.rank = rank;
		this.age = age;
		this.biz = biz;
		this.count = count;
	}

}
