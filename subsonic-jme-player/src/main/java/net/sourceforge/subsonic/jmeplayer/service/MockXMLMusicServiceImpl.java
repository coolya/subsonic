package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

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
        Reader reader = createReader(ARTIST_INDEX_XML);
        try {
            return new ArtistIndexParser().parse(reader);
        } finally {
            reader.close();
        }
    }

    private Reader createReader(String s) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("UTF-8");
        return new InputStreamReader(new ByteArrayInputStream(bytes));
    }


    public MusicDirectory getMusicDirectory(String path) throws Exception {
        Reader reader;
        if (path.equals("c:/music/abba")) {
            reader = createReader(MUSIC_DIRECTORY_XML_1);
        } else if (path.equals("c:/music/abba/gold")) {
            reader = createReader(MUSIC_DIRECTORY_XML_2);
        } else {
            throw new RuntimeException("Invalid path: " + path);
        }

        try {
            return new MusicDirectoryParser().parse(reader);
        } finally {
            reader.close();
        }
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
