package net.sourceforge.subsonic.android.activity;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.StreamService;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.TwoLineListAdapter;
import net.sourceforge.subsonic.android.util.Util;

import java.util.List;

public class StreamQueueActivity extends OptionsMenuActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = StreamQueueActivity.class.getSimpleName();
    private final StreamServiceConnection streamServiceConnection = new StreamServiceConnection();
    private StreamService streamService;
    private ListView listView;
    private BroadcastReceiver broadcastReceiver;
    private TextView progressTextView;
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_queue);
        progressTextView = (TextView) findViewById(R.id.stream_queue_progress_text);
        progressBar = (ProgressBar) findViewById(R.id.stream_queue_progress_bar);
        listView = (ListView) findViewById(R.id.stream_queue_list);

        listView.setOnItemClickListener(this);
        bindService(new Intent(this, StreamService.class), streamServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        streamQueueChanged();
        streamProgressChanged();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean progressChanged = Constants.INTENT_ACTION_STREAM_PROGRESS.equals(intent.getAction());
                if (progressChanged) {
                    streamProgressChanged();
                } else {
                    streamQueueChanged();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_STREAM_QUEUE));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_STREAM_PROGRESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void streamQueueChanged() {
        if (streamService == null) {
            return;
        }

        List<MusicDirectory.Entry> queue = streamService.getQueue();
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = streamService.getCurrent();

        if (current != null) {

            Long millisTotal = current.getSecond().getSecond();
            progressBar.setIndeterminate(millisTotal == null);

            if (millisTotal != null) {
                progressBar.setMax(millisTotal.intValue());
            }
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressTextView.setText("Playlist is empty");
            progressBar.setVisibility(View.INVISIBLE);
        }

        listView.setAdapter(new TwoLineListAdapter<MusicDirectory.Entry>(this, queue) {
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
        });
    }

    private void streamProgressChanged() {
        if (streamService == null) {
            return;
        }
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = streamService.getCurrent();
        if (current != null) {

            // TODO: Handle that total time is unknown?
            int millisPlayed = current.getSecond().getFirst().intValue();
            int millisTotal = current.getSecond().getSecond().intValue();
            progressTextView.setText(current.getFirst().getTitle() + "\n" + Util.formatDuration(millisPlayed / 1000) +
                                     " of " + Util.formatDuration(millisTotal / 1000));

            if (!progressBar.isIndeterminate()) {
                progressBar.setProgress(millisPlayed);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(streamServiceConnection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        streamService.play(position);
    }

    private class StreamServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            streamService = ((SimpleServiceBinder<StreamService>) service).getService();
            Log.i(TAG, "Connected to Stream Service");
            streamQueueChanged();
            streamProgressChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            streamService = null;
            Log.i(TAG, "Disconnected from Stream Service");
        }
    }

}