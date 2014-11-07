package jp.co.freedom.common.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * XMLDOM(Xerces)編集ユーティリティクラス
 * 
 * @author フリーダム・グループ
 * 
 */
public class DOMUtil {

	/**
	 * DOMライター
	 * 
	 * @param document
	 *            出力対象<b>Document</b>
	 * @param path
	 *            出力ファイルパス
	 * @return ファイル出力処理の成否のブール値
	 */
	public static boolean domWriter(Document document, String path) {
		assert StringUtil.isNotEmpty(path);
		File file = new File(path);
		// Transformerインスタンスの生成
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		// Transformerの設定
		transformer.setOutputProperty("indent", "yes"); // 改行指定
		transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング
		// XMLファイルの作成
		try {
			transformer.transform(new DOMSource(document), new StreamResult(
					file));
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * XMLファイルのパーズ
	 * 
	 * @param directoryPath
	 *            　XMLファイルのファイルパス（絶対パス）
	 * @return <b>Document</b>
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document domParser(String directoryPath) throws SAXException,
			IOException {
		DOMParser parser = new DOMParser();
		// 妥当性検証の設定
		parser.setFeature("http://xml.org/sax/features/validation", true);
		parser.setFeature("http://xml.org/sax/features/namespaces", true);
		parser.setFeature("http://apache.org/xml/features/validation/schema",
				true);
		// XML文書の取得
		parser.parse(directoryPath);
		// Documentの取得
		Document document = parser.getDocument();
		return document;
	}

	/**
	 * 指定<b>Element</b>ノードの子孫を対象にしたDOMトラバーサル
	 * 
	 * @param root
	 *            探索開始<b>Element</b>ノード
	 * @param name
	 *            探索対象<b>Element</b>ノードのローカル名
	 * @param namespaceUri
	 *            探索対象<b>Element</b>ノードのローカル名
	 * @return 探索の結果、発見された<b>Element</b>ノード
	 */
	public static Element searchElement(Element root, String name,
			String namespaceUri) {
		Document document = root.getOwnerDocument();
		DOMImplementation domImpl = document.getImplementation();
		if (domImpl.hasFeature("Traversal", "2.0")) {
			DocumentTraversal traversal = (DocumentTraversal) document;
			TreeWalker walker = traversal.createTreeWalker(root,
					NodeFilter.SHOW_ALL, null, false);
			for (Node current = root; current != null; current = walker
					.nextNode()) {
				if (current != null && current instanceof Element) {
					Element element = (Element) current;
					if (equals(element, element.getLocalName(),
							element.getNamespaceURI())) {
						return element;
					}
				}
			}
		}
		return null;
	}

	/**
	 * XPath式をNodeSetとして評価する
	 * 
	 * 【XPath式の例】
	 * /*[local-name()='config']/*[local-name()='properties']//*[local
	 * -name()='property' and position()=2]/@type
	 * 
	 * @param xpathStr
	 *            XPath式
	 * @param document
	 *            <b>Document</b>ノード
	 * @return XPath式を評価して得られる<b>NodeList</b>
	 * @throws XPathExpressionException
	 */
	public static NodeList evaluateXPathAsNodeSet(String xpathStr,
			Document document) throws XPathExpressionException {
		assert StringUtil.isNotEmpty(xpathStr) && document != null;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xpathStr);
		Object result = expr.evaluate(document, XPathConstants.NODESET);
		if (result != null && result instanceof NodeList) {
			return (NodeList) result;
		}
		return null;
	}

	/**
	 * XPath式を文字列として評価する
	 * 
	 * 【XPath式の例】
	 * /*[local-name()='config']/*[local-name()='properties']//*[local
	 * -name()='property' and position()=2]/@type
	 * 
	 * @param xpathStr
	 *            XPath式
	 * @param document
	 *            <b>Document</b>ノード
	 * @return XPath式を評価して得られる文字列
	 * @throws XPathExpressionException
	 */
	public static String evaluateXPathAsString(String xpathStr,
			Document document) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xpathStr);
		Object result = expr.evaluate(document, XPathConstants.STRING);
		if (result != null && result instanceof String) {
			return (String) result;
		}
		return null;
	}

	/**
	 * XPath式を数値として評価する
	 * 
	 * 【XPath式の例】
	 * /*[local-name()='config']/*[local-name()='properties']//*[local
	 * -name()='property' and position()=2]/@type
	 * 
	 * @param xpathStr
	 *            XPath式
	 * @param document
	 *            <b>Document</b>ノード
	 * @return XPath式を評価して得られる数値
	 * @throws XPathExpressionException
	 */
	public static Number evaluateXPathAsNumber(String xpathStr,
			Document document) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xpathStr);
		Object result = expr.evaluate(document, XPathConstants.NUMBER);
		if (result != null && result instanceof Number) {
			return (Number) result;
		}
		return null;
	}

	/**
	 * node1とnode2が同一<b>Node</b>であるか否かの検証
	 * 
	 * @param node1
	 *            ノード1
	 * @param node2
	 *            ノード2
	 * @return 検証結果のブール値
	 */
	@SuppressWarnings("unused")
	private static boolean equals(Node node1, Node node2) {
		assert node1 != null && node2 != null;
		String name1 = node1.getLocalName();
		String name2 = node2.getLocalName();
		String uri1 = node1.getNamespaceURI();
		String uri2 = node2.getNamespaceURI();
		if (uri1 == null && uri2 == null) {
			return name1.equals(name2);
		} else if (uri1 != null && uri2 != null) {
			return name1.equals(name2);
		} else {
			return false;
		}
	}

	/**
	 * node1とnode2（ローカル名：name2／名前空間URI参照:uri2）が同一<b>Node</b>であるか否かの検証
	 * 
	 * @param node1
	 *            ノード1
	 * @param name2
	 *            ノード2のローカル名
	 * @param uri2
	 *            ノード2の名前空間URI参照
	 * @return 検証結果のブール値
	 */
	private static boolean equals(Node node1, String name2, String uri2) {
		assert node1 != null;
		String name1 = node1.getLocalName();
		String uri1 = node1.getNamespaceURI();
		if (uri1 == null && uri2 == null) {
			return name1.equals(name2);
		} else if (uri1 != null && uri2 != null) {
			return name1.equals(name2);
		} else {
			return false;
		}
	}

	/**
	 * 最初の<b>Element</b>ノードを取得
	 * 
	 * @param parent
	 *            　親<b>Node</b>
	 * @return 最初の<b>Element</b>ノード
	 */
	public static Element getFirstChildElement(Node parent) {
		assert parent != null;
		for (Node child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child != null && child instanceof Element) {
				return (Element) child;
			}
		}
		return null;
	}

	/**
	 * 直近の兄<b>Element</b>ノードを取得
	 * 
	 * @param parent
	 *            　親<b>Node</b>
	 * @return 最初の<b>Element</b>ノード
	 */
	public static Element getPreviousSiblingElement(Node node) {
		assert node != null;
		for (Node child = node.getPreviousSibling(); child != null; child = child
				.getPreviousSibling()) {
			if (child != null && child instanceof Element) {
				return (Element) child;
			}
		}
		return null;
	}

	/**
	 * 直近の弟<b>Element</b>ノードを取得
	 * 
	 * @param parent
	 *            　親<b>Node</b>
	 * @return 最初の<b>Element</b>ノード
	 */
	public static Element getNextSiblingElement(Node node) {
		assert node != null;
		for (Node child = node.getNextSibling(); child != null; child = child
				.getNextSibling()) {
			if (child != null && child instanceof Element) {
				return (Element) child;
			}
		}
		return null;
	}

	/**
	 * 指定した<b>Element</b>ノードの属性値を取得
	 * 
	 * @param node
	 *            　<b>Element</b>
	 * @param attrQName
	 *            属性ノードのQName
	 * @return
	 */
	public static String getAttributeValue(Element node, String attrQName) {
		assert node != null;
		Attr attr = node.getAttributeNode(attrQName);
		if (attr != null) {
			return attr.getValue();
		}
		return null;
	}

	/**
	 * 指定ノードが子<b>Element</b>ノードを持つかどうかの検証
	 * 
	 * @param parent
	 *            指定親ノード
	 * @return 検証結果のブール値
	 */
	public static boolean hasChild(Element parent) {
		assert parent != null;
		List<Element> children = getChildrenElement(parent);
		return children.size() != 0;
	}

	/**
	 * 指定ノードの子<b>Element</b>ノードの<b>List</b>を取得
	 * 
	 * @param parent
	 *            　指定ノード
	 * @return　子<b>Element</b>ノードの<b>List</b>
	 */
	public static List<Element> getChildrenElement(Element parent) {
		assert parent != null;
		List<Element> children = new ArrayList<Element>();
		for (Node child = parent.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child != null && child instanceof Element) {
				children.add((Element) child);
			}
		}
		return children;
	}

	/**
	 * 指定したQNameを持つ子<b>Element</b>ノード数を算出する
	 * 
	 * @param parent
	 *            親<b>Element</b>ノード
	 * @param qname
	 *            検索対象のQName
	 * @return 子<b>Element</b>ノード数
	 */
	public static int getChildElementCount(Element parent, String qname) {
		assert parent != null && StringUtil.isNotEmpty(qname);
		List<Element> children = getChildrenElement(parent);
		int count = 0;
		for (Element child : children) {
			if (child != null) {
				if (qname.equals(child.getNodeName())) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 指定したQNameを持つ子<b>Element</b>ノードを取得する
	 * 
	 * @param parent
	 *            親<b>Element</b>ノード
	 * @param qname
	 *            検索対象のQName
	 * @return 子<b>Element</b>ノード
	 */
	public static Element getChildElement(Element parent, String qname) {
		assert parent != null && StringUtil.isNotEmpty(qname);
		List<Element> children = getChildrenElement(parent);
		for (Element child : children) {
			if (child != null) {
				if (qname.equals(child.getNodeName())) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * 指定ノードのテキストデータを取得
	 * 
	 * @param parent
	 *            対象ノード
	 * @return テキストデータ
	 */
	public static String getData(Element parent) {
		assert parent != null;
		Node node = parent.getFirstChild();
		if (node != null && node instanceof Text) {
			Text text = (Text) node;
			return text.getData();
		}
		return null;
	}
}