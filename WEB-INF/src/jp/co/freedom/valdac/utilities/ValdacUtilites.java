package jp.co.freedom.valdac.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.valdac.ValdacBuhiDto;
import jp.co.freedom.master.dto.valdac.ValdacCardDto;
import jp.co.freedom.master.dto.valdac.ValdacQuestionDto;
import jp.co.freedom.master.dto.valdac.ValdacUserDataDto;

/**
 * Shimazu向けユーティリティクラス2
 *
 * @author フリーダム・グループ
 *
 */
public class ValdacUtilites {

	private static Object mTable;

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<String[]> createInstance(List<String[]> csvData,
			Integer event) {
		List<String[]> userDataList = new ArrayList<String[]>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理0
		Integer length = csvData.get(0).length;
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			Integer rowLen = row.length; // 実際の長
			if (rowLen > 0) {
				String[] result = new String[length + 1];
				result[0] = String.valueOf(event + nIndex);
				for (int nTable = 1; nTable < rowLen; nTable++) {
					result[nTable] = toBigJp(row[nTable]);
				}
				for (int mTable = rowLen; mTable < length + 1; mTable++) {
					result[mTable] = "";
				}
				result[length] = "end";
				userDataList.add(result);
			}
		}
		return userDataList;
	}

	/**
	 * カナ半角を全角変換.
	 *
	 * @param string
	 *            変換対象の文字列を設定します.
	 * @return String 変換された文字列が返されます.
	 */
	public static final String toBigJp(String string) {
		if (string == null || string.length() <= 0) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int len = string.length();
		char[] o = new char[1];
		for (int i = 0; i < len; i++) {
			i += toBigJpOne(o, string, i);
			buf.append(o[0]);
		}
		return buf.toString();
	}

	/**
	 * (カナ)半角から全角変換.
	 */
	private static final int toBigJpOne(char[] out, String str, int index) {
		char code = str.charAt(index);
		if (index + 1 >= str.length()) {
			switch (code) {
			case 'ｧ':
				out[0] = 'ァ';
				return 0;
			case 'ｨ':
				out[0] = 'ィ';
				return 0;
			case 'ｩ':
				out[0] = 'ゥ';
				return 0;
			case 'ｪ':
				out[0] = 'ェ';
				return 0;
			case 'ｫ':
				out[0] = 'ォ';
				return 0;
			case 'ｬ':
				out[0] = 'ャ';
				return 0;
			case 'ｭ':
				out[0] = 'ュ';
				return 0;
			case 'ｮ':
				out[0] = 'ョ';
				return 0;
			case 'ｯ':
				out[0] = 'ッ';
				return 0;
			case 'ｰ':
				out[0] = 'ー';
				return 0;
			case 'ｱ':
				out[0] = 'ア';
				return 0;
			case 'ｲ':
				out[0] = 'イ';
				return 0;
			case 'ｳ':
				out[0] = 'ウ';
				return 0;
			case 'ｴ':
				out[0] = 'エ';
				return 0;
			case 'ｵ':
				out[0] = 'オ';
				return 0;
			case 'ｶ':
				out[0] = 'カ';
				return 0;
			case 'ｷ':
				out[0] = 'キ';
				return 0;
			case 'ｸ':
				out[0] = 'ク';
				return 0;
			case 'ｹ':
				out[0] = 'ケ';
				return 0;
			case 'ｺ':
				out[0] = 'コ';
				return 0;
			case 'ｻ':
				out[0] = 'サ';
				return 0;
			case 'ｼ':
				out[0] = 'シ';
				return 0;
			case 'ｽ':
				out[0] = 'ス';
				return 0;
			case 'ｾ':
				out[0] = 'セ';
				return 0;
			case 'ｿ':
				out[0] = 'ソ';
				return 0;
			case 'ﾀ':
				out[0] = 'タ';
				return 0;
			case 'ﾁ':
				out[0] = 'チ';
				return 0;
			case 'ﾂ':
				out[0] = 'ツ';
				return 0;
			case 'ﾃ':
				out[0] = 'テ';
				return 0;
			case 'ﾄ':
				out[0] = 'ト';
				return 0;
			case 'ﾅ':
				out[0] = 'ナ';
				return 0;
			case 'ﾆ':
				out[0] = 'ニ';
				return 0;
			case 'ﾇ':
				out[0] = 'ヌ';
				return 0;
			case 'ﾈ':
				out[0] = 'ネ';
				return 0;
			case 'ﾉ':
				out[0] = 'ノ';
				return 0;
			case 'ﾊ':
				out[0] = 'ハ';
				return 0;
			case 'ﾋ':
				out[0] = 'ヒ';
				return 0;
			case 'ﾌ':
				out[0] = 'フ';
				return 0;
			case 'ﾍ':
				out[0] = 'ヘ';
				return 0;
			case 'ﾎ':
				out[0] = 'ホ';
				return 0;
			case 'ﾏ':
				out[0] = 'マ';
				return 0;
			case 'ﾐ':
				out[0] = 'ミ';
				return 0;
			case 'ﾑ':
				out[0] = 'ム';
				return 0;
			case 'ﾒ':
				out[0] = 'メ';
				return 0;
			case 'ﾓ':
				out[0] = 'モ';
				return 0;
			case 'ﾔ':
				out[0] = 'ヤ';
				return 0;
			case 'ﾕ':
				out[0] = 'ユ';
				return 0;
			case 'ﾖ':
				out[0] = 'ヨ';
				return 0;
			case 'ﾗ':
				out[0] = 'ラ';
				return 0;
			case 'ﾘ':
				out[0] = 'リ';
				return 0;
			case 'ﾙ':
				out[0] = 'ル';
				return 0;
			case 'ﾚ':
				out[0] = 'レ';
				return 0;
			case 'ﾛ':
				out[0] = 'ロ';
				return 0;
			case 'ﾜ':
				out[0] = 'ワ';
				return 0;
			case 'ｦ':
				out[0] = 'ヲ';
				return 0;
			case 'ﾝ':
				out[0] = 'ン';
				return 0;
			case 'ﾞ':
				out[0] = 'ﾞ';
				return 0;
			case 'ﾟ':
				out[0] = 'ﾟ';
				return 0;
			}
			out[0] = code;
			return 0;
		} else {
			int type = 0;
			char code2 = str.charAt(index + 1);
			if (code2 == 'ﾞ') {
				type = 1;
			} else if (code2 == 'ﾟ') {
				type = 2;
			}
			switch (code) {
			case 'ｧ':
				out[0] = 'ァ';
				return 0;
			case 'ｨ':
				out[0] = 'ィ';
				return 0;
			case 'ｩ':
				out[0] = 'ゥ';
				return 0;
			case 'ｪ':
				out[0] = 'ェ';
				return 0;
			case 'ｫ':
				out[0] = 'ォ';
				return 0;
			case 'ｬ':
				out[0] = 'ャ';
				return 0;
			case 'ｭ':
				out[0] = 'ュ';
				return 0;
			case 'ｮ':
				out[0] = 'ョ';
				return 0;
			case 'ｯ':
				out[0] = 'ッ';
				return 0;
			case 'ｰ':
				out[0] = 'ー';
				return 0;
			case 'ｱ':
				out[0] = 'ア';
				return 0;
			case 'ｲ':
				out[0] = 'イ';
				return 0;
			case 'ｳ':
				if (type == 1) {
					out[0] = 'ヴ';
					return 1;
				} else {
					out[0] = 'ウ';
					return 0;
				}
			case 'ｴ':
				out[0] = 'エ';
				return 0;
			case 'ｵ':
				out[0] = 'オ';
				return 0;
			case 'ｶ':
				if (type == 1) {
					out[0] = 'ガ';
					return 1;
				} else {
					out[0] = 'カ';
					return 0;
				}
			case 'ｷ':
				if (type == 1) {
					out[0] = 'ギ';
					return 1;
				} else {
					out[0] = 'キ';
					return 0;
				}
			case 'ｸ':
				if (type == 1) {
					out[0] = 'グ';
					return 1;
				} else {
					out[0] = 'ク';
					return 0;
				}

			case 'ｹ':
				if (type == 1) {
					out[0] = 'ゲ';
					return 1;
				} else {
					out[0] = 'ケ';
					return 0;
				}
			case 'ｺ':
				if (type == 1) {
					out[0] = 'ゴ';
					return 1;
				} else {
					out[0] = 'コ';
					return 0;
				}
			case 'ｻ':
				if (type == 1) {
					out[0] = 'ザ';
					return 1;
				} else {
					out[0] = 'サ';
					return 0;
				}
			case 'ｼ':
				if (type == 1) {
					out[0] = 'ジ';
					return 1;
				} else {
					out[0] = 'シ';
					return 0;
				}
			case 'ｽ':
				if (type == 1) {
					out[0] = 'ズ';
					return 1;
				} else {
					out[0] = 'ス';
					return 0;
				}
			case 'ｾ':
				if (type == 1) {
					out[0] = 'ゼ';
					return 1;
				} else {
					out[0] = 'セ';
					return 0;
				}
			case 'ｿ':
				if (type == 1) {
					out[0] = 'ゾ';
					return 1;
				} else {
					out[0] = 'ソ';
					return 0;
				}
			case 'ﾀ':
				if (type == 1) {
					out[0] = 'ダ';
					return 1;
				} else {
					out[0] = 'タ';
					return 0;
				}
			case 'ﾁ':
				if (type == 1) {
					out[0] = 'ヂ';
					return 1;
				} else {
					out[0] = 'チ';
					return 0;
				}
			case 'ﾂ':
				if (type == 1) {
					out[0] = 'ヅ';
					return 1;
				} else {
					out[0] = 'ツ';
					return 0;
				}
			case 'ﾃ':
				if (type == 1) {
					out[0] = 'デ';
					return 1;
				} else {
					out[0] = 'テ';
					return 0;
				}
			case 'ﾄ':
				if (type == 1) {
					out[0] = 'ド';
					return 1;
				} else {
					out[0] = 'ト';
					return 0;
				}
			case 'ﾅ':
				out[0] = 'ナ';
				return 0;
			case 'ﾆ':
				out[0] = 'ニ';
				return 0;
			case 'ﾇ':
				out[0] = 'ヌ';
				return 0;
			case 'ﾈ':
				out[0] = 'ネ';
				return 0;
			case 'ﾉ':
				out[0] = 'ノ';
				return 0;
			case 'ﾊ':
				if (type == 1) {
					out[0] = 'バ';
					return 1;
				} else if (type == 2) {
					out[0] = 'パ';
					return 1;
				} else {
					out[0] = 'ハ';
					return 0;
				}
			case 'ﾋ':
				if (type == 1) {
					out[0] = 'ビ';
					return 1;
				} else if (type == 2) {
					out[0] = 'ピ';
					return 1;
				} else {
					out[0] = 'ヒ';
					return 0;
				}
			case 'ﾌ':
				if (type == 1) {
					out[0] = 'ブ';
					return 1;
				} else if (type == 2) {
					out[0] = 'プ';
					return 1;
				} else {
					out[0] = 'フ';
					return 0;
				}
			case 'ﾍ':
				if (type == 1) {
					out[0] = 'ベ';
					return 1;
				} else if (type == 2) {
					out[0] = 'ペ';
					return 1;
				} else {
					out[0] = 'ヘ';
					return 0;
				}
			case 'ﾎ':
				if (type == 1) {
					out[0] = 'ボ';
					return 1;
				} else if (type == 2) {
					out[0] = 'ポ';
					return 1;
				} else {
					out[0] = 'ホ';
					return 0;
				}
			case 'ﾏ':
				out[0] = 'マ';
				return 0;
			case 'ﾐ':
				out[0] = 'ミ';
				return 0;
			case 'ﾑ':
				out[0] = 'ム';
				return 0;
			case 'ﾒ':
				out[0] = 'メ';
				return 0;
			case 'ﾓ':
				out[0] = 'モ';
				return 0;
			case 'ﾔ':
				out[0] = 'ヤ';
				return 0;
			case 'ﾕ':
				out[0] = 'ユ';
				return 0;
			case 'ﾖ':
				out[0] = 'ヨ';
				return 0;
			case 'ﾗ':
				out[0] = 'ラ';
				return 0;
			case 'ﾘ':
				out[0] = 'リ';
				return 0;
			case 'ﾙ':
				out[0] = 'ル';
				return 0;
			case 'ﾚ':
				out[0] = 'レ';
				return 0;
			case 'ﾛ':
				out[0] = 'ロ';
				return 0;
			case 'ﾜ':
				out[0] = 'ワ';
				return 0;
			case 'ｦ':
				out[0] = 'ヲ';
				return 0;
			case 'ﾝ':
				out[0] = 'ン';
				return 0;
			case 'ﾞ':
				out[0] = 'ﾞ';
				return 0;
			case 'ﾟ':
				out[0] = 'ﾟ';
				return 0;
			}
		}
		out[0] = code;
		return 0;
	}

	/**
	 * 照合結果をTXT形式でダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param catalogList
	 *            カタログリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<String[]> userDataList, String dim) throws IOException {
		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (String[] userdata : userDataList) {
			FileUtil.writer(userdata, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * DBの初期化
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 処理結果の成否のブール値
	 * @throws SQLException
	 */
	public static boolean resetDB(Connection conn, String tablename)
			throws SQLException {
		String sql = "DELETE FROM  " + tablename + " ;";
		PreparedStatement ps = conn.prepareStatement(sql);
		boolean result = ps.execute();
		if (ps != null) {
			ps.close();
		}
		return result;
	}

	/**
	 *機器システムテーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataKikiSys(Connection conn,
			List<String[]> csvDataList, String tablename) throws SQLException {
		int count = 1000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			Integer leng = csvData.length;
			for (int nIndex = 1; nIndex <= 28; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++);
			for (int nIndex =2; nIndex <= 5; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			for (int nIndex = 12; nIndex <= 30; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			for (int nIndex = 32; nIndex <= 35; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			int result = ps.executeUpdate();
			if (result == 0) {
				return false;
			}
			if (ps != null) {
				ps.close();
			}
		}
		return true;
	}

	/**
	 *機器テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataKiki(Connection conn,
			List<String[]> csvDataList, String tablename) throws SQLException {
		int count =20000000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			Integer leng = csvData.length;
			for (int nIndex = 1; nIndex <= 16; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++);
			for (int nIndex =1; nIndex <= 12; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, "");
			for (int nIndex = 27; nIndex <= 28; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			int result = ps.executeUpdate();
			if (result == 0) {
				return false;
			}
			if (ps != null) {
				ps.close();
			}
		}
		return true;
	}

	/**
	 *部品テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataBuhi(Connection conn,
			List<String[]> csvDataList, String tablename) throws SQLException {
		int count = 40000000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			Integer leng = csvData.length;
			for (int nIndex = 1; nIndex <= 21; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++);
			for (int nIndex =1; nIndex <= 8; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, "");//hyojunSiyou
			for (int nIndex = 9; nIndex <= 13; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			for (int nIndex = 15; nIndex <= 16; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			for (int nIndex = 18; nIndex <= 18; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, "");//imageId
			for (int nIndex = 29; nIndex <= 30; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			int result = ps.executeUpdate();
			if (result == 0) {
				return false;
			}
			if (ps != null) {
				ps.close();
			}
		}
		return true;
	}


	/**
	 * 機器システムID取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getKikiSysIdData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM kikisys;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();
			ValdacCardDto cardInfo = new ValdacCardDto(); // 名刺情報DTO
			ValdacQuestionDto questionInfo = new ValdacQuestionDto(); // アンケート情報DTO

			// 新機器システムID番号
			userdata.id = rs.getString("kikiSysId"); // バーコード番号
			userdata.KikiSysIdOld = StringUtil.concatWithDelimit("",
					rs.getString("kCode"),
					rs.getString("kikiSysSeq"));

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 機器ID取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getKikiIdData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM kiki;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();
			ValdacCardDto cardInfo = new ValdacCardDto(); // 名刺情報DTO
			ValdacQuestionDto questionInfo = new ValdacQuestionDto(); // アンケート情報DTO
			// 機器Key
			userdata.kikiIDOld = StringUtil.concatWithDelimit("",
					rs.getString("kikiIdOld"),
					rs.getString("kikiBunrui"),
					rs.getString("kikiBunruiSeq"));
			// 機器Key
			userdata.id = StringUtil.concatWithDelimit("",
					rs.getString("kikiIdOld"),
					rs.getString("kikiBunrui"),
					rs.getString("kikiBunruiSeq"));
			// 機器ID番号
			userdata.kikiID = rs.getString("kikiId");

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 機器部品ID取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getBuhiIdData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM buhin;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();
			ValdacCardDto cardInfo = new ValdacCardDto(); // 名刺情報DTO
			ValdacQuestionDto questionInfo = new ValdacQuestionDto(); // アンケート情報DTO

			// 機器Key
			userdata.buhinIDOld = StringUtil.concatWithDelimit("",
					"0",
					rs.getString("kikiSysIdOld"),
					rs.getString("kikiBunrui"),
					"0",
					rs.getString("kikiBunruiSeq"),
					rs.getString("buhinKbn"),
					rs.getString("buhinSeq"));
			userdata.id = StringUtil.concatWithDelimit("",
					"0",
					rs.getString("kikiSysIdOld"),
					rs.getString("kikiBunrui"),
					"0",
					rs.getString("kikiBunruiSeq"),
					rs.getString("buhinKbn"),
					rs.getString("buhinSeq"));
			// 機器ID番号
			userdata.buhinID = rs.getString("buhinId");

			userdata.questionInfo = questionInfo;
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return userDataList;
	}

	/**
	 * 【DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 *
	 * @param userDataList
	 *            <b>ValdacUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, ValdacUserDataDto> getKikiDataIdMap(
			List<ValdacUserDataDto> userDataList) {
		Map<String, ValdacUserDataDto> map = new HashMap<String, ValdacUserDataDto>();
		for (ValdacUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 照合結果をTXT形式でダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            照合結果を格納するリスト
	 * @param dim
	 *            デリミタ
	 * @param catalogList
	 *            カタログリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean downLoadIdRealation(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<ValdacUserDataDto> userDataList, String dim)
			throws IOException {
		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		List<String> header = new ArrayList<String>();
		header.add(String.valueOf("id"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikiid"));
		header.add(StringUtil.enquote("buhinid"));
		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新ID
		int count = 1;

		for (ValdacUserDataDto userdata : userDataList) {
			Map<String, ValdacBuhiDto> kiki = userdata.kikiList;

			if (kiki!=null){
				Iterator kikiSort = kiki.entrySet().iterator();
				while (kikiSort.hasNext()) {
					Map.Entry entry = (Map.Entry)kikiSort.next();
					String keyName = (String)entry.getKey();
					ValdacBuhiDto buhinList=(ValdacBuhiDto)entry.getValue();
					//部品部分
					if (buhinList.buhinIDList.size()>0) {
						for (int nIndex = 0; nIndex < buhinList.buhinIDList.size(); nIndex++) {
									List<String> cols = new ArrayList<String>();
									cols.add(String.valueOf(count++));
									cols.add(StringUtil.enquote(userdata.id));
									cols.add(StringUtil.enquote(keyName));
									cols.add(StringUtil.enquote(buhinList.buhinIDList
											.get(nIndex)));
									FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
						}
					} else {
						List<String> cols = new ArrayList<String>();
						cols.add(String.valueOf(count++));
						cols.add(StringUtil.enquote(userdata.id));
						cols.add(StringUtil.enquote(keyName));
						cols.add(StringUtil.enquote(""));
						FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
					}
				}
			}else{
				List<String> cols = new ArrayList<String>();
				cols.add(String.valueOf(count++));
				cols.add(StringUtil.enquote(userdata.id));
				cols.add(StringUtil.enquote(""));
				cols.add(StringUtil.enquote(""));
				FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
			}
			}
	     try {
			writer.close();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}