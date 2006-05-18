package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.domain.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Provides services for searching for music.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.22 $ $Date: 2006/02/27 21:39:39 $
 */
public class SearchService {

    private static final int INDEX_VERSION = 7;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final Logger LOG = Logger.getLogger(SearchService.class);

    private Map<File, Line> cachedIndex;
    private List<Line> cachedSongs;
    private SortedSet<Line> cachedAlbums;  // Sorted chronologically.
    private MediaLibraryStatistics statistics;

    private boolean creatingIndex;
    private Timer timer;

    /**
     * Creates a new instance.
     */
    public SearchService() {
        schedule();
    }

    /**
     * Returns whether the search index exists.
     * @return Whether the search index exists.
     */
    public synchronized boolean isIndexCreated() {
        return getIndexFile().exists();
    }

    /**
     * Returns whether the search index is currently being created.
     * @return Whether the search index is currently being created.
     */
    public synchronized boolean isIndexBeingCreated() {
        return creatingIndex;
    }

    /**
     * Generates the search index.  If the index already exists it will be
     * overwritten.  The index is created asynchronously, i.e., this method returns
     * before the index is created.
     */
    public synchronized void createIndex() {
        if (isIndexBeingCreated()) {
            return;
        }
        creatingIndex = true;

        Thread thread = new Thread("Search Index Generator") {
            public void run() {
                deleteOldIndexFiles();
                LOG.info("Starting to create search index.");
                PrintWriter writer = null;

                try {

                    // Get existing index.
                    Map<File, Line> oldIndex = getIndex();

                    writer = new PrintWriter(new FileWriter(getIndexFile()));
                    SettingsService settings = ServiceFactory.getSettingsService();

                    // Read entire music directory.
                    MusicFolder[] musicFolders = settings.getAllMusicFolders();
                    int count = 0;
                    for (MusicFolder musicFolder : musicFolders) {

                        MusicFile root = new MusicFile(musicFolder.getPath());
                        MusicFile[] all = root.getChildren(true, true);

                        for (MusicFile file : all) {
                            count++;
                            writer.println(Line.forFile(file, oldIndex));
                        }
                    }

                    // Clear memory cache.
                    synchronized (SearchService.this) {
                        cachedIndex = null;
                        cachedSongs = null;
                        cachedAlbums = null;
                        statistics = null;
                    }

                    LOG.info("Created search index with " + count + " entries.");

                } catch (Exception x) {
                    LOG.error("Failed to create search index.", x);
                } finally {
                    creatingIndex = false;
                    if (writer != null) writer.close();
                }
            }};

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * Schedule background execution of index creation.
     */
    public synchronized void schedule() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);

        TimerTask task = new TimerTask() {
            public void run() {
                createIndex();
            }
        };

        SettingsService settings = ServiceFactory.getSettingsService();
        long daysBetween = settings.getIndexCreationInterval();
        int hour = settings.getIndexCreationHour();

        if (daysBetween == -1) {
            LOG.info("Automatic index creation disabled.");
            return;
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTime().before(now)) {
            cal.add(Calendar.DATE, 1);
        }

        Date firstTime = cal.getTime();
        long period = daysBetween * 24L * 3600L * 1000L;
        timer.schedule(task, firstTime, period);

