<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
</head>

<body class="mainframe bgcolor1" style="margin:15px" onload="init();">

<sub:url value="/stream" var="streamUrl">
    <sub:param name="path" value="${model.video.path}"/>
</sub:url>

<script type="text/javascript" language="javascript">
    function init() {
        createPlayer();
    }
    function createPlayer() {
        var flashvars = {
            id:"player1",
            file:"${streamUrl}",
            autostart:"true",
            backcolor:"<spring:theme code="backgroundColor"/>",
            frontcolor:"<spring:theme code="textColor"/>",
            provider:"video"
        };
        var params = {
            allowfullscreen:"true",
            allowscriptaccess:"always"
        };
        var attributes = {
            id:"player1",
            name:"player1"
        };
        swfobject.embedSWF("<c:url value="/flash/jw-player-5.0.swf"/>", "placeholder", "100%", "100%", "9.0.0", false, flashvars, params, attributes);
    }
</script>

<div style="width:100%; height:100%">
    <div id="placeholder">
        <a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a>
    </div>
</div>

</body>
</html>
