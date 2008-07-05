package net.sourceforge.subsonic.security;

import net.sourceforge.subsonic.Logger;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Performs authentication based on credentials being present in the HTTP request parameters.
 * <p/>
 * The username should be set in parameter "u", and the password should be set in parameter "p".
 *
 * @author Sindre Mehus
 */
public class RequestParameterProcessingFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(RequestParameterProcessingFilter.class);

    private ProviderManager authenticationManager;

    /**
     * {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Can only process HttpServletRequest");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Can only process HttpServletResponse");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String username = httpRequest.getParameter("u");
        String password = httpRequest.getParameter("p");

        try {
            if (username == null || password == null) {
                throw new BadCredentialsException("Missing username and/or password");
            }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authResult = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            LOG.debug("Successfully authenticated user " + username);

        } catch (AuthenticationException failed) {

            // Authentication failed
            LOG.info("Authentication failed for user " + username);

            SecurityContextHolder.getContext().setAuthentication(null);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong username/password");

            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
    }

    public void setAuthenticationManager(ProviderManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
