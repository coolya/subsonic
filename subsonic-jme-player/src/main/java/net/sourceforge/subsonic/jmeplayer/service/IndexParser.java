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

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.xml.XMLElement;

import java.io.Reader;
import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class IndexParser extends AbstractParser {

    public Index[] parse(Reader reader) throws Exception {
        XMLElement root = new XMLElement();
        root.parseFromReader(reader);
        checkForError(root);

        Vector children = root.getChildren();
        Index[] indexes = new Index[children.size()];
        for (int i = 0; i < children.size(); i++) {
            XMLElement indexElement = (XMLElement) children.elementAt(i);
            Vector artistChildren = indexElement.getChildren();
            Artist[] artists = new Artist[artistChildren.size()];
            for (int j = 0; j < artistChildren.size(); j++) {
                XMLElement artistElement = (XMLElement) artistChildren.elementAt(j);
                artists[j] = new Artist(artistElement.getProperty("name"), artistElement.getProperty("path"));
            }
            indexes[i] = new Index(indexElement.getProperty("name"), artists);
        }

        return indexes;
    }
}
