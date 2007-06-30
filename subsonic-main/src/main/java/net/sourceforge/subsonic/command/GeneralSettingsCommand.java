package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.domain.*;

/**
 * Command used in {@link GeneralSettingsController}.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsCommand {
    private String playlistFolder;
    private String musicMask;
    private String coverArtMask;
    private String downsampleCommand;
    private String index;
    private String ignoredArticles;
    private String shortcuts;
    private String welcomeMessage;
    private String coverArtLimit;
    private String downloadLimit;
    private String uploadLimit;
    private String streamPort;
    private String localeIndex;
    private String[] locales;
    private String themeIndex;
    private Theme[] themes;
    private boolean isReloadNeeded;

    public String getPlaylistFolder() {
        return playlistFolder;
    }

    public void setPlaylistFolder(String playlistFolder) {
        this.playlistFolder = playlistFolder;
    }

    public String getMusicMask() {
        return musicMask;
    }

    public void setMusicMask(String musicMask) {
        this.musicMask = musicMask;
    }

    public String getCoverArtMask() {
        return coverArtMask;
    }

    public void setCoverArtMask(String coverArtMask) {
        this.coverArtMask = coverArtMask;
    }

    public String getDownsampleCommand() {
        return downsampleCommand;
    }

    public void setDownsampleCommand(String downsampleCommand) {
        this.downsampleCommand = downsampleCommand;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIgnoredArticles() {
        return ignoredArticles;
    }

    public void setIgnoredArticles(String ignoredArticles) {
        this.ignoredArticles = ignoredArticles;
    }

    public String getShortcuts() {
        return shortcuts;
    }

    public void setShortcuts(String shortcuts) {
        this.shortcuts = shortcuts;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getCoverArtLimit() {
        return coverArtLimit;
    }

    public void setCoverArtLimit(String coverArtLimit) {
        this.coverArtLimit = coverArtLimit;
    }

    public String getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(String downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public String getUploadLimit() {
        return uploadLimit;
    }

    public String getStreamPort() {
        return streamPort;
    }

    public void setStreamPort(String streamPort) {
        this.streamPort = streamPort;
    }

    public void setUploadLimit(String uploadLimit) {
        this.uploadLimit = uploadLimit;
    }

    public String getLocaleIndex() {
        return localeIndex;
    }

    public void setLocaleIndex(String localeIndex) {
        this.localeIndex = localeIndex;
    }

    public String[] getLocales() {
        return locales;
    }

    public void setLocales(String[] locales) {
        this.locales = locales;
    }

    public String getThemeIndex() {
        return themeIndex;
    }

    public void setThemeIndex(String themeIndex) {
        this.themeIndex = themeIndex;
    }

    public Theme[] getThemes() {
        return themes;
    }

    public void setThemes(Theme[] themes) {
        this.themes = themes;
    }

    public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }
}
