<%--$Revision: 1.15 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>
<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();

    // Create array of players to control. If the "player" attribute is set for this session,
    // only the player with this ID is controlled.  Otherwise, all players are controlled.
    PlayerService playerService = ServiceFactory.getPlayerService();
    Player[] players = playerService.getAllPlayers();
    String title = is.get("wap.playlist.title");

    String playerId = (String) session.getAttribute("player");
    if (playerId != null) {
        Player player = playerService.getPlayerById(playerId);
        if (player != null) {
            players = new Player[] {player};
            title += " - " + player.getName();
        }
    }

    for (int i = 0; i < players.length; i++) {
        Player player = players[i];
        Playlist playlist = player.getPlaylist();

        if (request.getParameter("play") != null) {
            MusicFile file = new MusicFile(request.getParameter("play"));
            playlist.addFile(file, false);
        } else if (request.getParameter("add") != null) {
            MusicFile file = new MusicFile(request.getParameter("add"));
            playlist.addFile(file);
        } else if (request.getParameter("skip") != null) {
            playlist.setIndex(Integer.parseInt(request.getParameter("skip")));
        } else if (request.getParameter("clear") != null) {
            playlist.clear();
        } else if (request.getParameter("load") != null) {
            ServiceFactory.getPlaylistService().loadPlaylist(playlist, request.getParameter("load"));
        }
    }
%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<wml>

	<head>
		<meta http-equiv="Cache-Control" content="max-age=0" forua="true"/>
		<meta http-equiv="Cache-Control" content="must-revalidate" forua="true"/>
	</head>

    <template>
        <do type="prev" name="back" label="<%=is.get("common.back")%>"><prev/></do>
    </template>


    <card id="main" title="subsonic" newcontext="false">
    <p><small><b><%= title%></b></small></p>
    <p><small>
    <%
        if (players.length == 0) {
            out.println(is.get("wap.playlist.noplayer"));
        } else {

            Playlist playlist = players[0].getPlaylist();
            MusicFile[] files = playlist.getFiles();

            out.println("<b><a href=\"index.jsp\">[" + is.get("common.home") + "]</a></b><br/>");
            out.println("<b><a href=\"loadPlaylist.jsp\">[" + is.get("wap.playlist.load") + "]</a></b><br/>");

            if (!playlist.isEmpty()) {
                out.println("<b><a href=\"playlist.jsp?clear\">[" + is.get("wap.playlist.clear") + "]</a></b></small></p><p><small>");

                for (int i = 0; i < files.length; i++) {
                    MusicFile file = files[i];
                    boolean isCurrent = file.equals(playlist.getCurrentFile()) && i == playlist.getIndex();
                    out.println((isCurrent ? "<b>" : "") + "<a href=\"playlist.jsp?skip=" + i + "\">" +
                            StringUtil.toHtml(file.getTitle()) + "</a>" + (isCurrent ? "</b>" : "") + "<br/>");
                }
            }
        }
    %>
    </small></p>

</card>
</wml>

