<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
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
            <td colspan="2" class="warning"><form:errors path="name"/></td>
        </tr>
    </table>
</form:form>
</body></html>
