/**
 * 
 */
package com.atosorigin.saml.backend;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * @author a108600
 *
 */
public interface HelloWorldRemoteHome extends EJBHome {

	HelloWorldRemote create() throws CreateException, RemoteException; 
	

}
