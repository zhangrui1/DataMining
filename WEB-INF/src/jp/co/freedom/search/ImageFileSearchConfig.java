package jp.co.freedom.search;

/**
 * イメージファイル検索コンフィグレーションクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImageFileSearchConfig {

	// ◆◆◆OCRエンジン◆◆◆
	/** OCRのEXEのファイルパス */
	public static final String OCR_EXE_FILE_PATH = "f:\\Program Files (x86)\\Tesseract-OCR\\tesseract.exe";

	/** OCRのスキャニング結果テキストファイルのファイルのベース名 */
	public static final String RESULT_FILE_BASENAME = "result";

	/** OCRのスキャニング結果テキストファイルのファイル名 */
	public static final String RESULT_FILE_NAME = "result.txt";

	// ◆◆◆ローカルの検索対象TIFFファイル◆◆◆
	/** TIFFファイルの配置フォルダのパス */
	public static final String ENQUETE_DATA_DIRECTORY = "f:\\search\\tiff";

	/** ファイル拡張子(TXT) */
	public static final String ALLOWS_EXTENSIONS_TIFF[] = { "tiff", "tif",
			"TIFF", "TIF" };

}
