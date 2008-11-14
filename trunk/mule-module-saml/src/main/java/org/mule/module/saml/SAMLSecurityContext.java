/**
 * 
 */
package org.mule.module.saml;

import org.mule.api.security.Authentication;
import org.mule.api.security.SecurityContext;

/**
 * @author a108600
 *
 */
public class SAMLSecurityContext implements SecurityContext {
	private Authentication authentication;

	/**
	 * @param authentication 
	 * 
	 */
	public SAMLSecurityContext(Authentication authentication) {
		this.authentication = authentication;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.SecurityContext#getAuthentication()
	 */
	public Authentication getAuthentication() {
		return this.authentication;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.SecurityContext#setAuthentication(org.mule.api.security.Authentication)
	 */
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

}
