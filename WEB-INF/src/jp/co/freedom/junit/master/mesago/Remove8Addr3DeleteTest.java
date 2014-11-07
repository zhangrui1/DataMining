package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove8Addr3DeleteTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRemoveAllSpaceFromAddr3_01() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = "　　　　　　   　　　  東　　 沖洲　1 -   1　　    -   　　　　  2　　　 ";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);
		assertEquals("東沖洲1-1-2", userdata.cardInfo.V_ADDR3);
	}

	public void testRemoveAllSpaceFromAddr3_02() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = "東沖洲1-1-2 3";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);
		assertEquals("東沖洲1-1-2 3", userdata.cardInfo.V_ADDR3);
	}

	public void testRemoveAllSpaceFromAddr3_03() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = " 　東沖洲1-1-２　 　 ３";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);
		assertEquals("東沖洲1-1-２ ３", userdata.cardInfo.V_ADDR3);
	}

	public void testRemoveAllSpaceFromAddr3_04() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = " 　東　 沖　 　 　洲 　1- 1 -２  　３   　4 　　５";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);
		assertEquals("東沖洲1-1-２ ３ 4 ５", userdata.cardInfo.V_ADDR3);
	}

	public void testRemoveAllSpaceFromAddr3_05() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR3 = "　東　沖　洲　";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);
		assertEquals("東沖洲", userdata.cardInfo.V_ADDR3);
	}

}
