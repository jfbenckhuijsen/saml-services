/**
 * 
 */
package org.mule.module.saml;

import org.mule.api.security.Authentication;
import org.mule.api.security.SecurityContext;
import org.mule.api.security.SecurityContextFactory;

/**
 * @author a108600
 *
 */
public class SAMLSecurityContextFactory implements SecurityContextFactory {

	/**
	 * 
	 */
	public SAMLSecurityContextFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.SecurityContextFactory#create(org.mule.api.security.Authentication)
	 */
	public SecurityContext create(Authentication authentication) {
		return new SAMLSecurityContext(authentication);
	}

}
