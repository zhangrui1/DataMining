package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove6ForGarbageSchoolDataTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 6. 会社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」または未選択であるデータを削除 Junitテスト
	 */

	public void testRemoveDataForGarbageSchoolData00() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(false, exist);
	}

	public void testRemoveDataForGarbageSchoolData01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(false, exist);
	}

	public void testRemoveDataForGarbageSchoolData02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学校法人学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学校法人学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学（学）学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大（学）学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(true, exist);
	}

	public void testRemoveDataForGarbageSchoolData10() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大（学）学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "業種";
		DataCleansingForMesagoUtil.removeDataForGarbageSchoolData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions6;
		assertEquals(false, exist);
	}

}
