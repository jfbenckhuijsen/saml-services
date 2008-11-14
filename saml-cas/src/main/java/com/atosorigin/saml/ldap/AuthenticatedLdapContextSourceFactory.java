/**
 * 
 */
package com.atosorigin.saml.ldap;

import org.jasig.cas.adaptors.ldap.util.AuthenticatedLdapContextSource;
import org.springframework.ldap.core.support.LdapContextSource;


/**
 * @author a108600
 *
 */
public class AuthenticatedLdapContextSourceFactory extends
		LdapContextSourceFactory {

	/**
	 * 
	 */
	public AuthenticatedLdapContextSourceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.ldap.LdapContextSourceFactory#createContextSource()
	 */
	@Override
	protected LdapContextSource createContextSource() {
		return new AuthenticatedLdapContextSource();
	}
	
	
}
