<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="/include.jsp" %>

<html><head>
    <!--[if gte IE 5.5000]>
     <script type="text/javascript" src="pngfix.js"></script>
     <![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="/subsonic/style.css" rel="stylesheet">
</head><body>

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

<h1><fmt:message key="help.title"/></h1>

<c:if test="${model.newVersionAvailable}">
    <p style="color:red"><fmt:message key="help.upgrade"><fmt:param value="${model.latestVersion}"/></fmt:message></p>
</c:if>

<table border="1" cellpadding="5" width="75%" rules="all">
    <tr><td><b><fmt:message key="help.version.title"/></b></td><td>${versionString}</td></tr>
    <tr><td><b><fmt:message key="help.builddate.title"/></b></td><td>${buildDateString}</td></tr>
    <tr><td><b><fmt:message key="help.license.title"/></b></td><td><fmt:message key="help.license.text"/></td></tr>
    <tr><td><b><fmt:message key="help.homepage.title"/></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/">http://subsonic.sourceforge.net/</a></td></tr>
    <tr><td><b><fmt:message key="help.faq.title"/></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/faq.html">http://subsonic.sourceforge.net/faq.html</a></td></tr>
    <tr><td><b><fmt:message key="help.forum.title"/></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/forum.html">http://subsonic.sourceforge.net/forum.html</a></td></tr>
    <tr><td><b><fmt:message key="help.contact.title"/></b></td><td><fmt:message key="help.contact.text"/></td></tr>
</table>
<p/>

<table width="75%"><tr>
    <td><a target="_blank" href="http://sourceforge.net/donate/index.php?group_id=126265"><img src="icons/donate.gif"/></a></td>
    <td><fmt:message key="help.donate"/></td>
</tr></table>

<h2><img src="icons/log.png" width="22" height="22"/>&nbsp;<fmt:message key="help.log"/></h2>

<table cellpadding="2" style="white-space:nowrap;">
    <c:forEach items="${model.logEntries}" var="entry">
        <tr>
            <td>[<fmt:formatDate value="${entry.date}" dateStyle="short" timeStyle="long" type="both"></fmt:formatDate>]</td>
            <td>${entry.level}</td><td>${entry.category}</td><td>${entry.message}</td>
        </tr>
    </c:forEach>
</table>

<p><a href='help.view?'>[<fmt:message key="common.refresh"/>]</a></p>

</body></html>