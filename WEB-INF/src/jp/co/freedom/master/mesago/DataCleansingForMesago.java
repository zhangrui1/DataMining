package jp.co.freedom.master.mesago;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;
import jp.co.freedom.master.utilities.mesago.DataCleansingForMesagoUtil;
import jp.co.freedom.master.utilities.mesago.osaka.OsakaConfig;

/**
 * 【MESAGO】データクレンジング用サーブレット
 *
 * @author フリーダム・グループ
 *
 */
public class DataCleansingForMesago extends HttpServlet {

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

		// TEL番号の修復フラグ
		Object restoreTelFlgObj = request.getParameter("restoreTel");
		String restoreTelFlg = (String) restoreTelFlgObj;
		assert StringUtil.isNotEmpty(restoreTelFlg);
		boolean restoreTel = StringUtil.toBoolean(restoreTelFlg);

		// JDBCドライバーのロード
		DataCleansingForMesagoUtil.loadJdbcPluginJar();
		Connection conn = null;
		try {
			// DBサーバーへの接続情報
//			conn = DriverManager.getConnection(
//					MesagoConfig.PSQL_URL_REMOTE_IP2014,
//					MesagoConfig.PSQL_USER, MesagoConfig.PSQL_PASSWORD_REMOTE);
			conn = DriverManager.getConnection(
					OsakaConfig.PSQL_URL_LOCAL_IFFT2014, OsakaConfig.PSQL_USER,
					OsakaConfig.PSQL_PASSWORD_LOCAL);
			// 2データ生成用secondテーブル初期化
			DataCleansingForMesagoUtil.initializedSecondTable(conn);
			// 1次データ
			List<MesagoUserDataDto> allFirstData = DataCleansingForMesagoUtil
					.getData(conn, "first", null, restoreTel);
			for (MesagoUserDataDto userdata : allFirstData) {
				/* □□□□□□　データ補正　□□□□□□ */
				// ■■■　会社名に対するデータ補正　■■■■
				/*
				 * 1. 会社名に「●」が含まれる場合の対応は手作業
				 */
				DataCleansingForMesagoUtil.repairWildcardForCompanyName(
						userdata, allFirstData);
				/*
				 * 2.会社名に「営業」「工場」「支社」「事業」「（地名）店」「（地名）支店」を含む単語の検索
				 */
				DataCleansingForMesagoUtil
						.searchCompanyNameContainsNGKeyword(userdata);
				/*
				 * 3.会社名がNGワードである場合は除去
				 */
				DataCleansingForMesagoUtil
						.removeCompanyNameContainsNGKeyword(userdata);
				/*
				 * 4.法人格前後のホワイトスペース除去
				 */
				DataCleansingForMesagoUtil.removeWhiteSpaceOnCompany(userdata);

				/*
				 * 5.社名と個人名が完全一致である場合は社名を削除
				 */
				DataCleansingForMesagoUtil.removeCompanyEqualsName(userdata);

				// ■■■　住所に対するデータ補正　■■■■
				/*
				 * 5.海外住所は国名を除きAddress2に集約
				 */
				DataCleansingForMesagoUtil
						.moveAddr2ForOverseaAddrInfo(userdata);
				/*
				 * 海外住所にかぎり「以下住所」の末尾に指定国名および国名欄記載の国名が含まれる場合はそれを削除
				 */
				DataCleansingForMesagoUtil
						.removeAddr2LastCountryNameForOverseaAddrInfo(userdata);
				/*
				 * 6.「以下住所」に含まれる「ビル名」「建物名」を確認し、ビル名のセルに移す
				 * 7.「ビル名」に丁目や番地が入っている場合、「以下住所」を確認の上、「以下住所」に戻す
				 */
				DataCleansingForMesagoUtil.validateAddressInfo(userdata);

				/*
				 * 8.「以下住所」に「●」が含まれる場合は、「社名」と「電話番号」の完全一致の場合はデータを修正する
				 */
				DataCleansingForMesagoUtil.compairAddr3(userdata, allFirstData);

				/*
				 * 10.上記クレンジングの結果でも住所に「●」が含まれる場合、「メールアドレス」が完全であれば住所に関連するセルを削除し
				 * データ自体は残す。「メールアドレス」が不完全であればデータ自体を削除する。
				 */
				DataCleansingForMesagoUtil
						.finalCleaningForAddressInfo(userdata);

				// ■■■　氏名に対するデータ補正　■■■■
				/*
				 * 氏名が空値である場合には「見本市ご担当者」に置換
				 */
				DataCleansingForMesagoUtil.replaceEmptyName(userdata);

				// ■■■　電話／FAXに対するデータ補正　■■■■
				/*
				 * 「0」と「-」のみで構成される番号を削除
				 */
				DataCleansingForMesagoUtil.removeGarbageTelFaxNumber(userdata);

				// ■■■　メールアドレスに対するデータ補正　■■■■
				/*
				 * 「@」前に「●」が含まれる場合は削除
				 */
				DataCleansingForMesagoUtil.removeGarbageEmail(userdata);
				/*
				 * 「@」以降に「●」が含まれる場合、「会社名」「住所」が完全一致するデータがある場合はデータを修復する。ない場合はセルデータを削除
				 */
				DataCleansingForMesagoUtil.removeGarbageEmail2(userdata,
						allFirstData);

				// ■■■　URLに対するデータ補正　■■■■
				/*
				 * 「@」を含む場合は削除
				 */
				DataCleansingForMesagoUtil.removeGarbageUrl(userdata);

				/* □□□□□□　データ削除　□□□□□□ */
				/*
				 * 1. 住所が全て英語で入力されている国名が「Japan」であるデータを削除
				 */
				// DataCleansingForMesagoUtil.removeEnglishJapanData(userdata);
				// 2014.4.17 お客様要望により廃止

				/*
				 * 2. メアドと住所の「以下住所」に対する妥当性検証
				 */
				DataCleansingForMesagoUtil
						.validateCompletenessDataForEmailAndAddr(userdata);

				/*
				 * 3. 業種区分が「学生」でかつ会社名が空欄であるデータを削除
				 *
				 * 4. 業種区分が「学生(BN14）」で、社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」
				 * または未選択であるデータを削除
				 */
				DataCleansingForMesagoUtil.removeStudentData(userdata);

				/*
				 * 5. 会社名が空欄でかつ業種区分が「その他」または未選択であるデータを削除
				 */
				DataCleansingForMesagoUtil
						.removeDataForEmptyCompanyName(userdata);

				/*
				 * 6. 会社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」または未選択であるデータを削除
				 */
				DataCleansingForMesagoUtil
						.removeDataForGarbageSchoolData(userdata);

				/*
				 * 7. NG会社名を持つデータを削除
				 */
				DataCleansingForMesagoUtil
						.removeDataWithNgCompanyName(userdata);

				/*
				 * 8.「以下住所」中の全てのスペース(半角・全角問わず)文字を削除
				 */
				DataCleansingForMesagoUtil.removeAllSpaceFromAddr3(userdata);

			}
			DataCleansingForMesagoUtil.saveDB(conn, allFirstData); // DB保存

			/*
			 * 重複氏名＋メールアドレスデータの除去
			 */
			List<String> duplicateList = DataCleansingForMesagoUtil
					.searchDuplicateNameAndEmail(conn); // 重複氏名＋メールアドレスのリスト
			for (String nameAndEmail : duplicateList) {
				try {
					DataCleansingForMesagoUtil
							.setDuplicateFlgForSameNameAndEmailOrTelOrAddr3(
									conn, nameAndEmail, null, null);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			/*
			 * 重複氏名＋電話番号データの除去
			 */
			List<String> duplicateList2 = DataCleansingForMesagoUtil
					.searchDuplicateNameAndTel(conn); // 重複氏名＋電話番号のリスト
			for (String nameAndTel : duplicateList2) {
				try {
					DataCleansingForMesagoUtil
							.setDuplicateFlgForSameNameAndEmailOrTelOrAddr3(
									conn, null, nameAndTel, null);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			/*
			 * 重複氏名＋以下住所データの除去
			 */
			List<String> duplicateList3 = DataCleansingForMesagoUtil
					.searchDuplicateNameAndAddr3(conn); // 重複氏名＋以下住所のリスト
			for (String nameAndAddr3 : duplicateList3) {
				try {
					DataCleansingForMesagoUtil
							.setDuplicateFlgForSameNameAndEmailOrTelOrAddr3(
									conn, null, null, nameAndAddr3);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
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

		// JSPにフォワード
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/result.jsp");
		dispatcher.forward(request, response);
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
}