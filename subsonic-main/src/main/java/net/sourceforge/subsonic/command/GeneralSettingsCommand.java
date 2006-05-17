package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;

/**
 * Command used in {@link GeneralSettingsController}.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsCommand {
    private String playlistFolder;
    private String musicMask;
    private String coverArtMask;
    private String index;
    private String ignoredArticles;
    private String shortcuts;
    private String welcomeMessage;
    private String coverArtLimit;
    private String downloadLimit;
    private String uploadLimit;
    private String localeIndex;
    private String[] locales;
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

    public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }
}
