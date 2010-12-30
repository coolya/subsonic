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
                <a href="http://shop.o2.co.uk/update/iphone.html">I phone 4</a> via iTunes or high-quality rips from CDs.
                The transcoding is performed on-the-fly and doesn't require any disk usage.
            </p>

            <p>
                The actual transcoding is done by third-party command line programs which must be installed in <code>c:\subsonic\transcode</code> (on Windows), or
                <code>/var/subsonic/transcode</code> (other operating systems).
            </p>

            <p>
                Up to three transcoders can be chained together. For instance, to convert FLAC to MP3 you would typically use a FLAC decoder which converts to WAV,
                and chain it with a WAV to MP3 encoder.
            </p>

            <div class="featureitem">
                <div class="heading">Transcoding pack for Windows</div>
                <div class="content">
                    <div class="wide-content">

                        <p>
                            A transcoding pack for Windows with a selection of codecs can be
                            <a href="http://prdownloads.sourceforge.net/subsonic/transcoding-windows-1.1.zip"><b>downloaded here</b></a>.
                            The list of supported media types includes:
                        </p>

                        <div class="floatcontainer margin10-t margin10-b">
                            <ul class="stars column-left">
                                <li>Ogg Vorbis</li>
                                <li>FLAC (Free Lossless Audio Codec)</li>
                                <li>AAC/MP4</li>
                                <li>WMA</li>
                                <li>WAV</li>
                            </ul>
                            <ul class="stars column-right">
                                <li>APE (Monkey's Audio)</li>
                                <li>MPC (Musepack)</li>
                                <li>WavPack</li>
                                <li>SHN (Shorten)</li>
                            </ul>
                        </div>

                    </div>
                </div>
            </div>

            <h2 class="div">Adding custom transcoders</h2>
            <p>
                You can add your own custom transcoder given that it fulfills the following requirements:
            </p>
            <ul class="list">
                <li>It must have a command line interface.</li>
                <li>It must be able to send output to stdout.</li>
                <li>If used in transcoding step 2 or 3, it must be able to read input from stdin.</li>
            </ul>

            <h2 class="div">Sample configuration</h2>
            <p>
                This is the default transcoding configuration for the Windows transcoding pack.
                Note that "%s" is substituted with the path of the original file at run-time, and "%b" is substituted with
                the max bitrate of the player. "%t", "%a" and "%l" are substituted with the song's title, artist and album.
            </p>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="bottomspace">
                <tr>
                    <th class="transcoding-heading">From</th>
                    <th class="transcoding-heading">To</th>
                    <th class="transcoding-heading">Step 1</th>
                    <th class="transcoding-heading">Step 2</th>
                </tr>
                <tr class="table-altrow">
                    <td class="transcoding">wav</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr>
                    <td class="transcoding">ogg</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr class="table-altrow">
                    <td class="transcoding">wma</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr>
                    <td class="transcoding">flac</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr class="table-altrow">
                    <td class="transcoding">ape</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr>
                    <td class="transcoding">m4a</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr class="table-altrow">
                    <td class="transcoding">aac</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr>
                    <td class="transcoding">mpc</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr class="table-altrow">
                    <td class="transcoding">wv</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
                <tr>
                    <td class="transcoding">shn</td>
                    <td class="transcoding">mp3</td>
                    <td class="transcoding">ffmpeg -i %s -f wav -</td>
                    <td class="transcoding">lame -b %b --tt %t --ta %a --tl %l -S --resample 44.1 - -</td>
                </tr>
            </table>

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
