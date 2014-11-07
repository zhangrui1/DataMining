
<%@page import="java.util.LinkedHashMap"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Map.Entry"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page
	import="jp.co.freedom.promptreport.gp.mesago.BwjGeneralPurposePromptReportResult"%>
<%@ page import="jp.co.freedom.promptreport.util.Utilities"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String backURL = (String) request.getAttribute("backURL");
	String[] days = (String[]) request.getAttribute("days");
	@SuppressWarnings("unchecked")
	List<String> categoryList = (ArrayList<String>) request
			.getAttribute("categoryList");
	@SuppressWarnings("unchecked")
	List<BwjGeneralPurposePromptReportResult> resultList = (ArrayList<BwjGeneralPurposePromptReportResult>) request
			.getAttribute("resultList");
	@SuppressWarnings("unchecked")
	Map<String, Integer> repeatTotal = (LinkedHashMap<String, Integer>) request
			.getAttribute("repeatTotal");

	int newRegistAppTotal = (Integer) request
			.getAttribute("newRegistAppTotal");
	int newRegistPreTotal = (Integer) request
			.getAttribute("newRegistPreTotal");
	int repeatTotalTotal = (Integer) request
			.getAttribute("repeatTotalTotal");

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
		<table id="mytable" style="BACKGROUND-COLOR: #a9a9a9">
			<tr>
				<th rowspan="2"></th>
				<th colspan="2">5月13日</th>
				<th colspan="3">5月14日</th>
				<th colspan="3">5月15日</th>
				<th colspan="3">合計</th>
			</tr>
			<tr>
				<th colspan="2">新規登録数</th>
				<th colspan="2">新規登録数</th>
				<th rowspan="2">リピータ－</th>
				<th colspan="2">新規登録数</th>
				<th rowspan="2">リピータ－</th>
				<th colspan="2">新規登録数</th>
				<th rowspan="2">リピータ－</th>
			</tr>
			<tr>
				<th>業種</th>
				<th>当日</th>
				<th>事前</th>
				<th>当日</th>
				<th>事前</th>
				<th>当日</th>
				<th>事前</th>
				<th>当日</th>
				<th>事前</th>
			</tr>
			<%
				//1日目
				List<Map.Entry<String, Integer>> newRegistAppList1 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(0).newRegistApp.entrySet());
				List<Map.Entry<String, Integer>> newRegistPreList1 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(0).newRegistPre.entrySet());
				List<Map.Entry<String, Integer>> repeatList1 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(0).repeat.entrySet());
				//2日目
				List<Map.Entry<String, Integer>> newRegistAppList2 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(1).newRegistApp.entrySet());
				List<Map.Entry<String, Integer>> newRegistPreList2 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(1).newRegistPre.entrySet());
				List<Map.Entry<String, Integer>> repeatList2 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(1).repeat.entrySet());
				//3日目
				List<Map.Entry<String, Integer>> newRegistAppList3 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(2).newRegistApp.entrySet());
				List<Map.Entry<String, Integer>> newRegistPreList3 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(2).newRegistPre.entrySet());
				List<Map.Entry<String, Integer>> repeatList3 = new ArrayList<Map.Entry<String, Integer>>(
						resultList.get(2).repeat.entrySet());
				//リピーター合計
				List<Map.Entry<String, Integer>> repeatTotalList = new ArrayList<Map.Entry<String, Integer>>(
						repeatTotal.entrySet());
			%>
			<%
				for (int nCategory = 0; nCategory <= categoryList.size(); nCategory++) {
			%>
			<tr>
				<!-- 1日目 -->
				<td class="alt" align="left"><%=nCategory != categoryList.size() ? categoryList
						.get(nCategory) : "2013年度来場者"%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistAppList1.get(nCategory)
						.getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistPreList1.get(nCategory)
						.getValue())%></td>
				<!-- 2日目 -->
				<td class="alt" align="right"><%=nfNum.format(newRegistAppList2.get(nCategory)
						.getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistPreList2.get(nCategory)
						.getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(repeatList2.get(nCategory).getValue())%></td>
				<!-- 3日目 -->
				<td class="alt" align="right"><%=nfNum.format(newRegistAppList3.get(nCategory)
						.getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistPreList3.get(nCategory)
						.getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(repeatList3.get(nCategory).getValue())%></td>
				<!-- 合計 -->
				<td class="alt" align="right"><%=nfNum.format(newRegistAppList1.get(nCategory)
						.getValue()
						+ newRegistAppList2.get(nCategory).getValue()
						+ newRegistAppList3.get(nCategory).getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistPreList1.get(nCategory)
						.getValue()
						+ newRegistPreList2.get(nCategory).getValue()
						+ newRegistPreList3.get(nCategory).getValue())%></td>
				<td class="alt" align="right"><%=nfNum.format(repeatTotalList.get(nCategory)
						.getValue())%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="alt" align="right">合計</td>
				<%
					for (int nDay = 0; nDay < days.length; nDay++) {
				%>
				<td class="alt" align="right"><%=nfNum.format(Utilities.sum(resultList.get(nDay).newRegistApp))%></td>
				<td class="alt" align="right"><%=nfNum.format(Utilities.sum(resultList.get(nDay).newRegistPre))%></td>
				<%
					if (nDay != 0) {
				%>
				<td class="alt" align="right"><%=nfNum.format(Utilities.sum(resultList.get(nDay).repeat))%></td>
				<%
					}
					}
				%>
				<!-- 合計 -->
				<td class="alt" align="right"><%=nfNum.format(newRegistAppTotal)%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegistPreTotal)%></td>
				<td class="alt" align="right"><%=nfNum.format(repeatTotalTotal)%></td>
			</tr>
		</table>
		<br /> <br /> <a href="<%=backURL%>">もどる</a> <br /> <br />
	</div>
</body>
</html>