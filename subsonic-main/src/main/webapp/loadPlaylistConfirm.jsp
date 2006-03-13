 <%--$Revision: 1.5 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                  net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
    Playlist playlist = player.getPlaylist();

    String name = request.getParameter("name");
    ServiceFactory.getPlaylistService().loadPlaylist(playlist, name);
%>

<script language="javascript">parent.frames.playlist.location.href="playlist.jsp"</script>
<script language="javascript">parent.frames.main.location.href="nowPlaying.jsp"</script>

 </body></html>