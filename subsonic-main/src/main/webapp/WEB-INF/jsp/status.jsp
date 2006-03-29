<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="/include.jsp" %>

<html><head>
    <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
    <meta http-equiv="REFRESH" content="20;URL=status.view">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="/subsonic/style.css" rel="stylesheet">
</head><body>

<h1><fmt:message key="status.title"/></h1>
<table border="1" cellpadding="10" rules="all">
    <tr>
        <th><fmt:message key="status.player"/></th>
        <th><fmt:message key="status.user"/></th>
        <th><fmt:message key="status.current"/></th>
        <th><fmt:message key="status.transmitted"/></th>
        <th><fmt:message key="status.bitrate"/></th>
    </tr>

    <c:set var="unknown"><fmt:message key="common.unknown"/></c:set>
    <c:forEach items="${model.streamStatuses}" var="status" varStatus="loopStatus">

        <c:choose>
            <c:when test="${empty status.player.type}">
                <fmt:message key="common.unknown" var="type"/>
            </c:when>
            <c:otherwise>
                <c:set var="type" value="${status.player.type})"/>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${empty status.player.username}">
                <fmt:message key="common.unknown" var="user"/>
            </c:when>
            <c:otherwise>
                <c:set var="user" value="${status.player.username}"/>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${empty status.file}">
                <fmt:message key="common.unknown" var="current"/>
            </c:when>
            <c:otherwise>
                <c:set var="current" value="${status.file.path}<br/>(${status.file.bitRate} Kbps)"/>
            </c:otherwise>
        </c:choose>

        <c:url value="statusChart" var="chartUrl">
            <c:param name="type" value="stream"/>
            <c:param name="index" value="${loopStatus.count - 1}"/>
        </c:url>

        <tr>
            <td>${status.player}<br/>${type}</td>
            <td>${user}</td>
            <td>${current}</td>
            <td>${model.bytesStreamed[loopStatus.count - 1]}</td>
            <td><img width="${model.chartWidth}" height="${model.chartHeight}" src="${chartUrl}"/></td>
        </tr>
    </c:forEach>
</table>

<c:if test="${not empty model.downloadStatuses}">

    <h1><fmt:message key="status.download.title"/></h1>
    <table border="1" cellpadding="10" rules="all">
        <tr>
            <th><fmt:message key="status.player"/></th>
            <th><fmt:message key="status.user"/></th>
            <th><fmt:message key="status.current"/></th>
            <th><fmt:message key="status.transmitted"/></th>
            <th><fmt:message key="status.bitrate"/></th>
        </tr>

        <c:forEach items="${model.downloadStatuses}" var="status" varStatus="loopStatus">

            <c:choose>
                <c:when test="${empty status.player.type}">
                    <fmt:message key="common.unknown" var="type"/>
                </c:when>
                <c:otherwise>
                    <c:set var="type" value="${status.player.type})"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${empty status.player.username}">
                    <fmt:message key="common.unknown" var="user"/>
                </c:when>
                <c:otherwise>
                    <c:set var="user" value="${status.player.username}"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${empty status.file}">
                    <fmt:message key="common.unknown" var="current"/>
                </c:when>
                <c:otherwise>
                    <c:set var="current" value="${status.file.path}<br/>(${status.file.bitRate} Kbps)"/>
                </c:otherwise>
            </c:choose>

            <c:url value="statusChart" var="chartUrl">
                <c:param name="type" value="download"/>
                <c:param name="index" value="${loopStatus.count - 1}"/>
            </c:url>

            <tr>
                <td>${status.player}<br/>${type}</td>
                <td>${user}</td>
                <td>${current}</td>
                <td>${model.bytesStreamed[loopStatus.count - 1]}</td>
                <td><img width="${model.chartWidth}" height="${model.chartHeight}" src="${chartUrl}"/></td>
            </tr>
        </c:forEach>
    </table>

</c:if>

<p><a href="status.view" target="main">[<fmt:message key="common.refresh"/>]</a></p>

</body></html>