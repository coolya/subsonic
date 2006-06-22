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

        <tr><td align="center" colspan="2"><input type="submit" value="<fmt:message key="common.save"/>"></td></tr>
    </table>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.upper.location.href="top.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</body></html>