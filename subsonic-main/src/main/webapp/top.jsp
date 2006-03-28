<%--$Revision: 1.28 $ $Date: 2006/03/01 18:58:36 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
     <!--[if gte IE 5.5000]>
     <script type="text/javascript" src="pngfix.js"></script>
     <![endif]-->
 </head>
 <body style="background-color:#DEE3E7">
<%
    SettingsService settingsService = ServiceFactory.getSettingsService();
    InternationalizationService is = ServiceFactory.getInternationalizationService();

    String home = is.get("top.home");
    String nowPlaying = is.get("top.now_playing");
    String settings = is.get("top.settings");
    String status = is.get("top.status");
    String more = is.get("top.more");
    String help = is.get("top.help");
    String search = is.get("top.search");
%>

<table><tr valign='middle'>
<td style='font-size:16pt'><a href='help.view?' target='main'><img src='icons/logo.gif' alt='' title='<%=help%>'/></a>&nbsp;Subsonic</td>
<td style='padding-left:20pt; padding-right:20pt'>

<%
    MusicFolder[] folders = settingsService.getAllMusicFolders();
    if (folders.length > 0) {
        String indexString = settingsService.getIndexString();
        String[] ignoredArticles = settingsService.getIgnoredArticlesAsArray();

        Map children = MusicIndex.getIndexedChildren(folders, MusicIndex.createIndexesFromExpression(indexString), ignoredArticles);

        int counter = 0;
        int indexesPerLine = (int) Math.ceil(children.size() / 3.0);

        for (Iterator i = children.keySet().iterator(); i.hasNext();) {
            MusicIndex index = (MusicIndex) i.next();
            out.print("<a style=\"cursor:pointer;\" onclick=\"javascript:parent.frames.left.location.hash='" + index.getIndex() + "'\">" + index.getIndex() + "</a>");
            out.print((++counter % indexesPerLine == 0) ? " " : "&nbsp;");
        }
    } else {
        out.print("<p style='color:red'>" + is.get("top.missing") + "</p>");
    }

%>
</td>
<td>
<table><tr align="middle">
<td style="width:40pt;padding-right:10pt"><a href="home.jsp?" target="main"><img src="icons/home.png" title="<%=home%>" alt="<%=home%>"/><br/><%=home%></a></td>
<td style="width:40pt;padding-right:10pt"><a href="nowPlaying.jsp?" target="main"><img src="icons/now_playing.png" title="<%=nowPlaying%>" alt="<%=nowPlaying%>"/><br/><%=nowPlaying%></a></td>
<td style="width:40pt;padding-right:10pt"><a href="settings.jsp?" target="main"><img src="icons/settings.png" title="<%=settings%>" alt="<%=settings%>"/><br/><%=settings%></a></td>
<td style="width:40pt;padding-right:10pt"><a href="status.jsp?" target="main"><img src="icons/status.png" title="<%=status%>" alt="<%=status%>"/><br/><%=status%></a></td>
<td style="width:40pt;padding-right:10pt"><a href="more.jsp?" target="main"><img src="icons/more.png" title="<%=more%>" alt="<%=more%>"/><br/><%=more%></a></td>
<td style="width:40pt;padding-right:10pt"><a href="help.view?" target="main"><img src="icons/help.png" title="<%=help%>" alt="<%=help%>"/><br/><%=help%></a></td>

<td style="padding-left:15pt">
<table><tr>
<form method="post" action="search.jsp" target="main">
<td><input type="text" name="query" size="14"/></td><td><input type="image" src="icons/search_small.png" alt="<%=search%>" title="<%=search%>"/></td>
<input type="hidden" name="includeTitle" value="on"/>
<input type="hidden" name="includeArtistAndAlbum" value="on"/>
</form>
</tr></table>

</td>

<%
    VersionService versionService = ServiceFactory.getVersionService();
    if (versionService.isNewVersionAvailable()) {
        String message = is.get("top.upgrade", versionService.getLatestVersion().toString());
        out.println("<td style='padding-left:15pt'><p style='color:red; white-space:nowrap;'>" + message  + "</p></td>");
    }
%>
</tr></table>
</td>

</tr></table>
</body></html>