<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
</head>

<body class="mainframe bgcolor1" style="margin:15px" onload="init();">

<sub:url value="/stream" var="streamUrl">
    <sub:param name="path" value="${model.video.path}"/>
</sub:url>

<script type="text/javascript" language="javascript">
    var player = null;
    var playerVisible = true;

    function init() {
//        createPlayer();
    }

    function playerReady(thePlayer) {
        player = $(thePlayer.id);
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

<p>The video is available in the following qualities:</p>
<table>
    <c:forEach items="${model.processedVideos}" var="video">
        <tr>
            <td>${video.quality}</td>
            <td>${video.status}</td>
            <td>${video.bitRate}</td>
            <td>${video.size}</td>
        </tr>
    </c:forEach>
</table>

<form action="videoPlayer.view" method="POST">
    Process video in quality
    <select name="quality">
        <c:forEach items="${model.qualities}" var="quality">
            <option value="${quality}">${quality}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="process" value="true"/>
    <input type="hidden" name="path" value="${model.video.path}"/>
    <input type="submit" value="Start"/>
</form>
<%--<input type="button" value="Toggle player" onClick="togglePlayer();"/>--%>

<div id="control">
</div>

<div id="foo" style="width:100%; height:95%">
    <div id="wrapper" style="padding-top:1em">
        <div id="placeholder1"></div>
    </div>
</div>

</body>
</html>
