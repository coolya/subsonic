<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>

<wml>

    <%@ include file="head.jsp" %>

    <card id="main" title="Subsonic" newcontext="false">
        <p><small>

            <c:choose>
            <c:when test="${empty model.artists}">

                <b><a href="playlist.view">[<fmt:message key="wap.index.playlist"/>]</a></b><br/>
                <b><a href="search.view">[<fmt:message key="wap.index.search"/>]</a></b><br/>
                <b><a href="settings.view">[<fmt:message key="wap.index.settings"/>]</a></b><br/>
        </small></p>
        <p><small>
            <c:forEach items="${model.indexes}" var="index">
                <sub:url var="url" value="index.view">
                    <sub:param name="index" value="${index.index}"/>
                </sub:url>
                <a href="${url}">${index.index}</a>
            </c:forEach>
            </c:when>

            <c:otherwise>
                <c:forEach items="${model.artists}" var="artist">
                    <sub:url var="url" value="browse.view">
                        <sub:param name="path" value="${artist.path}"/>
                    </sub:url>
                    <a href="${url}">${artist.title}</a>
                </c:forEach>
            </c:otherwise>
            </c:choose>

            <c:if test="${model.noMusic}">
                <fmt:message key="wap.index.missing"/>
            </c:if>

        </small></p>
    </card>
</wml>

