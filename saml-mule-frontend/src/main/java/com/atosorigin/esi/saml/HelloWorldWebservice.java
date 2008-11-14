/**
 * 
 */
package com.atosorigin.esi.saml;

import javax.jws.WebService;

import org.mule.RequestContext;
import org.mule.api.security.SecurityContext;

//import com.atosorigin.saml.backend.HelloWorldRemote;

/**
 * @author a108600
 *
 */
@WebService(targetNamespace = "http://www.atosorigin.com/esi/saml/", 
		portName = "SamlPort", 
		serviceName = "SamlService", 
		endpointInterface = "com.atosorigin.esi.saml.Saml")
public class HelloWorldWebservice implements Saml {
	
	private HelloWorldService helloWorld;

	/**
	 * 
	 */
	public HelloWorldWebservice() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.esi.saml.Saml#helloWorld(java.lang.String)
	 */
	public String helloWorld(String name) {
		SecurityContext securityContext = RequestContext.getEvent().getSession().getSecurityContext();
		boolean auth = securityContext.getAuthentication().isAuthenticated();
		String helloString = this.helloWorld.sayHello(name);
		return helloString + "," + (auth? " authenticated " : "") + " from a webservice";
	}

	/**
	 * @return the helloWorld
	 */
	public HelloWorldService getHelloWorld() {
		return helloWorld;
	}

	/**
	 * @param helloWorld the helloWorld to set
	 */
	public void setHelloWorld(HelloWorldService helloWorld) {
		this.helloWorld = helloWorld;
	}

}
