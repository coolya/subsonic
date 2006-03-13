<%--$Revision: 1.12 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>
<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    String path = request.getParameter("path");
    MusicFile file = new MusicFile(path);

    // Create array of file(s) to display.
    MusicFile[] musicFiles;
    if (file.isDirectory()) {
        musicFiles = file.getChildren(false, true);
    } else {
        musicFiles = new MusicFile[] {file};
    }

    String playString = is.get("wap.browse.playall");
    String addString = is.get("wap.browse.addall");
    if (musicFiles.length == 1 && musicFiles[0].isFile()) {
        playString = is.get("wap.browse.playone");
        addString = is.get("wap.browse.addone");
    }

%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<wml>

	<head>
		<meta http-equiv="Cache-Control" content="max-age=0" forua="true"/>
		<meta http-equiv="Cache-Control" content="must-revalidate" forua="true"/>
	</head>

	<template>
		<do type="prev" name="back" label="<%=is.get("common.back")%>"><prev/></do>
	</template>

    <card id="main" title="subsonic" newcontext="false">

    <p><small><b>
    <a href="playlist.jsp?play=<%= file.urlEncode()%>">[<%= playString%>]</a><br/>
    <a href="playlist.jsp?add=<%= file.urlEncode()%>">[<%= addString%>]</a><br/>
    <a href="index.jsp">[<%=is.get("common.home")%>]</a><br/>
    </b></small></p>

    <p><small>

<%
    for (int i = 0; i < musicFiles.length; i++) {
        out.print("<a href=\"browse.jsp?path=" + musicFiles[i].urlEncode() + "\">" +
                StringUtil.toHtml(musicFiles[i].getTitle()) + "</a><br/>");
    }
%>

    </small></p>
</card>
</wml>

