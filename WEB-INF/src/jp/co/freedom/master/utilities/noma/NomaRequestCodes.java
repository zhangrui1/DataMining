package jp.co.freedom.master.utilities.noma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOMA向けリクエストコード表クラス
 *
 * @author フリーダム・グループ
 *
 */
public class NomaRequestCodes {

	/* リクエストコード対応表 NOMA2014用 */
	/** J・グリーン株式会社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_GREEN = createRequestCodesMapForGreen();

	/** (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けリクエストコード */
	public static Map<String, String> REQUESTCODES_REKISEN = createRequestCodesMapForRekisen();

	/** (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けリクエストコード */
	public static Map<String, String> REQUESTCODES_OHSAKI = createRequestCodesMapForOhsaki();

	/** (株)栄光社向けリクエストコード */
	public static Map<String, String> REQUESTCODES_EIKOU = createRequestCodesMapForEikou();

	/** (株)医用工学研究所向けリクエストコード */
	public static Map<String, String> REQUESTCODES_IYOU = createRequestCodesMapForIyou();

	/** (株)ワコム向けリクエストコード */
	public static Map<String, String> REQUESTCODES_WACOM = createRequestCodesMapForWacom();

	/** (株)マーキュリー向けリクエストコード */
	public static Map<String, String> REQUESTCODES_MAR = createRequestCodesMapForMar();

	/** (株)カナミックネットワーク向けリクエストコード */
	public static Map<String, String> REQUESTCODES_KANAMIC = createRequestCodesMapForKanamic();

	/** インフォコム(株)向けリクエストコード */
	public static Map<String, String> REQUESTCODES_INFOCOM = createRequestCodesMapForInfocom();

	/** 日立向けリクエストコード */
	public static Map<String, String> REQUESTCODES_HITACHI = createRequestCodesMapForHitachi();

	/* リーダー端末対応リスト NOMA2014用 */
	/** J・グリーン株式会社向けバーコードリーダーリスト */
	public static List<String> READER_GREEN = createReaderForGreen();

	/** (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けバーコードリーダーリスト */
	public static List<String> READER_REKISEN = createReaderForRekisen();

	/** (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けバーコードリーダーリスト */
	public static List<String> READER_OHSAKI = createReaderForOhsaki();

	/** (株)栄光社向けバーコードリーダーリスト */
	public static List<String> READER_EIKOU = createReaderForEikou();

	/** (株)医用工学研究所向けバーコードリーダーリスト */
	public static List<String> READER_IYOU = createReaderForIyou();

	/** (株)ワコム向けバーコードリーダーリスト */
	public static List<String> READER_WACOM = createReaderForWacom();

	/** (株)マーキュリー向けバーコードリーダーリスト */
	public static List<String> READER_MAR = createReaderForMar();

	/** (株)カナミックネットワーク向けバーコードリーダーリスト */
	public static List<String> READER_KANAMIC = createReaderForKanamic();

	/** インフォコム(株)向けバーコードリーダーリスト */
	public static List<String> READER_INFOCOM = createReaderForInfocom();

	/** 日立向けバーコードリーダーリスト */
	public static List<String> READER_HITACHI = createReaderForHitachi();

