 <%--$Revision: 1.22 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.domain.*,
                  net.sourceforge.subsonic.service.*,
                  net.sourceforge.subsonic.servlet.*,
                  net.sourceforge.subsonic.util.*"%>

 <html><head>
     <meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
     <meta http-equiv="REFRESH" content="20;URL=status.jsp">
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

 <%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
%>

<h1><%=is.get("status.title")%></h1>
<table border="1" cellpadding="10" rules="all">
<tr><th><%=is.get("status.player")%></th><th><%=is.get("status.user")%></th><th><%=is.get("status.current")%></th>
<th><%=is.get("status.transmitted")%></th><th><%=is.get("status.bitrate")%></th></tr>
<%

    StreamStatus[] streamStatuses = ServiceFactory.getStatusService().getAllStreamStatuses();
    for (int i = 0; i < streamStatuses.length; i++) {
        StreamStatus status = streamStatuses[i];
        Player player = status.getPlayer();
        String user = player.getUsername() == null ? is.get("common.unknown") : player.getUsername();
        String type = player.getType() == null ? is.get("common.unknown") : player.getType();
        String current = is.get("common.unknown");
        String transmitted = StringUtil.formatBytes(status.getBytesStreamed(), is.getLocale());

        MusicFile file = status.getFile();
        if (file != null) {
            current = StringUtil.toHtml(file.getPath()) + "<br/>(" + file.getBitRate() + " Kbps)";
        }
        
        String chart = "<img width='" + StatusChartServlet.IMAGE_WIDTH + "' height='" + StatusChartServlet.IMAGE_HEIGHT +
                "' src='statusChart?type=stream&index=" + i + "'/>";
    %>

    <tr><td><%= player%><br/><%= type%></td><td><%= user%></td><td><%= current%></td><td><%= transmitted %></td><td><%= chart%></td></tr>

    <%
    }
%>
</table>

<%
    DownloadStatus[] downloadStatuses = ServiceFactory.getStatusService().getAllDownloadStatuses();
    if (downloadStatuses.length > 0) {
%>

<h1><%=is.get("status.download.title")%></h1>
<table border="1" cellpadding="10" rules="all">
<tr><th><%=is.get("status.player")%></th><th><%=is.get("status.user")%></th><th><%=is.get("status.current")%></th>
<th><%=is.get("status.transmitted")%></th><th><%=is.get("status.bitrate")%></th></tr>

<%
        for (int i = 0; i < downloadStatuses.length; i++) {
            StreamStatus status = downloadStatuses[i];
            Player player = status.getPlayer();
            String user = player.getUsername() == null ? is.get("common.unknown") : player.getUsername();
            String type = player.getType() == null ? is.get("common.unknown") : player.getType();
            String current = is.get("common.unknown");
            String transmitted = StringUtil.formatBytes(status.getBytesStreamed(), is.getLocale());

            MusicFile file = status.getFile();
            if (file != null) {
                current = StringUtil.toHtml(file.getPath()) + "<br/>(" + file.getBitRate() + " Kbps)";
            }

            String chart = "<img width='" + StatusChartServlet.IMAGE_WIDTH + "' height='" + StatusChartServlet.IMAGE_HEIGHT +
                    "' src='statusChart?type=download&index=" + i + "'/>";
%>
    <tr><td><%= player%><br/><%= type%></td><td><%= user%></td><td><%= current%></td><td><%= transmitted %></td><td><%= chart%></td></tr>

<%
        }
%>

</table>

<%
    }
%>

<p><a href='status.jsp' target='main'>[<%=is.get("common.refresh")%>]</a></p>

 </body></html>