/**
 * 
 */
package com.atosorigin.saml.wss;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.saml.SAMLIssuer;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthenticationQuery;
import org.opensaml.SAMLBinding;
import org.opensaml.SAMLConfig;
import org.opensaml.SAMLException;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLResponse;
import org.opensaml.SAMLSOAPHTTPBinding;
import org.opensaml.SAMLSubject;
import org.opensaml.XML;
import org.opensaml.provider.SOAPHTTPBindingProvider;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.cas.CasAuthenticationToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author a108600
 * 
 */
public class SAMLCasIssuer implements SAMLIssuer {

	private Crypto issuerCrypto;
	private String issuerKeyName;
	private String issuerKeyPassword;

	private String casSamlUrl;
	private int milisBeforeExpiration;

	// Setters
	private Document instanceDoc;
	private Crypto userCrypto = null;
	private String username = null;

	private boolean senderVouches = true;
	
	private static ConcurrentHashMap<String, SAMLAssertion> assertionCache = new ConcurrentHashMap<String, SAMLAssertion>();

	/**
	 * 
	 */
	public SAMLCasIssuer() {
		super();
		this.issuerCrypto = null;
		this.issuerKeyName = null;
		this.issuerKeyPassword = null;
	}

	public SAMLCasIssuer(Properties properties) throws NamingException {
		super();

		String cryptoProp = properties
				.getProperty("org.apache.ws.security.saml.issuer.cryptoProp.file");
		if (cryptoProp != null) {
			issuerCrypto = CryptoFactory.getInstance(cryptoProp);
			issuerKeyName = properties
					.getProperty("org.apache.ws.security.saml.issuer.key.name");
			issuerKeyPassword = properties
					.getProperty("org.apache.ws.security.saml.issuer.key.password");
		}
		String casUrlName = properties
				.getProperty("com.atosorigin.saml.wss.casSamlUrl");
		
		InitialContext context = new InitialContext();
		this.casSamlUrl = (String) context.lookup(casUrlName);
		
		this.milisBeforeExpiration = Integer.parseInt(properties
				.getProperty("com.atosorigin.saml.wss.milisBeforeExpiration"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#newAssertion()
	 */
	public SAMLAssertion newAssertion() {
		SAMLAssertion cached = assertionCache.get(this.username);
		if (cached != null) {
			Calendar calNow = Calendar.getInstance();
			Date now = calNow.getTime();
			calNow.add(Calendar.MILLISECOND, this.milisBeforeExpiration);
			Date nowPlus = calNow.getTime();
			
			// Return prematurely as we can reused the cached instance.
			if (cached.getNotBefore().after(now) && cached.getNotOnOrAfter().before(nowPlus)) {
				return cached;
			} else {
				assertionCache.remove(this.username, cached);
			}
		}
		
		CasAuthenticationToken webAuth = (CasAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		// TODO: service is now hard-coded
		String service = "www.atosorigin.com";
		AttributePrincipal principal = webAuth.getAssertion().getPrincipal();
		String pt = principal.getProxyTicketFor(service);

		try {
			final SAMLRequest samlRequest = new SAMLRequest();
			
			final SAMLAuthenticationQuery samlQuery = new SAMLAuthenticationQuery();
			samlRequest.setQuery(samlQuery);
			samlRequest.setId(SAMLConfig.instance().getDefaultIDProvider().getIdentifier());

			final SAMLSubject samlSubject = new SAMLSubject();
			final SAMLNameIdentifier samlNameIdentifier = new SAMLNameIdentifier();
			samlNameIdentifier.setName(this.username);
			samlSubject.setNameIdentifier(samlNameIdentifier);
			
			final Document doc = XML.parserPool.newDocument();
			final Element confData = doc.createElementNS(XML.SAML_NS, "SubjectConfirmationData");
			final Element ticketNode = doc.createElementNS("http://www.jasig.org/cas", "casTicket");
			confData.appendChild(ticketNode);
			final Text ticketData = doc.createTextNode(pt);
			ticketNode.appendChild(ticketData);
			final Element serviceNode = doc.createElementNS("http://www.jasig.org/cas", "casTargetService");
			confData.appendChild(serviceNode);
			final Text serviceData = doc.createTextNode(service);
			serviceNode.appendChild(serviceData);
			samlSubject.setConfirmationData(confData);
			samlSubject.addConfirmationMethod("http://www.jasig.org/cas/ticket");
			
			samlQuery.setSubject(samlSubject);
			
			final SAMLSOAPHTTPBinding binding = new SOAPHTTPBindingProvider(SAMLBinding.SOAP, null);
			final SAMLResponse response = binding.send(casSamlUrl, samlRequest);

			final SAMLAssertion assertion = (SAMLAssertion) response.getAssertions().next();
			
			// Cache the newly created assertion
			assertionCache.put(this.username, assertion);
			
			return assertion;
		} catch (SAMLException e) {
			throw new RuntimeException(e.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ws.security.saml.SAMLIssuer#setUserCrypto(org.apache.ws.security
	 * .components.crypto.Crypto)
	 */
	public void setUserCrypto(Crypto userCrypto) {
		this.userCrypto = userCrypto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#getIssuerCrypto()
	 */
	public Crypto getIssuerCrypto() {
		return issuerCrypto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#getIssuerKeyName()
	 */
	public String getIssuerKeyName() {
		return this.issuerKeyName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#getIssuerKeyPassword()
	 */
	public String getIssuerKeyPassword() {
		return this.issuerKeyPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ws.security.saml.SAMLIssuer#isSenderVouches()
	 */
	public boolean isSenderVouches() {
		return senderVouches;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ws.security.saml.SAMLIssuer#setInstanceDoc(org.w3c.dom.Document
	 * )
	 */
	public void setInstanceDoc(Document instanceDoc) {
		this.instanceDoc = instanceDoc;
	}

}