	/**
	 * J・グリーン株式会社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return J・グリーン株式会社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForGreen() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000004", "北海道、病院向けの提案をしている");
		requests.put("000007", "介護施設90床　テラス緑化3階 10㎡ 目黒区");
		requests.put("000008", "官公庁、病院、介護公報関連");
		requests.put("000010", "病院関連・マット");
		requests.put("000011", "さいたま赤十字病院　リハビリテーション化（建て替え)");
		requests.put("000012", "さいたま赤十字病院　リハビリテーション化（建て替え)");
		requests.put("000013", "札幌市の内科病院 屋上緑化");
		return requests;
	}

	/**
	 * (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForRekisen() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "資料希望");
		requests.put("000002", "連絡希望");
		requests.put("000003", "説明希望");
		requests.put("000004", "デモ希望");
		requests.put("000005", "来場のみ");
		requests.put("000018", "シリアル");
		requests.put("000019", "新生児");
		requests.put("000020", "徘徊");
		return requests;
	}

	/**
	 * (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForOhsaki() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "ALICE_NEO デモンストレーション依頼");
		requests.put("000002", "ALICE_NEO 営業訪問依頼");
		requests.put("000003", "ALICE_NEO 見積（概算）依頼");
		requests.put("000004", "ALICE_NEO 情報収集");
		requests.put("000005", "ALICE_NEO その他");
		requests.put("000006", "精神科医療機関 その他");
		requests.put("000007", "OMSB デモンストレーション依頼");
		requests.put("000008", "OMSB 見積（概算）依頼");
		requests.put("000009", "fMaintainer デモンストレーション依頼");
		requests.put("000010", "fMaintainer 見積（概算）依頼");
		requests.put("000011", "OMSB fMaintainer 発展商談");
		requests.put("000012", "OCE その他の医療ソリューション 訪問依頼");
		requests.put("000013", "OCE その他の医療ソリューション 見積依頼");
		requests.put("000014", "メディブレイン 担当商談");
		requests.put("000016", "1");
		requests.put("000017", "2");
		requests.put("000018", "3");
		requests.put("000019", "4");
		requests.put("000020", "5");
		return requests;
	}

	/**
	 * (株)栄光社向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)栄光社向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForEikou() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "エフマチック に関心");
		requests.put("000002", "シートクリーナー に関心");
		requests.put("000003", "その他製品 に関心");
		requests.put("000006", "カタログが欲しい");
		requests.put("000007", "モニター希望 エフマチック");
		requests.put("000008", "モニター希望 シートクリーナー");
		requests.put("000009", "他社 使用中");
		requests.put("000010", "話が聞きたい");
		requests.put("000011", "後日 栄光社が連絡");
		requests.put("000013", "後日 代理店が連絡");
		requests.put("000016", "北島");
		requests.put("000017", "池田");
		requests.put("000018", "中谷");
		requests.put("000019", "南");
		requests.put("000020", "森下");
		return requests;
	}

	/**
	 * (株)医用工学研究所向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)医用工学研究所向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForIyou() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "苅屋");
		requests.put("000002", "高木");
		requests.put("000003", "富永");
		requests.put("000004", "細野");
		requests.put("000005", "早川");
		requests.put("000006", "松本");
		requests.put("000007", "降旗");
		requests.put("000008", "内藤");
		requests.put("000009", "北岡");
		requests.put("000010", "吉田");
		return requests;
	}

	/**
	 * (株)ワコム向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)ワコム向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForWacom() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "経営・管理部門（理事長・院長・副院長・事務長・部長他）");
		requests.put("000002", "診療部門（医長・医師他）");
		requests.put("000003", "診療技術部門（薬剤・検査・放射線・リハビリ・営業管理他）");
		requests.put("000004", "看護部門（病棟・手術・外来・中材・教育他）");
		requests.put("000005", "事務部門（医事・庶務人事・用度施設・会計経理・企画他）");
		requests.put("000006", "医療情報システム部門");
		requests.put("000007", "調剤薬局");
		requests.put("000008", "病院・クリニックその他（ソーシャルワーカー・コンサルタント他）");
		requests.put("000011", "病院・クリニック関係者（医療従事者）");
		requests.put("000012", "保健・福祉施設（老健施設・老人ホームなど）");
		requests.put("000013", "在宅サービス");
		requests.put("000014", "建築設計・施工関係");
		requests.put("000015", "医療関連メーカー");
		requests.put("000016", "医療関連ディーラー");
		requests.put("000017", "官公庁・団体・教育機関");
		requests.put("000018", "一般");
		requests.put("000019", "学生");
		return requests;
	}

	/**
	 * (株)マーキュリー向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)マーキュリー向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForMar() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "人事担当");
		requests.put("000002", "広報担当");
		requests.put("000003", "名刺交換");
		requests.put("000004", "資料配布");
		requests.put("000005", "商談");
		requests.put("000006", "訪問依頼");
		requests.put("000007", "資料送付");
		requests.put("000008", "見積作成");
		requests.put("000009", "中村");
		requests.put("000010", "木村");
		requests.put("000011", "並木");
		requests.put("000012", "松井");
		requests.put("000013", "水野");
		requests.put("000014", "近江");
		requests.put("000015", "飯島");
		requests.put("000016", "大藪");
		requests.put("000017", "和泉");
		requests.put("000018", "太田");
		requests.put("000019", "人見");
		requests.put("000020", "後追い要");
		return requests;
	}

	/**
	 * (株)カナミックネットワーク向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return (株)カナミックネットワーク向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForKanamic() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "医療関係の方");
		requests.put("000002", "介護関係の方");
		requests.put("000004", "A");
		requests.put("000005", "B");
		requests.put("000006", "C");
		requests.put("000011", "東京営業所管轄");
		requests.put("000012", "名古屋営業所管轄");
		requests.put("000013", "大阪営業所管轄");
		requests.put("000014", "福岡営業所管轄");
		return requests;
	}

	/**
	 * インフォコム(株)向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return インフォコム(株)向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForInfocom() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "レベルS");
		requests.put("000002", "レベルA");
		requests.put("000003", "レベルB");
		requests.put("000004", "安否システム未導入");
		requests.put("000005", "安否システム導入済");
		requests.put("000006", "システム導入を検討している");
		requests.put("000007", "システムリプレイスを検討している");
		requests.put("000008", "導入先：セコム");
		requests.put("000009", "導入先：Ncom");
		requests.put("000010", "導入先：富士通");
		requests.put("000011", "導入先：その他");
		requests.put("000012", "資料送付希望");
		requests.put("000013", "訪問説明希望");
		requests.put("000014", "3か月以内に導入検討");
		requests.put("000015", "本年度中に導入検討");
		requests.put("000016", "導入時期未定");
		requests.put("000017", "100名以下");
		requests.put("000018", "100～499名");
		requests.put("000019", "500～2000名");
		requests.put("000020", "2001名以上");
		requests.put("000024", "RS");
		requests.put("000025", "RW");
		requests.put("000026", "RT");
		requests.put("000027", "QA");
		requests.put("000028", "OT");
		requests.put("000029", "IA");
		requests.put("000030", "eFilm");
		requests.put("000031", "LINUX");
		requests.put("000032", "FlowInsight");
		requests.put("000033", "その他");
		requests.put("000045", "資料送付");
		requests.put("000046", "デモ依頼");
		requests.put("000047", "見積依頼");
		requests.put("000048", "製品（問診票）");
		requests.put("000049", "製品（オリエンテーション、同意書）");
		requests.put("000050", "製品（その他）");
		requests.put("000051", "タブレット端末導入状況：iPad導入済");
		requests.put("000052", "タブレット端末導入状況：iPad以外 導入済");
		requests.put("000053", "タブレット端末導入状況：計画中");
		requests.put("000054", "タブレット端末導入状況：導入予定なし");
		requests.put("000055", "電子カルテ：MegaOakHR（NEC)");
		requests.put("000056", "電子カルテ：MIRAIs(CSIなど）");
		requests.put("000057", "電子カルテ：HOPE EGMAIN（富士通）");
		requests.put("000058", "電子カルテ：e-カルテ（SSI）");
		requests.put("000059", "電子カルテ：その他");
		requests.put("000060", "電子カルテ：未導入");
		requests.put("000065", "製品（CWS 看護部門管理システム 勤務管理）");
		requests.put("000066", "製品（CWS 看護部門管理システム 教育支援）");
		requests.put("000067", "製品（CWS 就業管理システム）");
		requests.put("000068", "製品（Medi-UNITE チーム医療）");
		requests.put("000069", "製品（Medi-UNITE 看護管理日誌）");
		requests.put("000071", "資料請求");
		requests.put("000072", "デモ依頼");
		requests.put("000073", "連絡");
		requests.put("000075", "時期（2014年度）");
		requests.put("000076", "時期（2015年度）");
		requests.put("000077", "時期（2016年度以降）");
		requests.put("000085", "電子カルテベンダー（NEC）");
		requests.put("000086", "電子カルテベンダー（富士通）");
		requests.put("000087", "電子カルテベンダー（IBM）");
		requests.put("000088", "電子カルテベンダー（MIRAIS）");
		requests.put("000089", "電子カルテベンダー（ほか）");
		requests.put("000091", "担当者（津田）");
		requests.put("000092", "担当者（小野）");
		requests.put("000093", "担当者（吉田）");
		requests.put("000094", "担当者（岡）");
		requests.put("000105", "資料送付");
		requests.put("000106", "デモ依頼");
		requests.put("000107", "見積依頼");
		requests.put("000108", "貸出");
		requests.put("000109", "その他（コメント参照）");
		requests.put("000111", "HiSMRT99/DMAT（救急災害映像伝送）");
		requests.put("000112", "HiSMRT-Eye（遠隔診断支援システム）");
		requests.put("000113", "HiSMRT-VIEW（e-learning）");
		requests.put("000114", "映像アーカイブシステム");
		requests.put("000115", "その他製品");
		requests.put("000117", "福重");
		requests.put("000118", "宮内");
		requests.put("000119", "鈴木");
		requests.put("000120", "山下");
		requests.put("000124", "製品：ORCHID");
		requests.put("000125", "製品：SRS");
		requests.put("000126", "担当：澤田");
		requests.put("000127", "担当：冨本");
		requests.put("000128", "担当：渡井");
		requests.put("000129", "担当：引尻");
		requests.put("000130", "電子カルテ：MegaOakHR（NEC)");
		requests.put("000131", "電子カルテ：MIRAIｓ（CSIなど）");
		requests.put("000132", "電子カルテ：HOPE　EGMAIN（富士通）");
		requests.put("000133", "電子カルテ：e-カルテ（SSI)");
		requests.put("000134", "電子カルテ：その他");
		requests.put("000135", "生体情報モニタ：日本光電");
		requests.put("000136", "生体情報モニタ：オムロンコーリン");
		requests.put("000137", "生体情報モニタ：GEヘルスケア");
		requests.put("000138", "生体情報モニタ：フィリップス");
		requests.put("000139", "生体情報モニタ：フクダ電子");
		requests.put("000140", "生体情報モニタ：その他");
		requests.put("000144", "保存画像：術野カメラ");
		requests.put("000145", "保存画像：室内カメラ");
		requests.put("000146", "保存画像：医療機器");
		requests.put("000147", "保存画像：その他");
		requests.put("000148", "手術室：～2室");
		requests.put("000149", "手術室：3室");
		requests.put("000150", "手術室：4室");
		requests.put("000151", "手術室：5室");
		requests.put("000152", "手術室：6室");
		requests.put("000153", "手術室：7室");
		requests.put("000154", "手術室：8室");
		requests.put("000155", "手術室：9室");
		requests.put("000156", "手術室：10室～");
		requests.put("000157", "資料請求");
		requests.put("000158", "製品デモ");
		requests.put("000159", "見積もり");
		requests.put("000160", "その他");
		requests.put("000164", "既存ユーザ");
		requests.put("000165", "対応：萩田");
		requests.put("000166", "対応：中野");
		requests.put("000167", "対応：井上");
		requests.put("000168", "対応：塚本");
		requests.put("000169", "対応：虎谷");
		requests.put("000170", "対応：その他");
		requests.put("000171", "デモ調整");
		requests.put("000172", "資料送付");
		requests.put("000173", "PICS WEB");
		requests.put("000174", "PICS KS");
		requests.put("000175", "DICS");
		requests.put("000176", "DICS-PS");
		requests.put("000177", "J-Reporter");
		requests.put("000178", "Ward Meister");
		requests.put("000179", "CT Meister");
		requests.put("000180", "DMEntry");
		requests.put("000181", "既存カルテ：EGMAIN-GX");
		requests.put("000182", "既存カルテ：EGMAIN-LX");
		requests.put("000183", "既存カルテ：MegaOakHR（NEC）");
		requests.put("000184", "既存カルテ：MI・RA・Is/PX（CSI,NJC,ICC）");
		requests.put("000185", "既存カルテ：CIS（IBM)");
		requests.put("000186", "既存カルテ：e-カルテ（SSI）");
		requests.put("000187", "既存カルテ：その他（エクリュ,KAI,Happy 等）");
		requests.put("000188", "既存調剤：TOSHO");
		requests.put("000189", "既存調剤：YUYAMA");
		requests.put("000190", "既存調剤：その他（高園,小西 等）");
		requests.put("000191", "時期カルテ：EGMAIN-GX");
		requests.put("000192", "時期カルテ：EGMAIN-LX");
		requests.put("000193", "時期カルテ：MegaOakHR（NEC）");
		requests.put("000194", "時期カルテ：MI・RA・Is/PX（CSI,NJC,ICC）");
		requests.put("000195", "時期カルテ：CIS（IBM)");
		requests.put("000196", "時期カルテ：e-カルテ（SSI）");
		requests.put("000197", "時期カルテ：その他（エクリュ,KAI,Happy 等）");
		requests.put("000198", "時期調剤：TOSHO");
		requests.put("000199", "時期調剤：YUYAMA");
		requests.put("000200", "時期調剤：その他（高園,小西 等）");
		requests.put("000204", "製品（Medi-Bank 退院サマリ/診療情報管理/がん登録）");
		requests.put("000205", "製品（Medi-Bank/DPC DPC決定支援・様式1）");
		requests.put("000206", "製品（DPC-Management DPC分析）");
		requests.put("000207", "製品（Medi-UNITE 医療文書/診断書作成）");
		requests.put("000208", "製品（Medi-UNITE 紙文書スキャン管理/マトリクスビュー）");
		requests.put("000209", "製品（Medi-UNITE チーム医療管理）");
		requests.put("000210", "製品（Medi-UNITE 地域連携/医療相談）");
		requests.put("000211", "資料請求");
		requests.put("000212", "デモ依頼");
		requests.put("000213", "見積");
		requests.put("000215", "電子カルテベンダー（NEC）");
		requests.put("000216", "電子カルテベンダー（富士通）");
		requests.put("000217", "電子カルテベンダー（IBM）");
		requests.put("000218", "電子カルテベンダー（MI・RA・Is）");
		requests.put("000219", "電子カルテベンダー（ほか）");
		requests.put("000220", "医事システムのみ");
		return requests;
	}

	/**
	 * 日立向けリクエストコード<b>Map</b>の初期化
	 *
	 * @return 日立向けリクエストコード<b>Map</b>
	 */
	private static Map<String, String> createRequestCodesMapForHitachi() {
		Map<String, String> requests = new HashMap<String, String>();
		requests.put("000001", "HIHOPS-HR（大～中規模病院向け電子カルテ）");
		requests.put("000002", "Open-Karte（中～小規模病院向け電子カルテ）");
		requests.put("000003", "Open-Karte AD（有床診療所～小規模病院向け電子カルテ）※新商品");
		requests.put("000004", "Hi-SEED AS（診療所向け電子カルテ）");
		requests.put("000005", "WeVIEW Z-dition（PACS）");
		requests.put("000006", "べてらん君collaboration（レセプト院内審査支援システム）");
		requests.put("000007", "歯科べてらん君（レセプト院内審査支援システム）");
		requests.put("000008", "ヘルゼア　ネクスト（健診業務トータルサポートシステム）");
		requests.put("000009", "ヘルゼア　ウィリング（特定保健指導業務支援システム）");
		requests.put("000010", "メタボ・ジャッジ（生活習慣病リスクシュミレーション）");
		requests.put("000011", "はらすまダイエット（クラウド型健康支援サービス）");
		requests.put("000012", "指整脈認証管理システム");
		requests.put("000013", "デジタルペンソリューション");
		requests.put("000014", "病院情報みえる化ソリューション");
		requests.put("000015", "スマートアナリシス MI(Medical Intelligence)");
		requests.put("000016", "Lavolute8");
		requests.put("000017", "Hi-Scene 地域包括ケア支援");
		requests.put("000018", "ヘルスケアデータ統合プラットフォーム");
		requests.put("000019", "あなたの健康を見守る”セルフヘルスケア”システム");
		requests.put("000020", "地域医療ネットワーク");
		requests.put("000021", "電子カルテシステム");
		requests.put("000022", "医用画像に関するシステム");
		requests.put("000023", "レセプトチェックに関するシステム");
		requests.put("000024", "健康・保健指導に関するシステム");
		requests.put("000025", "セキュリティに関するシステム");
		requests.put("000026", "経営支援に関するシステム");
		requests.put("000027", "地域医療に関するシステム");
		requests.put("000028", "介護に関するシステム");
		requests.put("000029", "医事会計に関するシステム");
		requests.put("000030", "データ二次活用に関するシステム");
		requests.put("000031", "2014年度");
		requests.put("000032", "2015年度");
		requests.put("000033", "2016年度");
		requests.put("000034", "2017年度");
		requests.put("000035", "2018年度以降");
		requests.put("000038", "その他（別紙）");
		requests.put("000040", "キャンセル（一つ前）");
		return requests;
	}

