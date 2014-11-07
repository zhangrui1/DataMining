package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningEmailTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**不正なメールアドレスを削除
	 * 
	 * 「@」前に「●」が含まれる場合は削除
	 */
	public void testRemoveGarbageEmail01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_EMAIL = "●zui@ned-data.jp";
		DataCleansingForMesagoUtil.removeGarbageEmail(userdate);
		assertEquals(null, userdate.cardInfo.V_EMAIL);
	}

	public void testRemoveGarbageEmail02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_EMAIL = "z●ui@ned-data.jp";
		DataCleansingForMesagoUtil.removeGarbageEmail(userdate);
		assertEquals(null, userdate.cardInfo.V_EMAIL);
	}

	public void testRemoveGarbageEmail03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_EMAIL = "z●u●i@ned-data.jp";
		DataCleansingForMesagoUtil.removeGarbageEmail(userdate);
		assertEquals(null, userdate.cardInfo.V_EMAIL);
	}

	public void testRemoveGarbageEmail04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_EMAIL = "zui●@ned-data.jp";
		DataCleansingForMesagoUtil.removeGarbageEmail(userdate);
		assertEquals(null, userdate.cardInfo.V_EMAIL);
	}

	public void testRemoveGarbageEmail05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		userdate.cardInfo.V_EMAIL = "zui@n●ed-data.jp";
		DataCleansingForMesagoUtil.removeGarbageEmail(userdate);
		assertEquals("zui@n●ed-data.jp", userdate.cardInfo.V_EMAIL);
	}
}
