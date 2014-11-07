package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove5ForEmptyCompanyTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 5. 会社名が空欄でかつ業種区分が「その他」または未選択であるデータを削除 Junitテスト
	 */
	public void testRemoveDataForEmptyCompanyName01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForEmptyCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions5;
		assertEquals(true, exist);
	}

	public void testRemoveDataForEmptyCompanyName02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "";
		DataCleansingForMesagoUtil.removeDataForEmptyCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions5;
		assertEquals(true, exist);
	}

	public void testRemoveDataForEmptyCompanyName03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "NED";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForEmptyCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions5;
		assertEquals(false, exist);
	}

	public void testRemoveDataForEmptyCompanyName04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "NED";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "";
		DataCleansingForMesagoUtil.removeDataForEmptyCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions5;
		assertEquals(false, exist);
	}
}
