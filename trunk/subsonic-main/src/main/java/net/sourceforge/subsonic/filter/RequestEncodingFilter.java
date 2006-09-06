package net.sourceforge.subsonic.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Configurable filter for setting the character encoding to use for the HTTP request.
 * Typically used to set UTF-8 encoding when reading request parameters with non-Latin
 * content.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2006/03/01 16:58:08 $
 */
public class RequestEncodingFilter implements Filter {

    private String encoding;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        request.setCharacterEncoding(encoding);

        // Pass the request/response on
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
        encoding = filterConfig.getInitParameter("encoding");
    }

    public void destroy() {
        encoding = null;
    }

}
