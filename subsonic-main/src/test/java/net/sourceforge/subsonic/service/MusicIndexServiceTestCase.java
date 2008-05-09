package net.sourceforge.subsonic.service;


import junit.framework.TestCase;
import net.sourceforge.subsonic.domain.MusicIndex;

import java.util.List;

/**
 * Unit test of {@link MusicIndex}.
 *
 * @author Sindre Mehus
 */
public class MusicIndexServiceTestCase extends TestCase {

    private final MusicIndexService musicIndexService = new MusicIndexService();

    public void testCreateIndexFromExpression() throws Exception {
        MusicIndex index = musicIndexService.createIndexFromExpression("A");
        assertEquals("A", index.getIndex());
        assertEquals(1, index.getPrefixes().size());
        assertEquals("A", index.getPrefixes().get(0));

        index = musicIndexService.createIndexFromExpression("The");
        assertEquals("The", index.getIndex());
        assertEquals(1, index.getPrefixes().size());
        assertEquals("The", index.getPrefixes().get(0));

        index = musicIndexService.createIndexFromExpression("X-Z(XYZ)");
        assertEquals("X-Z", index.getIndex());
        assertEquals(3, index.getPrefixes().size());
        assertEquals("X", index.getPrefixes().get(0));
        assertEquals("Y", index.getPrefixes().get(1));
        assertEquals("Z", index.getPrefixes().get(2));
    }

    public void testCreateIndexesFromExpression() throws Exception {
        List<MusicIndex> indexes = musicIndexService.createIndexesFromExpression("A B  The X-Z(XYZ)");
        assertEquals(4, indexes.size());

        assertEquals("A", indexes.get(0).getIndex());
        assertEquals(1, indexes.get(0).getPrefixes().size());
        assertEquals("A", indexes.get(0).getPrefixes().get(0));

        assertEquals("B", indexes.get(1).getIndex());
        assertEquals(1, indexes.get(1).getPrefixes().size());
        assertEquals("B", indexes.get(1).getPrefixes().get(0));

        assertEquals("The", indexes.get(2).getIndex());
        assertEquals(1, indexes.get(2).getPrefixes().size());
        assertEquals("The", indexes.get(2).getPrefixes().get(0));

        assertEquals("X-Z", indexes.get(3).getIndex());
        assertEquals(3, indexes.get(3).getPrefixes().size());
        assertEquals("X", indexes.get(3).getPrefixes().get(0));
        assertEquals("Y", indexes.get(3).getPrefixes().get(1));
        assertEquals("Z", indexes.get(3).getPrefixes().get(2));
    }
}