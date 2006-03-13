 <%--$Revision: 1.10 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

<%
    PlayerService playerService = ServiceFactory.getPlayerService();
    String playerId = request.getParameter("playerId");
    String action = request.getParameter("action");

    if ("delete".equals(action)) {
        playerService.removePlayerById(playerId);
    }

    else if ("clone".equals(action)) {
        playerService.clonePlayer(playerId);
    }

    else {
        Player player = playerService.getPlayerById(playerId);
        player.setName(request.getParameter("playerName"));
        player.setCoverArtScheme(CoverArtScheme.valueOf(request.getParameter("cover")));
        player.setTranscodeScheme(TranscodeScheme.valueOf(request.getParameter("transcode")));
        player.setAutoControlEnabled(request.getParameter("autoControl") != null);
        player.setDynamicIp(request.getParameter("dynamicIp") != null);
        playerService.updatePlayer(player);
    }

    response.sendRedirect("settings.jsp?");
%>


