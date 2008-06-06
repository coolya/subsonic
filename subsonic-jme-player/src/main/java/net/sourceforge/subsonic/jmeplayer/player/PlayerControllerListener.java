package net.sourceforge.subsonic.jmeplayer.player;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 */
public interface PlayerControllerListener {

    void stateChanged(int state);

    void songChanged(MusicDirectory.Entry entry);

    void error(String message);

    void busy(boolean busy);

    void bytesRead(long n);
}
