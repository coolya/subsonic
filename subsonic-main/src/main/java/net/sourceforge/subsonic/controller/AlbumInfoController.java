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
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the album info page.
 *
 * @author Sindre Mehus
 */
public class AlbumInfoController extends SimpleFormController {

    private static final Logger LOG = Logger.getLogger(AlbumInfoController.class);

    private AmazonSearchService amazonSearchService;
    private MusicFileService musicFileService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        String path = request.getParameter("path");
        String artist = request.getParameter("artist");
        String album = request.getParameter("album");

        MusicFile child = musicFileService.getMusicFile(path).getFirstChild();
        MusicFile.MetaData metaData = child.getMetaData();
        if (artist == null) {
            artist = metaData.getArtist();
        }
        if (album == null) {
            album = metaData.getAlbum();
        }

        return new AlbumInfoCommand(path, artist, album);
    }

    protected void doSubmitAction(Object comm) throws Exception {
        AlbumInfoCommand command = (AlbumInfoCommand) comm;

        List<AlbumInfoCommand.Match> matches = new ArrayList<AlbumInfoCommand.Match>();
        try {
            AmazonAlbumInfo[] infos = amazonSearchService.getAlbumInfo(command.getArtist(), command.getAlbum());
            for (AmazonAlbumInfo info : infos) {
                AlbumInfoCommand.Match match = new AlbumInfoCommand.Match();
                match.setArtists(formatArtists(info));
                match.setAlbum(formatAlbum(info));
                match.setReviews(formatReviews(info));
                match.setLabel(replaceSpecialChars(info.getLabel()));
                match.setReleased(replaceSpecialChars(info.getReleaseDate()));
                match.setImageUrl(info.getImageUrl() == null ? "coverart?size=160" : info.getImageUrl());
                match.setDetailPageUrl(info.getDetailPageUrl());
                matches.add(match);
            }
            command.setMatches(matches);
        } catch (Exception x) {
            LOG.warn("Failed to search for album info at Amazon.com.", x);
        }
    }

    protected boolean isFormSubmission(HttpServletRequest request) {
        return true;
    }

    private String formatArtists(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        String[] artists = info.getArtists();
        for (int i = 0; i < artists.length; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(artists[i]);
        }
        return replaceSpecialChars(s.toString());
    }

    private String formatAlbum(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        s.append(info.getAlbum());
        String[] formats = info.getFormats();
        for (int i = 0; i < formats.length; i++) {
            s.append(" [").append(formats[i]).append(']');
        }
        return replaceSpecialChars(s.toString());
    }

    private String formatReviews(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        String[] reviews = info.getEditorialReviews();
        for (int i = 0; i < reviews.length; i++) {
            if (i > 0) {
                s.append("</p>");
            }
            s.append(reviews[i]);
        }
        return replaceSpecialChars(s.toString());
    }

    private String replaceSpecialChars(String s) {
        if (s == null) {
            return null;
        }

        s = s.replaceAll("'", "&#39;");
        s = s.replaceAll("\"", "&#34;");
        s = s.replaceAll("\\n", "<br/>");

        return s ;
    }

    public void setAmazonSearchService(AmazonSearchService amazonSearchService) {
        this.amazonSearchService = amazonSearchService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}