	/**
	 * J・グリーン株式会社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return J・グリーン株式会社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForGreen() {
		List<String> reader = new ArrayList<String>();
		reader.add("065");
		return reader;
	}

	/**
	 * (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (有)礫川ｼｽﾃﾑﾃﾞｻﾞｲﾝ事務所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForRekisen() {
		List<String> reader = new ArrayList<String>();
		reader.add("052");
		return reader;
	}

	/**
	 * (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)大﨑ｺﾝﾋﾟｭｰﾀｴﾝﾁﾞﾆｱﾘﾝｸﾞ向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForOhsaki() {
		List<String> reader = new ArrayList<String>();
		reader.add("013");
		return reader;
	}

	/**
	 * (株)栄光社向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)栄光社向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForEikou() {
		List<String> reader = new ArrayList<String>();
		reader.add("050");
		return reader;
	}

	/**
	 * (株)医用工学研究所向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)医用工学研究所向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForIyou() {
		List<String> reader = new ArrayList<String>();
		reader.add("002");
		reader.add("003");
		return reader;
	}

	/**
	 * (株)ワコム向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)ワコム向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForWacom() {
		List<String> reader = new ArrayList<String>();
		reader.add("009");
		reader.add("010");
		reader.add("011");
		reader.add("012");
		return reader;
	}

	/**
	 * (株)マーキュリー向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)マーキュリー向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForMar() {
		List<String> reader = new ArrayList<String>();
		reader.add("047");
		reader.add("048");
		reader.add("049");
		return reader;
	}

	/**
	 * (株)カナミックネットワーク向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return (株)カナミックネットワーク向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForKanamic() {
		List<String> reader = new ArrayList<String>();
		reader.add("046");
		return reader;
	}

	/**
	 * インフォコム(株)向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return インフォコム(株)向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForInfocom() {
		List<String> reader = new ArrayList<String>();
		for (int nIndex = 53; nIndex <= 64; nIndex++) {
			reader.add("0" + String.valueOf(nIndex));
		}
		return reader;
	}

	/**
	 * 日立向けバーコードリーダー端末対応リストの初期化
	 *
	 * @return 日立向けバーコードリーダー端末対応<b>List</b>
	 */
	private static List<String> createReaderForHitachi() {
		List<String> reader = new ArrayList<String>();
		for (int nIndex = 25; nIndex <= 45; nIndex++) {
			reader.add("0" + String.valueOf(nIndex));
		}
		return reader;
	}
}