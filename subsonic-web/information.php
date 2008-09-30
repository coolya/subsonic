<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<?php
    $current = 'information';
    include("header.php");
?>

<body class="section-information page-free">

<a name="top"/>

<div id="container">
<?php include("menu.php"); ?>

<div id="content">
<div id="main-col">
<h1>Free Version Features</h1>

<div class="featureitem">
    <a name="manage"></a>

    <div class="heading">Manage Your Music <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/manage.jpg" alt="MediaMonkey Manager UI"/></div>
        <div class="description">
            <p>Manage a music library from 100 to 50,000+ audio files and playlists<span class="important">*</span>;
                whether Rock, Classical, Audiobooks, or Podcasts; whether they&rsquo;re located on your hard drive, CDs,
                or a network. Organize, browse, or search music by Genre, Artist, Year, Rating, etc., and never waste
                your time trying to find mp3s you know you have.</p>

            <p>MediaMonkey is the music organizer for the serious collector.</p>

            <p><span class="important">*</span>
                <small>Supports MP3, AAC (M4A), OGG, WMA, FLAC, MPC, WAV, CDA, M3U, PLS, etc.</small>
            </p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="synchronize"></a>

    <div class="heading">Sync <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                        alt="Back To Top" height="16"
                                                                                        width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/sync.jpg" alt="MediaMonkey Sync UI"/></div>
        <div class="description">
            <p>Sync with iPhones, iPods, and most any MP3 phone or Audio Device. Just click the Sync button to sync
                tracks, podcasts, and related properties such as Album Art, ratings, play history, and playlists with a
                <a href="/addons/device/">broad range of portable devices</a><span class="important">*</span>. With
                Volume Leveling, the tracks will even play back on your device at consistent volumes!</p>

            <p>Try MediaMonkey if you want painless synchronization with almost any portable player.</p>

            <p><span class="important">*</span>
                <small>The information that can be synced is device and plug-in dependent.</small>
            </p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="record"></a>
    <a name="convert"></a>

    <div class="heading">Record &amp; Convert <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/rip.jpg" alt="MediaMonkey Rip CD UI"/></div>
        <div class="description">
            <p>Record CDs to your hard drive using the high quality
                <a href="http://lame.sourceforge.net/">LAME MP3 encoder</a><span class="important">*</span>,
                <a href="http://sourceforge.net/projects/faac">M4A encoder</a><span class="important">*</span>,
                <a href="http://www.vorbis.com/faq.psp">OGG encoder</a>,
                <a href="http://www.microsoft.com/windows/windowsmedia/9series/codecs.aspx">WMA encoder</a>, or
                <a href="http://flac.sourceforge.net/">FLAC encoder</a>). MediaMonkey&rsquo;s CD Ripper copies the CDs,
                automatically filling in track properties via <a href="http://www.freedb.org/">freedb</a>.</p>

            <p>Convert audio files from almost any audio format and preserve tag information<span
                    class="important">**</span>, using the MP3 converter, AAC/M4A converter, OGG converter, WMA
                converter, WAV converter and FLAC converters.</p>

            <p><span class="important">*</span>
                <small>The MP3 encoder is limited to 30 days in the free version. The M4A encoder is limited to 30 days
                    unless the encoder is purchased separately.<br/>
                    <span class="important">**</span> Tags are preserved for MP3, AAC/M4A, OGG, WMA, WAV, APE, FLAC or
                    MPC.
                </small>
            </p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="lookup"></a>
    <a name="freedb"></a>

    <div class="heading">Identify Tracks <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/auto-tag-from-web.jpg"
                                     alt="MediaMonkey Auto-tag from Web UI"/></div>
        <div class="description">
            <p>Automatically identify tracks that are missing information, whose tags are not synchronized, or that are
                duplicated elsewhere.</p>

            <p>Fix Tags with Automatic Lookup and Tagging of album art and other track information from <a
                    href="http://www.amazon.com">Amazon</a>.</p>

            <p>Lookup CD information on <a href="http://www.freedb.org">Freedb</a> or via <a
                    href="http://www.ncf.carleton.ca/%7Eaa571/cdtext.htm">CD-TEXT</a>, and find missing track details
                through music-related sites such as <a href="http://www.allmusic.com">Allmusic</a>.</p>

            <p>If you have a large music collection, MediaMonkey will help you update your tags quickly and
                accurately.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="tag"></a>

    <div class="heading">Tag <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                       alt="Back To Top" height="16"
                                                                                       width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/tag.jpg" alt="MediaMonkey Tagger UI"/></div>
        <div class="description">
            <p>Tag music easily using industry-standard formats. MediaMonkey includes an MP3 Tag editor (an ID3 tag
                editor supporting ID3v1 &amp; <a href="http://www.id3.org/">ID3v2</a>), AAC tag editor (for M4A/M4P
                files), an OGG tag editor (for OGG and FLAC files), a WMA tag editor, an APE2 tag editor (for APE
                files), and a WAV tag editor. Update and correct mislabeled tracks via drag-and-drop from one
                artist/genre to another or use the categorization toolbar allows you to quickly set ratings, mood, and
                other information as you&rsquo;re listening to music. Automatically and intelligently tag files based on
                filenames with the Auto-Tagger.</p>

            <p>The serious or classical music collector can also assign a broad range of more advanced attributes, such
                as composer, original year and album, lyrics, etc.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="rename"></a>

    <div class="heading">Organize &amp; Rename <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/auto-organize.jpg" alt="MediaMonkey Auto-organize UI"/>
        </div>
        <div class="description">
            <p>Automatically organize and rename files on your hard drive into a logical hierarchy. Instead of storing
                your files haphazardly all over your hard drive, MediaMonkey&rsquo;s auto-organizer can organize them
                into folders and filenames of your choice based on attributes such as artist, album, track title and
                track number.</p>

            <p>If you need to organize a music collection exceeding 10,000 files, MediaMonkey is the music organizer for
                you.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="playlists"></a>

    <div class="heading">Playlists <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                             alt="Back To Top"
                                                                                             height="16"
                                                                                             width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/auto-dj.jpg" alt="MediaMonkey Auto-DJ UI"/></div>
        <div class="description">
            <p>Create Playlists with ease. Just drag and drop your tunes to mix mp3s and other files from your Library,
                create AutoPlaylists based on simple search criteria, or use the Auto-DJ to automatically create a mix
                for you. Your playlists will remain intact even when you rename files and/or retag them. If you're using
                third-party devices or players, MediaMonkey can export playlists to m3u files.</p>

            <p>Whether you want to create a casual playlist or a professional DJ party mix, MediaMonkey helps you do it
                with ease.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="burn"></a>

    <div class="heading">Burn <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                        alt="Back To Top" height="16"
                                                                                        width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/burn.jpg"
                                     alt="MediaMonkey Burn Audio CD and MP3 CD/DVD UI"/></div>
        <div class="description">
            <p>Burn Audio CDs using the integrated burner powered by <a href="http://www.primoburner.com">PrimoBurner&#0153;</a>.
                Just select any tracks or playlists and quickly create CD masterpieces using the most reliable CD
                burning engine around.</p>

            <p>Burn MP3 (Data) CD/DVDs to backup your entire music collection or for playback on any MP3-capable CD or
                DVD players. There&rsquo;s no need to manually copy and arrange directories &mdash; simply choose which
                Artists, Albums, or Playlists to include, set the disc&rsquo;s format, and burn!</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="podcasts"></a>

    <div class="heading">Podcast Catcher <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/podcast.jpg" alt="MediaMonkey Podcast Catcher UI"/>
        </div>
        <div class="description">
            <p>Download audio content using the new integrated Podcatcher. Define custom subscription rules for any
                audio podcast, and let MediaMonkey take care of downloading and syncing the content.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="play"></a>

    <div class="heading">Play <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                        alt="Back To Top" height="16"
                                                                                        width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/play.jpg" alt="MediaMonkey Player and Tray Player UI"/>
        </div>
        <div class="description">
            <p>Play CDs and digital audio files (MP3, AAC/M4A, OGG, WMA<span class="important">*</span>, MPC, APE, FLAC,
                WAV, etc.) with MediaMonkey, or use MediaMonkey to manage your library in conjunction with <a
                    href="http://www.winamp.com/">Winamp</a> as the player. Adjust volume levels automatically (using <a
                    href="http://replaygain.hydrogenaudio.org/">Replay Gain</a> and <a
                    href="http://mp3gain.sourceforge.net/faq.php">MP3 Gain</a> technology), and fine-tune your audio
                using an equalizer and hundreds of available dsp audio-effect plug-ins. </p>

            <p>If you&rsquo;re a serious audiophile, MediaMonkey gives you all the quality you need.</p>

            <p><span class="important">*</span>
                <small>Includes support for version 9 DRM.</small>
            </p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="party"></a>

    <div class="heading">Party <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                         alt="Back To Top" height="16"
                                                                                         width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/party.jpg"
                                     alt="MediaMonkey Party Mode with Album Art UI"/></div>
        <div class="description">
            <p>Use MediaMonkey for Parties or other public places with Party Mode, which allows users to make requests
                while protecting your library from being modified.</p>

            <p>Use the Auto-DJ to automatically play tracks when manually selected tracks run out.</p>

            <p>If you need to manage music for a large audience, MediaMonkey is the jukebox for you.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="visualize"></a>

    <div class="heading">Visualize <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                             alt="Back To Top"
                                                                                             height="16"
                                                                                             width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/visualizations.jpg" alt="MediaMonkey Visualization"/>
        </div>
        <div class="description">
            <p>Visualize your music with funky trance-like effects powered by Milkdrop and hundreds of other <a
                    href="/addons/visualizations/">visualization plug-ins</a>.</p>

            <p>When you want to totally immerse yourself in your music, MediaMonkey will be there for you.</p>
        </div>
    </div>
