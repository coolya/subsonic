package net.sourceforge.subsonic.jmeplayer.service;

import javax.microedition.io.Connector;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Sindre Mehus
 */
public class HTTPMusicServiceDataSource implements MusicServiceDataSource {

    private String baseUrl = "http://localhost:8080/";

    public Reader getIndexesReader() throws Exception {
        String url = baseUrl + "getIndexes.view";
        InputStream in = Connector.openInputStream(url);
        return new InputStreamReader(in);
    }

    public Reader getMusicDirectoryReader(String path) throws Exception {
        String url = baseUrl + "getMusicDirectory.view";
        InputStream in = Connector.openInputStream(url);
        return new InputStreamReader(in);
    }
}
