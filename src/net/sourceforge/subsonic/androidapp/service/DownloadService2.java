/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.service;

import java.util.List;

import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.DownloadFile;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public interface DownloadService2 {

    void download(List<MusicDirectory.Entry> songs, boolean save, boolean play);

    void clear();

    List<DownloadFile> getDownloads();

    DownloadFile getDownloadAt(int index);

    int getCurrentPlayingIndex();

    void play(int index);

    void seekTo(int position);

    void previous();

    void next();

    void pause();

    void start();

    StreamService.PlayerState getPlayerState();
}
