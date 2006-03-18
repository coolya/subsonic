<%--$Revision: 1.9 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="net.sourceforge.subsonic.util.*"%>
<%@ page import="java.io.*"%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>

<%
    PlaylistService playlistService = ServiceFactory.getPlaylistService();
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    User user = ServiceFactory.getSecurityService().getCurrentUser(request);

    out.println("<h1>" + is.get("playlist.load.title") + "</h1>");

    if (!playlistService.getPlaylistDirectory().exists()) {
        out.println("<p>" + is.get("playlist.load.missing_folder", playlistService.getPlaylistDirectory().toString()) + "</p>");
    } else {
        File[] playlists = playlistService.getSavedPlaylists();
        String load = is.get("playlist.load.load");
        String delete = is.get("playlist.load.delete");
        String download = is.get("common.download");

        if (playlists.length == 0) {
            out.println("<p>" + is.get("playlist.load.empty") + "</p>");
        } else {
            out.println("<table>");
            for (int i = 0; i < playlists.length; i++) {

                String name = StringUtil.removeSuffix(playlists[i].getName());
                String encodedName = StringUtil.urlEncode(playlists[i].getName());
                out.println("<tr><td>" + name + "</td><td><a href='loadPlaylistConfirm.jsp?name=" + encodedName + "'>[" + load + "]</a></td>");
                if (user.isPlaylistRole()) {
                    out.println("<td><a href='deletePlaylist.jsp?name=" + encodedName + "'>[" + delete + "]</a></td>");
                }
                if (user.isDownloadRole()) {
                    out.print("<td><a href='download?playlist=" + encodedName + "'>[" + download + "]</a></td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }
%>
</body></html>