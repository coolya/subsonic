<%--$Revision: 1.13 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head><title>Subsonic</title>
    <link rel="shortcut icon" href="icons/favicon.ico"/>
</head>

<frameset rows="70,*,0" border="1" framespacing="1" frameborder="1">
    <frame name="top" src="top.jsp?">

    <frameset cols="20%,80%">
        <frame name="left" src="left.jsp?" marginwidth="10" marginheight="10">

        <frameset rows="70%,30%">
            <frame name="main" src="nowPlaying.jsp?" marginwidth="10" marginheight="10">
            <frame name="playlist" src="playlist.jsp?">
        </frameset>

    </frameset>

    <frame name="hidden" frameborder="0" noresize="noresize">

</frameset>

</html>