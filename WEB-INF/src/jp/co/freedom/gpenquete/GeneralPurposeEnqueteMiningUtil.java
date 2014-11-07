package jp.co.freedom.gpenquete;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.AnalysisTelFax;
import jp.co.freedom.common.utilities.DOMUtil;
import jp.co.freedom.common.utilities.EnqueteUtil;
import jp.co.freedom.common.utilities.FileUtil;
import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.gpenquete.schema.ChildSchema;
import jp.co.freedom.gpenquete.schema.ChoiceSchema;
import jp.co.freedom.gpenquete.schema.GeneralValidationRulesSchema;
import jp.co.freedom.gpenquete.schema.IfElseSchema;
import jp.co.freedom.gpenquete.schema.QuestionSchema;
import jp.co.freedom.master.dto.UserDataDto;
import jp.co.freedom.master.utilities.mesago.MesagoUtil;

import org.w3c.dom.Element;

/**
 * アンケートスキーマ編集ユーティリティクラス
 *
 * @author フリーダム・グループ
 *
 */
public class GeneralPurposeEnqueteMiningUtil {

	// 入力データ関連のプロパティ名
	/** プロパティ：CSVデータのセパレイト文字 */
	private static final String PROPERTY_INPUT_SEPARATE_MARK = "input-separate-mark";
	/** プロパティ：CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値 */
	private static final String PROPERTY_ENQUOTE_BY_DOUBLE_QUOTATION = "enquote-by-double-quotation";
	/** 最初の行がヘッダー行であるか否かのブール値 */
	private static final String PROPERTY_REMOVE_HEADER_RECORD = "remove-header-record";
	// 出力データ関連のプロパティ名
	/** プロパティ：ヘッダー行の出力フラグ */
	private static final String PROPERTY_OUTPUT_HEADER_ROW = "output-header-row";
	/** プロパティ：CSVデータのセパレイト文字 */
	private static final String PROPERTY_OUTPUT_SEPARATE_MARK = "output-separate-mark";
	/** プロパティ：CSVデータがダブルコーテーションによるエンクォートが適用されているか否かのブール値 */
	private static final String PROPERTY_OUTPUT_ENQUOTE_BY_DOUBLE_QUOTATION = "output-enquote-by-double-quotation";
	/** 出力ファイル名 */
	private static final String PROPERTY_OUTPUT_FILENAME = "output-filename";
	/** 単純回答の連結時に使用するデリミタ */
	private static final String PROPERTY_CONCAT_DELIMITER_FOR_SIMPLE_ANSWER = "concat-delimiter-for-simple-answer";
	/** 複数回答の連結時に使用するデリミタ */
	private static final String PROPERTY_CONCAT_DELIMITER_FOR_MULTI_ANSWER = "concat-delimiter-for-multi-answer";
	/** 内訳回答の連結時に使用するデリミタ */
	private static final String PROPERTY_CONCAT_DELIMITER_FOR_CHILD = "concat-delimiter-for-child";
	/** 1フラグ展開に使用する文字列 */
	private static final String PROPERTY_1_FLG_MARK = "1-flg-mark";
	/** 選択肢型回答の入力タイプ */
	private static final String PROPERTY_CHOICE_ANSWER_INPUT_TYPE = "choice-answer-input-type";
	/** 不明マーク */
	private static final String PROPERTY_UNDECIPHERABLE_MARK = "undecipherable-mark";
	// ロジック制御プロパティ名
	/** その他FAや内訳回答が存在する場合にフラグ補正を行うか否かのブール値 */
	private static final String PROPERY_CORRECT_CHOICE_SELECT_FA = "correct-choice-select-fa";

	/** エラー種別: 必須制約違反 */
	private static final String ERROR_TYPE_REQUIRED = "required";

	/** エラー種別: オーバーフロー */
	private static final String ERROR_TYPE_OVERFLOW = "overflow";

	/** エラー種別：都道府県名が不正 */
	private static final String ERROR_TYPE_INVALID_ADDR1 = "invalid-addr1";

	/** エラー種別：文字列長が不正 */
	private static final String ERROR_TYPE_DATA_LENGTH_ERROR = "data-length-error";

	/** エラー種別：メールアドレスが不正 */
	private static final String ERROR_TYPE_INVALID_EMAIL = "invalid-email";

	/** エラー種別：日付情報が不正 */
	private static final String ERROR_TYPE_INVALID_DATE = "invalid-date";

	/** エラー種別：データタイプが不正 */
	private static final String ERROR_TYPE_INVALID_DATA_TYPE = "invalid-data-type";

	/** エラー種別：データ範囲が不正 */
	private static final String ERROR_TYPE_INVALID_DATA_RANGE = "invalid-data-range";

	/** エラー種別：千趣会用日付情報が不正日付 */
	private static final String ERROR_TYPE_INVALID_DATE_FOR_SENSYUKAI = "invalid-date-for-sensyukai";

	/** エラー情報 */
	public static List<GeneralPurposeErrorInfo> errorInfoList = new ArrayList<GeneralPurposeErrorInfo>();

