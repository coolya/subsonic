<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<h1><fmt:message key="share.title"/></h1>

    Bla bla bla.  Bla.  Bla bla. Blabla bla blabla.
    Bla bla bla.  Bla.  Bla bla. Blabla bla blabla.
    Bla bla bla.  Bla.  Bla bla. Blabla bla blabla.
    Bla bla bla.  Bla.  Bla bla. Blabla bla blabla.
    Bla bla bla.  Bla.  Bla bla. Blabla bla blabla.
</p>

<p>
    <c:url value="http://www.facebook.com/sharer.php" var="facebookUrl"><c:param name="u" value="${model.playUrl}"/></c:url>
    <img src="<spring:theme code="shareFacebookImage"/>" alt="">&nbsp;<a href="${facebookUrl}" target="_blank">Share on Facebook</a>
</p>

<p>
    <%--<c:url value="http://twitter.com/" var="twitterUrl"><c:param name="status" value="${playUrl}"/></c:url>--%>
    <%--<img src="<spring:theme code="shareTwitterImage"/>" alt="">&nbsp;<a href="${twitterUrl}" target="_blank">Share on Twitter</a>--%>
    <img src="<spring:theme code="shareTwitterImage"/>" alt="">&nbsp;<a href="http://twitter.com/?status=Listening to ${model.playUrl} on #Subsonic" target="_blank">Share on Twitter</a>
</p>

<p>
    Share this album with someone by sending them this link:
    <a href="${model.playUrl}">${model.playUrl}</a>
</p>

<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.file.path}"/></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

</body>
</html>