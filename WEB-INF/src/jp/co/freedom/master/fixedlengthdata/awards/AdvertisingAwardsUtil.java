package jp.co.freedom.master.fixedlengthdata.awards;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;

/**
 * 【広告対象】審査表データ マスターデータDL用Config
 *
 * @author フリーダム・グループ
 *
 */
public class AdvertisingAwardsUtil {

	/**
	 * マスターデータを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return　マスターデータ
	 * @throws SQLException
	 */
	public static List<AdvertisingAwardsDto> getMasterData(Connection conn)
			throws SQLException {
		List<AdvertisingAwardsDto> userDataList = new ArrayList<AdvertisingAwardsDto>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM master;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			AdvertisingAwardsDto userdata = new AdvertisingAwardsDto();
			userdata.sex = rs.getString("V_SEX"); // 2. 性別
			assert userdata.sex.length() == 1;
			userdata.judgeManId = rs.getString("V_SHINSA"); // 6. 審査員NO
			assert userdata.judgeManId.length() == 4;
			userdata.media = getMedia(rs.getString("IMAGE_PATH")); // 7. 媒体
			StringBuffer sb = new StringBuffer();
			for (int nIndex = 1; nIndex <= 120; nIndex++) {
				String point = rs.getString("V_" + complementZero(nIndex, 3));
				if (StringUtil.isNotEmpty(point)) {
					userdata.judgeDataSize = nIndex;
				}
				sb.append(StringUtil.isNotEmpty(point) ? point
						: AdvertisingAwardsConfig.EMPTY_MARK);
			}
			userdata.judgeData = sb.toString(); // 審査データ
			userDataList.add(userdata);
		}
		rs.close();
		stmt.close();
		return userDataList;
	}

	/**
	 * 指定数値を0補完して長さsizeの数値文字列に変換
	 *
	 * @param value
	 *            指定数値
	 * @param size
	 *            　文字列長
	 * @return　変換後の数値文字列
	 */
	private static String complementZero(int value, int size) {
		String valueStr = String.valueOf(value);
		int zeroNum = size - valueStr.length();
		return createSuccessiveString(zeroNum, "0") + valueStr;
	}

	/**
	 * イメージファイルパスより媒体を特定
	 *
	 * @param imagePath
	 *            イメージファイルパス
	 * @return 媒体ID
	 */
	private static String getMedia(String imagePath) {
		assert StringUtil.isNotEmpty(imagePath);
		if (StringUtil.find(imagePath, "新聞")) {
			return "1";
		} else if (StringUtil.find(imagePath, "テレビ")) {
			return "2";
		} else if (StringUtil.find(imagePath, "ラジオ")) {
			return "3";
		} else if (StringUtil.find(imagePath, "雑誌")) {
			return "4";
		}
		return null;
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
	 * @param areaId
	 *            地区ID
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoad(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<AdvertisingAwardsDto> userDataList, String areaId)
			throws ServletException, IOException, SQLException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		for (AdvertisingAwardsDto awardInfo : userDataList) {
			for (int nRecord = 1;; nRecord++) {
				List<String> data = new ArrayList<String>(); // 出力用リスト
				String subNo = String.valueOf(nRecord); // サブNo.
				int start = AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* (nRecord - 1);
				int end = AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* nRecord <= 120 ? AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* nRecord
						: 120;
				data.add("SM3244"); // 固定ヘッダー(1～3)
				data.add(awardInfo.sex); // 4.性別
				data.add(areaId); // 5.地区
				data.add(awardInfo.judgeManId); // 6.審査員ID
				data.add(awardInfo.media); // 7.媒体
				data.add("1"); // 8.部門
				data.add(subNo); // 9.サブNo.
				String judgeData = awardInfo.judgeData.substring(start, end);
				data.add(normalize(judgeData)); // 審査データ
				data.add(createSuccessiveString(16 + 3 * 5 + 4,
						AdvertisingAwardsConfig.EMPTY_MARK)); // DUMMY+未使用領域+DUMMY
				data.add(System.getProperty("line.separator")); // CRLF
				FileUtil.writerWithNoReturnCode(data, writer, ""); // 1行分書き出し
				if (end >= awardInfo.judgeDataSize) {
					break;
				}
			}
		}
		return true;
	}

	/**
	 * CSVデータのダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param userDataList
	 *            マスターデータを格納するリスト
	 * @param areaId
	 *            地区ID
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadCsv(HttpServletRequest request,
			HttpServletResponse response, String outputFileName,
			List<AdvertisingAwardsDto> userDataList, String areaId)
			throws ServletException, IOException, SQLException {
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

		header.add(StringUtil.enquote("固定ヘッダー(1～3)"));
		header.add(StringUtil.enquote("性別"));
		header.add(StringUtil.enquote("地区"));
		header.add(StringUtil.enquote("審査員ID"));
		header.add(StringUtil.enquote("媒体"));
		header.add(StringUtil.enquote("部門"));
		header.add(StringUtil.enquote("サブNo"));
		for (int nIndex = 1; nIndex <= 80; nIndex++) {
			header.add(StringUtil.enquote(String.valueOf(nIndex)));
		}
		FileUtil.writer(header, writer, "\t");

		for (AdvertisingAwardsDto awardInfo : userDataList) {
			for (int nRecord = 1;; nRecord++) {
				List<String> output = new ArrayList<String>();
				List<String> data = new ArrayList<String>(); // 出力用リスト
				String subNo = String.valueOf(nRecord); // サブNo.
				int start = AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* (nRecord - 1);
				int end = AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* nRecord <= 120 ? AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
						* nRecord
						: 120;
				data.add("SM3244"); // 固定ヘッダー(1～3)
				data.add(awardInfo.sex); // 4.性別
				data.add(areaId); // 5.地区
				data.add(awardInfo.judgeManId); // 6.審査員ID
				data.add(awardInfo.media); // 7.媒体
				data.add("1"); // 8.部門
				data.add(subNo); // 9.サブNo.
				String judgeData = awardInfo.judgeData.substring(start, end);
				data.add(normalize(judgeData)); // 審査データ
				data.add(createSuccessiveString(16 + 3 * 5 + 4,
						AdvertisingAwardsConfig.EMPTY_MARK)); // DUMMY+未使用領域+DUMMY
				data.add(System.getProperty("line.separator")); // CRLF

				String record = StringUtil.concatWithDelimit("", data);
				output.add(StringUtil.enquote(record.substring(0, 6))); // 固定ヘッダー
				output.add(StringUtil.enquote(record.substring(6, 7))); // 性別
				output.add(StringUtil.enquote(record.substring(7, 8))); // 地区
				output.add(StringUtil.enquote(record.substring(8, 12))); // 審査員ID
				output.add(StringUtil.enquote(record.substring(12, 13))); // 媒体
				output.add(StringUtil.enquote(record.substring(13, 14))); // 部門
				output.add(StringUtil.enquote(record.substring(14, 15))); // サブNo
				for (int nIndex = 1; nIndex <= 80; nIndex++) {
					output.add(StringUtil.enquote(record.substring(14 + nIndex,
							15 + nIndex))); // 審査データ
				}
				FileUtil.writer(output, writer, "\t"); // 1レコード分のデータ書き出し
				if (end >= awardInfo.judgeDataSize) {
					break;
				}
			}
		}
		return true;
	}

	/**
	 * 審査データの正規化
	 *
	 * @param judgeData
	 *            審査データ
	 * @return 正規化後の審査データ
	 */
	private static String normalize(String judgeData) {
		assert StringUtil.isNotEmpty(judgeData);
		if (judgeData.length() < AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER) {
			return judgeData
					+ createSuccessiveString(
							AdvertisingAwardsConfig.JUDGE_DATA_MAX_NUMBER
									- judgeData.length(),
							AdvertisingAwardsConfig.EMPTY_MARK);
		}
		return judgeData;
	}

	/**
	 * 指定個数分のvalueの連続する文字列を作成
	 *
	 * @param size
	 *            個数
	 * @return 生成文字列
	 */
	private static String createSuccessiveString(int size, String value) {
		StringBuffer sb = new StringBuffer();
		for (int nIndex = 0; nIndex < size; nIndex++) {
			sb.append(value);
		}
		return sb.toString();
	}

}
