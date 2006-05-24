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
            <img src="${imageUrl}" alt="<fmt:message key="rating.rating"/> ${param.rating}"/>
        </c:when>
        <c:otherwise>
            <a href="${ratingUrl}"><img src="${imageUrl}" alt="<fmt:message key="rating.rating"/> ${param.rating}"/></a>
        </c:otherwise>
    </c:choose>

</c:forEach>
