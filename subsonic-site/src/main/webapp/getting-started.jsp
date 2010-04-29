<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%! String current = "getting-started"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1>Getting Started</h1>

            <p>
                This guide assumes that you have successfully installed the Subsonic server on your computer. If not,
                please refer to the <a href="installation.jsp">installation instructions</a>.
            </p>
            <p>
                After installing and starting Subsonic, open the Subsonic web page. The web address may differ depending
                on your installation options, but is typically <a href="http://localhost" target="_blank">http://localhost</a>
                or <a href="http://localhost:4040" target="_blank">http://localhost:4040</a>.
            </p>

            <h2>Setting up music folders</h2>

            <p>You must tell Subsonic where you keep your music. Select <b>Settings &gt; Music folders</b> to add one or
                more folders.</p>

            <p>Note that if you add more than one music folder, a list will appear on the left side of the screen where you can
                select the active folder.</p>

            <p>Also note that Subsonic will organize your music according to how they are organized on your disk.
                Unlike many other music applications, Subsonic does not organize the music according to the tag information
                embedded in the files. (It does, however, also read the tags for presentation and search purposes.)</p>

            <p>Consequently, it's recommended that the music folders you add to Subsonic are organized in an
                "artist/album/song" manner. There are music managers, like <a href="http://www.mediamonkey.com/" target="_blank">MediaMonkey</a> ,
                that can help you achieve this.</p>

            <h2>Setting up remote access</h2>
            <p>TODO</p>
        </div>

        <div id="side-col">
            <%@ include file="download-subsonic.jsp" %>
        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
