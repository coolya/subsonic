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
package net.sourceforge.subsonic.androidapp.activity;

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
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.DownloadServiceImpl;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.BackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ImageLoader;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.SimpleServiceBinder;
import net.sourceforge.subsonic.androidapp.util.SongView;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SelectAlbumActivity extends OptionsMenuActivity {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private static final int MENU_ITEM_DOWNLOAD = 1;
    private static final int MENU_ITEM_ADD = 2;
    private static final int MENU_ITEM_DELETE = 3;
    private static final int MENU_ITEM_PLAY_ALL = 4;

    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private ImageLoader imageLoader;
    private DownloadService downloadService;
    private ListView entryList;
    private Button selectButton;
    private Button playButton;
    private Button moreButton;
    private boolean licenseValid;

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
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
                    if (entry.isDirectory()) {
                        Intent intent = new Intent(SelectAlbumActivity.this, SelectAlbumActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_NAME_PATH, entry.getId());
                        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getTitle());
                        startActivity(intent);
                    } else {
                        enableButtons();
                    }
                }
            }
        });
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAllOrNone();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(false, false, true);
                selectAll(false);
            }
        });

        registerForContextMenu(moreButton);
        registerForContextMenu(entryList);

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
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_ITEM_DOWNLOAD:
                download(true, true, false);
                selectAll(false);
                break;
            case MENU_ITEM_ADD:
                download(true, false, false);
                selectAll(false);
                break;
            case MENU_ITEM_DELETE:
                delete();
                selectAll(false);
                break;
            case MENU_ITEM_PLAY_ALL:
                playAll(menuItem);
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        if (view == moreButton) {
            if (!Util.isOffline(this)) {
                menu.add(Menu.NONE, MENU_ITEM_DOWNLOAD, MENU_ITEM_DOWNLOAD, R.string.select_album_save);
            }
            menu.add(Menu.NONE, MENU_ITEM_ADD, MENU_ITEM_ADD, R.string.select_album_add);

            for (MusicDirectory.Entry song : getSelectedSongs()) {
                DownloadFile downloadFile = downloadService.forSong(song);
                if (downloadFile.getCompleteFile().exists()) {
                    menu.add(Menu.NONE, MENU_ITEM_DELETE, MENU_ITEM_DELETE, R.string.select_album_delete);
                    break;
                }
            }
        } else {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            MusicDirectory.Entry entry = (MusicDirectory.Entry) entryList.getItemAtPosition(info.position);
            if (entry.isDirectory()) {
                menu.add(Menu.NONE, MENU_ITEM_PLAY_ALL, MENU_ITEM_PLAY_ALL, R.string.select_album_play_album);
            }
        }
    }

    private void getMusicDirectory() {
        String title = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME);
        if (Util.isOffline(this)) {
            title += " (" + getResources().getString(R.string.select_album_offline) + ")";
        }
        setTitle(title);
        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                String path = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PATH);
                return service.getMusicDirectory(path, SelectAlbumActivity.this, this);
            }
        }.execute();
    }

    private void search() {
        setTitle(R.string.select_album_searching);
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
                if (n == 0) {
                    setTitle(R.string.select_album_0_search_result);
                } else {
                    setTitle(getResources().getQuantityString(R.plurals.select_album_n_search_result, n, n));
                }
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

    private void download(final boolean append, final boolean save, final boolean autoplay) {
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

                downloadService.download(songs, save, autoplay);
                if (autoplay) {
                    startActivity(new Intent(SelectAlbumActivity.this, DownloadActivity.class));
                } else if (save) {
                    Util.toast(SelectAlbumActivity.this, getResources().getQuantityString(R.plurals.select_album_n_songs_downloading, songs.size(), songs.size()));
                } else if (append) {
                    Util.toast(SelectAlbumActivity.this, getResources().getQuantityString(R.plurals.select_album_n_songs_added, songs.size(), songs.size()));
                }
            }
        };

        checkLicenseAndTrialPeriod(onValid);
    }

    private void playAll(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

        MusicDirectory.Entry entry = (MusicDirectory.Entry) entryList.getItemAtPosition(info.position);

        Intent intent = new Intent(SelectAlbumActivity.this, SelectAlbumActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_NAME_PATH, entry.getId());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getTitle());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_PLAY_ALL, true);
        startActivity(intent);
    }

    private void delete() {
        if (downloadService == null) {
            return;
        }

        downloadService.delete(getSelectedSongs());
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
            Util.toast(this, getResources().getString(R.string.select_album_not_licensed, trialDaysLeft));
            onValid.run();
        }
    }

    private void showDonationDialog(int trialDaysLeft, final Runnable onValid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        if (trialDaysLeft == 0) {
            builder.setTitle(R.string.select_album_donate_dialog_0_trial_days_left);
        } else {
            builder.setTitle(getResources().getQuantityString(R.plurals.select_album_donate_dialog_n_trial_days_left, trialDaysLeft, trialDaysLeft));
        }

        builder.setMessage(R.string.select_album_donate_dialog_message);

        builder.setPositiveButton(R.string.select_album_donate_dialog_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATION_URL)));
            }
        });

        builder.setNegativeButton(R.string.select_album_donate_dialog_later, new DialogInterface.OnClickListener() {
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
                view.setDownloadFile(downloadService.forSong(entry), downloadService, true);

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
            MusicService musicService = MusicServiceFactory.getMusicService(SelectAlbumActivity.this);
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

            boolean playAll = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_NAME_PLAY_ALL, false);
            if (playAll) {
                selectAll(true);
                download(false, false, true);
                selectAll(false);
            }
        }

        @Override
        protected void cancel() {
            MusicServiceFactory.getMusicService(SelectAlbumActivity.this).cancel(SelectAlbumActivity.this, this);
            finish();
        }
    }
}