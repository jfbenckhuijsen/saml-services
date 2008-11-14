/**
 * 
 */
package com.atosorigin.saml.businessbeans;

import com.atosorigin.esi.saml.Saml;

/**
 * @author a108600
 *
 */
public class HelloWorldBB {
	private Saml samlService;
	private String helloReply;
	
	/**
	 * 
	 */
	public HelloWorldBB() {
		super();
	}

	public void sayHello() {
		this.helloReply = this.samlService.helloWorld("SAML");
	}

	public String getHelloReply() {
		return helloReply;
	}

	public void setHelloReply(String helloReply) {
		this.helloReply = helloReply;
	}

	public Saml getSamlService() {
		return samlService;
	}

	public void setSamlService(Saml samlService) {
		this.samlService = samlService;
	}

}
