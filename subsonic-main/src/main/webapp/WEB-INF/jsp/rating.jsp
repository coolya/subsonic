<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<%--
Creates HTML for displaying the rating stars.
PARAMETERS
  path: Album path. May be null if readonly.
  readonly: Whether rating can be changed.
  rating: The rating (from 0 to 5).
--%>

<c:forEach var="i" begin="1" end="5">

    <sub:url value="setMusicFileInfo.view" var="ratingUrl">
        <sub:param name="path" value="${param.path}"/>
        <sub:param name="action" value="rating"/>
        <sub:param name="rating" value="${i}"/>
    </sub:url>

    <c:choose>
        <c:when test="${param.rating lt i}">
            <spring:theme code="ratingOffImage" var="imageUrl"/>
        </c:when>
        <c:otherwise>
            <spring:theme code="ratingOnImage" var="imageUrl"/>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${param.readonly}">
            <img src="${imageUrl}" alt=""/>
        </c:when>
        <c:otherwise>
            <a href="${ratingUrl}"><img src="${imageUrl}" style="margin-right:-3px" alt="" title="<fmt:message key="rating.rating"/> ${i}"/></a>
        </c:otherwise>
    </c:choose>

</c:forEach>

<sub:url value="setMusicFileInfo.view" var="clearRatingUrl">
    <sub:param name="path" value="${param.path}"/>
    <sub:param name="action" value="rating"/>
    <sub:param name="rating" value="0"/>
</sub:url>

<c:if test="${not param.readonly}">
    <a href="${clearRatingUrl}"><img src="<c:url value="/icons/clearRating.png"/>" alt="" title="<fmt:message key="rating.clearrating"/>" style="margin-left:5px; margin-right:5px"/></a>
</c:if>
