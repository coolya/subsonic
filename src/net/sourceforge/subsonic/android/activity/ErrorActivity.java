package net.sourceforge.subsonic.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.R;
import net.sourceforge.subsonic.android.util.Constants;
import net.sourceforge.subsonic.android.util.ErrorDialog;

public class ErrorActivity extends Activity {

    private static final String TAG = ErrorActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String errorMessage = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME_ERROR);

        Log.i(TAG, "ErrorActivity got message: " + errorMessage + ", " + System.identityHashCode(this));
        new ErrorDialog(this, errorMessage, true);
    }
}