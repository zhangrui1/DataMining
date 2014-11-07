
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String caption = (String) request.getAttribute("caption");
	@SuppressWarnings("unchecked")
	List<Integer> e3single = (ArrayList<Integer>) request
			.getAttribute("e3single");
	@SuppressWarnings("unchecked")
	List<Integer> e3multi = (ArrayList<Integer>) request
			.getAttribute("e3multi");
	@SuppressWarnings("unchecked")
	List<Integer> e4single = (ArrayList<Integer>) request
			.getAttribute("e4single");
	@SuppressWarnings("unchecked")
	List<Integer> e4multi = (ArrayList<Integer>) request
			.getAttribute("e4multi");
	@SuppressWarnings("unchecked")
	List<Integer> e5single = (ArrayList<Integer>) request
			.getAttribute("e5single");
	@SuppressWarnings("unchecked")
	List<Integer> e5multi = (ArrayList<Integer>) request
			.getAttribute("e5multi");
	@SuppressWarnings("unchecked")
	List<Integer> e6single = (ArrayList<Integer>) request
			.getAttribute("e6single");
	@SuppressWarnings("unchecked")
	List<Integer> e6multi = (ArrayList<Integer>) request
			.getAttribute("e6multi");
	@SuppressWarnings("unchecked")
	List<String> categoryList = (ArrayList<String>) request
			.getAttribute("categoryList");

	int e3singleTotal = (Integer) request.getAttribute("e3singleTotal");
	int e3multiTotal = (Integer) request.getAttribute("e3multiTotal");
	int e4singleTotal = (Integer) request.getAttribute("e4singleTotal");
	int e4multiTotal = (Integer) request.getAttribute("e4multiTotal");
	int e5singleTotal = (Integer) request.getAttribute("e5singleTotal");
	int e5multiTotal = (Integer) request.getAttribute("e5multiTotal");
	int e6singleTotal = (Integer) request.getAttribute("e6singleTotal");
	int e6multiTotal = (Integer) request.getAttribute("e6multiTotal");

	NumberFormat nfNum = NumberFormat.getNumberInstance();
%>
<html>
<head>
<title>集計結果</title>
<style type="text/css">
#mytable {
	width: 700px;
	margin: 0 0 0 1px;
	padding: 0;
	border: 0;
	border-spacing: 0;
	border-collapse: collapse;
}

caption {
	padding: 0 0 5px 0;
	font: italic 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	text-align: right;
}

th {
	font: bold 11px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	color: #4f6b72;
	border: 1px solid #c1dad7;
	letter-spacing: 2px;
	text-transform: uppercase;
	text-align: left;
	padding: 6px 6px 6px 12px;
	background: #cae8ea url("img/css/bg_header.jpg") no-repeat;
}

th.nobg {
	border-top: 0;
	border-left: 0;
	background: none;
}

td {
	border: 1px solid #c1dad7;
	background: #fff;
	padding: 6px 6px 6px 12px;
	color: #4f6b72;
}

td.alt {
	background: #F5FAFA;
	color: #797268;
}

th.spec {
	background: #fff url("img/css/bullet1.gif") no-repeat;
	font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
}

th.specalt {
	background: #f5fafa url("img/css/bullet2.gif") no-repeat;
	font: bold 10px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
	color: #797268;
}
</style>
</head>
<body>
	<div align="center">
		<div>東3ホール</div>
		<table id="mytable" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=caption%></caption>
			<tr>
				<th></th>
				<th>シングル</th>
				<th>マルチ</th>
			</tr>
			<%
				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=categoryList.get(nIndex)%></td>
				<td class="alt" align="right"><%=nfNum.format(e3single.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e3multi.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="spec" align="right">合計</td>
				<td class="spec" align="right"><%=nfNum.format(e3singleTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e3multiTotal)%></td>
			</tr>
		</table>
		<p />
		<div>東4ホール</div>
		<table id="mytable" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=caption%></caption>
			<tr>
				<th></th>
				<th>シングル</th>
				<th>マルチ</th>
			</tr>
			<%
				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=categoryList.get(nIndex)%></td>
				<td class="alt" align="right"><%=nfNum.format(e4single.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e4multi.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="spec" align="right">合計</td>
				<td class="spec" align="right"><%=nfNum.format(e4singleTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e4multiTotal)%></td>
			</tr>
		</table>
		<p />
		<div>東5ホール</div>
		<table id="mytable" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=caption%></caption>
			<tr>
				<th></th>
				<th>シングル</th>
				<th>マルチ</th>
			</tr>
			<%
				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=categoryList.get(nIndex)%></td>
				<td class="alt" align="right"><%=nfNum.format(e5single.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e5multi.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="spec" align="right">合計</td>
				<td class="spec" align="right"><%=nfNum.format(e5singleTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e5multiTotal)%></td>
			</tr>
		</table>
		<p />
		<div>東6ホール</div>
		<table id="mytable" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=caption%></caption>
			<tr>
				<th></th>
				<th>シングル</th>
				<th>マルチ</th>
			</tr>
			<%
				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=categoryList.get(nIndex)%></td>
				<td class="alt" align="right"><%=nfNum.format(e6single.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e6multi.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="spec" align="right">合計</td>
				<td class="spec" align="right"><%=nfNum.format(e6singleTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e6multiTotal)%></td>
			</tr>
		</table>
		<br /> <br /> <a
			href="html/prompt/bwjGeneralPurposePromptReport.html">もどる</a>
	</div>
</body>
</html>