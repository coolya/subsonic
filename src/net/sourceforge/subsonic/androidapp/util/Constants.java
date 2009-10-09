/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
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
    public static final String REST_PROTOCOL_VERSION = "1.0.0";
    public static final String REST_CLIENT_ID = "android";

    // Intent actions.
    public static final String INTENT_ACTION_DOWNLOAD_QUEUE = "net.sourceforge.subsonic.androidapp.DOWNLOAD_QUEUE";
    public static final String INTENT_ACTION_DOWNLOAD_PROGRESS = "net.sourceforge.subsonic.androidapp.DOWNLOAD_PROGRESS";
    public static final String INTENT_ACTION_STREAM_PLAYLIST = "net.sourceforge.subsonic.androidapp.STREAM_PLAYLIST";
    public static final String INTENT_ACTION_STREAM_CURRENT = "net.sourceforge.subsonic.androidapp.STREAM_CURRENT";
    public static final String INTENT_ACTION_STREAM_PROGRESS = "net.sourceforge.subsonic.androidapp.STREAM_PROGRESS";

    // Names for intent extras.
    public static final String INTENT_EXTRA_NAME_PATH = "subsonic.path";
    public static final String INTENT_EXTRA_NAME_NAME = "subsonic.name";
    public static final String INTENT_EXTRA_NAME_ERROR = "subsonic.error";

    // Notification IDs.
    public static final int NOTIFICATION_ID_DOWNLOAD_QUEUE = 1;
    public static final int NOTIFICATION_ID_DOWNLOAD_ERROR = 2;
    public static final int NOTIFICATION_ID_STREAM_QUEUE = 3;
    public static final int NOTIFICATION_ID_STREAM_ERROR = 4;

    // Preferences keys.
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

    // Socket timeouts
    public static final int SOCKET_CONNECT_TIMEOUT = 10000;
    public static final int SOCKET_READ_TIMEOUT = 20000;

    // URL for project donations.
    public static final String DONATION_URL = "http://subsonic.sf.net/android-donation.php";

    private Constants() {
    }
}
