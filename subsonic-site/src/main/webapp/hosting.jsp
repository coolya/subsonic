<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%!
    String current = "hosting";
    String gigaProsUrl = "http://www.gigapros.com/affiliate/scripts/click.php?a_aid=subsonic&desturl=http://www.gigapros.com/portal/index.php/products-a-services/specialty-hosting/subsonic-server.html";
%>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Subsonic Hosting</h1>

            <p>
                An alternative to running the Subsonic on your own computer is to get a pre-installed Subsonic server from
                our hosting partner <a href="<%=gigaProsUrl%>">GigaPros</a>.
            </p>
            <p>
                Subsonic hosting servers are actually powerful Virtual Private Servers (VPS), which are highly optimized to run Subsonic.
                These VPS'es have fully functional pre-installed Subsonic server and they behave exactly like your own Dedicated Server
                with full root access.
            </p>

            <p>
                <b><a href="<%=gigaProsUrl%>">Check out GigaPros' server plans and prices.</a></b>
            </p>
            <a href="<%=gigaProsUrl%>"><img src="https://www.gigapros.com/portal/images/stories/pics/top_hosting_subsonic.jpg" alt=""/></a>
        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
