package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningURLTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 不正なURLを削除
	 */
	public void testRemoveGarbageUrll01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_URL = "http://www.ned-data.jp";
		DataCleansingForMesagoUtil.removeGarbageUrl(userdate);
		assertEquals("http://www.ned-data.jp", userdate.cardInfo.V_URL);
	}

	public void testRemoveGarbageUrll02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_URL = "http://www.n@ed-data.jp";
		DataCleansingForMesagoUtil.removeGarbageUrl(userdate);
		assertEquals(null, userdate.cardInfo.V_URL);
	}

	public void testRemoveGarbageUrll03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_URL = "http://www.n＠ed-data.jp";
		DataCleansingForMesagoUtil.removeGarbageUrl(userdate);
		assertEquals(null, userdate.cardInfo.V_URL);
	}

	public void testRemoveGarbageUrll05() {
		String url = "http://www.ned-data.jp";
		assertEquals(true, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll06() {
		String url = "http://www.n@ed-data.jp";
		assertEquals(false, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll07() {
		String url = "N/A";
		assertEquals(false, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll08() {
		String url = "http://118.22.30.55:10111";
		assertEquals(true, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll09() {
		String url = "https://118.22.30.55:10111";
		assertEquals(true, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll10() {
		String url = "https://ned-data.jp:10111";
		assertEquals(true, StringUtil.isUrl(url));
	}

	public void testRemoveGarbageUrll11() {
		String url = "https://ned-data:10111";
		assertEquals(true, StringUtil.isUrl(url));
	}

}
