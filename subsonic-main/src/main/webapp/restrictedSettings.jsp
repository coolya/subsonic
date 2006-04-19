 <%--$Revision: 1.6 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
     <script language="javascript" type="text/javascript" src="scripts.js"></script>
 </head><body>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    User currentUser = ServiceFactory.getSecurityService().getCurrentUser(request);
    String username = currentUser.getUsername();
%>

 <h2><%=is.get("settings.player.title")%></h2>
 <p><%=is.get("settings.player.text")%></p>
 <table>
<%
     Player[] players = ServiceFactory.getPlayerService().getAllPlayers();
     for (int i = 0; i < players.length; i++) {
         Player player = players[i];

         // Only display authorized players.
         if (currentUser.isAdminRole() || username.equals(player.getUsername())) {
             out.println("<tr><td><a href='playerSettings.view?id=" + player.getId() + "'>" + player + "</a></td></tr>");
         }
     }
%>
 </table>

<h2><%=is.get("restrictedsettings.password.title", username)%></h2>

 <form method="post" action='changePassword.jsp' method="post">
     <table>
         <tr><td><%=is.get("restrictedsettings.oldpassword")%></td><td><input type="password" name="oldPassword"/></td></tr>
         <tr><td><%=is.get("restrictedsettings.newpassword")%></td><td><input type="password" name="newPassword"/></td></tr>
         <tr><td><%=is.get("restrictedsettings.confirmpassword")%></td><td><input type="password" name="confirmPassword"/></td></tr>
         <tr><td colspan="2" align="right"><input type='submit' value='<%=is.get("common.ok")%>'></td></tr>
     </table>
 </form>

 </body></html>