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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * @author Sindre Mehus
 */
public class LRUCache<K,V>{

    private static final String TAG = LRUCache.class.getSimpleName();

    private final int capacity;
    private final Map<K, TimestampedValue> map;

    public LRUCache(int capacity) {
        map = new HashMap<K, TimestampedValue>(capacity);
        this.capacity = capacity;
    }

    public synchronized V get(K key) {
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

    public synchronized void put(K key, V value) {
        if (map.size() >= capacity) {
            removeOldest();
        }
        map.put(key, new TimestampedValue(value));
    }

    public void clear() {
        map.clear();
    }

    private void removeOldest() {
        K oldestKey = null;
        long oldestTimestamp = Long.MAX_VALUE;

        for (Map.Entry<K, TimestampedValue> entry : map.entrySet()) {
            K key = entry.getKey();
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

        private final SoftReference<V> value;
        private long timestamp;

        public TimestampedValue(V value) {
            this.value = new SoftReference<V>(value);
            updateTimestamp();
        }

        public V getValue() {
            return value.get();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void updateTimestamp() {
            timestamp = System.currentTimeMillis();
        }
    }

}