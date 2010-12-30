<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>

    <sub:url value="/stream" var="streamUrl">
        <sub:param name="path" value="${model.video.path}"/>
    </sub:url>

    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" language="javascript">

        var player;
        var position;
        var maxBitRate = ${model.maxBitRate};
        var timeOffset = ${model.timeOffset};

        function init() {

            var flashvars = {
                id:"player1",
                skin:"<c:url value="/flash/whotube.zip"/>",
//                plugins:"metaviewer-1",
                screencolor:"000000",
                controlbar:"over",
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

            swfobject.embedSWF("<c:url value="/flash/jw-player-5.4.swf"/>", "placeholder1", "600", "360", "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            player = $("player1");
            player.addModelListener("TIME", "timeListener");

        <c:if test="${not (model.trial and model.trialExpired)}">
            play();
        </c:if>
        }

        function play() {
            var list = new Array();
            list[0] = {
                file:"${streamUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&player=${model.player}",
                duration:${model.duration} - timeOffset,
                provider:"video"
            };
            player.sendEvent("LOAD", list);
            player.sendEvent("PLAY");
        }

        function timeListener(obj) {
            var newPosition = Math.round(obj.position);
            if (newPosition != position) {
                position = newPosition;
                updatePosition();
            }
        }

        function updatePosition() {
            var pos = parseInt(timeOffset) + parseInt(position);

            var minutes = Math.round(pos / 60);
            var seconds = pos % 60;

            var result = minutes + ":";
            if (seconds < 10) {
                result += "0";
            }
            result += seconds;
            $("position").innerHTML = result;
        }

        function changeBitRateAndOffset() {
            maxBitRate = $("maxBitRate").getValue();
            timeOffset = $("timeOffset").getValue();
            play();
        }

    </script>
</head>

<body class="mainframe bgcolor1" onload="init();">
<h1>${model.video.title}</h1>

<c:if test="${model.trial}">
    <fmt:formatDate value="${model.trialExpires}" dateStyle="long" var="expiryDate"/>

    <p class="warning" style="padding-top:1em">
        <c:choose>
            <c:when test="${model.trialExpired}">
                <fmt:message key="networksettings.trialexpired"><fmt:param>${expiryDate}</fmt:param></fmt:message>
            </c:when>
            <c:otherwise>
                <fmt:message
                        key="networksettings.trialnotexpired"><fmt:param>${expiryDate}</fmt:param></fmt:message>
            </c:otherwise>
        </c:choose>
    </p>
</c:if>


<div id="wrapper" style="padding-top:1em">
    <div id="placeholder1"><a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a></div>
</div>

<div style="padding-top:0.7em;padding-bottom:0.7em">

    <span id="position" style="padding-right:0.5em">0:00</span>
    <select id="timeOffset" onchange="changeBitRateAndOffset();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.skipOffsets}" var="skipOffset">
            <c:choose>
                <c:when test="${skipOffset.value eq model.timeOffset}">
                    <option selected="selected" value="${skipOffset.value}">${skipOffset.key}</option>
                </c:when>
                <c:otherwise>
                    <option value="${skipOffset.value}">${skipOffset.key}</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>

    <select id="maxBitRate" onchange="changeBitRateAndOffset();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.bitRates}" var="bitRate">
            <c:choose>
                <c:when test="${bitRate eq model.maxBitRate}">
                    <option selected="selected" value="${bitRate}">${bitRate} Kbps</option>
                </c:when>
                <c:otherwise>
                    <option value="${bitRate}">${bitRate} Kbps</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
</div>

<div style="padding-bottom:0.5em">
    <sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.video.parent.path}"/></sub:url>
    <div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
</div>

</body>
</html>
