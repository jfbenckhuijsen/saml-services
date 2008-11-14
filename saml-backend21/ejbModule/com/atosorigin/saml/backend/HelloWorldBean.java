/**
 * 
 */
package com.atosorigin.saml.backend;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * @author a108600
 *
 */
public class HelloWorldBean implements SessionBean, HelloWorld {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3268209680120928267L;

	private javax.ejb.SessionContext context;
	
	/**
	 * 
	 */
	public HelloWorldBean() {
		super();
	}
	
	/**
	 * @throws RemoteException
	 */
	public void ejbCreate() {
		// no op
	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	@Override
	public void ejbActivate() throws EJBException, RemoteException {
		// noop
	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		// noop
	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	@Override
	public void ejbRemove() throws EJBException, RemoteException {
		// noop
	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	@Override
	public void setSessionContext(SessionContext context) throws EJBException,
			RemoteException {
		this.context = context;
	}

	@Override
	public String sayHello(String type) {
		return "Hello " + context.getCallerPrincipal().getName() + 
			" in a "+ type + " World";
	}

}
