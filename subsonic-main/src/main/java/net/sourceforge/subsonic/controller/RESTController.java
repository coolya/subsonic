/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicIndex;
import net.sourceforge.subsonic.domain.Player;
import net.sourceforge.subsonic.restapi.Artist;
import net.sourceforge.subsonic.restapi.Child;
import net.sourceforge.subsonic.restapi.Directory;
import net.sourceforge.subsonic.restapi.Index;
import net.sourceforge.subsonic.restapi.Indexes;
import net.sourceforge.subsonic.restapi.Response;
import net.sourceforge.subsonic.restapi.ResponseStatus;
import net.sourceforge.subsonic.restapi.ObjectFactory;
import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.MusicIndexService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.service.TranscodingService;
import net.sourceforge.subsonic.util.StringUtil;
import net.sourceforge.subsonic.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Multi-controller used for the REST API.
 *
 * @author Sindre Mehus
 */
public class RESTController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(RESTController.class);

    private SettingsService settingsService;
    private PlayerService playerService;
    private MusicFileService musicFileService;
    private MusicIndexService musicIndexService;
    private TranscodingService transcodingService;
    private JAXBContext jaxbContext;
    private final String schemaVersion;

    public RESTController() {
        try {
            jaxbContext = JAXBContext.newInstance("net.sourceforge.subsonic.restapi");
        } catch (JAXBException x) {
            LOG.warn("Failed to initalize JAXB context.", x);
        }
        schemaVersion = "1.0.0"; // TODO: Read directly from xsd
    }

    public ModelAndView getIndexes(HttpServletRequest request, HttpServletResponse servletResponse) throws Exception {

        Indexes indexes = new Indexes();
//            indexes.setLastModified(); TODO
        SortedMap<MusicIndex, SortedSet<MusicIndex.Artist>> indexedArtists = musicIndexService.getIndexedArtists(settingsService.getAllMusicFolders());

        for (Map.Entry<MusicIndex, SortedSet<MusicIndex.Artist>> entry : indexedArtists.entrySet()) {
            Index index = new Index();
            index.setName(entry.getKey().getIndex());
            indexes.getIndex().add(index);
            for (MusicIndex.Artist artist : entry.getValue()) {
                for (MusicFile musicFile : artist.getMusicFiles()) {
                    if (musicFile.isDirectory()) {
                        Artist a = new Artist();
                        a.setId(StringUtil.utf8HexEncode(musicFile.getPath()));
                        a.setName(artist.getName());
                        index.getArtist().add(a);
                    }
                }
            }
        }

        Response response = new Response();
        response.setIndexes(indexes);
        response.setStatus(ResponseStatus.OK);
        marshal(servletResponse, response);

        return null;
    }

    public ModelAndView getMusicDirectory(HttpServletRequest request, HttpServletResponse servletResponse) throws Exception {
        Player player = playerService.getPlayer(request, servletResponse);

        String path = StringUtil.utf8HexDecode(request.getParameter("id"));
        MusicFile dir = musicFileService.getMusicFile(path);
        // TODO: Handle non-existing dir.

        Directory directory = new Directory();
        directory.setId(StringUtil.utf8HexEncode(dir.getPath()));
        directory.setName(dir.getName());

        for (MusicFile musicFile : dir.getChildren(true, true)) {

            Child child = new Child();
            directory.getChild().add(child);

            child.setId(StringUtil.utf8HexEncode(musicFile.getPath()));
            child.setTitle(musicFile.getTitle());
            child.setIsDir(musicFile.isDirectory());

            if (musicFile.isFile()) {
                child.setAlbum(musicFile.getMetaData().getAlbum());
                child.setArtist(musicFile.getMetaData().getArtist());
                child.setSize(musicFile.length());
                String suffix = musicFile.getSuffix();
                child.setSuffix(suffix);
                child.setContentType(StringUtil.getMimeType(suffix));

                if (transcodingService.isTranscodingRequired(musicFile, player)) {
                    String transcodedSuffix = transcodingService.getSuffix(player, musicFile);
                    child.setTranscodedSuffix(transcodedSuffix);
                    child.setTranscodedContentType(StringUtil.getMimeType(transcodedSuffix));
                }
            }
        }

        Response response = new Response();
        response.setDirectory(directory);
        response.setStatus(ResponseStatus.OK);
        marshal(servletResponse, response);

        return null;
    }

    private void marshal(HttpServletResponse servletResponse, Response response) throws Exception {
        servletResponse.setContentType("text/xml");
        servletResponse.setCharacterEncoding(StringUtil.ENCODING_UTF8);

        response.setVersion(schemaVersion);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, StringUtil.ENCODING_UTF8);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(new ObjectFactory().createSubsonicResponse(response), servletResponse.getOutputStream());

    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}