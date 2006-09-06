<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
    <c:if test="${model.listType eq 'random'}">
        <meta http-equiv="refresh" content="20">
    </c:if>
</head><body>

<h1>
    <img src="<c:url value="/icons/home.png"/>" alt=""/>
    ${model.welcomeMessage}
</h1>

<h2>
    <c:forTokens items="random newest highest frequent recent users" delims=" " var="cat" varStatus="loopStatus">
        <c:if test="${loopStatus.count > 1}">&nbsp;|&nbsp;</c:if>
        <c:url var="url" value="home.view">
            <c:param name="listSize" value="${model.listSize}"/>
            <c:param name="listType" value="${cat}"/>
        </c:url>

        <c:choose>
            <c:when test="${model.listType eq cat}">
                <span class="headerSelected"><fmt:message key="home.${cat}.title"/></span>
            </c:when>
            <c:otherwise>
                <a href="${url}"><fmt:message key="home.${cat}.title"/></a>
            </c:otherwise>
        </c:choose>

    </c:forTokens>
</h2>

<c:if test="${model.isIndexBeingCreated}">
    <p class="warning"><fmt:message key="home.scan"/></p>
</c:if>

<h2><fmt:message key="home.${model.listType}.text"/></h2>

<c:choose>
<c:when test="${model.listType eq 'users'}">
    <table>
        <tr>
            <th><fmt:message key="home.chart.total"/></th>
            <th><fmt:message key="home.chart.stream"/></th>
        </tr>
        <tr>
            <td><img src="<c:url value="/userChart.view"><c:param name="type" value="total"/></c:url>" alt=""/></td>
            <td><img src="<c:url value="/userChart.view"><c:param name="type" value="stream"/></c:url>" alt=""/></td>
        </tr>
        <tr>
            <th><fmt:message key="home.chart.download"/></th>
            <th><fmt:message key="home.chart.upload"/></th>
        </tr>
        <tr>
            <td><img src="<c:url value="/userChart.view"><c:param name="type" value="download"/></c:url>" alt=""/></td>
            <td><img src="<c:url value="/userChart.view"><c:param name="type" value="upload"/></c:url>" alt=""/></td>
        </tr>
</table>

</c:when>
<c:otherwise>

    <table>
        <c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
            <c:if test="${loopStatus.count % 5 == 1}">
                <tr>
            </c:if>

            <td style="vertical-align:top">
                <table>
                    <tr><td>
                        <c:import url="coverArt.jsp">
                            <c:param name="albumPath" value="${album.path}"/>
                            <c:param name="albumName" value="${album.albumTitle}"/>
                            <c:param name="coverArtSize" value="110"/>
                            <c:param name="coverArtPath" value="${album.coverArtPath}"/>
                            <c:param name="showLink" value="true"/>
                            <c:param name="showZoom" value="false"/>
                            <c:param name="showChange" value="false"/>
                        </c:import>

                        <div class="detail">
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
                                <c:import url="rating.jsp">
                                    <c:param name="readonly" value="true"/>
                                    <c:param name="rating" value="${album.rating}"/>
                                </c:import>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${empty album.artist and empty album.albumTitle}">
                                <div class="detail"><fmt:message key="common.unknown"/></div>
                            </c:when>
                            <c:otherwise>
                                <div class="detail"><em><str:truncateNicely lower="15" upper="15">${album.artist}</str:truncateNicely></em></div>
                                <div class="detail"><str:truncateNicely lower="15" upper="15">${album.albumTitle}</str:truncateNicely></div>
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
                    <td><div class="forward"><a href="home.view?listType=random&listSize=${model.listSize}"><fmt:message key="common.more"/></a></div></td>
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
                    <td style="padding-right:7pt"><div class="back"><a href="${previousUrl}"><fmt:message key="common.previous"/></a></div></td>
                    <td><div class="forward"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></td>
                </c:otherwise>
            </c:choose>
        </tr>
    </table>
</c:otherwise>
</c:choose>
</body></html>
