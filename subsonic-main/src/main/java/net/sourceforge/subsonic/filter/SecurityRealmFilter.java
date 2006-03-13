package net.sourceforge.subsonic.filter;

import net.sourceforge.subsonic.*;
import net.sourceforge.subsonic.service.*;
import org.securityfilter.realm.*;

/**
 * A filter which implements authentication.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/11/13 15:28:06 $
 */
public class SecurityRealmFilter extends SimpleSecurityRealmBase {

    private static final Logger LOG = Logger.getLogger(SecurityRealmFilter.class);

    /**
    * Authenticates a user.
    * @param username The username.
    * @param password The plain text password, as entered by the user.
    * @return Whether the user is authenticated.
    */
    public boolean booleanAuthenticate(String username, String password) {
        boolean ok = ServiceFactory.getSecurityService().authenticate(username, password);
        if (ok) {
            LOG.info("User " + username + " logged in successfully.");
        } else {
            LOG.info("User " + username + " failed to log in.");
        }
        return ok;
    }

    /**
     * Tests for role membership.
     * @param username The name of the user.
     * @param role Name of a role to test for membership.
     * @return Whether the user is in the role.
     */
    public boolean isUserInRole(String username, String role) {
        boolean ok = ServiceFactory.getSecurityService().authorize(username, role);
        if (!ok) {
            LOG.info("User " + username + " not authorized for role '" + role + "'.");
        }
        return ok;
    }
}