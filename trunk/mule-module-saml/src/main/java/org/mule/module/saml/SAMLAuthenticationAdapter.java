/**
 * 
 */
package org.mule.module.saml;

import java.util.Iterator;
import java.util.Map;

import org.mule.api.security.Authentication;
import org.mule.api.security.UnauthorisedException;
import org.mule.config.i18n.CoreMessages;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLSubject;
import org.opensaml.SAMLSubjectStatement;

/**
 * @author a108600
 *
 */
public class SAMLAuthenticationAdapter implements Authentication {
	
	private boolean authenticated;
	@SuppressWarnings("unchecked")
	private Map properties;
	private SAMLAssertion assertion;
	private SAMLSubject subject;
	private String certificateAlias;

	/**
	 * @throws UnauthorisedException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public SAMLAuthenticationAdapter(SAMLAssertion assertion, String certificateAlias) throws UnauthorisedException {
		super();
		this.assertion = assertion;
		this.certificateAlias = certificateAlias;
		
		this.subject = null;
		for (Iterator i = assertion.getStatements() ; i.hasNext() ; ) {
			Object statement = i.next();
			if (statement instanceof SAMLSubjectStatement) {
				SAMLSubjectStatement subjectStatement = (SAMLSubjectStatement) statement;
				subject = subjectStatement.getSubject();
				break;
			}
		}
		
		if (this.subject == null) {
			throw new UnauthorisedException(CoreMessages.authNoCredentials());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#getCredentials()
	 */
	public Object getCredentials() {
		return this.assertion;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#getPrincipal()
	 */
	public Object getPrincipal() {
		return this.subject;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#getProperties()
	 */
	@SuppressWarnings("unchecked")
	public Map getProperties() {
		return this.properties;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#isAuthenticated()
	 */
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#setAuthenticated(boolean)
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;

	}

	/* (non-Javadoc)
	 * @see org.mule.api.security.Authentication#setProperties(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void setProperties(Map properties) {
		this.properties = properties;
	}

	/**
	 * @return the certificateAlias
	 */
	public String getCertificateAlias() {
		return certificateAlias;
	}

}
