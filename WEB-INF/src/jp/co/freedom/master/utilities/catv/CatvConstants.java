package jp.co.freedom.master.utilities.catv;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.co.freedom.master.dto.catv.CatvSeminarDto;

/**
 * CATV向け定数定義クラス
 *
 * @author フリーダム・グループ
 *
 */
public class CatvConstants {

	/** 年齢カテゴリMAP */
	final public static Map<String, String> AGE_CATEGORIES_MAP = createAgeCategoriesMap();

	/** 業種カテゴリMAP */
	final public static Map<String, String> DEPT_CATEGORIES_MAP = createDeptCategoriesMap();

	/** 職種カテゴリMAP */
	final public static Map<String, String> BIZ_CATEGORIES_MAP = createBizCategoriesMap();

	/** 役職カテゴリMAP */
	final public static Map<String, String> POS_CATEGORIES_MAP = createPosCategoriesMap();

	/** 7/29のセミナー情報 */
	final public static List<CatvSeminarDto> SEMINAR_29_INFO = createSeminar29List();

	/** 7/30のセミナー情報 */
	final public static List<CatvSeminarDto> SEMINAR_30_INFO = createSeminar30List();

	/**
	 * 年齢カテゴリ<b>Map</b>の初期化
	 *
	 * @return 年齢カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createAgeCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "10・20代");
		map.put("2", "30代");
		map.put("3", "40代");
		map.put("4", "50代");
		map.put("5", "60歳以上");
		return map;
	}

	/**
	 * 業種カテゴリ<b>Map</b>の初期化
	 *
	 * @return 業種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createDeptCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "ケーブルテレビ事業者");
		map.put("2", "ベンダー（ハード・ソフト）／工事業者");
		map.put("3", "番組サプライヤー");
		map.put("4", "一般ビジネス／その他");
		return map;
	}

	/**
	 * 職種カテゴリ<b>Map</b>の初期化
	 *
	 * @return 職種カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createBizCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "総務");
		map.put("2", "経理");
		map.put("3", "マーケティング／企画");
		map.put("4", "カスタマーサポート");
		map.put("5", "営業");
		map.put("6", "制作");
		map.put("7", "技術");
		map.put("8", "その他");
		return map;
	}

	/**
	 * 役職カテゴリ<b>Map</b>の初期化
	 *
	 * @return 役職カテゴリ<b>Map</b>
	 */
	private static Map<String, String> createPosCategoriesMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "役員クラス");
		map.put("2", "部長クラス");
		map.put("3", "課長クラス");
		map.put("4", "主任／係長／チーム長クラス");
		map.put("5", "一般社員");
		map.put("6", "その他");
		return map;
	}

	/**
	 * 7/29のセミナー情報<b>List</b>の初期化
	 *
	 * @return 7/29のセミナー情報<b>List</b>
	 */
	private static List<CatvSeminarDto> createSeminar29List() {
		List<CatvSeminarDto> list = new LinkedList<CatvSeminarDto>();
		CatvSeminarDto info = null;
		// B5-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 8:00");
		info.setEnd("2014/7/29 14:00");
		info.seminar = "B5-01";
		info.hole = "B5";
		list.add(info);
		// B5-02
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 14:00");
		info.setEnd("2014/7/29 15:15");
		info.seminar = "B5-02";
		info.hole = "B5";
		list.add(info);
		// B5-03
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 15:15");
		info.setEnd("2014/7/29 17:15");
		info.seminar = "B5-03";
		info.hole = "B5";
		list.add(info);

		// B7-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 17:15");
		info.setEnd("2014/7/29 21:00");
		info.seminar = "B7-01";
		info.hole = "B5";
		list.add(info);

		// D5-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 10:00");
		info.setEnd("2014/7/29 15:00");
		info.seminar = "D5-01";
		info.hole = "D5";
		list.add(info);
		// D5-02
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 15:00");
		info.setEnd("2014/7/29 17:00");
		info.seminar = "D5-02";
		info.hole = "D5";
		list.add(info);

		// G407-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 8:00");
		info.setEnd("2014/7/29 14:00");
		info.seminar = "G407-01";
		info.hole = "G407";
		list.add(info);
		// G407-02
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 14:00");
		info.setEnd("2014/7/29 15:30");
		info.seminar = "G407-02";
		info.hole = "G407";
		list.add(info);
		// G407-03
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 15:30");
		info.setEnd("2014/7/29 18:00");
		info.seminar = "G407-03";
		info.hole = "G407";
		list.add(info);

		// G409-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 10:00");
		info.setEnd("2014/7/29 15:00");
		info.seminar = "G409-01";
		info.hole = "G409";
		list.add(info);
		// G409-02
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 15:00");
		info.setEnd("2014/7/29 17:00");
		info.seminar = "G409-02";
		info.hole = "G409";
		list.add(info);

		// G402-01
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 10:00");
		info.setEnd("2014/7/29 12:30");
		info.seminar = "G402-01";
		info.hole = "G402";
		list.add(info);
		// G402-02
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 12:30");
		info.setEnd("2014/7/29 14:00");
		info.seminar = "G402-02";
		info.hole = "G402";
		list.add(info);
		// G402-03
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 14:00");
		info.setEnd("2014/7/29 15:30");
		info.seminar = "G402-03";
		info.hole = "G402";
		list.add(info);
		// G402-04
		info = new CatvSeminarDto();
		info.setStart("2014/7/29 15:30");
		info.setEnd("2014/7/29 18:00");
		info.seminar = "G402-04";
		info.hole = "G402";
		list.add(info);

		return list;
	}

	/**
	 * 7/30のセミナー情報<b>List</b>の初期化
	 *
	 * @return 7/30のセミナー情報<b>List</b>
	 */
	private static List<CatvSeminarDto> createSeminar30List() {
		List<CatvSeminarDto> list = new LinkedList<CatvSeminarDto>();
		CatvSeminarDto info = null;
		// B5-04
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 8:30");
		info.setEnd("2014/7/30 12:00");
		info.seminar = "B5-04";
		info.hole = "B5";
		list.add(info);
		// B5-05
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 12:00");
		info.setEnd("2014/7/30 15:00");
		info.seminar = "B5-05";
		info.hole = "B5";
		list.add(info);
		// B5-06
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 15:00");
		info.setEnd("2014/7/30 17:00");
		info.seminar = "B5-06";
		info.hole = "B5";
		list.add(info);

		// D5-03
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 8:00");
		info.setEnd("2014/7/30 12:00");
		info.seminar = "D5-03";
		info.hole = "D5";
		list.add(info);
		// D5-04
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 12:00");
		info.setEnd("2014/7/30 15:00");
		info.seminar = "D5-04";
		info.hole = "D5";
		list.add(info);
		// D5-05
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 15:00");
		info.setEnd("2014/7/30 17:00");
		info.seminar = "D5-05";
		info.hole = "D5";
		list.add(info);

		// G407-04
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 8:00");
		info.setEnd("2014/7/30 11:00");
		info.seminar = "G407-04";
		info.hole = "G407";
		list.add(info);
		// G407-05
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 11:00");
		info.setEnd("2014/7/30 12:30");
		info.seminar = "G407-05";
		info.hole = "G407";
		list.add(info);
		// G407-06
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 12:30");
		info.setEnd("2014/7/30 15:00");
		info.seminar = "G407-06";
		info.hole = "G407";
		list.add(info);
		// G407-07
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 15:00");
		info.setEnd("2014/7/30 17:00");
		info.seminar = "G407-07";
		info.hole = "G407";
		list.add(info);

		// G409-03
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 8:00");
		info.setEnd("2014/7/30 10:45");
		info.seminar = "G409-03";
		info.hole = "G409";
		list.add(info);
		// G409-04
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 10:45");
		info.setEnd("2014/7/30 12:15");
		info.seminar = "G409-04";
		info.hole = "G409";
		list.add(info);
		// G409-05
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 12:15");
		info.setEnd("2014/7/30 14:00");
		info.seminar = "G409-05";
		info.hole = "G409";
		list.add(info);
		// G409-06
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 14:00");
		info.setEnd("2014/7/30 19:00");
		info.seminar = "G409-06";
		info.hole = "G409";
		list.add(info);

		// G402-06
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 10:00");
		info.setEnd("2014/7/30 14:00");
		info.seminar = "G402-06";
		info.hole = "G402";
		list.add(info);
		// G402-07
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 14:00");
		info.setEnd("2014/7/30 15:30");
		info.seminar = "G402-07";
		info.hole = "G402";
		list.add(info);
		// G402-08
		info = new CatvSeminarDto();
		info.setStart("2014/7/30 15:30");
		info.setEnd("2014/7/30 19:00");
		info.seminar = "G402-08";
		info.hole = "G402";
		list.add(info);

		return list;
	}

}