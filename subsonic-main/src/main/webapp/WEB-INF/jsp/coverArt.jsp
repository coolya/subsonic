<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<!--
PARAMETERS
  coverArtSize: Height and width of cover art.
  coverArtPath: Path to cover art, or nil if generic cover art image should be displayed.
  albumPath: Path to album.
  showLink: Whether to make the cover art image link to the album page.
  showZoom: Whether to display a link for zooming the cover art.
  showChange: Whether to display a link for changing the cover art.
-->

<div>
    <sub:url value="main.view" var="mainUrl">
        <sub:param name="path" value="${param.albumPath}"/>
    </sub:url>

    <sub:url value="coverart" var="coverArtUrl">
        <sub:param name="size" value="${param.coverArtSize}"/>
        <c:if test="${not empty param.coverArtPath}">
            <sub:param name="path" value="${param.coverArtPath}"/>
        </c:if>
    </sub:url>

    <div class="outerpair1">
        <div class="outerpair2">
            <div class="shadowbox">
                <div class="innerbox">
                    <c:if test="${param.showLink}"><a href="${mainUrl}"></c:if>
                        <img src="${coverArtUrl}" alt="" height="${param.coverArtSize}" width="${param.coverArtSize}"/>
                        <c:if test="${param.showLink}"></a></c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<div style="text-align:right; padding-right: 8px">
    <c:if test="${param.showChange}">
        <sub:url value="/changeCoverArt.view" var="changeCoverArtUrl">
            <sub:param name="path" value="${param.albumPath}"/>
        </sub:url>
        <a class="detail" href="${changeCoverArtUrl}"><fmt:message key="coverart.change"/></a>
    </c:if>

    <c:if test="${param.showZoom and param.showChange}">
        |
    </c:if>

    <c:if test="${param.showZoom}">
        <sub:url value="/zoomCoverArt.view" var="zoomCoverArtUrl">
            <sub:param name="path" value="${param.coverArtPath}"/>
        </sub:url>
        <a class="detail" href="${zoomCoverArtUrl}" onclick="return popup(this, 'Cover')"><fmt:message key="coverart.zoom"/></a>
    </c:if>
</div>