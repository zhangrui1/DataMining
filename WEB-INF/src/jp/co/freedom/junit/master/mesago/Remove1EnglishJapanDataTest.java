package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove1EnglishJapanDataTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 1.住所が全て英語で入力されている国名が「Japan」であるデータを削除 Junitテスト
	 */
	public void testRemoveEnglishJapanData01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "china";
		userdate.cardInfo.V_ADDR2 = "beijing";
		userdate.cardInfo.V_ADDR3 = "chaoyang";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(true, exist);
	}

	public void testRemoveEnglishJapanData02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "china";
		userdate.cardInfo.V_ADDR2 = "beijing";
		userdate.cardInfo.V_ADDR3 = "chaoyang";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "China";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "東京都";
		userdate.cardInfo.V_ADDR2 = "beijing";
		userdate.cardInfo.V_ADDR3 = "chaoyang";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "東京都";
		userdate.cardInfo.V_ADDR2 = "港区";
		userdate.cardInfo.V_ADDR3 = "chaoyang";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "東京都";
		userdate.cardInfo.V_ADDR2 = "港区";
		userdate.cardInfo.V_ADDR3 = "大橋";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(false, exist);
	}

	public void testRemoveEnglishJapanData06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_ADDR1 = "Tokyo";
		userdate.cardInfo.V_ADDR2 = "Minatoku";
		userdate.cardInfo.V_ADDR3 = "obashi";
		userdate.cardInfo.V_ADDR4 = "1-1-4";
		userdate.cardInfo.V_COUNTRY = "Japan";
		DataCleansingForMesagoUtil.removeEnglishJapanData(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions1;
		assertEquals(true, exist);
	}

}
