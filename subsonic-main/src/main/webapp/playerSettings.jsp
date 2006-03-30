 <%--$Revision: 1.19 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>
 <%@ page import="java.text.*"%>

 <html><head>
     <script language="javascript" type="text/javascript" src="scripts.js"></script>
     <!--[if gte IE 5.5000]>
     <script type="text/javascript" src="pngfix.js"></script>
     <![endif]-->
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <%
    String id = request.getParameter("id");
    Player player = ServiceFactory.getPlayerService().getPlayerById(id);
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, is.getLocale());

    String help = is.get("common.help");
    String name = player.getName() == null ? "" : player.getName();
    String type = player.getType() == null ? is.get("common.unknown") : player.getType();
    String lastSeen = player.getLastSeen() == null ? "" : dateFormat.format(player.getLastSeen());
%>

<h1><%=is.get("playersettings.title", player.toString())%></h1>


 <form method="post" action='playerSettingsConfirm.jsp'>

<input type="hidden" name="playerId" value="<%= id%>"/> 
    <table border="0" style="border-spacing:3pt">

        <tr><td><%=is.get("playersettings.type")%></td><td colspan="3"><%=type%></td></tr>
        <tr><td><%=is.get("playersettings.lastseen")%></td><td colspan="3"><%=lastSeen%></td></tr>
        <tr><td colspan="4">&nbsp;</td></tr>

        <tr><td><%=is.get("playersettings.name")%></td><td><input type="text" name="playerName" size="16" value="<%=name%>">
        </td><td><a href="helpPopup.view?topic=playerName" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

        <tr><td><%=is.get("playersettings.coverartsize")%></td><td><select name="cover">

<%

    CoverArtScheme coverArtScheme = player.getCoverArtScheme();
    CoverArtScheme[] allCoverArtSchemes = CoverArtScheme.values();
    for (int i = 0; i < allCoverArtSchemes.length; i++) {
        CoverArtScheme scheme = allCoverArtSchemes[i];
        out.println("<option " + (scheme == coverArtScheme ? "selected" : "") + " value='" + scheme.name() + "'>" + scheme + "</option>");
    }
%>
        </select></td><td><a href="helpPopup.view?topic=cover" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

        <tr><td><%=is.get("playersettings.maxbitrate")%></td><td><select name="transcode">
<%
    TranscodeScheme transcodeScheme = player.getTranscodeScheme();
    TranscodeScheme[] allTranscodeSchemes = TranscodeScheme.values();
    for (int i = 0; i < allTranscodeSchemes.length; i++) {
        TranscodeScheme scheme = allTranscodeSchemes[i];
        out.println("<option " + (scheme == transcodeScheme ? "selected" : "") + " value='" + scheme.name() + "'>" + scheme + "</option>");
    }
%>
        </select></td><td><a href="helpPopup.view?topic=transcode" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td>

<%
    if (!TranscodedInputStream.isTranscodingSupported()) {
        out.println("<td>" + is.get("playersettings.nolame") + "</td>");
    }
%>
        </tr>
        <tr><td colspan="3">&nbsp;</td></tr>

        <tr>
            <td colspan="2"><input type="checkbox" name="dynamicIp" <%= player.isDynamicIp() ? "checked" : ""%> id="dynamicIp"/>
                <label for="dynamicIp"><%=is.get("playersettings.dynamicip")%></label></td>
            <td><a href="helpPopup.view?topic=dynamicIp" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td>
        </tr>
        <tr>
            <td colspan="2"><input type="checkbox" name="autoControl" <%= player.isAutoControlEnabled() ? "checked" : ""%> id="autoControl"/>
                <label for="autoControl"><%=is.get("playersettings.autocontrol")%></label></td>
            <td><a href="helpPopup.view?topic=autoControl" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td>
            <td><input type='submit' value='<%=is.get("playersettings.ok")%>'></td>
        </tr>
    </table>
</form>


<a href="settings.jsp">[<%=is.get("common.back")%>]</a>&nbsp;&nbsp;
<a href="playerSettingsConfirm.jsp?action=delete&playerId=<%=id%>">[<%=is.get("playersettings.forget")%>]</a>&nbsp;&nbsp;
<a href="playerSettingsConfirm.jsp?action=clone&playerId=<%=id%>">[<%=is.get("playersettings.clone")%>]</a>


</body></html>