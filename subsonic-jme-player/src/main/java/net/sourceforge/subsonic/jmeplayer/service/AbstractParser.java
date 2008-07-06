package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.xml.XMLElement;

/**
 * @author Sindre Mehus
 */
public abstract class AbstractParser {

    protected void checkForError(XMLElement root) throws Exception {
        if ("error".equals(root.getTagName())) {
            throw new Exception(root.getContents());
        }
    }
}
