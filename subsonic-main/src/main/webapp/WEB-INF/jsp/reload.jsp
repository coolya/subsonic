<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>
<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<c:forEach items="${model.reloadFrames}" var="reloadFrame">
    <script language="javascript" type="text/javascript">parent.frames.${reloadFrame.frame}.location.href="${reloadFrame.view}"</script>
</c:forEach>

</body></html>
