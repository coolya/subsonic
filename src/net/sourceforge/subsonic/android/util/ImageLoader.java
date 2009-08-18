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
import net.sourceforge.subsonic.android.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.Map;

/**
 * Intended for short-lived usage, typically one activity's onCreate() - onDestroy() lifecycle.
 *
 * @author Sindre Mehus
 */
public class ImageLoader implements Runnable {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private final BlockingQueue<Task> queue;
    private final Map<String, Drawable> cache = new ConcurrentHashMap<String, Drawable>();
    private final Thread thread;

    public ImageLoader() {
        queue = new LinkedBlockingQueue<Task>(500);
        thread = new Thread(this, "ImageLoader");
        thread.start();
    }

    public void loadImage(TextView view, MusicDirectory.Entry entry) {
        if (entry.getCoverArt() == null) {
            setUnknownImage(view);
            return;
        }

        Drawable drawable = cache.get(entry.getCoverArt());
        if (drawable != null) {
            setImage(view, drawable);
            return;
        }

        setUnknownImage(view);
        queue.offer(new Task(view, entry));
    }

    private void setImage(TextView view, Drawable drawable) {
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void setUnknownImage(TextView view) {
        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unknown_album, 0, 0, 0);
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

    private class Task {
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
                cache.put(entry.getCoverArt(), drawable);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(view, drawable);
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
