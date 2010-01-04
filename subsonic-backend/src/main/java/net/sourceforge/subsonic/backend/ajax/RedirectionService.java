package net.sourceforge.subsonic.backend.ajax;

import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import net.sourceforge.subsonic.backend.dao.RedirectionDao;
import net.sourceforge.subsonic.backend.domain.Redirection;

/**
 * Provides AJAX-based redirection services.
 *
 * @author Sindre Mehus
 * @version $Id$
 */
public class RedirectionService {

    private RedirectionDao redirectionDao;

    public String testRedirection(String redirectFrom) {

        Redirection redirection = redirectionDao.getRedirection(redirectFrom);
        String webAddress = redirectFrom + ".gosubsonic.com";
        if (redirection == null) {
            return "Web address " + webAddress + " not registered.";
        }

        if (redirection.getTrialExpires() != null && redirection.getTrialExpires().after(new Date())) {
            return "Trial period expired. Please donate to activate web address.";
        }

        String url = redirection.getRedirectTo();
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                return webAddress + " responded successfully.";
            } else {
                return webAddress + " returned HTTP error code " + method.getStatusCode() + method.getStatusText();
            }

        } catch (Throwable x) {
            return webAddress + " is registered, but could not connect to it: " + x.getMessage() + " (" + x.getClass().getSimpleName() + ")";
        } finally {
            method.releaseConnection();
        }
    }

    public void setRedirectionDao(RedirectionDao redirectionDao) {
        this.redirectionDao = redirectionDao;
    }
}
