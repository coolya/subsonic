<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body>

<h1><fmt:message key="changecoverart.title"/></h1>
<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.path}"/></sub:url>
<a href="${backUrl}"><b>[<fmt:message key="common.back"/>]</b></a>

<form method="post" action="changeCoverArt.view">
    <input type="hidden" name="path" value="${model.path}"/>
    <table class="indent"><tr>
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
    <sub:url value="changeCoverArt.view" var="url">
        <sub:param name="path" value="${model.path}"/>
        <sub:param name="url" value="${coverArtUrl}"/>
    </sub:url>
    <a href="${url}"><img src="${coverArtUrl}" hspace="5" vspace="5" alt=""/></a>
</c:forEach>

</body></html>