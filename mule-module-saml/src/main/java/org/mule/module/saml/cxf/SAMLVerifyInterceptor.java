/**
 * 
 */
package org.mule.module.saml.cxf;

import java.util.Collection;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.AbstractWSS4JInterceptor;
import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.security.Authentication;
import org.mule.api.security.SecurityContext;
import org.mule.api.security.SecurityException;
import org.mule.api.security.SecurityManager;
import org.mule.api.security.SecurityProviderNotFoundException;
import org.mule.api.security.UnknownAuthenticationTypeException;
import org.mule.module.saml.SAMLAuthenticationAdapter;
import org.opensaml.SAMLAssertion;


/**
 * @author a108600
 *
 */
public class SAMLVerifyInterceptor extends AbstractWSS4JInterceptor {
	
	private String certificateAlias;

	/**
	 * 
	 */
	public SAMLVerifyInterceptor() {
		super();
		
		setPhase(Phase.PRE_INVOKE);
	}

	/* (non-Javadoc)
	 * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
	 */
	public void handleMessage(SoapMessage message) throws Fault {
		MuleContext muleContext = RequestContext.getEventContext().getMuleContext();
		MuleEvent event = RequestContext.getEvent();
		SecurityManager securityManager = muleContext.getSecurityManager();
		
		Collection<SAMLAssertion> assertions = SAMLCxfUtil.getAssertions(message);
		
		try {
			SAMLAuthenticationAdapter samlAuthentication = new SAMLAuthenticationAdapter(assertions.iterator().next(), this.certificateAlias);

			Authentication authResult = securityManager.authenticate(samlAuthentication);

			SecurityContext context = securityManager.createSecurityContext(authResult);
	        context.setAuthentication(authResult);
	        event.getSession().setSecurityContext(context);
		} catch (SecurityException e) {
			throw new SoapFault(e.getLocalizedMessage(), e, Fault.FAULT_CODE_CLIENT);
		} catch (SecurityProviderNotFoundException e) {
			throw new SoapFault(e.getLocalizedMessage(), e, Fault.FAULT_CODE_SERVER);
		} catch (UnknownAuthenticationTypeException e) {
			throw new SoapFault(e.getLocalizedMessage(), e, Fault.FAULT_CODE_SERVER);
		}
	}

	/**
	 * @return the certificateAlias
	 */
	public String getCertificateAlias() {
		return certificateAlias;
	}

	/**
	 * @param certificateAlias the certificateAlias to set
	 */
	public void setCertificateAlias(String certificateAlias) {
		this.certificateAlias = certificateAlias;
	}
	
}
