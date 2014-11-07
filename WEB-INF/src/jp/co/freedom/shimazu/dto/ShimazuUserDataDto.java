package jp.co.freedom.shimazu.dto;

/**
 * Shimazu向けユーザー情報保持クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ShimazuUserDataDto {

	/** イベント名 */
	public String event;

	/** 来場日 */
	public String day;

	/** 名刺情報 */
	public ShimazuCardForEnqueteDto cardInfo;

	/** 用件票番号 */
	public String orderId;

	/** 用件記入担当名 */
	public String orderPerson;

	/** ご説明機種 */
	public String referenceModel;

	/** ご説明装置 */
	public String referenceModelName;

	/** ご用件 */
	public String order;

	/** 設備導入検討調査フラグ */
	public String implementationPlanFlg;

	/** 設備導入予定年 */
	public String implementationYear;

	/** 設備導入予定月 */
	public String implementationMonth;

	/** 設備導入検討調査内容 */
	public String implementationResearchDetail;

	/** 情報収集フラグ */
	public String researchFlg;

	/** 情報収集内容 */
	public String researchDetail;

	/** 島津製品ユーザーフラグ */
	public String shimazuUserFlg;

	/** 島津製品ユーザー内容 */
	public String shimazuUserDetail;

	/** 営業訪問必要・不要 */
	public String follow;

	/** 訪問希望期日 月 */
	public String followMonth;

	/** 訪問希望期日 日 */
	public String followDay;

	/** 渡し済みカタログ */
	public String sendCatalog;

	/** 渡し済みカタログ（手書き） */
	public String sendCatalogOther;

	/** カタログ後送希望 */
	public String wantCatalog;

	/** カタログ後送希望（手書き） */
	public String wantCatalogOther;

	/** コンストラクタ */
	public ShimazuUserDataDto() {
	}
}
