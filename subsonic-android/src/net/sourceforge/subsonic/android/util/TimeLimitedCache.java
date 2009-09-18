/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android.util;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class TimeLimitedCache<T> {

    private final T value;
    private final long expires;

    public TimeLimitedCache(T value, int ttlSeconds) {
        this.value = value;
        expires = System.currentTimeMillis() + ttlSeconds * 1000L;
    }

    public T get() {
        return System.currentTimeMillis() < expires ? value : null;
    }
}
