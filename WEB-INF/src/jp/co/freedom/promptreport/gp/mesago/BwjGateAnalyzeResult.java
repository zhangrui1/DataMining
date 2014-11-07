package jp.co.freedom.promptreport.gp.mesago;

/**
 * 【BWJ専用】ホール×時間×日ごと集計結果DTO
 *
 * @author フリーダム・グループ
 *
 */
public class BwjGateAnalyzeResult {

	/** 区切りの終了時間 */
	public int endTime;

	/** ホール番号 */
	public int hole;

	/** 当日受付数 */
	public int appointedday;

	/** 事前登録数 */
	public int preentry;

	/** リピーター数 */
	public int repeat;

	/** コンストラクタ */
	public BwjGateAnalyzeResult() {

	}

}
