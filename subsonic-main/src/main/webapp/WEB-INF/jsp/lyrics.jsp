<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <title><fmt:message key="lyrics.title"/></title>
    <script type="text/javascript" src="<c:url value="/dwr/interface/lyricsService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>

    <script type="text/javascript" language="javascript">

        DWREngine.setErrorHandler(null);

        function getLyrics(artist, song) {
            window.focus();
            $("wait").style.display = "inline";
            $("lyrics").style.display = "none";
            $("noLyricsFound").style.display = "none";
            DWRUtil.setValue("lyrics", "wait");
            lyricsService.getLyrics(artist, song, getLyricsCallback);
        }

        function getLyricsCallback(lyrics) {
            DWRUtil.setValue("lyrics", lyrics);
            $("wait").style.display = "none";
            if (lyrics != null) {
                $("lyrics").style.display = "inline";
            } else {
                $("noLyricsFound").style.display = "inline";
            }
        }
    </script>

</head>
<body onload="getLyrics('${model.artist}', '${model.song}')">

<table>
    <tr>
        <td><fmt:message key="lyrics.artist"/></td>
        <td style="padding-left:0.50em"><input id="artist" type="text" size="40" value="${model.artist}"/></td>
        <td style="padding-left:0.75em"><input type="submit" value="<fmt:message key="lyrics.search"/>" style="width:6em" onclick="getLyrics(DWRUtil.getValue('artist'), DWRUtil.getValue('song'))"/></td>
    </tr>
    <tr>
        <td><fmt:message key="lyrics.song"/></td>
        <td style="padding-left:0.50em"><input id="song" type="text" size="40" value="${model.song}"/></td>
        <td style="padding-left:0.75em"><input type="submit" value="<fmt:message key="common.close"/>" style="width:6em" onclick="self.close()"/></td>
    </tr>
</table>

<hr/>
<p id="wait"><b><fmt:message key="lyrics.wait"/></b></p>
<p id="noLyricsFound" style="display:none"><b><fmt:message key="lyrics.nolyricsfound"/></b></p>
<div id="lyrics" style="display:none;"></div>

<hr/>
<p style="text-align:center">
    <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
</p>

</body>
</html>
