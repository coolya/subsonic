<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
    <c:if test="${not model.updateNowPlaying}">
        <meta http-equiv="refresh" content="180;URL=nowPlaying.view?">
    </c:if>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scriptaculous.js?load=effects"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>
</head><body class="mainframe bgcolor1" onload="init()">

<script type="text/javascript" language="javascript">
    function init() {
        setupZoom('<c:url value="/"/>');
    }
</script>

<c:if test="${model.updateNowPlaying}">

    <script type="text/javascript" language="javascript">
        // Variable used by javascript in playlist.jsp
        var updateNowPlaying = true;
    </script>
</c:if>
 
<h1>
    <img src="<spring:theme code="nowPlayingImage"/>" alt="">

    <c:forEach items="${model.ancestors}" var="ancestor">
        <sub:url value="main.view" var="ancestorUrl">
            <sub:param name="path" value="${ancestor.path}"/>
        </sub:url>
        <a href="${ancestorUrl}">${ancestor.name}</a> &raquo;
    </c:forEach>
    ${model.dir.name}

    <c:if test="${model.dir.album and model.averageRating gt 0}">
        &nbsp;&nbsp;
        <c:import url="rating.jsp">
            <c:param name="path" value="${model.dir.path}"/>
            <c:param name="readonly" value="true"/>
            <c:param name="rating" value="${model.averageRating}"/>
        </c:import>
    </c:if>
</h1>

<c:if test="${not model.partyMode}">
<h2>
    <c:if test="${model.navigateUpAllowed}">
        <sub:url value="main.view" var="upUrl">
            <sub:param name="path" value="${model.dir.parent.path}"/>
        </sub:url>
        <a href="${upUrl}"><fmt:message key="main.up"/></a>
        <c:set var="needSep" value="true"/>
    </c:if>

    <c:set var="path">
        <sub:escapeJavaScript string="${model.dir.path}"/>
    </c:set>

    <c:if test="${model.user.streamRole}">
        <c:if test="${needSep}">|</c:if>
        <a href="javascript:noop()" onclick="top.playlist.onPlay('${path}')"><fmt:message key="main.playall"/></a> |
        <a href="javascript:noop()" onclick="top.playlist.onPlayRandom('${path}', 10)"><fmt:message key="main.playrandom"/></a> |
        <a href="javascript:noop()" onclick="top.playlist.onAdd('${path}')"><fmt:message key="main.addall"/></a>
        <c:set var="needSep" value="true"/>
    </c:if>

    <c:if test="${model.dir.album}">

        <c:if test="${model.user.downloadRole}">
            <sub:url value="download.view" var="downloadUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            <c:if test="${needSep}">|</c:if>
            <a href="${downloadUrl}"><fmt:message key="common.download"/></a>
            <c:set var="needSep" value="true"/>
        </c:if>

        <c:if test="${model.user.coverArtRole}">
            <sub:url value="editTags.view" var="editTagsUrl">
                <sub:param name="path" value="${model.dir.path}"/>
            </sub:url>
            <c:if test="${needSep}">|</c:if>
            <a href="${editTagsUrl}"><fmt:message key="main.tags"/></a>
            <c:set var="needSep" value="true"/>
        </c:if>

    </c:if>

    <c:if test="${model.user.commentRole}">
        <c:if test="${needSep}">|</c:if>
        <a href="javascript:toggleComment()"><fmt:message key="main.comment"/></a>
    </c:if>
</h2>
</c:if>

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

        <c:set var="artist" value="${model.children[0].metaData.artist}"/>
        <c:set var="album" value="${model.children[0].metaData.album}"/>
        <c:if test="${not empty artist and not empty album}">
            <sub:url value="http://www.google.com/musicsearch" var="googleUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${artist}\" \"${album}\""/>
            </sub:url>
            <sub:url value="http://en.wikipedia.org/wiki/Special:Search" var="wikipediaUrl" encoding="UTF-8">
                <sub:param name="search" value="\"${album}\""/>
                <sub:param name="go" value="Go"/>
            </sub:url>
            <sub:url value="allmusic.view" var="allmusicUrl">
                <sub:param name="album" value="${album}"/>
            </sub:url>
            <sub:url value="http://www.last.fm/search" var="lastFmUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${artist}\" \"${album}\""/>
                <sub:param name="type" value="album"/>
            </sub:url>
            <fmt:message key="top.search"/> <a target="_blank" href="${googleUrl}">Google</a> |
            <a target="_blank" href="${wikipediaUrl}">Wikipedia</a> |
            <a target="_blank" href="${allmusicUrl}">allmusic</a> |
            <a target="_blank" href="${lastFmUrl}">Last.fm</a>
        </c:if>
    </span>
</c:if>

<div id="comment" class="albumComment"><sub:wiki text="${model.comment}"/></div>

