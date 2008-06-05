package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;

/**
 * @author Sindre Mehus
 */
public class MockXMLMusicServiceImplImpl extends MockMusicServiceImpl {

    private final ArtistIndexParser artistIndexParser = new ArtistIndexParser();

    private static final String ARTIST_INDEX_XML = "<artistIndexes lastModified='237462836472342'>\n" +
                                                   "  <artistIndex index='A'>\n" +
                                                   "    <artist name='ABBA' path='c:/music/abba'/>\n" +
                                                   "    <artist name='Alphaville' path='c:/music/alphaville'/>\n" +
                                                   "  </artistIndex>\n" +
                                                   "  <artistIndex index='XYZ'>\n" +
                                                   "    <artist name='The Zoo' path='c:/music/the zoo'/>\n" +
                                                   "  </artistIndex>\n" +
                                                   "</artistIndexes>";

    public ArtistIndex[] getArtistIndexes() throws Exception {
        return artistIndexParser.parse(ARTIST_INDEX_XML.getBytes("UTF-8"));
    }
}
