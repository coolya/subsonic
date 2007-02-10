package net.sourceforge.subsonic.domain;

/**
 * Enumeration of transcoding schemes. Transcoding is the process of
 * converting an audio stream to a lower bit rate.
 *
 * @author Sindre Mehus
 */
public enum TranscodeScheme {

    OFF(0),
    MAX_32(32),
    MAX_40(40),
    MAX_48(48),
    MAX_56(56),
    MAX_64(64),
    MAX_80(80),
    MAX_96(96),
    MAX_112(112),
    MAX_128(128),
    MAX_160(160),
    MAX_192(192),
    MAX_224(224),
    MAX_256(256),
    MAX_320(320);

    private int maxBitRate;

    TranscodeScheme(int maxBitRate) {
        this.maxBitRate = maxBitRate;
    }

    /**
     * Returns the maximum bit rate for this transcoding scheme.
     *
     * @return The maximum bit rate for this transcoding scheme.
     */
    public int getMaxBitRate() {
        return maxBitRate;
    }

    /**
     * Returns the strictest transcode scheme (i.e., the scheme with the lowest max bitrate).
     *
     * @param other The other transcode scheme. May be <code>null</code>, in which case 'this' is returned.
     * @return The strictest scheme.
     */
    public TranscodeScheme strictest(TranscodeScheme other) {
        if (other == null || other == TranscodeScheme.OFF) {
            return this;
        }

        if (this == TranscodeScheme.OFF) {
            return other;
        }

        return maxBitRate < other.maxBitRate ? this : other;
    }

    /**
     * Returns a human-readable string representation of this object.
     *
     * @return A human-readable string representation of this object.
     */
    public String toString() {
        if (this == OFF) {
            return "No limit";
        }
        return "" + getMaxBitRate() + " Kbps";
    }
}
