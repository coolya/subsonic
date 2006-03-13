<%--$Revision: 1.11 $ $Date: 2006/03/04 14:45:55 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.*"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="net.sourceforge.subsonic.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>
 <%!
     private static final Logger LOG = Logger.getLogger("net.sourceforge.subsonic.jsp.home");
     private static final int IMAGE_SIZE           =  110;
     private static final int DEFAULT_LIST_SIZE    =   10;
     private static final int MAX_LIST_SIZE        =  500;
     private static final int DEFAULT_LIST_OFFSET  =    0;
     private static final int MAX_LIST_OFFSET      = 5000;
     private static final int ALBUMS_PER_ROW       =    5;

     private InternationalizationService is = ServiceFactory.getInternationalizationService();
     private MusicInfoService musicInfoService = ServiceFactory.getMusicInfoService();
     private SearchService searchService = ServiceFactory.getSearchService();
     private SettingsService settingsService = ServiceFactory.getSettingsService();
 %>
<%
    int listSize = DEFAULT_LIST_SIZE;
    int listOffset = DEFAULT_LIST_OFFSET;
    if (request.getParameter("listSize") != null) {
        listSize = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listSize")), MAX_LIST_SIZE));
    }
    if (request.getParameter("listOffset") != null) {
        listOffset = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listOffset")), MAX_LIST_OFFSET));
    }

    String listType = request.getParameter("listType");
    if (listType == null) {
        listType = "random";
    }

    MusicFile[] files = null;
    MusicFileInfo[] fileInfos = null;
    boolean showRating = false;
    boolean showPlayCount = false;
    boolean showLastPlayed = false;
    boolean showLastModified = false;

    if ("highest".equals(listType)) {
        fileInfos = musicInfoService.getHighestRated(listOffset, listSize);
        showRating = true;
    } else if ("frequent".equals(listType)) {
        fileInfos = musicInfoService.getMostFrequentlyPlayed(listOffset, listSize);
        showPlayCount = true;
    } else if ("recent".equals(listType)) {
        fileInfos = musicInfoService.getMostRecentlyPlayed(listOffset, listSize);
        showLastPlayed = true;
    } else if ("newest".equals(listType)) {
        try {
            files = (MusicFile[]) ServiceFactory.getSearchService().getNewestAlbums(listOffset, listSize).toArray(new MusicFile[0]);
        } catch (Exception x) {
            files = new MusicFile[0];
            LOG.warn("Failed to get list of newest albums.", x);
        }
        showLastModified = true;
    } else {
        try {
            files = (MusicFile[]) ServiceFactory.getSearchService().getRandomMusicFiles(listSize).toArray(new MusicFile[0]);
        } catch (Exception x) {
            files = new MusicFile[0];
            LOG.warn("Failed to get list of random songs.", x);
        }
    }

    if (files == null) {
        List musicFiles = new ArrayList();
        for (int i = 0; i < fileInfos.length; i++) {
            try {
                musicFiles.add(new MusicFile(fileInfos[i].getPath()));
            } catch (Exception x) {
                LOG.warn("Failed to create album list entry for " + fileInfos[i].getPath(), x);
                musicFiles.add(null);
            }
        }
        files = (MusicFile[]) musicFiles.toArray(new MusicFile[0]);
    }
%>

 <h1><%=settingsService.getWelcomeMessage()%></h1>
 <h2>
     <a href="home.jsp?listSize=<%=listSize%>&listType=random"><%=is.get("home.random.title")%></a> |
     <a href="home.jsp?listSize=<%=listSize%>&listType=newest"><%=is.get("home.newest.title")%></a> |
     <a href="home.jsp?listSize=<%=listSize%>&listType=highest"><%=is.get("home.highest.title")%></a> |
     <a href="home.jsp?listSize=<%=listSize%>&listType=frequent"><%=is.get("home.frequent.title")%></a> |
     <a href="home.jsp?listSize=<%=listSize%>&listType=recent"><%=is.get("home.recent.title")%></a>
 </h2>

<%
    MusicFolder[] folders = settingsService.getAllMusicFolders();

    if (!searchService.isIndexCreated() && folders.length > 0) {
        searchService.createIndex();
    }

    if (searchService.isIndexBeingCreated()) {
        out.println("<p style=\"color:red\">" + is.get("home.scan") + "</p>");
    }
%>

<div style='color:dimgray'><b><%=is.get("home." + listType + ".text")%></b></div>
 <%=getAlbumList(files, fileInfos, showPlayCount, showLastPlayed, showRating, showLastModified)%>

<table><tr>
    <td style="padding-right:7pt"><select name="listSize" onchange="location='home.jsp?listType=<%=listType%>&listOffset=<%=listOffset%>&listSize=' + options[selectedIndex].value;">
