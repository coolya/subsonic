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
package net.sourceforge.subsonic.domain;

import java.util.Date;

import net.sourceforge.subsonic.service.SearchService;

/**
 * Defines criteria used when searching.
 *
 * @author Sindre Mehus
 * @see SearchService#search
 */
public class SearchCriteria {

    private String any;
    private String title;
    private String album;
    private String artist;

    private Date newerThan;
    private int offset;
    private int count;

    public void setAny(String any) {
        this.any = any;
    }

    public String getAny() {
        return any;
    }

    @Deprecated
    public String getTitle() {
        return title;
    }

    @Deprecated
    public void setTitle(String title) {
        this.title = title;
    }

    @Deprecated
    public String getAlbum() {
        return album;
    }

    @Deprecated
    public void setAlbum(String album) {
        this.album = album;
    }

    @Deprecated
    public String getArtist() {
        return artist;
    }

    @Deprecated
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Deprecated
    public Date getNewerThan() {
        return newerThan;
    }

    @Deprecated
    public void setNewerThan(Date newerThan) {
        this.newerThan = newerThan;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}