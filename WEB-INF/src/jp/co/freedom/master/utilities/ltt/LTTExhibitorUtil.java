package jp.co.freedom.master.utilities.ltt;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.dto.ltt.LttCardDto;
import jp.co.freedom.master.dto.ltt.LttQuestionDto;
import jp.co.freedom.master.dto.ltt.LttRfidDto;
import jp.co.freedom.master.dto.ltt.LttUserDataDto;
import jp.co.freedom.master.utilities.Util;

/**
 * LTT 出展者納品データ作成
 * 
 * @author フリーダム・グループ
 * 
 */
public class LTTExhibitorUtil extends Util {

	@Override
	public boolean isPreEntry(UserDataDto userdata) {
		if (userdata instanceof LttUserDataDto) {
			LttUserDataDto info = (LttUserDataDto) userdata;
			return info.preentry;
		}
		return false;
	}

	@Override
	public boolean isAppEntry(UserDataDto userdata) {
		if (userdata instanceof LttUserDataDto) {
			LttUserDataDto info = (LttUserDataDto) userdata;
			return info.appointedday;
		}
		return false;
	}

	/**
	 * 団体登録ユーザーであるか否かの検証
	 * 
	 * @param userdata
	 *            ユーザーデータ
	 */
	public boolean isGroup(UserDataDto userdata) {
		if (userdata instanceof LttUserDataDto) {
			LttUserDataDto info = (LttUserDataDto) userdata;
			return info.group;
		}
		return false;
	}

	/* リクエストコード対応表 ILF2014用 */
	/** アイセルリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_AISERU = createRequestCodesMapForLTTAiseru();

	/** アドバンテック向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_ADOBA = createRequestCodesMapForLTTAdoba();

	/** エーゼーゴム洋行向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_IIZE = createRequestCodesMapForLTTIize();

	/** オカバマネジメン向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_OKABA = createRequestCodesMapForLTTOkaba();

	/** バルーフ向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_BRUFU = createRequestCodesMapForLTTBrufu();

	/** マキシンコー向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_MAKISI = createRequestCodesMapForLTTMakisi();

	/** ユーザックシステム向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_YUZA = createRequestCodesMapForLTTYuza();

	/** 神戸車輌製作所向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_KOBE = createRequestCodesMapForLTTKobe();

	/** 都市再生機構向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_TOSHI = createRequestCodesMapForLTTToshi();

	/** ｶｽｹｰﾄﾞ(ｼﾞｬﾊﾟﾝ)ﾘﾐﾃｯﾄ向けリクエストコード */
	public static Map<String, String> REQUESTCODES_LTT_KASUKE = createRequestCodesMapForLTTKasuke();

	/* リーダー端末対応リスト ILF2014用 */
	/** アイセル　バーコードリーダーリスト */
	public static List<String> READER_LTT_AISERU = createReaderForLTTAiseru();

	/** アドバンテック バーコードリーダーリスト */
	public static List<String> READER_LTT_ADOBA = createReaderForLTTAdoba();

	/** エーゼーゴム洋行 バーコードリーダーリスト */
	public static List<String> READER_LTT_IIZE = createReaderForLTTIize();

	/** オカバマネジメン バーコードリーダーリスト */
	public static List<String> READER_LTT_OKABA = createReaderForLTTOkaba();

	/** バルーフ バーコードリーダーリスト */
	public static List<String> READER_LTT_BRUFU = createReaderForLTTBrufu();

	/** マキシンコー バーコードリーダーリスト */
	public static List<String> READER_LTT_MAKISI = createReaderForLTTMakisi();

	/** ユーザックシステム バーコードリーダーリスト */
	public static List<String> READER_LTT_YUZA = createReaderForLTTYuza();

	/** 神戸車輌製作所 バーコードリーダーリスト */
	public static List<String> READER_LTT_KOBE = createReaderForLTTKobe();

	/** 都市再生機構 バーコードリーダーリスト */
	public static List<String> READER_LTT_TOSHI = createReaderForLTTToshi();

	/** ｶｽｹｰﾄ バーコードリーダーリスト */
	public static List<String> READER_LTT_KASUKE = createReaderForLTTKasuke();

