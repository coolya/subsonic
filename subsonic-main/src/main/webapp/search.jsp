 <%--$Revision: 1.18 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.util.*,
                 java.util.regex.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    String query = request.getParameter("query");

    boolean includeTitle = request.getParameter("includeTitle") != null || query == null;
    boolean includeArtistAndAlbum = request.getParameter("includeArtistAndAlbum") != null || query == null;

    if ("".equals(query)) {
        query = null;
    }

    String time = request.getParameter("time");
    long millis = 0;
    long now = System.currentTimeMillis();
    final long MILLIS_IN_DAY = 24 * 3600 * 1000;

    if ("1d".equals(time)) {
        millis = now - 1 * MILLIS_IN_DAY;
    } else if ("1w".equals(time)) {
        millis = now - 7 * MILLIS_IN_DAY;
    } else if ("2w".equals(time)) {
        millis = now - 14 * MILLIS_IN_DAY;
    } else if ("1m".equals(time)) {
        millis = now - 30 * MILLIS_IN_DAY;
    } else if ("3m".equals(time)) {
        millis = now - 90 * MILLIS_IN_DAY;
    } else if ("6m".equals(time)) {
        millis = now - 180 * MILLIS_IN_DAY;
    } else if ("1y".equals(time)) {
        millis = now - 365 * MILLIS_IN_DAY;
    }
%>

<h1><%=is.get("search.title")%></h1>

<form method="post" action="search.jsp">
<p>
<input type="text" name="query" size="55" value="<%= query == null ? "" : query.replaceAll("\"", "&quot;")%>"/>
<input type="submit" value="<%=is.get("search.search")%>"/>
</p>
<input type="checkbox" <%=includeTitle ? "checked" : ""%> name="includeTitle" id="includeTitle"/>
<label for="includeTitle"><%=is.get("search.include.title")%></label> |
<input type="checkbox" <%=includeArtistAndAlbum ? "checked" : ""%> name="includeArtistAndAlbum" id="includeArtistAndAlbum"/>
<label for="includeArtistAndAlbum"><%=is.get("search.include.artistandalbum")%></label> |
<label for="time"><%=is.get("search.newer")%></label>
<select style="vertical-align:middle" name="time" id="time">
<option value="0"><%=is.get("search.select")%></option>
<option <%="1d".equals(time) ? "selected" : ""%> value="1d">1 <%=is.get("search.day")%></option>
<option <%="1w".equals(time) ? "selected" : ""%> value="1w">1 <%=is.get("search.week")%></option>
<option <%="2w".equals(time) ? "selected" : ""%> value="2w">2 <%=is.get("search.weeks")%></option>
<option <%="1m".equals(time) ? "selected" : ""%> value="1m">1 <%=is.get("search.month")%></option>
<option <%="3m".equals(time) ? "selected" : ""%> value="3m">3 <%=is.get("search.months")%></option>
<option <%="6m".equals(time) ? "selected" : ""%> value="6m">6 <%=is.get("search.months")%></option>
<option <%="1y".equals(time) ? "selected" : ""%> value="1y">1 <%=is.get("search.year")%></option>
</select>
</form>

<%
    if (query == null && millis == 0) {
        return;
    }

    SearchService searchService = ServiceFactory.getSearchService();

    if (!searchService.isIndexCreated()) {
        searchService.createIndex();
    }

    if (searchService.isIndexBeingCreated()) {
        out.println("<p style=\"color:red\">" + is.get("search.index") + "</p>");
        return;
    }

    final int MAX_HITS = 100;
    List result = searchService.heuristicSearch(query, MAX_HITS, includeArtistAndAlbum, includeArtistAndAlbum, includeTitle, new Date(millis));
    String[] criteria = searchService.splitQuery(query);

    if (result.size() == MAX_HITS) {
        out.println("<p>" + is.get("search.hits.max", MAX_HITS) + "</p>");
    } else if (result.isEmpty()) {
        out.println("<p>" + is.get("search.hits.none") + "</p>");
    } else if (result.size() == 1) {
        out.println("<p>" + is.get("search.hits.one") + "</p>");
    } else {
        out.println("<p>" + is.get("search.hits.many", result.size()) + "</p>");
    }

    User user = ServiceFactory.getSecurityService().getCurrentUser(request);

    String play = is.get("common.play");
    String add = is.get("common.add");
    String download = is.get("common.download");

    out.println("<table valign='top' border='0'>");
    for (int i = 0; i < result.size(); i++) {
        MusicFile file = (MusicFile) result.get(i);

        out.print("<tr>");
        out.print("<td><a target='playlist' href='playlist.jsp?play=" + file.urlEncode() +
                "'><img width='13' height='13' src='icons/play.gif' alt='" + play + "' title='" + play + "'/></a></td>");
        out.print("<td><a target='playlist' href='playlist.jsp?add=" + file.urlEncode() +
                "'><img width='13' height='13' src='icons/add.gif' alt='" + add + "' title='" + add + "'/></a></td>");

        if (user.isDownloadRole()) {
            out.print("<td><a href='download?path=" + file.urlEncode() +
                      "'><img width='13' height='13' src='icons/download.gif' alt='" + download + "' title='" + download + "'/></a></td>");
        }

        String style = "style='padding-left:5;padding-right:5" + (i % 2 == 0 ? ";background-color:#DEE3E7'" : "'");

        out.print("<td " + style + '>' + adorn(file.getTitle(), criteria) + "</td>");
        out.println("<td " + style + "><a target='main' href='main.jsp?path=" + file.getParent().urlEncode() + "'>" +
                    getArtistAlbumYear(file.getMetaData(), criteria) + "</td></tr>");
    }
    out.println("</table>");

%>

 </body></html>

<%!
    private CharSequence getArtistAlbumYear(MusicFile.MetaData metaData, String[] criteria) {

        String artist = metaData.getArtist();
        String album  = metaData.getAlbum();
        String year   = metaData.getYear();

        if ("".equals(artist)) { artist = null; }
        if ("".equals(album)) { album = null; }
        if ("".equals(year)) { year = null; }

        StringBuffer buf = new StringBuffer();

        if (artist != null) {
            buf.append("<em>" + adorn(artist, criteria) + "</em>");
        }

        if (artist != null && album != null) {
            buf.append(" - ");
        }

        if (album != null) {
            buf.append(adorn(album, criteria));
        }

        if (year != null) {
            buf.append(" (" + StringUtil.toHtml(year) + ")");
        }

        return buf;
    }
%>

<%!
    private String adorn(String text, String[] criteria) {
        text = StringUtil.toHtml(text);
        if (criteria.length == 0) {
            return text;
        }

        StringBuffer regexp = new StringBuffer();
        for (int i = 0; i < criteria.length; i++) {
            regexp.append(StringUtil.toHtml(criteria[i]));
            if (i < criteria.length - 1) {
                regexp.append("|");
            }
        }

        try {
            Pattern pattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            StringBuffer buf = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buf, "<font color='red'>$0</font>");
            }
            matcher.appendTail(buf);

            return buf.toString();
        } catch (Exception x) {
            return text;
        }
    }
%>

