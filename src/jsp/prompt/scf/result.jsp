
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.NumberFormat"%>
<%
	// パラメータの取得
	String backURL = (String) request.getAttribute( "backURL" );
	List<Integer> newRegist = (ArrayList<Integer>) request.getAttribute("newRegist");
	List<Integer> repeat = (ArrayList<Integer>) request.getAttribute("repeat");
	List<Integer> dupRegist = (ArrayList<Integer>) request.getAttribute("dupRegist");

	
	int totalNewRegist = (Integer) request.getAttribute("totalNewRegist");
	int totalRepeat = (Integer) request.getAttribute("totalRepeat");
	int totaldupRegist = (Integer) request.getAttribute("totaldupRegist");
	
	// 11月8日のリピーター数詳細
	int day6_8 = (Integer) request.getAttribute("day6_8");
	int day7_8 = (Integer) request.getAttribute("day7_8");
	int day6_7_8 = (Integer) request.getAttribute("day6_7_8");


	NumberFormat nfNum = NumberFormat.getNumberInstance();
%>
<html>
<head>
<title>集計結果</title>
	<style type="text/css">
	html, body {
		height: 100%;
	}/*高さ100%に指定*/
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
			<tr>
				<th>11月6日</th>
				<td class="alt" align="right"><%=nfNum.format(dupRegist.get(0))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(0))%></td>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(0))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(0)+repeat.get(0))%></td>
			</tr>
			<tr>
				<th>11月7日</th>
				<td class="alt" align="right"><%=nfNum.format(dupRegist.get(1))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(1))%></td>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(1))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(1)+repeat.get(1))%></td>
			</tr>
			<tr>
				<th>11月8日</th>
				<td class="alt" align="right"><%=nfNum.format(dupRegist.get(2))%></td>				
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(2))%></td>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(2))%></td>
				<td class="alt" align="right"><%=nfNum.format(newRegist.get(2)+repeat.get(2))%></td>
			</tr>
			<tr>
				<td class="alt" align="left">合計</td>
				<td class="alt" align="right"><%=nfNum.format(totaldupRegist)%></td>				
				<td class="alt" align="right"><%=nfNum.format(totalNewRegist)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalRepeat)%></td>
				<td class="alt" align="right"><%=nfNum.format(totalNewRegist+totalRepeat)%></td>
			</tr>
		</table>
		<br/><br/>
		<table id="mytable" width="100%" style="BACKGROUND-COLOR: #a9a9a9">
			<tr>
				<th>6日 & 8日</th>
				<th>7日 & 8日</th>
				<th>6日 & 7日 & 8日</th>
				<th>リピーター合計</th>
			</tr>
			<tr>
				<td class="alt" align="right"><%=nfNum.format(day6_8)%></td>
				<td class="alt" align="right"><%=nfNum.format(day7_8)%></td>
				<td class="alt" align="right"><%=nfNum.format(day6_7_8)%></td>
				<td class="alt" align="right"><%=nfNum.format(repeat.get(2))%></td>
			</tr>
		</table>		
		<br/><br/>
		<a href="<%= backURL %>">もどる</a>
		<br/>
		<br/>
	</div>
</body>
</html>