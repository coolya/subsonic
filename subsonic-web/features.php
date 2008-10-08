<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'features';
    include("header.php");
?>

<body class="section-information page-free">

<a name="top"/>

<div id="container">
<?php include("menu.php"); ?>

<div id="content">
<div id="main-col">
<h1>Subsonic Features</h1>

<div class="featureitem">
    <a name="userfriendly"></a>

    <div class="heading">User-friendly <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <a href="inc/img/features/amy.png"><img src="inc/img/features/amy-small.png" alt=""/></a>
        </div>
        <div class="description">
            <ul class="list">
                <li>Listen to your music from anywhere &ndash; all you need is a browser.</li>
                <li>Lean web interface optimized for constrained bandwidth environments and efficient browsing through large music collections.</li>
                <li>Free-text search functionality.</li>
                <li>Displays cover art, including images embedded in ID3 tags.</li>
                <li>You can assign ratings and comments to albums. View best liked and most played albums.</li>
                <li>Common playlist features (add, remove, rearrange, repeat, shuffle, undo). Playlist can be managed by server or player.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="mediasupport"></a>

    <div class="heading">Media support <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <img src="inc/img/features/media-support.png" alt=""/>
        </div>

        <div class="description">
            <ul class="list">
                <li>Supports MP3, OGG, AAC and any other audio or video format that can be streamed over HTTP.</li>
                <li><a href="transcoding.php"><b>Transcoding engine</b></a> allows for streaming of a variety of lossy and lossless formats by converting to MP3 on-the-fly.</li>
                <li>Works with any network-enabled media player, such as Winamp, iTunes, XMMS, MusicMatch and Windows Media Player. Also includes an embedded Flash-based player.</li>
                <li>Tag parsing and editing of MP3, OGG, FLAC, WMA and APE files.</li>
                <li>Playlists can be saved and restored. M3U, PLS and XSPF formats are supported. Saved playlists are available as Podcasts.</li>
                <li>On-the-fly resampling to lower bitrates using the high-quality LAME encoder.</li>
                <li>Implements the SHOUTcast protocol. Players which support this (including Winamp, iTunes and XMMS) will display the current artist and song name.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="customize"></a>
    <div class="heading">Customize <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <a href="inc/img/features/personal-settings.png"><img src="inc/img/features/personal-settings-small.png" alt=""/></a>
            <p/>
            <a href="inc/img/features/avatar.png"><img src="inc/img/features/avatar-small.png" alt=""/></a>
        </div>
        <div class="description">
            <ul class="list">
                <li>Full internationalization support. Currently available translations:<br/><br/>

                    <table style="padding-left:1.5em">
                        <tr><td>o English </td><td>(by Sindre Mehus)</td></tr>
                        <tr><td>o French </td><td>(by JohnDillinger)</td></tr>
                        <tr><td>o Spanish </td><td>(by Jorge Bueno Magdalena)</td></tr>
                        <tr><td>o German </td><td>(by Harald Weiss and J&ouml;rg Frommann)</td></tr>
                        <tr><td>o Italian </td><td>(by Michele Petrecca)</td></tr>
                        <tr><td>o Chinese </td><td>(by Neil Gao)</td></tr>
                        <tr><td>o Russian </td><td>(by Iaroslav Andrusiak)</td></tr>
                        <tr><td>o Dutch </td><td>(by Ronald Knot)</td></tr>
                        <tr><td>o Norwegian </td><td>(by Sindre Mehus)</td></tr>
                        <tr><td>o Macedonian </td><td>(by Stefan Ivanovski)</td></tr>
                    </table>
                </li>
                <li>Theme support. Currently ships with eight themes.</li>
                <li>Configurable user interface.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="flexible"></a>

    <div class="heading">Flexible <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <a href="inc/img/features/wap.png"><img src="inc/img/features/wap-small.png" alt=""/></a>
        </div>
        <div class="description">
            <ul class="list">
                <li>WAP interface allows you to control Subsonic from any mobile phone or PDA.</li>
                <li>Supports multiple simultaneous players. The playlist for any player can be managed from any location.</li>
                <li>In addition to streaming, single files or entire directories may be downloaded from Subsonic.</li>
                <li>Files can be uploaded to Subsonic. Zip-files can be automatically unpacked.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="integrate"></a>

    <div class="heading">Integrate <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <img src="inc/img/features/last-fm.png" alt=""  style="padding-bottom:20px"/>
            <p/>
            <img src="inc/img/features/wikipedia.png" alt=""/>
        </div>
        <div class="description">
            <ul class="list">
                <li>Audioscrobbling support. Automatically register what you're playing on Last.fm</li>
                <li>Finds cover art, lyrics and album info using web services (Amazon, MetroLyrics etc).</li>
                <li>Provides links to album reviews and more at Wikipedia, Google Music and allmusic.com.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="secure"></a>

    <div class="heading">Secure <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <a href="inc/img/features/logon.png"><img src="inc/img/features/logon-small.png" alt=""/></a>
        </div>
        <div class="description">
            <ul class="list">
                <li>Users must log in using a username and password. Users can be assigned different privileges.</li>
                <li>You can specify a upload/download bandwidth limit.</li>
                <li>Supports HTTPS/SSL encryption.</li>
                <li>Supports authentication in LDAP and Active Directory.</li>
            </ul>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="Extras"></a>

    <div class="heading">Extras <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <img src="inc/img/features/extras.png" alt=""/>
        </div>
        <div class="description">
            <ul class="list">
                <li>Download Podcasts with the integrated Podcast receiver.</li>
                <li>Support for Internet TV and radio stations.</li>
                <li>Stream directly to your mobile phone, using the <a href="http://www.activeobjects.no/subsonic/forum/viewtopic.php?t=1288"><b>Subsonic Mobile Player</b></a>.</li>
            </ul>
        </div>
    </div>
</div>

</div>


<div id="side-col">
    <div class="sidebox">
        <h2>Features</h2>
        <ul class="list">
            <li><a href="#userfriendly">User-friendly</a></li>
            <li><a href="#mediasupport">Media support</a></li>
            <li><a href="#customize">Customize</a></li>
            <li><a href="#flexible">Flexible</a></li>
            <li><a href="#integrate">Integrate</a></li>
            <li><a href="#secure">Secure</a></li>
            <li><a href="#Extras">Extras</a></li>
        </ul>
    </div>

    <?php include("download-subsonic.php"); ?>
    <?php include("ripserver.php"); ?>
</div>

<div class="clear">
</div>
</div>
<hr/>
<?php include("footer.php"); ?>
</div>


</body>
</html>
