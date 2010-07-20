<%@ page import="java.net.URL" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%! String current = "apps"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1 class="bottomspace">Subsonic Apps</h1>


            <p>In addition to the normal web interface, there are several other Subsonic applications:</p>

            <div class="floatcontainer margin10-t margin10-b">
                <ul class="stars column-left">
                    <li><a href="#android">Subsonic</a> for Android</li>
                    <li><a href="#subair">SubAir</a> for desktops</li>
                </ul>
                <ul class="stars column-right">
                    <li><a href="#zsubsonic">Z-Subsonic</a> for iPhone</li>
                    <li><a href="#isub">iSub</a> for iPhone</li>
                </ul>
            </div>

            <p>
                Please note that most of the apps are made by third-party developers, and are not maintained by
                the Subsonic project. Some apps are commercial, while some are available for free.
            </p>
            <p>Also note that after a 30-day trial period you need a license key to use the apps.
                You get a license key by giving a donation to the Subsonic project. The license never expires and is
                valid for all current and future apps. By donating you also get other benefits; see info box on the
                right.
            </p>
            <p>
                Interested in making your own Subsonic app? Check out the <a href="api.jsp">API</a>.
            </p>

            <div class="featureitem">
                <a name="android"></a>

                <div class="heading">Subsonic for Android <a href="#top" class="backtotop" title="Top"><img src="inc/img/top.gif" alt="" height="16" width="16"/></a></div>
                <div class="content">
                    <div class="wide-content">
                        <p>
                            <img src="inc/img/android.png" alt="Android" class="img-left"/>
                            Stream and download music from your home computer to your Android phone.
                            All your music - anywhere, anytime!
                        </p>
                        <p>
                            Developed and maintained by Sindre Mehus, the author of Subsonic. Available for free
                            on Android Market, or download it <a href="http://sourceforge.net/projects/subsonic/files/android">here</a>.
                        </p>

                        <p>
                            Supports streaming, downloading, playlists, album art and searching. For improved performance, music you have
                            listened to is cached on the phone. An offline mode is also available for when you are outside 3G/Wi-Fi coverage.
                        </p>

                        <a href="inc/img/screenshots/screen12.png"><img src="inc/img/screenshots/thumb12.png" alt="" style="padding:3px"/></a>
                        <a href="inc/img/screenshots/screen13.png"><img src="inc/img/screenshots/thumb13.png" alt="" style="padding:3px"/></a>
                        <a href="inc/img/screenshots/screen14.png"><img src="inc/img/screenshots/thumb14.png" alt="" style="padding:3px"/></a>
                        <a href="inc/img/screenshots/screen15.png"><img src="inc/img/screenshots/thumb15.png" alt="" style="padding:3px"/></a>

                        <p>
                           Also check out the <a href="screenshots.jsp#video">video</a>!
                        </p>
                    </div>
                </div>
            </div>

            <div class="featureitem">
                <a name="subair"></a>

                <div class="heading">SubAir <a href="#top" class="backtotop" title="Top"><img src="inc/img/top.gif" alt="" height="16" width="16"/></a></div>
                <div class="content">
                    <div class="wide-content">
                        <p>
                            <img src="inc/img/air.png" alt="Adobe AIR" class="img-left"/>
                            <a href="http://www.nonpixel.com/subair/">SubAir</a> is a rich desktop application for Subsonic implemented with Adobe&copy; AIR.
                            Works with Windows, Mac and Linux.
                        </p>
                        <a href="inc/img/screenshots/screen16.png"><img src="inc/img/screenshots/thumb16.png" alt="" class="img-center"/></a>
                        <p>
                            Developed and maintained by <a href="http://www.nonpixel.com/">Jim Resnowski</a>, and
                            <a href="http://www.nonpixel.com/subair/">provided free or charge</a>.
                        </p>
                    </div>
                </div>
            </div>

            <div class="featureitem">
                <a name="zsubsonic"></a>

                <div class="heading">Z-Subsonic <a href="#top" class="backtotop" title="Top"><img src="inc/img/top.gif" alt="" height="16" width="16"/></a></div>
                <div class="content">
                    <div class="wide-content">
                        <p>
                            <img src="inc/img/appstore.png" alt="App Store" class="img-left"/>
                            <a href="http://z-subsonic.com/">Z-Subsonic</a> is an iPhone app developed by Olusola Abiodun, and is
                            available for purchase on the <a href="http://itunes.apple.com/us/app/z-subsonic/id358344265?mt=8">App Store</a>.
                        </p>
                            <a href="inc/img/screenshots/screen17.png"><img src="inc/img/screenshots/thumb17.png" alt="" style="margin-left:30px;padding:3px"/></a>
                            <a href="inc/img/screenshots/screen18.png"><img src="inc/img/screenshots/thumb18.png" alt="" style="padding:3px"/></a>
                            <a href="inc/img/screenshots/screen19.png"><img src="inc/img/screenshots/thumb19.png" alt="" style="padding:3px"/></a>

                        <ul class="list">
                            <li>Access your entire music library from anywhere on your iPhone or iPod regardless of the size of your music collection.</li>
                            <li>Play song formats that the iPhone/iPod will not normally play e.g. wma, flac, ogg.</li>
                            <li>Delete or change song priority on the Now Playing list while songs are playing.</li>
                            <li>Songs start with little or no delay.</li>
                            <li>Queue the same song multiple times.</li>
                            <li>Load playlists stored on the Subsonic server.</li>
                            <li>Easily select any of 5 Subsonic server connections.</li>
                            <li>SSL support.</li>
                            <li>Favorites.</li>
                            <li>Complete artist, album and song info caching.</li>
                        </ul>

                        <p>
                           See the <a href="http://www.youtube.com/watch?v=yFzM7-rfINM">video on YouTube</a>.
                        </p>

                        <p>
                            Support: <a href="mailto:helpdesk@z-subsonic.com">helpdesk@z-subsonic.com</a> &ndash;
                            Forum: <a href="http://z-subsonic.com/z-subsonic-forum">http://z-subsonic.com/z-subsonic-forum</a>
                        </p>
                        <a href="http://itunes.apple.com/us/app/z-subsonic/id358344265?mt=8"><img src="inc/img/available_on_appstore.png" alt="" class="img-center"/></a>


                    </div>
                </div>
            </div>

            <div class="featureitem">
                <a name="isub"></a>

                <div class="heading">iSub <a href="#top" class="backtotop" title="Top"><img src="inc/img/top.gif" alt="" height="16" width="16"/></a></div>
                <div class="content">
                    <div class="wide-content">
                        <p>
                            <img src="inc/img/appstore.png" alt="App Store" class="img-left"/>
                            <a href="http://einsteinx2.com/projects/isub/">iSub</a> is an iPhone app developed by Ben Baron, and is
                            available for purchase on the <a href="http://itunes.apple.com/us/app/isub-music-streamer/id362920532?mt=8">App&nbsp;Store</a>.
                        </p>
                        <a href="inc/img/screenshots/screen20.png"><img src="inc/img/screenshots/thumb20.png" alt="" style="margin-left:40px;padding:15px"/></a>
                        <a href="inc/img/screenshots/screen21.png"><img src="inc/img/screenshots/thumb21.png" alt="" style="padding:15px"/></a>
                        <a href="inc/img/screenshots/screen22.png"><img src="inc/img/screenshots/thumb22.png" alt="" style="padding:15px"/></a>

                        <ul class="list">
                            <li>iPod app style browsing.</li>
                            <li>Artist search.</li>
                            <li>Browsing multilevel directories.</li>
                            <li>Caching of all browsed directories for speedy browsing.</li>
                            <li>Album art while browsing.</li>
                            <li>Full size album art in player view.</li>
                            <li>Resuming music when interrupted by a call or text or when closing the app using the home button while a song is playing.</li>
                            <li>Each music folder is a playlist automatically so when you select a track from an album it will continue to play the rest of the tracks in that album.</li>
                            <li>Shuffle, Repeat 1, and Repeat All when playing an album.</li>
                            <li>Detailed track information by tapping the cover art.</li>
                            <li>Skipping through tracks.</li>
                        </ul>
                        <p>
                            Support: <a href="mailto:support@einsteinx2.com">support@einsteinx2.com</a>
                        </p>
                        <a href="http://itunes.apple.com/us/app/isub-music-streamer/id362920532?mt=8"><img src="inc/img/available_on_appstore.png" alt="" class="img-center"/></a>

                    </div>
                </div>
            </div>
        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
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
