/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.41
 * Generated at: 2014-05-07 08:09:58 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.src.jsp.prompt.gp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;

public final class gateAnalyzeMultiType_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");

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

      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<title>集計結果</title>\r\n");
      out.write("<style type=\"text/css\">\r\n");
      out.write("#mytable {\r\n");
      out.write("\twidth: 700px;\r\n");
      out.write("\tmargin: 0 0 0 1px;\r\n");
      out.write("\tpadding: 0;\r\n");
      out.write("\tborder: 0;\r\n");
      out.write("\tborder-spacing: 0;\r\n");
      out.write("\tborder-collapse: collapse;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("caption {\r\n");
      out.write("\tpadding: 0 0 5px 0;\r\n");
      out.write("\tfont: italic 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;\r\n");
      out.write("\ttext-align: right;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("th {\r\n");
      out.write("\tfont: bold 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;\r\n");
      out.write("\tcolor: #4f6b72;\r\n");
      out.write("\tborder: 1px solid #c1dad7;\r\n");
      out.write("\tletter-spacing: 2px;\r\n");
      out.write("\ttext-transform: uppercase;\r\n");
      out.write("\ttext-align: left;\r\n");
      out.write("\tpadding: 6px 6px 6px 12px;\r\n");
      out.write("\tbackground: #cae8ea url(\"img/css/bg_header.jpg\") no-repeat;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("th.nobg {\r\n");
      out.write("\tborder-top: 0;\r\n");
      out.write("\tborder-left: 0;\r\n");
      out.write("\tbackground: none;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("td {\r\n");
      out.write("\tborder: 1px solid #c1dad7;\r\n");
      out.write("\tbackground: #fff;\r\n");
      out.write("\tpadding: 6px 6px 6px 12px;\r\n");
      out.write("\tcolor: #4f6b72;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("td.alt {\r\n");
      out.write("\tbackground: #F5FAFA;\r\n");
      out.write("\tcolor: #797268;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("th.spec {\r\n");
      out.write("\tbackground: #fff url(\"img/css/bullet1.gif\") no-repeat;\r\n");
      out.write("\tfont: bold 10px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("th.specalt {\r\n");
      out.write("\tbackground: #f5fafa url(\"img/css/bullet2.gif\") no-repeat;\r\n");
      out.write("\tfont: bold 10px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;\r\n");
      out.write("\tcolor: #797268;\r\n");
      out.write("}\r\n");
      out.write("</style>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("\t<div align=\"center\">\r\n");
      out.write("\t\t<div>東3ホール</div>\r\n");
      out.write("\t\t<table id=\"mytable\" style=\"BACKGROUND-COLOR: #a9a9a9\">\r\n");
      out.write("\t\t\t<caption>");
      out.print(caption);
      out.write("</caption>\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<th></th>\r\n");
      out.write("\t\t\t\t<th>シングル</th>\r\n");
      out.write("\t\t\t\t<th>マルチ</th>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(categoryList.get(nIndex));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e3single.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e3multi.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				}
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">合計</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e3singleTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e3multiTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t</table>\r\n");
      out.write("\t\t<p />\r\n");
      out.write("\t\t<div>東4ホール</div>\r\n");
      out.write("\t\t<table id=\"mytable\" style=\"BACKGROUND-COLOR: #a9a9a9\">\r\n");
      out.write("\t\t\t<caption>");
      out.print(caption);
      out.write("</caption>\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<th></th>\r\n");
      out.write("\t\t\t\t<th>シングル</th>\r\n");
      out.write("\t\t\t\t<th>マルチ</th>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(categoryList.get(nIndex));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e4single.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e4multi.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				}
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">合計</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e4singleTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e4multiTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t</table>\r\n");
      out.write("\t\t<p />\r\n");
      out.write("\t\t<div>東5ホール</div>\r\n");
      out.write("\t\t<table id=\"mytable\" style=\"BACKGROUND-COLOR: #a9a9a9\">\r\n");
      out.write("\t\t\t<caption>");
      out.print(caption);
      out.write("</caption>\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<th></th>\r\n");
      out.write("\t\t\t\t<th>シングル</th>\r\n");
      out.write("\t\t\t\t<th>マルチ</th>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(categoryList.get(nIndex));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e5single.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e5multi.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				}
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">合計</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e5singleTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e5multiTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t</table>\r\n");
      out.write("\t\t<p />\r\n");
      out.write("\t\t<div>東6ホール</div>\r\n");
      out.write("\t\t<table id=\"mytable\" style=\"BACKGROUND-COLOR: #a9a9a9\">\r\n");
      out.write("\t\t\t<caption>");
      out.print(caption);
      out.write("</caption>\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<th></th>\r\n");
      out.write("\t\t\t\t<th>シングル</th>\r\n");
      out.write("\t\t\t\t<th>マルチ</th>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				for (int nIndex = 0; nIndex < categoryList.size(); nIndex++) {
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(categoryList.get(nIndex));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e6single.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"alt\" align=\"right\">");
      out.print(nfNum.format(e6multi.get(nIndex)));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t\t");

				}
			
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">合計</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e6singleTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t\t<td class=\"spec\" align=\"right\">");
      out.print(nfNum.format(e6multiTotal));
      out.write("</td>\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t</table>\r\n");
      out.write("\t\t<br /> <br /> <a\r\n");
      out.write("\t\t\thref=\"html/prompt/bwjGeneralPurposePromptReport.html\">もどる</a>\r\n");
      out.write("\t</div>\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}