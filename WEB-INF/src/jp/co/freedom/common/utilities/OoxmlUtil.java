package jp.co.freedom.common.utilities;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Apache POI 3.9によるXSSFコンポーネントを使ったOOXML編集ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class OoxmlUtil {

	/** デフォルトFontファイル名 */
	private static final String DEFAULT_FONT_NAME = "ＭＳ Ｐゴシック";

	/**
	 * xlsxファイルを読み込み<b>XSSFWorkbook</b>を生成する
	 * 
	 * @param filePath
	 * @return
	 */
	public static XSSFWorkbook load(String filePath) {
		try {
			InputStream inputStream = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			return workbook;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * xlsxファイルのファイル出力
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param filePath
	 *            ファイルパス
	 * @return ファイル出力の成否
	 */
	public static boolean saveAs(XSSFWorkbook workbook, String filePath) {
		boolean result = true;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} catch (IOException e) {
			result = false;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * 指定した行番号の<b>XSSFRow</b>を取得する。有効範囲外の行の場合は新規作成する。
	 * 
	 * @param sheet
	 *            <b>XSSFSheet</b>
	 * @param nRow
	 *            取得したい行の行番号
	 * @return <b>XSSFRow</b>
	 */
	public static XSSFRow getRowAnyway(XSSFSheet sheet, int nRow) {
		assert sheet != null;
		XSSFRow row = sheet.getRow(nRow);
		if (row == null) {
			row = sheet.createRow(nRow);
		}
		return row;
	}

	/**
	 * 指定した列番号の<b>XSSFCell</b>を取得する。有効範囲外のセルの場合は新規作成する。
	 * 
	 * @param row
	 *            <b>XSSFRow</b>
	 * @param nColumn
	 *            取得したいセルの列番号
	 * @return <b>XSSFCell</b>
	 */
	public static XSSFCell getCellAnyway(XSSFRow row, int nColumn) {
		assert row != null;
		XSSFCell cell = row.getCell(nColumn);
		if (cell == null) {
			cell = row.createCell(nColumn);
		}
		return cell;
	}

	/**
	 * デフォルトのセルスタイルを作成
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param bold
	 *            太字設定フラグ
	 * @param centering
	 *            センタリングフラグ
	 * @param fontSize
	 *            フォントサイズ
	 * @return <b>CellStyle</b>
	 */
	public static XSSFCellStyle createDefaultCellStyle(XSSFWorkbook workbook,
			boolean bold, boolean centering, int fontSize) {
		XSSFFont font = workbook.createFont();
		if (bold) {
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		}
		font.setFontHeightInPoints((short) fontSize);
		font.setFontName(DEFAULT_FONT_NAME);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		if (centering) {
			style.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
		}
		return style;
	}

	/**
	 * デフォルトのテーブルヘッダースタイルを作成
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param bold
	 *            太字設定フラグ
	 * @param fontSize
	 *            フォントサイズ
	 * @param backgroundColor
	 *            背景色
	 * @return <b>CellStyle</b>
	 */
	public static XSSFCellStyle createDefaultTableHeaderCellStyle(
			XSSFWorkbook workbook, boolean bold, boolean center, int fontSize,
			Color backgroundColor) {
		XSSFFont font = workbook.createFont();
		if (bold) {
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		}
		font.setFontHeightInPoints((short) fontSize);
		font.setFontName(DEFAULT_FONT_NAME);
		XSSFCellStyle style = workbook.createCellStyle();
		if (center) {
			style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		}
		style.setFont(font);
		style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);// 必ずsetFillForegroundColorの直前に記述すること
		style.setFillForegroundColor(new XSSFColor(backgroundColor));
		style.setBorderTop(XSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		style.setBorderRight(XSSFCellStyle.BORDER_THIN);
		return style;
	}

	/**
	 * 罫線スタイルの<b>CellStyle</b>を生成
	 * 
	 * @param workbook
	 *            ワークブック
	 * @return <b>CellStyle</b>
	 */
	public static XSSFCellStyle createTableDataCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(XSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		style.setBorderRight(XSSFCellStyle.BORDER_THIN);
		return style;
	}

	/**
	 * 指定セルのセルスタイルを取得
	 * 
	 * @param sheet
	 *            シート
	 * @param nRow
	 *            行番号
	 * @param nColumn
	 *            列番号
	 * @return <b>CellStyle</b>
	 */
	public static XSSFCellStyle getCellStyle(XSSFSheet sheet, int nRow,
			int nColumn) {
		assert sheet != null;
		XSSFRow row = getRowAnyway(sheet, nRow);
		XSSFCell cell = getCellAnyway(row, nColumn);
		return cell.getCellStyle();
	}

	/**
	 * 行方向のセルの値を合算する
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param nRow
	 *            行番号
	 * @param nStartColumn
	 *            開始列番号
	 * @param nEndColumn
	 *            終了列番号
	 * @return 合算値
	 */
	public static int sumRow(XSSFSheet sheet, int nRow, int nStartColumn,
			int nEndColumn) {
		XSSFRow row = sheet.getRow(nRow);
		assert row != null;
		int sum = 0;
		for (int nIndex = nStartColumn; nIndex <= nEndColumn; nIndex++) {
			XSSFCell cell = row.getCell(nIndex);
			assert cell != null;
			sum += cell.getNumericCellValue();
		}
		return sum;
	}

	/**
	 * 列方向のセルの値を合算する
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param nColumn
	 *            行番号
	 * @param nStartRow
	 *            開始列番号
	 * @param nEndRow
	 *            終了列番号
	 * @return 合算値
	 */
	public static int sumColumn(XSSFSheet sheet, int nColumn, int nStartRow,
			int nEndRow) {
		int sum = 0;
		for (int nIndex = nStartRow; nIndex <= nEndRow; nIndex++) {
			XSSFRow row = sheet.getRow(nIndex);
			assert row != null;
			XSSFCell cell = row.getCell(nColumn);
			assert cell != null;
			sum += cell.getNumericCellValue();
		}
		return sum;
	}

	/**
	 * 指定行を0で埋める
	 * 
	 * @param sheet
	 *            編集対象シート
	 * @param nRow
	 *            行番号
	 * @param nStartColumn
	 *            開始列番号
	 * @param nEndColumn
	 *            終了列番号
	 */
	public static void setZero(XSSFSheet sheet, int nRow, int nStartColumn,
			int nEndColumn) {
		XSSFRow row = getRowAnyway(sheet, nRow);
		for (int nIndex = nStartColumn; nIndex <= nEndColumn; nIndex++) {
			XSSFCell cell = getCellAnyway(row, nIndex);
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue((double) 0);
		}
	}

	/**
	 * 全てのシートの先頭セルにフォーカスをあてる
	 * 
	 * @param workbook
	 *            ワークブック
	 */
	public static void setFocusFistCell(XSSFWorkbook workbook) {
		assert workbook != null;
		for (int nIndex = 0; nIndex < workbook.getNumberOfSheets(); nIndex++) {
			XSSFSheet sheet = workbook.getSheetAt(nIndex);
			XSSFCell cell = getFirstCell(sheet);
			assert cell != null;
			cell.setAsActiveCell();
		}
	}

	/**
	 * 全てのシートの計算式を強制的に再評価させる
	 * 
	 * @param workbook
	 *            ワークブック
	 */
	public static void setForceFormulaRecalculation(XSSFWorkbook workbook) {
		assert workbook != null;
		for (int nIndex = 0; nIndex < workbook.getNumberOfSheets(); nIndex++) {
			XSSFSheet sheet = workbook.getSheetAt(nIndex);
			sheet.setForceFormulaRecalculation(true);
		}
	}

	/**
	 * 全てのシートの拡大率を指定する
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param numerator
	 *            拡大率は numerator/denominatorで算出される
	 * @param denominator
	 *            拡大率は numerator/denominatorで算出される
	 */
	public static void setZoom(XSSFWorkbook workbook, int numerator,
			int denominator) {
		assert workbook != null;
		for (int nIndex = 0; nIndex < workbook.getNumberOfSheets(); nIndex++) {
			XSSFSheet sheet = workbook.getSheetAt(nIndex);
			sheet.setZoom(numerator, denominator);
		}
	}

	/**
	 * シートの先頭セルを取得
	 * 
	 * @param sheet
	 *            シート
	 * @return 先頭<b>Cell</b>
	 */
	public static XSSFCell getFirstCell(XSSFSheet sheet) {
		XSSFRow row = getRowAnyway(sheet, 0);
		XSSFCell cell = getCellAnyway(row, 0);
		return cell;
	}

	/**
	 * セルに文字列を出力する
	 * 
	 * @param sheet
	 *            シート
	 * @param nRow
	 *            出力行番号
	 * @param nColumn
	 *            出力列番号
	 * @param object
	 *            出力データ
	 * @param style
	 *            セルスタイル
	 */
	public static void setData(XSSFSheet sheet, int nRow, int nColumn,
			Object object, XSSFCellStyle style) {
		assert sheet != null && object != null;
		XSSFRow row = getRowAnyway(sheet, nRow);
		XSSFCell cell = getCellAnyway(row, nColumn);
		if (style != null) {
			cell.setCellStyle(style);
		}
		if (object instanceof String) {
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellValue((String) object);
		} else if (object instanceof Integer) {
			Integer integer = (Integer) object;
			if (0 == integer) {
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue("-");
			} else {
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue((Integer) object);
			}
		} else if (object instanceof Double) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			if (Double.isNaN((Double) object)) {
				cell.setCellValue("-");
			} else {
				Double value = (Double) object;
				if (0 == value.compareTo((Double) 0.0)) {
					cell.setCellValue("-");
				} else {
					cell.setCellValue((Double) object);
				}
			}
		}
	}

	/**
	 * 指定範囲にセルを新規作成
	 * 
	 * @param sheet
	 *            シート
	 * @param startRow
	 *            開始行番号
	 * @param endRow
	 *            終了行番号
	 * @param startColumn
	 *            開始列番号
	 * @param endColumn
	 *            終了列番号
	 * @param style
	 *            セルスタイル
	 */
	public static void setStyle(XSSFSheet sheet, int startRow, int endRow,
			int startColumn, int endColumn, XSSFCellStyle style) {
		for (int nRow = startRow; nRow <= endRow; nRow++) {
			XSSFRow row = getRowAnyway(sheet, nRow);
			for (int nColumn = startColumn; nColumn <= endColumn; nColumn++) {
				XSSFCell cell = getCellAnyway(row, nColumn);
				cell.setCellStyle(style);
			}
		}
	}

	/**
	 * シートを生成する
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param names
	 *            シート名を格納する<b>List</b>
	 */
	public static void createSheet(XSSFWorkbook workbook, List<String> names) {
		assert workbook != null && names != null;
		for (String name : names) {
			workbook.createSheet(name);
		}
	}

	/**
	 * ハイパーリンクの設定
	 * 
	 * @param sheet
	 *            シート
	 * @param nRow
	 *            対象行番号
	 * @param nColumn
	 *            対象列番号
	 * @param value
	 *            ハイパーリンクテキスト
	 * @param url
	 *            ハイパーリンク先URL
	 */
	public static void setHyperLink(XSSFSheet sheet, int nRow, int nColumn,
			String value, String url) {
		assert sheet != null;
		XSSFWorkbook workbook = sheet.getWorkbook();
		CreationHelper helper = workbook.getCreationHelper();
		Hyperlink hyperlink = helper.createHyperlink(Hyperlink.LINK_URL);
		hyperlink.setAddress(url);
		XSSFRow row = getRowAnyway(sheet, nRow);
		XSSFCell cell = getCellAnyway(row, nColumn);
		cell.setCellValue(value);
		cell.setHyperlink(hyperlink);
		// ハイパーリンクテキストの装飾
		XSSFFont font = workbook.createFont();
		XSSFCellStyle style = workbook.createCellStyle();
		// font.setColor(new XSSFColor(new Color(0, 0, 255)));
		font.setUnderline(XSSFFont.U_SINGLE);
		style.setFont(font);
		cell.setCellStyle(style);
	}

	/**
	 * 指定セルの削除
	 * 
	 * @param sheet
	 *            シート
	 * @param startRow
	 *            開始行番号
	 * @param endRow
	 *            終了行番号
	 * @param startColumn
	 *            開始列番号
	 * @param endColumn
	 *            終了列番号
	 */
	public static void removeCell(XSSFSheet sheet, int startRow, int endRow,
			int startColumn, int endColumn) {
		assert sheet != null;
		for (int nRow = startRow; nRow <= endRow; nRow++) {
			XSSFRow row = sheet.getRow(nRow);
			if (row != null) {
				for (int nColumn = startColumn; nColumn <= endColumn; nColumn++) {
					XSSFCell cell = row.getCell(nColumn);
					if (cell != null) {
						row.removeCell(cell);
					}
				}
			}
		}
	}

	/**
	 * 指定シートの削除
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param sheetName
	 *            削除対象のシート名
	 */
	public static void removeSheet(XSSFWorkbook workbook, String sheetName) {
		assert workbook != null && StringUtil.isNotEmpty(sheetName);
		int removeId = workbook.getSheetIndex(sheetName);
		workbook.removeSheetAt(removeId);
	}

	/**
	 * シートのクローンを行う
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param insertSheetId
	 *            シートの挿入先番号
	 * @param from
	 *            コピー元のシート名
	 * @param to
	 *            追加するシート名
	 * @return 追加するシート
	 */
	public static XSSFSheet cloneSheet(XSSFWorkbook workbook,
			int insertSheetId, String from, String to) {
		assert workbook != null && StringUtil.isNotEmpty(from)
				&& StringUtil.isNotEmpty(to);
		int sheetId = workbook.getSheetIndex(from);
		XSSFSheet clone = workbook.cloneSheet(sheetId);
		String cloneName = clone.getSheetName();
		workbook.setSheetOrder(cloneName, insertSheetId);
		workbook.setSheetName(insertSheetId, to);
		int newSheetId = workbook.getSheetIndex(to);
		return workbook.getSheetAt(newSheetId);
	}

	/**
	 * 指定したシート名のシートを取得
	 * 
	 * @param workbook
	 *            ワークブック
	 * @param name
	 *            シート名
	 * @return シート
	 */
	public static XSSFSheet getSheet(XSSFWorkbook workbook, String name) {
		assert workbook != null && StringUtil.isNotEmpty(name);
		int nSheetIndex = workbook.getSheetIndex(name);
		return workbook.getSheetAt(nSheetIndex);
	}
}