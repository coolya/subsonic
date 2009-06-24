<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'home';
    include("header.php");
?>

<body>

<a name="top"/>

<div id="container">
<?php include("menu.php");?>

<div id="content">
<div id="main-col">
    <h1>Welcome to Subsonic!</h1>

    <div class="featureitem">
        <div class="content" style="background-color:white; border-top:1px solid #CBCEDC">
            <div class="wide-content">

                <a href="http://sf.net/community/cca09/vote/?f=463" target="_blank"><img src="http://c.fsdn.com/sf/images/cca/cca_120x60_2009_finalist.png" alt="Vote"
                                                                                            class="img-right" style="padding-bottom:10px; padding-right:10px"/></a>
                <p>
                    Subsonic is a finalist in the <b>SourceForge Community Choice Awards 2009</b>.
                </p>
                <p>
                    Please <b><a href="http://sf.net/community/cca09/vote/?f=463" target="_blank">vote for Subsonic now</a></b> to win the "Best Project for Multimedia" category!
                    Voting ends on July 20.
                </p>
                <p>
                </p>
                <p>
                    Thanks for your support :-)
                    <br/>
                    Sindre, Subsonic Developer
                </p>


            </div>
        </div>
    </div>

    <div class="floatcontainer margin10-t margin10-b">
        <ul class="stars column-left">
            <li>Listen to your music wherever you are. Share your collection with family and friends.</li>
            <li>Browse and manage your music collection with the user-friendly web interface.</li>
            <li>Stream music to your mobile phone, using the included <a href="http://www.activeobjects.no/subsonic/forum/viewtopic.php?t=1288">Subsonic Mobile Player</a>.</li>
            <li>Convert and stream lossless music on the fly.</li>
        </ul>
        <ul class="stars column-right">
            <li>Manage 100,000+ files in your music collection without hazzle.</li>
            <li>Download and listen to Podcasts.</li>
            <li>Get cover art, lyrics, reviews and other album info from Amazon, Discogs, MetroLyrics, allmusic, Wikipedia and Google Music.</li>
            <li><a href="features.php">Plus much more...</a></li>
        </ul>
    </div>

    <div class="featureitem">
        <div class="heading">What is Subsonic?</div>
        <div class="content">
            <div class="wide-content">

                <p>
                    Subsonic is a free, web-based media streamer, providing ubiquitous access to your music.
                    Use it to share your music with friends, or to listen to your own music while at work. You can
                    stream to multiple players simultaneously, for instance to one player in your kitchen and another in
                    your living room.
                </p>

                <a href="inc/img/screenshots/screen01.png"><img src="inc/img/screenshots/thumb01.png" alt="" class="img-right"/></a>

                <p>
                    Subsonic is designed to handle very large music collections (hundreds of gigabytes).
                    Although optimized for MP3 streaming, it works for any audio or video format that can stream over HTTP,
                    for instance AAC and OGG. By using <a href="transcoding.php">transcoder plug-ins</a>, Subsonic supports
                    on-the-fly conversion and streaming of virtually any audio format, including WMA, FLAC, APE, Musepack,
                    WavPack, Shorten and OptimFROG.
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
                    Based on Java technology, Subsonic runs on most platforms, including Windows, Mac, Linux and Unix variants.
                </p>

                <a href="http://www.gnu.org/copyleft/gpl.html"><img src="inc/img/gpl.png" alt="GPL" class="img-left"/></a>

                <p>
                    Subsonic is open-source software licensed under <a rel="license" href="http://www.gnu.org/copyleft/gpl.html">GPL</a>.
                </p>

            </div>
        </div>
    </div>

</div>

<div id="side-col">
    <?php include("download-subsonic.php"); ?>
    <?php include("quotes.php"); ?>
    <?php include("about.php"); ?>
    <?php include("donate.php"); ?>
    <div class="bottomspace">
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
