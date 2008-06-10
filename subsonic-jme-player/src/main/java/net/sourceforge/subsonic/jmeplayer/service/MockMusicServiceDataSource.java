package net.sourceforge.subsonic.jmeplayer.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author Sindre Mehus
 */
public class MockMusicServiceDataSource implements MusicServiceDataSource {
    private static final String INDEX_XML = "<indexes lastModified='237462836472342'>\n" +
                                            "  <index name='A'>\n" +
                                            "    <artist name='ABBA' path='c:/music/abba'/>\n" +
                                            "    <artist name='Alanis Morisette' path='c:/music/Alanis Morisette'/>\n" +
                                            "    <artist name='Alphaville' path='c:/music/alphaville'/>\n" +
                                            "  </index>\n" +
                                            "  <index name='XYZ'>\n" +
                                            "    <artist name='The Zoo' path='c:/music/the zoo'/>\n" +
                                            "  </index>\n" +
                                            "</indexes>";


    private static final String MUSIC_DIRECTORY_XML_1 = "<directory path='c:/music/abba' name='ABBA' longName='ABBA' parent='c:/music'>\n" +
                                                        "  <child path='c:/music/abba/gold' name='Gold' isDir='true'/>\n" +
                                                        "  <child path='c:/music/abba/1.mp3' name='res - bark.wav' isDir='false' contentType='audio/x-wav' url='resource:/audio/bark.wav'/>\n" +
                                                        "</directory>";

    private static final String MUSIC_DIRECTORY_XML_2 = "<directory path='c:/music/abba/gold' name='Gold' longName='ABBA - Gold' parent='c:/music/abba'>\n" +
                                                        "  <child path='c:/music/abba/1.mp3' name='res - bark.wav' isDir='false' contentType='audio/x-wav' url='resource:/audio/bark.wav'/>\n" +
                                                        "  <child path='c:/music/abba/4.mp3' name='http - mid' isDir='false' contentType='audio/midi' url='http://people.nnu.edu/WDHUGHES/Joy.MID'/>\n" +
                                                        "  <child path='c:/music/abba/5.mp3' name='res - pattern.mid' isDir='false' contentType='audio/midi' url='resource:/audio/pattern.mid'/>\n" +
                                                        "  <child path='c:/music/abba/2.mp3' name='http - mp3' isDir='false' contentType='audio/mpeg' url='http://www.loopmasters.com/products/wav-mp3/Sh_120%20Rhodes%20Loop%207C.mp3'/>\n" +
                                                        "  <child path='c:/music/abba/3.mp3' name='res - mp3' isDir='false' contentType='audio/mpeg' url='resource:/audio/rhodes.mp3'/>\n" +
                                                        "  <child path='c:/music/abba/6.mp3' name='http - mp3 long' isDir='false' contentType='audio/mpeg' url='http://www.blueaudio.com/audio/Blue%20Audio%20-%20Tonight.mp3'/>\n" +
                                                        "</directory>";


    private static final String MUSIC_DIRECTORY_XML_3 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                                                        "<directory name='Alanis Morisette'  longName='Alanis Morisette' path='c:\\music\\Alanis Morisette'>\n" +
                                                        "<child name='Jagged Little Pill' path='c:\\music\\Alanis Morisette\\Jagged Little Pill' isDir='true' contentType='application/octet-stream' url='http://localhost:8080/stream?path=c:\\music\\Alanis Morisette&suffix=.null'/>\n" +
                                                        "</directory>";


    public Reader getIndexesReader() throws Exception {
        Thread.sleep(500L);
        return createReader(INDEX_XML);
    }

    public Reader getMusicDirectoryReader(String path) throws Exception {
        Thread.sleep(250L);

        if (path.equals("c:/music/abba")) {
            return createReader(MUSIC_DIRECTORY_XML_1);
        } else if (path.equals("c:/music/Alanis Morisette")) {
            return createReader(MUSIC_DIRECTORY_XML_3);
        } else if (path.equals("c:/music/abba/gold")) {
            return createReader(MUSIC_DIRECTORY_XML_2);
        } else {
            throw new RuntimeException("Invalid path: " + path);
        }
    }

    private Reader createReader(String s) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("UTF-8");
        return new InputStreamReader(new ByteArrayInputStream(bytes));
    }

}
