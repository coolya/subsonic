package net.sourceforge.subsonic.android.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import net.sourceforge.subsonic.android.R;

public class HelpActivity extends OptionsMenuActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        TextView helpTextView = (TextView) findViewById(R.id.help_text);

        helpTextView.setText(
                "With Subsonic you can easily download music from your home computer to your Android phone. Once you've " +
                "downloaded your favorite tracks, they are available from any music player - also when you're offline.\n" +
                "\n" +
                "To install the Subsonic server software on your computer, please visit http://subsonic.sf.net. It's available for " +
                "Windows, Mac,Linux and Unix.\n" +
                "\n" +
                "By default, the Subsonic Android app is configured to use the Subsonic demo server. Once you've set up your own " +
                "server, please go to Settings and change the configuration so that it connects to your own computer.");

        // TODO: Mention 100 song limit.

        Button okButton = (Button) findViewById(R.id.help_close);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpActivity.this.finish();
            }
        });


    }
}