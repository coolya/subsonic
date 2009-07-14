/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public final class Constants {

    // Intent names.
    public static final String NAME_PATH = "subsonic.path";

    // Notification IDs.
    public static final int NOTIFICATION_ID_DOWNLOAD_QUEUE = 1;
    public static final int NOTIFICATION_ID_DOWNLOAD_ERROR = 2;

    // Preferences keys.
    public static final String PREFERENCES_KEY_SERVER_URL = "serverUrl";
    public static final String PREFERENCES_KEY_USERNAME = "username";
    public static final String PREFERENCES_KEY_PASSWORD = "password";

    // Name of the preferences file.
    public static final String PREFERENCES_FILE_NAME = "net.sourceforge.subsonic.android_preferences";

    private Constants() {
    }
}
