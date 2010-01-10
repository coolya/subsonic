<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.CoverArtSettingsCommand"--%>
<%--@elvariable id="musicFile" type="net.sourceforge.subsonic.domain.MusicFile"--%>

<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="coverArt"/>
</c:import>

<form:form commandName="command" action="coverArtSettings.view" method="post">

    <p><form:checkbox id="auto" path="auto"/> <label for="auto"><fmt:message key="coverartsettings.auto"/></label></p>

    <p>
    <c:choose>
        <c:when test="${command.batchRunning}">
            <fmt:message key="coverartsettings.running"/>
        </c:when>
        <c:otherwise>
            <div class="forward"><a href="coverArtSettings.view?runBatch"><fmt:message
                    key="coverartsettings.manual"/></a></div>
        </c:otherwise>
    </c:choose>
    </p>

    <p>
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>
</form:form>

<c:if test="${empty command.coverArtReport}">
    <div class="forward"><a href="coverArtSettings.view?report"><fmt:message key="coverartsettings.albumList"/></a></div>
</c:if>

<c:if test="${not empty command.coverArtReport}">
    <p><b>
        <fmt:message key="coverartsettings.missing">
            <fmt:param value="${fn:length(command.coverArtReport.albumsWithoutCover)}"/>
            <fmt:param value="${command.coverArtReport.totalNumberOfAlbums}"/>
        </fmt:message>
    </b></p>

    <table>
        <c:forEach items="${command.coverArtReport.albumsWithoutCover}" var="musicFile" begin="${command.startIndex}"
                   end="${command.endIndex}" varStatus="loopStatus">

            <c:set var="artist">${empty musicFile.metaData.artist ? musicFile.firstChild.metaData.artist : musicFile.metaData.artist}</c:set>
            <c:set var="album">${empty musicFile.metaData.album ? musicFile.firstChild.metaData.album : musicFile.metaData.album}</c:set>
            <tr>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""}>
                    <str:truncateNicely upper="50">${fn:escapeXml(artist)}</str:truncateNicely>
                </td>
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em">
                    <a href="<sub:url value="changeCoverArt.view"><sub:param name="path" value="${musicFile.path}"/></sub:url>">
                        <str:truncateNicely upper="75">${fn:escapeXml(album)}</str:truncateNicely></a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <p>
        <c:forEach items="${command.paginationElements}" var="page" varStatus="status">
            <c:choose>
                <c:when test="${page.active}">${page.position}</c:when>
                <c:otherwise><a href="coverArtSettings.view?report&pageNumber=${page.position}">${page.position}</a></c:otherwise>
            </c:choose>
        </c:forEach>
    </p>
</c:if>
</body>
</html>