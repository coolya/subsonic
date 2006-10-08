package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.util.*;

import java.io.*;

/**
 * Status for a single transfer (stream, download or upload).
 *
 * @author Sindre Mehus
 * @version $Revision: 1.5 $ $Date: 2005/06/13 17:20:43 $
 */
public class TransferStatus {

    private static final int HISTORY_LENGTH = 200;
    private static final long SAMPLE_INTERVAL_MILLIS = 5000;

    private Player player;
    private File file;
    private long bytesTransfered;
    private long bytesTotal;
    private SampleHistory history = new SampleHistory();
    private boolean isTerminated;


    /**
    * Return the number of bytes transfered.
    * @return The number of bytes transfered.
    */
    public synchronized long getBytesTransfered() {
        return bytesTransfered;
    }

    /**
     * Adds the given byte count to the total number of bytes transfered.
     * @param byteCount The byte count.
     */
    public synchronized void addBytesTransfered(long byteCount) {
        setBytesTransfered(bytesTransfered + byteCount);
    }

    /**
    * Sets the number of bytes transfered.
    * @param bytesTransfered The number of bytes transfered.
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
     * Returns the total number of bytes, or 0 if unknown.
     * @return The total number of bytes, or 0 if unknown.
     */
    public long getBytesTotal() {
        return bytesTotal;
    }

    /**
     * Sets the total number of bytes, or 0 if unknown.
     * @param bytesTotal The total number of bytes, or 0 if unknown.
     */
    public void setBytesTotal(long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    /**
     * Returns the file that is currently being transfered.
     * @return The file that is currently being transfered.
     */
    public synchronized File getFile() {
        return file;
    }

    /**
     * Sets the file that is currently being transfered.
     * @param file The file that is currently being transfered.
     */
    public synchronized void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the remote player for the stream.
     * @return The remote player for the stream.
     */
    public synchronized Player getPlayer() {
        return player;
    }

    /**
     * Sets the remote player for the stream.
     * @param player The remote player for the stream.
     */
    public synchronized void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Returns a history of samples for the stream
     * @return A (copy of) the history list of samples.
     */
    public synchronized SampleHistory getHistory() {
        return new SampleHistory(history);
    }

    /**
     * Sets the history of samples. A defensive copy is taken.
     * @param history The history list of samples.
     */
    public synchronized void setHistory(SampleHistory history) {
        this.history = new SampleHistory(history);
    }

    /**
    * Returns the history length in milliseconds.
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
     * @return Whether this stream has been terminated.
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * A sample containing a timestamp and the number of bytes transfered up to that point in time.
     */
    public static class Sample {
        private long bytesTransfered;
        private long timestamp;

        /**
         * Creates a new sample.
         * @param bytesTransfered The total number of bytes transfered.
         * @param timestamp A point in time, in milliseconds.
         */
        public Sample(long bytesTransfered, long timestamp) {
            this.bytesTransfered = bytesTransfered;
            this.timestamp = timestamp;
        }

        /**
         * Returns the number of bytes transfered.
         * @return The number of bytes transfered.
         */
        public long getBytesTransfered() {
            return bytesTransfered;
        }

        /**
         * Returns the timestamp of the sample.
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
