/**
 * 
 */
package org.mule.module.saml.ejb;

import java.io.IOException;
import java.security.Security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

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
import org.opensaml.SAMLSubject;
import org.springframework.core.io.Resource;

/**
 * @author a108600
 *
 */
public class EjbSAMLSecurityFilter extends AbstractSamlSecurityFilter {
	private Resource loginConfigUrl;

	/**
	 * 
	 */
	public EjbSAMLSecurityFilter() {
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
		throw new UnsupportedOperationException("Inboud authentication of EJB requests is not supported");
	}

	/* (non-Javadoc)
	 * @see org.mule.module.saml.AbstractSamlSecurityFilter#doAuthenticateOutbound(org.mule.api.MuleEvent, org.mule.module.saml.SAMLAuthenticationAdapter)
	 */
	@Override
	protected void doAuthenticateOutbound(final MuleEvent event,
			final SAMLAuthenticationAdapter auth) throws SecurityException {
		
		SAMLSubject subject = (SAMLSubject) auth.getPrincipal();
		final String username = subject.getNameIdentifier().getName();
		final char[] password = auth.getCredentials().toString().toCharArray();
		
		boolean alreadySet = false;

        int n = 1;
        String prefix = "login.config.url.";
        String existing = null;

        while ((existing = Security.getProperty(prefix + n)) != null)
        {
            alreadySet = existing.equals(loginConfigUrl);

            if (alreadySet)
            {
                break;
            }
            n++;
        }

        if (!alreadySet)
        {
            String key = prefix + n;
            try {
				Security.setProperty(key, loginConfigUrl.getURL().toExternalForm());
			} catch (IOException e) {
				throw new IllegalStateException("Invalid login url specified", e);
			}
        }
		
		try {
			LoginContext context = new LoginContext("saml", new CallbackHandler(){

				public void handle(Callback[] callbacks)
						throws IOException, UnsupportedCallbackException {
					for (Callback callback : callbacks) {
						if (callback instanceof NameCallback) {
							NameCallback nameCallback = (NameCallback) callback;
							nameCallback.setName(username);
						} else if (callback instanceof PasswordCallback) {
							PasswordCallback passwordCallback = (PasswordCallback) callback;
							passwordCallback.setPassword(password);
						} else {
							throw new UnsupportedCallbackException(callback);
						}
					}
				}
			});
			
			context.login();
		} catch (LoginException e) {
			throw new UnauthorisedException(CoreMessages.authFailedForUser(username),event.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.mule.security.AbstractEndpointSecurityFilter#doInitialise()
	 */
	@Override
	protected void doInitialise() throws InitialisationException {
		// no op
	}

	/**
	 * @return the loginConfigUrl
	 */
	public Resource getLoginConfigUrl() {
		return loginConfigUrl;
	}

	/**
	 * @param loginConfigUrl the loginConfigUrl to set
	 */
	public void setLoginConfigUrl(Resource loginConfigUrl) {
		this.loginConfigUrl = loginConfigUrl;
	}

}
