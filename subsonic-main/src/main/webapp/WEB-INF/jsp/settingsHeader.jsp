<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<h2 style="white-space:nowrap">

    <c:choose>
        <c:when test="${param.restricted}">
${param.cat eq 'password' ? "" : "<a href='passwordSettings.view?'>"}<fmt:message key="settingsheader.password"/>${param.cat eq 'password' ? "" : "</a>"} |
${param.cat eq 'player' ? "" : "<a href='playerSettings.view?'>"}<fmt:message key="settingsheader.player"/>${param.cat eq 'player' ? "" : "</a>"} 
        </c:when>

        <c:otherwise>
${param.cat eq 'general' ? "" : "<a href='generalSettings.view?'>"}<fmt:message key="settingsheader.general"/>${param.cat eq 'general' ? "" : "</a>"} |
${param.cat eq 'musicFolder' ? "" : "<a href='musicFolderSettings.view?'>"}<fmt:message key="settingsheader.musicfolder"/>${param.cat eq 'musicFolder' ? "" : "</a>"} |
${param.cat eq 'user' ? "" : "<a href='userSettings.view?'>"}<fmt:message key="settingsheader.user"/>${param.cat eq 'user' ? "" : "</a>"} |
${param.cat eq 'player' ? "" : "<a href='playerSettings.view?'>"}<fmt:message key="settingsheader.player"/>${param.cat eq 'player' ? "" : "</a>"} |
${param.cat eq 'internetRadio' ? "" : "<a href='internetRadioSettings.view?'>"}<fmt:message key="settingsheader.radio"/>${param.cat eq 'internetRadio' ? "" : "</a>"} |
${param.cat eq 'search' ? "" : "<a href='searchSettings.view?'>"}<fmt:message key="settingsheader.search"/>${param.cat eq 'search' ? "" : "</a>"}
        </c:otherwise>
    </c:choose>

</h2>

<p/>
