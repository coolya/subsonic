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
    <c:if test="${model.dir.album and model.averageRating gt 0}">
        <c:import url="rating.jsp">
            <c:param name="path" value="${model.dir.path}"/>
            <c:param name="readonly" value="true"/>
            <c:param name="rating" value="${model.averageRating}"/>
        </c:import>
    </c:if>
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

        <c:if test="${model.user.downloadRole}">
            <sub:url value="download.view" var="downloadUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            | <a href="${downloadUrl}"><fmt:message key="common.download"/></a>
        </c:if>

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

    <c:if test="${model.user.commentRole}">
        <c:import url="rating.jsp">
            <c:param name="path" value="${model.dir.path}"/>
            <c:param name="readonly" value="false"/>
            <c:param name="rating" value="${model.userRating}"/>
        </c:import>
    </c:if>

    <span class="detail">
        <fmt:message key="main.playcount"><fmt:param value="${model.playCount}"/></fmt:message>
        <c:if test="${not empty model.lastPlayed}">
            <fmt:message key="main.lastplayed">
                <fmt:param><fmt:formatDate type="date" dateStyle="long" value="${model.lastPlayed}"/></fmt:param>
            </fmt:message>
        </c:if>
    </span>

    <div id="comment" class="albumComment"><sub:wiki text="${model.comment}"/></div>

    <div id="commentForm" style="display:none">
    <form method="post" action="setMusicFileInfo.view">
        <input type="hidden" name="action" value="comment"/>
        <input type="hidden" name="path" value="${model.dir.path}"/>
        <textarea name="comment" rows="6" cols="70">${model.comment}</textarea>
        <input type="submit" value="<fmt:message key="common.save"/>"/>
    </form>
        <table class="detail">
            <tr><td style="padding-right:1em">__text__</td><td>Bold text           </td><td style="padding-left:3em;padding-right:1em">\\             </td><td>Creates a line break</td></tr>
            <tr><td style="padding-right:1em">~~text~~</td><td>Italic text         </td><td style="padding-left:3em;padding-right:1em">(empty line)   </td><td>Creates a new paragraph</td></tr>
            <tr><td style="padding-right:1em">* text  </td><td>List item           </td><td style="padding-left:3em;padding-right:1em">http://foo.com/</td><td>Creates an external link</td></tr>
            <tr><td style="padding-right:1em">1. text </td><td>Enumerated list item</td><td style="padding-left:3em;padding-right:1em">               </td><td>                        </td></tr>
        </table>
    </div>

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
        <table style="border-collapse:collapse;white-space:nowrap">
            <c:set var="cutoff" value="${model.visibility.captionCutoff}"/>
            <c:forEach items="${model.children}" var="child" varStatus="loopStatus">
                <c:choose>
                    <c:when test="${loopStatus.count % 2 == 1}">
                        <c:set var="class" value="class='bgcolor2'"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="class" value=""/>
                    </c:otherwise>
                </c:choose>

                <tr style="margin:0;padding:0;border:0">
                    <c:import url="playAddDownload.jsp">
                        <c:param name="path" value="${child.path}"/>
                        <c:param name="downloadEnabled" value="${model.user.downloadRole}"/>
                        <c:param name="asTable" value="true"/>
                    </c:import>

                    <c:choose>
                        <c:when test="${child.directory}">
                            <sub:url value="main.view" var="childUrl">
                                <sub:param name="path" value="${child.path}"/>
                            </sub:url>
                            <td style="padding-left:0.25em" colspan="4">
                                <a href="${childUrl}" title="${child.name}"><str:truncateNicely upper="${cutoff}">${child.name}</str:truncateNicely></a>
                            </td>
                        </c:when>

                        <c:otherwise>
                            <td ${class} style="padding-left:0.25em"/>

                            <c:if test="${model.visibility.trackNumberVisible}">
                                <td ${class} style="padding-right:0.5em;text-align:right">
                                    <span class="detail">${child.metaData.trackNumber}</span>
                                </td>
                            </c:if>

                            <td ${class} style="padding-right:1.25em">
                                <span title="${child.title}"><str:truncateNicely upper="${cutoff}">${child.title}</str:truncateNicely></span>
                            </td>

                            <c:if test="${model.visibility.albumVisible}">
                                <td ${class} style="padding-right:1.25em">
                                    <span class="detail" title="${child.metaData.album}"><str:truncateNicely upper="${cutoff}">${child.metaData.album}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.artistVisible and model.multipleArtists}">
                                <td ${class} style="padding-right:1.25em">
                                    <span class="detail" title="${child.metaData.artist}"><str:truncateNicely upper="${cutoff}">${child.metaData.artist}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.genreVisible}">
                                <td ${class} style="padding-right:1.25em">
                                    <span class="detail">${child.metaData.genre}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.yearVisible}">
                                <td ${class} style="padding-right:1.25em">
                                    <span class="detail">${child.metaData.year}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.formatVisible}">
                                <td ${class} style="padding-right:1.25em">
                                    <span class="detail">${fn:toLowerCase(child.metaData.format)}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.fileSizeVisible}">
                                <td ${class} style="padding-right:1.25em;text-align:right">
                                    <span class="detail"><sub:formatBytes bytes="${child.metaData.fileSize}"/></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.durationVisible}">
                                <td ${class} style="padding-right:1.25em;text-align:right">
                                    <span class="detail">${child.metaData.durationAsString}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.bitRateVisible}">
                                <td ${class} style="padding-right:0.25em">
                                    <span class="detail">
                                        <c:if test="${not empty child.metaData.bitRate}">
                                ${child.metaData.bitRate} Kbps ${child.metaData.variableBitRate ? "vbr" : ""}
                                        </c:if>
                                    </span>
                                </td>
                            </c:if>


                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </table>
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
