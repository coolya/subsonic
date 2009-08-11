/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android.util;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public final class Constants {

    // REST protocol version.
    public static final String PROTOCOL_VERSION = "1.0.0";

    // Intent actions.
    public static final String INTENT_ACTION_DOWNLOAD_QUEUE = "net.sourceforge.subsonic.android.DOWNLOAD_QUEUE";
    public static final String INTENT_ACTION_DOWNLOAD_PROGRESS = "net.sourceforge.subsonic.android.DOWNLOAD_PROGRESS";

    // Names for intent extras.
    public static final String INTENT_EXTRA_NAME_PATH = "subsonic.path";
    public static final String INTENT_EXTRA_NAME_NAME = "subsonic.name";

    // Notification IDs.
    public static final int NOTIFICATION_ID_DOWNLOAD_QUEUE = 1;
    public static final int NOTIFICATION_ID_DOWNLOAD_ERROR = 2;

    // Preferences keys.
    public static final String PREFERENCES_KEY_SERVER_URL = "serverUrl";
    public static final String PREFERENCES_KEY_USERNAME = "username";
    public static final String PREFERENCES_KEY_PASSWORD = "password";

    // Name of the preferences file.
    public static final String PREFERENCES_FILE_NAME = "net.sourceforge.subsonic.android_preferences";
    public static final int SOCKET_TIMEOUT = 10000;

    private Constants() {
    }
}
