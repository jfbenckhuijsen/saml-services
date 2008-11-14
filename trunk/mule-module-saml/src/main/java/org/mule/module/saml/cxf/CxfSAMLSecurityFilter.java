/**
 * 
 */
package org.mule.module.saml.cxf;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.cxf.message.Message;
import org.mule.api.MuleEvent;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.security.CryptoFailureException;
import org.mule.api.security.EncryptionStrategyNotFoundException;
import org.mule.api.security.SecurityException;
import org.mule.api.security.SecurityProviderNotFoundException;
import org.mule.api.security.UnknownAuthenticationTypeException;
import org.mule.api.transport.MessageAdapter;
import org.mule.module.saml.AbstractSamlSecurityFilter;
import org.mule.module.saml.SAMLAuthenticationAdapter;
import org.opensaml.SAMLAssertion;

/**
 * @author a108600
 *
 */
public class CxfSAMLSecurityFilter extends AbstractSamlSecurityFilter {

	/**
	 * 
	 */
	public CxfSAMLSecurityFilter() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.mule.security.AbstractEndpointSecurityFilter#authenticateInbound(org.mule.api.MuleEvent)
	 */
	@Override
	protected void authenticateInbound(MuleEvent event)
			throws SecurityException, CryptoFailureException,
			SecurityProviderNotFoundException,
			EncryptionStrategyNotFoundException,
			UnknownAuthenticationTypeException {
		
		MessageAdapter adapter = event.getMessage().getAdapter();
		Object payload;
		try {
			Field field = adapter.getClass().getDeclaredField("payload");
			field.setAccessible(true);
			payload = field.get(adapter);
		} catch (java.lang.SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		if (payload instanceof Message) {
			Message message = (Message) payload;
			
			Collection<SAMLAssertion> assertions = SAMLCxfUtil.getAssertions(message);
			
			doAuthenticateInbound(event, assertions.iterator().next());
		}
	}

	/* (non-Javadoc)
	 * @see org.mule.module.saml.AbstractSamlSecurityFilter#doAuthenticateOutbound(org.mule.api.MuleEvent, org.mule.module.saml.SAMLAuthenticationAdapter)
	 */
	@Override
	protected void doAuthenticateOutbound(MuleEvent event,
			SAMLAuthenticationAdapter auth) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.mule.security.AbstractEndpointSecurityFilter#doInitialise()
	 */
	@Override
	protected void doInitialise() throws InitialisationException {
		// no op
	}

}
