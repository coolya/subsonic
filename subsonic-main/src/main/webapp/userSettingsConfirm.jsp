 <%--$Revision: 1.7 $ $Date: 2006/02/28 22:38:17 $--%>
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

    String action = request.getParameter("action");
    String username = request.getParameter("username");

    if ("delete".equals(action)) {
        securityService.deleteUser(username);
        response.sendRedirect("settings.jsp");
    }

    else if ("role".equals(action)) {
        boolean admin = request.getParameter("admin") != null;
        boolean download = request.getParameter("download") != null;
        boolean upload = request.getParameter("upload") != null;
        boolean playlist = request.getParameter("playlist") != null;
        boolean coverArt = request.getParameter("coverart") != null;
        boolean comment = request.getParameter("comment") != null;
        User user = securityService.getUserByName(username);
        user.setAdminRole(admin);
        user.setDownloadRole(download);
        user.setUploadRole(upload);
        user.setPlaylistRole(playlist);
        user.setCoverArtRole(coverArt);
        user.setCommentRole(comment);
        securityService.updateUser(user);
        response.sendRedirect("settings.jsp");
    }

    else if ("password".equals(action)) {
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            out.println("<p>" + is.get("usersettingsconfirm.wrongpassword", username) + "</p>");
        } else if (newPassword.length() == 0) {
            out.println("<p>" + is.get("usersettingsconfirm.nopassword", username) + "</p>");
        } else {
            User user = securityService.getUserByName(username);
            user.setPassword(newPassword);
            securityService.updateUser(user);
            response.sendRedirect("settings.jsp");
        }
    }
%>

 </body></html>

