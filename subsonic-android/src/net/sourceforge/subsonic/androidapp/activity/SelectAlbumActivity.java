package net.sourceforge.subsonic.androidapp.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
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
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.service.StreamService;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

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
    private ImageButton downloadAndPlayButton;
    private boolean licenseValid;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_album);
        setTitle(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME));

        imageLoader = new ImageLoader();
        playButton = (ImageButton) findViewById(R.id.select_album_play);
        addButton = (ImageButton) findViewById(R.id.select_album_add);
        downloadButton = (ImageButton) findViewById(R.id.select_album_download);
        downloadAndPlayButton = (ImageButton) findViewById(R.id.select_album_download_and_play);
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

        downloadAndPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPlaylist(false, true);
                selectAll(false);
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
                addToPlaylist(false, false);
                selectAll(false);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPlaylist(true, false);
                selectAll(false);
            }
        });

        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, StreamService.class), streamServiceConnection, Context.BIND_AUTO_CREATE);

        enableButtons();
        load();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Repaint list when download completes.
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.INTENT_ACTION_DOWNLOAD_QUEUE.equals(intent.getAction())) {
                    repaintList();
                }
            }};

        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DOWNLOAD_QUEUE));
        repaintList();
    }

    private void repaintList() {
        EntryAdapter entryAdapter = (EntryAdapter) entryList.getAdapter();
        if (entryAdapter != null) {
            entryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
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

                selectAllOrNoneButton.setVisibility(visibility);
                playButton.setVisibility(visibility);
                addButton.setVisibility(visibility);
                downloadButton.setVisibility(visibility);
                downloadAndPlayButton.setVisibility(visibility);
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
            MusicDirectory.Entry entry = (MusicDirectory.Entry) entryList.getItemAtPosition(i);
            if (!entry.isDirectory()) {
                entryList.setItemChecked(i, selected);
            }
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
        playButton.setEnabled(checked);
        addButton.setEnabled(checked);
        downloadButton.setEnabled(checked);
        downloadAndPlayButton.setEnabled(checked);
    }

    private void download() {
        if (downloadService == null) {
            return;
        }

        final List<MusicDirectory.Entry> songs = getSelectedSongs();
        Runnable onValid = new Runnable() {
            @Override
            public void run() {
                downloadService.download(songs);
                startActivity(new Intent(SelectAlbumActivity.this, DownloadQueueActivity.class));
            }
        };

        checkLicenseAndTrialPeriod(onValid);
    }

    private void addToPlaylist(final boolean append, final boolean download) {
        if (streamService == null) {
            return;
        }

        final List<MusicDirectory.Entry> songs = getSelectedSongs();
        Runnable onValid = new Runnable() {
            @Override
            public void run() {
                if (download) {
                    downloadService.download(songs);
                }
                streamService.add(songs, append, download);
                startActivity(new Intent(SelectAlbumActivity.this, StreamQueueActivity.class));
            }
        };

        checkLicenseAndTrialPeriod(onValid);
    }

    private void checkLicenseAndTrialPeriod(Runnable onValid) {
        if (licenseValid) {
            onValid.run();
            return;
        }

        int trialDaysLeft = Util.getRemainingTrialDays(this);
        Log.i(TAG, trialDaysLeft + " trial days left.");

        if (trialDaysLeft == 0) {
            showDonationDialog(trialDaysLeft, null);
        } else if (trialDaysLeft < Constants.FREE_TRIAL_DAYS / 2) {
            showDonationDialog(trialDaysLeft, onValid);
        } else {
            Util.toast(this, "Server not licensed. " + trialDaysLeft + " trial days left.");
            onValid.run();
        }
    }

    private void showDonationDialog(int trialDaysLeft, final Runnable onValid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        if (trialDaysLeft == 0) {
            builder.setTitle("Trial period is over");
        } else if (trialDaysLeft == 1) {
            builder.setTitle("One day left of trial period");
        } else {
            builder.setTitle((trialDaysLeft + " days left of trial period"));
        }
        builder.setMessage("Get unlimited downloads by donating to Subsonic.");

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
                if (onValid != null) {
                    onValid.run();
                }
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
                imageLoader.loadImage(view, entry, 48);

            } else {
                if (convertView != null && convertView instanceof CheckedTextView) {
                    view = (TextView) convertView;
                } else {
                    view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                            android.R.layout.simple_list_item_checked, parent, false);
                }
                File file = downloadService.getSongFile(entry, false);
                if (file.exists()) {
                    view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.downloaded, 0);
                } else {
                    view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            view.setText(entry.getTitle());

            return view;
        }
    }
}