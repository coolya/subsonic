<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head>

<body>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="musicFolder"/>
</c:import>

<table>
    <tr>
        <th><fmt:message key="musicfoldersettings.name"/></th>
        <th><fmt:message key="musicfoldersettings.path"/></th>
        <th><fmt:message key="musicfoldersettings.enabled"/></th>
    </tr>

    <c:forEach items="${model.musicFolders}" var="folder">
        <tr>
            <form method="post" action="musicFolderSettings.view">
                <input type="hidden" name="id" value="${folder.id}"/>
                <td><input type="text" name="name" size="20" value="${folder.name}"/></td>
                <td><input type="text" name="path" size="40" value="${folder.path.path}"/></td>
                <td align="center"><input type="checkbox" ${folder.enabled ? "checked" : ""} name="enabled"/></td>
                <td><input type="submit" name="edit" value="<fmt:message key="common.save"/>"/></td>
                <td><input type="submit" name="delete" value="<fmt:message key="common.delete"/>"/></td>
            </form>
        </tr>
    </c:forEach>

    <tr>
        <form method="post" action="musicFolderSettings.view">
            <td><input type="text" name="name" size="20"/></td>
            <td><input type="text" name="path" size="40"/></td>
            <td align="center"><input name="enabled" checked type="checkbox"/></td>
            <td><input type="submit" name="create" value="<fmt:message key="common.create"/>"/></td>
        </form>
    </tr>
</table>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<c:if test="${model.reload}">
    <script type="text/javascript">
        parent.frames.top.location.href="top.view?";
        parent.frames.left.location.href="left.view?";
    </script>
</c:if>

</body></html>