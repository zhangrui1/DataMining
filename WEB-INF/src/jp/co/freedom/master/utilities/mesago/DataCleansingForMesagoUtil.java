package jp.co.freedom.master.utilities.mesago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.freedom.common.utilities.DateUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.CardDto;
import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoQuestionDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;

/**
 * 【MESAGO】データクレンジング用ユーティリティ
 *
 * @author フリーダム・グループ
 *
 */
public class DataCleansingForMesagoUtil {

	/**
	 * JDBCプラグインJARのロード
	 */
	public static void loadJdbcPluginJar() {
		// JDBCドライバーのロード
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 全ての1次データを取得
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param table
	 *            テーブル名
	 * @param condition
	 *            WHERE句
	 * @param restoreTelFaxFlg
	 *            TEL/FAX番号の復旧フラグ
	 * @return 全ての1次データ
	 * @throws SQLException
	 */
	public static List<MesagoUserDataDto> getData(Connection conn,
			String table, String condition, boolean restoreTelFaxFlg) {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql;
			if (StringUtil.isNotEmpty(condition)) {
				sql = StringUtil.concat("SELECT * FROM", table, condition);
			} else {
				sql = StringUtil.concat("SELECT * FROM", table);
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (StringUtil.isNotEmpty(condition)) {
					String duplicate = rs.getString("duplicate");
					String remove1 = rs.getString("remove1");
					String remove2 = rs.getString("remove2");
					String remove3 = rs.getString("remove3");
					String remove4 = rs.getString("remove4");
					String remove5 = rs.getString("remove5");
					String remove6 = rs.getString("remove6");
					String remove7 = rs.getString("remove7");
					String remove8 = rs.getString("remove8");
					if (mergeRemoveFlg(duplicate, remove1, remove2, remove3,
							remove4, remove5, remove6, remove7, remove8)) {
						continue;
					}
				}
				MesagoUserDataDto userdata = new MesagoUserDataDto();
				MesagoCardDto cardInfo = new MesagoCardDto(); // 名刺情報DTO
				MesagoQuestionDto questionInfo = new MesagoQuestionDto(); // アンケート情報DTO

				/* 登録日時、各種フラグ */
				cardInfo.COUNT = rs.getString("V_ID"); // 通し番号
				cardInfo.V_DAY = removeWhiteSpace(rs.getString("V_DAY")); // 来場日
				cardInfo.PREENTRY_ID = removeWhiteSpace(rs
						.getString("V_REGIST")); // 登録番号
				cardInfo.V_VID = removeWhiteSpace(rs.getString("V_VID")); // バーコード番号
				userdata.id = cardInfo.V_VID;
				cardInfo.V_PREENTRY = removeWhiteSpace(rs.getString("V_PRE")); // 事前登録フラグ
				cardInfo.V_INVITATION = removeWhiteSpace(rs
						.getString("V_SYOTAI")); // 招待状フラグ
				cardInfo.V_VIP = removeWhiteSpace(rs.getString("V_VIP")); // VIP招待券フラグ
				cardInfo.V_APPOINTEDDAY = removeWhiteSpace(rs
						.getString("V_APP")); // 当日登録フラグ

				/* 名刺情報 */
				cardInfo.V_CORP = removeWhiteSpace(rs.getString("V_CORP")); // 会社名
				cardInfo.V_CORP_UPDATE = cardInfo.V_CORP; // 会社名（編集用）
				cardInfo.V_DEPT1 = removeWhiteSpace(rs.getString("V_DEPT")); // 部署
				cardInfo.V_BIZ1 = removeWhiteSpace(rs.getString("V_BIZ")); // 役職
				cardInfo.V_TITLE = removeWhiteSpace(rs.getString("V_TITLE")); // 敬称
				cardInfo.V_NAME1 = removeWhiteSpace(rs.getString("V_NAME1")); // 氏名姓漢字
				cardInfo.V_NAME2 = removeWhiteSpace(rs.getString("V_NAME2")); // 氏名名漢字
				cardInfo.V_TEL_CNT = removeWhiteSpace(rs.getString("V_TEL_CNT")); // 電話番号(国番号)
				cardInfo.V_TEL_AREA = removeWhiteSpace(rs
						.getString("V_TEL_AREA")); // 電話番号(市外局番)
				cardInfo.V_TEL_LOCAL = removeWhiteSpace(rs
						.getString("V_TEL_LOCAL")); // 電話番号(市内局番-番号)
				cardInfo.V_TEL_EXT = removeWhiteSpace(rs.getString("V_TEL_EXT")); // 電話番号(内線)
				if (restoreTelFaxFlg) {
					String tel = StringUtil.concatWithDelimit("-",
							cardInfo.V_TEL_CNT, cardInfo.V_TEL_AREA,
							cardInfo.V_TEL_LOCAL);
					if (StringUtil.isNotEmpty(cardInfo.V_TEL_EXT)) {
						tel = tel + "(" + cardInfo.V_TEL_EXT + ")";
					}
					cardInfo.V_TEL = tel;
				} else {
					cardInfo.V_TEL = removeWhiteSpace(rs.getString("V_TEL")); // 電話番号
				}

				cardInfo.V_FAX_CNT = removeWhiteSpace(rs.getString("V_FAX_CNT")); // FAX番号(国番号)
				cardInfo.V_FAX_AREA = removeWhiteSpace(rs
						.getString("V_FAX_AREA")); // FAX番号(市外局番)
				cardInfo.V_FAX_LOCAL = removeWhiteSpace(rs
						.getString("V_FAX_LOCAL")); // FAX番号(市内局番-番号)
				if (restoreTelFaxFlg) {
					String fax = StringUtil.concatWithDelimit("-",
							cardInfo.V_FAX_CNT, cardInfo.V_FAX_AREA,
							cardInfo.V_FAX_LOCAL);
					cardInfo.V_FAX = fax;
				} else {
					cardInfo.V_FAX = removeWhiteSpace(rs.getString("V_FAX")); // FAX番号
				}

				cardInfo.V_EMAIL = removeWhiteSpace(rs.getString("V_EMAIL")); // Email
				cardInfo.V_NAME_EMAIL = StringUtil.concat(cardInfo.V_NAME1,
						cardInfo.V_NAME2, cardInfo.V_EMAIL); // 氏名＋メールアドレス
				cardInfo.V_NAME_TEL = StringUtil.concat(cardInfo.V_NAME1,
						cardInfo.V_NAME2, cardInfo.V_TEL); // 氏名＋電話番号
				cardInfo.V_URL = removeWhiteSpace(rs.getString("V_WEB")); // Web
				cardInfo.SEND_FLG = removeWhiteSpace(rs.getString("V_SEND")); // 送付先
				cardInfo.V_COUNTRY = removeWhiteSpace(rs.getString("V_COUNTRY")); // 国名
				cardInfo.V_ZIP = removeWhiteSpace(rs.getString("V_ZIP")); // 郵便番号
				cardInfo.V_ADDR1 = removeWhiteSpace(rs.getString("V_ADDR1")); // 都道府県
				cardInfo.V_ADDR2 = removeWhiteSpace(rs.getString("V_ADDR2")); // 市区部
				cardInfo.V_ADDR3 = removeWhiteSpace(rs.getString("V_ADDR3")); // 以下住所
				cardInfo.V_ADDR4 = removeWhiteSpace(rs.getString("V_ADDR4")); // ビル名
				cardInfo.V_NAME_ADDR3 = StringUtil.concat(cardInfo.V_NAME1,
						cardInfo.V_NAME2, cardInfo.V_ADDR3); // 氏名＋以下住所
				cardInfo.ADDR_ALL = StringUtil.concat(cardInfo.V_ADDR1,
						cardInfo.V_ADDR2, cardInfo.V_ADDR3, cardInfo.V_ADDR4); // 全ての住所情報（検索用）

				/* プレス関連情報 */
				cardInfo.V_P_NAME = removeWhiteSpace(rs.getString("V_P_NAME")); // プレス媒体名
				cardInfo.V_P_NAME_KUBUN = removeWhiteSpace(rs
						.getString("V_P_NAMEKUBUN")); // プレス媒体区分
				cardInfo.V_P_BIZ = removeWhiteSpace(rs.getString("V_P_BIZ")); // プレス業種区分
				cardInfo.V_P_BIZ_CODE = removeWhiteSpace(rs
						.getString("V_P_BIZCODE")); // プレス業種区分コード
				cardInfo.V_P_OCC = removeWhiteSpace(rs.getString("V_P_OCC")); // プレス職種区分
				cardInfo.V_P_OCC_CODE = removeWhiteSpace(rs
						.getString("V_P_OCCCODE")); // プレス職種区分コード

				/* アンケート情報 */
				questionInfo.V_Q1 = removeWhiteSpace(rs.getString("V_Q1")); // 業種番号
				questionInfo.V_Q1_kubun = removeWhiteSpace(rs
						.getString("V_Q1_KUBUN")); // 業種区分
				questionInfo.V_Q1_other = removeWhiteSpace(rs
						.getString("V_Q1_FA")); // 業種区分(その他FA)
				questionInfo.V_Q1_code = removeWhiteSpace(rs
						.getString("V_Q1_CODE")); // 業種区分(コード)
				questionInfo.V_Q2 = removeWhiteSpace(rs.getString("V_Q2")); // 専門分野
				questionInfo.V_Q2_code = removeWhiteSpace(rs
						.getString("V_Q2_CODE")); // 専門分野(コード)
				questionInfo.V_Q3 = removeWhiteSpace(rs.getString("V_Q3")); // 職種番号
				questionInfo.V_Q3_kubun = removeWhiteSpace(rs
						.getString("V_Q3_KUBUN")); // 職種区分
				questionInfo.V_Q3_other = removeWhiteSpace(rs
						.getString("V_Q3_FA")); // 職種(その他FA)
				questionInfo.V_Q3_code = removeWhiteSpace(rs
						.getString("V_Q3_CODE")); // 職種(コード)
				questionInfo.V_Q4 = removeWhiteSpace(rs.getString("V_Q4")); // 役職
				questionInfo.V_Q4_kubun = removeWhiteSpace(rs
						.getString("V_Q4_KUBUN")); // 役職区分
				questionInfo.V_Q4_other = removeWhiteSpace(rs
						.getString("V_Q4_FA")); // 役職(その他FA)
				questionInfo.V_Q4_code = removeWhiteSpace(rs
						.getString("V_Q4_CODE")); // 役職(コード)
				questionInfo.V_Q5 = removeWhiteSpace(rs.getString("V_Q5")); // 従業員数
				questionInfo.V_Q5_kubun = removeWhiteSpace(rs
						.getString("V_Q5_KUBUN")); // 従業員数区分
				questionInfo.V_Q5_code = removeWhiteSpace(rs
						.getString("V_Q5_CODE")); // 従業員数(コード)
				questionInfo.V_Q6 = removeWhiteSpace(rs.getString("V_Q6")); // 買付け決定権
				questionInfo.V_Q7 = removeWhiteSpace(rs.getString("V_Q7")); // 来場動機
				questionInfo.V_Q7_other = removeWhiteSpace(rs
						.getString("V_Q7_FA")); // 来場動機(その他FA)
				questionInfo.V_Q8 = removeWhiteSpace(rs.getString("V_Q8")); // 出展検討
				cardInfo.REGIST_DATE = removeWhiteSpace(rs
						.getString("V_REGIST_DATE")); // 登録日時

				// 仕分用
				cardInfo.V_TICKET_TYPE = rs.getString("V_TICKET_TYPE"); // 原票状況種別
				cardInfo.V_IMAGE_PATH = rs.getString("V_PATH"); // 画像パス

				/* 来場フラグの特定 */
				if ("first".equals(table)) {
					List<String> list = new LinkedList<String>();
					for (int nDay = 1; nDay <= MesagoConfig.DAYS.length; nDay++) {
						String visitDay = removeWhiteSpace(rs.getString("V_DAY"
								+ String.valueOf(nDay))); // 来場フラグ
						list.add(visitDay);
					}
					userdata.visitor = mergeVisitFlg(list);
				}

				userdata.questionInfo = questionInfo;
				userdata.cardInfo = cardInfo;
				userDataList.add(userdata);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userDataList;
	}

	/**
	 * 来場フラグのマージ
	 *
	 * @param visitFlg
	 *            複数の来場フラグ
	 * @return ブール型の来場フラグ
	 */
	private static boolean mergeVisitFlg(List<String> visitFlg) {
		for (String value : visitFlg) {
			if ("T".equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 会社名に「●」が含まれている場合に住所が完全一致する別データより会社名を修復
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @param allDataList
	 *            全ての1次データ
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto repairWildcardForCompanyName(
			MesagoUserDataDto userdata, List<MesagoUserDataDto> allDataList) {
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE;
		if (StringUtil.isNotEmpty(companyName)
				&& StringUtil.find(companyName, "●")) {
			String addr = StringUtil.concat(userdata.cardInfo.V_ADDR1,
					userdata.cardInfo.V_ADDR2, userdata.cardInfo.V_ADDR3,
					userdata.cardInfo.V_ADDR4);
			if (StringUtil.isNotEmpty(addr)) {
				for (MesagoUserDataDto master : allDataList) {
					String masterAddr = StringUtil.concat(
							master.cardInfo.V_ADDR1, master.cardInfo.V_ADDR2,
							master.cardInfo.V_ADDR3, master.cardInfo.V_ADDR4);
					if (addr.equals(masterAddr) && !StringUtil.find(addr, "●")) {
						((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE = master.cardInfo.V_CORP;
					}
				}
			}
		}
		return userdata;
	}

	/**
	 * 会社名がNGワードを含んでいるか否かの検証
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto searchCompanyNameContainsNGKeyword(
			MesagoUserDataDto userdata) {
		String keywords[] = { "営業", "工場", "支社", "事業", "店", "支店" };
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		if (StringUtil.isNotEmpty(companyName)) {
			for (String keyword : keywords) {
				if (StringUtil.find(companyName, keyword)) {
					userdata.result.check2Result = true;
					break;
				}
			}
		}
		return userdata;
	}

	/**
	 * NGワードを含む会社名を削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeCompanyNameContainsNGKeyword(
			MesagoUserDataDto userdata) {
		String keywords[] = { "個人", "なし", "なし」", "「なし", "「なし」", "無し", "「無し",
				"無し」", "「無し」", "ナシ", "「ナシ", "ナシ」", "「ナシ」", "無職", "自営", "フリー",
				"準備中", "個人エステ", "個人事業", "自営業", "一般", "無", "-", "自宅", "自宅サロン",
				"自宅ネイルサロン", "自宅エステ", "個人サロン", "個人経営", "個人事業主" };
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		if (StringUtil.isNotEmpty(companyName)) {
			companyName = DataCleansingForMesagoUtil
					.convertHalfWidthString(companyName); // 半角文字列に変換
			companyName = StringUtil.replace(companyName, "　|\\s", ""); // 全半角スペース除去
			if (StringUtil.isNotEmpty(companyName)) {
				for (String keyword : keywords) {
					if (keyword.equals(companyName)) {
//						((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE = null;
						//2015/2/21 大阪の件から修正
						((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE = "NO COMPANY NAME";
						break;
					}
				}
			}
		}
		return userdata;
	}

	/**
	 * 社名と個人名が完全一致する場合は社名を削除(個人での登録のため)
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeCompanyEqualsName(
			MesagoUserDataDto userdata) {
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		String personName = userdata.cardInfo.V_NAME1;
		if (StringUtil.isNotEmpty(companyName)
				&& StringUtil.isNotEmpty(personName)
				&& companyName.equals(personName)) {
			((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE = ""; // 会社名
		}
		return userdata;
	}

	/**
	 * 法人格の前後のホワイトスペースを除去
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeWhiteSpaceOnCompany(
			MesagoUserDataDto userdata) {
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		if (StringUtil.isNotEmpty(companyName)) {
			companyName = StringUtil.convertHalfWidthString(companyName, false); // 半角文字列に変換
			for (String shortName : MesagoConstants.COMPANY_SHORTNAME_WITHOUT_BRACKET) {
				companyName = StringUtil.replace(companyName, "(　|\\s)+\\("
						+ shortName + "\\)", "(" + shortName + ")");
				companyName = StringUtil.replace(companyName, "\\(" + shortName
						+ "\\)(　|\\s)+", "(" + shortName + ")");
			}
			companyName = StringUtil.replace(companyName, "^(　|\\s)+", "");// 先頭の空白文字削除
			companyName = StringUtil.replace(companyName, "(　|\\s)+$", "");// 末尾の空白文字削除
			companyName = StringUtil.replace(companyName, "\\(", "（");
			companyName = StringUtil.replace(companyName, "\\)", "）");
			companyName = MesagoUtil.normalizeCompanyName(companyName); // 法人格の略称表記の括弧が半角に誤って変換することを考慮しての”保険”処理
		}
		((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE = companyName;
		return userdata;
	}

	/**
	 * 海外住所は国名を除きAddress2に集約
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto moveAddr2ForOverseaAddrInfo(
			MesagoUserDataDto userdata) {
		if (isOversea(userdata)) {
			CardDto dto = userdata.cardInfo;
			String all = StringUtil.concat(dto.V_ADDR1, dto.V_ADDR2,
					dto.V_ADDR3, dto.V_ADDR4);
			dto.V_ADDR1 = null;
			dto.V_ADDR2 = null;
			dto.V_ADDR3 = all;
			dto.V_ADDR4 = null;
		}
		return userdata;
	}

	/**
	 * 特定国名がAddress2の末尾に含まれる場合は削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeAddr2LastCountryNameForOverseaAddrInfo(
			MesagoUserDataDto userdata) {
		if (isOversea(userdata)) {
			CardDto dto = userdata.cardInfo;
			String addr3 = dto.V_ADDR3;
			if (StringUtil.isNotEmpty(addr3)) {
				addr3 = StringUtil.convertHalfWidthString(addr3, false); // 半角文字変換
				addr3 = addr3.toLowerCase(); // 小文字変換
				if (addr3.endsWith(dto.V_COUNTRY)) { // 国名列に記載された国名が末尾に含まれている場合
					String country = StringUtil.convertHalfWidthString(
							dto.V_COUNTRY, false);
					country = country.toLowerCase();
					int index = addr3.lastIndexOf(country);
					dto.V_ADDR3 = removeWhiteSpace(dto.V_ADDR3.substring(0,
							index));
				} else { // 特定国名が末尾に含まれてる場合
					final String[] NG_COUNTRY_LIST = { "h.k", "taiwan (r.o.c)",
							"taiwan,r.o.c.", "taiwan r.o.c.", "taiwan(r.o.c)",
							"taiwan(r.o.c.)", "rep. of korea", "korea" };
					for (String ngCountry : NG_COUNTRY_LIST) {
						if (addr3.endsWith(ngCountry)) {
							int index = addr3.lastIndexOf(ngCountry);
							dto.V_ADDR3 = removeWhiteSpace(dto.V_ADDR3
									.substring(0, index));
							if (StringUtil.isNotEmpty(dto.V_ADDR3)) {
								String lastString = dto.V_ADDR3
										.substring(dto.V_ADDR3.length() - 1);
								if (",".equals(lastString)
										|| ".".equals(lastString)) {
									dto.V_ADDR3 = dto.V_ADDR3.substring(0,
											dto.V_ADDR3.length() - 1);
								}
							}
							break;
						}
					}
				}
			}

		}
		return userdata;
	}

	/**
	 * 「以下住所」に「●」が含まれる場合は、「社名」と「電話番号」の完全一致の場合はデータを修正する
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @param allFirstData
	 *            全ての1次データ
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto compairAddr3(MesagoUserDataDto userdata,
			List<MesagoUserDataDto> allFirstData) {
		CardDto dto = userdata.cardInfo;
		String addr3 = dto.V_ADDR3;
		if (StringUtil.isNotEmpty(addr3) && StringUtil.find(addr3, "●")) {
			String companayName = ((MesagoCardDto) dto).V_CORP_UPDATE;
			String tel = dto.V_TEL;
			if (StringUtil.isNotEmpty(companayName)
					&& StringUtil.isNotEmpty(tel)) {
				for (MesagoUserDataDto tmpUserdata : allFirstData) {
					String tmpCompanyName = ((MesagoCardDto) tmpUserdata.cardInfo).V_CORP_UPDATE;
					String tmpTel = tmpUserdata.cardInfo.V_TEL;
					if (StringUtil.isNotEmpty(tmpCompanyName)
							&& StringUtil.isNotEmpty(tmpTel)
							&& companayName.equals(tmpCompanyName)
							&& tel.equals(tmpTel)
							&& StringUtil
									.isNotEmpty(tmpUserdata.cardInfo.V_ADDR3)
							&& !StringUtil.find(tmpUserdata.cardInfo.V_ADDR3,
									"●")) {
						userdata.cardInfo.V_ADDR3 = tmpUserdata.cardInfo.V_ADDR3;
						((MesagoCardDto) userdata.cardInfo).V_NAME_ADDR3 = StringUtil
								.concat(dto.V_NAME1, dto.V_NAME2, dto.V_ADDR3); // 氏名＋以下住所
						((MesagoCardDto) userdata.cardInfo).ADDR_ALL = StringUtil
								.concat(dto.V_ADDR1, dto.V_ADDR2, dto.V_ADDR3,
										dto.V_ADDR4); // 全ての住所情報（検索用）
						break;
					}
				}
			}
		}
		return userdata;
	}

	/**
	 * 10.上記クレンジングの結果でも住所に「●」が含まれる場合、「メールアドレス」が完全であれば住所に関連するセルを削除し
	 * データ自体は残す。「メールアドレス」が不完全であればデータ自体を削除する。
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto finalCleaningForAddressInfo(
			MesagoUserDataDto userdata) {
		MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
		if (StringUtil.isNotEmpty(cardInfo.ADDR_ALL)
				&& StringUtil.find(cardInfo.ADDR_ALL, "●")) {
			if (StringUtil.isNotEmpty(cardInfo.V_EMAIL)
					&& StringUtil.isEmailAddress(cardInfo.V_EMAIL)) {
				cardInfo.V_ADDR1 = null;
				cardInfo.V_ADDR2 = null;
				cardInfo.V_ADDR3 = null;
				cardInfo.V_ADDR4 = null;
				cardInfo.ADDR_ALL = null;
			} else {
				userdata.result.qualifiedRemoveConditions8 = true;
			}

		}
		return userdata;
	}

	/**
	 * Addr2,Addr3,Addr4の妥当性検証
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 */
	public static void validateAddressInfo(MesagoUserDataDto userdata) {
		if (!isOversea(userdata)) {
			String addr2 = userdata.cardInfo.V_ADDR2; // 市区町村
			String addr3 = userdata.cardInfo.V_ADDR3; // 以下住所
			String addr4 = userdata.cardInfo.V_ADDR4; // ビル名
			/* 全半角スペース除去 */
			addr2 = StringUtil.replace(addr2, "　|\\s", "");
			addr3 = StringUtil.replace(addr3, "　|\\s", "");
			addr4 = StringUtil.replace(addr4, "　|\\s", "");
			/*
			 * 「市区町村」に対する妥当性検証
			 */
			if (StringUtil.isNotEmpty(addr2)
					&& StringUtil.find(addr2, "[0-9０-９]+")) { // 数字文字列を含む場合
				userdata.result.checkAddrResult = true;
			}
			/*
			 * 「以下住所」に対する妥当性検証
			 */
			// if (StringUtil.isNotEmpty(addr3)) {
			// if (StringUtil.find(addr3, "^[0-9０-９]+.*$")) { // 数字文字列から始まる場合
			// userdata.result.checkAddrResult = true;
			// }
			// // 「以下住所」にビル名を含む場合
			// final String[] NGWORDS = { "ビル", "マンション", "アパート", "住宅" };
			// for (String ngword : NGWORDS) {
			// if (StringUtil.find(addr3, ngword)) {
			// userdata.result.checkAddrResult = true;
			// }
			// }
			// }
			/*
			 * 「ビル名」に対する妥当性検証
			 */
			if (StringUtil.isNotEmpty(addr4)) {
				if (StringUtil.find(addr4, "^[0-9０-９]+.*$")) { // 数字文字列から始まる場合
					userdata.result.checkAddrResult = true;
				}
				// 「ビル名」に丁目・番地を含む場合
				final String[] NGWORDS = { "丁", "丁目", "番地" };
				for (String ngword : NGWORDS) {
					if (StringUtil.find(addr4, ngword)) {
						userdata.result.checkAddrResult = true;
					}
				}
			}
		}
	}

	/**
	 * 以下住所に含まれる全ての全半角スペース文字を削除。数字と数字の間のスペース文字は削除しない。
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeAllSpaceFromAddr3(
			MesagoUserDataDto userdata) {
		if (!isOversea(userdata)) {
			String addr3 = userdata.cardInfo.V_ADDR3; // 以下住所
			addr3 = StringUtil.replace(addr3, "[　|\\s]+", " ");
			if (StringUtil.isNotEmpty(addr3)) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < addr3.length(); i++) {
					char c = addr3.charAt(i);
					if (' ' == c || '　' == c) {
						if (i != 0) {
							char previous = ' ';
							if (i > 0) {
								previous = addr3.charAt(i - 1);
							}
							char next = ' ';
							if (i < addr3.length() - 1) {
								next = addr3.charAt(i + 1);
							}
							boolean previousFlg = StringUtil.isNotEmpty(String
									.valueOf(previous))
									&& StringUtil.find(
											String.valueOf(previous),
											"[0-9０-９]");
							boolean nextFlg = StringUtil.isNotEmpty(String
									.valueOf(next))
									&& StringUtil.find(String.valueOf(next),
											"[0-9０-９]");
							if (previousFlg && nextFlg) {
								sb.append(c);
							}
						}
					} else {
						sb.append(c);
					}
				}
				userdata.cardInfo.V_ADDR3 = sb.toString();
				MesagoCardDto dto = (MesagoCardDto) userdata.cardInfo;
				((MesagoCardDto) userdata.cardInfo).V_NAME_ADDR3 = StringUtil
						.concat(dto.V_NAME1, dto.V_NAME2, dto.V_ADDR3); // 氏名＋以下住所
				((MesagoCardDto) userdata.cardInfo).ADDR_ALL = StringUtil
						.concat(dto.V_ADDR1, dto.V_ADDR2, dto.V_ADDR3,
								dto.V_ADDR4); // 全ての住所情報（検索用）
			}
		}
		return userdata;
	}

	/**
	 * 海外住所フラグ
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return 海外住所であるか否かのブール値
	 */
	private static boolean isOversea(MesagoUserDataDto userdata) {
		return !"Japan".equals(userdata.cardInfo.V_COUNTRY);
	}

	/**
	 * 氏名が空値である場合には「見本市ご担当者様」に置換する
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto replaceEmptyName(MesagoUserDataDto userdata) {
		if (StringUtil.isEmpty(userdata.cardInfo.V_NAME1)
				&& StringUtil.isEmpty(userdata.cardInfo.V_NAME2)) {
			boolean japan = "Japan".equals(userdata.cardInfo.V_COUNTRY);
			if (japan) {
				userdata.cardInfo.V_NAME1 = "見本市ご担当者";
			} else {
				userdata.cardInfo.V_NAME1 = "To Whom It May Concern";
			}
		}
		return userdata;
	}

	/**
	 * 不正なTEL番号／FAX番号を削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeGarbageTelFaxNumber(
			MesagoUserDataDto userdata) {
		String tel = StringUtil.convertHalfWidthString(userdata.cardInfo.V_TEL,
				false); // 半角文字列に変換
		tel = StringUtil.replace(tel, "　|\\s", ""); // 全半角スペース除去
		if (StringUtil.isNotEmpty(tel)) {
			userdata.cardInfo.V_TEL = removeGarbageTelFaxNumber(tel); // TEL番号全体
			if (StringUtil.isEmpty(userdata.cardInfo.V_TEL)) {
				((MesagoCardDto) userdata.cardInfo).V_TEL_CNT = ""; // TEL国番号
				((MesagoCardDto) userdata.cardInfo).V_TEL_AREA = ""; // TEL市外局番
				((MesagoCardDto) userdata.cardInfo).V_TEL_LOCAL = ""; // TEL市内局番-番号
				((MesagoCardDto) userdata.cardInfo).V_TEL_EXT = ""; // TEL内線
			}
		}
		String fax = StringUtil.convertHalfWidthString(userdata.cardInfo.V_FAX,
				false); // 半角文字列に変換
		fax = StringUtil.replace(fax, "　|\\s", ""); // 全半角スペース除去
		if (StringUtil.isNotEmpty(fax)) {
			userdata.cardInfo.V_FAX = removeGarbageTelFaxNumber(fax); // FAX番号全体
			if (StringUtil.isEmpty(userdata.cardInfo.V_FAX)) {
				((MesagoCardDto) userdata.cardInfo).V_FAX_CNT = ""; // FAX国番号
				((MesagoCardDto) userdata.cardInfo).V_FAX_AREA = ""; // FAX市外局番
				((MesagoCardDto) userdata.cardInfo).V_FAX_LOCAL = ""; // FAX市内局番-番号
			}
		}
		return userdata;
	}

	/**
	 * 不正文字の除去
	 *
	 * 0もしくは-のみで構成される文字列である場合はnullを返却。それ以外は何もせず指定文字列を返却。
	 *
	 * @param value
	 *            指定文字列
	 * @return 処理後の文字列
	 */
	private static String removeGarbageTelFaxNumber(String value) {
		if (StringUtil.isNotEmpty(value)) {
			value = StringUtil.convertHalfWidthString(value, false); // 半角文字列に変換
			value = StringUtil.replace(value, "　|\\s", ""); // 全半角スペース除去
			if (StringUtil.isNotEmpty(value)) {
				if (!StringUtil.find(value, "[^0-]")) {
					return null;
				}
				if (StringUtil.find(value, "●")) {
					return null;
				}
				// 【例】81-000-000-000の場合
				String tmpValue = value;
				int index = tmpValue.indexOf("-");
				if (-1 != index) {
					tmpValue = tmpValue.substring(index + 1);
				}
				if (StringUtil.isNotEmpty(tmpValue)) {
					if (!StringUtil.find(tmpValue, "[^0-]")) {
						return null;
					}
				}
			}
		}
		return value;
	}

	/**
	 * 不正なメールアドレスを削除
	 *
	 * 「@」前に「●」が含まれる場合は削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeGarbageEmail(
			MesagoUserDataDto userdata) {
		String email = StringUtil.convertHalfWidthString(
				userdata.cardInfo.V_EMAIL, false); // 半角文字列に変換
		email = StringUtil.replace(email, "　|\\s", ""); // 全半角スペース除去
		if (StringUtil.isNotEmpty(email)) {
			int index = email.indexOf("@");
			if (index != -1) {
				if (StringUtil.find(email.substring(0, index), "●")) {
					userdata.cardInfo.V_EMAIL = null;
					((MesagoCardDto) userdata.cardInfo).V_NAME_EMAIL = userdata.cardInfo.V_NAME1; // 検索用データ更新
				}
			}
		}
		return userdata;
	}

	/**
	 * 不正なメールアドレスを削除
	 *
	 * 「@」以降に「●」が含まれる場合、「会社名」「住所」が完全一致するデータがある場合はデータを修復する。ない場合はセルデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @param allFirstData
	 *            全ての1次データ
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeGarbageEmail2(
			MesagoUserDataDto userdata, List<MesagoUserDataDto> allFirstData) {
		String email = StringUtil.convertHalfWidthString(
				userdata.cardInfo.V_EMAIL, false); // 半角文字列に変換
		email = StringUtil.replace(email, "　|\\s", ""); // 全半角スペース除去
		if (StringUtil.isNotEmpty(email)) {
			int index = email.indexOf("@");
			if (index != -1) {
				if (StringUtil.find(email.substring(index + 1, email.length()),
						"●")) {
					boolean match = false;
					String allAddrInfo = ((MesagoCardDto) userdata.cardInfo).ADDR_ALL;
					String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE;
					if (StringUtil.isNotEmpty(allAddrInfo)
							&& StringUtil.isNotEmpty(companyName)) {
						for (MesagoUserDataDto tmpUserdata : allFirstData) {
							String tmpAllAddrInfo = ((MesagoCardDto) tmpUserdata.cardInfo).ADDR_ALL;
							String tmpCompanyName = ((MesagoCardDto) tmpUserdata.cardInfo).V_CORP_UPDATE;
							if (StringUtil.isNotEmpty(tmpAllAddrInfo)
									&& StringUtil.isNotEmpty(tmpCompanyName)
									&& allAddrInfo.equals(tmpAllAddrInfo)
									&& companyName.equals(tmpCompanyName)
									&& StringUtil
											.isNotEmpty(tmpUserdata.cardInfo.V_EMAIL)
									&& !StringUtil.find(
											tmpUserdata.cardInfo.V_EMAIL, "●")) {
								userdata.cardInfo.V_EMAIL = tmpUserdata.cardInfo.V_EMAIL;
								((MesagoCardDto) userdata.cardInfo).V_NAME_EMAIL = StringUtil
										.concat(userdata.cardInfo.V_NAME1,
												userdata.cardInfo.V_EMAIL); // 検索用データ更新
								match = true;
								break;
							}
						}
					}
					if (!match) {
						userdata.cardInfo.V_EMAIL = null;
						((MesagoCardDto) userdata.cardInfo).V_NAME_EMAIL = userdata.cardInfo.V_NAME1; // 検索用データ更新
					}
				}
			}
		}
		// 妥当性検証
		if (StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& !StringUtil.isEmailAddress(userdata.cardInfo.V_EMAIL)) {
			userdata.cardInfo.V_EMAIL = null;
			((MesagoCardDto) userdata.cardInfo).V_NAME_EMAIL = userdata.cardInfo.V_NAME1; // 検索用データ更新
		}
		return userdata;
	}

	/**
	 * 不正なURLを削除
	 *
	 * 「@」が含まれる場合は削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeGarbageUrl(MesagoUserDataDto userdata) {
		String url = StringUtil.convertHalfWidthString(userdata.cardInfo.V_URL,
				false); // 半角文字列に変換
		url = StringUtil.replace(url, "　|\\s", ""); // 全半角スペース除去
		if (StringUtil.isNotEmpty(url) && StringUtil.find(url, "@")) {
			userdata.cardInfo.V_URL = null;
		}
		return userdata;
	}

	/**
	 * 不要な空白文字列を削除
	 *
	 * 1. 最後のスペース 　2. 連続スペースを1個の半角スペースに修正
	 *
	 * @param value
	 *            編集対象文字列
	 * @return 編集後の文字列
	 */
	public static String removeWhiteSpace(String value) {
		if (StringUtil.isNotEmpty(value)) {
			value = StringUtil.replace(value, "[　|\\s]+$", ""); // 最後のスペースがある場合は除去
			value = StringUtil.replace(value, "[　|\\s]+", " "); // 連続スペースを一つの半角スペースに置換
		}
		return value;
	}

	/**
	 * 全角文字列→半角文字列に変換
	 *
	 * @param str
	 *            変換対象文字列
	 * @param specialConvert
	 *            特殊文字の変換フラグ
	 * @return 半角文字列
	 */
	public static String convertHalfWidthString(String str) {
		if (StringUtil.isNotEmpty(str)) {
			final int DIFFERENCE = 'Ａ' - 'A'; // 全角アルファベットと半角アルファベットとの文字コードの差
			char[] cc = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (char c : cc) {
				char newChar = c;
				if ((('Ａ' <= c) && (c <= 'Ｚ')) || (('ａ' <= c) && (c <= 'ｚ'))
						|| (('０' <= c) && (c <= '９')) || StringUtil.is2Sign(c)) {
					// 変換対象のcharだった場合に全角文字と半角文字の差分を引く
					newChar = (char) (c - DIFFERENCE);
				} else if (isSpecial(c)) {
					newChar = specialConvert(c);
				}
				sb.append(newChar);
			}
			return sb.toString();
		}
		return str;
	}

	/**
	 * 特殊変換対象の全角記号であるか否かの検証
	 *
	 * @param pc
	 *            対象文字
	 * @return 検証結果のブール値
	 */
	private static boolean isSpecial(char pc) {
		final char[] SPECIAL = { '‐', 'ー', '―' };
		for (char c : SPECIAL) {
			if (c == pc) {
				return true;
			}
		}
		return false;
	}

	/**
	 * isSpecialで定義された特殊記号に対する変換表を定義する
	 *
	 * @param pc
	 *            対象文字
	 * @return 変換後の文字
	 */
	private static char specialConvert(char pc) {
		Map<String, String> SPECIAL = new HashMap<String, String>();
		SPECIAL.put("‐", "-");
		SPECIAL.put("ー", "-");
		SPECIAL.put("―", "-");
		String convert = SPECIAL.get(String.valueOf(pc));
		if (StringUtil.isNotEmpty(convert)) {
			return convert.charAt(0);
		}
		return pc;
	}

	/**
	 * 重複氏名＋メールアドレスの探索
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 重複氏名＋メールアドレスのリスト
	 * @throws SQLException
	 */
	public static List<String> searchDuplicateNameAndEmail(Connection conn)
			throws SQLException {
		List<String> duplicate = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM second GROUP BY V_NAME_EMAIL HAVING count(*) > 1;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String nameAndEmail = rs.getString("V_NAME_EMAIL");
			String duplicateFlg = rs.getString("duplicate");
			String remove1 = rs.getString("remove1");
			String remove2 = rs.getString("remove2");
			String remove3 = rs.getString("remove3");
			String remove4 = rs.getString("remove4");
			String remove5 = rs.getString("remove5");
			String remove6 = rs.getString("remove6");
			String remove7 = rs.getString("remove7");
			String remove8 = rs.getString("remove8");
			if (mergeRemoveFlg(duplicateFlg, remove1, remove2, remove3,
					remove4, remove5, remove6, remove7, remove8)) {
				continue;
			}
			if (StringUtil.isNotEmpty(nameAndEmail)) {
				duplicate.add(nameAndEmail);
			}
		}
		return duplicate;
	}

	/**
	 * 重複氏名＋電話番号の探索
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 重複氏名＋電話番号のリスト
	 * @throws SQLException
	 */
	public static List<String> searchDuplicateNameAndTel(Connection conn)
			throws SQLException {
		List<String> duplicate = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM second WHERE duplicate != '1' GROUP BY V_NAME_TEL HAVING count(*) > 1;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String nameAndTel = rs.getString("V_NAME_TEL");
			String duplicateFlg = rs.getString("duplicate");
			String remove1 = rs.getString("remove1");
			String remove2 = rs.getString("remove2");
			String remove3 = rs.getString("remove3");
			String remove4 = rs.getString("remove4");
			String remove5 = rs.getString("remove5");
			String remove6 = rs.getString("remove6");
			String remove7 = rs.getString("remove7");
			String remove8 = rs.getString("remove8");
			if (mergeRemoveFlg(duplicateFlg, remove1, remove2, remove3,
					remove4, remove5, remove6, remove7, remove8)) {
				continue;
			}
			if (StringUtil.isNotEmpty(nameAndTel)) {
				duplicate.add(nameAndTel);
			}
		}
		return duplicate;
	}

	/**
	 * 重複氏名＋以下住所の探索
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @return 重複氏名＋以下住所のリスト
	 * @throws SQLException
	 */
	public static List<String> searchDuplicateNameAndAddr3(Connection conn)
			throws SQLException {
		List<String> duplicate = new ArrayList<String>();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM second WHERE duplicate != '1' GROUP BY V_NAME_ADDR3 HAVING count(*) > 1;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String nameAndAddr3 = rs.getString("V_NAME_ADDR3");
			String duplicateFlg = rs.getString("duplicate");
			String remove1 = rs.getString("remove1");
			String remove2 = rs.getString("remove2");
			String remove3 = rs.getString("remove3");
			String remove4 = rs.getString("remove4");
			String remove5 = rs.getString("remove5");
			String remove6 = rs.getString("remove6");
			String remove7 = rs.getString("remove7");
			String remove8 = rs.getString("remove8");
			if (mergeRemoveFlg(duplicateFlg, remove1, remove2, remove3,
					remove4, remove5, remove6, remove7, remove8)) {
				continue;
			}
			if (StringUtil.isNotEmpty(nameAndAddr3)) {
				duplicate.add(nameAndAddr3);
			}
		}
		return duplicate;
	}

	/**
	 * 削除フラグのマージ
	 *
	 * @param removeFlgs
	 *            複数の削除フラグ
	 * @return ブール型の削除フラグ
	 */
	private static boolean mergeRemoveFlg(String... removeFlgs) {
		for (String flg : removeFlgs) {
			if ("1".equals(flg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 重複データの削除
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param nameAndEmail
	 *            探索対象の「氏名＋メールアドレス」
	 * @param nameAndTel
	 *            探索対象の「氏名＋電話番号」
	 * @param nameAndAddr3
	 *            探索対象の「氏名＋以下住所」
	 * @throws ParseException
	 */
	public static void setDuplicateFlgForSameNameAndEmailOrTelOrAddr3(
			Connection conn, String nameAndEmail, String nameAndTel,
			String nameAndAddr3) throws ParseException {
		List<String> deleteList = new ArrayList<String>(); // 削除IDリスト
		List<MesagoUserDataDto> userDataList = null;
		if (StringUtil.isNotEmpty(nameAndEmail)) {
			userDataList = getData(conn, "second", "WHERE V_NAME_EMAIL = '"
					+ nameAndEmail + "' AND duplicate!='1'", false);
		} else if (StringUtil.isNotEmpty(nameAndTel)) {
			userDataList = getData(conn, "second", "WHERE V_NAME_TEL = '"
					+ nameAndTel + "' AND duplicate!='1'", false);
		} else {
			userDataList = getData(conn, "second", "WHERE V_NAME_ADDR3 = '"
					+ nameAndAddr3 + "' AND duplicate!='1'", false);
		}
		/*
		 * 1. 来場事前登録同士のデータが重複している場合は古い登録を削除
		 */
		String preentryId = null; // フィルタ1,2を通過した事前登録ID
		Map<String, Date> preentry = new LinkedHashMap<String, Date>();
		for (MesagoUserDataDto userdata : userDataList) {
			MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
			if ("T".equals(cardInfo.V_PREENTRY)) {
				String id = cardInfo.COUNT;
				Date preentryDate = DateFormat.getDateTimeInstance().parse(
						cardInfo.REGIST_DATE);
				preentry.put(id, preentryDate);
			}
		}
		List<Map.Entry<String, Date>> mapValuesList = new ArrayList<Map.Entry<String, Date>>(
				preentry.entrySet());
		// 来場事前登録データが2件以上存在する場合は古い登録を削除リストに登録
		if (mapValuesList.size() > 1) {
			// 降順ソート
			Collections.sort(mapValuesList,
					new Comparator<Map.Entry<String, Date>>() {
						@Override
						public int compare(Entry<String, Date> entry1,
								Entry<String, Date> entry2) {
							return ((Date) entry2.getValue())
									.compareTo(((Date) entry1.getValue()));
						}
					});
			preentryId = mapValuesList.get(0).getKey();
			for (int nIndex = 1; nIndex < mapValuesList.size(); nIndex++) {
				deleteList.add(StringUtil.enquoteWith("'",
						mapValuesList.get(nIndex).getKey()));
			}
		} else if (mapValuesList.size() == 1) {
			preentryId = mapValuesList.get(0).getKey();
		}

		/*
		 * 2. 来場事前登録と当日登録が重複している場合は前者を削除
		 */
		if (StringUtil.isNotEmpty(preentryId)) {
			for (MesagoUserDataDto userdata : userDataList) {
				MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
				if (isAppointedday(cardInfo)) {
					deleteList.add(StringUtil.enquoteWith("'", preentryId));
					break;
				}
			}
		}

		/*
		 * 3. 当日登録データが重複している場合は来場日の古いデータを削除
		 */
		Map<String, Date> visited = new LinkedHashMap<String, Date>();
		for (MesagoUserDataDto userdata : userDataList) {
			MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
			if (isAppointedday(cardInfo)) {
				String id = cardInfo.COUNT;
				if (StringUtil.isNotEmpty(cardInfo.V_DAY)) {
					Date visitedDate = DateFormat.getDateTimeInstance().parse(
							cardInfo.V_DAY);
					visited.put(id, visitedDate);
				}
			}
		}
		// 当日登録データが2件以上存在する場合は古い登録を削除リストに登録
		List<Map.Entry<String, Date>> mapValuesList2 = new ArrayList<Map.Entry<String, Date>>(
				visited.entrySet());
		// 同一来場日である当日登録ユーザーリスト
		List<String> sameDayAppointeddayList = new ArrayList<String>();
		if (visited.size() > 1) {
			// 降順ソート
			Collections.sort(mapValuesList2,
					new Comparator<Map.Entry<String, Date>>() {
						@Override
						public int compare(Entry<String, Date> entry1,
								Entry<String, Date> entry2) {
							return ((Date) entry2.getValue())
									.compareTo(((Date) entry1.getValue()));
						}
					});
			Date newestDate = DateUtil.convertYMD(mapValuesList2.get(0)
					.getValue()); // 最も新しい日
			for (Map.Entry<String, Date> entry : mapValuesList2) {
				Date date = DateUtil.convertYMD(entry.getValue());
				if (0 != newestDate.compareTo(date)) {
					deleteList.add(StringUtil.enquoteWith("'", entry.getKey()));
				} else {
					sameDayAppointeddayList.add(StringUtil.enquoteWith("'",
							entry.getKey()));
				}
			}
			// TODO: 当日登録データが重複している場合に最も古い登録のみを残す場合は下記のソースを使用すること
			// // 昇順ソート
			// Collections.sort(mapValuesList2,
			// new Comparator<Map.Entry<String, Date>>() {
			// @Override
			// public int compare(Entry<String, Date> entry1,
			// Entry<String, Date> entry2) {
			// return ((Date) entry1.getValue())
			// .compareTo(((Date) entry2.getValue()));
			// }
			// });
			// for (int nIndex = 1; nIndex < mapValuesList2.size(); nIndex++) {
			// deleteList.add(StringUtil.enquoteWith("'",
			// mapValuesList2.get(nIndex).getKey()));
			// }

		}

		// 同一来場日である当日登録データが2件以上存在する場合
		if (sameDayAppointeddayList.size() > 1) {
			PreparedStatement ps1 = null;
			try {
				String sql;
				if (StringUtil.isNotEmpty(nameAndEmail)) {
					sql = "UPDATE second SET duplicate_group_id1 = ? WHERE V_ID IN ("
							+ StringUtil.concatWithDelimit(",",
									sameDayAppointeddayList) + ");";
				} else if (StringUtil.isNotEmpty(nameAndTel)) {
					sql = "UPDATE second SET duplicate_group_id2 = ? WHERE V_ID IN ("
							+ StringUtil.concatWithDelimit(",",
									sameDayAppointeddayList) + ");";
				} else {
					sql = "UPDATE second SET duplicate_group_id3 = ? WHERE V_ID IN ("
							+ StringUtil.concatWithDelimit(",",
									sameDayAppointeddayList) + ");";
				}
				ps1 = conn.prepareStatement(sql);
				String barcode = sameDayAppointeddayList.get(0);
				ps1.setString(1, barcode.substring(1, barcode.length() - 1));
				ps1.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (ps1 != null) {
					try {
						ps1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 削除フラグの更新
		if (deleteList.size() != 0) {
			PreparedStatement ps2 = null;
			try {
				String sql = "UPDATE second SET duplicate = ? WHERE V_ID IN ("
						+ StringUtil.concatWithDelimit(",", deleteList) + ");";
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, "1");
				ps2.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (ps2 != null) {
					try {
						ps2.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			// String deleteFlg = null;
			// if (StringUtil.isNotEmpty(nameAndEmail)) {
			// deleteFlg = "1";
			// } else if (StringUtil.isNotEmpty(nameAndTel)) {
			// deleteFlg = "2";
			// } else {
			// deleteFlg = "3";
			// }
			// /*
			// * duplicateフラグの取得
			// */
			// PreparedStatement ps = null;
			// String sql2 = "SELECT duplicate FROM second WHERE V_ID IN ("
			// + StringUtil.concatWithDelimit(",", deleteList) + ");";
			// String duplicate = null;
			// ResultSet rs = null;
			// try {
			// ps = conn.prepareStatement(sql2);
			// rs = ps.executeQuery();
			// while (rs.next()) {
			// duplicate = rs.getString("duplicate");
			// }
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// } finally {
			// if (rs != null) {
			// try {
			// rs.close();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// }
			// if (ps != null) {
			// try {
			// ps.close();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// /*
			// * すでにduplicateフラグが設定されている場合は更新しない
			// */
			// if (StringUtil.isEmpty(duplicate)) {
			// PreparedStatement ps2 = null;
			// try {
			// String sql = "UPDATE second SET duplicate = ? WHERE V_ID IN ("
			// + StringUtil.concatWithDelimit(",", deleteList)
			// + ");";
			// ps2 = conn.prepareStatement(sql);
			// ps2.setString(1, deleteFlg);
			// ps2.executeUpdate();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// } finally {
			// if (ps2 != null) {
			// try {
			// ps2.close();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// }
		}
	}

	/**
	 * 1. 住所が全て英語で入力されている国名が「Japan」であるデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeEnglishJapanData(
			MesagoUserDataDto userdata) {
		boolean addr1 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR1)
				&& StringUtil.isHalfWidthString(userdata.cardInfo.V_ADDR1);
		boolean addr2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2)
				&& StringUtil.isHalfWidthString(userdata.cardInfo.V_ADDR2);
		boolean addr3 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR3)
				&& StringUtil.isHalfWidthString(userdata.cardInfo.V_ADDR3);
		boolean addr4 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4)
				&& StringUtil.isHalfWidthString(userdata.cardInfo.V_ADDR4);
		boolean country = "Japan".equals(userdata.cardInfo.V_COUNTRY);
		if (addr1 && addr2 && addr3 && addr4 && country) {
			userdata.result.qualifiedRemoveConditions1 = true;
		}
		return userdata;
	}

	/**
	 * 2. メアドと住所(ADDR3)の両方が不完全であるデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto validateCompletenessDataForEmailAndAddr(
			MesagoUserDataDto userdata) {
		String addr3Value = StringUtil.convertHalfWidthString(
				userdata.cardInfo.V_ADDR3, false); // 半角文字に変換
		addr3Value = StringUtil.replace(addr3Value, " ", "");
		boolean email = StringUtil.isNotEmpty(userdata.cardInfo.V_EMAIL)
				&& StringUtil.isEmailAddress(userdata.cardInfo.V_EMAIL);
		// ADDR3が??-??である場合
		boolean addr3_1 = StringUtil.isNotEmpty(addr3Value)
				&& StringUtil
						.find(addr3Value, "[0-9-A-Za-z・#]+-[0-9A-Za-z・#]+");
		// ADDR3が??である場合
		// boolean addr3_2 = StringUtil.isNotEmpty(addr3Value)
		// && StringUtil.find(addr3Value, "[0-9-丁目班番地室部屋A-Za-z]+$")
		boolean addr3_2 = StringUtil.isNotEmpty(addr3Value)
				&& StringUtil.find(addr3Value, "[0-9一二三四五六七八九十百千]+番地");
		boolean addr3_3 = StringUtil.isNotEmpty(addr3Value)
				&& StringUtil.find(addr3Value, "[^0-9]+[0-9]+");

		/* || StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4) */;
		// // ADDR3が??であるがADDR4に記載がある場合
		// boolean addr3_2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR3)
		// && StringUtil.find(userdata.cardInfo.V_ADDR3, "[0-9-]+$")
		// && StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR4);
		boolean addr3 = addr3_1 || addr3_2 || addr3_3;

		if (!email && !addr3) {
			userdata.result.qualifiedRemoveConditions2 = true;
		}
		return userdata;
	}

	/**
	 * 3. 業種区分が「学生(BN14）」かつ会社名が空欄であるデータは削除
	 *
	 * 4. 業種区分が「学生(BN14）」で、社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」
	 * または未選択であるデータは削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeStudentData(MesagoUserDataDto userdata) {
		String bizKubun = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun;
		String bizCode = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_code;
		String positionKubun = ((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun;
		boolean biz = StringUtil.isNotEmpty(bizKubun)
				&& StringUtil.find(bizKubun, "学生") && "BN14".equals(bizCode); // 業種が「学生」である
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		boolean position = (StringUtil.isNotEmpty(positionKubun) && StringUtil
				.find(positionKubun, "その他"))
				|| StringUtil.isEmpty(positionKubun); // 役職が「その他」もしくは未選択
		if (biz && StringUtil.isEmpty(companyName)) {
			userdata.result.qualifiedRemoveConditions3 = true;
			return userdata;
		}
		if (biz && position) {
			String ngWords[] = { "大学", "専門学校", "学校法人", "（学）" };
			for (String word : ngWords) {
				if (StringUtil.find(companyName, word)) {
					userdata.result.qualifiedRemoveConditions4 = true;
					break;
				}
			}
		}
		return userdata;
	}

	/**
	 * 5. 会社名が空欄でかつ業種区分が「その他」または未選択であるデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeDataForEmptyCompanyName(
			MesagoUserDataDto userdata) {
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		String bizKubun = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun; // 業種区分
		boolean biz1 = StringUtil.isNotEmpty(bizKubun)
				&& StringUtil.find(bizKubun, "その他");
		boolean biz2 = StringUtil.isEmpty(bizKubun);
		if (StringUtil.isEmpty(companyName) && (biz1 || biz2)) {
			userdata.result.qualifiedRemoveConditions5 = true;
		}
		return userdata;
	}

	/**
	 * 6. 会社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」または未選択であるデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeDataForGarbageSchoolData(
			MesagoUserDataDto userdata) {
		String positionKubun = ((MesagoQuestionDto) userdata.questionInfo).V_Q4_kubun;
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE; // 会社名
		boolean position = (StringUtil.isNotEmpty(positionKubun) && StringUtil
				.find(positionKubun, "その他"))
				|| StringUtil.isEmpty(positionKubun); // 役職が「その他」もしくは未選択
		if (position) {
			String ngWords[] = { "大学", "専門学校", "学校法人", "（学）" };
			for (String word : ngWords) {
				if (StringUtil.isNotEmpty(companyName)
						&& StringUtil.find(companyName, word)) {
					userdata.result.qualifiedRemoveConditions6 = true;
					break;
				}
			}
		}
		return userdata;
	}

	/**
	 * 7. NG会社名を持つユーザーデータを削除
	 *
	 * @param userdata
	 *            <b>MesagoUserDataDto<b/>
	 * @return <b>MesagoUserDataDto<b/>
	 */
	public static MesagoUserDataDto removeDataWithNgCompanyName(
			MesagoUserDataDto userdata) {
		String companyName = ((MesagoCardDto) userdata.cardInfo).V_CORP_UPDATE;
		if (StringUtil.isNotEmpty(companyName)) {
			boolean exist = false;
			// 『アテックス(株)』での完全一致
			if (MesagoConstants.NG_COMPANY_NAME1.equals(companyName)) {
				exist = true;
			}
			// 登録NGワードでの部分一致
			if (!exist
					&& StringUtil.find(companyName,
							MesagoConstants.NG_COMPANY_NAMES)) {
				exist = true;
			}
			// 業種区分が「その他」、かつ『日本経済新聞社』に部分一致する場合
			if (!exist) {
				String biz = ((MesagoQuestionDto) userdata.questionInfo).V_Q1_kubun;
				if (StringUtil.isNotEmpty(biz)
						&& StringUtil.find(biz, "その他")
						&& StringUtil.find(companyName,
								MesagoConstants.NG_COMPANY_NAME2)) {
					exist = true;
				}
			}
			if (exist) {
				userdata.result.qualifiedRemoveConditions7 = true;
			}
		}
		return userdata;
	}

	/**
	 * 指定した名刺情報が当日登録データであるか否かの検証
	 *
	 * @param cardInfo
	 *            <b>MesagoCardDto</b>
	 * @return 検証結果のブール値
	 */
	private static boolean isAppointedday(MesagoCardDto cardInfo) {
		assert cardInfo != null;
		boolean invitation = "T".equals(cardInfo.V_INVITATION);
		boolean vip = "T".equals(cardInfo.V_VIP);
		boolean app = "T".equals(cardInfo.V_APPOINTEDDAY);
		return invitation || vip || app;
	}

	/**
	 * 修正データをDB保存
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param allData
	 *            修正済みのユーザーデータリスト
	 * @throws SQLException
	 */
	public static void saveDB(Connection conn,
			List<MesagoUserDataDto> allDataList) throws SQLException {
		for (MesagoUserDataDto userdata : allDataList) {
			String sql = "INSERT INTO second VALUES (";
			List<String> query = new ArrayList<String>();
			for (int nIndex = 1; nIndex <= 81; nIndex++) { // [備忘] カラム追加時はカウンタ追加
				query.add("?");
			}
			sql = sql + StringUtil.concatWithDelimit(",", query) + ");";
			PreparedStatement ps = conn.prepareStatement(sql);
			MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
			MesagoQuestionDto questionInfo = (MesagoQuestionDto) userdata.questionInfo;
			int position = 0;
			ps.setString(++position, cardInfo.COUNT);
			ps.setString(++position, userdata.visitor ? "T" : "");
			ps.setString(++position, ""); // 重複フラグ
			ps.setString(++position, ""); // 重複グループID (氏名+メールアドレス)
			ps.setString(++position, ""); // 重複グループID (氏名+電話番号)
			ps.setString(++position, ""); // 重複グループID (氏名+以下住所)
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions1 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions2 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions3 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions4 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions5 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions6 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions7 ? "1" : "");
			ps.setString(++position,
					userdata.result.qualifiedRemoveConditions8 ? "1" : "");
			ps.setString(++position, userdata.result.check2Result ? "T" : "");
			ps.setString(++position, userdata.result.checkAddrResult ? "T" : "");
			ps.setString(++position, cardInfo.V_DAY);
			ps.setString(++position, cardInfo.PREENTRY_ID);
			ps.setString(++position, cardInfo.V_VID);
			ps.setString(++position, cardInfo.V_PREENTRY);
			ps.setString(++position, cardInfo.V_INVITATION);
			ps.setString(++position, cardInfo.V_VIP);
			ps.setString(++position, cardInfo.V_APPOINTEDDAY);
			ps.setString(++position, cardInfo.V_CORP);
			ps.setString(++position, cardInfo.V_CORP_UPDATE);
			ps.setString(++position, cardInfo.V_DEPT1);
			ps.setString(++position, cardInfo.V_BIZ1);
			ps.setString(++position, cardInfo.V_TITLE);
			ps.setString(++position, cardInfo.V_NAME1);
			ps.setString(++position, cardInfo.V_NAME2);
			ps.setString(++position, cardInfo.V_NAME_TEL);
			ps.setString(++position, cardInfo.V_NAME_EMAIL);
			ps.setString(++position, cardInfo.V_NAME_ADDR3);
			ps.setString(++position, cardInfo.V_TEL);
			ps.setString(++position, cardInfo.V_TEL_CNT);
			ps.setString(++position, cardInfo.V_TEL_AREA);
			ps.setString(++position, cardInfo.V_TEL_LOCAL);
			ps.setString(++position, cardInfo.V_TEL_EXT);
			ps.setString(++position, cardInfo.V_FAX);
			ps.setString(++position, cardInfo.V_FAX_CNT);
			ps.setString(++position, cardInfo.V_FAX_AREA);
			ps.setString(++position, cardInfo.V_FAX_LOCAL);
			ps.setString(++position, cardInfo.V_EMAIL);
			ps.setString(++position, cardInfo.V_URL);
			ps.setString(++position, cardInfo.SEND_FLG);
			ps.setString(++position, cardInfo.V_COUNTRY);
			ps.setString(++position, cardInfo.V_ZIP);
			ps.setString(++position, cardInfo.V_ADDR1);
			ps.setString(++position, cardInfo.V_ADDR2);
			ps.setString(++position, cardInfo.V_ADDR3);
			ps.setString(++position, cardInfo.V_ADDR4);
			ps.setString(++position, questionInfo.V_Q1);
			ps.setString(++position, questionInfo.V_Q1_kubun);
			ps.setString(++position, questionInfo.V_Q1_other);
			ps.setString(++position, questionInfo.V_Q1_code);
			ps.setString(++position, questionInfo.V_Q2);
			ps.setString(++position, questionInfo.V_Q2_code);
			ps.setString(++position, questionInfo.V_Q3);
			ps.setString(++position, questionInfo.V_Q3_kubun);
			ps.setString(++position, questionInfo.V_Q3_other);
			ps.setString(++position, questionInfo.V_Q3_code);
			ps.setString(++position, questionInfo.V_Q4);
			ps.setString(++position, questionInfo.V_Q4_kubun);
			ps.setString(++position, questionInfo.V_Q4_other);
			ps.setString(++position, questionInfo.V_Q4_code);
			ps.setString(++position, questionInfo.V_Q5);
			ps.setString(++position, questionInfo.V_Q5_kubun);
			ps.setString(++position, questionInfo.V_Q5_code);
			ps.setString(++position, questionInfo.V_Q6);
			ps.setString(++position, questionInfo.V_Q7);
			ps.setString(++position, questionInfo.V_Q7_other);
			ps.setString(++position, questionInfo.V_Q8);
			ps.setString(++position, cardInfo.REGIST_DATE);
			ps.setString(++position, cardInfo.V_P_NAME);
			ps.setString(++position, cardInfo.V_P_NAME_KUBUN);
			ps.setString(++position, cardInfo.V_P_BIZ);
			ps.setString(++position, cardInfo.V_P_BIZ_CODE);
			ps.setString(++position, cardInfo.V_P_OCC);
			ps.setString(++position, cardInfo.V_P_OCC_CODE);
			ps.setString(++position, cardInfo.V_IMAGE_PATH);
			ps.setString(++position, cardInfo.V_TICKET_TYPE);
			ps.executeUpdate();
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 2次納品データ生成用のsecondテーブルの全レコードを削除
	 *
	 * @param conn
	 *            DBサーバーへの接続情報
	 */
	public static void initializedSecondTable(Connection conn) {
		PreparedStatement ps = null;
		try {
			String sql = "DELETE FROM second;";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}