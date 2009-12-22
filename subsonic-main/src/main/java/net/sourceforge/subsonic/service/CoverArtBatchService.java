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
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.ajax.CoverArtService;
import net.sourceforge.subsonic.domain.CoverArtReport;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Service for doing batch operations on cover art.
 *
 * @author Christian Nedregård
 */
public class CoverArtBatchService {
    private static final Logger LOG = Logger.getLogger(CoverArtBatchService.class);
    private SettingsService settingsService;
    private MusicFileService musicFileService;
    private CoverArtService coverArtService;
    private DiscogsSearchService discogsSearchService;
    private boolean batchRunning;

    public CoverArtReport getCoverArtReport() {
        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders();
        List<MusicFile> rootMusicFiles = new ArrayList<MusicFile>(allMusicFolders.size());
        for (MusicFolder musicFolder : allMusicFolders) {
            rootMusicFiles.add(musicFileService.getMusicFile(musicFolder.getPath()));
        }

        List<MusicFile> albumsWithCover = new ArrayList<MusicFile>();
        List<MusicFile> albumsWithoutCover = new ArrayList<MusicFile>();
        addAlbums(rootMusicFiles, albumsWithCover, albumsWithoutCover);
        return new CoverArtReport(albumsWithCover, albumsWithoutCover);
    }

    public synchronized void startBatch() {
        if (isBatchRunning()) {
            return;
        }
        batchRunning = true;

        Thread thread = new Thread("Cover art downloader") {
            @Override
            public void run() {
                try {
                    doRunBatch();
                } finally {
                    setBatchRunning(false);
                }
            }
        };

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public synchronized boolean isBatchRunning() {
        return batchRunning;
    }

    private synchronized void setBatchRunning(boolean batchRunning) {
        this.batchRunning = batchRunning;
    }

    private void doRunBatch() {
        List<MusicFile> albumsWithoutCover = getCoverArtReport().getAlbumsWithoutCover();
        for (MusicFile albumDir : albumsWithoutCover) {
            String artist = getArtist(albumDir);
            String albumName = getAlbum(albumDir);
            LOG.info("Looking up cover art for " + artist + " - " + albumName + ".");
            try {
                String[] imageUrls = discogsSearchService.getCoverArtImages(artist, albumName);
                if (imageUrls.length != 0) {
                    LOG.info("Image found.");
                    coverArtService.setCoverArtImage(albumDir.getPath(), imageUrls[0]);
                } else {
                    LOG.info("No image found.");
                }
            } catch (Exception e) {
                LOG.warn("Failed to retrieve cover art for " + artist + " - " + albumName + ": " + e, e);
                if (isBadRequestError(e)) {
                    LOG.error("Got 'Bad Request' reply from Discogs. This indicates that the daily request quota has " +
                            "been reached. Stopping cover art batch. The batch can be restarted in 24 hours.");
                    break;
                }
            }

        }
    }

    private boolean isBadRequestError(Exception e) {
        return e.getMessage() != null && e.getMessage().indexOf("400 Bad Request") != -1;
    }

    private void addAlbums(List<MusicFile> rootMusicFolders, List<MusicFile> albumsWithCover, List<MusicFile> albumsWithoutCover) {
        for (MusicFile musicFile : rootMusicFolders) {
            if (musicFile.isDirectory()) {
                try {
                    if (musicFile.isAlbum()) {
                        boolean hasCover = musicFileService.getCoverArt(musicFile, 1).size() > 0;
                        if (hasCover) {
                            albumsWithCover.add(musicFile);
                        } else {
                            albumsWithoutCover.add(musicFile);
                        }
                    }
                    // recursion
                    addAlbums(musicFile.getChildren(true, false), albumsWithCover, albumsWithoutCover);
                } catch (IOException e) {
                    LOG.warn("Failed to process cover art of '" + musicFile.getPath() + "'.", e);
                }
            }
        }
    }

    private String getArtist(MusicFile albumFolder) {
        String artist = null;
        MusicFile.MetaData metaData = getMetaDataOfFirstChild(albumFolder);
        if (metaData != null) {
            artist = metaData.getArtist();
        }
        if (artist == null) {
            try {
                MusicFile parent = albumFolder.getParent();
                artist = parent.getName();
            } catch (IOException e) {
                LOG.warn("Retrieval of parent of '" + albumFolder.getPath() + "' failed.", e);
            }
        }
        return artist;
    }

    private String getAlbum(MusicFile albumFolder) {
        String album = null;
        MusicFile.MetaData metaData = getMetaDataOfFirstChild(albumFolder);
        if (metaData != null) {
            album = metaData.getAlbum();
        }
        return album == null ? albumFolder.getName() : album;
    }

    private MusicFile.MetaData getMetaDataOfFirstChild(MusicFile folder) {
        MusicFile musicFile = null;
        try {
            musicFile = folder.getFirstChild();
        } catch (IOException e) {
            LOG.warn("Retrieval of first child of '" + folder.getPath() + "' failed.", e);
        }
        MusicFile.MetaData metaData = null;
        if (musicFile != null) {
            metaData = musicFile.getMetaData();
        }
        return metaData;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setCoverArtService(CoverArtService coverArtService) {
        this.coverArtService = coverArtService;
    }

    public void setDiscogsSearchService(DiscogsSearchService discogsSearchService) {
        this.discogsSearchService = discogsSearchService;
    }
}