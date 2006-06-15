<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<wml>

    <%@ include file="wapHead.jsp" %>

    <card id="main" title="Subsonic" newcontext="false">

        <p><small><b>

            <sub:url value="wapPlaylist.view" var="playUrl">
                <sub:param name="play" value="${model.parent.path}"/>
            </sub:url>
            <sub:url value="wapPlaylist.view" var="addUrl">
                <sub:param name="add" value="${model.parent.path}"/>
            </sub:url>

            <c:choose>
                <c:when test="${fn:length(model.children) eq 1 and model.children[0].file}">
                    <a href="${playUrl}">[<fmt:message key="wap.browse.playone"/>]</a><br/>
                    <a href="${addUrl}">[<fmt:message key="wap.browse.addone"/>]</a><br/>
                </c:when>
                <c:otherwise>
                    <a href="${playUrl}">[<fmt:message key="wap.browse.playall"/>]</a><br/>
                    <a href="${addUrl}">[<fmt:message key="wap.browse.addall"/>]</a><br/>
                </c:otherwise>
            </c:choose>

            <a href="wapIndex.view">[<fmt:message key="common.home"/>]</a><br/>
        </b></small></p>

        <p><small>

            <c:forEach items="${model.children}" var="child">
                <sub:url value="wapBrowse.view" var="browseUrl">
                    <sub:param name="path" value="${child.path}"/>
                </sub:url>
                <a href="${browseUrl}">${fn:escapeXml(child.title)}</a><br/>
            </c:forEach>

        </small></p>
    </card>
</wml>

