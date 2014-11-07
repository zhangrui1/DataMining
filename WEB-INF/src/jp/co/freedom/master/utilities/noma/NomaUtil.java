package jp.co.freedom.master.utilities.noma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.noma.NomaUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * 【NOMA】NOMA向けユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class NomaUtil extends Util {

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 *
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isPreEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof NomaUserDataDto;
		return userdata.id.startsWith(NomaConfig.PREENTRY_BARCODE_START_BIT)
				|| ((NomaUserDataDto) userdata).preentry;
	}

	// /**
	// * 指定IDが事前登録VIPユーザーであるか否かの検証
	// *
	// * @param userdata
	// * @return
	// */
	// public boolean isPreVIP(UserDataDto userdata) {
	// assert userdata != null && userdata instanceof NomaUserDataDto;
	// if (((NomaUserDataDto) userdata).vipSponsor
	// && ((NomaUserDataDto) userdata).preentry2) {
	// return true;
	// } else {
	// String barcodeStr = userdata.id;
	// if (StringUtil.isNotEmpty(barcodeStr)
	// && StringUtil.integerStringCheck(barcodeStr)) {
	// int barcode = Integer.parseInt(barcodeStr);
	// boolean check1 = 800001 <= barcode && barcode <= 800044;
	// return check1;
	// }
	// }
	// return false;
	// }

	/**
	 * 指定IDが当日登録ユーザーであるか否かの検証
	 *
	 * @param userdata
	 *            ユーザー情報
	 * @return 検証結果のブール値
	 */
	public boolean isAppEntry(UserDataDto userdata) {
		assert userdata != null && userdata instanceof NomaUserDataDto;
		return !userdata.id.startsWith(NomaConfig.PREENTRY_BARCODE_START_BIT)
				|| ((NomaUserDataDto) userdata).appointedday;
	}

	// /**
	// * 指定IDが招待状ユーザーであるか否かの検証
	// *
	// * @param userdata
	// * ユーザー情報
	// * @return 検証結果のブール値
	// */
	// public boolean isInvitation(UserDataDto userdata) {
	// assert userdata != null && userdata instanceof NomaUserDataDto;
	// return ((NomaUserDataDto) userdata).invitation;
	// }

	// /**
	// * 指定IDがVIP招待券ユーザーであるか否かの検証
	// *
	// * @param userdata
	// * ユーザー情報
	// * @return 検証結果のブール値
	// */
	// public boolean isVIPInvitation(UserDataDto userdata) {
	// assert userdata != null && userdata instanceof NomaUserDataDto;
	// return ((NomaUserDataDto) userdata).vipInvitation;
	// }

	// /**
	// * 指定IDがプレスユーザーであるか否かの検証
	// *
	// * @param userdata
	// * ユーザー情報
	// * @return 検証結果のブール値
	// */
	// public boolean isPress(UserDataDto userdata) {
	// assert userdata != null && userdata instanceof NomaUserDataDto;
	// return ((NomaUserDataDto) userdata).press;
	// }

	/**
	 * 海外住所フラグの検証
	 *
	 * @param cardInfo
	 *            <b>NomaCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(NomaUserDataDto userdata) {
		// NomaUtil util = new NomaUtil();
		// boolean result;
		// String country = userdata.cardInfo.V_COUNTRY;
		// if (util.isPreEntry(userdata)) { // 事前登録データである場合
		// boolean eng = "Japan".equals(country);
		// boolean jpn = "日本".equals(country);
		// result = !eng && !jpn;
		// } else { // 当日登録データである場合
		// result = StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA);
		// }
		// return result;
		return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA);
	}

	/**
	 * 来場フラグの更新
	 *
	 * @param conn
	 *            DBサーバーへのアクセス情報
	 * @param id
	 *            バーコード番号
	 * @param type
	 *            原票種別
	 * @throws SQLException
	 */
	public static void updateVisitorFlg(Connection conn, String id, String type)
			throws SQLException {
		String sql;
		if ("preentry".equals(type)) {
			sql = "update preentry set V_VISITOR_FLG = ? where V_VID = ?;";
		} else {
			sql = "update appointedday set V_VISITOR_FLG = ? where V_VID = ?;";
		}
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "1");
		ps.setString(2, id);
		ps.executeUpdate();
		if (ps != null) {
			ps.close();
		}
	}
}
