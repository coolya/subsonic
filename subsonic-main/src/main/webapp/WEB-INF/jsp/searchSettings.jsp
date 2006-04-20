<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<html><head>
    <script type="text/javascript" src="<c:url value="/scripts.js"/>"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head>

<body>

<form:form commandName="command" action="searchSettings.view" method="post">

<table>
    <tr>
        <td><fmt:message key="searchsettings.auto"/></td>
        <td>
            <form:select path="interval">
                <fmt:message key="searchsettings.interval.never" var="never"/>
                <fmt:message key="searchsettings.interval.one" var="one"/>
                <form:option value="-1" label="${never}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 7 14 30 60" delims=" " var="interval">
                    <fmt:message key="searchsettings.interval.many" var="many"><fmt:param value="${interval}"/></fmt:message>
                    <form:option value="${interval}" label="${many}"/>
                </c:forTokens>
            </form:select>
        </td>

        <td>
            <form:select path="hour">
                <c:forEach begin="0" end="23" var="hour">
                    <fmt:message key="searchsettings.hour" var="hourLabel"><fmt:param value="${hour}"/></fmt:message>
                    <form:option value="${hour}" label="${hourLabel}"/>
                </c:forEach>
            </form:select>
        </td>

        <td>
            <input type="submit" value="<fmt:message key="common.save"/>"/>
        </td>

    </tr>

    <tr>
        <td colspan="4">
            <a href="searchSettings.view?update"><fmt:message key="searchsettings.manual"/></a>
        </td>
    </tr>
</table>
</form:form>

<c:if test="${command.creatingIndex}">
    <p><b><fmt:message key="searchsettings.text"/></b></p>
</c:if>

</body></html>