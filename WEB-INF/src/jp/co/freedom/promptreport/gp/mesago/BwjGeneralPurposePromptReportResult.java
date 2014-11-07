package jp.co.freedom.promptreport.gp.mesago;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 【BWJ汎用】速報結果DTO
 *
 * @author フリーダム・グループ
 *
 */
public class BwjGeneralPurposePromptReportResult {

	/** [当日]新規登録数 */
	public Map<String, Integer> newRegistApp;

	/** [事前]新規登録数 */
	public Map<String, Integer> newRegistPre;

	/** リピーター数 */
	public Map<String, Integer> repeat;

	/** コンストラクタ */
	public BwjGeneralPurposePromptReportResult() {
		this.newRegistApp = new LinkedHashMap<String, Integer>();
		this.newRegistPre = new LinkedHashMap<String, Integer>();
		this.repeat = new LinkedHashMap<String, Integer>();
	}
}
