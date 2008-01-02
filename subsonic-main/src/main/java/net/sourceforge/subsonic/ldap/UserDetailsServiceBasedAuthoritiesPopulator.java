package net.sourceforge.subsonic.ldap;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.ldap.LdapDataAccessException;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;

/**
 * An {@link LdapAuthoritiesPopulator} that retrieves the roles from the
 * database using the {@link UserDetailsService} instead of retrieving the roles
 * from LDAP. An instance of this class can be configured for the
 * {@link org.acegisecurity.providers.ldap.LdapAuthenticationProvider} when
 * authentication should be done using LDAP and authorization using the
 * information stored in the database.
 *
 * @author Thomas M. Hofmann
 */
public class UserDetailsServiceBasedAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private UserDetailsService userDetailsService;

    public GrantedAuthority[] getGrantedAuthorities(LdapUserDetails userDetails) throws LdapDataAccessException {
        UserDetails details = userDetailsService.loadUserByUsername(userDetails.getUsername());
        return details.getAuthorities();
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}