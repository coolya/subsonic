 <%--$Revision: 1.33 $ $Date: 2006/03/04 22:12:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>

<%
    User user = ServiceFactory.getSecurityService().getCurrentUser(request);

    PlayerService playerService = ServiceFactory.getPlayerService();
    Player player = playerService.getPlayer(request, response);
    Playlist playlist = player.getPlaylist();

    // Whether a new M3U file should be sent, forcing the remote player to reconnect.
    boolean sendM3U = false;

    // The index of interest. Either the index of the currently playing song, or the index of the song
    // the user has done an operation on (add, remove, move up/down, skip).  Used to jump to
    // the right place on the page.
    int index = -2;

    if (request.getParameter("start") != null) {
        index = -1;
        sendM3U = true;
        playlist.setStatus(Playlist.Status.PLAYING);
    } else if (request.getParameter("stop") != null) {
        index = -1;
        sendM3U = true;
        playlist.setStatus(Playlist.Status.STOPPED);
    } else if (request.getParameter("play") != null) {
        sendM3U = true;
        MusicFile file = new MusicFile(request.getParameter("play"));
        playlist.addFile(file, false);
    } else if (request.getParameter("add") != null) {
        MusicFile file = new MusicFile(request.getParameter("add"));
        playlist.addFile(file);
        index = playlist.size() - 1;
    } else if (request.getParameter("clear") != null) {
        sendM3U = true;
        playlist.clear();
    } else if (request.getParameter("shuffle") != null) {
        index = -1;
        playlist.shuffle();
    } else if (request.getParameter("repeat") != null) {
        index = -1;
        playlist.setRepeatEnabled(!playlist.isRepeatEnabled());
    } else if (request.getParameter("skip") != null) {
        sendM3U = true;
        playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
    } else if (request.getParameter("remove") != null) {
        index = Integer.parseInt(request.getParameter("remove"));
        playlist.removeFileAt(index);
    } else if (request.getParameter("up") != null) {
        index = Integer.parseInt(request.getParameter("up"));
        playlist.moveUp(index);
        index--;
    } else if (request.getParameter("down") != null) {
        index = Integer.parseInt(request.getParameter("down"));
        playlist.moveDown(index);
        index++;
    } else if (request.getParameter("undo") != null) {
        index = -1;
        sendM3U = true;
        playlist.undo();
    }

    if (index == -2) {
        index = playlist.getIndex();
    }
    String anchor = "#" + Math.max(-1, index);

    InternationalizationService is = ServiceFactory.getInternationalizationService();
    String repeat = playlist.isRepeatEnabled() ? is.get("playlist.repeat_on") : is.get("playlist.repeat_off");
%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
     <link href="style.css" rel="stylesheet">
     <script type='text/javascript' src='/subsonic/dwr/interface/nowPlayingService.js'></script>
     <script type='text/javascript' src='/subsonic/dwr/engine.js'></script>
 </head>

 <body style="background-color:#DEE3E7" onload="onload()">

 <!-- This script uses AJAX to periodically check if the current song has changed. -->
 <script type="text/javascript" language="javascript">
     var currentFile = null;

     function onload() {
         DWREngine.setErrorHandler(null);
         location.hash="<%=anchor%>";
         startTimer();
     }

     function startTimer() {
         nowPlayingService.getFile(nowPlayingCallback);
         setTimeout("startTimer()", 10000);
     }

     function nowPlayingCallback(file){
         if (currentFile != null && currentFile != file) {
             location.replace("playlist.jsp?");
         }
         currentFile = file;
     }
 </script>

 <a name="-1">
     <h2><table style="white-space:nowrap;"><tr>
         <td><select name="player" onchange="location='playlist.jsp?player=' + options[selectedIndex].value;" >
<%
    Player[] allPlayers = playerService.getAllPlayers();
    for (int i = 0; i < allPlayers.length; i++) {
        String id = allPlayers[i].getId();
        String selected = id.equals(player.getId()) ? "selected" : "";

        // Only display authorized players.
        if (user.isAdminRole() || user.getUsername().equals(allPlayers[i].getUsername())) {
            out.println("<option " + selected + " value='" + id + "'>" + allPlayers[i] + "</option>");
        }
    }
%>
         </select></td>
