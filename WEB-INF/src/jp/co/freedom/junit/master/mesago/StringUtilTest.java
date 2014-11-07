package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;
import junit.framework.TestCase;

/**
 * StringUtilクラスに対するテストケース
 *
 * @author フリーダム・グループ
 *
 */
public class StringUtilTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	// 全角文字列判定
	public void testIsFullWidthString01() {
		assertTrue(StringUtil.isFullWidthString("ジャストシステム"));
	}

	public void testIsFullWidthString02() {
		assertTrue(!StringUtil.isFullWidthString("Justsystem"));
	}

	public void testIsFullWidthString03() {
		assertTrue(!StringUtil.isFullWidthString("ジャストSystem"));
	}

	public void testIsFullWidthString04() {
		assertTrue(StringUtil.isFullWidthString("ジャストＳｙｓｔｅｍ"));
	}

	public void testIsFullWidthString05() {
		assertTrue(StringUtil.isFullWidthString("￥＾ー＊＠＿？＞＜"));
	}

	public void testIsFullWidthString06() {
		assertTrue(!StringUtil.isFullWidthString("￥＾ー＊＠＿？＞<"));
	}

	public void testIsFullWidthString07() {
		assertTrue(!StringUtil.isFullWidthString("\\^-*@_?><"));
	}

	public void testIsFullWidthString08() {
		assertTrue(StringUtil.isFullWidthString("０１２３４５６７８９"));
	}

	// 半角文字列判定
	public void testIsHalfWidthString01() {
		assertTrue(!StringUtil.isHalfWidthString("ジャストシステム"));
	}

	public void testIsHalfWidthString02() {
		assertTrue(StringUtil.isHalfWidthString("Justsystem"));
	}

	public void testIsHalfWidthString03() {
		assertTrue(!StringUtil.isHalfWidthString("ジャストSystem"));
	}

	public void testIsHalfWidthString04() {
		assertTrue(!StringUtil.isHalfWidthString("ジャストＳｙｓｔｅｍ"));
	}

	public void testIsHalfWidthString05() {
		assertTrue(!StringUtil.isHalfWidthString("￥＾ー＊＠＿？＞＜"));
	}

	public void testIsHalfWidthString06() {
		assertTrue(!StringUtil.isHalfWidthString("￥＾ー＊＠＿？＞<"));
	}

	public void testIsHalfWidthString07() {
		assertTrue(StringUtil.isHalfWidthString("\\^-*@_?><"));
	}

	// メールアドレス構文チェック
	public void testEmailAddress01() {
		assertTrue(!StringUtil.isEmailAddress("test"));
	}

	public void testEmailAddress02() {
		assertTrue(!StringUtil.isEmailAddress("test@"));
	}

	public void testEmailAddress03() {
		assertTrue(!StringUtil.isEmailAddress("test@example"));
	}

	public void testEmailAddress04() {
		assertTrue(StringUtil.isEmailAddress("test@example.com"));
	}

	public void testEmailAddress05() {
		assertTrue(StringUtil.isEmailAddress("test@example.co.jp"));
	}

	public void testEmailAddress06() {
		assertTrue(!StringUtil.isEmailAddress("@example.com"));
	}

	public void testEmailAddress07() {
		assertTrue(!StringUtil.isEmailAddress("@example.co.jp"));
	}

	public void testEmailAddress08() {
		assertTrue(StringUtil.isEmailAddress("test.co.jp@example.co.jp"));
	}

	public void testEmailAddress09() {
		assertTrue(!StringUtil.isEmailAddress("●test.co.jp@example.co.jp"));
	}

	public void testEmailAddress10() {
		assertTrue(!StringUtil.isEmailAddress("test.co.jp@exam●ple.co.jp"));
	}

	// 住所に番地が含まれているか
	public void testContainsHouseNumber01() {
		assertTrue(StringUtil.find("千代田橋1-1-13-809", "[0-9-]+"));
	}

	public void testContainsHouseNumber02() {
		assertTrue(StringUtil.find("83794490", "\\d"));
	}

	// 半角カタカナ→全角カタカナ変換
	public void testConvertFullWidthKatakana1() {
		assertEquals(
				"今日はアイウエオです。１２３３４３４４５583839%DKDPE+#`",
				StringUtil
						.convertFullWidthKatakana("今日はｱｲｳｴｵです。１２３３４３４４５583839%DKDPE+#`"));
	}

	public void testConvertHalfWidthKatakana2() {
		assertEquals("今日はｱｲｳｴｵです。123343445583839%DKDPE+#`",
				StringUtil.convertHalfWidthString(
						"今日はアイウエオです。１２３３４３４４５583839%DKDPE+#`", true));
	}

	// 全角・半角変換
	public void testConvertFullHalfWidth1() {
		String value = StringUtil
				.convertHalfWidthString("（株）フリーダム・グループ", false);

		assertEquals("(株)フリーダム・グループ", value);
	}

	// 機種依存文字が含まれているかどうかの検証
	public void testCheckModelDependence1() {
		assertTrue(StringUtil.containsModelDependence("㈱"));
	}

	public void testCheckModelDependence2() {
		assertTrue(StringUtil.containsModelDependence("①"));
	}

	// NGなUnicodeを半角スペースに変換
	public void testCheckAsciiCharacter1() {
		assertEquals(" フリーダム グループ ",
				MesagoUtil.replaceUnicodeMarkByHalfSpace("♡フリーダム♡グループ♡"));
	}

	// NGなマークを半角スペースに変換
	public void testCheckAsciiCharacter2() {
		assertEquals(" フリーダム グループ ",
				MesagoUtil.replaceUnicodeMarkByHalfSpace("◎フリーダム♂グループ◆"));
	}

	public void testReplace1() {
		assertEquals("（株）ツジモト", StringUtil.replace("(株)ツジモト", "\\(株\\)", "（株）"));
	}

	public void testReplace2() {
		assertEquals("023~~291~0", StringUtil.replace("023da291a0", "\\D", "~"));
	}

	// 全角変換
	public void testConvertFullHalfWidth2() {
		String value = StringUtil.convertFullWidthString("徳島市国府町早淵694-1");
		assertEquals("徳島市国府町早淵６９４－１", value);
	}

	public void testFind() {
		// String value = StringUtil
		// .getMatchString(
		// "20140818_coopkinki_test/palyodo_01303000000000_20130830094535639_0006.jpg",
		// "^(.+)/(chiba|palyodo|izumi|kyoto|nara|shiga|wakayama)\\_([0-9]+)\\_([0-9]+)$",
		// 3);
		String value = StringUtil
				.getMatchString(
						"20140818_coopkinki_test/palyodo_01303000000000_20130830094535639_0006.jpg",
						"^(.+)/(chiba|palyodo|izumi|kyoto|nara|shiga|wakayama)\\_(.+)\\_(.+)\\_(.+)$",
						3);
		assertEquals("01303000000000", value);
	}
}
