<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'installation';
    include("header.php");
?>

<body class="section-home page-home">

<a name="top"/>

<div id="container">
<?php include("menu.php");?>

<div id="content">
<div id="main-col">
<h1 class="bottomspace">Installing Subsonic</h1>
    <p>
        Subsonic features several flexible installation options.  Select the one which suits you best:
    </p>

    <ul class="list">
        <li><a href="#window">Windows installation</a></li>
        <li><a href="#standalone">Stand-alone installation</a></li>
        <li><a href="#war">WAR installation</a> &ndash; Use this if you want to deploy Subsonic in an external server (Tomcat, Jetty, Geronimo etc).</li>
    </ul>

<a name="window"><h2 class="div">Windows installation</h2></a>
    <img class="img-right" style="margin-bottom:10px;margin-top:10px" src="inc/img/windows-installer.png" alt="Windows Installer"/>
    <p><b>Requirements: </b>Java 5 or later (<a href="http://www.java.com">Download</a>)</p>
    <p>
        On Windows, the easiest way to install Subsonic is using the provided Windows Installer: <code>subsonic-x.x-setup.exe</code>.
    </p>
    <p>
        It installs Subsonic as a service (available in <code>Control Panel &gt; Administrative Tools &gt; Services</code>) that is started automatically when you log on to Windows.
    </p>
    <p>
        It also creates links in the Start Menu (<code>All Programs > Subsonic</code>). From this menu you can start/stop the Subsonic service, open the Subsonic web page, or
        change basic settings such as which port number Subsonic should use.
    </p>
    <p>
        After installing and starting Subsonic, open the Subsonic web page on <a href="http://localhost">http://localhost</a>.
    </p>

    <a name="standalone"><h2 class="div">Stand-alone installation</h2></a>
    <p><b>Requirements: </b>Java 5 or later (<a href="http://www.java.com">Download</a>)</p>
    <p>
        This is Subsonic with an embedded Jetty server. Recommended for most Linux and Mac users.
    </p>
    <ul>
        <li>Unpack the zip-file (<code>subsonic-x.x-standalone.zip</code>) to <code>&lt;SUBSONIC_HOME&gt;/standalone</code>.
            <code>SUBSONIC_HOME</code> is typically <code>c:\subsonic</code> on Windows, and <code>/var/subsonic</code> on Unix-based operating systems.</li>
        <li>Optionally configure the startup script <code>&lt;SUBSONIC_HOME&gt;/standalone/subsonic.sh</code></li>
        <li>Execute the startup script.</li>
        <li>Open the Subsonic web page. The default is <a href="http://localhost:8080">http://localhost:8080</a>.</li>
    </ul>
</div>

<div id="side-col">

    <div class="sidebox">
        <h2>Releases</h2>
        <ul class="list">
            <li><a href="#3.5">Subsonic 3.5</a></li>
            <li><a href="#3.5.beta2">Subsonic 3.5.beta2</a></li>
        </ul>
    </div>

    <!--<div style="margin-top:4em">-->
        <!--<img class="img-center" src="inc/img/windows-installer.png" alt="Windows Installer"/>-->
    <!--</div>-->

</div>

<div class="clear">
</div>
</div>
<hr/>
<?php include("footer.php"); ?>
</div>


</body>
</html>
