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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpEntity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.activity.DownloadActivity;
import net.sourceforge.subsonic.androidapp.activity.ErrorActivity;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public final class Util {

    private static final String TAG = Util.class.getSimpleName();

    private static final DecimalFormat GIGA_BYTE_FORMAT = new DecimalFormat("0.00 GB");
    private static final DecimalFormat MEGA_BYTE_FORMAT = new DecimalFormat("0.00 MB");
    private static final DecimalFormat KILO_BYTE_FORMAT = new DecimalFormat("0 KB");

    // Used by hexEncode()
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Util() {
    }

    public static boolean isOffline(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
        return prefs.getBoolean(Constants.PREFERENCES_KEY_OFFLINE, false);
    }

    public static void setOffline(Context context, boolean offline) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.PREFERENCES_KEY_OFFLINE, offline);
        editor.commit();
    }

    public static String getRestUrl(Context context, String method) {
        StringBuilder builder = new StringBuilder();

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);

        String instance = prefs.getString(Constants.PREFERENCES_KEY_SERVER_INSTANCE, "1");
        String serverUrl = prefs.getString(Constants.PREFERENCES_KEY_SERVER_URL + instance, null);
        String username = prefs.getString(Constants.PREFERENCES_KEY_USERNAME + instance, null);
        String password = prefs.getString(Constants.PREFERENCES_KEY_PASSWORD + instance, null);

        // Slightly obfuscate password
        password = "enc:" + Util.utf8HexEncode(password);

        builder.append(serverUrl);
        if (builder.charAt(builder.length() - 1) != '/') {
            builder.append("/");
        }
        builder.append("rest/").append(method).append(".view");
        builder.append("?u=").append(username);
        builder.append("&p=").append(password);
        builder.append("&v=").append(Constants.REST_PROTOCOL_VERSION);
        builder.append("&c=").append(Constants.REST_CLIENT_ID);

        return builder.toString();
    }

    public static String getContentType(HttpEntity entity) {
        if (entity == null || entity.getContentType() == null) {
            return null;
        }
        return entity.getContentType().getValue();
    }

    public static int getRemainingTrialDays(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME, 0);
        long installTime = prefs.getLong(Constants.PREFERENCES_KEY_INSTALL_TIME, 0L);

        if (installTime == 0L) {
            installTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(Constants.PREFERENCES_KEY_INSTALL_TIME, installTime);
            editor.commit();
        }

        long now = System.currentTimeMillis();
        long millisPerDay = 24L * 60L * 60L * 1000L;
        int daysSinceInstall = (int) ((now - installTime) / millisPerDay);
        return Math.max(0, Constants.FREE_TRIAL_DAYS - daysSinceInstall);
    }

    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void atomicCopy(File from, File to) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        File tmp = null;
        try {
            tmp = new File(to.getPath() + ".tmp");
            in = new FileInputStream(from);
            out = new FileOutputStream(tmp);
            copy(in, out);
            out.close();
            if (!tmp.renameTo(to)) {
                throw new IOException("Failed to rename " + tmp + " to " + to);
            }
            Log.i(TAG, "Copied " + from + " to " + to);
        } catch (IOException x) {
            close(out);
            delete(to);
            throw x;
        } finally {
            close(in);
            close(out);
            delete(tmp);
        }
    }

    public static void close(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static boolean delete(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                Log.w(TAG, "Failed to delete file " + file);
                return false;
            }
        }
        return true;
    }

    public static void close(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void toast(final Context context, Handler handler, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast(context, message);
            }
        });
    }

    public static void toast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Converts a byte-count to a formatted string suitable for display to the user.
     * For instance:
     * <ul>
     * <li><code>format(918)</code> returns <em>"918 B"</em>.</li>
     * <li><code>format(98765)</code> returns <em>"96 KB"</em>.</li>
     * <li><code>format(1238476)</code> returns <em>"1.2 MB"</em>.</li>
     * </ul>
     * This method assumes that 1 KB is 1024 bytes.
     *
     * @param byteCount The number of bytes.
     * @return The formatted string.
     */
    public static synchronized String formatBytes(long byteCount) {

        // More than 1 GB?
        if (byteCount >= 1024 * 1024 * 1024) {
            NumberFormat gigaByteFormat = GIGA_BYTE_FORMAT;
            return gigaByteFormat.format((double) byteCount / (1024 * 1024 * 1024));
        }

        // More than 1 MB?
        if (byteCount >= 1024 * 1024) {
            NumberFormat megaByteFormat = MEGA_BYTE_FORMAT;
            return megaByteFormat.format((double) byteCount / (1024 * 1024));
        }

        // More than 1 KB?
        if (byteCount >= 1024) {
            NumberFormat kiloByteFormat = KILO_BYTE_FORMAT;
            return kiloByteFormat.format((double) byteCount / 1024);
        }

        return byteCount + " B";
    }

    public static String formatDuration(Integer seconds) {
        if (seconds == null) {
            return null;
        }

        int minutes = seconds / 60;
        int secs = seconds % 60;

        StringBuilder builder = new StringBuilder(6);
        builder.append(minutes).append(":");
        if (secs < 10) {
            builder.append("0");
        }
        builder.append(secs);
        return builder.toString();
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);

    }

    /**
     * Encodes the given string by using the hexadecimal representation of its UTF-8 bytes.
     *
     * @param s The string to encode.
     * @return The encoded string.
     */
    public static String utf8HexEncode(String s) {
        if (s == null) {
            return null;
        }
        byte[] utf8;
        try {
            utf8 = s.getBytes(Constants.UTF_8);
        } catch (UnsupportedEncodingException x) {
            throw new RuntimeException(x);
        }
        return hexEncode(utf8);
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data Bytes to convert to hexadecimal characters.
     * @return A string containing hexadecimal characters.
     */
    public static String hexEncode(byte[] data) {
        int length = data.length;
        char[] out = new char[length << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < length; i++) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void error(Context context, String message) {
        showDialog(context, android.R.drawable.ic_dialog_alert, context.getResources().getString(R.string.error_label), message);
    }

    public static void info(Context context, String title, String message) {
        showDialog(context, android.R.drawable.ic_dialog_info, title, message);
    }

    private static void showDialog(Context context, int icon, String title, String message) {
        new AlertDialog.Builder(context)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void confirm(Context context, String message, final Runnable task) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        task.run();
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showPlayingNotification(final Context context, Handler handler, MusicDirectory.Entry song) {

        // Use the same text for the ticker and the expanded notification
        String title = song.getTitle();

        // Set the icon, scrolling text and timestamp
        final Notification notification = new Notification(R.drawable.stat_sys_playing, title, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, DownloadActivity.class), 0);

        String text = song.getArtist();
        notification.setLatestEventInfo(context, title, text, contentIntent);

        // Send the notification.
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(Constants.NOTIFICATION_ID_PLAYING, notification);
            }
        });
    }

    public static void hidePlayingNotification(final Context context, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Constants.NOTIFICATION_ID_PLAYING);
            }
        });
    }

    public static void showErrorNotification(final Context context, Handler handler, String title, Exception error) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        StringBuilder text = new StringBuilder();
        if (error.getMessage() != null) {
            text.append(error.getMessage()).append(" (");
        }
        text.append(error.getClass().getSimpleName());
        if (error.getMessage() != null) {
            text.append(")");
        }

        // Set the icon, scrolling text and timestamp
        final Notification notification = new Notification(android.R.drawable.stat_sys_warning, title, System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ERROR, title + ".\n\n" + text);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, text, contentIntent);

        // Send the notification.
        handler.post(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(Constants.NOTIFICATION_ID_ERROR);
                notificationManager.notify(Constants.NOTIFICATION_ID_ERROR, notification);
            }
        });
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("net.sourceforge.subsonic.androidapp", 0);
            if (packageInfo != null) {
                return packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException x) {
            Log.w(TAG, "Failed to resolve application version name.", x);
        }
        return null;
    }

    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException x) {
            Log.e(TAG, "Interrupted from sleep.", x);
        }
    }
}