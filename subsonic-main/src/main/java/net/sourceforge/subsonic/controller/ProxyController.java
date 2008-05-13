package net.sourceforge.subsonic.controller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * A proxy for external HTTP requests.
 *
 * @author Sindre Mehus
 */
public class ProxyController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = ServletRequestUtils.getRequiredStringParameter(request, "url");
        HttpMethod method = new GetMethod(url);
        HttpClient client = new HttpClient();

        InputStream in = null;
        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                response.sendError(statusCode);
            } else {
                in = method.getResponseBodyAsStream();
                IOUtils.copy(in, response.getOutputStream());
            }

        } finally {
            IOUtils.closeQuietly(in);
            method.releaseConnection();
        }

        return null;
    }
}