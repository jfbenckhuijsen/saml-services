/**
 * 
 */
package org.mule.module.saml;

import org.mule.api.MuleEvent;
import org.mule.api.security.Authentication;
import org.mule.api.security.CryptoFailureException;
import org.mule.api.security.SecurityContext;
import org.mule.api.security.SecurityException;
import org.mule.api.security.SecurityProviderNotFoundException;
import org.mule.api.security.UnauthorisedException;
import org.mule.api.security.UnknownAuthenticationTypeException;
import org.mule.security.AbstractEndpointSecurityFilter;
import org.opensaml.SAMLAssertion;

/**
 * @author a108600
 * 
 */
public abstract class AbstractSamlSecurityFilter extends
		AbstractEndpointSecurityFilter {
	private String certificateAlias;

	/**
	 * 
	 */
	public AbstractSamlSecurityFilter() {
		super();
	}

	/**
	 * @return the certificateAlias
	 */
	public String getCertificateAlias() {
		return certificateAlias;
	}

	/**
	 * @param certificateAlias
	 *            the certificateAlias to set
	 */
	public void setCertificateAlias(String certificateAlias) {
		this.certificateAlias = certificateAlias;
	}

	/**
	 * @param assertion
	 * @throws SecurityProviderNotFoundException
	 * @throws SecurityException
	 * @throws UnknownAuthenticationTypeException
	 */
	protected void doAuthenticateInbound(MuleEvent event,
			SAMLAssertion assertion) throws SecurityException,
			SecurityProviderNotFoundException,
			UnknownAuthenticationTypeException {
		SAMLAuthenticationAdapter samlAuthentication = new SAMLAuthenticationAdapter(
				assertion, this.certificateAlias);

		Authentication authResult = securityManager
				.authenticate(samlAuthentication);

		SecurityContext context = securityManager
				.createSecurityContext(authResult);
		context.setAuthentication(authResult);
		event.getSession().setSecurityContext(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.security.AbstractEndpointSecurityFilter#authenticateOutbound
	 * (org.mule.api.MuleEvent)
	 */
	@Override
	protected void authenticateOutbound(MuleEvent event)
			throws SecurityException, SecurityProviderNotFoundException,
			CryptoFailureException {

		if (event.getSession().getSecurityContext() == null) {
			if (isAuthenticate()) {
				throw new UnauthorisedException(event.getMessage(), event
						.getSession().getSecurityContext(),
						event.getEndpoint(), this);
			} else {
				return;
			}
		}

		Authentication auth = event.getSession().getSecurityContext()
				.getAuthentication();
		if (isAuthenticate()) {
			auth = getSecurityManager().authenticate(auth);
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication success: " + auth.toString());
			}
		}

		doAuthenticateOutbound(event, (SAMLAuthenticationAdapter) auth);
	}

	protected abstract void doAuthenticateOutbound(MuleEvent event,
			SAMLAuthenticationAdapter auth) throws SecurityException;

}
