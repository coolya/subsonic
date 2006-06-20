<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">

<!-- This script uses AJAX to periodically check if the current song has changed. -->
<script type="text/javascript" language="javascript">
    var currentFile = null;

    function onload() {
        DWREngine.setErrorHandler(null);
        location.hash="${model.anchor}";
        startTimer();
    }

    function startTimer() {
        nowPlayingService.getFile(nowPlayingCallback);
        setTimeout("startTimer()", 10000);
    }

    function nowPlayingCallback(file){
        if (currentFile != null && currentFile != file) {
            location.replace("playlist.view?");
        }
        currentFile = file;
    }
</script>

<a name="-1">
    <h2><table style="white-space:nowrap;"><tr>
        <td><select name="player" onchange="location='playlist.view?player=' + options[selectedIndex].value;" >
            <c:forEach items="${model.players}" var="player">
                <option ${player.id eq model.player.id ? "selected" : ""} value="${player.id}">${player}</option>
            </c:forEach>
        </select></td>

        <c:choose>
            <c:when test="${model.isPlaying}">
                <td><a href="playlist.view?stop"><fmt:message key="playlist.stop"/></a> | </td>
            </c:when>
            <c:otherwise>
                <td><a href="playlist.view?start"><fmt:message key="playlist.start"/></a> | </td>
            </c:otherwise>
        </c:choose>

        <td><a href="playlist.view?clear"><fmt:message key="playlist.clear"/></a></td>
        <td> | <a href="playlist.view?shuffle"><fmt:message key="playlist.shuffle"/></a></td>

        <c:choose>
            <c:when test="${model.repeatEnabled}">
                <td> | <a href="playlist.view?repeat"><fmt:message key="playlist.repeat_on"/></a></td>
            </c:when>
            <c:otherwise>
                <td> | <a href="playlist.view?repeat"><fmt:message key="playlist.repeat_off"/></a></td>
            </c:otherwise>
        </c:choose>
        <td> | <a href="playlist.view?undo"><fmt:message key="playlist.undo"/></a></td>
        <td> | <a target="main" href="loadPlaylist.view?"><fmt:message key="playlist.load"/></a></td>
        <c:if test="${model.user.playlistRole}">
            <td> | <a target="main" href="savePlaylist.view?"><fmt:message key="playlist.save"/></a></td>
        </c:if>
        <c:if test="${model.user.downloadRole}">
            <td> | <a href="download.view?player=${model.player.id}"><fmt:message key="common.download"/></a></td>
        </c:if>
    </tr></table></h2>
</a>

<c:choose>
    <c:when test="${empty model.songs}">
        <p><em><fmt:message key="playlist.empty"/></em></p>
    </c:when>
    <c:otherwise>
        <table style="border-collapse:collapse;">
            <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
                <c:set var="i" value="${loopStatus.count - 1}"/>
                <tr style="margin:0;padding:0;border:0"><a name="${i}"></a>
                    <td><a href="playlist.view?remove=${i}"><img width="13" height="13" src="<spring:theme code="removeImage"/>"
                                                                 alt="<fmt:message key="playlist.remove"/>"
                                                                 title="<fmt:message key="playlist.remove"/>"/></a></td>
                    <td><a href="playlist.view?up=${i}"><img width="13" height="13" src="<spring:theme code="upImage"/>"
                                                             alt="<fmt:message key="playlist.up"/>"
                                                             title="<fmt:message key="playlist.up"/>"/></a></td>
                    <td><a href="playlist.view?down=${i}"><img width="13" height="13" src="<spring:theme code="downImage"/>"
                                                               alt="<fmt:message key="playlist.down"/>"
                                                               title="<fmt:message key="playlist.down"/>"/></a></td>
                    <c:if test="${model.user.downloadRole}">
                        <sub:url value="download.view" var="downloadUrl">
                            <sub:param name="path" value="${song.path}"/>
                        </sub:url>
                        <td><a href="${downloadUrl}"><img width="13" height="13" src="<spring:theme code="downloadImage"/>"
                                                          alt="<fmt:message key="common.download"/>"
                                                          title="<fmt:message key="common.download"/>"/></a></td>
                    </c:if>

                    <sub:url value="main.view" var="mainUrl">
                        <sub:param name="path" value="${song.parentPath}"/>
                    </sub:url>
                    <td ${i % 2 == 0 ? "class='bgcolor1'" : ""} style="padding-left:0.25em;padding-right:2em">
                        <c:if test="${song.current}">
                            <img src="<c:url value="/icons/current.gif"/>" alt=""/>
                        </c:if>
                        <a href="playlist.view?skip=${i}">${song.current ? "<b>" : ""}<str:truncateNicely upper="50">${song.title}</str:truncateNicely>${song.current ? "</b>" : ""}</a>
                    </td>
                    <td ${i % 2 == 0 ? "class='bgcolor1'" : ""} style="padding-right:0.25em">
                        <a target="main" href="${mainUrl}"><str:truncateNicely upper="50">${song.artistAlbumYear}</str:truncateNicely></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<c:if test="${model.sendM3U}">
    <script language="javascript" type="text/javascript">parent.frames.main.location.href="play.m3u?"</script>
</c:if>
</body></html>