<%
    int[] sizes = {5, 10, 15, 20, 30, 40, 50};
    for (int i = 0; i < sizes.length; i++) {
        int size = sizes[i];
        out.println("<option " + ((listSize == size) ? "selected" : "") + " value='" + size + "'>" + is.get("home.listsize", size) + "</option>");
    }
%>
    </select></td>

<% if ("random".equals(listType)) { %>
    <td style="padding-right:7pt"><a href="home.jsp?listType=random&listSize=<%=listSize%>">[<%=is.get("common.more")%>]</a></td>
<% } else { %>
    <td style="padding-right:7pt"><%=is.get("home.albums", listOffset + 1, listOffset + listSize)%></td>
    <td style="padding-right:7pt"><a href="home.jsp?listType=<%=listType%>&listOffset=<%=listOffset - listSize%>&listSize=<%=listSize%>">[<%=is.get("common.previous")%>]</a></td>
    <td><a href="home.jsp?listType=<%=listType%>&listOffset=<%=listOffset + listSize%>&listSize=<%=listSize%>">[<%=is.get("common.next")%>]</a></td>
<% } %>
</table>
</body></html>

<%!
     private CharSequence getAlbumList(MusicFile[] files, MusicFileInfo[] fileInfos, boolean showPlayCount,
                                       boolean showLastPlayed, boolean showRating, boolean showLastModified) throws IOException {
        StringBuffer buf = new StringBuffer();
        buf.append("<table>\n");
        for (int i = 0; i < files.length; i++) {
            MusicFile file = files[i];
            if (file == null || !file.exists()) {
                continue;
            }

            MusicFile album = file;
            if (file.isFile()) {
                album = album.getParent();
            }
            if (album.isRoot()) {
                continue;
            }

            if (i % ALBUMS_PER_ROW == 0) {
                buf.append("<tr>\n");
            }
            File[] coverArt = album.getCoverArt(1);
            String coverArtPath = (coverArt.length == 0) ? "" : "&path=" + StringUtil.urlEncode(coverArt[0].getPath());
            buf.append("<td style='vertical-align:top'><table><tr><td><a href='main.jsp?path=" + album.urlEncode() +"'>" +
                        "<img height='" + IMAGE_SIZE + "' width='" + IMAGE_SIZE + "' hspace='2' vspace='2' " +
                        "src='coverart?IMAGE_SIZE=" + IMAGE_SIZE + coverArtPath + "'/></a></td></tr>");
            buf.append("<tr><td>");
            if (showPlayCount) {
                buf.append("<div style='color:dimgray;font-size:8pt'>" + is.get("home.playcount", fileInfos[i].getPlayCount()) + "</div>");
            } else if (showLastPlayed) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, is.getLocale());
                buf.append("<div style='color:dimgray;font-size:8pt'>" + is.get("home.lastplayed", dateFormat.format(fileInfos[i].getLastPlayed())) + "</div>");
            } else if (showRating) {
                buf.append("<div><img src='icons/rating" + fileInfos[i].getRating() + ".gif'/></div>");
            } else if (showLastModified) {
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, is.getLocale());
                buf.append("<div style='color:dimgray;font-size:8pt'>" + is.get("home.lastmodified", dateFormat.format(new Date(album.lastModified()))) + "</div>");
            }

            buf.append(getArtistAndAlbum(file));
            buf.append("</td></tr></table></td>\n");

            if ((i + 1) % ALBUMS_PER_ROW == 0) {
                buf.append("</tr>\n");
            }
        }
        buf.append("</table>\n");

        return buf;
    }
%>

 <%!
    private CharSequence getArtistAndAlbum(MusicFile file) throws IOException {

        if (file.isDirectory()) {
            MusicFile[] children = file.getChildren(false);
            if (children.length == 0) {
                return is.get("common.unknown");
            }
            file = children[0];
        }

        String artist = file.getMetaData().getArtist();
        String album  = file.getMetaData().getAlbum();

        if ("".equals(artist)) { artist = null; }
        if ("".equals(album)) { album = null; }

        StringBuffer buf = new StringBuffer();

        if (artist != null) {
            if (artist.length() > 20) {
                artist = artist.substring(0, 17) + "...";
            }
            buf.append("<em>" + StringUtil.toHtml(artist) + "</em>");
        }

        if (artist != null && album != null) {
            buf.append("<br/>");
        }

        if (album != null) {
            if (album.length() > 20) {
                album = album.substring(0, 17) + "...";
            }
            buf.append(StringUtil.toHtml(album));
        }

        return buf;
    }
%>

