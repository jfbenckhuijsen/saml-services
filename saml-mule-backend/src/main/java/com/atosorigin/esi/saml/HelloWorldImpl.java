/**
 * 
 */
package com.atosorigin.esi.saml;

import java.rmi.RemoteException;

import org.mule.RequestContext;
import org.mule.api.security.Authentication;
import org.mule.api.security.SecurityContext;

import com.atosorigin.saml.backend.HelloWorldRemote;

/**
 * @author a108600
 *
 */
public class HelloWorldImpl implements HelloWorldService {
	private HelloWorldRemote helloWorld;

	/**
	 * 
	 */
	public HelloWorldImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.atosorigin.esi.saml.HelloWorldService#sayHello(java.lang.String)
	 */
	public String sayHello(String type) {
		final SecurityContext securityContext = RequestContext.getEvent().getSession().getSecurityContext();
		final Authentication authentication = securityContext.getAuthentication();
		boolean auth = authentication.isAuthenticated();
		
		try {
			String helloString = this.helloWorld.sayHello(type);
			return helloString + "," + (auth? " authenticated" : "") + " from a JMS service";
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the helloWorld
	 */
	public HelloWorldRemote getHelloWorld() {
		return helloWorld;
	}

	/**
	 * @param helloWorld the helloWorld to set
	 */
	public void setHelloWorld(HelloWorldRemote helloWorld) {
		this.helloWorld = helloWorld;
	}

}
