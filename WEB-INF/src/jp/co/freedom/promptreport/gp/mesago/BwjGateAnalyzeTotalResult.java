package jp.co.freedom.promptreport.gp.mesago;

/**
 * 【BWJ専用】ホール×時間×日ごとの総数DTO
 * 
 * @author フリーダム・グループ
 * 
 */
public class BwjGateAnalyzeTotalResult {

	/** 当日受付の総数 */
	public int appointeddayTotal;

	/** 事前登録の総数 */
	public int preentryTotal;

	/** リピーターの総数 */
	public int repeatTotal;

	/** 総合計数 */
	public int totalTotal;

	/** コンストラクタ */
	public BwjGateAnalyzeTotalResult() {
		this.appointeddayTotal = 0;
		this.preentryTotal = 0;
		this.repeatTotal = 0;
		this.totalTotal = 0;
	}

}
