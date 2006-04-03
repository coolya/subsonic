<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h1><fmt:message key="playlist.save.title"/></h1>
<form:form commandName="playlist" method="post" action="savePlaylist.view">
    <table>
        <tr>
            <td><fmt:message key="playlist.save.name"/></td>
            <td><form:input path="name" size="30"/>
                <input type="submit" value="<fmt:message key="playlist.save.save"/>"></td>
        </tr>
        <tr>
            <td colspan="2" style="color:red"><form:errors path="name"/></td>
        </tr>
    </table>
</form:form>
</body></html>
