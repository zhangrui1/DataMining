package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.common.utilities.AnalysisTelFax;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningTelFaxTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 不正なTEL番号／FAX番号を削除
	 */
	public void testRemoveGarbageTelFaxNumber01() {// 電話番号
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		userdata.cardInfo.V_TEL = "00-00-0000-0000(0000)";
		AnalysisTelFax answer = new AnalysisTelFax("00", "00", "0000-0000", "0000");
		DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);

		AnalysisTelFax test = new AnalysisTelFax(userdata.cardInfo.V_TEL, true);
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testRemoveGarbageTelFaxNumber02() {// Fax
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		userdata.cardInfo.V_FAX = "00-00-0000-0000";
		AnalysisTelFax answer = new AnalysisTelFax("", "", "", "");
		DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);
		AnalysisTelFax test = new AnalysisTelFax(userdata.cardInfo.V_FAX, false);
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testRemoveGarbageTelFaxNumber03() {// 電話ん番号
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		userdata.cardInfo.V_TEL = "81-00-0000-0000(1234)";
		AnalysisTelFax answer = new AnalysisTelFax("81", "00", "0000-0000", "1234");
		DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);
		AnalysisTelFax test = new AnalysisTelFax(userdata.cardInfo.V_TEL, false);
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testRemoveGarbageTelFaxNumber04() {// Fax
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		userdata.cardInfo.V_FAX = "81-03-1234-5678";
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "1234-5678", "");
		DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);

		AnalysisTelFax test = new AnalysisTelFax(userdata.cardInfo.V_FAX, false);
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testRemoveGarbageTelFaxNumber05() {// Fax
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		userdata.cardInfo.V_FAX = "81-03-123●-5678";
		AnalysisTelFax answer = new AnalysisTelFax("", "", "", "");
		DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);

		AnalysisTelFax test = new AnalysisTelFax(userdata.cardInfo.V_FAX, false);
		test.execute(false);
		assertTrue(test.equals(answer));
	}

}
