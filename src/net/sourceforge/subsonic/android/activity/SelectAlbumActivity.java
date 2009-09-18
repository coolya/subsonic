package net.sourceforge.subsonic.android.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.service.StreamService;
import net.sourceforge.subsonic.android.util.BackgroundTask;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ImageLoader;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SelectAlbumActivity extends OptionsMenuActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private final StreamServiceConnection streamServiceConnection = new StreamServiceConnection();
    private ImageLoader imageLoader;
    private DownloadService downloadService;
    private StreamService streamService;
    private ListView entryList;
    private ImageButton selectAllOrNoneButton;
    private ImageButton playButton;
    private ImageButton addButton;
    private ImageButton downloadButton;
    private boolean licenseValid;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_album);
        setTitle(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME));

        imageLoader = new ImageLoader();
        downloadButton = (ImageButton) findViewById(R.id.select_album_download);
        playButton = (ImageButton) findViewById(R.id.select_album_play);
        addButton = (ImageButton) findViewById(R.id.select_album_add);
        selectAllOrNoneButton = (ImageButton) findViewById(R.id.select_album_selectallornone);
        entryList = (ListView) findViewById(R.id.select_album_entries);

        entryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);// TODO:Specify in XML.
        entryList.setOnItemClickListener(this);

        selectAllOrNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAllOrNone();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
                selectAll(false);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPlaylist(false);
                selectAll(false);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPlaylist(true);
                selectAll(false);
            }
        });

        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, StreamService.class), streamServiceConnection, Context.BIND_AUTO_CREATE);

        enableButtons();
        load();
    }

    private void load() {
        new BackgroundTask<Pair<MusicDirectory, Boolean>>(SelectAlbumActivity.this) {
            @Override
            protected Pair<MusicDirectory, Boolean> doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService();
                String path = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PATH);
                MusicDirectory dir = musicService.getMusicDirectory(path, SelectAlbumActivity.this, this);
                boolean valid = musicService.isLicenseValid(SelectAlbumActivity.this, this);
                return new Pair<MusicDirectory, Boolean>(dir, valid);
            }

            @Override
            protected void done(Pair<MusicDirectory, Boolean> result) {
                List<MusicDirectory.Entry> entries = result.getFirst().getChildren();
                entryList.setAdapter(new EntryAdapter(entries));

                int visibility = View.GONE;
                for (MusicDirectory.Entry entry : entries) {
                    if (!entry.isDirectory()) {
                        visibility = View.VISIBLE;
                        break;
                    }
                }
                licenseValid = result.getSecond();
                downloadButton.setVisibility(visibility);
                playButton.setVisibility(visibility);
                addButton.setVisibility(visibility);
                selectAllOrNoneButton.setVisibility(visibility);
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(SelectAlbumActivity.this, this);
                finish();
            }
        }.execute();
    }

    private void selectAllOrNone() {
        boolean someUnselected = false;
        int count = entryList.getCount();
        for (int i = 0; i < count; i++) {
            if (!entryList.isItemChecked(i)) {
                someUnselected = true;
                break;
            }
        }
        selectAll(someUnselected);
    }

    private void selectAll(boolean selected) {
        int count = entryList.getCount();
        for (int i = 0; i < count; i++) {
            entryList.setItemChecked(i, selected);
        }
        enableButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
        unbindService(streamServiceConnection);
        imageLoader.cancel();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
            Log.d(TAG, entry + " clicked.");
            if (entry.isDirectory()) {
                Intent intent = new Intent(this, SelectAlbumActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_PATH, entry.getId());
                intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getTitle());
                startActivity(intent);
            } else {
                enableButtons();
            }
        }
    }

    private void enableButtons() {
        int count = entryList.getCount();
        boolean checked = false;
        for (int i = 0; i < count; i++) {
            if (entryList.isItemChecked(i)) {
                checked = true;
                break;
            }
        }
        downloadButton.setEnabled(checked);
        playButton.setEnabled(checked);
        addButton.setEnabled(checked);
    }

    private void download() {
        if (downloadService == null) {
            return;
        }

        List<MusicDirectory.Entry> songs = getSelectedSongs();
        if (isLicenseValidOrHasCredits(songs.size())) {
            downloadService.download(songs);
            startActivity(new Intent(this, DownloadQueueActivity.class));
        }
    }

    private void addToPlaylist(boolean append) {
        if (streamService == null) {
            return;
        }

        List<MusicDirectory.Entry> songs = getSelectedSongs();
        if (isLicenseValidOrHasCredits(songs.size())) {
            streamService.add(songs, append);
            startActivity(new Intent(this, StreamQueueActivity.class));
        }
    }

    private boolean isLicenseValidOrHasCredits(int creditsRequired) {
        Util.decrementCredits(this, creditsRequired);

        if (!licenseValid) {
            if (Util.getCredits(this) == 0) {
                showDonationDialog();
                return false;
            } else {
                Util.toast(this, "Server not licensed. " + Util.getCredits(this) + " credits left.");
            }
        }
        return true;
    }

    private void showDonationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("No credits left");
        builder.setMessage("Get unlimited downloads by donating to Subsonic. You decide the amount!");

        builder.setPositiveButton("Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://subsonic.sf.net/android-donation.php")));
            }
        });

        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private List<MusicDirectory.Entry> getSelectedSongs() {
        List<MusicDirectory.Entry> songs = new ArrayList<MusicDirectory.Entry>(10);
        int count = entryList.getCount();
        for (int i = 0; i < count; i++) {
            if (entryList.isItemChecked(i)) {
                songs.add((MusicDirectory.Entry) entryList.getItemAtPosition(i));
            }
        }
        return songs;
    }


    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((SimpleServiceBinder<DownloadService>) service).getService();
            Log.i(TAG, "Connected to Download Service");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
    }


    private class StreamServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            streamService = ((SimpleServiceBinder<StreamService>) service).getService();
            Log.i(TAG, "Connected to Stream Service");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            streamService = null;
            Log.i(TAG, "Disconnected from Stream Service");
        }
    }


    private class EntryAdapter extends ArrayAdapter<MusicDirectory.Entry> {
        public EntryAdapter(List<MusicDirectory.Entry> entries) {
            super(SelectAlbumActivity.this, android.R.layout.simple_list_item_1, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicDirectory.Entry entry = getItem(position);
            TextView view;

            if (entry.isDirectory()) {
                view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                        android.R.layout.simple_list_item_1, parent, false);

                view.setCompoundDrawablePadding(10);
                imageLoader.loadImage(view, entry);

            } else {
                if (convertView != null && convertView instanceof CheckedTextView) {
                    view = (TextView) convertView;
                } else {
                    view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                            android.R.layout.simple_list_item_multiple_choice, parent, false);
                }
            }

            view.setText(entry.getTitle());

            return view;
        }
    }
}