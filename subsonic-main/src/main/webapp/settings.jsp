 <%--$Revision: 1.31 $ $Date: 2006/03/01 23:37:36 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>
 <%@ page import="java.util.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <script language="javascript" type="text/javascript" src="scripts.js"></script>
     <!--[if gte IE 5.5000]>
    <script type="text/javascript" src="pngfix.js"></script>
    <![endif]-->
     <link href="style.css" rel="stylesheet">
 </head><body>
<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    SettingsService settings = ServiceFactory.getSettingsService();
    User currentUser = ServiceFactory.getSecurityService().getCurrentUser(request);

    // Redirect if not admin.
    if (!currentUser.isAdminRole()) {
        response.sendRedirect("restrictedSettings.jsp");
        return;
    }

    String help = is.get("common.help");
%>

 <h2><%=is.get("settings.player.title")%></h2>
 <p><%=is.get("settings.player.text")%></p>
 <table>
<%
     Player[] players = ServiceFactory.getPlayerService().getAllPlayers();
     for (int i = 0; i < players.length; i++) {
         Player player = players[i];
         out.println("<tr><td><a href='playerSettings.jsp?id=" + player.getId() + "'>" + player + "</a></td></tr>");
     }
%>
 </table>

<h2><%=is.get("settings.musicfolder.title")%></h2>

 <table>
     <tr><th><%=is.get("settings.musicfolder.name")%></th><th><%=is.get("settings.musicfolder.path")%></th><th><%=is.get("settings.musicfolder.enabled")%></th></tr>
<%
 MusicFolder[] folders = ServiceFactory.getSettingsService().getAllMusicFolders(true);
 for (int i = 0; i < folders.length; i++) {
     MusicFolder folder = folders[i];
%>
     <tr>
         <form method="post" action="editMusicFolder.jsp">
             <input type="hidden" name="id" value="<%=folder.getId()%>"/>
             <td><input type="text" name="name" size="20" value="<%=folder.getName()%>"/></td>
             <td><input type="text" name="path" size="40" value="<%=folder.getPath().getPath()%>"/></td>
             <td align="center"><input type="checkbox" <%=folder.isEnabled() ? "checked" : ""%> name="enabled"/></td>
             <td><input type="submit" name="edit" value="<%=is.get("common.save")%>"/></td>
             <td><input type="submit" name="delete" value="<%=is.get("common.delete")%>"/></td>
         </form>
     </tr>
<%
 }
%>
     <tr>
         <form method="post" action="editMusicFolder.jsp">
             <td><input type="text" name="name" size="20"/></td>
             <td><input type="text" name="path" size="40"/></td>
             <td align="center"><input name="enabled" checked type="checkbox"/></td>
             <td><input type="submit" name="create" value="<%=is.get("common.create")%>"/></td>
         </form>
     </tr>
 </table>

 <h2><%=is.get("settings.radio.title")%></h2>

 <table>
     <tr><th><%=is.get("settings.radio.name")%></th><th><%=is.get("settings.radio.streamurl")%></th>
         <th><%=is.get("settings.radio.homepageurl")%></th><th><%=is.get("settings.radio.enabled")%></th></tr>
<%
    InternetRadio[] radios = ServiceFactory.getSettingsService().getAllInternetRadios(true);
    for (int i = 0; i < radios.length; i++) {
        InternetRadio radio = radios[i];
%>
     <tr>
         <form method="post" action="editInternetRadio.jsp">
             <input type="hidden" name="id" value="<%=radio.getId()%>"/>
             <td><input type="text" name="name" size="20" value="<%=radio.getName()%>"/></td>
             <td><input type="text" name="streamUrl" size="40" value="<%=radio.getStreamUrl()%>"/></td>
             <td><input type="text" name="homepageUrl" size="40" value="<%=radio.getHomepageUrl()%>"/></td>
             <td align="center"><input type="checkbox" <%=radio.isEnabled() ? "checked" : ""%> name="enabled"/></td>
             <td><input type="submit" name="edit" value="<%=is.get("common.save")%>"/></td>
             <td><input type="submit" name="delete" value="<%=is.get("common.delete")%>"/></td>
         </form>
     </tr>
<%
 }
%>
     <tr>
         <form method="post" action="editInternetRadio.jsp">
             <td><input type="text" name="name" size="20"/></td>
             <td><input type="text" name="streamUrl" size="40"/></td>
             <td><input type="text" name="homepageUrl" size="40"/></td>
             <td align="center"><input name="enabled" checked type="checkbox"/></td>
             <td><input type="submit" name="create" value="<%=is.get("common.create")%>"/></td>
         </form>
     </tr>
 </table>

