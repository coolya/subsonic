package net.sourceforge.subsonic.androidapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.util.ErrorDialog;

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