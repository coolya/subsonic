<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
</head>


<body>

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


<h1>${model.dir.formattedPath}</h1>
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
            <sub:url value="changeCoverArt.view" var="changeCoverArtUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            <sub:url value="editTags.view" var="editTagsUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            | <a href="${changeCoverArtUrl}"><fmt:message key="main.cover"/></a>
            | <a href="${editTagsUrl}"><fmt:message key="main.tags"/></a>

        </c:if>
        <c:if test="${model.user.commentRole}">
            | <a href="javascript:toggleComment()"><fmt:message key="main.comment"/></a>
        </c:if>
    </c:if>
</h2>

<c:if test="${model.dir.album}">
    <sub:url value="setMusicFileInfo.view" var="ratingUrl">
        <sub:param name="path" value="${model.dir.path}"/>
        <sub:param name="action" value="rating"/>
    </sub:url>

    <map id="ratingMap" name="ratingMap">
        <area href="${ratingUrl}&rating=1" shape="rect" coords="0,0,12,13"  alt="<fmt:message key="main.rating"/> 1"/>
        <area href="${ratingUrl}&rating=2" shape="rect" coords="13,0,25,13" alt="<fmt:message key="main.rating"/> 2"/>
        <area href="${ratingUrl}&rating=3" shape="rect" coords="26,0,38,13" alt="<fmt:message key="main.rating"/> 3"/>
        <area href="${ratingUrl}&rating=4" shape="rect" coords="39,0,51,13" alt="<fmt:message key="main.rating"/> 4"/>
        <area href="${ratingUrl}&rating=5" shape="rect" coords="52,0,64,13" alt="<fmt:message key="main.rating"/> 5"/>
    </map>

    <img src="icons/rating${model.rating}.gif"
         alt="<fmt:message key="main.rating"/> ${model.rating}"
         ${model.user.commentRole ? "usemap='#ratingMap'" : ""}/>

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
                        <a href="${childUrl}">${child.name}</a>
                    </c:when>
                    <c:otherwise>
                    ${child.title}
                    </c:otherwise>
                </c:choose>
            </p>
        </c:forEach>
    </td>

    <td>
        <c:forEach items="${model.coverArts}" var="coverArt">
            <sub:url value="main.view" var="mainUrl">
                <sub:param name="path" value="${coverArt.parentFile.path}"/>
            </sub:url>
            <sub:url value="coverart" var="coverArtUrl">
                <sub:param name="path" value="${coverArt.path}"/>
                <sub:param name="size" value="${model.coverArtSize}"/>
            </sub:url>
            <a href="${mainUrl}"><img src="${coverArtUrl}" alt="" hspace="5" vspace="5"
                                      height="${model.coverArtSize}" width="${model.coverArtSize}"/></a>
        </c:forEach>

        <c:if test="${model.showGenericCoverArt}">
            <table>
                <tr><td>
                    <sub:url value="/coverart" var="coverArtUrl">
                        <sub:param name="size" value="${model.coverArtSize}"/>
                    </sub:url>
                    <sub:url value="/changeCoverArt.view" var="changeCoverArtUrl">
                        <sub:param name="path" value="${model.dir.path}"/>
                    </sub:url>

                    <img height="${model.coverArtSize}" width="${model.coverArtSize}" hspace="5" vspace="5" src="${coverArtUrl}"/>
                </td></tr>
                <tr><td align="center">
                    <a href="${changeCoverArtUrl}"><fmt:message key="main.cover"/></a>
                </td></tr>
            </table>
        </c:if>
    </td>
</tr>
</table>

</body>
</html>
