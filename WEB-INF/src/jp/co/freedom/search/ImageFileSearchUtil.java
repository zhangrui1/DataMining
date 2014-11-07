package jp.co.freedom.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.freedom.common.utilities.StringUtil;

/**
 * イメージファイル検索ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class ImageFileSearchUtil {

	/**
	 * OCR読取データの取得
	 * 
	 * @param file
	 *            　OCR結果ファイル
	 * @return OCR読取データ
	 * @throws IOException
	 */
	public static String getOCRData(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<String> lists = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			lists.add(line);
		}
		br.close();
		return StringUtil.concat(lists);
	}
}
