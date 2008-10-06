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

TODO: Start with summary of what Subsonic is: web-based media streamer.


<div class="featureitem">
    <a name="mediasupport"></a>

    <div class="heading">Media support <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <img src="inc/img/features/winamp.png" alt=""/>
            <img src="inc/img/features/wmp.png" alt=""/>
            <img src="inc/img/features/itunes.png" alt=""/>
            <p/>
            <img src="inc/img/features/mp3.png" alt=""/>
            <img src="inc/img/features/flac.png" alt=""/>
            <p/>
            <img src="inc/img/features/lame.png" alt=""/>
            <p/>
            <img src="inc/img/features/shoutcast.png" alt=""/>
        </div>

        <div class="description">
            <ul class="list">
                <li>Supports MP3, OGG, AAC and any other audio or video format that can be streamed over HTTP.</li>
                <li><a href="transcoding.php">Transcoding engine</a> allows for streaming of a variety of lossy and lossless formats by converting to MP3 on-the-fly.</li>
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
                <li>Full internationalization support. Currently available translations: English, Norwegian, Macedonian (by Stefan Ivanovski), Simplified Chinese (by Neil Gao),
                    Spanish (by Jorge Bueno Magdalena), German (by Harald Weiss and J&ouml;rg Frommann), Dutch (by Ronald Knot), Russian (by Iaroslav Andrusiak),
                    Italian (Michele Petrecca), French (by JohnDillinger).</li>
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
        <ul class="list">
            <li>WAP interface allows you to control Subsonic from any mobile phone or PDA.</li>
            <li>Supports multiple simultaneous players. The playlist for any player can be managed from any location.</li>
            <li>In addition to streaming, single files or entire directories may be downloaded from Subsonic.</li>
            <li>Files can be uploaded to Subsonic. Zip-files can be automatically unpacked.</li>
        </ul>
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
    <a name="userfriendly"></a>

    <div class="heading">User-friendly <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <ul class="list">
            <li>Lean web interface optimized for constrained bandwidth environments and efficient browsing through large music collections.</li>
            <li>Free-text search functionality.</li>
            <li>Displays cover art, including images embedded in ID3 tags.</li>
            <li>You can assign ratings and comments to albums. View best liked and most played albums.</li>
            <li>Common playlist features (add, remove, rearrange, repeat, shuffle, undo). Playlist can be managed by server or player.</li>
        </ul>
    </div>
</div>

<div class="featureitem">
    <a name="secure"></a>

    <div class="heading">Secure <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <ul class="list">
            <li>Users must log in using a username and password. Users can be assigned different privileges.</li>
            <li>You can specify a upload/download bandwidth limit.</li>
            <li>Supports HTTPS/SSL encryption.</li>
            <li>Supports authentication in LDAP and Active Directory.</li>
        </ul>
    </div>
</div>

<div class="featureitem">
    <a name="Extras"></a>

    <div class="heading">Extras <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot">
            <img src="inc/img/features/podcast.png" alt=""/>
        </div>
        <div class="description">
            <ul class="list">
                <li>Download Podcasts with the integrated Podcast receiver.</li>
                <li>Support for Internet TV and radio stations.</li>
                <li>Stream directly to your mobile phone, using the Subsonic Mobile Player.</li>
            </ul>
        </div>
    </div>
</div>

</div>


<div id="side-col">
    <div class="sidebox">
        <h2>Features</h2>
        <ul class="list">
            <li><a href="#mediasupport">Media support</a></li>
            <li><a href="#customize">Customize</a></li>
            <li><a href="#flexible">Flexible</a></li>
            <li><a href="#integrate">Integrate</a></li>
            <li><a href="#userfriendly">User-friendly</a></li>
            <li><a href="#secure">Secure</a></li>
            <li><a href="#Extras">Extras</a></li>
        </ul>
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
