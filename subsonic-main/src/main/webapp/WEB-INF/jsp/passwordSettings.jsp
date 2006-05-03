<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head>

<body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="password"/>
    <c:param name="restricted" value="true"/>
</c:import>

<form:form method="post" action="passwordSettings.view" commandName="command">
    <table>
        <table>
            <tr>
                <td><fmt:message key="usersettings.username"/></td>
                <td><b>${command.username}</b></td>
                <td/>
            </tr>
            <tr>
                <td><fmt:message key="usersettings.password"/></td>
                <td><form:password path="password"/></td>
                <td class="warning"><form:errors path="password"/></td>
            </tr>
            <tr>
                <td><fmt:message key="usersettings.confirmpassword"/></td>
                <td><form:password path="confirmPassword"/></td>
                <td/>
            </tr>
            <tr>
                <td colspan="3" align="center"><input type="submit" value="<fmt:message key="common.ok"/>"/></td>
            </tr>
        </table>
    </table>
</form:form>

</body></html>