</div>


<div class="featureitem">
    <a name="statistics"></a>

    <div class="heading">Reports and Statistics <a href="#top" class="backtotop" title="Back To Top"><img
            src="inc/img/top.gif" alt="Back To Top" height="16" width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/statistics.jpg" alt="MediaMonkey Statistics Report"/>
        </div>
        <div class="description">
            <p>Create <a href="/files/mediamonkey-statistics.htm">Statistics</a> and other <a
                    href="/files/dj-playlist.htm">Reports</a> of your music collection as Excel, html, or xml files, so
                that you can show it to others.</p>

            <p>If you're anal-retentive about your music, MediaMonkey gives you all the reports you'll need.</p>
        </div>
    </div>
</div>

<div class="featureitem">
    <a name="customize"></a>

    <div class="heading">Customize <a href="#top" class="backtotop" title="Back To Top"><img src="inc/img/top.gif"
                                                                                             alt="Back To Top"
                                                                                             height="16"
                                                                                             width="16"/></a></div>
    <div class="content">
        <div class="screenshot"><img src="inc/img/screenshots/customize.jpg" alt="MediaMonkey Skinned interface"/>
        </div>
        <div class="description">
            <p><a href="/develop/">Customize MediaMonkey</a> with Skins, visualizations, plug-ins and scripts to make it
                do what you want it to. Integrate it with other applications such as sound editors, create customized
                reports, customize the view, or create custom Auto-DJ rules. You can <a
                    href="/wiki/index.php/Scripting">download scripts</a> created by other users, or write your own;
                either way, you can easily extend MediaMonkey&rsquo;s functionality.</p>

            <p>If you&rsquo;re a control freak, MediaMonkey gives you all the control you'll want.</p>
        </div>
    </div>
