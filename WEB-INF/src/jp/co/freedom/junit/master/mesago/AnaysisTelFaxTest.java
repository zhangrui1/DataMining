package jp.co.freedom.junit.master.mesago;

import jp.co.freedom.common.utilities.AnalysisTelFax;
import junit.framework.TestCase;

/**
 * 【MESAGO】データクレンジング案件のTEL番号／FAX番号の分解ロジックの単体テスト
 * 
 * @author フリーダム・グループ
 * 
 */
public class AnaysisTelFaxTest extends TestCase {
	/*
	 * ◇◇◇◇◇　国内住所のテストケース　◇◇◇◇◇
	 */

	/*
	 * 国内住所 ハイフン0個の場合
	 */
	public void testTelAnaysis0_1() {
		AnalysisTelFax test = new AnalysisTelFax("0366820298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "", "0366820298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis0_2() {
		AnalysisTelFax test = new AnalysisTelFax("(03)66820298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis0_3() {
		AnalysisTelFax test = new AnalysisTelFax("0366820298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "", "0366820298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 国内住所 ハイフン1個の場合
	 */
	public void testTelAnaysis1_1() {
		AnalysisTelFax test = new AnalysisTelFax("03-66820298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis1_2() {
		AnalysisTelFax test = new AnalysisTelFax("03-66820298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis1_3() {
		AnalysisTelFax test = new AnalysisTelFax("(81)03-66820298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis1_4() {
		AnalysisTelFax test = new AnalysisTelFax("81(81)03-66820298(1234)",
				false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "81", "03-66820298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 国内住所 ハイフン2個の場合
	 */
	public void testTelAnaysis2_1() {
		AnalysisTelFax test = new AnalysisTelFax("03-6682-0298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_2() {
		AnalysisTelFax test = new AnalysisTelFax("03-6682-0298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_3() {
		AnalysisTelFax test = new AnalysisTelFax("(81)03-6682-0298(1234)",
				false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_4() {
		AnalysisTelFax test = new AnalysisTelFax("070-6557-6724", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "070", "6557-6724", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_5() {
		AnalysisTelFax test = new AnalysisTelFax("070-6557-6724(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "070", "6557-6724",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_6() {
		AnalysisTelFax test = new AnalysisTelFax("+81-088-6680298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_7() {
		AnalysisTelFax test = new AnalysisTelFax("+81-088-6680298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis2_8() {
		AnalysisTelFax test = new AnalysisTelFax("(81)-088-6680298 (8755)",
				false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"8755");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 国内住所 ハイフン3個の場合
	 */
	public void testTelAnaysis3_1() {
		AnalysisTelFax test = new AnalysisTelFax("+81-088-668-0298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis3_2() {
		AnalysisTelFax test = new AnalysisTelFax("+81-088-668-0298(1234)",
				false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis3_3() {
		AnalysisTelFax test = new AnalysisTelFax("(81)-088-668-0298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis3_4() {
		AnalysisTelFax test = new AnalysisTelFax("(81)-088-668-0298(1234)",
				false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis3_5() {
		AnalysisTelFax test = new AnalysisTelFax("81-088-668-0298", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis3_6() {
		AnalysisTelFax test = new AnalysisTelFax("81-088-668-0298(1234)", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis5() {
		AnalysisTelFax test = new AnalysisTelFax(
				"　　（　８１）－－－０　８　８　　－　６６８　０２９８（　８　７　５　５　　）", false);
		AnalysisTelFax answer = new AnalysisTelFax("81", "088", "668-0298",
				"8755");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 海外住所 ハイフン1個の場合
	 */
	public void testTelAnaysisOversea1_1() {
		AnalysisTelFax test = new AnalysisTelFax("49-0366820298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "", "0366820298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea1_2() {
		AnalysisTelFax test = new AnalysisTelFax("49-0366820298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "", "0366820298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea1_3() {
		AnalysisTelFax test = new AnalysisTelFax("(49)03-66820298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "03", "66820298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea1_4() {
		AnalysisTelFax test = new AnalysisTelFax("(49)03-66820298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "03", "66820298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 海外住所 ハイフン2個の場合
	 */
	public void testTelAnaysisOversea2_3() {
		AnalysisTelFax test = new AnalysisTelFax("(49)03-6682-0298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "03", "6682-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea2_4() {
		AnalysisTelFax test = new AnalysisTelFax("(49)03-6682-0298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "03", "6682-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea2_5() {
		AnalysisTelFax test = new AnalysisTelFax("+49-088-6680298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "6680298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea2_6() {
		AnalysisTelFax test = new AnalysisTelFax("+49-088-6680298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "6680298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea2_7() {
		AnalysisTelFax test = new AnalysisTelFax("(49)-088-6680298 ", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "6680298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea2_8() {
		AnalysisTelFax test = new AnalysisTelFax("(49)-088-6680298 (8755)",
				true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "6680298",
				"8755");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	/*
	 * 海外住所 ハイフン3個の場合
	 */
	public void testTelAnaysisOversea3_1() {
		AnalysisTelFax test = new AnalysisTelFax("+49-088-668-0298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea3_2() {
		AnalysisTelFax test = new AnalysisTelFax("+49-088-668-0298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea3_3() {
		AnalysisTelFax test = new AnalysisTelFax("(49)-088-668-0298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea3_4() {
		AnalysisTelFax test = new AnalysisTelFax("(49)-088-668-0298(1234)",
				true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea3_5() {
		AnalysisTelFax test = new AnalysisTelFax("49-088-668-0298", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea3_6() {
		AnalysisTelFax test = new AnalysisTelFax("49-088-668-0298(1234)", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "668-0298",
				"1234");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysisOversea5() {
		AnalysisTelFax test = new AnalysisTelFax(
				"　　（　49）－－－０　８　８　　－　６６８　０２９８（　８　７　５　５　　）", true);
		AnalysisTelFax answer = new AnalysisTelFax("49", "088", "6680298",
				"8755");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

	public void testTelAnaysis6() {
		AnalysisTelFax test = new AnalysisTelFax("49-0-6105-2735-171", true);
		AnalysisTelFax answer = new AnalysisTelFax("", "",
				"49-0-6105-2735-171", "");
		test.execute(false);
		assertTrue(test.equals(answer));
	}

}
