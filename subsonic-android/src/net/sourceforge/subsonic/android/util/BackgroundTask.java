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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;


/**
 * @author Sindre Mehus
 */
public abstract class BackgroundTask<T> implements ProgressListener {

    private final Activity activity;
    private final Handler handler;
    private final AlertDialog progressDialog;
    private boolean cancelled;

    public BackgroundTask(Activity activity) {
        this.activity = activity;
        handler = new Handler();

        progressDialog = createProgressDialog();
    }

    private AlertDialog createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Please wait...");
        builder.setMessage("Loading.");
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cancelled = true;
                cancel();
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelled = true;
                cancel();
            }
        });

        return builder.create();
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
        new ErrorDialog(activity, getErrorMessage(error), true);
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
                progressDialog.setMessage(message);
            }
        });
    }
}
