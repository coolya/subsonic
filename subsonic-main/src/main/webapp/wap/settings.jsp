<%--$Revision: 1.6 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    String playerId = (String) session.getAttribute("id");
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
        <p><small>
          <b><a href="index.jsp">[<%=is.get("common.home")%>]</a><br/></b>
          <b><a href="#player">[<%=is.get("wap.settings.selectplayer")%>]</a></b>
        </small></p>
    </card>

    <card id="player" title="subsonic" newcontext="false">
        <p><small>

        <b><a href="index.jsp">[<%=is.get("common.home")%>]</a><br/></b>
        </small></p><p><small>

<%
    if (playerId != null) out.print("<a href=\"selectPlayer.jsp\">");
    out.print(is.get("wap.settings.allplayers"));
    if (playerId != null) out.print("</a>");
    out.print("<br/>");

    Player[] players = ServiceFactory.getPlayerService().getAllPlayers();
    User user = ServiceFactory.getSecurityService().getCurrentUser(request);
    for (int i = 0; i < players.length; i++) {
        Player player = players[i];

        // Only display authorized players.
        if (user.isAdminRole() || user.getUsername().equals(player.getUsername())) {
            boolean isSelected = player.getId().equals(playerId);
            if (!isSelected) out.print("<a href=\"selectPlayer.jsp?player=" + player.getId() + "\">");
            out.print(player);
            if (!isSelected) out.print("</a>");
            out.print("<br/>");
        }
    }
%>

        </small></p>
    </card>

</wml>

