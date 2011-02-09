<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <sub:param name="size" value="100"/>
        <c:if test="${not empty model.coverArt}">
            <sub:param name="path" value="${model.coverArt.path}"/>
        </c:if>
    </sub:url>

    <%--todo--%>
    <meta property="og:title" content="22nd October, 2010 - Electra Bristol" />
    <meta property="og:description" content="Electra at JPS Bristol 22nd October, 2010" />
    <meta property="og:type" content="album"/>
    <meta property="og:image" content="http://${model.redirectFrom}.subsonic.org/${coverArtUrl}"/>

    <sub:url value="/stream" var="streamUrl">
        <sub:param name="path" value="${model.dir.path}"/>
    </sub:url>
</head>

<body class="mainframe bgcolor1" style="padding-bottom:0.5em" onload="init();">
<h1>${model.songs[0].metaData.artist}</h1>
<h2>${model.songs[0].metaData.album}</h2>

<p>

    <img src="${coverArtUrl}" alt="">
</p>


</body>
</html>
