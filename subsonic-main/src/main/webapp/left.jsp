 <%--$Revision: 1.19 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.util.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head>
 <body style="background-color:#DEE3E7">

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    SettingsService settings = ServiceFactory.getSettingsService();

    MediaLibraryStatistics statistics = ServiceFactory.getSearchService().getStatistics();
    if (statistics != null) {
        long bytes = statistics.getTotalLengthInBytes();
        long hours = bytes * 8 / 1024 / 150 / 3600;

        String text = is.get("left.statistics", new Object[]{new Integer(statistics.getArtistCount()), new Integer(statistics.getAlbumCount()),
                                                             new Integer(statistics.getSongCount()), StringUtil.formatBytes(bytes, is.getLocale()),
                                                             new Long(hours)});
        out.println("<p style='font-size:8pt; white-space:nowrap'><em>" + text + "</em></p>");
    }

    int musicFolderIndex = -1;
    if (request.getParameter("musicFolderIndex") != null) {
        musicFolderIndex = Integer.parseInt(request.getParameter("musicFolderIndex"));
    }

    MusicFolder[] allFolders = settings.getAllMusicFolders();
    if (allFolders.length > 1) {
%>

 <select name="musicFolderIndex" style="width:100%" onchange="location='left.jsp?musicFolderIndex=' + options[selectedIndex].value;" >
     <option value="-1"><%=is.get("left.allfolders")%></option>
<%
    for (int i = 0; i < allFolders.length; i++) {
        String name = allFolders[i].getName();
        String selected = (i == musicFolderIndex) ? "selected" : "";
        out.println("<option " + selected + " value='" + i + "'>" + name + "</option>");
    }
%>
 </select>
<%
    }

    String play = is.get("common.play");
    String add = is.get("common.add");
    String download = is.get("common.download");

    InternetRadio[] radios = settings.getAllInternetRadios();
    if (radios.length > 0) {
        out.println("<h2>" + is.get("left.radio") + "</h2>");
    }

    for (int i = 0; i < radios.length; i++) {
        InternetRadio radio = radios[i];
        String homepage = radio.getHomepageUrl();
        out.print("<p class='dense'><a target='hidden' href='playRadio.jsp?id=" + radio.getId() +
                  "'><img width='13' height='13' src='icons/play.gif' alt='" + play + "' title='" + play + "'/></a> ");
        if (homepage != null && homepage.length() > 0) {
            out.println("<a target='main' href='" + homepage + "'>" + StringUtil.toHtml(radio.getName()) + "</a>");
        } else {
            out.println(StringUtil.toHtml(radio.getName()));
        }


    }

    MusicFolder[] foldersToShow = allFolders;
    if (musicFolderIndex >= 0) {
        foldersToShow = new MusicFolder[] {allFolders[musicFolderIndex]};
    }

    String indexString = settings.getIndexString();
    String[] ignoredArticles = settings.getIgnoredArticlesAsArray();
    Map children = MusicIndex.getIndexedChildren(foldersToShow, MusicIndex.createIndexesFromExpression(indexString), ignoredArticles);

    User user = ServiceFactory.getSecurityService().getCurrentUser(request);

    for (Iterator i = children.keySet().iterator(); i.hasNext();) {
        MusicIndex index = (MusicIndex) i.next();
        out.println("<a name='" + index.getIndex() + "'/><h2>" + index.getIndex() + "</h2>");
        List list = (List) children.get(index);

        for (Iterator j = list.iterator(); j.hasNext();) {
            MusicFile child = (MusicFile) j.next();

            String param = "path=" + child.urlEncode();

            out.print("<p class='dense'><a target='playlist' href='playlist.jsp?play=" + child.urlEncode() +
                    "'><img width='13' height='13' src='icons/play.gif' alt='" + play + "' title='" + play + "'/></a> ");
            out.print("<a target='playlist' href='playlist.jsp?add=" + child.urlEncode() +
                    "'><img width='13' height='13' src='icons/add.gif' alt='" + add + "' title='" + add + "'/></a> ");

            if (user.isDownloadRole()) {
                out.print("<a href='download?path=" + child.urlEncode() +
                        "'><img width='13' height='13' src='icons/download.gif' alt='" + download + "' title='" + download + "'/></a> ");
            }

            if (child.isDirectory()) {
                out.println("<a target='main' href='main.jsp?" + param + "'>" + StringUtil.toHtml(child.getName()) + "</a></p>");
            }
            else {
                out.println(StringUtil.toHtml(child.getTitle()) + "</p>");
            }
        }
    }
%>
</body></html>