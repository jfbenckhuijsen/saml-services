/**
 * 
 */
package com.atosorigin.saml.jaas;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

/**
 * @author a108600
 *
 */
public class LdapSamlLoginModule extends SamlLoginModule {
	
	private static final Object USER_RDN_PROPERTY = "ldap.user.rdn";
	private static final Object GROUP_RDN_PROPERTY = "ldap.group.rdn";
	private static final Object USER_ATTRIBUTE_ID_PROPERTY = "user.id.attribute";
	private static final Object GROUP_ATTRIBUTE_ID_PROPERTY = "group.id.attribute";
	private static final Object MEMBER_ATTRIBUTE_ID_PROPERTY = "member.id.attribute";
	
	// Options
	private String userRDN;
	private String groupRDN;
	private String userAttributeID = "uid";
	private String groupAttributeID = "ou";
	private String memberAttributeID = "member";

	// LDAP Context
	private InitialLdapContext ldapContext; 
	
	private SimpleGroup userRoles = new SimpleGroup("Roles");
	
	/**
	 * 
	 */
	public LdapSamlLoginModule() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.jaas.SamlLoginModule#abort()
	 */
	@Override
	public boolean abort() throws LoginException {
		if (!super.abort()) {
			return false;
		}
		
		try {
			this.ldapContext.close();
		} catch (NamingException e) {
			logger.error("Cannot close LDAP context", e);
		}
		this.ldapContext = null;
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.jaas.SamlLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		
		this.userRDN = (String) options.get(USER_RDN_PROPERTY);
		this.groupRDN = (String) options.get(GROUP_RDN_PROPERTY);
		if (options.get(USER_ATTRIBUTE_ID_PROPERTY) != null) {
			this.userAttributeID = (String) options.get(USER_ATTRIBUTE_ID_PROPERTY);
		}
		if (options.get(GROUP_ATTRIBUTE_ID_PROPERTY) != null) {
			this.groupAttributeID = (String) options.get(GROUP_ATTRIBUTE_ID_PROPERTY);
		}
		if (options.get(MEMBER_ATTRIBUTE_ID_PROPERTY) != null) {
			this.memberAttributeID = (String) options.get(MEMBER_ATTRIBUTE_ID_PROPERTY);
		}
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		for (Map.Entry<String, ?> entry : options.entrySet()) {
			if (entry.getKey().startsWith("java.naming")) {
				env.put(entry.getKey(), (String) entry.getValue());
			}
		}
		try {
			this.ldapContext = new InitialLdapContext(env , null);
		} catch (NamingException e) {
			throw new IllegalStateException("Cannot connect to LDAP", e); 
		}
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.jaas.SamlLoginModule#login()
	 */
	@Override
	public boolean login() throws LoginException {
		if (!super.login()) {
			return false;
		}
		
		String username = getUsername();
		if (username == null) {
			throw new IllegalStateException("No username found, but parent authentication succeeded");
		}
		
		SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setTimeLimit(10000);
        
        StringBuffer userFilter = new StringBuffer("(");
        userFilter.append(this.userAttributeID);
        userFilter.append("={0})");
        
        StringBuffer roleFilter = new StringBuffer("(");
        roleFilter.append(this.memberAttributeID);
        roleFilter.append("={0})");
        
        Object[] filterArgs = {username};
        
        controls.setReturningAttributes(new String[]{this.userAttributeID});
        try {
	        NamingEnumeration<SearchResult> userAnswer = this.ldapContext.search(userRDN, userFilter.toString(), filterArgs, controls);
	        
	        String userDN = null;
	        while (userAnswer.hasMore()) {
	        	SearchResult result = userAnswer.next();
	        	userDN = result.getName();
	        	if (result.isRelative()) {
	        		userDN +=  "," + userRDN + "," + ldapContext.getNameInNamespace();
	        	}
	        }
	        
	        if (userDN == null) {
	        	throw new LoginException("Cannot find user in LDAP");
	        }
	        
	        filterArgs = new Object[]{userDN};
	        controls.setReturningAttributes(new String[]{this.groupAttributeID});
	        NamingEnumeration<SearchResult> memberAnswer = this.ldapContext.search(groupRDN, roleFilter.toString(), filterArgs, controls);
	        
	        while (memberAnswer.hasMore()) {
	        	SearchResult result = memberAnswer.next();
	        	
	        	String group = (String) result.getAttributes().get(this.groupAttributeID).get();
	        	userRoles.addMember(new SimplePrincipal(group));
	        }
        } catch (NamingException e) {
        	logger.error("Error finding user in LDAP", e);
        	throw new LoginException("Cannot find user in LDAP");
        }
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.jaas.SamlLoginModule#commit()
	 */
	@Override
	public boolean commit() throws LoginException {
		if (!super.commit()) {
			return false;
		}
		
		this.subject.getPrincipals().add(this.userRoles);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.saml.jaas.SamlLoginModule#logout()
	 */
	@Override
	public boolean logout() throws LoginException {
		if (!super.logout()) {
			return false;
		}
		
		this.subject.getPrincipals().remove(this.userRoles);
		
		return true;
	}

}
