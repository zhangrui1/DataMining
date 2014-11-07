package jp.co.freedom.gpenquete;

/**
 * 汎用アンケート用エラー情報
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralPurposeErrorInfo {
	/** エラー種別 */
	public String type;

	/** エラーMSG */
	public String message;

	/** エラーの発生した入力データの行番号 */
	public int nRow;

	/** エラーの発生した入力データの列番号 */
	public int nColumn;

	/** エラーの発生した入力データ */
	public String inputRowData;

	/**
	 * コンストラクタ
	 */
	public GeneralPurposeErrorInfo() {

	}

}
