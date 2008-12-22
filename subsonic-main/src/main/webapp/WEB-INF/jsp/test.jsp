<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">


<script type="text/javascript">

    var player = null;
    var songs;

    function onload() {
        dwr.engine.setErrorHandler(null);
        createPlayer();
    }

    function createPlayer() {
        var flashvars = {
//            file:"video.flv",
//            autostart:"true"
        }
        var params = {
            allowfullscreen:"true",
            allowscriptaccess:"always"
        }
        var attributes = {
            id:"player",
            name:"player"
        }
        swfobject.embedSWF("<c:url value="/flash/player.swf"/>", "placeholder", "340", "20", "9.0.115", false, flashvars, params, attributes);
    }

    function playerReady(thePlayer) {
        player = $(thePlayer.id);
        player.addModelListener("STATE", "stateListener");
        getPlaylist();
    }

    function stateListener(obj) { // IDLE, BUFFERING, PLAYING, PAUSED, COMPLETED
        var currentState = obj.newstate;
        var previousState = obj.oldstate;

        dwr.util.setValue("state", previousState + " >  " + currentState);
    }

    function mute() {
        player.sendEvent("MUTE");
    }

    function getPlaylist() {
        playlistService.getPlaylist(playlistCallback);
    }

    function playlistCallback(playlist) {
        songs = playlist.entries;
        var list = new Array();
        for (var i = 0; i < songs.length; i++) {
            var song = songs[i];
            list[i] = {
                //                author:"Author",
                //                description:"Description",
                duration:song.duration,
                file:song.streamUrl,
                //            link:currentPlaylist[i].link,
                //            image:currentPlaylist[i].image,
                //            start:currentPlaylist[i].start,
                title:song.title,
                type:song.contentType
            };
        }
        player.sendEvent('LOAD', list);
    }

</script>

<div style="width:340px; height:20px;">
    <div id="placeholder"> </div>
</div>

<div id="state">Unknown</div>

<a href="javascript:noop()" onclick="mute()">Mute</a>

</body>
</html>