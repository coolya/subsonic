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
package net.sourceforge.subsonic.service.metadata;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.MusicFile;

import java.io.*;


/**
 * Parses meta data from media files.
 *
 * @author Sindre Mehus
 */
public abstract class MetaDataParser {
    private static final Logger LOG = Logger.getLogger(MetaDataParser.class);

    /**
     * Parses meta data for the given music file.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public MusicFile.MetaData getMetaData(MusicFile file) {

        MusicFile.MetaData metaData = getRawMetaData(file);
        String artist = metaData.getArtist();
        String album = metaData.getAlbum();
        String title = metaData.getTitle();

        if (artist == null) {
            artist = guessArtist(file);
        }
        if (album == null) {
            album = guessAlbum(file);
        }
        if (title == null) {
            title = guessTitle(file);
        }

        title = removeTrackNumberFromTitle(title, metaData.getTrackNumber());
        metaData.setArtist(artist);
        metaData.setAlbum(album);
        metaData.setTitle(title);

        return metaData;
    }

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    public abstract MusicFile.MetaData getRawMetaData(MusicFile file);

    /**
     * Updates the given file with the given meta data.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    public abstract void setMetaData(MusicFile file, MusicFile.MetaData metaData);

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    public abstract boolean isApplicable(MusicFile file);

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Whether tag editing is supported.
     */
    public abstract boolean isEditingSupported();

    /**
     * Guesses the artist for the given music file.
     */
    public String guessArtist(MusicFile file) {
        try {
            MusicFile parent = file.getParent();
            if (parent.isRoot()) {
                return "";
            }
            MusicFile grandParent = parent.getParent();
            return grandParent.isRoot() ? "" : grandParent.getName();
        } catch (IOException x) {
            LOG.warn("Error in guessArtist()", x);
            return null;
        }
    }

    /**
     * Returns meta-data containg file size and format.
     *
     * @param file The music file.
     * @return Meta-data containg file size and format.
     */
    protected MusicFile.MetaData getBasicMetaData(MusicFile file) {
        MusicFile.MetaData metaData = new MusicFile.MetaData();
        metaData.setFileSize(file.length());
        metaData.setFormat(file.getSuffix());
        return metaData;
    }

    /**
     * Guesses the album for the given music file.
     */
    public String guessAlbum(MusicFile file) {
        try {
            MusicFile parent = file.getParent();
            return parent.isRoot() ? "" : parent.getName();
        } catch (IOException x) {
            LOG.warn("Error in guessAlbum()", x);
            return null;
        }
    }

    /**
     * Guesses the title for the given music file.
     */
    public String guessTitle(MusicFile file) {
        return removeTrackNumberFromTitle(file.getNameWithoutSuffix(), null);
    }

    /**
     * Removes any prefixed track number from the given title string.
     *
     * @param title       The title with or without a prefixed track number, e.g., "02 - Back In Black".
     * @param trackNumber If specified, this is the "true" track number.
     * @return The title with the track number removed, e.g., "Back In Black".
     */
    protected String removeTrackNumberFromTitle(String title, Integer trackNumber) {
        title = title.trim();

        // Don't remove numbers if true track number is given, and title does not start with it.
        if (trackNumber != null && !title.matches("0?" + trackNumber + ".*")) {
            return title;
        }

        String result = title.replaceFirst("^\\d{2}[\\.\\- ]+", "");
        return result.length() == 0 ? title : result;
    }

}