package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove34StudentDataTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 3. 業種区分が「学生(BN14）」かつ会社名が空欄であるデータは削除 Junitテスト
	 */
	public void testRemoveEnglishJapanData01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions3;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions3;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions3;
		assertEquals(false, exist);
	}

	/**
	 * 4. 業種区分が「学生(BN14）」で、社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」
	 * または未選択であるデータは削除 Junitテスト
	 */
	public void testRemoveEnglishJapanData04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData10() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData11() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData12() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData13() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島専門学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData14() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学学校法人";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData15() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学学校法人";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData16() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学学校法人";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData17() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学学校法人";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData18() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島学校学校法人";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData19() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData20() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大（学）学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData21() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島大（学）学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "BN14";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData22() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島（学）大学";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData23() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "徳島（学）学校";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他学生";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_code = "";
		((MesagoQuestionDto) userdate.questionInfo).V_Q4_kubun = "その他";
		DataCleansingForMesagoUtil.removeStudentData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions4;
		assertEquals(false, exist);
	}
}
