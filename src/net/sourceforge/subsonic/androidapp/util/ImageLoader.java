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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.domain.MusicDirectory;
import net.sourceforge.subsonic.androidapp.service.MusicService;
import net.sourceforge.subsonic.androidapp.service.MusicServiceFactory;

/**
 * Asynchronous loading of images, with caching.
 * <p/>
 * There should normally be only one instance of this class.
 *
 * @author Sindre Mehus
 */
public class ImageLoader implements Runnable {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private static final int CONCURRENCY = 5;

    private final LRUCache<String, Drawable> cache = new LRUCache<String, Drawable>(100);
    private final BlockingQueue<Task> queue;
    private final int imageSizeDefault;
    private final int imageSizeLarge;

    public ImageLoader(Context context) {
        queue = new LinkedBlockingQueue<Task>(500);

        // Determine the density-dependent image sizes.
        imageSizeDefault = context.getResources().getDrawable(R.drawable.unknown_album).getIntrinsicHeight();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        imageSizeLarge = (int) Math.round(Math.min(metrics.widthPixels, metrics.heightPixels) * 0.65);

        for (int i = 0; i < CONCURRENCY; i++) {
            new Thread(this, "ImageLoader").start();
        }
    }

    public void loadImage(View view, MusicDirectory.Entry entry, boolean large) {
        if (entry == null || entry.getCoverArt() == null) {
            setUnknownImage(view, large);
            return;
        }

        int size = large ? imageSizeLarge : imageSizeDefault;
        Drawable drawable = cache.get(getKey(entry.getCoverArt(), size));
        if (drawable != null) {
            setImage(view, drawable);
            return;
        }

        setUnknownImage(view, large);
        queue.offer(new Task(view, entry, size, large, large));
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

    public void clear() {
        queue.clear();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Task task = queue.take();
                task.execute();
            } catch (Throwable x) {
                Log.e(TAG, "Unexpected exception in ImageLoader.", x);
            }
        }
    }

    private Bitmap createReflection(Bitmap originalImage) {

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // The gap we want between the reflection and the original image
        final int reflectionGap = 4;

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);


        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);

        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);

        // Draw in the gap
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);

        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);

        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    private class Task {
        private final View view;
        private final MusicDirectory.Entry entry;
        private final Handler handler;
        private final int size;
        private final boolean reflection;
        private final boolean saveToFile;

        public Task(View view, MusicDirectory.Entry entry, int size, boolean reflection, boolean saveToFile) {
            this.view = view;
            this.entry = entry;
            this.size = size;
            this.reflection = reflection;
            this.saveToFile = saveToFile;
            handler = new Handler();
        }

        public void execute() {
            try {
                MusicService musicService = MusicServiceFactory.getMusicService(view.getContext());
                Bitmap bitmap = musicService.getCoverArt(view.getContext(), entry, size, saveToFile, null);

                if (reflection) {
                    bitmap = createReflection(bitmap);
                }

                final Drawable drawable = Util.createDrawableFromBitmap(view.getContext(), bitmap);
                cache.put(getKey(entry.getCoverArt(), size), drawable);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(view, drawable);
                    }
                });
            } catch (Throwable x) {
                Log.e(TAG, "Failed to download album art.", x);
            }
        }
    }
}
