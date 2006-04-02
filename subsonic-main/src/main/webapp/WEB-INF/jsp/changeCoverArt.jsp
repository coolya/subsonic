<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>


<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h1><fmt:message key="changecoverart.title"/></h1>
<c:url value="main.jsp" var="backUrl"><c:param name="path" value="${model.path}"/></c:url>
<a href="${backUrl}"><b>[<fmt:message key="common.back"/>]</b></a>

<form method="post" action="changeCoverArt.view">
    <input type="hidden" name="path" value="${model.path}"/>
    <table><tr>
        <td><fmt:message key="changecoverart.artist"/></td>
        <td><input name="artist" type="text" value="${model.artist}"/></td>
        <td style="padding-left:10pt"><fmt:message key="changecoverart.album"/></td>
        <td><input name="album" type="text" value="${model.album}"/></td>
        <td><input type="submit" value="<fmt:message key="changecoverart.search"/>"/></td>
    </tr></table>
</form>

<p><fmt:message key="changecoverart.text"/></p>

<form method="post" action="changeCoverArt.view">
    <table><tr>
        <td><input type="hidden" name="path" value="${model.path}"/></td>
        <td><label for="url"><fmt:message key="changecoverart.address"/></label></td>
        <td><input type="text" name="url" size="40" id="url" value="http://"/></td>
        <td><input type='submit' value='<fmt:message key="common.ok"/>'></td>
    </tr></table>
</form>

<c:set var="hits" value="${fn:length(model.coverArtUrls)}"/>
<c:choose>
    <c:when test="${hits == 0}">
        <h2><fmt:message key="changecoverart.hits.none"/></h2>
    </c:when>
    <c:when test="${hits == 1}">
        <h2><fmt:message key="changecoverart.hits.one"/></h2>
    </c:when>
    <c:otherwise>
        <h2><fmt:message key="changecoverart.hits.many"><fmt:param value="${hits}"/></fmt:message></h2>
    </c:otherwise>
</c:choose>

<c:forEach items="${model.coverArtUrls}" var="coverArtUrl">
    <c:url value="changeCoverArt.view" var="url">
        <c:param name="path" value="${model.path}"/>
        <c:param name="url" value="${coverArtUrl}"/>
    </c:url>
    <a href="${url}"><img src="${coverArtUrl}" hspace="5" vspace="5"/></a>
</c:forEach>

</body></html>