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
                <span style="white-space:nowrap;">"artist/album/song"</span> manner. There are music managers, like
                <a href="http://www.mediamonkey.com/" target="_blank">MediaMonkey</a>, that can help you achieve this.</p>

            <h2>Setting up remote access</h2>
            <p>With Subsonic you can access your music anywhere on the internet. However in order to do that certain conditions must be met:</p>
            <ul class="list">
                <li>Your router must have a public IP address and must be accessible from the internet. This is true for most home internet connections.</li>
                <li>Your router must support the UPnP protocol so that Subsonic can setup the appropriate port forwarding. If your router doesn't support
                    this you will have to configure the port forwarding manually.</li>
            </ul>

            <h3>Automatic port forwarding</h3>
            <p>To enable automatic port forwarding in Subsonic, go to <b>Settings &gt; Network</b> and enable the
                "Automatically configure your router..." option. Click the Save button and pay close attention to the status message
                that is displayed. If you get an error message, you should first try to configure your router to enable UPnP.
                If it still fails, or your router doesn't support UPnP, follow the instructions below.
                If it works, the Subsonic server will at regular intervals (every hour) contact the router and tell it to
                forward incoming connections (from the internet) to the Subsonic server.
            </p>

            <h3>Manual port forwarding</h3>
            <p>If the automatic option didn't work you can still set it up manually. Consult <a href="http://portforward.com/" target="_blank">portforward.com</a>
                to get instructions specific to your router. Note that the instructions contain important steps on how to
                configure a <em>static IP address</em> for your computer. You should follow these. Later in the process, you
                will find that Subsonic is not on the list of applications, so select the "Default Guide" instead.
            </p>
            <p>
                When you get to the point where the router asks for <em>private and public ports</em>, enter the port number the
                Subsonic server is using (normally 80 on Windows and 4040 on other operating systems) for both values.
                If asked for a <em>protocol</em>, select TCP.
            </p>
            <p>
                If you are unsure what any of this means you will probably need to consult someone with network configuration
                experience.
            </p>

            <h3>Setting up your personal subsonic.org address</h3>
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
