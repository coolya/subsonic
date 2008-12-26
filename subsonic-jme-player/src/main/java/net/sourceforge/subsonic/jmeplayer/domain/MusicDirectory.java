/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.jmeplayer.domain;

import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class MusicDirectory {

    private final String name;
    private final String longName;
    private final String path;
    private final String parentPath;
    private final Entry[] children;

    public MusicDirectory(String name, String longName, String path, String parentPath, Entry[] children) {
        this.name = name;
        this.longName = longName;
        this.path = path;
        this.parentPath = parentPath;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    public String getPath() {
        return path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public Entry[] getChildren() {
        return getChildren(true);
    }

    public Entry[] getChildren(boolean includeDirs) {
        if (includeDirs) {
            return children;
        }

        Vector files = new Vector(children.length);
        for (int i = 0; i < children.length; i++) {
            Entry child = children[i];
            if (!child.isDirectory()) {
                files.addElement(child);
            }
        }
        Entry[] result = new Entry[files.size()];
        for (int i = 0; i < files.size(); i++) {
            result[i] = (Entry) files.elementAt(i);
        }
        return result;
    }

    public static class Entry {
        private final String name;
        private final String path;
        private final boolean directory;
        private final String url;
        private final String contentType;
        private final String suffix;

        public Entry(String name, String path, boolean directory, String url, String contentType, String suffix) {
            this.name = name;
            this.path = path;
            this.directory = directory;
            this.url = url;
            this.contentType = contentType;
            this.suffix = suffix;
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

        public String getSuffix() {
            return suffix;
        }
    }
}