	/**
	 * GeneralPurposeEnqueteConfigオブジェクトのインスタンス化
	 *
	 * @param configElement
	 *            　config.xmlのconfig要素ノード
	 * @return <b>EnqueteSchema</b>
	 */
	public static GeneralPurposeEnqueteConfig createConfig(Element configElement) {
		GeneralPurposeEnqueteConfig config = new GeneralPurposeEnqueteConfig();
		// config.xmlのルート要素
		Element propertiesElement = DOMUtil.getChildElement(configElement,
				"properties");
		assert propertiesElement != null;
		/* プロパティの読み込み */
		List<Element> properties = DOMUtil
				.getChildrenElement(propertiesElement);
		config.inputSeparateMark = getPropertyValue(properties,
				PROPERTY_INPUT_SEPARATE_MARK);
		// [備忘] output-header-rowプロパティのデフォルト値はfalse
		config.outputHeaderRow = StringUtil.toBoolean(getPropertyValue(
				properties, PROPERTY_OUTPUT_HEADER_ROW));
		config.outputSeparateMark = getPropertyValue(properties,
				PROPERTY_OUTPUT_SEPARATE_MARK);
		// [備忘] enquote-by-double-quotationプロパティのデフォルト値はfalse
		config.enquoteByDoubleQuotation = StringUtil
				.toBoolean(getPropertyValue(properties,
						PROPERTY_ENQUOTE_BY_DOUBLE_QUOTATION));
		// [備忘] output-enquote-by-double-quotation属性のデフォルト値はfalse
		config.outputEnquoteByDoubleQuotation = StringUtil
				.toBoolean(getPropertyValue(properties,
						PROPERTY_OUTPUT_ENQUOTE_BY_DOUBLE_QUOTATION));
		// [備忘] remove-header-record属性のデフォルト値はfalse
		config.removeHeaderRecord = StringUtil.toBoolean(getPropertyValue(
				properties, PROPERTY_REMOVE_HEADER_RECORD));
		config.outputFileName = getPropertyValue(properties,
				PROPERTY_OUTPUT_FILENAME);
		config.concatDelimiterForSimpleAnswer = getPropertyValue(properties,
				PROPERTY_CONCAT_DELIMITER_FOR_SIMPLE_ANSWER);
		config.concatDelimiterForMultiAnswer = getPropertyValue(properties,
				PROPERTY_CONCAT_DELIMITER_FOR_MULTI_ANSWER);
		config.concatDelimiterForChild = getPropertyValue(properties,
				PROPERTY_CONCAT_DELIMITER_FOR_CHILD);
		config.flg1Mark = getPropertyValue(properties, PROPERTY_1_FLG_MARK);
		// [備忘] correct-choice-select-fa属性のデフォルト値はfalse
		config.correctChoiceSelectFa = StringUtil.toBoolean(getPropertyValue(
				properties, PROPERY_CORRECT_CHOICE_SELECT_FA));
		config.choiceAnswerInputType = getPropertyValue(properties,
				PROPERTY_CHOICE_ANSWER_INPUT_TYPE);
		config.undecipherableMark = getPropertyValue(properties,
				PROPERTY_UNDECIPHERABLE_MARK);
		/* スキーマ定義の読み込み */
		Element schema = DOMUtil.getChildElement(configElement, "schema");
		assert schema != null;
		List<QuestionSchema> questionList = new ArrayList<QuestionSchema>();
		// schema要素以下の全てのelement要素を探索
		for (Element element = DOMUtil.getFirstChildElement(schema); element != null; element = DOMUtil
				.getNextSiblingElement(element)) {
			QuestionSchema question = new QuestionSchema();
			if ("fix-value".equals(element.getLocalName())) { // 固定値要素である場合
				question.fix = true;
				question.id = DOMUtil.getAttributeValue(element, "id");
				question.fixValue = DOMUtil
						.getAttributeValue(element, "select");
				question.fixFormat = DOMUtil.getAttributeValue(element,
						"format");
				question.fixLength = DOMUtil.getAttributeValue(element,
						"length");
				question.fixCompleteMark = DOMUtil.getAttributeValue(element,
						"complete-mark");
				question.fixCompleteMarkPosition = DOMUtil.getAttributeValue(
						element, "complete-mark-position");
				question.header = DOMUtil.getAttributeValue(element, "value");
				// [備忘] convert-full-string属性のデフォルト値はfalse
				question.convertFullString = StringUtil.toBoolean(DOMUtil
						.getAttributeValue(element, "convert-full-string"));
				question.insertPosition = StringUtil.toInteger(DOMUtil
						.getAttributeValue(element, "insert-position"));
				question.insertValue = DOMUtil.getAttributeValue(element,
						"insert-value");
				questionList.add(question);
				continue;
			}
			/*
			 * 【element要素の属性仕様】
			 *
			 * id属性：　　　　入力データの列番号
			 *
			 * group_id属性：　　　　サブグループ番号
			 *
			 * value属性：　 出力ファイルのデータヘッダ名称
			 *
			 * type属性：　　 アンケート種別（'expand'=1フラグ展開, 'choice'=choice回答）
			 *
			 * mode属性：　　 選択肢回答種別（'single'=シングル回答, 'multi'=マルチ回答）
			 *
			 * start属性:　　出力文字列開始位置
			 *
			 * end属性:　　　出力文字列終端位置
			 *
			 * match-by-regex:　　　部分文字列抽出用正規表現
			 *
			 * match-group-id:　　　部分文字列抽出用正規表現のグループ番号
			 *
			 * replace-text:
			 * 【出力データの加工用】match-by-regex属性で指定された正規表現にマッチした文字列を置換するテキスト
			 *
			 * contains-by-regex:　　　文字列検索用正規表現
			 *
			 * insert-position: 【出力データの加工用】 指定文字列の挿入先位置
			 *
			 * insert-value: 【出力データの加工用】 挿入する文字列
			 *
			 * replace-before-regex：【出力データの加工用】置換対象の正規表現
			 *
			 * replace-after-value：【出力データの加工用】置換後の文字列
			 *
			 * data-format: 【出力データの加工用】 出力データ・フォーマット
			 *
			 * required: 必須制約
			 *
			 * use-delimiter: 【出力データの加工用】デリミタ使用フラグ
			 */
			question.id = DOMUtil.getAttributeValue(element, "id");
			question.groupId = DOMUtil.getAttributeValue(element, "group_id");
			question.header = DOMUtil.getAttributeValue(element, "value");
			question.type = DOMUtil.getAttributeValue(element, "type");
			question.mode = DOMUtil.getAttributeValue(element, "mode");
			question.start = StringUtil.toInteger(DOMUtil.getAttributeValue(
					element, "start"));
			question.end = StringUtil.toInteger(DOMUtil.getAttributeValue(
					element, "end"));
			question.matchByRegex = normalize(DOMUtil.getAttributeValue(
					element, "match-by-regex"));
			question.matchGroupId = DOMUtil.getAttributeValue(element,
					"match-group-id");
			question.replaceText = normalize(DOMUtil.getAttributeValue(element,
					"replace-text"));
			question.containsByRegex = normalize(DOMUtil.getAttributeValue(
					element, "contains-by-regex"));
			question.insertPosition = StringUtil.toInteger(DOMUtil
					.getAttributeValue(element, "insert-position"));
			question.insertValue = DOMUtil.getAttributeValue(element,
					"insert-value");
			question.dataFormat = DOMUtil.getAttributeValue(element,
					"data-format");
			question.delimit = normalize(DOMUtil.getAttributeValue(element,
					"delimit"));
			question.replaceBeforeRegex = DOMUtil.getAttributeValue(element,
					"replace-before-regex");
			question.replaceAfterValue = DOMUtil.getAttributeValue(element,
					"replace-after-value");
			// [備忘] required属性のデフォルト値はfalse
			question.required = StringUtil.toBoolean(DOMUtil.getAttributeValue(
					element, "required"));
			question.dataLength = DOMUtil.getAttributeValue(element,
					"data-length");
			question.dataMinLength = DOMUtil.getAttributeValue(element,
					"data-min-length");
			question.dataMaxLength = DOMUtil.getAttributeValue(element,
					"data-max-length");
			// [備忘] useDelimiter属性のデフォルト値はtrue
			String useDelimiter = DOMUtil.getAttributeValue(element,
					"use-delimiter");
			if (StringUtil.isNotEmpty(useDelimiter)) {
				question.useDelimiter = StringUtil.toBoolean(useDelimiter);
			} else {
				question.useDelimiter = true;
			}

			List<ChoiceSchema> choices = new ArrayList<ChoiceSchema>();
			List<Element> children = DOMUtil.getChildrenElement(element);
			int nIndex = 0;
			if (StringUtil.isEmpty(question.type)
					&& StringUtil.isEmpty(question.mode)
					|| "group".equals(question.type)) {// 単純回答型(type,mode属性なし)
				for (Element child : children) {
					ChoiceSchema choiceSchema = new ChoiceSchema();
					// id属性は選択肢ID（連番）　※現状、利用していない
					choiceSchema.id = String.valueOf(++nIndex);
					// 単純回答型の場合はchildId属性に格納
					ChildSchema childSchema = new ChildSchema();
					childSchema.childId = DOMUtil
							.getAttributeValue(child, "id");
					List<ChildSchema> childSchemaList = new ArrayList<ChildSchema>();
					childSchemaList.add(childSchema);
					choiceSchema.children = childSchemaList;

					choices.add(choiceSchema);
				}
				question.choices = choices;
			} else if ("condition".equals(question.type)) {
				List<IfElseSchema> conditions = new ArrayList<IfElseSchema>();
				for (Element child : children) {
					IfElseSchema condition = new IfElseSchema();
					condition.regex = normalize(DOMUtil.getAttributeValue(
							child, "match-by-regex"));
					condition.value = DOMUtil.getAttributeValue(child, "value");
					condition.elseFlg = "else".equals(child.getLocalName());
					// 子要素としてchild要素ノードが指定されている場合
					if (DOMUtil.hasChild(child)) {
						Element childElement = DOMUtil
								.getFirstChildElement(child);
						ChildSchema childSchema = new ChildSchema();
						childSchema.childId = DOMUtil.getAttributeValue(
								childElement, "id");
						childSchema.matchByRegex = DOMUtil.getAttributeValue(
								childElement, "match-by-regex");
						childSchema.matchGroupId = DOMUtil.getAttributeValue(
								childElement, "match-group-id");
						condition.child = childSchema;
					}
					conditions.add(condition);
				}
				question.conditions = conditions;
			} else { // 選択肢型(type,mode属性あり)
				for (Element choice : children) {
					ChoiceSchema choiceSchema = new ChoiceSchema();
					// id属性は選択肢ID（連番）　※現状、利用していない
					choiceSchema.id = String.valueOf(++nIndex);
					// value属性はデータヘッダ名称として利用
					choiceSchema.value = DOMUtil.getAttributeValue(choice,
							"value");
					// refer属性はその他FAとの連携に利用。[その他FA]に入力データが存在する場合に[その他]にフラグを立てる等の対応を行う。
					choiceSchema.referId = DOMUtil.getAttributeValue(choice,
							"refer");
					/*
					 * 子にchild要素が存在する場合は、その選択肢に内訳項目が定義される。
					 *
					 * [例] 購入したいPCパーツは？1.HDD 2.メモリ 3.CPU (メーカー名：　) 4.その他(具体的に: )
					 *
					 * 【child要素の属性仕様】
					 *
					 * id属性：　　　　内訳項目ID　→ 上記例の「メーカー名」や「具体的に」に対応する入力項目に対応
					 *
					 * value属性：　 内訳表記で使用したい文字列　→ 出力例「メモリ,CPU（メーカー名：Intel）」
					 */
					List<ChildSchema> childSchemaList = new ArrayList<ChildSchema>();
					List<Element> childrenList = DOMUtil
							.getChildrenElement(choice);
					for (Element child : childrenList) {
						ChildSchema childSchema = new ChildSchema();
						childSchema.childId = DOMUtil.getAttributeValue(child,
								"id");
						childSchema.groupId = DOMUtil.getAttributeValue(child,
								"group_id");
						childSchema.childbeforeValue = DOMUtil
								.getAttributeValue(child, "before-label");
						childSchema.childAfterValue = DOMUtil
								.getAttributeValue(child, "after-label");
						childSchemaList.add(childSchema);
					}
					choiceSchema.children = childSchemaList;
					choices.add(choiceSchema);
				}
				question.choices = choices;
			}
			questionList.add(question);
		}
		config.enquetes = questionList;

		/* mapping要素が存在する場合にかぎり、マッピングルールを読み込む */
		Element mapping = DOMUtil.getChildElement(configElement, "mapping");
		if (mapping != null) {
			List<Element> children = DOMUtil.getChildrenElement(mapping);
			for (Element child : children) {
				String name = DOMUtil.getAttributeValue(child, "name");
				assert StringUtil.isNotEmpty(name);
				String idStr = DOMUtil.getAttributeValue(child, "id");
				assert StringUtil.isNotEmpty(idStr)
						&& StringUtil.integerStringCheck(idStr);
				config.mapping.put(name, Integer.parseInt(idStr));
			}
		}

		/* validation-rules要素が存在する場合にかぎり、データルールを読み込む */
		Element validationRules = DOMUtil.getChildElement(configElement,
				"validation-rules");
		if (validationRules != null) {
			List<Element> children = DOMUtil
					.getChildrenElement(validationRules);
			for (Element validationRule : children) {
				int id = StringUtil.toInteger(DOMUtil.getAttributeValue(
						validationRule, "id"));
				List<Element> values = DOMUtil
						.getChildrenElement(validationRule);
				GeneralValidationRulesSchema generalRules[] = new GeneralValidationRulesSchema[values
						.size()];
				int count = -1;
				for (Element value : values) {
					String start = DOMUtil.getAttributeValue(value, "start");
					String end = DOMUtil.getAttributeValue(value, "end");
					String regex = DOMUtil.getAttributeValue(value,
							"match-by-regex");
					// [備忘] reverse属性のデフォルト値はfalse
					boolean reverse = StringUtil.toBoolean(DOMUtil
							.getAttributeValue(value, "reverse"));
					generalRules[++count] = new GeneralValidationRulesSchema(
							start, end, regex, reverse);
				}
				config.validationRules.put(id, generalRules);
			}
		}

		return config;
	}

