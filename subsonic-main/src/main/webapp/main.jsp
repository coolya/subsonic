<%--$Revision: 1.32 $ $Date: 2006/03/01 20:58:12 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.io.*,
                 java.text.*"%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
    <script type='text/javascript' src='/subsonic/dwr/interface/nowPlayingService.js'></script>
    <script type='text/javascript' src='/subsonic/dwr/engine.js'></script>
</head>

<body>

<% if (request.getParameter("updateNowPlaying") != null) { %>

<!-- This script uses AJAX to periodically check if the current song has changed. -->
<script type="text/javascript" language="javascript">

    var currentDir = null;
    window.onload = onload();

    function onload() {
        DWREngine.setErrorHandler(null);
        startTimer();
    }

    function startTimer() {
        nowPlayingService.getDirectory(nowPlayingCallback);
        setTimeout("startTimer()", 10000);
    }

    function nowPlayingCallback(dir){
        if (currentDir != null && currentDir != dir) {
            location.replace("nowPlaying.jsp?");
        }
        currentDir = dir;
    }
</script>

<%
    }
    InternationalizationService is = ServiceFactory.getInternationalizationService();

    String path = request.getParameter("path");
    MusicFile dir = new MusicFile(path);
    MusicFile[] children = dir.getChildren(false, true);

    boolean hasChildDirectories = false;
    for (int i = 0; i < children.length; i++) {
        if (children[i].isDirectory()) {
            hasChildDirectories = true;
            break;
        }
    }

    // Guess if the path points to an album.
    boolean isAlbum = !hasChildDirectories && children.length > 0;

    User user = ServiceFactory.getSecurityService().getCurrentUser(request);
    Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
%>

<h1><%=StringUtil.toHtml(dir.getFormattedPath())%></h1>
<h2>

<% if (!dir.getParent().isRoot()) { %>
<a href="main.jsp?path=<%=dir.getParent().urlEncode()%>"><%=is.get("main.up")%></a> |
<% } %>
<a target="playlist" href="playlist.jsp?play=<%=dir.urlEncode()%>"><%=is.get("main.playall")%></a> |
<a target="playlist" href="playlist.jsp?add=<%=dir.urlEncode()%>"><%=is.get("main.addall")%></a>
<%
    if (isAlbum) {
        out.println("| <a href='albumInfo.jsp?path=" + dir.urlEncode() + "'>" + is.get("main.albuminfo") + "</a>");
        if (user.isCoverArtRole()) {
            out.println("| <a href='changeCoverArt.jsp?path=" + dir.urlEncode() + "'>" + is.get("main.cover") + "</a>");
        }
        if (user.isCommentRole()) {
            out.println("| <a href='javascript:toggleComment()'>" + is.get("main.comment") + "</a>");
        }
    }
%>
</h2>

<%
    if (isAlbum) {

        MusicInfoService musicInfoService = ServiceFactory.getMusicInfoService();
        MusicFileInfo musicInfo = musicInfoService.getMusicFileInfoForPath(path);
        int rating = musicInfo == null ? 0 : musicInfo.getRating();
        String comment = musicInfo == null || musicInfo.getComment() == null ? "" : musicInfo.getComment();
        String ratingString = is.get("main.rating");
        String useMap = user.isCommentRole() ?  "usemap='#ratingMap'" : "";

        StringBuffer playInfo = new StringBuffer();
        playInfo.append(is.get("main.playcount", musicInfo == null ? 0 : musicInfo.getPlayCount()));
        if (musicInfo != null && musicInfo.getLastPlayed() != null) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, is.getLocale());
            playInfo.append(' ').append(is.get("main.lastplayed", dateFormat.format(musicInfo.getLastPlayed())));
        }
