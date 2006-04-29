 <%--$Revision: 1.31 $ $Date: 2006/03/01 23:37:36 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>


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
 <h2><a href="userSettings.view?"><%=is.get("settings.user.title")%></a></h2>
 <h2><a href="generalSettings.view?">GENERAL</a></h2>

</body></html>