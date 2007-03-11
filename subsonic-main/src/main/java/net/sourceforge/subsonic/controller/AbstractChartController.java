package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.support.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.ui.context.*;

import javax.servlet.http.*;
import java.awt.*;
import java.util.*;

/**
 * Abstract super class for controllers which generate charts.
 *
 * @author Sindre Mehus
 */
public abstract class AbstractChartController implements Controller {

    /**
     * Returns the chart background color for the current theme.
     * @param request The servlet request.
     * @return The chart background color.
     */
    protected Color getBackground(HttpServletRequest request) {
        return getColor("backgroundColor", request);
    }

    /**
     * Returns the chart foreground color for the current theme.
     * @param request The servlet request.
     * @return The chart foreground color.
     */
    protected Color getForeground(HttpServletRequest request) {
        return getColor("textColor", request);
    }

    private Color getColor(String code, HttpServletRequest request) {
        Theme theme = RequestContextUtils.getTheme(request);
        Locale locale = RequestContextUtils.getLocale(request);
        String colorHex = theme.getMessageSource().getMessage(code, new Object[0], locale);
        return new Color(Integer.parseInt(colorHex, 16));
    }
}
