package net.sourceforge.subsonic.webservice;

import net.sourceforge.subsonic.webservice.dto.MusicFileDto;

/**
 * @author Sindre Mehus
 */
public class SubsonicWebServiceImpl implements SubsonicWebService {
    public String echo(String in) {
        return "Hello, " + in;
    }

    public MusicFileDto[] getMusicFiles(MusicFileDto parent) {

        if (parent == null) {
            return new MusicFileDto[] {
                    new MusicFileDto("ABBA", "c:/abba"),
                    new MusicFileDto("AC/DC", "c:/acdc"),
            };
        }
        return new MusicFileDto[] {
                new MusicFileDto("The Hjallas", "c:/hjallas")
        };
    }

    public MusicFileDto getMusicFile() {
        return new MusicFileDto("Iron Maiden", "c:/maiden");
    }


    public MusicFileDto getMusicFile2(MusicFileDto parent) {
        return getMusicFile();
    }

    public int add(int a, int b) {
        return a + b;
    }
}
