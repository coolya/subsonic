<%--$Revision: 1.3 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/xml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="net.sourceforge.subsonic.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>

<%!
    private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
%>
<%
    PlaylistService playlistService = ServiceFactory.getPlaylistService();

    String url = request.getRequestURL().toString();
    File[] playlists = playlistService.getSavedPlaylists();
%>

<rss version="2.0">
    <channel>
        <title>Subsonic Podcast</title>
        <link><%=url%></link>
        <description>Subsonic Podcast</description>
        <language>en-us</language>
        <image>
            <url>http://subsonic.sourceforge.net/images/subsonic.jpg</url>
            <title>Subsonic Podcast</title>
        </image>
<%
    for (int i = 0; i < playlists.length; i++) {

        String name = StringUtil.removeSuffix(playlists[i].getName());
        String encodedName = StringUtil.urlEncode(playlists[i].getName());
        String pubDate = RSS_DATE_FORMAT.format(new Date(playlists[i].lastModified()));

        // Resolve content type.
        Playlist playlist = new Playlist();
        playlistService.loadPlaylist(playlist, playlists[i].getName());
        String suffix = playlist.getSuffix();
        String type = StringUtil.getMimeType(suffix);
        long length = playlist.length();
        String enclosureUrl = url.replaceFirst("/podcast.*", "/stream?playlist=" + encodedName + "&amp;suffix=" + suffix);
%>
        <item>
            <title><%=name%></title>
            <link><%=url%></link>
            <description>Subsonic playlist "<%=name%>"</description>
            <pubDate><%=pubDate%></pubDate>
            <enclosure url="<%=enclosureUrl%>" length="<%=length%>" type="<%=type%>"/>
        </item>
<%
    }
%>

    </channel>
</rss>
