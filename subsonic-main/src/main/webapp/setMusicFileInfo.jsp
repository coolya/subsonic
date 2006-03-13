<%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="net.sourceforge.subsonic.util.*"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>

<%
    String path = request.getParameter("path");
    String action = request.getParameter("action");
    MusicInfoService infoService = ServiceFactory.getMusicInfoService();

    MusicFileInfo musicFileInfo = infoService.getMusicFileInfoForPath(path);
    boolean exists = musicFileInfo != null;
    if (!exists) {
        musicFileInfo = new MusicFileInfo(path);
    }

    if ("rating".equals(action)) {
        int rating = Integer.parseInt(request.getParameter("rating"));
        musicFileInfo.setRating(rating);
    } else if ("comment".equals(action)) {
        musicFileInfo.setComment(StringUtil.toHtml(request.getParameter("comment")));
    }

    if (exists) {
        infoService.updateMusicFileInfo(musicFileInfo);
    } else {
        infoService.createMusicFileInfo(musicFileInfo);
    }

    response.sendRedirect("main.jsp?path=" + StringUtil.urlEncode(path));

%>

