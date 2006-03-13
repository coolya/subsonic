 <%--$Revision: 1.5 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>

<%
    int interval = Integer.parseInt(request.getParameter("interval"));
    int hour = Integer.parseInt(request.getParameter("hour"));

    SettingsService settings = ServiceFactory.getSettingsService();
    settings.setIndexCreationInterval(interval);
    settings.setIndexCreationHour(hour);
    settings.save();

    ServiceFactory.getSearchService().schedule();

    response.sendRedirect("settings.jsp");
%>

