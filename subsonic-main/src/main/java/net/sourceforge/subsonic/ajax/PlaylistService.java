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
package net.sourceforge.subsonic.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.JukeboxService;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for manipulating the playlist of a player.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlaylistService {

    private PlayerService playerService;
    private MusicFileService musicFileService;
    private JukeboxService jukeboxService;
    private TranscodingService transcodingService;
    private SettingsService settingsService;

    /**
     * Returns the playlist for the player of the current user.
     *
     * @return The playlist.
     */
    public PlaylistInfo getPlaylist() throws Exception {
        Player player = getCurrentPlayer();
        return convert(player, false);
    }

    public PlaylistInfo start() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().setStatus(Playlist.Status.PLAYING);
        return convert(player, true);
    }

    public PlaylistInfo stop() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().setStatus(Playlist.Status.STOPPED);
        return convert(player, true);
    }

    public PlaylistInfo skip(int index) throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().setIndex(index);
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(player, serverSidePlaylist);
    }

    public PlaylistInfo play(String path) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        Player player = getCurrentPlayer();
        player.getPlaylist().addFiles(false, file);
        player.getPlaylist().setRandomSearchCriteria(null);
        return convert(player, true);
    }

    public PlaylistInfo playRandom(String path, int count) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        List<MusicFile> randomFiles = getRandomChildren(file, count);
        Player player = getCurrentPlayer();
        player.getPlaylist().addFiles(false, randomFiles);
        player.getPlaylist().setRandomSearchCriteria(null);
        return convert(player, true);
    }

    public PlaylistInfo add(String path) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        Player player = getCurrentPlayer();
        player.getPlaylist().addFiles(true, file);
        player.getPlaylist().setRandomSearchCriteria(null);
        return convert(player, false);
    }

    public PlaylistInfo clear() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().clear();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(player, serverSidePlaylist);
    }

    public PlaylistInfo shuffle() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().shuffle();
        return convert(player, false);
    }

    public PlaylistInfo remove(int index) throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().removeFileAt(index);
        return convert(player, false);
    }

    public PlaylistInfo removeMany(int[] indexes) throws Exception {
        Player player = getCurrentPlayer();
        for (int i = indexes.length - 1; i >= 0; i--) {
            player.getPlaylist().removeFileAt(indexes[i]);
        }
        return convert(player, false);
    }

    public PlaylistInfo up(int index) throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().moveUp(index);
        return convert(player, false);
    }

    public PlaylistInfo down(int index) throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().moveDown(index);
        return convert(player, false);
    }

    public PlaylistInfo toggleRepeat() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().setRepeatEnabled(!player.getPlaylist().isRepeatEnabled());
        return convert(player, false);
    }

    public PlaylistInfo undo() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().undo();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(player, serverSidePlaylist);
    }

    public PlaylistInfo sortByTrack() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().sort(Playlist.SortOrder.TRACK);
        return convert(player, false);
    }

    public PlaylistInfo sortByArtist() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().sort(Playlist.SortOrder.ARTIST);
        return convert(player, false);
    }

    public PlaylistInfo sortByAlbum() throws Exception {
        Player player = getCurrentPlayer();
        player.getPlaylist().sort(Playlist.SortOrder.ALBUM);
        return convert(player, false);
    }

    private List<MusicFile> getRandomChildren(MusicFile file, int count) throws IOException {
        List<MusicFile> children = file.getDescendants(false, false);
        if (children.isEmpty()) {
            return children;
        }
        Collections.shuffle(children);
        return children.subList(0, Math.min(count, children.size()));
    }

    private PlaylistInfo convert(Player player, boolean sendM3U) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String url = request.getRequestURL().toString();

        boolean isCurrentPlayer = player.getIpAddress() != null && player.getIpAddress().equals(request.getRemoteAddr());

        boolean jukebox = player.isJukebox();
        boolean m3uSupported = player.isExternal() || player.isExternalWithPlaylist();
        sendM3U = player.isAutoControlEnabled() && m3uSupported && isCurrentPlayer && sendM3U;

        // TODO
        if (sendM3U && jukebox) {
            jukeboxService.play(player);
        }
        List<PlaylistInfo.Entry> entries = new ArrayList<PlaylistInfo.Entry>();
        Playlist playlist = player.getPlaylist();
        for (MusicFile file : playlist.getFiles()) {
            MusicFile.MetaData metaData = file.getMetaData();
            String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                    StringUtil.utf8HexEncode(file.getParent().getPath()));
            String streamUrl = url.replaceFirst("/dwr/.*", "/stream?player=" + player.getId() + "&pathUtf8Hex=" +
                                                           StringUtil.utf8HexEncode(file.getPath()));

            // Rewrite URLs in case we're behind a proxy.
            if (settingsService.isRewriteUrlEnabled()) {
                String referer = request.getHeader("referer");
                albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                streamUrl = StringUtil.rewriteUrl(streamUrl, referer);
            }

            entries.add(new PlaylistInfo.Entry(metaData.getTrackNumber(), metaData.getTitle(), metaData.getArtist(),
                    metaData.getAlbum(), metaData.getGenre(), metaData.getYear(), formatBitRate(metaData),
                    metaData.getDuration(), metaData.getDurationAsString(), formatFormat(metaData.getFormat()),
                    formatContentType(player, file), formatFileSize(metaData.getFileSize()), albumUrl, streamUrl));
        }
        boolean isStopEnabled = playlist.getStatus() == Playlist.Status.PLAYING && !player.isExternalWithPlaylist();
        return new PlaylistInfo(entries, playlist.getIndex(), isStopEnabled, playlist.isRepeatEnabled(), sendM3U);
    }

    private String formatFileSize(Long fileSize) {
        if (fileSize == null) {
            return null;
        }

        WebContext webContext = WebContextFactory.get();
        Locale locale = RequestContextUtils.getLocale(webContext.getHttpServletRequest());
        return StringUtil.formatBytes(fileSize, locale);
    }

    private String formatFormat(String format) {
        return StringUtils.lowerCase(format);
    }

    private String formatContentType(Player player, MusicFile file) {
        String suffix = transcodingService.getSuffix(player, file);
        return StringUtil.getMimeType(suffix);
    }

    private String formatBitRate(MusicFile.MetaData metaData) {
        if (metaData.getBitRate() == null) {
            return null;
        }
        if (Boolean.TRUE.equals(metaData.getVariableBitRate())) {
            return metaData.getBitRate() + " Kbps vbr";
        }
        return metaData.getBitRate() + " Kbps";
    }

    private Player getCurrentPlayer() {
        WebContext webContext = WebContextFactory.get();
        return playerService.getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
    }


    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setJukeboxService(JukeboxService jukeboxService) {
        this.jukeboxService = jukeboxService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}