/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.androidapp.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import android.util.Log;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public abstract class CancellableTask {

    private static final String TAG = CancellableTask.class.getSimpleName();

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<Thread> thread = new AtomicReference<Thread>();

    public void cancel() {
        cancelled.set(true);
        Thread t = thread.get();
        if (t != null && t.isAlive()) {
            t.interrupt();
        }
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public abstract void execute();

    public void start() {
        thread.set(new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "Starting thread for " + CancellableTask.this);
                try {
                    execute();
                } finally {
                    Log.d(TAG, "Stopping thread for " + CancellableTask.this);
                }
            }
        });
        thread.get().start();
    }
}
