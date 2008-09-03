<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">

<!-- This script uses AJAX to periodically check if the current song has changed. -->
<script type="text/javascript" language="javascript">
    var currentFile = null;

    function onload() {
        DWREngine.setErrorHandler(null);
        location.hash="${model.anchor}";
        startTimer();
        onSelectionChange();
    }

    function startTimer() {
        nowPlayingService.getFile(nowPlayingCallback);
        setTimeout("startTimer()", 10000);
    }

    function nowPlayingCallback(file) {
        if (currentFile != null && currentFile != file) {
            location.replace("playlist.view?");
        }
        currentFile = file;
    }
    function clearPlaylist() {
    <c:choose>
    <c:when test="${model.partyMode}">
        if (confirm("<fmt:message key="playlist.confirmclear"/>")) {
            location.href = "playlist.view?clear";
        }
    </c:when>
    <c:otherwise>
        location.href = "playlist.view?clear";
    </c:otherwise>
    </c:choose>
    }
</script>

<!-- actionSelected() is invoked when the users selects from the "More actions..." combo box. -->
<script type="text/javascript" language="javascript">
    var N = ${fn:length(model.songs)};
    var downloadEnabled = ${model.user.downloadRole ? "true" : "false"};

    function actionSelected(id) {
        if (id == "top") {
            return;
        } else if (id == "loadPlaylist") {
            parent.frames.main.location.href = "loadPlaylist.view?";
        } else if (id == "savePlaylist") {
            parent.frames.main.location.href = "savePlaylist.view?";
        } else if (id == "downloadPlaylist") {
            location.href = "download.view?player=${model.player.id}";
        } else if (id == "sortByTrack") {
            location.href = "playlist.view?sortByTrack";
        } else if (id == "sortByArtist") {
            location.href = "playlist.view?sortByArtist";
        } else if (id == "sortByAlbum") {
            location.href = "playlist.view?sortByAlbum";
        } else if (id == "selectAll") {
            selectAll(true);
            onSelectionChange();
        } else if (id == "selectNone") {
            selectAll(false);
            onSelectionChange();
        } else if (id == "remove") {
            location.href = "playlist.view?remove=" + getSelectedIndexes();
        } else if (id == "download") {
            location.href = "download.view?player=${model.player.id}&indexes=" + getSelectedIndexes();
        }
        $("moreActions").selectedIndex = 0;
    }

    function getSelectedIndexes() {
        var result = "";
        for (var i = 0; i < N; i++) {
            if ($("songIndex" + i).checked) {
                result += (i + " ");
            }
        }
        return result;
    }

    function selectAll(b) {
        for (var i = 0; i < N; i++) {
            $("songIndex" + i).checked = b;
        }
    }

    function isSelectionEmpty() {
        for (var i = 0; i < N; i++) {
            if ($("songIndex" + i).checked) {
                return false;
            }
        }
        return true;
    }

    function onSelectionChange() {
        var selectionEmpty = isSelectionEmpty();

        var remove = $("moreActions").options["remove"];
        remove.disabled = selectionEmpty ? "disabled" : "";

        var download = $("moreActions").options["download"];
        if (download) {
            download.disabled = (selectionEmpty || !downloadEnabled) ? "disabled" : "";
        }
    }

</script>

<a name="-1">
    <h2>
        <table style="white-space:nowrap;">
            <tr>
                <td><select name="player" onchange="location='playlist.view?player=' + options[selectedIndex].value;">
                    <c:forEach items="${model.players}" var="player">
                <option ${player.id eq model.player.id ? "selected" : ""} value="${player.id}">${player}</option>
            </c:forEach>
        </select></td>

       <c:if test="${model.user.streamRole}">
           <c:choose>
               <c:when test="${model.isPlaying and not model.player.clientSidePlaylist}">
                   <td><b><a href="playlist.view?stop"><fmt:message key="playlist.stop"/></a></b> | </td>
               </c:when>
               <c:otherwise>
                   <td><b><a href="playlist.view?start"><fmt:message key="playlist.start"/></a></b> | </td>
               </c:otherwise>
           </c:choose>
       </c:if>

        <td><a href="javascript:clearPlaylist()"><fmt:message key="playlist.clear"/></a></td>
        <td> | <a href="playlist.view?shuffle"><fmt:message key="playlist.shuffle"/></a></td>

        <c:if test="${not model.player.clientSidePlaylist}">
            <c:choose>
                <c:when test="${model.repeatEnabled}">
                    <td> | <a href="playlist.view?repeat"><fmt:message key="playlist.repeat_on"/></a></td>
                </c:when>
                <c:otherwise>
                    <td> | <a href="playlist.view?repeat"><fmt:message key="playlist.repeat_off"/></a></td>
                </c:otherwise>
            </c:choose>
        </c:if>

        <td> | <a href="playlist.view?undo"><fmt:message key="playlist.undo"/></a></td>
                <c:if test="${model.user.streamRole and not empty model.songs}">
        <td> | <a href="webPlayer.view?"><fmt:message key="playlist.webplayer"/></a></td>
                </c:if>
        <td> | <a href="playerSettings.view?id=${model.player.id}" target="main"><fmt:message key="playlist.settings"/></a></td>

        <td> | <select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id)">
            <option id="top" selected="selected"><fmt:message key="playlist.more"/></option>
            <option disabled="disabled"><fmt:message key="playlist.more.playlist"/></option>
            <option id="loadPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.load"/></option>
            <c:if test="${model.user.playlistRole}">
                <option id="savePlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.save"/></option>
            </c:if>
            <c:if test="${model.user.downloadRole}">
                <option id="downloadPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
            </c:if>
            <option id="sortByTrack">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbytrack"/></option>
            <option id="sortByAlbum">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbyalbum"/></option>
            <option id="sortByArtist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.sortbyartist"/></option>
            <option disabled="disabled"><fmt:message key="playlist.more.selection"/></option>
            <option id="selectAll">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectall"/></option>
            <option id="selectNone">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectnone"/></option>
            <option id="remove">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.remove"/></option>
            <c:if test="${model.user.downloadRole}">
                <option id="download">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
            </c:if>
        </select>
        </td>
    </tr></table></h2>
