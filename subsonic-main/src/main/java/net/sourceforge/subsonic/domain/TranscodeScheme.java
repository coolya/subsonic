package net.sourceforge.subsonic.domain;

/**
 * Enumeration of transcoding schemes. Transcoding is the process of
 * converting an audio stream to a lower bit rate.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2005/04/14 20:46:34 $
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
     * @return The maximum bit rate for this transcoding scheme.
     */
    public int getMaxBitRate() {
        return maxBitRate;
    }

    /**
     * Returns a human-readable string representation of this object.
     * @return A human-readable string representation of this object.
     */
    public String toString() {
        if (this == OFF) {
            return "No limit";
        }
        return "" + getMaxBitRate() + " Kbps";
    }
}
