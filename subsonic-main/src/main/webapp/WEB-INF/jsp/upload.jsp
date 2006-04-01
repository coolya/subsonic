<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="/include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h2><fmt:message key="upload.title"/></h2>

<c:forEach items="${model.uploadedFiles}" var="file">
    <p><fmt:message key="upload.success"><fmt:param value="${file.path}"/></fmt:message></p>
</c:forEach>

<c:forEach items="${model.unzippedFiles}" var="file">
    <fmt:message key="upload.unzipped"><fmt:param value="${file.path}"/></fmt:message><br/>
</c:forEach>

<c:choose>
    <c:when test="${not empty model.exception}">
        <p><fmt:message key="upload.failed"><fmt:param value="${model.exception.message}"/></fmt:message></p>
    </c:when>
    <c:when test="${empty model.uploadedFiles}">
        <p><fmt:message key="upload.empty"/></p>
    </c:when>
</c:choose>

<p><a href="more.view?">[<fmt:message key="common.back"/>]</a></p>
</body></html>


