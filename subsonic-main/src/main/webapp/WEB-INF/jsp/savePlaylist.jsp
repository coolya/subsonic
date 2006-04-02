<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h1><fmt:message key="playlist.save.title"/></h1>
<form method="post" action="savePlaylist.view">
    <table>
        <spring:bind path="playlist.name">
            <tr>
                <td><fmt:message key="playlist.save.name"/></td>
                <td><input type="text" name="name" size="30" value="${status.value}">
                    <input type="submit" value="<fmt:message key="playlist.save.save"/>"></td>
            </tr>
            <tr>
                <td colspan="2" style="color:red">${status.errorMessage}</td>
            </tr>
        </spring:bind>
    </table>
</form>
</body></html>
