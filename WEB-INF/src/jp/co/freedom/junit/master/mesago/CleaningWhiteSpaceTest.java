package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import junit.framework.TestCase;

public class CleaningWhiteSpaceTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * 不要な空白文字列を削除
	 * 
	 * 1. 最後のスペース 　2. 連続スペースを1個の半角スペースに修正
	 */
	public void testRemoveWhiteSpace01() {//半角スペース
		String test="サロン   ほにゃらら";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue("サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace02() {
		String test="サロン   ほにゃらら ";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue("サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace03() {
		String test="サロン   ほにゃらら  ";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue("サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace04() {
		String test="  サロン   ほにゃらら ";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue(" サロン ほにゃらら".equals(answer));
	}	
	
	public void testRemoveWhiteSpace05() {//全角スペース
		String test="サロン　　ほにゃらら";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue("サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace06() {
		String test="　　サロン　　ほにゃらら";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue(" サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace07() {
		String test="サロン　　ほにゃらら　";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue("サロン ほにゃらら".equals(answer));
	}
	public void testRemoveWhiteSpace08() {
		String test="　　サロン　　ほにゃらら　　";
		String answer=DataCleansingForMesagoUtil.removeWhiteSpace(test);
		assertTrue(" サロン ほにゃらら".equals(answer));
	}	
	
}
