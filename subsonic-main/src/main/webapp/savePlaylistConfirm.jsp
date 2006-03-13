 <%--$Revision: 1.5 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                  net.sourceforge.subsonic.service.*"%>

<%
    Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
    Playlist playlist = player.getPlaylist();

    playlist.setName(request.getParameter("name"));
    ServiceFactory.getPlaylistService().savePlaylist(playlist);
%>

<jsp:forward page="nowPlaying.jsp"/>