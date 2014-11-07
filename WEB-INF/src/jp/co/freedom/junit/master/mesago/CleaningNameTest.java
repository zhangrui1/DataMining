package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningNameTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 氏名が空値である場合には「見本市ご担当者」に置換
	 */
	public void testReplaceEmptyName01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		 userdate.cardInfo.V_NAME1 = null;
		DataCleansingForMesagoUtil.replaceEmptyName(userdate);
		assertEquals("見本市ご担当者", userdate.cardInfo.V_NAME1);
	}

	public void testReplaceEmptyName02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		 userdate.cardInfo.V_NAME1 = "張●瑞";
		DataCleansingForMesagoUtil.replaceEmptyName(userdate);
		assertEquals("張●瑞", userdate.cardInfo.V_NAME1);
	}


}
