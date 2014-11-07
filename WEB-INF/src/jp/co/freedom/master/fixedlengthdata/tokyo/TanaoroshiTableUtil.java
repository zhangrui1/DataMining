package jp.co.freedom.master.fixedlengthdata.tokyo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * 【固定データ長】棚卸表用ユーティリティ
 *
 * @author フリーダム・グループ
 *
 */
public class TanaoroshiTableUtil {

	final private static int ORDER[] = { 0, 15, 1, 16, 2, 17, 3, 18, 4, 19, 5,
			20, 6, 21, 7, 22, 8, 23, 9, 24, 10, 25, 11, 26, 12, 27, 13, 28, 14,
			29 };

	/**
	 * CSVデータのインポート
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param csvdata
	 *            CSVデータ
	 * @return 実行可否のブール値
	 * @throws SQLException
	 */
	public static boolean importData(Connection conn, List<String[]> csvDataList)
			throws SQLException {
		int count = 0;
		for (String[] csvData : csvDataList) {
			String sql = "INSERT INTO tana VALUES (";
			List<String> query = new ArrayList<String>();
			for (int nIndex = 1; nIndex <= 155; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setString(++position, "b" + String.valueOf(++count));
			for (int nIndex = 0; nIndex <= 153; nIndex++) {
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
	 * DBの初期化
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 処理結果の成否のブール値
	 * @throws SQLException
	 */
	public static boolean resetDB(Connection conn) throws SQLException {
		String sql = "DELETE FROM tana;";
		PreparedStatement ps = conn.prepareStatement(sql);
		boolean result = ps.execute();
		if (ps != null) {
			ps.close();
		}
		return result;
	}

	/**
	 * 全ての入力データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 全ての入力データ
	 * @throws SQLException
	 */
	public static List<TanaoroshiTableDto> getAllData(Connection conn)
			throws SQLException {
		List<TanaoroshiTableDto> userDataList = new ArrayList<TanaoroshiTableDto>();
		String sql = "SELECT * FROM tana";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			TanaoroshiTableDto baseInfo = new TanaoroshiTableDto();
			// 伝票番号
			String paymentNo = rs.getString("v_no");
			// 店コード
			String shopCode = StringUtil.convertFixedLengthData(
					rs.getString("v_code"), 3, "0");
			List<TanaoroshiDetailDto> detailInfoList = new ArrayList<TanaoroshiDetailDto>(); // 明細情報
			String previous = null;
			for (int nIndex = 1; nIndex <= 30; nIndex++) {
				/*
				 * データ取得
				 */
				// 取消コード
				String cancel = rs.getString("v_cancel"
						+ String.valueOf(nIndex));
				// 部門コード
				String dept = rs.getString("v_dept" + String.valueOf(nIndex));
				// 仕入先コード
				String supplier = rs.getString("v_supplier"
						+ String.valueOf(nIndex));
				// 金額
				String moneyStr = rs.getString("v_money"
						+ String.valueOf(nIndex));
				// 数量
				String amountStr = rs.getString("v_amount"
						+ String.valueOf(nIndex));
				/*
				 * データ補完
				 */
				// 取消コードの正規化
				if (StringUtil.isNotEmpty(cancel)) {
					if (!"X".equals(cancel) && !"x".equals(cancel)) {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 取消コードが不正です→" + cancel);
					}
				} else {
					cancel = " ";
				}
				// 数量の補完
				if (StringUtil.isEmpty(amountStr)
						&& StringUtil.isNotEmpty(dept)
						&& StringUtil.isNotEmpty(moneyStr)) {
					amountStr = "1";
				}
				// 部門の補完
				if (StringUtil.isEmpty(moneyStr)
						&& StringUtil.isEmpty(amountStr)) {
					dept = "00";
				} else if (StringUtil.isEmpty(dept)) {
					dept = previous; // 直前の部門コードを補完
				}
				/*
				 * 数値型パーズ
				 */
				int money = 0;
				int amount = 0;
				if (StringUtil.isNotEmpty(moneyStr)) {
					// moneyStr = StringUtil.extractNumberStr(moneyStr, "0");
					if (StringUtil.integerStringCheck(moneyStr)) {
						money = Integer.parseInt(moneyStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 金額" + moneyStr + "に不正文字列が混入されています。");
					}
				}
				if (StringUtil.isNotEmpty(amountStr)) {
					// amountStr = StringUtil.extractNumberStr(amountStr, "0");
					if (StringUtil.integerStringCheck(amountStr)) {
						amount = Integer.parseInt(amountStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 数量" + amountStr + "に不正文字列が混入されています。");
					}
				}
				/*
				 * 固定データ長化
				 */
				dept = StringUtil.convertFixedLengthData(dept, 3, "0");
				supplier = StringUtil.convertFixedLengthData(supplier, 3, "0");
				moneyStr = StringUtil.convertFixedLengthData(moneyStr, 6, "0");
				amountStr = StringUtil
						.convertFixedLengthData(amountStr, 3, "0");
				/*
				 * データ収録
				 */
				TanaoroshiDetailDto detailInfo = new TanaoroshiDetailDto();
				detailInfo.cancel = cancel;
				detailInfo.dept = dept;
				detailInfo.supplier = supplier;
				detailInfo.money = money;
				detailInfo.amount = amount;
				detailInfo.moneyStr = moneyStr;
				detailInfo.amountStr = amountStr;
				detailInfoList.add(detailInfo);

				previous = dept;
			}
			baseInfo.paymentNo = paymentNo;
			baseInfo.shopCode = shopCode;
			baseInfo.detailInfo = detailInfoList;
			userDataList.add(baseInfo);
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
	 * テキストデータのダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            マスターデータを格納するリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<TanaoroshiTableDto> userDataList) throws ServletException,
			IOException, SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		for (TanaoroshiTableDto info : userDataList) {
			List<String> data = new ArrayList<String>(); // 出力用リスト
			data.add(info.paymentNo); // 伝票番号
			data.add(info.shopCode); // 店コード
			// 明細情報
			for (int nIndex = 0; nIndex < 30; nIndex++) {
				TanaoroshiDetailDto detail = info.detailInfo.get(ORDER[nIndex]);
				data.add(detail.cancel); // 取消コード
				data.add(detail.dept); // 部門コード
				data.add(detail.supplier); // 仕入先コード
				data.add(detail.moneyStr); // 金額
				data.add(detail.amountStr); // 数値
			}
			// 数量の小計1
			String sumAmount1 = String
					.valueOf(sumAmount(info.detailInfo, 1, 15));
			sumAmount1 = StringUtil.convertFixedLengthData(sumAmount1, 4, "0");
			data.add(sumAmount1);
			// 数量の小計2
			String sumAmount2 = String.valueOf(sumAmount(info.detailInfo, 16,
					30));
			sumAmount2 = StringUtil.convertFixedLengthData(sumAmount2, 4, "0");
			data.add(sumAmount2);

			// 金額の小計1
			String sumMoney1 = String.valueOf(sumMoney(info.detailInfo, 1, 15));
			sumMoney1 = StringUtil.convertFixedLengthData(sumMoney1, 8, "0");
			data.add(sumMoney1);

			// 金額の小計2
			String sumMoney2 = String
					.valueOf(sumMoney(info.detailInfo, 16, 30));
			sumMoney2 = StringUtil.convertFixedLengthData(sumMoney2, 8, "0");
			data.add(sumMoney2);
			// 未使用
			data.add("0");
			String all = StringUtil.concatWithDelimit("", data);
			if (all.length() != 512) {
				System.out.println("【伝票番号】" + info.paymentNo);
				System.out.println("データ長が不正です→" + all.length());
			}
			FileUtil.writer(data, writer, ""); // 1行分書き出し
		}
		return true;
	}

	/**
	 * テキストデータのダウンロード(CSV)
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            マスターデータを格納するリスト
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadCsv(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<TanaoroshiTableDto> userDataList) throws ServletException,
			IOException, SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();

		// ヘッダ情報の書き出し
		List<String> header = new ArrayList<String>();

		header.add(StringUtil.enquote("伝票番号"));
		header.add(StringUtil.enquote("店コード"));
		for (int nIndex = 1; nIndex <= 30; nIndex++) {
			header.add(StringUtil.enquote("取消" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("部門" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("仕入先" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("金額" + String.valueOf(nIndex)));
			header.add(StringUtil.enquote("数値" + String.valueOf(nIndex)));
		}
		header.add(StringUtil.enquote("合計数量1"));
		header.add(StringUtil.enquote("合計数量2"));
		header.add(StringUtil.enquote("合計金額1"));
		header.add(StringUtil.enquote("合計金額2"));
		header.add(StringUtil.enquote("未使用"));
		FileUtil.writer(header, writer, "\t");

		for (TanaoroshiTableDto info : userDataList) {
			List<String> data = new ArrayList<String>(); // 出力用リスト
			data.add(StringUtil.enquote(info.paymentNo)); // 伝票番号
			data.add(StringUtil.enquote(info.shopCode)); // 店コード
			// 明細情報
			for (int nIndex = 0; nIndex < 30; nIndex++) {
				TanaoroshiDetailDto detail = info.detailInfo.get(ORDER[nIndex]);
				data.add(StringUtil.enquote(detail.cancel)); // 取消コード
				data.add(StringUtil.enquote(detail.dept)); // 部門コード
				data.add(StringUtil.enquote(detail.supplier)); // 仕入先コード
				data.add(StringUtil.enquote(detail.moneyStr)); // 金額
				data.add(StringUtil.enquote(detail.amountStr)); // 数値
			}
			// 数量の小計1
			String sumAmount1 = String
					.valueOf(sumAmount(info.detailInfo, 1, 15));
			sumAmount1 = StringUtil.convertFixedLengthData(sumAmount1, 4, "0");
			data.add(StringUtil.enquote(sumAmount1));
			// 数量の小計2
			String sumAmount2 = String.valueOf(sumAmount(info.detailInfo, 16,
					30));
			sumAmount2 = StringUtil.convertFixedLengthData(sumAmount2, 4, "0");
			data.add(StringUtil.enquote(sumAmount2));

			// 金額の小計1
			String sumMoney1 = String.valueOf(sumMoney(info.detailInfo, 1, 15));
			sumMoney1 = StringUtil.convertFixedLengthData(sumMoney1, 8, "0");
			data.add(StringUtil.enquote(sumMoney1));

			// 金額の小計2
			String sumMoney2 = String
					.valueOf(sumMoney(info.detailInfo, 16, 30));
			sumMoney2 = StringUtil.convertFixedLengthData(sumMoney2, 8, "0");
			data.add(StringUtil.enquote(sumMoney2));
			// 未使用
			data.add(StringUtil.enquote("0"));
			FileUtil.writer(data, writer, "\t"); // 1行分書き出し
		}
		return true;
	}

	/**
	 * 数量の小計
	 *
	 * @param detailInfoList
	 *            明細情報
	 * @param start
	 *            計数の開始番号
	 * @param end
	 *            計数の終了番号
	 * @return 小計
	 */
	private static int sumAmount(List<TanaoroshiDetailDto> detailInfoList,
			int start, int end) {
		int sumAmount = 0;
		for (int nIndex = start; nIndex <= end; nIndex++) {
			TanaoroshiDetailDto detail = detailInfoList.get(nIndex - 1);
			sumAmount += detail.amount;
		}
		return sumAmount;
	}

	/**
	 * 金額の小計
	 *
	 * @param detailInfoList
	 *            明細情報
	 * @param start
	 *            計数の開始番号
	 * @param end
	 *            計数の終了番号
	 * @return 小計
	 */
	private static long sumMoney(List<TanaoroshiDetailDto> detailInfoList,
			int start, int end) {
		long sumMoney = 0;
		for (int nIndex = start; nIndex <= end; nIndex++) {
			TanaoroshiDetailDto detail = detailInfoList.get(nIndex - 1);
			sumMoney += (long) detail.money * (long) detail.amount;
		}
		return sumMoney;
	}
}