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
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.StreamService;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.Pair;
import net.sourceforge.subsonic.android.util.SimpleServiceBinder;
import net.sourceforge.subsonic.android.util.TwoLineListAdapter;
import net.sourceforge.subsonic.android.util.Util;
import net.sourceforge.subsonic.android.util.ImageLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class StreamQueueActivity extends OptionsMenuActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = StreamQueueActivity.class.getSimpleName();
    private final StreamServiceConnection streamServiceConnection = new StreamServiceConnection();
    private ImageLoader imageLoader;
    private StreamService streamService;

    private ListView currentView;
    private ListView playlistView;
    private BroadcastReceiver broadcastReceiver;
    private TextView positionTextView;
    private TextView durationTextView;
    private TextView bufferTextView;
    private ProgressBar progressBar;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private ImageButton stopButton;
    private ImageButton pauseButton;
    private ImageButton resumeButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_queue);

        currentView = (ListView) findViewById(R.id.stream_queue_current);
        positionTextView = (TextView) findViewById(R.id.stream_queue_position);
        durationTextView = (TextView) findViewById(R.id.stream_queue_duration);
        bufferTextView = (TextView) findViewById(R.id.stream_queue_buffer);
        progressBar = (ProgressBar) findViewById(R.id.stream_queue_progress_bar);
        playlistView = (ListView) findViewById(R.id.stream_queue_list);
        previousButton = (ImageButton) findViewById(R.id.stream_queue_previous);
        nextButton = (ImageButton) findViewById(R.id.stream_queue_next);
        stopButton = (ImageButton) findViewById(R.id.stream_queue_stop);
        pauseButton = (ImageButton) findViewById(R.id.stream_queue_pause);
        resumeButton = (ImageButton) findViewById(R.id.stream_queue_resume);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamService.previous();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamService.next();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamService.stop();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamService.togglePause();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamService.togglePause();
            }
        });

        playlistView.setOnItemClickListener(this);
        bindService(new Intent(this, StreamService.class), streamServiceConnection, Context.BIND_AUTO_CREATE);
        imageLoader = new ImageLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();

        onPlaylistChanged();
        onProgressChanged();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.INTENT_ACTION_STREAM_PROGRESS.equals(intent.getAction())) {
                    onProgressChanged();
                } else if (Constants.INTENT_ACTION_STREAM_PLAYLIST.equals(intent.getAction())) {
                    onPlaylistChanged();
                } else if (Constants.INTENT_ACTION_STREAM_CURRENT.equals(intent.getAction())) {
                    onCurrentChanged();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_STREAM_PROGRESS));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_STREAM_PLAYLIST));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_STREAM_CURRENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void onPlaylistChanged() {
        if (streamService == null) {
            return;
        }

        List<MusicDirectory.Entry> queue = streamService.getPlaylist();
        playlistView.setAdapter(new SongListAdapter(queue));
        if (queue.isEmpty()) {
            currentView.setAdapter(new EmptySongListAdapter());
        }
    }

    private void onCurrentChanged() {
        if (streamService == null) {
            return;
        }
        MusicDirectory.Entry current = streamService.getCurrentSong();
        if (current != null) {
            currentView.setAdapter(new SingleSongListAdapter(current));
        }
    }

    private void onProgressChanged() {
        if (streamService == null) {
            return;
        }
        Pair<MusicDirectory.Entry, Pair<Long, Long>> current = streamService.getCurrent();
        if (current != null) {

            int millisPlayed = current.getSecond().getFirst().intValue();
            int millisTotal = current.getSecond().getSecond().intValue();

            positionTextView.setText(Util.formatDuration(millisPlayed / 1000));
            durationTextView.setText(Util.formatDuration(millisTotal / 1000));
            progressBar.setProgress(millisPlayed);
            progressBar.setMax(millisTotal);

            if (millisTotal == 0) {
                bufferTextView.setText("Buffering " + streamService.getBuffer() + "%");
            } else {
                bufferTextView.setText(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(streamServiceConnection);
        imageLoader.cancel();
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
            onPlaylistChanged();
            onCurrentChanged();
            onProgressChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            streamService = null;
            Log.i(TAG, "Disconnected from Stream Service");
        }
    }

    private class SongListAdapter extends TwoLineListAdapter<MusicDirectory.Entry> {
        private final List<MusicDirectory.Entry> queue;

        public SongListAdapter(List<MusicDirectory.Entry> queue) {
            super(StreamQueueActivity.this, queue);
            this.queue = queue;
        }

        @Override
        protected String getFirstLine(MusicDirectory.Entry song) {
            return (queue.indexOf(song) + 1) + "  " + song.getTitle();
        }

        @Override
        protected String getSecondLine(MusicDirectory.Entry song) {
            StringBuilder builder = new StringBuilder();
            builder.append(song.getAlbum()).append(" - ").append(song.getArtist());
            return builder.toString();
        }
    }

    private class EmptySongListAdapter extends TwoLineListAdapter<MusicDirectory.Entry> {

        public EmptySongListAdapter() {
            super(StreamQueueActivity.this, Collections.<MusicDirectory.Entry>emptyList());
        }

        @Override
        protected String getFirstLine(MusicDirectory.Entry song) {
            return "Playlist is empty";
        }

        @Override
        protected String getSecondLine(MusicDirectory.Entry song) {
            return null;
        }
    }

    private class SingleSongListAdapter extends ArrayAdapter<MusicDirectory.Entry> {
        public SingleSongListAdapter(MusicDirectory.Entry song) {
            super(StreamQueueActivity.this, android.R.layout.simple_list_item_1, Arrays.asList(song));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicDirectory.Entry song = getItem(position);
            TextView view;
            if (convertView != null) {
                view = (TextView) convertView;
            } else {
                view = (TextView) LayoutInflater.from(StreamQueueActivity.this).inflate(
                        android.R.layout.simple_list_item_1, parent, false);
                view.setCompoundDrawablePadding(10);
            }
            view.setText(song.getTitle());
            imageLoader.loadImage(view, song);

            return view;
        }
    }
}