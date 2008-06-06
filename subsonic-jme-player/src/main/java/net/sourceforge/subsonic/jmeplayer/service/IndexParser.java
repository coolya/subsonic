package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.Index;
import net.sourceforge.subsonic.jmeplayer.xml.XMLElement;

import java.io.Reader;
import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class IndexParser {

    public Index[] parse(Reader reader) throws Exception {
        XMLElement root = new XMLElement();
        root.parseFromReader(reader);

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
