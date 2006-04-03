<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head>

<body onload="document.allmusic.submit();">
<h2><fmt:message key="allmusic.text"><fmt:param value="${album}"/></fmt:message></h2>

<form name="allmusic" action="http://www.allmusic.com/cg/amg.dll" method="POST" accept-charset="iso-8859-1">
    <input type="hidden" name="p" value="amg"/>
    <input type="hidden" name="SQL" value="${album}"/>
    <input type="hidden" name="OPT1" value="2"/>
</form>

</body>
</html>