package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.nanoxml.XMLElement;

import java.io.Reader;
import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class ArtistIndexParser {

    public ArtistIndex[] parse(Reader reader) throws Exception {
        XMLElement root = new XMLElement();
        root.parseFromReader(reader);

        Vector children = root.getChildren();
        ArtistIndex[] artistIndexes = new ArtistIndex[children.size()];
        for (int i = 0; i < children.size(); i++) {
            XMLElement artistIndexElement = (XMLElement) children.elementAt(i);
            Vector artistChildren = artistIndexElement.getChildren();
            Artist[] artists = new Artist[artistChildren.size()];
            for (int j = 0; j < artistChildren.size(); j++) {
                XMLElement artistElement = (XMLElement) artistChildren.elementAt(j);
                artists[j] = new Artist(artistElement.getProperty("name"), artistElement.getProperty("path"));
            }
            artistIndexes[i] = new ArtistIndex(artistIndexElement.getProperty("index"), artists);
        }

        return artistIndexes;
    }
}
