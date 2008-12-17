<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  path: Path to album.
  playEnabled: Whether the current user is allowed to play songs (default true).
  addEnabled: Whether the current user is allowed to add songs to the playlist (default true).
  downloadEnabled: Whether the current user is allowed to download songs (default false).
  asTable: Whether to put the images in td tags.
--%>

<sub:url value="playlist.view" var="playUrl">
    <sub:param name="play" value="${param.path}"/>
</sub:url>
<sub:url value="playlist.view" var="addUrl">
    <sub:param name="add" value="${param.path}"/>
</sub:url>
<sub:url value="/download.view" var="downloadUrl">
    <sub:param name="path" value="${param.path}"/>
</sub:url>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${empty param.playEnabled or param.playEnabled}">
    <a href="javascript:noop()" onclick="top.playlist.onPlay('${param.path}')">
    <img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${empty param.addEnabled or param.addEnabled}">
<a target="playlist" href="${addUrl}">
    <img src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"/></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${param.downloadEnabled}">
    <a href="${downloadUrl}">
        <img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"/></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>
