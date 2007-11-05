package net.sourceforge.subsonic.webservice;

import net.sourceforge.subsonic.webservice.dto.MusicFileDto;


/**
 * @author Sindre Mehus
 */
public interface SubsonicWebService {
    String echo(String in);

    MusicFileDto[] getMusicFiles(MusicFileDto parent);

    MusicFileDto getMusicFile();
    MusicFileDto getMusicFile2(MusicFileDto parent);

    int add(int a, int b);
}
