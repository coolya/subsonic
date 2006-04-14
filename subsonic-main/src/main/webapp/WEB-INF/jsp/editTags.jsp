<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/dwr/interface/tagService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
</head><body>

<script type="text/javascript" language="javascript">
    var index = 0;
    var fileCount = ${fn:length(model.songs)};
    function setArtist() {
        var artist = DWRUtil.getValue("artistAll");
        for (i = 0; i < fileCount; i++) {
            DWRUtil.setValue("artist" + i, artist);
        }
    }
    function setAlbum() {
        var album = DWRUtil.getValue("albumAll");
        for (i = 0; i < fileCount; i++) {
            DWRUtil.setValue("album" + i, album);
        }
    }
    function setYear() {
        var year = DWRUtil.getValue("yearAll");
        for (i = 0; i < fileCount; i++) {
            DWRUtil.setValue("year" + i, year);
        }
    }
    function suggestTitle() {
        for (i = 0; i < fileCount; i++) {
            var title = DWRUtil.getValue("suggestedTitle" + i);
            DWRUtil.setValue("title" + i, title);
        }
    }
    function updateTags() {
        document.getElementById("save").disabled = true;
        index = 0;
        DWRUtil.setValue("errors", "");
        for (i = 0; i < fileCount; i++) {
            DWRUtil.setValue("status" + i, "");
        }
        updateNextTag();
    }
    function updateNextTag() {
        var path = DWRUtil.getValue("path" + index);
        var artist = DWRUtil.getValue("artist" + index);
        var album = DWRUtil.getValue("album" + index);
        var title = DWRUtil.getValue("title" + index);
        var year = DWRUtil.getValue("year" + index);
        DWRUtil.setValue("status" + index, "Working");
        tagService.setTags(setTagsCallback, path, artist, album, title, year);
    }
    function setTagsCallback(result) {
        var message;
        if (result == "SKIPPED") {
            message = "<div style='color:blue'><fmt:message key="edittags.skipped"/></div>";
        } else if (result == "UPDATED") {
            message = "<div style='color:green'><fmt:message key="edittags.updated"/></div>";
        } else {
            message = "<div style='color:red'><fmt:message key="edittags.error"/></div>"
            var errors = DWRUtil.getValue("errors");
            errors += "<br/>" + result;
            DWRUtil.setValue("errors", errors);
        }
        DWRUtil.setValue("status" + index, message);
        index++;
        if (index < fileCount) {
            updateNextTag();
        } else {
            document.getElementById("save").disabled = false;
        }
    }
</script>

<h1><fmt:message key="edittags.title"/></h1>
<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.path}"/></sub:url>
<a href="${backUrl}"><b>[<fmt:message key="common.back"/>]</b></a>
<p/>

<table border="1" cellpadding="5" rules="all">
    <tr style="background-color:#DEE3E7;">
        <th><fmt:message key="edittags.file"/></th>
        <th><fmt:message key="edittags.songtitle"/></th>
        <th><fmt:message key="edittags.artist"/></th>
        <th><fmt:message key="edittags.album"/></th>
        <th><fmt:message key="edittags.year"/></th>
        <th width="60pt"><fmt:message key="edittags.status"/></th></tr>
    <tr style="background-color:#DEE3E7;"><th></th>
        <th><a href="javascript:suggestTitle()"><fmt:message key="edittags.suggest"/></a></th>
        <th><input type="text" name="artistAll" size="15" onkeypress="DWRUtil.onReturn(event, setArtist)" value="${model.defaultArtist}"/>&nbsp;<a href="javascript:setArtist()"><fmt:message key="edittags.set"/></a></th>
        <th><input type="text" name="albumAll" size="15" onkeypress="DWRUtil.onReturn(event, setAlbum)" value="${model.defaultAlbum}"/>&nbsp;<a href="javascript:setAlbum()"><fmt:message key="edittags.set"/></a></th>
        <th><input type="text" name="yearAll" size="5" onkeypress="DWRUtil.onReturn(event, setYear)" value="${model.defaultYear}"/>&nbsp;<a href="javascript:setYear()"><fmt:message key="edittags.set"/></a></th>
        <th></th>
    </tr>

    <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <tr>
            <str:truncateNicely lower="25" upper="25" var="fileName">${song.fileName}</str:truncateNicely>
            <input type="hidden" name="path${loopStatus.count - 1}" value="${song.path}"/>
            <input type="hidden" name="suggestedTitle${loopStatus.count - 1}" value="${song.suggestedTitle}"/>
            <td>${fileName}</td>
            <td><input type="text" size="30" name="title${loopStatus.count - 1}" value="${song.title}"/></td>
            <td><input type="text" size="15" name="artist${loopStatus.count - 1}" value="${song.artist}"/></td>
            <td><input type="text" size="15" name="album${loopStatus.count - 1}" value="${song.album}"/></td>
            <td><input type="text" size="5"  name="year${loopStatus.count - 1}" value="${song.year}"/></td>
            <td><div id="status${loopStatus.count - 1}"/></td>
        </tr>
    </c:forEach>

</table>

<p><input type="submit" id="save" value="<fmt:message key="common.save"/>" onclick="javascript:updateTags()"/></p>
<div style="color:red" id="errors"/>

</body></html>