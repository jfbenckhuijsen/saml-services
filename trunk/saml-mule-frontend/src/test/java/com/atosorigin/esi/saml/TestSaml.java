/**
 * 
 */
package com.atosorigin.esi.saml;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author a108600
 *
 */
public class TestSaml {

	/**
	 * 
	 */
	public TestSaml() {
		super();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSamlSecurity() throws MalformedURLException {
		SamlService service = new SamlService(new URL("http://localhost:8004/esi/saml-test?wsdl"));
		Saml saml = service.getSamlPort();
		
		// Setup WS-S
		Client client = ClientProxy.getClient(saml);
		Endpoint cxfEndpoint = client.getEndpoint();
		
		Map<String, Object> outProps = new HashMap<String, Object>();
		outProps.put(WSHandlerConstants.ACTION, 
				WSHandlerConstants.SAML_TOKEN_UNSIGNED);
		outProps.put(WSHandlerConstants.USER, "jeroen");
		outProps.put(WSHandlerConstants.SAML_PROP_FILE, "saml.properties");
		outProps.put(WSHandlerConstants.SIG_PROP_FILE, "saml.properties");
		outProps.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference");
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
		cxfEndpoint.getOutInterceptors().add(wssOut);
		cxfEndpoint.getActiveFeatures().add(new LoggingFeature());

		
		System.out.println(saml.helloWorld("Jeroen"));
	}

}
