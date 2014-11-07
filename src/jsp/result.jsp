
<%@ page contentType="text/html; charset=UTF-8"%>
<?xml version="1.0" encoding="UTF-8"?>

<html>
<head>
<title>検索結果</title>
	<style type="text/css">
	html, body {
		height: 100%;
	}/*高さ100%に指定*/
	.centerMiddle {
		position: absolute; /*body要素に対して絶対配置*/
		top: 30%; /*上端を中央に*/
		left: 50%; /*左端を中央に*/
		width: 600px; /*横幅*/
		height: 400px; /*縦幅*/
		background-color: #fff;
	}
	#mytable {
		width: 100%;
		margin: 0 0 0 1px;
		padding: 0;
		border: 0;
		border-spacing: 0;
		border-collapse: collapse;
	}

	caption {
		padding: 0 0 5px 0;
		font: italic 15px "Trebuchet MS", Verdana, Arial, Helvetica, sans-serif;
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
	
	td.on {
		background: #A2ADBC;
		color: #ffffff;
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
<body onLoad="document.form2.num.focus()">
	<div align="center">
		<div>処理が完了しました</div>
		<br/><br/>
		<a href="/DataMining/html/index.html">もどる</a>
		<br/>
		<br/>
	</div>
</body>
</html>