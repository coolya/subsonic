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
package net.sourceforge.subsonic.android.service;

import java.io.Reader;

import android.content.Context;
import net.sourceforge.subsonic.android.util.ProgressListener;

/**
 * @author Sindre Mehus
 */
public interface MusicServiceDataSource {

    Reader getPingReader(Context context, ProgressListener progressListener) throws Exception;

    Reader getLicenseReader(Context context, ProgressListener progressListener) throws Exception;

    Reader getIndexesReader(Context context, ProgressListener progressListener) throws Exception;

    Reader getMusicDirectoryReader(String id, Context context, ProgressListener progressListener) throws Exception;
}