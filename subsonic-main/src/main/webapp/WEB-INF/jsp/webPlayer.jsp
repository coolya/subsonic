<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <title>Subsonic</title>

    <c:set var="width" value="600"/>
    <c:set var="height" value="155"/>

    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" language="javascript">

        function detach() {
            popupSize("webPlayer.view?detached=", 'player', ${width + 20}, ${height + 60});
            location.href = "playlist.view?";
        }

    </script>
</head>

<body class="bgcolor2" <c:if test="${model.detached}">onload="window.focus();window.moveTo(300, 200);"</c:if>>

<c:if test="${not model.detached and not model.default}">
    <table><tr>
        <td style="padding-right:2em"><div class="back"><a href="playlist.view?"><fmt:message key="webplayer.back"/></a></div></td>
        <td style="padding-right:2em"><div class="forward"><a href="javascript:detach()"><fmt:message key="webplayer.detach"/></a></div></td>
    </tr></table>
</c:if>

<c:url var="playlistUrl" value="/xspfPlaylist.view">
    <%-- Hack to force Flash player to reload playlist. --%>
    <c:param name="dummy" value="${model.dummy}"/>
</c:url>
<c:url var="playerUrl" value="/flash/xspf_player-0.2.3.swf?playlist_url=${playlistUrl}&autoplay=true&autoload=true"/>

<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
        codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0"
        width="${width}" height="${height}">
    <param name="allowScriptAccess" value="sameDomain"/>
    <param name="movie" value="${playerUrl}"/>
    <param name="quality" value="high"/>
    <param name="bgcolor" value="#E6E6E6"/>
    <embed src="${playerUrl}" quality="high" bgcolor="#E6E6E6" name="xspf_player" allowscriptaccess="sameDomain"
           type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"
           align="middle" height="${height}" width="${width}"></embed>
</object>

<c:if test="${model.detached}">
    <p style="text-align:center;margin-top:1em">
        <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
    </p>
</c:if>

</body>
</html>