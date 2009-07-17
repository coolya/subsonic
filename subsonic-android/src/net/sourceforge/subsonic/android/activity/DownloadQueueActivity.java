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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import net.sourceforge.subsonic.android.service.DownloadService;
import net.sourceforge.subsonic.android.util.Constants;

public class DownloadQueueActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = DownloadQueueActivity.class.getSimpleName();
    private final DownloadServiceConnection downloadServiceConnection = new DownloadServiceConnection();
    private DownloadService downloadService;
    private ListView entryList;
    private Button downloadButton;
    private Button selectAllButton;
    private Button selectNoneButton;
    private BroadcastReceiver broadcastReceiver;
    private TextView textView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        textView.setText("Halloen. ");
        setContentView(textView);
//        setContentView(R.layout.select_album);
//        setTitle(getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_NAME));

//        downloadButton = (Button) findViewById(R.id.select_album_download);
//        selectAllButton = (Button) findViewById(R.id.select_album_selectall);
//        selectNoneButton = (Button) findViewById(R.id.select_album_selectnone);
//        entryList = (ListView) findViewById(R.id.select_album_entries);

//        entryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);// TODO:Specify in XML.
//        entryList.setOnItemClickListener(this);

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
                downloadQueueChanged();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DOWNLOAD_QUEUE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void downloadQueueChanged() {
        Log.i(TAG, "GOT BROADCAST. " + System.identityHashCode(this));
        textView.setText(textView.getText() + "BC ");
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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            downloadService = null;
            Log.i(TAG, "Disconnected from Download Service");
        }
    }
}