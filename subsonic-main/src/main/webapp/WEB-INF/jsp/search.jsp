<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h1><fmt:message key="search.title"/></h1>

<form:form commandName="command" method="post" action="search.view">
    <p>
        <form:input path="query" size="55"/>
        <input type="submit" value="<fmt:message key="search.search"/>"/>
    </p>
    <form:checkbox path="titleIncluded" id="titleIncluded"/>
    <label for="titleIncluded"><fmt:message key="search.include.title"/></label> |

    <form:checkbox path="artistAndAlbumIncluded" id="artistAndAlbumIncluded"/>
    <label for="artistAndAlbumIncluded"><fmt:message key="search.include.artistandalbum"/></label> |

    <label for="time"><fmt:message key="search.newer"/></label>
    <form:select path="time" cssStyle="vertical-align:middle" id="time">
        <fmt:message key="search.select" var="select"/>
        <fmt:message key="search.day" var="day"/>
        <fmt:message key="search.week" var="week"/>
        <fmt:message key="search.weeks" var="weeks"/>
        <fmt:message key="search.month" var="month"/>
        <fmt:message key="search.months" var="months"/>
        <fmt:message key="search.year" var="year"/>

        <form:option value="0" label="${select}"/>
        <form:option value="1d" label="1 ${day}"/>
        <form:option value="1w" label="1 ${week}"/>
        <form:option value="2w" label="2 ${weeks}"/>
        <form:option value="1m" label="1 ${month}"/>
        <form:option value="3m" label="3 ${months}"/>
        <form:option value="6m" label="6 ${months}"/>
        <form:option value="1y" label="1 ${year}"/>
    </form:select>
</form:form>

<c:if test="${command.indexBeingCreated}">
    <p style="color:red"><fmt:message key="search.index"/></p>
</c:if>

<c:if test="${command.matches != null}">
    <p>
        <c:choose>
            <c:when test="${fn:length(command.matches) == command.maxHits}">
                <fmt:message key="search.hits.max"><fmt:param value="${command.maxHits}"/></fmt:message>
            </c:when>
            <c:when test="${fn:length(command.matches) == 0}">
                <fmt:message key="search.hits.none"/>
            </c:when>
            <c:when test="${fn:length(command.matches) == 1}">
                <fmt:message key="search.hits.one"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="search.hits.many"><fmt:param value="${fn:length(command.matches)}"/></fmt:message>
            </c:otherwise>
        </c:choose>
    </p>
</c:if>

<table>
    <c:forEach items="${command.matches}" var="match" varStatus="loopStatus">
        <c:url value="playlist.jsp" var="playUrl">
            <c:param name="play" value="${match.musicFile.path}"/>
        </c:url>
        <c:url value="playlist.jsp" var="addUrl">
            <c:param name="add" value="${match.musicFile.path}"/>
        </c:url>
        <c:url value="/download" var="downloadUrl">
            <c:param name="path" value="${match.musicFile.path}"/>
        </c:url>
        <c:url value="/main.jsp" var="mainUrl">
            <c:param name="path" value="${match.musicFile.parent.path}"/>
        </c:url>

        <tr>
            <td style="white-space:nowrap">
                <a target="playlist" href="${playUrl}">
                    <img width="13" height="13" src="<c:url value="/icons/play.gif"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>

                <a target="playlist" href="${addUrl}">
                    <img width="13" height="13" src="<c:url value="/icons/add.gif"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"/></a>

                <c:if test="${command.downloadEnabled}">
                    <a href="${downloadUrl}">
                        <img width="13" height="13" src="<c:url value="/icons/download.gif"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"/></a>
                </c:if>
            </td>

            <td style="padding-left:5;padding-right:5;${loopStatus.count % 2 == 1 ? "background-color:#DEE3E7" : ""}">${match.title}</td>
            <td style="padding-left:5;padding-right:5;${loopStatus.count % 2 == 1 ? "background-color:#DEE3E7" : ""}"><a target="main" href="${mainUrl}">${match.artistAlbumYear}</a></td>
        </tr>
    </c:forEach>
</table>

</body></html>