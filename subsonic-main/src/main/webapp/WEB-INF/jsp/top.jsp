<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="/include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
    <!--[if gte IE 5.5000]>
     <script type="text/javascript" src="pngfix.js"></script>
     <![endif]-->
</head>
<body style="background-color:#DEE3E7">

<fmt:message key="top.home" var="home"/>
<fmt:message key="top.now_playing" var="nowPlaying"/>
<fmt:message key="top.settings" var="settings"/>
<fmt:message key="top.status" var="status"/>
<fmt:message key="top.more" var="more"/>
<fmt:message key="top.help" var="help"/>
<fmt:message key="top.search" var="search"/>

<table><tr valign="middle">
    <td style="font-size:16pt"><a href="help.view?" target="main"><img src="<c:url value="/icons/logo.gif"/>" title="${help}"/></a>&nbsp;Subsonic</td>
    <td style="padding-left:20pt; padding-right:20pt">

        <c:choose>
            <c:when test="${model.musicFoldersExist}">
                <c:if test="${fn:length(model.indexes) > 1}">
                    <div style="white-space:nowrap;">
                        <c:forEach begin="0" end="${fn:length(model.indexes) / 2 - 1}" var="i">
                            <c:set var="index" value="${model.indexes[i]}"/>
                            <a style="cursor:pointer" onclick="javascript:parent.frames.left.location.hash='${index.index}'">${index.index}</a>
                        </c:forEach>
                    </div>

                    <div style="white-space:nowrap;">
                        <c:forEach begin="${fn:length(model.indexes) / 2}" end="${fn:length(model.indexes) - 1}" var="i">
                            <c:set var="index" value="${model.indexes[i]}"/>
                            <a style="cursor:pointer" onclick="javascript:parent.frames.left.location.hash='${index.index}'">${index.index}</a>
                        </c:forEach>
                    </div>
                </c:if>
            </c:when>

            <c:otherwise>
                <p style="color:red"><fmt:message key="top.missing"/></p>
            </c:otherwise>
        </c:choose>

    </td>

    <td>
        <table><tr align="middle">
            <td style="width:40pt;padding-right:10pt"><a href="home.jsp?" target="main"><img src="<c:url value="/icons/home.png"/>" title="${home}" alt="${home}"/><br/>${home}</a></td>
            <td style="width:40pt;padding-right:10pt"><a href="nowPlaying.jsp?" target="main"><img src="<c:url value="/icons/now_playing.png"/>" title="${nowPlaying}" alt="${nowPlaying}"/><br/>${nowPlaying}</a></td>
            <td style="width:40pt;padding-right:10pt"><a href="settings.jsp?" target="main"><img src="<c:url value="/icons/settings.png"/>" title="${settings}" alt="${settings}"/><br/>${settings}</a></td>
            <td style="width:40pt;padding-right:10pt"><a href="status.view?" target="main"><img src="<c:url value="/icons/status.png"/>" title="${status}" alt="${status}"/><br/>${status}</a></td>
            <td style="width:40pt;padding-right:10pt"><a href="more.view?" target="main"><img src="<c:url value="/icons/more.png"/>" title="${more}" alt="${more}"/><br/>${more}</a></td>
            <td style="width:40pt;padding-right:10pt"><a href="help.view?" target="main"><img src="<c:url value="/icons/help.png"/>" title="${help}" alt="${help}"/><br/>${help}</a></td>

            <td style="padding-left:15pt">
                <table><tr>
                    <form method="post" action="search.jsp" target="main">
                        <td><input type="text" name="query" size="14"/></td><td><input type="image" src="<c:url value="/icons/search_small.png"/>" alt="${search}" title="${search}"/></td>
                        <input type="hidden" name="includeTitle" value="on"/>
                        <input type="hidden" name="includeArtistAndAlbum" value="on"/>
                    </form>
                </tr></table>
            </td>

            <c:if test="${model.newVersionAvailable}">
                <td style="padding-left:15pt">
                    <p style="color:red; white-space:nowrap;">
                        <fmt:message key="top.upgrade"><fmt:param value="${model.latestVersion}"/></fmt:message>
                    </p>
                </td>
            </c:if>
        </tr></table>
    </td>

</tr></table>
</body></html>