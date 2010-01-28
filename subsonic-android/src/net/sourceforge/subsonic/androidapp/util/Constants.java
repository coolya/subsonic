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
package net.sourceforge.subsonic.androidapp.util;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public final class Constants {

    // Character encoding used throughout.
    public static final String UTF_8 = "UTF-8";

    // REST protocol version and client ID.
    public static final String REST_PROTOCOL_VERSION = "1.1.1";
    public static final String REST_CLIENT_ID = "android";

    // Names for intent extras.
    public static final String INTENT_EXTRA_NAME_PATH = "subsonic.path";
    public static final String INTENT_EXTRA_NAME_NAME = "subsonic.name";
    public static final String INTENT_EXTRA_NAME_PLAY_ALL = "subsonic.playall";
    public static final String INTENT_EXTRA_NAME_ERROR = "subsonic.error";
    public static final String INTENT_EXTRA_NAME_QUERY = "subsonic.query";
    public static final String INTENT_EXTRA_NAME_PLAYLIST_ID = "subsonic.playlist.id";
    public static final String INTENT_EXTRA_NAME_PLAYLIST_NAME = "subsonic.playlist.name";

    // Notification IDs.
    public static final int NOTIFICATION_ID_PLAYING = 100;
    public static final int NOTIFICATION_ID_ERROR = 101;

    // Preferences keys.
    public static final String PREFERENCES_KEY_OFFLINE = "offline";
    public static final String PREFERENCES_KEY_SERVER_INSTANCE = "serverInstance";
    public static final String PREFERENCES_KEY_SERVER_NAME = "serverName";
    public static final String PREFERENCES_KEY_SERVER_URL = "serverUrl";
    public static final String PREFERENCES_KEY_USERNAME = "username";
    public static final String PREFERENCES_KEY_PASSWORD = "password";
    public static final String PREFERENCES_KEY_INSTALL_TIME = "installTime";

    // Name of the preferences file.
    public static final String PREFERENCES_FILE_NAME = "net.sourceforge.subsonic.androidapp_preferences";

    // Number of free trial days for non-licensed servers.
    public static final int FREE_TRIAL_DAYS = 30;

    // URL for project donations.
    public static final String DONATION_URL = "http://subsonic.org/pages/android-donation.jsp";

    public static final String ALBUM_ART_FILE = "folder.jpeg";

    private Constants() {
    }
}
