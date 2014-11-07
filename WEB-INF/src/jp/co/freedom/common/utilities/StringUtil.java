package jp.co.freedom.common.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字列ユーティリティクラス
 * 
 * @author フリーダム・グループ
 */
public class StringUtil {

	/** 全角／半角カタカナテーブル */
	private static final String kanaHanZenTbl[][] = {
			// 2文字構成の濁点付き半角カナ
			// 必ずテーブルに先頭に置いてサーチ順を優先すること
			{ "ｶﾞ", "ガ" }, { "ｷﾞ", "ギ" }, { "ｸﾞ", "グ" }, { "ｹﾞ", "ゲ" },
			{ "ｺﾞ", "ゴ" }, { "ｻﾞ", "ザ" }, { "ｼﾞ", "ジ" },
			{ "ｽﾞ", "ズ" },
			{ "ｾﾞ", "ゼ" },
			{ "ｿﾞ", "ゾ" },
			{ "ﾀﾞ", "ダ" },
			{ "ﾁﾞ", "ヂ" },
			{ "ﾂﾞ", "ヅ" },
			{ "ﾃﾞ", "デ" },
			{ "ﾄﾞ", "ド" },
			{ "ﾊﾞ", "バ" },
			{ "ﾋﾞ", "ビ" },
			{ "ﾌﾞ", "ブ" },
			{ "ﾍﾞ", "ベ" },
			{ "ﾎﾞ", "ボ" },
			{ "ﾊﾟ", "パ" },
			{ "ﾋﾟ", "ピ" },
			{ "ﾌﾟ", "プ" },
			{ "ﾍﾟ", "ペ" },
			{ "ﾎﾟ", "ポ" },
			{ "ｳﾞ", "ヴ" },
			// 1文字構成の半角カナ
			{ "ｱ", "ア" }, { "ｲ", "イ" }, { "ｳ", "ウ" }, { "ｴ", "エ" },
			{ "ｵ", "オ" }, { "ｶ", "カ" }, { "ｷ", "キ" }, { "ｸ", "ク" },
			{ "ｹ", "ケ" }, { "ｺ", "コ" }, { "ｻ", "サ" }, { "ｼ", "シ" },
			{ "ｽ", "ス" }, { "ｾ", "セ" }, { "ｿ", "ソ" }, { "ﾀ", "タ" },
			{ "ﾁ", "チ" }, { "ﾂ", "ツ" }, { "ﾃ", "テ" }, { "ﾄ", "ト" },
			{ "ﾅ", "ナ" }, { "ﾆ", "ニ" }, { "ﾇ", "ヌ" }, { "ﾈ", "ネ" },
			{ "ﾉ", "ノ" }, { "ﾊ", "ハ" }, { "ﾋ", "ヒ" }, { "ﾌ", "フ" },
			{ "ﾍ", "ヘ" }, { "ﾎ", "ホ" }, { "ﾏ", "マ" }, { "ﾐ", "ミ" },
			{ "ﾑ", "ム" }, { "ﾒ", "メ" }, { "ﾓ", "モ" }, { "ﾔ", "ヤ" },
			{ "ﾕ", "ユ" }, { "ﾖ", "ヨ" }, { "ﾗ", "ラ" }, { "ﾘ", "リ" },
			{ "ﾙ", "ル" }, { "ﾚ", "レ" }, { "ﾛ", "ロ" }, { "ﾜ", "ワ" },
			{ "ｦ", "ヲ" }, { "ﾝ", "ン" }, { "ｧ", "ァ" }, { "ｨ", "ィ" },
			{ "ｩ", "ゥ" }, { "ｪ", "ェ" }, { "ｫ", "ォ" }, { "ｬ", "ャ" },
			{ "ｭ", "ュ" }, { "ｮ", "ョ" }, { "ｯ", "ッ" }, { "｡", "。" },
			{ "｢", "「" }, { "｣", "」" }, { "､", "、" }, { "･", "・" },
			{ "ｰ", "ー" }, { "", "" } };

