/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.service;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * This class implements a simple player based on BasicPlayer.
 * BasicPlayer is a threaded class providing most features
 * of a music player. BasicPlayer works with underlying JavaSound
 * SPIs to support multiple audio formats. Basically JavaSound supports
 * WAV, AU, AIFF audio formats. Add MP3 SPI (from JavaZOOM) and Vorbis
 * SPI( from JavaZOOM) in your CLASSPATH to play MP3 and Ogg Vorbis file.
 */
public class BasicPlayerTest implements BasicPlayerListener {
    private PrintStream out = null;

    /**
     * Entry point.
     *
     * @param args filename to play.
     */
    public static void main(String[] args) {
        BasicPlayerTest test = new BasicPlayerTest();
        test.play("d:\\music\\Muse\\Absolution - 2003\\11 - Endlessly.mp3");
    }

    /**
     * Contructor.
     */
    public BasicPlayerTest() {
        out = System.out;
    }

    public void play(String filename) {
        // Instantiate BasicPlayer.
        BasicPlayer player = new BasicPlayer();

        // BasicPlayer is a BasicController.

        BasicController control = (BasicController) player;
        // Register BasicPlayerTest to BasicPlayerListener events.
        // It means that this object will be notified on BasicPlayer
        // events such as : opened(...), progress(...), stateUpdated(...)
        player.addBasicPlayerListener(this);

        try {
            // Open file, or URL or Stream (shoutcast) to play.
            control.open(new File(filename));

            // Start playback in a thread.
            control.play();

            // Set Volume (0 to 1.0).
            // setGain should be called after control.play().
            control.setGain(0.85);

            // Set Pan (-1.0 to 1.0).
            // setPan should be called after control.play().
            control.setPan(0.0);

            // If you want to pause/resume/pause the played file then
            // write a Swing player and just call control.pause(),
            // control.resume() or control.stop().
            // Use control.seek(bytesToSkip) to seek file
            // (i.e. fast forward and rewind). seek feature will
            // work only if underlying JavaSound SPI implements
            // skip(...). True for MP3SPI (JavaZOOM) and SUN SPI's
            // (WAVE, AU, AIFF).

        }
        catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open callback, stream is ready to play.
     * <p/>
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag,
     * id3v2/id3v1 (for MP3 only), comments (for Ogg Vorbis), ...
     *
     * @param stream     could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    @Override
    public void opened(Object stream, Map properties) {
        // Pay attention to properties. It's useful to get duration,
        // bitrate, channels, even tag such as ID3v2.
        display("opened : " + properties.toString());
    }

    /**
     * Progress callback while playing.
     * <p/>
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     *
     * @param bytesread    from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata      PCM samples.
     * @param properties   audio stream parameters.
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        // Pay attention to properties. It depends on underlying JavaSound SPI
        // MP3SPI provides mp3.equalizer.
//        display("progress : " + properties.toString());
    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *
     * @param event
     */
    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        // Notification of BasicPlayer states (opened, playing, end of media, ...)
        display("stateUpdated : " + event.toString());
        if (event.getCode() == BasicPlayerEvent.STOPPED) {
            System.exit(0);
        }
    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     *
     * @param controller : a handle to the player
     */
    @Override
    public void setController(BasicController controller) {
        display("setController : "+controller);
	}

	public void display(String msg) {
        if (out != null) {
            out.println(msg);
        }
	}

}
