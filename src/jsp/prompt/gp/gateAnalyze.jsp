
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String caption = (String) request.getAttribute("caption");
	@SuppressWarnings("unchecked")
	List<Integer> e3AppointedDay = (ArrayList<Integer>) request
			.getAttribute("e3AppointedDay");
	@SuppressWarnings("unchecked")
	List<Integer> e3breakdown = (ArrayList<Integer>) request
			.getAttribute("e3breakdown");
	@SuppressWarnings("unchecked")
	List<Integer> e3repeat = (ArrayList<Integer>) request
			.getAttribute("e3repeat");
	@SuppressWarnings("unchecked")
	List<Integer> e4AppointedDay = (ArrayList<Integer>) request
			.getAttribute("e4AppointedDay");
	@SuppressWarnings("unchecked")
	List<Integer> e4breakdown = (ArrayList<Integer>) request
			.getAttribute("e4breakdown");
	@SuppressWarnings("unchecked")
	List<Integer> e4repeat = (ArrayList<Integer>) request
			.getAttribute("e4repeat");
	@SuppressWarnings("unchecked")
	List<Integer> e5AppointedDay = (ArrayList<Integer>) request
			.getAttribute("e5AppointedDay");
	@SuppressWarnings("unchecked")
	List<Integer> e5breakdown = (ArrayList<Integer>) request
			.getAttribute("e5breakdown");
	@SuppressWarnings("unchecked")
	List<Integer> e5repeat = (ArrayList<Integer>) request
			.getAttribute("e5repeat");
	@SuppressWarnings("unchecked")
	List<Integer> e6AppointedDay = (ArrayList<Integer>) request
			.getAttribute("e6AppointedDay");
	@SuppressWarnings("unchecked")
	List<Integer> e6breakdown = (ArrayList<Integer>) request
			.getAttribute("e6breakdown");
	@SuppressWarnings("unchecked")
	List<Integer> e6repeat = (ArrayList<Integer>) request
			.getAttribute("e6repeat");
	@SuppressWarnings("unchecked")
	List<String> categoryList = (ArrayList<String>) request
			.getAttribute("categoryList");

	int e3AppointedDayTotal = (Integer) request
			.getAttribute("e3AppointedDayTotal");
	int e3breakdownTotal = (Integer) request
			.getAttribute("e3breakdownTotal");
	int e3RepeatTotal = (Integer) request.getAttribute("e3RepeatTotal");
	int e4AppointedDayTotal = (Integer) request
			.getAttribute("e4AppointedDayTotal");
	int e4breakdownTotal = (Integer) request
			.getAttribute("e4breakdownTotal");
	int e4RepeatTotal = (Integer) request.getAttribute("e4RepeatTotal");
	int e5AppointedDayTotal = (Integer) request
			.getAttribute("e5AppointedDayTotal");
	int e5breakdownTotal = (Integer) request
			.getAttribute("e5breakdownTotal");
	int e5RepeatTotal = (Integer) request.getAttribute("e5RepeatTotal");
	int e6AppointedDayTotal = (Integer) request
			.getAttribute("e6AppointedDayTotal");
	int e6breakdownTotal = (Integer) request
			.getAttribute("e6breakdownTotal");
	int e6RepeatTotal = (Integer) request.getAttribute("e6RepeatTotal");

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
				<th colspan=4>東3ホール</th>
				<th colspan=4>東4ホール</th>
				<th colspan=4>東5ホール</th>
				<th colspan=4>東6ホール</th>
			</tr>
			<tr>
				<th></th>
				<th>当日登録</th>
				<th>事前登録</th>
				<th>リピーター</th>
				<th>合計</th>
				<th>当日登録</th>
				<th>事前登録</th>
				<th>リピーター</th>
				<th>合計</th>
				<th>当日登録</th>
				<th>事前登録</th>
				<th>リピーター</th>
				<th>合計</th>
				<th>当日登録</th>
				<th>事前登録</th>
				<th>リピーター</th>
				<th>合計</th>
			</tr>
			<%
				for (int nIndex = 1; nIndex <= categoryList.size(); nIndex++) {
			%>
			<tr>
				<td class="alt" align="right"><%=categoryList.get(nIndex - 1)%></td>
				<td class="alt" align="right"><%=nfNum.format(e3AppointedDay.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e3breakdown.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e3repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e3AppointedDay.get(nIndex)
						+ e3breakdown.get(nIndex) + e3repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e4AppointedDay.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e4breakdown.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e4repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e4AppointedDay.get(nIndex)
						+ e4breakdown.get(nIndex) + e4repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e5AppointedDay.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e5breakdown.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e5repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e5AppointedDay.get(nIndex)
						+ e5breakdown.get(nIndex) + e5repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e6AppointedDay.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e6breakdown.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e6repeat.get(nIndex))%></td>
				<td class="alt" align="right"><%=nfNum.format(e6AppointedDay.get(nIndex)
						+ e6breakdown.get(nIndex) + e6repeat.get(nIndex))%></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="spec" align="right">合計</td>
				<td class="spec" align="right"><%=nfNum.format(e3AppointedDayTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e3breakdownTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e3RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e3AppointedDayTotal + e3breakdownTotal
					+ e3RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e4AppointedDayTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e4breakdownTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e4RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e4AppointedDayTotal + e4breakdownTotal
					+ e4RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e5AppointedDayTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e5breakdownTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e5RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e5AppointedDayTotal + e5breakdownTotal
					+ e5RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e6AppointedDayTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e6breakdownTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e6RepeatTotal)%></td>
				<td class="spec" align="right"><%=nfNum.format(e6AppointedDayTotal + e6breakdownTotal
					+ e6RepeatTotal)%></td>
			</tr>
		</table>
		<p />
		<br /> <br /> <a href="html/prompt/bwjGeneralPurposePromptReport.html">もどる</a>
	</div>
</body>
</html>