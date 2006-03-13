 <%--$Revision: 1.8 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    PlaylistService playlistService = ServiceFactory.getPlaylistService();
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    Player player = ServiceFactory.getPlayerService().getPlayer(request, response);
    Playlist playlist = player.getPlaylist();

    out.println("<h1>" + is.get("playlist.save.title") + "</h1>");

    if (!playlistService.getPlaylistDirectory().exists()) {
        out.println("<p>" + is.get("playlist.save.missing_folder", playlistService.getPlaylistDirectory().toString()) + "</p>");
        return;
    }

%>
<form method="post" action='savePlaylistConfirm.jsp'>
    <table>
        <tr><td><%=is.get("playlist.save.name")%></td><td><input type="text" name="name" size="30" value="<%= playlist.getName() %>"></td>
            <td align="center" colspan="2"><input type='submit' value='<%=is.get("playlist.save.save")%>'></td></tr>
    </table>
</form>

 </body></html>
