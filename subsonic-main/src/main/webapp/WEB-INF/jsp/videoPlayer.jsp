<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/videoService.js"/>"></script>
</head>

<body class="mainframe bgcolor1" style="margin:15px" onload="init();">

<sub:url value="/stream" var="streamUrl">
    <sub:param name="path" value="${model.video.path}"/>
</sub:url>

<script type="text/javascript" language="javascript">
    var player = null;
    var playerVisible = true;

    function init() {
        dwr.engine.setErrorHandler(null);
        videoService.getVideoQualities(getVideoQualitiesCallback);
        createPlayer();
    }

    function playerReady(thePlayer) {
        player = $(thePlayer.id);
    }

    function getVideoQualitiesCallback(qualities) {
        for (var i = 0; i < qualities.length; i++) {
//            alert(qualities[i]);
        }
    }

    function togglePlayer() {
        var control = $("control");
        var foo = $("foo");

        if (playerVisible) {
            //            deletePlayer();
            foo.hide();
            control.show();

        } else {
            //        if (player == null) {
            //            createPlayer();
            control.hide();
            foo.show();
        }
        playerVisible = !playerVisible;
    }

    function createPlayer() {
        var flashvars = {
            id:"player1",
            file:"${streamUrl}",
            duration:"${model.video.metaData.duration}",
            autostart:"false",
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
        swfobject.embedSWF("<c:url value="/flash/jw-player-5.0.swf"/>", "placeholder1", "100%", "100%", "9.0.0", false, flashvars, params, attributes);
    }

    function deletePlayer() {
        swfobject.removeSWF("player1");
        player = null;
        var tmp = document.getElementById("wrapper");
        if (tmp) {
            tmp.innerHTML = "<div id='placeholder1'></div>";
        }
    }

</script>


<input type="button" value="Toggle player" onClick="togglePlayer();"/>

<div id="control">
</div>

<div id="foo" style="width:100%; height:95%">
    <div id="wrapper" style="padding-top:1em">
        <div id="placeholder1"></div>
    </div>
</div>

</body>
</html>
