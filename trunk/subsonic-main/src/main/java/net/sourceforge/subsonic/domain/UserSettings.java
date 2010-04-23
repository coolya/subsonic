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
    private boolean showNowPlayingEnabled;
    private boolean showChatEnabled;
    private boolean finalVersionNotificationEnabled;
    private boolean betaVersionNotificationEnabled;
    private Visibility mainVisibility = new Visibility();
    private Visibility playlistVisibility = new Visibility();
    private boolean lastFmEnabled;
    private String lastFmUsername;
    private String lastFmPassword;
    private TranscodeScheme transcodeScheme = TranscodeScheme.OFF;
    private int selectedMusicFolderId = -1;
    private boolean partyModeEnabled;
    private boolean nowPlayingAllowed;
    private AvatarScheme avatarScheme = AvatarScheme.NONE;
    private Integer systemAvatarId;
    private Date changed = new Date();

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

    public boolean isShowNowPlayingEnabled() {
        return showNowPlayingEnabled;
    }

    public void setShowNowPlayingEnabled(boolean showNowPlayingEnabled) {
        this.showNowPlayingEnabled = showNowPlayingEnabled;
    }

    public boolean isShowChatEnabled() {
        return showChatEnabled;
    }

    public void setShowChatEnabled(boolean showChatEnabled) {
        this.showChatEnabled = showChatEnabled;
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

    public boolean isLastFmEnabled() {
        return lastFmEnabled;
    }

    public void setLastFmEnabled(boolean lastFmEnabled) {
        this.lastFmEnabled = lastFmEnabled;
    }

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public String getLastFmPassword() {
        return lastFmPassword;
    }

    public void setLastFmPassword(String lastFmPassword) {
        this.lastFmPassword = lastFmPassword;
    }

    public TranscodeScheme getTranscodeScheme() {
        return transcodeScheme;
    }

    public void setTranscodeScheme(TranscodeScheme transcodeScheme) {
        this.transcodeScheme = transcodeScheme;
    }

    public int getSelectedMusicFolderId() {
        return selectedMusicFolderId;
    }

    public void setSelectedMusicFolderId(int selectedMusicFolderId) {
        this.selectedMusicFolderId = selectedMusicFolderId;
    }

    public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public boolean isNowPlayingAllowed() {
        return nowPlayingAllowed;
    }

    public void setNowPlayingAllowed(boolean nowPlayingAllowed) {
        this.nowPlayingAllowed = nowPlayingAllowed;
    }

    public AvatarScheme getAvatarScheme() {
        return avatarScheme;
    }

    public void setAvatarScheme(AvatarScheme avatarScheme) {
        this.avatarScheme = avatarScheme;
    }

    public Integer getSystemAvatarId() {
        return systemAvatarId;
    }

    public void setSystemAvatarId(Integer systemAvatarId) {
        this.systemAvatarId = systemAvatarId;
    }

    /**
     * Returns when the corresponding database entry was last changed.
     *
     * @return When the corresponding database entry was last changed.
     */
    public Date getChanged() {
        return changed;
    }

    /**
     * Sets when the corresponding database entry was last changed.
     *
     * @param changed When the corresponding database entry was last changed.
     */
    public void setChanged(Date changed) {
        this.changed = changed;
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
