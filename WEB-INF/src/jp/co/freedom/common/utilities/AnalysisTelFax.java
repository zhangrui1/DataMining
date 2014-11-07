package jp.co.freedom.common.utilities;

/**
 * 電話番号、FAX番号を分析／分解するクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class AnalysisTelFax {

	private boolean isForeign;

	/** 解析前データ */
	public String value;

	/** 国番号 */
	public String country;

	/** 市外局番 */
	public String areaCode;

	/** 市内局番-番号 */
	public String localCode;

	/** 内線 */
	public String extension;

	/**
	 * コンストラクタ
	 * 
	 * @param value
	 *            電話番号
	 * @param isForeign
	 *            海外住所フラグ
	 */
	public AnalysisTelFax(String value, boolean isForeign) {
		this.value = value;
		this.isForeign = isForeign;
	}

	/**
	 * コンストラクタ(JUnit用)
	 * 
	 * @param country
	 *            国コード
	 * @param areaCode
	 *            市外局番1
	 * @param localCode
	 *            市内局番＋番号
	 * @param extension
	 *            内線
	 */
	public AnalysisTelFax(String country, String areaCode, String localCode,
			String extension) {
		this.country = country;
		this.areaCode = areaCode;
		this.localCode = localCode;
		this.extension = extension;
	}

	/**
	 * 国番号／局番号／番号に分解する
	 * 
	 * 携帯番号を特別視する場合には、「市内局番-番号」にハイフン区切りで全ての番号を収録
	 * 
	 * @param giveEspecialWeightPersonalPhone
	 *            携帯番号を特別視するか否かのブール値
	 */
	public void execute(boolean giveEspecialWeightPersonalPhone) {
		if (StringUtil.isNotEmpty(this.value)) {
			// データ正規化
			String value = StringUtil.replace(this.value, "　|\\s", ""); // 全半角スペース除去
			value = StringUtil.convertHalfWidthString(value, false); // 半角文字列に変換
			// 内線番号抽出
			if (value.endsWith(")")) {
				this.extension = StringUtil.getLastMatchString(value,
						"\\((.+?)\\)");
			}
			// 内線番号除去
			if (StringUtil.isNotEmpty(this.extension)) {
				value = StringUtil.replaceLastMatch(value, "\\(.+?\\)", "");
			}
			value = StringUtil.replace(value, "\\(", "\\-"); // 内線以外の括弧を-(ハイフン)に置換
			value = StringUtil.replace(value, "\\)", "\\-"); // 内線以外の括弧を-(ハイフン)に置換
			if (value.startsWith("-")) { // 先頭のハイフンを除去
				value = value.substring(1);
			}
			value = StringUtil.replace(value, "[-]+", "-"); // 連続ハイフンを1つのハイフンに置換

			if (this.isForeign) { // 海外フラグがたっている場合
				// 先頭の+除去
				if (value.startsWith("+")) {
					value = StringUtil.replace(value, "\\+", ""); // +の除去
				}
				value = StringUtil.replace(value, "\\+", "-"); // 先頭以外の+除去
				String[] buff = value.split("-");
				if (buff.length == 1) { // ハイフンで区切られてない場合
					this.localCode = buff[0];
				} else if (buff.length == 2) {
					String tmp = buff[0];
					if (tmp.length() > 3) { // ハイフンで区切られてるが国番号は独立していない場合
						this.localCode = value;
					} else {
						this.country = buff[0]; // 国番号は正しい
						this.localCode = buff[1]; // それ以外
					}
				} else if (buff.length == 3) { // 正常パターン
					this.country = buff[0];
					this.areaCode = buff[1];
					this.localCode = buff[2]; // 市内局番＋番号はハイフンなしで格納　//TODO: 要確認
				} else if (buff.length == 4) { // 正常パターン
					this.country = buff[0];
					this.areaCode = buff[1];
					this.localCode = buff[2] + "-" + buff[3];
				} else { // ハイフンが多すぎる場合
					this.localCode = value;
				}
			} else {
				// 先頭の+除去
				if (value.startsWith("+")) {
					value = StringUtil.replace(value, "\\+", ""); // +の除去
				}
				// 携帯番号であるかどうかの判定
				if (giveEspecialWeightPersonalPhone && isPersonal(value)) {
					this.country = "81";
					if (value.startsWith("81-")) {
						value = value.substring(3);
					}
					this.localCode = value;
					return;
				}
				if (!value.startsWith("81")) {
					value = "81-" + value;
				}
				value = StringUtil.replace(value, "\\+", "-"); // 先頭以外の+除去
				String[] buff = value.split("-");
				if (buff.length == 1) { // ハイフンで区切られてない場合
					this.localCode = buff[0];
				} else if (buff.length == 2) {
					String tmp = buff[0];
					if ("81".equals(tmp)) { // 国番号は正しい
						this.country = tmp;
						this.localCode = buff[1];
					} else { // 国番号が不正
						this.localCode = value;
					}
				} else if (buff.length == 3) { // 正常パターン
					String tmp = buff[0];
					if ("81".equals(tmp)) {
						this.country = tmp;
						String localCode = buff[2];
						if (localCode.length() >= 5) { // 市内局番＋番号が5桁以上
							this.areaCode = buff[1];
							int size = localCode.length();
							this.localCode = localCode.substring(0, size - 4)
									+ "-" + localCode.substring(size - 4, size);
						} else { // 市内局番＋番号が5桁未満
							this.localCode = buff[1] + "-" + buff[2];
						}
					} else {
						this.localCode = value;
					}
				} else if (buff.length == 4) { // 正常パターン
					String tmp = buff[0];
					if ("81".equals(tmp)) { // 国番号は正しい
						this.country = tmp;
						this.areaCode = buff[1];
						this.localCode = buff[2] + "-" + buff[3];
					} else { // 国番号が不正
						this.localCode = value;
					}
				} else {
					this.localCode = value;
				}
			}
		}
	}

	/**
	 * 携帯電話番号であるか否かの判定
	 * 
	 * @param 判定対象の電話番号
	 * @return 判定結果のブール値
	 */
	private boolean isPersonal(String value) {
		if (StringUtil.isNotEmpty(value)) {
			boolean check090 = value.startsWith("090")
					|| value.startsWith("81-090");
			boolean check080 = value.startsWith("080")
					|| value.startsWith("81-080");
			boolean check070 = value.startsWith("070")
					|| value.startsWith("81-070");
			return check090 || check080 || check070;
		}
		return false;
	}

	/**
	 * オブジェクト比較
	 * 
	 * @param other
	 *            別の<b>AnalysisTelFax</b>オブジェクト
	 * @return 検証結果のブール値
	 */
	public boolean equals(AnalysisTelFax other) {
		boolean countryCheck = StringUtil.equals(this.country, other.country);
		boolean areaCodeCheck = StringUtil
				.equals(this.areaCode, other.areaCode);
		boolean localCodeCheck = StringUtil.equals(this.localCode,
				other.localCode);
		boolean extensionCheck = StringUtil.equals(this.extension,
				other.extension);
		return countryCheck && areaCodeCheck && localCodeCheck
				&& extensionCheck;
	}

}
