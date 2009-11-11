/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import android.content.Context;
import net.sourceforge.subsonic.androidapp.util.Util;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class MusicServiceFactory {

    private static final MusicService REST_MUSIC_SERVICE = new CachedMusicService(new RESTMusicService());
    private static final MusicService OFFLINE_MUSIC_SERVICE = new CachedMusicService(new OfflineMusicService());

    public static MusicService getMusicService(Context context) {
        return Util.isOffline(context) ? OFFLINE_MUSIC_SERVICE : REST_MUSIC_SERVICE;
    }
}
