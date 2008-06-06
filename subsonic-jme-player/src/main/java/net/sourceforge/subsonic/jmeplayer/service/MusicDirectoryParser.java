package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.xml.XMLElement;

import java.io.Reader;
import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class MusicDirectoryParser {

    public MusicDirectory parse(Reader reader) throws Exception {
        XMLElement root = new XMLElement();
        root.parseFromReader(reader);

        Vector children = root.getChildren();
        MusicDirectory.Entry[] entries = new MusicDirectory.Entry[children.size()];
        for (int i = 0; i < children.size(); i++) {
            XMLElement childElement = (XMLElement) children.elementAt(i);
            entries[i] = new MusicDirectory.Entry(childElement.getProperty("name"),
                                                  childElement.getProperty("path"),
                                                  childElement.getProperty("isDir", "true", "false", false),
                                                  childElement.getProperty("url"),
                                                  childElement.getProperty("contentType"));
        }

        return new MusicDirectory(root.getProperty("name"), root.getProperty("path"), root.getProperty("parent"), entries);
    }
}