%>
<map id="ratingMap" name="ratingMap">
    <area href="setMusicFileInfo.jsp?action=rating&path=<%=dir.urlEncode()%>&rating=1" shape="rect" coords="0,0,12,13"  alt="<%=ratingString%> 1"/>
    <area href="setMusicFileInfo.jsp?action=rating&path=<%=dir.urlEncode()%>&rating=2" shape="rect" coords="13,0,25,13" alt="<%=ratingString%> 2"/>
    <area href="setMusicFileInfo.jsp?action=rating&path=<%=dir.urlEncode()%>&rating=3" shape="rect" coords="26,0,38,13" alt="<%=ratingString%> 3"/>
    <area href="setMusicFileInfo.jsp?action=rating&path=<%=dir.urlEncode()%>&rating=4" shape="rect" coords="39,0,51,13" alt="<%=ratingString%> 4"/>
    <area href="setMusicFileInfo.jsp?action=rating&path=<%=dir.urlEncode()%>&rating=5" shape="rect" coords="52,0,64,13" alt="<%=ratingString%> 5"/>
</map>
<img src="icons/rating<%=rating%>.gif" alt="<%=ratingString + ' ' + rating%>" <%=useMap%>/>
<%=playInfo%>

<div id="commentDiv" style="width:50%;font-style:italic;"><%=comment.replaceAll("\\n", "<br/>")%></div>
<form method="post" id="commentForm" action="setMusicFileInfo.jsp" style="display:none">
    <input type="hidden" name="action" value="comment"/>
    <input type="hidden" name="path" value="<%=path%>"/>
    <textarea name="comment" rows="6" cols="70"><%=comment%></textarea>
    <input type="submit" value="<%=is.get("common.save")%>"/>
</form>

<script type='text/javascript'>
    function toggleComment() {
        var commentForm = document.getElementById('commentForm');
        var commentDiv = document.getElementById('commentDiv');

        if (commentForm.style.display == "none")  {
            commentForm.style.display = "";
            commentDiv.style.display = "none";
        } else {
            commentForm.style.display = "none";
            commentDiv.style.display = "";
        }
    }
</script>

<%
    }
%>

<table cellpadding="10"><tr style="vertical-align:top;"><td style="vertical-align:top;">

<%
    String play = is.get("common.play");
    String add = is.get("common.add");
    String download = is.get("common.download");

    for (int i = 0; i < children.length; i++) {
        MusicFile child = children[i];

        String param = "path=" + child.urlEncode();

        out.print("<p class='dense'><a target='playlist' href='playlist.jsp?play=" + child.urlEncode() +
                "'><img width='13' height='13' src='icons/play.gif' alt='" + play + "' title='" + play + "'/></a> ");
        out.print("<a target='playlist' href='playlist.jsp?add=" + child.urlEncode() +
                "'><img width='13' height='13' src='icons/add.gif' alt='" + add + "' title='" + add + "'/></a> ");

        if (user.isDownloadRole()) {
            out.print("<a href='download?path=" + child.urlEncode() +
                    "'><img width='13' height='13' src='icons/download.gif' alt='" + download + "' title='" + download + "'/></a> ");
        }

        if (child.isDirectory()) {
            out.println("<a href='main.jsp?" + param + "'>" + StringUtil.toHtml(child.getName()) + "</a></p>");
        }
        else {
            out.println(StringUtil.toHtml(child.getTitle()) + "</p>");
        }
    }

    out.println("</td><td>");

    CoverArtScheme scheme = player.getCoverArtScheme();
    if (scheme != CoverArtScheme.OFF) {
        int limit = ServiceFactory.getSettingsService().getCoverArtLimit();
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }
        File[] coverArt = dir.getCoverArt(limit);
        int baseSize = scheme.getSize();

        for (int i = 0; i < coverArt.length; i++) {
            int size = coverArt.length == 1 ? baseSize * 2 : baseSize;
            out.println("<a href='main.jsp?path=" + StringUtil.urlEncode(coverArt[i].getParent()) + "'>" +
                    "<img height='" + size + "' width='" + size + "' hspace='5' vspace='5' src='coverart?size=" + size + "&path=" +
                    StringUtil.urlEncode(coverArt[i].getPath()) + "'/></a>");
        }

        if (coverArt.length == 0 && isAlbum) {
            int size = baseSize * 2;
            out.println("<table><tr><td><img height='" + size + "' width='" + size + "' hspace='5' vspace='5' " +
                    "src='coverart?size=" + size + "'/></td></tr><tr><td align='center'><a href='changeCoverArt.jsp?path=" +
                    dir.urlEncode() + "'>"+ is.get("main.cover")+ "</a></td></tr></table>");
        }
    }
%>

</td></tr></table>
</body>
</html>
