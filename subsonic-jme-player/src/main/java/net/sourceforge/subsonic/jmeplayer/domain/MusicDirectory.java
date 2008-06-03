package net.sourceforge.subsonic.jmeplayer.domain;

/**
 * @author Sindre Mehus
 */
public class MusicDirectory {

    private final String name;
    private final String path;
    private final String parentPath;
    private final Entry[] children;

    public MusicDirectory(String name, String path, String parentPath, Entry[] children) {
        this.name = name;
        this.path = path;
        this.parentPath = parentPath;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public Entry[] getChildren() {
        return children;
    }

    public static class Entry {
        private final String name;
        private final String path;
        private final boolean directory;
        private final String url;
        private final String contentType;

        public Entry(String name, String path, boolean directory, String url, String contentType) {
            this.name = name;
            this.path = path;
            this.directory = directory;
            this.url = url;
            this.contentType = contentType;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public boolean isDirectory() {
            return directory;
        }

        public String getUrl() {
            return url;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