</div>


</div>

<div id="side-col">
    <div class="sidebox">
        <h2>Free Features</h2>
        <ul class="list">
            <li><a href="#manage">Manage Your Music</a></li>
            <li><a href="#syncronize">Sync</a></li>
            <li><a href="#record">Record &amp; Convert</a></li>
            <li><a href="#lookup">Identify Tracks</a></li>
            <li><a href="#tag">Tag</a></li>
            <li><a href="#rename">Organize &amp; Rename</a></li>
            <li><a href="#playlists">Playlists</a></li>
            <li><a href="#burn">Burn</a></li>
            <li><a href="#podcasts">Podcast Catcher</a></li>
            <li><a href="#play">Play</a></li>
            <li><a href="#party">Party</a></li>
            <li><a href="#visualize">Visualize</a></li>
            <li><a href="#statistics">Reports and Statistics</a></li>
            <li><a href="#customize">Customize</a></li>
        </ul>
    </div>
    <div class="sidebox sidenews"><h2>Recent News</h2>
        <ul class="list">
            <li><a href="http://www.mediamonkey.com/news/2008/08/11/support-news/">Support News</a></li>
            <li>
                <a href="http://www.mediamonkey.com/news/2008/06/19/mediamonkey-303-music-manager-launches-supports-100000-tracks/">MediaMonkey
                    3.0.3 Music Manager Launches, Supports 100,000+ Tracks</a></li>
            <li><a href="http://www.mediamonkey.com/news/2008/02/01/mediamonkey-302-released/">MediaMonkey 3.0.2
                Released!</a></li>
            <li><a href="http://www.mediamonkey.com/news/2007/12/25/mediamonkey-301-released/">MediaMonkey 3.0.1
                Released!</a></li>
            <li><a href="http://www.mediamonkey.com/news/2007/01/30/5/">MediaMonkey 2.5.5 Released!</a></li>
        </ul>
    </div>
</div>
<div class="clear"></div>
</div>
<hr/>
<div id="footer">
    <a href="http://www.cleverstarfish.com/" id="starfish"><img src="inc/img/cleverstarfish.gif"
                                                                alt="Site by Clever Starfish"/></a>

    <div class="footercontent">
        <div>&copy; Copyright 2008 Ventis Media</div>
        <div><a href="http://www.mediamonkey.com/sitemap/">Sitemap</a> | <a href="http://www.mediamonkey.com/contact/">Contact</a>
            | <a href="http://www.mediamonkey.com/privacy/">Privacy</a> | <a
                href="http://www.mediamonkey.com/affiliates/">Affiliates</a></div>
    </div>
</div>
</div>


</body>
</html>
