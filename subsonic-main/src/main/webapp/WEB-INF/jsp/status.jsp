<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
    <meta http-equiv="REFRESH" content="20;URL=status.view">
</head><body>

<h1>
    <img src="<c:url value="/icons/status.png"/>" alt=""/>
    <fmt:message key="status.title"/>
</h1>

<table width="100%" class="ruleTable indent">
    <tr>
        <th class="ruleTableHeader"><fmt:message key="status.type"/></th>
        <th class="ruleTableHeader"><fmt:message key="status.player"/></th>
        <th class="ruleTableHeader"><fmt:message key="status.user"/></th>
        <th class="ruleTableHeader"><fmt:message key="status.current"/></th>
        <th class="ruleTableHeader"><fmt:message key="status.transmitted"/></th>
        <th class="ruleTableHeader"><fmt:message key="status.bitrate"/></th>
    </tr>

    <c:forEach items="${model.transferStatuses}" var="status">

        <c:choose>
            <c:when test="${empty status.playerType}">
                <fmt:message key="common.unknown" var="type"/>
            </c:when>
            <c:otherwise>
                <c:set var="type" value="(${status.playerType})"/>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${status.stream}">
                <fmt:message key="status.stream" var="transferType"/>
            </c:when>
            <c:when test="${status.download}">
                <fmt:message key="status.download" var="transferType"/>
            </c:when>
            <c:when test="${status.upload}">
                <fmt:message key="status.upload" var="transferType"/>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${empty status.username}">
                <fmt:message key="common.unknown" var="user"/>
            </c:when>
            <c:otherwise>
                <c:set var="user" value="${status.username}"/>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${empty status.path}">
                <fmt:message key="common.unknown" var="current"/>
            </c:when>
            <c:otherwise>
                <c:set var="current" value="${status.path}"/>
            </c:otherwise>
        </c:choose>

        <c:url value="/statusChart" var="chartUrl">
            <c:if test="${status.stream}">
                <c:param name="type" value="stream"/>
            </c:if>
            <c:if test="${status.download}">
                <c:param name="type" value="download"/>
            </c:if>
            <c:if test="${status.upload}">
                <c:param name="type" value="upload"/>
            </c:if>
            <c:param name="index" value="${status.index}"/>
        </c:url>

        <tr>
            <td class="ruleTableCell">${transferType}</td>
            <td class="ruleTableCell">${status.player}<br/>${type}</td>
            <td class="ruleTableCell">${user}</td>
            <td class="ruleTableCell">${current}</td>
            <td class="ruleTableCell">${status.bytes}</td>
            <td class="ruleTableCell" width="${model.chartWidth}"><img width="${model.chartWidth}" height="${model.chartHeight}" src="${chartUrl}" alt=""/></td>
        </tr>
    </c:forEach>
</table>

<p><a href="status.view?">[<fmt:message key="common.refresh"/>]</a></p>

</body></html>