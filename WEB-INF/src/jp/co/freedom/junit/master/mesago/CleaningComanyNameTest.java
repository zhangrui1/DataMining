package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningComanyNameTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * 2.会社名に「営業」「工場」「支社」「事業」「（地名）店」「（地名）支店」を含む単語の検索
	 */
	public void testSearchCompanyNameContainsNGKeyword01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED徳島営業";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	public void testSearchCompanyNameContainsNGKeyword02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED徳島工場";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	public void testSearchCompanyNameContainsNGKeyword03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED徳島支社";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	public void testSearchCompanyNameContainsNGKeyword04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED徳島事業";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	public void testSearchCompanyNameContainsNGKeyword05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED徳島支店";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	public void testSearchCompanyNameContainsNGKeyword06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社NED工場徳島";
		DataCleansingForMesagoUtil.searchCompanyNameContainsNGKeyword(userdate);
		boolean check2Result = userdate.result.check2Result;
		assertTrue(check2Result);
	}

	/*
	 * 3.会社名がNGワードである場合は除去
	 */

	public void testRemoveCompanyNameContainsNGKeyword01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "個人";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "なし";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword02_1() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「なし」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword02_2() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「なし";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword02_3() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "なし」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "無し";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword03_1() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「無し";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword03_2() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "無し」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword03_3() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「無し」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ナシ";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword04_1() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「ナシ";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword04_2() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ナシ」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword04_3() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "「ナシ」";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "無職";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword05_1() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "無職";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "自営";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "準備中";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "個人エステ";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "個人事業";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword10() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "自営業";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword11() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "一般";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword12() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "無";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword13() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "‐";// 全ハイフン
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword14() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ー";// 長音
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword15() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "－";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword16() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "―";// 全ダッシュ
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword17() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "-";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals(null, check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword18() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社無職";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("株式会社無職", check2Result);
	}

	public void testRemoveCompanyNameContainsNGKeyword19() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "株式会社なし";
		DataCleansingForMesagoUtil.removeCompanyNameContainsNGKeyword(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("株式会社なし", check2Result);
	}

	/*
	 * 4.法人格前後のホワイトスペース除去
	 */
	public void testRemoveWhiteSpaceOnCompany01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = " (有)NED徳島データ部";// 前
																			// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(有) NED徳島データ部";// 後
																			// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = " (有) NED徳島データ部";// 前後
																				// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "　(有)NED徳島データ部";// 前
																			// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(有)　NED徳島データ部";// 後
																			// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "　(有)　NED徳島データ部";// 前後
																				// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (有)NED徳島データ部";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(有)  NED徳島データ部";// 後
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (有)  NED徳島データ部";// 前後
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（有）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany11() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = " (株)NED徳島データ部";// 前
																			// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany12() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(株) NED徳島データ部";// 後
																			// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany13() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = " (株) NED徳島データ部";// 前後
																				// 半角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany14() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "　(株)NED徳島データ部";// 前
																			// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany15() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(株)　NED徳島データ部";// 後
																			// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany16() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "　(株)　NED徳島データ部";// 前後
																				// 全角スペース
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany17() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (株)NED徳島データ部";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany18() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(株)  NED徳島データ部";// 後
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany19() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (株)  NED徳島データ部";// 前後
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany20() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (医)NED徳島データ部";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（医）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany21() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  (公財)NED徳島データ部";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（公財）NED徳島データ部", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany22() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  　　  　 　 　株式会社evahline(エヴァライン) 　 　　  　　　    　　　  　 　　　";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）evahline（エヴァライン）", check2Result);
	}

	public void testRemoveWhiteSpaceOnCompany23() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "  株式會社NED徳島(データ)部";// 前
																				// 半角スペース
																				// 二つ
		DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdate);
		String check2Result = ((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE;
		assertEquals("（株）NED徳島（データ）部", check2Result);
	}

}
