package net.sourceforge.subsonic.androidapp.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Constants;

public class HelpActivity extends OptionsMenuActivity {

    private static final String TAG = HelpActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        TextView helpTextView = (TextView) findViewById(R.id.help_text);

        StringBuilder text = new StringBuilder();
        text.append("With Subsonic you can easily stream or download music from your home computer to your Android phone " +
                    "(and do lots of other cool stuff too).\n" +
                    "\n" +
                    "To install the Subsonic server software on your computer, please visit http://subsonic.org. It's available for " +
                    "Windows, Mac, Linux and Unix.\n" +
                    "\n" +
                    "By default, this program is configured to use the Subsonic demo server. Once you've set up your own " +
                    "server, please go to Settings and change the configuration so that it connects to your own computer.\n" +
                    "\n" +
                    "You can use this program freely for 30 days. After that you will have to make a donation to the Subsonic project. " +
                    "As a donor you get the following benefits:\n" +
                    "\n" +
                    " o Unlimited streaming and download to any number of Android phones.\n" +
                    " o No ads in the Subsonic web interface.\n" +
                    " o Free access to new premium features.\n" +
                    "\n" +
                    "The suggested donation amount is \u20ac20.\n\n");

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("net.sourceforge.subsonic.androidapp", 0);
            if (packageInfo != null) {
                text.append("Application version: ").append(packageInfo.versionName).append("\n");
            }
        } catch (PackageManager.NameNotFoundException x) {
            Log.w(TAG, "Failed to resolve application version name.", x);
        }
        text.append("REST API version: ").append(Constants.REST_PROTOCOL_VERSION).append("\n");

        helpTextView.setText(text);

        Button okButton = (Button) findViewById(R.id.help_close);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button donateButton = (Button) findViewById(R.id.help_donate);
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATION_URL)));
            }
        });
    }
}