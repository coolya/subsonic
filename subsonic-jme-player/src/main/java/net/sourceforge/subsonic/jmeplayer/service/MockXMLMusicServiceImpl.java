package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;
import net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement;

import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class MockXMLMusicServiceImpl implements MusicService {

    private static final String ARTIST_INDEX_XML = "<artistIndexes lastModified='237462836472342'>\n" +
                                                   "  <artistIndex index='A'>\n" +
                                                   "    <artist name='ABBA' path='c:/music/abba'/>\n" +
                                                   "    <artist name='Alphaville' path='c:/music/alphaville'/>\n" +
                                                   "  </artistIndex>\n" +
                                                   "  <artistIndex index='XYZ'>\n" +
                                                   "    <artist name='The Zoo' path='c:/music/the zoo'/>\n" +
                                                   "  </artistIndex>\n" +
                                                   "</artistIndexes>";


    private static final String MUSIC_DIRECTORY_XML_1 = "<directory path='c:/music/abba' name='ABBA' parent='c:/music'>\n" +
                                                        "  <child path='c:/music/abba/gold' name='Gold' isDir='true'/>\n" +
                                                        "  <child path='c:/music/abba/1.mp3' name='res - bark.wav' isDir='false' contentType='audio/x-wav' url='resource:/audio/bark.wav'/>\n" +
                                                        "</directory>";

    private static final String MUSIC_DIRECTORY_XML_2 = "<directory path='c:/music/abba/gold' name='Gold' parent='c:/music/abba'>\n" +
                                                        "  <child path='c:/music/abba/1.mp3' name='res - bark.wav' isDir='false' contentType='audio/x-wav' url='resource:/audio/bark.wav'/>\n" +
                                                        "  <child path='c:/music/abba/2.mp3' name='http - mp3' isDir='false' contentType='audio/mpeg' url='http://www.loopmasters.com/products/wav-mp3/Sh_120%20Rhodes%20Loop%207C.mp3'/>\n" +
                                                        "  <child path='c:/music/abba/3.mp3' name='res - mp3' isDir='false' contentType='audio/mpeg' url='resource:/audio/rhodes.mp3'/>\n" +
                                                        "  <child path='c:/music/abba/4.mp3' name='http - mid' isDir='false' contentType='audio/midi' url='http://people.nnu.edu/WDHUGHES/Joy.MID'/>\n" +
                                                        "  <child path='c:/music/abba/5.mp3' name='res - pattern.mid' isDir='false' contentType='audio/midi' url='resource:/audio/pattern.mid'/>\n" +
                                                        "  <child path='c:/music/abba/6.mp3' name='http - mp3 long' isDir='false' contentType='audio/mpeg' url='http://www.blueaudio.com/audio/Blue%20Audio%20-%20Tonight.mp3'/>\n" +
                                                        "</directory>";

    public ArtistIndex[] getArtistIndexes() throws Exception {
        kXMLElement root = new kXMLElement();
        root.parseString(ARTIST_INDEX_XML);

        Vector children = root.getChildren();
        ArtistIndex[] artistIndexes = new ArtistIndex[children.size()];
        for (int i = 0; i < children.size(); i++) {
            kXMLElement artistIndexElement = (kXMLElement) children.elementAt(i);
            Vector artistChildren = artistIndexElement.getChildren();
            Artist[] artists = new Artist[artistChildren.size()];
            for (int j = 0; j < artistChildren.size(); j++) {
                kXMLElement artistElement = (kXMLElement) artistChildren.elementAt(j);
                artists[j] = new Artist(artistElement.getProperty("name"), artistElement.getProperty("path"));
            }
            artistIndexes[i] = new ArtistIndex(artistIndexElement.getProperty("index"), artists);
        }

        return artistIndexes;
    }


    public MusicDirectory getMusicDirectory(String path) {
        kXMLElement root = new kXMLElement();

        if (path.equals("c:/music/abba")) {
            root.parseString(MUSIC_DIRECTORY_XML_1);
        } else if (path.equals("c:/music/abba/gold")) {
            root.parseString(MUSIC_DIRECTORY_XML_2);
        } else {
            throw new RuntimeException("Invalid path: " + path);
        }

        Vector children = root.getChildren();
        MusicDirectory.Entry[] entries = new MusicDirectory.Entry[children.size()];
        for (int i = 0; i < children.size(); i++) {
            kXMLElement childElement = (kXMLElement) children.elementAt(i);
            entries[i] = new MusicDirectory.Entry(childElement.getProperty("name"),
                                                  childElement.getProperty("path"),
                                                  childElement.getProperty("isDir", "true", "false", false),
                                                  childElement.getProperty("url"),
                                                  childElement.getProperty("contentType"));
        }

        return new MusicDirectory(root.getProperty("name"), root.getProperty("path"), root.getProperty("parent"), entries);
    }

    public static void main(String[] args) throws Exception {
//        ArtistIndex[] indexes = new MockXMLMusicServiceImpl().getArtistIndexes();
//        for (int i = 0; i < indexes.length; i++) {
//            ArtistIndex index = indexes[i];
//            System.out.println(index.getIndex());
//            Artist[] artists = index.getArtists();
//            for (int j = 0; j < artists.length; j++) {
//                Artist artist = artists[j];
//                System.out.println(artist.getName() + ": " + artist.getPath());
//            }
//        }

        MusicDirectory musicDirectory = new MockXMLMusicServiceImpl().getMusicDirectory("c:/music/abba");
        System.out.println(musicDirectory.getName() + ": " + musicDirectory.getPath());
        for (int i = 0; i < musicDirectory.getChildren().length; i++) {
            MusicDirectory.Entry entry = musicDirectory.getChildren()[i];
            System.out.println(entry.getName() + ": " + entry.getPath() + ", " + entry.isDirectory() + ", " + entry.getContentType() + ", " + entry.getUrl());
        }
    }
}
