<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head><body>

<c:if test="${model.updateNowPlaying}">

    <!-- This script uses AJAX to periodically check if the current song has changed. -->
    <script type="text/javascript" language="javascript">

        var currentDir = null;
        window.onload = onload();

        function onload() {
            DWREngine.setErrorHandler(null);
            startTimer();
        }

        function startTimer() {
            nowPlayingService.getDirectory(nowPlayingCallback);
            setTimeout("startTimer()", 10000);
        }

        function nowPlayingCallback(dir){
            if (currentDir != null && currentDir != dir) {
                location.replace("nowPlaying.view?");
            }
            currentDir = dir;
        }
    </script>
</c:if>


<h1>
    <img src="<c:url value="/icons/now_playing.png"/>" alt=""/>
    ${model.dir.formattedPath}
</h1>

<h2>
    <c:if test="${not model.dir.parent.root}">
        <sub:url value="main.view" var="upUrl">
            <sub:param name="path" value="${model.dir.parent.path}"/>
        </sub:url>
        <a href="${upUrl}"><fmt:message key="main.up"/></a> |
    </c:if>

    <sub:url value="playlist.view" var="playUrl">
        <sub:param name="play" value="${model.dir.path}"/>
    </sub:url>
    <sub:url value="playlist.view" var="addUrl">
        <sub:param name="add" value="${model.dir.path}"/>
    </sub:url>

    <a target="playlist" href="${playUrl}"><fmt:message key="main.playall"/></a> |
    <a target="playlist" href="${addUrl}"><fmt:message key="main.addall"/></a>

    <c:if test="${model.dir.album}">
        <sub:url value="albumInfo.view" var="albumInfoUrl">
            <sub:param name="path" value="${model.dir.path}"/>
        </sub:url>
        | <a href="${albumInfoUrl}"><fmt:message key="main.albuminfo"/></a>

        <c:if test="${model.user.coverArtRole}">
            <sub:url value="editTags.view" var="editTagsUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            | <a href="${editTagsUrl}"><fmt:message key="main.tags"/></a>

        </c:if>
        <c:if test="${model.user.commentRole}">
            | <a href="javascript:toggleComment()"><fmt:message key="main.comment"/></a>
        </c:if>
    </c:if>
</h2>

<c:if test="${model.dir.album}">

    <c:import url="rating.jsp">
        <c:param name="path" value="${model.dir.path}"/>
        <c:param name="readonly" value="${not model.user.commentRole}"/>
        <c:param name="rating" value="${model.rating}"/>
    </c:import>

    <span class="detail">
        <fmt:message key="main.playcount"><fmt:param value="${model.playCount}"/></fmt:message>
        <c:if test="${not empty model.lastPlayed}">
            <fmt:message key="main.lastplayed">
                <fmt:param><fmt:formatDate type="date" dateStyle="long" value="${model.lastPlayed}"/></fmt:param>
            </fmt:message>
        </c:if>
    </span>

    <div id="comment" class="albumComment">${model.comment}</div>

    <form method="post" id="commentForm" action="setMusicFileInfo.view" style="display:none">
        <input type="hidden" name="action" value="comment"/>
        <input type="hidden" name="path" value="${model.dir.path}"/>
        <textarea name="comment" rows="6" cols="70">${model.comment}</textarea>
        <input type="submit" value="<fmt:message key="common.save"/>"/>
    </form>

    <script type='text/javascript'>
        function toggleComment() {
            var commentForm = document.getElementById('commentForm');
            var commentDiv = document.getElementById('comment');

            if (commentForm.style.display == "none")  {
                commentForm.style.display = "";
                commentDiv.style.display = "none";
            } else {
                commentForm.style.display = "none";
                commentDiv.style.display = "";
            }
        }
    </script>

</c:if>

<table cellpadding="10"><tr style="vertical-align:top;">
    <td style="vertical-align:top;">
        <c:forEach items="${model.children}" var="child">
            <p class="dense">
                <c:import url="playAddDownload.jsp">
                    <c:param name="path" value="${child.path}"/>
                    <c:param name="downloadEnabled" value="${model.user.downloadRole}"/>
                </c:import>

                <c:choose>
                    <c:when test="${child.directory}">
                        <sub:url value="main.view" var="childUrl">
                            <sub:param name="path" value="${child.path}"/>
                        </sub:url>
                        <a href="${childUrl}" title="${child.name}"><str:truncateNicely lower="35" upper="35">${child.name}</str:truncateNicely></a>
                    </c:when>
                    <c:otherwise>
                        <span title="${child.title}"><str:truncateNicely lower="35" upper="35">${child.title}</str:truncateNicely></span>
                        <c:if test="${model.showArtist and not empty child.metaData.artist}">
                            - <span class="detail"><str:truncateNicely lower="35" upper="35">${child.metaData.artist}</str:truncateNicely></span>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </p>
        </c:forEach>
    </td>

    <td style="vertical-align:top;">
        <c:forEach items="${model.coverArts}" var="coverArt">
            <div style="float:left; padding:5px">
                <c:import url="coverArt.jsp">
                    <c:param name="albumPath" value="${coverArt.parentFile.path}"/>
                    <c:param name="albumName" value="${coverArt.parentFile.name}"/>
                    <c:param name="coverArtSize" value="${model.coverArtSize}"/>
                    <c:param name="coverArtPath" value="${coverArt.path}"/>
                    <c:param name="showLink" value="${coverArt.parentFile.path ne model.dir.path}"/>
                    <c:param name="showZoom" value="${coverArt.parentFile.path eq model.dir.path}"/>
                    <c:param name="showChange" value="${(coverArt.parentFile.path eq model.dir.path) and model.user.coverArtRole}"/>
                    <c:param name="showCaption" value="true"/>
                </c:import>
            </div>
        </c:forEach>

        <c:if test="${model.showGenericCoverArt}">
            <c:import url="coverArt.jsp">
                <c:param name="albumPath" value="${model.dir.path}"/>
                <c:param name="coverArtSize" value="${model.coverArtSize}"/>
                <c:param name="showLink" value="false"/>
                <c:param name="showZoom" value="false"/>
                <c:param name="showChange" value="${model.user.coverArtRole}"/>
            </c:import>
        </c:if>
    </td>
</tr>
</table>

</body>
</html>
