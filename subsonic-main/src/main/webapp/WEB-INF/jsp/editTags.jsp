<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/tagService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
</head>
<body class="mainframe">

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
    function setGenre() {
        var genre = DWRUtil.getValue("genreAll");
        for (i = 0; i < fileCount; i++) {
            DWRUtil.setValue("genre" + i, genre);
        }
    }
    function suggestTitle() {
        for (i = 0; i < fileCount; i++) {
            var title = DWRUtil.getValue("suggestedTitle" + i);
            DWRUtil.setValue("title" + i, title);
        }
    }
    function resetTitle() {
        for (i = 0; i < fileCount; i++) {
            var title = DWRUtil.getValue("originalTitle" + i);
            DWRUtil.setValue("title" + i, title);
        }
    }
    function suggestTrack() {
        for (i = 0; i < fileCount; i++) {
            var track = DWRUtil.getValue("suggestedTrack" + i);
            DWRUtil.setValue("track" + i, track);
        }
    }
    function resetTrack() {
        for (i = 0; i < fileCount; i++) {
            var track = DWRUtil.getValue("originalTrack" + i);
            DWRUtil.setValue("track" + i, track);
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
        var track = DWRUtil.getValue("track" + index);
        var album = DWRUtil.getValue("album" + index);
        var title = DWRUtil.getValue("title" + index);
        var year = DWRUtil.getValue("year" + index);
        var genre = DWRUtil.getValue("genre" + index);
        DWRUtil.setValue("status" + index, "<fmt:message key="edittags.working"/>");
        tagService.setTags(setTagsCallback, path, track, artist, album, title, year, genre);
    }
    function setTagsCallback(result) {
        var message;
        if (result == "SKIPPED") {
            message = "<fmt:message key="edittags.skipped"/>";
        } else if (result == "UPDATED") {
            message = "<b><fmt:message key="edittags.updated"/></b>";
        } else {
            message = "<div class='warning'><fmt:message key="edittags.error"/></div>"
            var errors = DWRUtil.getValue("errors");
            errors += result + "<br/>";
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
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

<table class="ruleTable indent">
    <tr>
        <th class="ruleTableHeader"><fmt:message key="edittags.file"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.track"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.songtitle"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.artist"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.album"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.year"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.genre"/></th>
        <th class="ruleTableHeader" width="60pt"><fmt:message key="edittags.status"/></th>
    </tr>
    <tr>
        <th class="ruleTableHeader"/>
        <th class="ruleTableHeader"><a href="javascript:suggestTrack()"><fmt:message key="edittags.suggest.short"/></a> |
            <a href="javascript:resetTrack()"><fmt:message key="edittags.reset.short"/></a></th>
        <th class="ruleTableHeader"><a href="javascript:suggestTitle()"><fmt:message key="edittags.suggest"/></a> |
            <a href="javascript:resetTitle()"><fmt:message key="edittags.reset"/></a></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="artistAll" size="15" onkeypress="DWRUtil.onReturn(event, setArtist)" value="${model.defaultArtist}"/>&nbsp;<a href="javascript:setArtist()"><fmt:message key="edittags.set"/></a></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="albumAll" size="15" onkeypress="DWRUtil.onReturn(event, setAlbum)" value="${model.defaultAlbum}"/>&nbsp;<a href="javascript:setAlbum()"><fmt:message key="edittags.set"/></a></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="yearAll" size="5" onkeypress="DWRUtil.onReturn(event, setYear)" value="${model.defaultYear}"/>&nbsp;<a href="javascript:setYear()"><fmt:message key="edittags.set"/></a></th>
        <th class="ruleTableHeader" style="white-space: nowrap">
            <select name="genreAll" style="width:7em">
                <option value=""/>
                <c:forEach items="${model.allGenres}" var="genre">
                    <option ${genre eq model.defaultGenre ? "selected" : ""} value="${genre}">${genre}</option>
                </c:forEach>
            </select>

            <a href="javascript:setGenre()"><fmt:message key="edittags.set"/></a>
        </th>
        <th class="ruleTableHeader"/>
    </tr>

    <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <tr>
            <str:truncateNicely lower="25" upper="25" var="fileName">${song.fileName}</str:truncateNicely>
            <input type="hidden" name="path${loopStatus.count - 1}" value="${song.path}"/>
            <input type="hidden" name="suggestedTitle${loopStatus.count - 1}" value="${song.suggestedTitle}"/>
            <input type="hidden" name="originalTitle${loopStatus.count - 1}" value="${song.title}"/>
            <input type="hidden" name="suggestedTrack${loopStatus.count - 1}" value="${song.suggestedTrack}"/>
            <input type="hidden" name="originalTrack${loopStatus.count - 1}" value="${song.track}"/>
            <td class="ruleTableCell" title="${song.fileName}">${fileName}</td>
            <td class="ruleTableCell"><input type="text" size="5" name="track${loopStatus.count - 1}" value="${song.track}"/></td>
            <td class="ruleTableCell"><input type="text" size="30" name="title${loopStatus.count - 1}" value="${song.title}"/></td>
            <td class="ruleTableCell"><input type="text" size="15" name="artist${loopStatus.count - 1}" value="${song.artist}"/></td>
            <td class="ruleTableCell"><input type="text" size="15" name="album${loopStatus.count - 1}" value="${song.album}"/></td>
            <td class="ruleTableCell"><input type="text" size="5"  name="year${loopStatus.count - 1}" value="${song.year}"/></td>
            <td class="ruleTableCell"><input type="text" name="genre${loopStatus.count - 1}" value="${song.genre}" style="width:7em"/></td>
            <td class="ruleTableCell"><div id="status${loopStatus.count - 1}"/></td>
        </tr>
    </c:forEach>

</table>

<p><input type="submit" id="save" value="<fmt:message key="common.save"/>" onclick="javascript:updateTags()"/></p>
<div class="warning" id="errors"/>
</body></html>