<form method="post" action='settingsConfirm.jsp'>

<table>

<tr><td><%=is.get("settings.playlistfolder")%></td><td><input type="text" name="playlistFolder" size="70" value="<%= settings.getPlaylistFolder()%>"/>
<a href="helpPopup.view?topic=playlistFolder" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.musicmask")%></td><td><input type="text" name="musicMask" size="70" value="<%= settings.getMusicMask()%>"/>
<a href="helpPopup.view?topic=musicMask" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.coverartmask")%></td><td><input type="text" name="coverArtMask" size="70" value="<%= settings.getCoverArtMask()%>"/>
<a href="helpPopup.view?topic=coverArtMask" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr><td><%=is.get("settings.index")%></td><td><input type="text" name="index" size="70" value="<%= settings.getIndexString()%>"/>
<a href="helpPopup.view?topic=index" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.ignoredarticles")%></td><td><input type="text" name="ignoredArticles" size="70" value="<%= settings.getIgnoredArticles()%>"/>
<a href="helpPopup.view?topic=ignoredArticles" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.welcomemessage")%></td><td><input type="text" name="welcomeMessage" size="70" value="<%= settings.getWelcomeMessage()%>"/>
<a href="helpPopup.view?topic=welcomeMessage" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr><td><%=is.get("settings.language")%></td>
<td><select name="locale">
<%
    Locale[] locales = is.getAvailableLocales();
    Locale currentLocale = is.getLocale();
    for (int i = 0; i < locales.length; i++) {
        String language = locales[i].getDisplayLanguage(locales[i]);
        out.println("<option " + (currentLocale.equals(locales[i]) ? "selected" : "") + " value='" + i + "'>" + language + "</option>");
    }
%>
</select>
<a href="helpPopup.view?topic=language" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.coverartlimit")%></td><td><input type="text" name="coverArtLimit" size="8" value="<%= settings.getCoverArtLimit()%>"/>
<a href="helpPopup.view?topic=coverArtLimit" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td><%=is.get("settings.downloadlimit")%></td><td><input type="text" name="downloadLimit" size="8" value="<%= settings.getDownloadBitrateLimit()%>"/>
<a href="helpPopup.view?topic=downloadLimit" onclick="return popup(this, 'help')"><img src="icons/help_small.png" alt="<%=help%>" title="<%=help%>"></a></td></tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr><td align="center" colspan="2"><input type='submit' value='<%=is.get("settings.ok")%>'></td></tr>
</table>
</form>

<h2><%=is.get("settings.searchindex.title")%></h2>

<form method="post" action='indexSettingsConfirm.jsp'>
<table><tr>
<td><%=is.get("settings.searchindex.auto")%></td>
<td><select name="interval">
<%
    int[] intervals = {-1, 1, 2, 3, 7, 14, 30, 60};
    int selectedInterval = settings.getIndexCreationInterval();
    for (int i = 0; i < intervals.length; i++) {
        out.print("<option " + (intervals[i] == selectedInterval ? "selected " : "") + " value='" + intervals[i] + "'>");

        switch (intervals[i]) {
            case -1:
                out.print(is.get("settings.searchindex.interval.never"));
                break;
            case 1:
                out.print(is.get("settings.searchindex.interval.one"));
                break;
            default:
                out.print(is.get("settings.searchindex.interval.many", intervals[i]));
                break;
        }
        out.println("</option>");
    }
%>
</select>
&nbsp;
<select name="hour">
<%
    int selectedHour = settings.getIndexCreationHour();
    for (int hour = 0; hour < 24; hour++) {
        out.print("<option " + (hour == selectedHour? "selected " : "") + " value='" + hour + "'>" +
                is.get("settings.searchindex.hour", hour) + "</option>");
    }
%>
</select>
&nbsp;&nbsp;<input type='submit' value='<%=is.get("settings.ok")%>'></td></tr>
<tr><td colspan="2"><%=is.get("settings.searchindex.manual")%></td></tr>
</table>
</form>

 <h2><%=is.get("settings.user.title")%></h2>
 <p><%=is.get("settings.user.text")%></p>
 <table>
<%
     User[] users = ServiceFactory.getSecurityService().getAllUsers();
     for (int i = 0; i < users.length; i++) {
         User user = users[i];
         out.println("<tr><td><a href='userSettings.jsp?username=" + StringUtil.urlEncode(user.getUsername()) + "'>" + user + "</a></td></tr>");
     }
%>
 </table>

 </body></html>