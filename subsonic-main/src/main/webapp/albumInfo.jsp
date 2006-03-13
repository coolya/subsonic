 <%--$Revision: 1.9 $ $Date: 2006/03/01 17:23:15 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.*"%>
 <%@ page import="net.sourceforge.subsonic.domain.*"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <%@ page import="net.sourceforge.subsonic.util.*"%>
 <%@ page import="javax.servlet.jsp.*"%>
 <%@ page import="java.io.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head>

<%!

    private static final Logger LOG = Logger.getLogger("net.sourceforge.subsonic.jsp.albumInfo");

    private InternationalizationService is = ServiceFactory.getInternationalizationService();
    private AmazonSearchService amazonService = ServiceFactory.getAmazonSearchService();

    private CharSequence formatAlbumTitle(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        s.append(info.getAlbum());
        String[] formats = info.getFormats();
        for (int i = 0; i < formats.length; i++) {
            s.append(" [").append(formats[i]).append(']');
        }
        return s;
    }

    private CharSequence formatArtists(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        String[] artists = info.getArtists();
        for (int i = 0; i < artists.length; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(artists[i]);
        }
        return s;
    }

    private CharSequence formatReviews(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        String[] reviews = info.getEditorialReviews();
        for (int i = 0; i < reviews.length; i++) {
            if (i > 0) {
                s.append("</p>");
            }
            s.append(reviews[i]);
        }
        return s;
    }

    private void generateJavaScript(JspWriter out, CharSequence id, CharSequence content) throws IOException {
        if (content == null) {
            content = "";
        }
        String formattedContent = content.toString().replaceAll("'", "&#39;");
        formattedContent = formattedContent.replaceAll("\"", "&#34;");
        out.println("    document.getElementById('" + id + "').innerHTML = \"" + formattedContent + '\"');
    }

    private void generateJavaScriptForLink(JspWriter out, CharSequence id, CharSequence url) throws IOException {
        out.println("    document.getElementById('" + id + "').href = \"" + url + '\"');
    }

    private void generateJavaScriptForImageSource(JspWriter out, CharSequence id, CharSequence source) throws IOException {
        if (source == null) {
            source = "coverart?size=160";
        }
        out.println("    document.getElementById('" + id + "').src = \"" + source + '\"');
    }
%>

 <body onload="javascript:populate0()">
 <h1><%=is.get("albuminfo.title")%></h1>
 <%

     String path = request.getParameter("path");
     String artist = request.getParameter("artist");
     String album = request.getParameter("album");
     MusicFile dir = new MusicFile(path);

     MusicFile[] children = dir.getChildren(false);
     AmazonAlbumInfo[] infos = new AmazonAlbumInfo[0];

     MusicFile.MetaData metaData = children[0].getMetaData();
     if (artist == null) {
         artist = metaData.getArtist();
     }
     if (album == null) {
         album = metaData.getAlbum();
     }

     out.println("<a href='main.jsp?path=" + dir.urlEncode() + "'><b>[" + is.get("common.back") + "]</b></a>");
%>

 <form method="post" action="albumInfo.jsp">
     <input type="hidden" name="path" value="<%=path%>"/>

     <table><tr>
         <td><%=is.get("albuminfo.artist")%></td><td><input name="artist" type="text" value="<%=artist%>"/></td>
         <td style="padding-left:10pt"><%=is.get("albuminfo.album")%></td><td><input name="album" type="text" value="<%=album%>"/></td>
         <td><input type="submit" value="<%=is.get("albuminfo.search")%>"/></td>
     </tr></table>
 </form>

<%
     try {
         infos = amazonService.getAlbumInfo(artist, album);

         switch (infos.length) {
             case 0:
                 out.println("<p>" + is.get("albuminfo.hits.none") + "</p>");
                 break;
             case 1:
                 out.println("<p>" + is.get("albuminfo.hits.one") + "</p>");
                 break;
             default:
                 out.println("<p>" + is.get("albuminfo.hits.many", infos.length) + "</p>");
                 break;
         }

         out.println("<ol>");
         for (int i = 0; i < infos.length; i++) {
             out.println("<li><a href='javascript:populate" + i + "()'>" + formatAlbumTitle(infos[i]) + "</a></li>");
         }
         out.println("</ol>");

         out.println("<script type='text/javascript'>");
         for (int i = 0; i < infos.length; i++) {
             out.println("  function populate" + i + "() {");
             AmazonAlbumInfo info = infos[i];
             generateJavaScript(out, "artists", formatArtists(info));
             generateJavaScript(out, "released", info.getReleaseDate());
             generateJavaScript(out, "label", info.getLabel());
             generateJavaScript(out, "review", formatReviews(info));
             generateJavaScriptForLink(out, "buy", info.getDetailPageUrl());
             generateJavaScriptForImageSource(out, "image", info.getImageUrl());
             out.println("  }");
         }
         out.println("</script>");

     } catch (Exception x) {
         LOG.warn("Failed to search for album info at Amazon.com.", x);
     }

     if (infos.length > 0) {
%>

 <table>
     <tr><td rowspan="6"><img id="image" src="" alt="" hspace="15"/></td></tr>
     <tr><td><em><%=is.get("albuminfo.artist")%></em></td><td id="artists"></td></tr>
     <tr><td><em><%=is.get("albuminfo.released")%></em></td><td id="released"></td></tr>
     <tr><td><em><%=is.get("albuminfo.label")%></em></td><td id="label"></td></tr>
     <tr><td><em><%=is.get("albuminfo.review")%></em></td><td id="review"></td></tr>
     <tr><td colspan="2"><a id="buy" target="_blank"><b><%=is.get("albuminfo.amazon")%></b></a></td></tr>
 </table>

 <%
     }

     if (album != null && album.length() > 0) {

         String allMusicUrl = "<a target='_blank' href='allmusic.jsp?album=" + StringUtil.urlEncode(album) + "'>allmusic.com</a>";
         out.println("<br/><b>" + is.get("albuminfo.allmusic", album, allMusicUrl) + "</b>");

         String googleUrl = "<a target='_blank' href='http://www.google.com/musicsearch?q=";
         if (artist != null && artist.length() > 0) {
             googleUrl += StringUtil.urlEncode('"' + StringUtil.utfToLatin(artist) + '"');
         }
         googleUrl += StringUtil.urlEncode(" \"" + StringUtil.utfToLatin(album) + '"') + "'>Google Music</a>";
         out.println("<br/><b>" + is.get("albuminfo.google", album, googleUrl) + "</b>");
     }
 %>

 </body>
</html>