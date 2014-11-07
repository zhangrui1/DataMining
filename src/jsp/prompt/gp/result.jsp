
<%@page import="java.util.LinkedHashMap"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Map.Entry"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String backURL = (String) request.getAttribute("backURL");
	String[] days = (String[]) request.getAttribute("days");
	@SuppressWarnings("unchecked")
	List<Integer> newRegist = (ArrayList<Integer>) request
			.getAttribute("newRegist");
	@SuppressWarnings("unchecked")
	List<Integer> repeat = (ArrayList<Integer>) request
			.getAttribute("repeat");
	@SuppressWarnings("unchecked")
	List<Integer> dupRegist = (ArrayList<Integer>) request
			.getAttribute("dupRegist");
	@SuppressWarnings("unchecked")
	Map<String, Integer> repeatDetail1 = (LinkedHashMap<String, Integer>) request
			.getAttribute("repeatDetail1");
	@SuppressWarnings("unchecked")
	Map<String, Integer> repeatDetail2 = (LinkedHashMap<String, Integer>) request
			.getAttribute("repeatDetail2");
	@SuppressWarnings("unchecked")
	Map<String, Integer> repeatDetail3 = (LinkedHashMap<String, Integer>) request
			.getAttribute("repeatDetail3");
	@SuppressWarnings("unchecked")
	Map<String, Integer> repeatDetail4 = (LinkedHashMap<String, Integer>) request
			.getAttribute("repeatDetail4");
	int totalNewRegist = (Integer) request
			.getAttribute("totalNewRegist");
	int totalRepeat = (Integer) request.getAttribute("totalRepeat");
	int totaldupRegist = (Integer) request
			.getAttribute("totaldupRegist");

	NumberFormat nfNum = NumberFormat.getNumberInstance();
%>
<html>
<head>
<title>集計結果</title>
<style type="text/css">
html,body {
	height: 100%;
} /*高さ100%に指定*/
.centerMiddle {
	margin: -200px 0 0 -300px; /*縦横の半分をネガティブマージンでずらす*/
	position: absolute; /*body要素に対して絶対配置*/
	top: 30%; /*上端を中央に*/
	left: 50%; /*左端を中央に*/
	width: 600px; /*横幅*/
	height: 400px; /*縦幅*/
	background-color: #fff;
}

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
		<table id="mytable" width="100%" style="BACKGROUND-COLOR: #a9a9a9">
			<tr>
				<th></th>
				<th>総数（重複含）</th>
				<th>新規登録数</th>
				<th>リピーター数</th>
				<th>当日のみ</th>
			</tr>
			<%
				for (int nIndex = 0; nIndex < days.length; nIndex++) {
			%>
			<tr>
				<th><%=days[nIndex] + "日"%></th>
				<td class="alt" align="right"><%=nfNum.format(dupRegist.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(nIndex)
						+ repeat.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="alt" align="left">合計</td>
				<td class="alt" align="right"><%=nfNum.format(totaldupRegist)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalNewRegist)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalRepeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalNewRegist + totalRepeat)%></td>
			</tr>
		</table>
		<br /> <br />
		<%
			if (days.length >= 3) {
		%>
		<table id="mytable" width="100%" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=days[2]%>日のリピーター詳細</caption>
			<tr>
				<%
					List<Map.Entry<String, Integer>> entries3 = new ArrayList<Map.Entry<String, Integer>>(
								repeatDetail3.entrySet());
						for (Map.Entry<String, Integer> entry : entries3) {
				%>
				<th><%=entry.getKey()%></th>
				<%
					}
				%>
				<th>リピーター合計</th>
			</tr>
			<tr>
				<%
					for (Map.Entry<String, Integer> entry : entries3) {
				%>
				<td class="alt" align="right"><%=nfNum.format(entry.getValue())%></td>
				<%
					}
				%>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(2))%></td>
			</tr>
		</table>
		<%
			}
		%>
		<%
			if (days.length >= 4) {
		%>
		<table id="mytable" width="100%" style="BACKGROUND-COLOR: #a9a9a9">
			<caption><%=days[3]%>日のリピーター詳細</caption>
			<tr>
				<%
					List<Map.Entry<String, Integer>> entries4 = new ArrayList<Map.Entry<String, Integer>>(
								repeatDetail4.entrySet());
						for (Map.Entry<String, Integer> entry : entries4) {
				%>
				<th><%=entry.getKey()%></th>
				<%
					}
				%>
				<th>リピーター合計</th>
			</tr>
			<tr>
				<%
					for (Map.Entry<String, Integer> entry : entries4) {
				%>
				<td class="alt" align="right"><%=nfNum.format(entry.getValue())%></td>
				<%
					}
				%>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(3))%></td>
			</tr>
		</table>
		<%
			}
		%>
		<br /> <br /> <a href="<%=backURL%>">もどる</a> <br /> <br />
	</div>
</body>
</html>