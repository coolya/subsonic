<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <title>Subsonic</title>
</head>

<body class="bgcolor2">

<table><tr>
    <td style="padding-right:2em"><div class="back"><a href="playlist.view?"><fmt:message key="webplayer.back"/></a></div></td>
    <td style="padding-right:2em"><div class="forward"><a href="webPlayer.view?" onclick="return popup(this, 'player')"><fmt:message key="webplayer.detach"/></a></div></td>
</tr></table>

<c:url var="playlistUrl" value="/xspfPlaylist.view"/>
<c:url var="playerUrl" value="/flash/xspf_player.swf?playlist_url=${playlistUrl}&autoplay=true&autoload=true"/>

<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
        codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0"
        width="600" height="155">
    <param name="allowScriptAccess" value="sameDomain"/>
    <param name="movie" value="${playerUrl}"/>
    <param name="quality" value="high"/>
    <param name="bgcolor" value="#E6E6E6"/>
    <embed src="${playerUrl}" quality="high" bgcolor="#E6E6E6" name="xspf_player" allowscriptaccess="sameDomain"
           type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"
           align="middle" height="155" width="600"></embed>
</object>

</body>
</html>