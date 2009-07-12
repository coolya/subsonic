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
package net.sourceforge.subsonic.android.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sindre Mehus
 */
public class MusicDirectory {

    private String name;
    private String longName;
    private String path;
    private String parentPath;
    private final List<Entry> children = new ArrayList<Entry>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void addChild(Entry child) {
        children.add(child);
    }

    public List<Entry> getChildren() {
        return getChildren(true, true);
    }

    public List<Entry> getChildren(boolean includeDirs, boolean includeFiles) {
        if (includeDirs && includeFiles) {
            return children;
        }

        List<Entry> result = new ArrayList<Entry>(children.size());
        for (Entry child : children) {
            if (child.isDirectory() && includeDirs || !child.isDirectory() && includeFiles) {
                result.add(child);
            }
        }
        return result;
    }

    public static class Entry {
        private String name;
        private String path;
        private boolean directory;
        private String url;
        private String contentType;
        private String suffix;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isDirectory() {
            return directory;
        }

        public void setDirectory(boolean directory) {
            this.directory = directory;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}