 <%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>

 <%
     String name = request.getParameter("name");
     ServiceFactory.getPlaylistService().deletePlaylist(name);
 %>

 <jsp:forward page="loadPlaylist.jsp"/>
