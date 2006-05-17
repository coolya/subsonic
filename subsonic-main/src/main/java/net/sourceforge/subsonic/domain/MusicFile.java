package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;

import java.io.*;
import java.util.*;

/**
 * Represents a file or directory containing music. Music files can be put in a {@link Playlist},
 * and may be streamed to remote players.  All music files are located in a configurable root music folder.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.32 $ $Date: 2006/01/23 22:01:08 $
 */
public class MusicFile {

    private static final Logger LOG = Logger.getLogger(MusicFile.class);

    private File file;
    private MetaData metaData;
    private int bitRate = -1;

    /**
     * Creates a new instance for the given file.
     * @param file A file on the local file system.
     * @exception SecurityException If access is denied to the given file.
     */
    public MusicFile(File file) {
        if (!ServiceFactory.getSecurityService().isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }
        this.file = file;
    }

    /**
     * Creates a new instance for the given path name.
     * @param pathName A path name for a file on the local file system.
     * @exception SecurityException If access is denied to the given file.
     */
    public MusicFile(String pathName) {
        this(new File(pathName));
    }

    /**
     * Empty constructor.  Used for testing purposes only.
     */
    protected MusicFile() {}

    /**
     * Returns a possibly transcoded input stream to the music file.
     * @param transcode Whether transcoding should be performed if necessary.
     * @param maxBitRate If transcoding is enabled, and the bitrate of the original file
     * is higher than this value, a transcoded input stream is returned. The bitrate of the
     * returned input stream will be at most <code>maxBitRate</code>.
     * @return An input stream to the possibly transcoded music file.
     * @exception IOException If an I/O error occurs.
     */
    public InputStream getInputStream(boolean transcode, int maxBitRate) throws IOException {
        if (transcode && getBitRate() > maxBitRate) {
            try {
                return TranscodedInputStream.create(file, maxBitRate);
            } catch (Exception x) {
                LOG.warn("Failed to create transcoded stream. Using ordinary.");
            }
        }

        return new FileInputStream(file);
    }

    /**
     * Returns the underlying {@link File};
     * @return The file wrapped by this MusicFile.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns whether this music file is a normal file (and not a directory).
     * @return Whether this music file is a normal file (and not a directory).
     */
    public boolean isFile() {
        return file.isFile();
    }

    /**
     * Returns whether this music file is a directory.
     * @return Whether this music file is a directory.
     */
    public boolean isDirectory() {
        return file.isDirectory();
    }

    /**
     * Returns whether this music file is an album, i.e., whether it is a directory containing
     * songs.
     * @return Whether this music file is an album
     * @throws IOException If an I/O error occurs.
     */
    public boolean isAlbum() throws IOException {
        if (isFile()) {
            return false;
        }

        MusicFile[] children = getChildren(false);
        return children.length > 0;
    }

