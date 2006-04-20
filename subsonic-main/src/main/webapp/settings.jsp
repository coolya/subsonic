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

 <h2><a href="playerSettings.view?"><%=is.get("settings.player.title")%></a></h2>
 <h2><a href="musicFolderSettings.view?"><%=is.get("settings.musicfolder.title")%></a></h2>
 <h2><a href="internetRadioSettings.view?"><%=is.get("settings.radio.title")%></a></h2>
 <h2><a href="searchSettings.view?"><%=is.get("settings.searchindex.title")%></a></h2>


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