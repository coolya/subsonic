<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/smooth-scroll.js"/>"></script>
</head>

<body class="bgcolor2 leftframe">
<a name="top"></a>

<div style="padding-bottom:0.5em">
    <c:forEach items="${model.indexes}" var="index">
        <a href="#${index.index}" accesskey="${index.index}">${index.index}</a>
    </c:forEach>
</div>

<c:if test="${model.statistics.songCount gt 0}">
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
    <div style="padding-top:1em">
        <select name="musicFolderId" style="width:100%" onchange="location='left.view?musicFolderId=' + options[selectedIndex].value;" >
            <option value="-1"><fmt:message key="left.allfolders"/></option>
            <c:forEach items="${model.musicFolders}" var="musicFolder">
                <option ${model.selectedMusicFolder.id == musicFolder.id ? "selected" : ""} value="${musicFolder.id}">${musicFolder.name}</option>
            </c:forEach>
        </select>
    </div>
</c:if>

<c:if test="${not empty model.shortcuts}">
    <h2 class="bgcolor1"><fmt:message key="left.shortcut"/></h2>
    <c:forEach items="${model.shortcuts}" var="shortcut">
        <p class="dense" style="padding-left:0.5em">
            <sub:url value="main.view" var="mainUrl">
                <sub:param name="path" value="${shortcut.path}"/>
            </sub:url>
            <a target="main" href="${mainUrl}">${shortcut.name}</a>
        </p>
    </c:forEach>
</c:if>

<c:if test="${not empty model.radios}">
    <h2 class="bgcolor1"><fmt:message key="left.radio"/></h2>
    <c:forEach items="${model.radios}" var="radio">
        <p class="dense">
            <a target="hidden" href="${radio.streamUrl}">
                <img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
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

<c:forEach items="${model.indexedArtists}" var="entry">
    <table class="bgcolor1" style="width:100%;padding:0;margin:1em 0 0 0;border:0">
        <tr style="padding:0;margin:0;border:0">
            <th style="text-align:left;padding:0;margin:0;border:0"><a name="${entry.key.index}"></a>
                <h2 style="padding:0;margin:0;border:0">${entry.key.index}</h2>
            </th>
            <th style="text-align:right;">
                <a href="#top"><img src="<spring:theme code="upImage"/>" alt=""></a>
            </th>
        </tr>
    </table>

    <c:forEach items="${entry.value}" var="artist">
        <p class="dense" style="padding-left:0.5em">
            <span title="${artist.name}">
                <sub:url value="main.view" var="mainUrl">
                    <c:forEach items="${artist.musicFiles}" var="musicFile">
                        <sub:param name="path" value="${musicFile.path}"/>
                    </c:forEach>
                </sub:url>
                <a target="main" href="${mainUrl}"><str:truncateNicely upper="${model.captionCutoff}">${artist.name}</str:truncateNicely></a>
            </span>
        </p>
    </c:forEach>
</c:forEach>

<div style="padding-top:1em"></div>

<c:forEach items="${model.singleSongs}" var="song">
    <p class="dense" style="padding-left:0.5em">
        <span title="${song.title}">
            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${song.path}"/>
                <c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode}"/>
                <c:param name="addEnabled" value="${model.user.streamRole}"/>
                <c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode}"/>
                <c:param name="video" value="${song.video}"/>
            </c:import>
            <str:truncateNicely upper="${model.captionCutoff}">${song.title}</str:truncateNicely>
        </span>
    </p>
</c:forEach>

<div style="height:5em"></div>

<div class="bgcolor2" style="opacity: 1.0; clear: both; position: fixed; bottom: 0; right: 0; left: 0;
      padding: 0.25em 0.75em 0.25em 0.75em; border-top:1px solid black; max-width: 850px;">
    <c:forEach items="${model.indexes}" var="index">
        <a href="#${index.index}">${index.index}</a>
    </c:forEach>
</div>


</body></html>