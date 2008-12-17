<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">

<html><head>
    <%@ include file="head.jsp" %>
    <link rel="alternate" type="application/rss+xml" title="Subsonic Podcast" href="podcast.view?suffix=.rss"/>
    <title>${model.brand}</title>
</head>

<frameset rows="70,*,0" border="1" framespacing="1" frameborder="1">
    <frame name="upper" src="top.view?">
    <frameset cols="15%,85%">
        <frame name="left" src="left.view?" marginwidth="10" marginheight="10">

        <frameset rows="70%,30%">
            <frame name="main" src="nowPlaying.view?" marginwidth="10" marginheight="10">
            <frame name="playlist" src="playlist.view?">
        </frameset>
    </frameset>
    <frame name="hidden" frameborder="0" noresize="noresize">

</frameset>

</html>