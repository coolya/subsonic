 <%--$Revision: 1.21 $ $Date: 2006/03/06 20:20:27 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.*,
                 net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.text.*,
                 java.util.*"%>
 <html><head>
     <!--[if gte IE 5.5000]>
     <script type="text/javascript" src="pngfix.js"></script>
     <![endif]-->
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head><body>

<%
    VersionService versionService = ServiceFactory.getVersionService();
    InternationalizationService is = ServiceFactory.getInternationalizationService();

    Version version = versionService.getLocalVersion();
    Date buildDate = versionService.getLocalBuildDate();
    String buildNumber = versionService.getLocalBuildNumber();

    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, is.getLocale());
    String versionString = version == null ? is.get("common.unknown") : version.toString() + " (build " + buildNumber + ')';
    String buildDateString = buildDate == null ? is.get("common.unknown") : dateFormat.format(buildDate);
%>

<h1><%=is.get("help.title")%></h1>

<%
    if (versionService.isNewVersionAvailable()) {
        out.println("<p style='color:red'>" + is.get("help.upgrade", versionService.getLatestVersion().toString()) + "</p>");
    }
%>
<table border="1" cellpadding="5" width="75%" rules="all">
<tr><td><b><%=is.get("help.version.title")%></b></td><td><%= versionString%></td></tr>
<tr><td><b><%=is.get("help.builddate.title")%></b></td><td><%= buildDateString%></td></tr>
<tr><td><b><%=is.get("help.license.title")%></b></td><td><%=is.get("help.license.text")%></td></tr>
<tr><td><b><%=is.get("help.homepage.title")%></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/">http://subsonic.sourceforge.net/</a></td></tr>
<tr><td><b><%=is.get("help.faq.title")%></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/faq.html">http://subsonic.sourceforge.net/faq.html</a></td></tr>
<tr><td><b><%=is.get("help.forum.title")%></b></td><td><a target="_blank" href="http://subsonic.sourceforge.net/forum.html">http://subsonic.sourceforge.net/forum.html</a></td></tr>
<tr><td><b><%=is.get("help.contact.title")%></b></td><td><%=is.get("help.contact.text")%></td></tr>
</table>
<p/>

<table width="75%"><tr>
    <td><a target="_blank" href="http://sourceforge.net/donate/index.php?group_id=126265"><img src="icons/donate.gif"/></a></td>
    <td><%=is.get("help.donate")%></td>
</tr></table>

<h2><img src="icons/log.png" width="22" height="22"/>&nbsp;<%=is.get("help.log")%></h2>

<pre>
<%
    DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, is.getLocale());

    Logger.Entry[] entries = Logger.getLatestLogEntries();
    for (int i = 0; i < entries.length; i++) {
        Logger.Entry entry = entries[i];
        out.print('[' + dateTimeFormat.format(entry.getDate()) + "] ");
        out.print(entry.getLevel() + " ");
        out.print(entry.getCategory() + " - ");
        out.println(StringUtil.toHtml(entry.getMessage().toString()));
    }
%>
</pre>
<p><a href='help.jsp?'>[<%=is.get("common.refresh")%>]</a></p>

 </body></html>