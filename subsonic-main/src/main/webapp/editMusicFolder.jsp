 <%--$Revision: 1.3 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <%@ page import="java.io.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <%
     InternationalizationService is = ServiceFactory.getInternationalizationService();

     String id = request.getParameter("id");
     String path = request.getParameter("path");
     String name = request.getParameter("name");
     boolean enabled = request.getParameter("enabled") != null;
     boolean create = request.getParameter("create") != null;
     boolean delete = request.getParameter("delete") != null;

     SettingsService settings = ServiceFactory.getSettingsService();
     if (delete) {
         settings.deleteMusicFolder(new Integer(id));
     } else {

         if (path.length() == 0) {
             out.println("<p>" + is.get("editmusicfolder.nopath") + "</p>");
         } else {
             File file = new File(path);
             if (name.length() == 0) {
                 name = file.getName();
             }

             if (create) {
                 settings.createMusicFolder(new MusicFolder(file, name, enabled));
             } else {
                 settings.updateMusicFolder(new MusicFolder(new Integer(id), file, name, enabled));

             }
         }
     }
%>

 <script language="javascript">parent.frames.top.location.href="top.jsp?"</script>
 <script language="javascript">parent.frames.left.location.href="left.jsp?"</script>
 <script language="javascript">parent.frames.main.location.href="settings.jsp?"</script>

 </body></html>
