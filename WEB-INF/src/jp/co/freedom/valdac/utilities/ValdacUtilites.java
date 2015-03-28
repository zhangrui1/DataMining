package jp.co.freedom.valdac.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	/**会社コード */
	public static Map<String, String> KCORD_MAP = createKcordMap();
	private static Map<String, String> createKcordMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "10101");
		map.put("2", "10102");
		map.put("3", "10103");
//		map.put("4", "10104");
		map.put("4", "10105");

		return map;
	}

	/**会社コード */
	public static Map<String, String> KCORD_MAP_2 = createKcord2Map();
	private static Map<String, String> createKcord2Map() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1");
		map.put("2", "100000");
		map.put("3", "200000");
		map.put("4", "300000");
		map.put("5", "400000");
		return map;
	}

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
//				result[0] = String.valueOf(event + nIndex);
				for (int nTable = 0; nTable < rowLen; nTable++) {
					result[nTable] = toBigJp(row[nTable]);
//				    result[nTable] = zenkakuToHankaku(row[nTable]);
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
	 * ユーザーデータのインスタンスを生成しリストに格納
	 *
	 * @param csvData
	 *            CSVデータ
	 * @param event
	 *            イベント名
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<String[]> createInstanceCheckDataLength(List<String[]> csvData,
			Integer event) {
		List<String[]> userDataList = new ArrayList<String[]>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理0
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			String[] result = new String[event + 1];
			if(row.length<=event){
				for (int nTable = 0; nTable < row.length; nTable++) {
					result[nTable] = toBigJp(row[nTable]);
				}
				result[event] = "end";
				userDataList.add(result);
			}else{
				System.out.println(row[0]+" , "+row[1]);
			}
		}
		return userDataList;
	}

	private static String zenkakuToHankaku(String value) {
	    StringBuilder sb = new StringBuilder(value);
	    for (int i = 0; i < sb.length(); i++) {
	        int c = (int) sb.charAt(i);
	        if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
	            sb.setCharAt(i, (char) (c - 0xFEE0));
	        }
	    }
	    value = sb.toString();
	    return value;
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
	public static boolean downLoadKoujiRealation(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<ValdacUserDataDto> userDataList, String dim) throws IOException {
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
		header.add(StringUtil.enquote("kouji"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));
		header.add(StringUtil.enquote("kikiid"));
		header.add(StringUtil.enquote("kikiidOld"));

		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新ID
		int count = 1;

		for (ValdacUserDataDto userdata : userDataList) {
			List<String> cols = new ArrayList<String>();
//			cols.add(String.valueOf(count++));
			cols.add(StringUtil.enquote(userdata.id));
			cols.add(StringUtil.enquote(userdata.koujiID));
			cols.add(StringUtil.enquote(userdata.koujiIDOld));
			cols.add(StringUtil.enquote(userdata.KikiSysId));
			cols.add(StringUtil.enquote(userdata.KikiSysIdOld));
			cols.add(StringUtil.enquote(userdata.kikiID));
			cols.add(StringUtil.enquote(userdata.kikiIDOld));
			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}

		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean downLoadKoujiRealation2(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Map<String, List<ValdacUserDataDto>> userDataList, String dim) throws IOException {
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
		header.add(StringUtil.enquote("kouji"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));
		header.add(StringUtil.enquote("kikiid"));
		header.add(StringUtil.enquote("kikiidOld"));

		header.add(StringUtil.enquote("tenkenSisin"));
		header.add(StringUtil.enquote("tenkenRank"));
		header.add(StringUtil.enquote("tenkenNaiyo"));
		header.add(StringUtil.enquote("gyosya"));
		header.add(StringUtil.enquote("tenkenKekka0"));
		header.add(StringUtil.enquote("tenkenKekka1"));
		header.add(StringUtil.enquote("tenkenKekka2"));
		header.add(StringUtil.enquote("tenkenKekka3"));
		header.add(StringUtil.enquote("tenkenKekka4"));
		header.add(StringUtil.enquote("tenkenKekka5"));
		header.add(StringUtil.enquote("tenkenNendo"));
		header.add(StringUtil.enquote("KanryoFlg"));

		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新ID
		int count = 1;
		for(int nIndex=1;nIndex<=ValdacUtilites.KCORD_MAP.size();nIndex++){
			List<ValdacUserDataDto> allKoujiKikiDataList = userDataList.get(ValdacUtilites.KCORD_MAP.get(String.valueOf(nIndex)));
			if (allKoujiKikiDataList!=null){
				for (ValdacUserDataDto userdata : allKoujiKikiDataList) {
					List<String> cols = new ArrayList<String>();
					cols.add(String.valueOf(count++));
	//				cols.add(StringUtil.enquote(userdata.id));
					cols.add(StringUtil.enquote(userdata.koujiID));
					cols.add(StringUtil.enquote(userdata.koujiIDOld));
					cols.add(StringUtil.enquote(userdata.KikiSysId));
					cols.add(StringUtil.enquote(userdata.KikiSysIdOld));
					cols.add(StringUtil.enquote(userdata.kikiID));
					cols.add(StringUtil.enquote(userdata.kikiIDOld));

					cols.add(StringUtil.enquote(userdata.tenkenSisin));
					cols.add(StringUtil.enquote(userdata.tenkenRank));
					cols.add(StringUtil.enquote(userdata.tenkenNaiyo));
					cols.add(StringUtil.enquote(userdata.gyosya));
					cols.add(StringUtil.enquote(userdata.tenkenKekka0));
					cols.add(StringUtil.enquote(userdata.tenkenKekka1));
					cols.add(StringUtil.enquote(userdata.tenkenKekka2));
					cols.add(StringUtil.enquote(userdata.tenkenKekka3));
					cols.add(StringUtil.enquote(userdata.tenkenKekka4));
					cols.add(StringUtil.enquote(userdata.tenkenKekka5));

					cols.add(StringUtil.enquote(userdata.tenkenNendo));
					cols.add(StringUtil.enquote(userdata.KanryoFlg));

					FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
				}
			}

		}

		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean downLoadTestKoujiRealation2(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			Map<String, List<ValdacUserDataDto>> userDataList, String dim,int startnIndex ) throws IOException {
		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定するstartnIndex
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		List<String> header = new ArrayList<String>();
		header.add(String.valueOf("id"));
		header.add(StringUtil.enquote("kouji"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));
		header.add(StringUtil.enquote("kikiid"));
		header.add(StringUtil.enquote("kikiidOld"));

		header.add(StringUtil.enquote("tenkenSisin"));
		header.add(StringUtil.enquote("tenkenRank"));
		header.add(StringUtil.enquote("tenkenNaiyo"));
		header.add(StringUtil.enquote("gyosya"));
		header.add(StringUtil.enquote("tenkenKekka0"));
		header.add(StringUtil.enquote("tenkenKekka1"));
		header.add(StringUtil.enquote("tenkenKekka2"));
		header.add(StringUtil.enquote("tenkenKekka3"));
		header.add(StringUtil.enquote("tenkenKekka4"));
		header.add(StringUtil.enquote("tenkenKekka5"));
		header.add(StringUtil.enquote("tenkenNendo"));
		header.add(StringUtil.enquote("KanryoFlg"));

		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新IDInteger.toString(nIndex)
		int count = 1;
		for(int nIndex=startnIndex;nIndex<startnIndex+20;nIndex++){
			List<ValdacUserDataDto> allKoujiKikiDataList = userDataList.get(String.valueOf(nIndex));
			if (allKoujiKikiDataList!=null){
				for (ValdacUserDataDto userdata : allKoujiKikiDataList) {
					List<String> cols = new ArrayList<String>();
//					cols.add(String.valueOf(count++));
					cols.add(StringUtil.enquote(userdata.id));
					cols.add(StringUtil.enquote(userdata.koujiID));
					cols.add(StringUtil.enquote(userdata.koujiIDOld));
					cols.add(StringUtil.enquote(userdata.KikiSysId));
					cols.add(StringUtil.enquote(userdata.KikiSysIdOld));
					cols.add(StringUtil.enquote(userdata.kikiID));
					cols.add(StringUtil.enquote(userdata.kikiIDOld));

					cols.add(StringUtil.enquote(userdata.tenkenSisin));
					cols.add(StringUtil.enquote(userdata.tenkenRank));
					cols.add(StringUtil.enquote(userdata.tenkenNaiyo));
					cols.add(StringUtil.enquote(userdata.gyosya));
					cols.add(StringUtil.enquote(userdata.tenkenKekka0));
					cols.add(StringUtil.enquote(userdata.tenkenKekka1));
					cols.add(StringUtil.enquote(userdata.tenkenKekka2));
					cols.add(StringUtil.enquote(userdata.tenkenKekka3));
					cols.add(StringUtil.enquote(userdata.tenkenKekka4));
					cols.add(StringUtil.enquote(userdata.tenkenKekka5));

					cols.add(StringUtil.enquote(userdata.tenkenNendo));
					cols.add(StringUtil.enquote(userdata.KanryoFlg));

					FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
				}
			}

		}

		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean downLoadTenkenRireki(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			 List<ValdacUserDataDto> userDataList, String dim) throws IOException {
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
		header.add(StringUtil.enquote("kouji"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));
		header.add(StringUtil.enquote("kikiid"));
		header.add(StringUtil.enquote("kikiidOld"));

		header.add(StringUtil.enquote("tenkenSisin"));
		header.add(StringUtil.enquote("tenkenRank"));
		header.add(StringUtil.enquote("tenkenNaiyo"));
		header.add(StringUtil.enquote("gyosya"));
		header.add(StringUtil.enquote("tenkenKekka0"));
		header.add(StringUtil.enquote("tenkenKekka1"));
		header.add(StringUtil.enquote("tenkenKekka2"));
		header.add(StringUtil.enquote("tenkenKekka3"));
		header.add(StringUtil.enquote("tenkenKekka4"));
		header.add(StringUtil.enquote("tenkenKekka5"));
		header.add(StringUtil.enquote("tenkenNendo"));
		header.add(StringUtil.enquote("KanryoFlg"));

		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新ID
		int count = 1580000;
			if (userDataList!=null){
				for (ValdacUserDataDto userdata : userDataList) {
					List<String> cols = new ArrayList<String>();
					cols.add(String.valueOf(count++));
	//				cols.add(StringUtil.enquote(userdata.id));
					cols.add(StringUtil.enquote(userdata.koujiID));
					cols.add(StringUtil.enquote(userdata.koujiIDOld));
					cols.add(StringUtil.enquote(userdata.KikiSysId));
					cols.add(StringUtil.enquote(userdata.KikiSysIdOld));
					cols.add(StringUtil.enquote(userdata.kikiID));
					cols.add(StringUtil.enquote(userdata.kikiIDOld));

					cols.add(StringUtil.enquote(userdata.tenkenSisin));
					cols.add(StringUtil.enquote(userdata.tenkenRank));
					cols.add(StringUtil.enquote(userdata.tenkenNaiyo));
					cols.add(StringUtil.enquote(userdata.gyosya));
					cols.add(StringUtil.enquote(userdata.tenkenKekka0));
					cols.add(StringUtil.enquote(userdata.tenkenKekka1));
					cols.add(StringUtil.enquote(userdata.tenkenKekka2));
					cols.add(StringUtil.enquote(userdata.tenkenKekka3));
					cols.add(StringUtil.enquote(userdata.tenkenKekka4));
					cols.add(StringUtil.enquote(userdata.tenkenKekka5));

					cols.add(StringUtil.enquote(userdata.tenkenNendo));
					cols.add(StringUtil.enquote(userdata.KanryoFlg));

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
	/**
	 * 懸案事項ダウンロード
	 *
	 * */
	public static boolean downLoadKenan(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			 List<ValdacUserDataDto> userDataList, String dim) throws IOException {
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
		header.add(StringUtil.enquote("koujiId"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));
		header.add(StringUtil.enquote("kikiId"));
		header.add(StringUtil.enquote("kikiidOld"));

		header.add(StringUtil.enquote("w05KenanNo"));
		header.add(StringUtil.enquote("hakkenDate"));
		header.add(StringUtil.enquote("taisakuDate"));
		header.add(StringUtil.enquote("taiouFlg"));
		header.add(StringUtil.enquote("jisyo"));
		header.add(StringUtil.enquote("buhin"));
		header.add(StringUtil.enquote("gensyo"));
		header.add(StringUtil.enquote("youin"));
		header.add(StringUtil.enquote("taisaku"));
		header.add(StringUtil.enquote("hakkenJyokyo"));
		header.add(StringUtil.enquote("syotiNaiyou"));
		header.add(StringUtil.enquote("trkDate"));
		header.add(StringUtil.enquote("updDate"));
		header.add(StringUtil.enquote("end"));
		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		 //現在日時を取得する
        Calendar c = Calendar.getInstance();
      //フォーマットパターンを指定して表示する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


		// 対応関係テーブルの新ID
		int count = 1700000;
			if (userDataList!=null){
				for (ValdacUserDataDto userdata : userDataList) {
					List<String> cols = new ArrayList<String>();
					cols.add(String.valueOf(count++));
					cols.add(StringUtil.enquote("0"));
					cols.add(StringUtil.enquote(userdata.koujiIDOld));
					cols.add(StringUtil.enquote(userdata.KikiSysId));
					cols.add(StringUtil.enquote(userdata.KikiSysIdOld));
					cols.add(StringUtil.enquote(userdata.kikiID));
					cols.add(StringUtil.enquote(userdata.kikiIDOld));

					cols.add(StringUtil.enquote(userdata.kenanNo));
					cols.add(StringUtil.enquote(userdata.hakkenDate));
					cols.add(StringUtil.enquote(userdata.taisakuDate));
					cols.add(StringUtil.enquote(userdata.taiouFlg));
					cols.add(StringUtil.enquote(userdata.jisyo));
					cols.add(StringUtil.enquote(userdata.buhin));
					cols.add(StringUtil.enquote(userdata.gensyo));
					cols.add(StringUtil.enquote(userdata.youin));
					cols.add(StringUtil.enquote(userdata.taisaku));
					cols.add(StringUtil.enquote(userdata.hakkenJyokyo));
					cols.add(StringUtil.enquote(userdata.syotiNaiyou));
					//作成時間
					cols.add(StringUtil.enquote(userdata.hakkenDate));
					//更新時間
					if("".equals(userdata.taisakuDate)){
						cols.add(StringUtil.enquote(userdata.hakkenDate));
					}else{
						cols.add(StringUtil.enquote(userdata.taisakuDate));
					}
					cols.add(StringUtil.enquote("end"));
//					cols.add(StringUtil.enquote(sdf.format(c.getTime())));
//					cols.add(StringUtil.enquote(sdf.format(c.getTime())));

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

	/**
	 * 画像ダウンロード
	 *
	 * */
	public static boolean downLoadImage(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			 List<ValdacUserDataDto> userDataList, String dim) throws IOException {
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
		header.add(StringUtil.enquote("koujiId"));
		header.add(StringUtil.enquote("koujiOld"));
		header.add(StringUtil.enquote("kikisysid"));
		header.add(StringUtil.enquote("kikisysidOld"));

		header.add(StringUtil.enquote("imagesyu"));
		header.add(StringUtil.enquote("imagesyuNoSub"));
		header.add(StringUtil.enquote("page"));
		header.add(StringUtil.enquote("imageDir"));
		header.add(StringUtil.enquote("imagename"));
		header.add(StringUtil.enquote("objectName"));
		header.add(StringUtil.enquote("imagenameAll"));

		header.add(StringUtil.enquote("papersize"));
		header.add(StringUtil.enquote("imagebiko"));
		header.add(StringUtil.enquote("tosyoMei"));

		header.add(StringUtil.enquote("trkDate"));
		header.add(StringUtil.enquote("updDate"));
		header.add(StringUtil.enquote("end"));
		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		 //現在日時を取得する
        Calendar c = Calendar.getInstance();
      //フォーマットパターンを指定して表示する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


		// 対応関係テーブルの新ID
		int count = 1;
			if (userDataList!=null){
				for (ValdacUserDataDto userdata : userDataList) {
					List<String> cols = new ArrayList<String>();
					cols.add(String.valueOf(count++));
					cols.add(StringUtil.enquote(userdata.koujiID));
					cols.add(StringUtil.enquote(userdata.koujiIDOld));
					cols.add(StringUtil.enquote(userdata.KikiSysId));
					cols.add(StringUtil.enquote(userdata.KikiSysIdOld));


					cols.add(StringUtil.enquote(userdata.imagesyu));
					cols.add(StringUtil.enquote(userdata.imagesyuNoSub));
					cols.add(StringUtil.enquote(userdata.page));
					cols.add(StringUtil.enquote(userdata.imageDir));
					cols.add(StringUtil.enquote(userdata.imagename));
					cols.add(StringUtil.enquote(userdata.objectName));
					cols.add(StringUtil.enquote(userdata.imagenameAll));

					cols.add(StringUtil.enquote(userdata.papersize));
					cols.add(StringUtil.enquote(userdata.imagebiko));
					cols.add(StringUtil.enquote(userdata.tosyoMei));


					//更新時間
					if("".equals(userdata.trkDate)){
						cols.add(StringUtil.enquote("2014/12/01"));
						cols.add(StringUtil.enquote("2014/12/01"));
					}else{
						cols.add(StringUtil.enquote(userdata.trkDate));
						cols.add(StringUtil.enquote(userdata.updDate));
					}
					cols.add(StringUtil.enquote("end"));


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

	/**
	 *顧客先ダウンロード
	 *
	 * */
	public static boolean downLoadKokyaku(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			 List<ValdacUserDataDto> userDataList, String dim) throws IOException {
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
		header.add(String.valueOf("kCode"));
		header.add(StringUtil.enquote("kCodeL"));
		header.add(StringUtil.enquote("kCodeM"));
		header.add(StringUtil.enquote("kCodeS"));
		header.add(StringUtil.enquote("kCodeLKanji"));
		header.add(StringUtil.enquote("kCodeMKanji"));
		header.add(StringUtil.enquote("kCodeSKanji"));
		header.add(StringUtil.enquote("kCodeKanji"));
		header.add(StringUtil.enquote("end"));
		FileUtil.writer(header, writer, dim); // headerのデータ書き出し

		// 対応関係テーブルの新ID
		int count = 1;
			if (userDataList!=null){
				for (ValdacUserDataDto userdata : userDataList) {
					List<String> cols = new ArrayList<String>();
					cols.add(String.valueOf(count++));
					cols.add(StringUtil.enquote(userdata.id));
					cols.add(StringUtil.enquote(userdata.kCodeL));
					cols.add(StringUtil.enquote(userdata.kCodeM));
					cols.add(StringUtil.enquote(userdata.kCodeS));


					cols.add(StringUtil.enquote(userdata.kCodeLKanji));
					cols.add(StringUtil.enquote(userdata.kCodeMKanji));
					cols.add(StringUtil.enquote(userdata.kCodeSKanji));
					cols.add(StringUtil.concatWithDelimit(" ",userdata.kCodeLKanji,userdata.kCodeMKanji,userdata.kCodeSKanji));
					cols.add(StringUtil.enquote("end"));


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
			List<String[]> csvDataList, String tablename,Map<String, ValdacUserDataDto> allLocationDataMap) throws SQLException {
		int count = 1000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			for (int nIndex = 1; nIndex <= ValdacConfig.Length_New_Kikisystem; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++); //新機器システムID設定
			//弁の顧客先
			String locationCode=csvData[2];
			String location=csvData[2];
			if(!("".equals(locationCode))){
				ValdacUserDataDto Tempresult=allLocationDataMap.get(csvData[2]);
				if (Tempresult!=null)
				location=Tempresult.kName;
			}
			ps.setString(++position,location);//弁の顧客先追加
			//kcode,kikisysseq,vno,vnosub,benmeisyo
			for (int nIndex =2; nIndex <= 6; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//keisikiryaku~ryutai
			for (int nIndex = 13; nIndex <= 30; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//ics
			ps.setString(++position, csvData[31]);
			//futai
			String futai=StringUtil.concatWithDelimit("", csvData[12],csvData[31],csvData[33]);
			ps.setString(++position, futai);

			//trkDate,updDate
			for (int nIndex = 34; nIndex <= 35; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//setBasyo,setKiki,setSetubi
			for (int nIndex = 8; nIndex <= 10; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//keitou
			ps.setString(++position, csvData[11]);
			//kougu1
			ps.setString(++position, csvData[12]);
			for (int nIndex = 2; nIndex <= 5; nIndex++) {
				ps.setString(++position, "");
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
			for (int nIndex = 1; nIndex <= ValdacConfig.Length_New_Kiki; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			//kikiID
			ps.setInt(++position, count++);
			for (int nIndex =1; nIndex <= 12; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, "");
			//trkDate,updDate
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
			for (int nIndex = 1; nIndex <= ValdacConfig.Length_New_Buhin; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			//BuhinID
			ps.setInt(++position, count++);
			//kikisysIdOld,kikiBunrui,kikiBunruiSeq,buhinKbn,buhinSeq,asbKbn
			for (int nIndex =1; nIndex <= 6; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, csvData[17]);//buhinzuBikou
			ps.setString(++position, csvData[8]);//buhinmei
			ps.setString(++position, csvData[14]);//hyojunSiyou
			//buhinMei,siyouKasyo,sizaiName,hiban,maker...
			for (int nIndex = 9; nIndex <= 13; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//sunpon,bikou
			for (int nIndex = 15; nIndex <= 16; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//suryo
			for (int nIndex = 18; nIndex <= 18; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			ps.setString(++position, "");//imageId
			//trkDate,updDate
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
	 *工事テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataKouji(Connection conn,
			List<String[]> csvDataList, String tablename,Map<String, ValdacUserDataDto> allLocationDataMap) throws SQLException {
		int count = 60000000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			Integer leng = csvData.length;
			for (int nIndex = 1; nIndex <= ValdacConfig.Length_New_Kouji; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++);
			for (int nIndex =3; nIndex <= 10; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
			//gyosyaRyakuA
			for (int nIndex = 15; nIndex <= 15; nIndex++) {
				ps.setString(++position, csvData[nIndex]);
			}
            //location
			for (int nIndex = 16; nIndex <= 16; nIndex++) {
//				String location=StringUtil.concatWithDelimit("",csvData[1],csvData[2]);
				String locationCode=csvData[1];
				String location=csvData[1];
				if(!("".equals(locationCode))){
					ValdacUserDataDto Tempresult=allLocationDataMap.get(csvData[1]);
					if (Tempresult!=null)
					location=Tempresult.kName;
				}
				ps.setString(++position,location);
			}
			ps.setString(++position, csvData[13]);//status
			ps.setString(++position, "");//person
			ps.setString(++position, "2015/04/01");//person
			ps.setString(++position, "2015/04/01");//person

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
	 *工事作業用テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataKoujiTemp(Connection conn,
			List<String[]> csvDataList, String tablename) throws SQLException {
		int count = 60000000;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();
			Integer leng = csvData.length;
			for (int nIndex = 1; nIndex <= 2; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setInt(++position, count++);
			String koujiOldId=StringUtil.concatWithDelimit("", csvData[1],csvData[2]);
			ps.setString(++position, koujiOldId);//koujiOldId


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
	 *点検履歴機器テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importDataTenkenKiki(Connection conn,
			List<ValdacUserDataDto> allKoujiRirekiDataList, String tablename) throws SQLException {
		int count = 1;
		for (ValdacUserDataDto koujiRirekiDataList : allKoujiRirekiDataList) {
			String sql = "INSERT INTO  " + tablename + " VALUES (";
			List<String> query = new ArrayList<String>();

			for (int nIndex = 1; nIndex <= ValdacConfig.Length_New_TenkenRireki; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			//ID
			ps.setInt(++position, count++);
			//koujiId
			ps.setString(++position,koujiRirekiDataList.koujiID);
			ps.setString(++position,koujiRirekiDataList.KikiSysId);
			ps.setString(++position,koujiRirekiDataList.kikiID);
			ps.setString(++position,koujiRirekiDataList.tenkenDate);
			ps.setString(++position,koujiRirekiDataList.tenkenNendo);
			ps.setString(++position,koujiRirekiDataList.tenkenRank);
			ps.setString(++position,koujiRirekiDataList.tenkenNaiyo);
			ps.setString(++position,koujiRirekiDataList.tenkenKekka0);
			ps.setString(++position,koujiRirekiDataList.tenkenBikou);
			ps.setString(++position,koujiRirekiDataList.KanryoFlg);
			ps.setString(++position,koujiRirekiDataList.tenkenDate);
			ps.setString(++position,koujiRirekiDataList.tenkenDate);


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
	 *懸案テーブルに CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLExceptionKenan
	 */
	public static boolean importDataKenan(Connection conn,
			List<String[]> csvDataList, String tablename) throws SQLException {
		int count = 80000000;
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
//			ps.setInt(++position, count++);
			ps.setString(++position, csvData[0]);//Id

			ps.setString(++position, csvData[1]);//Id
			ps.setString(++position, csvData[1]);//Id
			ps.setString(++position, csvData[5]);//Id

//			ps.setInt(++position, 1);//koujiId
//			ps.setInt(++position, 1);//koujirelationId
//			ps.setInt(++position, Integer.parseInt(csvData[5]));//koujirelationId
			for (int nIndex =8; nIndex <= 19; nIndex++) {
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
		String sql = "SELECT * FROM kikisystem;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			// 新機器システムID番号
			userdata.id = rs.getString("kikiSysId"); // バーコード番号
			userdata.KikiSysId = rs.getString("kikiSysId"); // バーコード番号
			userdata.KikiSysIdOld = StringUtil.concatWithDelimit("",
					rs.getString("kCode"),
					rs.getString("kikiSysSeq"));
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
	 * 顧客データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getKokyakuData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM x01kokyaku;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			// 新機器システムID番号
			userdata.kCodeL = rs.getString("x01KCodeL"); // 会社名
			userdata.kCodeM = rs.getString("x01KCodeM"); // 会社名
			userdata.kCodeS = rs.getString("x01KCodeS"); // 会社名
			userdata.kName=toBigJp(rs.getString("x01KName"));
		    userdata.id=StringUtil.concatWithDelimit("",userdata.kCodeL,userdata.kCodeM,userdata.kCodeS);
			userdata.kCode= StringUtil.concatWithDelimit("",userdata.kCodeL,userdata.kCodeM,userdata.kCodeS);

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
	 * 顧客データ変更後のデータを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getKokyakuAfterData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM location;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			userdata.kCode=toBigJp(rs.getString("kCode"));
			userdata.kName=toBigJp(rs.getString("kCodeKanji"));
		    userdata.id=toBigJp(rs.getString("kCode"));


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
	 * 工事ID取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<ValdacUserDataDto> getKoujiIdData(Connection conn)
			throws SQLException {
		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql = "SELECT * FROM tempkouji;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			ValdacUserDataDto userdata = new ValdacUserDataDto();
			ValdacCardDto cardInfo = new ValdacCardDto(); // 名刺情報DTO
			ValdacQuestionDto questionInfo = new ValdacQuestionDto(); // アンケート情報DTO
			// 工事新ID
			userdata.id = rs.getString("id");
			// 工事OldID
			userdata.koujiIDOld = rs.getString("koujiidOld");
			userdata.koujiID = rs.getString("id");
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
	 * 工事の点検機器
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> getKoujiKikiIdData(Connection conn,String KCodename,HttpServletRequest request,
			HttpServletResponse response)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql="";
		sql = "SELECT * FROM k04tenkenkiki  where k04KCode ='"+KCodename+"' ;";
//		if (KCodename=="10104-1"){
//			 sql = "SELECT * FROM k04tenkenkiki  where k04KCode ='10104' and  k04KjSeq>'110';";
//		}else if (KCodename=="10104-2"){
//			 sql = "SELECT * FROM k04tenkenkiki  where k04KCode ='10104' and  k04KjSeq<='110';";
//		}else{
//			 sql = "SELECT * FROM k04tenkenkiki  where k04KCode ='"+KCodename+"' ;";
//		}

		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;

		while (rs.next()) {
			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			String KCode=StringUtil.concatWithDelimit("", "0",rs.getString("k04KCode"));
			String KjSeq=StringUtil.convertFixedLengthData(rs.getString("k04KjSeq"),3,"0");
			String Kikisys=StringUtil.concatWithDelimit("", "0",rs.getString("k04KikiSysId"));
			String KikiBunrui=rs.getString("k04KikiBunrui");
			String KikiBunruiSeq=StringUtil.convertFixedLengthData(rs.getString("k04KikiBunruiSeq"),2,"0");

//			userdata.id=StringUtil.concatWithDelimit("",KCode,KjSeq, Kikisys,KikiBunrui,KikiBunruiSeq);
            userdata.id=Integer.toString(count);
			// 工事旧ID
			userdata.koujiIDOld = StringUtil.concatWithDelimit("", KCode,KjSeq);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;
			// kiki旧ID
			userdata.kikiIDOld = StringUtil.concatWithDelimit("", Kikisys,KikiBunrui,KikiBunruiSeq);

//			//点検結果部分
//			userdata.tenkenSisin=rs.getString("k04TenkenSisin");
//			userdata.tenkenRank=rs.getString("k04TenkenRank");
//			userdata.tenkenNaiyo=rs.getString("k04TenkenNaiyo");
//			userdata.gyosya=rs.getString("k04Gyosya");
//			userdata.tenkenKekka0=rs.getString("k04Mae0Nen");
//			userdata.tenkenKekka1=rs.getString("k04Mae1Nen");
//			userdata.tenkenKekka2=rs.getString("k04Mae2Nen");
//			userdata.tenkenKekka3=rs.getString("k04Mae3Nen");
//			userdata.tenkenKekka4=rs.getString("k04Mae4Nen");
//			userdata.tenkenKekka5=rs.getString("k04Mae5Nen");

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
	 * 工事の点検機器
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> getKoujiKikiIdDataForStep(Connection conn,int nIndex)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();

		int start=nIndex*10000;
		int end=(nIndex+1)*10000;

		String sql= "SELECT * FROM k04tenkenkiki where id >= "+ start + " and id<" + end+" order by id;";


		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=0;

		while (rs.next()) {

			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();
			String KCode=StringUtil.convertFixedLengthData(rs.getString("k04KCode"),6,"0");
//			String KCode=StringUtil.concatWithDelimit("", "0",rs.getString("k04KCode"));
			String KjSeq=StringUtil.convertFixedLengthData(rs.getString("k04KjSeq"),3,"0");
			String Kikisys=StringUtil.convertFixedLengthData(rs.getString("k04KikiSysId"),11,"0");
//			String Kikisys=StringUtil.concatWithDelimit("", "0",rs.getString("k04KikiSysId"));
			String KikiBunrui=rs.getString("k04KikiBunrui");
			String KikiBunruiSeq=StringUtil.convertFixedLengthData(rs.getString("k04KikiBunruiSeq"),2,"0");

//			userdata.id=StringUtil.concatWithDelimit("",KCode,KjSeq, Kikisys,KikiBunrui,KikiBunruiSeq);
//            userdata.id=Integer.toString(count);
			 userdata.id=rs.getString("id");
			// 工事旧ID
			userdata.koujiIDOld = StringUtil.concatWithDelimit("", KCode,KjSeq);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;
			// kiki旧ID
			userdata.kikiIDOld = StringUtil.concatWithDelimit("", Kikisys,KikiBunrui,KikiBunruiSeq);

			//点検結果部分
			userdata.tenkenSisin=toBigJp(rs.getString("k04TenkenSisin"));
			userdata.tenkenRank=toBigJp(rs.getString("k04TenkenRank"));
			userdata.tenkenNaiyo=toBigJp(rs.getString("k04TenkenNaiyo"));
			userdata.gyosya=toBigJp(rs.getString("k04Gyosya"));
			userdata.tenkenKekka0=toBigJp(rs.getString("k04Mae0Nen"));
			userdata.tenkenKekka1=toBigJp(rs.getString("k04Mae1Nen"));
			userdata.tenkenKekka2=toBigJp(rs.getString("k04Mae2Nen"));
			userdata.tenkenKekka3=toBigJp(rs.getString("k04Mae3Nen"));
			userdata.tenkenKekka4=toBigJp(rs.getString("k04Mae4Nen"));
			userdata.tenkenKekka5=toBigJp(rs.getString("k04Mae5Nen"));

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
	 * 工事の点検機器履歴
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> getTenkenRirekiIdData(Connection conn)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql="SELECT * FROM k05Tenkenrireki ;";

		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;
		while (rs.next()) {
			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			String KCode=StringUtil.convertFixedLengthData(rs.getString("k05KCode"),6,"0");
			String KjSeq=StringUtil.convertFixedLengthData(rs.getString("k05KjSeq"),3,"0");
			String Kikisys=StringUtil.convertFixedLengthData(rs.getString("k05KikiSysId"),11,"0");
			String KikiBunrui=rs.getString("k05KikiBunrui");
			String KikiBunruiSeq=StringUtil.convertFixedLengthData(rs.getString("k05KikiBunruiSeq"),2,"0");

//			userdata.id=StringUtil.concatWithDelimit("",KCode,KjSeq, Kikisys,KikiBunrui,KikiBunruiSeq);
            userdata.id=Integer.toString(count);
			// 工事旧ID
			userdata.koujiIDOld = StringUtil.concatWithDelimit("", KCode,KjSeq);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;
			// kiki旧ID
			userdata.kikiIDOld = StringUtil.concatWithDelimit("", Kikisys,KikiBunrui,KikiBunruiSeq);

//			//点検結果部分

			userdata.tenkenRank=toBigJp(rs.getString("k05TenkenRank"));
			userdata.tenkenNaiyo=toBigJp(rs.getString("k05Naiyo"));
			userdata.tenkenKekka0=toBigJp(rs.getString("k05Kekka"));
            userdata.tenkenNendo=toBigJp(rs.getString("k05TenkenNendo"));
            userdata.KanryoFlg=toBigJp(rs.getString("k05KanryoFlg"));

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
	 * 懸案事項
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> getKenanData(Connection conn)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql="SELECT * FROM w05Kenan ;";

		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;
		while (rs.next()) {
			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			String Kikisys=StringUtil.convertFixedLengthData(rs.getString("w05KikiSysId"),11,"0");
			String KikiBunrui=rs.getString("w05KikiBunrui");
			String KikiBunruiSeq=StringUtil.convertFixedLengthData(rs.getString("w05KikiBunruiSeq"),2,"0");

            userdata.id=Integer.toString(count);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;
			// kiki旧ID
			userdata.kikiIDOld = StringUtil.concatWithDelimit("", Kikisys,KikiBunrui,KikiBunruiSeq);

//			//懸案事項部分

			userdata.kenanNo=toBigJp(rs.getString("w05KenanNo"));
			userdata.hakkenDate=toBigJp(rs.getString("w05HakkenDate"));
			userdata.taisakuDate=toBigJp(rs.getString("w05TaisakuDate"));
            userdata.taiouFlg=toBigJp(rs.getString("w05TaiouFlg"));
            userdata.jisyo=toBigJp(rs.getString("w05Jisyo"));
            userdata.buhin=toBigJp(rs.getString("w05Buhin"));
            userdata.gensyo=toBigJp(rs.getString("w05Gensyo"));
            userdata.youin=toBigJp(rs.getString("w05Youin"));
            userdata.taisaku=toBigJp(rs.getString("w05Taisaku"));
            userdata.hakkenJyokyo=toBigJp(rs.getString("w05HakkenJyokyo"));
            userdata.syotiNaiyou=toBigJp(rs.getString("w05SyotiNaiyou"));


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
	 * 画像
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> getImageData(Connection conn)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();
		String sql="SELECT * FROM w04image where w04KikiSysId  not like '%STOCK%';";

		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		int count=1;
		while (rs.next()) {
			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			String Kikisys=StringUtil.convertFixedLengthData(rs.getString("w04KikiSysId"),11,"0");

            userdata.id=Integer.toString(count);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;

			//画像部分

			userdata.imagesyu=toBigJp(rs.getString("w04ImageSyu"));
			userdata.imagesyuNoSub=toBigJp(rs.getString("w04ImageSyuNoSub"));
			userdata.page=toBigJp(rs.getString("w04Page"));
			userdata.imageDir=toBigJp(rs.getString("w04ImageDir"));
			userdata.imagename=toBigJp(rs.getString("w04ImageName"));
			userdata.objectName=toBigJp(rs.getString("w04ObjectName"));
			userdata.imagenameAll=StringUtil.concatWithDelimit("",toBigJp(rs.getString("w04ImageDir")),toBigJp(rs.getString("w04ImageName")));
            userdata.papersize=toBigJp(rs.getString("w04PaperSize"));
            userdata.imagebiko=toBigJp(rs.getString("w04ImageSyubetu"));
            userdata.tosyoMei=toBigJp(rs.getString("w04TosyoMei"));
            userdata.trkDate=rs.getString("w04SakuseiDate");
            userdata.updDate=rs.getString("w04SakuseiDate");

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
//			userdata.buhinIDOld = StringUtil.concatWithDelimit("",
//					"0",
//					rs.getString("kikiSysIdOld"),
//					rs.getString("kikiBunrui"),
//					"0",
//					rs.getString("kikiBunruiSeq"),
//					rs.getString("buhinKbn"),
//					rs.getString("buhinSeq"));
//			userdata.id = StringUtil.concatWithDelimit("",
//					"0",
//					rs.getString("kikiSysIdOld"),
//					rs.getString("kikiBunrui"),
//					"0",
//					rs.getString("kikiBunruiSeq"),
//					rs.getString("buhinKbn"),
//					rs.getString("buhinSeq"));
			userdata.buhinIDOld = StringUtil.concatWithDelimit("",
					rs.getString("kikiSysIdOld"),
					rs.getString("kikiBunrui"),
					rs.getString("kikiBunruiSeq"),
					rs.getString("buhinKbn"),
					rs.getString("buhinSeq"));
			userdata.id = StringUtil.concatWithDelimit("",
					rs.getString("kikiSysIdOld"),
					rs.getString("kikiBunrui"),
					rs.getString("kikiBunruiSeq"),
					rs.getString("buhinKbn"),
					rs.getString("buhinSeq"));

			// 機器ID番号
			userdata.buhinID = rs.getString("buhinId");


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
	 * 工事の点検機器履歴
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<ValdacUserDataDto> changeToUserDataList(List<String[]> csvData)
			throws SQLException, IOException {

		List<ValdacUserDataDto> userDataList = new ArrayList<ValdacUserDataDto>();

		int count=0;
		for (String[] TempcsvData : csvData){
			count=count+1;
			ValdacUserDataDto userdata = new ValdacUserDataDto();

			String KCode=StringUtil.convertFixedLengthData(TempcsvData[7],6,"0");
			String KjSeq=StringUtil.convertFixedLengthData(TempcsvData[8],3,"0");
			String Kikisys=StringUtil.convertFixedLengthData(TempcsvData[1],11,"0");
			String KikiBunrui=TempcsvData[2];
			String KikiBunruiSeq=StringUtil.convertFixedLengthData(TempcsvData[3],2,"0");

//			userdata.id=StringUtil.concatWithDelimit("",KCode,KjSeq, Kikisys,KikiBunrui,KikiBunruiSeq);
            userdata.id=Integer.toString(count);
			// 工事旧ID
			userdata.koujiIDOld = StringUtil.concatWithDelimit("", KCode,KjSeq);
			// 弁旧ID
			userdata.KikiSysIdOld = Kikisys;
			// kiki旧ID
			userdata.kikiIDOld = StringUtil.concatWithDelimit("", Kikisys,KikiBunrui,KikiBunruiSeq);

//			//点検結果部分

			userdata.tenkenRank=toBigJp(TempcsvData[6]);
			userdata.tenkenNaiyo=toBigJp(TempcsvData[14]);
			userdata.tenkenKekka0=toBigJp(TempcsvData[13]);
            userdata.tenkenNendo=toBigJp(TempcsvData[5]);
            userdata.KanryoFlg=toBigJp(TempcsvData[15]);

			userDataList.add(userdata);
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
			//kikisysIDのみ行
			List<String> colsKikisystem = new ArrayList<String>();
			colsKikisystem.add(String.valueOf(count++));
			colsKikisystem.add(String.valueOf(userdata.id));
			colsKikisystem.add(String.valueOf("0"));
			colsKikisystem.add(String.valueOf("0"));
			FileUtil.writer(colsKikisystem, writer, dim); // 1レコード分のデータ書き出し

			if (kiki!=null){
				Iterator kikiSort = kiki.entrySet().iterator();
				while (kikiSort.hasNext()) {
					Map.Entry entry = (Map.Entry)kikiSort.next();
					String keyName = (String)entry.getKey();
					ValdacBuhiDto buhinList=(ValdacBuhiDto)entry.getValue();
					//部品部分
					if (buhinList.buhinIDList.size()>0) {
						//kikisystem,kikiのみの行
						List<String> colsKiki = new ArrayList<String>();
						colsKiki.add(String.valueOf(count++));
						colsKiki.add(StringUtil.enquote(userdata.id));
						colsKiki.add(StringUtil.enquote(keyName));
						colsKiki.add(StringUtil.enquote("0"));
						FileUtil.writer(colsKiki, writer, dim); // 1レコード分のデータ書き出し

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
						cols.add(StringUtil.enquote("0"));
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


/**
 *
 * 顧客IDをKey
 *
 * */
	public static Map<String, ValdacUserDataDto> getallKokyakuDataMap(
			List<ValdacUserDataDto> AllDatalist) {
		Map<String, ValdacUserDataDto> map = new HashMap<String, ValdacUserDataDto>();
		for (ValdacUserDataDto userData : AllDatalist) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}
	/**
	 *
	 * 機器システムの旧IDをKey
	 *
	 * */
		public static Map<String, ValdacUserDataDto> getallKikiSysIdDataMap(
				List<ValdacUserDataDto> AllDatalist) {
			Map<String, ValdacUserDataDto> map = new HashMap<String, ValdacUserDataDto>();
			for (ValdacUserDataDto userData : AllDatalist) {
				if (StringUtil.isNotEmpty(userData.id)) {
					map.put(userData.KikiSysIdOld, userData);
				}
			}
			return map;
		}
	/**
	 *
	 * Kiki旧IDをKey
	 *
	 * */
		public static Map<String, ValdacUserDataDto> getallKikiDataMap(
				List<ValdacUserDataDto> AllDatalist) {
			Map<String, ValdacUserDataDto> map = new HashMap<String, ValdacUserDataDto>();
			for (ValdacUserDataDto userData : AllDatalist) {
				if (StringUtil.isNotEmpty(userData.id)) {
					map.put(userData.kikiIDOld, userData);
				}
			}
			return map;
		}

		/**
		 *
		 * 工事旧IDをKey
		 *
		 * */
			public static Map<String, ValdacUserDataDto> getallKoujiDataListMap(
					List<ValdacUserDataDto> AllDatalist) {
				Map<String, ValdacUserDataDto> map = new HashMap<String, ValdacUserDataDto>();
				for (ValdacUserDataDto userData : AllDatalist) {
					if (StringUtil.isNotEmpty(userData.id)) {
						map.put(userData.koujiIDOld, userData);
					}
				}
				return map;
			}

}