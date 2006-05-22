<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>

<body class="bgcolor2">

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
            <option ${model.selectedMusicFolder == musicFolder ? "selected" : ""} value="${musicFolder.id}">${musicFolder.name}</option>
        </c:forEach>
    </select>
</c:if>

<c:if test="${not empty model.shortcuts}">
    <h2><fmt:message key="left.shortcut"/></h2>
    <c:forEach items="${model.shortcuts}" var="shortcut">
        <c:url value="playRadio.view" var="playRadioUrl">
            <c:param name="id" value="${radio.id}"/>
        </c:url>
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
        <c:url value="playRadio.view" var="playRadioUrl">
            <c:param name="id" value="${radio.id}"/>
        </c:url>
        <p class="dense">
            <a target="hidden" href="${playRadioUrl}">
                <img width="13" height="13" src="<c:url value="/icons/play.gif"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"/></a>
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
    <h2>${entry.key.index}</h2>

    <c:forEach items="${entry.value}" var="child">
        <p class="dense">

            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${child.path}"/>
                <c:param name="downloadEnabled" value="${model.downloadEnabled}"/>
            </c:import>

            <c:choose>
                <c:when test="${child.directory}">
                    <sub:url value="main.view" var="mainUrl">
                        <sub:param name="path" value="${child.path}"/>
                    </sub:url>
                    <a target="main" href="${mainUrl}">${child.name}</a>
                </c:when>
                <c:otherwise>
                    <c:out value="${child.name}"></c:out>
                </c:otherwise>
            </c:choose>
        </p>
    </c:forEach>
</c:forEach>

</body></html>