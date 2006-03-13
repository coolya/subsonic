<%--$Revision: 1.7 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>

<%
    ServiceFactory.getSearchService().createIndex();
    InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

<h1><%=is.get("createsearchindex.title")%></h1>
<p><%=is.get("createsearchindex.text")%></p>

<a href="settings.jsp?">[<%=is.get("common.back")%>]</a>

</body></html>

