 <%--$Revision: 1.8 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                  net.sourceforge.subsonic.service.*"%>
 <%@ page import="net.sourceforge.subsonic.util.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    String username = request.getParameter("username");
    User user = ServiceFactory.getSecurityService().getUserByName(username);
    InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

 <h1><%=is.get("usersettings.title", username)%></h1>
 <a href="settings.jsp">[<%=is.get("common.back")%>]</a>&nbsp;&nbsp;

<%
    if (!User.USERNAME_ADMIN.equals(username)) {
%>
 <form method="post" action='userSettingsConfirm.jsp'>
     <input type="hidden" name="username" value="<%= username%>"/>
     <input type="hidden" name="action" value="role"/>
     <table>
         <tr>
             <td><input type="checkbox" name="admin" <%= user.isAdminRole() ? "checked" : ""%> id="admin"/></td>
             <td><label for="admin"><%=is.get("usersettings.admin")%></label></td>
         </tr>
         <tr>
             <td><input type="checkbox" name="download" <%= user.isDownloadRole() ? "checked" : ""%> id="download"/></td>
             <td><label for="download"><%=is.get("usersettings.download")%></label></td>
         </tr>
         <tr>
             <td><input type="checkbox" name="upload" <%= user.isUploadRole() ? "checked" : ""%> id="upload"/></td>
             <td><label for="upload"><%=is.get("usersettings.upload")%></label></td>
         </tr>
         <tr>
             <td><input type="checkbox" name="playlist" <%= user.isPlaylistRole() ? "checked" : ""%> id="playlist"/></td>
             <td><label for="playlist"><%=is.get("usersettings.playlist")%></label></td>
         </tr>
         <tr>
             <td><input type="checkbox" name="coverart" <%= user.isCoverArtRole() ? "checked" : ""%> id="coverart"/></td>
             <td><label for="coverart"><%=is.get("usersettings.coverart")%></label></td>
         </tr>
         <tr>
             <td><input type="checkbox" name="comment" <%= user.isCommentRole() ? "checked" : ""%> id="comment"/></td>
             <td><label for="comment"><%=is.get("usersettings.comment")%></label></td>
             <td><input type='submit' value='<%=is.get("common.save")%>'></td>
         </tr>
     </table>
 </form>
<%
    }
%>

 <br/>

 <form action='userSettingsConfirm.jsp' method="post">
     <input type="hidden" name="username" value="<%= username%>"/>
     <input type="hidden" name="action" value="password"/>
     <table>
         <tr><td><%=is.get("usersettings.newpassword")%></td><td><input type="password" name="newPassword"/></td></tr>
         <tr><td><%=is.get("usersettings.confirmpassword")%></td><td><input type="password" name="confirmPassword"/></td></tr>
         <tr><td colspan="2" align="right"><input type='submit' value='<%=is.get("common.ok")%>'></td></tr>
     </table>
 </form>

 <%
     if (!User.USERNAME_ADMIN.equals(username)) {
         out.println(is.get("usersettings.delete", StringUtil.urlEncode(username)));
     }
 %>

 </body></html>
