/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class HorizontalSlider extends ProgressBar {

    private boolean slidingEnabled;
    private OnSliderChangeListener listener;
    private static final int PADDING = 2;
    private boolean sliding;
    private int sliderPosition;
    private int startPosition;

    public interface OnSliderChangeListener {
        void onSliderChanged(View view, int position);
    }

    public HorizontalSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HorizontalSlider(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public HorizontalSlider(Context context) {
        super(context);
    }

    public void setSlidingEnabled(boolean slidingEnabled) {
        if (this.slidingEnabled != slidingEnabled) {
            this.slidingEnabled = slidingEnabled;
            invalidate();
        }
    }

    public boolean isSlidingEnabled() {
        return slidingEnabled;
    }

    public void setOnSliderChangeListener(OnSliderChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int max = getMax();
        if (!slidingEnabled || max == 0) {
            return;
        }

        // TODO: Optimize

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int w = getWidth() - paddingLeft - paddingRight;
        int h = getHeight() - paddingTop - paddingBottom;

        int position = sliding ? sliderPosition : getProgress();

        float radius = 10.0F;
        float cx = paddingLeft + w * ((float) position / max);
        cx = Math.max(paddingLeft + radius, cx);
        cx = Math.min(paddingLeft + w - radius, cx);
        float cy = paddingTop + h / 2.0F;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE); // TODO: Use icon, or off-white.
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slidingEnabled) {
            return false;
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {

            if (action == MotionEvent.ACTION_DOWN) {
                sliding = true;
                startPosition = getProgress();
            }

            float x = event.getX() - PADDING;
            float width = getWidth() - 2 * PADDING;
            sliderPosition = Math.round((float) getMax() * (x / width));
            sliderPosition = Math.max(sliderPosition, 0);

            setProgress(Math.min(startPosition, sliderPosition));
            setSecondaryProgress(Math.max(startPosition, sliderPosition));

        } else if (action == MotionEvent.ACTION_UP) {
            sliding = false;
            setProgress(sliderPosition);
            setSecondaryProgress(0);
            if (listener != null) {
                listener.onSliderChanged(this, sliderPosition);
            }
        }

        return true;
    }
}