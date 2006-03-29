 <%--$Revision: 1.5 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <%
     InternationalizationService is = ServiceFactory.getInternationalizationService();

     String id = request.getParameter("id");
     String streamUrl = request.getParameter("streamUrl");
     String homepageUrl = request.getParameter("homepageUrl");
     String name = request.getParameter("name");
     boolean enabled = request.getParameter("enabled") != null;
     boolean create = request.getParameter("create") != null;
     boolean delete = request.getParameter("delete") != null;

     SettingsService settings = ServiceFactory.getSettingsService();
     if (delete) {
         settings.deleteInternetRadio(new Integer(id));
     } else {

         if (name.length() == 0) {
             out.println("<p>" + is.get("editinternetradio.noname") + "</p>");
         } else if (streamUrl.length() == 0) {
             out.println("<p>" + is.get("editinternetradio.nourl") + "</p>");
         } else {
             if (create) {
                 settings.createInternetRadio(new InternetRadio(name, streamUrl, homepageUrl, enabled));
             } else {
                 settings.updateInternetRadio(new InternetRadio(new Integer(id), name, streamUrl, homepageUrl, enabled));

             }
         }
     }
%>

 <script language="javascript">parent.frames.left.location.href="left.view?"</script>
 <script language="javascript">parent.frames.main.location.href="settings.jsp?"</script>
 </body></html>
