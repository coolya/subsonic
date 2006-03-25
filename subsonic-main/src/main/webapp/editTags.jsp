<%--$Revision: 1.19 $ $Date: 2006/03/01 16:58:08 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.util.*"%>


<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
    <script type='text/javascript' src='/subsonic/dwr/interface/tagService.js'></script>
    <script type='text/javascript' src='/subsonic/dwr/engine.js'></script>
    <script type='text/javascript' src='/subsonic/dwr/util.js'></script>
</head><body>

<!-- TODO:  i18n -->

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    MusicFile dir = new MusicFile(request.getParameter("path"));
    MusicFile[] files = dir.getChildren(false);

    String defaultArtist = "";
    String defaultAlbum = "";
    String defaultYear = "";
    if (files.length > 0) {
        MusicFile.MetaData metaData = files[0].getMetaData();
        defaultArtist = metaData.getArtist() == null ? "" : metaData.getArtist();
        defaultAlbum = metaData.getAlbum() == null ? "" : metaData.getAlbum();
        defaultYear = metaData.getYear() == null ? "" : metaData.getYear();
    }

%>
<script type="text/javascript" language="javascript">
    var index = 0;
    var fileCount = <%=files.length%>;
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
            message = "<div style='color:blue'><%=is.get("edittags.skipped")%></div>";
        } else if (result == "UPDATED") {
            message = "<div style='color:green'><%=is.get("edittags.updated")%></div>";
        } else {
            message = "<div style='color:red'><%=is.get("edittags.error")%></div>"
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
<h1><%=is.get("edittags.title")%></h1>
<a href="main.jsp?path=<%=dir.urlEncode()%>"><b>[<%=is.get("common.back")%>]</b></a>

<table border="1" cellpadding="5" rules="all">
    <tr style="background-color:#DEE3E7;">
        <th><%=is.get("edittags.file")%></th>
        <th><%=is.get("edittags.songtitle")%></th>
        <th><%=is.get("edittags.artist")%></th>
        <th><%=is.get("edittags.album")%></th>
        <th><%=is.get("edittags.year")%></th>
        <th width="60pt"><%=is.get("edittags.status")%></th></tr>
    <tr style="background-color:#DEE3E7;"><th></th>
        <th><a href="javascript:suggestTitle()"><%=is.get("edittags.suggest")%></a></th>
        <th><input type="text" name="artistAll" size="15" onkeypress="DWRUtil.onReturn(event, setArtist)" value="<%=defaultArtist%>"/>&nbsp;<a href="javascript:setArtist()"><%=is.get("edittags.set")%></a></th>
        <th><input type="text" name="albumAll" size="15" onkeypress="DWRUtil.onReturn(event, setAlbum)" value="<%=defaultAlbum%>"/>&nbsp;<a href="javascript:setAlbum()"><%=is.get("edittags.set")%></a></th>
        <th><input type="text" name="yearAll" size="5" onkeypress="DWRUtil.onReturn(event, setYear)" value="<%=defaultYear%>"/>&nbsp;<a href="javascript:setYear()"><%=is.get("edittags.set")%></a></th>
        <th></th>
    </tr>
<%
    Mp3Parser parser = new Mp3Parser();
    for (int i = 0; i < files.length; i++) {
        MusicFile file = files[i];
        String fileName = StringUtil.abbrev(file.getName(), 25);

        MusicFile.MetaData metaData = parser.isApplicable(file) ? parser.getRawMetaData(file) : new MusicFile.MetaData("", "", "", "");
        String suggestedTitle = parser.guessTitle(file);
        String title = metaData.getTitle() == null ? "" : metaData.getTitle();
        String artist = metaData.getArtist() == null ? "" : metaData.getArtist();
        String album = metaData.getAlbum() == null ? "" : metaData.getAlbum();
        String year = metaData.getYear() == null ? "" : metaData.getYear();
%>
    <tr>
        <input type="hidden" name="path<%=i%>" value="<%=file.getPath()%>"/>
        <input type="hidden" name="suggestedTitle<%=i%>" value="<%=suggestedTitle%>"/>
        <td><%=fileName%></td>
        <td><input type="text" size="30" name="title<%=i%>" value="<%=title%>"/></td>
        <td><input type="text" size="15" name="artist<%=i%>" value="<%=artist%>"/></td>
        <td><input type="text" size="15" name="album<%=i%>" value="<%=album%>"/></td>
        <td><input type="text" size="5"  name="year<%=i%>" value="<%=year%>"/></td>
        <td><div id="status<%=i%>"/></td>
    </tr>
<% } %>
</table>
<p><input type="submit" id="save" value="<%=is.get("common.save")%>" onclick="javascript:updateTags()"/></p>

<div style="color:red" id="errors"/>

</body></html>