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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.command.SearchCommand;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.SearchCriteria;
import net.sourceforge.subsonic.domain.SearchResult;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.domain.UserSettings;
import net.sourceforge.subsonic.service.SearchService;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import net.sourceforge.subsonic.util.StringUtil;

/**
 * Controller for the search page.
 *
 * @author Sindre Mehus
 */
public class SearchController extends SimpleFormController {

    private static final long MILLIS_IN_DAY = 24 * 3600 * 1000;
    private static final int HITS_PER_PAGE = 25;
    private static final Logger LOG = Logger.getLogger(SearchService.class);

    private SearchService searchService;
    private SecurityService securityService;
    private SettingsService settingsService;

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        SearchCommand command = new SearchCommand();
        command.setOffset(0);
        command.setCount(HITS_PER_PAGE);
        return command;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object com, BindException errors)
            throws Exception {
        SearchCommand command = (SearchCommand) com;

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        command.setUser(user);
        command.setPartyModeEnabled(userSettings.isPartyModeEnabled());

        String any = StringUtils.trimToNull(command.getAny());
        String title = StringUtils.trimToNull(command.getTitle());
        String album = StringUtils.trimToNull(command.getAlbum());
        String artist = StringUtils.trimToNull(command.getArtist());
        long millis = getNewerThanMillis(command);

        if (any != null || title != null || album != null || artist != null || millis != 0) {

            if (searchService.isIndexBeingCreated()) {
                command.setIndexBeingCreated(true);
            } else {

                SearchCriteria criteria = new SearchCriteria();
                criteria.setOffset(command.getOffset());
                criteria.setCount(HITS_PER_PAGE);
                criteria.setAny(any);
                criteria.setTitle(title);
                criteria.setAlbum(album);
                criteria.setArtist(artist);
                criteria.setNewerThan(new Date(millis));

                SearchResult result = searchService.search(criteria);
                command.setMatches(createMatches(criteria, result));
                command.setHitsPerPage(HITS_PER_PAGE);
                command.setFirstHit(criteria.getOffset() + 1);
                command.setLastHit(Math.min(criteria.getOffset() + HITS_PER_PAGE, result.getTotalHits()));
                command.setTotalHits(result.getTotalHits());
            }
        }

        return new ModelAndView(getSuccessView(), errors.getModel());
    }

    private List<SearchCommand.Match> createMatches(SearchCriteria criteria, SearchResult result) {
        List<SearchCommand.Match> matches = new ArrayList<SearchCommand.Match>();
        for (MusicFile musicFile : result.getMusicFiles()) {

            String title = adorn(musicFile.getTitle(), criteria.getTitle(), criteria.getAny());
            String album = adorn(musicFile.getMetaData().getAlbum(), criteria.getAlbum(), criteria.getAny());
            String artist = adorn(musicFile.getMetaData().getArtist(), criteria.getArtist(), criteria.getAny());

            matches.add(new SearchCommand.Match(musicFile, title, album, artist));
        }
        return matches;
    }

    private String adorn(String text, String... terms) {

        StringBuilder regexp = new StringBuilder();
        for (String term : terms) {
            term = StringUtils.trimToNull(term);
            if (term != null) {
                if (regexp.length() > 0) {
                    regexp.append("|");
                }
                regexp.append(term);
            }
        }

        text = StringUtil.toHtml(text);
        if (regexp.length() == 0) {
            return text;
        }

        try {
            Pattern pattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            StringBuffer buf = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buf, "<b>$0</b>");
            }
            matcher.appendTail(buf);

            return buf.toString();
        } catch (Exception x) {
            LOG.warn("Failed to adorn text '" + text + "' with term '" + regexp + "'.");
            return text;
        }
    }

    private long getNewerThanMillis(SearchCommand command) {
        String time = command.getTime();
        long now = System.currentTimeMillis();

        if ("1d".equals(time)) {
            return now - MILLIS_IN_DAY;
        } else if ("1w".equals(time)) {
            return now - 7L * MILLIS_IN_DAY;
        } else if ("2w".equals(time)) {
            return now - 14L * MILLIS_IN_DAY;
        } else if ("1m".equals(time)) {
            return now - 30L * MILLIS_IN_DAY;
        } else if ("3m".equals(time)) {
            return now - 90L * MILLIS_IN_DAY;
        } else if ("6m".equals(time)) {
            return now - 180L * MILLIS_IN_DAY;
        } else if ("1y".equals(time)) {
            return now - 365L * MILLIS_IN_DAY;
        } else {
            return 0L;
        }
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
