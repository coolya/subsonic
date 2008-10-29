package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller for the page used to administrate the search index.
 *
 * @author Sindre Mehus
 */
public class SearchSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SearchService searchService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        SearchSettingsCommand command = new SearchSettingsCommand();

        if (request.getParameter("update") != null) {
            searchService.createIndex();
            command.setCreatingIndex(true);
        }

        command.setInterval("" + settingsService.getIndexCreationInterval());
        command.setHour("" + settingsService.getIndexCreationHour());
        command.setBrand(settingsService.getBrand());

        return command;
    }

    protected void doSubmitAction(Object comm) throws Exception {
        SearchSettingsCommand command = (SearchSettingsCommand) comm;

        settingsService.setIndexCreationInterval(Integer.parseInt(command.getInterval()));
        settingsService.setIndexCreationHour(Integer.parseInt(command.getHour()));
        settingsService.save();

        searchService.schedule();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
