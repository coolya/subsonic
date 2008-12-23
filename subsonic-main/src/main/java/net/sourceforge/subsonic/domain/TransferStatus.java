package net.sourceforge.subsonic.domain;

import java.io.File;

import net.sourceforge.subsonic.util.BoundedList;

/**
 * Status for a single transfer (stream, download or upload).
 *
 * @author Sindre Mehus
 */
public class TransferStatus {

    private static final int HISTORY_LENGTH = 200;
    private static final long SAMPLE_INTERVAL_MILLIS = 5000;

    private Player player;
    private File file;
    private long bytesTransfered;
    private long bytesSkipped;
    private long bytesTotal;
    private SampleHistory history = new SampleHistory();
    private boolean isTerminated;
    private boolean active = true;


    /**
     * Return the number of bytes transferred.
     *
     * @return The number of bytes transferred.
     */
    public synchronized long getBytesTransfered() {
        return bytesTransfered;
    }

    /**
     * Adds the given byte count to the total number of bytes transferred.
     *
     * @param byteCount The byte count.
     */
    public synchronized void addBytesTransfered(long byteCount) {
        setBytesTransfered(bytesTransfered + byteCount);
    }

    /**
     * Sets the number of bytes transferred.
     *
     * @param bytesTransfered The number of bytes transferred.
     */
    public synchronized void setBytesTransfered(long bytesTransfered) {
        this.bytesTransfered = bytesTransfered;
        long now = System.currentTimeMillis();

        if (history.isEmpty()) {
            history.add(new TransferStatus.Sample(bytesTransfered, now));
        } else {
            TransferStatus.Sample lastSample = history.getLast();
            if (now - lastSample.getTimestamp() > TransferStatus.SAMPLE_INTERVAL_MILLIS) {
                history.add(new TransferStatus.Sample(bytesTransfered, now));
            }
        }
    }

    /**
     * Returns the number of milliseconds since the transfer status was last updated.
     *
     * @return Number of milliseconds, or <code>0</code> if never updated.
     */
    public synchronized long getMillisSinceLastUpdate() {
        if (history.isEmpty()) {
            return 0L;
        }
        return System.currentTimeMillis() - history.getLast().timestamp;
    }

    /**
     * Returns the total number of bytes, or 0 if unknown.
     *
     * @return The total number of bytes, or 0 if unknown.
     */
    public long getBytesTotal() {
        return bytesTotal;
    }

    /**
     * Sets the total number of bytes, or 0 if unknown.
     *
     * @param bytesTotal The total number of bytes, or 0 if unknown.
     */
    public void setBytesTotal(long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    /**
     * Returns the number of bytes that has been skipped (for instance when
     * resuming downloads).
     *
     * @return The number of skipped bytes.
     */
    public synchronized long getBytesSkipped() {
        return bytesSkipped;
    }

    /**
     * Sets the number of bytes that has been skipped (for instance when
     * resuming downloads).
     *
     * @param bytesSkipped The number of skipped bytes.
     */
    public synchronized void setBytesSkipped(long bytesSkipped) {
        this.bytesSkipped = bytesSkipped;
    }


    /**
     * Adds the given byte count to the total number of bytes skipped.
     *
     * @param byteCount The byte count.
     */
    public synchronized void addBytesSkipped(long byteCount) {
        bytesSkipped += byteCount;
    }

    /**
     * Returns the file that is currently being transferred.
     *
     * @return The file that is currently being transferred.
     */
    public synchronized File getFile() {
        return file;
    }

    /**
     * Sets the file that is currently being transferred.
     *
     * @param file The file that is currently being transferred.
     */
    public synchronized void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the remote player for the stream.
     *
     * @return The remote player for the stream.
     */
    public synchronized Player getPlayer() {
        return player;
    }

    /**
     * Sets the remote player for the stream.
     *
     * @param player The remote player for the stream.
     */
    public synchronized void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Returns a history of samples for the stream
     *
     * @return A (copy of) the history list of samples.
     */
    public synchronized SampleHistory getHistory() {
        return new SampleHistory(history);
    }

    /**
     * Sets the history of samples. A defensive copy is taken.
     *
     * @param history The history list of samples.
     */
    public synchronized void setHistory(SampleHistory history) {
        this.history = new SampleHistory(history);
    }

    /**
     * Returns the history length in milliseconds.
     *
     * @return The history length in milliseconds.
     */
    public long getHistoryLengthMillis() {
        return TransferStatus.SAMPLE_INTERVAL_MILLIS * (TransferStatus.HISTORY_LENGTH - 1);
    }

    /**
     * Indicate that the stream should be terminated.
     */
    public void terminate() {
        isTerminated = true;
    }

    /**
     * Returns whether this stream has been terminated.
     *
     * @return Whether this stream has been terminated.
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * Returns whether this transfer is active, i.e., if the connection is still established.
     *
     * @return Whether this transfer is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether this transfer is active, i.e., if the connection is still established.
     *
     * @param active Whether this transfer is active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * A sample containing a timestamp and the number of bytes transferred up to that point in time.
     */
    public static class Sample {
        private long bytesTransfered;
        private long timestamp;

        /**
         * Creates a new sample.
         *
         * @param bytesTransfered The total number of bytes transferred.
         * @param timestamp       A point in time, in milliseconds.
         */
        public Sample(long bytesTransfered, long timestamp) {
            this.bytesTransfered = bytesTransfered;
            this.timestamp = timestamp;
        }

        /**
         * Returns the number of bytes transferred.
         *
         * @return The number of bytes transferred.
         */
        public long getBytesTransfered() {
            return bytesTransfered;
        }

        /**
         * Returns the timestamp of the sample.
         *
         * @return The timestamp in milliseconds.
         */
        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Contains recent history of samples.
     */
    public static class SampleHistory extends BoundedList<Sample> {

        public SampleHistory() {
            super(HISTORY_LENGTH);
        }

        public SampleHistory(SampleHistory other) {
            super(HISTORY_LENGTH);
            addAll(other);
        }
    }
}
