<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head><body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="player"/>
    <c:param name="restricted" value="${not command.admin}"/>
</c:import>

<fmt:message key="common.help" var="help"/>
<fmt:message key="common.unknown" var="unknown"/>
<c:url value="/icons/help_small.png" var="helpUrl"/>

<c:choose>
<c:when test="${empty command.players}">
    <p><fmt:message key="playersettings.noplayers"/></p>
</c:when>
<c:otherwise>

<c:url value="playerSettings.view" var="deleteUrl">
    <c:param name="delete" value="${command.playerId}"/>
</c:url>
<c:url value="playerSettings.view" var="cloneUrl">
    <c:param name="clone" value="${command.playerId}"/>
</c:url>

<table class="indent">
    <tr>
        <td><b><fmt:message key="playersettings.title"/></b></td>
        <td>
            <select name="player" onchange="location='playerSettings.view?id=' + options[selectedIndex].value;">
                <c:forEach items="${command.players}" var="player">
                    <option ${player.id eq command.playerId ? "selected" : ""}
                            value="${player.id}">${player.description}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <td><div class="forward"><a href="${deleteUrl}"><fmt:message key="playersettings.forget"/></a></div></td>
        <td><div class="forward"><a href="${cloneUrl}"><fmt:message key="playersettings.clone"/></a></div></td>
    </tr>
</table>

<form:form commandName="command" method="post" action="playerSettings.view">
<form:hidden path="playerId"/>
<table class="indent" style="border-spacing:3pt">
    <tr>
        <td><fmt:message key="playersettings.type"/></td>
        <td colspan="3">
            <c:choose>
                <c:when test="${empty command.type}">${unknown}</c:when>
                <c:otherwise>${command.type}</c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td><fmt:message key="playersettings.lastseen"/></td>
        <td colspan="3"><fmt:formatDate value="${command.lastSeen}" type="both" dateStyle="long" timeStyle="medium"/></td>
    </tr>

    <tr>
        <td colspan="3">&nbsp;</td>
    </tr>

    <tr>
        <td><fmt:message key="playersettings.name"/></td>
        <td><form:input path="name" size="16"/></td>
        <td><a href="helpPopup.view?topic=playerName" onclick="return popup(this, 'help')">
            <img src="${helpUrl}" alt="${help}" title="${help}"></a></td>
    </tr>

    <tr>
        <td><fmt:message key="playersettings.coverartsize"/></td>
        <td>
            <form:select path="coverArtSchemeName" cssStyle="width:8em">
                <c:forEach items="${command.coverArtSchemeHolders}" var="coverArtSchemeHolder">
                    <form:option value="${coverArtSchemeHolder.name}" label="${coverArtSchemeHolder.description}"/>
                </c:forEach>
            </form:select>
        </td>
        <td><a href="helpPopup.view?topic=cover" onclick="return popup(this, 'help')">
            <img src="${helpUrl}" alt="${help}" title="${help}"></a></td>
    </tr>

    <tr>
        <td><fmt:message key="playersettings.maxbitrate"/></td>
        <td>
            <form:select path="transcodeSchemeName" cssStyle="width:8em">
                <c:forEach items="${command.transcodeSchemeHolders}" var="transcodeSchemeHolder">
                    <form:option value="${transcodeSchemeHolder.name}" label="${transcodeSchemeHolder.description}"/>
                </c:forEach>
            </form:select>
        </td>
        <td><a href="helpPopup.view?topic=transcode" onclick="return popup(this, 'help')">
            <img src="${helpUrl}" alt="${help}" title="${help}"></a></td>
        <c:if test="${not command.transcodingSupported}">
            <td class="warning"><fmt:message key="playersettings.nolame"></fmt:message></td>
        </c:if>
    </tr>

    <tr><td colspan="3">&nbsp;</td></tr>

    <tr>
        <td colspan="2">
            <form:checkbox path="dynamicIp" id="dynamicIp" cssClass="checkbox"/>
            <label for="dynamicIp"><fmt:message key="playersettings.dynamicip"/></label>
        </td>
        <td><a href="helpPopup.view?topic=dynamicIp" onclick="return popup(this, 'help')">
            <img src="${helpUrl}" alt="${help}" title="${help}"></a></td>
    </tr>

    <tr>
        <td colspan="2">
            <form:checkbox path="autoControlEnabled" id="autoControlEnabled" cssClass="checkbox"/>
            <label for="autoControlEnabled"><fmt:message key="playersettings.autocontrol"/></label>
        </td>
        <td><a href="helpPopup.view?topic=autoControl" onclick="return popup(this, 'help')">
            <img src="${helpUrl}" alt="${help}" title="${help}"></a></td>
    </tr>

</table>

    <c:if test="${not empty command.allTranscodings}">
        <table class="indent">
            <tr><td><b><fmt:message key="playersettings.transcodings"/></b></td></tr>
            <c:forEach items="${command.allTranscodings}" var="transcoding" varStatus="loopStatus">
                <c:if test="${loopStatus.count % 3 == 1}"><tr></c:if>
                <td style="padding-right:2em">
                    <form:checkbox path="activeTranscodingIds" id="transcoding${transcoding.id}" value="${transcoding.id}" cssClass="checkbox"/>
                    <label for="transcoding${transcoding.id}">${transcoding.name}</label>
                </td>
                <c:if test="${loopStatus.count % 3 == 0 or loopStatus.count eq fn:length(command.allTranscodings)}"></tr></c:if>
            </c:forEach>
        </table>
    </c:if>

    <input type="submit" value="<fmt:message key="playersettings.ok"/>" style="margin-top:1em">
</form:form>

</c:otherwise>
</c:choose>

</body></html>