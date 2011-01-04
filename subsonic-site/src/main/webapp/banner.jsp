<script type="text/javascript" language="javascript" src="inc/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" language="javascript">

    $(document).ready(function() {
        var currentSlide = 0;
        var numberSlides = $('#bannercontent div.slide').length - 1;

        function slideTo(slideNumber) {
            currentSlide = slideNumber;
            if(currentSlide > numberSlides) {
                currentSlide = 0;
            }
            slidePosition = currentSlide * 900;
            $('#bannercontent').animate({left: '-'+slidePosition+'px'},{duration:1000});
//            setTimeout(function() {slideTo(currentSlide + 1)}, 4000);
        }
        slideTo(1);
    });
</script>

<hr/>
<div id="banner-full">
    
    <div id="bannercontent">
        <div class="slide1 slide">
            <div class="slidecontent">
                <img src="inc/img/banner/banner-01.jpg" alt="" class="screenshot"/>

                <div class="title">
                    <div class="large">The soundtrack of your life</div>
                    <div class="small">Non-stop streaming.  Don't leave your music behind.</div>
                </div>
                <%--<div class="text">Manage small to very large collections of audio files and playlists (50,000+), whether--%>
                    <%--on a hard drive, network, or CDs. Rip CDs, download podcasts, lookup missing information and album--%>
                    <%--art online, tag almost any audio format, and automatically rename/re-organize tracks on your hard--%>
                    <%--drive.--%>
                <%--</div>--%>
            </div>
        </div>
        <div class="slide2 slide">
            <div class="slidecontent">
                <img src="inc/img/banner/banner-01.jpg" alt="" class="screenshot"/>

                <div class="title">
                    <div class="large">The soundtrack of your life</div>
                    <div class="small"><a href="apps.jsp">Apps</a> available for Android, iPhone and Windows Phone&nbsp;7.</div>
                </div>
                <%--<div class="text">Manage small to very large collections of audio files and playlists (50,000+), whether--%>
                    <%--on a hard drive, network, or CDs. Rip CDs, download podcasts, lookup missing information and album--%>
                    <%--art online, tag almost any audio format, and automatically rename/re-organize tracks on your hard--%>
                    <%--drive.--%>
                <%--</div>--%>
            </div>
        </div>

        <div class="slide3 slide">
            <div class="slidecontent">
                <img src="/_inc/img/slides/touchsync.jpg" alt="Screenshot" class="screenshot"/>

                <div class="title">
                    <div class="large">Sync</div>
                    <div class="small">MediaMonkey: The Universal Music Manager</div>
                </div>
                <div class="text">Sync music and playlists to your iPod, iPhone, iPad, Android device, or most any other
                    MP3 player, and let MediaMonkey convert unsupported formats and normalize volume levels on the fly.
                </div>
            </div>
        </div>
        <div class="slide4 slide">
            <div class="slidecontent">
                <img src="/_inc/img/slides/play.gif" alt="Screenshot" class="screenshot"/>

                <div class="title">
                    <div class="large">Play</div>
                    <div class="small">MediaMonkey: Liven up your events</div>
                </div>
                <div class="text">You or your guests can choose music, while MediaMonkey's Jukebox secures your
                    collection, normalizes volume levels, and kicks in automatically with music and playlists based on
                    your criteria.
                </div>
            </div>
        </div>
        <div class="slide5 slide">
            <div class="slidecontent">
                <img src="/_inc/img/slides/customize.jpg" alt="Screenshot" class="screenshot"/>

                <div class="title">
                    <div class="large">Customize</div>
                    <div class="small">MediaMonkey: For Uncompromising Collectors</div>
                </div>
                <div class="text">Customize with Skins, plugins, visualizations, and hundreds of user-written scripts
                    that extend MediaMonkey's functionality.
                </div>
            </div>
        </div>
    </div>
</div>
<hr/>