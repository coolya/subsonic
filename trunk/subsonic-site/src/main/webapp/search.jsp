<%
    String query = request.getParameter("query") + "+site:subsonic.org+OR+site:activeobjects.no";
    response.sendRedirect("http://www.google.com/search?q=" + query);
%>
