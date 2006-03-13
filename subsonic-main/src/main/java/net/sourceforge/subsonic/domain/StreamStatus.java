package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.util.*;

import java.util.*;

/**
 * Status for a single stream.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.5 $ $Date: 2005/06/13 17:20:43 $
 */
public class StreamStatus {

    private static final int HISTORY_LENGTH = 200;
    private static final long SAMPLE_INTERVAL_MILLIS = 5000;

    private Player player;
    private MusicFile file;
    private long bytesStreamed;
    private LinkedList<Sample> history = new BoundedList<Sample>(HISTORY_LENGTH);
    private boolean isTerminated;

    /**
     * Return the number of bytes streamed.
     * @return The number of bytes streamed.
     */
    public synchronized long getBytesStreamed() {
        return bytesStreamed;
    }

    /**
     * Sets the number of bytes streamed.
     * @param bytesStreamed The number of bytes streamed.
     */
    public synchronized void setBytesStreamed(long bytesStreamed) {
        this.bytesStreamed = bytesStreamed;
        long now = System.currentTimeMillis();

        if (history.isEmpty()) {
            history.add(new Sample(bytesStreamed, now));
        } else {
            Sample lastSample = history.getLast();
            if (now - lastSample.getTimestamp() > SAMPLE_INTERVAL_MILLIS) {
                history.add(new Sample(bytesStreamed, now));
            }
        }
    }

    /**
     * Returns the music file that is currently being streamed.
     * @return The music file that is currently being streamed.
     */
    public synchronized MusicFile getFile() {
        return file;
    }

    /**
     * Sets the music file that is currently being streamed.
     * @param file The music file that is currently being streamed.
     */
    public synchronized void setFile(MusicFile file) {
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
    public synchronized List<Sample> getHistory() {
        return new ArrayList<Sample>(history);
    }

    /**
     * Returns the history length in milliseconds.
     * @return The history length in milliseconds.
     */
    public long getHistoryLengthMillis() {
        return SAMPLE_INTERVAL_MILLIS * (HISTORY_LENGTH - 1);
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
     * A sample containing a timestamp and the number of bytes streamed up to that point in time.
     */
    public static class Sample {
        private long bytesStreamed;
        private long timestamp;

        /**
         * Creates a new sample.
         * @param bytesStreamed The total number of bytes streamed.
         * @param timestamp A point in time, in milliseconds.
         */
        public Sample(long bytesStreamed, long timestamp) {
            this.bytesStreamed = bytesStreamed;
            this.timestamp = timestamp;
        }

        /**
         * Returns the number of bytes streamed.
         * @return The number of bytes streamed.
         */
        public long getBytesStreamed() {
            return bytesStreamed;
        }

        /**
         * Returns the timestamp of the sample.
         * @return The timestamp in milliseconds.
         */
        public long getTimestamp() {
            return timestamp;
        }
    }

}