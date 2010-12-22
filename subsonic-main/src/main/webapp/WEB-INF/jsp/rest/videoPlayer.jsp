<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="../include.jsp" %>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="../<spring:theme code="styleSheet"/>" type="text/css">
    <link rel="shortcut icon" href="../<spring:theme code="faviconImage"/>">

    <c:url value="/rest/stream.view" var="streamUrl">
        <c:param name="u" value="${model.u}"/>
        <c:param name="p" value="${model.p}"/>
        <c:param name="c" value="${model.c}"/>
        <c:param name="v" value="${model.v}"/>
        <c:param name="id" value="${model.id}"/>
        <c:param name="maxBitRate" value="${model.maxBitRate}"/>
        <c:param name="timeOffset" value="${model.timeOffset}"/>
        <c:param name="suffix" value=".flv"/>
    </c:url>

    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" language="javascript">

        function init() {
            var flashvars = {
                id:"player1",
                file:"<str:replace replace="&" with="%26">${streamUrl}</str:replace>",
                skin:"<c:url value="/flash/whotube.zip"/>",
                screencolor:"000000",
                duration:"${model.duration}",
                autostart:"${model.autoplay}",
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

            swfobject.embedSWF("<c:url value="/flash/jw-player-5.4.swf"/>", "placeholder1", "360", "240", "9.0.0", false, flashvars, params, attributes);
        }

    </script>
</head>

<body class="mainframe bgcolor1" onload="init();">
<h1>${model.video.title}</h1>

<div id="wrapper" style="padding-top:1em">
    <div id="placeholder1"><span class="warning"><fmt:message key="playlist.getflash"/></span></div>
</div>

<div style="padding-top:0.7em;padding-bottom:0.7em">
    <form action="videoPlayer.view" method="post" name="videoForm">
    <input type="hidden" name="id" value="${model.id}">
    <input type="hidden" name="u" value="${model.u}">
    <input type="hidden" name="p" value="${model.p}">
    <input type="hidden" name="c" value="${model.c}">
    <input type="hidden" name="v" value="${model.v}">

    <select name="timeOffset" onchange="document.videoForm.submit()" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
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

    <select name="maxBitRate" onchange="document.videoForm.submit()" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
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
</form>
</div>

</body>
</html>
