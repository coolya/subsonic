/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.androidapp.service;

import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.domain.PlayerState;
import net.sourceforge.subsonic.androidapp.domain.RepeatMode;

import java.util.List;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public interface DownloadService {

    void download(List<MusicDirectory.Entry> songs, boolean save, boolean autoplay, boolean playNext);

    void setShufflePlayEnabled(boolean enabled);

    boolean isShufflePlayEnabled();

    void shuffle();

    RepeatMode getRepeatMode();

    void setRepeatMode(RepeatMode repeatMode);

    boolean getKeepScreenOn();

    void setKeepScreenOn(boolean screenOn);

    void clear();

    void clearIncomplete();

    int size();

    void remove(DownloadFile downloadFile);

    List<DownloadFile> getDownloads();

    int getCurrentPlayingIndex();

    DownloadFile getCurrentPlaying();

    DownloadFile getCurrentDownloading();

    void play(DownloadFile file);

    void play(int index);

    void seekTo(int position);

    void previous();

    void next();

    void pause();

    void start();

    void reset();

    PlayerState getPlayerState();

    int getPlayerPosition();

    int getPlayerDuration();

    void delete(List<MusicDirectory.Entry> songs);

    DownloadFile forSong(MusicDirectory.Entry song);

    long getDownloadListUpdateRevision();

    void setSuggestedPlaylistName(String name);

    String getSuggestedPlaylistName();
}
