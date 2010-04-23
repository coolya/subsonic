<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jsp" %>

    <script type="text/javascript" language="javascript">
        function deletePlaylist(deleteUrl) {
            if (confirm("<fmt:message key="playlist.load.confirm_delete"/>")) {
                location.href = deleteUrl;
            }
        }
    </script>
</head>
<body class="mainframe bgcolor1">

<h1>
    <c:choose>
        <c:when test="${model.load}">
            <fmt:message key="playlist.load.title"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="playlist.load.appendtitle"/>
        </c:otherwise>
    </c:choose>
</h1>
<c:choose>
    <c:when test="${not model.playlistDirectoryExists}">
        <p class="warning"><fmt:message key="playlist.load.missing_folder"><fmt:param value="${model.playlistDirectory}"/></fmt:message></p>
    </c:when>
    <c:when test="${empty model.playlists}">
        <p class="warning"><fmt:message key="playlist.load.empty"/></p>
    </c:when>
    <c:otherwise>
        <table class="ruleTable indent">
            <c:forEach items="${model.playlists}" var="playlist">
                <sub:url value="loadPlaylistConfirm.view" var="loadUrl"><sub:param name="name" value="${playlist}"/></sub:url>
                <sub:url value="appendPlaylistConfirm.view" var="appendUrl"><sub:param name="name" value="${playlist}"/><sub:param name="indexes" value="${model.songIndexes}"/></sub:url>
                <sub:url value="deletePlaylist.view" var="deleteUrl"><sub:param name="name" value="${playlist}"/></sub:url>
                <sub:url value="download.view" var="downloadUrl"><sub:param name="playlist" value="${playlist}"/></sub:url>
                <tr>
                    <td class="ruleTableHeader">${fn:substringBefore(playlist,".")}</td>
                    <td class="ruleTableCell">
                        <c:choose>
                            <c:when test="${model.load}">
                                <div class="forward"><a href="${loadUrl}"><fmt:message key="playlist.load.load"/></a></div>
                                <c:if test="${model.user.downloadRole}">
                                    <div class="forward"><a href="${downloadUrl}"><fmt:message key="common.download"/></a></div>
                                </c:if>
                                <c:if test="${model.user.playlistRole}">
                                    <div class="forward"><a href="javascript:deletePlaylist('${deleteUrl}')"><fmt:message key="playlist.load.delete"/></a></div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${model.user.playlistRole}">
                                    <div class="forward"><a href="${appendUrl}"><fmt:message key="playlist.load.append"/></a></div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body></html>