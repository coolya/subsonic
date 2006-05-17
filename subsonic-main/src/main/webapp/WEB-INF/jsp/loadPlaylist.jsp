<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head><body>

<h1><fmt:message key="playlist.load.title"/></h1>
<c:choose>
    <c:when test="${not model.playlistDirectoryExists}">
        <p><fmt:message key="playlist.load.missing_folder"><fmt:param value="${model.playlistDirectory}"/></fmt:message></p>
    </c:when>
    <c:when test="${empty model.playlists}">
        <p><fmt:message key="playlist.load.empty"/></p>
    </c:when>
    <c:otherwise>
        <table class="ruleTable">
            <c:forEach items="${model.playlists}" var="playlist">
                <sub:url value="loadPlaylistConfirm.view" var="loadUrl"><sub:param name="name" value="${playlist}"/></sub:url>
                <sub:url value="deletePlaylist.view" var="deleteUrl"><sub:param name="name" value="${playlist}"/></sub:url>
                <sub:url value="download" var="downloadUrl"><sub:param name="playlist" value="${playlist}"/></sub:url>
                <tr>
                    <td class="ruleTableHeader">${fn:substringBefore(playlist,".")}</td>
                    <td class="ruleTableCell"><a href="${loadUrl}">[<fmt:message key="playlist.load.load"/>]</a>
                    <c:if test="${model.user.playlistRole}">
                        <a href="${deleteUrl}">[<fmt:message key="playlist.load.delete"/>]</a>
                    </c:if>
                    <c:if test="${model.user.downloadRole}">
                        <a href="${downloadUrl}">[<fmt:message key="common.download"/>]</a>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body></html>