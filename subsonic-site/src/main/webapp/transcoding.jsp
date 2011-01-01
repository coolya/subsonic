<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%! String current = "transcoding"; %>
<%@ include file="header.jsp" %>

<body>

<a name="top"/>

<div id="container">
    <%@ include file="menu.jsp" %>

    <div id="content">
        <div id="main-col">
            <h1>Transcoding</h1>
            <p>
                Transcoding is the process of converting music from one format to another. Subsonic's transcoding engine allows for streaming of
                media that would normally not be streamable, for instance lossless formats, such as the ones you'll hear on your
                <a href="http://shop.o2.co.uk/update/iphone.html">iPhone 4</a> via iTunes or high-quality rips from CDs.
                The transcoding is performed on-the-fly and doesn't require any disk usage.
            </p>

            <p>
                The actual transcoding is done by third-party command line programs which are installed in:
            </p>
            <p>
                <b>Windows</b>&nbsp;&nbsp;<code>c:\subsonic\transcode</code><br/>
                <b>Mac</b>&nbsp;&nbsp;<code>/Library/Application Support/Subsonic/transcode</code><br/>
                <b>Linux</b>&nbsp;&nbsp;<code>/var/subsonic/transcode</code>
            </p>

            <p>
                Up to three transcoders can be chained together. For instance, to convert FLAC to MP3 you would typically use a FLAC decoder which converts to WAV,
                and chain it with a WAV to MP3 encoder.
            </p>

            <h2 class="div">Recommended configuration</h2>
            <p>
                The recommended settings for audio transcoding is:
            </p>
            <p>
                <b>Step 1</b>&nbsp;&nbsp;<code>ffmpeg -i %s -f wav -</code><br/>
                <b>Step 2</b>&nbsp;&nbsp;<code>lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</code>
            </p>

            <p>
                The recommended settings for video transcoding is:
            </p>
            <p>
                <b>Step 1</b>&nbsp;&nbsp;<code>ffmpeg -ss %o -i %s -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -</code><br/>
            </p>

            <p>
                Note that "%s" is substituted with the path of the original file at run-time, and "%b" is substituted with
                the max bitrate of the player. "%t", "%a" and "%l" are substituted with the song's title, artist and album.
            </p>


            <h2 class="div">Adding custom transcoders</h2>
            <p>
                You can add your own custom transcoder given that it fulfills the following requirements:
            </p>
            <ul class="list">
                <li>It must have a command line interface.</li>
                <li>It must be able to send output to stdout.</li>
                <li>If used in transcoding step 2 or 3, it must be able to read input from stdin.</li>
            </ul>

            <h2 class="div">Troubleshooting</h2>
            <ul class="list">
                <li>Is the transcoder installed in <code>c:\subsonic\transcode</code> (or <code>/var/subsonic/transcode</code>)?</li>
                <li>Is the transcoder's enabled checkbox turned on (in Settings &gt; Transcoders)?</li>
                <li>Is the transcoder activated for your player (in Settings &gt; Players)?</li>
                <li>Is the proper file extension added to the music mask (in Settings &gt; General)?</li>
                <li>If it still doesn't work, please check the Subsonic log.</li>
            </ul>

        </div>

        <div id="side-col">
            <%@ include file="google-translate.jsp" %>
            <%@ include file="donate.jsp" %>
            <%@ include file="merchandise.jsp" %>
        </div>

        <div class="clear">
        </div>
    </div>
    <hr/>
    <%@ include file="footer.jsp" %>
</div>


</body>
</html>
