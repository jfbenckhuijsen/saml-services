/**
 * 
 */
package org.mule.module.saml.jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mule.api.MuleEvent;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.security.CryptoFailureException;
import org.mule.api.security.EncryptionStrategyNotFoundException;
import org.mule.api.security.SecurityException;
import org.mule.api.security.SecurityProviderNotFoundException;
import org.mule.api.security.UnauthorisedException;
import org.mule.api.security.UnknownAuthenticationTypeException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.saml.AbstractSamlSecurityFilter;
import org.mule.module.saml.SAMLAuthenticationAdapter;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;

/**
 * @author a108600
 * 
 */
public class JmsSAMLSecurityFilter extends AbstractSamlSecurityFilter {
	/**
	 * 
	 */
	public JmsSAMLSecurityFilter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.security.AbstractEndpointSecurityFilter#authenticateInbound(
	 * org.mule.api.MuleEvent)
	 */
	@Override
	protected void authenticateInbound(MuleEvent event)
			throws SecurityException, CryptoFailureException,
			SecurityProviderNotFoundException,
			EncryptionStrategyNotFoundException,
			UnknownAuthenticationTypeException {
		
		String assertionText = (String) event.getMessage().getProperty("SAML_ASSERTION");
		SAMLAssertion assertion;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(assertionText.getBytes());
			assertion = new SAMLAssertion(bais);
		} catch (SAMLException e) {
			throw new UnauthorisedException(CoreMessages.authDeniedOnEndpoint(event.getEndpoint().getEndpointURI()));
		}
		doAuthenticateInbound(event, assertion);
	}

	@Override
	protected void doAuthenticateOutbound(MuleEvent event,
			SAMLAuthenticationAdapter auth) throws SecurityException {
		
		SAMLAssertion assertion = (SAMLAssertion) auth.getCredentials();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			assertion.toStream(baos);
		} catch (IOException e) {
			throw new UnauthorisedException(CoreMessages.authDeniedOnEndpoint(event.getEndpoint().getEndpointURI()));
		} catch (SAMLException e) {
			throw new UnauthorisedException(CoreMessages.authDeniedOnEndpoint(event.getEndpoint().getEndpointURI()));
		}
		event.getMessage().setStringProperty("SAML_ASSERTION", baos.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.security.AbstractEndpointSecurityFilter#doInitialise()
	 */
	@Override
	protected void doInitialise() throws InitialisationException {
		// no op
	}

}
