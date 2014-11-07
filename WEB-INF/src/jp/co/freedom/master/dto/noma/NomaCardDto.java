package jp.co.freedom.master.dto.noma;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.CardDto;

/**
 * 【NOMA】名刺情報DTO
 *
 * @author フリーダム・グループ
 *
 */
public class NomaCardDto extends CardDto {

	/** 通し番号 */
	public String COUNT;

	/** 【事前登録専用】 登録番号（来場事前登録） */
	public String PREENTRY_ID;

	/** 送付先 */
	public String SEND_FLG;

	/** 登録日時 */
	public String REGIST_DATE;

	/** プレス媒体名 */
	public String V_P_NAME;

	/** プレス媒体区分 */
	public String V_P_NAME_KUBUN;

	/** プレス業種区分 */
	public String V_P_BIZ;

	/** プレス業種区分コード */
	public String V_P_BIZ_CODE;

	/** プレス職種区分 */
	public String V_P_OCC;

	/** プレス職種区分コード */
	public String V_P_OCC_CODE;

	/* 2次データ専用 ここから↓↓　 */

	/** 編集後の会社名 */
	public String V_CORP_UPDATE;

	/** 事前登録フラグ */
	public String V_PREENTRY;

	/** 招待状フラグ */
	public String V_INVITATION;

	/** VIP招待券フラグ */
	public String V_VIP;

	/** 当日登録フラグ */
	public String V_APPOINTEDDAY;

	/** TEL番号(国番号) */
	public String V_TEL_CNT;

	/** TEL番号(市外局番) */
	public String V_TEL_AREA;

	/** TEL番号(市内局番-番号) */
	public String V_TEL_LOCAL;

	/** TEL番号(内線) */
	public String V_TEL_EXT;

	/** FAX番号(国番号) */
	public String V_FAX_CNT;

	/** FAX番号(市外局番) */
	public String V_FAX_AREA;

	/** FAX番号(市内局番-番号) */
	public String V_FAX_LOCAL;

	/** (検索用) 氏名 + メールアドレス */
	public String V_NAME_EMAIL;

	/** (検索用) 氏名 + 電話番号 */
	public String V_NAME_TEL;

	/** (検索用) 氏名 + 以下住所 */
	public String V_NAME_ADDR3;

	/** (検索用) 全ての住所情報 */
	public String ADDR_ALL;

	/** 原票状況種別 */
	public String V_TICKET_TYPE;

	// /** [デバッグ用]JPG画像パス */
	// public String V_IMAGE_PATH;

	/*
	 * [備忘]事前登録の国名はnullである場合には「日本」と解釈する必要があり、アンマッチでnullであるのか
	 * 「日本」という意味でのnullであるのか識別できないため専用フラグを用意して識別する
	 */
	/** アンマッチフラグ */
	public boolean unmatching;

	/* 2次データ用専用 ここまで↑↑ */

	/** コンストラクタ */
	public NomaCardDto() {
		super();
	}

	/**
	 * 環境依存文字を含むかどうかの検証
	 *
	 * @return 検証結果のブール値
	 */
	public boolean contailsModelDependence() {
		boolean CORP = StringUtil.containsModelDependence(this.V_CORP);
		boolean CORP_UPDATE = StringUtil
				.containsModelDependence(this.V_CORP_UPDATE);
		boolean DEPT1 = StringUtil.containsModelDependence(this.V_DEPT1);
		boolean DEPT2 = StringUtil.containsModelDependence(this.V_DEPT2);
		boolean DEPT3 = StringUtil.containsModelDependence(this.V_DEPT3);
		boolean DEPT4 = StringUtil.containsModelDependence(this.V_DEPT4);
		boolean BIZ1 = StringUtil.containsModelDependence(this.V_BIZ1);
		boolean BIZ2 = StringUtil.containsModelDependence(this.V_BIZ2);
		boolean BIZ3 = StringUtil.containsModelDependence(this.V_BIZ3);
		boolean BIZ4 = StringUtil.containsModelDependence(this.V_BIZ4);
		boolean PREFIX = StringUtil.containsModelDependence(this.V_TITLE);
		boolean NAME1 = StringUtil.containsModelDependence(this.V_NAME1);
		boolean NAME2 = StringUtil.containsModelDependence(this.V_NAME2);
		boolean TEL = StringUtil.containsModelDependence(this.V_TEL);
		boolean FAX = StringUtil.containsModelDependence(this.V_FAX);
		boolean EMAIL = StringUtil.containsModelDependence(this.V_EMAIL);
		boolean URL = StringUtil.containsModelDependence(this.V_URL);
		boolean COUNTRY = StringUtil.containsModelDependence(this.V_COUNTRY);
		boolean ZIP = StringUtil.containsModelDependence(this.V_ZIP);
		boolean ADDR1 = StringUtil.containsModelDependence(this.V_ADDR1);
		boolean ADDR2 = StringUtil.containsModelDependence(this.V_ADDR2);
		boolean ADDR3 = StringUtil.containsModelDependence(this.V_ADDR3);
		boolean ADDR4 = StringUtil.containsModelDependence(this.V_ADDR4);
		return CORP || CORP_UPDATE || DEPT1 || DEPT2 || DEPT3 || DEPT4 || BIZ1
				|| BIZ2 || BIZ3 || BIZ4 || PREFIX || NAME1 || NAME2 || TEL
				|| FAX || EMAIL || URL || COUNTRY || ZIP || ADDR1 || ADDR2
				|| ADDR3 || ADDR4;
	}
}
