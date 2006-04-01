<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <!--[if gte IE 5.5000]>
    <script type="text/javascript" src="pngfix.js"></script>
    <![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head><body>

<h2><img src="<c:url value="/icons/random.png"/>" width="16" height="16"/>&nbsp;<fmt:message key="more.random.title"/></h2>

<form method="post" action="randomPlaylist.view">
    <table>
        <tr>
            <td><fmt:message key="more.random.text"/></td>
            <td>
                <select name="size">
                    <option value="5"><fmt:message key="more.random.songs"><fmt:param value="5"/></fmt:message></option>
                    <option value="10" selected="true"><fmt:message key="more.random.songs"><fmt:param value="10"/></fmt:message></option>
                    <option value="20"><fmt:message key="more.random.songs"><fmt:param value="20"/></fmt:message></option>
                    <option value="50"><fmt:message key="more.random.songs"><fmt:param value="50"/></fmt:message></option>
                </select>
            </td>
            <td>
                <input type="submit" value="<fmt:message key="more.random.ok"/>">
            </td>
        </tr>
    </table>
</form>

<h2><img src="<c:url value="/icons/wap.jpeg"/>" width="16" height="16"/>&nbsp;<fmt:message key="more.mobile.title"/></h2>
<fmt:message key="more.mobile.text"/>

<h2><img src="<c:url value="/icons/podcast.png"/>" width="16" height="16"/>&nbsp;<fmt:message key="more.podcast.title"/></h2>
<fmt:message key="more.podcast.text"/>

<c:if test="${model.uploadEnabled}">

    <h2><img src="<c:url value="/icons/upload.gif"/>" width="16" height="16"/>&nbsp;<fmt:message key="more.upload.title"/></h2>

    <form method="post" enctype="multipart/form-data" action="upload.view">
        <table>
            <tr>
                <td><fmt:message key="more.upload.source"/></td>
                <td colspan="2"><input type="file" id="file" name="file" size="40"/></td>
            </tr>
            <tr>
                <td><fmt:message key="more.upload.target"/></td>
                <td><input type="text" id="dir" name="dir" size="37" value="${model.uploadDirectory}"/></td>
                <td><input type="submit" value="<fmt:message key="more.upload.ok"/>"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="checkbox" checked name="unzip" id="unzip"/>
                    <label for="unzip"><fmt:message key="more.upload.unzip"/></label>
                </td>
            </tr>
        </table>
    </form>

</c:if>

</body></html>