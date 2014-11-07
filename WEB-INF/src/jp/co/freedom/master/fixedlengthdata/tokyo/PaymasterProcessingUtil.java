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
 * 【固定データ長】経理処理控用ユーティリティ
 *
 * @author フリーダム・グループ
 *
 */
public class PaymasterProcessingUtil {

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
			String sql = "INSERT INTO ido VALUES (";
			List<String> query = new ArrayList<String>();
			for (int nIndex = 1; nIndex <= 37; nIndex++) { // [備忘]カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			int position = 0;
			ps.setString(++position, "b" + String.valueOf(++count));
			for (int nIndex = 0; nIndex <= 35; nIndex++) {
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
		String sql = "DELETE FROM ido;";
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
	public static List<PaymasterProcessingDto> getAllData(Connection conn)
			throws SQLException {
		List<PaymasterProcessingDto> userDataList = new ArrayList<PaymasterProcessingDto>();
		String sql = "SELECT * FROM ido";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			PaymasterProcessingDto baseInfo = new PaymasterProcessingDto();
			// 伝票番号
			String paymentNo = rs.getString("v_no");
			// 店コード
			String shopCode = StringUtil.convertFixedLengthData(
					rs.getString("v_inputcode"), 3, "0");
			// 出荷店コード
			String shippingShopCode = StringUtil.convertFixedLengthData(
					rs.getString("v_outputcode"), 3, "0");
			// 日付
			String date = rs.getString("v_day");
			if (StringUtil.isEmpty(date) || date.length() != 6) {
				System.out.println("★伝票番号=" + paymentNo + "→ 日付情報が不正です");
			}
			List<PaymasterProcessingDetailDto> detailInfoList = new ArrayList<PaymasterProcessingDetailDto>(); // 明細情報
			String previous = null;
			for (int nIndex = 1; nIndex <= 6; nIndex++) {
				/*
				 * データ取得
				 */
				// 部門
				String dept = rs.getString("v_dept" + String.valueOf(nIndex));
				// 数量
				String amountStr = rs.getString("v_amount"
						+ String.valueOf(nIndex));
				// 単価
				String priceStr = rs.getString("v_price"
						+ String.valueOf(nIndex));
				// 金額
				String moneyStr = rs.getString("v_money"
						+ String.valueOf(nIndex));
				// 売価
				String saleStr = rs
						.getString("v_sale" + String.valueOf(nIndex));
				/*
				 * データ補完
				 */
				// 数量の補完
				if (StringUtil.isEmpty(amountStr)
						&& StringUtil.isNotEmpty(dept)
						&& StringUtil.isNotEmpty(priceStr)) {
					amountStr = "1";
				}
				// 部門の補完
				if (StringUtil.isEmpty(amountStr)
						&& StringUtil.isEmpty(priceStr)
						&& StringUtil.isEmpty(moneyStr)
						&& StringUtil.isEmpty(saleStr)) {
					dept = "00";
				} else if (StringUtil.isEmpty(dept)) {
					dept = previous; // 直前の部門コードを補完
				}
				/*
				 * 数値型パーズ
				 */
				long amount = 0;
				long price = 0;
				long money = 0;
				long sale = 0;
				if (StringUtil.isNotEmpty(amountStr)) {
					if (StringUtil.integerStringCheck(amountStr)) {
						amount = Integer.parseInt(amountStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 数量" + amountStr + "に不正文字列が混入されています。");
					}
				}
				if (StringUtil.isNotEmpty(priceStr)) {
					if (StringUtil.integerStringCheck(priceStr)) {
						price = Integer.parseInt(priceStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 単価" + priceStr + "に不正文字列が混入されています。");
					}
				}
				if (StringUtil.isNotEmpty(moneyStr)) {
					if (StringUtil.integerStringCheck(moneyStr)) {
						money = Integer.parseInt(moneyStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 金額" + moneyStr + "に不正文字列が混入されています。");
					}
				}
				if (StringUtil.isNotEmpty(amountStr)
						&& StringUtil.isNotEmpty(priceStr)) {
					money = amount * price;
					moneyStr = String.valueOf(money);
				}

				if (StringUtil.isNotEmpty(saleStr)) {
					if (StringUtil.integerStringCheck(saleStr)) {
						sale = Integer.parseInt(saleStr);
					} else {
						System.out.println("★伝票番号=" + paymentNo);
						System.out.println("《" + String.valueOf(nIndex)
								+ "番目》 売価" + saleStr + "に不正文字列が混入されています。");
					}
				}

				/*
				 * 固定データ長化
				 */
				dept = StringUtil.convertFixedLengthData(dept, 2, "0");
				amountStr = StringUtil
						.convertFixedLengthData(amountStr, 4, "0");
				priceStr = StringUtil.convertFixedLengthData(priceStr, 6, "0");
				moneyStr = StringUtil.convertFixedLengthData(moneyStr, 7, "0");
				saleStr = StringUtil.convertFixedLengthData(saleStr, 6, "0");

				/*
				 * データ収録
				 */
				PaymasterProcessingDetailDto detailInfo = new PaymasterProcessingDetailDto();
				detailInfo.dept = dept;
				detailInfo.amount = amount;
				detailInfo.amountStr = amountStr;
				detailInfo.price = price;
				detailInfo.priceStr = priceStr;
				detailInfo.money = money;
				detailInfo.moneyStr = moneyStr;
				detailInfo.sale = sale;
				detailInfo.saleStr = saleStr;
				detailInfoList.add(detailInfo);

				previous = dept;
			}
			baseInfo.paymentNo = paymentNo;
			baseInfo.date = date;
			baseInfo.shopCode = shopCode;
			baseInfo.shippingShopCode = shippingShopCode;
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
			List<PaymasterProcessingDto> userDataList) throws ServletException,
			IOException, SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		for (PaymasterProcessingDto info : userDataList) {
			List<String> data = new ArrayList<String>(); // 出力用リスト
			data.add(info.paymentNo); // 伝票番号
			data.add(info.shopCode); // 店コード
			data.add(info.date); // 日付
			data.add("21"); // 区分
			data.add(info.shippingShopCode); // 出荷店
			// 明細情報
			for (PaymasterProcessingDetailDto detail : info.detailInfo) {
				data.add("0"); // 未使用
				data.add(detail.dept); // 部門コード
				data.add("00000000"); // 品番・色
				data.add(detail.amountStr); // 数値
				data.add(detail.priceStr); // 単価
				data.add(detail.moneyStr); // 金額
				data.add(detail.saleStr); // 売価
			}
			// 数量の小計
			String sumAmount = String.valueOf(sumAmount(info.detailInfo, 1, 6));
			sumAmount = StringUtil.convertFixedLengthData(sumAmount, 4, "0");
			data.add(sumAmount);

			// 金額の小計
			String sumMoney = String.valueOf(sumMoney1(info, 1, 6));
			sumMoney = StringUtil.convertFixedLengthData(sumMoney, 7, "0");
			data.add(sumMoney);

			// 未使用
			data.add("000000000000");

			// EOF
			data.add("GAIB10000");

			String all = StringUtil.concatWithDelimit("", data);
			if (256 != all.length()) {
				System.out.println("★伝票番号=" + info.paymentNo + "→　データ長が不正です");
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
			List<PaymasterProcessingDto> userDataList) throws ServletException,
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
		header.add(StringUtil.enquote("日付"));
		header.add(StringUtil.enquote("区分"));
		header.add(StringUtil.enquote("出荷店"));
		for (int nIndex = 1; nIndex <= 6; nIndex++) {
			header.add(StringUtil.enquote("未使用"));
			header.add(StringUtil.enquote("部門"));
			header.add(StringUtil.enquote("品番・色"));
			header.add(StringUtil.enquote("数量"));
			header.add(StringUtil.enquote("単価"));
			header.add(StringUtil.enquote("金額"));
			header.add(StringUtil.enquote("売価"));
		}
		header.add(StringUtil.enquote("合計数"));
		header.add(StringUtil.enquote("合計額1"));
		header.add(StringUtil.enquote("合計額2"));
		header.add(StringUtil.enquote("未使用"));
		header.add(StringUtil.enquote("EOF"));

		FileUtil.writer(header, writer, "\t");

		for (PaymasterProcessingDto info : userDataList) {
			List<String> data = new ArrayList<String>(); // 出力用リスト
			data.add(StringUtil.enquote(info.paymentNo)); // 伝票番号
			data.add(StringUtil.enquote(info.shopCode)); // 店コード
			data.add(StringUtil.enquote(info.date)); // 日付
			data.add(StringUtil.enquote("21")); // 区分
			data.add(StringUtil.enquote(info.shippingShopCode)); // 出荷店
			// 明細情報
			for (PaymasterProcessingDetailDto detail : info.detailInfo) {
				data.add(StringUtil.enquote("0")); // 未使用
				data.add(StringUtil.enquote(detail.dept)); // 部門コード
				data.add(StringUtil.enquote("00000000")); // 品番・色
				data.add(StringUtil.enquote(detail.amountStr)); // 数値
				data.add(StringUtil.enquote(detail.priceStr)); // 単価
				data.add(StringUtil.enquote(detail.moneyStr)); // 金額
				data.add(StringUtil.enquote(detail.saleStr)); // 売価
			}
			// 数量の小計
			String sumAmount = String.valueOf(sumAmount(info.detailInfo, 1, 6));
			sumAmount = StringUtil.convertFixedLengthData(sumAmount, 4, "0");
			data.add(StringUtil.enquote(sumAmount));

			// 金額の小計
			String sumMoney1 = String.valueOf(sumMoney1(info, 1, 6));
			String sumMoney2 = String.valueOf(sumMoney2(info, 1, 6));

			sumMoney1 = StringUtil.convertFixedLengthData(sumMoney1, 7, "0");
			data.add(StringUtil.enquote(sumMoney1));
			sumMoney2 = StringUtil.convertFixedLengthData(sumMoney2, 7, "0");
			data.add(StringUtil.enquote(sumMoney2));

			if (!sumMoney1.equals(sumMoney2)) {
				System.out.println("★伝票番号=" + info.paymentNo
						+ "→　数量×単価と金額が一致しない");
			}

			// 未使用
			data.add(StringUtil.enquote("000000000000"));

			// EOF
			data.add(StringUtil.enquote("GAIB100000"));

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
	private static int sumAmount(
			List<PaymasterProcessingDetailDto> detailInfoList, int start,
			int end) {
		int sumAmount = 0;
		for (int nIndex = start; nIndex <= end; nIndex++) {
			PaymasterProcessingDetailDto detail = detailInfoList
					.get(nIndex - 1);
			sumAmount += detail.amount;
		}
		return sumAmount;
	}

	/**
	 * 金額の小計1
	 *
	 * @param detailInfoList
	 *            明細情報
	 * @param start
	 *            計数の開始番号
	 * @param end
	 *            計数の終了番号
	 * @return 小計
	 */
	private static long sumMoney1(PaymasterProcessingDto info, int start,
			int end) {
		long sumMoney1 = 0;
		List<PaymasterProcessingDetailDto> detailInfoList = info.detailInfo;
		for (int nIndex = start; nIndex <= end; nIndex++) {
			PaymasterProcessingDetailDto detail = detailInfoList
					.get(nIndex - 1);
			sumMoney1 += (long) detail.amount * (long) detail.price;
		}
		return sumMoney1;
	}

	/**
	 * 金額の小計2
	 *
	 * @param detailInfoList
	 *            明細情報
	 * @param start
	 *            計数の開始番号
	 * @param end
	 *            計数の終了番号
	 * @return 小計
	 */
	private static long sumMoney2(PaymasterProcessingDto info, int start,
			int end) {
		long sumMoney2 = 0;
		List<PaymasterProcessingDetailDto> detailInfoList = info.detailInfo;
		for (int nIndex = start; nIndex <= end; nIndex++) {
			PaymasterProcessingDetailDto detail = detailInfoList
					.get(nIndex - 1);
			sumMoney2 += (long) detail.money;
		}
		return sumMoney2;
	}

}