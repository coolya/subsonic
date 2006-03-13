<%--$Revision: 1.11 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
    <title><%=is.get("helppopup.title")%></title>
</head><body>

<script type="text/javascript">
    window.focus();
</script>

<%
    String topic = request.getParameter("topic").toLowerCase();
%>
<h2><%=is.get("helppopup." + topic + ".title")%></h2>
<%=is.get("helppopup." + topic + ".text")%>

<p style="text-align:center">
<a href="javascript:self.close()">[<%=is.get("common.close")%>]</a>
</p>

</body></html>