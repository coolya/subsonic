<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>

<body class="bgcolor2" style="margin:0.4em 1em 0.4em 1em">

<fmt:message key="top.home" var="home"/>
<fmt:message key="top.now_playing" var="nowPlaying"/>
<fmt:message key="top.settings" var="settings"/>
<fmt:message key="top.status" var="status"/>
<fmt:message key="top.podcast" var="podcast"/>
<fmt:message key="top.more" var="more"/>
<fmt:message key="top.help" var="help"/>
<fmt:message key="top.search" var="search"/>

<table style="margin:0"><tr valign="middle">
    <td class="logo"><a href="help.view?" target="main"><img src="<spring:theme code="logoImage"/>" title="${help}" alt=""/></a></td>
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
                <p class="warning"><fmt:message key="top.missing"/></p>
            </c:otherwise>
        </c:choose>

    </td>

    <td>
        <table><tr align="middle">
            <td style="width:4em;padding-right:1.5em"><a href="home.view?" target="main"><img src="<c:url value="/icons/home.png"/>" title="${home}" alt="${home}"/><br/>${home}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="nowPlaying.view?" target="main"><img src="<c:url value="/icons/now_playing.png"/>" title="${nowPlaying}" alt="${nowPlaying}"/><br/>${nowPlaying}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="podcastReceiver.view?" target="main"><img src="<c:url value="/icons/podcast_large.png"/>" title="${podcast}" alt="${podcast}"/><br/>${podcast}</a></td>            <td style="width:40pt;padding-right:10pt"><a href="settings.view?" target="main"><img src="<c:url value="/icons/settings.png"/>" title="${settings}" alt="${settings}"/><br/>${settings}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="status.view?" target="main"><img src="<c:url value="/icons/status.png"/>" title="${status}" alt="${status}"/><br/>${status}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="more.view?" target="main"><img src="<c:url value="/icons/more.png"/>" title="${more}" alt="${more}"/><br/>${more}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="help.view?" target="main"><img src="<c:url value="/icons/help.png"/>" title="${help}" alt="${help}"/><br/>${help}</a></td>

            <td style="padding-left:15pt">
                <table><tr>
                    <form method="post" action="search.view" target="main" name="searchForm">
                        <td><input type="text" name="query" id="query" size="14" value="${search}" onclick="javascript:document.searchForm.query.select();"/></td>

                        <td><a href="javascript:document.searchForm.submit()"><img src="<c:url value="/icons/search_small.png"/>" alt="${search}" title="${search}"/></a></td>
                        <input type="hidden" name="includeTitle" value="on"/>
                        <input type="hidden" name="includeArtistAndAlbum" value="on"/>
                    </form>
                </tr></table>
            </td>

            <td style="padding-left:15pt;text-align:center;">
                <p class="detail">
                    <a href="j_acegi_logout" target="_top"><fmt:message key="top.logout"><fmt:param value="${model.username}"/></fmt:message></a>
                </p>
            </td>

            <c:if test="${model.newVersionAvailable}">
                <td style="padding-left:15pt">
                    <p class="warning">
                        <fmt:message key="top.upgrade"><fmt:param value="${model.latestVersion}"/></fmt:message>
                    </p>
                </td>
            </c:if>
        </tr></table>
    </td>

</tr></table>

</body></html>