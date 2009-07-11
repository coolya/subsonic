package net.sourceforge.subsonic.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;
import net.sourceforge.subsonic.android.util.BackgroundTask;

public class SelectAlbumActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAlbumActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackgroundTask<MusicDirectory> task = new BackgroundTask<MusicDirectory>(this) {
            @Override
            protected MusicDirectory doInBackground() throws Throwable {
                MusicService musicService = MusicServiceFactory.getMusicService();
                String path = getIntent().getStringExtra(Constants.NAME_PATH);
                return musicService.getMusicDirectory(path, this);
            }

            @Override
            protected void done(MusicDirectory result) {
                ListView listView = new ListView(SelectAlbumActivity.this);
                listView.setAdapter(new ArrayAdapter<MusicDirectory.Entry>(SelectAlbumActivity.this, android.R.layout.simple_list_item_1, result.getChildren()));
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(SelectAlbumActivity.this);
                setContentView(listView);
            }

            @Override
            protected void cancel() {
                MusicServiceFactory.getMusicService().cancel(this);
                finish();
            }
        };
        task.execute();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
            MusicDirectory.Entry entry = (MusicDirectory.Entry) parent.getItemAtPosition(position);
            Log.d(TAG, entry + " clicked.");
            if (entry.isDirectory()) {
                Intent intent = new Intent(this, SelectAlbumActivity.class);
                intent.putExtra(Constants.NAME_PATH, entry.getPath());
                startActivity(intent);
            }
        }
    }

}