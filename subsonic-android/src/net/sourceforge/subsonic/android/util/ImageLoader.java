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
import android.widget.ImageView;
import android.view.View;
import net.sourceforge.subsonic.android.R;
import net.sourceforge.subsonic.android.domain.MusicDirectory;
import net.sourceforge.subsonic.android.service.MusicService;
import net.sourceforge.subsonic.android.service.MusicServiceFactory;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Intended for short-lived usage, typically one activity's onCreate() - onDestroy() lifecycle.
 *
 * @author Sindre Mehus
 */
public class ImageLoader implements Runnable {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private final Task POISON = new Task(null, null, 0);
    private final BlockingQueue<Task> queue;
    private final Map<String, Drawable> cache = new ConcurrentHashMap<String, Drawable>();
    private final Thread thread;


    public ImageLoader() {
        queue = new LinkedBlockingQueue<Task>(500);
        thread = new Thread(this, "ImageLoader");
        thread.start();
    }

    public void loadImage(ImageView view, MusicDirectory.Entry entry, int size) {
        doLoadImage(view, entry, size);
    }

    public void loadImage(TextView view, MusicDirectory.Entry entry, int size) {
        doLoadImage(view, entry, size);
    }

    private void doLoadImage(View view, MusicDirectory.Entry entry, int size) {
        if (entry.getCoverArt() == null) {
            setUnknownImage(view, size);
            return;
        }

        String key = entry.getCoverArt() + size;
        Drawable drawable = cache.get(key);
        if (drawable != null) {
            setImage(view, drawable);
            return;
        }

        setUnknownImage(view, size);
        queue.offer(new Task(view, entry, size));
    }

    private void setImage(View view, Drawable drawable) {
        if (view instanceof TextView) {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        }
    }

    private void setUnknownImage(View view, int size) {
        int imageResource = 0;
        switch (size) {
            case 48:
                imageResource = R.drawable.unknown_album_48;
                break;
            case 320:
                imageResource = R.drawable.unknown_album_320;
                break;
            default:
                imageResource = R.drawable.unknown_album;
                break;
        }
        if (view instanceof TextView) {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(imageResource, 0, 0, 0);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(imageResource);
        }
    }

    public void cancel() {
        queue.clear();
        queue.offer(POISON);
        thread.interrupt();
    }

    @Override
    public void run() {

        Log.i(TAG, "Starting ImageLoader " + hashCode() % 100);
        while (!Thread.interrupted()) {
            try {
                Task task = queue.take();
                if (task == POISON) {
                    break;
                }

                task.execute();
            } catch (InterruptedException x) {
                break;
            }
        }
        Log.i(TAG, "Stopping ImageLoader " + hashCode() % 100);

    }

    private class Task {
        private final View view;
        private final MusicDirectory.Entry entry;
        private final Handler handler;
        private int size;

        public Task(View view, MusicDirectory.Entry entry, int size) {
            this.view = view;
            this.entry = entry;
            this.size = size;
            this.handler = new Handler();
        }

        public void execute() {
            MusicService musicService = MusicServiceFactory.getMusicService();
            try {
                byte[] bytes = musicService.getCoverArt(view.getContext(), entry.getCoverArt(), size, null);
                final Drawable drawable = Drawable.createFromStream(new ByteArrayInputStream(bytes), "src");
                String key = entry.getCoverArt() + size;
                cache.put(key, drawable);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(view, drawable);
                    }
                });
            } catch (Exception x) {
                Log.e(TAG, "Failed to download album art.", x);
            }
        }
    }
}
