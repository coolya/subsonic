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
package net.sourceforge.subsonic.androidapp.util;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;

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
    private final int imageSizeDefault;
    private final int imageSizeLarge;
    private final Context context;


    public ImageLoader(Context context) {
        this.context = context;
        queue = new LinkedBlockingQueue<Task>(500);
        thread = new Thread(this, "ImageLoader");
        thread.start();

        // Determine the density-dependent image sizes.
        imageSizeDefault = context.getResources().getDrawable(R.drawable.unknown_album).getIntrinsicHeight();
        imageSizeLarge = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void loadImage(View view, MusicDirectory.Entry entry, boolean large) {
        loadImage(view, entry.getCoverArt(), large);
    }

    public void loadImage(View view, String coverArtId, boolean large) {
        if (coverArtId == null) {
            setUnknownImage(view, large);
            return;
        }

        int size = large ? imageSizeLarge : imageSizeDefault;
        Drawable drawable = cache.get(getKey(coverArtId, size));
        if (drawable != null) {
            setImage(view, drawable);
            return;
        }

        setUnknownImage(view, large);
        queue.offer(new Task(view, coverArtId, size));
    }

    private String getKey(String coverArtId, int size) {
        return coverArtId + size;
    }

    private void setImage(View view, Drawable drawable) {
        if (view instanceof TextView) {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        }
    }

    private void setUnknownImage(View view, boolean large) {
        int imageResource = large ? R.drawable.unknown_album_large : R.drawable.unknown_album;

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
        private final String coverArtId;
        private final Handler handler;
        private final int size;

        public Task(View view, String coverArtId, int size) {
            this.view = view;
            this.coverArtId = coverArtId;
            this.size = size;
            handler = new Handler();
        }

        public void execute() {
            MusicService musicService = MusicServiceFactory.getMusicService(view.getContext());
            try {
                Bitmap bitmap = musicService.getCoverArt(view.getContext(), coverArtId, size, null);
                final Drawable drawable = Util.createDrawableFromBitmap(context, bitmap);
                cache.put(getKey(coverArtId, size), drawable);

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
