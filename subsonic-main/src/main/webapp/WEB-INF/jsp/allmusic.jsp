<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>

<body onload="document.allmusic.submit();" class="mainframe bgcolor1">
<h2><fmt:message key="allmusic.text"><fmt:param value="${album}"/></fmt:message></h2>

<form name="allmusic" action="http://www.allmusic.com/cg/amg.dll" method="POST" accept-charset="iso-8859-1">
    <input type="hidden" name="p" value="amg"/>
    <input type="hidden" name="SQL" value="${album}"/>
    <input type="hidden" name="OPT1" value="2"/>
</form>

</body>
</html>