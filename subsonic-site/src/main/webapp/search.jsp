<%
    String query = request.getParameter("query") + "+site:subsonic.sourceforge.net+OR+site:activeobjects.no";
    response.sendRedirect("http://www.google.com/search?q=" + query);
%>