	/**
	 * ILF2014 アイセル向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return アイセル向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTAiseru() {
		List<String> reader = new ArrayList<String>();
		reader.add("4120");
		return reader;
	}

	/**
	 * ILF2014 アドバンテック向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return アドバンテック向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTAdoba() {
		List<String> reader = new ArrayList<String>();
		reader.add("4013");
		reader.add("4123");
		return reader;
	}

	/**
	 * ILF2014 エーゼーゴム洋行向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return エーゼーゴム洋行向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTIize() {
		List<String> reader = new ArrayList<String>();
		reader.add("4051");
		return reader;
	}

	/**
	 * ILF2014 オカバマネジメント向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return オカバマネジメント向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTOkaba() {
		List<String> reader = new ArrayList<String>();
		reader.add("4142");
		return reader;
	}

	/**
	 * ILF2014 バルーフ向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return バルーフ向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTBrufu() {
		List<String> reader = new ArrayList<String>();
		reader.add("4054");
		reader.add("4109");
		return reader;
	}

	/**
	 * ILF2014 マキシンコー向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return マキシンコー向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTMakisi() {
		List<String> reader = new ArrayList<String>();
		reader.add("4006");
		return reader;
	}

	/**
	 * ILF2014 ユーザックシステム向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return ユーザックシステム向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTYuza() {
		List<String> reader = new ArrayList<String>();
		reader.add("4035");
		reader.add("4040");
		return reader;
	}

	/**
	 * ILF2014 神戸車輌製作所向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 神戸車輌製作所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTKobe() {
		List<String> reader = new ArrayList<String>();
		reader.add("4064");
		return reader;
	}

	/**
	 * ILF2014 都市再生機構向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return 都市再生機構向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTToshi() {
		List<String> reader = new ArrayList<String>();
		reader.add("4001");
		reader.add("4042");
		reader.add("4066");
		return reader;
	}

	/**
	 * ILF2014 ｶｽｹｰﾄﾞ(ｼﾞｬﾊﾟﾝ)ﾘﾐﾃｯﾄﾞ向けバーコードリーダー端末対応リストの初期化
	 * 
	 * @return ｶｽｹｰﾄﾞ(ｼﾞｬﾊﾟﾝ)ﾘﾐﾃｯﾄﾞ向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForLTTKasuke() {
		List<String> reader = new ArrayList<String>();
		reader.add("4115");
		return reader;
	}

	/**
	 * ILF2014 アイセル向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return アイセル向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTAiseru() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "マシンシャッター総合");
		requests.put("002", "シャッターバリエーション（物流展）");
		requests.put("003", "自動倉庫用シャッター");
		requests.put("004", "マシンシャッター10のメリット");
		requests.put("005", "マシンシャッター次世代の安全カバー");
		requests.put("006", "カタログ一式後送");
		requests.put("007", "精密測定器用防塵カバー");
		requests.put("008", "カタログ後送");
		requests.put("011", "コンベアー");
		requests.put("012", "台車");
		requests.put("013", "シート");
		requests.put("014", "レーザーマーカー");
		requests.put("015", "吊り下げ");
		return requests;
	}

	/**
	 * ILF2014 アドバンテック向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return アドバンテック向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTAdoba() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "（倉庫管理／構内物流／港湾業務向け）堅牢タッチパネルコンピュータ");
		requests.put("002", "（フォークリフト、産業車両向け）堅牢タッチパネルコンピュータ");
		requests.put("003", "産業用7インチタブレット");
		requests.put("004", "堅牢モバイルタブレットPC");
		requests.put("005", "モバイル車載データ端末");
		requests.put("006", "無線LANアクセスポイント");
		requests.put("007", "シリアルデバイスサーバ");
		requests.put("008", "産業用イーサネット／PoEスイッチ");
		requests.put("009", "ファンレス組込みPC");
		requests.put("010", "リモートI/Oモジュール");
		return requests;
	}

	/**
	 * ILF2014 （株）エーゼーゴム洋行向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return （株）エーゼーゴム洋行向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTIize() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "カタログ請求①全社");
		requests.put("002", "カタログ請求②IP");
		requests.put("003", "カタログ請求③バース");
		requests.put("004", "カタログ請求④宇昌社");
		requests.put("005", "アンケート回答あり");
		requests.put("006", "見積依頼あり");
		requests.put("007", "カタログ持ち帰り　全社");
		requests.put("008", "カタログ持ち帰り　IP");
		requests.put("009", "カタログ持ち帰り　バース");
		requests.put("010", "カタログ持ち帰り　宇昌社");
		requests.put("016", "柳");
		requests.put("017", "井沼田");
		requests.put("018", "西村");
		return requests;
	}

	/**
	 * ILF2014 （株）オカバマネジメント向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return （株）オカバマネジメント向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTOkaba() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "最優先");
		requests.put("002", "引越業界");
		requests.put("003", "物流業界");
		requests.put("004", "その他業界");
		requests.put("006", "パット屋.comに興味");
		requests.put("007", "ハイパットに興味");
		requests.put("008", "エコパットに興味");
		requests.put("009", "旧エコパットに興味");
		requests.put("010", "ニューパットに興味");
		requests.put("011", "エアーパットⅠに興味");
		requests.put("012", "エアーパットⅡに興味");
		requests.put("014", "資料希望");
		return requests;
	}

	/**
	 * ILF2014 バルーフ株式会社向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return バルーフ株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTBrufu() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "資料のみ");
		requests.put("002", "後日カタログ送付");
		requests.put("003", "営業マンからの連絡");
		return requests;
	}

	/**
	 * ILF2014 （株）マキシンコー向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return （株）マキシンコー向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTMakisi() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "RSロータリーストッカー");
		requests.put("002", "XPシャトル");
		requests.put("003", "減速機・ジャッキ");
		requests.put("004", "ファクトリーマネージャー");
		requests.put("005", "訪問・打合せ要");
		requests.put("006", "見積要");
		requests.put("007", "その他");
		requests.put("008", "カタログ送付");
		return requests;
	}

	/**
	 * ILF2014 ユーザックシステム向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return ユーザックシステム向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTYuza() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "検品支援名人");
		requests.put("002", "送り状名人");
		requests.put("003", "運賃管理名人");
		requests.put("004", "iPhone");
		requests.put("005", "伝発名人");
		requests.put("006", "EOS名人");
		requests.put("007", "Autoブラウザ名人");
		requests.put("008", "Autoメール名人");
		requests.put("009", "i名人");
		requests.put("010", "物流在庫名人");
		requests.put("011", "FAXお助け名人");
		requests.put("012", "1G");
		requests.put("013", "2G");
		requests.put("014", "3G");
		requests.put("015", "PB");
		requests.put("016", "ユーザー");
		requests.put("017", "A");
		requests.put("018", "B");
		requests.put("019", "C");
		requests.put("020", "Z");
		return requests;
	}

	/**
	 * ILF2014 （株）神戸車両製作所向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return （株）神戸車両製作所向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTKobe() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "お礼状");
		requests.put("002", "総合カタログ送付");
		requests.put("003", "オリジナルカタログ送付");
		requests.put("004", "総合カタログ渡し");
		requests.put("005", "オリジナルカタログ渡し");
		return requests;
	}

	/**
	 * ILF2014 都市再生機構向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return 都市再生機構向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTToshi() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "ブース対応");
		requests.put("002", "プレゼンテーションセミナー対応");
		return requests;
	}

	/**
	 * ILF2014 カスケードリミテッド向けリクエストコード<b>Map</b>の初期化
	 * 
	 * @return カスケードリミテッド向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForLTTKasuke() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("001", "フォークポジショナー");
		requests.put("002", "マルチロードハンドラー（シングルダブル）");
		requests.put("003", "ホワイトグッズクランプ（カートン）");
		requests.put("004", "iForks");
		requests.put("005", "レイヤーピッカ");
		requests.put("006", "ロールクランプ");
		requests.put("007", "ベールクランプ");
		requests.put("008", "プッシュプル");
		requests.put("009", "ローテータ");
		requests.put("010", "パレットフォーククランプ");
		requests.put("011", "ＴＦＣ");
		requests.put("012", "ＶＦＣ");
		requests.put("013", "フォーク");
		requests.put("014", "その他");
		requests.put("015", "全部");
		return requests;
	}

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<LttUserDataDto> createInstance(List<String[]> csvData) {
		List<LttUserDataDto> userData = new ArrayList<LttUserDataDto>();// ユーザーデータを保持するリスト
		LttUserDataDto dataDto = null;
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = removeLastSpaceElement(csvData.get(nIndex));
			if (row.length == 3) {// アンケートシール番号／要望コードの行
				if (dataDto == null) {
					dataDto = new LttUserDataDto();
				}
				String value = row[2];
				if (value.startsWith("A")) {// アンケートシール番号の格納
					String enquete = row[2].substring(1);
					dataDto.enqueteCode.add(enquete);
				} else if (value.startsWith("Y")) {// 要望コードの格納
					String request = row[2].substring(1);
					dataDto.requestCode.add(request);
				}
			} else if (row.length == 4) {// RFID番号の行
				// ユーザーデータのリスト格納
				if (dataDto != null) {
					// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
					userData.add(dataDto);
				}
				// RFID番号の格納
				dataDto = new LttUserDataDto();
				dataDto.filename = row[0]; // CSVファイル名
				dataDto.reader = row[1];// バーコードリーダーID
				String rfid = row[2];
				dataDto.id = rfid.substring(1);
				dataDto.timeByRfid = row[3];
			} else {
				// 読み飛ばし
				continue;
			}
		}
		// 最後尾のユーザーデータのリスト格納
		if (dataDto != null) {
			// Collections.sort(dataDto.requestCode); // リクエストコードを昇順ソート
			userData.add(dataDto);
		}
		return userData;
	}

	/**
	 * 指定配列の最後の要素を削除
	 * 
	 * @param array
	 *            対象配列
	 * @return 最後の要素を削除した配列
	 */
	private static String[] removeLastSpaceElement(String[] array) {
		assert array != null;
		String[] newArray = new String[array.length - 1];
		for (int nIndex = 0; nIndex < array.length - 1; nIndex++) {
			newArray[nIndex] = array[nIndex];
		}
		return newArray;
	}

