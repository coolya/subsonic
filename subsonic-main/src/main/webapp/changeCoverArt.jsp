 <%--$Revision: 1.7 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.*"%>
 <%@ page import="net.sourceforge.subsonic.domain.*"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%!
    private static final Logger LOG = Logger.getLogger("net.sourceforge.subsonic.jsp.changeCoverArt");

    private InternationalizationService is = ServiceFactory.getInternationalizationService();
    private AmazonSearchService amazonService = ServiceFactory.getAmazonSearchService();
%>

 <%
     String path = request.getParameter("path");
     String artist = request.getParameter("artist");
     String album = request.getParameter("album");
     MusicFile dir = new MusicFile(path);

     MusicFile[] children = dir.getChildren(false);
     String[] amazonUrls = new String[0];
     if (children.length > 0) {
         try {
             MusicFile.MetaData metaData = children[0].getMetaData();
             if (artist == null) {
                 artist = metaData.getArtist();
             }
             if (album == null) {
                 album = metaData.getAlbum();
             }

             amazonUrls = amazonService.getCoverArtImages(artist, album);
         } catch (Exception x) {
             LOG.warn("Failed to search for cover images at Amazon.com.", x);
         }
     }
%>

 <h1><%=is.get("changecoverart.title")%></h1>
 <a href="main.jsp?path=<%=dir.urlEncode()%>"><b>[<%=is.get("common.back")%>]</b></a>

 <form method="post" action="changeCoverArt.jsp">
     <input type="hidden" name="path" value="<%=path%>"/>

     <table><tr>
         <td><%=is.get("changecoverart.artist")%></td><td><input name="artist" type="text" value="<%=artist%>"/></td>
         <td style="padding-left:10pt"><%=is.get("changecoverart.album")%></td><td><input name="album" type="text" value="<%=album%>"/></td>
         <td><input type="submit" value="<%=is.get("changecoverart.search")%>"/></td>
     </tr></table>
 </form>

 <p><%=is.get("changecoverart.text")%></p>

 <form method="post" action="changeCoverArtConfirm.jsp">
     <table><tr>
         <td><input type="hidden" name="path" value="<%=path%>"/></td>
         <td><label for="url"><%=is.get("changecoverart.address")%></label></td>
         <td><input type="text" name="url" size="40" id="url" value="http://"/></td>
         <td><input type='submit' value='<%=is.get("common.ok")%>'></td>
     </tr></table>
 </form>

 <%
     switch (amazonUrls.length) {
         case 0:
             out.println("<h2>" + is.get("changecoverart.hits.none") + "</h2>");
             break;
         case 1:
             out.println("<h2>" + is.get("changecoverart.hits.one") + "</h2>");
             break;
         default:
             out.println("<h2>" + is.get("changecoverart.hits.many", amazonUrls.length) + "</h2>");
             break;
     }

    for (int i = 0; i < amazonUrls.length; i++) {
         out.println("<a href='changeCoverArtConfirm.jsp?path=" + dir.urlEncode() + "&url=" + amazonUrls[i] + "'><img src='" +
                     amazonUrls[i] + "' hspace='5' vspace='5'/></a>");
     }
 %>
</body></html>