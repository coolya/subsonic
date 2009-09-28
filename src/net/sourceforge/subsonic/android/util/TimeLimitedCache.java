/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android.util;

import java.util.concurrent.TimeUnit;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class TimeLimitedCache<T> {

    private T value;
    private final long ttlMillis;
    private long expires;

    public TimeLimitedCache(int ttl, TimeUnit timeUnit) {
        this.ttlMillis = TimeUnit.MILLISECONDS.convert(ttl, timeUnit);
    }

    public T get() {
        return System.currentTimeMillis() < expires ? value : null;
    }

    public void set(T value) {
        this.value = value;
        expires = System.currentTimeMillis() + ttlMillis;
    }

    public void clear() {
        expires = 0L;
        value = null;
    }
}
