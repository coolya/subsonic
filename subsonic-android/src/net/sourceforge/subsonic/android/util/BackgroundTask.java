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
import android.os.Handler;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import net.sourceforge.subsonic.android.R;


/**
 * @author Sindre Mehus
 */
public abstract class BackgroundTask<T> implements ProgressListener {

    private final Activity activity;
    private final Handler handler;
    private boolean cancelled;
    private ProgressDialog progressDialog;

    public BackgroundTask(Activity activity) {
        this.activity = activity;
        handler = new Handler();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
    }

    public void execute() {
        cancelled = false;
//        activity.setContentView(R.layout.progress);
        progressDialog.show();
//        Button cancelButton = (Button) activity.findViewById(R.id.progress_cancel);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cancelled = true;
//                cancel();
//            }
//        });

        new Thread() {
            @Override
            public void run() {
                try {
                    final T result = doInBackground();
                    if (cancelled) {
                        return;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
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
                            progressDialog.hide();
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
        // TODO
        TextView textView = new TextView(activity);
        textView.setText("An error occurred.\n" + error);
        activity.setContentView(textView);
    }

    @Override
    public void updateProgress(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
//                TextView textView = (TextView) activity.findViewById(R.id.progress_text);
//                textView.setText(message);
            }
        });
    }
}
