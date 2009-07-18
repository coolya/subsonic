package net.sourceforge.subsonic.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;
import android.widget.ProgressBar;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.util.TwoLineListAdapter;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.R;

import java.util.List;

public class DownloadQueueActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = DownloadQueueActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private DownloadService downloadService;
    private ListView listView;
    private Button downloadButton;
    private Button selectAllButton;
    private Button selectNoneButton;
    private BroadcastReceiver broadcastReceiver;
    private TextView progressTextView;
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_queue);
        progressTextView = (TextView) findViewById(R.id.download_queue_progress_text);
        progressBar = (ProgressBar) findViewById(R.id.download_queue_progress_bar);

//        downloadButton = (Button) findViewById(R.id.select_album_download);
//        selectAllButton = (Button) findViewById(R.id.select_album_selectall);
//        selectNoneButton = (Button) findViewById(R.id.select_album_selectnone);
        listView = (ListView) findViewById(R.id.download_queue_list);

//        listView.setOnItemClickListener(this);

//        selectAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectAll(true);
//            }
//        });

        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "GOT BROADCAST " + intent);
                boolean progressChanged = Constants.INTENT_ACTION_DOWNLOAD_PROGRESS.equals(intent.getAction());
                if (progressChanged) {
                    downloadProgressChanged();
                } else {
                    downloadQueueChanged();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DOWNLOAD_QUEUE));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DOWNLOAD_PROGRESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void downloadQueueChanged() {
        if (downloadService == null) {
            return;
        }

        List<MusicDirectory.Entry> queue = downloadService.getQueue();
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = downloadService.getCurrent();

        if (current != null) {
            queue.add(0, current.getFirst());

            Long bytesTotal = current.getSecond().getSecond();
            progressBar.setIndeterminate(bytesTotal != null);
            if (bytesTotal != null) {
                progressBar.setMax(bytesTotal.intValue());
            }
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressTextView.setText("Download queue is empty");
            progressBar.setVisibility(View.INVISIBLE);
        }

        listView.setAdapter(new TwoLineListAdapter<MusicDirectory.Entry>(this, queue) {
            @Override
            protected String getFirstLine(MusicDirectory.Entry item) {
                return item.getName();
            }

            @Override
            protected String getSecondLine(MusicDirectory.Entry item) {
                return "Send Away The Tigers - Manic Street Foo Bar Preachers\n4.55 MB"; //TODO
            }
        });
    }

    private void downloadProgressChanged() {
        if (downloadService == null) {
            return;
        }
        Pair<MusicDirectory.Entry, Pair<Long,Long>> current = downloadService.getCurrent();
        if (current != null) {
            Long bytesDownloaded = current.getSecond().getFirst();
            progressTextView.setText(current.getFirst().getName() + "\n" + "Downloaded " + Util.formatBytes(bytesDownloaded));

            if (!progressBar.isIndeterminate()) {
                progressBar.setProgress(bytesDownloaded.intValue());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position >= 0) {
//            MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
//            Log.d(TAG, entry + " clicked.");
//            if (entry.isDirectory()) {
//                Intent intent = new Intent(this, DownloadQueueActivity.class);
//                intent.putExtra(Constants.INTENT_EXTRA_NAME_PATH, entry.getId());
//                intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getName());
//                startActivity(intent);
//            } else {
//                int count = entryList.getCount();
//                boolean checked = false;
//                for (int i = 0; i < count; i++) {
//                    if (entryList.isItemChecked(i)) {
//                        checked = true;
//                        break;
//                    }
//                }
//                downloadButton.setEnabled(checked);
//            }
//        }
    }

    private void download() {
//        try {
//            if (downloadService != null) {
//                List<MusicDirectory.Entry> songs = new ArrayList<MusicDirectory.Entry>(10);
//                int count = entryList.getCount();
//                for (int i = 0; i < count; i++) {
//                    if (entryList.isItemChecked(i)) {
//                        songs.add((MusicDirectory.Entry) entryList.getItemAtPosition(i));
//                    }
//                }
//                downloadService.download(songs);
//            } else {
//                Log.e(TAG, "Not connected to Download Service.");
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to contact Download Service.");
//        }
    }


    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
            Log.i(TAG, "Connected to Download Service");
            downloadQueueChanged();
            downloadProgressChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
    }

}