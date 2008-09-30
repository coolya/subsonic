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
    <h1>MediaMonkey</h1>

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
            <li><a href="/information/">Plus much more...</a></li>
        </ul>
    </div>

    <div class="featureitem">
        <div class="heading">What others are saying...</div>
        <div class="content">
            <div class="wide-content">
                <ul id="home-reviews">
                    <li><em>&ldquo;We can see the winner: Mediamonkey pips (beats) Winamp to the post! iTunes is
                        left for dust trailing behind by 8 points.&rdquo;</em> &mdash; <strong><a
                            href="http://www.skytopia.com/project/articles/music/players.html" target="_blank">Skytopia:
                        Windows Music Manager Shootout, May 2008</a></strong></li>
                    <li><em>&ldquo;Considering the overall fit and finish of this application, it'll be tough to
                        find anything you won't like about it. Although it is definitely geared toward managing
                        extensive collections, just about anyone will appreciate the comprehensive feature
                        set.&rdquo;</em> &mdash; <strong><a href="http://download.com.com/3000-2141-10109807.html"
                                                            target="_blank">Download.com Review</a></strong></li>
                    <li><em>&ldquo;I just wanted to thank you for such an awesome piece of kit. I've used
                        (MediaMonkey) every day for six months, I couldn't be without it. It's no understatement to
                        say it's improved the quality of my life.&rdquo;</em></li>
                    <li><em>&ldquo;...When I decided to put my entire CD collection on my pc in mp3 format,
                        RealJukebox just bogged down unbearably (9000+ songs). (MediaMonkey) is the first thing I
                        tried and I have been running it nonstop since then. I have had no trouble with slowdowns,
                        freezing, stalling, and it does things that RJ doesn't do, such as normalization of volume.
                        It's fairly intuitive, more so than RJ, and it's free!&rdquo;</em> &mdash;
                        <strong>Nate</strong></li>
                </ul>
            </div>
        </div>
    </div>

</div>

<div id="side-col">
    <div class="sidebox">
        <h2>Subscribe to the News</h2>

        <form method="post" action="/news/subscribe/">
            <div><label for="s2_email">Email:</label> <input type="text" name="email" value="" size="20"
                                                             id="s2_email"/></div>
            <div><label for="s2_subscribe"><input type="radio" name="s2_action" id="s2_subscribe" value="subscribe"
                                                  checked="checked"/> Subscribe</label></div>
            <div><label for="s2_unsubscribe"><input type="radio" name="s2_action" id="s2_unsubscribe"
                                                    value="unsubscribe"/> Unsubscribe</label></div>
            <div>
                <button type="submit">Subscribe</button>
            </div>
        </form>
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
    <div class="awards-side">
        <ul id="awards-fader">
            <li><a href="http://download.com.com/3000-2141-10109807.html"><img
                    src="inc/img/reviews/cnet_top_rated.gif" alt="download.com 5-star review"/></a></li>
            <li><a href="http://www.freewaregenius.com/2006/12/06/mediamonkey/"><img
                    src="inc/img/reviews/freeware_genius_5.gif" alt="download.com 5-star review"/></a></li>
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
