/**
 * 
 */
package com.atosorigin.saml.jaas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.logging.Logger;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.callback.ObjectCallback;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.opensaml.SAMLSubjectStatement;

import com.sun.security.auth.UserPrincipal;

/**
 * @author a108600
 *
 */
public class SamlLoginModule implements LoginModule {

	public static final String KEY_STORE_FILE = "com.atosorigin.saml.jaas.keystorefile";
	public static final String KEY_STORE_PASSWORD = "com.atosorigin.saml.jaas.keystorepassword";
	public static final String KEY_STORE_TYPE = "com.atosorigin.saml.jaas.keystoretype";
	public static final String CERTIFICATE_ALIASES = "com.atosorigin.saml.jaas.certificate.aliases";
	
	// May be used by overriding classes
	protected Logger logger;
	
	protected Map<String, ?> options;
	protected CallbackHandler callbackHandler;
	protected Subject subject;

	// Configured certificates
	private Certificate[] certificates;
	
	// From callbacks
	private SAMLAssertion assertion;
	private String username;

	/**
	 * 
	 */
	public SamlLoginModule() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		this.certificates = null;
		this.assertion = null;
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {
		logger.trace("Commiting verified SAML assertion");
		this.subject.getPrincipals().add(new SimplePrincipal(username));
		this.subject.getPublicCredentials().add(assertion);

		return true;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.options = options;
		
		this.logger = Logger.getLogger(SamlLoginModule.class);
		
		String keyStoreType = (String) this.options.get(KEY_STORE_TYPE);
		URL keyStoreFile;
		try {
			keyStoreFile = new URL((String) this.options.get(KEY_STORE_FILE));
		} catch (MalformedURLException e) {
			this.logger.error("Failed to load keystore:" + e.getMessage());
			throw new IllegalArgumentException("Invalid keystore url specified", e);
		}
		String keyStorePw = (String) this.options.get(KEY_STORE_PASSWORD);
		String certificateAliases = (String) this.options.get(CERTIFICATE_ALIASES);

		this.logger.trace("Loading keystore type " + keyStoreType + " from file " + keyStoreFile);
		try {
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			URLConnection keyStoreConn = keyStoreFile.openConnection();
			InputStream keyStoreIS = keyStoreConn.getInputStream();
			
			keyStore.load(keyStoreIS, keyStorePw.toCharArray());
			
			String[] aliases = certificateAliases.split(",");
			this.certificates = new Certificate[aliases.length];
			for (int i = 0 ; i < aliases.length ; i++) {
				this.certificates[i] = keyStore.getCertificate(aliases[i]);
			}
		} catch (KeyStoreException e) {
			this.logger.error("Failed to load keystore:" + e.getMessage());
			throw new IllegalArgumentException("Cannot load keystore", e);
		} catch (NoSuchAlgorithmException e) {
			this.logger.error("Failed to load keystore:" + e.getMessage());
			throw new IllegalArgumentException("Cannot load keystore", e);
		} catch (CertificateException e) {
			this.logger.error("Failed to load keystore:" + e.getMessage());
			throw new IllegalArgumentException("Cannot load keystore", e);
		} catch (IOException e) {
			this.logger.error("Failed to load keystore:" + e.getMessage());
			throw new IllegalArgumentException("Cannot load keystore", e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		Callback callbacks[] = new Callback[2];
		
		callbacks[0] = new NameCallback("Enter username");
		callbacks[1] = new ObjectCallback("Enter saml assertion");

		this.logger.trace("Perfoming SAML Login, executing callbacks");
		try {
			this.callbackHandler.handle(callbacks);
		} catch (IOException e) {
			throw new LoginException("Login failed due to IO problems");
		} catch (UnsupportedCallbackException e) {
			throw new LoginException("One or more callbacks were not supported");
		}
		
		// Reconstruct the SAML Assertion
		String assertionText = new String((char[]) ((ObjectCallback) callbacks[1]).getCredential());

		if (logger.isTraceEnabled()) {
			this.logger.trace("Converting assertion text into an assertion:" + assertionText);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(assertionText.getBytes());
		try {
			this.assertion = new SAMLAssertion(bais);
		} catch (SAMLException e1) {
			throw new LoginException("Cannot reconstruct the SAML assertion");
		}
		
		logger.trace("Searching for a SAMLSubject to retrieve the username");
		// Search for a SAMLSubject
		boolean foundSubject = false;
		this.username = null;
		for (Iterator<?> i = assertion.getStatements() ; i.hasNext(); ) {
			Object o = i.next();
			if (o instanceof SAMLSubjectStatement) {
				foundSubject = true;
				SAMLSubjectStatement subjectStatement = (SAMLSubjectStatement) o;
				this.username = subjectStatement.getSubject().getNameIdentifier().getName();
				break;
			}
		}
		if (!foundSubject) {
			throw new LoginException("No SAMLSubjectStatement found");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Found username from SAMLSubject: " + username);
		}
		
		// Verify the SAMLSubject with the entered username
		String enteredUsername = ((NameCallback) callbacks[0]).getName();
		if (logger.isTraceEnabled()) {
			logger.trace("Verifying saml subject name with name from callback:" + enteredUsername);
		}
		if (enteredUsername == null || !enteredUsername.equals(username)) {
			throw new LoginException("Entered username does not match the SAML Subject statement");
		}
		
		// Verify the SAML assertion with one of the certificates
		logger.trace("Verifiying the SAML assertion using one of the configured certificates");
		boolean verified = false;
		for (Certificate certificate : this.certificates) {
			try {
				assertion.verify(certificate);
				verified = true;
				break;
			} catch (SAMLException e) {
				// No-op, try the next one
			}
		}
		
		if (!verified) {
			throw new LoginException("Could not validate the SAML assertion");
		}
		logger.trace("SAML Assertion validation completed");
		
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		for (Iterator<Principal> i = this.subject.getPrincipals().iterator() ; i.hasNext() ; ) {
			Principal p = i.next();
			if (p instanceof UserPrincipal) {
				i.remove();
			}
		}
		
		for (Iterator<Object> i = this.subject.getPublicCredentials().iterator() ; i.hasNext() ; ) {
			Object o = i.next();
			if (o instanceof SAMLAssertion) {
				i.remove();
			}
		}

		return true;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

}
