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
package net.sourceforge.subsonic.security;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.util.XMLBuilder;
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
            sendErrorXml(httpResponse);

            return;
        }

        chain.doFilter(request, response);
    }

    private void sendErrorXml(HttpServletResponse httpResponse) throws IOException {
        XMLBuilder builder = new XMLBuilder();

        builder.preamble("UTF-8");
        builder.addClosed("error", "Wrong username/password.");

        httpResponse.getWriter().write(builder.toString());
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