        LOG.info("Automatic index creation scheduled to run every " + daysBetween + " day(s), starting at " + firstTime);
    }

    /**
     * Similar to {@link #search}, but uses a simple heuristic approach to get the most relevant matches first.
     *
     * @param query Text to match.
     * @param maxHits The maximum number of hits to return.
     * @param includeArtist Whether to include artist name in search.
     * @param includeAlbum Whether to include album name in search.
     * @param includeTitle Whether to include song title in search.
     * @param newerThan Only return music files newer than this date. If <code>null</code>, this parameter has no effect.
     * @return A list of music files fulfilling the search criteria.
     * @throws IOException If an I/O error occurs.
     */
    public synchronized List<MusicFile> heuristicSearch(String query, int maxHits, boolean includeArtist, boolean includeAlbum,
                                                        boolean includeTitle, Date newerThan) throws IOException {
        if (query == null) {
            query = "";
        }

        // Step one: search for exact match.
        List<MusicFile> resultOne = search(new String[] {query}, maxHits, includeArtist, includeAlbum, includeTitle, newerThan);

        // If a substantial amount of hits were found, return it.
        if (resultOne.size() > maxHits / 10) {
            return resultOne;
        }

        // Step two: split query and re-run search.
        List<MusicFile> resultTwo = search(splitQuery(query), maxHits - resultOne.size(), includeArtist, includeAlbum, includeTitle, newerThan);

        // Step three: compute the union
        for (MusicFile file : resultTwo) {
            if (!resultOne.contains(file)) {
                resultOne.add(file);
            }
        }

        return resultOne;
    }

    /**
     * Search for music files fulfilling the given search criteria. Only songs (files, not directories)
     * are returned.
     *
     * @param query Array of strings to match. All of the strings must match.
     * @param maxHits The maximum number of hits to return.
     * @param includeArtist Whether to include artist name in search.
     * @param includeAlbum Whether to include album name in search.
     * @param includeTitle Whether to include song title in search.
     * @param newerThan Only return music files newer than this date. If <code>null</code>, this parameter has no effect.
     * @return A list of music files fulfilling the search criteria.
     * @throws IOException If an I/O error occurs.
     */
    public synchronized List<MusicFile> search(String[] query, int maxHits, boolean includeArtist, boolean includeAlbum,
                                               boolean includeTitle, Date newerThan) throws IOException {
        List<MusicFile> result = new ArrayList<MusicFile>();
        if (!isIndexCreated() || isIndexBeingCreated()) {
            return result;
        }

        if (query.length == 0) {
            query = new String[] {""};
        }

        long newerThanTime = newerThan == null ? 0 : newerThan.getTime();

        Map<File,Line> index = getIndex();

        for (Line line : index.values()) {
            try {

                if (!line.isFile || line.lastModified < newerThanTime) {
                    continue;
                }

                boolean isMatch = false;
                for (String criteria : query) {
                    boolean isArtistMatch = includeArtist && containsIgnoreCase(line.artist, criteria);
                    boolean isAlbumMatch  = includeAlbum  && containsIgnoreCase(line.album, criteria);
                    boolean isTitleMatch  = includeTitle  && containsIgnoreCase(line.title, criteria);
                    isMatch = isArtistMatch || isAlbumMatch || isTitleMatch;
                    if (!isMatch) {
                        break;
                    }
                }

                if (!isMatch) {
                    continue;
                }

                if (line.file.exists()) {
                    result.add(new MusicFile(line.file));
                    if (result.size() >= maxHits) {
                        return result;
                    }
                }

            } catch (Exception x) {
                LOG.error("An error occurred while searching '" + line + "'.", x);
            }
        }
        return result;
    }

    /**
     * Splits the criteria of a query.  For instance, the input <code>"u2 rem "greatest hits""</code> will return an array with
     * three elements: <code>{"u2", "rem", "greatest hits"}</code>
     * @param query The query string.
     * @return Array of query criteria.
     */
    public String[] splitQuery(String query) {
        if (query == null) {
            return new String[0];
        }

        Pattern pattern = Pattern.compile("\".*?\"|\\S+");
        Matcher matcher = pattern.matcher(query);

        List<String> result = new ArrayList<String>();
        while (matcher.find()) {
            String criteria = matcher.group();
            if (criteria.startsWith("\"") && criteria.endsWith("\"") && criteria.length() > 1) {
                criteria = criteria.substring(1, criteria.length() - 1);
            }
            result.add(criteria);
        }

        return result.toArray(new String[0]);
    }

    /**
     * Returns media libaray statistics, including the number of artists, albums and songs.
     * @return Media library statistics.
     * @throws IOException If an I/O error occurs.
     */
    public MediaLibraryStatistics getStatistics() throws IOException {
        if (!isIndexCreated() || isIndexBeingCreated()) {
            return null;
        }

        // Ensure that index is read to memory.
        getIndex();
        return statistics;
    }

    /**
     * Returns a number of random music files.
     * @param count Maximum number of music files to return.
     * @return Array of random music files.
     * @throws IOException If an I/O error occurs.
     */
    public List<MusicFile> getRandomMusicFiles(int count) throws IOException {
        long t0 = System.currentTimeMillis();

        List<MusicFile> result = new ArrayList<MusicFile>(count);

        // Ensure that index is read to memory.
        getIndex();

        if (!isIndexCreated() || isIndexBeingCreated() || cachedSongs.isEmpty()) {
            return result;
        }

        SecurityService securityService = ServiceFactory.getSecurityService();
        for (int i = 0; i < count; i++) {
            int n = RANDOM.nextInt(cachedSongs.size());
            File file = cachedSongs.get(n).file;
            if (file.exists() && securityService.isReadAllowed(file)) {
                result.add(new MusicFile(file));
            }
        }

        long t1 = System.currentTimeMillis();
        LOG.debug("Found " + result.size() + " random files in " + (t1 - t0) + " ms.");

        return result;
    }

    /**
     * Returns a number of least recently modified music files. Only directories (albums) are returned.
     * @param offset Number of music files to skip.
     * @param count Maximum number of music files to return.
     * @return Array of new music files.
     * @throws IOException If an I/O error occurs.
     */
    public List<MusicFile> getNewestAlbums(int offset, int count) throws IOException {
        long t0 = System.currentTimeMillis();

        List<MusicFile> result = new ArrayList<MusicFile>(count);

        // Ensure that index is read to memory.
        getIndex();

        if (!isIndexCreated() || isIndexBeingCreated()) {
            return result;
        }

        SecurityService securityService = ServiceFactory.getSecurityService();
        int n = 0;
        for (Line line : cachedAlbums) {
            if (n == count + offset) {
                break;
            }
            if (line.file.exists() && securityService.isReadAllowed(line.file)) {
                if (n >= offset) {
                    result.add(new MusicFile(line.file));
                }
                n++;
            }
        }

        long t1 = System.currentTimeMillis();
        LOG.debug("Found " + result.size() + " new albums in " + (t1 - t0) + " ms.");

        return result;
    }

    /**
    * Returns the search index as a map from files to {@link Line} instances.
    * @return The search index.
    * @throws IOException If an I/O error occurs.
    */
    private synchronized Map<File, Line> getIndex() throws IOException {
        if (!isIndexCreated()) {
            return new HashMap<File,Line>();
        }

        if (cachedIndex != null) {
            return cachedIndex;
        }

        cachedIndex = new HashMap<File,Line>();

        // Statistics.
        int songCount = 0;
        long totalLength = 0;
        Set<String> artists = new HashSet<String>();
        Set<String> albums = new HashSet<String>();

        cachedSongs = new ArrayList<Line>();
        cachedAlbums = new TreeSet<Line>(new Comparator<Line>() {
            public int compare(Line line1, Line line2) {
                if (line2.lastModified < line1.lastModified) {
                    return -1;
                }
                if (line1.lastModified < line2.lastModified) {
                    return 1;
                }
                return 0;
            }
        });

        BufferedReader reader = new BufferedReader(new FileReader(getIndexFile()));

        try {

            for (String s = reader.readLine(); s != null; s = reader.readLine()) {

                try {

                    Line line = Line.parse(s);
                    cachedIndex.put(line.file, line);

                    if (line.isAlbum) {
                        cachedAlbums.add(line);
                    } else if (line.isFile) {
                        songCount++;
                        totalLength += line.length;
                        artists.add(line.artist);
                        albums.add(line.album);
                        cachedSongs.add(line);
                    }

                } catch (Exception x) {
                    LOG.error("An error occurred while reading index entry '" + s + "'.", x);
                }
            }
        } finally {
            reader.close();
        }

        statistics = new MediaLibraryStatistics(artists.size(), albums.size(), songCount, totalLength);

        return cachedIndex;
    }

    /**
     * Returns whether the given text contains the given substring, ignoring upper/lower-case.
     * @param text The string to search in.
     * @param substring The string to search for.
     * @return Whether the substring is found.
     */
    private boolean containsIgnoreCase(String text, String substring) {
        if (text == null) {
            return false;
        }
        text = text.toUpperCase();
        substring = substring.toUpperCase();

        return text.contains(substring);
    }

    /**
     * Returns the file containing the index.
     * @return The file containing the index.
     */
    private File getIndexFile() {
        return getIndexFile(INDEX_VERSION);
    }

    /**
     * Returns the index file for the given index version.
     * @param version The index version.
     * @return The index file for the given index version.
     */
    private File getIndexFile(int version) {
        File home = SettingsService.getSubsonicHome();
        return new File(home, "subsonic" + version + ".index");
    }

    /**
     * Deletes old versions of the index file.
     */
    private void deleteOldIndexFiles() {
        for (int i = 2; i < INDEX_VERSION; i++) {
            File file = getIndexFile(i);
            try {
                if (file.exists()) {
                    if (file.delete()) {
                        LOG.info("Deleted old index file: " + file.getPath());
                    }
                }
            } catch (Exception x) {
                LOG.warn("Failed to delete old index file: " + file.getPath(), x);
            }
        }
    }

    /**
     * Contains the content of a single line in the index file.
     */
    static class Line {

        /** Column separator. */
        static final String SEPARATOR = " ixYxi ";

        private boolean isFile;
        private boolean isAlbum;
        private boolean isDirectory;
        private long    lastModified;
        private File    file;
        private long    length;
        private String  artist;
        private String  album;
        private String  title;
        private String  year;

        private Line() {}

        /**
         * Creates a line instance by parsing the given string.
         * @param s The string to parse.
         * @return The line created by parsing the string.
         */
        public static Line parse(String s) {
            Line line = new Line();

            String[] tokens = s.split(SEPARATOR, -1);
            line.isFile = "F".equals(tokens[0]);
            line.isAlbum = "A".equals(tokens[0]);
            line.isDirectory = "D".equals(tokens[0]);
            line.lastModified = Long.parseLong(tokens[1]);
            line.file    = new File(tokens[2]);
            if (line.isFile) {
                line.length  = Long.parseLong(tokens[3]);
                line.artist = tokens[4].length() == 0 ? null : tokens[4];
                line.album  = tokens[5].length() == 0 ? null : tokens[5];
                line.title  = tokens[6].length() == 0 ? null : tokens[6];
                line.year   = tokens[7].length() == 0 ? null : tokens[7];
            }

            return line;
        }

        /**
         * Creates a line instance representing the given music file.
         * @param file The music file.
         * @param index The existing search index. Used to avoid parsing metadata if the file has not changed
         * since the last time the search index was created.
         * @return A line instance representing the given music file.
         */
        public static Line forFile(MusicFile file, Map<File, Line> index) {
            // Look in existing index first.
            Line existingLine = index.get(file.getFile());

            // Found up-to-date line?
            if (existingLine != null && file.lastModified() == existingLine.lastModified) {
                return existingLine;
            }

            // Otherwise, construct meta data.
            Line line = new Line();

            MusicFile.MetaData metaData = file.getMetaData();
            line.isFile = file.isFile();
            line.isDirectory = file.isDirectory();
            if (line.isDirectory) {
                try {
                    line.isAlbum = file.isAlbum();
                } catch (IOException x) {
                    LOG.warn("Failed to determine if " + file + " is an album.", x);
                }
            }
            line.lastModified = file.lastModified();
            line.file = file.getFile();
            if (line.isFile) {
                line.length = file.length();
                line.artist = metaData.getArtist();
                line.album  = metaData.getAlbum();
                line.title  = metaData.getTitle();
                line.year   = metaData.getYear();
            }
            return line;
        }

        /**
         * Returns the content of this line as a string.
         * @return The content of this line as a string.
         */
        public String toString() {
            StringBuffer buf = new StringBuffer(256);

            if (isFile) {
                buf.append('F').append(SEPARATOR);
            } else if (isAlbum) {
                buf.append('A').append(SEPARATOR);
            } else {
                buf.append('D').append(SEPARATOR);
            }

            buf.append(lastModified).append(SEPARATOR);
            buf.append(file.getPath()).append(SEPARATOR);
            buf.append(length).append(SEPARATOR);
            buf.append(artist == null ? "" : artist).append(SEPARATOR);
            buf.append(album == null ? "" : album).append(SEPARATOR);
            buf.append(title == null ? "" : title).append(SEPARATOR);
            buf.append(year == null ? "" : year);

            return buf.toString();
        }
    }
}
