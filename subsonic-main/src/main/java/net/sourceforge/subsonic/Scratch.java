package net.sourceforge.subsonic;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;

/**
 * @author Sindre Mehus
 */
public class Scratch {

    public Scratch() throws Exception {
        InputStream in = new FileInputStream("c:/music/Samples/Drum Loop 2.mp3");
//        InputStream in = new URL("http://192.168.0.7/subsonic/stream?player=3&suffix=.mp3").openStream();
        Player player = new Player(in);
        System.out.println("start");
        player.play();
        System.out.println("done");
    }

    public static void main(String[] args) throws Exception {
        new Scratch();
    }
}
