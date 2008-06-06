package net.sourceforge.subsonic.jmeplayer;


import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * This is a demo midlet to show the basic audio functionalities, to
 * play wave file, tone, tone sequence from http, resource jar file
 * and record store.
 */
public class AudioPlayer extends MIDlet implements CommandListener {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private Player player;

    public AudioPlayer() {
    }

    /**
     * Called when this MIDlet is started for the first time,
     * or when it returns from paused mode.
     * If a player is visible, and it was playing
     * when the MIDlet was paused, call its playSound method.
     */
    public void startApp() {

        try {
            InputStream in = Connector.openInputStream("http://www.ericgiguere.com/nanoxml/index.html");
            System.out.println(toString(in));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] mediaTypes = Manager.getSupportedContentTypes(null);
        for (int i = 0; i < mediaTypes.length; i++) {
            System.out.println(mediaTypes[i]);
        }

        try {
//            player = Manager.createPlayer("http://www.nch.com.au/acm/8k16bitpcm.wav");
            player = Manager.createPlayer("http://www.sotone.com/samples/!2-A.Brain_Bach.4th.mvmt.mp3");
            System.out.println("Player created.");
            player.start();
            System.out.println("Player started.");
        } catch (Exception x) {
            x.printStackTrace();
        }

//        InputStream is = getClass().getResourceAsStream("/example.mp3");
//        try{
//            player = Manager.createPlayer(is, "audio/mpeg");
//            player.start();
//        }catch(Exception e){}
    }

    /**
     * Called when this MIDlet is paused.
     * If the player GUI is visible, call its pauseSound method.
     * For consistency across different VM's
     * it's a good idea to stop the player if it's currently
     * playing.
     */
    public void pauseApp() {
    }

    /**
     * Destroy must cleanup everything not handled
     * by the garbage collector.
     */
    public void destroyApp(boolean unconditional) {
        if (player != null) {
            try {
                player.stop();
                System.out.println("Player stopped.");
            } catch (Exception x) {
                x.printStackTrace();
            }
            try {
                player.deallocate();
                System.out.println("Player deallocated.");
            } catch (Exception x) {
                x.printStackTrace();
            }
        }


    }

    public void commandAction(Command c, Displayable s) {
    }


    public static String toString(InputStream input) throws IOException {
        return new String(toByteArray(input), "UTF-8");
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
