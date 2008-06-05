/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer;

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
