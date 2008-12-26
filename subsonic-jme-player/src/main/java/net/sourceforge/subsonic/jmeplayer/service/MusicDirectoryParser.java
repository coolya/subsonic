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
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.xml.XMLElement;

import java.io.Reader;
import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryParser extends AbstractParser {

    public MusicDirectory parse(Reader reader) throws Exception {
        XMLElement root = new XMLElement();
        root.parseFromReader(reader);
        checkForError(root);

        Vector children = root.getChildren();
        MusicDirectory.Entry[] entries = new MusicDirectory.Entry[children.size()];
        for (int i = 0; i < children.size(); i++) {
            XMLElement childElement = (XMLElement) children.elementAt(i);
            entries[i] = new MusicDirectory.Entry(childElement.getProperty("name"),
                                                  childElement.getProperty("path"),
                                                  childElement.getProperty("isDir", "true", "false", false),
                                                  childElement.getProperty("url"),
                                                  childElement.getProperty("contentType"),
                                                  childElement.getProperty("suffix"));
        }

        return new MusicDirectory(root.getProperty("name"), root.getProperty("longName"),
                                  root.getProperty("path"), root.getProperty("parent"), entries);
    }
}