/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.subsonic.jmeplayer;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;


/**
 * This is a demo midlet to show the basic audio functionalities, to
 * play wave file, tone, tone sequence from http, resource jar file
 * and record store.
 */
public class AudioPlayer extends MIDlet implements CommandListener {

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

}
