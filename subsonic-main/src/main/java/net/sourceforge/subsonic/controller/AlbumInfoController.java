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

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        String path = request.getParameter("path");
        String artist = request.getParameter("artist");
        String album = request.getParameter("album");

        MusicFile[] children = new MusicFile(path).getChildren(false);
        MusicFile.MetaData metaData = children[0].getMetaData();
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
                match.setLabel(replaceQuotes(info.getLabel()));
                match.setReleased(replaceQuotes(info.getReleaseDate()));
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
        return replaceQuotes(s.toString());
    }

    private String formatAlbum(AmazonAlbumInfo info) {
        StringBuffer s = new StringBuffer();
        s.append(info.getAlbum());
        String[] formats = info.getFormats();
        for (int i = 0; i < formats.length; i++) {
            s.append(" [").append(formats[i]).append(']');
        }
        return replaceQuotes(s.toString());
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
        return replaceQuotes(s.toString());
    }

    private String replaceQuotes(String s) {
        if (s == null) {
            return null;
        }

        return s.replaceAll("'", "&#39;").replaceAll("\"", "&#34;");
    }

    public void setAmazonSearchService(AmazonSearchService amazonSearchService) {
        this.amazonSearchService = amazonSearchService;
    }
}
