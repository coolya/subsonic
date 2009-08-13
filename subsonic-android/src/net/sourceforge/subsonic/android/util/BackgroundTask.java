/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.android.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Dialog;
import android.os.Handler;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.DialogInterface;
import android.util.Log;
import net.sourceforge.subsonic.android.R;


/**
 * @author Sindre Mehus
 */
public abstract class BackgroundTask<T> implements ProgressListener {

    private final Activity activity;
    private final Handler handler;
    private final Dialog progressDialog;
    private boolean cancelled;

    public BackgroundTask(Activity activity) {
        this.activity = activity;
        handler = new Handler();
        progressDialog = new Dialog(activity);

        progressDialog.setContentView(R.layout.progress);
        progressDialog.setTitle("Please wait...");
        progressDialog.setOwnerActivity(activity);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancelled = true;
                cancel();
            }
        });
    }

    public void execute() {
        cancelled = false;
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {
                try {
                    final T result = doInBackground();
                    if (cancelled) {
                        progressDialog.dismiss();
                        return;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            done(result);
                        }
                    });
                } catch (final Throwable t) {
                    if (cancelled) {
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            error(t);
                        }
                    });
                }
            }
        }.start();
    }

    protected abstract T doInBackground() throws Throwable;

    protected abstract void done(T result);

    protected void cancel() {
        activity.finish();
    }

    protected void error(Throwable error) {
        new ErrorDialog(activity, getErrorMessage(error)).show();
    }

    protected String getErrorMessage(Throwable error) {
        String message = error.getMessage();
        if (message != null) {
            return message;
        }
        return error.getClass().getSimpleName();
    }

    @Override
    public void updateProgress(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TextView text = (TextView) progressDialog.findViewById(R.id.progress_message);
                text.setText(message);
            }
        });
    }
}
