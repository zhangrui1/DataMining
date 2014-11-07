package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class ValidateAddressInfoTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testValidateAddressInfo_01() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "新宿区2丁目";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_01_2() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "2新宿区丁目";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_01_3() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "新宿区丁目2-1";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_02_2() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = "33新宿区西新宿";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertEquals(false, userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_02_3() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "３新宿区西新";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_03() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR4 = "1丁目";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_03_2() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR4 = "1番地";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

	public void testValidateAddressInfo_03_3() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR4 = "1丁";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.validateAddressInfo(userdata);
		assertTrue(userdata.result.checkAddrResult);
	}

}
