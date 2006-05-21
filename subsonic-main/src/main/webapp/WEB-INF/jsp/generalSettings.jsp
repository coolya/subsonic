<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="general"/>
</c:import>

<fmt:message key="common.help" var="help"/>
<c:url value="/icons/help_small.png" var="helpUrl"/>

<form:form method="post" action="generalSettings.view" commandName="command">

    <table style="white-space:nowrap">
        <tr>
            <td><fmt:message key="generalsettings.playlistfolder"/></td>
            <td>
                <form:input path="playlistFolder" size="70"/>
                <a href="helpPopup.view?topic=playlistFolder" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.musicmask"/></td>
            <td>
                <form:input path="musicMask" size="70"/>
                <a href="helpPopup.view?topic=musicMask" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.coverartmask"/></td>
            <td>
                <form:input path="coverArtMask" size="70"/>
                <a href="helpPopup.view?topic=coverArtMask" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="3">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="generalsettings.index"/></td>
            <td>
                <form:input path="index" size="70"/>
                <a href="helpPopup.view?topic=index" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.ignoredarticles"/></td>
            <td>
                <form:input path="ignoredArticles" size="70"/>
                <a href="helpPopup.view?topic=ignoredArticles" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.shortcuts"/></td>
            <td>
                <form:input path="shortcuts" size="70"/>
                <a href="helpPopup.view?topic=shortcuts" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.welcomemessage"/></td>
            <td>
                <form:input path="welcomeMessage" size="70"/>
                <a href="helpPopup.view?topic=welcomeMessage" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="3">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="generalsettings.language"/></td>
            <td>
                <form:select path="localeIndex">
                    <c:forEach items="${command.locales}" var="locale" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${locale}"/>
                    </c:forEach>
                </form:select>
                <a href="helpPopup.view?topic=language" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.theme"/></td>
            <td>
                <form:select path="themeIndex">
                    <c:forEach items="${command.themes}" var="theme" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${theme}"/>
                    </c:forEach>
                </form:select>
                <a href="helpPopup.view?topic=theme" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="3">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="generalsettings.coverartlimit"/></td>
            <td>
                <form:input path="coverArtLimit" size="8"/>
                <a href="helpPopup.view?topic=coverArtLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.downloadlimit"/></td>
            <td>
                <form:input path="downloadLimit" size="8"/>
                <a href="helpPopup.view?topic=downloadLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="generalsettings.uploadlimit"/></td>
            <td>
                <form:input path="uploadLimit" size="8"/>
                <a href="helpPopup.view?topic=uploadLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="3">&nbsp;</td></tr>

        <tr><td align="center" colspan="2"><input type="submit" value="<fmt:message key="common.save"/>"></td></tr>
    </table>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.top.location.href="top.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</body></html>