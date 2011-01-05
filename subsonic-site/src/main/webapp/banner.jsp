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
            setTimeout(function() {slideTo(currentSlide + 1);}, 10000);
        }
        slideTo(0);
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
                    <div class="small">Non-stop music and video streaming.</div>
                </div>
            </div>
        </div>
        <div class="slide2 slide">
            <div class="slidecontent">
                <img src="inc/img/banner/apps.png" alt="" class="screenshot"/>

                <div class="title">
                    <div class="large">Don't leave home without it</div>
                    <div class="small"><a href="apps.jsp">Apps</a> available for Android, iPhone and Windows&nbsp;Phone&nbsp;7.</div>
                </div>
            </div>
        </div>

        <div class="slide3 slide">
            <div class="slidecontent">
                <img src="inc/img/banner/screenshot.png" alt="" class="screenshot"/>

                <div class="title">
                    <div class="large">The most complete personal streaming system</div>
                    <div class="small">Subsonic comes packed with features.</div>
                </div>
                <div class="text">
                    Podcast receiver, jukebox mode, on-the-fly downsampling and conversion,
                    multiple frontends, highly configurable, full support for tags, lyrics and album art, open API
                    and <a href="features.jsp">much more</a>.
                </div>
            </div>
        </div>
        <div class="slide4 slide">
            <div class="slidecontent">
                <img src="inc/img/banner/video.png" alt="" class="screenshot"/>

                <div class="title">
                    <div class="large">Coming soon</div>
                    <div class="small">Stream all your movies too!</div>
                    <img src="inc/img/banner/android-video.png" alt="" style="margin-top:30px;margin-left:90px;"/>
                </div>
            </div>
        </div>
        <%--<div class="slide5 slide">--%>
            <%--<div class="slidecontent">--%>
                <%--<img src="inc/img/banner/wp7.png" alt="" class="screenshot"/>--%>

                <%--<div class="title">--%>
                    <%--<div class="large">No strings attached</div>--%>
                    <%--<div class="small">Relax to your favourite tunes no matter where you are.</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>
</div>
<hr/>