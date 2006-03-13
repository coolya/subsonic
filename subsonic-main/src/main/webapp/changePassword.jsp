 <%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <p>
 <%
     SecurityService securityService = ServiceFactory.getSecurityService();
     InternationalizationService is = ServiceFactory.getInternationalizationService();

     String oldPassword = request.getParameter("oldPassword");
     String newPassword = request.getParameter("newPassword");
     String confirmPassword = request.getParameter("confirmPassword");

     User user = securityService.getCurrentUser(request);

     if (!user.getPassword().equals(oldPassword)) {
         out.println(is.get("changepassword.error.oldpassword", user.getUsername()));
     }


     else if (!newPassword.equals(confirmPassword)) {
         out.println(is.get("changepassword.error.newpassword"));
     }

     else if (newPassword.length() == 0) {
         out.println(is.get("changepassword.error.nopassword"));
     }

     else {
         user.setPassword(newPassword);
         securityService.updateUser(user);
         out.println(is.get("changepassword.ok", user.getUsername()));
     }
%>
 </p>

 <a href="restrictedSettings.jsp">[<%=is.get("common.back")%>]</a>

 </body></html>
