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
package net.sourceforge.subsonic.android.service;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * @author Sindre Mehus
 */
public class LRUCache {

    private static final String TAG = LRUCache.class.getSimpleName();

    private final int capacity;
    private final Map<Object, TimestampedValue> map;

    public LRUCache(int capacity) {
        map = new HashMap<Object, TimestampedValue>(capacity);
        this.capacity = capacity;
    }

    public synchronized Object get(Object key) {
        TimestampedValue value = map.get(key);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Cache " + (value == null ? "miss" : "hit") + " for " + key + ". Size is " + map.size());
        }

        if (value != null) {
            value.updateTimestamp();
            return value.getValue();
        }
        return null;
    }

    public synchronized void put(Object key, Object value) {
        if (map.size() >= capacity) {
            removeOldest();
        }
        map.put(key, new TimestampedValue(value));
    }

    private void removeOldest() {
        Object oldestKey = null;
        long oldestTimestamp = Long.MAX_VALUE;

        for (Map.Entry<Object, TimestampedValue> entry : map.entrySet()) {
            Object key = entry.getKey();
            TimestampedValue value = entry.getValue();
            if (value.getTimestamp() < oldestTimestamp) {
                oldestTimestamp = value.getTimestamp();
                oldestKey = key;
            }
        }

        if (oldestKey != null) {
            map.remove(oldestKey);
        }
    }

    private final class TimestampedValue {

        private final Object value;
        private long timestamp;

        public TimestampedValue(Object value) {
            this.value = value;
            updateTimestamp();
        }

        public Object getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TimestampedValue that = (TimestampedValue) o;
            return value.equals(that.value);

        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        public void updateTimestamp() {
            timestamp = System.currentTimeMillis();
        }
    }

}