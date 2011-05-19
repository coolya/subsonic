<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<h1 style="padding-bottom:1em"><fmt:message key="share.title"/></h1>

<c:choose>
    <c:when test="${model.urlRedirectionEnabled}">

        <p>
            Play fair - don't share copyrighted material in any way that violates your local laws.
        </p>
        <p>
            <img src="<spring:theme code="shareFacebookImage"/>" alt="">&nbsp;<a
                href="http://www.facebook.com/sharer.php?u=${model.playUrl}" target="_blank">Share on Facebook</a>
        </p>

        <p>
            <img src="<spring:theme code="shareTwitterImage"/>" alt="">&nbsp;<a
                href="http://twitter.com/?status=Listening to ${model.playUrl}" target="_blank">Share on Twitter</a>
        </p>

        <p>
            Share this album with someone by sending them this link:
            <a href="${model.playUrl}" target="_blank">${model.playUrl}</a>
        </p>
    </c:when>
    <c:otherwise>
        <p>
            To share your music with someone you must first register your own <em>subsonic.org</em> address.<br>
            Please go to <a href="/networkSettings.view"><b>Settings &gt; Network</b></a> (administrative
            rights required).
        </p>
    </c:otherwise>
</c:choose>

<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.dir.path}"/></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

</body>
</html>