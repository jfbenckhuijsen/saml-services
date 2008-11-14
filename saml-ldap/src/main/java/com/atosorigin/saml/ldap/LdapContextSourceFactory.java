/**
 * 
 */
package com.atosorigin.saml.ldap;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author a108600
 * 
 */
public class LdapContextSourceFactory implements FactoryBean {

	private static final Log LOG = LogFactory
			.getLog(LdapContextSourceFactory.class);

	private URI uri;

	/**
	 * 
	 */
	public LdapContextSourceFactory() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObject() throws Exception {
		LdapContextSource source = createContextSource();
		source.setAnonymousReadOnly(false);
		source.setPooled(true);
		Map<String, String> baseEnvironmentProperties = new HashMap<String, String>();
		// TODO: reactivate SSl when certificate's are available
		// baseEnvironmentProperties.put("java.naming.security.protocol",
		// "ssl");
		baseEnvironmentProperties.put("java.naming.security.authentication",
				"simple");
		source.setBaseEnvironmentProperties(baseEnvironmentProperties);

		String userInfo = this.uri.getUserInfo();

		String userDn = null;
		String password = null;
		if (userInfo != null) {
			int pwStart = userInfo.indexOf(':');
			if (pwStart != -1) {
				userDn = userInfo.substring(0, pwStart);
				password = userInfo.substring(pwStart + 1, userInfo.length());
			} else {
				LOG.warn("Password not found in LDAP connection url");
			}
		}
		

		String ldapUrl = this.uri.getScheme() + "://" + this.uri.getHost()
				+ ":" + this.uri.getPort() + this.uri.getPath();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Connecting to LDAP: url = " + ldapUrl + " userDN = "
					+ userDn);
		}

		source.setUrl(ldapUrl);
		source.setUserDn(userDn);
		source.setPassword(password);
		source.afterPropertiesSet();

		return source;
	}

	/**
	 * @return
	 */
	protected LdapContextSource createContextSource() {
		return new LdapContextSource();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Class<LdapContextSource> getObjectType() {
		return LdapContextSource.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

}
