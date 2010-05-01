<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%! String current = "faq"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1>Frequently Asked Questions</h1>

            <h3>How long does it take to get the license after I have donated?</h3>
            <p>Normally no more than ten minutes.  If it should take longer, please <a href="mailto:sindre@activeobjects.no">take contact</a>,
                but please check your spam filter first.</p>

            <h3>I can't access my Subsonic server from the internet or from my iPhone/Android phone.</h3>
            <p>Please follow the guide in the <a href="getting-started.jsp">Getting Started</a> documentation.</p>

            <%--TODO: Network shares--%>
            <%--TODO: How to install the license--%>
        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
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
