<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'home';
    include("header.php");
?>

<body class="section-home page-home">

<a name="top"/>

<div id="container">
<?php include("menu.php");?>

<div id="content">
<div id="main-col">
    <h1>Welcome to Subsonic!</h1>

    <div class="floatcontainer margin10-t margin10-b">
        <ul class="stars column-left">
            <li>Organize music and edit tags in your audio library with a powerful, intuitive interface.</li>
            <li>Automatically lookup and tag Album Art and other metadata.</li>
            <li>Manage 50,000+ files in your music collection without bogging down.</li>
            <li>Manage all genres of audio: Rock, Classical, Audiobooks, Comedy, Podcasts, etc.</li>
        </ul>
        <ul class="stars column-right">
            <li>Play MP3s and other audio formats, and never again worry about varying volume.</li>
            <li>Record CDs and convert MP3s, M4A, OGG, FLAC and WMA files etc. into other formats.</li>
            <li>Create playlists and let Auto-DJ &amp; Party Mode take care of your party.</li>
            <li>Sync iPhones, iPods, &amp; MP3 players, converting &amp; leveling tracks on-the-fly.</li>
            <li><a href="features.php">Plus much more...</a></li>
        </ul>
    </div>

    <div class="featureitem">
        <div class="heading">What is Subsonic?</div>
        <div class="content">
            <div class="wide-content">

                <p>
                    Subsonic is a free, web-based media streamer, providing access to your entire music collection whuterever
                    you are. Use it to share your music with friends, or to listen to your own music while at work. You can
                    stream to multiple players simultaneously, for instance to one player in your kitchen and another in
                    your living room.
                </p>

                <p>
                    Subsonic is designed to handle very large music collections (many thousand albums). It uses a
                    combination of directory structure and tag parsing to organize the music. Although optimized for MP3 streaming, it
                    works for any audio or video format that can stream over HTTP (for instance AAC and OGG). By using transcoder
                    plug-ins, Subsonic supports on-the-fly conversion and streaming of virtually any audio format, including WMA, FLAC, APE,
                    Musepack, WavPack, Shorten and OptimFROG.
                </p>

                <p>
                    If you have constrained bandwidth, you may set an upper limit for the bitrate of the music streams.
                    Subsonic will then automatically resample the music to a suitable bitrate.
                </p>

                <p>
                    In addition to being a streaming media server, Subsonic works very well as a local jukebox. The
                    intuitive web interface, as well as search and index facilities, are optimized for efficient browsing through large
                    media libraries.  Subsonic also comes with an integrated Podcast receiver, with many of the same features
                    as you find in iTunes.
                </p>

                <p>
                    Subsonic is free software distributed under the <a href="http://www.gnu.org/copyleft/lesser.html">LGPL</a>
                    open-source license.
                </p>
            </div>
        </div>
    </div>

</div>

<div id="side-col">
    <div class="sidebox">
        <h2>Download Subsonic</h2>
        <p>
            Available for Windows, Mac, Linux and Unix.
        </p>
        <p>
            <a href="download.php"><img src="inc/img/button-download.gif" alt="Download" class="img-center"/></a>
        </p>
    </div>
    <div class="sidebox">
        <h2>About</h2>
        <p>
            <img src="inc/img/sindre.jpeg" alt="Sindre Mehus" hspace="10" vspace="10" style="float:right"/>
            Subsonic is developed by <a href="mailto:sindre@activeobjects.no">Sindre Mehus</a>.
            I live in Oslo, Norway and work as a Java software contractor.
        </p>
        <p>
            If you have any questions, comments or suggestions for improvements, please visit the <a href="forum.php">Subsonic Forum</a>.
        </p>
    </div>
    <div class="sidebox">
        <h2>Get involved!</h2>
        <p>
            I'm looking for volunteers who'd like to translate Subsonic to new languages, and to improve existing translations. Interested? Please read <a href="translate.php">this</a>.
        </p>
    </div>
    <div style="margin-top:4em">
        <a href="http://sourceforge.net/projects/subsonic/"><img src="http://sourceforge.net/sflogo.php?group_id=126265&type=4" alt="SourceForge.net" class="img-center"/></a>
    </div>
</div>
<div class="clear">
</div>
</div>
<hr/>
<?php include("footer.php"); ?>
</div>


</body>
</html>
