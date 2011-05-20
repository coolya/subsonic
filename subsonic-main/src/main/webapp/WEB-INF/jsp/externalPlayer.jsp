<%--@elvariable id="model" type="java.util.Map"--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <sub:param name="size" value="120"/>
        <c:if test="${not empty model.coverArt}">
            <sub:param name="path" value="${model.coverArt.path}"/>
        </c:if>
    </sub:url>

    <%--todo--%>
    <meta name="og:title" content="${model.songs[0].metaData.artist} - ${model.songs[0].metaData.album}" />
    <%--<meta property="og:description" content="Electra at JPS Bristol 22nd October, 2010" />--%>
    <meta name="og:type" content="album"/>
    <meta name="og:image" content="http://${model.redirectFrom}.subsonic.org/${coverArtUrl}"/>

    <script type="text/javascript">
        function init() {
            var flashvars = {
                id:"player1",
                backcolor:"<spring:theme code="backgroundColor"/>",
                screencolor:"<spring:theme code="backgroundColor"/>",
                frontcolor:"<spring:theme code="textColor"/>",
                "playlist.position": "bottom",
                "playlist.size": 275
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.4.swf"/>", "placeholder", "340", "300", "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            var player = $("player1");
            var list = new Array();

        <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <sub:url value="/stream" var="streamUrl">
        <sub:param name="path" value="${song.path}"/>
        </sub:url>

//            TODO: Escape song title
            list[${loopStatus.count-1}] = {
                file: "${streamUrl}&player=${model.player}",
                title: "${song.title}",
                provider:"sound",
                description: "${song.metaData.artist}"
            };

        <c:if test="${not empty song.metaData.duration}">
            list[${loopStatus.count-1}].duration = ${song.metaData.duration};
        </c:if>

        </c:forEach>


//            TODO: Replace
            player.sendEvent("LOAD", list);
            player.sendEvent("PLAY");
        }

    </script>
</head>

<body class="mainframe bgcolor1" style="padding-top:3em" onload="init();">

<div style="margin:auto;width:500px">
    <div style="float:left;padding-right:1.5em">
        <img src="${coverArtUrl}" alt="">
    </div>
    <h1>${model.songs[0].metaData.artist}</h1>
    <h2>${model.songs[0].metaData.album}</h2>

    <div style="clear:both;padding-top:2em">
        <div id="placeholder">
            <a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a>
        </div>
    </div>

    <div class="detail" style="padding-top:2em">Streaming by <a href="http://subsonic.org/" target="_blank">Subsonic</a></div>
</div>
</body>
</html>
