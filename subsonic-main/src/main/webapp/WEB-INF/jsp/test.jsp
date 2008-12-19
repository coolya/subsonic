<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">


<script type="text/javascript">

    var player = null;
    var songs;

    function onload() {
        dwr.engine.setErrorHandler(null);
        createPlayer();
        getPlaylist();
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
        loadPlaylist();
    }

    function getPlaylist() {
        playlistService.getPlaylist(playlistCallback);
    }

    function playlistCallback(playlist) {
        songs = playlist.entries;
        if (songs.length > 0) {

        }
    }

    function loadPlaylist() {
        var list = new Array();
        list[0] = {
            author:"Author",
            description:"Description",
            duration:33,
            file:"http://localhost/stream?player=2&pathUtf8Hex=653a5c6d757369635c42617265204567696c2042616e645c4162736f6c75747420496b6b652042617265204567696c2042616e645c3038202d205661736b65204d65672053656c762e6d7033&suffix=.mp3",
            //            link:currentPlaylist[i].link,
            //            image:currentPlaylist[i].image,
            //            start:currentPlaylist[i].start,
            title:"Title",
            type:"audio/mp3"
        };
        player.sendEvent('LOAD', list);
    }

</script>

<div style="width:340px; height:20px;">
    <div id="placeholder">Player goes here</div>
</div>

</body>
</html>