 <%--$Revision: 1.7 $ $Date: 2006/03/01 16:58:08 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>
 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

 <h1><%=is.get("createuser.title")%></h1>
 <a href="settings.jsp">[<%=is.get("common.back")%>]</a>&nbsp;&nbsp;

 <form method="post" action='createUserConfirm.jsp'>
     <table>
         <tr><td><%=is.get("createuser.username")%></td><td><input type="text" name="username"/></td></tr>
         <tr><td><%=is.get("createuser.password")%></td><td><input type="password" name="password"/></td></tr>
         <tr><td><%=is.get("createuser.confirmpassword")%></td><td><input type="password" name="confirmPassword"/></td></tr>

         <tr><td style="padding-top:15pt" colspan="2"><input type="checkbox" name="admin" id="admin"/>
             <label for="admin"><%=is.get("createuser.admin")%></label></td></tr>
         <tr><td colspan="2"><input type="checkbox" name="download" id="download"/>
             <label for="download"><%=is.get("createuser.download")%></label></td></tr>
         <tr><td colspan="2"><input type="checkbox" name="upload" id="upload"/>
             <label for="upload"><%=is.get("createuser.upload")%></label></td></tr>
         <tr><td colspan="2"><input type="checkbox" name="playlist" id="playlist"/>
             <label for="playlist"><%=is.get("createuser.playlist")%></label></td></tr>
         <tr><td colspan="2"><input type="checkbox" name="coverart" id="coverart"/>
             <label for="coverart"><%=is.get("createuser.coverart")%></label></td></tr>
         <tr><td colspan="2" style="padding-bottom:10pt"><input type="checkbox" name="comment" id="comment"/>
             <label for="comment"><%=is.get("createuser.comment")%></label></td></tr>

         <tr><td colspan="2" align="center"><input type='submit' value='<%=is.get("common.ok")%>'></td></tr>
     </table>
 </form>

 </body></html>
