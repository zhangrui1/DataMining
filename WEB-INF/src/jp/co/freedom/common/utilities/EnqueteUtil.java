package jp.co.freedom.common.utilities;

/**
 * アンケート集計用ユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class EnqueteUtil {

	/**
	 * マルチ回答を1立ての配列に展開し、その配列を返却する。 返却する配列の要素数は「選択肢の数+1」
	 *
	 * 【例】 ”2 4 5”の場合は 配列｛0 0 1 0 1 1｝を返却
	 *
	 * @param multiAnswer
	 *            マルチ回答内容
	 * @param answerCount
	 *            マルチ回答の選択肢の数
	 * @return 1立て配列
	 */
	public static int[] convertMultiAnswerTo1FlgBuffer(String multiAnswer,
			int answerCount) {
		int integerBuff[] = new int[answerCount + 1];
		for (int nIndex = 0; nIndex <= answerCount; nIndex++) {
			integerBuff[nIndex] = 0;
		}
		if (StringUtil.isNotEmpty(multiAnswer)) {
			String[] enqueteBuff = multiAnswer.split(" ");
			for (String value : enqueteBuff) {
				if (StringUtil.integerStringCheck(value)) {
					int intValue = Integer.parseInt(value);
					integerBuff[intValue] = 1;
				}
			}
		}
		return integerBuff;
	}

	/**
	 * 数値型配列を新規作成し0で初期化する
	 *
	 * @param size
	 *            配列のサイズ
	 * @return 初期化された配列
	 */
	public static int[] arrayInit(int size) {
		int[] targetArray = new int[size]; // 新規配列
		for (int nIndex = 0; nIndex <= size - 1; nIndex++) {
			targetArray[nIndex] = 0;
		}
		return targetArray;
	}

	/**
	 * マルチ回答を数値型配列に展開し、その配列を返却する 【例】 ”2 4 5”の場合は 配列｛2 4 5｝を返却
	 *
	 * @param multiAnswer
	 *            マルチ回答内容
	 * @return 数値型配列(マルチ回答が空もしくはnullである場合にはnullを返却)
	 */
	public static int[] convertMultiAnswer(String multiAnswer) {
		if (StringUtil.isNotEmpty(multiAnswer)) {
			String buff[] = multiAnswer.split(" ");
			int check[] = new int[buff.length];
			for (int nIndex = 0; nIndex < buff.length; nIndex++) {
				String value = buff[nIndex];
				if (StringUtil.integerStringCheck(value)) {
					check[nIndex] = Integer.parseInt(value);
				}
			}
			return check;
		}
		return null;
	}

	/**
	 * マルチ回答を前方一致にてシングル回答に変換
	 *
	 * @param multiAnswer
	 *            マルチ回答内容
	 * @return シングル回答
	 */
	public static String convertSingleAnswer(String multiAnswer) {
		if (StringUtil.isNotEmpty(multiAnswer)) {
			String buff[] = multiAnswer.split(" ");
			return buff[0];
		}
		return null;
	}

	/**
	 * 来場日の取得
	 *
	 * @param fileName
	 *            ファイル名
	 * @return 来場日
	 */
	public static String getVisitDay(String fileName) {
		String month = fileName.substring(4, 6);
		month = month.startsWith("0") ? month.substring(1) : month;
		String day = fileName.substring(6, 8);
		day = day.startsWith("0") ? day.substring(1) : day;
		return month + "月" + day + "日";
	}

}