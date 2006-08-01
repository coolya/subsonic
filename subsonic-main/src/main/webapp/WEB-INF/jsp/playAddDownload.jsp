<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  path: Path to album.
  downloadEnabled: Whether the current user is allowed to download.
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
<a target="playlist" href="${playUrl}">
    <img width="13" height="13" src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>

<c:if test="${param.asTable}"></td><td></c:if>
<a target="playlist" href="${addUrl}">
    <img width="13" height="13" src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"/></a>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.downloadEnabled}">
    <c:if test="${param.asTable}"><td></c:if>
    <a href="${downloadUrl}">
        <img width="13" height="13" src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"/></a>
    <c:if test="${param.asTable}"></td></c:if>
</c:if>
