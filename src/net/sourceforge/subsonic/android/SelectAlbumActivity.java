package net.sourceforge.subsonic.android;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;

public class SelectAlbumActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private DownloadService downloadService;
    private ListView albumList;
    private ListView songList;
    private Button downloadButton;
    private Button selectAllButton;
    private Button selectNoneButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_album);

        downloadButton = (Button) findViewById(R.id.select_album_download);
        selectAllButton = (Button) findViewById(R.id.select_album_selectall);
        selectNoneButton = (Button) findViewById(R.id.select_album_selectnone);
        albumList = (ListView) findViewById(R.id.select_album_albums);
        songList = (ListView) findViewById(R.id.select_album_songs);

        albumList.setOnItemClickListener(this);
        songList.setItemsCanFocus(false);
        songList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);// TODO:Specify in XML.
        songList.setOnItemClickListener(this);

        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll(true);
            }
        });

        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll(false);
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });

        bindService(new Intent(this, DownloadService.class),
                downloadServiceConnection, Context.BIND_AUTO_CREATE);
        BackgroundTask<MusicDirectory> task = new BackgroundTask<MusicDirectory>(this) {
            @Override
            protected MusicDirectory doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService();
                String path = getIntent().getStringExtra(Constants.NAME_PATH);
                return musicService.getMusicDirectory(path, this);
            }

            @Override
            protected void done(MusicDirectory result) {
                List<MusicDirectory.Entry> albums = result.getChildren(true, false);
                List<MusicDirectory.Entry> songs = result.getChildren(false, true);
                albumList.setAdapter(new ArrayAdapter<MusicDirectory.Entry>(SelectAlbumActivity.this, android.R.layout.simple_list_item_1, albums));
                songList.setAdapter(new ArrayAdapter<MusicDirectory.Entry>(SelectAlbumActivity.this, android.R.layout.simple_list_item_multiple_choice, songs));
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(this);
                finish();
            }
        };
        task.execute();
    }

    private void selectAll(boolean selected) {
        int count = songList.getCount();
        for (int i = 0; i < count; i++) {
            songList.setItemChecked(i, selected);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
            Log.d(TAG, entry + " clicked.");
            // TODO: Use (view == albumList) instead?
            if (entry.isDirectory()) {
                Intent intent = new Intent(this, SelectAlbumActivity.class);
                intent.putExtra(Constants.NAME_PATH, entry.getPath());
                startActivity(intent);
            } else {
                int count = songList.getCount();
                boolean checked = false;
                for (int i = 0; i < count; i++) {
                    if (songList.isItemChecked(i)) {
                        checked = true;
                        break;
                    }
                }
                downloadButton.setEnabled(checked);
            }
        }
    }

    private void download() {
        try {
            if (downloadService != null) {
                int count = songList.getCount();
                for (int i = 0; i < count; i++) {
                    if (songList.isItemChecked(i)) {
                        MusicDirectory.Entry entry = (MusicDirectory.Entry) songList.getItemAtPosition(i);
                        downloadService.download(entry);
                    }
                }
            } else {
                Log.e(TAG, "Not connected to Download Service.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to contact Download Service.");
        }
    }


    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
            Log.i(TAG, "Connected to Download Service");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
    }

}