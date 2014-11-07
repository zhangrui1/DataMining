package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class Remove7ComanyDeleteTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * 7.会社名に("税理","税務","銀行","保険","信用金庫","信金","トレードショーオーガナイザーズ",
	 * "TRADESHOW ORGANIZERS INC."
	 * ,"ビジネスガイド社","Business Guide-Sha","リード エグジビション "
	 * ,"Reed Exhibitions ","日本能率協会"
	 * ,"Japan Management Association","日本インテリアファブリックス協会"
	 * ,"Nippon Interior Fabrics Association "
	 * ,"デザインアソシエーション","DESIGN ASSOCIATION NPO"
	 * ,"大阪見本市協会","Osaka International Trade Fair Commission "
	 * ,"ICSコンベンションデザイン","ICS Convention Design, Inc."
	 * ,"シー・エヌ・ティ","CNT Inc.","フジサンケイビジネスアイ"
	 * ,"Fuji Sankei Business i","UBMジャパン","UBM Japan " ,"UBMメディア","UBM Media",
	 * ,"Reed ISG Japan KK","リードジャパン","一般（社）日本能率協会" ,"一般社団法人日本能率協会")を含む単語の検索
	 * 
	 * 「アテックス（株）」を完全一致
	 */
	public void testemoveDataWithNgCompanyName01() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "アテックス（株）";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName02() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "アテックス（株）一部";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(false, exist);
	}

	public void testemoveDataWithNgCompanyName03() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "アテックス（株)";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(false, exist);
	}

	public void testemoveDataWithNgCompanyName40() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "(株)アテックス";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(false, exist);
	}

	public void testemoveDataWithNgCompanyName04() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "税理";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName05() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "税務";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName06() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "銀行";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName07() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "保険";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName08() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "信用金庫";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName09() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "信金";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName10() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "トレードショーオーガナイザーズ";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName11() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "TRADESHOW ORGANIZERS INC.";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName12() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ビジネスガイド社";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName13() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Business Guide-Sha";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName14() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "リード エグジビション";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName15() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Reed Exhibitions ";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName16() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "日本能率協会";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName17() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Japan Management Association";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName18() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "日本インテリアファブリックス協会";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName19() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Nippon Interior Fabrics Association";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName20() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "デザインアソシエーション";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName21() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "DESIGN ASSOCIATION NPO";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName22() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "大阪見本市協会";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName23() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Osaka International Trade Fair Commission";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName24() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ICSコンベンションデザイン";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName25() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "ICS Convention Design, Inc.";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName26() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "シー・エヌ・ティ";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName27() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "CNT Inc.";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName28() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "フジサンケイビジネスアイ";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName29() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Fuji Sankei Business i";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName30() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "UBMジャパン";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName31() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "UBM Japan ";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName32() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "UBMメディア";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName33() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "UBM Media";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName34() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "Reed ISG Japan KK";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName35() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "リードジャパン";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName36() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "一般（社）日本能率協会";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	public void testemoveDataWithNgCompanyName37() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "一般社団法人日本能率協会";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	// 会社名+業種区分が「その他」
	public void testemoveDataWithNgCompanyName38() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "日本経済新聞社";
		((MesagoQuestionDto) userdate.questionInfo).V_Q1_kubun = "その他";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(true, exist);
	}

	// 会社名+業種区分が「その他」
	public void testemoveDataWithNgCompanyName39() {
		MesagoUserDataDto userdate = new MesagoUserDataDto();
		((MesagoCardDto) userdate.cardInfo).V_CORP_UPDATE = "日本経済新聞社";
		userdate.questionInfo.V_Q1 = "1 2";
		DataCleansingForMesagoUtil.removeDataWithNgCompanyName(userdate);
		boolean exist = userdate.result.qualifiedRemoveConditions7;
		assertEquals(false, exist);
	}
}
