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

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import net.sourceforge.subsonic.android.domain.MusicDirectory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sindre Mehus
 */
public class ImageLoader implements Runnable {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private final LinkedBlockingQueue<Task> queue;
    private final Thread thread;

    public ImageLoader() {
        queue = new LinkedBlockingQueue<Task>(500);
        thread = new Thread(this, "ImageLoader");
        thread.start();
    }

    public void loadImage(TextView view, MusicDirectory.Entry entry) {
        queue.offer(new Task(view, entry));
    }

    public void cancel() {
        queue.clear();
        thread.interrupt();
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            try {
                Task task = queue.take();
                task.execute();
            } catch (InterruptedException x) {
                return;
            }
        }
    }

    private static class Task {
        private final TextView view;
        private final MusicDirectory.Entry entry;
        private final Handler handler;

        public Task(TextView view, MusicDirectory.Entry entry) {
            this.view = view;
            this.entry = entry;
            this.handler = new Handler();
        }

        public void execute() {
            String url = Util.getRestUrl(view.getContext(), "getCoverArt") + "&id=" + entry.getCoverArt() + "&size=48";
            InputStream in = null;
            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(Constants.SOCKET_TIMEOUT);
                connection.setReadTimeout(Constants.SOCKET_TIMEOUT);
                connection.connect();
                in = connection.getInputStream();
                final Drawable drawable = Drawable.createFromStream(in, "src");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    }
                });
            } catch (Exception x) {
                Log.e(TAG, "Failed to download album art.", x);
            } finally {
                Util.close(in);
            }
        }
    }
}
