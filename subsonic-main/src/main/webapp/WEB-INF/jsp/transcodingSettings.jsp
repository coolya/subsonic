<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="transcoding"/>
</c:import>

<form method="post" action="transcodingSettings.view">
<table class="indent">
    <tr>
        <th><fmt:message key="transcodingsettings.name"/></th>
        <th><fmt:message key="transcodingsettings.sourceformat"/></th>
        <th><fmt:message key="transcodingsettings.targetformat"/></th>
        <th><fmt:message key="transcodingsettings.step1"/></th>
        <th><fmt:message key="transcodingsettings.step2"/></th>
        <th><fmt:message key="transcodingsettings.step3"/></th>
        <th style="padding-left:1em"><fmt:message key="transcodingsettings.defaultactive"/></th>
        <th style="padding-left:1em"><fmt:message key="transcodingsettings.enabled"/></th>
        <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
    </tr>

    <c:forEach items="${model.transcodings}" var="transcoding">
        <tr>
            <td><input type="text" name="name[${transcoding.id}]" size="12" value="${transcoding.name}"/></td>
            <td><input type="text" name="sourceFormat[${transcoding.id}]" size="12" value="${transcoding.sourceFormat}"/></td>
            <td><input type="text" name="targetFormat[${transcoding.id}]" size="12" value="${transcoding.targetFormat}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step1[${transcoding.id}]" size="22" value="${transcoding.step1}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step2[${transcoding.id}]" size="22" value="${transcoding.step2}"/></td>
            <td><input  style="font-family:monospace" type="text" name="step3[${transcoding.id}]" size="22" value="${transcoding.step3}"/></td>
            <td align="center" style="padding-left:1em"><input type="checkbox" ${transcoding.defaultActive ? "checked" : ""} name="defaultActive[${transcoding.id}]" class="checkbox"/></td>
            <td align="center" style="padding-left:1em"><input type="checkbox" ${transcoding.enabled ? "checked" : ""} name="enabled[${transcoding.id}]" class="checkbox"/></td>
            <td align="center" style="padding-left:1em"><input type="checkbox" name="delete[${transcoding.id}]" class="checkbox"/></td>
        </tr>
    </c:forEach>

    <tr>
        <th colspan="9" align="left" style="padding-top:1em"><fmt:message key="transcodingsettings.add"/></th>
    </tr>

    <tr>
        <td><input type="text" name="name" size="12" value="${model.newTranscoding.name}"/></td>
        <td><input type="text" name="sourceFormat" size="12" value="${model.newTranscoding.sourceFormat}"/></td>
        <td><input type="text" name="targetFormat" size="12" value="${model.newTranscoding.targetFormat}"/></td>
        <td><input  style="font-family:monospace" type="text" name="step1" size="22" value="${model.newTranscoding.step1}"/></td>
        <td><input  style="font-family:monospace" type="text" name="step2" size="22" value="${model.newTranscoding.step2}"/></td>
        <td><input  style="font-family:monospace" type="text" name="step3" size="22" value="${model.newTranscoding.step3}"/></td>
        <td align="center" style="padding-left:1em"><input name="defaultActive" checked type="checkbox" class="checkbox"/></td>
        <td align="center" style="padding-left:1em"><input name="enabled" checked type="checkbox" class="checkbox"/></td>
        <td/>
    </tr>

    <tr>
        <td colspan="9" style="padding-top:1.5em;padding-bottom:1.5em">
            <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
            <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
        </td>
    </tr>

</table>
</form>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<div style="width:60%">
    <fmt:message key="transcodingsettings.info"><fmt:param value="${model.transcodeDirectory}"/><fmt:param value="${model.brand}"/></fmt:message>
</div>
</body></html>