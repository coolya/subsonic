package net.sourceforge.subsonic.jmeplayer.service;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Sindre Mehus
 */
public class LRUCache {

    private final int capacity;
    private final Hashtable map;

    public LRUCache(int capacity) {
        map = new Hashtable(capacity);
        this.capacity = capacity;
    }

    public synchronized Object get(Object key) {
        TimestampedValue value = (TimestampedValue) map.get(key);
        System.out.println("Cache " + (value == null ? "miss" : "hit") + " for " + key + ". Size is " + map.size());
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

        Enumeration e = map.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            TimestampedValue value = (TimestampedValue) map.get(key);
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

        public int hashCode() {
            return value.hashCode();
        }

        public void updateTimestamp() {
            timestamp = System.currentTimeMillis();
        }
    }

}
