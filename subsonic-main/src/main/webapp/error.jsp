<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" isErrorPage="true" %>
<%@ page import="java.io.*"%>

<html><head>
    <!--[if gte IE 5.5000]>
    <script type="text/javascript" src="script/pngfix.js"></script>
    <![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="style/default.css" type="text/css"/>
</head>

<body>
<h1><img src="icons/error.png" alt=""/> Error</h1>

<p>
    Subsonic encountered an internal error. You can report this error in the
    <a href="http://subsonic.sourceforge.net/forum.html" target="_blank">Subsonic Forum</a>.
    Please include the information below.
</p>

<%
    StringWriter sw = new StringWriter();
    exception.printStackTrace(new PrintWriter(sw));
%>

<table class="ruleTable indent">
    <tr><td class="ruleTableHeader">Exception</td>
        <td class="ruleTableCell"><%=exception.getClass().getName()%></td></tr>
    <tr><td class="ruleTableHeader">Message</td>
        <td class="ruleTableCell"><%=exception.getMessage()%></td></tr>
    <tr><td class="ruleTableHeader">Java version</td>
        <td class="ruleTableCell"><%=System.getProperty("java.vendor") + ' ' + System.getProperty("java.version")%></td></tr>
    <tr><td class="ruleTableHeader">Operating system</td>
        <td class="ruleTableCell"><%=System.getProperty("os.name") + ' ' + System.getProperty("os.version")%></td></tr>
    <tr><td class="ruleTableHeader">Tomcat home</td>
        <td class="ruleTableCell"><%=System.getProperty("catalina.home")%></td></tr>
    <tr><td class="ruleTableHeader" style="vertical-align:top;">Stack trace</td>
        <td class="ruleTableCell" style="white-space:pre"><%=sw.getBuffer()%></td></tr>
</table>

</body>
</html>
