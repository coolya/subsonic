<%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>

<%
    if (request.getParameter("player") != null) {
        session.setAttribute("player", request.getParameter("player"));
    } else {
        session.setAttribute("player", null);
    }
    response.sendRedirect("settings.jsp");
%>