	/**
	 * 変換表のインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return　ユーザデータのインスタンスを格納するリスト
	 */
	public static List<LttUserDataDto> createInstanceForConvertTable(
			List<String[]> csvData) {
		List<LttUserDataDto> userData = new ArrayList<LttUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			String[] row = csvData.get(nIndex);
			LttUserDataDto dataDto = new LttUserDataDto();
			if (row.length == 2) {
				String barcode = row[0];
				dataDto.id = barcode;
				dataDto.cardInfo.V_CID = row[1];
				userData.add(dataDto);
			} else {
				System.out.println("変換表データ中に2列ではない行を発見");
			}
		}
		return userData;
	}

	/**
	 * 全ての当日登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての当日登録データ
	 * @throws SQLException
	 */
	public static List<LttUserDataDto> getAllAppointedDayData(Connection conn)
			throws SQLException {
		List<LttUserDataDto> userDataList = new ArrayList<LttUserDataDto>();
		String sql = "SELECT * FROM appointedday;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			LttUserDataDto userdata = new LttUserDataDto();
			LttCardDto cardInfo = new LttCardDto(); // 名刺情報DTO
			LttQuestionDto questionInfo = new LttQuestionDto(); // アンケート情報DTO

			/* 当日入力フラグ */
			userdata.appointedday = true;

			// バーコード番号
			userdata.id = rs.getString("V_VID"); // バーコード番号
			cardInfo.V_VID = userdata.id;

			/* 名刺情報 */
			cardInfo.V_OVERSEA = rs.getString("V_OVERSEA"); // 海外住所フラグ
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_COUNTRY = rs.getString("V_Country"); // 海外住所フラグ
			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名1
			cardInfo.V_DEPT2 = rs.getString("V_DEPT2"); // 所属部署名2
			cardInfo.V_DEPT3 = rs.getString("V_DEPT3"); // 所属部署名3
			cardInfo.V_DEPT4 = rs.getString("V_DEPT4"); // 所属部署名4
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職1
			cardInfo.V_BIZ2 = rs.getString("V_BIZ2"); // 役職2
			cardInfo.V_BIZ3 = rs.getString("V_BIZ3"); // 役職3
			cardInfo.V_BIZ4 = rs.getString("V_BIZ4"); // 役職4

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 丁目番地
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス
			cardInfo.V_DAY = rs.getString("V_DAY"); // 来場日

			/* アンケート情報 */
			// Q1 業種
			questionInfo.V_Q1 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q1"));
			// Q2 職種
			questionInfo.V_Q2 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q2"));
			// Q3 役職
			questionInfo.V_Q3 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q3"));
			// Q4 関与
			questionInfo.V_Q4 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q4"));
			// Q5 来場目的(複数選択可能)
			questionInfo.V_Q5 = rs.getString("V_Q5");
			// Q6 ご案内
			questionInfo.V_Q6 = EnqueteUtil.convertSingleAnswer(rs
					.getString("V_Q6"));

			cardInfo.V_IMAGE_PATH = rs.getString("IMAGE_PATH"); // イメージパス

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
	 * 全ての事前登録データを取得
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　全ての事前登録データ
	 * @throws SQLException
	 */
	public static List<LttUserDataDto> getAllPreRegistData(Connection conn)
			throws SQLException {
		List<LttUserDataDto> userDataList = new ArrayList<LttUserDataDto>();
		String sql = "SELECT * FROM preentry;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			LttUserDataDto userdata = new LttUserDataDto();
			LttCardDto cardInfo = new LttCardDto(); // 名刺情報DTO
			LttQuestionDto questionInfo = new LttQuestionDto(); // アンケート情報DTO

			userdata.preentry = true; // 事前登録フラグ

			/* 名刺情報 */
			cardInfo.PREENTRY_ID = rs.getString("V_NO"); // 登録券番号
			userdata.id = cardInfo.PREENTRY_ID; // [備忘] MAPのkeyとして利用するために設定
			String id = rs.getString("V_ID");
			cardInfo.V_OVERSEA = id.startsWith("E") ? "1" : ""; // 海外住所フラグ
			if ("1".equals(cardInfo.V_OVERSEA)) {
				cardInfo.V_COUNTRY = rs.getString("V_ADDR1");
			}
			cardInfo.V_NAME1 = rs.getString("V_NAME1"); // 氏名姓漢字
			cardInfo.V_NAME2 = rs.getString("V_NAME2"); // 氏名名漢字
			cardInfo.V_NAMEKANA1 = rs.getString("V_NAMEKANA1"); // 氏名姓仮名
			cardInfo.V_NAMEKANA2 = rs.getString("V_NAMEKANA2"); // 氏名名仮名

			cardInfo.V_CORP = rs.getString("V_CORP"); // 会社名
			cardInfo.V_CORP_KANA = rs.getString("V_CORPKANA"); // 会社名仮名
			cardInfo.V_DEPT1 = rs.getString("V_DEPT1"); // 所属部署名
			cardInfo.V_BIZ1 = rs.getString("V_BIZ1"); // 役職

			cardInfo.V_ZIP = zipNormalize(rs.getString("V_ZIP")); // 郵便番号
			cardInfo.V_ADDR1 = rs.getString("V_ADDR1"); // 都道府県
			cardInfo.V_ADDR2 = rs.getString("V_ADDR2"); // 市区郡
			cardInfo.V_ADDR3 = rs.getString("V_ADDR3"); // 町域
			cardInfo.V_ADDR4 = rs.getString("V_ADDR4"); // 以下住所
			cardInfo.V_ADDR5 = rs.getString("V_ADDR5"); // ビル名
			cardInfo.V_TEL = rs.getString("V_TEL"); // 電話番号
			cardInfo.V_FAX = rs.getString("V_FAX"); // FAX番号
			cardInfo.V_EMAIL = rs.getString("V_EMAIL"); // メールアドレス

			/* アンケート情報 */
			questionInfo.V_Q1 = rs.getString("V_Q1");
			questionInfo.V_Q1_other = rs.getString("V_Q1_FA");
			questionInfo.V_Q2 = rs.getString("V_Q2");
			questionInfo.V_Q2_other = rs.getString("V_Q2_FA");
			questionInfo.V_Q3 = rs.getString("V_Q3");
			questionInfo.V_Q3_other = rs.getString("V_Q3_FA");
			questionInfo.V_Q4 = rs.getString("V_Q4");
			questionInfo.V_Q4_other = rs.getString("V_Q4_FA");
			questionInfo.V_Q5 = rs.getString("V_Q5");
			questionInfo.V_Q5_other = rs.getString("V_Q5_FA");
			questionInfo.V_Q6 = rs.getString("V_Q6");
			questionInfo.V_Q6_other = rs.getString("V_Q6_FA");
			questionInfo.V_Q7 = rs.getString("V_Q7");
			questionInfo.V_Q7_other = rs.getString("V_Q7_FA");
			questionInfo.V_Q8 = rs.getString("V_Q8");
			questionInfo.V_Q8_other = rs.getString("V_Q8_FA");
			questionInfo.V_Q9 = rs.getString("V_Q9");
			questionInfo.V_Q10 = rs.getString("V_Q10");
			questionInfo.V_Q10_other = rs.getString("V_Q10_FA");
			questionInfo.V_Q11 = rs.getString("V_Q11");
			questionInfo.V_Q12 = rs.getString("V_Q12");
			/* アンケート情報 */

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
	 * @param isPreMaster
	 *            事前登録マスターデータ作成フラグ
	 * @param isAppMaster
	 *            当日登録マスターデータ作成フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, Connection conn,
			String outputFileName, List<LttUserDataDto> userDataList,
			String dim, boolean isPreMaster, boolean isAppMaster,
			boolean visitFlg) throws ServletException, IOException,
			SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		List<String> header = new ArrayList<String>();
		header.add(StringUtil.enquote("ファイル名"));
		header.add(StringUtil.enquote("ID番号"));
		header.add(StringUtil.enquote("読取日時"));
		// header.add(StringUtil.enquote("読取時間"));
		header.add(StringUtil.enquote("アンマッチフラグ"));
		header.add(StringUtil.enquote("原票状況不備フラグ"));
		header.add(StringUtil.enquote("原票不備詳細"));
		header.add(StringUtil.enquote("海外住所フラグ"));
		// 名刺情報
		header.add(StringUtil.enquote("氏名"));
		header.add(StringUtil.enquote("氏名フリガナ"));
		header.add(StringUtil.enquote("勤務先名"));
		// header.add(StringUtil.enquote("会社名フリガナ(カタカナ)"));
		header.add(StringUtil.enquote("所属部署"));
		header.add(StringUtil.enquote("役職"));

		header.add(StringUtil.enquote("国名"));
		header.add(StringUtil.enquote("郵便番号"));
		header.add(StringUtil.enquote("住所1"));
		header.add(StringUtil.enquote("住所2"));
		header.add(StringUtil.enquote("住所3"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));
		header.add(StringUtil.enquote("E-mailアドレス"));

		// アンケート情報
		if (LTTConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("①業種"));
			header.add(StringUtil.enquote("②職種"));
			header.add(StringUtil.enquote("③役種"));
			header.add(StringUtil.enquote("④購買決定権限"));
			header.add(StringUtil.enquote("⑤来場目的"));
			header.add(StringUtil.enquote("⑤来場目的 その他"));
			// header.add(StringUtil.enquote("➅希望しない"));
		}

		// アンケートシール／要望コード
		header.add(StringUtil.enquote("端末番号"));

		header.add(StringUtil.enquote("アンケートシール番号1"));
		header.add(StringUtil.enquote("アンケートシール番号2"));
		for (int nIndex = 1; nIndex <= LTTConfig.REQUEST_CODE_NUM; nIndex++) {
			header.add(StringUtil.enquote("リクエストコード" + String.valueOf(nIndex)));
		}

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (LttUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			// ファイル名
			cols.add(StringUtil.enquote(userdata.filename));

			// バーコード [備忘]サブバーコードがあればそちらを出力
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.subBarcode) ? userdata.subBarcode
					: userdata.id));
			String timestamp = userdata.timeByRfid;
			// バーコード読取日
			cols.add(StringUtil.enquote(timestamp.substring(0, 8) + " "
					+ timestamp.substring(8)));
			// バーコード読取日
			// cols.add(StringUtil.enquote(timestamp.substring(0, 8)));
			//
			// // バーコード時間
			// cols.add(StringUtil.enquote(timestamp.substring(8)));

			// アンマッチフラグ
			cols.add(StringUtil.enquote(userdata.unmatch ? "1" : ""));

			// 原票状況不備
			String lackFlg = getLackFlg(userdata);
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (LTTConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}

			// 海外住所フラグ
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? "1" : ""));

			// 氏名、会社情報項目
			outputNameAndCompanyData(cols, userdata);
			// 住所項目
			outputAddressData(cols, userdata);
			// アンケート項目
			if (LTTConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				LTTExhibitorUtil util = new LTTExhibitorUtil();
				if (util.isPreEntry(userdata)) { // 事前登録データ
					outputEnqueteDataForPreentry(cols, userdata);
				} else if (util.isAppEntry(userdata)) { // 当日入力データ
					outputEnqueteDataForAppointedday(cols, userdata);
					// } else if (util.isGroup(userdata)) { // 団体登録データ
					// outputEnqueteDataForGroup(cols, userdata);
				} else {
					outputEnqueteDataForUnmatch(cols, userdata); // アンマッチデータ
				}
			}
			// バーコードデータ
			outputBarcodeData(cols, userdata);

			// 1レコード分のデータ書き出し
			FileUtil.writer(cols, writer, dim);
		}
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 当日データ照合結果をTXT形式でダウンロード
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
	 * @param isPreMaster
	 *            事前登録マスターデータ作成フラグ
	 * @param isAppMaster
	 *            当日登録マスターデータ作成フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */

	public static boolean downLoadForApp(HttpServletRequest request,
			HttpServletResponse response, Connection conn,
			String outputFileName, List<LttUserDataDto> userDataList,
			String dim, boolean isPreMaster, boolean isAppMaster,
			boolean visitFlg) throws ServletException, IOException,
			SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		List<String> header = new ArrayList<String>();

		header.add(StringUtil.enquote("来場日"));
		header.add(StringUtil.enquote("事前登録番号"));
		header.add(StringUtil.enquote("日本語サイト"));
		header.add(StringUtil.enquote("英語サイト"));
		header.add(StringUtil.enquote("原票状況不備フラグ"));
		header.add(StringUtil.enquote("原票不備詳細"));
		header.add(StringUtil.enquote("海外住所フラグ"));
		// 名刺情報
		header.add(StringUtil.enquote("名前:姓"));
		header.add(StringUtil.enquote("名前:名"));
		header.add(StringUtil.enquote("名前:姓フリガナ"));
		header.add(StringUtil.enquote("名前:名フリガナ"));
		header.add(StringUtil.enquote("勤務先名"));
		header.add(StringUtil.enquote("会社名フリガナ(カタカナ)"));
		header.add(StringUtil.enquote("所属部署"));
		header.add(StringUtil.enquote("役職"));

		header.add(StringUtil.enquote("国名"));
		header.add(StringUtil.enquote("郵便番号1"));
		header.add(StringUtil.enquote("郵便番号2"));
		header.add(StringUtil.enquote("都道府県"));
		header.add(StringUtil.enquote("市区郡"));
		header.add(StringUtil.enquote("町村番地"));
		header.add(StringUtil.enquote("ビル・マンション名"));
		header.add(StringUtil.enquote("電話番号"));
		header.add(StringUtil.enquote("FAX番号"));
		header.add(StringUtil.enquote("E-mailアドレス"));

		// アンケート情報
		if (LTTConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
			header.add(StringUtil.enquote("1業種"));
			header.add(StringUtil.enquote("2職種"));
			header.add(StringUtil.enquote("3役種"));
			header.add(StringUtil.enquote("4従業員"));
			header.add(StringUtil.enquote("5購買決定権限"));	
			header.add(StringUtil.enquote("6来場目的"));
			header.add(StringUtil.enquote("6来場目的 その他"));
			header.add(StringUtil.enquote("7興味のある製品"));
			header.add(StringUtil.enquote("8見たい理由"));
			header.add(StringUtil.enquote("9課題"));
			header.add(StringUtil.enquote("10その他・物流・ロジスティクス"));
			header.add(StringUtil.enquote("11次回ご出展"));
			header.add(StringUtil.enquote("12希望しない"));
		}
		header.add(StringUtil.enquote("画像パス"));

		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (LttUserDataDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();

			// 来場日
			if (userdata.visitFlgs != null) {
				cols.add(StringUtil.enquote("1"));
			} else {
				cols.add(StringUtil.enquote(""));
			}

			// バーコード [備忘]サブバーコードがあればそちらを出力
			LTTExhibitorUtil util_1 = new LTTExhibitorUtil();
			if (util_1.isPreEntry(userdata)) { // 事前登録データ
				cols.add(StringUtil.enquote(userdata.id));
			} else { // 当日入力データ
				cols.add(StringUtil.enquote(userdata.cardInfo.V_VID));
			}

			// サイト種別
			if (userdata.id.startsWith("1011")) {
				cols.add(StringUtil.enquote("1"));
				cols.add(StringUtil.enquote(""));
			} else {
				cols.add(StringUtil.enquote(""));
				cols.add(StringUtil.enquote("1"));
			}

			// 原票状況不備
			String lackFlg = getLackFlg(userdata);
			cols.add(StringUtil.isNotEmpty(lackFlg) ? StringUtil
					.enquote(lackFlg) : StringUtil.enquote(""));
			// 不備詳細情報
			if (LTTConfig.OUTPUT_VALIDATION_ERROR_RESULT) {
				cols.add(StringUtil.isNotEmpty(userdata.validationErrResult) ? StringUtil
						.enquote(userdata.validationErrResult) : StringUtil
						.enquote(""));
			}

			// 海外住所フラグ
			cols.add(StringUtil.enquote(StringUtil
					.isNotEmpty(userdata.cardInfo.V_OVERSEA) ? "1" : ""));

			// 氏名、会社情報項目
			outputNameAndCompanyDataForMaster(cols, userdata);
			// 住所項目
			outputAddressDataForMaster(cols, userdata);
			// アンケート項目
			if (LTTConfig.OUTPUT_ENQUETE_RESULTS_FLG) {
				LTTExhibitorUtil util = new LTTExhibitorUtil();
				if (util.isPreEntry(userdata)) { // 事前登録データ
					outputEnqueteDataForPreentryMaster(cols, userdata);
				} else if (util.isAppEntry(userdata)) { // 当日入力データ
					outputEnqueteDataForAppointedday(cols, userdata);
				} else {
					outputEnqueteDataForUnmatch(cols, userdata); // アンマッチデータ
				}
			}
			cols.add(StringUtil.enquote(userdata.cardInfo.V_IMAGE_PATH));
			// 1レコード分のデータ書き出し
			FileUtil.writer(cols, writer, dim);
		}
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 【事前登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForPreentry(List<String> cols,
			LttUserDataDto userdata) {

		LttQuestionDto questionDto = (LttQuestionDto) userdata.questionInfo;

		// ①業種
		cols.add(StringUtil.enquote(questionDto.V_Q1));
		// ②職種
		cols.add(StringUtil.enquote(questionDto.V_Q2));
		// ③役職
		cols.add(StringUtil.enquote(questionDto.V_Q3));
		// ④
		cols.add(StringUtil.enquote(questionDto.V_Q5));
		// ⑤来場目的
		cols.add(StringUtil.enquote(questionDto.V_Q6));
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(questionDto.V_Q6_other));
		// ➅希望しない
		String q6 = questionDto.V_Q12;
		if (StringUtil.isNotEmpty(q6) && "01".equals(q6)) {
			cols.add(StringUtil.enquote(questionDto.V_Q12));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		return cols;
	}

	/**
	 * 【事前登録】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForPreentryMaster(
			List<String> cols, LttUserDataDto userdata) {

		LttQuestionDto questionDto = (LttQuestionDto) userdata.questionInfo;

		// ①業種
		cols.add(StringUtil.enquote(questionDto.V_Q1));
		// ②職種
		cols.add(StringUtil.enquote(questionDto.V_Q2));
		// ③役職
		cols.add(StringUtil.enquote(questionDto.V_Q3));
		// ④従業員
		cols.add(StringUtil.enquote(questionDto.V_Q4));
		// ⑤関与
		cols.add(StringUtil.enquote(questionDto.V_Q5));
		// ➅来場目的
		cols.add(StringUtil.enquote(questionDto.V_Q6));
		// ➅来場目的 その他
		cols.add(StringUtil.enquote(questionDto.V_Q6_other));
		// ➅興味
		cols.add(StringUtil.enquote(questionDto.V_Q7));
		// ➅興味 その他
		cols.add(StringUtil.enquote(questionDto.V_Q8_other));
		// 課題
		cols.add(StringUtil.enquote(questionDto.V_Q9));
		// その他・物流・ロジスティクス
		cols.add(StringUtil.enquote(questionDto.V_Q10_other));
		// 次回ご出展
		cols.add(StringUtil.enquote(questionDto.V_Q11));		
		// 希望しない
		String q6 = questionDto.V_Q12;
		if (StringUtil.isNotEmpty(q6) && "01".equals(q6)) {
			cols.add(StringUtil.enquote(questionDto.V_Q12));
		} else {
			cols.add(StringUtil.enquote(""));
		}
		return cols;
	}

	/**
	 * 【当日入力】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForAppointedday(
			List<String> cols, LttUserDataDto userdata) {
		LttQuestionDto questionDto = (LttQuestionDto) userdata.questionInfo;

		// ①業種
		String q1 = questionDto.V_Q1;
		if (StringUtil.isNotEmpty(q1)) {
			q1 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q1);
			if (StringUtil.isNotEmpty(q1) && q1.length() == 1) {
				q1 = "0" + q1;
			}
		}
		cols.add(StringUtil.enquote(q1));
		// ②職種
		String q2 = questionDto.V_Q2;
		if (StringUtil.isNotEmpty(q2)) {
			q2 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q2);
			if (StringUtil.isNotEmpty(q2) && q2.length() == 1) {
				q2 = "0" + q2;
			}
		}
		cols.add(StringUtil.enquote(q2));
		// ③役職
		String q3 = questionDto.V_Q3;
		if (StringUtil.isNotEmpty(q3)) {
			q3 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q3);
			if (StringUtil.isNotEmpty(q3) && q3.length() == 1) {
				q3 = "0" + q3;
			}
		}
		cols.add(StringUtil.enquote(q3));
		// ④
		String q4 = questionDto.V_Q4;
		if (StringUtil.isNotEmpty(q4)) {
			q4 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q4);
			if (StringUtil.isNotEmpty(q4) && q4.length() == 1) {
				q4 = "0" + q4;
			}
		}
		cols.add(StringUtil.enquote(q4));
		// ⑤来場目的
		if (StringUtil.isNotEmpty(questionDto.V_Q5)) {
			String q5Buff[] = questionDto.V_Q5.split(" ");
			List<String> q5ValueList = new LinkedList<String>();
			for (String value : q5Buff) {
				if (StringUtil.isNotEmpty(value) && value.length() == 1) {
					value = "0" + value;
				}
				q5ValueList.add(value);
			}
			cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(",",
					q5ValueList)));
		} else {
			cols.add(StringUtil.enquote(""));
		}

		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(""));
		// ➅希望しない
		// String q6 = questionDto.V_Q6;
		// if (StringUtil.isNotEmpty(q6)) {
		// q6 = EnqueteUtil.convertSingleAnswer(questionDto.V_Q6);
		// if (StringUtil.isNotEmpty(q6) && q6.length() == 1) {
		// q6 = "0" + q6;
		// }
		// }
		// cols.add(StringUtil.enquote(q6));
		return cols;
	}

	/**
	 * 【アンマッチ】アンケート項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputEnqueteDataForUnmatch(List<String> cols,
			LttUserDataDto userdata) {

		// ①業種
		cols.add(StringUtil.enquote(""));
		// ②職種
		cols.add(StringUtil.enquote(""));
		// ③役職
		cols.add(StringUtil.enquote(""));
		// ④年齢
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的
		cols.add(StringUtil.enquote(""));
		// ⑤来場目的 その他
		cols.add(StringUtil.enquote(""));
		// ➅希望しない
		// cols.add(StringUtil.enquote(""));
		return cols;
	}

	/**
	 * バーコードデータの出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return バーコードデータを出力後の出力バッファ
	 */
	private static List<String> outputBarcodeData(List<String> cols,
			LttUserDataDto userdata) {

		// バーコードリーダー番号
		cols.add(StringUtil.enquote(userdata.reader));
		// String timestamp = userdata.timeByRfid;
		// // バーコード読取日
		// cols.add(StringUtil.enquote(timestamp.substring(0, 8)));
		// // バーコード時間
		// cols.add(StringUtil.enquote(timestamp.substring(8)));
		// アンケートシール番号
		if (userdata.enqueteCode.size() == 0) {
			cols.add(StringUtil.enquote(""));
			cols.add(StringUtil.enquote(""));
		} else if (userdata.enqueteCode.size() == 1) {
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(0)));
			cols.add(StringUtil.enquote(""));
		} else if (userdata.enqueteCode.size() == 2) {
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(0)));
			cols.add(StringUtil.enquote(userdata.enqueteCode.get(1)));
		} else {
			System.out.println("アンケートシールが2個以上存在" + userdata.id);
		}
		// 要望コード
		// List<String> requestCodes = userdata.requestCode;
		// for (String code : requestCodes) {
		// cols.add(StringUtil.enquote(code));
		// }
		// if (LTTConfig.REQUEST_CODE_NUM < requestCodes.size()) {
		// System.out.println("リクエストコード数が上限に達しました");
		// } else {
		// for (int nIndex = 0; nIndex < LTTConfig.REQUEST_CODE_NUM
		// - requestCodes.size(); nIndex++) {
		// cols.add(StringUtil.enquote(""));
		// }
		// }
		// リクエストコード
		List<String> requestCodes = userdata.requestCode;
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 0; nIndex < requestCodes.size(); nIndex++) {
			sb.append(getRequestValue(userdata.reader, requestCodes.get(nIndex)));
			if (nIndex != requestCodes.size() - 1) {
				sb.append(",");
			}
		}
		cols.add(StringUtil.isNotEmpty(sb.toString()) ? StringUtil.enquote(sb
				.toString()) : StringUtil.enquote(""));

		return cols;
	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressData(List<String> cols,
			LttUserDataDto userdata) {

		if (isOversea(userdata)) {
			cols.add(StringUtil.enquote(userdata.cardInfo.V_COUNTRY));
			// 郵便番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			String addr = StringUtil.concat(userdata.cardInfo.V_ADDR5,
					userdata.cardInfo.V_ADDR4, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR1);
			// 都道府県
			cols.add(StringUtil.enquote(""));
			// 住所1
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote(userdata.cardInfo.V_COUNTRY));
			// 郵便番号
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			// 都道府県
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
			// 住所1
			String addr = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4);
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR5));
		}
		// 電話番号
		String tel = userdata.cardInfo.V_TEL;
		tel = StringUtil.replace(tel, "tel:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(tel)
				&& !StringUtil.containWildcard(tel) ? tel : ""));
		// FAX番号
		String fax = userdata.cardInfo.V_FAX;
		fax = StringUtil.replace(fax, "fax:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(fax)
				&& !StringUtil.containWildcard(fax) ? fax : ""));
		// E-mail
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.isEmailAddress(userdata.cardInfo.V_EMAIL)) { // E-mailに対する妥当性検証
			System.out.println("E-mailアドレスの構文エラー:" + userdata.id
					+ userdata.cardInfo.V_EMAIL);
		}
		cols.add(StringUtil.enquote(StringUtil
				.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? userdata.cardInfo.V_EMAIL
				: ""));
		return cols;

	}

	/**
	 * 住所項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputAddressDataForMaster(List<String> cols,
			LttUserDataDto userdata) {

		if (isOversea(userdata)) {
			cols.add(StringUtil.enquote(userdata.cardInfo.V_COUNTRY));
			// 郵便番号
			String zip = userdata.cardInfo.V_ZIP;
			zip = StringUtil.replace(zip, "-", "");

			cols.add(StringUtil.enquote(StringUtil.isNotEmpty(zip)
					&& !StringUtil.containWildcard(zip) ? zip : ""));
			cols.add(StringUtil.enquote(""));
			String addr = StringUtil.concat(userdata.cardInfo.V_ADDR5,
					userdata.cardInfo.V_ADDR4, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR1);
			// 都道府県
			cols.add(StringUtil.enquote(""));
			// 住所1
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(""));
			// 住所3
			cols.add(StringUtil.enquote(""));
		} else {
			cols.add(StringUtil.enquote("Japan"));
			// 郵便番号
			// cols.add(StringUtil.enquote(userdata.cardInfo.V_ZIP));
			String zip = userdata.cardInfo.V_ZIP;
			zip = StringUtil.replace(zip, "-", "");
			cols.add(StringUtil.enquote(zip.substring(0, 3)));
			cols.add(StringUtil.enquote(zip.substring(3)));

			// 都道府県
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR1));
			// 都道府県
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR2));
			// 住所1
			String addr = StringUtil.concatWithDelimit("",
					userdata.cardInfo.V_ADDR3, userdata.cardInfo.V_ADDR4);
			cols.add(StringUtil.enquote(addr));
			// 住所2
			cols.add(StringUtil.enquote(userdata.cardInfo.V_ADDR5));
		}
		// 電話番号
		String tel = userdata.cardInfo.V_TEL;
		tel = StringUtil.replace(tel, "tel:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(tel)
				&& !StringUtil.containWildcard(tel) ? tel : ""));
		// FAX番号
		String fax = userdata.cardInfo.V_FAX;
		fax = StringUtil.replace(fax, "fax:", "");
		cols.add(StringUtil.enquote(StringUtil.isNotEmpty(fax)
				&& !StringUtil.containWildcard(fax) ? fax : ""));
		// E-mail
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.isEmailAddress(userdata.cardInfo.V_EMAIL)) { // E-mailに対する妥当性検証
			System.out.println("E-mailアドレスの構文エラー:" + userdata.id
					+ userdata.cardInfo.V_EMAIL);
		}
		cols.add(StringUtil.enquote(StringUtil
				.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.containWildcard(userdata.cardInfo.V_EMAIL) ? userdata.cardInfo.V_EMAIL
				: ""));
		return cols;

	}

	/**
	 * アンマッチバーコード番号リストをTXT形式でダウンロード
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
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForUnmatch(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<LttUserDataDto> userDataList, String dim)
			throws ServletException, IOException, SQLException {

		// CSVの列数の決定
		int columnNum = 3;

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		String[] header = new String[columnNum];

		int headerNum = -1;
		header[++headerNum] = StringUtil.enquote("バーコード番号");
		header[++headerNum] = StringUtil.enquote("リーダー番号");
		header[++headerNum] = StringUtil.enquote("来場日");
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (LttUserDataDto userdata : userDataList) {

			String[] cols = new String[columnNum];
			int nColumn = -1;

			// バーコード番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.id) ? StringUtil
					.enquote(userdata.id) : StringUtil.enquote("");
			// リーダー番号
			cols[++nColumn] = StringUtil.isNotEmpty(userdata.reader) ? StringUtil
					.enquote(userdata.reader) : StringUtil.enquote("");
			// 来場日
			String time = normalizedDateStrForExhibitorTimestamp(userdata.timeByRfid);
			cols[++nColumn] = StringUtil.isNotEmpty(time) ? StringUtil
					.enquote(time) : StringUtil.enquote("");

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * ハイフンなし7桁郵便番号であるか否かの検証
	 * 
	 * @param zip
	 *            郵便番号
	 * @return　検証結果のブール値
	 */
	private static boolean is7Zip(String zip) {
		if (StringUtil.isNotEmpty(zip)) {
			boolean digitCheck = zip.length() == 7;
			boolean isValid = StringUtil.isNumeric(zip);
			return digitCheck && isValid;
		}
		return false;
	}

	/**
	 * 郵便番号の正規化
	 * 
	 * @param zip
	 *            郵便番号
	 * @return　正規化後の郵便番号
	 */
	private static String zipNormalize(String zip) {
		if (is7Zip(zip)) {
			return zip.substring(0, 3) + "-" + zip.substring(3);
		}
		return zip;
	}

	/**
	 * リクエストコードに対応する値を取得
	 * 
	 * @param reader
	 *            バーコードリーダーID
	 * @param code
	 *            リクエストコード
	 * @return　リクエストコードに対応する値
	 */
	public static String getRequestValue(String reader, String code) {
		assert StringUtil.isNotEmpty(reader);
		String value = null;
		if (StringUtil.isNotEmpty(code)) {
			if (READER_LTT_AISERU.contains(reader)) {
				value = REQUESTCODES_LTT_AISERU.get(code);
			} else if (READER_LTT_ADOBA.contains(reader)) {
				value = REQUESTCODES_LTT_ADOBA.get(code);
			} else if (READER_LTT_IIZE.contains(reader)) {
				value = REQUESTCODES_LTT_IIZE.get(code);
			} else if (READER_LTT_OKABA.contains(reader)) {
				value = REQUESTCODES_LTT_OKABA.get(code);
			} else if (READER_LTT_BRUFU.contains(reader)) {
				value = REQUESTCODES_LTT_BRUFU.get(code);
			} else if (READER_LTT_MAKISI.contains(reader)) {
				value = REQUESTCODES_LTT_MAKISI.get(code);
			} else if (READER_LTT_YUZA.contains(reader)) {
				value = REQUESTCODES_LTT_YUZA.get(code);
			} else if (READER_LTT_KOBE.contains(reader)) {
				value = REQUESTCODES_LTT_KOBE.get(code);
			} else if (READER_LTT_TOSHI.contains(reader)) {
				value = REQUESTCODES_LTT_TOSHI.get(code);
			} else if (READER_LTT_KASUKE.contains(reader)) {
				value = REQUESTCODES_LTT_KASUKE.get(code);
			} else {
				value = code; // 対応表が存在しない場合はリクエストコードをそのまま返却
			}
			if (StringUtil.isEmpty(value)) {
				value = code; // 対応表にリクエストコードが掲載されていない場合はリクエストコードをそのまま返却
			}
		}
		return value;
	}

	/**
	 * 【バーコードマッチング高速化対応】当日登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>LttUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, LttUserDataDto> getAppointedDayMap(
			List<LttUserDataDto> userDataList) {
		Map<String, LttUserDataDto> map = new HashMap<String, LttUserDataDto>();
		for (LttUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * 【バーコードマッチング高速化対応】事前登録用：DBアクセスに依らずにユーザー情報を検索するためにMAPを生成する
	 * 
	 * @param userDataList
	 *            <b>LttUserDataDto</b>
	 * @return ユーザー情報を格納するMAP
	 */
	public static Map<String, LttUserDataDto> getPreEntryMap(
			List<LttUserDataDto> userDataList) {
		Map<String, LttUserDataDto> map = new HashMap<String, LttUserDataDto>();
		for (LttUserDataDto userData : userDataList) {
			if (StringUtil.isNotEmpty(userData.id)) {
				map.put(userData.id, userData);
			}
		}
		return map;
	}

	/**
	 * バーコードデータより来場日を特定する
	 * 
	 * @param timestamp
	 *            バーコードデータ中のタイムスタンプ
	 * @return 来場日
	 */
	public static String normalizedDateStrForExhibitorTimestamp(String timestamp) {
		if (StringUtil.isNotEmpty(timestamp) && timestamp.length() == 8) {
			StringBuffer sb = new StringBuffer();
			sb.append(timestamp.substring(4, 8));
			return sb.toString();
		}
		return null;
	}

	/**
	 * 画像パスより来場日を特定する
	 * 
	 * @param timestamp
	 *            バーコードデータ中のタイムスタンプ
	 * @return 来場日
	 */
	public static String normalizedDateStrForExhibitorImagePath(String imagepath) {
		if (StringUtil.isNotEmpty(imagepath)) {
			if (imagepath.contains("0909")) {
				return "0909";
			} else if (imagepath.contains("0910")) {
				return "0910";
			} else if (imagepath.contains("0911")) {
				return "0911";
			} else if (imagepath.contains("0912")) {
				return "0912";
			} else {
				return null;
			}
		}
		return null;
	}

	// /**
	// * 【事前登録】アンケート項目の出力
	// *
	// * @param cols
	// * 出力バッファ
	// * @param userdata
	// * <b>LttUserDataDto</b>
	// * @return アンケート項目を出力後の出力バッファ
	// */
	// private static List<String> outputExhibitorEnqueteDataForJPPreentry(
	// List<String> cols, LttUserDataDto userdata) {
	// // 業種
	// cols.add(StringUtil.enquote(LTTConstants.BIZ_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q1)));
	// // 役職
	// cols.add(StringUtil.enquote(LTTConstants.DEPT_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q2)));
	// // 職種
	// cols.add(StringUtil.enquote(LTTConstants.SYKUSYU_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q3)));
	// // 関与
	// cols.add(StringUtil.enquote(LTTConstants.KANYO_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q4)));
	// // 来場目的
	// cols.add(StringUtil.enquote(LTTConstants.PURPOSE_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q5)));
	// // ご案内
	// cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));
	// return cols;
	// }
	//
	// /**
	// * 【当日登録】アンケート項目の出力
	// *
	// * @param cols
	// * 出力バッファ
	// * @param userdata
	// * <b>LttUserDataDto</b>
	// * @param slxMaster
	// * SLXコードマスター
	// * @return アンケート項目を出力後の出力バッファ
	// */
	// private static List<String> outExhibitorputEnqueteDataForJPAppointedday(
	// List<String> cols, LttUserDataDto userdata) {
	//
	// // 業種
	// cols.add(StringUtil.enquote(LTTConstants.BIZ_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q1)));
	// // 役職
	// cols.add(StringUtil.enquote(LTTConstants.DEPT_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q2)));
	// // 職種
	// cols.add(StringUtil.enquote(LTTConstants.SYKUSYU_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q3)));
	// // 関与
	// cols.add(StringUtil.enquote(LTTConstants.KANYO_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q4)));
	// // 来場目的
	// cols.add(StringUtil.enquote(LTTConstants.PURPOSE_CATEGORIES
	// .get(((LttQuestionDto) userdata.questionInfo).V_Q5)));
	// // ご案内
	// cols.add(StringUtil.enquote(userdata.questionInfo.V_Q6));
	// return cols;
	// }

	/**
	 * 不備フラグの特定
	 * 
	 * @param userdata
	 *            　<b>LttUserDataDto</b>
	 * @return 不備フラグ
	 */
	private static String getLackFlg(LttUserDataDto userdata) {
		LTTValidator validator = new LTTValidator("■");
		validator.validate(userdata, "1"); // ワイルドカード(■)の存在チェック
		validator.validate(userdata, "2"); // 氏名、連絡先情報に対する妥当性検証
		boolean lack1Flg = !validator.getResult1(); // 不備フラグ1
		boolean lack2Flg = !validator.getResult2(); // 不備フラグ2
		userdata.validationErrResult = validator.getResultDetail(); // タイプ2に対する妥当性検証違反の詳細情報を格納
		if (lack1Flg && !lack2Flg) {
			return "1";
		} else if (lack2Flg) {
			return "2";
		} else {
			return "";
		}

	}

	/**
	 * 海外住所フラグの検証
	 * 
	 * @param cardInfo
	 *            <b>JecaCardDto</b>
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(LttUserDataDto userdata) {
		LTTExhibitorUtil util = new LTTExhibitorUtil();
		boolean result;
		if (util.isPreEntry(userdata)) { // 事前登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else if (util.isAppEntry(userdata)) { // 当日登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else if (util.isGroup(userdata)) { // 団体登録データである場合
			result = "1".equals(userdata.cardInfo.V_OVERSEA);
		} else {
			// System.out.println("海外住所フラグの特定に失敗しました:" + userdata.id);
			result = false;
		}
		return result;
	}

	/**
	 * 指定IDが事前登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isPreEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(LTTConfig.PREENTRY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDが当日入力ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isAppEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(LTTConfig.APPOINTEDDAY_BARCODE_START_BIT);
	}

	/**
	 * 指定IDが団体登録ユーザーであるか否かの検証
	 * 
	 * @param id
	 *            バーコード番号
	 * @return 検証結果のブール値
	 */
	public static boolean isGroupEntry(String id) {
		assert StringUtil.isNotEmpty(id);
		return id.startsWith(LTTConfig.GROUP_BARCODE_START_BIT);
	}

	/**
	 * ホール入り口のバーコードデータ
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, LttRfidDto> getAllRfidMap(Connection conn)
			throws SQLException {

		Map<String, LttRfidDto> allRfidData = new HashMap<String, LttRfidDto>();
		String sql = "SELECT * FROM rfid;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery(sql);
		while (rs.next()) {
			String rfid = rs.getString("rfid_no"); // RFID番号
			LttRfidDto rfidDto = new LttRfidDto();
			int nIndex = -1;
			for (String day : LTTConfig.DAYS) {
				rfidDto.visitFlgs[++nIndex] = 0 != rs.getInt(day + "_cnt");
			}
			allRfidData.put(rfid, rfidDto);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return allRfidData;
	}

	/**
	 * 氏名および住所情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyData(List<String> cols,
			LttUserDataDto userdata) {
		/*
		 * 氏名情報の間のデリミタの特定
		 */
		String delimit = "";
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_NAME1)
				&& StringUtil.isNotEmpty(userdata.cardInfo.V_NAME2)) {
			if (StringUtil.isHalfWidthString(userdata.cardInfo.V_NAME1)
					&& StringUtil.isHalfWidthString(userdata.cardInfo.V_NAME2)) {
				delimit = " ";
			} else {
				delimit = "　";
			}
		}
		// 氏名
		cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(delimit,
				userdata.cardInfo.V_NAME1, userdata.cardInfo.V_NAME2)));
		// 氏名フリガナ(カタカナ)
		cols.add(StringUtil.enquote(StringUtil.concatWithDelimit(delimit,
				userdata.cardInfo.V_NAMEKANA1, userdata.cardInfo.V_NAMEKANA2)));

		// 会社名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));
		// // 会社名フリガナ(カタカナ)
		// LTTExhibitorUtil util = new LTTExhibitorUtil();
		// cols.add(StringUtil.enquote(util.isPreEntry(userdata) ?
		// userdata.cardInfo.V_CORP_KANA
		// : ""));

		// 所属部署名
		String dept = StringUtil.concat(userdata.cardInfo.V_DEPT1,
				userdata.cardInfo.V_DEPT2, userdata.cardInfo.V_DEPT3,
				userdata.cardInfo.V_DEPT4);
		cols.add(StringUtil.enquote(dept));
		// 役職
		String biz = StringUtil.concat(userdata.cardInfo.V_BIZ1,
				userdata.cardInfo.V_BIZ2, userdata.cardInfo.V_BIZ3,
				userdata.cardInfo.V_BIZ4);
		cols.add(StringUtil.enquote(biz));
		return cols;
	}

	/**
	 * 氏名および住所情報項目の出力
	 * 
	 * @param cols
	 *            出力バッファ
	 * @param userdata
	 *            <b>JecaUserDataDto</b>
	 * @return アンケート項目を出力後の出力バッファ
	 */
	private static List<String> outputNameAndCompanyDataForMaster(
			List<String> cols, LttUserDataDto userdata) {
		// 氏名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME1));
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAME2));
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA1));
		cols.add(StringUtil.enquote(userdata.cardInfo.V_NAMEKANA2));

		// 会社名
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP));
		cols.add(StringUtil.enquote(userdata.cardInfo.V_CORP_KANA));

		// 所属部署名
		String dept = StringUtil.concat(userdata.cardInfo.V_DEPT1,
				userdata.cardInfo.V_DEPT2, userdata.cardInfo.V_DEPT3,
				userdata.cardInfo.V_DEPT4);
		cols.add(StringUtil.enquote(dept));
		// 役職
		String biz = StringUtil.concat(userdata.cardInfo.V_BIZ1,
				userdata.cardInfo.V_BIZ2, userdata.cardInfo.V_BIZ3,
				userdata.cardInfo.V_BIZ4);
		cols.add(StringUtil.enquote(biz));
		return cols;
	}

}