<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe">

<c:choose>
    <c:when test="${empty model.buildDate}">
        <fmt:message key="common.unknown" var="buildDateString"/>
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${model.buildDate}" dateStyle="long" var="buildDateString"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty model.localVersion}">
        <fmt:message key="common.unknown" var="versionString"/>
    </c:when>
    <c:otherwise>
        <c:set var="versionString" value="${model.localVersion} (build ${model.buildNumber})"/>
    </c:otherwise>
</c:choose>

<h1>
    <img src="<spring:theme code="helpImage"/>" alt=""/>
    <fmt:message key="help.title"/>
</h1>

<c:if test="${model.newVersionAvailable}">
    <p class="warning"><fmt:message key="help.upgrade"><fmt:param value="${model.latestVersion}"/></fmt:message></p>
</c:if>

<table width="75%" class="ruleTable indent">
    <tr><td class="ruleTableHeader"><fmt:message key="help.version.title"/></td><td class="ruleTableCell">${versionString}</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.builddate.title"/></td><td class="ruleTableCell">${buildDateString}</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.server.title"/></td><td class="ruleTableCell">${model.serverInfo} (<sub:formatBytes bytes="${model.usedMemory}"/> / <sub:formatBytes bytes="${model.totalMemory}"/>)</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.license.title"/></td><td class="ruleTableCell"><fmt:message key="help.license.text"/></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.homepage.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://subsonic.sourceforge.net/">http://subsonic.sourceforge.net/</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.faq.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://subsonic.sourceforge.net/faq.html">http://subsonic.sourceforge.net/faq.html</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.forum.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://subsonic.sourceforge.net/forum.html">http://subsonic.sourceforge.net/forum.html</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.shop.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://www.cafepress.com/subsonic_shop">http://www.cafepress.com/subsonic_shop</a></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.contact.title"/></td><td class="ruleTableCell"><fmt:message key="help.contact.text"/></td></tr>
</table>

<p/>

<table width="75%"><tr>
    <td><a href="<c:url value="/donate.view"/>"><img src="<spring:theme code="paypalImage"/>" alt=""/></a></td>
    <td><fmt:message key="help.donate"/></td>
</tr></table>

<h2><img src="<spring:theme code="logImage"/>" alt=""/>&nbsp;<fmt:message key="help.log"/></h2>

<table cellpadding="2" class="log indent">
    <c:forEach items="${model.logEntries}" var="entry">
        <tr>
            <td>[<fmt:formatDate value="${entry.date}" dateStyle="short" timeStyle="long" type="both"></fmt:formatDate>]</td>
            <td>${entry.level}</td><td>${entry.category}</td><td>${entry.message}</td>
        </tr>
    </c:forEach>
</table>

<p><fmt:message key="help.logfile"><fmt:param value="${model.logFile}"/></fmt:message> </p>

<div class="forward"><a href="help.view?"><fmt:message key="common.refresh"/></a></div>

</body></html>