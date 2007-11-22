<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>

<body class="bgcolor2">
<a name="top"/>

<div style="padding-bottom:0.5em">
    <c:forEach items="${model.indexes}" var="index">
        <a href="#${index.index}">${index.index}</a>
    </c:forEach>
</div>

<c:if test="${model.statistics != null}">
    <div class="detail">
        <fmt:message key="left.statistics">
            <fmt:param value="${model.statistics.artistCount}"/>
            <fmt:param value="${model.statistics.albumCount}"/>
            <fmt:param value="${model.statistics.songCount}"/>
            <fmt:param value="${model.bytes}"/>
            <fmt:param value="${model.hours}"/>
        </fmt:message>
    </div>
</c:if>

<c:if test="${fn:length(model.musicFolders) > 1}">
    <select name="musicFolderId" style="width:100%" onchange="location='left.view?musicFolderId=' + options[selectedIndex].value;" >
        <option value="-1"><fmt:message key="left.allfolders"/></option>
        <c:forEach items="${model.musicFolders}" var="musicFolder">
            <option ${model.selectedMusicFolder.id == musicFolder.id ? "selected" : ""} value="${musicFolder.id}">${musicFolder.name}</option>
        </c:forEach>
    </select>
</c:if>

<c:if test="${not empty model.shortcuts}">
    <h2><fmt:message key="left.shortcut"/></h2>
    <c:forEach items="${model.shortcuts}" var="shortcut">
        <p class="dense">
            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${shortcut.path}"/>
                <c:param name="downloadEnabled" value="${model.downloadEnabled}"/>
            </c:import>
            <sub:url value="main.view" var="mainUrl">
                <sub:param name="path" value="${shortcut.path}"/>
            </sub:url>
            <a target="main" href="${mainUrl}">${shortcut.name}</a>
        </p>
    </c:forEach>
</c:if>

<c:if test="${not empty model.radios}">
    <h2><fmt:message key="left.radio"/></h2>
    <c:forEach items="${model.radios}" var="radio">
        <p class="dense">
            <a target="hidden" href="${radio.streamUrl}">
                <img width="13" height="13" src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>
            <c:choose>
                <c:when test="${empty radio.homepageUrl}">
                    ${radio.name}
                </c:when>
                <c:otherwise>
                    <a target="main" href="${radio.homepageUrl}">${radio.name}</a>
                </c:otherwise>
            </c:choose>
        </p>
    </c:forEach>
</c:if>

<c:forEach items="${model.indexedChildren}" var="entry">
    <a name="${entry.key.index}"/>
    <h2><a href="#top">${entry.key.index}</a></h2>

    <c:forEach items="${entry.value}" var="child">
        <p class="dense">

            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${child.path}"/>
                <c:param name="downloadEnabled" value="${model.downloadEnabled}"/>
            </c:import>

            <span title="${child.name}">
                <c:choose>
                    <c:when test="${child.directory}">
                        <sub:url value="main.view" var="mainUrl">
                            <sub:param name="path" value="${child.path}"/>
                        </sub:url>
                        <a target="main" href="${mainUrl}"><str:truncateNicely upper="${model.captionCutoff}">${child.name}</str:truncateNicely></a>
                    </c:when>
                    <c:otherwise>
                        <str:truncateNicely upper="${model.captionCutoff}">${child.name}</str:truncateNicely>
                    </c:otherwise>
                </c:choose>
            </span>
        </p>
    </c:forEach>
</c:forEach>

</body></html>