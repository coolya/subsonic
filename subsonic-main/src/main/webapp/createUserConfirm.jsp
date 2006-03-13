 <%--$Revision: 1.6 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <%
     SecurityService securityService = ServiceFactory.getSecurityService();
     InternationalizationService is = ServiceFactory.getInternationalizationService();

     String username = request.getParameter("username");
     String password = request.getParameter("password");
     String confirmPassword = request.getParameter("confirmPassword");
     boolean adminRole = request.getParameter("admin") != null;
     boolean downloadRole = request.getParameter("download") != null;
     boolean uploadRole = request.getParameter("upload") != null;
     boolean playlistRole = request.getParameter("playlist") != null;
     boolean coverArtRole = request.getParameter("coverart") != null;
     boolean commentRole = request.getParameter("comment") != null;

     if (username == null || username.length() == 0) {
         out.println("<p>" + is.get("createuserconfirm.nousername") + "</p>");
     } else {
         if (!password.equals(confirmPassword)) {
             out.println("<p>" + is.get("createuserconfirm.wrongpassword") + "</p>");
         } else if (password.length() == 0) {
             out.println("<p>" + is.get("createuserconfirm.nopassword", username) + "</p>");
         } else {
             User user = new User(username, password);
             user.setAdminRole(adminRole);
             user.setUploadRole(uploadRole);
             user.setDownloadRole(downloadRole);
             user.setPlaylistRole(playlistRole);
             user.setCoverArtRole(coverArtRole);
             user.setCommentRole(commentRole);
             securityService.createUser(user);
             response.sendRedirect("settings.jsp");
         }
     }
%>
 </body></html>

