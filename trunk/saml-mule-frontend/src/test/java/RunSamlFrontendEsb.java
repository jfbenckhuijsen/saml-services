import org.apache.log4j.Logger;
import org.mule.api.MuleException;

import com.atosorigin.saml.mule.ESBStarter;

/**
 * 
 */

/**
 * @author a108600
 *
 */
public final class RunSamlFrontendEsb {

	private static final Logger LOGGER = Logger.getLogger(RunSamlFrontendEsb.class);
	
	private RunSamlFrontendEsb() {
		super();
	}
	
	public static void main(String[] args) {

			System.setProperty("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
			System.setProperty("java.naming.provider.url", 
					"ldap://127.0.0.1:10389/ou=jndi,dc=saml,dc=nl,dc=atosorigin,dc=com");
	
			try {
				ESBStarter.main(new String[] {"mule-config.xml"});
			} catch (MuleException e) {
				LOGGER.error(e.getMessage());
			}

	}

}
