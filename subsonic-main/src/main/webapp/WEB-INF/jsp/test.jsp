<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body class="bgcolor2" onload="onload()">


<script type="text/javascript" src="http://www.jeroenwijering.com/embed/swfobject.js"></script>

<div id="player">This text will be replaced</div>

<script type="text/javascript">
    var so = new SWFObject('/jw/embed/player.swf', 'mpl', '340', '20', '9');
    so.addParam('allowscriptaccess', 'always');
    so.addParam('allowfullscreen', 'true');
    so.addParam('flashvars', '');
    so.write('player');
</script>


<!-- This script uses AJAX to periodically check if the current song has changed. -->
<script type="text/javascript" language="javascript">

    var songs;

    function onload() {
        dwr.engine.setErrorHandler(null);
        getPlaylist();
    }

    function getPlaylist() {
        playlistService.getPlaylist(playlistCallback);
    }

    function playlistCallback(playlist) {
        songs = playlist.entries;
    }

</script>


</body>
</html>