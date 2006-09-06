package net.sourceforge.subsonic.domain;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.util.*;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * Represents a file or directory containing music. Music files can be put in a {@link Playlist},
 * and may be streamed to remote players.  All music files are located in a configurable root music folder.
 *
 * @author Sindre Mehus
 */
public class MusicFile {

    private static final Logger LOG = Logger.getLogger(MusicFile.class);

    private File file;
    private MetaData metaData;
    private Set<String> excludes;

    /**
     * Creates a new instance for the given file.
     * @param file A file on the local file system.
     * @exception SecurityException If access is denied to the given file.
     */
    public MusicFile(File file) {
        if (!ServiceLocator.getSecurityService().isReadAllowed(file)) {
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

        return getFirstChild() != null;
    }

    /**
    * Returns whether this music file is one of the root music folders.
    * @return Whether this music file is one of the root music folders.
    */
    public boolean isRoot() {
        SettingsService settings = ServiceLocator.getSettingsService();
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
     * Returns the file suffix, e.g., "mp3".
     * @return The file suffix.
     */
    public String getSuffix() {
        return StringUtils.getFilenameExtension(getName());
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
            MusicFile[] musicFiles = new MusicFile[files.length];
            for (int i = 0; i < files.length; i++) {
                musicFiles[i] = new MusicFile(files[i]);
            }
            Arrays.sort(musicFiles, new MusicFileSorter());

            for (MusicFile musicFile : musicFiles) {
                if (acceptMusic(musicFile.getFile()) && (musicFile.isFile() || includeDirectories)) {
                    try {
                        result.add(musicFile);
                    } catch (SecurityException x) {
                        LOG.warn("Failed to create MusicFile for " + musicFile, x);
                    }
                }
            }
        }

        return result.toArray(new MusicFile[0]);
    }

    /**
     * Returns the first direct child (excluding directories).
     * This method is an optimization.
     * @return The first child, or <code>null</code> if not found.
     * @throws IOException If an I/O error occurs.
     */
    public MusicFile getFirstChild() throws IOException {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile() && acceptMusic(f)) {
                try {
                    return new MusicFile(f);
                } catch (SecurityException x) {
                    LOG.warn("Failed to create MusicFile for " + f, x);
                }
            }
        }
        return null;
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
            if (file.isDirectory() && !isExcluded(file)) {
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
        for (String mask : ServiceLocator.getSettingsService().getCoverArtMaskAsArray()) {
            for (File candidate : candidates) {
                if (candidate.getName().toUpperCase().endsWith(mask.toUpperCase())) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private boolean acceptMusic(File file) throws IOException {

        if (isExcluded(file)) {
            return false;
        }

        if (file.isDirectory()) {
            return true;
        }

        for (String suffix : ServiceLocator.getSettingsService().getMusicMaskAsArray()) {
            if (file.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given file is excluded, i.e., whether it is listed in 'subsonic_exlude.txt' in
     * the current directory.
     * @param file The child file in question.
     * @return Whether the child file is excluded.
     */
    private boolean isExcluded(File file) throws IOException {
        if (excludes == null) {
            excludes = new HashSet<String>();
            File excludeFile = new File(this.file, "subsonic_exclude.txt");
            if (excludeFile.exists()) {
                String[] lines = StringUtil.readLines(new FileInputStream(excludeFile));
                for (String line : lines) {
                    excludes.add(line.toLowerCase());
                }
            }
        }

        return excludes.contains(file.getName().toLowerCase());
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

        private static final SimpleDateFormat TIME_IN_FORMAT    = new SimpleDateFormat("ss");
        private static final SimpleDateFormat TIME_OUT_FORMAT   = new SimpleDateFormat("m:ss");

        private Integer trackNumber;
        private String title;
        private String artist;
        private String album;
        private String genre;
        private String year;
        private Integer bitRate;
        private Boolean variableBitRate;
        private Integer duration;
        private String format;
        private Long fileSize;

        public Integer getTrackNumber() {
            return trackNumber;
        }

        public void setTrackNumber(Integer trackNumber) {
            this.trackNumber = trackNumber;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public Integer getBitRate() {
            return bitRate;
        }

        public void setBitRate(Integer bitRate) {
            this.bitRate = bitRate;
        }

        public Boolean getVariableBitRate() {
            return variableBitRate;
        }

        public void setVariableBitRate(Boolean variableBitRate) {
            this.variableBitRate = variableBitRate;
        }

        public Integer getDuration() {
            return duration;
        }

        public String getDurationAsString() {
            if (duration == null) {
                return null;
            }
            try {
                Date timeIn = TIME_IN_FORMAT.parse(String.valueOf(duration));
                return TIME_OUT_FORMAT.format(timeIn);
            } catch (ParseException x) {
                return null;
            }
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }

    /**
     * Comparator for sorting music files.
     */
    private static class MusicFileSorter implements Comparator<MusicFile> {

        public int compare(MusicFile a, MusicFile b) {
            if (a.isFile() && b.isDirectory()) {
                return 1;
            }

            if (a.isDirectory() && b.isFile()) {
                return -1;
            }

            if (a.isDirectory() && b.isDirectory()) {
                return a.getName().compareToIgnoreCase(b.getName());
            }

            Integer trackA = a.getMetaData().getTrackNumber();
            Integer trackB = b.getMetaData().getTrackNumber();

            if (trackA == null && trackB != null) {
                return 1;
            }

            if (trackA != null && trackB == null) {
                return -1;
            }

            if (trackA == null && trackB == null) {
                return a.getName().compareToIgnoreCase(b.getName());
            }

            return trackA.compareTo(trackB);
        }
    }
}
