package net.sourceforge.subsonic.ajax;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.domain.Playlist;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Provides AJAX-enabled services for manipulating the playlist of a player.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlaylistService {

    private PlayerService playerService;
    private MusicFileService musicFileService;


    /**
     * Returns the playlist for the player of the current user.
     *
     * @return The playlist.
     */
    public PlaylistInfo getPlaylist() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        return convert(playlist);
    }

    public PlaylistInfo play(String path) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        Playlist playlist = getCurrentPlaylist();
        playlist.addFiles(false, file);
        playlist.setRandomSearchCriteria(null);
        return convert(playlist);
    }

    public PlaylistInfo playRandom(String path, int count) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        List<MusicFile> randomFiles = getRandomChildren(file, count);
        Playlist playlist = getCurrentPlaylist();
        playlist.addFiles(false, randomFiles);
        playlist.setRandomSearchCriteria(null);
        return convert(playlist);
    }

    public PlaylistInfo add(String path) throws Exception {
        MusicFile file = musicFileService.getMusicFile(path);
        Playlist playlist = getCurrentPlaylist();
        playlist.addFiles(true, file);
        playlist.setRandomSearchCriteria(null);
        return convert(playlist);
    }

    public PlaylistInfo clear() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.clear();
        return convert(playlist);
    }

    public PlaylistInfo shuffle() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.shuffle();
        return convert(playlist);
    }

    public PlaylistInfo remove(int index) throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.removeFileAt(index);
        return convert(playlist);
    }

    public PlaylistInfo up(int index) throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.moveUp(index);
        return convert(playlist);
    }

    public PlaylistInfo down(int index) throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.moveDown(index);
        return convert(playlist);
    }

    public PlaylistInfo toggleRepeat() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.setRepeatEnabled(!playlist.isRepeatEnabled());
        return convert(playlist);
    }

    public PlaylistInfo undo() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.undo();
        return convert(playlist);
    }

    public PlaylistInfo sortByTrack() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.sort(Playlist.SortOrder.TRACK);
        return convert(playlist);
    }

    public PlaylistInfo sortByArtist() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.sort(Playlist.SortOrder.ARTIST);
        return convert(playlist);
    }

    public PlaylistInfo sortByAlbum() throws Exception {
        Playlist playlist = getCurrentPlaylist();
        playlist.sort(Playlist.SortOrder.ALBUM);
        return convert(playlist);
    }

    private List<MusicFile> getRandomChildren(MusicFile file, int count) throws IOException {
        List<MusicFile> children = file.getDescendants(false, false);
        if (children.isEmpty()) {
            return children;
        }
        Collections.shuffle(children);
        return children.subList(0, Math.min(count, children.size()));
    }

    private PlaylistInfo convert(Playlist playlist) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String url = request.getRequestURL().toString();

        List<PlaylistInfo.Entry> entries = new ArrayList<PlaylistInfo.Entry>();
        for (MusicFile file : playlist.getFiles()) {
            MusicFile.MetaData metaData = file.getMetaData();
            String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?pathUtf8Hex=" +
                                                          StringUtil.utf8HexEncode(file.getParent().getPath()));

            entries.add(new PlaylistInfo.Entry(metaData.getTrackNumber(), metaData.getTitle(), metaData.getArtist(),
                                               metaData.getAlbum(), metaData.getGenre(), metaData.getYear(), formatBitRate(metaData),
                                               metaData.getDurationAsString(), formatFormat(metaData.getFormat()),
                                               formatFileSize(metaData.getFileSize()), albumUrl));
        }

        return new PlaylistInfo(entries, playlist.isRepeatEnabled());
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

    private String formatBitRate(MusicFile.MetaData metaData) {
        if (metaData.getBitRate() == null) {
            return null;
        }
        if (Boolean.TRUE.equals(metaData.getVariableBitRate())) {
            return metaData.getBitRate() + " Kbps vbr";
        }
        return metaData.getBitRate() + " Kbps";
    }

    private Playlist getCurrentPlaylist() {
        WebContext webContext = WebContextFactory.get();
        Player player = playerService.getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        return player.getPlaylist();
    }


    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }
}