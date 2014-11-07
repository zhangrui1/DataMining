package jp.co.freedom.ooxml.omron.utilities;

import jp.co.freedom.common.utilities.OoxmlUtil;
import jp.co.freedom.common.utilities.StringUtil;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * 【OMRON】OOXML共通編集ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class CommonOoxmlUtil {

	/**
	 * 出力先の行番号を探索
	 * 
	 * @param sheet
	 *            シート
	 * @param nColumn
	 *            探索列番号
	 * @param startRow
	 *            探索開始行番号
	 * @param endRow
	 *            探索終了行番号
	 * @param target
	 *            探索対象の文字列
	 * @return 出力先の行番号
	 */
	public static int searchRow(XSSFSheet sheet, int nColumn, int startRow,
			int endRow, String target) {
		assert StringUtil.isNotEmpty(target);
		for (int nRow = startRow; nRow <= endRow; nRow++) {
			XSSFRow row = OoxmlUtil.getRowAnyway(sheet, nRow);
			XSSFCell cell = OoxmlUtil.getCellAnyway(row, nColumn);
			String value = cell.getStringCellValue();
			if (StringUtil.equalsIgnoreDifference(target, value)) {
				return nRow;
			}
		}
		return -1;
	}
}