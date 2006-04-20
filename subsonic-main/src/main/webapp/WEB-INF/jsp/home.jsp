<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h1>${model.welcomeMessage}</h1>
<h2>
    <a href="home.view?listSize=${model.listSize}&listType=random"><fmt:message key="home.random.title"/></a> |
    <a href="home.view?listSize=${model.listSize}&listType=newest"><fmt:message key="home.newest.title"/></a> |
    <a href="home.view?listSize=${model.listSize}&listType=highest"><fmt:message key="home.highest.title"/></a> |
    <a href="home.view?listSize=${model.listSize}&listType=frequent"><fmt:message key="home.frequent.title"/></a> |
    <a href="home.view?listSize=${model.listSize}&listType=recent"><fmt:message key="home.recent.title"/></a>
</h2>

<c:if test="${model.isIndexBeingCreated}">
    <p style="color:red"><fmt:message key="home.scan"/></p>
</c:if>

<div style="color:dimgray"><b><fmt:message key="home.${model.listType}.text"/></b></div>

<table>
    <c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
        <c:if test="${loopStatus.count % 5 == 1}">
            <tr>
        </c:if>

        <sub:url value="/coverart" var="coverArtUrl">
            <sub:param name="size" value="110"/>
            <sub:param name="path" value="${album.coverArtPath}"/>
        </sub:url>
        <sub:url value="/main.view" var="mainUrl">
            <sub:param name="path" value="${album.path}"/>
        </sub:url>

        <td style="vertical-align:top">
            <table>
                <tr><td>
                    <a href="${mainUrl}"><img height="110" width="110" hspace="2" vspace="2" src="${coverArtUrl}"/></a>
                </td></tr>

                <tr><td>
                    <div style="color:dimgray;font-size:8pt">
                        <c:if test="${not empty album.playCount}">
                            <fmt:message key="home.playcount"><fmt:param value="${album.playCount}"/></fmt:message>
                        </c:if>
                        <c:if test="${not empty album.lastPlayed}">
                            <fmt:formatDate value="${album.lastPlayed}" dateStyle="short" var="lastPlayedDate"/>
                            <fmt:message key="home.lastplayed"><fmt:param value="${lastPlayedDate}"/></fmt:message>
                        </c:if>
                        <c:if test="${not empty album.lastModified}">
                            <fmt:formatDate value="${album.lastModified}" dateStyle="short" var="lastModifiedDate"/>
                            <fmt:message key="home.lastmodified"><fmt:param value="${lastModifiedDate}"/></fmt:message>
                        </c:if>
                        <c:if test="${not empty album.rating}">
                            <img src="<c:url value="/icons/rating${album.rating}.gif"/>"/>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${empty album.artist and empty album.albumTitle}">
                            <em><fmt:message key="common.unknown"/></em>
                        </c:when>
                        <c:otherwise>
                            <em><str:truncateNicely lower="17" upper="17">${album.artist}</str:truncateNicely></em><br/>
                            <str:truncateNicely lower="17" upper="17">${album.albumTitle}</str:truncateNicely>
                        </c:otherwise>
                    </c:choose>

                </td></tr>
            </table>
        </td>
        <c:if test="${loopStatus.count % 5 == 0}">
            </tr>
        </c:if>
    </c:forEach>
</table>

<table>
    <tr>
        <td style="padding-right:7pt">
            <select name="listSize" onchange="location='home.view?listType=${model.listType}&listOffset=${model.listOffset}&listSize=' + options[selectedIndex].value;">
                <c:forTokens items="5 10 15 20 30 40 50" delims=" " var="size">
                    <option ${size eq model.listSize ? "selected" : ""} value="${size}"><fmt:message key="home.listsize"><fmt:param value="${size}"/></fmt:message></option>
                </c:forTokens>
            </select>
        </td>

        <c:choose>
            <c:when test="${model.listType eq 'random'}">
                <td style="padding-right:7pt"><a href="home.view?listType=random&listSize=${model.listSize}">[<fmt:message key="common.more"/>]</a></td>
            </c:when>

            <c:otherwise>
                <c:url value="home.view" var="previousUrl">
                    <c:param name="listType" value="${model.listType}"/>
                    <c:param name="listOffset" value="${model.listOffset - model.listSize}"/>
                    <c:param name="listSize" value="${model.listSize}"/>
                </c:url>
                <c:url value="home.view" var="nextUrl">
                    <c:param name="listType" value="${model.listType}"/>
                    <c:param name="listOffset" value="${model.listOffset + model.listSize}"/>
                    <c:param name="listSize" value="${model.listSize}"/>
                </c:url>

                <td style="padding-right:7pt"><fmt:message key="home.albums"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
                <td style="padding-right:7pt"><a href="${previousUrl}">[<fmt:message key="common.previous"/>]</a></td>
                <td><a href="${nextUrl}">[<fmt:message key="common.next"/>]</a></td>
            </c:otherwise>
        </c:choose>
    </tr>
</table>

</body></html>
