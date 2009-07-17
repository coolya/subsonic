package net.sourceforge.subsonic.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;

public class SelectAlbumActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private DownloadService downloadService;
    private ListView entryList;
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
        setTitle(getIntent().getStringExtra(Constants.NAME_NAME));

        downloadButton = (Button) findViewById(R.id.select_album_download);
        selectAllButton = (Button) findViewById(R.id.select_album_selectall);
        selectNoneButton = (Button) findViewById(R.id.select_album_selectnone);
        entryList = (ListView) findViewById(R.id.select_album_entries);

        entryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);// TODO:Specify in XML.
        entryList.setOnItemClickListener(this);

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

        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        BackgroundTask<MusicDirectory> task = new BackgroundTask<MusicDirectory>(this) {
            @Override
            protected MusicDirectory doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService();
                String path = getIntent().getStringExtra(Constants.NAME_PATH);
                return musicService.getMusicDirectory(path, SelectAlbumActivity.this, this);
            }

            @Override
            protected void done(MusicDirectory result) {
                List<MusicDirectory.Entry> entries = result.getChildren();
                entryList.setAdapter(new EntryAdapter(entries));
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(SelectAlbumActivity.this, this);
                finish();
            }
        };
        task.execute();
    }

    private void selectAll(boolean selected) {
        int count = entryList.getCount();
        for (int i = 0; i < count; i++) {
            entryList.setItemChecked(i, selected);
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
            if (entry.isDirectory()) {
                Intent intent = new Intent(this, SelectAlbumActivity.class);
                intent.putExtra(Constants.NAME_PATH, entry.getId());
                intent.putExtra(Constants.NAME_NAME, entry.getName());
                startActivity(intent);
            } else {
                int count = entryList.getCount();
                boolean checked = false;
                for (int i = 0; i < count; i++) {
                    if (entryList.isItemChecked(i)) {
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
                List<MusicDirectory.Entry> songs = new ArrayList<MusicDirectory.Entry>(10);
                int count = entryList.getCount();
                for (int i = 0; i < count; i++) {
                    if (entryList.isItemChecked(i)) {
                        songs.add((MusicDirectory.Entry) entryList.getItemAtPosition(i));
                    }
                }
                downloadService.download(songs);
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


    private class EntryAdapter extends ArrayAdapter<MusicDirectory.Entry> {
        public EntryAdapter(List<MusicDirectory.Entry> entries) {
            super(SelectAlbumActivity.this, android.R.layout.simple_list_item_1, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicDirectory.Entry entry = getItem(position);
            TextView view;

            if (entry.isDirectory()) {
                if (convertView != null && convertView instanceof TextView && !(convertView instanceof CheckedTextView)) {
                    view = (TextView) convertView;
                    Log.i(TAG, "Reusing album view.");
                } else {
                    view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                            android.R.layout.simple_list_item_1, parent, false);
                    Log.i(TAG, "Creating new album view.");
                }
            } else {
                if (convertView != null && convertView instanceof CheckedTextView) {
                    view = (TextView) convertView;
                    Log.i(TAG, "Reusing song view.");
                } else {
                    view = (TextView) LayoutInflater.from(SelectAlbumActivity.this).inflate(
                            android.R.layout.simple_list_item_multiple_choice, parent, false);
                    Log.i(TAG, "Creating new song view.");
                }
            }

            view.setText(entry.getName());

            return view;
        }
    }
}