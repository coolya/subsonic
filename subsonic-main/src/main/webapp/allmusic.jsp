 <%--$Revision: 1.10 $ $Date: 2006/03/01 17:23:15 $--%>
 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>

 <html><head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <link href="style.css" rel="stylesheet">
 </head>
 <%
     InternationalizationService is = ServiceFactory.getInternationalizationService();
     String album = request.getParameter("album");
 %>

 <body onload="document.allmusic.submit();">
 <h2><%=is.get("allmusic.text", album)%></h2>

 <form name="allmusic" action="http://www.allmusic.com/cg/amg.dll" method="POST" accept-charset="iso-8859-1">
     <input type="hidden" name="p" value="amg"/>
     <input type="hidden" name="SQL" value="<%=album%>"/>
     <input type="hidden" name="OPT1" value="2"/>
 </form>

 </body>
</html>