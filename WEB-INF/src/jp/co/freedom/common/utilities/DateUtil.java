package jp.co.freedom.common.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * 日付用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class DateUtil {

	/**
	 * yyyy-mm-dd形式のDateに変換
	 * 
	 * @param date
	 *            対象<b>Date</b>インスタンス
	 * @return 変換後の<b>Date</b>インスタンス
	 */
	public static Date convertYMD(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
}