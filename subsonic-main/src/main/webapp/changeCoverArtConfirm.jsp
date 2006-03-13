 <%--$Revision: 1.6 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.*"%>
 <%@ page import="net.sourceforge.subsonic.domain.*"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <%@ page import="net.sourceforge.subsonic.util.*"%>
 <%@ page import="java.io.*"%>
 <%@ page import="java.net.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%!
    private static final Logger LOG = Logger.getLogger("net.sourceforge.subsonic.jsp.changeCoverArtConfirm");

    private InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

 <%
     String path = request.getParameter("path");
     String url = request.getParameter("url");

     InputStream input = null;
     OutputStream output = null;

     try {
         input = new URL(url).openStream();

         // Attempt to resolve proper suffix.
         String suffix = "jpg";
         if (url.toLowerCase().endsWith(".gif")) {
             suffix = "gif";
         } else if (url.toLowerCase().endsWith(".png")) {
             suffix = "png";
         }

         // Check permissions.
         File newCoverFile = new File(path, "folder." + suffix);
         if (!ServiceFactory.getSecurityService().isWriteAllowed(newCoverFile)) {
             throw new Exception("Permission denied: " + StringUtil.toHtml(newCoverFile.getPath()));
         }

         // Write file.
         output = new FileOutputStream(newCoverFile);
         byte[] buf = new byte[8192];
         while (true) {
             int n = input.read(buf);
             if (n == -1) {
                 break;
             }
             output.write(buf, 0, n);
         }

         // Rename existing cover file if new cover file is not the preferred.
         try {
             File[] coverFiles = new MusicFile(path).getCoverArt(1);
             if (coverFiles.length > 0) {
                 if (!newCoverFile.equals(coverFiles[0])) {
                     coverFiles[0].renameTo(new File(coverFiles[0].getCanonicalPath() + ".old"));
                     LOG.info("Renamed old image file " + coverFiles[0]);
                 }
             }
         } catch (Exception x) {
             LOG.warn("Failed to rename existing cover file.", x);
         }

         response.sendRedirect("main.jsp?path=" + StringUtil.urlEncode(path));

     } catch (Exception x) {
         LOG.error("Failed to change cover art.", x);
         out.println("<p>" + is.get("changeCoverArtConfirm.failed", x.getMessage()) + "</p>");

     } finally {
         try { input.close(); } catch (Exception x) {/* Ignored */}
         try { output.close(); } catch (Exception x) {/* Ignored */}
     }

%>
</body></html>