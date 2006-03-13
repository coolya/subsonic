<%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>

<%
    Integer id = new Integer(request.getParameter("id"));
    InternetRadio radio = ServiceFactory.getSettingsService().getInternetRadioById(id);
    response.sendRedirect(radio.getStreamUrl());
%>