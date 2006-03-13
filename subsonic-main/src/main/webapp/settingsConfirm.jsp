 <%--$Revision: 1.12 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    String playlistFolder = request.getParameter("playlistFolder");
    String musicMask = request.getParameter("musicMask");
    String coverArtMask = request.getParameter("coverArtMask");
    String index = request.getParameter("index");
    String ignoredArticles = request.getParameter("ignoredArticles");
    String welcomeMessage = request.getParameter("welcomeMessage");
    String coverArtLimit = request.getParameter("coverArtLimit");
    int localeIndex = Integer.parseInt(request.getParameter("locale"));


    SettingsService settings = ServiceFactory.getSettingsService();
    settings.setIndexString(index);
    settings.setIgnoredArticles(ignoredArticles);
    settings.setPlaylistFolder(playlistFolder);
    settings.setMusicMask(musicMask);
    settings.setCoverArtMask(coverArtMask);
    settings.setWelcomeMessage(welcomeMessage);
    try {
        settings.setCoverArtLimit(Integer.parseInt(coverArtLimit));
    } catch (NumberFormatException x) { /* Intentionally ignored. */ }
    settings.save();

    InternationalizationService is = ServiceFactory.getInternationalizationService();
    is.setLocale(is.getAvailableLocales()[localeIndex]);
%>

<script language="javascript">parent.location.href="index.jsp"</script>

</body></html>