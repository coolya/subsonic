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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.DownloadFile;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.EntryAdapter;
import net.sourceforge.subsonic.androidapp.util.Pair;
import net.sourceforge.subsonic.androidapp.util.TabActivityBackgroundTask;
import net.sourceforge.subsonic.androidapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SelectAlbumActivity extends SubsonicTabActivity {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private static final int MENU_ITEM_PLAY_ALL = 1;

    private ListView entryList;
    private View header;
    private View footer;
    private View emptyView;
    private Button selectButton;
    private Button playButton;
    private Button queueButton;
    private Button saveButton;
    private Button deleteButton;
    private Button moreButton;
    private ImageView coverArtView;
    private TextView headerText1;
    private TextView headerText2;
    private ImageButton playAllButton;
    private boolean licenseValid;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_album);

        entryList = (ListView) findViewById(R.id.select_album_entries);

        header = LayoutInflater.from(this).inflate(R.layout.select_album_header, entryList, false);
        footer = LayoutInflater.from(this).inflate(R.layout.select_album_footer, entryList, false);
        entryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
                    if (entry.isDirectory()) {
                        Intent intent = new Intent(SelectAlbumActivity.this, SelectAlbumActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, entry.getId());
                        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getTitle());
                        Util.startActivityWithoutTransition(SelectAlbumActivity.this, intent);
                    } else {
                        enableButtons();
                    }
                }
            }
        });

        coverArtView = (ImageView) header.findViewById(R.id.select_album_cover_art);
        headerText1 = (TextView) header.findViewById(R.id.select_album_text1);
        headerText2 = (TextView) header.findViewById(R.id.select_album_text2);
        playAllButton = (ImageButton) header.findViewById(R.id.select_album_play_all);

        selectButton = (Button) findViewById(R.id.select_album_select);
        playButton = (Button) findViewById(R.id.select_album_play);
        queueButton = (Button) findViewById(R.id.select_album_queue);
        saveButton = (Button) footer.findViewById(R.id.select_album_save);
        deleteButton = (Button) footer.findViewById(R.id.select_album_delete);
        moreButton = (Button) footer.findViewById(R.id.select_album_more);
        emptyView = findViewById(R.id.select_album_empty);

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
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(true, false, false);
                selectAll(false);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(true, true, false);
                selectAll(false);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
                selectAll(false);
            }
        });

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll(true);
                download(false, false, true);
                selectAll(false);
            }
        });

        registerForContextMenu(entryList);

        enableButtons();

        String id = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_ID);
        String name = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME);
        String playlistId = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_ID);
        String playlistName = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME);
        String albumListType = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE);
        int albumListSize = getIntent().getIntExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_SIZE, 0);
        int albumListOffset = getIntent().getIntExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_OFFSET, 0);

        if (playlistId != null) {
            getPlaylist(playlistId, playlistName);
        } else if (albumListType != null) {
            getAlbumList(albumListType, albumListSize, albumListOffset);
        } else {
            getMusicDirectory(id, name);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
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

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MusicDirectory.Entry entry = (MusicDirectory.Entry) entryList.getItemAtPosition(info.position);
        if (entry.isDirectory()) {
            menu.add(Menu.NONE, MENU_ITEM_PLAY_ALL, MENU_ITEM_PLAY_ALL, R.string.select_album_play_album);
        }
    }

    private void getMusicDirectory(final String id, String name) {
        headerText1.setText(name);
        setTitle(Util.isOffline(this) ? R.string.music_library_label_offline : R.string.music_library_label);

        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                return service.getMusicDirectory(id, SelectAlbumActivity.this, this);
            }
        }.execute();
    }

    private void getPlaylist(final String playlistId, String playlistName) {
        setTitle(playlistName);
        headerText1.setText(playlistName);

        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                return service.getPlaylist(playlistId, SelectAlbumActivity.this, this);
            }
        }.execute();
    }

    private void getAlbumList(final String albumListType, final int size, final int offset) {

        if ("newest".equals(albumListType)) {
            setTitle(R.string.main_albums_newest);
        } else if ("random".equals(albumListType)) {
            setTitle(R.string.main_albums_random);
        } else if ("highest".equals(albumListType)) {
            setTitle(R.string.main_albums_highest);
        } else if ("recent".equals(albumListType)) {
            setTitle(R.string.main_albums_recent);
        } else if ("frequent".equals(albumListType)) {
            setTitle(R.string.main_albums_frequent);
        }

        new LoadTask() {
            @Override
            protected MusicDirectory load(MusicService service) throws Exception {
                return service.getAlbumList(albumListType, size, offset, SelectAlbumActivity.this, this);
            }

            @Override
            protected void done(Pair<MusicDirectory, Boolean> result) {
                if (!result.getFirst().getChildren().isEmpty()) {
                    saveButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                    moreButton.setVisibility(View.VISIBLE);
                    entryList.addFooterView(footer);

                    moreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SelectAlbumActivity.this, SelectAlbumActivity.class);
                            String type = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE);
                            int size = getIntent().getIntExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_SIZE, 0);
                            int offset = getIntent().getIntExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_OFFSET, 0) + size;

                            intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_TYPE, type);
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_SIZE, size);
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_ALBUM_LIST_OFFSET, offset);
                            Util.startActivityWithoutTransition(SelectAlbumActivity.this, intent);
                        }
                    });
                }
                super.done(result);
            }
        }.execute();
    }

    private void selectAllOrNone() {
        boolean someUnselected = false;
        int count = entryList.getCount();
        for (int i = 0; i < count; i++) {
            if (!entryList.isItemChecked(i) && entryList.getItemAtPosition(i) instanceof MusicDirectory.Entry) {
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
            if (entry != null && !entry.isDirectory()) {
                entryList.setItemChecked(i, selected);
            }
        }
        enableButtons();
    }

    private void enableButtons() {
        if (getDownloadService() == null) {
            return;
        }

        List<MusicDirectory.Entry> selection = getSelectedSongs();
        boolean enabled = !selection.isEmpty();
        boolean deleteEnabled = false;

        for (MusicDirectory.Entry song : selection) {
            DownloadFile downloadFile = getDownloadService().forSong(song);
            if (downloadFile.getCompleteFile().exists()) {
                deleteEnabled = true;
                break;
            }
        }

        playButton.setEnabled(enabled);
        queueButton.setEnabled(enabled);
        saveButton.setEnabled(enabled && !Util.isOffline(this));
        deleteButton.setEnabled(deleteEnabled);
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

    private void download(final boolean append, final boolean save, final boolean autoplay) {
        if (getDownloadService() == null) {
            return;
        }

        final List<MusicDirectory.Entry> songs = getSelectedSongs();
        Runnable onValid = new Runnable() {
            @Override
            public void run() {
                if (!append) {
                    getDownloadService().clear();
                }

                warnIfNetworkOrStorageUnavailable();
                getDownloadService().download(songs, save, autoplay);
                getDownloadService().setSuggestedPlaylistName(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_PLAYLIST_NAME));
                if (autoplay) {
                    Util.startActivityWithoutTransition(SelectAlbumActivity.this, DownloadActivity.class);
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
        intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, entry.getId());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getTitle());
        intent.putExtra(Constants.INTENT_EXTRA_NAME_AUTOPLAY, true);
        Util.startActivityWithoutTransition(SelectAlbumActivity.this, intent);
    }

    private void delete() {
        if (getDownloadService() == null) {
            return;
        }

        getDownloadService().delete(getSelectedSongs());
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

    private abstract class LoadTask extends TabActivityBackgroundTask<Pair<MusicDirectory, Boolean>> {

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

            int songCount = 0;
            for (MusicDirectory.Entry entry : entries) {
                if (!entry.isDirectory()) {
                    songCount++;
                }
            }

            if (songCount > 0) {
                headerText2.setText(getResources().getQuantityString(R.plurals.select_album_n_songs, songCount, songCount));
                getImageLoader().loadImage(coverArtView, entries.get(0), false);
                entryList.addHeaderView(header);
                entryList.addFooterView(footer);
                selectButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
                queueButton.setVisibility(View.VISIBLE);
            }

            emptyView.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
            entryList.setAdapter(new EntryAdapter(SelectAlbumActivity.this, getImageLoader(), entries, true));
            licenseValid = result.getSecond();

            boolean playAll = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_NAME_AUTOPLAY, false);
            if (playAll && songCount > 0) {
                selectAll(true);
                download(false, false, true);
                selectAll(false);
            }
        }
    }
}