</a>

<c:choose>
    <c:when test="${empty model.songs}">
        <p><em><fmt:message key="playlist.empty"/></em></p>
    </c:when>
    <c:otherwise>
        <table style="border-collapse:collapse;white-space:nowrap;">
            <c:set var="cutoff" value="${model.visibility.captionCutoff}"/>
            <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
                <c:set var="i" value="${loopStatus.count - 1}"/>
                <tr style="margin:0;padding:0;border:0">

                    <td><a name="${i}" href="playlist.view?remove=${i}"><img src="<spring:theme code="removeImage"/>"
                                                                 alt="<fmt:message key="playlist.remove"/>"
                                                                 title="<fmt:message key="playlist.remove"/>"/></a></td>
                    <td><a href="playlist.view?up=${i}"><img src="<spring:theme code="upImage"/>"
                                                             alt="<fmt:message key="playlist.up"/>"
                                                             title="<fmt:message key="playlist.up"/>"/></a></td>
                    <td><a href="playlist.view?down=${i}"><img src="<spring:theme code="downImage"/>"
                                                               alt="<fmt:message key="playlist.down"/>"
                                                               title="<fmt:message key="playlist.down"/>"/></a></td>
                    <sub:url value="main.view" var="mainUrl">
                        <sub:param name="path" value="${song.musicFile.parent.path}"/>
                    </sub:url>
                    <c:choose>
                        <c:when test="${i % 2 == 0}">
                            <c:set var="class" value="class='bgcolor1'"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="class" value=""/>
                        </c:otherwise>
                    </c:choose>

                    <td style="padding-left: 0.1em"><input type="checkbox" class="checkbox" id="songIndex${i}" onchange="onSelectionChange()"/></td>
                    <td ${class} style="padding-right:0.25em"/>

                    <c:if test="${model.visibility.trackNumberVisible}">
                        <td ${class} style="padding-right:0.5em;text-align:right">
                            <span class="detail">${song.musicFile.metaData.trackNumber}</span>
                        </td>
                    </c:if>

                    <td ${class} style="padding-right:1.25em">
                        <c:choose>
                            <c:when test="${model.player.clientSidePlaylist}">
                                <span title="${song.musicFile.metaData.title}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(song.musicFile.title)}</str:truncateNicely></span>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${song.current}">
                                    <img src="<spring:theme code="currentImage"/>" alt=""/>
                                </c:if>
                                <a href="playlist.view?skip=${i}" title="${song.musicFile.metaData.title}">${song.current ? "<b>" : ""}<str:truncateNicely upper="${cutoff}">${fn:escapeXml(song.musicFile.title)}</str:truncateNicely>${song.current ? "</b>" : ""}</a>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <c:if test="${model.visibility.albumVisible}">
                        <td ${class} style="padding-right:1.25em">
                            <span class="detail" title="${song.musicFile.metaData.album}"><a target="main" href="${mainUrl}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(song.musicFile.metaData.album)}</str:truncateNicely></a></span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.artistVisible}">
                        <td ${class} style="padding-right:1.25em">
                            <span class="detail" title="${song.musicFile.metaData.artist}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(song.musicFile.metaData.artist)}</str:truncateNicely></span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.genreVisible}">
                        <td ${class} style="padding-right:1.25em">
                            <span class="detail">${song.musicFile.metaData.genre}</span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.yearVisible}">
                        <td ${class} style="padding-right:1.25em">
                            <span class="detail">${song.musicFile.metaData.year}</span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.formatVisible}">
                        <td ${class} style="padding-right:1.25em">
                            <span class="detail">${fn:toLowerCase(song.musicFile.metaData.format)}</span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.fileSizeVisible}">
                        <td ${class} style="padding-right:1.25em;text-align:right">
                            <span class="detail"><sub:formatBytes bytes="${song.musicFile.metaData.fileSize}"/></span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.durationVisible}">
                        <td ${class} style="padding-right:1.25em;text-align:right">
                            <span class="detail">${song.musicFile.metaData.durationAsString}</span>
                        </td>
                    </c:if>

                    <c:if test="${model.visibility.bitRateVisible}">
                        <td ${class} style="padding-right:0.25em">
                            <span class="detail">
                                <c:if test="${not empty song.musicFile.metaData.bitRate}">
                                ${song.musicFile.metaData.bitRate} Kbps ${song.musicFile.metaData.variableBitRate ? "vbr" : ""}
                                </c:if>
                            </span>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<c:if test="${model.sendM3U}">
    <script language="javascript" type="text/javascript">parent.frames.main.location.href="play.m3u?"</script>
</c:if>
</body></html>