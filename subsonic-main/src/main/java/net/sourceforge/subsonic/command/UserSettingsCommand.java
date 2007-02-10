package net.sourceforge.subsonic.command;

import net.sourceforge.subsonic.controller.*;
import net.sourceforge.subsonic.domain.*;

/**
 * Command used in {@link UserSettingsController}.
 *
 * @author Sindre Mehus
 */
public class UserSettingsCommand {
    private String username;
    private boolean isAdminRole;
    private boolean isDownloadRole;
    private boolean isUploadRole;
    private boolean isPlaylistRole;
    private boolean isCoverArtRole;
    private boolean isCommentRole;

    private User[] users;
    private boolean isAdmin;
    private boolean isPasswordChange;
    private boolean isNew;
    private boolean isDelete;
    private String password;
    private String confirmPassword;

    private String transcodeSchemeName;
    private EnumHolder[] transcodeSchemeHolders;
    private boolean transcodingSupported;
    private String transcodeDirectory;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdminRole() {
        return isAdminRole;
    }

    public void setAdminRole(boolean adminRole) {
        isAdminRole = adminRole;
    }

    public boolean isDownloadRole() {
        return isDownloadRole;
    }

    public void setDownloadRole(boolean downloadRole) {
        isDownloadRole = downloadRole;
    }

    public boolean isUploadRole() {
        return isUploadRole;
    }

    public void setUploadRole(boolean uploadRole) {
        isUploadRole = uploadRole;
    }

    public boolean isPlaylistRole() {
        return isPlaylistRole;
    }

    public void setPlaylistRole(boolean playlistRole) {
        isPlaylistRole = playlistRole;
    }

    public boolean isCoverArtRole() {
        return isCoverArtRole;
    }

    public void setCoverArtRole(boolean coverArtRole) {
        isCoverArtRole = coverArtRole;
    }

    public boolean isCommentRole() {
        return isCommentRole;
    }

    public void setCommentRole(boolean commentRole) {
        isCommentRole = commentRole;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isPasswordChange() {
        return isPasswordChange;
    }

    public void setPasswordChange(boolean passwordChange) {
        isPasswordChange = passwordChange;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getTranscodeSchemeName() {
        return transcodeSchemeName;
    }

    public void setTranscodeSchemeName(String transcodeSchemeName) {
        this.transcodeSchemeName = transcodeSchemeName;
    }

    public EnumHolder[] getTranscodeSchemeHolders() {
        return transcodeSchemeHolders;
    }

    public void setTranscodeSchemes(TranscodeScheme[] transcodeSchemes) {
        transcodeSchemeHolders = new EnumHolder[transcodeSchemes.length];
        for (int i = 0; i < transcodeSchemes.length; i++) {
            TranscodeScheme scheme = transcodeSchemes[i];
            transcodeSchemeHolders[i] = new EnumHolder(scheme.name(), scheme.toString());
        }
    }

    public boolean isTranscodingSupported() {
        return transcodingSupported;
    }

    public void setTranscodingSupported(boolean transcodingSupported) {
        this.transcodingSupported = transcodingSupported;
    }

    public String getTranscodeDirectory() {
        return transcodeDirectory;
    }

    public void setTranscodeDirectory(String transcodeDirectory) {
        this.transcodeDirectory = transcodeDirectory;
    }

    public void setUser(User user) {
        username = user == null ? null : user.getUsername();
        isAdminRole = user != null && user.isAdminRole();
        isDownloadRole = user != null && user.isDownloadRole();
        isUploadRole = user != null && user.isUploadRole();
        isPlaylistRole = user != null && user.isPlaylistRole();
        isCoverArtRole = user != null && user.isCoverArtRole();
        isCommentRole = user != null && user.isCommentRole();
    }
}