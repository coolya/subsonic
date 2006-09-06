<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head><body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="password"/>
    <c:param name="restricted" value="true"/>
</c:import>

<h2><fmt:message key="passwordsettings.title"><fmt:param>${command.username}</fmt:param></fmt:message></h2>
<form:form method="post" action="passwordSettings.view" commandName="command">
    <table class="indent">
        <tr>
            <td><fmt:message key="usersettings.newpassword"/></td>
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
</form:form>

</body></html>
