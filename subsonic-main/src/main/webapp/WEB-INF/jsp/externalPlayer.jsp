<%--@elvariable id="model" type="java.util.Map"--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <sub:param name="size" value="100"/>
        <c:if test="${not empty model.coverArt}">
            <sub:param name="path" value="${model.coverArt.path}"/>
        </c:if>
    </sub:url>

    <%--todo--%>
    <meta name="og:title" content="${model.songs[0].metaData.artist} - ${model.songs[0].metaData.album}" />
    <%--<meta property="og:description" content="Electra at JPS Bristol 22nd October, 2010" />--%>
    <meta name="og:type" content="album"/>
    <meta name="og:image" content="http://${model.redirectFrom}.subsonic.org/${coverArtUrl}"/>

    <%--<sub:url value="/stream" var="streamUrl">--%>
        <%--<sub:param name="path" value="${model.dir.path}"/>--%>
    <%--</sub:url>--%>
    <script type="text/javascript">
        function init() {
            var flashvars = {
                backcolor:"<spring:theme code="backgroundColor"/>",
                frontcolor:"<spring:theme code="textColor"/>",
                id:"player1"
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.4.swf"/>", "placeholder", "340", "24", "9.0.0", false, flashvars, params, attributes);
        }
    </script>
</head>

<body class="mainframe bgcolor1" style="padding-bottom:0.5em" onload="init();">

<div style="float:none;">
    <div style="float:left;">
        <img src="${coverArtUrl}" alt="">
    </div>
    <h1>${model.songs[0].metaData.artist}</h1>
    <h2>${model.songs[0].metaData.album}</h2>
</div>

<%--<div id="placeholder">--%>
    <%--<a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a>--%>
<%--</div>--%>
<div style="float:none;">
    Heeeeeeeeeeeeeeeia
</div>
</body>
</html>
