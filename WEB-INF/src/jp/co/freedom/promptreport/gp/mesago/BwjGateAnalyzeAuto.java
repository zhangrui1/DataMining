package jp.co.freedom.promptreport.gp.mesago;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.Config;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.promptreport.gp.GeneralPurposePromptReportConfig;
import jp.co.freedom.promptreport.util.Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * ゲート別データ集計用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
@WebServlet(name = "BwjGateAnalyzeAuto", urlPatterns = { "/BwjGateAnalyzeAuto" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 10000000)
public class BwjGateAnalyzeAuto extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Getメソッドの処理
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// アップロードファイルの保存
		FileUtil.saveUploadFiles(request, Config.UPLOAD_DIR);
		// アップロードディレクトリの絶対パス
		String uploadDirPath = request.getServletContext().getRealPath("")
				+ File.separator + Config.UPLOAD_DIR;

		// config.xmlのパーズ
		File configXml = FileUtil.getXmlFile(uploadDirPath);
		assert configXml != null;
		GeneralPurposePromptReportConfig config = null;
		try {
			// xmlファイルのパーズによりDocumentノードを生成
			Document document = DOMUtil.domParser(configXml.getAbsolutePath());
			assert document != null;
			// config.xmlのルート要素ノード
			Element configElement = document.getDocumentElement();
			config = Utilities.createConfig(configElement);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// アップロードファイルの削除
		FileUtil.delete(uploadDirPath);
		Connection conn = null;
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			conn = DriverManager.getConnection(Config.PSQL_URL_COMMONDB_LOCAL,
					Config.PSQL_USER, Config.PSQL_PASSWORD_LOCAL);

			Object dayObj = request.getParameter("day");
			assert dayObj != null && dayObj instanceof String;
			String day = (String) dayObj;

			List<BwjGateAnalyzeResult[]> resultList = new ArrayList<BwjGateAnalyzeResult[]>(); // 集計結果

			for (int nTime = 10; nTime <= 19; nTime++) {
				// for (int nTime = 10; nTime <= 19; nTime++) {
				BwjGateAnalyzeResult[] resultDto = new BwjGateAnalyzeResult[4];
				for (int resultIndex = 0; resultIndex < resultDto.length; resultIndex++) {
					resultDto[resultIndex] = new BwjGateAnalyzeResult();
				}
				String time = String.valueOf(nTime);
				List<String[]> newEntry = Utilities.getNewEntryRfidDetailInfo(
						conn, config.days, day, "9", time); // 新規エントリ総数
				List<String[]> repeat = Utilities.getRepeatRfidDetailInfo(conn,
						config.days, day, "9", time); // リピーター総数

				// 業種別集計結果
				List<Integer> e3AppointedDay = new ArrayList<Integer>();// 当日登録
				List<Integer> e3breakdown = new ArrayList<Integer>(); // 事前登録
				List<Integer> e3repeat = new ArrayList<Integer>(); // リピーター
				List<Integer> e4AppointedDay = new ArrayList<Integer>();// 当日登録
				List<Integer> e4breakdown = new ArrayList<Integer>(); // 事前登録
				List<Integer> e4repeat = new ArrayList<Integer>(); // リピーター
				List<Integer> e5AppointedDay = new ArrayList<Integer>();// 当日登録
				List<Integer> e5breakdown = new ArrayList<Integer>(); // 事前登録
				List<Integer> e5repeat = new ArrayList<Integer>(); // リピーター
				List<Integer> e6AppointedDay = new ArrayList<Integer>();// 当日登録
				List<Integer> e6breakdown = new ArrayList<Integer>(); // 事前登録
				List<Integer> e6repeat = new ArrayList<Integer>(); // リピーター
				// 初期化
				for (int nIndex = 0; nIndex <= config.bwjCategoryList.size(); nIndex++) {
					e3AppointedDay.add(0);
					e3breakdown.add(0);
					e3repeat.add(0);
					e4AppointedDay.add(0);
					e4breakdown.add(0);
					e4repeat.add(0);
					e5AppointedDay.add(0);
					e5breakdown.add(0);
					e5repeat.add(0);
					e6AppointedDay.add(0);
					e6breakdown.add(0);
					e6repeat.add(0);
				}
				// 新規登録集計
				for (int nIndex = 0; nIndex < newEntry.size(); nIndex++) {
					String[] info = newEntry.get(nIndex);
					int categoryId = Integer.parseInt(info[1]);
					boolean preFlg = "1".equals(info[2]);
					if ("E3".equals(info[3])) {
						if (preFlg) {
							e3breakdown.set(categoryId,
									e3breakdown.get(categoryId) + 1);
						} else {
							e3AppointedDay.set(categoryId,
									e3AppointedDay.get(categoryId) + 1);
						}
					} else if ("E4".equals(info[3])) {
						if (preFlg) {
							e4breakdown.set(categoryId,
									e4breakdown.get(categoryId) + 1);
						} else {
							e4AppointedDay.set(categoryId,
									e4AppointedDay.get(categoryId) + 1);
						}
					} else if ("E5".equals(info[3])) {
						if (preFlg) {
							e5breakdown.set(categoryId,
									e5breakdown.get(categoryId) + 1);
						} else {
							e5AppointedDay.set(categoryId,
									e5AppointedDay.get(categoryId) + 1);
						}
					} else if ("E6".equals(info[3])) {
						if (preFlg) {
							e6breakdown.set(categoryId,
									e6breakdown.get(categoryId) + 1);
						} else {
							e6AppointedDay.set(categoryId,
									e6AppointedDay.get(categoryId) + 1);
						}
					}
				}
				// リピーター集計
				for (int nIndex = 0; nIndex < repeat.size(); nIndex++) {
					String[] info = repeat.get(nIndex);
					int categoryId = Integer.parseInt(info[1]);
					if ("E3".equals(info[3])) {
						e3repeat.set(categoryId, e3repeat.get(categoryId) + 1);
					} else if ("E4".equals(info[3])) {
						e4repeat.set(categoryId, e4repeat.get(categoryId) + 1);
					} else if ("E5".equals(info[3])) {
						e5repeat.set(categoryId, e5repeat.get(categoryId) + 1);
					} else if ("E6".equals(info[3])) {
						e6repeat.set(categoryId, e6repeat.get(categoryId) + 1);
					}
				}
				// TODO: 集計結果リストに格納（直前のデータから減算）
				if (nTime != 10) {
					// BwjGateAnalyzeResult[] previousResultDto = resultList
					// .get(resultList.size() - 1); // 一つ前の集計結果
					List<BwjGateAnalyzeTotalResult> previousResultDto = sum(resultList);// 一つ前の集計結果

					/* 東3ホール */
					resultDto[0].hole = 3;
					resultDto[0].endTime = nTime;
					resultDto[0].appointedday = Utilities.sum(e3AppointedDay)
							- previousResultDto.get(0).appointeddayTotal;
					resultDto[0].preentry = Utilities.sum(e3breakdown)
							- previousResultDto.get(0).preentryTotal;
					resultDto[0].repeat = Utilities.sum(e3repeat)
							- previousResultDto.get(0).repeatTotal;
					/* 東4ホール */
					resultDto[1].hole = 4;
					resultDto[1].endTime = nTime;
					resultDto[1].appointedday = Utilities.sum(e4AppointedDay)
							- previousResultDto.get(1).appointeddayTotal;
					resultDto[1].preentry = Utilities.sum(e4breakdown)
							- previousResultDto.get(1).preentryTotal;
					resultDto[1].repeat = Utilities.sum(e4repeat)
							- previousResultDto.get(1).repeatTotal;

					/* 東4ホール */
					resultDto[2].hole = 5;
					resultDto[2].endTime = nTime;
					resultDto[2].appointedday = Utilities.sum(e5AppointedDay)
							- previousResultDto.get(2).appointeddayTotal;
					resultDto[2].preentry = Utilities.sum(e5breakdown)
							- previousResultDto.get(2).preentryTotal;
					resultDto[2].repeat = Utilities.sum(e5repeat)
							- previousResultDto.get(2).repeatTotal;

					/* 東4ホール */
					resultDto[3].hole = 6;
					resultDto[3].endTime = nTime;
					resultDto[3].appointedday = Utilities.sum(e6AppointedDay)
							- previousResultDto.get(3).appointeddayTotal;
					resultDto[3].preentry = Utilities.sum(e6breakdown)
							- previousResultDto.get(3).preentryTotal;
					resultDto[3].repeat = Utilities.sum(e6repeat)
							- previousResultDto.get(3).repeatTotal;

				} else {
					/* 東3ホール */
					resultDto[0].hole = 3;
					resultDto[0].endTime = nTime;
					resultDto[0].appointedday = Utilities.sum(e3AppointedDay);
					resultDto[0].preentry = Utilities.sum(e3breakdown);
					resultDto[0].repeat = Utilities.sum(e3repeat);

					/* 東4ホール */
					resultDto[1].hole = 4;
					resultDto[1].endTime = nTime;
					resultDto[1].appointedday = Utilities.sum(e4AppointedDay);
					resultDto[1].preentry = Utilities.sum(e4breakdown);
					resultDto[1].repeat = Utilities.sum(e4repeat);

					/* 東4ホール */
					resultDto[2].hole = 5;
					resultDto[2].endTime = nTime;
					resultDto[2].appointedday = Utilities.sum(e5AppointedDay);
					resultDto[2].preentry = Utilities.sum(e5breakdown);
					resultDto[2].repeat = Utilities.sum(e5repeat);

					/* 東4ホール */
					resultDto[3].hole = 6;
					resultDto[3].endTime = nTime;
					resultDto[3].appointedday = Utilities.sum(e6AppointedDay);
					resultDto[3].preentry = Utilities.sum(e6breakdown);
					resultDto[3].repeat = Utilities.sum(e6repeat);

				}
				resultList.add(resultDto);
			}
			// 合計値の算出
			List<BwjGateAnalyzeTotalResult> totalList = sum(resultList);

			// JSPへのデータ引継ぎ
			request.setAttribute("caption", "2013/05/" + day + "を対象としたデータ分析");
			request.setAttribute("resultList", resultList);
			request.setAttribute("totalList", totalList);
			// JSPにフォワード
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/src/jsp/prompt/gp/gateTimeAnalyze.jsp");
			dispatcher.forward(request, response);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * POSTメソッドの処理
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response); // Getメソッドに受け流す
	}

	private static List<BwjGateAnalyzeTotalResult> sum(
			List<BwjGateAnalyzeResult[]> resultList) {
		List<BwjGateAnalyzeTotalResult> totalList = new ArrayList<BwjGateAnalyzeTotalResult>();
		for (int nHole = 0; nHole < 4; nHole++) {
			BwjGateAnalyzeTotalResult totalDto = new BwjGateAnalyzeTotalResult();
			for (int nHour = 0; nHour < resultList.size(); nHour++) {
				BwjGateAnalyzeResult resultDto[] = resultList.get(nHour);
				totalDto.appointeddayTotal += resultDto[nHole].appointedday;
				totalDto.preentryTotal += resultDto[nHole].preentry;
				totalDto.repeatTotal += resultDto[nHole].repeat;
				totalDto.totalTotal += (resultDto[nHole].appointedday
						+ resultDto[nHole].preentry + resultDto[nHole].repeat);
			}
			totalList.add(totalDto);
		}
		return totalList;
	}
}