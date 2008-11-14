/**
 * 
 */
package org.mule.module.saml;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.signature.XMLSignature;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.security.Authentication;
import org.mule.api.security.SecurityContext;
import org.mule.api.security.SecurityContextFactory;
import org.mule.api.security.SecurityException;
import org.mule.api.security.SecurityProvider;
import org.mule.api.security.UnauthorisedException;
import org.mule.api.security.UnknownAuthenticationTypeException;
import org.mule.config.i18n.CoreMessages;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.opensaml.SAMLSubject;

/**
 * @author a108600
 * 
 */
public class SAMLSecurityProvider implements SecurityProvider {

    protected static final Log logger = LogFactory.getLog(SAMLSecurityProvider.class);

    private SecurityContextFactory factory;
	private String name;
	private SamlKeyInfo keyInfo;

	private KeyStore keyStore;
	private Key issuerPK;
	private X509Certificate[] issuerCerts;

	/**
	 * 
	 */
	public SAMLSecurityProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.api.security.SecurityProvider#authenticate(org.mule.api.security
	 * .Authentication)
	 */
	public Authentication authenticate(Authentication authentication)
			throws SecurityException {

		logger.debug("Starting SAML authentication");
		
		if (!supports(authentication.getClass())) {
			throw new UnauthorisedException(CoreMessages
					.authTypeNotRecognised(authentication.getClass()
							.getCanonicalName()));
		}
		SAMLAuthenticationAdapter samlAuth = (SAMLAuthenticationAdapter) authentication;

		logger.debug("Verifying signature of the SAML assertion");
		// Check signature
		SAMLAssertion assertion = (SAMLAssertion) samlAuth.getCredentials();
		Certificate certificate;
		try {
			certificate = keyStore.getCertificate(samlAuth
					.getCertificateAlias());
			assertion.verify(certificate);
		} catch (Exception e) {
			SAMLSubject subject = (SAMLSubject) samlAuth.getPrincipal();
			throw new UnauthorisedException(CoreMessages
					.authFailedForUser(subject.getNameIdentifier()));
		}

		// Check date
		logger.debug("Checking date validity of the assertion");
		Date now = new Date();
		if (now.before(assertion.getNotBefore())
				|| now.after(assertion.getNotOnOrAfter())
				|| now.equals(assertion.getNotOnOrAfter())) {
			SAMLSubject subject = (SAMLSubject) samlAuth.getPrincipal();
			throw new UnauthorisedException(CoreMessages
					.authFailedForUser(subject.getNameIdentifier()));
		}

		logger.debug("Creating result assertion, resigned with my private key");
		// Create the result assertion and resign it
		try {
			SAMLAssertion resultAssertion = (SAMLAssertion) assertion.clone();

			String sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_RSA;
			String pubKeyAlgo = this.issuerCerts[0].getPublicKey()
					.getAlgorithm();
			if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
				sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_DSA;
			}

			resultAssertion.sign(sigAlgo, issuerPK, Arrays.asList(issuerCerts));
			if (logger.isDebugEnabled()) {
				logger.debug("Result assertion created: " + resultAssertion.toString());
			}
			
			SAMLAuthenticationAdapter result = new SAMLAuthenticationAdapter(
					resultAssertion, this.keyInfo.getSignKeyAlias());
			result.setAuthenticated(true);
			
			return result;
		} catch (CloneNotSupportedException e) {
			SAMLSubject subject = (SAMLSubject) samlAuth.getPrincipal();
			throw new UnauthorisedException(CoreMessages
					.authFailedForUser(subject.getNameIdentifier()));
		} catch (SAMLException e) {
			SAMLSubject subject = (SAMLSubject) samlAuth.getPrincipal();
			throw new UnauthorisedException(CoreMessages
					.authFailedForUser(subject.getNameIdentifier()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.api.security.SecurityProvider#createSecurityContext(org.mule
	 * .api.security.Authentication)
	 */
	public SecurityContext createSecurityContext(Authentication authentication)
			throws UnknownAuthenticationTypeException {

		return factory.create(authentication);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.api.security.SecurityProvider#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.api.security.SecurityProvider#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.api.security.SecurityProvider#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return (SAMLAuthenticationAdapter.class.isAssignableFrom(clazz));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.api.lifecycle.Initialisable#initialise()
	 */
	public void initialise() throws InitialisationException {
		this.factory = new SAMLSecurityContextFactory();
		
		ClassLoader classLoader = SAMLSecurityProvider.class.getClassLoader();
		
		try {
			this.keyStore = KeyStore.getInstance(keyInfo.getKeyStoreType());
			this.keyStore.load(classLoader.getResourceAsStream(keyInfo.getKeyStoreFile()), keyInfo.getKeyStorePassword().toCharArray());

			this.issuerPK = keyStore.getKey(keyInfo.getSignKeyAlias(), keyInfo.getSignKeyPassword().toCharArray());
			if (!(issuerPK instanceof PrivateKey)) {
				throw new InitialisationException(CoreMessages.cryptoFailure(), this);
			}

		} catch (KeyStoreException e1) {
			throw new InitialisationException(CoreMessages.cryptoFailure(), this);
		} catch (NoSuchAlgorithmException e) {
			throw new InitialisationException(CoreMessages.cryptoFailure(), this);
		} catch (CertificateException e) {
			throw new InitialisationException(CoreMessages.cryptoFailure(), this);
		} catch (IOException e) {
			throw new InitialisationException(CoreMessages.cryptoFailure(), this);
		} catch (UnrecoverableKeyException e) {
			throw new InitialisationException(CoreMessages.cryptoFailure(), this);
		}

		Certificate[] certs = null;
        Certificate cert = null;
        try {
            //There's a chance that there can only be a set of trust stores
            certs = keyStore.getCertificateChain(keyInfo.getSignKeyAlias());
            if (certs == null || certs.length == 0) {
                // no cert chain, so lets check if getCertificate gives us a
                // result.
                cert = keyStore.getCertificate(keyInfo.getSignKeyAlias());
            }

            if (cert != null) {
                certs = new Certificate[]{cert};
            } else if (certs == null) {
                // At this pont we don't have certs or a cert
                
            }
        } catch (KeyStoreException e) {
        	throw new InitialisationException(CoreMessages.cryptoFailure(), this);
        }

        if (certs != null) {
	        X509Certificate[] x509certs = new X509Certificate[certs.length];
	        for (int i = 0; i < certs.length; i++) {
	            x509certs[i] = (X509Certificate) certs[i];
	        }
	        
	        this.issuerCerts = x509certs;
        }
	}

	/**
	 * @return the keyInfo
	 */
	public SamlKeyInfo getKeyInfo() {
		return keyInfo;
	}

	/**
	 * @param keyInfo
	 *            the keyInfo to set
	 */
	public void setKeyInfo(SamlKeyInfo keyInfo) {
		this.keyInfo = keyInfo;
	}

}
