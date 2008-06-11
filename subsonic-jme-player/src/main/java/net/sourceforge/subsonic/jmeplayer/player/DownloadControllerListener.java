package net.sourceforge.subsonic.jmeplayer.player;

/**
 * @author Sindre Mehus
 */
public interface DownloadControllerListener {
    void stateChanged(int state);

    void bytesRead(long n);
}