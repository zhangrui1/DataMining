
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page
	import="jp.co.freedom.promptreport.gp.mesago.BwjGateAnalyzeResult"%>
<%@ page
	import="jp.co.freedom.promptreport.gp.mesago.BwjGateAnalyzeTotalResult"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String caption = (String) request.getAttribute("caption");
	@SuppressWarnings("unchecked")
	List<BwjGateAnalyzeResult[]> resultList = (ArrayList<BwjGateAnalyzeResult[]>) request
			.getAttribute("resultList");
	@SuppressWarnings("unchecked")
	List<String> categoryList = (ArrayList<String>) request
			.getAttribute("categoryList");
	@SuppressWarnings("unchecked")
	List<BwjGateAnalyzeTotalResult> totalList = (ArrayList<BwjGateAnalyzeTotalResult>) request
			.getAttribute("totalList");
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
				<th colspan="4">第3ホール</th>
				<th colspan="4">第4ホール</th>
				<th colspan="4">第5ホール</th>
				<th colspan="4">第6ホール</th>
			</tr>
			<tr>
				<th></th>
				<%
					for (int nIndex = 1; nIndex <= 4; nIndex++) {
				%>
				<th>当日登録</th>
				<th>事前登録</th>
				<th>リピーター</th>
				<th>合計</th>
				<%
					}
				%>
			</tr>
			<%
				for (int nIndex = 1; nIndex <= resultList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=resultList.get(nIndex - 1)[0].endTime - 1%>:00～<%=resultList.get(nIndex - 1)[0].endTime%>:00</td>
				<!-- 東3ホール  -->
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[0].appointedday)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[0].preentry)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[0].repeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[0].appointedday
						+ resultList.get(nIndex - 1)[0].preentry
						+ resultList.get(nIndex - 1)[0].repeat)%></td>
				<!-- 東4ホール  -->
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[1].appointedday)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[1].preentry)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[1].repeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[1].appointedday
						+ resultList.get(nIndex - 1)[1].preentry
						+ resultList.get(nIndex - 1)[1].repeat)%></td>
				<!-- 東5ホール  -->
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[2].appointedday)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[2].preentry)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[2].repeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[2].appointedday
						+ resultList.get(nIndex - 1)[2].preentry
						+ resultList.get(nIndex - 1)[2].repeat)%></td>
				<!-- 東6ホール  -->
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[3].appointedday)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[3].preentry)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[3].repeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(resultList.get(nIndex - 1)[3].appointedday
						+ resultList.get(nIndex - 1)[3].preentry
						+ resultList.get(nIndex - 1)[3].repeat)%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="alt" align="right">合計</td>
				<%
					for (int nIndex = 0; nIndex < totalList.size(); nIndex++) {
				%>

				<!-- 東3ホール  -->
				<td class="alt" align="right"><%=nfNum.format(totalList.get(nIndex).appointeddayTotal)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalList.get(nIndex).preentryTotal)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalList.get(nIndex).repeatTotal)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalList.get(nIndex).totalTotal)%></td>
				<%
					}
				%>
			</tr>


		</table>
		<p />

		<br /> <br /> <a
			href="html/prompt/bwjGeneralPurposePromptReport.html">もどる</a>
	</div>
</body>
</html>