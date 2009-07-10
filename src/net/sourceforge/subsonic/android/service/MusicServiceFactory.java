/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android.service;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class MusicServiceFactory {

//    public static final MusicService MUSIC_SERVICE = new CachedMusicService(new XMLMusicService(new MockMusicServiceDataSource()));
    public static final MusicService MUSIC_SERVICE = new CachedMusicService(new XMLMusicService(new HTTPMusicServiceDataSource(new MockSettingsService())));

    public static MusicService getMusicService() {
        return MUSIC_SERVICE;
    }
}