<div id="commentForm" style="display:none">
    <form method="post" action="setMusicFileInfo.view">
        <input type="hidden" name="action" value="comment">
        <input type="hidden" name="path" value="${model.dir.path}">
        <textarea name="comment" rows="6" cols="70">${model.comment}</textarea>
        <input type="submit" value="<fmt:message key="common.save"/>">
    </form>
    <fmt:message key="main.wiki"/>
</div>

<script type='text/javascript'>
    function toggleComment() {
        $("commentForm").toggle();
        $("comment").toggle();
    }
</script>


<table cellpadding="10" style="width:100%">
<tr style="vertical-align:top;">
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
                        <c:param name="video" value="${child.video}"/>
                        <c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode}"/>
                        <c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not child.directory)}"/>
                        <c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode}"/>
                        <c:param name="asTable" value="true"/>
                    </c:import>

                    <c:choose>
                        <c:when test="${child.directory}">
                            <sub:url value="main.view" var="childUrl">
                                <sub:param name="path" value="${child.path}"/>
                            </sub:url>
                            <td style="padding-left:0.25em" colspan="4">
                                <a href="${childUrl}" title="${child.name}"><span style="white-space:nowrap;"><str:truncateNicely upper="${cutoff}">${child.name}</str:truncateNicely></span></a>
                            </td>
                        </c:when>

                        <c:otherwise>
                            <td ${class} style="padding-left:0.25em"></td>

                            <c:if test="${model.visibility.trackNumberVisible}">
                                <td ${class} style="padding-right:0.5em;text-align:right">
                                    <span class="detail">${child.metaData.trackNumber}</span>
                                </td>
                            </c:if>

                            <td ${class} style="padding-right:1.25em;white-space:nowrap">
                                <span title="${child.title}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.title)}</str:truncateNicely></span>
                            </td>

                            <c:if test="${model.visibility.albumVisible}">
                                <td ${class} style="padding-right:1.25em;white-space:nowrap">
                                    <span class="detail" title="${child.metaData.album}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.metaData.album)}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.artistVisible and model.multipleArtists}">
                                <td ${class} style="padding-right:1.25em;white-space:nowrap">
                                    <span class="detail" title="${child.metaData.artist}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.metaData.artist)}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.genreVisible}">
                                <td ${class} style="padding-right:1.25em;white-space:nowrap">
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
                                        <c:if test="${child.video and not empty child.metaData.width and not empty child.metaData.height}">
                                            (${child.metaData.width}x${child.metaData.height})
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

    <td style="vertical-align:top;width:100%">
        <c:forEach items="${model.coverArts}" var="coverArt" varStatus="loopStatus">
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
                    <c:param name="appearAfter" value="${loopStatus.count * 30}"/>
                </c:import>
            </div>
        </c:forEach>

        <c:if test="${model.showGenericCoverArt}">
            <div style="float:left; padding:5px">
                <c:import url="coverArt.jsp">
                    <c:param name="albumPath" value="${model.dir.path}"/>
                    <c:param name="coverArtSize" value="${model.coverArtSize}"/>
                    <c:param name="showLink" value="false"/>
                    <c:param name="showZoom" value="false"/>
                    <c:param name="showChange" value="${model.user.coverArtRole}"/>
                    <c:param name="appearAfter" value="0"/>
                </c:import>
            </div>
        </c:if>
    </td>

    <td style="vertical-align:top;">
        <div style="padding:0 1em 0 1em;">
            <c:if test="${not empty model.ad}">
                <div class="detail" style="text-align:center">
                        ${model.ad}
                    <br/>
                    <br/>
                    <sub:url value="donate.view" var="donateUrl">
                        <sub:param name="path" value="${model.dir.path}"/>
                    </sub:url>
                    <fmt:message key="main.donate"><fmt:param value="${donateUrl}"/><fmt:param value="${model.brand}"/></fmt:message>
                </div>
            </c:if>
        </div>
    </td>
</tr>
</table>

<table>
    <c:if test="${not empty model.previousAlbum}">
        <sub:url value="main.view" var="previousUrl">
            <sub:param name="path" value="${model.previousAlbum.path}"/>
        </sub:url>
        <td style="padding-right:10pt"><div class="back"><a href="${previousUrl}" title="${model.previousAlbum.name}">
            <str:truncateNicely upper="30">${fn:escapeXml(model.previousAlbum.name)}</str:truncateNicely>
        </a></div></td>
    </c:if>
    <c:if test="${not empty model.nextAlbum}">
        <sub:url value="main.view" var="nextUrl">
            <sub:param name="path" value="${model.nextAlbum.path}"/>
        </sub:url>
        <td><div class="forward"><a href="${nextUrl}" title="${model.nextAlbum.name}">
            <str:truncateNicely upper="30">${fn:escapeXml(model.nextAlbum.name)}</str:truncateNicely>
        </a></div></td>
    </c:if>
</table>

</body>
</html>