<%
    if (playlist.getStatus() == Playlist.Status.PLAYING) {
        out.println("<td><a href='playlist.jsp?stop'>" + is.get("playlist.stop") + "</a> | </td>");
    } else {
        out.println("<td><a href='playlist.jsp?start'>" + is.get("playlist.start") + "</a> | </td>");
    }
%>
         <td><a href='playlist.jsp?clear'><%=is.get("playlist.clear")%></a></td>
         <td> | <a href='playlist.jsp?shuffle'><%=is.get("playlist.shuffle")%></a></td>
         <td> | <a href='playlist.jsp?repeat'><%= repeat %></a></td>
         <td> | <a href='playlist.jsp?undo'><%= is.get("playlist.undo")%></a></td>
         <td> | <a target='main' href='loadPlaylist.jsp?'><%=is.get("playlist.load")%></a></td>
<% if (user.isPlaylistRole()) { %>
         <td> | <a target='main' href='savePlaylist.jsp?'><%=is.get("playlist.save")%></a></td>
<% }%>
   </tr></table></h2></a>
<%


    MusicFile[] files = playlist.getFiles();
    MusicFile currentFile = playlist.getCurrentFile();

    if (playlist.isEmpty()) {
        out.println("<p><em>" + is.get("playlist.empty") + "</em></p>");
    }

    else {
        out.println("<table valign='top'>");

        String remove = is.get("playlist.remove");
        String up = is.get("playlist.up");
        String down = is.get("playlist.down");
        String download = is.get("common.download");

        for (int i = 0; i < files.length; i++) {
            MusicFile file = files[i];
            boolean isCurrent = file.equals(currentFile) && i == playlist.getIndex();

            MusicFile.MetaData metaData = file.getMetaData();

            out.print("<tr><a name='" + i + "'></a>");
            out.print("<td><a href='playlist.jsp?remove=" + i + "'><img width='13' height='13' src='icons/remove.gif' alt='" + remove + "' title='" + remove + "'/></a></td>");
            out.print("<td><a href='playlist.jsp?up=" + i + "'><img width='13' height='13' src='icons/up.gif' alt='" + up + "' title='" + up + "'/></a></td>");
            out.print("<td><a href='playlist.jsp?down=" + i + "'><img width='13' height='13' src='icons/down.gif' alt='" + down + "' title='" + down + "'/></a></td>");
            out.print("<td>");
            if (user.isDownloadRole()) {
                out.print("<a href='download?path=" + file.urlEncode() + "'><img width='13' height='13' src='icons/download.gif' alt='" + download + "' title='" + download + "'/></a>");
            }
            out.print("</td>");
            String style = "style='padding-left:5;padding-right:5" + (i % 2 == 0 ? ";background-color:#f0f0f0'" : "'");
            out.print("<td " + style + "><a href='playlist.jsp?skip=" + i + "'>" +
                      (isCurrent ? "<b>" : "") + StringUtil.toHtml(file.getTitle()) + (isCurrent ? "</b>" : "") +
                      "</a></td>");
            out.print("<td " + style + "><a target='main' href='main.jsp?path=" + file.getParent().urlEncode() + "'>" + getArtistAlbumYear(metaData) + "</a></td>");
            out.println("</tr>");
        }
        out.println("</table>");
    }

    // Send new M3U playlist.
    boolean isCurrentPlayer = player.getIpAddress() != null && player.getIpAddress().equals(request.getRemoteAddr());
    if (player.isAutoControlEnabled() && isCurrentPlayer && sendM3U) {
        out.println("<script language='javascript'>parent.frames.main.location.href='play.m3u'</script>");
    }
%>


<%!
    private CharSequence getArtistAlbumYear(MusicFile.MetaData metaData) {

        String artist = metaData.getArtist();
        String album  = metaData.getAlbum();
        String year   = metaData.getYear();

        if ("".equals(artist)) { artist = null; }
        if ("".equals(album)) { album = null; }
        if ("".equals(year)) { year = null; }

        StringBuffer buf = new StringBuffer();

        if (artist != null) {
            buf.append("<em>" + StringUtil.toHtml(artist) + "</em>");
        }

        if (artist != null && album != null) {
            buf.append(" - ");
        }

        if (album != null) {
            buf.append(StringUtil.toHtml(album));
        }

        if (year != null) {
            buf.append(" (" + StringUtil.toHtml(year) + ")");
        }

        return buf;
    }
%>

</body></html>