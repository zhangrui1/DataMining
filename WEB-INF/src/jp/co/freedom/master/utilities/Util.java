package jp.co.freedom.master.utilities;

import jp.co.freedom.master.dto.UserDataDto;

/**
 * 集計用ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public abstract class Util {

	/**
	 * 事前登録データであるか否かの検証
	 * 
	 * @param userdata
	 *            <b>UserDataDto</b>
	 * @return 検証結果のブール値
	 */
	public abstract boolean isPreEntry(UserDataDto userdata);

	/**
	 * 当日登録データであるか否かの検証
	 * 
	 * @param userdata
	 *            <b>UserDataDto</b>
	 * @return 検証結果のブール値
	 */
	public abstract boolean isAppEntry(UserDataDto userdata);
}
