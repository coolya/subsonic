<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<sub:url value="playlist.jsp" var="playUrl">
    <sub:param name="play" value="${param.path}"/>
</sub:url>
<sub:url value="playlist.jsp" var="addUrl">
    <sub:param name="add" value="${param.path}"/>
</sub:url>
<sub:url value="/download" var="downloadUrl">
    <sub:param name="path" value="${param.path}"/>
</sub:url>

<a target="playlist" href="${playUrl}">
    <img width="13" height="13" src="<c:url value="/icons/play.gif"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>
<a target="playlist" href="${addUrl}">
    <img width="13" height="13" src="<c:url value="/icons/add.gif"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"/></a>
<c:if test="${param.downloadEnabled}">
    <a href="${downloadUrl}">
        <img width="13" height="13" src="<c:url value="/icons/download.gif"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"/></a>
</c:if>
