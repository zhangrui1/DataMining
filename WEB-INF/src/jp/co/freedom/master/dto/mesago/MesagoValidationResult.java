package jp.co.freedom.master.dto.mesago;

import jp.co.freedom.master.dto.QuestionDto;

/**
 * 【MESAGO】クレンジング結果DTO
 *
 * @author フリーダム・グループ
 *
 */
public class MesagoValidationResult extends QuestionDto {

	/* 会社名に「営業」「工場」「支社」「事業」「（地名）店」「（地名）支店」を含む単語は「所属」に移動する */
	/** チェック2の結果 */
	public boolean check2Result;

	/** 住所情報の妥当性検証結果 */
	public boolean checkAddrResult;

	/** 英語住所でかつ国名が「Japan」である */
	public boolean qualifiedRemoveConditions1;

	/** メールアドレスとADDR3が不完全である */
	public boolean qualifiedRemoveConditions2;

	/** 業種区分が「学生(BN14）」かつ会社名が空欄である */
	public boolean qualifiedRemoveConditions3;

	/** 業種区分が「学生(BN14）」で、社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」または未選択 */
	public boolean qualifiedRemoveConditions4;

	/** 社名が空欄で、且つ業種区分が「その他」または未選択 */
	public boolean qualifiedRemoveConditions5;

	/** 社名に「大学」「専門学校」「学校法人」「（学）」を含み、「役職」の選択が「その他」または未選択 */
	public boolean qualifiedRemoveConditions6;

	/** 社名にNGワードを含む */
	public boolean qualifiedRemoveConditions7;

	/** 住所情報が不完全である */
	public boolean qualifiedRemoveConditions8;

	/** 環境依存文字を含む */
	public boolean containsModelDependenceCharacter;

	/** コンストラクタ */
	public MesagoValidationResult() {
		super();
	}
}
