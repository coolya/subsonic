<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">

<!-- This script uses AJAX to periodically check if the current song has changed. -->
<script type="text/javascript" language="javascript">

    function onload() {
        dwr.engine.setErrorHandler(null);
        getPlaylist();
    }

    function doNothing() {
    }

    function getPlaylist() {
        playlistService.getPlaylist(playlistCallback);
    }

    function shuffle() {
        playlistService.shuffle(playlistCallback);
    }

    function playlistCallback(playlist) {

        // Delete all the rows except for the "pattern" row
        dwr.util.removeAllRows("playlistBody", { filter:function(tr) {
            return (tr.id != "pattern");
        }});

        // Create a new set cloned from the pattern row
        for (var i = 0; i < playlist.entries.length; i++) {
            var entry  = playlist.entries[i];
            var id = i + 1;
            dwr.util.cloneNode("pattern", { idSuffix:id });
            dwr.util.setValue("title" + id, entry.title);
            dwr.util.setValue("album" + id, entry.album);
            dwr.util.setValue("artist" + id, entry.artist);
            $("albumUrl" + id).href = entry.albumUrl;
            $("pattern" + id).style.display = "table-row";
            $("pattern" + id).className = (i % 2 == 0) ? "bgcolor1" : "bgcolor2";
        }
    }

</script>


<div>
    <a href="javascript:doNothing()" onclick="shuffle()">Shuffle</a>
</div>

<table border="1">
    <tbody id="playlistBody">
        <tr id="pattern" style="display:none;">
            <td><span id="title">Title</span></td>
            <td style="display:none;"><span id="year">Year</span></td>
            <td><a id="albumUrl"><span id="album">Album</span></a></td>
            <td><span id="artist">Artist</span></td>
        </tr>
    </tbody>
</table>

</body></html>