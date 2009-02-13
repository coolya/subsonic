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
    <td class="logo" style="padding-right:2em"><a href="help.view?" target="main"><img src="<spring:theme code="logoImage"/>" title="${help}" alt=""/></a></td>

    <c:if test="${not model.musicFoldersExist}">
        <td style="padding-right:2em">
            <p class="warning"><fmt:message key="top.missing"/></p>
        </td>
    </c:if>

    <td>
        <table><tr align="middle">
            <td style="width:4em;padding-right:1.5em"><a href="home.view?" target="main"><img src="<spring:theme code="homeImage"/>" title="${home}" alt="${home}"/><br/>${home}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="nowPlaying.view?" target="main"><img src="<spring:theme code="nowPlayingImage"/>" title="${nowPlaying}" alt="${nowPlaying}"/><br/>${nowPlaying}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="podcastReceiver.view?" target="main"><img src="<spring:theme code="podcastLargeImage"/>" title="${podcast}" alt="${podcast}"/><br/>${podcast}</a></td>
            <c:if test="${model.user.settingsRole}">
                <td style="width:40pt;padding-right:10pt"><a href="settings.view?" target="main"><img src="<spring:theme code="settingsImage"/>" title="${settings}" alt="${settings}"/><br/>${settings}</a></td>
            </c:if>
            <td style="width:4em;padding-right:1.5em"><a href="status.view?" target="main"><img src="<spring:theme code="statusImage"/>" title="${status}" alt="${status}"/><br/>${status}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="more.view?" target="main"><img src="<spring:theme code="moreImage"/>" title="${more}" alt="${more}"/><br/>${more}</a></td>
            <td style="width:4em;padding-right:1.5em"><a href="help.view?" target="main"><img src="<spring:theme code="helpImage"/>" title="${help}" alt="${help}"/><br/>${help}</a></td>

            <td style="padding-left:2em">
                <table><tr>
                    <form method="post" action="search.view" target="main" name="searchForm">
                        <td><input type="text" name="title" id="title" size="14" value="${search}" onclick="javascript:document.searchForm.title.select();"/></td>
                        <td><a href="javascript:document.searchForm.submit()"><img src="<spring:theme code="searchImage"/>" alt="${search}" title="${search}"/></a></td>
                    </form>
                </tr></table>
            </td>

            <td style="padding-left:15pt;text-align:center;">
                <p class="detail">
                    <a href="j_acegi_logout" target="_top"><fmt:message key="top.logout"><fmt:param value="${model.user.username}"/></fmt:message></a>
                </p>
            </td>

            <c:if test="${model.newVersionAvailable}">
                <td style="padding-left:15pt">
                    <p class="warning">
                        <fmt:message key="top.upgrade"><fmt:param value="${model.brand}"/><fmt:param value="${model.latestVersion}"/></fmt:message>
                    </p>
                </td>
            </c:if>
        </tr></table>
    </td>

</tr></table>

</body></html>