package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove2CompletenessDataForEmailAndAddrTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * メアドと住所(ADDR3)の両方が不完全であるデータを削除 Junit用
	 */
	public void testValidatecompletenessDataForEmailAndAddr01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "1-1-4";
		userdate.cardInfo.V_ADDR4 = "マリンピタ";
		userdate.cardInfo.V_EMAIL = "zui@ned-data.jp";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "東沖洲";
		userdate.cardInfo.V_ADDR4 = "マリンピタ";
		userdate.cardInfo.V_EMAIL = "zui@ned-data.jp";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "";
		userdate.cardInfo.V_ADDR4 = "";
		userdate.cardInfo.V_EMAIL = "zui@ned-data.jp";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "";
		userdate.cardInfo.V_ADDR4 = "";
		userdate.cardInfo.V_EMAIL = "zu●i@ned-data.jp";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "東沖洲1";
		userdate.cardInfo.V_ADDR4 = "マリンピタ";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "東沖洲";
		userdate.cardInfo.V_ADDR4 = "マリンピタ";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(true, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "東沖洲";
		userdate.cardInfo.V_ADDR4 = "";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(true, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR3 = "";
		userdate.cardInfo.V_ADDR4 = "マリンピタ";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(true, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "兵庫県";
		userdate.cardInfo.V_ADDR2 = "芦屋市";
		userdate.cardInfo.V_ADDR3 = "朝日ケ丘町 19-16-#203";
		userdate.cardInfo.V_ADDR4 = "";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(false, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr10() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "";
		userdate.cardInfo.V_ADDR2 = "";
		userdate.cardInfo.V_ADDR3 = "";
		userdate.cardInfo.V_ADDR4 = "";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(true, exist);
	}

	public void testValidatecompletenessDataForEmailAndAddr11() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "奈良県";
		userdate.cardInfo.V_ADDR2 = "奈良市";
		userdate.cardInfo.V_ADDR3 = "上三条町";
		userdate.cardInfo.V_ADDR4 = "三条三和ビル3F";
		userdate.cardInfo.V_EMAIL = "";
		DataCleansingForMesagoUtil
				.validateCompletenessDataForEmailAndAddr(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions2;
		assertEquals(true, exist);
	}

}
