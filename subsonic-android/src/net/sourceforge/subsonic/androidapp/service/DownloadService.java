/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;

import java.util.List;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public interface DownloadService {

    void download(List<MusicDirectory.Entry> songs, boolean save, boolean autoplay);

    void clear();

    List<DownloadFile> getDownloads();

    DownloadFile getCurrentPlaying();

    DownloadFile getCurrentDownloading();

    DownloadFile getDownloadAt(int index);

    void play(DownloadFile file);

    void play(int index);

    void seekTo(int position);

    void previous();

    void next();

    void pause();

    void start();

    PlayerState getPlayerState();

    int getPlayerPosition();

    void delete(List<MusicDirectory.Entry> selectedSongs);

    DownloadFile forSong(MusicDirectory.Entry song);
}
