package net.sourceforge.subsonic.androidapp.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.FileUtil;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.SongView;
import net.sourceforge.subsonic.androidapp.util.Util;

public class SelectAlbumActivity extends OptionsMenuActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private ImageLoader imageLoader;
    private DownloadService downloadService;
    private ListView entryList;
    private Button selectButton;
    private Button playButton;
    private Button moreButton;
    private boolean licenseValid;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_album);

        imageLoader = new ImageLoader();
        selectButton = (Button) findViewById(R.id.select_album_select);
        playButton = (Button) findViewById(R.id.select_album_play);
        moreButton = (Button) findViewById(R.id.select_album_more);
        entryList = (ListView) findViewById(R.id.select_album_entries);

        entryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        entryList.setOnItemClickListener(this);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAllOrNone();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(false, false);
                selectAll(false);
            }
        });

        registerForContextMenu(moreButton);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreButton.showContextMenu();
            }
        });

        bindService(new Intent(this, DownloadServiceImpl.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        enableButtons();

        String query = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_QUERY);
        String playlist = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME);

        if (query != null) {
            search();
        } else if (playlist != null) {
            getPlaylist();
        } else {
            getMusicDirectory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Repaint list when download completes.
        // TODO
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.INTENT_ACTION_DOWNLOAD_QUEUE.equals(intent.getAction())) {
                    repaintList();
                }
            }
        };

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

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 1:
                download(false, true);
                selectAll(false);
                break;
            case 2:
                download(true, false);
                selectAll(false);
                break;
            case 3:
                delete();
                selectAll(false);
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(Menu.NONE, 1, 1, "Play + Save");
        menu.add(Menu.NONE, 2, 2, "Add to play queue");

        for (MusicDirectory.Entry song : getSelectedSongs()) {
            // TODO: Check for .complete also.
            File file = FileUtil.getSongFile(song, false);
            if (file.exists()) {
                menu.add(Menu.NONE, 3, 3, "Delete from phone");
                break;
            }
        }
    }

    private void getMusicDirectory() {
        setTitle(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME));
        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                String path = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PATH);
                return service.getMusicDirectory(path, SelectAlbumActivity.this, this);
            }
        }.execute();
    }

    private void search() {
        setTitle("Search results");
        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                String query = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_QUERY);
                return service.search(query, SelectAlbumActivity.this, this);
            }

            @Override
            protected void done(Pair<MusicDirectory, Boolean> result) {
                super.done(result);
                int n = result.getFirst().getChildren().size();
                setTitle("Search results - " + n + " match" + (n == 1 ? "" : "es"));
            }
        }.execute();
    }

    private void getPlaylist() {
        setTitle(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME));
        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                String id = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_ID);
                return service.getPlaylist(id, SelectAlbumActivity.this, this);
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
        moreButton.setEnabled(checked);
    }

    private void download(final boolean append, final boolean save) {
        if (downloadService == null) {
            return;
        }

        final List<MusicDirectory.Entry> songs = getSelectedSongs();
        Runnable onValid = new Runnable() {
            @Override
            public void run() {
                if (!append) {
                    downloadService.clear();
                }

                downloadService.download(songs, save, true);
                startActivity(new Intent(SelectAlbumActivity.this, DownloadActivity.class));
            }
        };

        checkLicenseAndTrialPeriod(onValid);
    }

    private void delete() {
        if (downloadService == null) {
            return;
        }

        // TODO
//        downloadService.delete(getSelectedSongs());
        repaintList();
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATION_URL)));
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


    private class EntryAdapter extends ArrayAdapter<MusicDirectory.Entry> {
        public EntryAdapter(List<MusicDirectory.Entry> entries) {
            super(SelectAlbumActivity.this, android.R.layout.simple_list_item_1, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicDirectory.Entry entry = getItem(position);

            if (entry.isDirectory()) {
                TextView view;
                view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                        android.R.layout.simple_list_item_1, parent, false);

                view.setCompoundDrawablePadding(10);
                imageLoader.loadImage(view, entry, 48);
                view.setText(entry.getTitle());

                return view;

            } else {
                SongView view;
                if (convertView != null && convertView instanceof SongView) {
                    view = (SongView) convertView;
                } else {
                    view = new SongView(SelectAlbumActivity.this);
                }
                File file = FileUtil.getSongFile(entry, false);
                view.setSong(entry, file);
                return view;
            }
        }
    }


    private abstract class LoadTask extends BackgroundTask<Pair<MusicDirectory, Boolean>> {

        public LoadTask() {
            super(SelectAlbumActivity.this);
        }

        protected abstract MusicDirectory load(MusicService service) throws Exception;

        @Override
        protected Pair<MusicDirectory, Boolean> doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService();
            MusicDirectory dir = load(musicService);
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

            selectButton.setVisibility(visibility);
            playButton.setVisibility(visibility);
            moreButton.setVisibility(visibility);
        }

        @Override
        protected void cancel() {
            MusicServiceFactory.getMusicService().cancel(SelectAlbumActivity.this, this);
            finish();
        }
    }
}