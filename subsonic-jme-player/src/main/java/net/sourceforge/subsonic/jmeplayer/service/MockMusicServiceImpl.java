/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.service;

import net.sourceforge.subsonic.jmeplayer.domain.Artist;
import net.sourceforge.subsonic.jmeplayer.domain.ArtistIndex;
import net.sourceforge.subsonic.jmeplayer.domain.MusicDirectory;

/**
 * @author Sindre Mehus
 */
public class MockMusicServiceImpl implements MusicService {


    public ArtistIndex[] getArtistIndexes() {
        return new ArtistIndex[]{
                new ArtistIndex("A", new Artist[]{new Artist("A-Ha", "/music/1"),
                                                  new Artist("Accept", "/music/2"),
                                                  new Artist("Anja Garbarek", "/music/3")}),
                new ArtistIndex("B", new Artist[]{new Artist("Bad Liver", "/music/4"),
                                                  new Artist("Bauhaus", "/music/5"),
                                                  new Artist("The Beatles", "/music/6")}),
        };
    }

    public MusicDirectory getMusicDirectory(String path) {
        String name = "Album " + path.substring(path.indexOf('/'));
        String parentPath = path.substring(0, path.lastIndexOf('/'));

        MusicDirectory.Entry[] children = new MusicDirectory.Entry[]{
                new MusicDirectory.Entry("http - wav", path + "/pcm.wav", false, "http://www.nch.com.au/acm/8k16bitpcm.wav", "audio/x-wav"),
                new MusicDirectory.Entry("http - mid", path + "/joy.mid", false, "http://people.nnu.edu/WDHUGHES/Joy.MID", "audio/midi"),
                new MusicDirectory.Entry("http - mp3", path + "/bach.mp3", false, "http://www.sotone.com/samples/!2-A.Brain_Bach.4th.mvmt.mp3", "audio/mpeg"),
                new MusicDirectory.Entry("bark.wav", path + "/bark.wav", false, "resource:/audio/bark.wav", "audio/x-wav"),
                new MusicDirectory.Entry("pattern.mid", path + "/pattern.mid", false, "resource:/audio/pattern.mid", "audio/midi")
        };

        return new MusicDirectory(name, path, parentPath, children);
    }
}