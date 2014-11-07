package jp.co.freedom.common.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.dto.zip.ZipDto;

/**
 * 共通サービス「7桁郵便番号から都道府県を特定するサービス」向けユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ZipUtil {

	/** 都道府県コード */
	public static Map<Integer, String> ADDR1_MAP = createAddr1Map();

	/**
	 * 共通データベースのmeishiテーブルから変換対象である郵便番号の一覧を取得する
	 * 
	 * @param conn
	 *            共通データベースサーバーへの接続情報
	 * @param userData
	 *            ユーザーデータ
	 * @return　マッチングする事前登録データ
	 * @throws SQLException
	 */
	public static List<ZipDto> getData(Connection conn) throws SQLException {
		List<ZipDto> zipList = new ArrayList<ZipDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM meishi ORDER BY CAST(V_ID AS UNSIGNED);";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ZipDto cardInfo = new ZipDto();
			cardInfo.id = rs.getString("V_ID");
			String zip = rs.getString("V_ZIP");
			if (StringUtil.isNotEmpty(zip)) {
				zip = StringUtil.replace(zip, "-", "");
			}
			cardInfo.zip = zip;
			zipList.add(cardInfo);
		}
		rs.close();
		stmt.close();
		return zipList;
	}

	/**
	 * 郵便番号から都道府県を特定
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param zip
	 *            郵便番号
	 * @return 都道府県
	 * @throws SQLException
	 */
	public static String getAddr1(Connection conn, String zip)
			throws SQLException {
		String addr1 = null;
		if (StringUtil.isNotEmpty(zip)) {
			Statement stmt = conn.createStatement();
			String sql = "SELECT ADDR_KAJI1 FROM zip7 where ZIP7 = '" + zip
					+ "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				addr1 = rs.getString("ADDR_KAJI1");
			}
			rs.close();
			stmt.close();
		}
		return addr1;
	}

	/**
	 * 郵便番号から住所を特定
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @throws SQLException
	 */
	public static Map<String, ZipDto> getAddrAll(Connection conn)
			throws SQLException {
		Map<String, ZipDto> map = new HashMap<String, ZipDto>();
		String sql = "SELECT * from zip7;";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ZipDto info = new ZipDto();
			info.zip = rs.getString("ZIP7");
			info.addr1 = rs.getString("ADDR_KAJI1");
			info.addr2 = rs.getString("ADDR_KAJI2");
			info.addr3 = rs.getString("ADDR_KAJI3");
			map.put(info.zip, info);
		}
		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		return map;
	}

	/**
	 * 　共通データベースのmeishiテーブルの都道府県(V_ADDR)を更新する
	 * 
	 * @param conn
	 *            　DBサーバーへの接続情報
	 * @param cardInfo
	 *            <b>ZipDto</b>
	 * @return DB情報の更新処理の成否のブール値
	 * @throws SQLException
	 */
	public static boolean updateAddr1(Connection conn, ZipDto cardInfo)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "UPDATE meishi SET V_ADDR = '" + cardInfo.addr1
				+ "' WHERE V_ID = '" + cardInfo.id + "';";
		int result = stmt.executeUpdate(sql);
		return result > 0;
	}

	/**
	 * 都道府県コード<b>Map</b>の初期化
	 * 
	 * @return 都道府県コード<b>Map</b>
	 */
	private static Map<Integer, String> createAddr1Map() {
		Map<Integer, String> addr1 = new HashMap<Integer, String>();
		// addr1.put(0, "海外");
		addr1.put(1, "北海道");
		addr1.put(2, "青森県");
		addr1.put(3, "岩手県");
		addr1.put(4, "宮城県");
		addr1.put(5, "秋田県");
		addr1.put(6, "山形県");
		addr1.put(7, "福島県");
		addr1.put(8, "茨城県");
		addr1.put(9, "栃木県");
		addr1.put(10, "群馬県");
		addr1.put(11, "埼玉県");
		addr1.put(12, "千葉県");
		addr1.put(13, "東京都");
		addr1.put(14, "神奈川県");
		addr1.put(15, "新潟県");
		addr1.put(16, "富山県");
		addr1.put(17, "石川県");
		addr1.put(18, "福井県");
		addr1.put(19, "山梨県");
		addr1.put(20, "長野県");
		addr1.put(21, "岐阜県");
		addr1.put(22, "静岡県");
		addr1.put(23, "愛知県");
		addr1.put(24, "三重県");
		addr1.put(25, "滋賀県");
		addr1.put(26, "京都府");
		addr1.put(27, "大阪府");
		addr1.put(28, "兵庫県");
		addr1.put(29, "奈良県");
		addr1.put(30, "和歌山県");
		addr1.put(31, "鳥取県");
		addr1.put(32, "島根県");
		addr1.put(33, "岡山県");
		addr1.put(34, "広島県");
		addr1.put(35, "山口県");
		addr1.put(36, "徳島県");
		addr1.put(37, "香川県");
		addr1.put(38, "愛媛県");
		addr1.put(39, "高知県");
		addr1.put(40, "福岡県");
		addr1.put(41, "佐賀県");
		addr1.put(42, "長崎県");
		addr1.put(43, "熊本県");
		addr1.put(44, "大分県");
		addr1.put(45, "宮崎県");
		addr1.put(46, "鹿児島県");
		addr1.put(47, "沖縄県");
		return addr1;
	}

	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<ZipDto> userDataList, String dim) throws ServletException,
			IOException, SQLException {

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
		// String[] header = new String[columnNum];
		List<String> header = new ArrayList<String>();

		header.add(StringUtil.enquote("ID"));
		header.add(StringUtil.enquote("ZIP"));
		header.add(StringUtil.enquote("ADDR1"));
		header.add(StringUtil.enquote("ADDR2"));
		header.add(StringUtil.enquote("ADDR3"));
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (ZipDto userdata : userDataList) {

			List<String> cols = new ArrayList<String>();
			cols.add(StringUtil.enquote(userdata.id));
			cols.add(StringUtil.enquote(userdata.zip));
			cols.add(StringUtil.enquote(userdata.addr1));
			cols.add(StringUtil.enquote(userdata.addr2));
			cols.add(StringUtil.enquote(userdata.addr3));

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}