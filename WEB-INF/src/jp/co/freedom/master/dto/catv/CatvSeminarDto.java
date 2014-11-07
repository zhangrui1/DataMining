package jp.co.freedom.master.dto.catv;

import java.util.Date;

import jp.co.freedom.master.utilities.catv.CatvUtil;

/**
 * 【CATV】セミナー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public class CatvSeminarDto {

	/** ホール番号 */
	public String hole;

	/** セミナー番号 */
	public String seminar;

	/** 開始時間 */
	public Date start;

	/** 終了時間 */
	public Date end;

	/** コンストラクタ */
	public CatvSeminarDto() {
	}

	/**
	 * 開始時間の設定
	 *
	 * @param startTime
	 *            開始時間(文字列)
	 */
	public void setStart(String startTime) {
		this.start = CatvUtil.convert(startTime);
	}

	/**
	 * 終了時間の設定
	 *
	 * @param endTime
	 *            終了時間(文字列)
	 */
	public void setEnd(String endTime) {
		this.end = CatvUtil.convert(endTime);
	}

}
