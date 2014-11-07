package jp.co.freedom.master.utilities.omron;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OMRON用定数定義クラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class OmronConstants {

	/** オムロングループ会社の会社名略称 */
	public static Map<String, String> OC_COMPANY_SHORTNAME = createOcCompanyShortname();

	/** オムロングループ会社の会社名略称 */
	public static Map<String, String> OCG_COMPANY_SHORTNAME = createOcgCompanyShortname();

	/** その他の会社名略称 */
	public static Map<String, String> OTHER_COMPANY_SHORTNAME = createOtherCompanyShortname();

	/**
	 * オムロン本体の会社名略称<b>Map</b>の初期化
	 * 
	 * @return　オムロン本体の会社名略称<b>Map</b>
	 */
	private static Map<String, String> createOcCompanyShortname() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("IAB", "IAB");
		map.put("EMC", "EMC");
		map.put("HCB", "HCB");
		map.put("AEC", "AEC");
		map.put("SSB", "SSB");
		return map;
	}

	/**
	 * オムロングループ会社の会社名略称<b>Map</b>の初期化
	 * 
	 * @return　オムロングループ会社の会社名略称<b>Map</b>
	 */
	private static Map<String, String> createOcgCompanyShortname() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("OHQ", "OHQ");
		map.put("OAE", "OAE");
		map.put("OSS", "OSS");
		map.put("OFE", "OFE");
		map.put("OSK", "OSK");
		map.put("OKS", "OKS");
		map.put("OHK", "OHK");
		map.put("OAM", "OAM");
		map.put("OBA", "OBA");
		map.put("OFJ", "OFJ");
		map.put("(太)京", "(太)京");
		map.put("OSLC", "OSLC");
		map.put("TAS", "TAS");
		map.put("OFS", "OFS");
		map.put("OCM", "OCM");
		map.put("ONA", "ONA");
		map.put("KIP", "KIP");
		map.put("HRI", "HRI");
		map.put("FAT", "FAT");
		map.put("オムロンヘルスケア", "OHQ");
		map.put("オムロンオートモーティブエレクトロニクス", "OAE");
		map.put("オムロンソーシアルソリューションズ", "OSS");
		map.put("オムロンフィールドエンジニアリング", "OFE");
		map.put("オムロンソフトウェア", "OSK");
		map.put("オムロン関西制御機器", "OKS");
		map.put("オムロンコーリン", "OHK");
		map.put("オムロンアミューズメント", "OAM");
		map.put("オムロンビジネスアソシエイツ", "OBA");
		map.put("オムロンファイナンス", "OFJ");
		map.put("オムロン京都太陽", "(太)京");
		map.put("オムロン住倉ロジスティック", "OSLC");
		map.put("オムロンティー・エーエス", "TAS");
		map.put("オムロンエフエーストア", "OFS");
		map.put("オムロンマーケティング", "OCM");
		map.put("オムロンネットワークアプリケーションズ", "ONA");
		map.put("オムロンクレジットサービス", "KIP");
		map.put("ヒューマンルネッサンス研究所", "HRI");
		map.put("ヒューマンルネッサンス", "HRI");
		map.put("エフ・エー・テクノ", "FAT");
		return map;
	}

	/**
	 * その他の会社名略称<b>Map</b>の初期化
	 * 
	 * @return　その他の会社名略称<b>Map</b>
	 */
	private static Map<String, String> createOtherCompanyShortname() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("オムロン労働組合", "-");
		map.put("オムロン健康保険組合", "-");
		return map;
	}

}
