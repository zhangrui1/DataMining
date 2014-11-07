package jp.co.freedom.master.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザー情報保持クラス
 *
 * @author フリーダム・グループ
 *
 */
public abstract class UserDataDto {

	/** バーコードリーダーID */
	public String reader;

	/** バーコード番号 */
	public String id;

	/** 来場日（バーコードログ） */
	public String timeByRfid;

	/** 名刺情報 */
	public CardDto cardInfo;

	/** アンケート情報 */
	public QuestionDto questionInfo;

	/** リクエストID */
	public List<String> requestCode;

	/** アンケートID */
	public List<String> enqueteCode;

	/** 妥当性検証違反詳細 */
	public String validationErrResult;

	/** サブバーコード */
	public String subBarcode;

	/** コンストラクタ */
	public UserDataDto() {
		requestCode = new ArrayList<String>();
		enqueteCode = new ArrayList<String>();
	}
}