	/**
	 * 出力ファイル（CSV形式／文字コード:UTF-8／BOM付き）のダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param config
	 *            コンフィグ
	 * @param csvData
	 *            CSVデータ
	 * @param dim
	 *            デリミタ
	 * @param exhibition
	 *            展示会データ集計フラグ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws IOException
	 */
	public static boolean download(HttpServletRequest request,
			HttpServletResponse response, GeneralPurposeEnqueteConfig config,
			List<String[]> csvData, String dim, boolean exhibition)
			throws IOException {
		String encordedUrl = URLEncoder.encode(config.outputFileName, "UTF-8");

		/* コンテキストにダウンロードファイル情報を設定する */
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		/* ヘッダ部の書き出し */
		if (config.outputHeaderRow) {
			List<String> header = new ArrayList<String>();
			// 展示会データ集計時にかぎり、原票不備フラグと海外住所フラグを出力
			if (exhibition) {
				header.add(StringUtil.enquote("原票不備フラグ",
						config.outputEnquoteByDoubleQuotation));
				header.add(StringUtil.enquote("原票不備詳細",
						config.outputEnquoteByDoubleQuotation));
				// header.add(StringUtil.enquote("海外住所フラグ"));
			}
			for (QuestionSchema schema : config.enquetes) {
				if ("expand".equals(schema.type)) { // 1フラグ展開型の場合は全ての選択肢名称を出力
					List<ChoiceSchema> choices = schema.choices;
					for (ChoiceSchema choice : choices) {
						header.add(StringUtil.enquote(schema.header
								+ choice.value,
								config.outputEnquoteByDoubleQuotation));
					}
				} else if (schema.fix && StringUtil.isEmpty(schema.header)) { // 固定値要素である場合
					header.add(StringUtil.enquote(" ",
							config.outputEnquoteByDoubleQuotation));
				} else { // 通常の場合
					if ("mesagoTelFormat".equals(schema.dataFormat)) { // メサゴメッセ標準の電話番号フォーマットの場合
						header.add(StringUtil.enquote("TEL",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("TEL(国番号)",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("TEL(市外局番)",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("TEL(市内局番-番号)",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("TEL(内線)",
								config.outputEnquoteByDoubleQuotation));
					} else if ("mesagoFaxFormat".equals(schema.dataFormat)) { // メサゴメッセ標準のFAX番号フォーマットの場合
						header.add(StringUtil.enquote("FAX",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("FAX(国番号)",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("FAX(市外局番)",
								config.outputEnquoteByDoubleQuotation));
						header.add(StringUtil.enquote("FAX(市内局番-番号)",
								config.outputEnquoteByDoubleQuotation));
					} else {
						if (StringUtil.isEmpty(schema.groupId)) { // サブグループ以外のelementノードを対象とする
							header.add(StringUtil.enquote(schema.header,
									config.outputEnquoteByDoubleQuotation));
						}
					}
				}
			}
			FileUtil.writer(header, writer, dim);
		}

		/* データ部の書き出し */
		int nRow = 0;
		for (String[] data : csvData) { // csvデータを一行ごとに処理
			nRow++;
			List<String> cols = new ArrayList<String>(); // データ部出力用リスト
			if (exhibition) {
				outputExhibiInfo(config, cols, data); // 原票不備フラグ／海外住所フラグ
				updateExhibitionInputData(config, data); // ワイルドカードを含むTEL,FAX,EMAILの削除
			}
			/*
			 * サブグループの先読み
			 */
			Map<String, String> subGroupEvaluationResult = new HashMap<String, String>();
			for (QuestionSchema schema : config.enquetes) {
				if (StringUtil.isEmpty(schema.groupId)) { // サブグループ以外は読み飛ばす
					continue;
				} else if ("choice".equals(schema.type)) {
					subGroupEvaluationResult.put(schema.groupId,
							outputChoiceAnswer(nRow, config, data, schema)); // 選択肢回答型(choice回答)
				}
			}

			// スキーマ定義を確認しながらcsvデータを1行ごとに処理
			for (QuestionSchema schema : config.enquetes) {
				if (StringUtil.isNotEmpty(schema.groupId)) { // サブグループは読み飛ばす
					continue;
				}
				if (schema.fix) {
					outputFixedValue(nRow, cols, data, schema, config); // 固定値
				} else if (StringUtil.isEmpty(schema.type)
						&& StringUtil.isEmpty(schema.mode)) {
					if ("mesagoTelFormat".equals(schema.dataFormat)) { // メサゴメッセ標準の電話番号フォーマットの場合
						outputSimpleAnswerWithMesagoTelFormat(config, cols,
								data);
					} else if ("mesagoFaxFormat".equals(schema.dataFormat)) { // メサゴメッセ標準のFAX番号フォーマットの場合
						outputSimpleAnswerWithMesagoFaxFormat(config, cols,
								data);
					} else {
						outputSimpleAnswer(nRow, config, cols, data, schema); // 単純回答型
					}
				} else if ("expand".equals(schema.type)) {
					output1ExpandAnswer(nRow, config, cols, data, schema); // 選択回答型(1フラグ展開)
				} else if ("choice".equals(schema.type)) {
					cols.add(StringUtil.enquote(
							outputChoiceAnswer(nRow, config, data, schema,
									subGroupEvaluationResult),
							config.outputEnquoteByDoubleQuotation));// 選択肢回答型(choice回答)
				} else if ("condition".equals(schema.type)) {
					outputConditionAnswer(nRow, config, cols, data, schema); // 条件式による回答
				} else if ("group".equals(schema.type)) {
					outputGroupAnswer(nRow, config, cols, data, schema); // グループ型
				}
			}
			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * エラーログをTXT形式でダウンロード
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param outputFileName
	 *            出力ファイル名
	 * @param dim
	 *            デリミタ
	 * @return ダウンロード実行結果の成否のブール値
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean downLoadForErrolog(HttpServletRequest request,
			HttpServletResponse response, String outputFileName, String dim)
			throws IOException {

		String encordedUrl = URLEncoder.encode(outputFileName, "UTF-8");

		// コンテキストにダウンロードファイル情報を設定する
		response.setContentType("application/csv;charset=UTF-8");
		response.setHeader("Content-disposition", "attachment;filename="
				+ encordedUrl);

		// 出力用のWriterを生成する
		PrintWriter writer = response.getWriter();
		// エンコード=UTF-8であるCSVをExcelで正しく表示できるようにBOMを出力
		writer.print('\uFEFF');

		// ヘッダ情報の書き出し
		// String[] header = new String[columnNum];
		List<String> header = new ArrayList<String>();

		header.add(StringUtil.enquote("種別"));
		header.add(StringUtil.enquote("メッセージ"));
		header.add(StringUtil.enquote("行番号"));
		header.add(StringUtil.enquote("列番号"));
		header.add(StringUtil.enquote("入力データ"));
		FileUtil.writer(header, writer, dim);

		// String配列に格納されたカラムを デリミタで区切って1レコードに結合する
		for (GeneralPurposeErrorInfo info : errorInfoList) {

			List<String> cols = new ArrayList<String>();
			// 種別
			if (ERROR_TYPE_REQUIRED.equals(info.type)) {
				cols.add(StringUtil.enquote("必須制約違反"));
			} else if (ERROR_TYPE_OVERFLOW.equals(info.type)) {
				cols.add(StringUtil.enquote("入力値不正"));
			} else if (ERROR_TYPE_INVALID_ADDR1.equals(info.type)) {
				cols.add(StringUtil.enquote("都道府県名"));
			} else if (ERROR_TYPE_DATA_LENGTH_ERROR.equals(info.type)) {
				cols.add(StringUtil.enquote("データ長不正"));
			} else if (ERROR_TYPE_INVALID_EMAIL.equals(info.type)) {
				cols.add(StringUtil.enquote("メールアドレス"));
			} else if (ERROR_TYPE_INVALID_DATE.equals(info.type)) {
				cols.add(StringUtil.enquote("日付情報が不正"));
			} else if (ERROR_TYPE_INVALID_DATA_TYPE.equals(info.type)) {
				cols.add(StringUtil.enquote("データタイプが不正"));
			} else if (ERROR_TYPE_INVALID_DATA_RANGE.equals(info.type)) {
				cols.add(StringUtil.enquote("データ範囲が不正"));
			} else if (ERROR_TYPE_INVALID_DATE_FOR_SENSYUKAI.equals(info.type)) {
				cols.add(StringUtil.enquote("千趣会用日付情報が不正"));
			} else {
				cols.add(StringUtil.enquote(""));
			}
			// メッセージ
			cols.add(StringUtil.enquote(info.message));
			// 行番号
			cols.add(StringUtil.enquote(String.valueOf(info.nRow)));
			// 列番号
			cols.add(StringUtil.enquote(String.valueOf(info.nColumn)));
			// 入力データ
			cols.add(StringUtil.enquote(String.valueOf(info.inputRowData)));

			FileUtil.writer(cols, writer, dim); // 1レコード分のデータ書き出し
		}
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * エラーログの削除
	 *
	 */
	public static void removeErrorLog() {
		errorInfoList = new ArrayList<GeneralPurposeErrorInfo>();
	}

	/**
	 * 展示会データにかぎり、ワイルドカードを含むTEL番号／FAX番号／メールアドレスを空値に置換
	 *
	 * @param config
	 *            コンフィグ
	 * @param data
	 *            入力データ
	 * @return 変更後の入力データ
	 */
	private static String[] updateExhibitionInputData(
			GeneralPurposeEnqueteConfig config, String[] data) {
		String tel = data[config.mapping.get("V_TEL") - 1];
		if (StringUtil.containWildcard(tel, config.undecipherableMark)) {
			data[config.mapping.get("V_TEL") - 1] = "";
		}
		String fax = data[config.mapping.get("V_FAX") - 1];
		if (StringUtil.containWildcard(fax, config.undecipherableMark)) {
			data[config.mapping.get("V_FAX") - 1] = "";
		}
		String email = data[config.mapping.get("V_EMAIL") - 1];
		if (StringUtil.containWildcard(email, config.undecipherableMark)) {
			data[config.mapping.get("V_EMAIL") - 1] = "";
		}
		return data;
	}

	/**
	 * 固定値の出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @param config
	 *            コンフィグ
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputFixedValue(int nRow, List<String> cols,
			String[] data, QuestionSchema schema,
			GeneralPurposeEnqueteConfig config) {
		String value;
		if (StringUtil.isNotEmpty(schema.id)) {
			int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
			value = data[nIndex];
		} else if ("sequence".equals(schema.fixFormat)) { // 連番フォーマット
			value = String.valueOf(nRow);
		} else { // 通常の固定値
			value = schema.fixValue;
		}
		if (schema.convertFullString) {
			value = StringUtil.convertFullWidthString(value);
		}
		// 固定長連番化
		if (StringUtil.isNotEmpty(schema.fixLength)) {
			assert StringUtil.integerStringCheck(schema.fixLength);
			String mark = "0";
			if (StringUtil.isNotEmpty(schema.fixCompleteMark)) {
				mark = schema.fixCompleteMark;
			}
			value = StringUtil.convertFixedLengthData(value,
					StringUtil.toInteger(schema.fixLength), mark,
					schema.fixCompleteMarkPosition);
		}
		// insert-position属性、insert-value属性の属性値を考慮して出力文字列を加工
		if (StringUtil.isNotEmpty(schema.insertValue)) {
			value = StringUtil.insert(value, schema.insertPosition,
					schema.insertValue);
		}
		cols.add(StringUtil.enquote(value,
				config.outputEnquoteByDoubleQuotation));
		return cols;
	}

	/**
	 * メサゴメッセ標準TEL番号フォーマットの出力
	 *
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputSimpleAnswerWithMesagoTelFormat(
			GeneralPurposeEnqueteConfig config, List<String> cols, String[] data) {
		// 海外住所フラグ
		boolean isOversea = "1"
				.equals(data[config.mapping.get("V_OVERSEA") - 1]);
		// TEL番号
		String tel = data[config.mapping.get("V_TEL") - 1];
		AnalysisTelFax telAnalysis = new AnalysisTelFax(tel, isOversea);
		telAnalysis.execute(false);
		// TEL
		cols.add(StringUtil.enquote(tel, config.outputEnquoteByDoubleQuotation));
		// TEL（国番号）
		cols.add(StringUtil.enquote(telAnalysis.country,
				config.outputEnquoteByDoubleQuotation));
		// TEL（市外局番）
		cols.add(StringUtil.enquote(telAnalysis.areaCode,
				config.outputEnquoteByDoubleQuotation));
		// TEL（市内局番-番号）
		cols.add(StringUtil.enquote(telAnalysis.localCode,
				config.outputEnquoteByDoubleQuotation));
		// 内線番号
		cols.add(StringUtil.enquote(telAnalysis.extension,
				config.outputEnquoteByDoubleQuotation));

		return cols;
	}

	/**
	 * メサゴメッセ標準FAX番号フォーマットの出力
	 *
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputSimpleAnswerWithMesagoFaxFormat(
			GeneralPurposeEnqueteConfig config, List<String> cols, String[] data) {
		// 海外住所フラグ
		boolean isOversea = "1"
				.equals(data[config.mapping.get("V_OVERSEA") - 1]);

		// FAX番号
		String fax = data[config.mapping.get("V_FAX") - 1];
		AnalysisTelFax faxAnalysis = new AnalysisTelFax(fax, isOversea);
		faxAnalysis.execute(false);
		// FAX
		cols.add(StringUtil.enquote(fax, config.outputEnquoteByDoubleQuotation));
		// FAX（国番号）
		cols.add(StringUtil.enquote(faxAnalysis.country,
				config.outputEnquoteByDoubleQuotation));
		// FAX（市外局番）
		cols.add(StringUtil.enquote(faxAnalysis.areaCode,
				config.outputEnquoteByDoubleQuotation));
		// FAX（市内局番-番号）
		cols.add(StringUtil.enquote(faxAnalysis.localCode,
				config.outputEnquoteByDoubleQuotation));

		return cols;
	}

	/**
	 * グループ型のアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputGroupAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, List<String> cols,
			String[] data, QuestionSchema schema) {
		Map<String, String> calendarMap = new HashMap<String, String>();
		calendarMap.put("1", "大正");
		calendarMap.put("2", "昭和");
		calendarMap.put("3", "平成");
		if ("convertCalendarJpnToEng".equals(schema.dataFormat)) { // 和暦日付⇒西暦日付
																	// 変換
			if (schema.choices.size() != 0) {
				List<ChoiceSchema> children = schema.choices;
				/*
				 * 和暦、年、月、日を格納する列番号の取得
				 */
				String jpnCalendarId = children.get(0).children.get(0).childId;
				String yearId = children.get(1).children.get(0).childId;
				String monthId = children.get(2).children.get(0).childId;
				String dayId = children.get(3).children.get(0).childId;

				/*
				 * 和暦、年、月、日の値の取得
				 */
				String jpnCalendar = data[Integer.parseInt(jpnCalendarId) - 1];
				String year = data[Integer.parseInt(yearId) - 1];
				String month = data[Integer.parseInt(monthId) - 1];
				String day = data[Integer.parseInt(dayId) - 1];

				// 和暦年月日の文字列
				String japaneseDate = calendarMap.get(jpnCalendar) + year + "年"
						+ month + "月" + day + "日";

				// CalendarインスタンスおよびDateFormatインスタンスの準備
				Locale locale = new Locale("ja", "JP", "JP");
				Calendar calendar = Calendar.getInstance(locale);
				calendar.setLenient(false);
				DateFormat japaneseFormat = new SimpleDateFormat("GGGGy年M月d日",
						locale);

				// 和暦年月日の文字列のパーズ
				Date date = null;
				try {
					date = japaneseFormat.parse(japaneseDate);
				} catch (ParseException e) {
					storeErrorInfo(data, nRow, -1, ERROR_TYPE_INVALID_DATE); // デバッグ情報をコンソール出力
				}

				// 西暦に変換して出力
				if (date != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					cols.add(StringUtil.enquote(sdf.format(date),
							config.outputEnquoteByDoubleQuotation));
				} else {
					cols.add(StringUtil.enquote("",
							config.outputEnquoteByDoubleQuotation));
				}
			}
		}
		return cols;
	}

	/**
	 * 単純回答型のアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputSimpleAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, List<String> cols,
			String[] data, QuestionSchema schema) {
		int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
		List<String> output = new ArrayList<String>();
		String value = data[nIndex];
		/*
		 * 入力データの加工
		 */
		// start属性、end属性の属性値を考慮して出力文字列を算出
		if (schema.start == -1 && schema.end == -1) {
		} else if (schema.start != -1 && schema.end == -1) {
			if (value.length() >= schema.start) {
				value = value.substring(schema.start);
			} else {
				value = "";
			}
		} else if (schema.start == -1 && schema.end != -1) {
			if (value.length() >= schema.end) {
				value = value.substring(0, schema.end);
			} else {
				value = "";
			}
		} else {
			if (value.length() >= schema.start && value.length() >= schema.end) {
				value = value.substring(schema.start, schema.end);
			} else {
				value = "";
			}
		}
		/*
		 * 出力データの加工
		 */
		// match-by-regex属性、contains-by-regex属性の属性値を考慮して出力文字列を加工
		if (StringUtil.isNotEmpty(schema.replaceText)) {
			if (StringUtil.isNotEmpty(schema.matchByRegex)
					&& StringUtil.isEmpty(schema.matchGroupId)) {
				value = StringUtil.replace(value, schema.matchByRegex,
						schema.replaceText);
			} else if (StringUtil.isNotEmpty(schema.groupId)) {
				System.out
						.println("【XML構文エラー】match-group-id属性とreplace-text属性は同時に指定できません。");
			}
		} else {
			if (StringUtil.isNotEmpty(schema.matchByRegex)) {
				if (StringUtil.isNotEmpty(schema.matchGroupId)) {
					int groupId = Integer.parseInt(schema.matchGroupId);
					value = StringUtil.getMatchString(value,
							schema.matchByRegex, groupId);
				} else {
					value = StringUtil.getFirstMatchString(value,
							schema.matchByRegex);
				}
			} else if (StringUtil.isNotEmpty(schema.containsByRegex)) {
				value = StringUtil.find(value, schema.containsByRegex) ? "1"
						: "";
			}
		}
		// 必須制約条件による妥当性検証
		if (schema.required && StringUtil.isEmpty(value)) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_REQUIRED); // デバッグ情報をコンソール出力
		}
		// insert-position属性、insert-value属性の属性値を考慮して出力文字列を加工
		if (StringUtil.isNotEmpty(schema.insertValue)) {
			value = StringUtil.insert(value, schema.insertPosition,
					schema.insertValue);
		}
		// 正規表現による置換
		if (StringUtil.isNotEmpty(schema.replaceBeforeRegex)
				&& StringUtil.isNotEmpty(schema.replaceAfterValue)) {
			value = StringUtil.replace(value, schema.replaceBeforeRegex,
					schema.replaceAfterValue);
		}
		// data-format属性の属性値を考慮して出力文字列を加工
		if (StringUtil.isNotEmpty(schema.dataFormat)) {
			if ("7zip".equals(schema.dataFormat)) { // 7桁郵便番号フォーマット
				value = zipNormalize(value);
			} else if ("addr1".equals(schema.dataFormat)) { // 都道府県名の妥当性検証
				validateAddr1(data, nRow, nIndex, value);
			} else if ("email".equals(schema.dataFormat)) { // メールアドレスの妥当性検証
				validateEmailAddress(data, nRow, nIndex, value);
			} else if ("sensyukai_date".equals(schema.dataFormat)) { // 千趣会用日付の妥当性検証
				validateSensyukaiDate(data, nRow, nIndex, value);
			}
		}
		output.add(value);
		/*
		 * 連結データの出力
		 */
		if (schema.choices.size() != 0) {
			for (ChoiceSchema child : schema.choices) {
				List<ChildSchema> childSchemaList = child.children;
				ChildSchema childSchema = childSchemaList.get(0);
				int id = Integer.parseInt(childSchema.childId) - 1;
				output.add(data[id]);
			}
		}
		String delimit = config.concatDelimiterForSimpleAnswer; // デフォルトのデリミタ文字
		if (StringUtil.isNotEmpty(schema.delimit)) {
			delimit = schema.delimit; // 個別デリミタ文字
		}
		// デリミタ使用フラグがfalseの場合はデリミタを使用しない
		if (!schema.useDelimiter) {
			delimit = "";
		}
		String outputString = MesagoUtil.normalize(StringUtil
				.concatWithDelimit(delimit, output));
		if ("mesagoCompanyNameFormat".equals(schema.dataFormat)) { // メサゴメッセ標準会社名フォーマットである場合
			outputString = MesagoUtil.normalizeCompanyName(outputString);
		}
		// データ長に対する妥当性検証
		if (StringUtil.isNotEmpty(schema.dataLength)) {
			int length = StringUtil.isNotEmpty(outputString) ? outputString
					.length() : 0;
			if (length != StringUtil.toInteger(schema.dataLength)) {
				storeErrorInfo(data, nRow, nIndex + 1,
						ERROR_TYPE_DATA_LENGTH_ERROR);
			}
		}
		if (StringUtil.isNotEmpty(schema.dataMinLength)) {
			int length = StringUtil.isNotEmpty(outputString) ? outputString
					.length() : 0;
			if (length < StringUtil.toInteger(schema.dataMinLength)) {
				storeErrorInfo(data, nRow, nIndex + 1,
						ERROR_TYPE_DATA_LENGTH_ERROR);
			}
		}
		if (StringUtil.isNotEmpty(schema.dataMaxLength)) {
			int length = StringUtil.isNotEmpty(outputString) ? outputString
					.length() : 0;
			if (length > StringUtil.toInteger(schema.dataMaxLength)) {
				storeErrorInfo(data, nRow, nIndex + 1,
						ERROR_TYPE_DATA_LENGTH_ERROR);
			}
		}
		// 妥当性検証ルールに基づく妥当性検証
		GeneralValidationRulesSchema validationRule[] = config.validationRules
				.get(nIndex + 1);
		if (validationRule != null && StringUtil.isNotEmpty(outputString)) {
			boolean valid = false;
			for (GeneralValidationRulesSchema rule : validationRule) {
				if (StringUtil.isNotEmpty(rule.start)
						&& StringUtil.isNotEmpty(rule.end)) {
					if (!StringUtil.integerStringCheck(outputString)) {
						storeErrorInfo(data, nRow, nIndex + 1,
								ERROR_TYPE_INVALID_DATA_TYPE);
					} else {
						int nOutput = Integer.parseInt(outputString);
						int start = Integer.parseInt(rule.start);
						int end = Integer.parseInt(rule.end);
						boolean result = start <= nOutput && nOutput <= end;
						valid = rule.reverse ? !result : result;
						if (valid) {
							break;
						}
					}
				} else if (StringUtil.isNotEmpty(rule.regex)) {
					valid = StringUtil.find(outputString, rule.regex);
					if (valid) {
						break;
					}
				}
			}
			if (!valid) {
				storeErrorInfo(data, nRow, nIndex + 1,
						ERROR_TYPE_INVALID_DATA_RANGE);
			}
		}
		// データ出力（単純回答型）
		cols.add(StringUtil.enquote(outputString,
				config.outputEnquoteByDoubleQuotation));
		return cols;
	}

	/**
	 * 条件式によるアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputConditionAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, List<String> cols,
			String[] data, QuestionSchema schema) {
		int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
		String value = data[nIndex]; // 評価対象
		// 必須制約条件による妥当性検証
		if (schema.required && StringUtil.isEmpty(value)) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_REQUIRED); // デバッグ情報をコンソール出力
		}
		List<String> output = new ArrayList<String>();
		boolean match = false;
		for (IfElseSchema conditionSchema : schema.conditions) {
			if (conditionSchema.elseFlg && !match) { // else文を満たす場合
				if (conditionSchema.child != null) {
					ChildSchema childSchema = conditionSchema.child;
					int nChildIndex = Integer.parseInt(childSchema.childId) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
					value = data[nChildIndex];
					if (StringUtil.isNotEmpty(childSchema.matchByRegex)) {
						if (StringUtil.isNotEmpty(childSchema.matchGroupId)) {
							int groupId = Integer
									.parseInt(childSchema.matchGroupId);
							value = StringUtil.getMatchString(value,
									childSchema.matchByRegex, groupId);
						} else {
							value = StringUtil.getFirstMatchString(value,
									childSchema.matchByRegex);
						}
					}
					output.add(value);
				} else {
					output.add(conditionSchema.value);
				}
			} else { // if文の検証
				boolean result = StringUtil.find(value, conditionSchema.regex);
				if (result) { // if文を満たす場合
					if (conditionSchema.child != null) {
						ChildSchema childSchema = conditionSchema.child;
						int nChildIndex = Integer.parseInt(childSchema.childId) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
						value = data[nChildIndex];
						if (StringUtil.isNotEmpty(childSchema.matchByRegex)) {
							if (StringUtil.isNotEmpty(childSchema.matchGroupId)) {
								int groupId = Integer
										.parseInt(childSchema.matchGroupId);
								value = StringUtil.getMatchString(value,
										childSchema.matchByRegex, groupId);
							} else {
								value = StringUtil.getFirstMatchString(value,
										childSchema.matchByRegex);
							}
						}
						output.add(value);
					} else {
						output.add(conditionSchema.value);
					}
					match = true;
					if ("single".equals(schema.mode)) {
						break;
					}
				}
			}
		}
		String delimit = config.concatDelimiterForMultiAnswer;
		// デリミタ使用フラグがfalseの場合はデリミタを使用しない
		if (!schema.useDelimiter) {
			delimit = "";
		}

		// 出力文字列
		cols.add(StringUtil.enquote(
				StringUtil.concatWithDelimit(delimit, output),
				config.outputEnquoteByDoubleQuotation));
		return cols;
	}

	/**
	 * 1フラグ展開型のアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> output1ExpandAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, List<String> cols,
			String[] data, QuestionSchema schema) {
		int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
		String value = data[nIndex];
		if ("alphabet".equals(config.choiceAnswerInputType)) { // 入力タイプがアルファベットである場合
			value = StringUtil.convertHalfWidthString(value, false); // 全角→半角変換
			value = StringUtil.convertAtoNum(value); // アルファベット→数値変換
		}
		// 入力値に対する妥当性検証(オーバーフロー)
		if (!validateForMaxConstraint(value, schema.choices.size(), " ")) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_OVERFLOW); // デバッグ情報をコンソール出力
			for (int nCheckIndex = 1; nCheckIndex < schema.choices.size(); nCheckIndex++) {
				cols.add(StringUtil.enquote("",
						config.outputEnquoteByDoubleQuotation));
			}
			return cols;
		}
		int[] check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(value,
				schema.choices.size());// 1フラグ型配列
		// refer属性値が示す参照先Elementノードの項目に入力データが存在する場合はフラグ1を補完する
		if (config.correctChoiceSelectFa) {
			for (int nChoiceIndex = 0; nChoiceIndex < schema.choices.size(); nChoiceIndex++) {
				ChoiceSchema choice = schema.choices.get(nChoiceIndex);
				if (StringUtil.isNotEmpty(choice.referId)
						&& StringUtil.isNotEmpty(data[Integer
								.parseInt(choice.referId) - 1])) {
					check[nChoiceIndex + 1] = 1;
				}
			}
		}
		// シングル回答の場合は前方一致（2番目以降のフラグ1の除去）
		if ("single".equals(schema.mode)) {
			check = convertSingle(check);
		}
		// データ出力（1フラグ展開）
		for (int nCheckIndex = 1; nCheckIndex < check.length; nCheckIndex++) {
			cols.add(StringUtil.enquote(
					check[nCheckIndex] == 1 ? config.flg1Mark : "",
					config.outputEnquoteByDoubleQuotation));
		}
		return cols;
	}

	/**
	 * choice回答型のアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static String outputChoiceAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, String[] data,
			QuestionSchema schema) {
		int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
		String value = data[nIndex];
		if ("alphabet".equals(config.choiceAnswerInputType)) { // 入力タイプがアルファベットである場合
			value = StringUtil.convertAtoNum(value); // アルファベット→数値変換
		}
		// 入力値に対する妥当性検証(オーバーフロー)
		if (!validateForMaxConstraint(value, schema.choices.size(), " ")) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_OVERFLOW); // デバッグ情報をコンソール出力
			return "";
		}
		int[] check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(value,
				schema.choices.size());// 1フラグ型配列
		/* 【前準備】内訳回答やその他FAの入力データを検証して、フラグ1を修正する */
		if (config.correctChoiceSelectFa) {
			for (int nChoiceIndex = 0; nChoiceIndex < schema.choices.size(); nChoiceIndex++) {
				ChoiceSchema choice = schema.choices.get(nChoiceIndex);
				// 子childノードが示す項目に入力データが存在する場合はフラグ1を補完する
				List<ChildSchema> childSchemaList = choice.children;
				boolean exist = false;
				for (ChildSchema childSchema : childSchemaList) {
					if (StringUtil.isNotEmpty(childSchema.childId)
							&& StringUtil.isNotEmpty(data[Integer
									.parseInt(childSchema.childId) - 1])) {
						exist = true;
						break;
					}
				}
				if (exist) {
					check[nChoiceIndex + 1] = 1;
				}
				// refer属性値が示す参照先Elementノードの項目に入力データが存在する場合はフラグ1を補完する
				if (StringUtil.isNotEmpty(choice.referId)
						&& StringUtil.isNotEmpty(data[Integer
								.parseInt(choice.referId) - 1])) {
					check[nChoiceIndex + 1] = 1;
				}
			}
		}
		/* choice要素と入力データをみて出力データを決定 */
		List<String> output = new ArrayList<String>();
		for (int nCheckIndex = 1; nCheckIndex < check.length; nCheckIndex++) {
			if (check[nCheckIndex] == 1) { // フラグ1がたっている
				ChoiceSchema choice = schema.choices.get(nCheckIndex - 1);
				StringBuffer sb = new StringBuffer();
				sb.append(choice.value);// 選択肢のデータ名称を出力
				// 内訳回答が存在する場合
				List<ChildSchema> childSchemaList = choice.children;
				List<String> childValueList = new ArrayList<String>();
				for (ChildSchema childSchema : childSchemaList) {
					StringBuffer childSb = new StringBuffer();
					if (StringUtil.isNotEmpty(childSchema.childId)
							&& StringUtil.isNotEmpty(data[Integer
									.parseInt(childSchema.childId) - 1])) {
						if (StringUtil.isNotEmpty(childSchema.childbeforeValue)) {
							childSb.append(childSchema.childbeforeValue);
						}
						childSb.append(data[Integer
								.parseInt(childSchema.childId) - 1]);
						if (StringUtil.isNotEmpty(childSchema.childAfterValue)) {
							childSb.append(childSchema.childAfterValue);
						}
					}
					childValueList.add(childSb.toString());
				}
				// 内訳回答内容を出力
				if (childValueList.size() != 0) {
					sb.append("(");
					String delimit = config.concatDelimiterForChild;
					// デリミタ使用フラグがfalseの場合はデリミタを使用しない
					if (!schema.useDelimiter) {
						delimit = "";
					}
					sb.append(StringUtil.concatWithDelimit(delimit,
							childValueList));
					sb.append(")");
				}
				output.add(sb.toString());
				// シングル回答の場合は最初のフラグ1のみ処理
				if ("single".equals(schema.mode)) {
					break;
				}
			}
		}
		String delimit = config.concatDelimiterForMultiAnswer;
		// デリミタ使用フラグがfalseの場合はデリミタを使用しない
		if (!schema.useDelimiter) {
			delimit = "";
		}
		// データ出力（choice回答）
		return StringUtil.concatWithDelimit(delimit, output);
	}

	/**
	 * choice回答型のアンケートデータ出力
	 *
	 * @param nRow
	 *            入力データの行番号
	 * @param config
	 *            コンフィグ情報
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @param subGroupEvaluationResult
	 *            サブグループの評価結果
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static String outputChoiceAnswer(int nRow,
			GeneralPurposeEnqueteConfig config, String[] data,
			QuestionSchema schema, Map<String, String> subGroupEvaluationResult) {
		int nIndex = Integer.parseInt(schema.id) - 1;// 対象アンケート項目の入力データ上での出現位置（列番号）
		String value = data[nIndex];
		if ("alphabet".equals(config.choiceAnswerInputType)) { // 入力タイプがアルファベットである場合
			value = StringUtil.convertAtoNum(value); // アルファベット→数値変換
		}
		// 入力値に対する妥当性検証(オーバーフロー)
		if (!validateForMaxConstraint(value, schema.choices.size(), " ")) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_OVERFLOW); // デバッグ情報をコンソール出力
			return "";
		}
		int[] check = EnqueteUtil.convertMultiAnswerTo1FlgBuffer(value,
				schema.choices.size());// 1フラグ型配列
		/* 【前準備】内訳回答やその他FAの入力データを検証して、フラグ1を修正する */
		if (config.correctChoiceSelectFa) {
			for (int nChoiceIndex = 0; nChoiceIndex < schema.choices.size(); nChoiceIndex++) {
				ChoiceSchema choice = schema.choices.get(nChoiceIndex);
				// 子childノードが示す項目に入力データが存在する場合はフラグ1を補完する
				List<ChildSchema> childSchemaList = choice.children;
				boolean exist = false;
				for (ChildSchema childSchema : childSchemaList) {
					if (StringUtil.isNotEmpty(childSchema.childId)
							&& StringUtil.isNotEmpty(data[Integer
									.parseInt(childSchema.childId) - 1])) {
						exist = true;
						break;
					} else if (StringUtil.isNotEmpty(childSchema.groupId)) { // サブグループ参照が設定されている場合
						String subGroupEvaluation = subGroupEvaluationResult
								.get(childSchema.groupId);// 参照先のサブグループの評価結果
						if (StringUtil.isNotEmpty(subGroupEvaluation)) {
							exist = true;
							break;
						}
					}
				}
				if (exist) {
					check[nChoiceIndex + 1] = 1;
				}
				// refer属性値が示す参照先Elementノードの項目に入力データが存在する場合はフラグ1を補完する
				if (StringUtil.isNotEmpty(choice.referId)
						&& StringUtil.isNotEmpty(data[Integer
								.parseInt(choice.referId) - 1])) {
					check[nChoiceIndex + 1] = 1;
				}
			}
		}
		/* choice要素と入力データをみて出力データを決定 */
		List<String> output = new ArrayList<String>();
		for (int nCheckIndex = 1; nCheckIndex < check.length; nCheckIndex++) {
			if (check[nCheckIndex] == 1) { // フラグ1がたっている
				ChoiceSchema choice = schema.choices.get(nCheckIndex - 1);
				StringBuffer sb = new StringBuffer();
				sb.append(choice.value);// 選択肢のデータ名称を出力
				// 内訳回答が存在する場合
				List<ChildSchema> childSchemaList = choice.children;
				List<String> childValueList = new ArrayList<String>();
				for (ChildSchema childSchema : childSchemaList) {
					StringBuffer childSb = new StringBuffer();
					if (StringUtil.isNotEmpty(childSchema.childId)
							&& StringUtil.isNotEmpty(data[Integer
									.parseInt(childSchema.childId) - 1])) {
						if (StringUtil.isNotEmpty(childSchema.childbeforeValue)) {
							childSb.append(childSchema.childbeforeValue);
						}
						childSb.append(data[Integer
								.parseInt(childSchema.childId) - 1]);
						if (StringUtil.isNotEmpty(childSchema.childAfterValue)) {
							childSb.append(childSchema.childAfterValue);
						}
					} else if (StringUtil.isNotEmpty(childSchema.groupId)) { // サブグループ参照が設定されている場合
						String subGroupEvaluation = subGroupEvaluationResult
								.get(childSchema.groupId);// 参照先のサブグループの評価結果
						if (StringUtil.isNotEmpty(subGroupEvaluation)) {
							if (StringUtil
									.isNotEmpty(childSchema.childbeforeValue)) {
								childSb.append(childSchema.childbeforeValue);
							}
							childSb.append(subGroupEvaluation);
							if (StringUtil
									.isNotEmpty(childSchema.childAfterValue)) {
								childSb.append(childSchema.childAfterValue);
							}
						}
					}
					if (StringUtil.isNotEmpty(childSb.toString())) {
						childValueList.add(childSb.toString());
					}
				}
				// 内訳回答内容を出力
				if (childValueList.size() != 0) {
					sb.append("(");
					String delimit = config.concatDelimiterForChild;
					// デリミタ使用フラグがfalseの場合はデリミタを使用しない
					if (!schema.useDelimiter) {
						delimit = "";
					}
					sb.append(StringUtil.concatWithDelimit(delimit,
							childValueList));
					sb.append(")");
				}
				output.add(sb.toString());
				// シングル回答の場合は最初のフラグ1のみ処理
				if ("single".equals(schema.mode)) {
					break;
				}
			}
		}
		String delimit = config.concatDelimiterForMultiAnswer;
		// デリミタ使用フラグがfalseの場合はデリミタを使用しない
		if (!schema.useDelimiter) {
			delimit = "";
		}
		// データ出力（choice回答）
		return StringUtil.concatWithDelimit(delimit, output);
	}

	/**
	 * 最大数に対する制約に基づく妥当性検証
	 *
	 * @param value
	 *            検証対象の入力値
	 * @param max
	 *            許容される最大値
	 * @return 検証結果のブール値(妥当性違反がない場合はtrueを返却)
	 */
	public static boolean validateForMaxConstraint(String value, int max,
			String delimit) {
		if (StringUtil.isNotEmpty(value)) {
			String[] buff = value.split(delimit);
			for (String data : buff) {
				if (StringUtil.integerStringCheck(data)
						&& max < Integer.parseInt(data)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 前方一致の原則でシングル回答に変換
	 *
	 * @param check
	 *            チェック配列(1,0配列)
	 * @return 変換後のチェック配列(1,0配列)
	 */
	private static int[] convertSingle(int[] check) {
		boolean exist = false;
		for (int nIndex = 0; nIndex < check.length; nIndex++) {
			if (check[nIndex] == 1 && !exist) {
				exist = true;
			} else if (check[nIndex] == 1 && exist) {
				check[nIndex] = 0;
			}
		}
		return check;
	}

	/**
	 * 指定プロパティの値を取得
	 *
	 * @param children
	 *            　property要素ノードの<b>List</b>
	 * @param target
	 *            プロパティ名
	 * @return プロパティの値
	 */
	private static String getPropertyValue(List<Element> children, String target) {
		assert StringUtil.isNotEmpty(target);
		for (Element property : children) {
			String name = property.getAttribute("name");
			if (target.equals(name)) {
				String value = property.getAttribute("value");
				return normalize(value);
			}
		}
		return null;
	}

	/**
	 * config.xmlの属性値の正規化
	 *
	 * @param value
	 *            属性値
	 * @return 正規化後の属性値
	 */
	private static String normalize(String value) {
		if (StringUtil.isNotEmpty(value)) {
			value = value.replaceAll("\\\\t", "\t");
		}
		return value;
	}

	/**
	 * 【展示会納品データ集計用】 海外住所フラグおよび不備フラグの出力
	 *
	 * @param config
	 *            コンフィグ情報
	 * @param cols
	 *            出力データ用<b>List</b>
	 * @param data
	 *            入力データ
	 * @param schema
	 *            アンケートスキーマ情報
	 * @return データ出力後の出力データ<b>List</b>
	 */
	private static List<String> outputExhibiInfo(
			GeneralPurposeEnqueteConfig config, List<String> cols, String[] data) {
		GeneralExhibiUserDataDto userdata = new GeneralExhibiUserDataDto();
		// 氏名情報
		userdata.cardInfo.V_NAME1 = data[config.mapping.get("NAME1") - 1];
		userdata.cardInfo.V_NAME2 = data[config.mapping.get("NAME2") - 1];
		// 会社情報
		userdata.cardInfo.V_CORP = data[config.mapping.get("V_CORP") - 1];
		userdata.cardInfo.V_DEPT1 = data[config.mapping.get("V_DEPT1") - 1];
		userdata.cardInfo.V_DEPT2 = data[config.mapping.get("V_DEPT2") - 1];
		userdata.cardInfo.V_DEPT3 = data[config.mapping.get("V_DEPT3") - 1];
		userdata.cardInfo.V_DEPT4 = data[config.mapping.get("V_DEPT4") - 1];
		userdata.cardInfo.V_BIZ1 = data[config.mapping.get("V_BIZ1") - 1];
		userdata.cardInfo.V_BIZ2 = data[config.mapping.get("V_BIZ2") - 1];
		userdata.cardInfo.V_BIZ3 = data[config.mapping.get("V_BIZ3") - 1];
		userdata.cardInfo.V_BIZ4 = data[config.mapping.get("V_BIZ4") - 1];
		// 連絡先情報
		userdata.cardInfo.V_ZIP = data[config.mapping.get("V_ZIP") - 1];
		userdata.cardInfo.V_ADDR1 = data[config.mapping.get("V_ADDR1") - 1];
		userdata.cardInfo.V_ADDR2 = data[config.mapping.get("V_ADDR2") - 1];
		userdata.cardInfo.V_ADDR3 = data[config.mapping.get("V_ADDR3") - 1];
		userdata.cardInfo.V_ADDR4 = data[config.mapping.get("V_ADDR4") - 1];
		userdata.cardInfo.V_ADDR5 = data[config.mapping.get("V_ADDR5") - 1];
		userdata.cardInfo.V_TEL = data[config.mapping.get("V_TEL") - 1];
		userdata.cardInfo.V_FAX = data[config.mapping.get("V_FAX") - 1];
		userdata.cardInfo.V_EMAIL = data[config.mapping.get("V_EMAIL") - 1];
		// 海外住所フラグ
		userdata.cardInfo.V_OVERSEA = data[config.mapping.get("V_OVERSEA") - 1];
		String lackFlg = getLackFlg(userdata, config.undecipherableMark); // 原票不備フラグ
		cols.add(StringUtil.enquote(lackFlg,
				config.outputEnquoteByDoubleQuotation)); // 原票不備フラグの出力
		cols.add(StringUtil.enquote(userdata.validationErrResult,
				config.outputEnquoteByDoubleQuotation)); // 原票不備詳細の出力
		// cols.add(isOversea(userdata) && StringUtil.isEmpty(lackFlg) ?
		// StringUtil
		// .enquote("1") : StringUtil.enquote("")); // 海外住所フラグの出力
		return cols;
	}

	/**
	 * 【展示会納品データ集計用】海外住所フラグの検証
	 *
	 * @param userdata
	 *            <b>ExhibiUserDataDto</b>
	 *
	 * @return 検証結果のブール値
	 */
	public static boolean isOversea(UserDataDto userdata) {
		// boolean check1 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR1);
		// boolean check2 = StringUtil.isNotEmpty(userdata.cardInfo.V_ADDR2);
		// boolean check3 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR3);
		// boolean check4 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR4);
		// boolean check5 = StringUtil.isEmpty(userdata.cardInfo.V_ADDR5);
		// return check1 && check2 && check3 && check4 && check5;
		return StringUtil.isNotEmpty(userdata.cardInfo.V_OVERSEA);
	}

	/**
	 * 【展示会納品データ集計用】不備フラグの特定
	 *
	 * @param userdata
	 *            <b>ExhibiUserDataDto</b>
	 * @param undecipherableMark
	 *            不明マーク
	 * @return 不備フラグ
	 */
	private static String getLackFlg(UserDataDto userdata,
			String undecipherableMark) {
		GeneralExhibiValidator validator = new GeneralExhibiValidator(
				undecipherableMark);
		validator.validate(userdata, "1"); // ワイルドカードの存在チェック
		validator.validate(userdata, "2"); // 氏名、連絡先情報に対する妥当性検証
		boolean lack1Flg = !validator.getResult1(); // 不備フラグ1
		boolean lack2Flg = !validator.getResult2(); // 不備フラグ2
		userdata.validationErrResult = validator.getResultDetail(); // タイプ2に対する妥当性検証違反の詳細情報を格納
		if (lack1Flg && !lack2Flg) {
			return "1";
		} else if (lack2Flg) {
			return "2";
		} else {
			return "";
		}
	}

	/**
	 * 郵便番号の正規化
	 *
	 * @param zip
	 *            郵便番号
	 * @return　正規化後の郵便番号
	 */
	private static String zipNormalize(String zip) {
		if (is7Zip(zip)) {
			return zip.substring(0, 3) + "-" + zip.substring(3);
		}
		return zip;
	}

	/**
	 * 都道府県名に対する妥当性検証
	 *
	 * @param data
	 *            入力データ
	 * @param nRow
	 *            誤り箇所の行番号
	 * @param nIndex
	 *            誤り箇所の列番号
	 * @param addr1
	 *            都道府県
	 */
	private static void validateAddr1(String[] data, int nRow, int nIndex,
			String addr1) {
		String keywords[] = { "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
				"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県", "新潟県", "富山県",
				"石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県", "三重県", "滋賀県",
				"京都府", "大阪府", "兵庫県", "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県",
				"山口県", "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県", "熊本県",
				"大分県", "宮崎県", "鹿児島県", "沖縄県" };
		if (StringUtil.isNotEmpty(addr1)
				&& !StringUtil.contains(keywords, addr1)) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_INVALID_ADDR1); // デバッグ情報をコンソール出力
		}
	}

	/**
	 * メールアドレスに対する妥当性検証
	 *
	 * @param data
	 *            入力データ
	 * @param nRow
	 *            誤り箇所の行番号
	 * @param nIndex
	 *            誤り箇所の列番号
	 * @param email
	 *            メールアドレス
	 */
	private static void validateEmailAddress(String[] data, int nRow,
			int nIndex, String email) {
		if (StringUtil.isNotEmpty(email) && !StringUtil.isEmailAddress(email)) {
			storeErrorInfo(data, nRow, nIndex + 1, ERROR_TYPE_INVALID_EMAIL); // デバッグ情報をコンソール出力
		}
	}

	/**
	 * 千趣会用日付型に対する妥当性検証
	 *
	 * @param data
	 *            入力データ
	 * @param nRow
	 *            誤り箇所の行番号
	 * @param nIndex
	 *            誤り箇所の列番号
	 * @param dateStr
	 *            日付文字列
	 */
	private static void validateSensyukaiDate(String[] data, int nRow,
			int nIndex, String dateStr) {
		if (StringUtil.isNotEmpty(dateStr)) {
			boolean check = true;
			Date date = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			format.setLenient(false); // 日付を厳密にバリデーションチェックする
			try {
				date = format.parse(dateStr);
			} catch (ParseException e) {
				check = false;
			}
			if (check) {
				Date currentDate = new Date(); // 現在日時
				if (currentDate.compareTo(date) == -1) { // 未来日付である場合
					storeErrorInfo(data, nRow, nIndex + 1,
							ERROR_TYPE_INVALID_DATE_FOR_SENSYUKAI); // デバッグ情報をコンソール出力
				}
			} else { // 日付情報が不正
				storeErrorInfo(data, nRow, nIndex + 1,
						ERROR_TYPE_INVALID_DATE_FOR_SENSYUKAI); // デバッグ情報をコンソール出力
			}
		}
	}

	/**
	 * ハイフンなし7桁郵便番号であるか否かの検証
	 *
	 * @param zip
	 *            郵便番号
	 * @return 検証結果のブール値
	 */
	private static boolean is7Zip(String zip) {
		if (StringUtil.isNotEmpty(zip)) {
			boolean digitCheck = zip.length() == 7;
			boolean isValid = StringUtil.isNumeric(zip);
			return digitCheck && isValid;
		}
		return false;
	}

	/**
	 * 入力データの誤り箇所をコンソール出力
	 *
	 * @param data
	 *            入力データ
	 * @param nRow
	 *            誤り箇所の行番号
	 * @param nColumn
	 *            誤り箇所の列番号
	 * @param errorType
	 *            エラー種別
	 */
	private static void storeErrorInfo(String[] data, int nRow, int nColumn,
			String errorType) {
		GeneralPurposeErrorInfo errorInfo = new GeneralPurposeErrorInfo();
		errorInfo.type = errorType;
		if (ERROR_TYPE_REQUIRED.equals(errorType)) {
			errorInfo.message = "空値である必須項目が存在";
		} else if (ERROR_TYPE_OVERFLOW.equals(errorType)) {
			errorInfo.message = "オーバーフロー";
		} else if (ERROR_TYPE_INVALID_ADDR1.equals(errorType)) {
			errorInfo.message = "都道府県名が不正";
		} else if (ERROR_TYPE_DATA_LENGTH_ERROR.equals(errorType)) {
			errorInfo.message = "文字列長が不正";
		} else if (ERROR_TYPE_INVALID_EMAIL.equals(errorType)) {
			errorInfo.message = "メールアドレスが不正";
		} else if (ERROR_TYPE_INVALID_DATE.equals(errorType)) {
			errorInfo.message = "日付情報が不正";
		} else if (ERROR_TYPE_INVALID_DATA_TYPE.equals(errorType)) {
			errorInfo.message = "データタイプが不正";
		} else if (ERROR_TYPE_INVALID_DATA_RANGE.equals(errorType)) {
			errorInfo.message = "データ範囲が不正(開始)";
		} else if (ERROR_TYPE_INVALID_DATE_FOR_SENSYUKAI.equals(errorType)) {
			errorInfo.message = "千趣会用日付情報が不正";
		}
		errorInfo.nRow = nRow;
		errorInfo.nColumn = nColumn;
		errorInfo.inputRowData = StringUtil.concat(data);
		errorInfoList.add(errorInfo);
	}

}