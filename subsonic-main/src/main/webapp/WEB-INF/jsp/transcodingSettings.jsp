<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="transcoding"/>
</c:import>

<table class="indent">
    <tr>
        <th><fmt:message key="transcodingsettings.name"/></th>
        <th><fmt:message key="transcodingsettings.sourceformat"/></th>
        <th><fmt:message key="transcodingsettings.targetformat"/></th>
        <th><fmt:message key="transcodingsettings.step1"/></th>
        <th><fmt:message key="transcodingsettings.step2"/></th>
        <th><fmt:message key="transcodingsettings.step3"/></th>
        <th><fmt:message key="transcodingsettings.defaultactive"/></th>
        <th><fmt:message key="transcodingsettings.enabled"/></th>
    </tr>

    <c:forEach items="${model.transcodings}" var="transcoding">
        <tr>
            <form method="post" action="transcodingSettings.view">
                <input type="hidden" name="id" value="${transcoding.id}"/>
                <td><input type="text" name="name" size="12" value="${transcoding.name}"/></td>
                <td><input type="text" name="sourceFormat" size="12" value="${transcoding.sourceFormat}"/></td>
                <td><input type="text" name="targetFormat" size="12" value="${transcoding.targetFormat}"/></td>
                <td><input  style="font-family:monospace" type="text" name="step1" size="22" value="${transcoding.step1}"/></td>
                <td><input  style="font-family:monospace" type="text" name="step2" size="22" value="${transcoding.step2}"/></td>
                <td><input  style="font-family:monospace" type="text" name="step3" size="22" value="${transcoding.step3}"/></td>
                <td align="center"><input type="checkbox" ${transcoding.defaultActive ? "checked" : ""} name="defaultActive" class="checkbox"/></td>
                <td align="center"><input type="checkbox" ${transcoding.enabled ? "checked" : ""} name="enabled" class="checkbox"/></td>
                <td><input type="submit" name="edit" value="<fmt:message key="common.save"/>" style="width:75px"/></td>
                <td><input type="submit" name="delete" value="<fmt:message key="common.delete"/>" style="width:75px"/></td>
            </form>
        </tr>
    </c:forEach>

    <tr>
        <form method="post" action="transcodingSettings.view">
            <td><input type="text" name="name" size="12" value="${model.newTranscoding.name}"/></td>
            <td><input type="text" name="sourceFormat" size="12" value="${model.newTranscoding.sourceFormat}"/></td>
            <td><input type="text" name="targetFormat" size="12" value="${model.newTranscoding.targetFormat}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step1" size="22" value="${model.newTranscoding.step1}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step2" size="22" value="${model.newTranscoding.step2}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step3" size="22" value="${model.newTranscoding.step3}"/></td>
            <td align="center"><input name="defaultActive" checked type="checkbox" class="checkbox"/></td>
            <td align="center"><input name="enabled" checked type="checkbox" class="checkbox"/></td>
            <td><input type="submit" name="create" value="<fmt:message key="common.create"/>" style="width:75px"/></td>
        </form>
    </tr>
</table>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<div style="width:60%">
    <fmt:message key="transcodingsettings.info"><fmt:param value="${model.transcodeDirectory}"/></fmt:message>
</div>
</body></html>