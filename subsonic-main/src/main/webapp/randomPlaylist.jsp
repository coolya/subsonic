<%--$Revision: 1.8 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>
<%@ page import="java.util.*"%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>

<%
    int size = Integer.parseInt(request.getParameter("size"));

    Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
    Playlist playlist = player.getPlaylist();
    playlist.clear();

    List randomFiles = ServiceFactory.getSearchService().getRandomMusicFiles(size);
    for (int i = 0; i < randomFiles.size(); i++) {
        playlist.addFile((MusicFile) randomFiles.get(i));
    }
%>

<script language="javascript">parent.frames.playlist.location.href="playlist.jsp?"</script>
<script language="javascript">parent.frames.main.location.href="more.jsp?"</script>

</body></html>
