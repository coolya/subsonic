package net.sourceforge.subsonic;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author Sindre Mehus
 */
public class Scratch {

    public Scratch() throws Exception {

        byte[] bytesA = IOUtils.toByteArray(new FileInputStream("c:/music/Samples/Hello.mp3"));
        byte[] bytesB = IOUtils.toByteArray(new FileInputStream("c:/music/Samples/Organ.mp3"));

        byte[] bytes = ArrayUtils.addAll(bytesA, bytesB);
        InputStream in = new ByteArrayInputStream(bytes);

        Player player = new Player(in);
        System.out.println("start");
        player.play();
        System.out.println("done");
    }

    public static void main(String[] args) throws Exception {
        new Scratch();
    }
}
