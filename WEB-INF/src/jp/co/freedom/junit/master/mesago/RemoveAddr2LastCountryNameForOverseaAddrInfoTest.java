package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class RemoveAddr2LastCountryNameForOverseaAddrInfoTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRemoveLastCountry01() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "山西省 DAIMON ＫoreＡ";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Korea";
		DataCleansingForMesagoUtil
				.removeAddr2LastCountryNameForOverseaAddrInfo(userdata);
		assertEquals("山西省 DAIMON", userdata.cardInfo.V_ADDR2);
	}

	public void testRemoveLastCountry02() {
		MesagoUserDataDto userdata = new MesagoUserDataDto();
		((MesagoCardDto) userdata.cardInfo).V_ADDR2 = "Tokushima, Japan";
		((MesagoCardDto) userdata.cardInfo).V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil
				.removeAddr2LastCountryNameForOverseaAddrInfo(userdata);
		assertEquals("Tokushima, Japan", userdata.cardInfo.V_ADDR2);
	}

}
