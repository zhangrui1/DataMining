package jp.co.freedom.promptreport.util;

/**
 * SQL編集ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class SqlUtil {

	/**
	 * SQLのWhere句に指定した条件を追加する(追加する条件が文字列型である場合)
	 * 
	 * @param whereQuery
	 *            SQLのWhere句
	 * @param key
	 *            Where句の条件の項目
	 * @param value
	 *            Where句の条件の値
	 * @param isEqual
	 *            Where句の条件にイコールを用いるか否かのブール値
	 * @return 追加後のSQLのWhere句を格納した<b>StringBuffer</b>
	 */
	public static StringBuffer appendStringQuery(StringBuffer whereQuery,
			String key, String value, boolean isEqual) {
		if (whereQuery.length() != 0) {
			whereQuery.append(" and ");
		}
		whereQuery.append(key);
		if (isEqual) {
			whereQuery.append("=");
		} else {
			whereQuery.append("!=");
		}
		whereQuery.append("'" + value + "'");
		return whereQuery;
	}

	/**
	 * SQLのWhere句に指定した条件を追加する(追加する条件が数値型である場合)
	 * 
	 * @param whereQuery
	 *            SQLのWhere句
	 * @param key
	 *            Where句の条件の項目
	 * @param value
	 *            Where句の条件の値
	 * @param isEqual
	 *            Where句の条件にイコールを用いるか否かのブール値
	 * @return 追加後のSQLのWhere句を格納した<b>StringBuffer</b>
	 */
	public static StringBuffer appendIntegerQuery(StringBuffer sql, String key,
			int value, boolean isEqual) {
		if (sql.length() != 0) {
			sql.append(" and ");
		}
		sql.append(key);
		if (isEqual) {
			sql.append("=");
		} else {
			sql.append("!=");
		}
		sql.append(value);
		return sql;
	}
}
