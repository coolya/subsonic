<%--$Revision: 1.19 $ $Date: 2006/03/01 16:58:08 $--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*"%>
<%@ page import="net.sourceforge.subsonic.service.*"%>
<%@ page import="java.io.*"%>

<html><head>
    <!--[if gte IE 5.5000]>
    <script type="text/javascript" src="pngfix.js"></script>
    <![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="style.css" rel="stylesheet">
</head><body>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    User user = ServiceFactory.getSecurityService().getCurrentUser(request);
%>

<h2><img src="icons/random.png" width="16" height="16"/>&nbsp;<%=is.get("more.random.title")%></h2>

<form method="post" action='randomPlaylist.jsp'>

<table>
<tr><td><%=is.get("more.random.text")%></td>

<td><select name="size">
<option value="5"><%=is.get("more.random.songs", 5)%></option>
<option value="10" selected="true"><%=is.get("more.random.songs", 10)%></option>
<option value="20"><%=is.get("more.random.songs", 20)%></option>
<option value="50"><%=is.get("more.random.songs", 50)%></option>
</select></td>

<td><input type="submit" value="<%=is.get("more.random.ok")%>"></td></tr>
</table>
</form>

<h2><img src="icons/wap.jpeg" width="16" height="16"/>&nbsp;<%=is.get("more.mobile.title")%></h2>
<%=is.get("more.mobile.text")%>

<h2><img src="icons/podcast.png" width="16" height="16"/>&nbsp;<%=is.get("more.podcast.title")%></h2>
<%=is.get("more.podcast.text")%>

<% if (user.isUploadRole()) { %>

<h2><img src="icons/upload.gif" width="16" height="16"/>&nbsp;<%=is.get("more.upload.title")%></h2>
<%
    File defaultDir = null;
    MusicFolder[] musicFolders = ServiceFactory.getSettingsService().getAllMusicFolders();
    if (musicFolders.length > 0) {
        defaultDir = new File(musicFolders[0].getPath(), "incoming");
    }
%>

<form method="post" enctype="multipart/form-data" action="upload.jsp">
<table>
<tr><td><%=is.get("more.upload.source")%></td><td colspan="2"><input type="file" id="file" name="file" size="40"/></td></tr>
<tr><td><%=is.get("more.upload.target")%></td><td><input type="text" id="dir" name="dir" size="37" value="<%=defaultDir == null ? null : defaultDir.getPath()%>"/></td>
<td><input type="submit" value="<%=is.get("more.upload.ok")%>"/></td></tr>
<tr><td colspan="2">
<input type="checkbox" checked name="unzip" id="unzip"/>
<label for="unzip"><%=is.get("more.upload.unzip")%></label>
</td></tr>
</table>
</form>

<% } %>

</body></html>