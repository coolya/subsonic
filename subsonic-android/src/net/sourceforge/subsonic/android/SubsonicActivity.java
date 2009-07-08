package net.sourceforge.subsonic.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class SubsonicActivity extends Activity {

    private static final String TAG = "Subsonic";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i(TAG, "Test of logging.");
        queryContentProviders();
        saveContact();
        saveImage();
//        saveArtist();
        saveAudio();
        saveVideo();
//        streamAudio();
        streamVideo();
        saveAudioToFile();
    }

    private void saveAudioToFile() {
        String url = "http://192.168.0.7/subsonic/stream?player=18&pathUtf8Hex=433a5c6d757369635c414344435c4c657420746865726520626520726f636b5c4c657420546865726520426520526f636b5f41432044432e6d7033&suffix=.mp3";

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new URL(url).openStream();
            out = openFileOutput("download", 0);
            long n = copy(in, out);
            Log.i(TAG, "Downloaded " + n + " bytes.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to save audio stream.", e);
        } finally {
            close(in);
            close(out);
        }
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

    public static void close(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
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

    private void streamAudio() {

        try {
            String url = "http://192.168.0.7/subsonic/stream?player=18&pathUtf8Hex=433a5c6d757369635c414344435c4c657420746865726520626520726f636b5c4c657420546865726520426520526f636b5f41432044432e6d7033&suffix=.mp3";

            Log.i(TAG, "Creating player.");
            MediaPlayer player = new MediaPlayer();

//            MediaController controller = new MediaController(this);
//            controller.setMediaPlayer(new VideoView(this));
//            controller.show();
//            controller.setAnchorView(get);
            player.setDataSource(url);

            Log.i(TAG, "Preparing player.");
            player.prepare();

            Log.i(TAG, "Starting player.");
            player.start();

            Log.i(TAG, "Started player.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to stream audio.", e);
        }
    }

    private void streamVideo() {

        try {
            String url = "http://192.168.0.7/subsonic/stream?player=18&pathUtf8Hex=433a5c6d757369635c414344435c4c657420746865726520626520726f636b5c4c657420546865726520426520526f636b5f41432044432e6d7033&suffix=.mp3";
            VideoView videoView = new VideoView(this);
            videoView.setVideoPath(url);
            MediaController controller = new MediaController(this);
            controller.setMediaPlayer(videoView);
            setContentView(videoView);
            Log.i(TAG, "Starting video.");
            videoView.start();

        } catch (Exception e) {
            Log.e(TAG, "Failed to stream video.", e);
        }
    }

    private void saveContact() {
        try {
            ContentValues values = new ContentValues();

            // Add Abraham Lincoln to contacts and make him a favorite.
            values.put(Contacts.PeopleColumns.NAME, "Abraham Lincoln");
            // 1 = the new contact is added to favorites
            // 0 = the new contact is not added to favorites
            values.put(Contacts.PeopleColumns.STARRED, 0);
            Uri uri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
            if (uri == null) {
                Log.e(TAG, "Failed to create contact.");
                return;
            }
            Log.i(TAG, "Saved contact: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create contact.", e);
        }
    }

    private void saveArtist() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.ArtistColumns.ARTIST, "U2");
            Uri uri = getContentResolver().insert(MediaStore.Audio.Artists.INTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Log.e(TAG, "Failed to create artist.");
                return;
            }
            Log.i(TAG, "Saved artist: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create artist.", e);
        }
    }

    private void saveImage() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "road_trip_1");
            values.put(MediaStore.Images.ImageColumns.DESCRIPTION, "Day 1, trip to Los Angeles");
            values.put(MediaStore.MediaColumns.TITLE, "Day 1, trip to Los Angeles");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            // Add a new record without the bitmap, but with the values just set.
            // insert() returns the URI of the new record.
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Log.e(TAG, "Failed to create image.");
                return;
            }
            Log.i(TAG, "Saved image: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create image.", e);
        }
    }

    private void saveVideo() {
        try {
            ContentValues values = new ContentValues();
//            values.put(MediaStore.Video.VideoColumns.TITLE, "Home video");

            // Add a new record without the bitmap, but with the values just set.
            // insert() returns the URI of the new record.
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Log.e(TAG, "Failed to create video.");
                return;
            }
            Log.i(TAG, "Saved video: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create video.", e);
        }
    }

    private void saveAudio() {
        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "foo");
//        values.put(MediaStore.MediaColumns.TITLE, "My title");
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
//        values.put(MediaStore.Audio.AudioColumns.ARTIST, "Sindre");
//        values.put(MediaStore.Audio.AudioColumns.ALBUM, "Pick");
//        values.put(MediaStore.Audio.AudioColumns.DURATION, 15000L);
//        values.put(MediaStore.Audio.AudioColumns.DATE_ADDED, System.currentTimeMillis() / 1000L);
//        values.put(MediaStore.Audio.AudioColumns.IS_ALARM, 0);
//        values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, 1);
//        values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, 0);
//        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, 0);

        // Add a new record without the bitmap, but with the values just set.
        // insert() returns the URI of the new record.
        Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            Log.e(TAG, "Failed to create audio.");
            return;
        }
        Log.i(TAG, "Saved audio: " + uri);

        // Now get a handle to the file for that record, and save the data into it.
        // Here, sourceBitmap is a Bitmap object representing the file to save to the database.
        try {
            OutputStream out = getContentResolver().openOutputStream(uri);
            for (int i = 0; i < 256; i++) {
                out.write(i);
            }
            out.close();
            Log.i(TAG, "Successfully saved audio to MediaStore: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "exception while writing audio", e);
        }

        // TODO: Investigate
        // Notify those applications such as Music listening to the
        // scanner events that a recorded audio file just created.
//         sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
    }

    private void queryContentProviders() {
        queryContentProvider(Contacts.People.CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Albums.INTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Artists.INTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        queryContentProvider(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private void queryContentProvider(Uri uri) {
        try {
            Log.i(TAG, "\nContent provider: " + uri);
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                Log.i(TAG, "No cursor.");
                return;
            }

            int count = 0;
            if (cursor.moveToFirst()) {
                int titleColumn = cursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
                int nameColumn = cursor.getColumnIndex(Contacts.PeopleColumns.NAME);

                do {
                    if (titleColumn != -1) {
                        Log.i(TAG, "Title " + count + ": " + cursor.getString(titleColumn));
                    }
                    if (nameColumn != -1) {
                        Log.i(TAG, "Name " + count + ": " + cursor.getString(nameColumn));
                    }

                    count++;

                } while (cursor.moveToNext());
            }

            Log.i(TAG, "Found " + count + " row(s).");

        } catch (Throwable e) {
            Log.i(TAG, "Failed to iterate cursor., ", e);
        }
    }
}