	/**
	 * 文字列を結合する
	 * 
	 * @param buff
	 *            対象文字列を格納する配列
	 * @return　結合後の文字列
	 */
	public static String concat(String... buff) {
		StringBuffer sb = new StringBuffer("");
		for (String value : buff) {
			if (isNotEmpty(value)) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(value);
			}
		}
		return sb.toString();
	}

	/**
	 * 文字列を結合する
	 * 
	 * @param delimit
	 *            デリミタ文字列
	 * @param buff
	 *            対象文字列を格納する配列
	 * @return　結合後の文字列
	 */
	public static String concatWithDelimit(String delimit, String... buff) {
		StringBuffer sb = new StringBuffer("");
		for (String value : buff) {
			if (isNotEmpty(value)) {
				if (sb.length() != 0) {
					sb.append(delimit);
				}
				sb.append(value);
			}
		}
		return sb.toString();
	}

	/**
	 * 文字列を結合する
	 * 
	 * @param buff
	 *            対象文字列を格納する配列
	 * @return　結合後の文字列
	 */
	public static String concat(List<String> list) {
		StringBuffer sb = new StringBuffer("");
		for (String value : list) {
			if (isNotEmpty(value)) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(value);
			}
		}
		return sb.toString();
	}

	/**
	 * 指定した2つの文字列の同一性チェック
	 * 
	 * @param value1
	 *            文字列1
	 * @param value2
	 *            文字列2
	 * @return 検証結果のブール値
	 */
	public static boolean equals(String value1, String value2) {
		if (isEmpty(value1) && isEmpty(value2)) {
			return true;
		} else if (isEmpty(value1) || isEmpty(value2)) {
			return false;
		} else {
			return value1.equals(value2);
		}
	}

	/**
	 * 文字列を結合する
	 * 
	 * @param delimit
	 *            デリミタ文字列
	 * @param buff
	 *            対象文字列を格納する配列
	 * @return　結合後の文字列
	 */
	public static String concatWithDelimit(String delimit, List<String> list) {
		StringBuffer sb = new StringBuffer("");
		for (String value : list) {
			if (isNotEmpty(value)) {
				if (sb.length() != 0) {
					sb.append(delimit);
				}
				sb.append(value);
			}
		}
		return sb.toString();
	}

	/**
	 * 指定文字列が空文字列であるか否かの検証
	 * 
	 * @param value
	 *            文字列
	 * @return　検証結果のブール値
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	/**
	 * 指定文字列が空文字列でないか否かの検証
	 * 
	 * @param value
	 *            文字列
	 * @return 検証結果のブール値
	 */
	public static boolean isNotEmpty(String value) {
		return value != null && value.length() != 0;
	}

	/**
	 * ダブルクォートでエンクォート
	 * 
	 * @param value
	 *            文字列
	 * @return エンクォート済み文字列
	 */
	public static String enquote(String value) {
		if (StringUtil.isNotEmpty(value)) {
			return "\"" + value + "\"";
		} else {
			return "\"\"";
		}
	}

	/**
	 * ダブルクォートでエンクォート
	 * 
	 * @param value
	 *            文字列
	 * @param enquoteFlg
	 *            エンクォートフラグ
	 * @return エンクォート済み文字列
	 */
	public static String enquote(String value, boolean enquoteFlg) {
		if (enquoteFlg) {
			return enquote(value);
		} else {
			return value;
		}
	}

	/**
	 * 指定デリミタでエンクォート
	 * 
	 * @param delimit
	 *            デリミタ
	 * @param value
	 *            対象文字列
	 * @return エンクォート済み文字列
	 */
	public static String enquoteWith(String delimit, String value) {
		if (StringUtil.isNotEmpty(value)) {
			return delimit + value + delimit;
		} else {
			return delimit + delimit;
		}
	}

	/**
	 * 指定データがリスト中に存在しているか否かを検証する
	 * 
	 * @param list
	 *            IDリスト
	 * @param data
	 *            対象データ
	 * @return 検証結果のブール値
	 */
	public static boolean contains(String[] list, String data) {
		if (list != null && list.length != 0) {
			for (String item : list) {
				if (item.equals(data)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 指定した条件で文字列置換を行う
	 * 
	 * @param data
	 *            編集対象の文字列
	 * @param before
	 *            置換対象の文字列
	 * @param after
	 *            置換文字列
	 * @return 置換後の文字列
	 */
	public static String replace(String data, String before, String after) {
		if (isNotEmpty(data)) {
			String regex = before;
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(data);
			return m.replaceAll(after);
		}
		return data;
	}

	/**
	 * 指定した条件で文字列置換を行う
	 * 
	 * @param data
	 *            編集対象の文字列
	 * @param before
	 *            置換対象の文字列
	 * @param after
	 *            置換文字列
	 * @return 置換後の文字列
	 */
	public static String replaceLastMatch(String data, String before,
			String after) {
		assert isNotEmpty(data) && isNotEmpty(before) && isNotEmpty(after);
		String regex = before;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		int start = -1;
		int end = -1;
		while (m.find()) {
			MatchResult result = m.toMatchResult();
			start = result.start();
			end = result.end();
		}
		if (end + 1 < data.length()) {
			return data.substring(0, start) + after
					+ data.substring(end + 1, data.length());
		} else {
			return data.substring(0, start) + after;
		}
	}

	/**
	 * 指定キーワードでgrepを実行
	 * 
	 * @param data
	 *            　検索対象文字列
	 * @param keyword
	 *            検索キーワード（正規表現可）
	 * @return 実行結果のブール値
	 */
	public static boolean find(String data, String keyword) {
		assert isNotEmpty(data) && isNotEmpty(keyword);
		String regex = keyword;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		return m.find();
	}

	/**
	 * 指定文字列の出現回数を求める
	 * 
	 * @param value
	 *            　検索対象文字列
	 * @param regex
	 *            正規表現
	 * @return 出現回数
	 */
	public static int matchCount(String value, String regex) {
		assert isNotEmpty(value) && isNotEmpty(regex);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * 最初にマッチした文字列を抽出する
	 * 
	 * @param data
	 *            検索対象文字列
	 * @param keyword
	 *            検索キーワード（正規表現可）
	 * @return マッチした文字列
	 */
	public static String getFirstMatchString(String data, String keyword) {
		assert isNotEmpty(data) && isNotEmpty(keyword);
		String regex = keyword;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		while (m.find()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * 指定グループ番号にマッチする文字列を抽出する
	 * 
	 * @param data
	 *            検索対象文字列
	 * @param keyword
	 *            検索キーワード（正規表現可）
	 * @param groupId
	 *            グループ番号
	 * @return マッチした文字列
	 */
	public static String getMatchString(String data, String keyword, int groupId) {
		assert isNotEmpty(data) && isNotEmpty(keyword);
		String regex = keyword;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		while (m.find()) {
			return m.group(groupId);
		}
		return null;
	}

	/**
	 * 最後にマッチした文字列を抽出する
	 * 
	 * @param data
	 *            検索対象文字列
	 * @param keyword
	 *            検索キーワード（正規表現可）
	 * @return　マッチした文字列
	 */
	public static String getLastMatchString(String data, String keyword) {
		assert isNotEmpty(data) && isNotEmpty(keyword);
		String regex = keyword;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		String matchString = null;
		while (m.find()) {
			matchString = m.group(1);
		}
		return matchString;
	}

	/**
	 * 不明マークが含まれているか否かの検証
	 * 
	 * @param data
	 *            検証文字列
	 * @return 検証結果のブール値
	 */
	public static boolean containWildcard(String data) {
		if (isNotEmpty(data)) {
			return data.indexOf("■") != -1;
		}
		return false;
	}

	/**
	 * 不明マークが含まれているか否かの検証
	 * 
	 * @param data
	 *            検証文字列
	 * @param mark
	 *            不明マーク
	 * @return 検証結果のブール値
	 */
	public static boolean containWildcard(String data, String mark) {
		assert isNotEmpty(mark);
		if (isNotEmpty(data)) {
			return data.indexOf(mark) != -1;
		}
		return false;
	}

	/**
	 * 指定リストからユニーク要素のみを抽出してリスト形式で返却
	 * 
	 * @param list
	 *            対象リスト
	 * @return ユニーク要素のみを抽出してリスト形式で返却
	 */
	public static List<String> unique(List<String> list) {
		List<String> unique = new ArrayList<String>();
		Set<String> hashSet = new HashSet<String>();
		for (String data : list) {
			if (!hashSet.contains(data)) {
				hashSet.add(data);
				unique.add(data);
			}
		}
		return unique;
	}

	/**
	 * Integer に変換できない文字列が渡された場合は false を返します。
	 * 
	 * @param str
	 *            チェック文字列
	 * @return 引数の文字列が数値である場合 true を返す。
	 */
	public static boolean integerStringCheck(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * ブール型 に変換できない文字列が渡された場合は false を返します。
	 * 
	 * @param str
	 *            チェック文字列
	 * @return 引数の文字列がブール型である場合 true を返す。
	 */
	public static boolean booleanStringCheck(String str) {
		if ("true".equals(str) || "false".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 指定文字列が数字のみで構成されているかを検証する
	 * 
	 * @param value
	 *            指定文字列
	 * @return 検証結果のブール値
	 */
	public static boolean isNumeric(String value) {
		// 判定するパターンを生成
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(value);
		return m.find();
	}

	/**
	 * 指定文字列から数字のみを抽出する
	 * 
	 * @param value
	 *            指定文字列
	 * @param noExistReturn
	 *            数字が存在しなかった場合の戻り値
	 * @return 抽出した数字
	 */
	public static int extractNumber(String value, int noExistReturn) {
		if (isNotEmpty(value)) {
			String extractedNumStr = value.replaceAll("[^0-9]", ""); // 数字以外を除去
			if (isNotEmpty(extractedNumStr) && integerStringCheck(value)) {
				return Integer.parseInt(extractedNumStr);
			}
		}
		return noExistReturn;
	}

	/**
	 * 指定文字列から数字のみを抽出する
	 * 
	 * @param value
	 *            指定文字列
	 * @param noExistReturn
	 *            数字が存在しなかった場合の戻り値
	 * @return 抽出した数字(文字列)
	 */
	public static String extractNumberStr(String value, String noExistReturn) {
		if (isNotEmpty(value)) {
			String extractedNumStr = value.replaceAll("[^0-9]", ""); // 数字以外を除去
			if (isNotEmpty(extractedNumStr) && integerStringCheck(value)) {
				return extractedNumStr;
			}
		}
		return noExistReturn;
	}

	/**
	 * 全角半角の相違を無視した文字列比較
	 * 
	 * @param value1
	 *            文字列1
	 * @param value2
	 *            文字列2
	 * @return 検証結果のブール値
	 */
	public static boolean equalsIgnoreDifference(String value1, String value2) {
		assert isNotEmpty(value1) && isNotEmpty(value2);
		value1 = convertHalfWidthString(value1, false);
		value2 = convertHalfWidthString(value2, false);
		return value1.equals(value2);
	}

	/**
	 * String→booleanに変換
	 * 
	 * @param value
	 *            　変換対象文字列
	 * @return ブール値
	 */
	public static boolean toBoolean(String value) {
		if (isNotEmpty(value)) {
			if ("true".equals(value)) {
				return true;
			} else if ("false".equals(value)) {
				return false;
			}
		}
		return false;
	}

	/**
	 * String→Integerに変換
	 * 
	 * @param value
	 *            変換対象文字列
	 * @return 数値（変換に失敗する場合は-1を返却）
	 */
	public static int toInteger(String value) {
		if (isNotEmpty(value) && integerStringCheck(value)) {
			return Integer.parseInt(value);
		}
		return -1;
	}

	/**
	 * 数字とハイフンのみ全角を半角に変換する
	 * 
	 * @param str
	 *            対象文字列
	 * @return 変換後の文字列
	 */
	public static String convertHalfWidthNumberAndAlphabetAndHyphenhyphenate(
			String str) {
		if (isNotEmpty(str)) {
			final int DIFFERENCE = 'Ａ' - 'A'; // 全角アルファベットと半角アルファベットとの文字コードの差
			char[] cc = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (char c : cc) {
				char newChar = c;
				if ((('Ａ' <= c) && (c <= 'Ｚ')) || (('ａ' <= c) && (c <= 'ｚ'))
						|| (('０' <= c) && (c <= '９')) || isHyphenhyphenate(c)) {
					// 変換対象のcharだった場合に全角文字と半角文字の差分を引く
					newChar = (char) (c - DIFFERENCE);
				}
				sb.append(newChar);
			}
			return sb.toString();
		}
		return str;
	}

	/**
	 * ハイフンであるか否かの判定
	 * 
	 * @param pc
	 *            対象文字
	 * @return 判定結果のブール値
	 */
	public static boolean isHyphenhyphenate(char pc) {
		final char[] SIGNS = { '−', '－' }; // ハイフン
		for (char c : SIGNS) {
			if (c == pc) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 全角文字列→半角文字列に変換(対象は全角アルファベット、数字、一部の記号、カタカナ)
	 * 
	 * 【変換対象の記号一覧】 '！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋', '，', '−', '－',
	 * '．', '／', '：', '；', '＜', '＝', '＞', '？', '＠', '［', '］', '＾', '＿', '｛',
	 * '｜', '｝'
	 * 
	 * 
	 * @param str
	 *            変換対象文字列
	 * @return 半角文字列
	 */
	public static String convertHalfWidthString(String str,
			boolean executeConvertKatakana) {
		if (isNotEmpty(str)) {
			final int DIFFERENCE = 'Ａ' - 'A'; // 全角アルファベットと半角アルファベットとの文字コードの差
			char[] cc = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (char c : cc) {
				char newChar = c;
				if ((('Ａ' <= c) && (c <= 'Ｚ')) || (('ａ' <= c) && (c <= 'ｚ'))
						|| (('０' <= c) && (c <= '９')) || is2Sign(c)) {
					// 変換対象のcharだった場合に全角文字と半角文字の差分を引く
					newChar = (char) (c - DIFFERENCE);
				}
				sb.append(newChar);
			}
			if (executeConvertKatakana) {
				return convertHalfWidthKatakana(sb.toString()); // 全角カタカナ→半角カタカナ変換
			} else {
				return sb.toString();
			}
		}
		return str;
	}

	/**
	 * 全角文字列→半角文字列に変換(対象は全角アルファベット、数字、一部の記号、カタカナ)
	 * 
	 * 【変換対象の記号一覧】 '！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋', '，', '−', '－',
	 * '．', '／', '：', '；', '＜', '＝', '＞', '？', '＠', '［', '］', '＾', '＿', '｛',
	 * '｜', '｝'
	 * 
	 * 
	 * @param str
	 *            変換対象文字列
	 * @return 半角文字列
	 */
	public static String convertFullWidthString(String str) {
		if (isNotEmpty(str)) {
			final int DIFFERENCE = 'Ａ' - 'A'; // 全角アルファベットと半角アルファベットとの文字コードの差
			char[] cc = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (char c : cc) {
				char newChar = c;
				if ((('A' <= c) && (c <= 'Z')) || (('a' <= c) && (c <= 'z'))
						|| (('0' <= c) && (c <= '9')) || is2SignHalf(c)) {
					// 変換対象のcharだった場合に全角文字と半角文字の差分を引く
					newChar = (char) (c + DIFFERENCE);
				}
				if (' ' == c) {
					newChar = '　';
				} else if('\'' == c){
					newChar = '’';
				}
				sb.append(newChar);
			}
			return sb.toString();
		}
		return str;
	}

	/**
	 * 変換対象全角記号であるか否かの判定
	 * 
	 * @param pc
	 *            対象文字
	 * @return 判定結果のブール値
	 */
	public static boolean is2SignHalf(char pc) {
		final char[] SIGNS2 = { '!', '#', '$', '%', '&', '(', ')', '*', '+',
				',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[',
				']', '^', '_', '{', '|', '}' }; // 変更対象全角記号配列
		for (char c : SIGNS2) {
			if (c == pc) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 変換対象全角記号であるか否かの判定
	 * 
	 * @param pc
	 *            対象文字
	 * @return 判定結果のブール値
	 */
	public static boolean is2Sign(char pc) {
		final char[] SIGNS2 = { '！', '＃', '＄', '％', '＆', '（', '）', '＊', '＋',
				'，', '−', '－', '．', '／', '：', '；', '＜', '＝', '＞', '？', '＠',
				'［', '］', '＾', '＿', '｛', '｜', '｝' }; // 変更対象全角記号配列
		for (char c : SIGNS2) {
			if (c == pc) {
				return true;
			}
		}
		return false;
	}

	/**
	 * バーコードデータ中のタイムスタンプ文字列を正規化
	 * 
	 * @param timestamp
	 *            バーコードデータ中のタイムスタンプ
	 * @return 正規化後のタイムスタンプ
	 */
	public static String normalizedDateStrForBarcodeTimestamp(String timestamp) {
		if (StringUtil.isNotEmpty(timestamp) && timestamp.length() == 14) {
			StringBuffer sb = new StringBuffer();
			sb.append(timestamp.substring(0, 4));
			sb.append("/");
			sb.append(timestamp.substring(4, 6));
			sb.append("/");
			sb.append(timestamp.substring(6, 8));
			sb.append(" ");
			sb.append(timestamp.substring(8, 10));
			sb.append(":");
			sb.append(timestamp.substring(10, 12));
			sb.append(":");
			sb.append(timestamp.substring(12, 14));
			return sb.toString();
		}
		return null;
	}

	/**
	 * タイムスタンプ文字列の正規化化
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @param hour
	 *            時間
	 * @return 正規化後のタイムスタンプ
	 */
	public static String normalizedDateStr(String year, String month,
			String day, String hour) {
		if (StringUtil.isNotEmpty(year) && StringUtil.isNotEmpty(month)
				&& StringUtil.isNotEmpty(day) && StringUtil.isNotEmpty(hour)) {
			StringBuffer sb = new StringBuffer();
			sb.append(year);
			sb.append("/");
			sb.append(month);
			sb.append("/");
			sb.append(day);
			sb.append(" ");
			sb.append(hour);
			sb.append(":");
			sb.append("00");
			sb.append(":");
			sb.append("00");
			return sb.toString();
		}
		return null;
	}

	/**
	 * 数値文字列の固定長文字列化(先頭に0を補完)
	 * 
	 * @param value
	 *            対象数値文字列
	 * @param length
	 *            固定長
	 * @return 変換後の文字列
	 */
	public static String convertFixLengthFromInteger(String value, int length) {
		assert isNotEmpty(value);
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 0; nIndex < length - value.length(); nIndex++) {
			sb.append("0");
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * 数値文字列の固定長文字列化(先頭に0を補完)
	 * 
	 * @param value
	 *            対象数値文字列
	 * @param length
	 *            固定長
	 * @return 変換後の文字列
	 */
	public static String convertFixLengthFromInteger(int num, int length) {
		String value = String.valueOf(num);
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 0; nIndex < length - value.length(); nIndex++) {
			sb.append("0");
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * 全角文字列であるかどうかの検証
	 * 
	 * @param value
	 *            対象文字列
	 * @return 検証結果のブール値
	 */
	public static boolean isFullWidthString(String value) {
		assert isNotEmpty(value);
		String regex = "[^ -~｡-ﾟ]*";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(value).matches();
	}

	/**
	 * 半角文字列であるかどうかの検証
	 * 
	 * @param value
	 *            対象文字列
	 * @return 検証結果のブール値
	 */
	public static boolean isHalfWidthString(String value) {
		assert isNotEmpty(value);
		String regex = "[ -~｡-ﾟ]+";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(value).matches();
	}

	/**
	 * メールアドレスに対する構文チェック
	 * 
	 * @param address
	 *            検証対象アドレス
	 * @return 検証結果のブール値
	 */
	public static boolean isEmailAddress(String address) {
		assert isNotEmpty(address);
		String regex = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(address).matches();
	}

	/**
	 * URLに対する構文チェック
	 * 
	 * @param url
	 *            検証対象URL文字列
	 * @return 検証結果のブール値
	 */
	public static boolean isUrl(String url) {
		assert isNotEmpty(url);
		String regex = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(url).matches();
	}

	/**
	 * 半角カタカナ→全角カタカナ変換
	 * 
	 * @param value
	 *            対象文字列
	 * @return 変換後の文字列
	 */
	public static String convertFullWidthKatakana(String value) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = 0; i < value.length(); i++) {
			Character c = new Character(value.charAt(i));
			// Unicode半角カタカナのコード範囲か？
			if (c.compareTo(new Character((char) 0xff61)) >= 0
					&& c.compareTo(new Character((char) 0xff9f)) <= 0) {
				// 半角全角変換テーブルを検索する
				for (j = 0; j < kanaHanZenTbl.length; j++) {
					if (value.substring(i).startsWith(kanaHanZenTbl[j][0])) {
						sb.append(kanaHanZenTbl[j][1]);
						i += kanaHanZenTbl[j][0].length() - 1;
						break;
					}
				}

				// 検索できなければ、変換しない
				if (j >= kanaHanZenTbl.length) {
					sb.append(c);
				}
			} else { // Unicode半角カタカナ以外なら変換しない
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 全角カタカナ→半角カタカナ変換
	 * 
	 * @param value
	 *            対象文字列
	 * @return 変換後の文字列
	 */
	public static String convertHalfWidthKatakana(String value) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = 0; i < value.length(); i++) {
			Character c = new Character(value.charAt(i));
			// Unicode全角カタカナのコード範囲か?
			if (c.compareTo(new Character((char) 0x30a1)) >= 0
					&& c.compareTo(new Character((char) 0x30fc)) <= 0) {
				// 半角全角変換テーブルを検索する
				for (j = 0; j < kanaHanZenTbl.length; j++) {
					if (value.substring(i).startsWith(kanaHanZenTbl[j][1])) {
						sb.append(kanaHanZenTbl[j][0]);
						break;
					}
				}
				// 検索できなければ、変換しない
				if (j >= kanaHanZenTbl.length) {
					sb.append(c);
				}
			} else { // 全角カタカナ以外なら変換しない
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 指定文字列が環境依存文字を含むかどうかの検証
	 * 
	 * @param value
	 *            指定文字列
	 * @return 検証結果のブール値
	 */
	public static boolean containsModelDependence(String value) {
		if (isNotEmpty(value)) {
			if (find(
					value,
					"[№℡ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹ∑∟∮⊿①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳〝〟㈱㈲㈹㊤㊥㊦㊧㊨㌃㌍㌔㌘㌢㌣㌦㌧㌫㌶㌻㍉㍊㍍㍑㍗㍻㍼㍽㍾㎎㎏㎜㎝㎞㎡㏄㏍丨仡仼仼伀伃伹佖侊侒侔侚俉俍俿倞倢偀偂偆偰傔僘僴兊兤冝冾凬刕劜劦劯勀勛匀匇匤卲厓厲叝咊咜咩哿喆坙坥垬埇埈增墲夋奓奛奝奣妤妺孖寀寘寬尞岦岺峵崧嵂嵓嵭嶸嶹巐弡弴彅彧德忞恝悅悊惕惞惲愑愠愰愷憘戓抦揵摠撝擎敎昀昉昕昞昤昮昱昻晗晙晥晳暙暠暲暿曺曻朎杦枻柀栁桄桒棈棏楨榘槢樰橆橫橳橾櫢櫤毖氿汜汯沆泚洄浯涇涖涬淏淲淸淼渧渹渼湜溿澈澵濵瀅瀇瀨瀨炅炫炻焄焏煆煇煜燁燾犱犾猤獷玽珉珒珖珣珵琇琦琩琪琮瑢璉璟甁甯畯皂皛皜皞皦睆砡硎硤硺礰禔禛竑竧竫箞絈絜綠綷緖繒纊罇羡茁荢荿菇菶葈蒴蓜蕓蕙蕫薰蠇裵褜訒訷詹誧誾諟諶譓譿賰賴贒赶軏遧郞鄕鄧釗釚釞釤釥釭釮鈆鈊鈐鈹鈺鈼鉀鉎鉑鉙鉧鉷鉸銈銧鋐鋓鋕鋗鋙鋠鋧鋹鋻鋿錂錝錞錡錥鍈鍗鍰鎤鏆鏞鏸鐱鑅鑈閒隝隯霳霻靃靍靏靑靕顗顥餧馞驎髙髜魲魵鮏鮱鮻鰀鵫鵰鸙黑朗隆﨎﨎﨏塚﨑晴﨓﨔凞猪益礼神祥福靖精羽﨟蘒﨡諸﨣﨤逸都﨧﨨﨩飯飼館鶴]")) {
				System.out.println("■環境依存文字列:" + value);
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定文字列中のアルファベットを1~26までの数値文字に置換
	 * 
	 * @param value
	 *            　対象文字列
	 * @return 変換後の文字列
	 */
	public static String convertAtoNum(String value) {
		if (isNotEmpty(value)) {
			StringBuffer sb = new StringBuffer();
			for (int nIndex = 0; nIndex < value.length(); nIndex++) {
				char c = value.charAt(nIndex);
				if (c >= 'A' && c <= 'Z') { // アルファベット大文字の場合
					sb.append(c - 'A' + 1);
				} else if (c >= 'a' && c <= 'z') { // アルファベット小文字の場合
					sb.append(c - 'a' + 1);
				} else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
		return value;
	}

	/**
	 * 指定文字列を指定位置に挿入する
	 * 
	 * @param value
	 *            編集対象の文字列
	 * @param position
	 *            挿入位置
	 * @param target
	 *            挿入する文字列
	 * @return 編集後の文字列
	 */
	public static String insert(String value, int position, String target) {
		assert isNotEmpty(target) && position >= 0;
		if (value.length() >= position) {
			value = value.substring(0, position - 1) + target
					+ value.substring(position - 1, value.length());
		}
		return value;
	}

	/**
	 * 指定した固定データ長の文字列に変換
	 * 
	 * @param value
	 *            対象文字列
	 * @param maxLength
	 *            文字列長
	 * @param mark
	 *            ビット埋めに使用するマーク
	 * @return 変換後の固定データ長の文字列
	 */
	public static String convertFixedLengthData(String value, int maxLength,
			String mark) {
		assert isNotEmpty(mark);
		if (isEmpty(value)) {
			return repeat(mark, maxLength);
		}
		int length = value.length();
		if (maxLength > length) {
			return repeat(mark, maxLength - length) + value;
		}
		return value;
	}

	/**
	 * 指定した固定データ長の文字列に変換
	 * 
	 * @param value
	 *            対象文字列
	 * @param maxLength
	 *            文字列長
	 * @param mark
	 *            ビット埋めに使用するマーク
	 * @param position
	 *            挿入ポジション
	 * @return 変換後の固定データ長の文字列
	 */
	public static String convertFixedLengthData(String value, int maxLength,
			String mark, String position) {
		assert isNotEmpty(mark);
		if (isEmpty(value)) {
			return repeat(mark, maxLength);
		}
		int length = value.length();
		if (maxLength > length) {
			if ("before".equals(position) || isEmpty(position)) {
				return repeat(mark, maxLength - length) + value;
			} else {
				return value + repeat(mark, maxLength - length);
			}
		}
		return value;
	}

	/**
	 * 指定文字列を指定回数繰り返して生成できる文字列を返却
	 * 
	 * @param target
	 *            対象文字列
	 * @param length
	 *            回数
	 * @return 生成文字列
	 */
	private static String repeat(String target, int length) {
		assert isNotEmpty(target);
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 1; nIndex <= length; nIndex++) {
			sb.append(target);
		}
		return sb.toString();
	}
}
