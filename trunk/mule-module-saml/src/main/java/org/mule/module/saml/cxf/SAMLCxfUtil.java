/**
 * 
 */
package org.mule.module.saml.cxf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.opensaml.SAMLAssertion;

/**
 * @author a108600
 *
 */
public class SAMLCxfUtil {

	/**
	 * 
	 */
	public SAMLCxfUtil() {
		super();
	}

	/**
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<SAMLAssertion> getAssertions (Message message) {
		Collection<SAMLAssertion> assertions = new LinkedList<SAMLAssertion>();
		
		List<Object> results = (List<Object>)message.get(WSHandlerConstants.RECV_RESULTS);
		for (Object result : results) {
			if (result instanceof WSHandlerResult) {
				WSHandlerResult rResult = (WSHandlerResult) result;
				Vector<Object> wsResult = rResult.getResults();
				getAssertions(wsResult, assertions);
			}
		}
		
		return assertions;
	}
	
	/**
	 * @param message
	 * @param wsResult
	 * @throws Fault
	 */
	private static void getAssertions (Vector<Object> wsResult, Collection<SAMLAssertion> assertions) {
		for (Object r : wsResult) {
			if (r instanceof WSSecurityEngineResult) {
				WSSecurityEngineResult result = (WSSecurityEngineResult) r;
				SAMLAssertion assertion = (SAMLAssertion) result.get(WSSecurityEngineResult.TAG_SAML_ASSERTION);
				assertions.add(assertion);
			}
		}
	}

}
