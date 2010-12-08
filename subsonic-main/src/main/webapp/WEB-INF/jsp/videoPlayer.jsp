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

        function init() {

            var flashvars = {
                id:"player1",
                <c:if test="${not (model.trial and model.trialExpired)}">
                file:"${streamUrl}%26maxBitRate=${model.maxBitRate}",
                </c:if>
                duration:"${model.video.metaData.duration}",
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

            swfobject.embedSWF("<c:url value="/flash/jw-player-5.0.swf"/>", "placeholder1", "600", "360", "9.0.0", false, flashvars, params, attributes);
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
    <div id="placeholder1"></div>
</div>

<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.video.parent.path}"/></sub:url>

<div class="detail" style="padding-top:0.7em;padding-bottom:0.7em">
    <span style="padding-right:0.5em"><fmt:message key="videoplayer.bitrate"/></span>
    <c:forEach items="${model.bitRates}" var="bitRate">
        <span style="padding-right:0.5em">
            <sub:url value="/videoPlayer.view" var="videoUrl">
                <sub:param name="path" value="${model.video.path}"/>
                <sub:param name="maxBitRate" value="${bitRate}"/>
            </sub:url>
            <c:choose>
                <c:when test="${bitRate eq model.maxBitRate}"><b>${bitRate}</b></c:when>
                <c:otherwise><a href="${videoUrl}">${bitRate}</a></c:otherwise>
            </c:choose>
        </span>
    </c:forEach>
</div>

<div style="padding-bottom:0.5em">
    <div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
</div>

</body>
</html>
