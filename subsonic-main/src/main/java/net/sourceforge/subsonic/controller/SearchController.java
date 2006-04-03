package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.util.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.*;
import org.springframework.validation.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;

/**
 * Controller for the search page.
 *
 * @author Sindre Mehus
 */
public class SearchController extends SimpleFormController {

    private static final long MILLIS_IN_DAY = 24 * 3600 * 1000;
    private static final int MAX_HITS = 100;

    private SearchService searchService;
    private SecurityService securityService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new SearchCommand();
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object com, BindException errors)
            throws Exception {
        SearchCommand command = (SearchCommand) com;
        command.setMaxHits(MAX_HITS);
        User user = securityService.getCurrentUser(request);
        command.setDownloadEnabled(user.isDownloadRole());

        String query = getQuery(command);
        long millis = getNewerThanMillis(command);

        if (query != null || millis != 0) {

            if (!searchService.isIndexCreated()) {
                searchService.createIndex();
            }

            if (searchService.isIndexBeingCreated()) {
                command.setIndexBeingCreated(true);
            } else {
                List<MusicFile> result = searchService.heuristicSearch(query, MAX_HITS, command.isArtistAndAlbumIncluded(),
                                                                       command.isArtistAndAlbumIncluded(), command.isTitleIncluded(),
                                                                       new Date(millis));
                String[] criteria = searchService.splitQuery(query);
                command.setMatches(createMatches(result, criteria));
            }
        }

        return new ModelAndView(getSuccessView(), errors.getModel());
    }

    private List<SearchCommand.Match> createMatches(List<MusicFile> result, String[] criteria) {
        List<SearchCommand.Match> matches = new ArrayList<SearchCommand.Match>();
        for (MusicFile musicFile : result) {
            String title = adorn(musicFile.getTitle(), criteria);
            String artistAlbumYear = getArtistAlbumYear(musicFile.getMetaData(), criteria);
            matches.add(new SearchCommand.Match(musicFile, title, artistAlbumYear));
        }
        return matches;
    }

    private String getArtistAlbumYear(MusicFile.MetaData metaData, String[] criteria) {

        String artist = metaData.getArtist();
        String album  = metaData.getAlbum();
        String year   = metaData.getYear();

        if ("".equals(artist)) { artist = null; }
        if ("".equals(album)) { album = null; }
        if ("".equals(year)) { year = null; }

        StringBuffer buf = new StringBuffer();

        if (artist != null) {
            buf.append("<em>").append(adorn(artist, criteria)).append("</em>");
        }

        if (artist != null && album != null) {
            buf.append(" - ");
        }

        if (album != null) {
            buf.append(adorn(album, criteria));
        }

        if (year != null) {
            buf.append(" (").append(StringUtil.toHtml(year)).append(')');
        }

        return buf.toString();
    }

    private String adorn(String text, String[] criteria) {
        text = StringUtil.toHtml(text);
        if (criteria.length == 0) {
            return text;
        }

        StringBuffer regexp = new StringBuffer();
        for (int i = 0; i < criteria.length; i++) {
            regexp.append(StringUtil.toHtml(criteria[i]));
            if (i < criteria.length - 1) {
                regexp.append('|');
            }
        }

        try {
            Pattern pattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            StringBuffer buf = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buf, "<font color='red'>$0</font>");
            }
            matcher.appendTail(buf);

            return buf.toString();
        } catch (Exception x) {
            return text;
        }
    }

    private String getQuery(SearchCommand command) {
        String query = command.getQuery();

        if (query == null || "".equals(query)) {
            return null;
        }
        return query.replaceAll("\"", "&quot;");
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
}
