package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller for the page which forwards to allmusic.com.
 *
 * @author Sindre Mehus
 */
public class AllmusicController extends ParameterizableViewController {

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("album", request.getParameter("album"));
        return result;
    }
}
