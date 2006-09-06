<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="appearance"/>
    <c:param name="restricted" value="${not command.user.adminRole}"/>
</c:import>

<h2><fmt:message key="appearancesettings.title"><fmt:param>${command.user.username}</fmt:param></fmt:message></h2>

<fmt:message key="common.help" var="help"/>
<fmt:message key="common.default" var="default"/>
<c:url value="/icons/help_small.png" var="helpUrl"/>

<form:form method="post" action="appearanceSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">

        <tr>
            <td><fmt:message key="appearancesettings.language"/></td>
            <td>
                <form:select path="localeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${default}"/>
                    <c:forEach items="${command.locales}" var="locale" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${locale}"/>
                    </c:forEach>
                </form:select>
                <a href="helpPopup.view?topic=language" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="appearancesettings.theme"/></td>
            <td>
                <form:select path="themeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${default}"/>
                    <c:forEach items="${command.themes}" var="theme" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${theme.name}"/>
                    </c:forEach>
                </form:select>
                <a href="helpPopup.view?topic=theme" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>
    </table>

    <table class="indent">
        <tr>
            <th style="padding:0 0.5em 0.5em 0;text-align:left;"><fmt:message key="appearancesettings.display"/></th>
            <th style="padding:0 0.5em 0.5em 0.5em;text-align:center;"><fmt:message key="appearancesettings.browse"/></th>
            <th style="padding:0 0 0.5em 0.5em;text-align:center;"><fmt:message key="appearancesettings.playlist"/></th>
            <th style="padding:0 0 0.5em 0.5em">
                <a href="helpPopup.view?topic=visibility" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </th>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.tracknumber"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.trackNumberVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.trackNumberVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.artist"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.artistVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.artistVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.album"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.albumVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.albumVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.genre"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.genreVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.genreVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.year"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.yearVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.yearVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.bitrate"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.bitRateVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.bitRateVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.duration"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.durationVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.durationVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.format"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.formatVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.formatVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.filesize"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.fileSizeVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.fileSizeVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="appearancesettings.captioncutoff"/></td>
            <td style="text-align:center"><form:input path="mainVisibility.captionCutoff" size="3"/></td>
            <td style="text-align:center"><form:input path="playlistVisibility.captionCutoff" size="3"/></td>
        </tr>
    </table>

    <table class="indent">
        <tr>
            <td><form:checkbox path="finalVersionNotificationEnabled" id="final" cssClass="checkbox"/></td>
            <td><label for="final"><fmt:message key="appearancesettings.finalversionnotification"/></label></td>
        </tr>
        <tr>
            <td><form:checkbox path="betaVersionNotificationEnabled" id="beta" cssClass="checkbox"/></td>
            <td><label for="beta"><fmt:message key="appearancesettings.betaversionnotification"/></label></td>
        </tr>
    </table>

    <input type="submit" value="<fmt:message key="common.save"/>"/>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.upper.location.href="top.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</body></html>