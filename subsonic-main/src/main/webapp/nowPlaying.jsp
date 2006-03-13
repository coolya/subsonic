 <%--$Revision: 1.15 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                  net.sourceforge.subsonic.service.*"%>

 <%
     Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
     Playlist playlist = player.getPlaylist();
    
     MusicFile current = playlist.getCurrentFile();
     if (current != null && !current.getParent().isRoot()) {
         response.sendRedirect("main.jsp?path=" + current.getParent().urlEncode() + "&updateNowPlaying=true");
     } else {
         response.sendRedirect("home.jsp?");
     }
 %>
