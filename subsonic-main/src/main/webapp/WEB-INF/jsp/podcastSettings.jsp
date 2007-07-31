<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
</head><body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="podcast"/>
</c:import>

<form:form commandName="command" action="podcastSettings.view" method="post">

<table class="indent">
    <tr>
        <td><fmt:message key="podcastsettings.update"/></td>
        <td>
            <form:select path="interval">
                <fmt:message key="podcastsettings.interval.never" var="never"/>
                <fmt:message key="podcastsettings.interval.hourly" var="hourly"/>
                <fmt:message key="podcastsettings.interval.daily" var="daily"/>
                <fmt:message key="podcastsettings.interval.weekly" var="weekly"/>

                <form:option value="-1" label="${never}"/>
                <form:option value="1" label="${hourly}"/>
                <form:option value="24" label="${daily}"/>
                <form:option value="168" label="${weekly}"/>
            </form:select>
        </td>

        <td>
            <form:select path="hour">
                <c:forEach begin="0" end="23" var="hour">
                    <fmt:message key="podcastsettings.hour" var="hourLabel"><fmt:param value="${hour}"/></fmt:message>
                    <form:option value="${hour}" label="${hourLabel}"/>
                </c:forEach>
            </form:select>
        </td>
    </tr>

    <tr>
        <td><fmt:message key="podcastsettings.keep"/></td>
        <td>
            <form:select path="episodeCount">
                <fmt:message key="podcastsettings.keep.all" var="all"/>
                <fmt:message key="podcastsettings.keep.one" var="one"/>

                <form:option value="-1" label="${all}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 4 5 10" delims=" " var="count">
                    <fmt:message key="podcastsettings.keep.many" var="many"><fmt:param value="${count}"/></fmt:message>
                    <form:option value="${count}" label="${many}"/>
                </c:forTokens>

            </form:select>
        </td>
    </tr>

    <tr>
        <td>Save Podcasts in directory</td>
        <td colspan="2"><form:input path="directory"/></td>
    </tr>

    <tr>
        <td colspan="3">
            <input type="submit" value="<fmt:message key="common.save"/>"/>
        </td>
    </tr>

</table>

</form:form>

</body></html>