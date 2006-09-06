package net.sourceforge.subsonic.domain;

import java.util.*;

/**
 * Represent user-specific settings.
 *
 * @author Sindre Mehus
 */
public class UserSettings {

    private String username;
    private Locale locale;
    private String themeId;
    private boolean finalVersionNotificationEnabled;
    private boolean betaVersionNotificationEnabled;

    private Visibility mainVisibility = new Visibility();
    private Visibility playlistVisibility = new Visibility();

    public UserSettings(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public boolean isFinalVersionNotificationEnabled() {
        return finalVersionNotificationEnabled;
    }

    public void setFinalVersionNotificationEnabled(boolean finalVersionNotificationEnabled) {
        this.finalVersionNotificationEnabled = finalVersionNotificationEnabled;
    }

    public boolean isBetaVersionNotificationEnabled() {
        return betaVersionNotificationEnabled;
    }

    public void setBetaVersionNotificationEnabled(boolean betaVersionNotificationEnabled) {
        this.betaVersionNotificationEnabled = betaVersionNotificationEnabled;
    }

    public Visibility getMainVisibility() {
        return mainVisibility;
    }

    public void setMainVisibility(Visibility mainVisibility) {
        this.mainVisibility = mainVisibility;
    }

    public Visibility getPlaylistVisibility() {
        return playlistVisibility;
    }

    public void setPlaylistVisibility(Visibility playlistVisibility) {
        this.playlistVisibility = playlistVisibility;
    }

    /**
     * Configuration of what information to display about a song.
     */
    public static class Visibility {
        private int captionCutoff;
        private boolean isTrackNumberVisible;
        private boolean isArtistVisible;
        private boolean isAlbumVisible;
        private boolean isGenreVisible;
        private boolean isYearVisible;
        private boolean isBitRateVisible;
        private boolean isDurationVisible;
        private boolean isFormatVisible;
        private boolean isFileSizeVisible;

        public Visibility() {}

        public Visibility(int captionCutoff, boolean trackNumberVisible, boolean artistVisible, boolean albumVisible,
                          boolean genreVisible, boolean yearVisible, boolean bitRateVisible,
                          boolean durationVisible, boolean formatVisible, boolean fileSizeVisible) {
            this.captionCutoff = captionCutoff;
            isTrackNumberVisible = trackNumberVisible;
            isArtistVisible = artistVisible;
            isAlbumVisible = albumVisible;
            isGenreVisible = genreVisible;
            isYearVisible = yearVisible;
            isBitRateVisible = bitRateVisible;
            isDurationVisible = durationVisible;
            isFormatVisible = formatVisible;
            isFileSizeVisible = fileSizeVisible;
        }

        public int getCaptionCutoff() {
            return captionCutoff;
        }

        public void setCaptionCutoff(int captionCutoff) {
            this.captionCutoff = captionCutoff;
        }

        public boolean isTrackNumberVisible() {
            return isTrackNumberVisible;
        }

        public void setTrackNumberVisible(boolean trackNumberVisible) {
            isTrackNumberVisible = trackNumberVisible;
        }

        public boolean isArtistVisible() {
            return isArtistVisible;
        }

        public void setArtistVisible(boolean artistVisible) {
            isArtistVisible = artistVisible;
        }

        public boolean isAlbumVisible() {
            return isAlbumVisible;
        }

        public void setAlbumVisible(boolean albumVisible) {
            isAlbumVisible = albumVisible;
        }

        public boolean isGenreVisible() {
            return isGenreVisible;
        }

        public void setGenreVisible(boolean genreVisible) {
            isGenreVisible = genreVisible;
        }

        public boolean isYearVisible() {
            return isYearVisible;
        }

        public void setYearVisible(boolean yearVisible) {
            isYearVisible = yearVisible;
        }

        public boolean isBitRateVisible() {
            return isBitRateVisible;
        }

        public void setBitRateVisible(boolean bitRateVisible) {
            isBitRateVisible = bitRateVisible;
        }

        public boolean isDurationVisible() {
            return isDurationVisible;
        }

        public void setDurationVisible(boolean durationVisible) {
            isDurationVisible = durationVisible;
        }

        public boolean isFormatVisible() {
            return isFormatVisible;
        }

        public void setFormatVisible(boolean formatVisible) {
            isFormatVisible = formatVisible;
        }

        public boolean isFileSizeVisible() {
            return isFileSizeVisible;
        }

        public void setFileSizeVisible(boolean fileSizeVisible) {
            isFileSizeVisible = fileSizeVisible;
        }
    }
}