    /**
    * Returns whether this music file is one of the root music folders.
    * @return Whether this music file is one of the root music folders.
    */
    public boolean isRoot() {
        SettingsService settings = ServiceFactory.getSettingsService();
        MusicFolder[] folders = settings.getAllMusicFolders();
        for (MusicFolder folder : folders) {
            if (file.equals(folder.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the time this music file was last modified.
     * @return The time since this music file was last modified, in milliseconds since the epoch.
     */
    public long lastModified() {
        return file.lastModified();
    }

    /**
     * Returns the length of the music file.
     * The return value is unspecified if this music file is a directory.
     *
     * @return  The length, in bytes, of the music file, or
     *          or <code>0L</code> if the file does not exist
     */
    public long length() {
        return file.length();
    }

    /**
    * Returns whether this music file exists.
    * @return Whether this music file exists.
    */
    public boolean exists() {
        return file.exists();
    }

    /**
     * Returns the path of this music file as a formatted string intended for display to the user,
     * e.g., "Pink Floyd - Dark Side Of The Moon - Money".
     * @return The path of this music file as a formatted string.
     * @exception IOException If an I/O error occurs.
     */
    public String getFormattedPath() throws IOException {
        if (isRoot()) {
            return "";
        }
        String s = getNameWithoutSuffix();
        MusicFile mf = getParent();

        while (mf != null && !mf.isRoot()) {
            s = mf.getName() + " - " + s;
            mf = mf.getParent();
        }
        return s;
    }

    /**
     * Returns the name of the music file. This is just the last name in the pathname's name
     * sequence.
     * @return  The name of the music file.
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Same as {@link #getName}, but without file suffix (unless this music file
     * represents a directory).
     * @return The name of the file without the suffix
     */
    public String getNameWithoutSuffix() {
        String name = getName();
        if (isDirectory()) {
            return name;
        }
        int i = name.lastIndexOf('.');
        return i == -1 ? name : name.substring(0, i);
    }

    /**
     * Returns the full pathname as a string.
     * @return The full pathname as a string.
     */
    public String getPath() {
        return file.getPath();
    }

    /**
     * Returns meta data for this music file.
     * @return Meta data (artist, album, title etc) for this music file.
     */
    public synchronized MetaData getMetaData() {
        if (metaData == null) {
            MetaDataParser parser = MetaDataParser.Factory.getInstance().getParser(this);
            metaData = (parser == null) ? null : parser.getMetaData(this);
        }
        return metaData;
    }

    /**
     * Returns the title of the music file, by attempting to parse relevant meta-data embedded in the file,
     * for instance ID3 tags in MP3 files. <p/>
     * If this music file is a directory, or if no tags are found, this method is equivalent to {@link #getNameWithoutSuffix}.
     * @return The song title of this music file.
     */
    public String getTitle() {
        return getMetaData() == null ? getNameWithoutSuffix() : getMetaData().getTitle();
    }

    /**
     * Returns the parent music file.
     * @return The parent music file, or <code>null</code> if no parent exists.
     * @exception IOException If an I/O error occurs.
     */
    public MusicFile getParent() throws IOException {
        File parent = file.getParentFile();
        return parent == null ? null : new MusicFile(parent);
    }

    /**
     * Returns all music files that are children of this music file.
     * Equivalent to <code>getChildren(recurse, false)</code>.
     * @param recurse Whether to recurse or not.
     * @return All children music files.
     * @exception IOException If an I/O error occurs.
     */
    public MusicFile[] getChildren(boolean recurse) throws IOException {
        return getChildren(recurse, false);
    }

    /**
     * Returns all music files that are children of this music file.
     * @param recurse Whether to recurse or not.
     * @param includeDirectories Whether directories should be included in the result.
     * @return All children music files.
     * @exception IOException If an I/O error occurs.
     */
    public MusicFile[] getChildren(boolean recurse, boolean includeDirectories) throws IOException {
        List<MusicFile> result = new ArrayList<MusicFile>();

        if (recurse) {
            listMusicRecursively(result, includeDirectories);
        } else {
            File[] files = file.listFiles();
            for (File file : files) {
                if (acceptMusic(file) && (file.isFile() || includeDirectories)) {
                    result.add(new MusicFile(file));
                }
            }
        }

        Collections.sort(result, new FileSorter());

        return result.toArray(new MusicFile[0]);
    }

    private void listMusicRecursively(List<MusicFile> musicFiles, boolean includeDirectories) throws IOException {
        if (isFile() || includeDirectories) {
            musicFiles.add(this);
        }

        if (isDirectory()) {
            MusicFile[] children = getChildren(false, true);

            for (MusicFile child : children) {
                child.listMusicRecursively(musicFiles, includeDirectories);
            }
        }
    }

    /**
     * Returns an array of appropriate cover art images for this music file.
     * @param limit Maximum number of images to return.
     * @return An array of appropriate cover art images for this music file.
     * @exception IOException If an I/O error occurs.
     */
    public File[] getCoverArt(int limit) throws IOException {
        List<File> result = new ArrayList<File>();
        listCoverArtRecursively(result, limit);

        return result.toArray(new File[0]);
    }

    private void listCoverArtRecursively(List<File> coverArtFiles, int limit) throws IOException {
        if (coverArtFiles.size() == limit) {
            return;
        }

        File[] files = file.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                new MusicFile(file).listCoverArtRecursively(coverArtFiles, limit);
            }
        }

        if (coverArtFiles.size() == limit) {
            return;
        }

        File best = getBestCoverArt(files);
        if (best != null) {
            coverArtFiles.add(best);
        }
    }

    private File getBestCoverArt(File[] candidates) {
        for (String mask : ServiceFactory.getSettingsService().getCoverArtMaskAsArray()) {
            for (File candidate : candidates) {
                if (candidate.getName().toUpperCase().endsWith(mask.toUpperCase())) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private boolean acceptMusic(File file) {
        if (file.isDirectory()) {
            return true;
        }

        for (String suffix : ServiceFactory.getSettingsService().getMusicMaskAsArray()) {
            if (file.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the bit rate of this music file.  NOTE: Only works for MP3 files.
     * @return The bit rate in kilobits per second, or 0 if the bit rate can't be resolved.
     */
    public synchronized int getBitRate() {
        if (bitRate == -1) {
            MetaDataParser parser = MetaDataParser.Factory.getInstance().getParser(this);
            bitRate = (parser == null) ? 0 : parser.getBitRate(this);
        }
        return bitRate;
    }

    /**
     * Returns whether this music file is equal to another object.
     * @param o The object to compare to.
     * @return Whether this music file is equal to another object.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicFile)) return false;

        final MusicFile musicFile = (MusicFile) o;

        if (file != null ? !file.equals(musicFile.file) : musicFile.file != null) return false;

        return true;
    }

    /**
     * Returns the hash code of this music file.
     * @return The hash code of this music file.
     */
    public int hashCode() {
        return (file != null ? file.hashCode() : 0);
    }

    /**
     * Returns the path of this music file as an URL-encoded string.
     * @return The path of this music file as an URL-encoded string.
     * @throws UnsupportedEncodingException
     */
    public String urlEncode() throws UnsupportedEncodingException {
        return StringUtil.urlEncode(getPath());
    }

    /**
     * Equivalent to {@link #getPath}.
     * @return This music file as a string.
     */
    public String toString() {
        return getPath();
    }

    /**
     * Contains meta-data (song title, artist, album etc) for a music file.
     */
    public static class MetaData {
        private String artist;
        private String album;
        private String title;
        private String year;

        public MetaData(String artist, String album, String title, String year) {
            this.artist = artist;
            this.album = album;
            this.title = title;
            this.year = year;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }

        public String getTitle() {
            return title;
        }

        public String getYear() {
            return year;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final MetaData metaData = (MetaData) o;

            if (album != null ? !album.equals(metaData.album) : metaData.album != null) return false;
            if (artist != null ? !artist.equals(metaData.artist) : metaData.artist != null) return false;
            if (title != null ? !title.equals(metaData.title) : metaData.title != null) return false;
            if (year != null ? !year.equals(metaData.year) : metaData.year != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (artist != null ? artist.hashCode() : 0);
            result = 29 * result + (album != null ? album.hashCode() : 0);
            result = 29 * result + (title != null ? title.hashCode() : 0);
            result = 29 * result + (year != null ? year.hashCode() : 0);
            return result;
        }
    }

    /**
     * Comparator for sorting music files.
     */
    private static class FileSorter implements Comparator<MusicFile> {

        public int compare(MusicFile a, MusicFile b) {
            if (a.isFile() && b.isDirectory()) {
                return 1;
            }

            if (a.isDirectory() && b.isFile()) {
                return -1;
            }

            return a.getPath().compareToIgnoreCase(b.getPath());
        }
    }
}
