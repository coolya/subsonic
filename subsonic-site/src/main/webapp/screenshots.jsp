<%@ page import="java.net.URL" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%! String current = "screenshots"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1>Subsonic Screenshots</h1>
            <a href="inc/img/screenshots/screen02.png"><img src="inc/img/screenshots/thumb02.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen01.png"><img src="inc/img/screenshots/thumb01.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen05.png"><img src="inc/img/screenshots/thumb05.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen04.png"><img src="inc/img/screenshots/thumb04.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen06.png"><img src="inc/img/screenshots/thumb06.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen03.png"><img src="inc/img/screenshots/thumb03.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen07.png"><img src="inc/img/screenshots/thumb07.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen08.png"><img src="inc/img/screenshots/thumb08.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen09.png"><img src="inc/img/screenshots/thumb09.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen10.png"><img src="inc/img/screenshots/thumb10.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen11.png"><img src="inc/img/screenshots/thumb11.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen12.png"><img src="inc/img/screenshots/thumb12.png" alt="" style="padding:3px"/></a>
            <a href="inc/img/screenshots/screen13.png"><img src="inc/img/screenshots/thumb13.png" alt="" style="padding:3px"/></a>

            <script type="text/javascript" src="inc/js/swfobject.js"></script>

            <div id="mediaspace"></div>

            <%
                URL url = new URL(request.getRequestURL().toString());
                String host = url.getHost();
                if (url.getPort() != 80 && url.getPort() != -1) {
                    host += ":" + url.getPort();
                }
            %>
            <script type="text/javascript">
                var so = new SWFObject("inc/flash/player.swf","mpl","640","360","9");
                so.addParam("allowfullscreen","true");
                so.addParam("allowscriptaccess","always");
                so.addParam("wmode","opaque");
                so.addVariable("duration","162");
                so.addVariable("file","http://<%=host%>/pages/inc/video/subsonic-medium.m4v");
                so.addVariable("image","http://<%=host%>/pages/inc/video/subsonic-medium.jpg");
                so.addVariable("stretching","none");
                so.write("mediaspace");
            </script>
        </div>


        <div id="side-col">
            <%@ include file="download-subsonic.jsp" %>
            <%@ include file="donate.jsp" %>
        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
