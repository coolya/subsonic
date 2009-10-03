package net.sourceforge.subsonic.android.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.TwoLineListAdapter;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.ImageLoader;

import java.util.List;

public class DownloadQueueActivity extends OptionsMenuActivity implements AdapterView.OnItemLongClickListener {

    private static final String TAG = DownloadQueueActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private ImageLoader imageLoader;
    private DownloadService downloadService;
    private ListView listView;
    private BroadcastReceiver broadcastReceiver;
    private TextView currentTextView;
    private TextView progressTextView;
    private TextView percentageTextView;
    private TextView totalTextView;
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_queue);
        currentTextView = (TextView) findViewById(R.id.download_queue_current);
        progressTextView = (TextView) findViewById(R.id.download_queue_progress);
        percentageTextView = (TextView) findViewById(R.id.download_queue_percentage);
        totalTextView = (TextView) findViewById(R.id.download_queue_total);
        progressBar = (ProgressBar) findViewById(R.id.download_queue_progress_bar);
        listView = (ListView) findViewById(R.id.download_queue_list);
        listView.setOnItemLongClickListener(this);

        bindService(new Intent(this, DownloadService.class), downloadServiceConnection, Context.BIND_AUTO_CREATE);
        imageLoader = new ImageLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();

        onDownloadQueueChanged();
        onDownloadProgressChanged();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean progressChanged = Constants.INTENT_ACTION_DOWNLOAD_PROGRESS.equals(intent.getAction());
                if (progressChanged) {
                    onDownloadProgressChanged();
                } else {
                    onDownloadQueueChanged();
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

    private void onDownloadQueueChanged() {
        if (downloadService == null) {
            return;
        }

        List<MusicDirectory.Entry> queue = downloadService.getQueue();
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = downloadService.getCurrent();

        if (current != null) {
            currentTextView.setText(current.getFirst().getTitle());
            imageLoader.loadImage(currentTextView, current.getFirst(), 48);

            Long bytesTotal = current.getSecond().getSecond();
            if (bytesTotal != null) {
                progressBar.setMax(bytesTotal.intValue());
            }
        } else if (queue.isEmpty()) {
            currentTextView.setText(null);
            currentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        listView.setAdapter(new SongListAdapter(queue));
    }

    private void onDownloadProgressChanged() {
        if (downloadService == null) {
            return;
        }
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = downloadService.getCurrent();
        if (current != null) {
            Long bytesDownloaded = current.getSecond().getFirst();
            progressTextView.setText(Util.formatBytes(bytesDownloaded));

            Long bytesTotal = current.getSecond().getSecond();
            if (bytesTotal != null) {
                long percentage = Math.round(100.0 * bytesDownloaded / bytesTotal);
                totalTextView.setText(Util.formatBytes(bytesTotal));
                percentageTextView.setText(percentage + " %");
                progressBar.setProgress(bytesDownloaded.intValue());
            } else {
                totalTextView.setText(null);
                percentageTextView.setText(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(downloadServiceConnection);
        imageLoader.cancel();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            final MusicDirectory.Entry song = (MusicDirectory.Entry) parent.getItemAtPosition(position);
            Log.d(TAG, song + " clicked.");

            final CharSequence[] items = {"Remove this song", "Remove all songs"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(song.getTitle());
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            downloadService.remove(song);
                            break;
                        case 1:
                            downloadService.clear();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return false;
    }

    private class DownloadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadService = ((SimpleServiceBinder<DownloadService>) service).getService();
            Log.i(TAG, "Connected to Download Service");
            onDownloadQueueChanged();
            onDownloadProgressChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
    }

    private class SongListAdapter extends TwoLineListAdapter<MusicDirectory.Entry> {
        public SongListAdapter(List<MusicDirectory.Entry> queue) {
            super(DownloadQueueActivity.this, queue);
        }

        @Override
            protected String getFirstLine(MusicDirectory.Entry song) {
            return song.getTitle();
        }

        @Override
            protected String getSecondLine(MusicDirectory.Entry song) {
            StringBuilder builder = new StringBuilder();
            builder.append(song.getAlbum()).append(" - ").append(song.getArtist());
            if (song.getSize() != null) {
                builder.append(" (").append(Util.formatBytes(song.getSize())).append(")");
            }
            return builder.toString();
        }
    }
}